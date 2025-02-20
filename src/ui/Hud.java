package ui;

import java.awt.*;

public abstract class Hud {

    protected int x;
    protected int y;
    protected int width;
    protected int height;

    public Hud(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void update();

    public abstract void render(Graphics g);
}
