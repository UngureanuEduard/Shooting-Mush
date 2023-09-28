package com.mygdx.game;

import com.badlogic.gdx.audio.Sound;
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
    private final float healthScale;
    Sound sound;

    public Enemy(Vector2 position, Vector2 playerPosition,int health,Assets assets) {
        this.health = health;
        this.position = position;
        this.playerPosition = playerPosition;
        Texture duckTexture = assets.getAssetManager().get(Assets.duckTexture);
        this.sound=assets.getAssetManager().get(Assets.duckSound);
        TextureRegion[] duckFrames = splitEnemyTexture(duckTexture);
        walkAnimation = new Animation<>(0.1f, duckFrames);
        stateTime = 0.0f; // Initialize the animation time
        this.healthScale = 0.7f + health/ 300.0f;
    }

    public Vector2 update(float deltaTime, Array<Bullet> bullets,Array<Enemy> enemies,Boolean isPaused) {
        if(!isPaused) {
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
                    if (takeDamage(bullet.getDamage(), enemies))
                        return new Vector2(this.position.x + this.getWidth() / 2, this.position.y + this.getHeight() / 2);
                    bullet.setActive(false); // Deactivate the bullet
                }
            }
        }
        return new Vector2(-1, -1);
    }

    private boolean isCollidingWithBullet(Bullet bullet) {
        // Check if the enemy's bounding box intersects with the bullet's position
        return position.x < bullet.getPosition().x + bullet.getWidth() &&
                position.x + getWidth() > bullet.getPosition().x &&
                position.y < bullet.getPosition().y + bullet.getHeight() &&
                position.y + getHeight() > bullet.getPosition().y;
    }

    private Boolean takeDamage(int damage,Array<Enemy> enemies) {
        health -= damage;
        if (health <= 0) {
            enemies.removeValue(this, true);
            return true;
        }
        sound.play(0.1f);
        return false;
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

    public float getHealthScale(){return healthScale;}
}
