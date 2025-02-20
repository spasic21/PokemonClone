package framework;

import framework.enums.EntityDirection;
import keyInput.GameKeyInput;
import objects.NPC;
import objects.World;
import screen.Screen;
import ui.Game;


public class Handler {

    private Game game;
    private World world;
    private Screen screen;
    private boolean entityCollision = false;
    private NPC currentNpc;
    private EntityDirection npcDirection;

    public Handler(Game game) {
        this.game = game;
    }

    public GameKeyInput getGameKeyInput() {
        return game.getGameKeyInput();
    }

    public int getWidth() {
        return game.getWidth();
    }

    public int getHeight() {
        return game.getHeight();
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
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
}
