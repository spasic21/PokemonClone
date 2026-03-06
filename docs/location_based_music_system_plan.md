# Location-Based Music System Plan

## Context
The game currently has a `SoundManager` with working sound effects but music playback is entirely commented out. Music files exist in `resources/sounds/` but aren't wired up. This plan adds a complete music system where each location plays its own background music with smooth crossfade transitions, and battles trigger immediate music changes.

## Files to Modify/Create

| File | Action |
|------|--------|
| `src/framework/enums/Location.java` | Modify — add `musicPath` field |
| `src/framework/SoundManager.java` | Modify — add clip caching & volume control, remove broken play/stop methods, fix issues #69/#70/#71 |
| `src/framework/MusicManager.java` | **Create** — music state machine with crossfade, battle, fanfare orchestration |
| `src/ui/Game.java` | Modify — wire MusicManager into game loop |
| `src/keyInput/BattleKeyInput.java` | Modify — trigger fanfare on win, resume location music on battle exit |

---

## Step 1: Location Enum — Add `musicPath` Field

Add a `musicPath` string field + constructor + getter to `Location`. Each enum constant maps to its WAV file:
- `World` → `/sounds/azalea_city.wav`
- `PlayerHouse` → `/sounds/azalea_city.wav` (shares town music)
- `PokeCenter` → `/sounds/azalea_town_soulsilver.wav`

If two locations share the same path, music continues seamlessly (no crossfade triggered).

---

## Step 2: Refactor SoundManager

**Remove:** `playMusic()`, `stopMusic()`, `isPlaying()` methods and dead fields (`sourceDataLine`, `isPlaying` boolean, `backgroundMusic`, `currentTrack`). These are replaced by MusicManager.

**Add new low-level clip API:**
- `getMusicClip(path)` — returns a cached `Clip`, loading from disk on first access only (fixes #69)
- `setClipVolume(clip, volume)` — sets volume via `FloatControl.MASTER_GAIN` with linear-to-dB conversion (`20 * log10(volume)`)
- `startClipLooping(clip)` — `setFramePosition(0)` + `loop(LOOP_CONTINUOUSLY)` + `start()`
- `startClipOnce(clip)` — `setFramePosition(0)` + `loop(0)` + `start()` (for victory fanfare)
- `stopClip(clip)` — calls `stop()` only, never `close()` (fixes #70)

Sound effects (`loadSound`, `playSound`) stay unchanged.

---

## Step 3: Create MusicManager

New class at `src/framework/MusicManager.java`. Not a singleton — instantiated once in `Game.java`.

### State Machine
```
IDLE → PLAYING_LOCATION (on entering a location)
PLAYING_LOCATION → CROSSFADING (on entering a different location)
CROSSFADING → PLAYING_LOCATION (when fade completes)
PLAYING_LOCATION → PLAYING_BATTLE (hard cut on wild/trainer encounter)
PLAYING_BATTLE → PLAYING_FANFARE (on battle win)
PLAYING_BATTLE → PLAYING_LOCATION (on run from battle)
PLAYING_FANFARE → IDLE (when fanfare clip finishes)
IDLE → PLAYING_LOCATION (on player dismissing win screen)
```

### Public API
- `update()` — called every frame from `Game.update()`, ticks crossfade progress and checks fanfare completion
- `playLocationMusic(Location)` — starts location music or crossfades from current track. No-op if same track already playing
- `playBattleMusic(boolean isWild)` — hard-cuts to battle music (wild or trainer), immediately stops all other music
- `playVictoryFanfare()` — stops battle music, plays victory fanfare once (no loop)
- `resumeLocationMusic()` — crossfades from whatever's playing back to current location's music (restarts from beginning)

### Crossfade
Runs over 90 frames (1.5s at 60fps). Two clips play simultaneously — old fades out linearly from 1.0→0.0, new fades in from 0.0→1.0. At completion, old clip is stopped, new clip becomes the active clip.

### Battle Music Constants
- Wild: `/sounds/johto_wild_pokemon_battle.wav`
- Trainer: `/sounds/rival_battle.wav`
- Victory: `/sounds/victory_wild_pokemon.wav`

---

## Step 4: Wire MusicManager into Game.java

1. **Field + getter:** `private MusicManager musicManager;` with `getMusicManager()`
2. **Instantiate** in `onDatabaseLoaded()` after `loadSounds()`
3. **Preload all music clips** during loading (iterate `Location.values()` + battle/victory paths → call `SoundManager.getMusicClip()` for each)
4. **Call `musicManager.update()`** at the top of `Game.update()` (every frame, all states)
5. **Game state — first entry:** In the `Game` case, detect `MusicState.IDLE` + world exists → call `playLocationMusic(handler.getWorld().getLocation())`
6. **Transition midpoint:** When `hasPendingWorld()` is applied and next state is `Game`, call `playLocationMusic()` with the new world's location
7. **Battle transition start:** When transition state entered with `nextGameState == Battle`, call `playBattleMusic(true)` (hard cut, immediate)
8. **Remove** the old `playMusicIfNeeded()` method entirely

---

## Step 5: Wire Battle Exit Points in BattleKeyInput.java

1. **Battle win** (progressControls, when `isBattleOver()` is true): call `getMusicManager().playVictoryFanfare()`
2. **Dismiss win screen** (BattleWin state key handler): call `getMusicManager().resumeLocationMusic()` before setting `GameState.Game`
3. **Run from battle** (battleOptionId == 4): call `getMusicManager().resumeLocationMusic()` before setting `GameState.Game`

---

## Edge Cases Handled

- **Same music across locations:** `playLocationMusic()` compares paths — if identical, no crossfade (music continues)
- **Rapid transitions:** `startCrossfade()` stops any previous fading-out clip before starting a new one
- **Fanfare finishes before player input:** State moves to `IDLE`; `resumeLocationMusic()` starts fresh
- **No music for a location:** If `getMusicPath()` returns null, all music stops gracefully
- **MP3 file:** `poke_center_soulsilver.mp3` won't work with `javax.sound.sampled` — needs conversion to WAV (noted, not part of this task)

---

## Verification

1. **Run the game** (`GameDriver.java`) — location music should start after loading screen transition
2. **Walk between World and PokeCenter** — crossfade should occur during the visual transition
3. **Trigger a wild encounter** (walk in grass) — battle music should cut in immediately with no fade
4. **Win the battle** — victory fanfare plays once, then pressing a key restarts location music
5. **Run from battle** — location music resumes immediately with crossfade from battle music
6. **Walk between World and PlayerHouse** — same music path, so no crossfade (seamless)
