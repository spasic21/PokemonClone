package screen;

import framework.Handler;
import ui.OptionPointer;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class MenuScreen extends Screen {

    private int menuX, menuY, menuWidth, menuHeight;

    private Font menuFont;
    private BasicStroke borderStroke;

    private OptionPointer optionPointer;

    private List<String> menuList = Arrays.asList("Pokedex", "Pokemon", "Bag", "Gugi", "Save", "Options", "Exit");

    public MenuScreen(Handler handler) {
        super(handler);

        this.optionPointer = new OptionPointer();
        this.menuWidth = 250;
        this.menuX = handler.getWidth() - this.menuWidth - 50;
        this.menuY = 50;

        try {
            InputStream inputStream = getClass().getResourceAsStream("/font/PokemonFont.ttf");
            Font font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            menuFont = font.deriveFont(48f);
            borderStroke = new BasicStroke(5);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        optionPointer.update();
    }

    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int spacing = 55;
        int padding = 75;

        menuHeight = (menuList.size() * spacing) + padding;

        g.setColor(Color.WHITE);
        g.fillRoundRect(menuX, menuY, menuWidth, menuHeight, 20, 20);

        g.setColor(Color.BLACK);
        g2d.setStroke(borderStroke);
        g.drawRoundRect(menuX, menuY, menuWidth, menuHeight, 20, 20);

        g.setFont(menuFont);

        int optionId = handler.getGameKeyInput().getMenuOptionId();

        for(int i = 0; i < menuList.size(); i++) {
            int x = menuX + 75;
            int y = menuY + padding + (i * spacing);
            g.drawString(menuList.get(i), x, y);
        }

        int optionPointerX = menuX + 30;
        int optionPointerY = menuY + 40 + (optionId * spacing);

        optionPointer.render(g, optionPointerX, optionPointerY);
    }
}
