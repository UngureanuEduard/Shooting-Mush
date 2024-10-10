package com.mygdx.game;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class Bullet implements Pool.Poolable {
    protected static final float MAX_LIFETIME = 3.5f;
    protected static final float DAMAGE_SCALE_BASE = 0.8f;
    protected static final float DAMAGE_SCALE_FACTOR = 200.0f;

    protected Vector2 position;
    protected Vector2 velocity;
    protected TextureRegion texture;
    protected float damage;
    protected float damageScale;
    protected float lifeTimer; // Tracks the lifetime of a bullet
    protected float angle;
    Assets assets;

    protected boolean alive;

    public Bullet(){
        this.alive=false;
        this.position=new Vector2();
        this.lifeTimer= 0.0f;
    }

    @Override
    public void reset() {
        position.set(-1,-1);
        this.alive = false;
        this.lifeTimer=0.0f;
    }

    protected void updatePosition(float deltaTime) {

        // Update position
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);


        //Update LifeTimer
        lifeTimer += deltaTime;

        // Check if lifeTimer exceeds 3.5 seconds
        if (lifeTimer >= MAX_LIFETIME) {
            this.alive = false;
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

    public boolean getAlive_n(){return !this.alive;}

    public void setAlive(boolean setAlive) {
        this.alive = setAlive;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setDamage(float setDmg){damage=setDmg;}


}
