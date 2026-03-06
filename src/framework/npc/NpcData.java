package framework.npc;

import framework.EventFlagManager;
import framework.enums.EntityDirection;

import java.util.LinkedHashMap;
import java.util.List;

public class NpcData {

    private final String npcId;
    private final String name;
    private final EntityDirection direction;
    private final boolean isTrainer;
    private final int spriteStartCol;
    private final int spriteStartRow;
    private final int interactionRange;
    // LinkedHashMap preserves insertion order — put specific flags before "default"
    private final LinkedHashMap<String, List<String>> dialogue;

    public NpcData(String npcId, String name, EntityDirection direction, boolean isTrainer,
                   int spriteStartCol, int spriteStartRow, int interactionRange,
                   LinkedHashMap<String, List<String>> dialogue) {
        this.npcId = npcId;
        this.name = name;
        this.direction = direction;
        this.isTrainer = isTrainer;
        this.spriteStartCol = spriteStartCol;
        this.spriteStartRow = spriteStartRow;
        this.interactionRange = interactionRange;
        this.dialogue = dialogue;
    }

    /**
     * Returns the first dialogue list whose key matches a set flag,
     * falling back to "default" if no flags match.
     */
    public List<String> resolveDialogue(EventFlagManager efm) {
        for (var entry : dialogue.entrySet()) {
            if ("default".equals(entry.getKey())) {
                return entry.getValue();
            }
            if (efm != null && efm.hasFlag(entry.getKey())) {
                return entry.getValue();
            }
        }
        return List.of("...");
    }

    public String getNpcId()             { return npcId; }
    public String getName()              { return name; }
    public EntityDirection getDirection(){ return direction; }
    public boolean isTrainer()           { return isTrainer; }
    public int getSpriteStartCol()       { return spriteStartCol; }
    public int getSpriteStartRow()       { return spriteStartRow; }
    public int getInteractionRange()     { return interactionRange; }
}
