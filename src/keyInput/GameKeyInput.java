package keyInput;

import battle.BattleManager;
import framework.Handler;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameKeyInput extends KeyAdapter {

    private Handler handler;

    private PlayerKeyInput playerKeyInput;

    private BattleKeyInput battleKeyInput;

    private MenuKeyInput menuKeyInput;

    private PokemonMenuKeyInput pokemonMenuKeyInput;

    private DialogueKeyInput dialogueKeyInput;

    public GameKeyInput(Handler handler, BattleManager battleManager) {
        this.handler = handler;
        this.playerKeyInput = new PlayerKeyInput(handler);
        this.battleKeyInput = new BattleKeyInput(handler, battleManager);
        this.menuKeyInput = new MenuKeyInput(handler);
        this.pokemonMenuKeyInput = new PokemonMenuKeyInput(handler);
        this.dialogueKeyInput = new DialogueKeyInput(handler);
    }

    public void keyPressed(KeyEvent e) {
        switch (handler.getGame().getGameState()) {
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
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }

        switch (handler.getGame().getGameState()) {
            case Game:
                playerKeyInput.keyReleased(e);
                break;

            case Battle:
                battleKeyInput.keyReleased(e);
                break;

            case Menu:
                menuKeyInput.keyReleased(e);
                break;

            case PokemonMenu:
                pokemonMenuKeyInput.keyReleased(e);
                break;

            case Dialogue:
                dialogueKeyInput.keyReleased(e);
                break;
        }
    }

    public boolean isUp() {
        return playerKeyInput.up;
    }

    public boolean isDown() {
        return playerKeyInput.down;
    }

    public boolean isRight() {
        return playerKeyInput.right;
    }

    public boolean isLeft() {
        return playerKeyInput.left;
    }

    public void resetKeys() {
        playerKeyInput.up = false;
        playerKeyInput.down = false;
        playerKeyInput.left = false;
        playerKeyInput.right = false;
    }

    public int getMenuOptionId() {
        return menuKeyInput.getMenuOptionId();
    }

    public int getPokemonMenuOptionId() {
        return pokemonMenuKeyInput.getPokemonOptionId();
    }
}
