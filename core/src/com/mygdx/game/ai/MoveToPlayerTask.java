package com.mygdx.game.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Enemy;

public class MoveToPlayerTask extends LeafTask<Enemy> {

    @Override
    public Status execute() {
        Enemy enemy = getObject();
        Vector2 playerPosition = enemy.getPlayerPosition();
        Vector2 enemyPosition = enemy.getPosition();
        enemy.setBehaviorStatus(Enemy.BehaviorStatus.MOVING);

        if (enemyPosition.dst(playerPosition) > 150f) {
            Vector2 direction = playerPosition.cpy().sub(enemyPosition).nor();
            enemy.getPosition().add(direction.scl(Enemy.MOVEMENT_SPEED * Gdx.graphics.getDeltaTime()));
            return Status.RUNNING;
        } else {
            return Status.SUCCEEDED;
        }
    }

    @Override
    protected Task<Enemy> copyTo(Task<Enemy> task) {
        return task;
    }
}
