package com.mygdx.game.animations_effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.utilities_resources.Assets;

public class CloudShadow {
    private final Texture cloudTexture;
    private final Vector2 position;
    private final float speed;
    private final float alpha;
    private boolean active;
    private final boolean movingRight;

    public CloudShadow(Assets assets) {
        this.cloudTexture = assets.getAssetManager().get(Assets.cloudTexture);
        this.speed = 20f;
        this.alpha = 0.2f;
        this.active = true;
        this.movingRight = MathUtils.randomBoolean();

        float y = MathUtils.random(0, 1500);
        float x = movingRight ? 1 : 1400;

        this.position = new Vector2(x, y);
    }

    public void update(float delta) {

        if (active) {
            position.x += (movingRight ? speed : -speed) * delta;
            if (movingRight && position.x > Gdx.graphics.getWidth() + cloudTexture.getWidth()) {
                active = false;
            }
            if (!movingRight && position.x < -cloudTexture.getWidth()) {
                active = false;
            }
        }
    }

    public void render(SpriteBatch batch) {
        if (!active) return;

        Color oldColor = batch.getColor().cpy();
        batch.setColor(0, 0, 0, alpha);

        float targetWidth = 150;
        float scale = targetWidth / cloudTexture.getWidth();
        float targetHeight = cloudTexture.getHeight() * scale;

        batch.draw(cloudTexture, position.x, position.y, targetWidth, targetHeight);
        batch.setColor(oldColor);
    }

    public boolean isActive() {
        return active;
    }

    public void dispose() {
        cloudTexture.dispose();
    }
}