package com.mygdx.game.animations_effects.firework;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class FireworkExplosion {
    private static final float FRAME_DURATION = 0.15f;
    private static final int FRAME_SIZE = 256;

    private final TextureRegion[] frames;
    private float stateTime = 0f;
    private boolean finished = false;
    private final Vector2 position;

    public FireworkExplosion(Texture texture, Vector2 position) {
        this.frames = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            frames[i] = new TextureRegion(texture, i * FRAME_SIZE, 0, FRAME_SIZE, FRAME_SIZE);
        }
        this.position = position;
    }

    public void update(float delta) {
        stateTime += delta;
        if (stateTime > FRAME_DURATION * frames.length) {
            finished = true;
        }
    }

    public void render(SpriteBatch batch) {
        int currentFrame = (int)(stateTime / FRAME_DURATION);
        if (currentFrame < frames.length) {
            batch.draw(frames[currentFrame], position.x, position.y);
        }
    }

    public boolean isFinished() {
        return finished;
    }
}