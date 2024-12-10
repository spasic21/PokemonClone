package keyInput;

import battle.BattleManager;
import objects.Player;
import ui.Game;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameKeyInput extends KeyAdapter {

    private PlayerKeyInput playerKeyInput;

    private BattleKeyInput battleKeyInput;

    public GameKeyInput(Player player, BattleManager battleManager) {
        this.playerKeyInput = new PlayerKeyInput(player);
        this.battleKeyInput = new BattleKeyInput(battleManager);
    }

    public void keyPressed(KeyEvent e) {
        switch (Game.gameState) {
            case Game:
                playerKeyInput.keyPressed(e);
                break;

            case Transition:
                break;

            case Battle:
                battleKeyInput.keyPressed(e);
                break;
        }
    }

    public void keyReleased(KeyEvent e) {
        switch (Game.gameState) {
            case Game:
                playerKeyInput.keyReleased(e);
                break;

            case Battle:
                battleKeyInput.keyReleased(e);
                break;
        }
    }
}
