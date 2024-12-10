package framework;

import objects.Player;

public class Camera {

    private float x, y;

    public Camera(float x, float y){
        this.x = x;
        this.y = y;
    }

    public void update(Player player){
        x = -player.getX() + player.getPlayerScreenPositionX();
        y = -player.getY() + player.getPlayerScreenPositionY();
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public void setX(float x){
        this.x = x;
    }

    public void setY(float y){
        this.y = y;
    }
}
