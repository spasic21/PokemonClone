package framework;

import framework.enums.GameState;
import framework.spawn.SpawnManager;
import keyInput.GameKeyInput;
import objects.NPC;
import objects.World;
import objects.pokemon.Pokemon;
import ui.Game;

import java.util.List;


public class Handler {

    private Game game;

    private List<Pokemon> pokemonParty;

    private SpawnManager spawnManager;

    private World world;

    private boolean entityCollision = false;

    private NPC currentNpc;

    private int transitionType;

    private GameState nextGameState;

    public Handler(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public int getWidth() {
        return game.getWidth();
    }

    public int getHeight() {
        return game.getHeight();
    }

    public GameKeyInput getGameKeyInput() {
        return game.getGameKeyInput();
    }

    public List<Pokemon> getPokemonParty() {
        return pokemonParty;
    }

    public void setPokemonParty(List<Pokemon> pokemonParty) {
        this.pokemonParty = pokemonParty;
    }

    public SpawnManager getSpawnManager() {
        return spawnManager;
    }

    public void setSpawnManager(SpawnManager spawnManager) {
        this.spawnManager = spawnManager;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public boolean isEntityCollision() {
        return entityCollision;
    }

    public void setEntityCollision(boolean entityCollision) {
        this.entityCollision = entityCollision;
    }

    public NPC getCurrentNpc() {
        return currentNpc;
    }

    public void setCurrentNpc(NPC currentNpc) {
        this.currentNpc = currentNpc;
    }

    public int getTransitionType() {
        return transitionType;
    }

    public GameState getNextGameState() {
        return nextGameState;
    }

    public void setNextTransition(int transitionType, GameState nextGameState) {
        this.transitionType = transitionType;
        this.nextGameState = nextGameState;
        this.game.setGameState(GameState.Transition);
    }
}
