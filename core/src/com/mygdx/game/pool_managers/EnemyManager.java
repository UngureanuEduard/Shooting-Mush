    package com.mygdx.game.pool_managers;

    import com.badlogic.gdx.Gdx;
    import com.badlogic.gdx.graphics.g2d.SpriteBatch;
    import com.badlogic.gdx.math.Vector2;
    import com.badlogic.gdx.utils.Array;
    import com.badlogic.gdx.utils.JsonReader;
    import com.badlogic.gdx.utils.JsonValue;
    import com.badlogic.gdx.utils.Pool;
    import com.mygdx.game.utilities_resources.Assets;
    import com.mygdx.game.entities.enemy.Enemy;
    import com.mygdx.game.entities.enemy.EnemyBoss;
    import com.mygdx.game.GameScene;
    import com.mygdx.game.utilities_resources.EnemyBasicInfo;

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
        public void spawnEnemy(Vector2 enemyPosition, Vector2 playerPosition, float health, Assets assets,
                               Integer soundVolume, Integer critRate, int mapIndex) {
            Enemy item = enemiesPool.obtain();

            if (item instanceof EnemyBoss) {
               item = new Enemy();
            }

            item.init(enemyPosition, playerPosition, health, assets, soundVolume, critRate, gameMode, mapIndex);
            activeEnemies.add(item);
        }

        public Array<Enemy> getActiveEnemies() {
            return activeEnemies;
        }
        public void dispose() {
            activeEnemies.clear();
            enemiesPool.clear();
        }

        public int updateAndRender(SpriteBatch batch , EnemyBulletsManager enemyBulletsManager , CharacterBulletsManager characterBulletsManager , boolean isPaused , int enemiesLeftToKill, ParticleEffectsManager particleEffects , int mapIndex){
            for (int i = activeEnemies.size - 1; i >= 0; i--) {
                Enemy enemy = activeEnemies.get(i);
                enemy.update(Gdx.graphics.getDeltaTime(), enemyBulletsManager, characterBulletsManager.getActiveCharacterBullets(), isPaused, activeEnemies ,  mapIndex);
                if (!enemy.isAlive() && !enemy.isMarkedForRemoval()) {
                    Vector2 poz = new Vector2(enemy.getPosition());
                    particleEffects.DeathParticles(poz, enemy.getSizeScale(), scaled);
                    scaled = true;
                    score += (enemy instanceof EnemyBoss) ? 50 : 5;
                    enemiesLeftToKill--;
                    enemy.setMarkedForRemoval(true);
                }

                if (enemy.isMarkedForRemoval()) {
                    activeEnemies.removeIndex(i);
                    enemiesPool.free(enemy);
                } else {
                    enemy.render(batch);
                }
            }

            return enemiesLeftToKill;
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
                JsonValue npcLocation = map.get("npcLocation");
                float frogX = npcLocation.getFloat("x");
                float frogY = npcLocation.getFloat("y");
                Vector2 npcPosition = new Vector2(frogX, frogY);
                EnemyMapLocationsInfo mapInfo = new EnemyMapLocationsInfo(npcPosition);
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
