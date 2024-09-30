package com.mygdx.game;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class EnemyBullet extends Bullet{
    private static final float WIDTH_SCALE = 216 * 0.04f;
    private static final float HEIGHT_SCALE = 297 * 0.04f;

    private final ShapeRenderer shapeRenderer;
    private final float width;
    private final float height;
    private final Polygon hitBox;

    public EnemyBullet(Vector2 position, Vector2 velocity, float damage, Assets assets, Integer soundVolume) {
        super(position, velocity, damage, assets);
        Texture bulletCornTexture = this.assets.getAssetManager().get(Assets.candyCornTexture);
        width = WIDTH_SCALE;
        height = HEIGHT_SCALE;
        Sound soundEnemy = assets.getAssetManager().get(Assets.duckShootSound);
        texture = new TextureRegion(bulletCornTexture);
        soundEnemy.play(soundVolume / 100f);
        shapeRenderer = new ShapeRenderer();

        // Define the isosceles triangle hitbox
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
        hitBox.setPosition(position.x, position.y);
        hitBox.setRotation(angle);
    }

    public void render(SpriteBatch batch, OrthographicCamera camera){
        renderTexture(batch);
        batch.end();
        drawHitboxes(camera);
        batch.begin();
    }

    private void renderTexture(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y, getWidth() / 2, getHeight() / 2,
                getWidth(), getHeight(), 1, 1, angle);

    }

    private void drawHitboxes(OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, 1, 0, 1);
        shapeRenderer.polygon(hitBox.getTransformedVertices());
        shapeRenderer.end();
    }

    private float getWidth() {
        return width;
    }

    private float getHeight() {
        return  height;
    }


    public Polygon getHitBox(){return hitBox;}
}
