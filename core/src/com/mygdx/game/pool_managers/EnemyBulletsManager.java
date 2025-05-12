package com.mygdx.game.pool_managers;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.utilities_resources.Assets;
import com.mygdx.game.combat_system.EnemyBullet;

public class EnemyBulletsManager {

    private final Array<EnemyBullet> activeEnemyBullets = new Array<>();

    private final Pool<EnemyBullet> enemyBulletPool = new Pool<>() {
        @Override
        protected EnemyBullet newObject() {
            return new EnemyBullet();
        }
    };

    public EnemyBulletsManager() {
    }

    public void fillPool(int amount){
        enemyBulletPool.fill(amount);
    }
    public void dispose() {
        activeEnemyBullets.clear();
        enemyBulletPool.clear();
    }

    public Array<EnemyBullet> getActiveEnemyBullets() {
        return activeEnemyBullets;
    }

    public void updateAndRender(SpriteBatch batch){
        for (int i = activeEnemyBullets.size - 1; i >= 0; i--) {
            EnemyBullet enemyBullet = activeEnemyBullets.get(i);
            enemyBullet.update(Gdx.graphics.getDeltaTime());
            if (enemyBullet.isAlive()) {
                enemyBullet.render(batch);
            } else {
                activeEnemyBullets.removeIndex(i);
                enemyBulletPool.free(enemyBullet);
            }
        }
    }

    public void generateBullet(Vector2 bulletStartPosition, Vector2 playerPosition, float damage, Assets assets, Integer soundVolume , int mapIndex , RayHandler rayHandler){
        EnemyBullet item = enemyBulletPool.obtain();
        item.init(bulletStartPosition, playerPosition, damage, assets, soundVolume , mapIndex , rayHandler);
        activeEnemyBullets.add(item);
    }
}
