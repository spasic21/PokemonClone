package battle.event;

import objects.Sprite;

import java.awt.*;

public class PokemonFaintEvent extends BattleEvent {

    private Sprite sprite;

    private int faintLine;

    public PokemonFaintEvent(Sprite sprite, int faintLine) {
        this.sprite = sprite;
        this.faintLine = faintLine;
    }

    @Override
    public void update() {
        if(sprite.getEndY() + 58 >= faintLine) {
            sprite.setAlpha(0.0f);
            isFinished = true;
        } else if(sprite.getEndY() < faintLine) {
            sprite.setEndY(sprite.getEndY() + 20);
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
