package battle.event;

import framework.Animation;
import framework.SpriteSheet;
import objects.Sprite;
import objects.pokemon.Pokemon;
import objects.pokemon.PokemonBackSprite;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TrainerSummonPokemonEvent extends BattleEvent {

    private final Pokemon pokemon;

    private final Sprite trainerSprite, pokemonSprite;

    private SpriteSheet pokeballSpriteSheet;

    private final BufferedImage[] pokeballSprite = new BufferedImage[3];

    private Animation pokeballAnimation;

    private int count = 0;

    private float x, y, angle;

    private final float centerX, centerY, radius, speed;

    private enum SummonState {
        TrainerMoving, PokeballThrow, PokeballAnimation, PokemonSummon
    }

    private SummonState summonState = SummonState.TrainerMoving;

    public TrainerSummonPokemonEvent(Sprite trainerSprite, Pokemon pokemon) {
        this.trainerSprite = trainerSprite;
        this.pokemon = pokemon;
        this.pokemonSprite = pokemon.getBackSprite();

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
        g.drawString("Go! " + pokemon.getName() + "!", x, y);

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
        pokeballSpriteSheet = new SpriteSheet("/pokeballs.png");

        for(int i = 0; i < pokeballSprite.length; i++) {
            pokeballSprite[i] = pokeballSpriteSheet.grabImage(1, i + 1, 18, 18);
        }

        pokeballAnimation = new Animation(180, pokeballSprite);
    }
}
