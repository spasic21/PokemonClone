package keyInput;

import framework.Handler;
import framework.SoundManager;
import framework.enums.GameState;

import java.awt.event.KeyEvent;

public class PokemonSummaryKeyInput extends KeyInput {
    private final Handler handler;
    private int pageId = 1;
    private int pokemonId = 0;
    private int moveId = 0;
    private boolean moveSelect = false;

    public PokemonSummaryKeyInput(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        boolean soundPlayed = true;
        int key = e.getKeyCode();

        if (moveSelect) {
            switch (key) {
                case KeyEvent.VK_W -> moveUp(true);
                case KeyEvent.VK_S -> moveDown(true);
                case KeyEvent.VK_K -> toggleMoveSelect(false);
                default -> soundPlayed = false;
            }
        } else {
            switch (key) {
                case KeyEvent.VK_W -> moveUp(false);
                case KeyEvent.VK_S -> moveDown(false);
                case KeyEvent.VK_A -> moveLeft();
                case KeyEvent.VK_D -> moveRight();
                case KeyEvent.VK_J -> { if (pageId == 3) toggleMoveSelect(true); }
                case KeyEvent.VK_K -> changeState(GameState.PokemonMenu);
                case KeyEvent.VK_ENTER -> changeState(GameState.Game);
                default -> soundPlayed = false;
            }
        }

        if (soundPlayed) playButtonSound();
    }

    private void moveUp(boolean isMoveSelection) {
        if (isMoveSelection) {
            if (moveId > 0) moveId--;
        } else {
            if (pokemonId > 0) pokemonId--;
        }
    }

    private void moveDown(boolean isMoveSelection) {
        if (isMoveSelection) {
            if (moveId < handler.getPokemonParty().get(pokemonId).getPokemonMovesList().size() - 1) moveId++;
        } else {
            if (pokemonId < handler.getPokemonParty().size() - 1) pokemonId++;
        }
    }

    private void moveLeft() {
        if (pageId > 1) pageId--;
    }

    private void moveRight() {
        if (pageId < 3) pageId++;
    }

    private void toggleMoveSelect(boolean enable) {
        moveSelect = enable;
        if (!enable) moveId = 0;
    }

    private void changeState(GameState state) {
        pageId = 1;
        handler.setNextTransition(1, state);
    }

    private void playButtonSound() {
        SoundManager.playSound("ButtonSound");
    }

    public int getPageId() { return pageId; }
    public int getPokemonId() { return pokemonId; }
    public int getMoveId() { return moveId; }
    public boolean isMoveSelect() { return moveSelect; }
}
