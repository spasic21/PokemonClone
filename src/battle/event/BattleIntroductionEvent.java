package battle.event;

import objects.Pokemon;
import objects.Sprite;

import java.awt.*;

public class BattleIntroductionEvent extends BattleEvent {

    private Sprite playerSprite, opponentSprite;

    private Pokemon opponentPokemon;

    private boolean positionOne = false;

    private boolean positionTwo = false;

    public BattleIntroductionEvent(Sprite playerSprite, Pokemon opponentPokemon) {
        this.playerSprite = playerSprite;
        this.opponentPokemon = opponentPokemon;
        this.opponentSprite = this.opponentPokemon.getFrontSprite();
    }

    @Override
    public void update() {
        if(playerSprite.getStartX() > playerSprite.getEndX()) {
            playerSprite.setStartX(playerSprite.getStartX() - 20);
        }else {
            playerSprite.setStartX(playerSprite.getEndX());
            positionOne = true;
        }

        if(opponentSprite.getStartX() < opponentSprite.getEndX()) {
            opponentSprite.setStartX(opponentSprite.getStartX() + 15);
        } else {
            opponentSprite.setStartX(opponentSprite.getEndX());
            positionTwo = true;
        }
    }

    @Override
    public void render(Graphics g, int x, int y) {
        if(positionOne && positionTwo) {
            g.setColor(new Color(201, 211, 211));
            g.drawString("A wild " + opponentPokemon.getName() + " appeared!", x, y);
            isFinished = true;
        }
    }
}
