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
    private final float damage;
    private boolean isActive = true;
    private final float damageScale;

    private final String type;

    Assets assets;



    public Bullet(Vector2 position, Vector2 velocity, float damage, Assets assets,String type,Integer soundVolume) {
        this.type=type;
        this.assets=assets;
        this.damage=damage;
        this.damageScale = 0.8f + damage / 200.0f;
        this.position = position;
        this.velocity = velocity;
        Texture bulletAppleTexture = this.assets.getAssetManager().get(Assets.bulletTexture);
        Texture bulletCornTexture = this.assets.getAssetManager().get(Assets.candyCornTexture);
        Sound soundCharacter = assets.getAssetManager().get(Assets.throwSound);
        Sound soundEnemy = assets.getAssetManager().get(Assets.duckShootSound);
        if (type.equals("Enemy")) {
            texture = new TextureRegion(bulletCornTexture);
            soundEnemy.play(soundVolume/100f);

        } else {
            texture = new TextureRegion(bulletAppleTexture);
            soundCharacter.play(soundVolume/100f);
        }
    }

    public void update(float deltaTime) {
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
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

    public String getType(){return type;}
}
