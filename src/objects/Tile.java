package objects;

import framework.ObjectId;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Tile extends GameObject {

    //	private int tileWidth = 36, tileHeight = 36;
    private BufferedImage image;

    public Tile(float x, float y, int width, int height, ObjectId id, BufferedImage image) {
        super(x, y, width, height, id);
        this.image = image;
    }

    @Override
    public void update() {

    }

    public void render(Graphics g) {
        g.drawImage(image, (int)x, (int)y, width, height, null);
    }

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }

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
}
