package com.mygdx.game.ai;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.mygdx.game.entities.Enemy;

public class IsPlayerInRangeCondition extends LeafTask<Enemy> {
    private static final float RANGE = 200.0f;

    @Override
    public Status execute() {
        if (getObject().getPlayerPosition().dst(getObject().getPosition()) <= RANGE) {
            getObject().setIsAttacked(true);
            return Status.SUCCEEDED;
        }
        return Status.FAILED;
    }

    @Override
    protected Task<Enemy> copyTo(Task<Enemy> task) {
        return task;
    }
}