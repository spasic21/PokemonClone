package objects;

import framework.SpriteSheet;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TrainerBackSprite extends Sprite {

    private BufferedImage sprite;

    public TrainerBackSprite(int column, int row, int width, int height) {
        super(column, row, width, height);
        this.startX = 1200 + width;
        this.startY = 313;
        this.endX = 167;
        this.endY = 313;

        try {
            SpriteSheet spriteSheet = new SpriteSheet(ImageIO.read(getClass().getResource("/sprites/player_back_sprite_sheet.png")));
            sprite = spriteSheet.grabImage(column, row, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage getSprite() {
        return sprite;
    }

    public int getSpriteWidth() {
        return sprite.getWidth() * 5;
    }
    public int getSpriteHeight() {
        return sprite.getHeight() * 5;
    }
}
