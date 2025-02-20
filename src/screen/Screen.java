package screen;

import framework.Handler;

import java.awt.*;

public abstract class Screen {

    protected Handler handler;

    public Screen(Handler handler) {
        this.handler = handler;
    }

    public abstract void update();

    public abstract void render(Graphics g);
}
