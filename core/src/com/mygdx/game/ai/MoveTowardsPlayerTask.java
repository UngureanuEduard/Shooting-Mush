package com.mygdx.game.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entities.enemy.Enemy;

public class MoveTowardsPlayerTask extends LeafTask<Enemy> {
    private static final float SPEED = Enemy.MOVEMENT_SPEED;

    @Override
    public Status execute() {
        Enemy enemy = getObject();
        Vector2 playerPosition = enemy.getPlayerPosition();
        Vector2 direction = new Vector2(playerPosition).sub(enemy.getPosition()).nor();
        enemy.setBehaviorStatus(Enemy.BehaviorStatus.MOVING);
        enemy.getPosition().add(direction.scl(SPEED*3 * Gdx.graphics.getDeltaTime()));
        enemy.setIsFlipped(enemy.getPlayerPosition().x < enemy.getPosition().x);

        if (enemy.getPlayerPosition().dst(enemy.getPosition()) <= 100.0f) {
            return Status.SUCCEEDED;
        }

        return Status.RUNNING;
    }

    @Override
    protected Task<Enemy> copyTo(Task<Enemy> task) {
        return task;
    }
}