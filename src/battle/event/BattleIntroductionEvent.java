package battle.event;

import objects.Sprite;

import java.awt.*;

public class BattleIntroductionEvent extends BattleEvent {

    private Sprite playerSprite, opponentSprite;

    private boolean positionOne = false;

    private boolean positionTwo = false;

    public BattleIntroductionEvent(Sprite playerSprite, Sprite opponentSprite) {
        this.playerSprite = playerSprite;
        this.opponentSprite = opponentSprite;
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

        if(positionOne && positionTwo) {
            isFinished = true;
        }
    }

    @Override
    public void render(Graphics g, int x, int y) {

    }
}
