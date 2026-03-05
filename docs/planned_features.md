# Features

## Core Gameplay (Build After Bugs Fixed)
These make the game feel like an actual game. Build in roughly this order.

### NPC Dialogue System
- **Current bugs to fix first**:
  - `DialogueScreen.dialogueText` is hardcoded as a single rival string — replace with a `startDialogue(List<String> pages, String speakerName)` entry point
  - `font.deriveFont(48f)` and `new BasicStroke(5)` created every `render()` call — cache in constructor
  - `getWrappedText()` recalculated every frame — compute once when page changes; re-run only on page advance
  - `DialogueKeyInput` J press immediately sets `GameState.Game` — no multi-page advancement; replace with `if (currentPage < pages.size() - 1) currentPage++ else exitDialogue()`
- **Multi-page flow**: `DialogueScreen` holds `List<String> pages` and `int currentPage`; J advances or exits
- **Speaker name box**: small box at top-left showing NPC name; driven by `speakerName` passed to `startDialogue()`
- **Typewriter effect**: `charIndex` increments each tick at a rate driven by text speed setting; J skips to full text
- **Choice prompts**: `DialoguePage` class with `text`, `isChoice`, `choiceOptions[]`, `choiceOutcomes[]` (outcome = event flag); routes J-key to selection logic
- **Event-aware dialogue**: NPC dialogue text varies by checking event flags before building `pages` list

### Battle Loss Flow
- Player faints → `BattleLoss` state → lose half money → fade transition → spawn at last Pokemon Center with full party healed
- Also: restore party to full HP/PP when entering Pokemon Center

### Pokemon Center
- Nurse Joy NPC triggers dialogue → "Your Pokemon are healed!" → full HP/PP restoration for all party members
- Logical save point

### Starter Selection Screen
- New game flow: choose from three starter Pokemon before entering the world
- Replace the hardcoded Charmander/Totodile party in `Game.getPlayerParty()`

### Location-Based Music
- Add `musicPath` field to `Location` enum so each location owns its track
- Pre-load all music at startup; trigger on map transition completion
- Battle music plays on transition to Battle state; location music resumes on return to Game

### Trainer System
- `Trainer` data class + `TrainerDatabase` (JSON-driven)
- `BattleManager.init()` accepts a `Trainer` object; separate wild vs trainer init paths (differ in flee rules, exp, and intro)
- Fixed-position trainers on maps defined in JSON with party, pre/post-battle dialogue, and defeat event flag

### Overworld Item Pickups
- Glowing Pokeball objects on map containing items; new `ObjectId.OverworldItem`
- Item and quantity defined in `map_items.json` keyed by map name and tile coordinates
- One-time pickup tracked by unique event flag; `SpawnManager` skips rendering if flag is set
- Brief "Got [Item]!" dialogue using `DialogueScreen`
- Hidden items: invisible until player steps on tile; Dowsing Machine key item causes nearby hidden items to flash

### Menu Implementations (Scaffolded, Needs Wiring)
- **Pokedex** (`case 0` in `MenuKeyInput`) — currently `System.out.println`
- **ID Card** (`case 3`) — player name, ID number, playtime
- **Save** (`case 4`) — wire to `SaveManager`
- **Options** (`case 5`) — wire to Settings screen

### Settings Screen
- `Settings` GameState; persisted to `settings.json`
- Settings: music volume, SFX volume, fullscreen toggle, text speed (slow/medium/fast), controller toggle
- `SettingsManager` singleton loads on startup, writes on any change

### Title Screen & Launcher
- **Title screen**: full-screen title background, "Press any button to start"
- **Title menu**: PLAY / UPDATE / EVENT options after version check
- **`version.json`** on GitHub: version string, download URL, SHA-256 checksum, event block
- **Launcher** (`PokemonLauncher.exe`): checks `version.json`, compares to local `version.txt`, downloads update if needed, verifies SHA-256, then launches game; invisible on happy path
- **Release workflow**: build new `PokemonGame.exe` → upload to GitHub releases → update `version.json` → push; players get it automatically on next launch
- **Crash reporting**: game writes uncaught exceptions and warnings to `%APPDATA%/PokemonClone/error.log` (append mode); on next launcher startup, if `error.log` is non-empty the launcher POSTs its contents as a GitHub issue via the GitHub REST API (using an embedded token with issue-create-only scope); report includes stack trace, game version from `version.txt`, and OS info; launcher clears the log after a successful POST to prevent duplicate reports; player needs no GitHub account and takes no action — reporting is fully automatic

---

## Planned Features

### Battle Enhancements
- **Battle intro animation**: GBC-style slide-in — trainer and player sprites scroll off screen, Pokeball throw animation, wild Pokemon silhouette reveals; needs trainer back sprite sheet and wild Pokemon silhouette sprites; `BattleIntroductionEvent` is already the right place to drive this once sprites exist
- Pokemon switching during battle
- Bag/item use during battle
- Proper wild flee formula — Gen 2 speed-based calculation
- Pokemon catching — Pokeball throwing animation, Gen 2 capture formula (HP + catch rate + ball modifier)
- Battle move animations: `MoveAnimationEvent` between damage text and `HPAnimationEvent`; start simple (screen flash for special, shake for physical), add per-move sprites later

### Pokemon Progression
- **Evolution**: evolution data in database (evolves-into species, trigger: level/item/trade); check on level-up; evolution screen with animation and stat recalculation
- **Move learning on level-up**: check if new move available at new level; if 4 moves already known, show "learn/forget" selection screen
- **Shiny Pokemon**: `shiny` field already exists; wire up 1/8192 random roll on generation; alternate sprite if shiny sprite sheets added
- **Gender**: `gender` field already exists; assign on generation using species gender ratio; display on summary screen
- **Ability and nature**: fields exist but unused; wire into battle calculations

### World & Overworld
- **Pokemon following player** (HG/SS style): first party Pokemon walks one tile behind player; mirrors last direction; J while facing it shows friendship-based dialogue; uses existing `Animation` class
- **Weather system**: `WeatherType` enum per route; overworld particle/overlay effect; `BattleManager` reads route weather and applies Gen 3+ damage modifier; weather set by moves (Rain Dance, etc.)
- **Fishing**: `FishingRod` key items (Old Rod, Good Rod, Super Rod); J facing water tile triggers timed button-press minigame; success rolls rod-specific encounter table
- **Rival/scripted trainer encounters**: fixed trainers that block path until defeated; defined in JSON with location, party, dialogue, defeat event flag; `SpawnManager` skips if defeat flag set

### Trainer Scaling & Difficulty
- **Trainer scaling**: each trainer JSON has `areaFloor`, `areaCeiling`, `scalingMode`, and per-Pokemon `levelOffset`; `TrainerScaler.scale(trainerData, playerParty)` at battle start resolves final levels
- **Scaling modes**: `average`, `highest`, `weighted` (best default — won't punish players for carrying a weak HM slave)
- **Boss bonus**: gym leaders, rivals, Elite Four get flat `bossBonus` on top of scaled result
- **Trainer JSON format**:
  ```json
  {
    "name": "Bug Catcher Joey",
    "scalingMode": "weighted",
    "areaFloor": 5,
    "areaCeiling": 18,
    "bossBonus": 0,
    "party": [
      { "species": "Caterpie", "levelOffset": 0 },
      { "species": "Weedle", "levelOffset": -1 }
    ]
  }
  ```
- **Rematch scaling**: defeated trainers recalculate against current party when re-challenged (VS Seeker)
- **`DifficultyLevel` enum**: `Easy`, `Normal`, `Hard`; chosen at new game start, stored in save; changeable from Settings

  | Setting | Easy | Normal | Hard |
  |---------|------|--------|------|
  | Scaling mode | `average` | `weighted` | `highest` |
  | Boss bonus | 0 | +2 | +4 |
  | Area ceiling | −2 | default | +3 |
  | Trainer AI | random | semi-optimal | optimal (best typed move via `TypeTable`) |
  | Money loss on blackout | disabled | half | all |
  | Catch rate | boosted | normal | normal |
  | Exp gain | 1.5× | 1.0× | 0.85× |

- **Nuzlocke mode**: separate toggle (not a difficulty tier); enforces first-catch-per-route, permanent faint, and mandatory nicknames — all three rules mechanically enforced

### Pokemon Dens (Raid System)
- **Den objects**: new `ObjectId.Den` on map; interact prompts "A strong Pokemon is hiding!"; locations in `den_database.json` with `starRating` (1–5), `possiblePokemon`, and `respawnHours`
- **Star rating**:

  | Stars | Level offset | Guaranteed IVs | Shield phases |
  |-------|-------------|----------------|---------------|
  | 1★ | +0 | 1 | 0 |
  | 2★ | +5 | 2 | 1 |
  | 3★ | +10 | 3 | 1 |
  | 4★ | +15 | 4 | 2 |
  | 5★ | +20 | 5 (flawless) | 2 |

- **Boosted HP**: raid Pokemon HP × `raidHpMultiplier` (e.g. 3× for 5★)
- **Shield mechanic**: at certain HP thresholds Pokemon raises shield for N turns; damage reduced to 1 until broken; `RaidShieldEvent` in battle queue
- **No fleeing**: must win or lose
- **Guaranteed catch**: one free throw at end regardless of outcome; boosted catch rate
- **Rewards**: star-rated item drops (TMs, rare candies, held items, evolution stones) + **Watts** (den-specific currency)
- **Watts economy**: spend at Watts Trader NPC for rare held items, special Poke Balls, den-exclusive TMs
- **Respawn**: cleared dens tracked via event flags; `TimeManager` checks elapsed time against `respawnHours`

### Day/Night & Time System
- **`TimeManager` singleton**: reads `java.time.LocalTime` (no libraries needed); converts to `TimePeriod` enum: `Morning` (6–9am), `Day` (9am–6pm), `Evening` (6–9pm), `Night` (9pm–6am)
- **Screen tint overlay**: semi-transparent `fillRect` over world after all layers; Morning = pale warm yellow (low alpha), Day = none, Evening = soft orange (low alpha), Night = deep blue (medium alpha)
- **Encounter table filtering**: `TimePeriod` passed into encounter roll so certain Pokemon only appear at night (Hoothoot, Gastly) or morning (Caterpie, Pidgey)
- **NPC schedules**: NPCs can have an allowed `TimePeriod` list; `EntityManager` skips spawning NPCs outside their schedule
- **Music shift**: alternate track at night; time period passed to `SoundManager` when selecting which track to play
- **Time-gated events**: legendary encounters, special traders, story triggers check `TimeManager.getCurrent()`

### Economy & Progression
- **Money loss on blackout**: lose half money when party faints, before teleport to last Pokemon Center
- **PokeMart system**: shop NPCs with JSON-driven inventory; different stock per town; reuses bag screen layout
- **Move Tutors**: NPC teaches a move for a fee; data in JSON (move name, cost, compatible species); plugs into move-learning screen
- **Pokemon Daycare**: flat drop-off fee + per-step fee; step counter in `Player.java`; exp proportional to steps; foundation for breeding
- **Bike Shop**: purchase Bicycle for large sum or story reward; until bought, Bike option in bag does nothing
- **Ferry/travel tickets**: pay NPC at dock to sail to new island; can alternatively be key items from story events
- **Pokemon Lottery (Lotto-ID)**: daily lottery NPC checks party trainer IDs; prizes for matching 1–5 digits
- **Haircut/grooming NPC**: pay fee, select party Pokemon; raises friendship by fixed amount

### Procedural World Generation (Hybrid)
No mainline Pokemon game has done this. The world wilderness is procedurally generated; towns, gyms, and story-critical locations are hand-crafted anchors placed within it. Inspired by Core Keeper's biome approach.

**Architecture decision**: commit to this early — hand-crafting more Tiled maps before switching to proc gen wastes that work. Replaces `TileMapLoader` for the overworld; town/dungeon interiors can still use Tiled JSON.

#### Generation Approach
- **Perlin/Simplex noise** to define biome regions (height + moisture map → biome type)
- **Tile adjacency rules** for clean borders (water needs a sand border, forest needs a treeline transition) — simplified Wave Function Collapse; full WFC is complex, noise + border rules gets 90% of the visual quality at 20% of the complexity
- **World seed**: stored in `save.json`; same seed always regenerates the same world
- **Seed selection at new game**: player chooses — enter a specific seed (static, shareable) or leave blank for a random one; random seed generated via `UUID.randomUUID()` or `new Random().nextLong()`; displayed on the save screen so the player can note it down and share it

#### Biomes

| Biome | Tiles | Pokemon types |
|-------|-------|--------------|
| Plains | tall grass, paths, flowers | Normal, Flying |
| Forest | dense trees, clearings, ponds | Bug, Grass, Normal |
| Mountain | rock faces, caves, ledges | Rock, Ground, Fighting |
| Ocean/Lake | water, sand beaches | Water, Ice |
| Swamp | murky water, mud, dead trees | Poison, Water, Ghost |
| Volcano | ash tiles, lava cracks | Fire, Rock |
| Tundra | snow, ice, frozen ponds | Ice, Steel |

#### Chunk-Based Loading
- World divided into chunks (e.g. 32×32 tiles); only generate/load chunks near the player
- `WorldGenerator.generateChunk(chunkX, chunkY, seed)` produces tile data at runtime — replaces `TileMapLoader` for the overworld
- Only save **modified** chunks to `world.json` (items picked up, dens cleared, etc.); regenerate unmodified chunks from seed on load
- Chunk rendering: `World.renderTileLayer()` iterates visible chunks instead of one flat tile array
- Collision derived from tile type rather than a separate Tiled layer — simpler once the shift is made

#### Hybrid: Proc Gen Wilderness + Hand-Crafted Anchors
- **Proc gen**: biome wilderness, route-style tall grass zones, cave entrances, water routes, trainer encounter zones, overworld item pickups, Pokemon dens (placed in matching biomes)
- **Hand-crafted anchors**: towns placed at fixed world coordinates; gyms, Pokemon Centers, PokeMarts inside towns; story-critical dungeons; key NPCs
- Story nodes tied to **biome presence** rather than fixed map positions — e.g. the fire gym appears in the nearest Volcano biome town; story flexes around whatever world the seed generated
- Legendaries tied to specific biomes: Ho-Oh appears above Volcano biome, Suicune roams Tundra/Plains, Lugia in deep Ocean

#### Integration with Other Systems
- Biome type drives encounter table; time-of-day filtering stacks on top
- Pokemon dens spawn at seeded positions within matching biomes
- Event flags track which biome anchors have been visited/unlocked (for Charizard Glide fast travel)
- Weather system sets default weather per biome (Rain in Swamp, HarshSun in Volcano, Hail in Tundra)

### Field Moves & Exploration Gating
- **Poke Ride system** (Gen 7, no HM slaves): unlock via story progression; stored as boolean flags in save
  - **Tauros Charge**: breaks `CuttableTile` trees and `SmashableRock` tiles
  - **Lapras Paddle**: movement over `WaterTile`
  - **Machamp Shove**: pushes strength boulders (puzzle mechanic); World needs a mutable object layer saved to save.json
  - **Sharpedo Blast**: fast water travel
  - **Charizard Glide**: fly to any previously visited town; reads visited-town flags from save
- **New `ObjectId` values**: `WaterTile`, `CuttableTile`, `SmashableRock`, `StrengthBoulder`, `HiddenItem`, `DenTile`, `SoilTile`
- **Dark caves**: small visibility circle around player; rest of screen black; toggled by `darkCave` flag on `Location` enum

### Environment Animations
- **Ambient tile animations** (grass swaying, water ripples, flowers, trees): optional `Animation animation` field on `Tile`; if non-null, `update()` advances it and `render()` draws `animation.getCurrentFrame()`; World needs a tile update pass; ~150–250ms per frame for GBC feel
- **Triggered animations** (doors opening): door animation plays once on `DoorTile` contact before map transition; needs a small `WorldEvent` queue in `World` similar to the battle event queue
- **Tiled integration**: Tiled's JSON export includes an `"animation"` array per tile with frame IDs and durations; `TileMapLoader` currently ignores this; reading it would let Tiled drive which tiles animate

### Battle Move Animations
- Each move plays a visual effect before HP drain — flash, projectile, slash, etc.
- `MoveAnimationEvent` runs between damage text and `HPAnimationEvent` in the battle event queue
- Start simple (screen flash for special, shake for physical); add per-move sprites later
- Move category from `PokemonMove.getMoveCategory()` drives default animation style

### Player Bike
- `onBike` boolean on `Player`; toggled by a key (e.g. B); requires Bicycle in bag
- Increase `Player.speed` while `onBike`; collision and encounter logic unchanged
- Separate `Animation` objects for bike directions; needs bike sprite frames added to player sprite sheet
- Looping bike music on mount; resume overworld music on dismount
- `bikeAllowed` flag on `Location` enum to restrict indoors

### Controller Support
- Plain Java has no gamepad API — requires `JInput` (lightest option: just a JAR + native DLLs, poll-based, fits 60fps loop)
- **Input abstraction**: `InputSource` interface with `isUp()`, `isDown()`, `isLeft()`, `isRight()`, `isConfirm()`, `isCancel()`, `isMenu()`; `KeyboardInput` and `ControllerInput` both implement it; `GameKeyInput` holds whichever is active
- Default mapping: left stick / D-pad → movement, A → confirm (J), B → cancel (K), Start → menu (Enter)
- Always apply a ~0.2f dead zone to analog axes to prevent drift
