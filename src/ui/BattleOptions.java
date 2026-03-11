package ui;

import java.awt.Color;
import java.awt.Graphics;

import battle.BattleManager;
import framework.Handler;

public class BattleOptions {

    private BattleManager battleManager;

    private int x;
    private int y;
    private int width;
    private int height;

    private OptionPointer optionPointer;

    private int textLocationX;
    private int textLocationY;
    private int fightOptionX, fightOptionY;
    private int bagOptionX, bagOptionY;
    private int pokemonOptionX, pokemonOptionY;
    private int runOptionX, runOptionY;

    public BattleOptions(Handler handler, BattleManager battleManager, int x, int y, int width, int height) {
        this.battleManager = battleManager;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.optionPointer = new OptionPointer();

        this.textLocationX = 75;
        this.textLocationY = handler.getHeight() - 125;

        this.fightOptionX = x + 40;
        this.fightOptionY = y + 40;

        this.bagOptionX = x + 305;
        this.bagOptionY = y + 40;

        this.pokemonOptionX = x + 40;
        this.pokemonOptionY = y + 130;

        this.runOptionX = x + 305;
        this.runOptionY = y + 130;
    }

    public void update() {
        optionPointer.update();
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
                optionPointer.render(g, bagOptionX, bagOptionY);
                break;
            case 3:
                optionPointer.render(g, pokemonOptionX, pokemonOptionY);
                break;
            case 4:
                optionPointer.render(g, runOptionX, runOptionY);
                break;
            default:
                optionPointer.render(g, fightOptionX, fightOptionY);
                break;
        }
    }
}
