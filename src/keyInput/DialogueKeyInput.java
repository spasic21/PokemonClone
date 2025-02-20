package keyInput;

import framework.Handler;
import framework.SoundManager;
import framework.enums.GameState;

import java.awt.event.KeyEvent;

public class DialogueKeyInput extends KeyInput {

    private Handler handler;

    public DialogueKeyInput(Handler handler) { this.handler = handler; }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_J) {
            SoundManager.playSound("ButtonSound");
            handler.getGame().setGameState(GameState.Game);
        }
    }
}
