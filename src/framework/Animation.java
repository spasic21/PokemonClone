package framework;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Animation {

    private int speed;
    private int frames;
    private int index = 0;
    private int count = 0;

    private BufferedImage[] images;
    private BufferedImage currentImage;

    public Animation(int speed, BufferedImage... images) {
        this.speed = speed;
        this.images = new BufferedImage[images.length];

        System.arraycopy(images, 0, this.images, 0, images.length);

        frames = this.images.length;
    }

    public void runAnimation(){
        index++;

        if(index >= speed){
            index = 0;
            nextFrame();
        }
    }

    private void nextFrame(){
        if(count >= frames){
            count = 0;
        }

        currentImage = images[count];

        count++;
    }

    public void drawAnimation(Graphics g, int x, int y, int scaleX, int scaleY){
        g.drawImage(currentImage, x, y, scaleX, scaleY, null);
    }
}
