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

    private final Polygon hitBox;
    private final ShapeRenderer shapeRenderer;

    float[] vertices = {
            0, 0, // bottom-left corner
            20, 0, // bottom-right corner
            10, 20  // top-center point
    };

    public EnemyBullet(Vector2 position, Vector2 velocity, float damage, Assets assets, Integer soundVolume) {
        super(position, velocity, damage, assets);
        Texture bulletCornTexture = this.assets.getAssetManager().get(Assets.candyCornTexture);
        Sound soundEnemy = assets.getAssetManager().get(Assets.duckShootSound);
        texture = new TextureRegion(bulletCornTexture);
        soundEnemy.play(soundVolume/100f);
        hitBox=new Polygon(vertices);
        hitBox.setOrigin(10,10);
        shapeRenderer = new ShapeRenderer();
        hitBox.setRotation(angle);

    }
    public void update(float deltaTime){
        updatePosition(deltaTime);
        hitBox.setRotation(angle);
        hitBox.setPosition((float) (position.x+getWidth()/4.3), (float) (position.y+getWidth()/4.1));
    }

    public void render(SpriteBatch batch, OrthographicCamera camera){
        renderTexture(batch);
        batch.end();
        drawHitboxes(camera);
        batch.begin();
    }
    private void drawHitboxes(OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        // Draw the circle hitbox (head)
        shapeRenderer.setColor(0, 1, 0, 1); // Green color for the circle
        shapeRenderer.polygon(hitBox.getTransformedVertices());
        shapeRenderer.end();
    }

    public Polygon getHitBox(){return hitBox;}
}
