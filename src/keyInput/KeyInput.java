package keyInput;

import java.awt.event.KeyEvent;

public abstract class KeyInput {

    protected boolean up;
    protected boolean down;
    protected boolean left;
    protected boolean right;

    public KeyInput() {
        up = false;
        down = false;
        left = false;
        right = false;
    }

    public abstract void keyPressed(KeyEvent e);

    public abstract void keyReleased(KeyEvent e);
}
