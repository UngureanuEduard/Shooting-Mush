package com.mygdx.game.cutscene;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.entities.character.Sprite;

public class ScreenFadeEvent implements CutsceneEvent {

    private final Image blackOverlay;
    private boolean complete = false;
    private boolean started = false;
    private final float fadeInDuration, waitDuration, fadeOutDuration;

    private final Sprite sprite1, sprite2;
    private final Vector2 teleportPos1, teleportPos2;

    public ScreenFadeEvent(Stage stage, Texture blackPixel,
                           float fadeInDuration, float waitDuration, float fadeOutDuration,
                           Sprite sprite1, Vector2 teleportPos1,
                           Sprite sprite2, Vector2 teleportPos2) {

        this.fadeInDuration = fadeInDuration;
        this.waitDuration = waitDuration;
        this.fadeOutDuration = fadeOutDuration;
        this.sprite1 = sprite1;
        this.teleportPos1 = teleportPos1;
        this.sprite2 = sprite2;
        this.teleportPos2 = teleportPos2;

        blackOverlay = new Image(new TextureRegionDrawable(blackPixel));
        blackOverlay.getColor().a = 0f;
        stage.addActor(blackOverlay);
    }

    @Override
    public boolean update(float delta) {
        if (!started) {
            blackOverlay.addAction(Actions.sequence(
                    Actions.fadeIn(fadeInDuration),
                    Actions.delay(waitDuration/5),
                    Actions.run(() -> {
                        if (sprite1 != null && teleportPos1 != null) sprite1.setPosition(teleportPos1);
                        if (sprite2 != null && teleportPos2 != null) sprite2.setPosition(teleportPos2);
                    }),
                    Actions.delay(waitDuration),
                    Actions.fadeOut(fadeOutDuration),
                    Actions.run(() -> complete = true)
            ));
            started = true;
        }

        blackOverlay.setPosition(0 , 0);
        blackOverlay.setSize(2000, 2000);

        return complete;
    }

    @Override
    public void skip() {
        complete = true;
        blackOverlay.remove();
    }


    @Override
    public boolean isComplete() {
        return complete;
    }
}
