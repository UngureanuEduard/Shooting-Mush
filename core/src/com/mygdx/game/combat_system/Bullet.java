package com.mygdx.game.combat_system;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class Bullet implements Pool.Poolable {
    private static final float MAX_LIFETIME = 10f;

    private final Vector2 position;
    private Vector2 velocity;
    private TextureRegion texture;
    private float damage;
    private float damageScale;
    private float lifeTimer;
    private float angle;
    private boolean alive;

    public Bullet(){
        alive = false;
        position = new Vector2();
        lifeTimer = 0.0f;
    }

    public void init(Vector2 position , Vector2 velocity, float damage  ) {
        this.position.set(position);
        alive = true;
        this.velocity = velocity.cpy();
        this.damage = damage;
    }

    @Override
    public void reset() {
        position.set(-1,-1);
        alive = false;
        lifeTimer=0.0f;
    }

    protected void updatePosition(float deltaTime) {

        position.add(velocity.x * deltaTime, velocity.y * deltaTime);

        lifeTimer += deltaTime;

        if (lifeTimer >= MAX_LIFETIME) {
            alive = false;
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

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean setAlive) {
        alive = setAlive;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setDamage(float setDmg){damage = setDmg;}

    public void setTexture(TextureRegion texture) {
        this.texture = texture;
    }

    public float getAngle() {
        return angle;
    }

    public TextureRegion getTexture() {
        return texture;
    }

    public float getDamageScale() {
        return damageScale;
    }

    public void setDamageScale(float damageScale) {
        this.damageScale = damageScale;
    }

    public Vector2 getVelocity() {
        return velocity;
    }
}
