package ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class OptionPointer {

    private int width;
    private int height;

    private static final int BOUNCE_PERIOD = 30;
    private static final int BOUNCE_AMPLITUDE = 8;
    private int tick = 0;
    private int arrowOffset = 0;

    private BufferedImage optionPointer;

    public OptionPointer() {

        // Original width and height multiplied by a factor value.
        this.width = 5 * 5;
        this.height = 9 * 5;

        try {
            this.optionPointer = ImageIO.read(getClass().getResource("/hud/option_pointer.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        tick = (tick + 1) % BOUNCE_PERIOD;
        int half = BOUNCE_PERIOD / 2;
        arrowOffset = tick < half
                ? tick * BOUNCE_AMPLITUDE / half
                : (BOUNCE_PERIOD - tick) * BOUNCE_AMPLITUDE / half;
    }

    public void render(Graphics g, int x, int y) {
        g.drawImage(optionPointer, x + arrowOffset, y, width, height, null);
        
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
