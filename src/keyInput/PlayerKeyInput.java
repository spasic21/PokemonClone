package keyInput;

import framework.Handler;
import framework.SoundManager;
import framework.enums.EntityDirection;
import framework.enums.GameState;

import java.awt.event.KeyEvent;

public class PlayerKeyInput extends KeyInput {

    private Handler handler;

    public PlayerKeyInput(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_W && !up) {
            up = true;
        }

        if (key == KeyEvent.VK_S && !down) {
            down = true;
        }

        if (key == KeyEvent.VK_D && !right) {
            right = true;
        }

        if (key == KeyEvent.VK_A && !left) {
            left = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_W) {
            up = false;
        }
        if (key == KeyEvent.VK_S) {
            down = false;
        }

        if (key == KeyEvent.VK_D) {
            right = false;
        }

        if (key == KeyEvent.VK_A) {
            left = false;
        }

        if(key == KeyEvent.VK_J && handler.isEntityCollision()) {
            SoundManager.playSound("ButtonSound");
            float x = handler.getWorld().getEntityManager().getPlayer().getX();
            float y = handler.getWorld().getEntityManager().getPlayer().getY();
            float deltaX = x - handler.getCurrentNpc().getX();
            float deltaY = y - handler.getCurrentNpc().getY();

            if(Math.abs(deltaX) > Math.abs(deltaY)) {
                handler.getCurrentNpc().setEntityDirection(deltaX > 0 ? EntityDirection.RIGHT : EntityDirection.LEFT);
            } else {
                handler.getCurrentNpc().setEntityDirection(deltaY > 0 ? EntityDirection.DOWN : EntityDirection.UP);
            }

            handler.getGame().setGameState(GameState.Dialogue);
        }

        if(key == KeyEvent.VK_ENTER) {
            SoundManager.playSound("MenuSound");
            handler.getGame().setGameState(GameState.Menu);
        }
    }
}
