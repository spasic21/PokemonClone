package objects;

import framework.Animation;
import framework.Handler;
import framework.SpriteSheet;
import framework.enums.EntityDirection;
import framework.enums.EntityState;
import framework.enums.ObjectId;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NPC extends Entity {

    private int speed;

    private Map<String, Animation> animations = new HashMap<>();
    private BufferedImage[][] actions = new BufferedImage[4][3];
    private SpriteSheet spriteSheet;

    public NPC(Handler handler, float x, float y, int width, int height, EntityDirection entityDirection, ObjectId id) {
        super(handler, x, y, width, height, entityDirection, id);

        init();
    }

    public NPC(Handler handler, float x, float y, int width, int height, EntityDirection entityDirection, EntityState entityState, ObjectId id) {
        super(handler, x, y, width, height, entityDirection, entityState, id);

        init();
    }

    private void init() {
        try {
            spriteSheet = new SpriteSheet(ImageIO.read(getClass().getResource("/npc_sprite_sheet.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadActions();
        loadAnimations();
    }
    
    private void loadActions() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                actions[i][j] = spriteSheet.grabImage(j + 1, i + 1, 18, 18);
            }
        }
    }
    
    private void loadAnimations() {
        animations.put("walkDown", new Animation(120, actions[0][0], actions[0][1], actions[0][2], actions[0][1]));
        animations.put("walkUp", new Animation(120, actions[1][0], actions[1][1], actions[1][2], actions[1][1]));
        animations.put("walkLeft", new Animation(120, actions[2][0], actions[2][1], actions[2][0], actions[2][1]));
        animations.put("walkRight", new Animation(120, actions[3][0], actions[3][1], actions[3][0], actions[3][1]));
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
                case UP -> animations.get("walkUp").getCurrentFrame();
                case DOWN -> animations.get("walkDown").getCurrentFrame();
                case LEFT -> animations.get("walkLeft").getCurrentFrame();
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

//        g.setColor(Color.RED);
//        g.drawRect(renderX + 10,
//                renderY + (height / 2) - 15,
//                getBounds(true).width,
//                getBounds(true).height);
    }

    @Override
    public Rectangle getBounds(boolean isCollidingEntity) {
        int borderX, borderY, borderWidth, borderHeight;

        if(!isCollidingEntity) {
            borderX = (int) x + 15;
            borderY = (int) y + (height / 2) + 5;
            borderWidth = (width / 2) + 5;
            borderHeight = (height / 2) - 5;
        } else {
            borderX = (int) x + 10;
            borderY = (int) y + (height / 2) - 15;
            borderWidth = (width / 2) + 15;
            borderHeight = (height / 2) + 15;
        }

        return new Rectangle(borderX, borderY, borderWidth, borderHeight);
    }
}
