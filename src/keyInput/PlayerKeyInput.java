package keyInput;

import objects.Player;

import java.awt.event.KeyEvent;

public class PlayerKeyInput extends KeyInput {

    private Player player;

    public PlayerKeyInput(Player player) {
        this.player = player;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_D) {
            player.setPlayerState(Player.PlayerState.Walking);
            player.setPlayerDirection(Player.PlayerDirection.RIGHT);
            player.setVelX(player.getMovementSpeed());
            keyDown[0] = true;
        }

        if (key == KeyEvent.VK_A) {
            player.setPlayerState(Player.PlayerState.Walking);
            player.setPlayerDirection(Player.PlayerDirection.LEFT);
            player.setVelX(-player.getMovementSpeed());
            keyDown[1] = true;
        }

        if (key == KeyEvent.VK_S) {
            player.setPlayerState(Player.PlayerState.Walking);
            player.setPlayerDirection(Player.PlayerDirection.DOWN);
            player.setVelY(player.getMovementSpeed());
            keyDown[2] = true;
        }

        if (key == KeyEvent.VK_W) {
            player.setPlayerState(Player.PlayerState.Walking);
            player.setPlayerDirection(Player.PlayerDirection.UP);
            player.setVelY(-player.getMovementSpeed());
            keyDown[3] = true;
        }

        if(key == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        player.setPlayerState(Player.PlayerState.Standing);

        if (key == KeyEvent.VK_D) {
            keyDown[0] = false;
        }
        if (key == KeyEvent.VK_A) {
            keyDown[1] = false;
        }
        if (key == KeyEvent.VK_S) {
            keyDown[2] = false;
        }
        if (key == KeyEvent.VK_W) {
            keyDown[3] = false;
        }

        if (!keyDown[0] && !keyDown[1]) player.setVelX(0);
        if (!keyDown[2] && !keyDown[3]) player.setVelY(0);

        if(key == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }
}
