package objects.pokemon;

import framework.SpriteSheet;
import objects.Sprite;

import java.awt.image.BufferedImage;

public class PokemonBackSprite extends Sprite {

    private BufferedImage sprite;

    public PokemonBackSprite(int id, int column, int row, int width, int height) {
        super(column, row, width, height);
        this.endX = 167;
        this.endY = 313;

        SpriteSheet spriteSheet;

        if(id < 151) {
            spriteSheet = new SpriteSheet("/sprites/kanto_pokemon_back_sprites.png");
        } else {
            spriteSheet = new SpriteSheet("/sprites/johto_pokemon_back_sprites.png");
        }

        sprite = spriteSheet.grabImage(column, row, width, height);
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
