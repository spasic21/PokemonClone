package keyInput;

import framework.Handler;
import framework.SoundManager;
import framework.enums.EntityDirection;
import framework.enums.GameState;
import objects.Entity;
import objects.NPC;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

public class PlayerKeyInput extends KeyInput {

    private static final Random RANDOM = new Random();

    public PlayerKeyInput(Handler handler) {
        super(handler);
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

        if (key == KeyEvent.VK_SPACE) {
            handler.getGameKeyInput().resetKeys();
            handler.setNextTransition(RANDOM.nextInt(3) + 1, GameState.Battle);
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

        if (key == KeyEvent.VK_J) {
            NPC targetNpc = null;

            // Direct collision (adjacent NPC)
            if (handler.isEntityCollision() && handler.getCurrentNpc() != null) {
                targetNpc = handler.getCurrentNpc();
            }

            // Interaction zone (e.g. NPC behind a counter)
            if (targetNpc == null) {
                int stw = handler.getWorld().getScaledTileWidth();
                int sth = handler.getWorld().getScaledTileHeight();
                Rectangle playerBounds = handler.getWorld().getEntityManager().getPlayer().getBounds(false);
                for (Entity entity : handler.getWorld().getEntityManager().getEntities()) {
                    if (entity instanceof NPC npc && npc.getInteractionZone(stw, sth).intersects(playerBounds)) {
                        targetNpc = npc;
                        break;
                    }
                }
            }

            if (targetNpc != null) {
                SoundManager.playSound("ButtonSound");
                float px = handler.getWorld().getEntityManager().getPlayer().getX();
                float py = handler.getWorld().getEntityManager().getPlayer().getY();
                float deltaX = px - targetNpc.getX();
                float deltaY = py - targetNpc.getY();
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    targetNpc.setEntityDirection(deltaX > 0 ? EntityDirection.RIGHT : EntityDirection.LEFT);
                } else {
                    targetNpc.setEntityDirection(deltaY > 0 ? EntityDirection.DOWN : EntityDirection.UP);
                }
                handler.setCurrentNpc(targetNpc);
                handler.getDialogueScreen().startDialogue(targetNpc);
                handler.getGame().setGameState(GameState.Dialogue);
            }
        }

        if(key == KeyEvent.VK_ENTER) {
            SoundManager.playSound("MenuSound");
            handler.getGame().setGameState(GameState.Menu);
        }
    }
}
