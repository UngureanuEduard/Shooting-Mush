package com.mygdx.game.cutscene;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class ShakeCameraEvent implements CutsceneEvent {

    private final Stage stage;
    private final float duration;
    private final float intensity;

    private float elapsed = 0f;
    private boolean complete = false;

    private float originalX, originalY;

    public ShakeCameraEvent(Stage stage, float duration, float intensity) {
        this.stage = stage;
        this.duration = duration;
        this.intensity = intensity;
    }

    @Override
    public boolean update(float delta) {
        if (complete) return true;

        OrthographicCamera camera = (OrthographicCamera) stage.getCamera();

        if (elapsed == 0f) {
            originalX = camera.position.x;
            originalY = camera.position.y;
        }

        elapsed += delta;

        if (elapsed >= duration) {
            camera.position.set(originalX, originalY, 0);
            camera.update();
            complete = true;
            return true;
        }

        float shakeX = (float) ((Math.random() - 0.5f) * 2 * intensity);
        float shakeY = (float) ((Math.random() - 0.5f) * 2 * intensity);

        camera.position.set(originalX + shakeX, originalY + shakeY, 0);
        camera.update();

        return false;
    }

    @Override
    public boolean isComplete() {
        return complete;
    }
}
