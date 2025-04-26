package com.mygdx.game.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entities.enemy.Enemy;

public class WanderLeftTask extends LeafTask<Enemy> {
    private static final float WANDER_DISTANCE = 5.0f;
    private static final float SPEED = Enemy.MOVEMENT_SPEED;

    @Override
    public Status execute() {
        Enemy enemy = getObject();
        Vector2 spawnPosition = enemy.getSpawnPosition();
        enemy.setIsFlipped(true);
        enemy.setBehaviorStatus(Enemy.BehaviorStatus.MOVING);
        // Determine target position to move left
        float targetX = spawnPosition.x - WANDER_DISTANCE;

        // Move towards target position
        if (enemy.getPosition().x > targetX) {
            enemy.getPosition().x -= SPEED * Gdx.graphics.getDeltaTime();
            return Status.RUNNING;
        }

        enemy.getPosition().x = targetX;
        return Status.SUCCEEDED;
    }

    @Override
    protected Task<Enemy> copyTo(Task<Enemy> task) {
        return task;
    }
}