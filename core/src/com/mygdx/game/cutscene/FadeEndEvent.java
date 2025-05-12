package com.mygdx.game.cutscene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class FadeEndEvent implements CutsceneEvent {

    private final Image blackOverlay;
    private final Label label;
    private boolean complete = false;
    private boolean started = false;
    private final float fadeInBlackDuration;
    private final float textFadeDuration;
    private final float textDisplayDuration;
    private final OrthographicCamera camera;
    private final Stage stage;

    public FadeEndEvent(Stage stage, Skin skin, Texture blackPixel,
                        float fadeInBlackDuration, float textFadeDuration, float textDisplayDuration, String text) {
        this.stage = stage;
        this.fadeInBlackDuration = fadeInBlackDuration;
        this.textFadeDuration = textFadeDuration;
        this.textDisplayDuration = textDisplayDuration;
        this.camera = (OrthographicCamera) stage.getCamera();

        blackOverlay = new Image(new TextureRegionDrawable(blackPixel));
        blackOverlay.getColor().a = 0f;
        blackOverlay.setSize(2000, 2000);
        stage.addActor(blackOverlay);

        label = new Label(text, skin);
        label.setFontScale(0.30f);
        label.setColor(new Color(1, 1, 1, 0));
        stage.addActor(label);
    }

    @Override
    public boolean update(float delta) {
        if (!started) {
            blackOverlay.addAction(Actions.sequence(
                    Actions.fadeIn(fadeInBlackDuration),
                    Actions.run(() -> label.addAction(Actions.sequence(
                            Actions.fadeIn(textFadeDuration),
                            Actions.delay(textDisplayDuration),
                            Actions.fadeOut(textFadeDuration),
                            Actions.run(() -> complete = true)
                    )))
            ));
            started = true;
        }

        CutsceneUtils.centerOverlayAndLabel(camera, stage, blackOverlay, label);
        return complete;
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    @Override
    public void skip() {
        complete = true;
        blackOverlay.remove();
        label.remove();
    }
}
