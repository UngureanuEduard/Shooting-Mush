package com.mygdx.game.ai;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.mygdx.game.entities.enemy.Enemy;

public class IsAttackedCondition extends LeafTask<Enemy> {
    @Override
    public Status execute() {
        if (getObject().getIsAttacked()) {
            return Status.SUCCEEDED;
        }
        return Status.FAILED;
    }

    @Override
    protected Task<Enemy> copyTo(Task<Enemy> task) {
        return task;
    }
}