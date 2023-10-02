package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

public class DamageText {
    private final float text;
    private final Vector2 position;
    private final float duration;
    private float elapsedTime;

    public DamageText(float text, Vector2 position, float duration) {
        this.text = text;
        this.position = position;
        this.duration = duration;
        this.elapsedTime = 0;
    }

    public void update(float deltaTime) {
        elapsedTime += deltaTime;
    }

    public boolean isFinished() {
        return elapsedTime >= duration;
    }

    public String getText() {
        return Float.toString(text);
    }

    public Vector2 getPosition() {
        return position;
    }
}
