package com.mygdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.combat_system.EnemyBullet;
import com.mygdx.game.utilities_resources.Assets;

public class Character  extends  BasicCharacter{
    private final float DASH_COOLDOWN_TIME = 10.0f;
    private boolean isDashing = false;
    private float dashDuration = 0.0f;
    private float dashCooldown = DASH_COOLDOWN_TIME;
    private final float dashSpeed;
    private final float movementSpeed;
    private boolean canDash = true;
    public float SPEED = 200;
    private int lives;
    private int lostLives=0;
    private final Texture heartTexture;
    private final Texture emptyHeartTexture;
    private final Vector2 pushBackDirection = new Vector2(0, 0);
    private float pushBackTime = 0f;
    private Boolean dialogOrGameOver = false;
    private final Rectangle bodyHitbox;
    private final Circle headHitbox;

    public Character(Vector2 initialPosition, Assets assets) {
        super(initialPosition, assets);
        heartTexture = assets.getAssetManager().get(Assets.heartTexture);
        emptyHeartTexture = assets.getAssetManager().get(Assets.emptyHeartTexture);
        lives = 3;
        dashSpeed=SPEED*2;
        movementSpeed=SPEED;
        bodyHitbox = new Rectangle();
        headHitbox = new Circle();
    }

    public void update(Array<Enemy> enemies, TiledMap tiledMap, Boolean isPaused, Array<EnemyBullet> enemyBullets , Boolean inDialog) {
        if (!isPaused) {
            float deltaTime = Gdx.graphics.getDeltaTime();
            setStateTime(getStateTime()+deltaTime);
                dialogOrGameOver = false;

            if (!inDialog) {

                handleMovement(deltaTime);

                handleDash(deltaTime);

                checkTileCollisions(tiledMap);

                checkEnemyCollisions(enemies);

                checkBulletCollisions(enemyBullets);

                setTimeSinceLastLifeLost(getTimeSinceLastLifeLost()+deltaTime);

                handlePushBack(deltaTime);

            }
            else {
                dialogOrGameOver = true;
            }
        }
    }

    private void handleMovement(float deltaTime) {
        boolean moveLeft = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean moveRight = Gdx.input.isKeyPressed(Input.Keys.D);
        boolean moveUp = Gdx.input.isKeyPressed(Input.Keys.W);
        boolean moveDown = Gdx.input.isKeyPressed(Input.Keys.S);

        float potentialX = getPosition().x;
        float potentialY = getPosition().y;
        String direction = "";

        if (isDashing) {
            SPEED = dashSpeed;
        } else {
            SPEED = movementSpeed;
        }

        if (moveUp) {
            potentialY += SPEED * deltaTime;
            direction = "up";
        }
        if (moveDown) {
            potentialY -= SPEED * deltaTime;
            direction = "down";
        }
        if (moveLeft) {
            potentialX -= SPEED * deltaTime;
            if (!getIsFlipped()) {
                flipAnimations();
                setIsFlipped(true);
            }
            direction = "left";
        }
        if (moveRight) {
            potentialX += SPEED * deltaTime;
            if (getIsFlipped()) {
                flipAnimations();
                setIsFlipped(false);
            }
            direction = "right";
        }

        setIsWalking(direction);
        setPosition(new Vector2(potentialX,potentialY));
    }


    private void checkTileCollisions(TiledMap tiledMap) {

        float buffedPotentialX = getPosition().x;
        float buffedPotentialY = getPosition().y;

        if (isTileBlocked(buffedPotentialX, getPosition().y, tiledMap) && isTileBlocked(getPosition().x, buffedPotentialY, tiledMap)) {
            bodyHitbox.set(buffedPotentialX + getWidth() * 29 / 100, buffedPotentialY + getHeight() * 10 / 100,
                    (float) (getWidth() * 41.66 / 100), (float) (getHeight() * 31.25 / 100));
            headHitbox.set(buffedPotentialX + getWidth() / 2, (float) (buffedPotentialY + getHeight() / 1.7),
                    (float) (getWidth() / 3.1));
        }
    }

    private void checkEnemyCollisions(Array<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            if (isCollidingWithEnemy(enemy)) {
                if (getTimeSinceLastLifeLost() >= 5.0f) {
                    loseLife();
                }
            }
        }
    }

    private void checkBulletCollisions(Array<EnemyBullet> enemyBullets) {
        for (EnemyBullet enemyBullet : enemyBullets) {
            if (isCollidingWithBullet(enemyBullet) && getTimeSinceLastLifeLost() >= 5.0f) {
                loseLife();
                Vector2 bulletDirection = new Vector2(getPosition()).sub(enemyBullet.getPosition()).nor(); // Reverse the direction
                pushBackDirection.set(bulletDirection);
                pushBackTime = 0.5f;
                enemyBullet.setAlive(false);
            }
        }
    }

    private void handlePushBack(float deltaTime) {
        if (pushBackTime > 0f) {
            pushBackTime -= deltaTime;
            float PUSH_BACK_FORCE = 50.0f;
            getPosition().add(pushBackDirection.scl(PUSH_BACK_FORCE * deltaTime));
        }
    }



    //change it -- to work
    public void loseLife() {
        lives++;
        lostLives++;
        setTimeSinceLastLifeLost(0);
    }

    private boolean isCollidingWithEnemy(Enemy enemy) {
        float characterLeft = getPosition().x;
        float characterRight = getPosition().x + getWidth();
        float characterTop = getPosition().y + getHeight();
        float characterBottom = getPosition().y;

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

    @Override
    protected TextureRegion getCurrentFrame(){
        if(dialogOrGameOver)
            return getIdleAnimationLeftAndRight().getKeyFrame(getStateTime(), true);
        else{
          return super.getCurrentFrame();
        }
    }

    public Integer getLives(){return lives;}

}