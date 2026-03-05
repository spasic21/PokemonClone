package battle.event;

import framework.pokemon.ExperienceCalculator;
import objects.pokemon.Pokemon;

import java.awt.*;

public class ExpAnimationEvent extends BattleEvent {

    private final Pokemon pokemon;
    private final ExperienceCalculator expCalculator;

    // Total exp still to be applied. Decremented as the bar animates.
    private int remainingExp;

    public ExpAnimationEvent(Pokemon pokemon, int exp) {
        this.pokemon = pokemon;
        this.expCalculator = new ExperienceCalculator();
        this.remainingExp = exp;
    }

    @Override
    public void update() {
        // currentExp reached expNextLevel last tick — process the level-up now.
        if (pokemon.getCurrentExp() >= pokemon.getExpNextLevel()) {
            pokemon.setCurrentExp(0);
            pokemon.setLevel(pokemon.getLevel() + 1);
            // TODO: queue a stat-difference popup event here before recalculating
            pokemon.recalculateStats();
            pokemon.setExpNextLevel(expCalculator.calculateExpNextLevel(pokemon));

            if (remainingExp <= 0) {
                isFinished = true;
            }
            return;
        }

        if (remainingExp <= 0) {
            isFinished = true;
            return;
        }

        int toNextLevel = pokemon.getExpNextLevel() - pokemon.getCurrentExp();
        int step = Math.min(30, remainingExp);

        if (step >= toNextLevel) {
            // This step reaches the level-up threshold; clamp and let next tick handle it.
            remainingExp -= toNextLevel;
            pokemon.setCurrentExp(pokemon.getExpNextLevel());
        } else {
            pokemon.setCurrentExp(pokemon.getCurrentExp() + step);
            remainingExp -= step;
        }
    }

    @Override
    public void render(Graphics g, int x, int y) {

    }
}
