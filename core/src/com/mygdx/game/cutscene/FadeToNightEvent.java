package com.mygdx.game.cutscene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class FadeToNightEvent implements CutsceneEvent {
    private final Image darkOverlay;
    private boolean complete = false;
    private boolean started = false;
    private final float fadeDuration;
    private final OrthographicCamera camera;
    private final Stage stage;

    public FadeToNightEvent(Stage stage, Texture blackPixel, float fadeDuration ) {
        this.stage = stage;
        this.fadeDuration = fadeDuration;
        this.camera = (OrthographicCamera) stage.getCamera();

        darkOverlay = new Image(new TextureRegionDrawable(blackPixel));
        darkOverlay.setSize(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        darkOverlay.setColor(new Color(0f, 0f, 0f, 0f));

        stage.addActor(darkOverlay);
    }

    @Override
    public boolean update(float delta) {
        if (!started) {
            darkOverlay.addAction(Actions.alpha(0.5f, fadeDuration));
            started = true;
            complete = true;
        }

        CutsceneUtils.centerOverlay(camera, stage, darkOverlay);
        return complete;
    }

    @Override
    public void skip() {
        darkOverlay.getColor().a = 0.5f;
        complete = true;
    }

    @Override
    public boolean isComplete() {
        return complete;
    }
}
