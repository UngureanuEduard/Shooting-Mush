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
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;

public class Character {
    private final float DASH_COOLDOWN_TIME = 10.0f;
    private boolean isDashing = false;
    private float dashDuration = 0.0f;
    private float dashCooldown = DASH_COOLDOWN_TIME;
    private final float dashSpeed;
    private final float movementSpeed;
    private boolean canDash = true;
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
    private float timeSinceLastLifeLost = 5.0f;
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
        dashSpeed=SPEED*2;
        movementSpeed=SPEED;
    }

    public void update( Array<Enemy> enemies,TiledMap tiledMap,Boolean isPaused, Array<EnemyBullet> enemyBullets) {
        if(!isPaused) {
            float deltaTime = Gdx.graphics.getDeltaTime();

            boolean moveLeft = Gdx.input.isKeyPressed(Input.Keys.A);
            boolean moveRight = Gdx.input.isKeyPressed(Input.Keys.D);
            boolean moveUp = Gdx.input.isKeyPressed(Input.Keys.W);
            boolean moveDown = Gdx.input.isKeyPressed(Input.Keys.S);

            handleDash(deltaTime);

            //The potential new position based on input
            float potentialX = position.x;
            float potentialY = position.y;

            //The potential new position based on a buff distance (avoid overlaps of the character with the tile)
            float buffedpotentialX = position.x;
            float buffedpotentialY = position.y;

            isWalking = "";

            if (isDashing) {
                SPEED =dashSpeed;
            }
            else SPEED =movementSpeed;

            if (moveUp) {
                potentialY += SPEED * deltaTime;
                buffedpotentialY = potentialY + 5;
                isWalking = "up";
            }
            if (moveDown) {
                potentialY -= SPEED * deltaTime;
                buffedpotentialY = potentialY;
                isWalking = "down";
            }
            if (moveLeft) {
                potentialX -= SPEED * deltaTime;
                if (!isFlipped) {
                    flipAnimations();
                    isFlipped = true;
                }
                buffedpotentialX = potentialX;
                isWalking = "left";
            }
            if (moveRight) {
                potentialX += SPEED * deltaTime;
                if (isFlipped) {
                    flipAnimations();
                    isFlipped = false;
                }
                buffedpotentialX = potentialX + 5;
                isWalking = "right";
            }

            // Check if the potential new position collides with blocked tiles
            if (isTileBlocked(buffedpotentialX, position.y, tiledMap) && isTileBlocked(position.x, buffedpotentialY, tiledMap)) {
                position.set(potentialX, potentialY);
                bodyHitbox.set(potentialX + getWidth() * 29 / 100, potentialY + getHeight() * 10 / 100, (float) (getWidth() * 41.66 / 100), (float) (getHeight() * 31.25 / 100)); // Body hitbox (rectangle)
                headHitbox.set(potentialX + getWidth() / 2, (float) (potentialY + getHeight() / 1.7), (float) (getWidth() / 3.1)); // Head hitbox (circle)
            }

            // Update animation stateTime
            stateTime += deltaTime;

            // Check for enemy collisions
            for (Enemy enemy : enemies) {
                if (isCollidingWithEnemy(enemy)) {
                    if (timeSinceLastLifeLost >= 5.0f) {
                        loseLife();
                    }
                }
            }

            // Check for bullet collisions
            for (EnemyBullet enemyBullet : enemyBullets) {
                if (isCollidingWithBullet(enemyBullet)&&timeSinceLastLifeLost>=5.0f) {
                    loseLife();
                    enemyBullet.setActive(false); // Deactivate the bullet
                }
            }

            timeSinceLastLifeLost += deltaTime;
        }

    }

    public void render(SpriteBatch batch , OrthographicCamera camera) {
        TextureRegion currentFrame;

        // If the character is not invincible, show the animation as usual
        if (!isInvincible()) {
            currentFrame=getCurrentFrame();
            batch.draw(currentFrame, position.x, position.y,getWidth(),getHeight());
        } else {
            // If the character is invincible, make it flash by showing/hiding every 0.5 seconds
            float flashTime = 0.3f;
            if ((int) (timeSinceLastLifeLost / flashTime) % 2 == 0) {
                // Only draw the character when the time divided by flashTime
                currentFrame=getCurrentFrame();
                batch.draw(currentFrame, position.x, position.y,getWidth(),getHeight());
            }
        }

        batch.end();

        //Debugging
        drawHitboxes(camera);
    }

    private TextureRegion getCurrentFrame(){
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

    private boolean isInvincible() {
        return timeSinceLastLifeLost < 5.0f; // Invincible for 4 seconds after losing a life
    }

    //Debugging
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
        return 24;
    }

    public float getHeight() {
        return 24;
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
                batch.draw(heartTexture, heartContainerX, heartY, (float) heartTexture.getWidth() /2, (float) heartTexture.getHeight() /2);
                batch.draw(emptyHeartTexture, heartContainerX, heartY, (float) emptyHeartTexture.getWidth() /2, (float) emptyHeartTexture.getHeight() /2);
            } else {
                batch.draw(emptyHeartTexture, heartContainerX, heartY, (float) emptyHeartTexture.getWidth() /2, (float) emptyHeartTexture.getHeight() /2);
            }
        }
    }
    private boolean isTileBlocked(float x, float y, TiledMap tiledMap) {
        // Get the collision layer from the TiledMap
        TiledMapTileLayer collisionLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Block");

        // Calculate the tile indices for the given position
        int tileX = (int) (x / 15.9);
        int tileY = (int) (y / 15.9);

        // Get the cell at the calculated tile indices
        TiledMapTileLayer.Cell cell = collisionLayer.getCell(tileX, tileY);

        // Check if the cell exists and has the "blocked" property
        return !(cell != null && cell.getTile().getProperties().containsKey("blocked"));
    }

    private void handleDash(float deltaTime) {
        boolean dashPressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);

        if (dashPressed && canDash) {
            isDashing = true;
            canDash = false;
            dashDuration = 0;
            dashCooldown = 0; // Reset cooldown timer when dash starts
        }

        if (isDashing) {
            dashDuration += deltaTime;
            float DASH_TIME = 5.0f;
            if (dashDuration >= DASH_TIME || !dashPressed ) {
                isDashing = false;

                // Calculate remaining dash percentage
                float dashPercentageUsed = dashDuration / DASH_TIME;
                float remainingDashPercentage = 1 - dashPercentageUsed;

                // Scale the cooldown based on the remaining dash percentage
                dashCooldown = DASH_COOLDOWN_TIME * remainingDashPercentage;
            }
        } else {
            dashCooldown += deltaTime;
            if (dashCooldown >= DASH_COOLDOWN_TIME) {
                canDash = true;
            }
        }
    }

    public void GainLife(){
        lives++;
    }

    public void GainSpeed(){
        SPEED+=SPEED*10/100;
    }

    private boolean isCollidingWithBullet(EnemyBullet bullet) {
        Polygon bulletPolygon = bullet.getHitBox(); // Assuming this is a Polygon already.

        Polygon bodyPolygon = new Polygon(new float[]{
                bodyHitbox.x, bodyHitbox.y,
                bodyHitbox.x + bodyHitbox.width, bodyHitbox.y,
                bodyHitbox.x + bodyHitbox.width, bodyHitbox.y + bodyHitbox.height,
                bodyHitbox.x, bodyHitbox.y + bodyHitbox.height
        });

        // Convert the Circle to a Polygon approximation (8-sided for simplicity)
        int numVertices = 8;
        float[] circleVertices = new float[2 * numVertices];
        for (int i = 0; i < numVertices; i++) {
            double angle = 2 * Math.PI * i / numVertices;
            circleVertices[2 * i] = (float) (headHitbox.x + headHitbox.radius * Math.cos(angle));
            circleVertices[2 * i + 1] = (float) (headHitbox.y + headHitbox.radius * Math.sin(angle));
        }
        Polygon headPolygon = new Polygon(circleVertices);

        return Intersector.overlapConvexPolygons(bulletPolygon, bodyPolygon) ||
                Intersector.overlapConvexPolygons(bulletPolygon, headPolygon);
    }

    public Integer getLives(){return lives;}
}