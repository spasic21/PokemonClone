package battle;

import battle.event.*;
import framework.TypeTable;
import framework.enums.MoveCategory;
import framework.enums.Type;
import framework.pokemon.ExperienceCalculator;
import framework.pokemon.PokemonDatabase;
import framework.pokemon.PokemonGenerator;
import objects.Sprite;
import objects.TrainerBackSprite;
import objects.pokemon.Pokemon;
import objects.pokemon.PokemonMove;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BattleManager {

    private static BattleManager battleManager;

    private List<Pokemon> playerParty;

    private List<Pokemon> trainerParty;

    private Pokemon playerPokemon;

    private Pokemon trainerPokemon;

    private Sprite playerSprite;

    public Queue<BattleEvent> battleEventQueue;

    private BattleEvent currentEvent = null;

    private boolean battleOver;

    private BattleScreenState battleScreenState;

    private int battleOptionId = 1;

    private static int moveSelectId = 1;

    private int trainerPartyIndex = 0;

    private final TypeTable typeTable = new TypeTable();

    public enum BattleScreenState {
        Introduction,
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

    public void init(PokemonDatabase pokemonDatabase, List<Pokemon> playerParty) {
        PokemonGenerator pokemonGenerator = new PokemonGenerator(pokemonDatabase);
        this.playerParty = playerParty;
        this.trainerParty = Collections.singletonList(pokemonGenerator.generatePokemon(false));

        this.playerPokemon = playerParty.get(0);
        this.trainerPokemon = this.trainerParty.get(0);

        this.playerSprite = new TrainerBackSprite(1, 1, 58, 58);

        this.playerPokemon.getBackSprite().setAlpha(1.0f);
        this.trainerPokemon.getFrontSprite().setAlpha(1.0f);
        this.trainerPokemon.getFrontSprite().setStartX(-trainerPokemon.getFrontSprite().getWidth());
        this.trainerPokemon.getFrontSprite().setEndY(100);

        if(battleEventQueue != null) {
            battleEventQueue.clear();
        }

        battleEventQueue = new LinkedList<>();

        battleEventQueue.add(new BattleIntroductionEvent(playerSprite, trainerPokemon));
        battleEventQueue.add(new TrainerSummonPokemonEvent(playerSprite, playerPokemon.getBackSprite()));

        currentEvent = battleEventQueue.poll();

        battleScreenState = BattleScreenState.Introduction;

        battleOver = false;
    }

    private int calculateDamage(PokemonMove pokemonMove, Pokemon attacker, Pokemon defender) {
        int damage;
        double attackDefenseRatio = 1.0;
        int targets = 1;
        double parentalBond = 1.0;
        double weather = 1.0;
        double critical = 1.0;
        double random = 0.85 + (Math.random() * 0.15);
        double stab = 1.0;

        int movePower = pokemonMove.getDamage();

        if (pokemonMove.getMoveCategory() == MoveCategory.Physical) {
            attackDefenseRatio = (double) attacker.getAttack() / (double) defender.getDefense();
        } else if (pokemonMove.getMoveCategory() == MoveCategory.Special) {
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

            if(!trainerPokemon.isFainted()) battleTurn(trainerMove, trainerPokemon, playerPokemon);
        } else {
            battleTurn(trainerMove, trainerPokemon, playerPokemon);

            if(!playerPokemon.isFainted()) battleTurn(playerMove, playerPokemon, trainerPokemon);
        }
    }

    private void battleTurn(PokemonMove move, Pokemon attacker, Pokemon defender) {
        move.setCurrentPowerPoints(move.getCurrentPowerPoints() - 1);
        battleEventQueue.add(new TextEvent(attacker.getName() + " used " + move.getName() + "!"));

        if(move.getMoveCategory() == MoveCategory.Physical || move.getMoveCategory() == MoveCategory.Special) {
            int damage = calculateDamage(move, attacker, defender);

            battleEventQueue.add(new HPAnimationEvent(defender, damage));

            if (defender.getCurrentHealth() - damage < 0) defender.setFainted(true);
        } else {
            battleEventQueue.add(new TextEvent(attacker.getName() + " did nothing!"));
        }

        if(defender.isFainted()) handleFaint(defender);
    }

    private void handleFaint(Pokemon faintedPokemon) {
        if(faintedPokemon == playerPokemon) {
            int playerFaintLine = 580;

            battleEventQueue.add(new PokemonFaintEvent(faintedPokemon.getBackSprite(), playerFaintLine));
            battleEventQueue.add(new TextEvent(faintedPokemon.getName() + " has fainted!"));

            if(allPokemonFainted(playerParty)) {
                battleOver = true;
                battleEventQueue.add(new TextEvent(trainerPokemon.getName() + " wins!"));
            }
        } else {
            int trainerFaintLine = 400;

            battleEventQueue.add(new PokemonFaintEvent(faintedPokemon.getFrontSprite(), trainerFaintLine));
            battleEventQueue.add(new TextEvent(faintedPokemon.getName() + " has fainted!"));

            gainExperience();

            if(allPokemonFainted(trainerParty)) {
                battleOver = true;
                battleEventQueue.add(new TextEvent(playerPokemon.getName() + " wins!"));
            } else {
                trainerPartyIndex++;
                battleEventQueue.add(new TextEvent("Trainer sent out " + trainerParty.get(trainerPartyIndex).getName() + " !"));
//                battleEventQueue.add(new ChangeSpriteEvent(battleManager, trainerParty.get(trainerPartyIndex)));
            }
        }
    }

    private void gainExperience() {
        ExperienceCalculator calculator = new ExperienceCalculator();
        int exp = calculator.calculateExp(trainerPokemon);

        battleEventQueue.add(new TextEvent(playerPokemon.getName() + " gained " + exp + " EXP. Points!"));
        battleEventQueue.add(new ExpAnimationEvent(playerPokemon, exp));
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

        return highestDamageMove != null ? highestDamageMove : new PokemonMove("Struggle", Type.Normal, 50, 100, 100, 100, MoveCategory.Physical);
    }

    private double getEffectiveRatio(PokemonMove move, Pokemon defender) {
        double effectiveRatio;

        if (defender.getType2() != null) {
            double typeRatio1 = typeTable.getMoveEffectiveValue(move.getType(), defender.getType1());
            double typeRatio2 = typeTable.getMoveEffectiveValue(move.getType(), defender.getType2());
            effectiveRatio = typeRatio1 * typeRatio2;
        } else {
            effectiveRatio = typeTable.getMoveEffectiveValue(move.getType(), defender.getType1());
        }

        return effectiveRatio;
    }

    public int getBattleOptionId() {
        return battleOptionId;
    }

    public void setBattleOptionId(int battleOptionId) {
        this.battleOptionId = battleOptionId;
    }

    public BattleScreenState getBattleScreenState() {
        return battleScreenState;
    }

    public void setBattleScreenState(BattleScreenState battleScreenState) {
        this.battleScreenState = battleScreenState;
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

    public Sprite getTrainerBackSprite() {
        return playerSprite;
    }

    public boolean isPlayerPokemonLowHealth() {
        double healthRatio = (double) playerPokemon.getCurrentHealth() / playerPokemon.getMaxHealth();

        return  healthRatio > 0.0 && healthRatio <= 0.25;
    }
}
