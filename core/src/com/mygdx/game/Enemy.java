package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

public class Enemy {

    public static final float MOVEMENT_SPEED = 40.0f;
    private static final float BULLET_COOLDOWN = 5.0f;
    private static final float SCALE = 0.8f;
    private static final Random RANDOM = new Random();

    protected final Vector2 position;
    protected final Vector2 playerPosition;
    protected Animation<TextureRegion> walkAnimation;

    protected float stateTime;
    protected boolean isFlipped = false;
    protected float health;
    protected float sizeScale;
    protected final Sound sound;
    protected float shootTimer = 0.0f;

    protected final Integer soundVolume;
    Array<DamageText> damageTexts = new Array<>();
    protected Assets assets;
    protected final BitmapFont defaultFont;

    protected final Integer critRate;

    protected Rectangle bodyHitbox;
    protected Circle headHitbox;
    protected ShapeRenderer shapeRenderer;
    protected Vector2 direction;

    public Enemy(Vector2 position, Vector2 playerPosition, float health, Assets assets, Integer soundVolume, Integer critRate) {
        this.assets = assets;
        this.health = health;
        this.position = position;
        this.playerPosition = playerPosition;
        this.soundVolume = soundVolume;
        this.critRate = critRate;

        sizeScale = SCALE;

        bodyHitbox = new Rectangle();
        headHitbox = new Circle();
        shapeRenderer = new ShapeRenderer();
        defaultFont = new BitmapFont();
        Texture duckTexture;
        duckTexture = assets.getAssetManager().get(Assets.duckTexture);

        TextureRegion[] duckFrames = splitEnemyTexture(duckTexture, 6);
        walkAnimation = new Animation<>(0.1f, duckFrames);
        this.sound = assets.getAssetManager().get(Assets.duckSound);
        stateTime = 0.0f; // Initialize the animation time
    }

    public void update(float deltaTime, Array<EnemyBullet> enemyBullets, Array<CharacterBullet> Characterbullets, boolean isPaused, Array<Enemy> enemies) {
        if (!isPaused) {
            // Calculate the direction from the enemy to the player
            direction = playerPosition.cpy().sub(position).nor();

            boolean isColliding = isCollidingWithEnemy(enemies);
            if (!isColliding) {
                specialBehavior(deltaTime, direction);
            }

            updateHitboxes();

            // Update animation stateTime
            stateTime += deltaTime;
            // Determine if the enemy should be flipped
            isFlipped = direction.x < 0;

            // Check for bullet collisions
            CheckBulletCollisions(Characterbullets);

            shootTimer += deltaTime;
            shootBullet(enemyBullets);
        }
    }

    private void updateHitboxes() {
        bodyHitbox.set(position.x + getWidth() / 3.8f * sizeScale, position.y + getHeight() / 10.0f * sizeScale, getWidth() / 2.3f * sizeScale, getHeight() / 2.7f * sizeScale);
        headHitbox.set(position.x + getWidth() / 2.0f * sizeScale, position.y + getHeight() / 1.5f * sizeScale, getHeight() / 5.0f * sizeScale);
    }

    public boolean isCollidingWithEnemy(Array<Enemy> enemies) {
        Array<Enemy> enemiesCopy = new Array<>(enemies);
        for (Enemy otherEnemy : enemiesCopy) {
            if (otherEnemy != this) {
                // Check for collision with body hitbox
                if (Intersector.overlaps(this.bodyHitbox, otherEnemy.bodyHitbox) ||
                        Intersector.overlaps(this.headHitbox, otherEnemy.headHitbox)) {
                    resolveCollisionWithEnemy(otherEnemy); // Resolve the collision
                    return true;
                }
            }
        }
        return false;
    }

    private void resolveCollisionWithEnemy(Enemy otherEnemy) {
        Vector2 collisionVector = new Vector2();

        // Determine the direction to resolve the collision
        if (Intersector.overlaps(this.bodyHitbox, otherEnemy.bodyHitbox)) {
            collisionVector.set(otherEnemy.bodyHitbox.x - this.bodyHitbox.x, otherEnemy.bodyHitbox.y - this.bodyHitbox.y);
        } else {
            collisionVector.set(otherEnemy.headHitbox.x - this.headHitbox.x, otherEnemy.headHitbox.y - this.headHitbox.y);
        }

        // Normalize and scale the collision vector
        collisionVector.nor().scl(0.5f);

        // Adjust positions to prevent overlapping
        this.position.sub(collisionVector);
        otherEnemy.position.add(collisionVector);
    }

    public void CheckBulletCollisions(Array<CharacterBullet> bullets) {
        for (CharacterBullet bullet : bullets) {
            if (isCollidingWithBullet(bullet)) {
                takeDamage(bullet.getDamage());
                bullet.setActive(false); // Deactivate the bullet
            }
        }
    }

    public boolean isCollidingWithBullet(CharacterBullet bullet) {
        // Check if the enemy's bounding box intersects with the bullet's position
        if (Intersector.overlaps(bullet.getHitBox(), headHitbox) || Intersector.overlaps(bullet.getHitBox(), bodyHitbox)) {
            Boolean isCrit = isCrit();
            if (isCrit) {
                bullet.setDamage(bullet.getDamage() * 4);
            }
            damageTexts.add(new DamageText(bullet.getDamage(), bullet.getPosition(), 1f, isCrit));
            return true;
        } else return false;
    }

    public void takeDamage(float damage) {
        health -= damage;
        sound.play(soundVolume / 100f);
    }

    public void render(SpriteBatch batch, OrthographicCamera camera) {
        TextureRegion currentFrame = getCurrentFrame();
        float scaledWidth = calculateScaledDimension(getWidth());
        float scaledHeight = calculateScaledDimension(getHeight());

        drawCurrentFrame(batch, currentFrame, scaledWidth, scaledHeight, isFlipped);
        renderDamageTexts(batch, Gdx.graphics.getDeltaTime());

        batch.end();

        drawHitboxes(camera);  // Debugging
    }

    protected void drawHitboxes(OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // Draw the rectangle hitbox (body)
        shapeRenderer.setColor(1, 0, 0, 1); // Red color for the rectangle
        shapeRenderer.rect(bodyHitbox.x, bodyHitbox.y, bodyHitbox.width, bodyHitbox.height);

        // Draw the circle hitbox (head)
        shapeRenderer.setColor(0, 1, 0, 1); // Green color for the circle
        shapeRenderer.circle(headHitbox.x, headHitbox.y, headHitbox.radius);

        shapeRenderer.end();
    }

    protected TextureRegion getCurrentFrame() {
        return walkAnimation.getKeyFrame(stateTime, true);
    }

    protected float calculateScaledDimension(float dimension) {
        return dimension * sizeScale;
    }

    protected void drawCurrentFrame(SpriteBatch batch, TextureRegion currentFrame, float scaledWidth, float scaledHeight, boolean isFlipped) {
        batch.draw(currentFrame, position.x, position.y, scaledWidth / 2, scaledHeight / 2, scaledWidth, scaledHeight, isFlipped ? -1 : 1, 1, 0);
    }

    protected TextureRegion[] splitEnemyTexture(Texture characterTexture, int n) {
        TextureRegion[][] tmp = TextureRegion.split(characterTexture, 32, 32);
        TextureRegion[] characterFrames = new TextureRegion[n];
        System.arraycopy(tmp[0], 0, characterFrames, 0, n);
        return characterFrames;
    }

    protected void shootBullet(Array<EnemyBullet> enemyBullets) {
        if (shootTimer >= BULLET_COOLDOWN) {
            shootTimer = 0;
            Vector2 direction = playerPosition.cpy().sub(position).nor();
            EnemyBullet bullet = new EnemyBullet(position.cpy(), direction.cpy().scl(200), 1, assets, soundVolume);
            enemyBullets.add(bullet);
        }
    }

    public float getWidth() {
        return 32 * sizeScale;
    }

    public float getHeight() {
        return 35 * sizeScale;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getSizeScale() {
        return sizeScale;
    }

    public float getHealth() {
        return health;
    }

    public void renderDamageTexts(SpriteBatch batch, float deltaTime) {
        Array<DamageText> textsToRemove = new Array<>();

        for (DamageText damageText : damageTexts) {
            damageText.update(deltaTime);
            float newY = damageText.getPosition().y + 20 * deltaTime;
            damageText.getPosition().set(damageText.getPosition().x, newY);
            float textScale = 0.5f; // Adjust this value to change the size
            defaultFont.getData().setScale(textScale, textScale);

            if (!damageText.getIsCrit()) {
                defaultFont.draw(batch, damageText.getText(), damageText.getPosition().x, damageText.getPosition().y);
            } else {
                defaultFont.setColor(1, 0, 0, 1);
                defaultFont.draw(batch, damageText.getText(), damageText.getPosition().x, damageText.getPosition().y);
                defaultFont.setColor(1, 1, 1, 1);
            }

            if (damageText.isFinished()) {
                textsToRemove.add(damageText);
            }
        }

        damageTexts.removeAll(textsToRemove, true);
    }

    public Boolean isCrit() {
        int randomNumber = RANDOM.nextInt(100) + 1;
        return randomNumber <= critRate;
    }

    // Override for each class with a special behavior
    protected void specialBehavior(float deltaTime, Vector2 direction) {
        position.add(direction.x * Enemy.MOVEMENT_SPEED * deltaTime, direction.y * Enemy.MOVEMENT_SPEED * deltaTime);
    }

    public float getSpeed() {
        return MOVEMENT_SPEED;
    }
}