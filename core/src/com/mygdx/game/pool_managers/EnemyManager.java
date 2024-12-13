package com.mygdx.game.pool_managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.utilities_resources.Assets;
import com.mygdx.game.Enemy;
import com.mygdx.game.EnemyBoss;
import com.mygdx.game.GameScene;

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
    private final GameScene.GameMode gameMode;
    private final Array<EnemyMapLocationsInfo> mapInfos = new Array<>();

    public EnemyManager(GameScene.GameMode gameMode) {
    this.gameMode = gameMode;
    }
    public void fillPool(int amount){
        enemiesPool.fill(amount);
    }
    public void spawnEnemy(Vector2 enemyPosition , Vector2 playerPosition, float health, Assets assets, Integer soundVolume, Integer critRate){
        Enemy item = enemiesPool.obtain();
        item.init(enemyPosition, playerPosition, health, assets, soundVolume,critRate ,gameMode);
        activeEnemies.add(item);
    }

    public Array<Enemy> getActiveEnemies() {
        return activeEnemies;
    }
    public void dispose() {
        activeEnemies.clear();
        enemiesPool.clear();
    }

    public void updateAndRender(SpriteBatch batch , EnemyBulletsManager enemyBulletsManager , CharacterBulletsManager characterBulletsManager , boolean isPaused , int enemiesLeftToKill, ParticleEffectsManager particleEffects){
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
                enemy.render(batch);
            }
        }
    }

    public void setScaled(Boolean scaled) {
        this.scaled = scaled;
    }

    public int getScore() {
        return this.score;
    }

    public void loadEnemiesFromJson(String filePath) {
        JsonReader json = new JsonReader();
        JsonValue base = json.parse(Gdx.files.internal(filePath));

        for (JsonValue map : base.get("maps")) {
            String mapName = map.getString("mapName");
            JsonValue npcLocation = map.get("npcLocation");
            float frogX = npcLocation.getFloat("x");
            float frogY = npcLocation.getFloat("y");
            Vector2 npcPosition = new Vector2(frogX, frogY);
            EnemyMapLocationsInfo mapInfo = new EnemyMapLocationsInfo(mapName , npcPosition);
            for (JsonValue enemy : map.get("enemies")) {
                float x = enemy.getFloat("x");
                float y = enemy.getFloat("y");
                String type = enemy.getString("type");
                mapInfo.addEnemy(new EnemyBasicInfo(new Vector2(x, y), type));
            }
            mapInfos.add(mapInfo);
        }
    }

    public Array<EnemyMapLocationsInfo> getEnemyMapLocationsInfos() {
        return mapInfos;
    }

}
