package ui;

import battle.BattleManager;
import framework.SpriteSheet;
import framework.TypeTable;
import objects.pokemon.Pokemon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TrainerHud extends Hud {

    private BattleManager battleManager;

    private int healthBarWidth;
    private int healthBarHeight;
    private int healthBarX;
    private int healthBarY;
    private int hpSymbolX;
    private int typeSymbolStartX;
    private int typeSymbolFinalX;
    private int typeSymbolDelta;
    private int typeSymbolWidth;
    private int typeSymbolHeight;

    private BufferedImage hpSymbol;
    private BufferedImage typeSymbol1, typeSymbol2;

    private SpriteSheet typeSymbolSpriteSheet;

    private TypeTable typeTable;

    public TrainerHud(BattleManager battleManager, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.battleManager = battleManager;
        this.typeTable = new TypeTable();

        try {
            hpSymbol = ImageIO.read(getClass().getResource("/hud/hp_symbol.png"));
            typeSymbolSpriteSheet = new SpriteSheet(ImageIO.read(getClass().getResource("/hud/type_symbols.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadDimensions();
    }

    private void loadDimensions() {
        healthBarWidth = 200;
        healthBarHeight = 18;

        healthBarX = x + width - healthBarWidth - 25;
        healthBarY = y + 65;
        hpSymbolX = healthBarX - hpSymbol.getWidth() - 10;

        typeSymbolWidth = 19 * 2;
        typeSymbolHeight = 19 * 2;

        typeSymbolStartX = x;
        typeSymbolFinalX = x - typeSymbolWidth;
        typeSymbolDelta = typeSymbolStartX;
    }

    @Override
    public void update() {
        if (battleManager.getBattleScreenState() == BattleManager.BattleScreenState.MoveSelect) {
            if (typeSymbolDelta > typeSymbolFinalX) {
                typeSymbolDelta -= 10;
            }
        } else {
            if (typeSymbolDelta < typeSymbolStartX) {
                typeSymbolDelta += 10;
            }
        }
    }

    @Override
    public void render(Graphics g) {
        Pokemon pokemon = battleManager.getTrainerPokemon();

        if (battleManager.getBattleScreenState() == BattleManager.BattleScreenState.MoveSelect
                || battleManager.getBattleScreenState() == BattleManager.BattleScreenState.BattleOptionSelect) {
            renderTypeSymbol(g, pokemon);
        }

        g.setColor(new Color(254, 255, 221));
        g.fillRect(x, y, width, height);

        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);

        g.drawString(pokemon.getName(), x + 30, y + 50);
        g.drawString("Lv " + pokemon.getLevel(), x + width - 120, y + 50);

        g.drawImage(hpSymbol, hpSymbolX, healthBarY, 43, 18, null);
        g.setColor(new Color(85, 106, 89));
        g.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);

        renderNewHealth(g, pokemon, healthBarX, healthBarY);
    }

    private void renderNewHealth(Graphics g, Pokemon pokemon, int x, int y) {
        double healthRatio = (double) pokemon.getCurrentHealth() / pokemon.getMaxHealth();

        if (healthRatio <= 0.25) {
            g.setColor(new Color(243, 90, 64));
        } else if (healthRatio <= 0.5) {
            g.setColor(new Color(252, 232, 57));
        } else {
            g.setColor(new Color(117, 254, 172));
        }

        int newHealthBarWidth = (int) Math.round(healthRatio * healthBarWidth);

        g.fillRect(x, y, newHealthBarWidth, healthBarHeight);
    }

    private void renderTypeSymbol(Graphics g, Pokemon pokemon) {
        int typeValue1;
        int typeValue2;

        g.setColor(Color.BLACK);
        g.drawRect(typeSymbolDelta, y, typeSymbolWidth, typeSymbolHeight);
        g.drawRect(typeSymbolDelta, y, typeSymbolWidth, typeSymbolHeight);

        if (pokemon.getType2() != null) {
            typeValue1 = typeTable.getTypeValue(pokemon.getType1()) + 1;
            typeValue2 = typeTable.getTypeValue(pokemon.getType2()) + 1;

            typeSymbol1 = typeSymbolSpriteSheet.grabImage(1, typeValue1, 19, 19);
            typeSymbol2 = typeSymbolSpriteSheet.grabImage(1, typeValue2, 19, 19);

            g.drawImage(typeSymbol1, typeSymbolDelta, y, typeSymbolWidth, typeSymbolHeight, null);

            g.drawRect(typeSymbolDelta, y + typeSymbolHeight, typeSymbolWidth, typeSymbolHeight);
            g.drawImage(typeSymbol2, typeSymbolDelta, y + typeSymbolHeight, typeSymbolWidth, typeSymbolHeight, null);
        } else {
            typeValue1 = typeTable.getTypeValue(pokemon.getType1()) + 1;

            typeSymbol1 = typeSymbolSpriteSheet.grabImage(1, typeValue1, 19, 19);
            g.drawImage(typeSymbol1, typeSymbolDelta, y, typeSymbolWidth, typeSymbolHeight, null);
        }
    }
}
