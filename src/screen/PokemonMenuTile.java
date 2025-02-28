package screen;

import framework.Animation;
import framework.Handler;
import framework.SpriteSheet;
import objects.pokemon.Pokemon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class PokemonMenuTile {

    private Handler handler;

    private int x, y;

    private int pokeballSpriteIndex = 0;

    private Pokemon pokemon;

    private SpriteSheet pokemonSpriteSheet, pokeballSpriteSheet;
    private BufferedImage[] pokeballSprite = new BufferedImage[2];
    private BufferedImage[] pokemonSprite = new BufferedImage[2];
    private BufferedImage hpSymbol;
    private Animation pokemonAnimation;
    private Font font;

    public PokemonMenuTile(int x, int y, int spriteX, int spriteY, Pokemon pokemon) {
        this.x = x;
        this.y = y;
        this.pokemon = pokemon;

        try {
            pokemonSpriteSheet = new SpriteSheet(ImageIO.read(getClass().getResource("/sprites/menu_sprite_sheet.png")));
            pokeballSpriteSheet = new SpriteSheet(ImageIO.read(getClass().getResource("/pokemon_menu_pokeball_sprite_sheet.png")));
            hpSymbol = ImageIO.read(getClass().getResource("/hud/hp_symbol.png"));

            InputStream inputStream = getClass().getResourceAsStream("/font/PokemonFont.ttf");
            font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }

        pokemonSprite[0] = pokemonSpriteSheet.grabImage(spriteX, spriteY, 18, 18);
        pokemonSprite[1] = pokemonSpriteSheet.grabImage(spriteX + 1, spriteY, 18, 18);

        pokemonAnimation = new Animation(180, pokemonSprite[0], pokemonSprite[1]);

        pokeballSprite[0] = pokeballSpriteSheet.grabImage(1, 1, 20, 20);
        pokeballSprite[1] = pokeballSpriteSheet.grabImage(1, 2, 20, 20);
    }

    public void update(boolean selected) {
        if(selected) {
            pokeballSpriteIndex = 1;
            pokemonAnimation.update();
        } else {
            pokeballSpriteIndex = 0;
        }

    }

    public void render(Graphics g) {
        // Start point: x = 150, y = 75
        g.setColor(new Color(173, 216, 230));
        g.fillRoundRect(x, y, 400, 175, 20, 20);

        g.drawImage(pokeballSprite[pokeballSpriteIndex], x + 10, y + 5, 75, 75, null);

        g.setColor(Color.WHITE);
        Font tileFont = font.deriveFont(48f);
        g.setFont(tileFont);

        g.drawString(pokemon.getName(), x + 190, y + 45);
        g.drawString("Lv " + pokemon.getLevel(), x + 280, y + 85);
        g.drawString(pokemon.getCurrentHealth() + "/" + pokemon.getMaxHealth(), x + 235, y + 160);

        g.drawImage(hpSymbol, x + 120, y + 105, 43, 18, null);
        g.setColor(new Color(85, 106, 89));
        g.fillRect(x + 170, y + 105, 200, 18);
        renderNewHealth(g, pokemon, x + 170, y + 105);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(5));

        if(pokeballSpriteIndex == 1) {
            g.setColor(new Color(216, 82, 75));
            g.drawRoundRect(x, y, 400, 175, 20, 20);
        } else {
            g.setColor(Color.LIGHT_GRAY);
            g.drawRoundRect(x, y, 400, 175, 20, 20);

            pokemonAnimation.setIndex(0);
        }

        g.drawImage(pokemonAnimation.getCurrentFrame(), x + 20, y + 85, 75, 75, null);
    }

    private void renderNewHealth(Graphics g, Pokemon pokemon, int x, int y) {
        double healthRatio = (double) pokemon.getCurrentHealth() / pokemon.getMaxHealth();

        if (healthRatio <= 0.25) {
            g.setColor(new Color(243, 90, 64));
        } else if (healthRatio <= 0.5) {
            g.setColor(new Color(252, 232, 57));
        } else {
            g.setColor(new Color(117, 254, 172));
        }

        int newHealthBarWidth = (int) Math.round(healthRatio * 200);

        g.fillRect(x, y, newHealthBarWidth, 18);
    }
}
