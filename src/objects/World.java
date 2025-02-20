package objects;

import framework.Camera;
import framework.Handler;
import framework.TileMapLoader;
import framework.enums.EntityDirection;
import framework.enums.EntityState;
import framework.enums.ObjectId;

import java.awt.*;

public class World {

    private Handler handler;

    private EntityManager entityManager;

    private Camera camera;

    private TileMapLoader tileMapLoader;
    private Tile[][] tileLayer1;
    private Tile[][] tileLayer2;
    private Tile[][] tileLayer3;
    private Tile[][] collisionLayer;

    public World(Handler handler, String path) {
        this.handler = handler;
        this.entityManager = new EntityManager(handler, new Player(handler, 1240, 1816, 72, 72, ObjectId.Player));
        this.camera = new Camera(handler.getWidth(), handler.getHeight());

        this.entityManager.addEntity(new NPC(handler, 1545, 1103, 72, 72, EntityDirection.RIGHT, EntityState.Standing, ObjectId.NPC));

        try {
            tileMapLoader = new TileMapLoader(path);
            tileLayer1 = tileMapLoader.getTileLayer1();
            tileLayer2 = tileMapLoader.getTileLayer2();
            tileLayer3 = tileMapLoader.getTileLayer3();
            collisionLayer = tileMapLoader.getCollisionLayer();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void update() {
        entityManager.update();
        camera.update(entityManager.getPlayer());
    }

    public void render(Graphics g) {
        Color backgroundColor = new Color(182, 226, 160);

        g.setColor(backgroundColor);
        g.fillRect(0, 0, handler.getWidth(), handler.getHeight());

        renderTileLayer(tileLayer1, g);
        renderTileLayer(tileLayer2, g);

        entityManager.render(g);

        renderTileLayer(tileLayer3, g);

//        renderTileLayer(collisionLayer, g);
    }

    private void renderTileLayer(Tile[][] tileLayer, Graphics g) {
        int tileWidth = tileMapLoader.getTileWidth() * 5;
        int tileHeight = tileMapLoader.getTileHeight() * 5;
        int tileMapWidth = tileMapLoader.getTileMapWidth();
        int tileMapHeight = tileMapLoader.getTileMapHeight();

        float cameraX = camera.getX();
        float cameraY = camera.getY();

        int screenWidth = handler.getWidth();
        int screenHeight = handler.getHeight();

        int startX = Math.max(0, (int) (cameraX / tileWidth));
        int startY = Math.max(0, (int) (cameraY / tileHeight));
        int endX = Math.min(tileMapWidth, (int) ((cameraX + screenWidth) / tileWidth) + 1);
        int endY = Math.min(tileMapHeight, (int) ((cameraY + screenHeight) / tileHeight) + 1);

        for (int i = startX; i < endX; i++) {
            for (int j = startY; j < endY; j++) {
                if (tileLayer[i][j] != null) {
                    int worldX = i * tileWidth;
                    int worldY = j * tileHeight;

                    int renderX = (int) (worldX - cameraX);
                    int renderY = (int) (worldY - cameraY);

                    tileLayer[i][j].render(g, renderX, renderY);
                }
            }
        }
    }

    public Tile[][] getCollisionLayer() {
        return collisionLayer;
    }

    public Tile getCollisionTile(int x, int y) {
        return collisionLayer[x][y];
    }

    public Camera getCamera() {
        return camera;
    }
}
