package battle.event;

import framework.Animation;
import framework.SpriteSheet;
import objects.PokemonBackSprite;
import objects.Sprite;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TrainerSummonPokemonEvent extends BattleEvent {

    private Sprite trainerSprite, pokemonSprite;

    private SpriteSheet pokeballSpriteSheet;

    private BufferedImage[] pokeballSprite = new BufferedImage[3];

    private Animation pokeballAnimation;

    private int count = 0;

    private boolean partOne = false, partTwo = false, partThree = false;

    private float x, y, centerX, centerY, radius, angle, speed;

    private enum SummonState {
        TrainerMoving, PokeballThrow, PokeballAnimation, PokemonSummon
    }

    private SummonState summonState = SummonState.TrainerMoving;

    public TrainerSummonPokemonEvent(Sprite trainerSprite, Sprite pokemonSprite) {
        this.trainerSprite = trainerSprite;
        this.pokemonSprite = pokemonSprite;

        loadAnimations();

        this.centerX = 0;
        this.centerY = 570 - (pokeballSprite[0].getWidth() * 4);
        this.radius = 300;
        this.angle = 0;
        this.speed = 0.1f;
    }

    @Override
    public void update() {
        switch(summonState) {
            case TrainerMoving -> {
                if(trainerSprite.getStartX() > -300) {
                    trainerSprite.setStartX(trainerSprite.getStartX() - 20);
                } else {
                    trainerSprite.setStartX(-300);
                    summonState = SummonState.PokeballThrow;
                }
            }

            case PokeballThrow -> {
                if(angle < Math.PI / 2) {
                    angle += speed;
                    x = centerX + (radius * (float) Math.sin(angle));
                    y = centerY - (radius * (float) Math.cos(angle));
                } else {
                    summonState = SummonState.PokeballAnimation;
                }
            }

            case PokeballAnimation -> {
                pokeballAnimation.update();

                if(++count == 22) {
                    summonState = SummonState.PokemonSummon;
                }
            }

            case PokemonSummon -> isFinished = true;
        }
    }

    @Override
    public void render(Graphics g, int x, int y) {
        g.setColor(new Color(201, 211, 211));
        g.drawString("Go! Charmander!", x, y);

        if(summonState == SummonState.PokeballThrow || summonState == SummonState.PokeballAnimation) {
            g.drawImage(
                    summonState == SummonState.PokeballThrow ? pokeballSprite[0] : pokeballAnimation.getCurrentFrame(),
                    (int) this.x, (int) this.y,
                    pokeballSprite[0].getWidth() * 4, pokeballSprite[0].getHeight() * 4,null
            );
        }

        if(summonState == SummonState.PokemonSummon) {
            PokemonBackSprite pokemonBackSprite = (PokemonBackSprite) pokemonSprite;
            g.drawImage(pokemonBackSprite.getSprite(), pokemonSprite.getEndX(), pokemonBackSprite.getEndY(), pokemonBackSprite.getSpriteWidth(), pokemonBackSprite.getSpriteHeight(), null);
        }
    }

    private void loadAnimations() {
        try {
            pokeballSpriteSheet = new SpriteSheet(ImageIO.read(getClass().getResource("/pokeballs.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < pokeballSprite.length; i++) {
            pokeballSprite[i] = pokeballSpriteSheet.grabImage(1, i + 1, 18, 18);
        }

        pokeballAnimation = new Animation(180, pokeballSprite);
    }
}
