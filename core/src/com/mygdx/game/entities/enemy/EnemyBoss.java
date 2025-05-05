package com.mygdx.game.entities.enemy;

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

    public EnemyBoss(Vector2 position, Vector2 playerPosition, float health, Assets assets, Integer soundVolume, Integer critRate , GameScene.GameMode gameMode , int mapIndex) {
        init(position, playerPosition, health, assets, soundVolume, critRate , gameMode , mapIndex);
        setSizeScale(SCALE);
        setPUSH_BACK_FORCE(0f);
    }

    @Override
    protected void loadEnemyTextures(int mapIndex) {
        setWalkTexture(getAssets().getAssetManager().get(Assets.bossTexture));
        setIdleTexture(getAssets().getAssetManager().get(Assets.idleBossTexture));
        TextureRegion[] walkingFrames = splitEnemyTexture(getWalkTexture(), 6 ,32 ,32);
        TextureRegion[] idleFrames = splitEnemyTexture(getIdleTexture(), 4,32,32);
        setWalkAnimation(new Animation<>(0.1f, walkingFrames));
        setIdleAnimation(new Animation<>(0.1f, idleFrames));
    }


    @Override
    public void shootBullet(EnemyBulletsManager enemyBulletsManager ,int mapIndex) {
            Vector2 bulletPosition = new Vector2(getBodyHitbox().x + getBodyHitbox().width / 2, getBodyHitbox().y + getBodyHitbox().height / 2);
            for (float angle = 0; angle < 360; angle += FIRE_ANGLE_STEP) {
                Vector2 direction = new Vector2(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle)).scl(BULLET_SPEED);
                enemyBulletsManager.generateBullet(bulletPosition.cpy(), direction, 1, getAssets(), getSoundVolume() , 0);
            }
    }

    @Override
    protected void renderHealthBar(SpriteBatch batch){
    }
}