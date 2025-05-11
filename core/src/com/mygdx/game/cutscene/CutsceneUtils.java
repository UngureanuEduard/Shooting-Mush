package com.mygdx.game.cutscene;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class CutsceneUtils {
    public static void centerOverlayAndLabel(OrthographicCamera camera, Stage stage, Image overlay, Label label) {
        float camX = camera.position.x;
        float camY = camera.position.y;
        float zoom = camera.zoom;

        float worldWidth = stage.getViewport().getWorldWidth() * zoom;
        float worldHeight = stage.getViewport().getWorldHeight() * zoom;

        overlay.setPosition(camX - worldWidth / 2f, camY - worldHeight / 2f);
        overlay.setSize(worldWidth, worldHeight);

        label.setPosition(
                camX - label.getPrefWidth() / 2f,
                camY - label.getPrefHeight() / 2f
        );
    }
    public static void startThrow(Stage stage, Image image, Vector2 start, Sound throwSound) {
        image.setPosition(start.x, start.y);
        image.setVisible(true);
        stage.addActor(image);
        if (throwSound != null) {
            throwSound.play();
        }
    }

    public static void centerOverlay(OrthographicCamera camera, Stage stage, Image overlay) {
        overlay.setPosition(
                camera.position.x - stage.getViewport().getWorldWidth() / 2f,
                camera.position.y - stage.getViewport().getWorldHeight() / 2f
        );
    }
}
