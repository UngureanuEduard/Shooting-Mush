package com.mygdx.game.cutscene;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class PortalActor extends Actor {
    private final Animation<TextureRegion> animation;
    private float stateTime = 0f;

    public PortalActor(Animation<TextureRegion> animation) {
        this.animation = animation;
        setSize(96, 80);
        setOrigin(getWidth() / 2f, getHeight() / 2f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion frame = animation.getKeyFrame(stateTime, true);
        batch.draw(
                frame,
                getX(), getY(),
                getOriginX(), getOriginY(),
                getWidth(), getHeight(),
                getScaleX(), getScaleY(),
                0
        );
    }

}

