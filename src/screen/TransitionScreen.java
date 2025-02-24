package screen;

import framework.Handler;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransitionScreen extends Screen {

    private int alpha = 0;
    private int fadeSpeed = 5;
    private int transitionTime = 90;
    private boolean fadingOut = true;

    private int startAngle = 270;
    private int sweepAngle = 0;
    private final int maxAngle = 400;
    private final int centerX, centerY, radius;

    private final int tileSize = 40;
    private final int columns, rows;
    private final boolean[][] filledTiles;
    private final List<int[]> tileOrder;
    private int tilesFilled = 0;

    private boolean finished = false;

    public TransitionScreen(Handler handler) {
        super(handler);

        this.centerX = handler.getWidth() / 2;
        this.centerY = handler.getHeight() / 2;
        this.radius = (int) Math.sqrt(Math.pow(handler.getWidth(), 2) + Math.pow(handler.getHeight(), 2));

        this.columns = (int) Math.ceil((double) handler.getWidth() / tileSize) + 1;
        this.rows = (int) Math.ceil((double) handler.getHeight() / tileSize) + 1;
        this.filledTiles = new boolean[rows][columns];

        this.tileOrder = new ArrayList<>();

        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < columns; col++) {
                tileOrder.add(new int[]{row, col});
            }
        }

        Collections.shuffle(tileOrder);
    }

    @Override
    public void update() {}

    public void update(int transitionType) {
        switch (transitionType) {
            case 1 -> fadeOutUpdate();
            case 2 -> radarSweepUpdate();
            case 3 -> pixelatedUpdate();
        }
    }

    @Override
    public void render(Graphics g) {}

    public void render(Graphics g, int transitionType) {
        g.setColor(Color.BLACK);

        switch(transitionType) {
            case 1 -> fadeOutRender(g);
            case 2 -> radarSweepRender(g);
            case 3 -> pixelatedRender(g);
        }
    }

    private void fadeOutUpdate() {
        if(fadingOut) {
            alpha += fadeSpeed;

            if(alpha >= 255) {
                fadingOut = false;
                alpha = 255;
            }
        } else {
            alpha -= fadeSpeed;

            if(alpha <= 0) {
                alpha = 0;
            }
        }

        transitionTime--;
    }

    private void radarSweepUpdate() {
        if(sweepAngle <= maxAngle) {
            sweepAngle += 2;
        } else {
            sweepAngle = 0;
            finished = true;
        }
    }

    private void pixelatedUpdate() {
        int tilesPerFrame = 5;

        for (int i = 0; i < tilesPerFrame; i++) {
            if (tilesFilled >= tileOrder.size()) {
                finished = true;
                break;
            }

            int[] tile = tileOrder.get(tilesFilled);
            filledTiles[tile[0]][tile[1]] = true;
            tilesFilled++;
        }
    }

    private void fadeOutRender(Graphics g) {
        g.setColor(new Color(0, 0, 0, Math.min(alpha, 255)));
        g.fillRect(0, 0, handler.getGame().getWidth(), handler.getGame().getHeight());
    }

    private void radarSweepRender(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillArc(centerX - radius, centerY - radius, radius * 2, radius * 2, startAngle, sweepAngle);
    }

    private void pixelatedRender(Graphics g) {
        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < columns; col++) {
                if(filledTiles[row][col]) {
                    g.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);
                }
            }
        }
    }

    public boolean isFinished(int transitionType) {
        switch(transitionType) {
            case 1 -> {
                if(transitionTime <= 0) {
                    transitionTime = 90;
                    alpha = 0;
                    fadingOut = true;

                    return true;
                }
            }

            case 2, 3 -> {
                if(finished) {
                    tilesFilled = 0;
                    finished = false;
                    resetTiles();

                    return true;
                }
            }
        }

        return false;
    }

    private void resetTiles() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                filledTiles[row][col] = false;
            }
        }
    }
}
