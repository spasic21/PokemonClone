import ui.Game;
import ui.Window;

public class GameDriver {
    public static void main(String[] args) {
        new Window(1200, 800, "Pokemon Game", new Game());
    }
}