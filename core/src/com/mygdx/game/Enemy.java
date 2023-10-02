package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Enemy {
    private final Vector2 position;
    private final Vector2 playerPosition;
    private final Animation<TextureRegion> walkAnimation;

    private Animation<TextureRegion> idleAnimation;
    private float stateTime;
    private boolean isFlipped = false;
    private float health;
    private final float healthScale;
    private final Sound sound;
    private float shootTimer = 0.0f;

    private float bossMovementTimer = 0.0f;
    private boolean isBossMoving = true;
    private final Integer soundVolume;
    Array<DamageText> damageTexts = new Array<>();
    Assets assets;
    private final BitmapFont defaultFont;

    Boolean isBoss;

    public Enemy(Vector2 position, Vector2 playerPosition,float health,Assets assets,Boolean isBoss,Integer soundVolume) {
        this.assets=assets;
        this.health = health;
        this.position = position;
        this.playerPosition = playerPosition;
        this.isBoss=isBoss;
        this.soundVolume=soundVolume;
        defaultFont = new BitmapFont();
        Texture duckTexture;
        Texture duckIdleTexture;
        if(!isBoss){
            duckTexture = assets.getAssetManager().get(Assets.duckTexture);
        }
        else {
            duckTexture =assets.getAssetManager().get(Assets.bossTexture);
            duckIdleTexture=assets.getAssetManager().get(Assets.idleBossTexture);
            TextureRegion[] duckIdleFrame=splitEnemyTexture(duckIdleTexture,4);
            idleAnimation = new Animation<>(0.1f, duckIdleFrame);
        }
        TextureRegion[] duckFrames = splitEnemyTexture(duckTexture,6);
        walkAnimation = new Animation<>(0.1f, duckFrames);
        this.sound=assets.getAssetManager().get(Assets.duckSound);
        stateTime = 0.0f; // Initialize the animation time
        this.healthScale = 0.7f + health/ 300.0f;
    }

    public Vector2 update(float deltaTime, Array<Bullet> bullets,Array<Enemy> enemies,Boolean isPaused) {
        if(!isPaused) {
            // Calculate the direction from the enemy to the player
            Vector2 direction = playerPosition.cpy().sub(position).nor();

            float movementSpeed = 60.0f; // Adjust the speed

            if (isBoss && isBossMoving) {
                bossMovementTimer += deltaTime;
                if (bossMovementTimer >= 3.0f) {
                    isBossMoving = false;
                    bossMovementTimer = 0.0f;
                }
            } else{
                bossMovementTimer += deltaTime;
                if (bossMovementTimer >= 11.0f) {
                    isBossMoving = true;
                    bossMovementTimer = 0.0f;
                }
            }

            if(isBossMoving||!isBoss){
                position.add(direction.x * movementSpeed * deltaTime, direction.y * movementSpeed * deltaTime);
            }

            // Update animation stateTime
            stateTime += deltaTime;

            // Determine if the enemy should be flipped
            isFlipped = direction.x < 0;

            // Check for bullet collisions
            for (Bullet bullet : bullets) {
                if(!bullet.getType().equals("Enemy")){
                    if (isCollidingWithBullet(bullet)) {
                        if (takeDamage(bullet.getDamage(), enemies))
                            return new Vector2(this.position.x + this.getWidth() / 2, this.position.y + this.getHeight() / 2);
                        bullet.setActive(false); // Deactivate the bullet
                    }
                }
            }

            shootTimer += deltaTime;

            float shootInterval = 1.0f;

            if (shootTimer >= shootInterval)
            { if(!isBoss){
                shootBullet(bullets);
            }
            else if(!isBossMoving)
            {
                    shootBulletsInAllDirections(bullets);
                }
                shootTimer = 0.0f;
            }
        }
        return new Vector2(-1, -1);
    }

    private boolean isCollidingWithBullet(Bullet bullet) {
        // Check if the enemy's bounding box intersects with the bullet's position
        if(position.x < bullet.getPosition().x + bullet.getWidth() &&
                position.x + getWidth() > bullet.getPosition().x &&
                position.y < bullet.getPosition().y + bullet.getHeight() &&
                position.y + getHeight() > bullet.getPosition().y)
        {
            damageTexts.add(new DamageText(bullet.getDamage(),bullet.getPosition(),1f));
            return true;
        }
        else return false;
    }

    private Boolean takeDamage(float damage,Array<Enemy> enemies) {
        health -= damage;
        if (health <= 0) {
            enemies.removeValue(this, true);
            return true;
        }
        sound.play(soundVolume/100f);
        return false;
    }

    public void render(SpriteBatch batch) {
        // Get the current frame from the walk animation
        TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        if(!isBossMoving)
        {
            currentFrame=idleAnimation.getKeyFrame(stateTime,true);
        }

        float scaledWidth = getWidth() * healthScale;
        float scaledHeight = getHeight() * healthScale;

        // Draw the current frame at the enemy's position
        batch.draw(currentFrame, position.x, position.y, scaledWidth / 2, scaledHeight / 2, scaledWidth, scaledHeight, isFlipped ? -1 : 1, 1, 0);
        // Render damage texts
        renderDamageTexts(batch, Gdx.graphics.getDeltaTime());
    }

    public TextureRegion[] splitEnemyTexture(Texture characterTexture,int n) {
        TextureRegion[][] tmp = TextureRegion.split(characterTexture, 32, 32);
        TextureRegion[] characterFrames = new TextureRegion[n];
        System.arraycopy(tmp[0], 0, characterFrames, 0, n);
        return characterFrames;
    }

    private void shootBullet(Array<Bullet> bullets) {
        // Calculate the direction from the enemy to the player
        Vector2 direction = playerPosition.cpy().sub(position).nor();

        // Create a new Bullet and set the damage
        Bullet bullet = new Bullet(position.cpy(), direction.cpy().scl(400), 1, assets,"Enemy",soundVolume);

        // Add bullet to array
        bullets.add(bullet);
    }

    private void shootBulletsInAllDirections(Array<Bullet> bullets) {
        float bulletSpeed = 450.0f;
        float angle=MathUtils.random(0,3);

        for (; angle < 360; angle += 15) {
            Vector2 direction = new Vector2(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle));
            Bullet bullet = new Bullet(new Vector2(position.x+getWidth(),position.y), direction.cpy().scl(bulletSpeed), 1, assets, "Enemy",soundVolume);
            bullets.add(bullet);
        }
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

    public float getHealth(){return health;}

    public void renderDamageTexts(SpriteBatch batch, float deltaTime) {
        Array<DamageText> textsToRemove = new Array<>();

        for (DamageText damageText : damageTexts) {
            damageText.update(deltaTime);
            float newY = damageText.getPosition().y + 20 * deltaTime;
            damageText.getPosition().set(damageText.getPosition().x, newY);

            defaultFont.draw(batch, damageText.getText(), damageText.getPosition().x, damageText.getPosition().y);

            if (damageText.isFinished()) {
                textsToRemove.add(damageText);
            }
        }

        damageTexts.removeAll(textsToRemove, true);
    }
}
