package com.mygdx.game.poolmanagers;

import com.badlogic.gdx.utils.Array;

public class EnemyMapLocationsInfo {
    private final String mapName;
    private final Array<EnemyBasicInfo> enemies;

    public EnemyMapLocationsInfo(String mapName){
        this.mapName = mapName;
        enemies = new Array<>();
    }
    public String getMapName() {
        return mapName;
    }

    public Array<EnemyBasicInfo> getEnemies() {
        return enemies;
    }

    public void addEnemy(EnemyBasicInfo enemyBasicInfo) {
        enemies.add(enemyBasicInfo);
    }
    public void removeEnemy(EnemyBasicInfo enemyInfo) {
         enemies.removeValue(enemyInfo, true);
    }
}
