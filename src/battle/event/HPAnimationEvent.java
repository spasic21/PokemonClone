package battle.event;

import objects.pokemon.Pokemon;

import java.awt.*;

public class HPAnimationEvent extends BattleEvent {

    private final Pokemon pokemon;

    private int newHealth;

    public HPAnimationEvent(Pokemon pokemon , int damage) {
        this.pokemon = pokemon;
        this.newHealth = this.pokemon.getCurrentHealth() - damage;

        if(this.newHealth < 0) {
            this.newHealth = 0;
            this.pokemon.setFainted(true);
        }
    }

    @Override
    public void update() {
        if (pokemon.getCurrentHealth() <= 0) {
            pokemon.setCurrentHealth(0);
            isFinished = true;
        } else if (pokemon.getCurrentHealth() > newHealth) {
            pokemon.setCurrentHealth(pokemon.getCurrentHealth() - 3);
        } else if (pokemon.getCurrentHealth() <= newHealth) {
            pokemon.setCurrentHealth(newHealth);
            isFinished = true;
        }
    }

    @Override
    public void render(Graphics g, int x, int y) {

    }
}
