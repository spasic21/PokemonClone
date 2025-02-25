package keyInput;

import battle.BattleManager;
import battle.event.PokemonFaintEvent;
import battle.event.TextEvent;
import framework.Handler;
import framework.SoundManager;
import framework.enums.GameState;
import objects.Pokemon;

import java.awt.event.KeyEvent;

public class BattleKeyInput extends KeyInput {

    private int battleOptionId = 1;
    private int moveOptionId = 1;

    private Handler handler;
    private BattleManager battleManager;

    public BattleKeyInput(Handler handler, BattleManager battleManager) {
        this.handler = handler;
        this.battleManager = battleManager;
    }

    public void keyPressed(KeyEvent e) {}

    public void keyReleased(KeyEvent e) {
        if(battleManager.getBattleScreenState() == BattleManager.BattleScreenState.Introduction) {
            introductionControls(e.getKeyCode());
        } else if (battleManager.getBattleScreenState() == BattleManager.BattleScreenState.BattleOptionSelect) {
            battleControls(e.getKeyCode());
        } else if (battleManager.getBattleScreenState() == BattleManager.BattleScreenState.MoveSelect) {
            moveSelectControls(e.getKeyCode());
        } else if (battleManager.getBattleScreenState() == BattleManager.BattleScreenState.Battle) {
            progressControls(e.getKeyCode());
        } else if (battleManager.getBattleScreenState() == BattleManager.BattleScreenState.BattleWin) {

            handler.getGame().setBattleStarted(false);
            handler.getGame().setGameState(GameState.Game);
        }
    }

    private void introductionControls(int keyCode) {
        if(keyCode == KeyEvent.VK_J) {
            if(battleManager.getCurrentEvent().isFinished() && battleManager.getBattleEventQueue().peek() != null) {
                battleManager.setCurrentEvent(battleManager.getBattleEventQueue().poll());
                SoundManager.playSound("ButtonSound");
            } else if (battleManager.getCurrentEvent().isFinished() && battleManager.getBattleEventQueue().peek() == null) {
                battleManager.setCurrentEvent(null);
                battleManager.setBattleScreenState(BattleManager.BattleScreenState.BattleOptionSelect);
            }
        }
    }

    private void battleControls(int keyCode) {
        if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_D) {
            if (battleOptionId == 1 || battleOptionId == 3) {
                ++battleOptionId;
            } else {
                --battleOptionId;
            }

            SoundManager.playSound("ButtonSound");
        } else if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_S) {
            if (battleOptionId == 1 || battleOptionId == 2) {
                battleOptionId += 2;
            } else {
                battleOptionId -= 2;
            }

            SoundManager.playSound("ButtonSound");
        } else if (keyCode == KeyEvent.VK_J) {
            if (battleOptionId == 1) {
                battleManager.setBattleScreenState(BattleManager.BattleScreenState.MoveSelect);
            } else if (battleOptionId == 4) {
                SoundManager.playSound("RunningAwaySound");
                handler.getGame().setBattleStarted(false);
                handler.getGame().setGameState(GameState.Game);
            }

            SoundManager.playSound("ButtonSound");
        }

        battleManager.setBattleOptionId(battleOptionId);
    }

    private void moveSelectControls(int keyCode) {
        Pokemon playerPokemon = battleManager.getPlayerPokemon();
        int numMove = playerPokemon.getPokemonMovesList().size();

        if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_D) {
            if (moveOptionId == 1 || moveOptionId == 3) {
                ++moveOptionId;

                if ((moveOptionId == 2 && numMove == 1) || (moveOptionId == 4 && numMove == 3)) {
                    --moveOptionId;
                }
            } else {
                --moveOptionId;
            }

            SoundManager.playSound("ButtonSound");
        } else if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_S) {
            if (moveOptionId == 1 || moveOptionId == 2) {
                moveOptionId += 2;

                if ((moveOptionId == 3 && numMove == 2) || (moveOptionId == 4 && numMove == 2) || (moveOptionId == 4 && numMove == 3)) {
                    moveOptionId -= 2;
                }
            } else {
                moveOptionId -= 2;
            }

            SoundManager.playSound("ButtonSound");
        } else if (keyCode == KeyEvent.VK_J) {
            battleManager.setupBattleTurns();
            battleManager.setCurrentEvent(battleManager.getBattleEventQueue().poll());
            battleManager.setBattleScreenState(BattleManager.BattleScreenState.Battle);
            SoundManager.playSound("ButtonSound");
        } else if (keyCode == KeyEvent.VK_K) {
            battleManager.setBattleScreenState(BattleManager.BattleScreenState.BattleOptionSelect);
            SoundManager.playSound("ButtonSound");
        }

        battleManager.setMoveSelectId(moveOptionId);
    }

    private void progressControls(int keyCode) {
        if (keyCode == KeyEvent.VK_J) {
            if (battleManager.getCurrentEvent().isFinished() && battleManager.getBattleEventQueue().peek() != null) {
                if(battleManager.getBattleEventQueue().peek() instanceof PokemonFaintEvent) SoundManager.playSound("FaintedSound");

//                if(battleManager.getBattleEventQueue().peek() instanceof TextEvent textEvent && textEvent.getText().contains("wins")) handler.getGame().playMusicIfNeeded("/sounds/victory_wild_pokemon.wav");

                battleManager.setCurrentEvent(battleManager.getBattleEventQueue().poll());
            } else if (battleManager.getCurrentEvent().isFinished() && battleManager.getBattleEventQueue().peek() == null) {
                battleManager.setCurrentEvent(null);
            }

            if (battleManager.getBattleScreenState() == BattleManager.BattleScreenState.Battle) {
                if (battleManager.getBattleEventQueue().isEmpty() && battleManager.getCurrentEvent() == null) {
                    if (battleManager.isBattleOver()) {
                        battleManager.setBattleScreenState(BattleManager.BattleScreenState.BattleWin);
                    } else {
                        battleManager.setBattleScreenState(BattleManager.BattleScreenState.BattleOptionSelect);
                    }
                }
            }

            SoundManager.playSound("ButtonSound");
        }
    }
}
