package com.mygdx.game.utilities_resources;

import com.badlogic.gdx.math.Vector2;

public class MapDetails {
    private final String mapAsset;
    private final Vector2 spawnPoint;
    private final TransitionArea transitionArea;

    public MapDetails(String mapAsset, Vector2 spawnPoint, TransitionArea transitionArea) {
        this.mapAsset = mapAsset;
        this.spawnPoint = spawnPoint;
        this.transitionArea = transitionArea;
    }

    public String getMapAsset() {
        return mapAsset;
    }
    public Vector2 getSpawnPoint() {
        return spawnPoint;
    }
    public TransitionArea getTransitionArea() {
        return transitionArea;
    }
}
