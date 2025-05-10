package com.mygdx.game.cutscene;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class FadeEndEvent implements CutsceneEvent {

    private final Image blackOverlay;
    private boolean complete = false;
    private boolean started = false;
    private final float fadeInDuration;
    private final float waitDuration;

    public FadeEndEvent(Stage stage, Texture blackPixel, float fadeInDuration, float waitDuration ) {
        this.fadeInDuration = fadeInDuration;
        this.waitDuration = waitDuration;
        blackOverlay = new Image(new TextureRegionDrawable(blackPixel));
        blackOverlay.getColor().a = 0f;
        stage.addActor(blackOverlay);
    }

    @Override
    public boolean update(float delta) {
        if (!started) {
            blackOverlay.addAction(Actions.sequence(
                    Actions.fadeIn(fadeInDuration),
                    Actions.delay(waitDuration),
                    Actions.run(() -> complete = true)
            ));
            started = true;
        }

        blackOverlay.setPosition(0, 0);
        blackOverlay.setSize(2000, 2000);
        return complete;
    }

    @Override
    public void skip() {
        complete = true;
    }


    @Override
    public boolean isComplete() {
        return complete;
    }
}
