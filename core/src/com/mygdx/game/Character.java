package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Character {
    private static final float SPEED = 400;
    private final Vector2 position;
    private float stateTime;
    private final Animation<TextureRegion> walkAnimation;
    private final Animation<TextureRegion> idleAnimation;
    private boolean isWalking;
    private boolean isFlipped;

    public Character( Vector2 initialPosition) {

        // Set the initial position of the character
        position = initialPosition;

        // Use the splitCharacterTexture method in this class for the walk animation
        Texture walkTexture = new Texture("cute mushroom walk.png");
        Texture idleTexture = new Texture("cute mushroom idle.png");
        TextureRegion[] walkFrames = splitCharacterTexture(walkTexture,4);
        TextureRegion[] idleFrames = splitCharacterTexture(idleTexture,9);
        walkAnimation = new Animation<>(0.1f, walkFrames);
        idleAnimation = new Animation<>(0.1f, idleFrames);
        isWalking = false; // Initially, the character is not walking
        isFlipped = false; // Initially, the character is not flipped
    }

    public void update() {
        // Handle character movement based on keyboard input
        float deltaTime = Gdx.graphics.getDeltaTime();
        boolean moveLeft = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean moveRight = Gdx.input.isKeyPressed(Input.Keys.D);
        // Check if any movement keys are pressed
        isWalking = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.S)
                || moveLeft || moveRight;


        // Update character's position based on user input
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            position.y += SPEED * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            position.y -= SPEED * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            position.x -= SPEED * deltaTime;
            if (!isFlipped) {
                flipAnimations();
                isFlipped = true;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            position.x += SPEED * deltaTime;
            if (isFlipped) {
                flipAnimations();
                isFlipped = false;
            }
        }

        // Update animation stateTime
        stateTime += deltaTime;
    }

    public void render(SpriteBatch batch) {
        // Get the current frame from the appropriate animation
        TextureRegion currentFrame;
        if (isWalking) {
            currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        } else {
            currentFrame = idleAnimation.getKeyFrame(stateTime, true);
        }
        // Draw the character at its current position
        batch.draw(currentFrame, position.x, position.y);
    }

    public void dispose() {
        // Dispose of character textures here
        walkAnimation.getKeyFrames()[0].getTexture().dispose();
        walkAnimation.getKeyFrames()[1].getTexture().dispose();
        walkAnimation.getKeyFrames()[2].getTexture().dispose();
        walkAnimation.getKeyFrames()[3].getTexture().dispose();
        idleAnimation.getKeyFrames()[0].getTexture().dispose();
        idleAnimation.getKeyFrames()[1].getTexture().dispose();
        idleAnimation.getKeyFrames()[2].getTexture().dispose();
        idleAnimation.getKeyFrames()[3].getTexture().dispose();
        idleAnimation.getKeyFrames()[4].getTexture().dispose();
        idleAnimation.getKeyFrames()[5].getTexture().dispose();
        idleAnimation.getKeyFrames()[6].getTexture().dispose();
        idleAnimation.getKeyFrames()[7].getTexture().dispose();
        idleAnimation.getKeyFrames()[8].getTexture().dispose();
    }


    public Vector2 getPosition() {
        return position;
    }

    public float getWidth() {
        return 48;
    }

    public float getHeight() {
        return 48;
    }

    private TextureRegion[] splitCharacterTexture(Texture characterTexture, int n) {
        TextureRegion[][] tmp = TextureRegion.split(characterTexture, 48, 48);
        TextureRegion[] characterFrames = new TextureRegion[n];
        System.arraycopy(tmp[0], 0, characterFrames, 0, n);
        return characterFrames;
    }

    private void flipAnimations() {

        walkAnimation.getKeyFrames()[0].flip(true, false);
        walkAnimation.getKeyFrames()[1].flip(true, false);
        walkAnimation.getKeyFrames()[2].flip(true, false);
        walkAnimation.getKeyFrames()[3].flip(true, false);
        idleAnimation.getKeyFrames()[0].flip(true, false);
        idleAnimation.getKeyFrames()[1].flip(true, false);
        idleAnimation.getKeyFrames()[2].flip(true, false);
        idleAnimation.getKeyFrames()[3].flip(true, false);
        idleAnimation.getKeyFrames()[4].flip(true, false);
        idleAnimation.getKeyFrames()[5].flip(true, false);
        idleAnimation.getKeyFrames()[6].flip(true, false);
        idleAnimation.getKeyFrames()[7].flip(true, false);
        idleAnimation.getKeyFrames()[8].flip(true, false);
    }

}
