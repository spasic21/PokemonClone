package framework.npc;

import framework.enums.EntityDirection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NpcDatabase {

    private final Map<String, NpcData> database = new HashMap<>();

    public void initDatabase() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("npc_database.json");
            if (is == null) {
                System.err.println("NpcDatabase: npc_database.json not found");
                return;
            }

            JSONArray root = (JSONArray) new JSONParser().parse(new InputStreamReader(is));

            for (Object item : root) {
                JSONObject obj = (JSONObject) item;

                String npcId    = (String) obj.get("npcId");
                String name     = (String) obj.get("name");
                boolean trainer = Boolean.TRUE.equals(obj.get("isTrainer"));

                String dirStr = (String) obj.get("direction");
                EntityDirection direction = EntityDirection.DOWN;
                if (dirStr != null) {
                    direction = EntityDirection.valueOf(dirStr.toUpperCase());
                }

                LinkedHashMap<String, List<String>> dialogue = new LinkedHashMap<>();
                JSONObject dialogueObj = (JSONObject) obj.get("dialogue");
                if (dialogueObj != null) {
                    for (Object key : dialogueObj.keySet()) {
                        String flagKey = (String) key;
                        JSONArray lines = (JSONArray) dialogueObj.get(flagKey);
                        List<String> lineList = new ArrayList<>();
                        for (Object line : lines) {
                            lineList.add((String) line);
                        }
                        dialogue.put(flagKey, lineList);
                    }
                }

                int spriteStartCol    = obj.get("spriteStartCol")    != null ? ((Long) obj.get("spriteStartCol")).intValue()    : 1;
                int spriteStartRow    = obj.get("spriteStartRow")    != null ? ((Long) obj.get("spriteStartRow")).intValue()    : 1;
                int interactionRange  = obj.get("interactionRange")  != null ? ((Long) obj.get("interactionRange")).intValue()  : 1;

                database.put(npcId, new NpcData(npcId, name, direction, trainer, spriteStartCol, spriteStartRow, interactionRange, dialogue));
            }

            System.out.println("NpcDatabase loaded " + database.size() + " NPC(s)");

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public NpcData getNpcData(String npcId) {
        return database.get(npcId);
    }
}
