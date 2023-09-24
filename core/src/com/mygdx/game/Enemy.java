package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Enemy {
    private final Vector2 position;
    private final Vector2 playerPosition;
    private final Animation<TextureRegion> walkAnimation;
    private float stateTime;
    private boolean isFlipped = false;
    private int health;
    private float healthScale=1f;

    public Enemy(Vector2 position, Vector2 playerPosition,int health) {
        this.health = health;
        this.position = position;
        this.playerPosition = playerPosition;
        Texture duckTexture = new Texture("Environment/Duck.png");
        System.out.println(duckTexture.getHeight());
        TextureRegion[] duckFrames = splitEnemyTexture(duckTexture);
        walkAnimation = new Animation<>(0.1f, duckFrames);
        stateTime = 0.0f; // Initialize the animation time
        this.healthScale = 0.5f + (health - 100) / 200.0f;
    }

    public void update(float deltaTime, Array<Bullet> bullets,Array<Enemy> enemies) {
        // Calculate the direction from the enemy to the player
        Vector2 direction = playerPosition.cpy().sub(position).nor();

        float movementSpeed = 100.0f; // Adjust the speed

        position.add(direction.x * movementSpeed * deltaTime, direction.y * movementSpeed * deltaTime);

        // Update animation stateTime
        stateTime += deltaTime;

        // Determine if the enemy should be flipped
        isFlipped = direction.x < 0;

        // Check for bullet collisions
        for (Bullet bullet : bullets) {
            if (isCollidingWithBullet(bullet)) {
                takeDamage(bullet.getDamage(),enemies); // Reduce enemy's health
                bullet.setActive(false); // Deactivate the bullet
            }
        }
    }

    private boolean isCollidingWithBullet(Bullet bullet) {
        // Check if the enemy's bounding box intersects with the bullet's position
        return position.x < bullet.getPosition().x + bullet.getWidth() &&
                position.x + getWidth() > bullet.getPosition().x &&
                position.y < bullet.getPosition().y + bullet.getHeight() &&
                position.y + getHeight() > bullet.getPosition().y;
    }

    private void takeDamage(int damage,Array<Enemy> enemies) {
        health -= damage;
        if (health <= 0) {
            enemies.removeValue(this, true);
        }

    }

    public void render(SpriteBatch batch) {
        // Get the current frame from the walk animation
        TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);

        float scaledWidth = getWidth() * healthScale;
        float scaledHeight = getHeight() * healthScale;

        // Draw the current frame at the enemy's position
        batch.draw(currentFrame, position.x, position.y, scaledWidth / 2, scaledHeight / 2, scaledWidth, scaledHeight, isFlipped ? -1 : 1, 1, 0);
    }

    private TextureRegion[] splitEnemyTexture(Texture characterTexture) {
        TextureRegion[][] tmp = TextureRegion.split(characterTexture, 32, 32);
        TextureRegion[] characterFrames = new TextureRegion[6];
        System.arraycopy(tmp[0], 0, characterFrames, 0, 6);
        return characterFrames;
    }

    public float getWidth()
    {
        return 32*healthScale;
    }

    public float getHeight()
    {
        return 35*healthScale;
    }

    public Vector2 getPosition() {
        return position;
    }
}

