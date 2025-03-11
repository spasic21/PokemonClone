package keyInput;

import framework.Handler;
import framework.SoundManager;
import framework.enums.GameState;

import java.awt.event.KeyEvent;

public class MenuKeyInput extends KeyInput {

    private int menuOptionId = 0;

    public MenuKeyInput(Handler handler) {
        super(handler);
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_W) {
            if(menuOptionId == 0) {
                menuOptionId = 6;
            } else {
                menuOptionId--;
            }

            SoundManager.playSound("ButtonSound");
        }

        if(e.getKeyCode() == KeyEvent.VK_S) {
            if(menuOptionId == 6) {
                menuOptionId = 0;
            } else {
                menuOptionId++;
            }

            SoundManager.playSound("ButtonSound");
        }

        if(e.getKeyCode() == KeyEvent.VK_J) {
            switch(menuOptionId) {
                case 0 -> System.out.println("Pokedex");
                case 1 -> handler.setNextTransition(1, GameState.PokemonMenu);
                case 2 -> handler.setNextTransition(1, GameState.Bag);
                case 3 -> System.out.println("ID Card");
                case 4 -> System.out.println("Save");
                case 5 -> System.out.println("Options");
                default -> handler.getGame().setGameState(GameState.Game);
            }

            menuOptionId = 0;
            SoundManager.playSound("ButtonSound");
        }

        if(e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_K) {
            SoundManager.playSound("ButtonSound");
            handler.getGame().setGameState(GameState.Game);
        }
    }

    public int getMenuOptionId() {
        return menuOptionId;
    }
}
