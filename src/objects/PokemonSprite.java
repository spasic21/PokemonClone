package objects;

public abstract class PokemonSprite {

    protected int x;
    protected int y;
    protected int column;
    protected int row;
    protected int width;
    protected int height;

    protected float alpha = 1.0f;

    public PokemonSprite(int column, int row, int width, int height) {
        this.column = column;
        this.row = row;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
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
