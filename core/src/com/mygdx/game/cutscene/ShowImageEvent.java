package com.mygdx.game.cutscene;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class ShowImageEvent implements CutsceneEvent {
    private final Stage stage;
    private final Image image;
    private boolean complete = false;

    public ShowImageEvent(Stage stage, Texture texture, float x, float y, float width, float height) {
        this.stage = stage;
        image = new Image(new TextureRegionDrawable(texture));
        image.setSize(width, height);
        image.setPosition(x, y);
    }

    @Override
    public boolean update(float delta) {
        if (!complete) {
            stage.addActor(image);
            complete = true;
        }
        return complete;
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    @Override
    public void skip() {
        complete = true;
        image.remove();
    }

    public Image getImage() {
        return image;
    }
}
