package battle.event;

import objects.pokemon.Pokemon;

import java.awt.*;

public class ExpAnimationEvent extends BattleEvent {

    private final Pokemon pokemon;

    private int newExp;

    public ExpAnimationEvent(Pokemon pokemon, int exp) {
        this.pokemon = pokemon;
        this.newExp = this.pokemon.getCurrentExp() + exp;

        if(newExp > this.pokemon.getExpNextLevel()) {
            newExp = this.pokemon.getExpNextLevel();
        }
    }

    @Override
    public void update() {
        if(pokemon.getCurrentExp() >= pokemon.getExpNextLevel()) {
            pokemon.setCurrentExp(0);
            pokemon.setLevel(pokemon.getLevel() + 1);
            isFinished = true;
        }else if(pokemon.getCurrentExp() < newExp) {
            pokemon.setCurrentExp(pokemon.getCurrentExp() + 30);
        }else if(pokemon.getCurrentExp() >= newExp){
            pokemon.setCurrentExp(newExp);
            isFinished = true;
        }
    }

    @Override
    public void render(Graphics g, int x, int y) {

    }
}
