package framework;

import objects.Player;

public class Camera {

    private float x, y;
    private int worldWidth, worldHeight;

    public Camera(int worldWidth, int worldHeight){
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.x = 0;
        this.y = 0;
    }

    public void update(Player player){
        x = player.getX() - (float) worldWidth / 2 + (float) player.getWidth() / 2;
        y = player.getY() - (float) worldHeight / 2 + (float) player.getHeight() / 2;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }
}
