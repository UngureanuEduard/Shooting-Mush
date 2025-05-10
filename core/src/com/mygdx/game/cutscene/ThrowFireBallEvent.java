package com.mygdx.game.cutscene;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class ThrowFireBallEvent implements CutsceneEvent {

    private final Stage stage;
    private final Vector2 start;
    private final Vector2 end;
    private final float speed;
    private final Sound throwSound;
    private final Animation<TextureRegion> effectAnimation;
    private float animationTime = 0f;
    private final Image effectImage;
    private boolean complete = false;
    private boolean started = false;


    public ThrowFireBallEvent(Stage stage, Vector2 start, Vector2 end, float speed,
                              Texture effectTexture, Sound throwSound) {
        this.stage = stage;
        this.start = new Vector2(start);
        this.end = new Vector2(end);
        this.speed = speed;
        this.throwSound = throwSound;
        TextureRegion[][] splitFrames = TextureRegion.split(effectTexture, 32, 32);
        TextureRegion[] frames = new TextureRegion[4];
        System.arraycopy(splitFrames[0], 0, frames, 0, 4);
        this.effectAnimation = new Animation<>(0.1f, frames);
        this.effectAnimation.setPlayMode(Animation.PlayMode.LOOP);
        this.effectImage = new Image(frames[0]);
        this.effectImage.setSize(20, 20);
        this.effectImage.setVisible(false);
    }

    @Override
    public boolean update(float delta) {
        if (!started) {
            CutsceneUtils.startThrow(stage, effectImage, start, throwSound);
            started = true;
        }

        if (complete) return true;

        Vector2 currentPos = new Vector2(effectImage.getX(), effectImage.getY());
        Vector2 direction = new Vector2(end).sub(currentPos);
        float distance = direction.len();

        animationTime += delta;

        TextureRegion currentFrame = effectAnimation.getKeyFrame(animationTime);
        effectImage.setDrawable(new TextureRegionDrawable(currentFrame));
        effectImage.setPosition(currentPos.x, currentPos.y);

        if (distance < speed * delta) {
            effectImage.setPosition(end.x, end.y);
            complete = true;
        } else {
            direction.nor().scl(speed * delta);
            effectImage.moveBy(direction.x, direction.y);
        }

        return complete;
    }

    @Override
    public boolean isComplete() {
        if (complete) {
            effectImage.remove();
        }
        return complete;
    }
}
