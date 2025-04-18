package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.GameScene;
import com.mygdx.game.utilities_resources.Assets;

public class EnemyBoss extends Enemy {


    private static final float BULLET_SPEED = 150.0f;
    private static final float BULLET_COOLDOWN = 1.0f;
    private static final float SCALE = 1.7f;
    private static final float FIRE_ANGLE_STEP = 15.0f;

    public EnemyBoss(Vector2 position, Vector2 playerPosition, float health, Assets assets, Integer soundVolume, Integer critRate , GameScene.GameMode gameMode) {
        init(position, playerPosition, health, assets, soundVolume, critRate , gameMode);
        sizeScale = SCALE;
        PUSH_BACK_FORCE = 0f;
    }

    @Override
    protected void loadEnemyTextures() {
        duckTexture = assets.getAssetManager().get(Assets.bossTexture);
        idleTexture = assets.getAssetManager().get(Assets.idleBossTexture);
    }


    @Override
    public void shootBullet() {
        if (shootTimer >= BULLET_COOLDOWN && behaviorStatus == BehaviorStatus.IDLE) {
            shootTimer = 0;

            Vector2 bulletPosition = new Vector2(bodyHitbox.x + bodyHitbox.width / 2, bodyHitbox.y + bodyHitbox.height / 2);

            for (float angle = 0; angle < 360; angle += FIRE_ANGLE_STEP) {
                Vector2 direction = new Vector2(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle)).scl(BULLET_SPEED);
                enemyBulletsManager.generateBullet(bulletPosition.cpy(), direction, 1, assets, soundVolume);
            }
        }
    }

    @Override
    protected void renderHealthBar(SpriteBatch batch){
    }
}