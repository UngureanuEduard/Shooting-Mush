package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

public class DamageText {
    private final float text;
    private final Vector2 position;
    private final float duration;
    private float elapsedTime;
    private final Boolean isCrit;

    public DamageText(float text, Vector2 position, float duration,Boolean isCrit) {
        this.text = text;
        this.position = new Vector2(position);
        this.duration = duration;
        this.elapsedTime = 0;
        this.isCrit=isCrit;
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

    public Boolean getIsCrit(){return isCrit; }
}
