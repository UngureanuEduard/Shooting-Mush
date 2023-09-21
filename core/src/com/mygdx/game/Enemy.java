package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Enemy {
    private final Vector2 position;
    private final Vector2 playerPosition;
    private final Animation<TextureRegion> walkAnimation;
    private float stateTime;
    private boolean isFlipped = false;

    public Enemy(Vector2 position, Vector2 playerPosition) {
        this.position = position;
        this.playerPosition = playerPosition;
        Texture duckTexture = new Texture("Duck.png");
        TextureRegion[] duckFrames = splitEnemyTexture(duckTexture);
        walkAnimation = new Animation<>(0.1f, duckFrames);
        stateTime = 0.0f; // Initialize the animation time
    }

    public void update(float deltaTime) {
        // Calculate the direction from the enemy to the player
        Vector2 direction = playerPosition.cpy().sub(position).nor();

        float movementSpeed = 100.0f; // Adjust the speed

        position.add(direction.x * movementSpeed * deltaTime, direction.y * movementSpeed * deltaTime);

        // Update animation stateTime
        stateTime += deltaTime;

        // Determine if the enemy should be flipped
        isFlipped = direction.x < 0;
    }

    public void render(SpriteBatch batch) {
        // Get the current frame from the walk animation
        TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);

        // Draw the current frame at the enemy's position
        batch.draw(currentFrame, position.x, position.y, isFlipped ? -currentFrame.getRegionWidth() : currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
    }

    private TextureRegion[] splitEnemyTexture(Texture characterTexture) {
        TextureRegion[][] tmp = TextureRegion.split(characterTexture, 32, 32);
        TextureRegion[] characterFrames = new TextureRegion[6];
        System.arraycopy(tmp[0], 0, characterFrames, 0, 6);
        return characterFrames;
    }
}

