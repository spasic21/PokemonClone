package objects.pokemon;

import framework.SpriteSheet;
import objects.Sprite;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class PokemonBackSprite extends Sprite {

    private BufferedImage sprite;

    public PokemonBackSprite(int id, int column, int row, int width, int height) {
        super(column, row, width, height);
        this.endX = 167;
        this.endY = 313;

        try {
            SpriteSheet spriteSheet;

            if(id < 151) {
                spriteSheet = new SpriteSheet(ImageIO.read(getClass().getResource("/sprites/kanto_pokemon_back_sprites.png")));
            } else {
                spriteSheet = new SpriteSheet(ImageIO.read(getClass().getResource("/sprites/johto_pokemon_back_sprites.png")));
            }

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
