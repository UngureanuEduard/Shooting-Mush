package com.mygdx.game.cutscene;

import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class RemoveImageEvent implements CutsceneEvent {

    private final Image image;
    private boolean complete = false;

    public RemoveImageEvent(Image image) {
        this.image = image;
    }

    @Override
    public boolean update(float delta) {
        if (!complete) {
            image.remove();
            complete = true;
        }
        return true;
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    @Override
    public void skip() {
        complete = true;
        image.remove();
    }
}
