package com.mygdx.game;


import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    private final Vector2 position;
    private final Vector2 velocity;
    private final TextureRegion texture;
    private final int damage;
    private boolean isActive = true;
    private final float damageScale;

    Assets assets;

    Sound sound;

    public Bullet(Vector2 position, Vector2 velocity, int damage, Assets assets) {
        this.assets=assets;
        this.damage=damage;
        this.damageScale = 0.8f + damage / 200.0f;
        this.position = position;
        this.velocity = velocity;
        Texture bulletTexture = this.assets.getAssetManager().get(Assets.bulletTexture);
        sound = assets.getAssetManager().get(Assets.throwSound);
        texture = new TextureRegion(bulletTexture);
        sound.play(0.5f);
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
