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

public class EnemyBoss extends Enemy{

    private final Vector2 position;
    private final Vector2 playerPosition;
    private final Animation<TextureRegion> walkAnimation;

    private final Animation<TextureRegion> idleAnimation;
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

    private final Integer critRate;

    private final float shootInterval = 1.0f;


    public EnemyBoss(Vector2 position, Vector2 playerPosition,float health,Assets assets,Integer soundVolume,Integer critRate) {
        super(position, playerPosition,health,assets,soundVolume,critRate);
        this.position=position;
        this.playerPosition=playerPosition;
        this.soundVolume=soundVolume;
        this.health=health;
        this.assets=assets;
        this.critRate=critRate;

        defaultFont = new BitmapFont();

        Texture duckTexture;
        Texture duckIdleTexture;
        duckTexture =assets.getAssetManager().get(Assets.bossTexture);
        duckIdleTexture=assets.getAssetManager().get(Assets.idleBossTexture);
        TextureRegion[] duckIdleFrame=splitEnemyTexture(duckIdleTexture,4);
        idleAnimation = new Animation<>(0.1f, duckIdleFrame);
        TextureRegion[] duckFrames = splitEnemyTexture(duckTexture,6);
        walkAnimation = new Animation<>(0.1f, duckFrames);

        this.sound=assets.getAssetManager().get(Assets.duckSound);

        stateTime = 0.0f; // Initialize the animation time

        this.healthScale = 0.7f + health/ 300.0f;

    }

    @Override
    public Vector2 update(float deltaTime, Array<Bullet> bullets, Array<Enemy> enemies, boolean isPaused) {
        if(!isPaused) {
            // Calculate the direction from the enemy to the player
            Vector2 direction = playerPosition.cpy().sub(position).nor();

            float movementSpeed = 60.0f; // Adjust the speed

            if (isBossMoving) {
                bossMovementTimer += deltaTime;
                if (bossMovementTimer >= 3.0f) {
                    isBossMoving = false;
                    bossMovementTimer = 0.0f;
                }
                position.add(direction.x * movementSpeed * deltaTime, direction.y * movementSpeed * deltaTime);
            } else{
                bossMovementTimer += deltaTime;
                if (bossMovementTimer >= 11.0f) {
                    isBossMoving = true;
                    bossMovementTimer = 0.0f;
                }
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
            if(!isBossMoving&&shootTimer >= shootInterval){

                shootBulletsInAllDirections(bullets);
                shootTimer=0f;
            }


        }
        return new Vector2(-1, -1);
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = getCurrentFrame();
        float scaledWidth = calculateScaledDimension(getWidth());
        float scaledHeight = calculateScaledDimension(getHeight());

        drawCurrentFrame(batch, currentFrame, scaledWidth, scaledHeight);
        renderDamageTexts(batch, Gdx.graphics.getDeltaTime());
    }

    private TextureRegion getCurrentFrame() {
        if (isBossMoving) {
            return walkAnimation.getKeyFrame(stateTime, true);
        } else {
            return idleAnimation.getKeyFrame(stateTime, true);
        }
    }

    private void shootBulletsInAllDirections(Array<Bullet> bullets) {
        float bulletSpeed = 450.0f;
        float angle= MathUtils.random(0,3);

        for (; angle < 360; angle += 15) {
            Vector2 direction = new Vector2(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle));
            Bullet bullet = new Bullet(new Vector2(position.x+getWidth(),position.y), direction.cpy().scl(bulletSpeed), 1, assets, "Enemy",soundVolume);
            bullets.add(bullet);
        }
    }


}
