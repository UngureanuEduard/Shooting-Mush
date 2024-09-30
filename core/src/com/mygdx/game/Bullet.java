package com.mygdx.game;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    private static final float MAX_LIFETIME = 3.5f;
    private static final float DAMAGE_SCALE_BASE = 0.8f;
    private static final float DAMAGE_SCALE_FACTOR = 200.0f;

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
        this.damageScale = DAMAGE_SCALE_BASE + damage / DAMAGE_SCALE_FACTOR;
        this.position = position;
        this.velocity = velocity;
    }

    protected void updatePosition(float deltaTime) {

        // Update position
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);


        //Update LifeTimer
        lifeTimer += deltaTime;

        // Check if lifeTimer exceeds 3.5 seconds
        if (lifeTimer >= MAX_LIFETIME) {
            isActive = false;
        }
    }

    protected void updateAngle() {
        angle = (float) Math.atan2(velocity.y, velocity.x);
        angle = (float) Math.toDegrees(angle) - 90;

        if (angle < 0) {
            angle += 360;
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
