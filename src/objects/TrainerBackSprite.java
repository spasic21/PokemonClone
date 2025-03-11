package objects;

import framework.SpriteSheet;

import java.awt.image.BufferedImage;

public class TrainerBackSprite extends Sprite {

    private final BufferedImage sprite;

    public TrainerBackSprite(int column, int row, int width, int height) {
        super(column, row, width, height);
        this.startX = 1200 + width;
        this.startY = 313;
        this.endX = 167;
        this.endY = 313;

        this.sprite = new SpriteSheet("/sprites/player_back_sprite_sheet.png").grabImage(column, row, width, height);
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
