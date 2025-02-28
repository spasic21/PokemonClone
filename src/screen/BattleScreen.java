package screen;

import battle.BattleManager;
import framework.Handler;
import framework.SpriteSheet;
import objects.TrainerBackSprite;
import objects.pokemon.Pokemon;
import objects.pokemon.PokemonFrontSprite;
import ui.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class BattleScreen extends Screen {

    private final BattleManager battleManager;

    private Pokemon playerPokemon;
    private Pokemon trainerPokemon;

    private TrainerBackSprite playerSprite;

    private BufferedImage battleBackground;

    private int textBoxHeight;
    private int textLocationX, textLocationY;
    private int playerHudX, playerHudY, trainerHudX, trainerHudY;
    private int playerHudWidth, playerHudHeight, trainerHudWidth, trainerHudHeight;
    private int battleOptionsX, battleOptionsY, battleOptionsWidth, battleOptionsHeight;

    private Font font;

    private final Hud playerHud, trainerHud;

    private final BattleOptions battleOptions;

    private final MoveSelectBox moveSelectBox;

    public BattleScreen(Handler handler, BattleManager battleManager) {
        super(handler);
        this.battleManager = battleManager;
//        this.playerPokemon = battleManager.getPlayerPokemon();
//        this.trainerPokemon = battleManager.getTrainerPokemon();

        try {
            SpriteSheet battleBackgroundSpriteSheet = new SpriteSheet(ImageIO.read(getClass().getResource("/battle_background_daytime_two.png")));
            battleBackground = battleBackgroundSpriteSheet.grabImage(1, 3, 428, 321);

            InputStream inputStream = getClass().getResourceAsStream("/font/PokemonFont.ttf");
            font = Font.createFont(Font.TRUETYPE_FONT, inputStream);

        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }

        loadDimensions();

        playerHud = new PlayerHud(battleManager, playerHudX, playerHudY, playerHudWidth, playerHudHeight);
        trainerHud = new TrainerHud(battleManager, trainerHudX, trainerHudY, trainerHudWidth, trainerHudHeight);
        battleOptions = new BattleOptions(handler, battleManager, battleOptionsX, battleOptionsY, battleOptionsWidth, battleOptionsHeight);
        moveSelectBox = new MoveSelectBox(handler, battleManager);
    }

    private void loadDimensions() {
        textBoxHeight = 220;
        textLocationX = 75;
        textLocationY = handler.getHeight() - 125;

        playerHudWidth = 430;
        playerHudHeight = 150;

        playerHudX = handler.getWidth() - 475;
        playerHudY = handler.getHeight() - 220 - playerHudHeight - 10;

        trainerHudWidth = 430;
        trainerHudHeight = 110;

        trainerHudX = 100;
        trainerHudY = 75;

        battleOptionsWidth = 500;
        battleOptionsHeight = textBoxHeight;

        battleOptionsX = handler.getWidth() - battleOptionsWidth;
        battleOptionsY = handler.getHeight() - battleOptionsHeight;
    }

    @Override
    public void update() {
        this.playerPokemon = battleManager.getPlayerPokemon();
        this.trainerPokemon = battleManager.getTrainerPokemon();
        this.playerSprite = (TrainerBackSprite) battleManager.getTrainerBackSprite();

        playerHud.update();
        trainerHud.update();

        switch(battleManager.getBattleScreenState()) {
            case Introduction, Battle -> {
                if (battleManager.getCurrentEvent() != null) {
                    battleManager.getCurrentEvent().update();
                }
            }
        }
    }

    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        renderBackground(g);
        renderTextBox(g);

        if(battleManager.getBattleScreenState() == BattleManager.BattleScreenState.Introduction) {
            renderBattleIntroduction(g);
            renderEventText(g);
        } else {
            renderPokemon(g);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            renderHud(g);
            renderBattleState(g);
        }
    }

    private void renderBackground(Graphics g) {
        g.drawImage(battleBackground, 0, 0, handler.getWidth(), handler.getHeight(), null);
    }

    private void renderBattleIntroduction(Graphics g) {
        PokemonFrontSprite trainerPokemonFrontSprite = trainerPokemon.getFrontSprite();

        g.drawImage(playerSprite.getSprite(), playerSprite.getStartX(), playerSprite.getStartY(), playerSprite.getSpriteWidth(), playerSprite.getSpriteHeight(), null);
        g.drawImage(trainerPokemonFrontSprite.getSprite(), trainerPokemonFrontSprite.getStartX(), trainerPokemonFrontSprite.getStartY(), trainerPokemonFrontSprite.getSpriteWidth(), trainerPokemonFrontSprite.getSpriteHeight(), null);
    }

    private void renderEventText(Graphics g) {
        if(battleManager.getCurrentEvent() != null) {
            g.setFont(font.deriveFont(60f));
            battleManager.getCurrentEvent().render(g, textLocationX, textLocationY);
        }
    }

    private void renderPokemon(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, playerPokemon.getBackSprite().getAlpha()));
        g.drawImage(playerPokemon.getBackSprite().getSprite(), playerPokemon.getBackSprite().getEndX(), playerPokemon.getBackSprite().getEndY(), playerPokemon.getBackSprite().getSpriteWidth(), playerPokemon.getBackSprite().getSpriteHeight(), null);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, trainerPokemon.getFrontSprite().getAlpha()));
        g.drawImage(trainerPokemon.getFrontSprite().getSprite(), trainerPokemon.getFrontSprite().getEndX(), trainerPokemon.getFrontSprite().getEndY(), trainerPokemon.getFrontSprite().getSpriteWidth(), trainerPokemon.getFrontSprite().getSpriteHeight(), null);
    }

    private void renderTextBox(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g.setColor(new Color(90, 140, 140));
        g.fillRect(0, handler.getHeight() - 220, handler.getWidth(), textBoxHeight);

        g.setColor(new Color(46, 58, 67));
        g2d.setStroke(new BasicStroke(5));
        g.drawRect(0, handler.getHeight() - 220, handler.getWidth(), textBoxHeight);
    }

    private void renderHud(Graphics g) {
        g.setFont(font.deriveFont(48f));
        playerHud.render(g);
        trainerHud.render(g);
    }

    private void renderBattleState(Graphics g) {
        switch(battleManager.getBattleScreenState()) {
            case BattleOptionSelect -> renderBattleOptions(g);
            case MoveSelect -> renderMoveSelectBox(g, battleManager.getPlayerPokemon());
            case Battle -> renderEventText(g);
        }
    }

    private void renderBattleOptions(Graphics g) {
        g.setFont(font.deriveFont(60f));
        battleOptions.render(g);
    }

    private void renderMoveSelectBox(Graphics g, Pokemon pokemon) {
        g.setFont(font.deriveFont(60f));
        moveSelectBox.render(g, pokemon);
    }
}
