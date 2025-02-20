package objects;

import framework.Handler;
import framework.enums.EntityDirection;
import framework.enums.EntityState;
import framework.enums.ObjectId;

import java.awt.*;

public abstract class Entity {

    protected Handler handler;
    protected float x;
    protected float y;
    protected int width;
    protected int height;
    protected ObjectId id;
    protected float velX = 0;
    protected float velY = 0;

    protected EntityDirection entityDirection;
    protected EntityState entityState;

    public Entity(Handler handler, float x, float y, int width, int height, ObjectId id) {
        this.handler = handler;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.id = id;

        this.entityDirection = EntityDirection.DOWN;
        this.entityState = EntityState.Standing;
    }

    public Entity(Handler handler, float x, float y, int width, int height, EntityDirection entityDirection, EntityState entityState, ObjectId id) {
        this.handler = handler;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.entityDirection = entityDirection;
        this.entityState = entityState;
        this.id = id;
    }

    protected void move() {
        if(velX != 0) moveAxis(true, velX);
        if(velY != 0) moveAxis(false, velY);
    }

    private void moveAxis(boolean isX, float velocity) {
        if (velocity == 0) return;

        if (isX) {
            x += velocity;
        } else {
            y += velocity;
        }

        for (Tile[] tiles : handler.getWorld().getCollisionLayer()) {
            for (Tile tile : tiles) {
                if (tile == null || tile.getId() != ObjectId.RestrictionTile) continue;

                while (getBounds(false).intersects(tile.getBounds())) {
                    if (isX) {
                        x -= Math.signum(velocity);
                    } else {
                        y -= Math.signum(velocity);
                    }
                }

                if (isX) {
                    velX = 0;
                } else {
                    velY = 0;
                }
            }
        }

        for (Entity e : handler.getWorld().getEntityManager().getEntities()) {
            if (e.equals(this)) continue;

            if (e.getBounds(true).intersects(getBounds(true))) {
                while (e.getBounds(true).intersects(getBounds(true))) {
                    if (isX) {
                        x -= Math.signum(velocity);
                    } else {
                        y -= Math.signum(velocity);
                    }
                }

                if (isX) {
                    velX = 0;
                } else {
                    velY = 0;
                }

                handler.setEntityCollision(true);

                if(e instanceof NPC npc && this instanceof Player) {
                    handler.setCurrentNpc(npc);
                }
            } else {
                handler.setEntityCollision(false);
            }
        }
    }

    protected abstract void update();

    protected abstract void render(Graphics g, int renderX, int renderY);

    protected abstract Rectangle getBounds(boolean isCollidingEntity);

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public ObjectId getId() {
        return id;
    }

    public float getVelX() {
        return velX;
    }

    public void setVelX(float velX) {
        this.velX = velX;
    }

    public float getVelY() {
        return velY;
    }

    public void setVelY(float velY) {
        this.velY = velY;
    }

    public EntityDirection getEntityDirection() {
        return entityDirection;
    }

    public void setEntityDirection(EntityDirection entityDirection) {
        this.entityDirection = entityDirection;
    }

    public EntityState getEntityState() {
        return entityState;
    }

    public void setEntityState(EntityState entityState) {
        this.entityState = entityState;
    }
}
