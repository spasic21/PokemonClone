package keyInput;

import framework.Handler;
import framework.SoundManager;
import framework.enums.GameState;

import java.awt.event.KeyEvent;

public class DialogueKeyInput extends KeyInput {

    public DialogueKeyInput(Handler handler) {
        super(handler);
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_J) {
            SoundManager.playSound("ButtonSound");
            if (handler.getDialogueScreen().isLastPage()) {
                handler.getGame().setGameState(GameState.Game);
            } else {
                handler.getDialogueScreen().advancePage();
            }
        }
    }
}
