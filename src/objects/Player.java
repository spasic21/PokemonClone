package objects;

import framework.Animation;
import framework.Handler;
import framework.SpriteSheet;
import framework.MapTransitionPoint;
import framework.enums.EntityDirection;
import framework.enums.EntityState;
import framework.enums.GameState;
import framework.enums.ObjectId;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Player extends Entity {

    private static final Random RANDOM = new Random();

    private final Rectangle bounds = new Rectangle();
    private final Rectangle collidingBounds = new Rectangle();

    private int speed;
    private float stepDistance = 0f;

    private Map<String, Animation> playerAnimations = new HashMap<>();
    private BufferedImage[][] playerActions = new BufferedImage[4][3];
    private SpriteSheet spriteSheet;

    public Player(Handler handler, float x, float y, int width, int height, EntityDirection entityDirection, ObjectId id) {
        super(handler, x, y, width, height, entityDirection, id);

        this.speed = 4;

        try {
            spriteSheet = new SpriteSheet("/player_sprite_sheet.png");
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
        checkBattleEncounter();
        move();
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
        if (!isCollidingEntity) {
            bounds.setBounds((int) x + 15, (int) y + (height / 2) + 5, (width / 2) + 5, (height / 2) - 5);
            return bounds;
        } else {
            collidingBounds.setBounds((int) x + 10, (int) y + (height / 2) - 15, (width / 2) + 15, (height / 2) + 15);
            return collidingBounds;
        }
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
        World world = handler.getWorld();
        int scaledTileWidth = world.getScaledTileWidth();
        int scaledTileHeight = world.getScaledTileHeight();

        // Accumulate distance only while moving; reset on stop so partial steps don't carry over
        if (entityState == EntityState.Walking) {
            stepDistance += Math.abs(velX) + Math.abs(velY);
        } else {
            stepDistance = 0f;
        }

        boolean stepCompleted = stepDistance >= scaledTileWidth;
        if (stepCompleted) {
            stepDistance -= scaledTileWidth;
        }

        Rectangle playerBounds = getBounds(false);
        int startTileX = Math.max(0, playerBounds.x / scaledTileWidth);
        int startTileY = Math.max(0, playerBounds.y / scaledTileHeight);
        int endTileX = Math.min(world.getMapCols() - 1, (playerBounds.x + playerBounds.width) / scaledTileWidth);
        int endTileY = Math.min(world.getMapRows() - 1, (playerBounds.y + playerBounds.height) / scaledTileHeight);

        // Check grass encounters
        for (int tileX = startTileX; tileX <= endTileX; tileX++) {
            for (int tileY = startTileY; tileY <= endTileY; tileY++) {
                Tile tile = world.getCollisionTile(tileX, tileY);
                if (tile == null || tile.getId() != ObjectId.GrassTile || !stepCompleted) continue;

                int randomNumber = RANDOM.nextInt(199) + 1;
                if (randomNumber == 5) {
                    entityState = EntityState.Standing;
                    stepDistance = 0f;
                    handler.getGameKeyInput().resetKeys();
                    handler.setNextTransition(RANDOM.nextInt(3) + 1, GameState.Battle);
                    return;
                }
            }
        }

        // Check map transitions
        for (MapTransitionPoint point : world.getTransitionPoints()) {
            if (point.triggerBounds().intersects(playerBounds)) {
                handler.getGameKeyInput().resetKeys();
                handler.setPendingWorld(new World(handler, point.targetLocation(), point.targetPoint(), this.entityDirection));
                handler.setNextTransition(1, GameState.Game);
                return;
            }
        }
    }
}
