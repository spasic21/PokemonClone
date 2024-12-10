package framework;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import objects.GameObject;
import framework.ObjectId;

/**
 * This is the handler class which holds all of the game objects that are used in the game.
 * Such as characters and map tiles.
 *
 * @author Aleksandar Spasic
 *
 */
public class Handler {

    private List<GameObject> gameObjects = new ArrayList<>();
    private Map<GameObject, Float> spritePositions = new HashMap<>();

    private GameObject tempObject, playerObject;

    public void update(){
        for (GameObject gameObject : gameObjects) {
//            gameObject.update(gameObject);
        }
    }

    public void render(Graphics g){
        for (GameObject gameObject : gameObjects) {
            gameObject.render(g);
        }

        for (GameObject gameObject : gameObjects) {
            if (gameObject.getId() == ObjectId.Player) {
                spritePositions.put(gameObject, gameObject.getY());
            }
        }

        spritePositions = sortMap(spritePositions);

        for(Entry<GameObject, Float> entry : spritePositions.entrySet()){
            entry.getKey().render(g);
        }

        spritePositions.clear();
    }

    public void addObject(GameObject object){
        this.gameObjects.add(object);
    }

    public void removeObject(GameObject object){
        this.gameObjects.remove(object);
    }

    public boolean contains(ObjectId id){
        for(int i = 0; i < gameObjects.size(); i++){
            if(gameObjects.get(i).getId() == id){
                return true;
            }
        }

        return false;
    }

    private static Map<GameObject, Float> sortMap(Map<GameObject, Float> map){
        List<Entry<GameObject, Float>> list = new LinkedList<Entry<GameObject, Float>>(map.entrySet());

        Collections.sort(list, new Comparator<Entry<GameObject, Float>>(){

            @Override
            public int compare(Entry<GameObject, Float> o1,
                               Entry<GameObject, Float> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }

        });

        Map<GameObject, Float> sortedMap = new LinkedHashMap<>();

        for(Entry<GameObject, Float> entry : list){
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }
}
