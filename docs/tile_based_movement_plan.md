# Tile-Based Movement Plan

## Context

The current movement system is free pixel-based (velocity + per-pixel collision resolution). This fights against the game's tile-based nature — the `stepDistance` accumulator in `Player.checkBattleEncounter()` already simulates tile steps. Switching to true tile-based movement simplifies collision, encounters, transitions, NPC interaction, and future systems (save, pathfinding, puzzles).

**Movement grid**: 40x40 pixels (1 base tile scaled by 5). Player sprite (72x72) visually overlaps neighboring cells — normal for Pokemon games where sprites extend beyond their logical tile. Sprite renders with an offset so feet are centered in the cell.

---

## Phase 1: Entity.java — Grid Movement Foundation

**File**: `src/objects/Entity.java`

### Remove
- `velX`, `velY` fields and their getters/setters
- Entire `move()` method (line 47-50)
- Entire `moveAxis()` method (lines 52-120) — pixel collision loop goes away

### Add Fields
```java
protected int gridX, gridY;              // logical grid position (source of truth)
protected float moveProgress;            // 0.0 = at source tile, 1.0 = at target tile
protected float moveSpeed = 0.1f;        // progress per frame (0.1 = 10 frames/step)
protected int moveSourcePixelX, moveSourcePixelY;
protected int moveTargetPixelX, moveTargetPixelY;
```

### Add Methods

**Grid-pixel conversion** — sprite anchored bottom-center on the tile:
```java
protected int gridToPixelX(int gx) { return gx * TILE_SIZE + (TILE_SIZE - width) / 2; }
protected int gridToPixelY(int gy) { return (gy + 1) * TILE_SIZE - height; }  // bottom-aligned
```
Use the world's `scaledTileWidth` dynamically since it's derived from the map JSON. Store as a field or pass the world.

**`tryMove(EntityDirection dir, World world)`** — attempts one tile step:
1. If already walking, return false
2. Compute target grid cell from direction
3. Check bounds (target >= 0 and < mapCols/mapRows)
4. Check `world.getCollisionTile(targetGX, targetGY)` — if non-null and `RestrictionTile`, blocked
5. Check entity occupancy (loop entities, compare gridX/gridY)
6. If clear: set direction, record source/target pixel positions, set `gridX/gridY` to target, `moveProgress = 0`, `entityState = Walking`

**`updateMovement()`** — call from `update()`:
1. If not Walking, return false
2. Increment `moveProgress += moveSpeed`
3. If >= 1.0: snap `x,y` to target pixel pos, set Standing, return true (step complete)
4. Else: lerp `x,y` between source and target
5. Return false

**`snapToGrid()`** — sets pixel position from grid position, used at init and after transitions

**`setGridFromPixel(float px, float py)`** — derives gridX/gridY from pixel coords (for construction)

### Constructor Changes
After existing field assignments, init grid fields:
```java
this.moveProgress = 0f;
// gridX, gridY set by subclass via setGridFromPixel + snapToGrid
```

### Keep
- `getBounds()` — still needed for NPC interaction zones
- All getters/setters for x, y, width, height, direction, state

---

## Phase 2: Player.java — Tile-Step Input

**File**: `src/objects/Player.java`

### Remove
- `speed` field
- `stepDistance` field
- `getKeyInput()` method
- `checkBattleEncounter()` method
- `move()` call

### Constructor
After `super(...)`:
```java
this.moveSpeed = 0.1f;  // 10 frames per step at 60fps
setGridFromPixel(x, y);
snapToGrid();
```

### Rewrite `update()`
```
1. Update only the active direction's animation (not all 4)
2. If Standing:
   a. Read held key → getRequestedDirection()
   b. If direction found: set entityDirection, call tryMove()
3. Call updateMovement()
4. If step completed → onStepCompleted()
5. If step completed AND key still held → immediately tryMove() again (continuous walking)
```

### New `getRequestedDirection()`
Reads `GameKeyInput.isUp/Down/Left/Right()`, returns `EntityDirection` or null. Same priority as current: Up > Down > Left > Right.

### New `onStepCompleted()`
Replaces `checkBattleEncounter()`:
1. Check if current grid cell has a GrassTile → roll encounter (1/200 chance)
2. Check map transitions — test grid cell center point against `MapTransitionPoint.triggerBounds()`
3. If encounter/transition triggered: set Standing, resetKeys, initiate transition

### `getBounds()` Update
Return bounds based on grid position instead of raw pixel position:
```java
bounds.setBounds(gridX * tileSize, gridY * tileSize, tileSize, tileSize);
```

---

## Phase 3: NPC.java — Grid Alignment

**File**: `src/objects/NPC.java`

### Constructor
After `super(...)`:
```java
this.moveSpeed = 0.1f;
setGridFromPixel(x, y);
snapToGrid();
```

### `update()`
- Only update active animation if Walking
- Call `updateMovement()` (for future NPC walking patterns)
- Static NPCs just stay Standing with no changes needed

### `getInteractionZone()`
Update to use grid-based coordinates instead of raw pixel offsets.

### `getBounds()`
Simplify to grid cell bounds.

---

## Phase 4: World.java — Spawn Alignment

**File**: `src/objects/World.java`

### Spawn Coordinate Changes
NPC and player spawn positions already map to grid cells since `tileX` is integer:
```java
float spawnX = tileX * scaledTileWidth;
float spawnY = tileY * scaledTileHeight;
```
The `setGridFromPixel` + `snapToGrid` in Entity/Player/NPC constructors will properly derive the grid cell.

### Add Helper
```java
public boolean isMoveCellWalkable(int gridX, int gridY) {
    if (gridX < 0 || gridY < 0 || gridX >= mapCols || gridY >= mapRows) return false;
    Tile tile = collisionLayer[gridX][gridY];
    return tile == null || tile.getId() != ObjectId.RestrictionTile;
}
```

### Update `findFreeSpawnTile()`
Ensure it finds tiles that are walkable in the grid sense (should already work since it checks collision tiles).

---

## Phase 5: PlayerKeyInput.java — Grid-Based NPC Interaction

**File**: `src/keyInput/PlayerKeyInput.java`

### J-Key Interaction Rewrite
Replace rectangle-intersection collision check with grid adjacency:
1. Get player's facing grid cell (gridX + direction offset)
2. Loop entities, find NPC at that grid cell
3. Fallback: check NPC interaction zones for range > 1 (counter NPCs)
4. If NPC found: face NPC toward player, open dialogue

### Add `oppositeDirection()` Utility
```java
private EntityDirection oppositeDirection(EntityDirection dir) {
    return switch (dir) {
        case UP -> DOWN; case DOWN -> UP;
        case LEFT -> RIGHT; case RIGHT -> LEFT;
    };
}
```

---

## Phase 6: Handler.java — Cleanup

**File**: `src/framework/Handler.java`

### Remove (if no other consumers)
- `entityCollision` field
- `setEntityCollision()` / `isEntityCollision()`

### Keep
- `currentNpc` and its getter/setter (used in dialogue)

---

## Phase 7: Animation Tuning

**Files**: `src/objects/Player.java`, `src/objects/NPC.java`

Adjust animation speed to sync with tile movement:
- 10 frames per step, 4-frame animation cycle
- Animation speed: ~42ms per frame (10 frames / 4 animation frames * 16.67ms)
- Or keep at 120ms and accept slight async — tune by feel

---

## Files NOT Changed
- `Camera.java` — already reads interpolated `player.getX/Y()`, smooth scrolling works automatically
- `EntityManager.java` — rendering uses pixel positions, unaffected
- `TileMapLoader.java` — transition data stays in pixel space
- `Animation.java` — no structural changes needed
- `Game.java` — game loop unchanged
- `EntityState.java` / `EntityDirection.java` — existing values sufficient
- All Battle/Menu/Screen classes — unaffected

---

## Implementation Order
1. `Entity.java` — grid fields + movement state machine + collision helpers
2. `Player.java` — tile-step input + encounter/transition on step complete
3. `NPC.java` — grid alignment
4. `World.java` — `isMoveCellWalkable()` helper
5. `PlayerKeyInput.java` — grid-based J-key interaction
6. `Handler.java` — remove `entityCollision` if safe
7. Animation tuning — adjust speeds by feel
8. **Test**: Walk around, check collision, NPC interaction, grass encounters, map transitions

## Verification
1. Run game (`GameDriver.java`), walk in all 4 directions — player should snap between tiles with smooth interpolation
2. Walk into walls/NPCs — should be blocked before moving
3. Walk into tall grass — encounter should trigger on step completion
4. Walk into door/transition zones — map transition should fire
5. Press J facing NPC — dialogue should open
6. Hold direction key — should continuously walk tile by tile
7. Release key mid-step — should complete current step then stop
8. Check NPC positions — should be aligned to grid cells
