package objects;

public abstract class PokemonSprite {

    protected int column;
    protected int row;
    protected int width;
    protected int height;

    public PokemonSprite(int column, int row, int width, int height) {
        this.column = column;
        this.row = row;
        this.width = width;
        this.height = height;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
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
}
