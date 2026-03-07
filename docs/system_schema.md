# PokemonClone — System Architecture Schema

> **Purpose**: Living architecture reference for developers and AI coding assistants.
> **Update Rule**: Edit only the affected section. Do not rewrite the entire file.
> **Last updated**: 2026-03-06

---

## Table of Contents
1. [Game Engine / Game Loop](#1-game-engine--game-loop)
2. [Rendering System](#2-rendering-system)
3. [Entity System](#3-entity-system)
4. [Player System](#4-player-system)
5. [Pokemon System](#5-pokemon-system)
6. [Battle System](#6-battle-system)
7. [Map / World System](#7-map--world-system)
8. [Event / Interaction System](#8-event--interaction-system)
9. [Input Handling](#9-input-handling)
10. [Audio System](#10-audio-system)
11. [Item / Bag System](#11-item--bag-system)
12. [UI System](#12-ui-system)
13. [Save / Load System](#13-save--load-system)

---

## 1. Game Engine / Game Loop

**Purpose**: Bootstrap the game, drive the fixed-timestep update/render loop, and own the top-level state machine.

**How it works**: `Game` runs in a dedicated thread at 60 FPS (16.67 ms per frame). Each tick calls `update()` then `render()` on the active `Screen`. Database loading runs on a background thread during the `Loading` state. `Handler` acts as a service locator, passing shared references to all systems.

**Key Classes**:
- `GameDriver` — entry point; constructs `Game(1200, 800)`
- `ui/Game.java` — main loop, all screen instances, state transitions, music triggering
- `framework/Handler.java` — service locator; holds `World`, party, `Bag`, `DialogueScreen`, `NpcDatabase`, `EventFlagManager`, transition metadata

**Flow**: `GameDriver.main()` → `Game.start()` → thread loop → `update(gameState)` dispatches to active screen → `render(gameState)` dispatches to active screen → repeat

**Dependencies**: All systems. `Handler` is injected into every major class.

---

## 2. Rendering System

**Purpose**: Draw each game frame through a hierarchy of `Screen` subclasses and supporting HUD/UI components.

**How it works**: `Game` uses a `BufferStrategy` (double buffering). Each `Screen` implements `update()` and `render(Graphics g)`. Screens are pre-instantiated and reused; `Game` switches the active one by `GameState`. Battle rendering uses an event queue — each `BattleEvent` drives its own rendering until `isFinished`.

**Key Classes**:
- `screen/Screen.java` — abstract base (`update()`, `render()`)
- `screen/LoadingScreen.java`, `GameScreen.java`, `BattleScreen.java`, `TransitionScreen.java`, `MenuScreen.java`, `PokemonMenuScreen.java`, `PokemonSummaryScreen.java`, `BagScreen.java`
- `ui/Window.java` — JFrame wrapper with AWT Canvas
- `framework/SpriteSheet.java` — decodes tiled PNG spritesheets via `grabImage(col, row, w, h)`
- `framework/Animation.java` — frame-based sprite animation with configurable duration

**Flow**: `Game.render()` → `Graphics g = bufferStrategy.getDrawGraphics()` → `activeScreen.render(g)` → sub-components render in order → `g.dispose()` → `bufferStrategy.show()`

**Dependencies**: `framework/SpriteSheet`, `framework/Animation`, `ui/` HUD classes, `battle/event/` classes

---

## 3. Entity System

**Purpose**: Provide a common base for all world objects (Player, NPCs, Tiles) with shared position, collision, and rendering contracts.

**How it works**: `Entity` is abstract with `x`, `y`, `width`, `height`, velocity, direction, and `ObjectId`. Movement is applied axis-by-axis; tile collision is resolved in up to 100 iterations per axis. `EntityManager` holds all active entities, sorts by Y for depth-correct rendering, and delegates `update()`/`render()` to each.

**Key Classes**:
- `objects/Entity.java` — abstract base; tile + entity collision resolution
- `objects/EntityManager.java` — list management, Y-sort, update/render delegation
- `objects/Tile.java` — single map tile with ID and `ObjectId` (RestrictedTile, GrassTile, etc.)
- `objects/Sprite.java` — abstract base for renderable sprites
- `objects/TrainerBackSprite.java` — trainer back sprite used in battle

**Flow**: `World.update()` → `EntityManager.update()` → each `Entity.update()` (move, collide) → `EntityManager.render()` sorts by Y → each `Entity.render()`

**Dependencies**: `framework/Camera`, `framework/SpriteSheet`, `framework/enums/ObjectId`

---

## 4. Player System

**Purpose**: Handle player movement, wild encounter detection, map transition triggers, and NPC interaction.

**How it works**: `Player` extends `Entity` (72×72). Movement is driven by `PlayerKeyInput`. Each completed tile step checks for a grass tile underfoot; a 1-in-200 chance triggers a battle. Collision with `MapTransitionPoint` rectangles loads a new `World` at the designated spawn. J-key within NPC interaction range opens dialogue.

**Key Classes**:
- `objects/Player.java` — movement, step counter, encounter logic, transition detection
- `keyInput/PlayerKeyInput.java` — WASD/arrow → direction + state

**Flow**: Key held → `PlayerKeyInput` sets direction → `Player.update()` moves by velocity → tile collision resolved → if grass tile + step complete → roll encounter → if hit: set `GameState.Transition` (then `Battle`)

**Dependencies**: `objects/World`, `objects/EntityManager`, `framework/Handler`, `framework/MapTransitionPoint`, `battle/BattleManager`, `framework/pokemon/PokemonGenerator`

---

## 5. Pokemon System

**Purpose**: Model Pokemon as live game objects with stats, moves, sprites, and experience. Provide a database of base templates and a generator for instances.

**How it works**: `PokemonDatabase` loads all templates from JSON on startup. `PokemonGenerator` copies a template into a new `Pokemon` instance with randomized IVs and level-appropriate moves. Stats use the Gen 3+ formula (`recalculateStats()`). `ExperienceCalculator` implements all 6 growth curves using the Gen 6 formula.

**Key Classes**:
- `objects/pokemon/Pokemon.java` — live instance: stats, IVs, moves (List<PokemonMove>), sprites, current HP/exp
- `objects/pokemon/PokemonMove.java` — name, type, power, accuracy, category, current/max PP
- `objects/pokemon/PokemonFrontSprite.java` / `PokemonBackSprite.java` — battle sprites extracted from spritesheet
- `framework/pokemon/PokemonDatabase.java` — loads and caches templates from `pokemon_base_stats.json`
- `framework/pokemon/PokemonGenerator.java` — creates wild or named instances from database templates
- `framework/pokemon/ExperienceCalculator.java` — all 6 growth curves, Gen 6 formula

**Data Files**:
- `resources/pokemon_base_stats.json` — ~251 Pokemon entries (Gen 1 + Gen 2, excludes Mew); each entry contains: dexNumber, name, sprite col/row, base stats (HP/Atk/Def/SpAtk/SpDef/Spe), moveset with level learned, types, growthRate, captureRate, baseHappiness, eggGroup, description
- `resources/sprites/kanto_pokemon_front_sprites.png` — tiled Gen 1 front battle sprites
- `resources/sprites/kanto_pokemon_back_sprites.png` — tiled Gen 1 back battle sprites
- `resources/sprites/johto_pokemon_front_sprites.png` — tiled Gen 2 front battle sprites
- `resources/sprites/johto_pokemon_back_sprites.png` — tiled Gen 2 back battle sprites

**Flow**: `PokemonDatabase.initDatabase()` parses JSON → templates stored → `PokemonGenerator.generatePokemon(name)` → copies base stats, assigns random IVs, filters moves by level, creates sprite instances → returns live `Pokemon`

**Dependencies**: `framework/SpriteSheet`, `json-simple-1.1.1.jar`, `framework/enums/Type`, `framework/enums/MoveCategory`, `framework/enums/ExpType`

---

## 6. Battle System

**Purpose**: Execute turn-based battles using the Gen 3+ damage formula, sequenced through an event queue for animations and text.

**How it works**: `BattleManager` (singleton) owns `battleEventQueue: Queue<BattleEvent>`. Each turn, `setupBattleTurns()` orders attacker/defender by Speed, then enqueues events: `TextEvent` (move used), `HPAnimationEvent` (HP drain), `TextEvent` (damage/faint messages), `ExpAnimationEvent` (on win). `BattleScreen` polls and renders one event at a time. States advance through `BattleScreenState` enum.

**Key Classes**:
- `battle/BattleManager.java` — singleton; damage calc, turn sequencing, exp gain, state machine
- `battle/event/BattleEvent.java` — abstract queued event (`update()`, `render()`, `isFinished`)
- `battle/event/TextEvent.java` — displays dialogue text
- `battle/event/HPAnimationEvent.java` — animates HP bar drain
- `battle/event/ExpAnimationEvent.java` — animates experience gain
- `battle/event/TrainerSummonPokemonEvent.java` — Pokeball throw + entrance animation
- `battle/event/PokemonFaintEvent.java` — fainting animation
- `framework/TypeTable.java` — 18×18 type effectiveness matrix (immune/resist/neutral/weak)

**Damage Formula** (Gen 3+):
```
damage = floor(floor((2×level/5+2) × power × (atk/def) / 50) + 2) × STAB × typeEffectiveness × variance
```
- STAB: 1.5× if move type matches attacker's type
- Type effectiveness: 0, 0.5, 1, or 2 (dual types multiply)
- Variance: random 0.85–1.0

**Flow**: Encounter triggered → `BattleManager.init()` → enqueue intro events → `BattleScreenState.BattleOptionSelect` → player selects move → `setupBattleTurns()` → damage events enqueued → HP animation → faint/win check → exp events → next turn or end

**Dependencies**: `objects/pokemon/Pokemon`, `framework/TypeTable`, `framework/SpriteSheet`, `framework/SoundManager`, `screen/BattleScreen`, `ui/PlayerHud`, `ui/TrainerHud`, `ui/BattleOptions`, `ui/MoveSelectBox`

---

## 7. Map / World System

**Purpose**: Load, represent, and render tile-based maps with layers, collision, NPCs, spawn points, and transition exits.

**How it works**: `TileMapLoader` parses Tiled-exported JSON files. Maps have 4 rendering layers + 1 collision layer. ObjectGroups in the JSON define transition rectangles (map exits), named spawn points, and NPC spawn positions. Coordinates are scaled from Tiled's 16px tile size to the game's 80px tile size (5× scale). `Camera` follows the player and culls off-screen tiles.

**Key Classes**:
- `objects/World.java` — holds all 5 tile layers, `EntityManager`, `Camera`, transitions, spawn points, and NPC list
- `framework/TileMapLoader.java` — JSON parser; produces tile arrays, transition points, spawn points, NPC spawns
- `framework/Camera.java` — player-following camera with map-boundary clamping
- `framework/MapTransitionPoint.java` — bounds + target location + target spawn name
- `framework/MapSpawnPoint.java` — named spawn point with tile coordinate
- `framework/MapNpcSpawn.java` — NPC ID + tile position for spawning

**Map Data Files** (Tiled JSON + TMX pairs):
- `resources/first_town.json` — overworld starting area; grass tiles, buildings, transition exits
- `resources/player_house.json` — player house interior
- `resources/pokemon_center.json` — Pokemon Center interior with nurse NPC
- `resources/poke_mart.json` — Poke Mart interior with merchant NPC
- `resources/pokemon_crystal_tileset.png` — shared tileset for all maps (Gen 2 GBC style)
- `resources/CollisionLayer.png` — special tileset used for the collision layer

**Flow**: Map transition detected → `Handler.setPendingWorld(new World(location, spawnName))` → `TransitionScreen` plays → `Handler.setWorld(pendingWorld)` → `GameScreen` renders new `World`

**Render Layer Order**: Layer1 (ground) → Layer2 (objects) → Entities (player, NPCs) → Layer3 (foreground overlay)

**Dependencies**: `json-simple-1.1.1.jar`, `framework/SpriteSheet`, `framework/npc/NpcDatabase`, `framework/SpawnManager`, `objects/EntityManager`

---

## 8. Event / Interaction System

**Purpose**: Track world state through flags/variables, display NPC dialogue, and enable context-sensitive NPC responses.

**How it works**: `EventFlagManager` holds a `Set<String>` of boolean flags and a `Map<String, Integer>` of named variables. NPCs check flags via `resolveDialogue(EventFlagManager)` to return the appropriate dialogue lines. `DialogueScreen` renders multi-line text boxes with advance-on-confirm behavior. `SpawnManager` reads NPC spawn data from the map and creates `NPC` entity instances at startup.

**Key Classes**:
- `framework/EventFlagManager.java` — `setFlag`, `clearFlag`, `hasFlag`, `setVariable`, `getVariable`
- `framework/EventFlags.java` — string constants for all flag IDs (needs full population)
- `framework/DialogueScreen.java` — line-by-line text rendering with input gating
- `framework/npc/NpcDatabase.java` — loads NPC definitions from `npc_database.json`
- `framework/npc/NpcData.java` — name, direction, isTrainer, dialogue branches, sprite position, interaction range
- `objects/NPC.java` — entity with identity; `resolveDialogue()`, `getInteractionZone()`
- `framework/SpawnManager.java` — singleton; reads map NPC spawns, creates `NPC` entities

**Data Files**:
- `resources/npc_database.json` — array of NPC definitions: npcId, name, isTrainer, direction, spriteStartCol/Row, interactionRange, dialogue (branched by event flags)
- `resources/sprites/npc_sprite_sheet.png` — tiled NPC walk animations (shared static SpriteSheet)

**Flow**: Player presses J near NPC → `Player` checks `NPC.getInteractionZone()` → `npc.resolveDialogue(flagManager)` returns lines → `DialogueScreen.setDialogue(lines)` → `GameState.Dialogue` → player advances → `GameState.Game`

**Dependencies**: `framework/EventFlagManager`, `framework/Handler`, `keyInput/DialogueKeyInput`, `framework/enums/GameState`

---

## 9. Input Handling

**Purpose**: Route keyboard events to the correct context-specific handler based on the current `GameState`.

**How it works**: `GameKeyInput` extends `KeyAdapter` and is attached to the AWT `Canvas`. On each key event it reads `Game.gameState` and delegates to the matching handler. A 20-frame cooldown prevents repeated triggers from held keys in menus. Each handler is stateless relative to the game — it only mutates external state (player direction, menu cursor, etc.).

**Key Classes**:
- `keyInput/GameKeyInput.java` — router; dispatches by `GameState`
- `keyInput/PlayerKeyInput.java` — WASD/arrows → player direction + state
- `keyInput/BattleKeyInput.java` — arrows + J/K → battle menu and move selection
- `keyInput/MenuKeyInput.java` — pause menu navigation
- `keyInput/PokemonMenuKeyInput.java` — party slot selection (6 slots)
- `keyInput/PokemonSummaryKeyInput.java` — Pokemon switching, move viewing
- `keyInput/BagKeyInput.java` — pocket and item navigation
- `keyInput/DialogueKeyInput.java` — advance dialogue on J

**Flow**: OS key event → `GameKeyInput.keyPressed/keyReleased` → read `gameState` → delegate to active handler → handler mutates game state or triggers transition

**Dependencies**: `framework/enums/GameState`, `objects/Player`, `battle/BattleManager`, `framework/Handler`

---

## 10. Audio System

**Purpose**: Play one-shot sound effects and looping location/battle music with crossfade support.

**How it works**: `SoundManager` pre-loads `.wav` files into static maps. Sound effects play immediately from the start on each call. Music is cached as `Clip` objects and managed by `MusicManager`, which drives crossfades over ~80 frames (~1.3 s). Battle music hard-cuts over location music. Victory fanfare plays after battle win, then crossfades back to location music.

**Key Classes**:
- `framework/SoundManager.java` — static maps for sound effects and music clips; volume control via `MASTER_GAIN`
- `framework/MusicManager.java` — state machine (`IDLE`, `PLAYING_LOCATION`, `CROSSFADING`, `PLAYING_BATTLE`, `PLAYING_FANFARE`, `FANFARE_TO_LOCATION`); `update()` drives fades

**Sound Files** (`resources/sounds/`):
- `button_sound.wav`, `menu_sound.wav` — UI feedback
- `johto_wild_pokemon_battle.wav` — wild battle theme
- `rival_battle.wav` — trainer battle theme
- `victory_wild_pokemon.wav` — victory fanfare
- `running_away_sound.wav`, `fainted_sound.wav`, `low_health_sound.wav` — battle cues
- `azalea_city.wav`, `pokemon_center_soulsilver.wav`, `poke_mart_soulsilver.wav` — location music

**Flow**: Location change → `MusicManager.playLocationMusic(location)` → crossfade to new clip. Battle start → `MusicManager.playBattleMusic(isWild)` → hard-cut. Battle end → fanfare → crossfade back to location.

**Dependencies**: `javax.sound.sampled`, `framework/enums/Location`

---

## 11. Item / Bag System

**Purpose**: Store and display the player's items and Pokéballs in a two-pocket inventory.

**How it works**: `ItemDatabase` loads all item definitions from JSON at startup. `Bag` holds two `List<Item>` pockets (items, Pokéballs). Items are currently display-only — no quantity decrement or use logic exists yet. `BagScreen` renders pocket contents with sprite icons.

**Key Classes**:
- `objects/Item.java` — name, description, category, sprite col/row
- `objects/Bag.java` — `itemPocket`, `pokeballPocket`; `addItem()`, `addPokeball()`
- `framework/ItemDatabase.java` — loads `item_database.json`; maps name → `Item`

**Data Files**:
- `resources/item_database.json` — array of item definitions: name, description, col, row, category (Item or Pokeball)
- `resources/sprites/item_sprite_sheet.png` — tiled item icons

**Flow**: Game start → `ItemDatabase.initDatabase()` → `Bag` constructed with starter items → `BagScreen` renders from `handler.getBag()` pockets

**Dependencies**: `framework/ItemDatabase`, `framework/enums/ItemCategory`, `screen/BagScreen`, `keyInput/BagKeyInput`

---

## 12. UI System

**Purpose**: Render in-world HUDs for battle and exploration, plus menu/summary/bag screens.

**How it works**: Battle UI components (`PlayerHud`, `TrainerHud`, `BattleOptions`, `MoveSelectBox`) are owned by `BattleScreen` and rendered in layers. Each component reads state from `BattleManager`. Exploration HUD is minimal (no persistent overlay yet). Menu screens are full-screen overlays rendered instead of `GameScreen`.

**Key Classes**:
- `ui/PlayerHud.java` — player Pokemon: HP bar, level, name, type symbols
- `ui/TrainerHud.java` — opponent Pokemon: HP bar, level, name, type symbols
- `ui/BattleOptions.java` — Fight / Bag / Pokemon / Run menu
- `ui/MoveSelectBox.java` — 4-move grid with type, power, PP, category icon
- `screen/PokemonMenuScreen.java` + `screen/PokemonMenuTile.java` — party list with HP bars and sprite thumbnails
- `screen/PokemonSummaryScreen.java` — full stat page: IVs, moves, types, OT, exp
- `screen/BagScreen.java` — two-pocket item list with sprite icons
- `screen/MenuScreen.java` — pause menu list (Save, Load, Pokemon, Bag, Settings, Quit)
- `framework/DialogueScreen.java` — reusable text box for NPC dialogue and battle text

**Fonts & HUD Assets**:
- `resources/font/PokemonFont.ttf` — custom bitmap-style Pokemon font
- `resources/hud/type_symbols.png` — 18 type badge icons
- `resources/hud/hp_symbol.png`, `exp_symbol.png` — stat bar labels
- `resources/hud/status_icons.png` — status condition badges (PSN, PAR, SLP, etc.)
- `resources/hud/menu_pointer_sprite_sheet.png` — animated cursor sprite

**Flow**: `GameState` determines active screen → `BattleScreen.render()` calls each HUD component in order → HUD reads from `BattleManager` for current values

**Dependencies**: `battle/BattleManager`, `framework/Handler`, `framework/SpriteSheet`, `framework/TypeTable`

---

## 13. Save / Load System

**Purpose**: Persist and restore all player progress (party, position, bag, event flags, PC boxes).

**How it works**: **Not yet implemented.** The architecture is designed in `CLAUDE.md`. A `SaveManager` singleton will serialize state to `%APPDATA%/PokemonClone/save.json` and deserialize it by reading species names from the JSON, fetching base data from `PokemonDatabase`, and constructing new `Pokemon` instances with saved level, HP, IVs, and moves. The database is never mutated during load.

**Planned Classes**:
- `framework/SaveManager.java` — serialization/deserialization of full save state
- `framework/EventFlagManager.java` — already implemented; will be serialized as part of save

**Planned Save Format** (JSON at `%APPDATA%/PokemonClone/save.json`):
- `player` — position (x, y), location name, money
- `party` — array of Pokemon: species, level, currentHp, currentExp, IVs, moves (name + currentPP)
- `bag` — items and Pokéballs with quantities
- `pc` — box array with nullable Pokemon slots
- `flags` — trainersDefeated, legendariesCaught, eventsCompleted, variables map

**Build Order**: (1) party + player position → (2) bag → (3) event flags → (4) trainer defeat flags → (5) PC boxes

**Dependencies**: `framework/pokemon/PokemonDatabase`, `framework/EventFlagManager`, `objects/Bag`, `objects/Player`, `json-simple-1.1.1.jar`

---

## Appendix: Key Enumerations

| Enum | Values |
|---|---|
| `GameState` | Loading, Game, Menu, Dialogue, Transition, Battle, PokemonMenu, PokemonSummary, Bag |
| `Location` | World, PlayerHouse, PokemonCenter, PokeMart |
| `EntityDirection` | UP, DOWN, LEFT, RIGHT |
| `EntityState` | Standing, Walking, Running (unused) |
| `ObjectId` | Player, NPC, GrassTile, RestrictedTile, … |
| `Type` | Normal, Fire, Water, Grass, Electric, Ice, Fighting, Poison, Ground, Flying, Psychic, Bug, Rock, Ghost, Dark, Dragon, Steel, Fairy |
| `MoveCategory` | Physical, Special, Status |
| `ItemCategory` | Item, Pokeball |
| `ExpType` | Erratic, Fast, MediumFast, MediumSlow, Slow, Fluctuating |

---

## Appendix: Source Tree (Top-Level)

```
src/
├── GameDriver.java
├── ui/            — Game loop, Window, battle HUD components
├── screen/        — Screen subclasses (one per GameState)
├── objects/       — Entity, Player, NPC, Tile, World, Item, Bag, Pokemon types
├── battle/        — BattleManager + event/ subpackage
├── framework/     — Handler, Camera, SpriteSheet, Animation, databases, managers
│   ├── pokemon/   — PokemonDatabase, PokemonGenerator, ExperienceCalculator
│   ├── npc/       — NpcDatabase, NpcData
│   └── enums/     — all enum types
└── keyInput/      — GameKeyInput router + per-state handlers

resources/
├── pokemon_base_stats.json
├── item_database.json
├── npc_database.json
├── font/
├── sprites/
├── hud/
├── sounds/
└── *.json / *.tmx  — map files (first_town, player_house, pokemon_center, poke_mart)
```
