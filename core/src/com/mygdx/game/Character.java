package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Character {
    public float SPEED = 200;
    private final Vector2 position;
    private float stateTime;
    private final Animation<TextureRegion> walkAnimationLeftAndRight;
    private final Animation<TextureRegion> idleAnimationLeftAndRight;
    private final Animation<TextureRegion> walkAnimationFront;
    private final Animation<TextureRegion> walkAnimationBack;
    private String isWalking;
    private boolean isFlipped;
    private int lives;
    private int lostLives=0;
    private final Texture heartTexture;
    private final Texture emptyHeartTexture;
    private float timeSinceLastLifeLost = 4.0f;
    private float timeDashCooldown = 5.0f;
    private final Rectangle bodyHitbox;
    private final Circle headHitbox;
    private final ShapeRenderer shapeRenderer;



    public Character( Vector2 initialPosition,Assets assets) {

        // Set the initial position of the character
        position = initialPosition;

        Texture walkTexture = assets.getAssetManager().get(Assets.walkTexture);
        Texture idleTexture = assets.getAssetManager().get(Assets.idleTexture);
        Texture walkFrontTexture = assets.getAssetManager().get(Assets.walkFrontTexture);
        Texture walkBackTexture = assets.getAssetManager().get(Assets.walkBackTexture);
        heartTexture = assets.getAssetManager().get(Assets.heartTexture);
        emptyHeartTexture = assets.getAssetManager().get(Assets.emptyHeartTexture);
        TextureRegion[] walkFrontFrames = splitCharacterTexture(walkFrontTexture,4);
        TextureRegion[] walkBackFrames = splitCharacterTexture(walkBackTexture,4);
        TextureRegion[] walkFrames = splitCharacterTexture(walkTexture,4);
        TextureRegion[] idleFrames = splitCharacterTexture(idleTexture,9);
        walkAnimationLeftAndRight = new Animation<>(0.1f, walkFrames);
        walkAnimationFront = new Animation<>(0.1f, walkFrontFrames);
        walkAnimationBack = new Animation<>(0.1f, walkBackFrames);
        idleAnimationLeftAndRight = new Animation<>(0.1f, idleFrames);
        isWalking = ""; // Initially, the character is not walking
        isFlipped = false; // Initially, the character is not flipped
        lives = 3; // Start with 3 lives
        bodyHitbox = new Rectangle();
        headHitbox = new Circle();
        shapeRenderer = new ShapeRenderer();
    }

    public void update( Array<Enemy> enemies,TiledMap tiledMap,Boolean isPaused, Array<EnemyBullet> enemyBullets) {
        if(!isPaused) {
            float deltaTime = Gdx.graphics.getDeltaTime();

            boolean moveLeft = Gdx.input.isKeyPressed(Input.Keys.A);
            boolean moveRight = Gdx.input.isKeyPressed(Input.Keys.D);
            boolean moveUp = Gdx.input.isKeyPressed(Input.Keys.W);
            boolean moveDown = Gdx.input.isKeyPressed(Input.Keys.S);
            boolean dash = Gdx.input.isKeyPressed(Input.Keys.SPACE);

            //The potential new position based on input
            float potentialX = position.x;
            float potentialY = position.y;

            //The potential new position based on a buff distance (avoid overlaps of the character with the tile)
            float buffedpotentialX = position.x;
            float buffedpotentialY = position.y;
            isWalking = "";

            if (moveUp) {
                potentialY += SPEED * deltaTime;
                buffedpotentialY = potentialY + 10;
                if(dash && timeDashCooldown >= 3.0f)
                    potentialY= Dash(potentialY,true);
                isWalking = "up";
            }
            if (moveDown) {
                potentialY -= SPEED * deltaTime;
                if(dash && timeDashCooldown >= 3.0f)
                    potentialY= Dash(potentialY,false);
                buffedpotentialY = potentialY - 4;
                isWalking = "down";
            }
            if (moveLeft) {
                potentialX -= SPEED * deltaTime;
                if (!isFlipped) {
                    flipAnimations();
                    isFlipped = true;
                }
                if(dash && timeDashCooldown >= 3.0f)
                    potentialX= Dash(potentialX,false);
                buffedpotentialX = potentialX - 4;
                isWalking = "left";
            }
            if (moveRight) {
                potentialX += SPEED * deltaTime;
                if (isFlipped) {
                    flipAnimations();
                    isFlipped = false;
                }
                if(dash && timeDashCooldown >= 3.0f)
                    potentialX= Dash(potentialX,true);
                buffedpotentialX = potentialX + 15;
                isWalking = "right";
            }

            // Check if the potential new position collides with blocked tiles
            if (isTileBlocked(buffedpotentialX, position.y, tiledMap) && isTileBlocked(position.x, buffedpotentialY, tiledMap)) {
                position.set(potentialX, potentialY);
                bodyHitbox.set(potentialX+getWidth()*29/100, potentialY+getHeight()*10/100, (float) (getWidth()*41.66/100), (float) (getHeight()*31.25/100)); // Body hitbox (rectangle)
                headHitbox.set(potentialX+getWidth()/2, (float) (potentialY+getHeight()/1.7), (float) (getWidth() / 3.1)); // Head hitbox (circle)
            }


            // Update animation stateTime
            stateTime += deltaTime;

            // Check for enemy collisions
            for (Enemy enemy : enemies) {
                if (isCollidingWithEnemy(enemy)) {
                    if (timeSinceLastLifeLost >= 4.0f) {
                        loseLife();
                    }
                }
            }

            // Check for bullet collisions
            for (EnemyBullet enemyBullet : enemyBullets) {
                if (isCollidingWithBullet(enemyBullet)&&timeSinceLastLifeLost>=4.0f) {
                    loseLife();
                    enemyBullet.setActive(false); // Deactivate the bullet
                }
            }

            timeSinceLastLifeLost += deltaTime;
            timeDashCooldown += deltaTime;


        }

    }

    public void render(SpriteBatch batch , OrthographicCamera camera) {
        // Get the current frame from the appropriate animation
        TextureRegion currentFrame;
        switch (isWalking) {
            case "right":
            case "left":
                currentFrame = walkAnimationLeftAndRight.getKeyFrame(stateTime, true);
                break;
            case "up":
                currentFrame = walkAnimationBack.getKeyFrame(stateTime, true);
                break;
            case "down":
                currentFrame = walkAnimationFront.getKeyFrame(stateTime, true);
                break;
            default:
                currentFrame = idleAnimationLeftAndRight.getKeyFrame(stateTime, true);
                break;
        }

        // Draw the character at its current position
        batch.draw(currentFrame, position.x, position.y);

        batch.end();

        //Debugging
        drawHitboxes(camera);
    }

    private void drawHitboxes(OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // Draw the rectangle hitbox (body)
        shapeRenderer.setColor(1, 0, 0, 1); // Red color for the rectangle
        shapeRenderer.rect(bodyHitbox.x, bodyHitbox.y, bodyHitbox.width, bodyHitbox.height);

        // Draw the circle hitbox (head)
        shapeRenderer.setColor(0, 1, 0, 1); // Green color for the circle
        shapeRenderer.circle(headHitbox.x, headHitbox.y, headHitbox.radius);

        shapeRenderer.end();
    }

    public void dispose() {
        // Dispose of character textures here
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

    //change it -- to work
    public void loseLife() {
        lives++;
        lostLives++;
        timeSinceLastLifeLost=0;
    }

    private boolean isCollidingWithEnemy(Enemy enemy) {
        // Calculate the hit-box of the character and the enemy
        float characterLeft = position.x;
        float characterRight = position.x + getWidth();
        float characterTop = position.y + getHeight();
        float characterBottom = position.y;

        float enemyLeft = enemy.getPosition().x;
        float enemyRight = enemy.getPosition().x + enemy.getWidth();
        float enemyTop = enemy.getPosition().y + enemy.getHeight();
        float enemyBottom = enemy.getPosition().y;

        // Check for collision by comparing the hit-boxes
        boolean horizontalCollision = characterRight > enemyLeft && characterLeft < enemyRight;
        boolean verticalCollision = characterTop > enemyBottom && characterBottom < enemyTop;

        return horizontalCollision && verticalCollision;
    }

    public void drawHearts(SpriteBatch batch, OrthographicCamera camera)
    {
        float heartX = camera.position.x - (camera.viewportWidth * camera.zoom) / 2 + 10 * camera.zoom;
        float heartY = camera.position.y + (camera.viewportHeight * camera.zoom) / 2 - 40 * camera.zoom;

        for (int i = 0; i < lives+lostLives; i++) {
            float heartContainerX = heartX + i * 40 *camera.zoom;
            if (i <lives) {
                batch.draw(heartTexture, heartContainerX, heartY);
                batch.draw(emptyHeartTexture, heartContainerX, heartY);
            } else {
                batch.draw(emptyHeartTexture, heartContainerX, heartY);
            }
        }
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

    // Increment direction for Dash action
    private float Dash(float cardinalPoint , Boolean positive){

        if(positive)
            {
                timeDashCooldown = 0;
                return cardinalPoint+80;
            }
            else {
                    timeDashCooldown = 0;
                    return cardinalPoint-80;
                 }
    }

    public void GainLife(){
        lives++;
    }

    public void GainSpeed(){
        SPEED+=SPEED*10/100;
    }

    private boolean isCollidingWithBullet(Bullet bullet) {
        // Check if the enemy's bounding box intersects with the bullet's position
        return position.x < bullet.getPosition().x + bullet.getWidth() &&
                position.x + getWidth() > bullet.getPosition().x &&
                position.y < bullet.getPosition().y + bullet.getHeight() &&
                position.y + getHeight() > bullet.getPosition().y;
    }

    public Integer getLives(){return lives;}
}