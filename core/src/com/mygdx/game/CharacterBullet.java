package com.mygdx.game;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public class CharacterBullet extends Bullet{

    private final Circle hitBox;
    private final ShapeRenderer shapeRenderer;

    public CharacterBullet(Vector2 position, Vector2 velocity, float damage, Assets assets, Integer soundVolume) {
        super(position, velocity, damage, assets);
        Texture bulletAppleTexture = this.assets.getAssetManager().get(Assets.bulletTexture);
        Sound soundCharacter = assets.getAssetManager().get(Assets.throwSound);
        texture = new TextureRegion(bulletAppleTexture);
        soundCharacter.play(soundVolume/100f);
        hitBox=new Circle();
        shapeRenderer = new ShapeRenderer();
    }

    public void update(float deltaTime){

        updatePosition(deltaTime);
        hitBox.set(getPosition().x+getWidth()/2,getPosition().y+getHeight()/2, (float) (getWidth()/2.8));
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
        shapeRenderer.circle(hitBox.x, hitBox.y, hitBox.radius);

        shapeRenderer.end();
    }

    public Circle getHitBox(){return hitBox;}


}
