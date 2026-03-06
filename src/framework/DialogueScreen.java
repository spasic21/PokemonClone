package framework;

import objects.NPC;
import screen.Screen;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DialogueScreen extends Screen {

    private int dialogueX, dialogueY, dialogueWidth, dialogueHeight;

    private Font dialogueFont;
    private BasicStroke borderStroke;

    private List<String> currentLines = List.of("...");
    private int currentPage = 0;

    public DialogueScreen(Handler handler) {
        super(handler);

        dialogueWidth = 700;
        dialogueHeight = 200;
        dialogueX = (handler.getWidth() / 2) - (dialogueWidth / 2);
        dialogueY = handler.getHeight() - dialogueHeight - 50;

        try {
            InputStream inputStream = getClass().getResourceAsStream("/font/PokemonFont.ttf");
            Font base = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            dialogueFont = base.deriveFont(48f);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        borderStroke = new BasicStroke(5);
    }

    public void startDialogue(NPC npc) {
        currentLines = npc.resolveDialogue(handler.getEventFlagManager());
        currentPage = 0;
    }

    public void advancePage() {
        if (!isLastPage()) currentPage++;
    }

    public boolean isLastPage() {
        return currentPage >= currentLines.size() - 1;
    }

    @Override
    public void update() {
    }

    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int padding = 60;
        int lineSpacing = 50;

        int textX = dialogueX + padding;
        int textY = dialogueY + padding;

        g.setColor(Color.WHITE);
        g.fillRoundRect(dialogueX, dialogueY, dialogueWidth, dialogueHeight, 20, 20);

        g.setColor(Color.BLACK);
        g2d.setStroke(borderStroke);
        g.drawRoundRect(dialogueX, dialogueY, dialogueWidth, dialogueHeight, 20, 20);

        g.setFont(dialogueFont);

        String pageText = currentLines.get(currentPage);
        List<String> wrappedLines = getWrappedText(g, pageText, dialogueWidth - padding * 2);

        int yOffset = 0;
        for (String line : wrappedLines) {
            g.drawString(line, textX, textY + yOffset);
            yOffset += lineSpacing;
        }

        // More pages indicator
        if (!isLastPage()) {
            FontMetrics fm = g.getFontMetrics();
            String arrow = "\u25BA";
            int arrowX = dialogueX + dialogueWidth - padding;
            int arrowY = dialogueY + dialogueHeight - padding / 2;
            g.drawString(arrow, arrowX, arrowY);
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
