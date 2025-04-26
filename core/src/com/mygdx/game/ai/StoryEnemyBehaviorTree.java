package com.mygdx.game.ai;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.mygdx.game.entities.enemy.Enemy;

public class StoryEnemyBehaviorTree extends BehaviorTree<Enemy> {
    public StoryEnemyBehaviorTree(Enemy enemy) {
        super(buildTree(), enemy);
    }
    @SuppressWarnings("unchecked")
    private static Task<Enemy> buildTree() {
        return new Selector<>(
                new Sequence<>(
                        new IsPlayerInRangeCondition(),
                        new ShootTask()
                ),
                new Sequence<>(
                        new IsAttackedCondition(),
                        new Selector<>(
                                new Sequence<>(
                                        new MoveTowardsPlayerTask(),
                                        new IsPlayerInRangeCondition(),
                                        new ShootTask()
                                ),
                                new ShootTask()
                        )
                ),
                new Sequence<>(
                        new WanderLeftTask(),
                        new WanderRightTask()
                )
        );
    }
}