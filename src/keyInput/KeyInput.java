package keyInput;

import java.awt.event.KeyEvent;

public abstract class KeyInput {

    protected boolean[] keyDown = new boolean[4];

    public KeyInput() {
        keyDown[0] = false;
        keyDown[1] = false;
        keyDown[2] = false;
        keyDown[3] = false;
    }

    public abstract void keyPressed(KeyEvent e);
    public abstract void keyReleased(KeyEvent e);
}
