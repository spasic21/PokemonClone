package keyInput;

import battle.BattleManager;
import framework.GameState;
import objects.Pokemon;
import ui.Game;

import java.awt.event.KeyEvent;

public class BattleKeyInput extends KeyInput {

    private int battleOptionId = 1;
    private int moveOptionId = 1;
    private BattleManager battleManager;

    public BattleKeyInput(BattleManager battleManager) {
        this.battleManager = battleManager;
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }

    public void keyReleased(KeyEvent e) {
        if (battleManager.getBattleScreenState() == BattleManager.BattleScreenState.BattleOptionSelect) {
            battleControls(e.getKeyCode());
        } else if (battleManager.getBattleScreenState() == BattleManager.BattleScreenState.MoveSelect) {
            moveSelectControls(e.getKeyCode());
        } else if (battleManager.getBattleScreenState() == BattleManager.BattleScreenState.Battle) {
            progressControls(e.getKeyCode());
        } else if (battleManager.getBattleScreenState() == BattleManager.BattleScreenState.BattleWin) {
//            battleManager.setBattleScreenState(BattleManager.BattleScreenState.BattleOptionSelect);

            Game.setBattleStarted(false);
            Game.setGameState(GameState.Game);
        }
    }

    private void battleControls(int keyCode) {
        if (keyCode == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        } else if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_D) {
            if (battleOptionId == 1 || battleOptionId == 3) {
                ++battleOptionId;
            } else {
                --battleOptionId;
            }
        } else if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_S) {
            if (battleOptionId == 1 || battleOptionId == 2) {
                battleOptionId += 2;
            } else {
                battleOptionId -= 2;
            }
        } else if (keyCode == KeyEvent.VK_J) {
            if (battleOptionId == 1) {
                battleManager.setBattleScreenState(BattleManager.BattleScreenState.MoveSelect);
            } else if (battleOptionId == 4) {
//                System.exit(0);

                Game.setBattleStarted(false);
                Game.setGameState(GameState.Game);
            }
        } else if (keyCode == KeyEvent.VK_K) {
            if (battleManager.getBattleScreenState() == BattleManager.BattleScreenState.MoveSelect) {
                battleManager.setBattleScreenState(BattleManager.BattleScreenState.BattleOptionSelect);
            }
        }

        battleManager.setBattleOption(battleOptionId);
    }

    private void moveSelectControls(int keyCode) {
        Pokemon playerPokemon = battleManager.getPlayerPokemon();
        int numMove = playerPokemon.getPokemonMovesList().size();

        if (keyCode == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        } else if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_D) {
            if (moveOptionId == 1 || moveOptionId == 3) {
                ++moveOptionId;

                if ((moveOptionId == 2 && numMove == 1) || (moveOptionId == 4 && numMove == 3)) {
                    --moveOptionId;
                }
            } else {
                --moveOptionId;
            }
        } else if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_S) {
            if (moveOptionId == 1 || moveOptionId == 2) {
                moveOptionId += 2;

                if ((moveOptionId == 3 && numMove == 2) || (moveOptionId == 4 && numMove == 2) || (moveOptionId == 4 && numMove == 3)) {
                    moveOptionId -= 2;
                }
            } else {
                moveOptionId -= 2;
            }
        } else if (keyCode == KeyEvent.VK_J) {
            battleManager.setupBattleTurns();
            battleManager.setCurrentEvent(battleManager.getBattleEventQueue().poll());
            battleManager.setBattleScreenState(BattleManager.BattleScreenState.Battle);
        } else if (keyCode == KeyEvent.VK_K) {
            battleManager.setBattleScreenState(BattleManager.BattleScreenState.BattleOptionSelect);
        }

        battleManager.setMoveSelectId(moveOptionId);
    }

    private void progressControls(int keyCode) {
        if (keyCode == KeyEvent.VK_J) {
            if (battleManager.getCurrentEvent().isFinished() && battleManager.getBattleEventQueue().peek() != null) {
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
        }
    }
}
