package com.mygdx.game.cutscene;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;

public class PortalSpawnEvent implements CutsceneEvent {
    private final Stage stage;
    private final PortalActor portalActor;
    private boolean added = false;

    public PortalSpawnEvent(Stage stage, Texture portalTexture, float x, float y) {
        this.stage = stage;

        TextureRegion[][] tmp = TextureRegion.split(portalTexture, 96, 80);
        Array<TextureRegion> frames = new Array<>();
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 4; col++) {
                frames.add(tmp[row][col]);
            }
        }

        Animation<TextureRegion> animation = new Animation<>(0.05f, frames, Animation.PlayMode.LOOP);
        portalActor = new PortalActor(animation);
        portalActor.setPosition(x, y);
    }

    @Override
    public boolean update(float delta) {
        if (!added) {
            stage.addActor(portalActor);
            portalActor.setScale(0f); // start small
            portalActor.addAction(Actions.sequence(
                    Actions.scaleTo(15f, 15f, 1.4f),
                    Actions.delay(0.4f),
                    Actions.removeActor()
            ));
            added = true;
        }
        return true;
    }


    @Override
    public boolean isComplete() {
        return true;
    }
}

