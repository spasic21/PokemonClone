package framework.spawn;

import framework.enums.Location;

import java.awt.*;

public record SpawnPoint(Rectangle rectangle, float spawnX, float spawnY, Location targetLocation) {}
