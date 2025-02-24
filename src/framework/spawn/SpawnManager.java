package framework.spawn;

import framework.enums.Location;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpawnManager {

    private static SpawnManager spawnManager;

    private Map<Location, List<SpawnPoint>> spawnMap = new HashMap<>();

    public SpawnManager() {}

    public static SpawnManager getInstance() {
        if (spawnManager == null) {
            spawnManager = new SpawnManager();
        }

        return spawnManager;
    }

    public void init() {
        spawnMap.put(Location.World, Collections.singletonList(new SpawnPoint(new Rectangle(680, 1440, 760, 40), 161, 450, Location.House_One)));
        spawnMap.put(Location.House_One, Collections.singletonList(new SpawnPoint(new Rectangle(120, 520, 120, 40), 680, 1440, Location.World)));
    }

    public List<SpawnPoint> getSpawnPoints(Location location) {
        return spawnMap.get(location);
    }
}

