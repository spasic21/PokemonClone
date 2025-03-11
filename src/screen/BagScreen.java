package screen;

import framework.Animation;
import framework.Handler;
import framework.SpriteSheet;
import objects.Item;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class BagScreen extends Screen {

    private String title = "";

    private Font font;

    private BufferedImage optionPointer;

    private final BufferedImage[] bagImages = new BufferedImage[3];

    private Animation leftPocketPointerAnimation, rightPocketPointerAnimation;

    public BagScreen(Handler handler) {
        super(handler);

        try {
            InputStream inputStream = getClass().getResourceAsStream("/font/PokemonFont.ttf");
            font = Font.createFont(Font.TRUETYPE_FONT, inputStream);

            SpriteSheet bagSpriteSheet = new SpriteSheet("/sprites/bag_sprite_sheet.png");
            for (int i = 0; i < 3; i++) bagImages[i] = bagSpriteSheet.grabImage(i + 2, 1, 64, 64);

            optionPointer = ImageIO.read(getClass().getResourceAsStream("/hud/option_pointer.png"));

            SpriteSheet menuPointerSpriteSheet = new SpriteSheet("/hud/menu_pointer_sprite_sheet.png");
            BufferedImage[] leftPocketPointerImages = new BufferedImage[2];
            BufferedImage[] rightPocketPointerImages = new BufferedImage[2];

            leftPocketPointerImages[0] = menuPointerSpriteSheet.grabImage(2, 1, 12, 12);
            leftPocketPointerImages[1] = menuPointerSpriteSheet.grabImage(2, 2, 12, 12);
            rightPocketPointerImages[0] = menuPointerSpriteSheet.grabImage(1, 1, 12, 12);
            rightPocketPointerImages[1] = menuPointerSpriteSheet.grabImage(1, 2, 12, 12);

            leftPocketPointerAnimation = new Animation(240, leftPocketPointerImages[0], leftPocketPointerImages[1]);
            rightPocketPointerAnimation = new Animation(240, rightPocketPointerImages[0], rightPocketPointerImages[1]);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        leftPocketPointerAnimation.update();
        rightPocketPointerAnimation.update();
    }

    @Override
    public void render(Graphics g) {
        g.setFont(font);
        g.setColor(new Color(254, 255, 221));

        g.fillRect(0, 0, handler.getWidth(), handler.getHeight());

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(6));

        g.setColor(new Color(248, 144, 72));
        g.fillRoundRect(150, 75, 900, 600, 20, 20);
        g.setColor(Color.GRAY);
        g.drawRoundRect(150, 75, 900, 600, 20, 20);

        g.setColor(new Color(248, 192, 48));
        g.fillRoundRect(153, 78, 895, 100, 20, 20);
        g.fillRoundRect(603, 78, 445, 445, 20, 20);

        g.setColor(Color.WHITE);
        g.setFont(font.deriveFont(48f));

        int titleBoxWidth = 603 - 153;
        centerTitle(g, title, titleBoxWidth, 138);

        g.fillRoundRect(615, 90, 420, 420, 20, 20);

        renderBag(g);
        renderItemList(g);
        renderItemDescriptionBox(g);
    }

    private void centerTitle(Graphics g, String title, int width, int y) {
        FontMetrics fm = g.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        int x = title.equals("Items") ? 335 : (width + titleWidth) / 2;

        g.drawString(title, x, y);
    }

    private void renderItemList(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(font.deriveFont(32f));

        List<Item> pocket = pocketIdToList(handler.getGameKeyInput().getPocketId());

        if (pocket == null || pocket.isEmpty()) return;

        int yOptionPointerStart = 115;
        int yItemStart = 140;
        int spacing = 55;
        int itemId = handler.getGameKeyInput().getItemId();
        int startIndex = handler.getGameKeyInput().getStartIndex();
        int endIndex = Math.min(startIndex + 6, pocket.size() - 1);

        int pointerY = yOptionPointerStart + Math.min(6, itemId - startIndex) * spacing;
        g.drawImage(optionPointer, 650, pointerY, optionPointer.getWidth() * 3, optionPointer.getHeight() * 3, null);

        List<Item> itemList = pocket.subList(startIndex, endIndex + 1);

        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);
            g.drawString(item.getName(), 680, yItemStart + (i * spacing));
            g.drawString("x" + item.getCount(), 930, yItemStart + (i * spacing));
        }

        if (pocket.size() > 7) {
            int barX = 1000;
            int barY = 115;
            int barWidth = 20;
            int barHeight = spacing * 7 - 20;

            g.setColor(Color.GRAY);
            g.fillRoundRect(barX, barY, barWidth, barHeight, 20, 20);

            int markerHeight = barHeight / pocket.size();
            float progress = (float) itemId / (pocket.size() - 1);

            g.setColor(new Color(248, 48, 72));
            int markerY = barY + (int) ((barHeight - markerHeight) * progress);
            g.fillRoundRect(barX, markerY, barWidth, markerHeight, 20, 20);
        }
    }

    private void renderBag(Graphics g) {
        int pocketId = handler.getGameKeyInput().getPocketId();
        title = new String[]{"Items", "Poke Balls", "Key Items"}[pocketId];

        g.setColor(Color.DARK_GRAY);
        g.fillOval(290, 380, 170, 60);
        g.drawImage(bagImages[pocketId], 280, 230, bagImages[pocketId].getWidth() * 3, bagImages[pocketId].getHeight() * 3, null);

        BufferedImage leftPocketPointer = leftPocketPointerAnimation.getCurrentFrame();
        BufferedImage rightPocketPointer = rightPocketPointerAnimation.getCurrentFrame();

        if (pocketId > 0) {
            g.drawImage(leftPocketPointer, 260, 320, leftPocketPointer.getWidth() * 3, leftPocketPointer.getHeight() * 3, null);
        }

        if (pocketId < 2) {
            g.drawImage(rightPocketPointer, 455, 320, rightPocketPointer.getWidth() * 3, rightPocketPointer.getHeight() * 3, null);
        }
    }

    private List<Item> pocketIdToList(int pocketId) {
        return switch (pocketId) {
            case 0 -> handler.getBag().getItemPocket();
            case 1 -> handler.getBag().getPokeballPocket();
            default -> null;
        };
    }

    private void renderItemDescriptionBox(Graphics g) {
        g.setColor(new Color(56, 128, 200));
        g.fillRoundRect(153, 523, 895, 150, 20, 20);

        g.setColor(Color.WHITE);
        g.fillRoundRect(203, 543, 115, 110, 20, 20);
        g.fillRoundRect(353, 543, 650, 110, 20, 20);

        List<Item> pocket = pocketIdToList(handler.getGameKeyInput().getPocketId());
        if (pocket == null || pocket.isEmpty()) return;

        int index = handler.getGameKeyInput().getItemId();
        Item item = pocket.get(index);

        g.drawImage(item.getImage(), 225, 560, item.getImage().getWidth() * 3, item.getImage().getHeight() * 3, null);

        g.setColor(Color.BLACK);
        g.setFont(font.deriveFont(32f));
        renderItemDescription(g, item.getDescription());
    }

    private void renderItemDescription(Graphics g, String description) {
        FontMetrics fontMetrics = g.getFontMetrics();
        int x = 380;
        int y = 580;
        int maxWidth = 610;
        int lineHeight = fontMetrics.getHeight();

        String[] words = description.split(" ");
        StringBuilder stringBuilder = new StringBuilder();

        for (String word : words) {
            String line = stringBuilder.isEmpty() ? word : stringBuilder + " " + word;

            if (fontMetrics.stringWidth(line) < maxWidth) {
                if (!stringBuilder.isEmpty()) stringBuilder.append(" ");
                stringBuilder.append(word);
            } else {
                g.drawString(stringBuilder.toString(), x, y);

                y += lineHeight;
                stringBuilder = new StringBuilder(word);
            }

            if (y + lineHeight - 543 > 110) break;
        }

        if (!stringBuilder.isEmpty()) g.drawString(stringBuilder.toString(), x, y);
    }
}
