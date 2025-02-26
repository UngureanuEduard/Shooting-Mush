package com.mygdx.game.utilities_resources;

import com.badlogic.gdx.math.Vector2;

public class EnemyBasicInfo {
    private final Vector2 position;
    private final String type;

    public EnemyBasicInfo(Vector2 position, String type) {
        this.position = position;
        this.type = type;
    }

    public Vector2 getPosition() {
        return position;
    }

    public String getType() {
        return type;
    }
}
