package objects;

import framework.Handler;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EntityManager {

    private Handler handler;
    private Player player;
    private List<Entity> entities;
    private Comparator<Entity> renderSorter = (a, b) ->
            Integer.compare((int)(a.getY() + a.getHeight()), (int)(b.getY() + b.getHeight()));

    public EntityManager(Handler handler, Player player) {
        this.handler = handler;
        this.player = player;
        entities = new ArrayList<>();

        addEntity(player);
    }

    public void update() {
        for (Entity entity : entities) {
            entity.update();
        }
    }

    public void render(Graphics g) {
        float cameraX = handler.getWorld().getCamera().getX();
        float cameraY = handler.getWorld().getCamera().getY();

        for(Entity e : entities) {
            int renderX = (int)(e.getX() - cameraX);
            int renderY = (int)(e.getY() - cameraY);

            e.render(g, renderX, renderY);
        }

        entities.sort(renderSorter);
    }

    public void addEntity(Entity e) {
        entities.add(e);
    }

    public void removeEntity(Entity e) {
        entities.remove(e);
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }
}
