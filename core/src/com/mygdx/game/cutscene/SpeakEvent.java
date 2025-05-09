package com.mygdx.game.cutscene;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class SpeakEvent implements CutsceneEvent {
    private final static float CHAR_DELAY = 0.07f;
    private final static float POST_TEXT_DELAY = 1f;

    private final Stage stage;
    private boolean complete = false;
    private final Label dialogLabel;
    private final String fullText;
    private int currentCharIndex = 0;
    private final Image background;
    private final Table dialogTable;
    private boolean textFullyDisplayed = false;
    private float delayTimer = 0f;
    private float timeAccumulator = 0f;
    private boolean added = false;


    public SpeakEvent(Stage stage, Skin skin , String fullText , Texture dialogBoxTexture ,Texture portrait  ) {
        this.stage = stage;
        this.fullText = fullText;


        background = new Image(new TextureRegionDrawable(new TextureRegion(dialogBoxTexture)));
        background.setSize(300, 70);

        OrthographicCamera cam = (OrthographicCamera) stage.getCamera();
        float camX = cam.position.x;
        float camY = cam.position.y;
        float zoom = cam.zoom;

        float dialogX = camX - (stage.getViewport().getWorldWidth() * zoom) / 2f + 50;
        float dialogY = camY - (stage.getViewport().getWorldHeight() * zoom) / 2f + 20;

        background.setPosition(dialogX, dialogY);

        Image portraitImage = new Image(portrait);

        dialogLabel = new Label("", skin);
        dialogLabel.setWrap(true);
        dialogLabel.setFontScale(0.30f);

        dialogTable = new Table();
        dialogTable.setSize(300, 70);
        dialogTable.setPosition(dialogX, dialogY);
        dialogTable.left().top();
        dialogTable.add(portraitImage).size(60, 60).padLeft(3).padTop(7).padRight(10);
        dialogTable.add(dialogLabel).width(200).top().left().padLeft(10).padTop(6);

    }

    private void updateText() {
        if (currentCharIndex < fullText.length()) {
            dialogLabel.setText(fullText.substring(0, currentCharIndex + 1));
            currentCharIndex++;
        } else {
            textFullyDisplayed = true;
        }
    }


    public boolean update(float delta) {
        OrthographicCamera cam = (OrthographicCamera) stage.getCamera();
        float camX = cam.position.x;
        float camY = cam.position.y;
        float zoom = cam.zoom;

        float dialogX = camX - (stage.getViewport().getWorldWidth() * zoom) / 2f + 50;
        float dialogY = camY - (stage.getViewport().getWorldHeight() * zoom) / 2f + 20;

        background.setPosition(dialogX, dialogY);
        dialogTable.setPosition(dialogX, dialogY);

        if (!textFullyDisplayed) {
            if(!added){
                stage.addActor(background);
                stage.addActor(dialogTable);
                added = true;
            }

            timeAccumulator += delta;
            if (timeAccumulator >= CHAR_DELAY) {
                updateText();
                timeAccumulator = 0f;
            }
        } else {
            delayTimer += delta;
            if (delayTimer >= POST_TEXT_DELAY) {
                complete = true;
            }
        }

        return complete;
    }

    @Override
    public boolean isComplete() {
        if (complete) {
            background.remove();
            dialogTable.remove();
        }
        return complete;
    }
}
