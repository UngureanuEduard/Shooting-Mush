package com.mygdx.game.cutscene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class FadeOutTextEvent implements CutsceneEvent {
    private final Stage stage;
    private final Label label;
    private final Image blackOverlay;
    private boolean complete = false;
    private final OrthographicCamera camera;


    public FadeOutTextEvent(Stage stage, Skin skin, String text, Texture blackPixel, float fadeInDuration, float displayDuration, float fadeOutDuration) {
        this.stage = stage;
        this.camera = (OrthographicCamera) stage.getCamera();

        blackOverlay = new Image(new TextureRegionDrawable(blackPixel));
        blackOverlay.setSize(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        blackOverlay.getColor().a = 1f;

        label = new Label(text, skin);
        label.setFontScale(0.4f);
        label.setColor(new Color(1, 1, 1, 0));

        float centerX = stage.getViewport().getWorldWidth() / 2f;
        float centerY = stage.getViewport().getWorldHeight() / 2f;
        label.setPosition(centerX - label.getPrefWidth() / 2f, centerY - label.getPrefHeight() / 2f);

        label.addAction(Actions.sequence(
                Actions.fadeIn(fadeInDuration),
                Actions.delay(displayDuration),
                Actions.fadeOut(fadeOutDuration)
        ));

        float totalTextDuration = fadeInDuration + displayDuration + fadeOutDuration;
        blackOverlay.addAction(Actions.sequence(
                Actions.delay(totalTextDuration),
                Actions.fadeOut(1f),
                Actions.run(() -> complete = true)
        ));

        stage.addActor(blackOverlay);
        stage.addActor(label);
    }

    @Override
    public boolean update(float delta) {
        CutsceneUtils.centerOverlayAndLabel(camera, stage, blackOverlay, label);
        return complete;
    }

    @Override
    public void skip() {
        complete = true;
        blackOverlay.remove();
        label.remove();
    }



    @Override
    public boolean isComplete() {
        return complete;
    }
}
