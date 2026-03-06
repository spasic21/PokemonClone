 NPC Spawn System Plan                                                                                                                                            

 Context

 NPCs currently have no data model, no spawning system, and hardcoded dialogue. The SpawnManager (hardcoded pixel coords) has already been replaced by the Tiled  
 object layer system for map transitions. This plan extends that same pattern to spawn NPCs from Tiled maps, with identity data in a JSON database and flag-aware 
  dialogue.

 Build Order

 Step 1: EventFlagManager + EventFlags

 New file: src/framework/EventFlagManager.java
 - Set<String> flags — boolean flags (trainer defeated, legendary caught, etc.)
 - Map<String, Integer> variables — integer state (story progression stages)
 - Methods: setFlag(String), hasFlag(String), setVariable(String, int), getVariable(String)

 New file: src/framework/EventFlags.java
 - Static final String constants for all flag IDs (e.g., RIVAL_DEFEATED, TEAM_ROCKET_GOLDENROD)
 - Start with a handful of placeholder flags; expand as needed

 Modify: src/framework/Handler.java
 - Add EventFlagManager eventFlagManager field + getter/setter

 Modify: src/ui/Game.java
 - Initialize EventFlagManager in onDatabaseLoaded(), set on Handler

 Step 2: NPC Database

 New file: resources/npc_database.json
 [
   {
     "npcId": "RIVAL_FIRST_TOWN",
     "name": "Rival",
     "direction": "DOWN",
     "isTrainer": false,
     "dialogue": {
       "default": ["Don't mind me standing here.", "What? You wanna fight?"],
       "RIVAL_DEFEATED": ["You beat me...", "I'll train harder!"]
     }
   }
 ]
 - dialogue keys: "default" is the fallback. Any other key is checked against EventFlagManager.hasFlag(). First matching flag wins, so order matters — put        
 specific flags before "default".

 New file: src/framework/npc/NpcData.java
 - Immutable class/record holding: npcId, name, EntityDirection direction, boolean isTrainer, LinkedHashMap<String, List<String>> dialogue
 - Method: resolveDialogue(EventFlagManager) — iterates dialogue map, returns first entry whose key matches a set flag (or "default")

 New file: src/framework/npc/NpcDatabase.java
 - Loads npc_database.json, parses into Map<String, NpcData> keyed by npcId
 - Method: getNpcData(String npcId) — returns immutable template
 - Follows same pattern as PokemonDatabase/ItemDatabase

 Modify: src/framework/Handler.java
 - Add NpcDatabase npcDatabase field + getter/setter

 Modify: src/ui/Game.java
 - Initialize NpcDatabase in onDatabaseLoaded(), set on Handler

 Step 3: Tiled NPC Parsing

 New file: src/framework/MapNpcSpawn.java
 public record MapNpcSpawn(String npcId, int tileX, int tileY) {}

 Modify: src/framework/TileMapLoader.java
 - Add List<MapNpcSpawn> npcSpawns field + getNpcSpawns() getter
 - In loadTransitionPoints(), add a third case alongside Transition and SpawnPoint:
 else if ("NPC".equals(type) && isPoint) {
     String npcId = null;
     for (Object propItem : props) {
         // extract npcId from properties
     }
     if (npcId != null) {
         int tileX = (int) Math.round(objX / tileWidth);
         int tileY = (int) Math.round(objY / tileHeight);
         npcSpawns.add(new MapNpcSpawn(npcId, tileX, tileY));
     }
 }

 Tiled object layer format (what user places in the map editor):
 - Type: Point object
 - Properties: type=NPC, npcId=RIVAL_FIRST_TOWN
 - Position is read from the point's x/y

 Step 4: NPC.java Data Model

 Modify: src/objects/NPC.java
 - Make sprite sheet private static SpriteSheet spriteSheet (shared, loaded once)
 - Add fields: String name, NpcData npcData
 - New constructor: NPC(Handler, float x, float y, int width, int height, NpcData npcData, ObjectId)
   - Sets direction from npcData.direction()
   - Stores npcData reference for dialogue resolution
 - Add method: resolveDialogue(EventFlagManager) — delegates to npcData.resolveDialogue(efm)
 - Add getName() getter

 Step 5: World NPC Spawning

 Modify: src/objects/World.java
 - In loadTileMap(): also read tileMapLoader.getNpcSpawns() into a local list
 - New private method spawnNpcs(List<MapNpcSpawn> npcSpawns, NpcDatabase npcDatabase):
   - For each spawn entry: look up npcDatabase.getNpcData(npcId)
   - If found: create new NPC(handler, tileX * scaledTileWidth, tileY * scaledTileHeight, 72, 72, npcData, ObjectId.NPC)
   - Add to entityManager.addEntity(npc)
   - If not found: print warning
 - Call spawnNpcs() in both constructors after EntityManager is created
 - NpcDatabase accessed via handler.getNpcDatabase() (already on Handler from step 2)

 Step 6: Dialogue System

 Modify: src/framework/DialogueScreen.java
 - Cache font.deriveFont(48f) and new BasicStroke(5) in constructor (not per-frame)
 - Add fields: List<String> currentLines, int currentPage
 - New method startDialogue(NPC npc):
   - Calls npc.resolveDialogue(handler.getEventFlagManager()) to get the right dialogue lines
   - Resets currentPage = 0
 - New method advancePage() → increments page, returns true if more pages remain
 - New method isLastPage() → checks if on final page
 - render() draws currentLines.get(currentPage) using existing word-wrap logic

 Move DialogueScreen ownership to Handler (so DialogueKeyInput can access it):

 Modify: src/framework/Handler.java
 - Add DialogueScreen dialogueScreen field + getter/setter

 Modify: src/screen/GameScreen.java (line 21, 37-38)
 - Remove local dialogueScreen field
 - Use handler.getDialogueScreen() instead
 - Initial World creation stays here (line 23)

 Modify: src/ui/Game.java
 - Create DialogueScreen in onDatabaseLoaded(), set on Handler (before GameScreen is constructed, since GameScreen will now use Handler's reference)

 Modify: src/keyInput/PlayerKeyInput.java (line 69)
 - Before setting GameState.Dialogue: call handler.getDialogueScreen().startDialogue(handler.getCurrentNpc())

 Modify: src/keyInput/DialogueKeyInput.java
 - On J press: check handler.getDialogueScreen().isLastPage()
   - If last page → exit to GameState.Game
   - Otherwise → handler.getDialogueScreen().advancePage()

 Step 7: Remove SpawnManager

 Delete: src/framework/spawn/SpawnManager.java
 Delete: src/framework/spawn/SpawnPoint.java

 Modify: src/framework/Handler.java
 - Remove spawnManager field, getter/setter, import

 Modify: src/ui/Game.java
 - Remove SpawnManager.getInstance() block from onDatabaseLoaded()

 File Summary

 ┌───────────────────────────────────────┬──────────────────────────────────────────────────────────────────────────────────┐
 │                 File                  │                                      Action                                      │
 ├───────────────────────────────────────┼──────────────────────────────────────────────────────────────────────────────────┤
 │ src/framework/EventFlagManager.java   │ NEW                                                                              │
 ├───────────────────────────────────────┼──────────────────────────────────────────────────────────────────────────────────┤
 │ src/framework/EventFlags.java         │ NEW                                                                              │
 ├───────────────────────────────────────┼──────────────────────────────────────────────────────────────────────────────────┤
 │ src/framework/npc/NpcData.java        │ NEW                                                                              │
 ├───────────────────────────────────────┼──────────────────────────────────────────────────────────────────────────────────┤
 │ src/framework/npc/NpcDatabase.java    │ NEW                                                                              │
 ├───────────────────────────────────────┼──────────────────────────────────────────────────────────────────────────────────┤
 │ src/framework/MapNpcSpawn.java        │ NEW                                                                              │
 ├───────────────────────────────────────┼──────────────────────────────────────────────────────────────────────────────────┤
 │ resources/npc_database.json           │ NEW                                                                              │
 ├───────────────────────────────────────┼──────────────────────────────────────────────────────────────────────────────────┤
 │ src/framework/TileMapLoader.java      │ MODIFY — parse NPC point objects                                                 │
 ├───────────────────────────────────────┼──────────────────────────────────────────────────────────────────────────────────┤
 │ src/objects/NPC.java                  │ MODIFY — add data model, static sprite sheet                                     │
 ├───────────────────────────────────────┼──────────────────────────────────────────────────────────────────────────────────┤
 │ src/objects/World.java                │ MODIFY — spawn NPCs from map data                                                │
 ├───────────────────────────────────────┼──────────────────────────────────────────────────────────────────────────────────┤
 │ src/framework/Handler.java            │ MODIFY — add eventFlagManager, npcDatabase, dialogueScreen; remove spawnManager  │
 ├───────────────────────────────────────┼──────────────────────────────────────────────────────────────────────────────────┤
 │ src/framework/DialogueScreen.java     │ MODIFY — multi-page flag-aware dialogue, cache per-frame allocs                  │
 ├───────────────────────────────────────┼──────────────────────────────────────────────────────────────────────────────────┤
 │ src/keyInput/DialogueKeyInput.java    │ MODIFY — multi-page advancement                                                  │
 ├───────────────────────────────────────┼──────────────────────────────────────────────────────────────────────────────────┤
 │ src/keyInput/PlayerKeyInput.java      │ MODIFY — call startDialogue()                                                    │
 ├───────────────────────────────────────┼──────────────────────────────────────────────────────────────────────────────────┤
 │ src/screen/GameScreen.java            │ MODIFY — use Handler's DialogueScreen                                            │
 ├───────────────────────────────────────┼──────────────────────────────────────────────────────────────────────────────────┤
 │ src/ui/Game.java                      │ MODIFY — init NpcDatabase, EventFlagManager, DialogueScreen; remove SpawnManager │
 ├───────────────────────────────────────┼──────────────────────────────────────────────────────────────────────────────────┤
 │ src/framework/spawn/SpawnManager.java │ DELETE                                                                           │
 ├───────────────────────────────────────┼──────────────────────────────────────────────────────────────────────────────────┤
 │ src/framework/spawn/SpawnPoint.java   │ DELETE                                                                           │
 └───────────────────────────────────────┴──────────────────────────────────────────────────────────────────────────────────┘

 Verification

 1. Add an NPC point object in first_town.json via Tiled: type=NPC, npcId=RIVAL_FIRST_TOWN
 2. Add the matching entry in npc_database.json
 3. Run the game — NPC should appear at the placed tile position
 4. Walk into NPC — collision should trigger, J key opens dialogue
 5. Dialogue shows correct text from database (multi-page, advance with J)
 6. Set a flag via code (e.g., in a debug block) and verify dialogue changes
 7. Transition to player house and back — NPC should persist (respawns from map data)

 Caution
 1. I still need to add spawn points in Tiled for npcs