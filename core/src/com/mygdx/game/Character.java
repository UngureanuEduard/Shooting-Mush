package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.combat_system.EnemyBullet;
import com.mygdx.game.utilities_resources.Assets;

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
    private Body body;
    private final World world;

    private final Vector2 pushBackDirection = new Vector2(0, 0);
    private float pushBackTime = 0f;

    public Character(Vector2 initialPosition, Assets assets , World world) {
        this.world = world;
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
        dashSpeed=SPEED*2;
        movementSpeed=SPEED;
        createPhysicsBody();
    }

    public void update(Array<Enemy> enemies, TiledMap tiledMap, Boolean isPaused, Array<EnemyBullet> enemyBullets , Boolean inDialog) {
        if(!isPaused) {
            float deltaTime = Gdx.graphics.getDeltaTime();
            stateTime += deltaTime;
            if(!inDialog){

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
                    body.setTransform(potentialX, potentialY, 0); // No rotation for this character
                }

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
                        Vector2 bulletDirection = new Vector2(position).sub(enemyBullet.getPosition()).nor(); // Reverse the direction
                        pushBackDirection.set(bulletDirection);
                        pushBackTime = 0f;
                        enemyBullet.setAlive(false);
                    }
                }

                timeSinceLastLifeLost += deltaTime;

                if (pushBackTime < 0.5f) {
                    pushBackTime += deltaTime;
                    float PUSH_BACK_FORCE = 50.0f;
                    position.add(pushBackDirection.scl(PUSH_BACK_FORCE * deltaTime));
                }
            }
        }

    }

    public void render(SpriteBatch batch ) {
        TextureRegion currentFrame;
        if (!isInvincible()) {
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
        float characterLeft = position.x;
        float characterRight = position.x + getWidth();
        float characterTop = position.y + getHeight();
        float characterBottom = position.y;

        float enemyLeft = enemy.getPosition().x;
        float enemyRight = enemy.getPosition().x + enemy.getWidth();
        float enemyTop = enemy.getPosition().y + enemy.getHeight();
        float enemyBottom = enemy.getPosition().y;

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

        int tileX = (int) (x / 15.9);
        int tileY = (int) (y / 15.9);

        TiledMapTileLayer.Cell cell = collisionLayer.getCell(tileX, tileY);

        return !(cell != null && cell.getTile().getProperties().containsKey("blocked"));
    }

    private void handleDash(float deltaTime) {
        boolean dashPressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);

        if (dashPressed && canDash) {
            isDashing = true;
            canDash = false;
            dashDuration = 0;
            dashCooldown = 0;
        }

        if (isDashing) {
            dashDuration += deltaTime;
            float DASH_TIME = 5.0f;
            if (dashDuration >= DASH_TIME || !dashPressed ) {
                isDashing = false;

                float dashPercentageUsed = dashDuration / DASH_TIME;
                float remainingDashPercentage = 1 - dashPercentageUsed;

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
        Polygon bulletPolygon = bullet.getHitBox();

        Polygon bodyPolygon = new Polygon(new float[]{
                bodyHitbox.x, bodyHitbox.y,
                bodyHitbox.x + bodyHitbox.width, bodyHitbox.y,
                bodyHitbox.x + bodyHitbox.width, bodyHitbox.y + bodyHitbox.height,
                bodyHitbox.x, bodyHitbox.y + bodyHitbox.height
        });

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

    public void setPosition(Vector2 position) {
        this.position.set(position);
    }

    private void createPhysicsBody() {
        // Define the body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position); // Set initial position

        // Create the body in the world
        body = world.createBody(bodyDef);

        // Create a rectangle shape for the body
        PolygonShape bodyShape = new PolygonShape();
        bodyShape.setAsBox(getWidth() / 2, getHeight() / 2);

        // Create a fixture for the body
        FixtureDef bodyFixture = new FixtureDef();
        bodyFixture.shape = bodyShape;
        bodyFixture.density = 1.0f;
        bodyFixture.friction = 0.5f;
        bodyFixture.restitution = 0.2f; // Bounciness

        // Attach the fixture to the body
        body.createFixture(bodyFixture);
        bodyShape.dispose();

        // Create the head as a circle shape
        CircleShape headShape = new CircleShape();
        headShape.setRadius(getWidth() / 6); // Example radius for head

        // Create a fixture for the head
        FixtureDef headFixture = new FixtureDef();
        headFixture.shape = headShape;
        headFixture.density = 0.5f;

        // Attach the fixture to the body
        body.createFixture(headFixture);
        headShape.dispose();
    }
}