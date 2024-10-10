package com.mygdx.game.ai;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Enemy;

public class ShootPlayerTask extends LeafTask<Enemy> {

    @Override
    public Status execute() {
        Enemy enemy = getObject();
        Vector2 playerPosition = enemy.getPlayerPosition();
        Vector2 enemyPosition = enemy.getPosition();
        enemy.setBehaviorStatus(Enemy.BehaviorStatus.IDLE);

        if (enemyPosition.dst(playerPosition) <= 150f) {
            enemy.shootBullet();
            return Status.RUNNING;
        } else {
            return Status.FAILED;
        }
    }

    @Override
    protected Task<Enemy> copyTo(Task<Enemy> task) {
        return task;
    }
}