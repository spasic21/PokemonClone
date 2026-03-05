# PokemonClone

A Pokemon fan game built in plain Java (no game engine). Targets Gen 2 visual aesthetic (GBC-era art style) with plans to implement up to Gen 6 mechanics. Currently has Gen 1 and Gen 2 Pokemon in the database, with more planned.

## Build & Run
- Java 21 (ms-21.0.7)
- IDE: VS Code (primary), IntelliJ (secondary)
- Dependency: `json-simple-1.1.1.jar` located in parent directory (`../`)
- VS Code requires `.vscode/settings.json` to include `resources/` on the classpath (already configured)
- Entry point: `GameDriver.java`

## Project Structure
- `src/` — all Java source files
- `resources/` — sprites, sounds, maps, fonts, JSON databases
- `out/` — IntelliJ build output (do not modify)
- `docs/FEATURES.md` — Core gameplay + planned features roadmap
- `docs/FUTURE.md` — Long-term / QoL / fun feature ideas

## Key Files
- `ui/Game.java` — main game loop (fixed 60fps timestep), screen switching, state management
- `battle/BattleManager.java` — turn-based battle logic (damage, turn order, exp, event queue)
- `framework/pokemon/PokemonGenerator.java` — Pokemon stat generation using Gen 3+ formulas
- `framework/pokemon/ExperienceCalculator.java` — all 6 exp growth curves, Gen 6 exp formula
- `framework/Handler.java` — service locator, passes references between systems
- `framework/TypeTable.java` — type effectiveness matrix
- `objects/Player.java` — movement, collision, battle encounter detection
- `resources/pokemon_base_stats.json` — Pokemon database (Gen 1 + Gen 2, excludes Mew)
- `resources/item_database.json` — item database

## Architecture
- State machine via `GameState` enum: `Loading → Game → Menu/Dialogue/Transition → Battle → PokemonMenu/PokemonSummary/Bag`
- Each state has a corresponding `Screen` subclass with `update()` and `render()`
- `GameKeyInput` dispatches to context-specific key handlers per game state
- Battle uses an event queue (`Queue<BattleEvent>`) to sequence animations and text

## Architectural Guardrails
- update() mutates state; render() must not.
- No new global static state.
- Do not expand Handler unless absolutely necessary.
- Preserve GameState flow and battle state transitions.
- Avoid shared mutable references from database templates.

## Database & Template Safety
- Never mutate objects returned by *Database classes.
- Always deep-copy moves, sprites, and base stats into instances.
- If you ever see `.getMoveList()` or `.getSprite()` being stored directly, flag it immediately.

## Battle System
- Damage formula: Gen 3+ (Physical/Special split)
- STAB, dual-type effectiveness, damage variance (0.85–1.0) all implemented
- Turn order by Speed stat
- All 6 exp growth curves implemented
- Accuracy, critical hits, natures, and status conditions are scaffolded but not yet wired up
- Status moves currently do nothing

## Performance Rules
- Avoid per-frame object allocations inside update loops.
- No streams or lambdas in hot paths.
- Avoid reflection.
- Prefer primitive types over boxed types in performance-critical systems.
- Battle calculations must remain deterministic.

## Model Behavior
- Ask for clarification if context is missing.
- Do not assume missing mechanics.
- If modifying battle logic, explain formula impact.
- Suggest test cases when fixing critical systems.
- Prioritize correctness and determinism over feature expansion.

## Refactor Policy
- Prefer minimal targeted fixes.
- Do not refactor unrelated systems.
- Preserve public APIs unless required for correctness.
- Fix root causes, not symptoms.

---

## Fix Order (work top to bottom)

**Always complete Critical before touching High Priority.**

### Critical — Fix First

**[Full detailed list with line numbers](docs/fixes_critical.md)**

### High Priority
These break gameplay or cause hard-to-debug misbehavior.

**[Full detailed list with line numbers](docs/fixes_high.md)**

### Foundation Work (do before Medium bugs or any new features)
These are prerequisites for most of the codebase to work correctly.

#### Pokemon Database & Instance Fix
- **Problem**: `PokemonDatabase` stores mutable `Pokemon` objects as templates; `PokemonGenerator` shallow-copies them, sharing `PokemonMove` and sprite references between the database and live instances
- **Fix 1 — deep copy moves**: in `PokemonGenerator.setMoves()`, construct a `new PokemonMove(...)` for each move; each instance owns its own PP state
- **Fix 2 — new sprites per instance**: in `PokemonGenerator.generatePokemon()`, construct `new PokemonFrontSprite(...)` and `new PokemonBackSprite(...)` instead of assigning database references; underlying `BufferedImage` pixel data is still shared through `SpriteSheet` — no memory waste
- **Long-term**: consider splitting `Pokemon` into `PokemonData` (immutable template) and `Pokemon` (mutable instance)

#### Save System
- **Format**: JSON file at `%APPDATA%/PokemonClone/save.json`
- **Save file structure**:
  ```json
  {
    "player": { "x": 1200, "y": 840, "location": "World", "money": 0 },
    "party": [
      {
        "species": "Chikorita", "level": 14, "currentHp": 38, "currentExp": 420,
        "ivs": { "hp": 24, "attack": 18, "defense": 31, "specialAttack": 20, "specialDefense": 28, "speed": 15 },
        "moves": [ { "name": "Tackle", "currentPP": 31 }, { "name": "Growl", "currentPP": 38 } ]
      }
    ],
    "bag": {
      "items": [ { "name": "Potion", "quantity": 3 } ],
      "pokeballs": [ { "name": "Poke Ball", "quantity": 5 } ]
    },
    "pc": {
      "boxes": [ { "name": "Box 1", "slots": [ { "species": "Pidgey" }, null, null ] } ]
    },
    "flags": {
      "trainersDefeated": ["ROUTE_30_BUG_CATCHER_1"],
      "legendariesCaught": [],
      "eventsCompleted": [],
      "variables": { "teamRocketStage": 0 }
    }
  }
  ```
- **Load flow**: read species name → `pokemonDatabase.getPokemon(species)` for base data + sprite → create new instance → override with saved level, HP, IVs, moves/PP; database is never modified
- **Build order**: (1) party + player position, (2) bag, (3) event flag system, (4) trainer defeat flags, (5) PC box
- **`SaveManager`**: singleton; serializes/deserializes full save; called on menu→save and on load from title menu

#### Event Flag System
- **Purpose**: tracks everything that happened in the world — trainer defeats, legendary encounters, story progression, one-time events
- **Two types**: boolean flags (`Set<String>`) and integer variables (`Map<String, Integer>`)
- **`EventFlags` class**: centralize all flag ID strings as constants:
  ```java
  public class EventFlags {
      public static final String TEAM_ROCKET_GOLDENROD = "TEAM_ROCKET_GOLDENROD";
      public static final String SUDOWOODO_BLOCKED     = "SUDOWOODO_BLOCKED";
      public static final String LUGIA_CAUGHT          = "LUGIA_CAUGHT";
  }
  ```
- **Build this early** — cheap to set up and painful to retrofit; every NPC interaction, trainer battle, and story trigger depends on it

### Medium Priority
Per-frame allocations, audio, and architectural issues.

36. `BattleScreen` constructs a full `SpriteSheet` just to call `grabImage` once for `battleBackground` — cache the sheet or use `ImageIO.read()` + `getSubimage()` directly
37. `font.deriveFont()` called in `renderEventText()`, `renderHud()`, `renderBattleOptions()`, and `renderMoveSelectBox()` every frame — derive and cache both sizes (48f and 60f) in the constructor
38. `new Color(90,140,140)`, `new Color(46,58,67)`, and `new BasicStroke(5)` in `BattleScreen.renderTextBox()` created every frame — cache in constructor
39. `AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)` in `BattleScreen.render():119` is a no-op — remove
40. `renderPokemon()` calls `getBackSprite()` four times and `getFrontSprite()` three times — store each in a local variable
41. `typeSymbolSpriteSheet.grabImage()` called every `render()` frame in `PlayerHud` — cache type symbol images; only re-grab when active Pokemon changes
42. `PlayerHud` creates its own `new TypeTable()` — `BattleManager` already has one; a third instance is in `TrainerHud`; consolidate to a single `static final TypeTable`
43. `new BasicStroke(8)` allocated every `render()` call in `MoveSelectBox` — cache in constructor
44. `MoveSelectBox.renderInfoBox():149` uses `moveCategorySymbol.getWidth() * 4` for type symbol width — should use `typeSymbol.getWidth() * 4`
45. `getTypeSymbol()` and `getCategorySymbol()` call `spriteSheet.grabImage()` every frame in `MoveSelectBox` — cache and invalidate on cursor movement
46. `MoveSelectBox.renderMoveBox():101` calls `pokemonMoves.get(0)` unconditionally — if a Pokemon has 0 moves this throws `IndexOutOfBoundsException`; add a guard
47. `Bag` stores direct references to `Item` objects from `ItemDatabase` — decrementing a quantity would affect the database; copy into new `Item` instances in the `Bag` constructor
48. `Bag` has `addItem()` and `addPokeball()` but no `removeItem()`, `useItem()`, or quantity-decrement — items can be added but never consumed
49. Transition types are raw `int` literals (1, 2, 3) — replace with a `TransitionType` enum (`Fade`, `RadarSweep`, `Pixelated`)
50. `SpawnManager.getInstance()` is not thread-safe — `spawnManager` field is not `volatile`
51. `PokemonMenuTile` creates a new `SpriteSheet`, font, and `hpSymbol` per tile instance — 6 tiles = 3 PNGs decoded + 1 font loaded 6 times; make all `static final` shared fields
52. `PokemonMenuTile.render()` calls `font.deriveFont(48f)` and creates `new BasicStroke(5)` every frame — cache in constructor
53. `PokemonSummaryScreen.update()` allocates two new `String[]` arrays every tick — only recompute when visible Pokemon changes
54. `PokemonSummaryScreen.render()` creates `new BasicStroke(5)`, calls `font.deriveFont(32f)`, and creates `new AffineTransform()` every frame — cache all in constructor
55. `PokemonSummaryScreen.getTypeSymbol()` and `getCategorySymbol()` call `grabImage()` every frame — cache; only change when selected move changes
56. `BagScreen.render()` calls `font.deriveFont(48f)`, `font.deriveFont(32f)`, and creates `new BasicStroke(6)` every frame — cache in constructor
57. `TrainerHud.renderTypeSymbol()` draws `g.drawRect(...)` twice consecutively (lines 125–126) — duplicate; remove one
58. `TrainerHud.renderTypeSymbol()` calls `grabImage()` every render frame — cache type symbol images; refresh when active Pokemon changes
59. OT hardcoded as `"Gugi"`, trainer ID as `"123456"`, Ability as `"Blaze"` in `PokemonSummaryScreen` — placeholders needing real data from save system
60. Player name hardcoded as `"Gugi"` in `MenuScreen.menuList` — read from player profile once save system exists
61. `BattleOptions.loadDimensions()` overwrites all position fields that the constructor just set — the first six constructor assignments are dead code; remove them
62. `PokemonSummaryKeyInput.moveSelect` state and `moveId` persist across Pokemon — navigating to Pokemon 2 stays in move-select with a potentially out-of-bounds `moveId`
63. `MenuKeyInput` wrap bounds `0` and `6` are hardcoded — derive from `menuList.size() - 1`
64. `TypeTable.table` is an instance field — every `new TypeTable()` allocates its own 18×18 double array; make it `static final`
65. `getTypeSymbol()` is duplicated identically in `PokemonSummaryScreen` and `MoveSelectBox`, and similarly in `PlayerHud`/`TrainerHud` — extract to a `TypeSpriteHelper` utility class
66. `TrainerSummonPokemonEvent.loadAnimations()` creates `new SpriteSheet("/pokeballs.png")` per instance — should be `static final`
67. `TrainerSummonPokemonEvent.render()` always draws `"Go! [name]!"` text regardless of `summonState` — gate to `summonState == PokemonSummon`
68. `count == 22` in `TrainerSummonPokemonEvent` is a magic number — derive from animation frame count
69. `SoundManager.playMusic()` loads music from disk on every call — pre-load into a cache like sound effects
70. `SoundManager.stopMusic()` calls `backgroundMusic.close()` forcing a reload every track change — use `stop()` + `setFramePosition(0)` on a cached clip instead
71. Dead fields in `SoundManager`: `sourceDataLine` and `isPlaying` are never read or set — safe to remove
72. `PokemonFrontSprite` and `PokemonBackSprite` each call `new SpriteSheet(...)` per instance — 6-Pokemon party = 12 full PNG decodes; make the four sheets `static final` shared class-level fields
73. `TrainerBackSprite` creates `new SpriteSheet(...)` per instance — should be a `static final` field
74. `NPC.java` loads a new `SpriteSheet` per NPC instance — make it `static final`
75. HP drain animation decrements by a fixed 3 HP per frame — scale the step by `max(3, totalDamage / 20)` to keep animation snappy for large hits
76. `System.currentTimeMillis()` called twice per `Animation.update()` — call once and store
77. `Integer.parseInt(String.valueOf(...))` used to parse JSON numbers — cast directly from `Long` instead
78. `gainExperience()` creates a new `ExperienceCalculator` instance every call — make it a field on `BattleManager`
79. Dead code in `BattleManager.init()`: null check + `.clear()` immediately before `new LinkedList<>()` — the clear never takes effect
80. Double sound on Run: `RunningAwaySound` then `ButtonSound` both play on the same keypress (`BattleKeyInput.java:78-83`)
81. `Tile.getBounds()` creates `new Rectangle(...)` on every call — not called anywhere; remove if unused
82. `BattleEvent.setIsFinished(boolean)` is a public setter while subclasses set `isFinished` directly as a protected field — the setter is redundant; remove or make package-private

### Lower Priority
Code quality, organization, and minor cleanup.

83. Wire up accuracy, critical hits, and nature modifiers (scaffolding already exists)
84. Status moves need implementation
85. Speed ties always favor the trainer — should be randomized
86. `battleEventQueue` recreated with `new LinkedList<>()` each battle — just call `.clear()`
87. All 4 player animations updated every frame regardless of active direction (`Player.java:59-62`)
88. Entity list sorted every frame — only sort when positions change (`EntityManager.java:52`)
89. Magic number coordinates throughout `BattleScreen`, `TileMapLoader`, `PlayerHud`
90. Magic numbers `3265`, `3266`, `3267` for collision tile IDs — use named constants
91. `BattleWin` state exits on any key release — should only fire on J (`BattleKeyInput.java:36-40`)
92. `BagKeyInput` allows navigating to pocket index 2 but `getPocketSize()` returns 0 — cap at 1 or implement third pocket
93. Move data duplicated per-Pokemon in `pokemon_base_stats.json` — a separate `move_database.json` would eliminate duplication
94. `shiny`, `gender`, `ability`, and `nature` fields exist on `Pokemon` but are never set or used
95. `ExperienceCalculator` modifier flags (`isTraded`, `hasLuckyEgg`, etc.) are unreachable — no setters; dead code
96. `ExperienceCalculator` growth curve methods are `public` — should be `private`
97. `restrictedTiles` in `PokemonMenuKeyInput` computed once at construction — re-evaluate on screen entry
98. `pokemonOptionId` in `PokemonMenuKeyInput` never resets on re-entry — cursor stays on last selected slot
99. `TypeTable` instantiated as `new TypeTable()` inside `BattleManager` — make it `static final`
100. `PokemonDatabase.initDatabase()` is separate from constructor — two-step init; if never called, all `getPokemon()` return null silently
101. `BattleManager` is a singleton — stale state can linger; consider creating fresh per battle
102. `Handler` is a god object — every class receives the full `Handler` and can reach everything; refactor by passing only what each class needs through its constructor (e.g. `BattleScreen` gets `screenWidth`, `screenHeight`, `BattleManager` instead of `Handler`); shrink `Handler` into a small `GameContext` holding only truly global state (screen dimensions, current world, game state); do this incrementally — each time you touch a class for a bug fix, replace its `Handler` usage with direct dependencies
103. `playMusicIfNeeded()` in `Game.java` is redundant — `SoundManager.playMusic()` already guards against restarting a playing track
104. Pixelated transition `tileOrder` only shuffled once at construction — never re-shuffles on reuse
105. Game state change happens in `render()` at `Game.java:210-216` — should be in `update()`
106. `SpawnManager` spawn points are hardcoded pixel coordinates — should be data-driven
107. World state resets on every map transition (`new World(...)` reconstructed each time)
108. `Game.gameState` is a public static field — should be an instance variable
109. ESC calls `System.exit(0)` immediately with no confirmation (`GameKeyInput.java:47`)
110. Tile selector logic in `TileMapLoader.loadTiles()` has unnecessary branch — simplify to `((tileId-1) % ratioX) + 1` and `((tileId-1) / ratioX) + 1`
111. Unused `path` field stored in `TileMapLoader` after construction
112. Empty `Tile.update()` and `World.getSpawnPoints()` — dead methods, safe to remove
113. `NPC` has no data model (no name, no dialogue, no trainer flag) — currently just a moving sprite with no identity
114. `BattleScreen.playerSprite` is named `playerSprite`, typed as `TrainerBackSprite`, populated from `getTrainerBackSprite()` — three different names for the same concept; rename consistently
115. `framework/DialogueScreen.java` is misplaced — should be in `screen/`
116. `objects/TrainerBackSprite.java` belongs with other sprite classes
117. `ui/` vs `screen/` boundary is blurry — `PlayerHud`, `TrainerHud`, `BattleOptions`, `MoveSelectBox` could move to a `battle/ui/` subpackage
118. `map1.json` and `map1.tmx` in resources appear unused — verify and remove
119. Tileset files and battle backgrounds are loose in `resources/` root — organize into subfolders
120. `EntityState.Running` enum value is never used — no run button, speed, or animation
121. `GameState` enum will need new values: `TitleScreen`, `TitleMenu`, `Settings`; adding them early and leaving `update()`/`render()` cases empty is better than retrofitting later
122. `Entity.setEntityCollision(false)` at line 105 can silently override a `true` set earlier in the same collision resolution pass — if two conditions both try to set the flag the second write wins; should use `OR`-assignment (`entityCollision |= newValue`) or restructure so the flag is set exactly once
123. `SpawnManager.spawnMap` is not declared `final` despite never being reassigned — mark it `final` to make intent clear
124. Window size is hardcoded as `1200×800` in `GameDriver` with no support for fullscreen or resize — all HUD layout depends on `handler.getWidth/Height()` which is correct, but a future fullscreen toggle will require passing a resize event through `handler` to all screens that cache layout coordinates in their constructors
125. `SpriteSheet.grabImage()` uses `getSubimage()` which returns a view sharing the parent's pixel data — efficient but the sub-image is invalidated if the parent is garbage collected; in practice fine since `SpriteSheet` holds the reference, but be aware if sprite sheets are ever unloaded
126. `BattleManager.battleEventQueue` is a public field — any class can add, remove, or clear events without going through `BattleManager`; make it private and expose only `addEvent()` / `pollEvent()` methods so the queue is controlled
