package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class EnemyBoss extends Enemy {

    private static final float BOSS_MOVEMENT_DURATION = 3.0f;
    private static final float BOSS_IDLE_DURATION = 11.0f;
    private static final float BULLET_SPEED = 150.0f;
    private static final float BULLET_COOLDOWN = 1.0f;
    private static final float SCALE =  1.7f;
    private static final float FIRE_ANGLE_STEP = 15.0f;

    private final Animation<TextureRegion> walkAnimation;
    private final Animation<TextureRegion> idleAnimation;
    private float bossMovementTimer = 0.0f;
    private boolean isBossMoving = true;

    public EnemyBoss(Vector2 position, Vector2 playerPosition, float health, Assets assets, Integer soundVolume, Integer critRate) {
        super(position, playerPosition, health, assets, soundVolume, critRate);

        shapeRenderer = new ShapeRenderer();
        sizeScale = SCALE;

        Texture duckTexture;
        Texture duckIdleTexture;
        duckTexture = assets.getAssetManager().get(Assets.bossTexture);
        duckIdleTexture = assets.getAssetManager().get(Assets.idleBossTexture);
        TextureRegion[] duckIdleFrame = splitEnemyTexture(duckIdleTexture, 4);
        idleAnimation = new Animation<>(0.1f, duckIdleFrame);
        TextureRegion[] duckFrames = splitEnemyTexture(duckTexture, 6);
        walkAnimation = new Animation<>(0.1f, duckFrames);
    }

    @Override
    protected TextureRegion getCurrentFrame() {
        return isBossMoving ? walkAnimation.getKeyFrame(stateTime, true) : idleAnimation.getKeyFrame(stateTime, true);
    }

    @Override
    protected void shootBullet(Array<EnemyBullet> bullets) {
        if (shootTimer >= BULLET_COOLDOWN && !isBossMoving) {
            shootTimer = 0;

            Vector2 bulletPosition = new Vector2(bodyHitbox.x + bodyHitbox.width / 2, bodyHitbox.y + bodyHitbox.height / 2);

            for (float angle = 0; angle < 360; angle += FIRE_ANGLE_STEP) {
                Vector2 direction = new Vector2(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle)).scl(BULLET_SPEED);
                EnemyBullet bullet = new EnemyBullet(bulletPosition.cpy(), direction, 1, assets, soundVolume);
                bullets.add(bullet);
            }
        }
    }

    @Override
    protected void specialBehavior(float deltaTime, Vector2 direction) {
        bossMovementTimer += deltaTime;

        if (isBossMoving) {
            if (bossMovementTimer >= BOSS_MOVEMENT_DURATION) {
                isBossMoving = false;
                bossMovementTimer = 0.0f;
            }
            position.add(direction.x * MOVEMENT_SPEED * deltaTime, direction.y * MOVEMENT_SPEED * deltaTime);
        } else {
            if (bossMovementTimer >= BOSS_IDLE_DURATION) {
                isBossMoving = true;
                bossMovementTimer = 0.0f;
            }
        }
    }
}