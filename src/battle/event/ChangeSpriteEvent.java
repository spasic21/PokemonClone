package battle.event;

import battle.BattleManager;
import objects.pokemon.Pokemon;

import java.awt.*;

public class ChangeSpriteEvent extends BattleEvent {

    private BattleManager battleManager;

    private Pokemon pokemon;

    public ChangeSpriteEvent(BattleManager battleManager, Pokemon pokemon) {
        this.battleManager = battleManager;
        this.pokemon = pokemon;
    }

    @Override
    public void update() {
        battleManager.setTrainerPokemon(pokemon);
        isFinished = true;
    }

    @Override
    public void render(Graphics g, int x, int y) {

    }
}
