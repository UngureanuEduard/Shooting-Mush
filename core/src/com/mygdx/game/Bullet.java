package com.mygdx.game;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    protected final Vector2 position;
    protected final Vector2 velocity;
    protected TextureRegion texture;
    protected float damage;
    protected boolean isActive = true;
    protected final float damageScale;
    protected float lifeTimer = 0.0f; // Tracks the lifetime of a bullet



    Assets assets;

    public Bullet(Vector2 position, Vector2 velocity, float damage, Assets assets ) {

        this.assets=assets;
        this.damage=damage;
        this.damageScale = 0.8f + damage / 200.0f;
        this.position = position;
        this.velocity = velocity;
    }

    public void update(float deltaTime) {

        // Update position
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);

        //Update LifeTimer
        lifeTimer += deltaTime;

        // Check if lifeTimer exceeds 2.5 second
        if (lifeTimer >= 2.5f) {
            isActive = false;
        }
    }

    public void render(SpriteBatch batch) {

        float angle = (float) Math.atan2(velocity.y, velocity.x);
        angle = (float) Math.toDegrees(angle) - 90;

        if (angle < 0) {
            angle += 360;
        }

        batch.draw(texture, position.x, position.y, getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), damageScale, damageScale, angle);
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

    public float getWidth() {
        return 35 *this.damageScale;
    }

    public float getHeight() {
        return 35 *this.damageScale;
    }


    public void setDamage(float damage){this.damage=damage;}
}
