package framework;

import objects.Tile;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import screen.Screen;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileReader;
import java.io.IOException;

/**
 * This is the TileMapLoader class which is used to create the map.
 * It creates the map by using a JSON parser to read a JSON file that is made from a program called "Tiled"
 * This program is used to create the layered map. The map is then exported as a JSON file and this class reads the file.
 * Thus, creating the needed map.
 *
 * @author Aleksandar Spasic
 */
public class TileMapLoader {

    private static int tileMapWidth;
    private static int tileMapHeight;
    private int tileWidth;
    private int tileHeight;
    private Tile[][] tileLayer1;
    private Tile[][] tileLayer2;
    private Tile[][] tileLayer3;
    private Tile[][] tileLayer4;
    private Tile[][] collisionLayer;
    private SpriteSheet spriteSheet, collisionSheet;

    public TileMapLoader() throws IOException, ParseException {
        loadLevel();
    }

    private void loadLevel() throws IOException, ParseException {
        JSONArray layers, layerData;
        JSONObject index;

        BufferedImage tilesetImage = ImageIO.read(getClass().getResource("/pokemon_crystal_tileset.png"));
        BufferedImage collisionTileset = ImageIO.read(getClass().getResource("/CollisionLayer.png"));
        spriteSheet = new SpriteSheet(tilesetImage);
        collisionSheet = new SpriteSheet(collisionTileset);

        Object obj = new JSONParser().parse(new FileReader("resources/map1.json"));
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
        index = (JSONObject) layers.get(0);
        layerData = (JSONArray) index.get("data");
        loadTiles(tileMapWidth, tileMapHeight, layerData, tileLayer1);

        index = (JSONObject) layers.get(1);
        layerData = (JSONArray) index.get("data");
        loadTiles(tileMapWidth, tileMapHeight, layerData, tileLayer2);

        index = (JSONObject) layers.get(2);
        layerData = (JSONArray) index.get("data");
        loadTiles(tileMapWidth, tileMapHeight, layerData, tileLayer3);

//        index = (JSONObject) layers.get(3);
//        layerData = (JSONArray) index.get("data");
//        loadTiles(width, height, layerData, tileLayer4);

        index = (JSONObject) layers.get(3);
        layerData = (JSONArray) index.get("data");
        loadCollisionTiles(tileMapWidth, tileMapHeight, layerData, collisionLayer);
    }

    private void loadTiles(int mapWidth, int mapHeight, JSONArray layerData, Tile[][] tileLayer) {
        int count = 0;
        int scaleWidth = tileWidth * 5;
        int scaleHeight = tileHeight * 5;

        int tileLocationX;
        int tileLocationY;

        for (int i = 0; i < mapHeight; i++) {
            for (int j = 0; j < mapWidth; j++) {
                int tileId = Integer.parseInt(String.valueOf(layerData.get(count)));
                int ratioX = Math.round((float) spriteSheet.getWidth() / tileWidth);

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
//				System.out.println("X-Coordinate: " + i + " Y-Coordinate: " + j + " Tile Id: " + tileId + " TileLocationX: " + tileLocationX + " TileLocationY: " + tileLocationY);
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

                if (tileId == 1821) {
                    tileLayer[j][i] = new Tile(j * scaleWidth, i * scaleHeight, scaleWidth, scaleHeight, ObjectId.RestrictionTile, collisionSheet.grabImage(1, 1, tileWidth, tileHeight));
                } else if (tileId == 1822) {
                    tileLayer[j][i] = new Tile(j * scaleWidth, i * scaleHeight, scaleWidth, scaleHeight, ObjectId.GrassTile, collisionSheet.grabImage(2, 1, tileWidth, tileHeight));
                } else if (tileId == 1823) {
                    tileLayer[j][i] = new Tile(j * scaleWidth, i * scaleHeight, scaleWidth, scaleHeight, ObjectId.DoorTile, collisionSheet.grabImage(3, 1, tileWidth, tileHeight));
                } else {
                    tileLayer[j][i] = null;
                }

                count++;
//				System.out.println("X-Coordinate: " + i + " Y-Coordinate: " + j + " Tile Id: " + tileId + " TileLocationX: " + tileLocationX + " TileLocationY: " + tileLocationY);
            }
        }
    }

    public Tile[][] getTileLayer1() {
        return tileLayer1;
    }

    public void setTileLayer1(Tile[][] tileLayer1) {
        this.tileLayer1 = tileLayer1;
    }

    public Tile[][] getTileLayer2() {
        return tileLayer2;
    }

    public void setTileLayer2(Tile[][] tileLayer2) {
        this.tileLayer2 = tileLayer2;
    }

    public Tile[][] getTileLayer3() {
        return tileLayer3;
    }

    public void setTileLayer3(Tile[][] tileLayer3) {
        this.tileLayer3 = tileLayer3;
    }

    public Tile[][] getTileLayer4() {
        return tileLayer4;
    }

    public void setTileLayer4(Tile[][] tileLayer4) {
        this.tileLayer4 = tileLayer4;
    }

    public Tile[][] getCollisionLayer() {
        return collisionLayer;
    }

    public void setCollisionLayer(Tile[][] collisionLayer) {
        this.collisionLayer = collisionLayer;
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
}