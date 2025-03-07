package screen;

import framework.Handler;
import framework.SpriteSheet;
import framework.enums.Type;
import objects.pokemon.Pokemon;
import objects.pokemon.PokemonMove;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class PokemonSummaryScreen extends Screen {

    private Pokemon pokemon;

    private Font font;
    private BufferedImage expSymbol;
    private SpriteSheet typeSymbolSpriteSheet;

    private final String[] infoPageList = {"Number", "Name", "Type", "OT", "ID", "Item"};
    private final String[] statsPageList = {"HP", "Attack", "Defense", "SP. Attack", "SP. Defense", "Speed"};
    private String[] pokemonInfo, pokemonStats;

    public PokemonSummaryScreen(Handler handler) {
        super(handler);

        try {
            InputStream inputStream = getClass().getResourceAsStream("/font/PokemonFont.ttf");
            font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            expSymbol = ImageIO.read(getClass().getResource("/hud/exp_symbol.png"));
            typeSymbolSpriteSheet = new SpriteSheet(ImageIO.read(getClass().getResource("/hud/pokemon_type_icons.png")));
            font = font.deriveFont(48f);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        this.pokemon = handler.getPokemonParty().get(handler.getGameKeyInput().getPokemonId());
        this.pokemonInfo = new String[]{String.valueOf(pokemon.getDexNumber()), pokemon.getName(), pokemon.getType1().toString(), "Gugi", "123456", "None"};
        this.pokemonStats = new String[]{
                pokemon.getCurrentHealth() + "/" + pokemon.getMaxHealth(),
                String.valueOf(pokemon.getAttack()),
                String.valueOf(pokemon.getDefense()),
                String.valueOf(pokemon.getSpecialAttack()),
                String.valueOf(pokemon.getSpecialDefense()),
                String.valueOf(pokemon.getSpeed())
        };
    }

    @Override
    public void render(Graphics g) {
        g.setFont(font);
        g.setColor(new Color(254, 255, 221));
        g.fillRect(0, 0, handler.getWidth(), handler.getHeight());

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(5));

        switch (handler.getGameKeyInput().getPokemonSummaryPageId()) {
            case 1 -> renderPokemonInfoSection(g);
            case 2 -> renderPokemonStatsSection(g);
            case 3 -> renderPokemonMovesSection(g);
        }

        renderPokemonSpriteSection(g);
    }

    private void renderPokemonInfoSection(Graphics g) {
        g.setColor(new Color(253, 161, 114));
        g.fillRoundRect(150, 75, 900, 600, 20, 20);
        g.setColor(Color.GRAY);
        g.drawRoundRect(150, 75, 900, 600, 20, 20);

        int yStart = 90;
        int textY = 130;

        for (int i = 0; i < infoPageList.length; i++) {
            g.setColor(Color.GRAY);
            g.fillRoundRect(620, yStart + (60 * i), 150, 50, 20, 20);

            g.setColor(Color.WHITE);
            g.fillRoundRect(780, yStart + (60 * i), 260, 50, 20, 20);
            g.drawString(infoPageList[i], 630, textY + (60 * i));

            g.setColor(Color.BLACK);

            if (i == 2) {
                BufferedImage typeSymbol = getTypeSymbol(pokemon.getType1(), typeSymbolSpriteSheet);
                g.drawImage(typeSymbol, 800, yStart + (60 * i) + 1, typeSymbol.getWidth() * 3, typeSymbol.getHeight() * 3, null);

                if (pokemon.getType2() != null) {
                    typeSymbol = getTypeSymbol(pokemon.getType2(), typeSymbolSpriteSheet);
                    g.drawImage(typeSymbol, 920, yStart + (60 * i) + 1, typeSymbol.getWidth() * 3, typeSymbol.getHeight() * 3, null);
                }
            } else {
                g.drawString(pokemonInfo[i], 790, textY + (60 * i));
            }
        }

        g.setColor(Color.GRAY);
        g.fillRoundRect(160, 390, 250, 50, 20, 20);
        g.setColor(Color.WHITE);
        g.drawString("Trainer Memo", 170, 430);
        g.fillRoundRect(160, 450, 880, 210, 20, 20);
    }

    private void renderPokemonStatsSection(Graphics g) {
        g.setColor(new Color(253, 253, 150));
        g.fillRoundRect(150, 75, 900, 600, 20, 20);
        g.setColor(Color.GRAY);
        g.drawRoundRect(150, 75, 900, 600, 20, 20);

        int yStart = 90;
        int textY = 130;

        for (int i = 0; i < statsPageList.length; i++) {
            g.setColor(Color.GRAY);
            g.fillRoundRect(620, yStart + (60 * i), 210, 50, 20, 20);

            g.setColor(Color.WHITE);
            g.fillRoundRect(840, yStart + (60 * i), 200, 50, 20, 20);
            g.drawString(statsPageList[i], 630, textY + (60 * i));

            g.setColor(Color.BLACK);
            g.drawString(pokemonStats[i], 850, textY + (60 * i));
        }

        g.setColor(Color.GRAY);
        g.fillRoundRect(160, 450, 200, 50, 20, 20);
        g.fillRoundRect(500, 450, 220, 50, 20, 20);
        g.fillRoundRect(500, 510, 220, 50, 20, 20);
        g.fillRoundRect(740, 570, 300, 25, 20, 20);

        g.setColor(Color.WHITE);
        g.drawString("Ability", 170, 490);
        g.drawString("EXP. Points", 510, 490);
        g.drawString("Next Level", 510, 550);
        g.fillRoundRect(160, 510, 300, 50, 20, 20);
        g.fillRoundRect(740, 450, 300, 50, 20, 20);
        g.fillRoundRect(740, 510, 300, 50, 20, 20);
        g.fillRoundRect(810, 575, 225, 15, 10, 10);

        g.setColor(Color.BLACK);
        g.drawString("Blaze", 170, 550);
        g.drawString(String.valueOf(pokemon.getCurrentExp()), 750, 490);
        g.drawString(String.valueOf(pokemon.getExpNextLevel()), 750, 550);
        g.drawImage(expSymbol, 750, 575, 49, 14, null);

        renderNewExp(g);
    }

    private void renderPokemonMovesSection(Graphics g) {
        g.setColor(new Color(207, 253, 188));
        g.fillRoundRect(150, 75, 900, 600, 20, 20);
        g.setColor(Color.GRAY);
        g.drawRoundRect(150, 75, 900, 600, 20, 20);

        int yStart = 90;
        int textY = 130;

        for (int i = 0; i < pokemon.getPokemonMovesList().size(); i++) {
            g.setColor(Color.WHITE);
            g.fillRoundRect(620, yStart + (110 * i), 420, 100, 20, 20);

            BufferedImage typeSymbol = getTypeSymbol(pokemon.getPokemonMovesList().get(i).getType(), typeSymbolSpriteSheet);
            g.drawImage(typeSymbol, 640, yStart + (110 * i) + 10, typeSymbol.getWidth() * 3, typeSymbol.getHeight() * 3, null);

            g.setColor(Color.BLACK);
            g.drawString(pokemon.getPokemonMovesList().get(i).getName(), 790, textY + (110 * i) + 5);
            g.drawString(pokemon.getPokemonMovesList().get(i).getCurrentPowerPoints() + "/" + pokemon.getPokemonMovesList().get(i).getMaxPowerPoints(), 920, textY + (110 * i) + 50);
        }

        if (handler.getGameKeyInput().isMoveSelect()) {
            PokemonMove move = pokemon.getPokemonMovesList().get(handler.getGameKeyInput().getMoveId());
            BufferedImage moveCategorySymbol = getCategorySymbol(move, typeSymbolSpriteSheet);

            g.setColor(Color.RED);
            g.drawRoundRect(620, yStart + (110 * handler.getGameKeyInput().getMoveId()), 420, 100, 20, 20);

            g.setColor(Color.GRAY);
            g.fillRoundRect(160, 390, 180, 50, 20, 20);
            g.fillRoundRect(160, 450, 180, 50, 20, 20);
            g.fillRoundRect(160, 510, 180, 50, 20, 20);

            g.setColor(Color.WHITE);
            g.drawString("Power", 170, 430);
            g.drawString("Accuracy", 170, 490);
            g.drawString("Category", 170, 550);
            g.fillRoundRect(360, 390, 180, 50, 20, 20);
            g.fillRoundRect(360, 450, 180, 50, 20, 20);
            g.fillRoundRect(360, 510, 180, 50, 20, 20);
            g.fillRoundRect(160, 570, 880, 95, 20, 20);

            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(move.getDamage()), 370, 430);
            g.drawString(String.valueOf(move.getAccuracy()), 370, 490);
            g.drawImage(moveCategorySymbol, 400, 510, moveCategorySymbol.getWidth() * 3, moveCategorySymbol.getHeight() * 3, null);

            g.setFont(g.getFont().deriveFont(32f));
            g.drawString(move.getDescription(), 180, 610);
        }
    }

    private void renderPokemonSpriteSection(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(5));

        g.setColor(new Color(224, 176, 255));
        g.fillRoundRect(150, 75, 450, 300, 20, 20);
        g.setColor(Color.GRAY);
        g.drawRoundRect(150, 75, 450, 300, 20, 20);

        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(48f));
        g.drawString("Lv " + pokemon.getLevel() + "     " + pokemon.getName(), 160, 120);

        g.fillRoundRect(160, 140, 430, 220, 20, 20);

        AffineTransform tx = new AffineTransform();
        tx.translate(150 + pokemon.getFrontSprite().getSpriteWidth(), 175);
        tx.scale(-3, 3);

        g2d.drawImage(pokemon.getFrontSprite().getSprite(), tx, null);
    }

    private void renderNewExp(Graphics g) {
        double expRatio = (double) pokemon.getCurrentExp() / pokemon.getExpNextLevel();
        int newExpBarWidth = (int) Math.round(expRatio * 225);

        g.setColor(new Color(63, 191, 232));
        g.fillRoundRect(810, 575, newExpBarWidth, 15, 10, 10);
    }

    private BufferedImage getTypeSymbol(Type type, SpriteSheet spriteSheet) {
        return switch (type) {
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

    private void renderMoveDescription(Graphics g, String moveDescription) {
        g.setColor(Color.WHITE);
        g.fillRoundRect(160, 570, 880, 95, 20, 20);

        g.setColor(Color.BLACK);
        g.setFont(g.getFont().deriveFont(32f));
        FontMetrics fontMetrics = g.getFontMetrics();

        int x = 180;
        int y = 610;
        int maxWidth = 860;
        int lineHeight = fontMetrics.getHeight();

        String[] words = moveDescription.split(" ");
        StringBuilder stringBuilder = new StringBuilder();

        for(String word : words) {
            if(fontMetrics.stringWidth(stringBuilder + " " + word) < maxWidth) {
                if(!stringBuilder.isEmpty()) stringBuilder.append(" ");

                stringBuilder.append(word);
            } else {
                g.drawString(stringBuilder.toString(), x, y);

                y += lineHeight;
                stringBuilder = new StringBuilder(word);
            }

            if(y + lineHeight - 570 > 95) {
                break;
            }
        }

        if(stringBuilder.isEmpty()) {
            g.drawString(moveDescription, x, y);
        }
    }
}
