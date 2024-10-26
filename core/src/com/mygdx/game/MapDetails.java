package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

public class MapDetails {
    String mapAsset;
    Vector2 spawnPoint;
    TransitionArea transitionArea;

    public MapDetails(String mapAsset, Vector2 spawnPoint, TransitionArea transitionArea) {
        this.mapAsset = mapAsset;
        this.spawnPoint = spawnPoint;
        this.transitionArea = transitionArea;
    }
}
