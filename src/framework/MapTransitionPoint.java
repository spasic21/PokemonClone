package framework;

import framework.enums.Location;

import java.awt.Rectangle;

public record MapTransitionPoint(
        String name,
        Rectangle triggerBounds,   // scaled to game pixel space (Tiled coords * 5)
        Location targetLocation,
        String targetPoint
) {}
