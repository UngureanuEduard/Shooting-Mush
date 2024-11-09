package com.mygdx.game.poolmanagers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class EnemyMapLocationsInfo {
    private final String mapName;
    private final Array<EnemyBasicInfo> enemies;
    private final Vector2 npcPosition;

    public EnemyMapLocationsInfo(String mapName , Vector2 npcPosition){
        this.mapName = mapName;
        enemies = new Array<>();
        this.npcPosition = npcPosition;
    }
    public Vector2 getNpcPosition() {
        return npcPosition;
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
