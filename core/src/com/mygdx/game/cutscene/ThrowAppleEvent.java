package com.mygdx.game.cutscene;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class ThrowAppleEvent implements CutsceneEvent {
    private final Stage stage;
    private final Vector2 start;
    private final Vector2 end;
    private final Image appleImage;
    private final Sound throwSound;
    private boolean complete = false;
    private boolean started = false;
    private float totalRotation = 0f;
    private float totalTime = 0f;
    private final float duration;


    public ThrowAppleEvent(Stage stage, Vector2 start, Vector2 end, float speed, Texture appleTexture, Sound throwSound) {
        this.stage = stage;
        this.start = new Vector2(start);
        this.end = new Vector2(end);
        this.throwSound = throwSound;
        Vector2 displacement = new Vector2(end).sub(start);
        this.duration = displacement.len() / speed;

        this.appleImage = new Image(appleTexture);
        this.appleImage.setSize(16, 16);
        this.appleImage.setOrigin(appleImage.getWidth() / 2f, appleImage.getHeight() / 2f);
        this.appleImage.setVisible(false);
    }

    @Override
    public boolean update(float delta) {
        if (!started) {
            CutsceneUtils.startThrow(stage, appleImage, start, throwSound);
            started = true;
        }

        if (complete) return true;

        totalTime += delta;
        float progress = totalTime / duration;

        if (progress >= 1f) {
            appleImage.setPosition(end.x, end.y);
            complete = true;
            return true;
        }

        float x = start.x + (end.x - start.x) * progress;
        float y = start.y + (end.y - start.y) * progress;

        float arcHeight = 20f;
        y += (float) Math.sin(progress * Math.PI) * arcHeight;

        appleImage.setPosition(x, y);

        float rotationSpeed = 360f;
        totalRotation += rotationSpeed * delta;
        appleImage.setRotation(totalRotation);

        return false;
    }


    @Override
    public boolean isComplete() {
        if (complete) {
            appleImage.remove();
        }
        return complete;
    }
}
