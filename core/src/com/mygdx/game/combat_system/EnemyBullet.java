package com.mygdx.game.combat_system;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.utilities_resources.Assets;

public class EnemyBullet extends Bullet{
    private static final float WIDTH_SCALE = 216 * 0.04f;
    private static final float HEIGHT_SCALE = 297 * 0.04f;

    private  float width;
    private  float height;
    private  Polygon hitBox;

    public EnemyBullet(){
        super();
    }

    public void init(Vector2 position , Vector2 velocity, float damage, Assets assets , Integer soundVolume) {
        super.init(position,velocity,damage);
        Texture bulletCornTexture = assets.getAssetManager().get(Assets.candyCornTexture);
        width = WIDTH_SCALE;
        height = HEIGHT_SCALE;
        Sound soundEnemy = assets.getAssetManager().get(Assets.duckShootSound);
        setTexture(new TextureRegion(bulletCornTexture));
        soundEnemy.play(soundVolume / 100f);

        float[] vertices = {
                width / 2, height,
                0, 0,
                width, 0
        };

        hitBox = new Polygon(vertices);
        hitBox.setOrigin(width / 2, height / 2);
    }

    public void update(float deltaTime){
        updatePosition(deltaTime);
        updateAngle();
        hitBox.setPosition(getPosition().x, getPosition().y);
        hitBox.setRotation(getAngle());
    }

    public void render(SpriteBatch batch){
        renderTexture(batch);
    }

    private void renderTexture(SpriteBatch batch) {
        batch.draw(getTexture(), getPosition().x, getPosition().y, getWidth() / 2, getHeight() / 2,
                getWidth(), getHeight(), 1, 1, getAngle());

    }

    private float getWidth() {
        return width;
    }

    private float getHeight() {
        return  height;
    }

    public Polygon getHitBox(){return hitBox;}

}
