package com.mygdx.game.entities;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.utilities_resources.Assets;

public class BasicCharacter {
    private final Animation<TextureRegion> walkAnimationLeftAndRight;
    private final Animation<TextureRegion> idleAnimationLeftAndRight;
    private final Animation<TextureRegion> walkAnimationFront;
    private final Animation<TextureRegion> walkAnimationBack;
    private final Vector2 position;
    private float stateTime;
    private String isWalking;
    private boolean isFlipped;
    private float timeSinceLastLifeLost = 5.0f;

    public BasicCharacter(Vector2 position , Assets assets) {
        this.position = position;
        Texture walkTexture = assets.getAssetManager().get(Assets.walkTexture);
        Texture idleTexture = assets.getAssetManager().get(Assets.idleTexture);
        Texture walkFrontTexture = assets.getAssetManager().get(Assets.walkFrontTexture);
        Texture walkBackTexture = assets.getAssetManager().get(Assets.walkBackTexture);
        TextureRegion[] walkFrontFrames = splitCharacterTexture(walkFrontTexture,4);
        TextureRegion[] walkBackFrames = splitCharacterTexture(walkBackTexture,4);
        TextureRegion[] walkFrames = splitCharacterTexture(walkTexture,4);
        TextureRegion[] idleFrames = splitCharacterTexture(idleTexture,9);
        walkAnimationLeftAndRight = new Animation<>(0.1f, walkFrames);
        walkAnimationFront = new Animation<>(0.1f, walkFrontFrames);
        walkAnimationBack = new Animation<>(0.1f, walkBackFrames);
        idleAnimationLeftAndRight = new Animation<>(0.1f, idleFrames);
        isWalking = "";
        isFlipped = false;
    }

    public void render(SpriteBatch batch ) {
        TextureRegion currentFrame;
        if (!(timeSinceLastLifeLost < 5.0f)) {
            currentFrame=getCurrentFrame();
            batch.draw(currentFrame, position.x, position.y,getWidth(),getHeight());
        } else {
            float flashTime = 0.3f;
            if ((int) (timeSinceLastLifeLost / flashTime) % 2 == 0) {
                currentFrame=getCurrentFrame();
                batch.draw(currentFrame, position.x, position.y,getWidth(),getHeight());
            }
        }
    }

    private TextureRegion[] splitCharacterTexture(Texture characterTexture, int n) {
        TextureRegion[][] tmp = TextureRegion.split(characterTexture, 48, 48);
        TextureRegion[] characterFrames = new TextureRegion[n];
        System.arraycopy(tmp[0], 0, characterFrames, 0, n);
        return characterFrames;
    }

    protected TextureRegion getCurrentFrame(){
            switch (isWalking) {
                case "right":
                case "left":
                    return walkAnimationLeftAndRight.getKeyFrame(stateTime, true);
                case "up":
                    return walkAnimationBack.getKeyFrame(stateTime, true);
                case "down":
                    return walkAnimationFront.getKeyFrame(stateTime, true);
                default:
                    return idleAnimationLeftAndRight.getKeyFrame(stateTime, true);
            }

    }

    public void dispose() {
        walkAnimationLeftAndRight.getKeyFrames()[0].getTexture().dispose();
        walkAnimationLeftAndRight.getKeyFrames()[1].getTexture().dispose();
        walkAnimationLeftAndRight.getKeyFrames()[2].getTexture().dispose();
        walkAnimationLeftAndRight.getKeyFrames()[3].getTexture().dispose();
        idleAnimationLeftAndRight.getKeyFrames()[0].getTexture().dispose();
        idleAnimationLeftAndRight.getKeyFrames()[1].getTexture().dispose();
        idleAnimationLeftAndRight.getKeyFrames()[2].getTexture().dispose();
        idleAnimationLeftAndRight.getKeyFrames()[3].getTexture().dispose();
        idleAnimationLeftAndRight.getKeyFrames()[4].getTexture().dispose();
        idleAnimationLeftAndRight.getKeyFrames()[5].getTexture().dispose();
        idleAnimationLeftAndRight.getKeyFrames()[6].getTexture().dispose();
        idleAnimationLeftAndRight.getKeyFrames()[7].getTexture().dispose();
        idleAnimationLeftAndRight.getKeyFrames()[8].getTexture().dispose();
    }

    public void flipAnimations() {

        walkAnimationLeftAndRight.getKeyFrames()[0].flip(true, false);
        walkAnimationLeftAndRight.getKeyFrames()[1].flip(true, false);
        walkAnimationLeftAndRight.getKeyFrames()[2].flip(true, false);
        walkAnimationLeftAndRight.getKeyFrames()[3].flip(true, false);
        idleAnimationLeftAndRight.getKeyFrames()[0].flip(true, false);
        idleAnimationLeftAndRight.getKeyFrames()[1].flip(true, false);
        idleAnimationLeftAndRight.getKeyFrames()[2].flip(true, false);
        idleAnimationLeftAndRight.getKeyFrames()[3].flip(true, false);
        idleAnimationLeftAndRight.getKeyFrames()[4].flip(true, false);
        idleAnimationLeftAndRight.getKeyFrames()[5].flip(true, false);
        idleAnimationLeftAndRight.getKeyFrames()[6].flip(true, false);
        idleAnimationLeftAndRight.getKeyFrames()[7].flip(true, false);
        idleAnimationLeftAndRight.getKeyFrames()[8].flip(true, false);
    }

    public float getWidth() {
        return 24;
    }

    public float getHeight() {
        return 24;
    }

    public float getTimeSinceLastLifeLost() {
        return timeSinceLastLifeLost;
    }

    public void setTimeSinceLastLifeLost(float timeSinceLastLifeLost) {
        this.timeSinceLastLifeLost = timeSinceLastLifeLost;
    }

    public String getIsWalking() {
        return isWalking;
    }

    public void setIsWalking(String isWalking) {
        this.isWalking = isWalking;
    }

    public boolean getIsFlipped() {
        return isFlipped;
    }

    public void setIsFlipped(boolean flipped) {
        isFlipped = flipped;
    }

    public float getStateTime() {
        return stateTime;
    }

    public void setStateTime(float stateTime) {
        this.stateTime = stateTime;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position.set(position);
    }

    public Animation<TextureRegion> getIdleAnimationLeftAndRight() {
        return idleAnimationLeftAndRight;
    }
}
