# High Priority Fixes

**Part of claude.md instructions for Sonnet/Opus**

**Focus on Proc Gen First**

## Proc Gen Prerequisites

Work through these in order before starting the map generator. Each step unblocks the next.

### Hard Blockers
*Proc gen cannot function without these.*

1. **EventFlags class** — `Set<String>` boolean flags + `Map<String, Integer>` integer variables; centralize all flag ID strings as constants; needed by trainer defeat tracking and every story trigger; build this first — it's cheap and painful to retrofit
   - `src/framework/EventFlags.java` (new file)
   - Example constants: `ROUTE_30_BUG_CATCHER_1`, `SUDOWOODO_BLOCKED`, `GYM_1_BADGE`

2. **NPC data model + TrainerDatabase** — `NPC` currently has no identity (no name, no trainerId, no dialogue, no defeated flag); add a `trainerId` field and a `TrainerDatabase` that loads `trainer_database.json`; proc gen picks trainer IDs from zone pools — those IDs need somewhere to resolve to
   - `resources/trainer_database.json` (new file) — trainer id, name, class, dialogue before/after, party (species + level)
   - `src/framework/TrainerDatabase.java` (new file)
   - Update `src/objects/NPC.java` with `trainerId`, `name`, `defeatFlag` fields

3. **MapEntitySpawn pipeline** — replace hardcoded NPC pixel coords with a data-driven spawn system; Tiled's Object Layer exports NPC markers with custom properties (`trainerId`, `facing`) in the map JSON; `TileMapLoader` reads the object layer and returns `List<MapEntitySpawn>`; `World` creates NPCs from that list using tile coords converted to pixels; proc gen outputs the same `MapEntitySpawn` format so the downstream pipeline is identical for both static and generated maps
   - `src/framework/MapEntitySpawn.java` (new record: `tileX`, `tileY`, `facing`, `trainerId`, `roaming`)
   - Update `TileMapLoader` to read the object layer
   - Update `World` to create NPCs from the spawn list

4. **Static town with Pokemon Center** — routes need to connect to something; Nurse Joy NPC restores all HP and PP across the party; without healing, multi-route exploration and proc gen routes with trainers are untestable
   - Add a town map (Tiled) with a Pokemon Center building
   - Nurse Joy: special NPC type, no trainerId, triggers heal dialogue + full party restore on J-press
   - Healing: iterate `handler.getPokemonParty()`, restore `currentHealth = maxHealth` and all move `currentPP = maxPP`

### Loop-Breakers
*Proc gen works but you can't meaningfully test or play through it.*

5. **Save system** — without saves, proc gen maps either need a fixed seed or regenerate every launch; trainer defeat flags become meaningless across sessions; spec already in `CLAUDE.md`
   - `src/framework/SaveManager.java` (singleton)
   - Save file: `%APPDATA%/PokemonClone/save.json`
   - Load order: player position → party → bag → event flags

6. **Pokemon catching (Pokeball mechanic)** — proc gen routes with grass tiles but no way to catch feels broken; needed before encounter rate and wild Pokemon pool tuning makes sense
   - Pokeball item action in `BagScreen` / `BagKeyInput` during battle
   - Catch rate formula (Gen 3+): `catchValue = (3 * maxHP - 2 * currentHP) * catchRate / (3 * maxHP)`
   - On catch: add to party or PC box, save to file

### Nice-to-Haves (after proc gen)
- Gym + badge progression
- Shops / item usage in battle
- Pokedex
- Starter selection screen

---

## Group 1 High Priority Fixes— Deferred (blocked on other systems)

1. `Game.getPlayerParty()` hardcodes Charmander and Totodile (`Game.java:257-267`) — `PokemonGenerator.createMyPokemon("Charmander")` and `createMyPokemon("Totodile")` are called directly; **fix**: replace with a starter selection screen (see `docs/FEATURES.md` Starter Selection), or as a short-term fix, load party from save file via `SaveManager`; either way, remove the hardcoded species names; **blocked on**: save system or starter selection screen

2. Battle menu options 2 (Pokemon) and 3 (Bag) have no J-key confirm handler yet — `BattleKeyInput.battleControls()` (`BattleKeyInput.java:74-81`) only handles `battleOptionId == 1` (Fight) and `battleOptionId == 4` (Run); **not a bug** — these are unimplemented features; **when ready**: add `else if (battleOptionId == 2) { /* switch to PokemonMenu with return-to-battle context */ }` and `else if (battleOptionId == 3) { /* switch to Bag with return-to-battle context */ }`; PokemonMenu and Bag screens will need a way to return to `Battle` state instead of `Game` state; **blocked on**: Pokemon switching and bag-in-battle features
