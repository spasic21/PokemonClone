package objects;

import framework.ObjectId;
import framework.SpriteSheet;
import ui.Game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player extends GameObject {

    private Map<String, List<BufferedImage>> playerAnimations = new HashMap<>();
    private BufferedImage[][] playerActions = new BufferedImage[4][3];
    private SpriteSheet spriteSheet;

    private boolean collision = false;

    private int spriteCounter = 0;
    private int spriteIndex = 0;
    private int standCounter = 0;

    private static PlayerDirection playerDirection = PlayerDirection.DOWN;
    private static PlayerState playerState = PlayerState.Standing;

    public enum PlayerDirection {
        UP, DOWN, LEFT, RIGHT
    }

    public enum PlayerState {
        Standing, Walking, Running
    }

    public Player(float x, float y, int width, int height, ObjectId id) {
        super(x, y, width, height, id);

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
        playerAnimations.put("playerWalkDown", Arrays.asList(playerActions[0][0], playerActions[0][1], playerActions[0][2], playerActions[0][1]));
        playerAnimations.put("playerWalkUp", Arrays.asList(playerActions[1][0], playerActions[1][1], playerActions[1][2], playerActions[1][1]));
        playerAnimations.put("playerWalkLeft", Arrays.asList(playerActions[2][0], playerActions[2][1], playerActions[2][0], playerActions[2][1]));
        playerAnimations.put("playerWalkRight", Arrays.asList(playerActions[3][0], playerActions[3][1], playerActions[3][0], playerActions[3][1]));
    }

    @Override
    public void update() {
        if (playerState == PlayerState.Walking) {
            x += velX;
            y += velY;

            spriteCounter++;

            if (spriteCounter > 8) {
                spriteIndex++;

                if (spriteIndex > 3) {
                    spriteIndex = 0;
                }

                spriteCounter = 0;
            }
        } else {
            standCounter++;

            if (standCounter == 10) {
                spriteIndex = 1;
                standCounter = 0;
            }
        }
    }

    @Override
    public void render(Graphics g) {
        BufferedImage image = switch (playerDirection) {
            case UP -> playerAnimations.get("playerWalkUp").get(spriteIndex);
            case DOWN -> playerAnimations.get("playerWalkDown").get(spriteIndex);
            case LEFT -> playerAnimations.get("playerWalkLeft").get(spriteIndex);
            case RIGHT -> playerAnimations.get("playerWalkRight").get(spriteIndex);
        };

        g.drawImage(image, (int) x, (int) y, width, height, null);

        g.setColor(Color.RED);
        g.drawRect((int) x + 12, (int) y + (height/2), (width/2) + 11, height/2);

    }

    @Override
    public Rectangle getBounds() {
        int borderX = (int) x + 12;
        int borderY = (int) y + (height / 2);
        int borderWidth = (width / 2) + 11;
        int borderHeight = height / 2;

        return new Rectangle(borderX, borderY, borderWidth, borderHeight);
    }

    public PlayerDirection getPlayerDirection() {
        return playerDirection;
    }

    public void setPlayerDirection(PlayerDirection playerDirection) {
        Player.playerDirection = playerDirection;
    }

    public PlayerState getPlayerState() {
        return Player.playerState;
    }

    public void setPlayerState(PlayerState playerState) {
        Player.playerState = playerState;
    }


    public int getPlayerScreenPositionX() {
        return Game.WIDTH / 2 - 10;
    }

    public int getPlayerScreenPositionY() {
        return Game.HEIGHT / 2 - height;
    }

    public int getMovementSpeed() {
        return 4;
    }

    public boolean isCollision() {
        return collision;
    }

    public void setCollision(boolean collision) {
        this.collision = collision;
    }
}
