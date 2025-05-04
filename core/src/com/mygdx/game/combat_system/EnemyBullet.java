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
    private static final float ROTATION_SPEED = 200f;

    private  float width;
    private  float height;
    private  Polygon hitBox;
    private boolean sent = false;
    private float rotationAngle = 0f;
    private boolean rotateAroundCenter = false;


    public EnemyBullet(){
        super();
    }

    public void init(Vector2 position , Vector2 velocity, float damage, Assets assets , Integer soundVolume , int mapIndex) {
        super.init(position,velocity,damage);

        width = WIDTH_SCALE;
        height = HEIGHT_SCALE;

        Texture texture;

        if (mapIndex == 1){
            texture = assets.getAssetManager().get(Assets.boneTexture);
            width = (float) (width * 1.8);
            rotateAroundCenter = true;

            float[] rectVertices = {
                    0, 0,
                    width, 0,
                    width, height,
                    0, height
            };
            hitBox = new Polygon(rectVertices);
        } else {
            texture = assets.getAssetManager().get(Assets.candyCornTexture);
            rotateAroundCenter = false;

            float[] vertices = {
                    width / 2, height,
                    0, 0,
                    width, 0
            };

            hitBox = new Polygon(vertices);
        }

        setTexture(new TextureRegion(texture));

        Sound soundEnemy = assets.getAssetManager().get(Assets.duckShootSound);
        soundEnemy.play(soundVolume / 100f);
        sent = false;

        hitBox.setOrigin(width / 2, height / 2);
    }


    public void update(float deltaTime){
        updatePosition(deltaTime);
        updateAngle();
        hitBox.setPosition(getPosition().x, getPosition().y);
        hitBox.setRotation(rotateAroundCenter ? rotationAngle : getAngle());

        if (rotateAroundCenter) {
            rotationAngle += ROTATION_SPEED * deltaTime;
            if (rotationAngle >= 360f) rotationAngle -= 360f;
        }

    }

    public void render(SpriteBatch batch ){
        float drawAngle = rotateAroundCenter ? rotationAngle : getAngle();

        batch.draw(getTexture(), getPosition().x, getPosition().y,
                getWidth() / 2, getHeight() / 2,
                getWidth(), getHeight(), 1, 1, drawAngle);
    }



    private float getWidth() {
        return width;
    }

    private float getHeight() {
        return  height;
    }

    public Polygon getHitBox(){return hitBox;}

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

}
