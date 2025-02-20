package objects;

import framework.Animation;
import framework.Handler;
import framework.SpriteSheet;
import framework.enums.EntityDirection;
import framework.enums.EntityState;
import framework.enums.GameState;
import framework.enums.ObjectId;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Player extends Entity {

    private int speed;

    private Map<String, Animation> playerAnimations = new HashMap<>();
    private BufferedImage[][] playerActions = new BufferedImage[4][3];
    private SpriteSheet spriteSheet;

    public Player(Handler handler, float x, float y, int width, int height, ObjectId id) {
        super(handler, x, y, width, height, id);

        this.speed = 4;

        try {
            spriteSheet = new SpriteSheet(ImageIO.read(getClass().getResource("/player_sprite_sheet.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadActions();
        loadAnimations();
    }

    private void loadActions() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                playerActions[i][j] = spriteSheet.grabImage(j + 1, i + 1, 18, 18);
            }
        }
    }

    private void loadAnimations() {
        playerAnimations.put("playerWalkDown", new Animation(120, playerActions[0][0], playerActions[0][1], playerActions[0][2], playerActions[0][1]));
        playerAnimations.put("playerWalkUp", new Animation(120, playerActions[1][0], playerActions[1][1], playerActions[1][2], playerActions[1][1]));
        playerAnimations.put("playerWalkLeft", new Animation(120, playerActions[2][0], playerActions[2][1], playerActions[2][0], playerActions[2][1]));
        playerAnimations.put("playerWalkRight", new Animation(120, playerActions[3][0], playerActions[3][1], playerActions[3][0], playerActions[3][1]));
    }

    @Override
    public void update() {
        playerAnimations.get("playerWalkUp").update();
        playerAnimations.get("playerWalkDown").update();
        playerAnimations.get("playerWalkLeft").update();
        playerAnimations.get("playerWalkRight").update();

        getKeyInput();
        move();
        checkBattleEncounter();
    }

    @Override
    public void render(Graphics g, int renderX, int renderY) {
        BufferedImage image;

        if (entityState == EntityState.Walking) {
            image = switch (entityDirection) {
                case UP -> playerAnimations.get("playerWalkUp").getCurrentFrame();
                case DOWN -> playerAnimations.get("playerWalkDown").getCurrentFrame();
                case LEFT -> playerAnimations.get("playerWalkLeft").getCurrentFrame();
                case RIGHT -> playerAnimations.get("playerWalkRight").getCurrentFrame();
            };
        } else {
            image = switch (entityDirection) {
                case UP -> {
                    playerAnimations.get("playerWalkUp").setIndex(1);
                    yield playerAnimations.get("playerWalkUp").getCurrentFrame();
                }
                case DOWN -> {
                    playerAnimations.get("playerWalkDown").setIndex(1);
                    yield playerAnimations.get("playerWalkDown").getCurrentFrame();
                }
                case LEFT -> {
                    playerAnimations.get("playerWalkLeft").setIndex(1);
                    yield playerAnimations.get("playerWalkLeft").getCurrentFrame();
                }
                default -> {
                    playerAnimations.get("playerWalkRight").setIndex(1);
                    yield playerAnimations.get("playerWalkRight").getCurrentFrame();
                }
            };
        }

        g.drawImage(image, renderX, renderY, width, height, null);

//        g.setColor(Color.RED);
//        g.drawRect(renderX + 15,
//                renderY + (height / 2) + 5,
//                getBounds(false).width,
//                getBounds(false).height);
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

    private void getKeyInput() {
        velX = 0;
        velY = 0;

        entityState = EntityState.Walking;

        if (handler.getGameKeyInput().isUp()) {
            entityDirection = EntityDirection.UP;
            velY = -speed;
            return;
        }

        if (handler.getGameKeyInput().isDown()) {
            entityDirection = EntityDirection.DOWN;
            velY = speed;
            return;
        }

        if (handler.getGameKeyInput().isLeft()) {
            entityDirection = EntityDirection.LEFT;
            velX = -speed;
            return;
        }

        if (handler.getGameKeyInput().isRight()) {
            entityDirection = EntityDirection.RIGHT;
            velX = speed;
            return;
        }

        entityState = EntityState.Standing;
    }

    private void checkBattleEncounter() {
        for (Tile[] collisionTiles : handler.getWorld().getCollisionLayer()) {
            for (Tile tile : collisionTiles) {
                if (tile != null) {
                    if (tile.getId() == ObjectId.GrassTile && getBounds(false).intersects(tile.getBounds()) && entityState == EntityState.Walking) {
                        int randomNumber = (int) (Math.random() * 99) + 1;

                        if (randomNumber == 5) {
                            entityState = EntityState.Standing;
                            handler.getGame().setGameState(GameState.Battle);
                        }
                    } else if (tile.getId() == ObjectId.DoorTile && getBounds(false).intersects(tile.getBounds()) && velY < 0) {
                        System.out.println("You walked into a door!");
                    }
                }
            }
        }
    }
}
