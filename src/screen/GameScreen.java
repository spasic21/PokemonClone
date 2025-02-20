package screen;

import framework.DialogueScreen;
import framework.Handler;
import framework.enums.GameState;
import objects.World;

import java.awt.*;

public class GameScreen extends Screen {

    private World world;
    private MenuScreen menuScreen;
    private DialogueScreen dialogueScreen;

    public GameScreen(Handler handler) {
        super(handler);

        world = new World(handler, "resources/map2.json");
        menuScreen = new MenuScreen(handler);
        dialogueScreen = new DialogueScreen(handler);

        handler.setWorld(world);
    }

    @Override
    public void update() {
        world.update();
    }

    @Override
    public void render(Graphics g) {
        world.render(g);

        if(handler.getGame().getGameState() == GameState.Menu) {
            menuScreen.render(g);
        } else if(handler.getGame().getGameState() == GameState.Dialogue) {
            dialogueScreen.render(g);
        }
    }
}
