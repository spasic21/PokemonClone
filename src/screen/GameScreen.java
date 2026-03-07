package screen;

import framework.Handler;
import framework.enums.EntityDirection;
import framework.enums.GameState;
import framework.enums.Location;
import objects.World;

import java.awt.*;

public class GameScreen extends Screen {

    private MenuScreen menuScreen;

    public GameScreen(Handler handler) {
        super(handler);

        menuScreen = new MenuScreen(handler);

        handler.setWorld(new World(handler, Location.World, 1240, 1816, EntityDirection.DOWN));
    }

    @Override
    public void update() {
        if (handler.getGame().getGameState() == GameState.Menu) {
            menuScreen.update();
        } else {
            handler.getWorld().update();
        }
    }

    @Override
    public void render(Graphics g) {
        handler.getWorld().render(g);

        if(handler.getGame().getGameState() == GameState.Menu) {
            menuScreen.render(g);
        } else if(handler.getGame().getGameState() == GameState.Dialogue) {
            handler.getDialogueScreen().render(g);
        }
    }
}
