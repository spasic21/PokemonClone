package objects;

public abstract class Sprite {

    protected int startX;
    protected int startY;
    protected int endX;
    protected int endY;
    protected int column;
    protected int row;
    protected int width;
    protected int height;

    protected float alpha = 1.0f;

    public Sprite(int column, int row, int width, int height) {
        this.column = column;
        this.row = row;
        this.width = width;
        this.height = height;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getEndX() {
        return endX;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public int getEndY() {
        return endY;
    }

    public void setEndY(int endY) {
        this.endY = endY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) { this.alpha = alpha; }
}
