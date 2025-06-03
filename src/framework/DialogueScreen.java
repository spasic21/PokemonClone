package framework;

import screen.Screen;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DialogueScreen extends Screen {

    private int dialogueX, dialogueY, dialogueWidth, dialogueHeight;

    private Font font;

    private String dialogueText = "Rival: Don't mind me standing here. What? You wanna fight?";

    public DialogueScreen(Handler handler) {
        super(handler);

        dialogueWidth = 700;
        dialogueHeight = 200;
        dialogueX = (handler.getWidth() / 2) - (dialogueWidth / 2);
        dialogueY = handler.getHeight() - dialogueHeight - 50;

        try {
            InputStream inputStream = getClass().getResourceAsStream("/font/PokemonFont.ttf");
            font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {

    }

    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Font dialogueFont = font.deriveFont(48f);
        int padding = 60;
        int lineSpacing = 50;

        int textX = dialogueX + padding;
        int textY = dialogueY + padding;

        g.setColor(Color.WHITE);
        g.fillRoundRect(dialogueX, dialogueY, dialogueWidth, dialogueHeight, 20, 20);

        g.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(5));
        g.drawRoundRect(dialogueX, dialogueY, dialogueWidth, dialogueHeight, 20, 20);

        g.setFont(dialogueFont);

        List<String> lines = getWrappedText(g, dialogueText, dialogueWidth - padding);

        int yOffset = 0;

        for (String line : lines) {
            g.drawString(line, textX, textY + yOffset);
            yOffset += lineSpacing;
        }


    }

    private List<String> getWrappedText(Graphics g, String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        FontMetrics metrics = g.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;
            int textWidth = metrics.stringWidth(testLine);

            if (textWidth > maxWidth) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            } else {
                currentLine.append(currentLine.isEmpty() ? word : " " + word);
            }
        }
        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString());
        }

        return lines;
    }
}
