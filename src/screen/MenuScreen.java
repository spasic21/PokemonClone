package screen;

import framework.Handler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class MenuScreen extends Screen {

    private int menuX, menuY, menuWidth, menuHeight;

    private int optionPointerWidth, optionPointerHeight;

    private Font font;

    private BufferedImage optionPointer;

    private List<String> menuList = Arrays.asList("Pokedex", "Pokemon", "Bag", "Gugi", "Save", "Options", "Exit");

    public MenuScreen(Handler handler) {
        super(handler);

        menuWidth = 250;
        menuX = handler.getWidth() - menuWidth - 50;
        menuY = 50;

        try {
            InputStream inputStream = getClass().getResourceAsStream("/font/PokemonFont.ttf");
            font = Font.createFont(Font.TRUETYPE_FONT, inputStream);

            optionPointer = ImageIO.read(getClass().getResource("/hud/option_pointer.png"));
            optionPointerWidth = optionPointer.getWidth() * 4;
            optionPointerHeight = optionPointer.getHeight() * 4;
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {}

    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Font menuFont = font.deriveFont(48f);
        int spacing = 55;
        int padding = 75;

        menuHeight = (menuList.size() * spacing) + padding;

        g.setColor(Color.WHITE);
        g.fillRoundRect(menuX, menuY, menuWidth, menuHeight, 20, 20);

        g.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(5));
        g.drawRoundRect(menuX, menuY, menuWidth, menuHeight, 20, 20);

        g.setFont(menuFont);

        int optionId = handler.getGameKeyInput().getMenuOptionId();

        for(int i = 0; i < menuList.size(); i++) {
            int x = menuX + 75;
            int y = menuY + padding + (i * spacing);
            g.drawString(menuList.get(i), x, y);
        }

        g.drawImage(optionPointer, menuX + 35, menuY + 42 + (optionId * spacing), optionPointerWidth, optionPointerHeight, null);
    }
}
