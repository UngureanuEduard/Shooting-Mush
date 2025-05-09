package com.mygdx.game.cutscene;

public interface CutsceneEvent {
    boolean update(float delta);
    boolean isComplete();
}
