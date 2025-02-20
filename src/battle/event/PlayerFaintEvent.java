package battle.event;

import objects.PokemonBackSprite;

import java.awt.*;

public class PlayerFaintEvent extends BattleEvent {

    private PokemonBackSprite sprite;

    private int faintLine;

    public PlayerFaintEvent(PokemonBackSprite sprite, int faintLine) {
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
