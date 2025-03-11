package screen;

import framework.Animation;
import framework.Handler;
import framework.SpriteSheet;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class LoadingScreen extends Screen {

    private final String[] stringArray = {"L", "O", "A", "D", "I", "N", "G"};

    private final BufferedImage[] unknownL = new BufferedImage[6];
    private final BufferedImage[] unknownO = new BufferedImage[5];
    private final BufferedImage[] unknownA = new BufferedImage[6];
    private final BufferedImage[] unknownD = new BufferedImage[5];
    private final BufferedImage[] unknownI = new BufferedImage[5];
    private final BufferedImage[] unknownN = new BufferedImage[4];
    private final BufferedImage[] unknownG = new BufferedImage[4];

    private final Map<String, Animation> unknownAnimations = new HashMap<>();

    public LoadingScreen(Handler handler) {
        super(handler);

        SpriteSheet unknownSpriteSheet = new SpriteSheet("/sprites/unknown_loading_screen_sprite_sheet.png");

        loadSprites(unknownL, unknownSpriteSheet, 1);
        loadSprites(unknownO, unknownSpriteSheet, 2);
        loadSprites(unknownA, unknownSpriteSheet, 3);
        loadSprites(unknownD, unknownSpriteSheet, 4);
        loadSprites(unknownI, unknownSpriteSheet, 5);
        loadSprites(unknownN, unknownSpriteSheet, 6);
        loadSprites(unknownG, unknownSpriteSheet, 7);

        loadAnimations();
    }

    private void loadSprites(BufferedImage[] sprites, SpriteSheet spriteSheet, int col) {
        for (int i = 0; i < sprites.length; i++) {
            sprites[i] = spriteSheet.grabImage(col, i + 1, 42, 42);
        }
    }

    private void loadAnimations() {
        unknownAnimations.put("L", new Animation(120, unknownL[0], unknownL[1], unknownL[2], unknownL[3], unknownL[4], unknownL[5]));
        unknownAnimations.put("O", new Animation(120, unknownO[0], unknownO[1], unknownO[2], unknownO[3], unknownO[4]));
        unknownAnimations.put("A", new Animation(120, unknownA[0], unknownA[1], unknownA[2], unknownA[3], unknownA[4], unknownA[5]));
        unknownAnimations.put("D", new Animation(120, unknownD[0], unknownD[1], unknownD[2], unknownD[3], unknownD[4]));
        unknownAnimations.put("I", new Animation(120, unknownI[0], unknownI[1], unknownI[2], unknownI[3], unknownI[4]));
        unknownAnimations.put("N", new Animation(120, unknownN[0], unknownN[1], unknownN[2], unknownN[3]));
        unknownAnimations.put("G", new Animation(120, unknownG[0], unknownG[1], unknownG[2], unknownG[3]));
    }

    @Override
    public void update() {
        for (String s : stringArray) {
            unknownAnimations.get(s).update();
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(new Color(254, 255, 221));
        g.fillRect(0, 0, handler.getWidth(), handler.getHeight());

        int startX = 150;
        int count = 0;
        int scale = 3;

        for (String s : stringArray) {
            BufferedImage image = unknownAnimations.get(s).getCurrentFrame();
            g.drawImage(image, startX + (count * image.getWidth() * scale) + 10, 200, image.getWidth() * scale, image.getHeight() * scale, null);
            count++;
        }
    }
}
