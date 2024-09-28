package com.mygdx.game;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    protected Vector2 position;
    protected final Vector2 velocity;
    protected TextureRegion texture;
    protected float damage;
    protected boolean isActive = true;
    protected float damageScale;
    protected float lifeTimer = 0.0f; // Tracks the lifetime of a bullet
    protected float angle;
    Assets assets;

    public Bullet(Vector2 position, Vector2 velocity, float damage, Assets assets ) {

        this.assets=assets;
        this.damage=damage;
        this.damageScale = 0.8f + damage / 200.0f;
        this.position = position;
        this.velocity = velocity;
    }

    protected void updatePosition(float deltaTime) {

        // Update position
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);


        //Update LifeTimer
        lifeTimer += deltaTime;

        // Check if lifeTimer exceeds 3.5 seconds
        if (lifeTimer >= 3.5f) {
            isActive = false;
        }
    }

    public void dispose() {
        texture.getTexture().dispose();
    }

    public float getDamage() {
        return damage;
    }

    public boolean isActive() {
        return !isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setDamage(float setDmg){damage=setDmg;}


}
