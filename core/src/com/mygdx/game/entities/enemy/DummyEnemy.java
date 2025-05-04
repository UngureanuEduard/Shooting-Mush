package com.mygdx.game.entities.enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.utilities_resources.Assets;

public class DummyEnemy extends Enemy {

    private boolean shouldBeRemoved = false;
    private float deathTimer = -1f;

    private float syncedStateTime = 0f;
    private boolean syncedIdle = true;


    public DummyEnemy(Vector2 position, float health, boolean isFlipped, Assets assets  ) {
        super();
        this.position = position;
        this.health = health;
        this.maxHealth = health;
        setFlipped(isFlipped);
        this.assets = assets;
        this.sizeScale = SCALE;
        loadEnemyTextures(1);

        walkAnimation = new com.badlogic.gdx.graphics.g2d.Animation<>(0.1f,
                splitEnemyTexture(walkTexture, 6 ,32 ,32));
        idleAnimation = new com.badlogic.gdx.graphics.g2d.Animation<>(0.1f,
                splitEnemyTexture(idleTexture, 4 , 32, 32));
    }

    public void updateFromNetwork(Vector2 position, float health, boolean isFlipped) {
        this.position.set(position);
        this.health = health;
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

        stateTime += deltaTime;
    }


    @Override
    public void render(SpriteBatch batch) {
        if (!isAlive()) {
            return;
        }

        TextureRegion frame = syncedIdle
                ? idleAnimation.getKeyFrame(syncedStateTime, true)
                : walkAnimation.getKeyFrame(syncedStateTime, true);

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