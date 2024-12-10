package battle.event;

import java.awt.*;

public abstract class BattleEvent {

    protected boolean isFinished = false;

    public abstract void update();

    public abstract void render(Graphics g, int x, int y);

    public boolean isFinished() {
        return isFinished;
    }

    public void setIsFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }
}
