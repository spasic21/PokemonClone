# Future / QoL / Fun Features (Long-Term)
Long-term ideas and stretch goals. Build these after the core game is solid.

### Seasons
- `Season` enum: `Spring`, `Summer`, `Fall`, `Winter`; derived from real month via `TimeManager`
- Visual: seasonal color overlay stacked on time-of-day tint; Winter = snow particles, Fall = orange wash
- Gameplay: different Pokemon per season; some map paths blocked or opened seasonally
- Derive purely from real-world month so no save data needed

### Held Items
- `heldItem` field on `Pokemon`; set on generation for trainer Pokemon, found in overworld pickups, or equipped from bag
- Battle integration in `BattleManager.calculateDamage()`:
  - Stat boosters: Choice Band, Choice Specs, Life Orb
  - Type boosters: Charcoal, Mystic Water, etc. — 20% boost, data-driven
  - Recovery: Leftovers (1/16 HP per turn), Shell Bell (HP from damage dealt)
  - One-time: Focus Sash, Sitrus Berry, Lum Berry
  - Evolution items: Metal Coat, Dragon Scale, etc.
- Bag UI: add "Give Item" and "Take Item" when selecting an item in `PokemonMenu` state

### Berry System & Apricorns
- **Berry planting**: interact with `SoilTile` while holding a berry; growth tracked via `TimeManager`; harvest after `growthHours`
- **Battle berries**: Oran (10HP at 50%), Sitrus (25%), Lum (cure status), stat-boost berries at low HP — consumed automatically in battle
- **Apricorns** (Gen 2): shake Apricorn trees on specific routes (once per day per tree); bring to Kurt NPC for custom Poke Ball crafted overnight
  - Friend Ball, Love Ball, Heavy Ball, Fast Ball, Level Ball, Lure Ball, Moon Ball

### Move Reminder & Heart Scales
- Move Reminder NPC: relearn any move learnable at or below current level; costs one Heart Scale
- Heart Scales: found in overworld spots, dropped by Luvdisc (fishing), or bought at high morale
- UI: reuses move-learning screen built for level-ups

### Type Matchup Preview (QoL)
- In `MoveSelectBox`, show effectiveness indicator next to each move: `◆◆` super effective, `◆` neutral, `▽` not very effective, `✕` immune
- Gate behind progression: locked early game; unlocked after obtaining Pokedex or reaching a story milestone

### Pokemon Contests
- Five categories: Cool, Beautiful, Cute, Clever, Tough
- Add `contestType` and `contestAppeal` fields to `PokemonMove` in future `move_database.json`
- Flow: pay entry fee → select Pokemon → appeal rounds → judge scores → ribbon awarded
- Condition stat separate from battle stats; raised by feeding Poffins (berries cooked at Berry Blender NPC)
- Ribbons displayed on Pokemon summary screen

### Achievements
- `AchievementManager` singleton; achievements defined in `achievements.json`
- Stored as `Set<String>` in `save.json` — same pattern as event flags, nearly free to implement
- Pop-up notification on unlock; viewable from main menu
- Examples: catch 100 species, win 10 battles in a row, find a shiny, complete Pokedex, finish a Nuzlocke, clear 5★ den, reach Heroic morale

### Bug Catching Contest
- Available specific days (`TimeManager` checks day of week — Tue/Thu/Sat like Gen 2)
- Enter during contest hours; receive one Park Ball; 20-minute real-time limit
- Score = level × remaining HP ratio; submit to judge NPC; prizes for 1st/2nd/3rd

### Pokerus
- 1/21845 chance of contracting after any wild battle
- `pokerusStatus` enum on `Pokemon`: `None`, `Active`, `Cured`
- Active Pokerus doubles EV gain; spreads to adjacent party Pokemon after battles
- Cures automatically after 1–4 real days (tracked via timestamp in save); cured status permanently retains EV bonus

### Photo Mode
- Hold a dedicated key in overworld to enter Photo Mode — movement pauses, camera frame overlay appears
- Capture via `ImageIO.write()` to `%APPDATA%/PokemonClone/photos/` as PNG
- Photo Album screen in menu displays thumbnails in a grid
- Achievement hooks: "Photograph all 251 species", "Take a photo at night", "Catch a shiny on camera"

### Morale System
- `moraleScore`: integer in save, −100 to +100, starts at 0; managed by `MoraleManager` singleton
- Tiers:

  | Score | Tier | Visual |
  |-------|------|--------|
  | 75–100 | Heroic | Warm golden aura on player sprite |
  | 25–74 | Good | No change |
  | −24–24 | Neutral | No change |
  | −25–74 | Questionable | Subtle dark tint on player sprite |
  | −75–100 | Villainous | Dark aura on player sprite |

- **Raises morale**: completing story beats honorably, helping NPCs, winning trainer battles
- **Lowers morale**: running from every wild battle (small penalty per flee), losing repeatedly, selfish story choices
- **What morale affects**: friendship gain rate, NPC prices (discounts/markups), NPC dialogue, legendary access (Ho-Oh/Suicune only appear at Good or above), post-game content gating, Heart Scale availability
- **Morale decay**: score drifts slowly toward 0 over time — prevents stale extremes

### Game Corner
- Buy coins with money; spend coins on rare prizes (TMs, rare Pokemon, held items)
- Voltorb Flip minigame (no gambling controversy) or simple slot machine

### Online Leaderboard (Stretch Goal)
- Track: fastest Elite Four clear, highest win streak, most Pokemon caught, most shinies found, highest morale
- POST stats to lightweight hosted endpoint on save; leaderboard screen fetches top 10 per category

### Wonder Trade (Stretch Goal)
- Select a Pokemon from party or PC → submit to shared pool → receive a random Pokemon from another player
- Submissions sit in server-side queue; matched randomly; no need to be online simultaneously

### Multiplayer — Trading & PvP (Long-Term)
- **Prerequisites**: save system, stable battle system, trainer system, and Pokemon catching must all work first
- **Networking**: Java has `ServerSocket` and `Socket` built in — no external libraries for basic TCP; start peer-to-peer (one player hosts), upgrade to client-server later
- **Trading**: both players select Pokemon → both confirm → swap + save update; rollback on connection drop
- **PvP Battles**: turn-based = one message exchange per turn; both games simulate same turn with same inputs → identical outcome
  - `Math.random()` must be replaced with a shared `Random` seeded identically at battle start — share seed in `BATTLE_START` handshake
  - `PvpBattleManager`: separate from `BattleManager` (no flee, both players human, turn resolution waits for both inputs)
