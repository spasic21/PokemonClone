package screen;

import java.awt.*;

public abstract class Screen {

    protected int width;
    protected int height;

    public Screen(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public abstract void update();

    public abstract void render(Graphics g);

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
