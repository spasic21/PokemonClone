package screen;

import framework.*;
import objects.Player;
import objects.Tile;
import ui.Game;

import java.awt.*;

public class GameScreen extends Screen {

    private Camera camera;

    private TileMapLoader tileMapLoader;
    private Tile[][] tileLayer1;
    private Tile[][] tileLayer2;
    private Tile[][] tileLayer3;
    private Tile[][] collisionLayer;

    private Player player;

    private CollisionChecker collisionChecker;

    public GameScreen(int width, int height, Player player) {
        super(width, height);

        this.player = player;
        this.camera = new Camera(0, 0);
        this.collisionChecker = new CollisionChecker();

        try {
            tileMapLoader = new TileMapLoader();
            tileLayer1 = tileMapLoader.getTileLayer1();
            tileLayer2 = tileMapLoader.getTileLayer2();
            tileLayer3 = tileMapLoader.getTileLayer3();
            collisionLayer = tileMapLoader.getCollisionLayer();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {

        player.update();
        camera.update(player);

        if(tileCollisionCheck()) {
            Game.setGameState(GameState.Battle);
        }
    }

    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Color backgroundColor = new Color(182, 226, 160);

        g.setColor(backgroundColor);
        g.fillRect(0, 0, getWidth(), getHeight());

        g2d.translate(camera.getX(), camera.getY());	//Beginning of Camera

        renderTileLayer(tileLayer1, g);
        renderTileLayer(tileLayer2, g);

        player.render(g);

        renderTileLayer(tileLayer3, g);

//        handler.render(g);

        renderTileLayer(collisionLayer, g);

        g2d.translate(-camera.getX(), -camera.getY());	//End of Camera
    }

    private void renderTileLayer(Tile[][] tileLayer, Graphics g) {
        int screenX = getWidth() / 2;
        int screenY = getHeight() / 2;
        int tileMapWidth = tileMapLoader.getTileMapWidth();
        int tileMapHeight = tileMapLoader.getTileMapHeight();

        for(int i = 0; i < tileMapWidth; i++){
            for(int j = 0; j < tileMapHeight; j++){
                if(tileLayer[i][j] != null) {
                    int worldX = (int)tileLayer[i][j].getX();
                    int worldY = (int)tileLayer[i][j].getY();
                    int playerX = (int)player.getX();
                    int playerY = (int)player.getY();

                    if(worldX + tileLayer[i][j].getWidth() > playerX - screenX &&
                            worldX - tileLayer[i][j].getWidth() < playerX + screenX &&
                            worldY + tileLayer[i][j].getHeight() > playerY - screenY &&
                            worldY - tileLayer[i][j].getHeight() < playerY + screenY) {

                        tileLayer[i][j].render(g);
                    }
                }
            }
        }
    }

    private boolean tileCollisionCheck() {
        for(Tile[] collisionTiles : collisionLayer){
            for(Tile tile : collisionTiles){
                if(tile != null) {
                    if(tile.getId() == ObjectId.RestrictionTile) {
//                        tile.checkCollision(player);
                    }else if(tile.getId() == ObjectId.GrassTile && player.getBounds().intersects(tile.getBounds()) && player.getPlayerState() == Player.PlayerState.Walking) {
                        int randomNumber = (int)(Math.random() * 99) + 1;

                        if(randomNumber == 5) {
                            player.setVelX(0);
                            player.setVelY(0);
                            player.setPlayerState(Player.PlayerState.Standing);

                            return true;
                        }
                    }else if(tile.getId() == ObjectId.DoorTile && player.getBounds().intersects(tile.getBounds()) && player.getVelY() < 0) {
                        System.out.println("You walked into a door!");
                    }
                }
            }
        }

        return false;
    }
}
