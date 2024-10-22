package com.mygdx.game.ai;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.mygdx.game.Enemy;

public class ShootTask extends LeafTask<Enemy> {
    @Override
    public Status execute() {
        getObject().shootBullet();
        getObject().setBehaviorStatus(Enemy.BehaviorStatus.IDLE);
        getObject().setIsFlipped(getObject().getPlayerPosition().x < getObject().getPosition().x);
        return Status.SUCCEEDED;
    }

    @Override
    protected Task<Enemy> copyTo(Task<Enemy> task) {
        return task;
    }
}