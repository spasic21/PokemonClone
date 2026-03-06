package objects;

import framework.Camera;
import framework.Handler;
import framework.MapNpcSpawn;
import framework.MapSpawnPoint;
import framework.MapTransitionPoint;
import framework.TileMapLoader;
import framework.enums.EntityDirection;
import framework.enums.Location;
import framework.enums.ObjectId;
import framework.npc.NpcData;
import framework.npc.NpcDatabase;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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

    private int scaledTileWidth, scaledTileHeight, mapCols, mapRows;
    private List<MapTransitionPoint> transitionPoints = new ArrayList<>();
    private List<MapSpawnPoint> spawnPoints = new ArrayList<>();
    private List<MapNpcSpawn> npcSpawns = new ArrayList<>();

    // Used for initial world load with known pixel coords (e.g. game start)
    public World(Handler handler, Location location, int spawnX, int spawnY, EntityDirection entityDirection) {
        this.handler = handler;
        this.location = location;
        this.camera = new Camera(handler.getWidth(), handler.getHeight());

        try {
            loadTileMap();
            this.entityManager = new EntityManager(handler, new Player(handler, spawnX, spawnY, 72, 72, entityDirection, ObjectId.Player));
            camera.update(entityManager.getPlayer());
            spawnNpcs();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // Used for map transitions — resolves spawn position from a named point in the destination map
    public World(Handler handler, Location location, String spawnPointName, EntityDirection entityDirection) {
        this.handler = handler;
        this.location = location;
        this.camera = new Camera(handler.getWidth(), handler.getHeight());

        try {
            loadTileMap();

            int spawnX = 0, spawnY = 0;
            boolean found = false;

            // Check dedicated SpawnPoints first
            for (MapSpawnPoint sp : spawnPoints) {
                if (sp.name().equals(spawnPointName)) {
                    spawnX = sp.tileX() * scaledTileWidth;
                    spawnY = sp.tileY() * scaledTileHeight;
                    found = true;
                    break;
                }
            }

            // Fall back to Transition rectangle position (used when no separate spawn point exists yet)
            if (!found) {
                for (MapTransitionPoint tp : transitionPoints) {
                    if (tp.name().equals(spawnPointName)) {
                        spawnX = tp.triggerBounds().x;
                        spawnY = tp.triggerBounds().y;
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                System.err.println("Spawn point '" + spawnPointName + "' not found in " + location + " — spawning at origin");
            }

            this.entityManager = new EntityManager(handler, new Player(handler, spawnX, spawnY, 72, 72, entityDirection, ObjectId.Player));
            camera.update(entityManager.getPlayer());
            spawnNpcs();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void spawnNpcs() {
        NpcDatabase npcDatabase = handler.getNpcDatabase();
        if (npcDatabase == null) return;

        for (MapNpcSpawn spawn : npcSpawns) {
            NpcData data = npcDatabase.getNpcData(spawn.npcId());
            if (data == null) {
                System.err.println("NPC not found in database: " + spawn.npcId());
                continue;
            }
            int[] freeTile = findFreeSpawnTile(spawn.tileX(), spawn.tileY(), spawn.npcId());
            if (freeTile == null) {
                System.err.println("No free tile found for NPC: " + spawn.npcId() + " near (" + spawn.tileX() + ", " + spawn.tileY() + ") — skipping");
                continue;
            }
            float x = freeTile[0] * scaledTileWidth;
            float y = freeTile[1] * scaledTileHeight;
            if (freeTile[0] != spawn.tileX() || freeTile[1] != spawn.tileY()) {
                System.out.println("NPC '" + spawn.npcId() + "' moved from (" + spawn.tileX() + "," + spawn.tileY() + ") to (" + freeTile[0] + "," + freeTile[1] + ") to avoid collision");
            }
            entityManager.addEntity(new NPC(handler, x, y, 72, 72, data, ObjectId.NPC));
        }
    }

    /**
     * Searches outward from (tileX, tileY) in expanding rings for a tile that has no RestrictionTile.
     * Returns the tile coordinates as [x, y], or null if none found within the search radius.
     */
    private int[] findFreeSpawnTile(int tileX, int tileY, String npcId) {
        final int MAX_RADIUS = 5;
        for (int radius = 0; radius <= MAX_RADIUS; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    if (Math.abs(dx) != radius && Math.abs(dy) != radius) continue; // only check ring edge
                    int tx = tileX + dx;
                    int ty = tileY + dy;
                    if (tx < 0 || ty < 0 || tx >= mapCols || ty >= mapRows) continue;
                    Tile collision = collisionLayer[tx][ty];
                    if (collision == null || collision.getId() != ObjectId.RestrictionTile) {
                        return new int[]{tx, ty};
                    }
                }
            }
        }
        return null;
    }

    private void loadTileMap() throws Exception {
        String path = getFilePath(this.location);
        tileMapLoader = new TileMapLoader(path);
        tileLayer1 = tileMapLoader.getTileLayer1();
        tileLayer2 = tileMapLoader.getTileLayer2();
        tileLayer3 = tileMapLoader.getTileLayer3();
        collisionLayer = tileMapLoader.getCollisionLayer();
        transitionPoints = tileMapLoader.getTransitionPoints();
        spawnPoints = tileMapLoader.getSpawnPoints();
        npcSpawns = tileMapLoader.getNpcSpawns();

        scaledTileWidth = tileMapLoader.getTileWidth() * 5;
        scaledTileHeight = tileMapLoader.getTileHeight() * 5;
        mapCols = tileMapLoader.getTileMapWidth();
        mapRows = tileMapLoader.getTileMapHeight();
        camera.setMapBounds(mapCols * scaledTileWidth, mapRows * scaledTileHeight);
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
            case World -> "first_town.json";
            case PlayerHouse -> "player_house.json";
            case PokeCenter -> "poke_center.json";
        };
    }

    private void renderTileLayer(Tile[][] tileLayer, Graphics g) {
        float cameraX = camera.getX();
        float cameraY = camera.getY();

        int screenWidth = handler.getWidth();
        int screenHeight = handler.getHeight();

        int startX = Math.max(0, (int) (cameraX / scaledTileWidth));
        int startY = Math.max(0, (int) (cameraY / scaledTileHeight));
        int endX = Math.min(mapCols, (int) ((cameraX + screenWidth) / scaledTileWidth) + 1);
        int endY = Math.min(mapRows, (int) ((cameraY + screenHeight) / scaledTileHeight) + 1);

        for (int i = startX; i < endX; i++) {
            for (int j = startY; j < endY; j++) {
                if (tileLayer[i][j] != null) {
                    int renderX = (int) (i * scaledTileWidth - cameraX);
                    int renderY = (int) (j * scaledTileHeight - cameraY);
                    tileLayer[i][j].render(g, renderX, renderY);
                }
            }
        }
    }

    public int getScaledTileWidth() { return scaledTileWidth; }

    public int getScaledTileHeight() { return scaledTileHeight; }

    public int getMapCols() { return mapCols; }

    public int getMapRows() { return mapRows; }

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

    public List<MapTransitionPoint> getTransitionPoints() {
        return transitionPoints;
    }
}
