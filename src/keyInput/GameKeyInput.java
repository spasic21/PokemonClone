package keyInput;

import battle.BattleManager;
import framework.Handler;
import framework.enums.GameState;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameKeyInput extends KeyAdapter {

    private final Handler handler;

    private final PlayerKeyInput playerKeyInput;

    private final BattleKeyInput battleKeyInput;

    private final MenuKeyInput menuKeyInput;

    private final PokemonMenuKeyInput pokemonMenuKeyInput;

    private final PokemonSummaryKeyInput pokemonSummaryKeyInput;

    private final BagKeyInput bagKeyInput;

    private final DialogueKeyInput dialogueKeyInput;

    public GameKeyInput(Handler handler, BattleManager battleManager) {
        this.handler = handler;
        this.playerKeyInput = new PlayerKeyInput(handler);
        this.battleKeyInput = new BattleKeyInput(handler, battleManager);
        this.menuKeyInput = new MenuKeyInput(handler);
        this.pokemonMenuKeyInput = new PokemonMenuKeyInput(handler);
        this.pokemonSummaryKeyInput = new PokemonSummaryKeyInput(handler);
        this.bagKeyInput = new BagKeyInput(handler);
        this.dialogueKeyInput = new DialogueKeyInput(handler);
    }

    public void keyPressed(KeyEvent e) {
        if (handler.getGame().getGameState() == GameState.Game) {
            playerKeyInput.keyPressed(e);
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }

        switch (handler.getGame().getGameState()) {
            case Game -> playerKeyInput.keyReleased(e);
            case Battle -> battleKeyInput.keyReleased(e);
            case Menu -> menuKeyInput.keyReleased(e);
            case PokemonMenu -> pokemonMenuKeyInput.keyReleased(e);
            case PokemonSummary -> pokemonSummaryKeyInput.keyReleased(e);
            case Bag -> bagKeyInput.keyReleased(e);
            case Dialogue -> dialogueKeyInput.keyReleased(e);
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

    public int getPokemonSummaryPageId() {
        return pokemonSummaryKeyInput.getPageId();
    }

    public int getPokemonId() {
        return pokemonSummaryKeyInput.getPokemonId();
    }

    public void setPokemonId(int pokemonId) {
        pokemonSummaryKeyInput.setPokemonId(pokemonId);
    }

    public boolean isMoveSelect() {
        return pokemonSummaryKeyInput.isMoveSelect();
    }

    public int getMoveId() {
        return pokemonSummaryKeyInput.getMoveId();
    }

    public int getPocketId() {
        return bagKeyInput.getPocketId();
    }

    public int getItemId() {
        return bagKeyInput.getItemId();
    }

    public int getStartIndex() {
        return bagKeyInput.getStartIndex();
    }
}
