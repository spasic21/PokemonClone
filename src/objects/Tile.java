package objects;

import framework.enums.ObjectId;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Tile {

    private int x;
    private int y;
    private int width;
    private int height;
    private ObjectId id;


    //	private int tileWidth = 36, tileHeight = 36;
    private BufferedImage image;

    public Tile(int x, int y, int width, int height, ObjectId id, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.id = id;

        this.image = image;
    }

    public void update() {

    }

    public void render(Graphics g, int renderX, int renderY) {
        g.drawImage(image, renderX, renderY, width, height, null);
    }

//    @Override
//    public void collision(Player player) {
//        if(getBounds().intersects(player.getBounds())) {
//            int collisionReduction = player.getMovementSpeed() + 5;
//
//            if(player.getVelX() > 0) {
//                player.setX(player.getX() - collisionReduction);
//                if(player.getVelY() > 0) player.setY(player.getY() - collisionReduction);
//                else if(player.getVelY() < 0) player.setY(player.getY() + collisionReduction);
//            }else if(player.getVelX() < 0) {
//                player.setX(player.getX() + collisionReduction);
//                if(player.getVelY() > 0) player.setY(player.getY() - collisionReduction);
//                else if(player.getVelY() < 0) player.setY(player.getY() + collisionReduction);
//            }else if(player.getVelY() > 0) {
//                player.setY(player.getY() - collisionReduction);
//                if(player.getVelX() > 0) player.setX(player.getX() - collisionReduction);
//                else if(player.getVelX() < 0) player.setX(player.getX() + collisionReduction);
//            }else {
//                player.setY(player.getY() + collisionReduction);
//                if(player.getVelX() > 0) player.setX(player.getX() - collisionReduction);
//                else if(player.getVelX() < 0) player.setX(player.getX() + collisionReduction);
//            }
//        }
//    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
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

    public ObjectId getId() {
        return id;
    }
}
