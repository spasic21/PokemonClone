package framework;

import framework.enums.GameState;
import framework.spawn.SpawnManager;
import keyInput.GameKeyInput;
import objects.Bag;
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

    private World pendingWorld;

    private boolean entityCollision = false;

    private NPC currentNpc;

    private int transitionType;

    private GameState nextGameState;

    private GameState transitionSourceState;

    private Bag bag;

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

    public void setPendingWorld(World world) {
        this.pendingWorld = world;
    }

    public boolean hasPendingWorld() {
        return pendingWorld != null;
    }

    public void applyPendingWorld() {
        if (pendingWorld != null) {
            this.world = pendingWorld;
            this.pendingWorld = null;
        }
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
        this.transitionSourceState = game.getGameState();
        this.transitionType = transitionType;
        this.nextGameState = nextGameState;
        this.game.setGameState(GameState.Transition);
    }

    public GameState getTransitionSourceState() {
        return transitionSourceState;
    }

    public Bag getBag() {
        return bag;
    }

    public void setBag(Bag bag) {
        this.bag = bag;
    }
}
