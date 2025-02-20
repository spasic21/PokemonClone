package battle;

import battle.event.*;
import framework.ExperienceCalculator;
import framework.PokemonGenerator;
import framework.TypeTable;
import framework.enums.Type;
import objects.Pokemon;
import objects.PokemonMove;

import java.util.*;

public class BattleManager {

    private static BattleManager battleManager;

    private List<Pokemon> playerParty;

    private List<Pokemon> trainerParty;

    private Pokemon playerPokemon;

    private Pokemon trainerPokemon;

    public Queue<BattleEvent> battleEventQueue = new LinkedList<>();

    private BattleEvent currentEvent = null;

    private boolean battleOver;

    private static BattleOption battleOption;

    private static BattleScreenState battleScreenState;

    private static int moveSelectId;

    private int trainerPartyIndex = 0;

    private TypeTable typeTable;

    public enum BattleOption {
        FIGHT(1),
        BAG(2),
        POKEMON(3),
        RUN(4);

        private int optionId;

        BattleOption(int optionId) {
            this.optionId = optionId;
        }

        int getOptionId() {
            return optionId;
        }

        void setOptionId(int optionId) {
            this.optionId = optionId;
        }

        Optional<BattleOption> valueOf(int optionId) {
            return Arrays.stream(values())
                    .filter(option -> option.optionId == optionId)
                    .findFirst();
        }
    }

    public enum BattleScreenState {
        Dialogue,
        BattleOptionSelect,
        MoveSelect,
        Battle,
        PlayerFainted,
        TrainerFainted,
        BattleWin,
        BattleLoss
    }

    private BattleManager() {
    }

    public static BattleManager getInstance() {
        if (battleManager == null) {
            battleManager = new BattleManager();
        }

        return battleManager;
    }

    public void init(List<Pokemon> playerParty, List<Pokemon> trainerParty) {
        PokemonGenerator generator = new PokemonGenerator();
        this.playerParty = playerParty;
        this.trainerParty = Collections.singletonList(generator.generatePokemon());

        this.playerPokemon = playerParty.get(0);
        this.trainerPokemon = this.trainerParty.get(0);
        this.typeTable = new TypeTable();
        battleOption = BattleOption.FIGHT;
        battleScreenState = BattleScreenState.BattleOptionSelect;
        moveSelectId = 1;
    }

    public int calculateDamage(PokemonMove pokemonMove, Pokemon attacker, Pokemon defender) {
        int damage;
        double attackDefenseRatio = 1.0;
        int targets = 1;
        double parentalBond = 1.0;
        double weather = 1.0;
        double critical = 1.0;
        double random = 0.85 + (Math.random() * 0.15);
        double stab = 1.0;

        int movePower = pokemonMove.getDamage();

        if (pokemonMove.getMoveCategory() == PokemonMove.MoveCategory.Physical) {
            attackDefenseRatio = (double) attacker.getAttack() / (double) defender.getDefense();
        } else if (pokemonMove.getMoveCategory() == PokemonMove.MoveCategory.Special) {
            attackDefenseRatio = (double) attacker.getSpecialAttack() / (double) defender.getSpecialDefense();
        }

        double levelPowerRatio = ((((2 * (double) attacker.getLevel() / 5) + 2) * movePower * attackDefenseRatio) / 50) + 2;

        if (attacker.getType1().equals(pokemonMove.getType()) || (attacker.getType2() != null && attacker.getType2().equals(pokemonMove.getType()))) {
            stab = 1.5;
        }

        double effectiveValue = getEffectiveRatio(pokemonMove, defender);

        if (effectiveValue >= 2) {
            battleEventQueue.add(new TextEvent("It's a super effective move!"));
        }else if (effectiveValue == 0) {
            battleEventQueue.add(new TextEvent("It doesn't affect " + defender.getName() + "!"));
        } else if(effectiveValue < 1){
            battleEventQueue.add(new TextEvent("It's not an effective move!"));
        }

        damage = (int) Math.round(levelPowerRatio * targets * parentalBond * weather * critical * random * stab * effectiveValue);

        return damage;
    }

    public void setupBattleTurns() {
        PokemonMove playerMove = playerPokemon.getMove(moveSelectId - 1);
        PokemonMove trainerMove = getTrainerMove();

        if (isFirstTurn()) {
            battleTurn(playerMove, playerPokemon, trainerPokemon);

            if (trainerPokemon.isFainted()) {
                trainerFaintCondition();
            } else {
                battleTurn(trainerMove, trainerPokemon, playerPokemon);

                if(playerPokemon.isFainted()) {
                    playerFaintCondition();
                }
            }
        } else {
            battleTurn(trainerMove, trainerPokemon, playerPokemon);

            if(playerPokemon.isFainted()) {
                playerFaintCondition();
            } else {
                battleTurn(playerMove, playerPokemon, trainerPokemon);

                if (trainerPokemon.isFainted()) {
                    trainerFaintCondition();
                }
            }
        }
    }

    private void playerFaintCondition() {
        battleEventQueue.add(new PlayerFaintEvent(playerPokemon.getBackSprite(), getPlayerFaintLine()));
        battleEventQueue.add(new TextEvent(playerPokemon.getName() + " has fainted!"));

        if(!allPokemonFainted(playerParty)) {
            // Choose another pokemon in pokemon party screen
        }else {
            battleOver = true;
            battleEventQueue.add(new TextEvent(trainerPokemon.getName() + " has won the battle!"));
        }
    }

    private void trainerFaintCondition() {
        ExperienceCalculator calculator = new ExperienceCalculator();
        int exp = calculator.calculateExp(trainerPokemon);

        battleEventQueue.add(new TrainerFaintEvent(trainerPokemon.getFrontSprite(), getTrainerFaintLine()));
        battleEventQueue.add(new TextEvent(trainerPokemon.getName() + " has fainted!"));
        battleEventQueue.add(new TextEvent(playerPokemon.getName() + " gained " + exp + " EXP. Points!"));
        battleEventQueue.add(new ExpAnimationEvent(playerPokemon, exp));

        if (!allPokemonFainted(trainerParty)) {
            trainerPartyIndex++;
            battleEventQueue.add(new TextEvent("Trainer sent out " + trainerParty.get(trainerPartyIndex).getName() + " !"));
            battleEventQueue.add(new ChangeSpriteEvent(battleManager, trainerParty.get(trainerPartyIndex)));
        } else {
            battleOver = true;
            battleEventQueue.add(new TextEvent("Gugi's " + playerPokemon.getName() + " has won the battle!"));
        }
    }

    private void battleTurn(PokemonMove move, Pokemon attacker, Pokemon defender) {
        move.setCurrentPowerPoints(move.getCurrentPowerPoints() - 1);
        battleEventQueue.add(new TextEvent(attacker.getName() + " used " + move.getName() + "!"));

        if(move.getMoveCategory() == PokemonMove.MoveCategory.Physical || move.getMoveCategory() == PokemonMove.MoveCategory.Special) {
            int damage = calculateDamage(move, attacker, defender);

            battleEventQueue.add(new HPAnimationEvent(defender, damage));

            if (defender.getCurrentHealth() - damage < 0) {
                defender.setFainted(true);
            }
        } else {
            battleEventQueue.add(new TextEvent(attacker.getName() + " did nothing!"));
        }
    }

    private PokemonMove getTrainerMove() {
        double effectiveRatio;
        int highestMovePower = 0;
        PokemonMove highestDamageMove = null;

        for (PokemonMove move : trainerPokemon.getPokemonMovesList()) {
            if(move.getCurrentPowerPoints() > 0) {
                effectiveRatio = getEffectiveRatio(move, playerPokemon);

                if(effectiveRatio > 1) {
                    move.setCurrentPowerPoints(move.getCurrentPowerPoints() - 1);

                    return move;
                } else if(effectiveRatio > 0 && move.getDamage() > highestMovePower) {
                    highestMovePower = move.getDamage();
                    highestDamageMove = move;
                }
            }
        }

        return highestDamageMove != null ? highestDamageMove : new PokemonMove("Struggle", Type.Normal, 50, 100, 100, 100, PokemonMove.MoveCategory.Physical);
    }

    private double getEffectiveRatio(PokemonMove move, Pokemon defender) {
        double effectiveRatio;

        if (defender.getType2() != null) {
            double typeRatio1 = typeTable.getMoveEffectiveValue(move.getType(), defender.getType1());
            double typeRatio2 = typeTable.getMoveEffectiveValue(move.getType(), defender.getType2());
            effectiveRatio = typeRatio1 * typeRatio2;
        } else {
            effectiveRatio = typeTable.getMoveEffectiveValue(move.getType(), playerPokemon.getType1());
        }

        return effectiveRatio;
    }

    public int getBattleOptionId() {
        return battleOption.getOptionId();
    }

    public void setBattleOption(int optionId) {
        battleOption = BattleOption.values()[optionId - 1];
    }

    public BattleScreenState getBattleScreenState() {
        return battleScreenState;
    }

    public void setBattleScreenState(BattleScreenState battleScreenState) {
        BattleManager.battleScreenState = battleScreenState;
    }

    public int getMoveSelectId() {
        return moveSelectId;
    }

    public void setMoveSelectId(int moveSelectId) {
        BattleManager.moveSelectId = moveSelectId;
    }

    public Queue<BattleEvent> getBattleEventQueue() {
        return battleEventQueue;
    }

    public BattleEvent getCurrentEvent() {
        return currentEvent;
    }

    public void setCurrentEvent(BattleEvent currentEvent) {
        this.currentEvent = currentEvent;
    }

    public boolean isBattleOver() {
        return battleOver;
    }

    public boolean allPokemonFainted(List<Pokemon> pokemonParty) {
        return pokemonParty.stream().allMatch(Pokemon::isFainted);
    }

    public boolean isFirstTurn() {
        return playerPokemon.getSpeed() > trainerPokemon.getSpeed();
    }

    public Pokemon getPlayerPokemon() {
        return playerPokemon;
    }

    public void setPlayerPokemon(Pokemon playerPokemon) {
        this.playerPokemon = playerPokemon;
    }

    public Pokemon getTrainerPokemon() {
        return trainerPokemon;
    }

    public void setTrainerPokemon(Pokemon trainerPokemon) {
        this.trainerPokemon = trainerPokemon;
    }

    public int getPlayerFaintLine() {
        return 0;
    }

    public int getTrainerFaintLine() {
        return 400;
    }
}
