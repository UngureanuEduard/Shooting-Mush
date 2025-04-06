package com.mygdx.game.animations_effects.firework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class FireworkRocket {
    private static final float FRAME_DURATION = 0.15f;
    private static final int FRAME_SIZE = 256;
    private static final float SPEED = 300f;

    private final TextureRegion[] frames;
    private float stateTime = 0f;
    private boolean isFinished = false;
    private final Vector2 position;
    private final float targetY;

    public FireworkRocket(Texture texture  , Vector2 position) {
        this.frames = new TextureRegion[3];
        this.position = position;
        for (int i = 0; i < 3; i++) {
            frames[i] = new TextureRegion(texture, i * FRAME_SIZE, 0, FRAME_SIZE, FRAME_SIZE);
        }
        float screenHeight = Gdx.graphics.getHeight();
        this.targetY = MathUtils.random(screenHeight / 2f, screenHeight - FRAME_SIZE);
    }

    public void update(float delta) {
        stateTime += delta;

        if (position.y < targetY) {
            position.y += SPEED * delta;
        } else {
            isFinished = true;
        }
    }

    public void render(SpriteBatch batch) {
        int frameIndex = (int)(stateTime / FRAME_DURATION);

        if (frameIndex < frames.length) {

            batch.draw(frames[frameIndex], position.x, position.y);
        } else {
            int loopIndex = 1 + (frameIndex % 2);
            batch.draw(frames[loopIndex], position.x, position.y);
        }
    }


    public boolean isNotFinished() {
        return !isFinished;
    }

    public Vector2 getPosition() {
        return position;
    }
}
