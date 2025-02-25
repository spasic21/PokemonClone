package keyInput;

import framework.Handler;
import framework.SoundManager;
import framework.enums.GameState;

import java.awt.event.KeyEvent;

public class PokemonMenuKeyInput extends KeyInput {
    private Handler handler;

    private int pokemonOptionId = 0;

    private boolean[] restrictedTiles = {true, true, true, true, true, true};

    public PokemonMenuKeyInput(Handler handler) {
        this.handler = handler;

        checkValidTiles();
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            if (pokemonOptionId < 2) {
                int firstChoice = pokemonOptionId + 4;
                int secondChoice = pokemonOptionId + 2;

                if (!restrictedTiles[firstChoice]) {
                    pokemonOptionId = firstChoice;
                } else if (!restrictedTiles[secondChoice]) {
                    pokemonOptionId = secondChoice;
                }
            } else {
                pokemonOptionId -= 2;
            }

            SoundManager.playSound("ButtonSound");
        }

        if (e.getKeyCode() == KeyEvent.VK_S) {
            if (pokemonOptionId < 2) {
                int next = pokemonOptionId + 2;

                if (!restrictedTiles[next]) {
                    pokemonOptionId = next;
                }
            } else if (pokemonOptionId == 2) {
                pokemonOptionId = !restrictedTiles[4] ? 4 : 0;
            } else if (pokemonOptionId == 3 && !restrictedTiles[5]) {
                pokemonOptionId = 5;
            } else {
                pokemonOptionId = pokemonOptionId == 4 ? 0 : 1;
            }

            SoundManager.playSound("ButtonSound");
        }

        if (e.getKeyCode() == KeyEvent.VK_A) {
            if (pokemonOptionId % 2 != 0) {
                pokemonOptionId--;
            } else if (!restrictedTiles[pokemonOptionId + 1]) {
                pokemonOptionId++;
            }

            SoundManager.playSound("ButtonSound");
        }

        if (e.getKeyCode() == KeyEvent.VK_D) {
            if (pokemonOptionId % 2 == 0 && !restrictedTiles[pokemonOptionId + 1]) {
                pokemonOptionId++;
            } else if (pokemonOptionId % 2 != 0) {
                pokemonOptionId--;
            }

            SoundManager.playSound("ButtonSound");
        }

        if(e.getKeyCode() == KeyEvent.VK_J) {
            handler.setNextTransition(1, GameState.PokemonSummary);
            SoundManager.playSound("ButtonSound");
        }

        if (e.getKeyCode() == KeyEvent.VK_K) {
            handler.setNextTransition(1, GameState.Menu);
            SoundManager.playSound("ButtonSound");
        }

        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
            handler.setNextTransition(1, GameState.Game);
            SoundManager.playSound("ButtonSound");
        }
    }

    private void checkValidTiles() {
        for (int i = 0; i < handler.getPokemonParty().size(); i++) {
            restrictedTiles[i] = false;
        }
    }

    public int getPokemonOptionId() {
        return pokemonOptionId;
    }
}
