package battle.event;

import java.awt.*;

public class TextEvent extends BattleEvent {

    private String text;

    public TextEvent(String text) {
        this.text = text;
    }

    @Override
    public void update() {

    }

    @Override
    public void render(Graphics g, int x, int y) {
        g.setColor(new Color(201, 211, 211));
        g.drawString(text, x, y);

        isFinished = true;
    }

    public String getText() {
        return text;
    }
}
