package com.mygdx.game.combat_system;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.utilities_resources.Assets;

public class FireBall extends CharacterBullet {
    private Animation<TextureRegion> animation;
    private float animationTime;
    private PointLight fireballLight;

    public FireBall() {
        super();
        animationTime = 0f;
    }

    public void init(Vector2 position, Vector2 velocity, float damage, Assets assets, Integer soundVolume, boolean fromHost , RayHandler rayHandler) {
        super.init(position, velocity, damage, assets, soundVolume, fromHost);
        setupAnimation(assets);
        animationTime = 0f;

        if (rayHandler != null) {
            fireballLight = new PointLight(rayHandler, 32, new Color(1f, 0.5f, 0f, 0.8f), 80, position.x, position.y);
            fireballLight.setSoft(true);
            fireballLight.setSoftnessLength(5f);
            fireballLight.setStaticLight(false);
        }
    }

    private void setupAnimation(Assets assets) {
        Texture fireballTexture = assets.getAssetManager().get(Assets.fireBallTexture);
        TextureRegion[][] split = TextureRegion.split(fireballTexture, 32, 32);
        TextureRegion[] frames = new TextureRegion[4];
        System.arraycopy(split[0], 0, frames, 0, 4);
        animation = new Animation<>(0.1f, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        animationTime += deltaTime;
        if (fireballLight != null) {
            fireballLight.setPosition(getPosition().x + getWidth() / 2, getPosition().y + getHeight() / 2);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = animation.getKeyFrame(animationTime);
        batch.draw(currentFrame, getPosition().x, getPosition().y,
                getWidth() / 2, getHeight() / 2,
                getWidth(), getHeight(),
                (float) (getDamageScale() - 0.3),
                (float) (getDamageScale() - 0.3),
                getAngle());
    }

    @Override
    protected void setTextureAndSound(Assets assets, Integer soundVolume) {
        Sound soundCharacter = assets.getAssetManager().get(Assets.throwExplosionSound);
        soundCharacter.play(soundVolume / 100f);
    }

    @Override
    public void reset() {
        super.reset();
        animationTime = 0f;
        if (fireballLight != null) {
            fireballLight.remove();
            fireballLight = null;
        }
    }
}