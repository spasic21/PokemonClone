package keyInput;

import framework.Handler;
import framework.SoundManager;
import framework.enums.GameState;

import java.awt.event.KeyEvent;

public class BagKeyInput extends KeyInput {

    private int pocketId = 0;

    private int itemId = 0;

    private int startIndex = 0;

    public BagKeyInput(Handler handler) {
        super(handler);
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        int pocketSize = getPocketSize();

        if (keyCode == KeyEvent.VK_W && itemId > 0) {
            switchItem(-1);
        } else if (keyCode == KeyEvent.VK_S && itemId < pocketSize - 1) {
            switchItem(1);
        } else if (keyCode == KeyEvent.VK_A && pocketId > 0) {
            switchPocket(-1);
        } else if(keyCode == KeyEvent.VK_D && pocketId < 2) {
            switchPocket(1);
        } else if(keyCode == KeyEvent.VK_K) {
            itemId = 0;
            pocketId = 0;
            startIndex = 0;

            SoundManager.playSound("ButtonSound");
            handler.setNextTransition(1, GameState.Menu);
        } else if(keyCode == KeyEvent.VK_ENTER) {
            SoundManager.playSound("ButtonSound");
            handler.setNextTransition(1, GameState.Game);
        }
    }

    private int getPocketSize() {
        return switch (pocketId) {
            case 0 -> handler.getBag().getItemPocket().size();
            case 1 -> handler.getBag().getPokeballPocket().size();
            default -> 0;
        };
    }

    private void switchItem(int delta) {
        SoundManager.playSound("ButtonSound");
        itemId += delta;

        if(itemId < startIndex || itemId > startIndex + 6) {
            startIndex += delta;
        }
    }

    private void switchPocket(int delta) {
        SoundManager.playSound("ButtonSound");
        pocketId += delta;
        itemId = 0;
        startIndex = 0;
    }

    public int getPocketId() {
        return pocketId;
    }

    public int getItemId() {
        return itemId;
    }

    public int getStartIndex() {
        return startIndex;
    }
}
