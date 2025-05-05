package com.mygdx.game.entities.enemy;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.utilities_resources.Assets;

public class DummyEnemy extends Enemy {

    private static final int TILE_SIZE = 32;
    private static final float SCALE = 0.8f;

    private boolean shouldBeRemoved = false;
    private float deathTimer = -1f;

    private float syncedStateTime = 0f;
    private boolean syncedIdle = true;


    public DummyEnemy(Vector2 position, float health, boolean isFlipped, Assets assets  ) {
        super();
        setPosition(position);
        setHealth(health);
        setMaxHealth(health);
        setFlipped(isFlipped);
        setAssets(assets);
        setSizeScale(SCALE);
        loadEnemyTextures(1);
        setWalkAnimation(new Animation<>(0.1f,splitEnemyTexture(getWalkTexture(), 6 ,TILE_SIZE ,TILE_SIZE)));
        setIdleAnimation(new Animation<>(0.1f,splitEnemyTexture(getIdleTexture(), 4 , TILE_SIZE, TILE_SIZE)));
    }

    public void updateFromNetwork(Vector2 position, float health, boolean isFlipped) {
        setPosition(position);
        setHealth(health);
        setIsFlipped(isFlipped);
    }


    public void update(float deltaTime) {
        if (!isAlive()) {
            if (deathTimer < 0) {
                deathTimer = 0;
            }

            deathTimer += deltaTime;

            if (deathTimer >= 1.0f && !shouldBeRemoved) {
                shouldBeRemoved = true;
            }

            return;
        }

        setStateTime(getStateTime() + deltaTime);
    }


    @Override
    public void render(SpriteBatch batch) {
        if (!isAlive()) {
            return;
        }

        TextureRegion frame = syncedIdle
                ? getIdleAnimation().getKeyFrame(syncedStateTime, true)
                : getWalkAnimation().getKeyFrame(syncedStateTime, true);

        drawCurrentFrame(batch, frame,
                calculateScaledDimension(getWidth()),
                calculateScaledDimension(getHeight()),
                isFlipped());

        if (isDamaged() || getIsAttacked()) {
            renderHealthBar(batch);
        }
    }


    @Override
    public void setAlive(boolean alive) {
        super.setAlive(alive);
    }

    public void setStateTime(float syncedStateTime) {
        this.syncedStateTime = syncedStateTime;
    }

    public void setIdle(boolean syncedIdle) {
        this.syncedIdle = syncedIdle;
    }

    public boolean shouldBeRemoved() { return shouldBeRemoved; }

}