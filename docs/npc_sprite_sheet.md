# NPC Sprite Sheet

**File:** `resources/npc_sprite_sheet.png`
**Tile size:** 18×18 px
**Block size per NPC:** 3 columns × 4 rows

---

## Block Layout

Each NPC occupies a 3-column × 4-row block on the sheet.

| Row (within block) | Direction |
|--------------------|-----------|
| 1                  | Down      |
| 2                  | Up        |
| 3                  | Left      |
| 4                  | Right     |

| Column (within block) | Frame       |
|-----------------------|-------------|
| 1                     | Walk A      |
| 2                     | Idle/Stand  |
| 3                     | Walk B      |

---

## NPC Registry Example - Actual json has real data

| npcId               | spriteStartCol | spriteStartRow | interactionRange | Description           |
|---------------------|----------------|----------------|------------------|-----------------------|
| RIVAL_FIRST_TOWN    | 1              | 1              | 1                | Rival (first town)    |
| RIVAL_2_FIRST_TOWN  | 4              | 1              | 1                | 2nd rival variant     |
| NURSE_JOY           | 7              | 1              | 3                | Nurse Joy (PokeCenter)|

---

## Adding a New NPC Sprite

1. Add the 3×4 block to `npc_sprite_sheet.png` at the next available column.
2. Add an entry to `resources/npc_database.json` with the correct `spriteStartCol` and `spriteStartRow`.
3. Add a row to the registry table above.

## Notes

- `spriteStartRow` is always 1 for standard NPCs. It will increase only if an NPC gains extra action rows (e.g. sitting, sleeping animations) that push subsequent NPCs down.
- NPCs are referenced in Tiled map files via `npcId` on a Point object with `type=NPC`.
