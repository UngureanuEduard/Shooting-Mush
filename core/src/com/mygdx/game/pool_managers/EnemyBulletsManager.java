package com.mygdx.game.pool_managers;

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
        for (EnemyBullet enemyBullet : activeEnemyBullets) {
            enemyBullet.update(Gdx.graphics.getDeltaTime());
            if (enemyBullet.getAlive_n()) {
                activeEnemyBullets.removeValue(enemyBullet, true);
                enemyBulletPool.free(enemyBullet);
            } else {
                enemyBullet.render(batch);
            }
        }
    }

    public void generateBullet(Vector2 bulletStartPosition, Vector2 directionToCursor, float damage, Assets assets, Integer soundVolume){
        EnemyBullet item = enemyBulletPool.obtain();
        item.init(bulletStartPosition, directionToCursor, damage, assets, soundVolume);
        activeEnemyBullets.add(item);
    }
}
