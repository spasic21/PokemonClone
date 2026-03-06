package framework;

import framework.enums.GameState;
import framework.npc.NpcDatabase;
import keyInput.GameKeyInput;
import objects.Bag;
import objects.NPC;
import objects.World;
import objects.pokemon.Pokemon;
import ui.Game;

import java.util.List;


public class Handler {

    private Game game;

    private EventFlagManager eventFlagManager;

    private NpcDatabase npcDatabase;

    private List<Pokemon> pokemonParty;

    private World world;

    private World pendingWorld;

    private boolean entityCollision = false;

    private NPC currentNpc;

    private int transitionType;

    private GameState nextGameState;

    private GameState transitionSourceState;

    private Bag bag;

    private DialogueScreen dialogueScreen;

    public Handler(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public EventFlagManager getEventFlagManager() {
        return eventFlagManager;
    }

    public void setEventFlagManager(EventFlagManager eventFlagManager) {
        this.eventFlagManager = eventFlagManager;
    }

    public NpcDatabase getNpcDatabase() {
        return npcDatabase;
    }

    public void setNpcDatabase(NpcDatabase npcDatabase) {
        this.npcDatabase = npcDatabase;
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

    public World getPendingWorld() {
        return pendingWorld;
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

    public DialogueScreen getDialogueScreen() {
        return dialogueScreen;
    }

    public void setDialogueScreen(DialogueScreen dialogueScreen) {
        this.dialogueScreen = dialogueScreen;
    }
}
