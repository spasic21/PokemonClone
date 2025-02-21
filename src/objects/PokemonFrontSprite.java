package objects;

import framework.SpriteSheet;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class PokemonFrontSprite extends PokemonSprite {

    private BufferedImage sprite;

    public PokemonFrontSprite(int column, int row, int width, int height) {
        super(column, row, width, height);
        this.x = 740;
        this.y = 100;

        try {
            SpriteSheet spriteSheet = new SpriteSheet(ImageIO.read(getClass().getResource("/sprites/kanto_pokemon_front_sprites.png")));
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
