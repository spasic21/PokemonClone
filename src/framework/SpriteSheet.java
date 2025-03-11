package framework;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

/**
 * This class is used to load in character sprite sheets,
 * which are used for character animations
 *
 * @author Aleksandar Spasic
 */
public class SpriteSheet {

    private BufferedImage image;

    public SpriteSheet(String path) {
        try {
            this.image = ImageIO.read(Objects.requireNonNull(getClass().getResource(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public BufferedImage grabImage(int col, int row, int width, int height) {
        return image.getSubimage((col * width) - width, (row * height) - height, width, height);

    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }
}