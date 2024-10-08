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

    private static final float BULLET_SIZE = 35f;
    private static final float HITBOX_RADIUS_SCALE = 0.25f;
    private  Circle hitBox;
    private  ShapeRenderer shapeRenderer;

    public CharacterBullet(){
        super();
    }

    public void init(Vector2 position , Vector2 velocity, float damage, Assets assets , Integer soundVolume) {
        this.position.set(position);
        this.alive = true;
        this.velocity = velocity;
        this.damage = damage;
        this.damageScale = DAMAGE_SCALE_BASE + damage / DAMAGE_SCALE_FACTOR;
        this.assets=assets;
        Texture bulletAppleTexture = this.assets.getAssetManager().get(Assets.bulletTexture);
        Sound soundCharacter = assets.getAssetManager().get(Assets.throwSound);
        texture = new TextureRegion(bulletAppleTexture);
        soundCharacter.play(soundVolume/100f);
        hitBox=new Circle();
        shapeRenderer = new ShapeRenderer();
    }

    public void update(float deltaTime){
        updatePosition(deltaTime);
        updateAngle();

        // Set hitbox center to be in the middle of the texture
        hitBox.set(position.x + getWidth() / 2, position.y + getHeight() / 2, getWidth() *HITBOX_RADIUS_SCALE); // Adjust radius as needed
    }

    public void render(SpriteBatch batch, OrthographicCamera camera){
        renderTexture(batch);
        batch.end();
        drawHitboxes(camera);
        batch.begin();
    }


    private void renderTexture(SpriteBatch batch) {
        batch.draw(texture,
                position.x, position.y,        // x, y position of the bottom-left corner
                getWidth() / 2, getHeight() / 2, // originX, originY (center for rotation)
                getWidth(), getHeight(),        // width and height of the drawn region
                (float) (damageScale-0.3), (float) (damageScale-0.3),       // scaleX and scaleY
                angle);                         // rotation angle
    }

    private void drawHitboxes(OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        // Draw the circle hitbox (head)
        shapeRenderer.setColor(0, 1, 0, 1); // Green color for the circle
        shapeRenderer.circle(hitBox.x, hitBox.y, hitBox.radius);

        shapeRenderer.end();
    }
    private float getWidth() {
        return BULLET_SIZE * damageScale * 0.8f;
    }

    private float getHeight() {
        return BULLET_SIZE * damageScale * 0.8f;
    }

    public Circle getHitBox(){return hitBox;}

}
