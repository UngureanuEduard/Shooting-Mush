package com.mygdx.game.ai;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.BranchTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.mygdx.game.Enemy;

public class EnemyBehaviorTree {

    private final BehaviorTree<Enemy> behaviorTree;

    public EnemyBehaviorTree(Enemy enemy) {
        // Create tasks
        Task<Enemy> moveToPlayer = new MoveToPlayerTask();
        Task<Enemy> shootPlayer = new ShootPlayerTask();

        // Build BehaviorTree
        Sequence<Enemy> sequence = new Sequence<>();
        sequence.addChild(moveToPlayer);
        sequence.addChild(shootPlayer);

        BranchTask<Enemy> selector = new Selector<>();
        selector.addChild(sequence);

        behaviorTree = new BehaviorTree<>(selector, enemy);
    }

    public void update() {
        behaviorTree.step();
    }
}