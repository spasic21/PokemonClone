package battle.event;

import objects.PokemonSprite;

import java.awt.*;

public class PokemonFaintEvent extends BattleEvent {

    private PokemonSprite sprite;

    private int faintLine;

    public PokemonFaintEvent(PokemonSprite sprite, int faintLine) {
        this.sprite = sprite;
        this.faintLine = faintLine;
        this.isFinished = false;
    }

    @Override
    public void update() {
        if(sprite.getY() + 58 >= faintLine) {
            sprite.setAlpha(0.0f);
            isFinished = true;
        } else if(sprite.getY() < faintLine) {
            sprite.setY(sprite.getY() + 20);
            sprite.setAlpha(sprite.getAlpha() - 0.2f);

            if(sprite.getAlpha() <= 0.0f) {
                sprite.setAlpha(0.0f);
                isFinished = true;
            }
        }
    }

    @Override
    public void render(Graphics g, int x, int y) {

    }
}
