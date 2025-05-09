package com.mygdx.game.cutscene;

public class WaitEvent implements CutsceneEvent {
    private final float duration;
    private float elapsed = 0;
    private boolean complete = false;

    public WaitEvent(float duration) {
        this.duration = duration;
    }

    @Override
    public boolean update(float delta) {
        if (complete) return true;

        elapsed += delta;
        if (elapsed >= duration) {
            complete = true;
        }
        return complete;
    }

    @Override
    public boolean isComplete() {
        return complete;
    }
}

