package ui;

import battle.BattleManager;
import framework.Handler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class BattleOptions {

    private Handler handler;

    private BattleManager battleManager;

    private int x;
    private int y;
    private int width;
    private int height;

    private BufferedImage optionPointer;

    private int textLocationX;
    private int textLocationY;
    private int optionPointerWidth;
    private int optionPointerHeight;
    private int fightOptionX, fightOptionY;
    private int bagOptionX, bagOptionY;
    private int pokemonOptionX, pokemonOptionY;
    private int runOptionX, runOptionY;

    public BattleOptions(Handler handler, BattleManager battleManager, int x, int y, int width, int height) {
        this.handler = handler;
        this.battleManager = battleManager;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        // Original width and height multiplied by a factor value.
        this.optionPointerWidth = 5 * 5;
        this.optionPointerHeight = 9 * 5;

        this.fightOptionX = x + 40;
        this.fightOptionY = y + 40;

        this.bagOptionX = x + 305;
        this.bagOptionY = y + 40;

        this.pokemonOptionX = x + 40;
        this.pokemonOptionY = y + 130;

        this.runOptionX = x + 305;
        this.runOptionY = y + 130;

        try {
            optionPointer = ImageIO.read(getClass().getResource("/hud/option_pointer.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadDimensions();
    }

    private void loadDimensions() {
        textLocationX = 75;
        textLocationY = handler.getHeight() - 125;

        // Original width and height multiplied by a factor value.
        optionPointerWidth = 5 * 5;
        optionPointerHeight = 9 * 5;

        fightOptionX = x + 40;
        fightOptionY = y + 40;

        bagOptionX = x + 305;
        bagOptionY = y + 40;

        pokemonOptionX = x + 40;
        pokemonOptionY = y + 130;

        runOptionX = x + 305;
        runOptionY = y + 130;
    }

    public void render(Graphics g) {
        g.setColor(new Color(201, 211, 211));
        g.drawString("What will " + battleManager.getPlayerPokemon().getName() + " do?", textLocationX, textLocationY);

        g.setColor(Color.WHITE);
        g.fillRect(x, y, width, height);

        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);

        g.drawString("FIGHT", x + 75, y + 80);
        g.drawString("BAG", x + 340, y + 80);
        g.drawString("POKEMON", x + 75, y + 170);
        g.drawString("RUN", x + 340, y + 170);

        renderOptionPointer(g, battleManager.getBattleOptionId());
    }

    private void renderOptionPointer(Graphics g, int battleOptionId) {
        switch (battleOptionId) {
            case 2:
                g.drawImage(optionPointer, bagOptionX, bagOptionY, optionPointerWidth, optionPointerHeight, null);
                break;
            case 3:
                g.drawImage(optionPointer, pokemonOptionX, pokemonOptionY, optionPointerWidth, optionPointerHeight, null);
                break;
            case 4:
                g.drawImage(optionPointer, runOptionX, runOptionY, optionPointerWidth, optionPointerHeight, null);
                break;
            default:
                g.drawImage(optionPointer, fightOptionX, fightOptionY, optionPointerWidth, optionPointerHeight, null);
                break;
        }
    }
}
