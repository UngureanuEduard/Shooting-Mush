package com.mygdx.game.cutscene;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class ImageDisplayEvent implements CutsceneEvent {
    private final Stage stage;
    private final Image image;
    private final float fadeInDuration;
    private final float displayDuration;
    private final float fadeOutDuration;
    private float timer = 0f;
    private boolean added = false;
    private boolean complete = false;

    public ImageDisplayEvent(Stage stage, Texture texture, float fadeInDuration, float displayDuration, float fadeOutDuration) {
        this.stage = stage;
        this.fadeInDuration = fadeInDuration;
        this.displayDuration = displayDuration;
        this.fadeOutDuration = fadeOutDuration;

        image = new Image(new TextureRegionDrawable(new TextureRegion(texture)));
        image.setColor(1, 1, 1, 0);
    }

    @Override
    public boolean update(float delta) {
        if (!added) {
            OrthographicCamera cam = (OrthographicCamera) stage.getCamera();
            float camX = cam.position.x;
            float camY = cam.position.y;
            image.setSize((float) (cam.viewportWidth/3.5), (float) (cam.viewportHeight/3.5));
            image.setPosition(camX - image.getWidth() / 2, camY - image.getHeight() / 2);
            stage.addActor(image);
            added = true;
        }

        timer += delta;

        float totalDuration = fadeInDuration + displayDuration + fadeOutDuration;

        if (timer < fadeInDuration) {

            image.getColor().a = timer / fadeInDuration;
        } else if (timer < fadeInDuration + displayDuration) {
            image.getColor().a = 1f;
        } else if (timer < totalDuration) {
            float t = timer - fadeInDuration - displayDuration;
            image.getColor().a = 1f - (t / fadeOutDuration);
        } else {
            image.remove();
            complete = true;
        }

        return complete;
    }

    @Override
    public boolean isComplete() {
        return complete;
    }
}
