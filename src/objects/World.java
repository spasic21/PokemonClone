package objects;

import framework.Camera;
import framework.Handler;
import framework.TileMapLoader;
import framework.enums.EntityDirection;
import framework.enums.Location;
import framework.enums.ObjectId;

import java.awt.*;

public class World {

    private Handler handler;

    private Location location;

    private EntityManager entityManager;

    private Camera camera;

    private TileMapLoader tileMapLoader;
    private Tile[][] tileLayer1;
    private Tile[][] tileLayer2;
    private Tile[][] tileLayer3;
    private Tile[][] collisionLayer;

    public World(Handler handler, Location location, int spawnX, int spawnY, EntityDirection entityDirection) {
        this.handler = handler;
        this.location = location;
        this.entityManager = new EntityManager(handler, new Player(handler, spawnX, spawnY, 72, 72, entityDirection, ObjectId.Player));
        this.camera = new Camera(handler.getWidth(), handler.getHeight());

//        this.entityManager.addEntity(new NPC(handler, 1535, 1103, 72, 72, EntityDirection.RIGHT, EntityState.Standing, ObjectId.NPC));

        try {
            String path = getFilePath(this.location);
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
//        Color backgroundColor = new Color(182, 226, 160);
        Color backgroundColor = Color.BLACK;

        g.setColor(backgroundColor);
        g.fillRect(0, 0, handler.getWidth(), handler.getHeight());

        renderTileLayer(tileLayer1, g);
        renderTileLayer(tileLayer2, g);

        entityManager.render(g);

        renderTileLayer(tileLayer3, g);

        // Debugging
//        g.setColor(Color.RED);
//        g.setFont(new Font("TimesRoman", Font.BOLD, 60));
//        g.drawString(entityManager.getPlayer().getX() + " " + entityManager.getPlayer().getY(), 100, 100);

//        renderTileLayer(collisionLayer, g);
    }

    private String getFilePath(Location location) {
        return switch (location) {
            case World -> "map2.json";
            case House_One -> "map3.json";
        };
    }

    private void getSpawnPoints() {
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

    public Location getLocation() {
        return location;
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
