package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Character {
    private static final float SPEED = 400;
    private final Vector2 position;
    private float stateTime;
    private final Animation<TextureRegion> walkAnimation;
    private final Animation<TextureRegion> idleAnimation;
    private boolean isWalking;
    private boolean isFlipped;
    private int lives;

    private float timeSinceLastLifeLost = 0.0f;

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
        lives = 3;
    }

    public void update( Array<Enemy> enemies,TiledMap tiledMap) {

        float deltaTime = Gdx.graphics.getDeltaTime();

        boolean moveLeft = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean moveRight = Gdx.input.isKeyPressed(Input.Keys.D);
        boolean moveUp = Gdx.input.isKeyPressed(Input.Keys.W);
        boolean moveDown = Gdx.input.isKeyPressed(Input.Keys.S);
        isWalking=moveDown||moveRight||moveLeft||moveUp;
        //The potential new position based on input
        float potentialX = position.x;
        float potentialY = position.y;

        //The potential new position based on a buff distance (avoid overlaps of the character with the tile)
        float buffedpotentialX = position.x;
        float buffedpotentialY = position.y;


        if (moveUp) {
            potentialY += SPEED * deltaTime;
            buffedpotentialY=potentialY+10;
        }
        if (moveDown) {
            potentialY -= SPEED * deltaTime;
            buffedpotentialY=potentialY-4;
        }
        if (moveLeft) {
            potentialX -= SPEED * deltaTime;
            if (!isFlipped) {
                flipAnimations();
                isFlipped = true;
            }
            buffedpotentialX=potentialX-4;
        }
        if (moveRight) {
            potentialX += SPEED * deltaTime;
            if (isFlipped) {
                flipAnimations();
                isFlipped = false;
            }
            buffedpotentialX=potentialX+15;
        }

        // Check if the potential new position collides with blocked tiles
        if (isTileBlocked(buffedpotentialX, position.y, tiledMap) && isTileBlocked(position.x, buffedpotentialY, tiledMap)) {
            position.set(potentialX, potentialY);
        }


        // Update animation stateTime
        stateTime += deltaTime;

        // Check for enemy collisions
        for (Enemy enemy : enemies) {
            if (isCollidingWithEnemy(enemy)) {
                if (timeSinceLastLifeLost >= 4.0f) {
                    loseLife(); // Character loses a life
                    timeSinceLastLifeLost = 0.0f; // Reset the timer
                }
            }
        }

        timeSinceLastLifeLost += Gdx.graphics.getDeltaTime();
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

    public int getLives() {
        return lives;
    }

    public void loseLife() {
        lives--;
    }

    private boolean isCollidingWithEnemy(Enemy enemy) {
        // Calculate the hitbox of the character and the enemy
        float characterLeft = position.x;
        float characterRight = position.x + getWidth();
        float characterTop = position.y + getHeight();
        float characterBottom = position.y;

        float enemyLeft = enemy.getPosition().x;
        float enemyRight = enemy.getPosition().x + enemy.getWidth();
        float enemyTop = enemy.getPosition().y + enemy.getHeight();
        float enemyBottom = enemy.getPosition().y;

        // Check for collision by comparing the hitboxes
        boolean horizontalCollision = characterRight > enemyLeft && characterLeft < enemyRight;
        boolean verticalCollision = characterTop > enemyBottom && characterBottom < enemyTop;

        return horizontalCollision && verticalCollision;
    }

    private boolean isTileBlocked(float x, float y, TiledMap tiledMap) {
        // Get the collision layer from the TiledMap
        TiledMapTileLayer collisionLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Tile Layer 3");

        // Calculate the tile indices for the given position
        int tileX = (int) (x / 15.9);
        int tileY = (int) (y / 15.9);

        // Get the cell at the calculated tile indices
        TiledMapTileLayer.Cell cell = collisionLayer.getCell(tileX, tileY);

        // Check if the cell exists and has the "blocked" property
        return !(cell != null && cell.getTile().getProperties().containsKey("blocked"));
    }



}
