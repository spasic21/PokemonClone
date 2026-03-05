package framework;

import objects.Player;

public class Camera {

    private float x, y;
    private int screenWidth, screenHeight;
    private int mapPixelWidth, mapPixelHeight;

    public Camera(int screenWidth, int screenHeight){
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.x = 0;
        this.y = 0;
    }

    public void setMapBounds(int mapPixelWidth, int mapPixelHeight) {
        this.mapPixelWidth = mapPixelWidth;
        this.mapPixelHeight = mapPixelHeight;
    }

    public void update(Player player){
        if (mapPixelWidth <= screenWidth) {
            x = (mapPixelWidth - screenWidth) / 2f;
        } else {
            x = player.getX() - (float) screenWidth / 2 + (float) player.getWidth() / 2;
            x = Math.max(0, Math.min(x, mapPixelWidth - screenWidth));
        }

        if (mapPixelHeight <= screenHeight) {
            y = (mapPixelHeight - screenHeight) / 2f;
        } else {
            y = player.getY() - (float) screenHeight / 2 + (float) player.getHeight() / 2;
            y = Math.max(0, Math.min(y, mapPixelHeight - screenHeight));
        }
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }
}
