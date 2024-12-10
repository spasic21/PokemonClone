package objects;

import framework.SpriteSheet;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class PokemonBackSprite extends PokemonSprite {

    private BufferedImage sprite;
    private int x;
    private int y;
    private float alpha = 1.0f;

    public PokemonBackSprite(int column, int row, int width, int height) {
        super(column, row, width, height);
        this.x = 167;
        this.y = 313;

        try {
            SpriteSheet spriteSheet = new SpriteSheet(ImageIO.read(getClass().getResource("/sprites/kanto_pokemon_back_sprites.png")));
            sprite = spriteSheet.grabImage(column, row, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage getSprite() {
        return sprite;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public int getSpriteWidth() {
        return sprite.getWidth() * 5;
    }

    public int getSpriteHeight() {
        return sprite.getHeight() * 5;
    }
}
