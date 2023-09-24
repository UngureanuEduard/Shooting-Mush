package com.mygdx.game;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    private final Vector2 position;
    private final Vector2 velocity;
    private final TextureRegion texture;
    private int damage;
    private boolean isActive = true;
    private float damageScale;


    public Bullet(Vector2 position, Vector2 velocity) {
        this.position = position;
        this.velocity = velocity;
        Texture bulletTexture = new Texture("Environment/apple.png");
        texture = new TextureRegion(bulletTexture);
    }

    public void update(float deltaTime) {
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y, getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), damageScale, damageScale, 0);
    }


    public void dispose() {
        texture.getTexture().dispose();
    }

    public int getDamage() {
        return damage;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setDamage(int damage)
    {
        this.damage=damage;
        this.damageScale = 0.5f + (damage - 100) / 200.0f;
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


}
