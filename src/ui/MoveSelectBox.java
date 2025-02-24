package ui;

import battle.BattleManager;
import framework.Handler;
import framework.SpriteSheet;
import objects.Pokemon;
import objects.PokemonMove;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class MoveSelectBox {

    private Handler handler;

    private final BattleManager battleManager;

    private BufferedImage optionPointer;
    private SpriteSheet typeSymbolSpriteSheet;

    private int moveBoxHeight;

    private int moveOneX, moveOneY;
    private int moveTwoX, moveTwoY;
    private int moveThreeX, moveThreeY;
    private int moveFourX, moveFourY;

    private int optionPointerWidth, optionPointerHeight;

    private int optionOneX, optionOneY;
    private int optionTwoX, optionTwoY;
    private int optionThreeX, optionThreeY;
    private int optionFourX, optionFourY;
    private int infoBoxX, infoBoxY;

    public MoveSelectBox(Handler handler, BattleManager battleManager) {
        this.handler = handler;
        this.battleManager = battleManager;

        try {
            optionPointer = ImageIO.read(getClass().getResource("/hud/option_pointer.png"));
            typeSymbolSpriteSheet = new SpriteSheet(ImageIO.read(getClass().getResource("/hud/pokemon_type_icons.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadDimensions();
    }

    private void loadDimensions() {
        moveBoxHeight = 220;

        moveOneX = 75;
        moveOneY = handler.getHeight() - moveBoxHeight + 75;
        moveTwoX = 475;
        moveTwoY = handler.getHeight() - moveBoxHeight + 75;
        moveThreeX = 75;
        moveThreeY = handler.getHeight() - moveBoxHeight + 175;
        moveFourX = 475;
        moveFourY = handler.getHeight() - moveBoxHeight + 175;

        // Original width and height multiplied by a factor value.
        this.optionPointerWidth = 5 * 5;
        this.optionPointerHeight = 9 * 5;

        optionOneX = moveOneX - 40;
        optionOneY = moveOneY - 40;

        optionTwoX = moveTwoX - 40;
        optionTwoY = moveTwoY - 40;

        optionThreeX = moveThreeX - 40;
        optionThreeY = moveThreeY - 40;

        optionFourX = moveFourX - 40;
        optionFourY = moveFourY - 40;

        infoBoxX = 800;
        infoBoxY = handler.getHeight() - moveBoxHeight;
    }

    public void render(Graphics g, Pokemon pokemon) {
        renderMoveBox(g, pokemon);
    }

    public void renderMoveBox(Graphics g, Pokemon pokemon) {
        List<PokemonMove> pokemonMoves = pokemon.getPokemonMovesList();

        Graphics2D g2d = (Graphics2D) g;
        g.setColor(Color.WHITE);
        g.fillRect(0, handler.getHeight() - moveBoxHeight, 800, moveBoxHeight);

        g.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(8));
        g.drawRect(0, handler.getHeight() - moveBoxHeight, 800, moveBoxHeight);


        g.drawString(pokemonMoves.get(0).getName(), moveOneX, moveOneY);

        if (pokemonMoves.size() == 2) {
            g.drawString(pokemonMoves.get(1).getName(), moveTwoX, moveTwoY);
            g.drawString("-", moveThreeX, moveThreeY);
            g.drawString("-", moveFourX, moveFourY);
        } else if (pokemonMoves.size() == 3) {
            g.drawString(pokemonMoves.get(1).getName(), moveTwoX, moveTwoY);
            g.drawString(pokemonMoves.get(2).getName(), moveThreeX, moveThreeY);
            g.drawString("-", moveFourX, moveFourY);
        } else if (pokemonMoves.size() == 4) {
            g.drawString(pokemonMoves.get(1).getName(), moveTwoX, moveTwoY);
            g.drawString(pokemonMoves.get(2).getName(), moveThreeX, moveThreeY);
            g.drawString(pokemonMoves.get(3).getName(), moveFourX, moveFourY);
        } else {
            g.drawString("-", moveTwoX, moveTwoY);
            g.drawString("-", moveThreeX, moveThreeY);
            g.drawString("-", moveFourX, moveFourY);
        }

        renderOptionPointer(g, battleManager.getMoveSelectId());

        PokemonMove pokemonMove = pokemonMoves.get(battleManager.getMoveSelectId() - 1);
        renderInfoBox(g, pokemonMove);
    }

    private void renderOptionPointer(Graphics g, int moveOptionId) {
        switch (moveOptionId) {
            case 2 -> g.drawImage(optionPointer, optionTwoX, optionTwoY, optionPointerWidth, optionPointerHeight, null);
            case 3 -> g.drawImage(optionPointer, optionThreeX, optionThreeY, optionPointerWidth, optionPointerHeight, null);
            case 4 -> g.drawImage(optionPointer, optionFourX, optionFourY, optionPointerWidth, optionPointerHeight, null);
            default -> g.drawImage(optionPointer, optionOneX, optionOneY, optionPointerWidth, optionPointerHeight, null);
        }
    }

    private void renderInfoBox(Graphics g, PokemonMove pokemonMove) {
        BufferedImage typeSymbol = getTypeSymbol(pokemonMove, typeSymbolSpriteSheet);
        BufferedImage moveCategorySymbol = getCategorySymbol(pokemonMove, typeSymbolSpriteSheet);
        Graphics2D g2d = (Graphics2D) g;
        g.setColor(Color.WHITE);
        g.fillRect(infoBoxX, infoBoxY, 400, moveBoxHeight);

        g.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(8));
        g.drawRect(infoBoxX, infoBoxY, 400, moveBoxHeight);

        g.drawString("PP", infoBoxX + 100, infoBoxY + 90);
        g.drawString(pokemonMove.getCurrentPowerPoints() + "/" + pokemonMove.getMaxPowerPoints(), infoBoxX + 175, infoBoxY + 90);
        g.drawImage(typeSymbol, infoBoxX + 60, infoBoxY + 120, moveCategorySymbol.getWidth() * 4, moveCategorySymbol.getHeight() * 4, null);
        g.drawImage(moveCategorySymbol, infoBoxX + 210, infoBoxY + 120, moveCategorySymbol.getWidth() * 4, moveCategorySymbol.getHeight() * 4, null);
    }

    private BufferedImage getTypeSymbol(PokemonMove move, SpriteSheet spriteSheet) {

        return switch (move.getType()) {
            case Fire -> spriteSheet.grabImage(3, 3, 32, 16);
            case Water -> spriteSheet.grabImage(4, 3, 32, 16);
            case Grass -> spriteSheet.grabImage(1, 4, 32, 16);
            case Electric -> spriteSheet.grabImage(2, 4, 32, 16);
            case Ice -> spriteSheet.grabImage(4, 4, 32, 16);
            case Fighting -> spriteSheet.grabImage(2, 1, 32, 16);
            case Poison -> spriteSheet.grabImage(4, 1, 32, 16);
            case Ground -> spriteSheet.grabImage(1, 2, 32, 16);
            case Flying -> spriteSheet.grabImage(3, 1, 32, 16);
            case Psychic -> spriteSheet.grabImage(3, 4, 32, 16);
            case Bug -> spriteSheet.grabImage(3, 2, 32, 16);
            case Rock -> spriteSheet.grabImage(2, 2, 32, 16);
            case Ghost -> spriteSheet.grabImage(4, 2, 32, 16);
            case Dark -> spriteSheet.grabImage(2, 5, 32, 16);
            case Dragon -> spriteSheet.grabImage(1, 5, 32, 16);
            case Steel -> spriteSheet.grabImage(1, 3, 32, 16);
            case Fairy -> spriteSheet.grabImage(4, 6, 32, 16);
            default -> spriteSheet.grabImage(1, 1, 32, 16);
        };
    }

    private BufferedImage getCategorySymbol(PokemonMove move, SpriteSheet spriteSheet) {

        return switch (move.getMoveCategory()) {
            case Special -> spriteSheet.grabImage(2, 7, 32, 16);
            case Status -> spriteSheet.grabImage(3, 7, 32, 16);
            default -> spriteSheet.grabImage(1, 7, 32, 16);
        };
    }
}
