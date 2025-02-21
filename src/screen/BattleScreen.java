package screen;

import battle.BattleManager;
import battle.BattleOptions;
import framework.Handler;
import framework.SpriteSheet;
import objects.Pokemon;
import ui.Hud;
import ui.MoveSelectBox;
import ui.PlayerHud;
import ui.TrainerHud;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class BattleScreen extends Screen {

    private BattleManager battleManager;

    private Pokemon playerPokemon;
    private Pokemon trainerPokemon;

    private BufferedImage battleBackground, battleBase, playerPokemonSprite, trainerPokemonSprite;
    private SpriteSheet battleBackgroundSpriteSheet, battleOptionsSpriteSheet, kantoPokemonSpriteSheetBack, kantoPokemonSpriteSheetFront;

    private int playerBaseMiddleX, playerBaseMiddleY;
    private int trainerBaseMiddleX, trainerBaseMiddleY;
    private int playerPokemonStartX, playerPokemonStartY, playerPokemonX, playerPokemonY;
    private int trainerPokemonStartX, trainerPokemonStartY, trainerPokemonX, trainerPokemonY, trainerPokemonFaintLine;
    private int textBoxHeight;
    private int textLocationX, textLocationY;
    private int playerHudX, playerHudY, trainerHudX, trainerHudY;
    private int playerHudWidth, playerHudHeight, trainerHudWidth, trainerHudHeight;
    private int battleOptionsX, battleOptionsY, battleOptionsWidth, battleOptionsHeight;

    private int optionSizeX, optionSizeY;

    private Font font;

    private Hud playerHud, trainerHud;

    private BattleOptions battleOptions;

    private MoveSelectBox moveSelectBox;

    public BattleScreen(Handler handler, BattleManager battleManager) {
        super(handler);
        this.battleManager = battleManager;
        this.playerPokemon = battleManager.getPlayerPokemon();
        this.trainerPokemon = battleManager.getTrainerPokemon();

        try {
            battleBackgroundSpriteSheet = new SpriteSheet(ImageIO.read(getClass().getResource("/battle_background_daytime_two.png")));
            battleBackground = battleBackgroundSpriteSheet.grabImage(1, 3, 428, 321);

            kantoPokemonSpriteSheetBack = new SpriteSheet(ImageIO.read(getClass().getResource("/sprites/kanto_pokemon_back_sprites.png")));
            kantoPokemonSpriteSheetFront = new SpriteSheet(ImageIO.read(getClass().getResource("/sprites/kanto_pokemon_front_sprites.png")));

            battleBase = ImageIO.read(getClass().getResource("/battle_base_grass_daytime.png"));

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

        playerBaseMiddleX = 60 + ((battleBase.getWidth() * 4) / 2);
        playerBaseMiddleY = 520 + ((battleBase.getHeight() * 3) / 2);

//        playerPokemonX = playerBaseMiddleX - ((playerPokemonSprite.getWidth() * 5) / 2);
//        playerPokemonX = playerBaseMiddleX - ((58 * 5) / 2);
//        playerPokemonY = Game.HEIGHT - textBoxHeight - (58 * 5) + 23;
//        playerPokemonY = Game.HEIGHT - textBoxHeight - playerPokemonSprite.getHeight() * 5 + 23;

        trainerBaseMiddleX = 660 + ((battleBase.getWidth() * 4) / 2);
        trainerBaseMiddleY = 300 + ((battleBase.getHeight() * 3) / 2);

//        trainerPokemonX = Game.WIDTH - (trainerPokemonSprite.getWidth() * 5) - 200;
//        trainerPokemonX = Game.WIDTH - (58 * 5) - 200;
//        trainerPokemonY = 150;

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

        playerHud.update();
        trainerHud.update();

        if (battleManager.getBattleScreenState() == BattleManager.BattleScreenState.Battle) {
            if (battleManager.getCurrentEvent() != null) {
                battleManager.getCurrentEvent().update();
            }
        }
    }

    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        renderBackground(g);
//        renderBattleBase(g);
        renderPokemon(g);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        renderTextBox(g);
        renderHud(g);

        if (battleManager.getBattleScreenState() == BattleManager.BattleScreenState.BattleOptionSelect) {
            renderBattleOptions(g);
        } else if (battleManager.getBattleScreenState() == BattleManager.BattleScreenState.MoveSelect) {
            renderMoveSelectBox(g, playerPokemon);
        } else if (battleManager.getCurrentEvent() != null && battleManager.getBattleScreenState() == BattleManager.BattleScreenState.Battle) {
            Font textFont = font.deriveFont(60f);
            g.setFont(textFont);
            battleManager.getCurrentEvent().render(g, textLocationX, textLocationY);
        }
    }

    private void renderBackground(Graphics g) {
        g.drawImage(battleBackground, 0, 0, handler.getWidth(), handler.getHeight(), null);
    }

    private void renderBattleBase(Graphics g) {
//        g.drawImage(battleBase, 60, 520, battleBase.getWidth()*4, battleBase.getHeight()*3, null);
//        g.drawImage(battleBase, 660, 300, battleBase.getWidth()*4, battleBase.getHeight()*3, null);
    }

    private void renderPokemon(Graphics g) {
        BufferedImage playerPokemonSprite = playerPokemon.getBackSprite().getSprite();
        BufferedImage trainerPokemonSprite = trainerPokemon.getFrontSprite().getSprite();

        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, playerPokemon.getBackSprite().getAlpha()));
        g.drawImage(playerPokemonSprite, playerPokemon.getBackSprite().getX(), playerPokemon.getBackSprite().getY(), playerPokemon.getBackSprite().getSpriteWidth(), playerPokemon.getBackSprite().getSpriteHeight(), null);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, trainerPokemon.getFrontSprite().getAlpha()));
        g.drawImage(trainerPokemonSprite, trainerPokemon.getFrontSprite().getX(), trainerPokemon.getFrontSprite().getY(), trainerPokemon.getFrontSprite().getSpriteWidth(), trainerPokemon.getFrontSprite().getSpriteHeight(), null);
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
        Font hudFont = font.deriveFont(48f);
        g.setFont(hudFont);
        playerHud.render(g);
        trainerHud.render(g);
    }

    private void renderBattleOptions(Graphics g) {
        Font battleOptionFont = font.deriveFont(60f);
        g.setFont(battleOptionFont);
        battleOptions.render(g);
    }

    private void renderMoveSelectBox(Graphics g, Pokemon pokemon) {
        Font moveSelectFont = font.deriveFont(60f);
        g.setFont(moveSelectFont);
        moveSelectBox.render(g, pokemon);
    }
}
