package framework;

import framework.enums.Location;
import framework.enums.ObjectId;
import objects.Tile;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.Rectangle;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the TileMapLoader class which is used to create the map.
 * It creates the map by using a JSON parser to read a JSON file that is made from a program called "Tiled"
 * This program is used to create the layered map. The map is then exported as a JSON file and this class reads the file.
 * Thus, creating the needed map.
 *
 * @author Aleksandar Spasic
 */
public class TileMapLoader {

    private String path;

    private int tileMapWidth;
    private int tileMapHeight;
    private int tileWidth;
    private int tileHeight;
    private Tile[][] tileLayer1;
    private Tile[][] tileLayer2;
    private Tile[][] tileLayer3;
    private Tile[][] collisionLayer;
    private SpriteSheet spriteSheet, collisionSheet;
    private List<MapTransitionPoint> transitionPoints = new ArrayList<>();
    private List<MapSpawnPoint> spawnPoints = new ArrayList<>();

    public TileMapLoader(String path) throws IOException, ParseException {
        this.path = path;

        loadLevel();
    }

    private void loadLevel() throws IOException, ParseException {
        JSONArray layers, layerData;
        JSONObject index = null;

        spriteSheet = new SpriteSheet("/pokemon_crystal_tileset.png");
        collisionSheet = new SpriteSheet("/CollisionLayer.png");

        InputStream is = getClass().getClassLoader().getResourceAsStream(path);

        if (is == null) {
            throw new FileNotFoundException("Could not find resource: " + path);
        }

        Object obj = new JSONParser().parse(new InputStreamReader(is));
        JSONObject jo = (JSONObject) obj;

        tileMapWidth = Integer.parseInt(String.valueOf(jo.get("width")));
        tileMapHeight = Integer.parseInt(String.valueOf(jo.get("height")));
        tileWidth = Integer.parseInt(String.valueOf(jo.get("tilewidth")));
        tileHeight = Integer.parseInt(String.valueOf(jo.get("tileheight")));

        tileLayer1 = new Tile[tileMapWidth][tileMapHeight];
        tileLayer2 = new Tile[tileMapWidth][tileMapHeight];
        tileLayer3 = new Tile[tileMapWidth][tileMapHeight];
        collisionLayer = new Tile[tileMapWidth][tileMapHeight];


        layers = (JSONArray) jo.get("layers");

        // Load tile layers in encounter order, skipping non-tile layers (objectgroups etc.)
        int tileLayerIndex = 0;
        for (Object layerObj : layers) {
            index = (JSONObject) layerObj;
            if (!"tilelayer".equals(index.get("type"))) continue;

            layerData = (JSONArray) index.get("data");
            switch (tileLayerIndex) {
                case 0 -> loadTiles(tileMapWidth, tileMapHeight, layerData, tileLayer1);
                case 1 -> loadTiles(tileMapWidth, tileMapHeight, layerData, tileLayer2);
                case 2 -> loadTiles(tileMapWidth, tileMapHeight, layerData, tileLayer3);
                case 3 -> loadCollisionTiles(tileMapWidth, tileMapHeight, layerData, collisionLayer);
            }
            tileLayerIndex++;
        }

        loadTransitionPoints(layers);
    }

    private void loadTransitionPoints(JSONArray layers) {
        final int SCALE = 5;

        for (Object layerObj : layers) {
            JSONObject layer = (JSONObject) layerObj;
            if (!"objectgroup".equals(layer.get("type"))) continue;

            JSONArray objects = (JSONArray) layer.get("objects");
            if (objects == null) continue;

            for (Object objItem : objects) {
                JSONObject obj = (JSONObject) objItem;
                String name = (String) obj.get("name");
                double objX = ((Number) obj.get("x")).doubleValue();
                double objY = ((Number) obj.get("y")).doubleValue();
                boolean isPoint = Boolean.TRUE.equals(obj.get("point"));

                JSONArray props = (JSONArray) obj.get("properties");
                if (props == null) continue;

                String type = null, targetLocation = null, targetPoint = null;
                for (Object propItem : props) {
                    JSONObject prop = (JSONObject) propItem;
                    String propName = (String) prop.get("name");
                    String propValue = (String) prop.get("value");
                    switch (propName) {
                        case "type"           -> type = propValue;
                        case "targetLocation" -> targetLocation = propValue;
                        case "targetPoint"    -> targetPoint = propValue;
                    }
                }

                if ("Transition".equals(type) && targetLocation != null && targetPoint != null && !isPoint) {
                    // Rectangle trigger — scale Tiled pixel coords up to game pixel space
                    double objW = ((Number) obj.get("width")).doubleValue();
                    double objH = ((Number) obj.get("height")).doubleValue();
                    Rectangle triggerBounds = new Rectangle(
                            (int) Math.round(objX * SCALE),
                            (int) Math.round(objY * SCALE),
                            (int) Math.round(objW * SCALE),
                            (int) Math.round(objH * SCALE)
                    );
                    try {
                        Location loc = Location.valueOf(targetLocation);
                        transitionPoints.add(new MapTransitionPoint(name, triggerBounds, loc, targetPoint));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Unknown targetLocation in map object '" + name + "': " + targetLocation);
                    }
                } else if ("SpawnPoint".equals(type) && isPoint) {
                    // Point spawn — convert to tile coords for spawn position
                    int tileX = (int) Math.round(objX / tileWidth);
                    int tileY = (int) Math.round(objY / tileHeight);
                    spawnPoints.add(new MapSpawnPoint(name, tileX, tileY));
                }
            }
        }
    }

    private void loadTiles(int mapWidth, int mapHeight, JSONArray layerData, Tile[][] tileLayer) {
        int count = 0;
        int scaleWidth = tileWidth * 5;
        int scaleHeight = tileHeight * 5;

        int tileLocationX;
        int tileLocationY;

        int ratioX = Math.round((float) spriteSheet.getWidth() / tileWidth);
        for (int i = 0; i < mapHeight; i++) {
            for (int j = 0; j < mapWidth; j++) {
                int tileId = Integer.parseInt(String.valueOf(layerData.get(count)));

                if (tileId != 0) {
                    tileLocationX = tileId % ratioX;

                    if (tileLocationX == 0) {
                        tileLocationX = ratioX;
                        tileLocationY = tileId / ratioX;
                    } else {

                        tileLocationY = (tileId + (ratioX - tileLocationX)) / ratioX;
                    }

                    tileLayer[j][i] = new Tile(j * scaleWidth, i * scaleHeight, scaleWidth, scaleHeight, ObjectId.Tile, spriteSheet.grabImage(tileLocationX, tileLocationY, tileWidth, tileHeight));
                } else {
                    tileLayer[j][i] = null;
                }

                count++;
            }
        }
    }

    private void loadCollisionTiles(int width, int height, JSONArray layerData, Tile[][] tileLayer) {
        int count = 0;
        int scaleWidth = tileWidth * 5;
        int scaleHeight = tileHeight * 5;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int tileId = Integer.parseInt(String.valueOf(layerData.get(count)));

                if (tileId == 3265) {
                    tileLayer[j][i] = new Tile(j * scaleWidth, i * scaleHeight, scaleWidth, scaleHeight, ObjectId.RestrictionTile, collisionSheet.grabImage(1, 1, tileWidth, tileHeight));
                } else if (tileId == 3266) {
                    tileLayer[j][i] = new Tile(j * scaleWidth, i * scaleHeight, scaleWidth, scaleHeight, ObjectId.GrassTile, collisionSheet.grabImage(2, 1, tileWidth, tileHeight));
                } else if (tileId == 3267) {
                    tileLayer[j][i] = new Tile(j * scaleWidth, i * scaleHeight, scaleWidth, scaleHeight, ObjectId.DoorTile, collisionSheet.grabImage(3, 1, tileWidth, tileHeight));
                } else {
                    tileLayer[j][i] = null;
                }

                count++;
            }
        }
    }

    public Tile[][] getTileLayer1() {
        return tileLayer1;
    }

    public Tile[][] getTileLayer2() {
        return tileLayer2;
    }

    public Tile[][] getTileLayer3() {
        return tileLayer3;
    }

    public Tile[][] getCollisionLayer() {
        return collisionLayer;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getTileMapWidth() {
        return tileMapWidth;
    }

    public int getTileMapHeight() {
        return tileMapHeight;
    }

    public List<MapTransitionPoint> getTransitionPoints() {
        return transitionPoints;
    }

    public List<MapSpawnPoint> getSpawnPoints() {
        return spawnPoints;
    }
}
