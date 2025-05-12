package com.mygdx.game.entities.enemy;

import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.GameScene;
import com.mygdx.game.pool_managers.EnemyBulletsManager;
import com.mygdx.game.utilities_resources.Assets;

public class EnemyBoss extends Enemy {


    private static final float BULLET_SPEED = 150.0f;
    private static final float SCALE = 1.7f;
    private static final float FIRE_ANGLE_STEP = 15.0f;
    private float shootDelayTimer = 0f;
    private boolean isPreparingToShoot = false;
    private boolean isInvincible = false;
    private float invincibilityTimer = 0f;
    private static final float INVINCIBILITY_DURATION = 2.5f;

    public EnemyBoss(Vector2 position, Vector2 playerPosition, float health, Assets assets, Integer soundVolume, Integer critRate , GameScene.GameMode gameMode , int mapIndex) {
        init(position, playerPosition, health, assets, soundVolume, critRate , gameMode , mapIndex);
        setSizeScale(SCALE);
        setPUSH_BACK_FORCE(0f);
        setIsAttacked(true);
    }

    @Override
    public void update(float deltaTime, EnemyBulletsManager enemyBulletsManager, boolean isPaused, com.badlogic.gdx.utils.Array<Enemy> enemies, int mapIndex, RayHandler rayHandler) {
        super.update(deltaTime, enemyBulletsManager, isPaused, enemies, mapIndex, rayHandler);

        if (isInvincible) {
            invincibilityTimer += deltaTime;
            if (invincibilityTimer >= INVINCIBILITY_DURATION) {
                isInvincible = false;
            }
        }
    }
    @Override
    public void render(SpriteBatch batch) {
        if (isInvincible && ((int)(getStateTime() * 10) % 2 == 0)) {
            batch.setColor(1, 1, 1, 0.3f);
        }
        super.render(batch);
        batch.setColor(1, 1, 1, 1);
    }

    @Override
    protected void simpleAI(float deltaTime, EnemyBulletsManager enemyBulletsManager, int mapIndex , RayHandler rayHandler) {

        shootDelayTimer += deltaTime;

        switch (mapIndex) {
            case 0:
                if (!isPreparingToShoot && getStateTime() % 3 < deltaTime) {
                    isPreparingToShoot = true;
                    shootDelayTimer = 0f;
                    setBehaviorStatus(BehaviorStatus.IDLE);
                }

                if (isPreparingToShoot) {
                    setBehaviorStatus(BehaviorStatus.IDLE);
                    if (shootDelayTimer >= 1f) {
                        shootBullet(enemyBulletsManager, mapIndex ,rayHandler);
                        isPreparingToShoot = false;
                    }
                } else {
                    moveTowards(getPlayerPosition(), deltaTime);
                    setBehaviorStatus(BehaviorStatus.MOVING);
                }
                break;

            case 1:
                if (!isPreparingToShoot && getStateTime() % 3 < deltaTime) {
                    isPreparingToShoot = true;
                    shootDelayTimer = 0f;
                    setBehaviorStatus(BehaviorStatus.IDLE);
                }

                if (isPreparingToShoot) {
                    setBehaviorStatus(BehaviorStatus.IDLE);
                    if (shootDelayTimer >= 1f) {
                        shootForwardBurst(enemyBulletsManager);
                        isPreparingToShoot = false;
                    }
                } else if ((int) getStateTime() % 10 == 0) {
                    triggerInvincibility();
                    setBehaviorStatus(BehaviorStatus.MOVING);
                } else {
                    moveTowards(getPlayerPosition(), deltaTime);
                    setBehaviorStatus(BehaviorStatus.MOVING);
                }
                break;

            case 2:
                if (!isPreparingToShoot && (int) getStateTime() % 3 == 0) {
                    isPreparingToShoot = true;
                    shootDelayTimer = 0f;
                    setBehaviorStatus(BehaviorStatus.IDLE);
                }
                if (isPreparingToShoot) {
                    setBehaviorStatus(BehaviorStatus.IDLE);
                    if (shootDelayTimer >= 1f) {
                        if (Math.random() > 0.5) {
                            areaSpray(enemyBulletsManager, rayHandler);
                        } else {
                            triggerInvincibility();
                        }
                        isPreparingToShoot = false;
                    }
                } else {
                    moveTowards(getPlayerPosition(), deltaTime);
                    setBehaviorStatus(BehaviorStatus.MOVING);
                }
                break;
        }
    }

    private void triggerInvincibility() {
        isInvincible = true;
        invincibilityTimer = 0f;
    }

    private void shootForwardBurst(EnemyBulletsManager manager) {
        Vector2 dir = getPlayerDirection().nor().scl(BULLET_SPEED);
        Vector2 pos = getBodyHitbox().getCenter(new Vector2());
        for (int i = -1; i <= 1; i++) {
            Vector2 spread = dir.cpy().rotateDeg(i * 10);
            manager.generateBullet(pos.cpy(), spread, 1, getAssets(), getSoundVolume(), 1 , null);
        }
    }

    private void areaSpray(EnemyBulletsManager manager , RayHandler rayHandler) {
        shootBullet(manager, 2 , rayHandler);
    }

    private Vector2 getPlayerDirection() {
        return new Vector2(getPlayerPosition()).sub(getPosition());
    }

    @Override
    protected void loadEnemyTextures(int mapIndex) {
        if( mapIndex == 0 ){
            setWalkTexture(getAssets().getAssetManager().get(Assets.bossTexture));
            setIdleTexture(getAssets().getAssetManager().get(Assets.idleBossTexture));
            TextureRegion[] walkingFrames = splitEnemyTexture(getWalkTexture(), 6 ,32 ,32);
            TextureRegion[] idleFrames = splitEnemyTexture(getIdleTexture(), 4,32,32);
            setWalkAnimation(new Animation<>(0.1f, walkingFrames));
            setIdleAnimation(new Animation<>(0.1f, idleFrames));
            setSound(getAssets().getAssetManager().get(Assets.duckSound));
        } else if( mapIndex == 1 ){
            setWalkTexture(getAssets().getAssetManager().get(Assets.skeletonBossWalkTexture));
            setIdleTexture(getAssets().getAssetManager().get(Assets.skeletonBossIdleTexture));
            TextureRegion[] walkingFrames = splitEnemyTexture(getWalkTexture(), 4 ,48 ,48);
            TextureRegion[] idleFrames = splitEnemyTexture(getIdleTexture(), 6,48,48);
            setWalkAnimation(new Animation<>(0.1f, walkingFrames));
            setIdleAnimation(new Animation<>(0.1f, idleFrames));
            setSound(getAssets().getAssetManager().get(Assets.skeletonSound));
        } else {
            setWalkTexture(getAssets().getAssetManager().get(Assets.zombieBossWalkTexture));
            setIdleTexture(getAssets().getAssetManager().get(Assets.zombieBossIdleTexture));
            TextureRegion[] walkingFrames = splitEnemyTexture(getWalkTexture(), 4 ,48 ,48);
            TextureRegion[] idleFrames = splitEnemyTexture(getIdleTexture(), 6,48,48);
            setWalkAnimation(new Animation<>(0.1f, walkingFrames));
            setIdleAnimation(new Animation<>(0.1f, idleFrames));
            setSound(getAssets().getAssetManager().get(Assets.zombieSound));
        }
    }


    @Override
    public void shootBullet(EnemyBulletsManager enemyBulletsManager , int mapIndex , RayHandler rayHandler) {
            Vector2 bulletPosition = new Vector2(getBodyHitbox().x + getBodyHitbox().width / 2, getBodyHitbox().y + getBodyHitbox().height / 2);
            for (float angle = 0; angle < 360; angle += FIRE_ANGLE_STEP) {
                Vector2 direction = new Vector2(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle)).scl(BULLET_SPEED);
                enemyBulletsManager.generateBullet(bulletPosition.cpy(), direction, 1, getAssets(), getSoundVolume() , mapIndex ,rayHandler);
            }
    }

    @Override
    protected void renderHealthBar(SpriteBatch batch){
    }

    @Override
    public void takeDamage(float damage) {
        if (isInvincible) return;
        super.takeDamage(damage);
    }

}