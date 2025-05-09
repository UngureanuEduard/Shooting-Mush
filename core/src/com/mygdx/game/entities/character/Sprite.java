package com.mygdx.game.entities.character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Sprite {

    private final static int WIDTH= 24;
    private final static int HEIGHT= 24;

    private Vector2 position;
    private float stateTime;
    private boolean isFlipped;
    private String isWalking;
    private final Animation<TextureRegion> idleAnimation;
    private final Animation<TextureRegion> walkAnimation;
    private float animationSpeed = 0.1f;



    public Sprite(Vector2 position , Texture idleTexture , Texture walkTexture , int idleTextureSplit , int walkTextureSplit , int tileSize) {
        this.position = position;
        TextureRegion[] walkFrames = splitCharacterTexture(walkTexture,walkTextureSplit ,tileSize);
        TextureRegion[] idleFrames = splitCharacterTexture(idleTexture,idleTextureSplit ,tileSize);
        idleAnimation = new Animation<>(animationSpeed, idleFrames);
        walkAnimation = new Animation<>(animationSpeed, walkFrames);
        isWalking = "";
        isFlipped = false;
    }

    private TextureRegion[] splitCharacterTexture(Texture characterTexture, int n , int tilseSize) {
        TextureRegion[][] tmp = TextureRegion.split(characterTexture, tilseSize, tilseSize);
        TextureRegion[] characterFrames = new TextureRegion[n];
        System.arraycopy(tmp[0], 0, characterFrames, 0, n);
        return characterFrames;
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = getCurrentFrame();

        float drawX = position.x;
        float drawWidth = WIDTH;

        if (isFlipped) {
            drawX += WIDTH;
            drawWidth = -WIDTH;
        }

        batch.draw(currentFrame, drawX, position.y, drawWidth, HEIGHT);

    }

    private TextureRegion getCurrentFrame(){
        switch (isWalking) {
            case "right":
            case "left":
                return walkAnimation.getKeyFrame(stateTime, true);
            default:
                return idleAnimation.getKeyFrame(stateTime, true);
        }

    }

    public Vector2 getPosition() {
        return position;
    }

    public float getStateTime() {
        return stateTime;
    }

    public void setStateTime(float stateTime) {
        this.stateTime = stateTime;
    }

    public void setFlipped(boolean flipped) {
        isFlipped = flipped;
    }

    public void setIsWalking(String isWalking) {
        this.isWalking = isWalking;
        if ("left".equals(isWalking)) {
            setFlipped(true);
        } else if ("right".equals(isWalking)) {
            setFlipped(false);
        }
    }

    public int getWidth(){
        return WIDTH;
    }
    public int getHeight(){
        return HEIGHT;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void setAnimationSpeed(float speed) {
        this.animationSpeed = speed;
        idleAnimation.setFrameDuration(speed);
        walkAnimation.setFrameDuration(speed);
    }

    public float getAnimationSpeed() {
        return animationSpeed;
    }

    public boolean isFlipped() {
        return isFlipped;
    }
}


