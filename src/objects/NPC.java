package objects;

import framework.Animation;
import framework.EventFlagManager;
import framework.Handler;
import framework.SpriteSheet;
import framework.enums.EntityDirection;
import framework.enums.EntityState;
import framework.enums.ObjectId;
import framework.npc.NpcData;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NPC extends Entity {

    private static SpriteSheet spriteSheet;

    private final Map<String, Animation> animations = new HashMap<>();
    private final BufferedImage[][] actions = new BufferedImage[4][3];

    private final NpcData npcData;

    // Constructor used by data-driven spawn system
    public NPC(Handler handler, float x, float y, int width, int height, NpcData npcData, ObjectId id) {
        super(handler, x, y, width, height, npcData.getDirection(), id);
        this.npcData = npcData;
        init();
    }

    // Legacy constructor — kept for compatibility, no data model
    public NPC(Handler handler, float x, float y, int width, int height, EntityDirection entityDirection, ObjectId id) {
        super(handler, x, y, width, height, entityDirection, id);
        this.npcData = null;
        init();
    }

    // Legacy constructor with explicit state
    public NPC(Handler handler, float x, float y, int width, int height, EntityDirection entityDirection, EntityState entityState, ObjectId id) {
        super(handler, x, y, width, height, entityDirection, entityState, id);
        this.npcData = null;
        init();
    }

    private void init() {
        if (spriteSheet == null) {
            spriteSheet = new SpriteSheet("/npc_sprite_sheet.png");
        }
        loadActions();
        loadAnimations();
    }

    private void loadActions() {
        int startCol = (npcData != null) ? npcData.getSpriteStartCol() : 1;
        int startRow = (npcData != null) ? npcData.getSpriteStartRow() : 1;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                actions[i][j] = spriteSheet.grabImage(startCol + j, startRow + i, 18, 18);
            }
        }
    }

    private void loadAnimations() {
        animations.put("walkDown",  new Animation(120, actions[0][0], actions[0][1], actions[0][2], actions[0][1]));
        animations.put("walkUp",    new Animation(120, actions[1][0], actions[1][1], actions[1][2], actions[1][1]));
        animations.put("walkLeft",  new Animation(120, actions[2][0], actions[2][1], actions[2][0], actions[2][1]));
        animations.put("walkRight", new Animation(120, actions[3][0], actions[3][1], actions[3][0], actions[3][1]));
    }

    /**
     * Returns the correct dialogue lines for this NPC based on current event flags.
     * Falls back to a placeholder if this NPC has no data model.
     */
    public List<String> resolveDialogue(EventFlagManager efm) {
        if (npcData == null) return List.of("...");
        return npcData.resolveDialogue(efm);
    }

    public String getName() {
        return npcData != null ? npcData.getName() : "NPC";
    }

    @Override
    public void update() {
        animations.get("walkUp").update();
        animations.get("walkDown").update();
        animations.get("walkLeft").update();
        animations.get("walkRight").update();
    }

    @Override
    public void render(Graphics g, int renderX, int renderY) {
        BufferedImage image;

        if (entityState == EntityState.Walking) {
            image = switch (entityDirection) {
                case UP    -> animations.get("walkUp").getCurrentFrame();
                case DOWN  -> animations.get("walkDown").getCurrentFrame();
                case LEFT  -> animations.get("walkLeft").getCurrentFrame();
                case RIGHT -> animations.get("walkRight").getCurrentFrame();
            };
        } else {
            image = switch (entityDirection) {
                case UP -> {
                    animations.get("walkUp").setIndex(1);
                    yield animations.get("walkUp").getCurrentFrame();
                }
                case DOWN -> {
                    animations.get("walkDown").setIndex(1);
                    yield animations.get("walkDown").getCurrentFrame();
                }
                case LEFT -> {
                    animations.get("walkLeft").setIndex(1);
                    yield animations.get("walkLeft").getCurrentFrame();
                }
                default -> {
                    animations.get("walkRight").setIndex(1);
                    yield animations.get("walkRight").getCurrentFrame();
                }
            };
        }

        g.drawImage(image, renderX, renderY, width, height, null);
    }

    public Rectangle getInteractionZone(int scaledTileWidth, int scaledTileHeight) {
        int range = (npcData != null) ? npcData.getInteractionRange() : 1;
        return switch (entityDirection) {
            case DOWN  -> new Rectangle((int) x, (int) y + scaledTileHeight, scaledTileWidth, scaledTileHeight * range);
            case UP    -> new Rectangle((int) x, (int) y - scaledTileHeight * range, scaledTileWidth, scaledTileHeight * range);
            case LEFT  -> new Rectangle((int) x - scaledTileWidth * range, (int) y, scaledTileWidth * range, scaledTileHeight);
            case RIGHT -> new Rectangle((int) x + scaledTileWidth, (int) y, scaledTileWidth * range, scaledTileHeight);
        };
    }

    @Override
    public Rectangle getBounds(boolean isCollidingEntity) {
        int borderX, borderY, borderWidth, borderHeight;

        if (!isCollidingEntity) {
            borderX     = (int) x + 15;
            borderY     = (int) y + (height / 2) + 5;
            borderWidth = (width / 2) + 5;
            borderHeight = (height / 2) - 5;
        } else {
            borderX     = (int) x + 10;
            borderY     = (int) y + (height / 2) - 15;
            borderWidth = (width / 2) + 15;
            borderHeight = (height / 2) + 15;
        }

        return new Rectangle(borderX, borderY, borderWidth, borderHeight);
    }
}
