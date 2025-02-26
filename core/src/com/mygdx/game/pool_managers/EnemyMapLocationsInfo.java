package com.mygdx.game.pool_managers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.utilities_resources.EnemyBasicInfo;

public class EnemyMapLocationsInfo {
    private final Array<EnemyBasicInfo> enemies;
    private final Vector2 npcPosition;

    public EnemyMapLocationsInfo(Vector2 npcPosition){
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
