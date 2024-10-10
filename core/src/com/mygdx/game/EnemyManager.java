package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class EnemyManager {

    private final Array<Enemy> activeEnemies = new Array<>();

    private final Pool<Enemy> enemiesPool = new Pool<>() {
        @Override
        protected Enemy newObject() {
            return new Enemy();
        }
    };

    private Boolean scaled = false;
    private int score = 0;

    public EnemyManager() {

    }
    public void fillPool(int amount){
        enemiesPool.fill(amount);
    }
    public void spawnEnemy(Vector2 enemyPosition , Vector2 playerPosition, float health, Assets assets, Integer soundVolume, Integer critRate){
        Enemy item = enemiesPool.obtain();
        item.init(enemyPosition, playerPosition, health, assets, soundVolume,critRate);
        activeEnemies.add(item);
    }

    public Array<Enemy> getActiveEnemies() {
        return activeEnemies;
    }
    public void dispose() {
        activeEnemies.clear();
        enemiesPool.clear();
    }

    public void updateAndRender(SpriteBatch batch , OrthographicCamera camera , EnemyBulletsManager enemyBulletsManager , CharacterBulletsManager characterBulletsManager , boolean isPaused , int enemiesLeftToKill, ParticleEffectsManager particleEffects){
        for (Enemy enemy : activeEnemies) {
            enemy.update(Gdx.graphics.getDeltaTime(), enemyBulletsManager, characterBulletsManager.getActiveCharacterBullets(), isPaused, activeEnemies );
            if (!enemy.isAlive()) {
                Vector2 poz = new Vector2(enemy.getPosition());
                particleEffects.DeathParticles(poz, enemy.getSizeScale() , scaled);
                scaled = true;
                score = enemy instanceof EnemyBoss ? +50 : +5;
                enemiesLeftToKill -= 1;
                activeEnemies.removeValue(enemy, true);
                enemiesPool.free(enemy);

            } else {
                enemy.render(batch, camera);
                batch.begin();
            }
        }
    }

    public void setScaled(Boolean scaled) {
        this.scaled = scaled;
    }

    public int getScore() {
        return this.score;
    }

}
