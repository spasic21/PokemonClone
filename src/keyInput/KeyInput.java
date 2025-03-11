package keyInput;

import framework.Handler;

import java.awt.event.KeyEvent;

public abstract class KeyInput {

    protected Handler handler;

    protected boolean up;
    protected boolean down;
    protected boolean left;
    protected boolean right;

    public KeyInput(Handler handler) {
        this.handler = handler;

        up = false;
        down = false;
        left = false;
        right = false;
    }

    public abstract void keyPressed(KeyEvent e);

    public abstract void keyReleased(KeyEvent e);
}
