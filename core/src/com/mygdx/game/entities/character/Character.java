    package com.mygdx.game.entities.character;

    import com.badlogic.gdx.Gdx;
    import com.badlogic.gdx.Input;
    import com.badlogic.gdx.graphics.OrthographicCamera;
    import com.badlogic.gdx.graphics.Texture;
    import com.badlogic.gdx.graphics.g2d.SpriteBatch;
    import com.badlogic.gdx.graphics.g2d.TextureRegion;
    import com.badlogic.gdx.math.*;
    import com.badlogic.gdx.utils.Array;
    import com.mygdx.game.combat_system.EnemyBullet;
    import com.mygdx.game.entities.enemy.Enemy;
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
        private Array<Rectangle> collisionRectangles;

        public Character(Vector2 initialPosition, Assets assets) {
            super(initialPosition, assets);
            heartTexture = assets.getAssetManager().get(Assets.heartTexture);
            emptyHeartTexture = assets.getAssetManager().get(Assets.emptyHeartTexture);
            lives = 3;
            dashSpeed=SPEED*2;
            movementSpeed=SPEED;

        }

        public void update(Array<Enemy> enemies , Boolean isPaused, Array<EnemyBullet> enemyBullets , Boolean inDialog , Boolean isChargingFireBall) {
            if (!isPaused) {
                float deltaTime = Gdx.graphics.getDeltaTime();
                setStateTime(getStateTime()+deltaTime);
                    dialogOrGameOver = false;

                if (!inDialog) {

                    if(!isChargingFireBall){
                        handleMovement(deltaTime);

                        handleDash(deltaTime);
                    }

                    updateHitboxes();

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

            Vector2 movement = new Vector2(0, 0);
            if (moveUp) movement.y += 1;
            if (moveDown) movement.y -= 1;

            if (moveLeft) {
                movement.x -= 1;
            }
            if (moveRight) {
                movement.x += 1;
            }

            if (movement.len() > 0) {
                movement.nor().scl(SPEED * deltaTime);
                potentialX += movement.x;
                potentialY += movement.y;
                direction = moveUp ? "up" : moveDown ? "down" : moveLeft ? "left" : "right";
            }

            setIsWalking(direction);

            Rectangle futureHitbox = new Rectangle(potentialX, potentialY, getWidth(), getHeight());

            boolean collides = false;
            if (collisionRectangles != null) {
                for (Rectangle rect : collisionRectangles) {
                    if (rect.overlaps(futureHitbox)) {
                        collides = true;
                        break;
                    }
                }
            }

            if (!collides) {
                setPosition(new Vector2(potentialX, potentialY));
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

        private void handlePushBack(float deltaTime) {
            if (pushBackTime > 0f) {
                pushBackTime -= deltaTime;
                float PUSH_BACK_FORCE = 50.0f;
                getPosition().add(pushBackDirection.scl(PUSH_BACK_FORCE * deltaTime));
            }
        }



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

        @Override
        protected TextureRegion getCurrentFrame(){
            if(dialogOrGameOver)
                return getIdleAnimationLeftAndRight().getKeyFrame(getStateTime(), true);
            else{
              return super.getCurrentFrame();
            }
        }

        public int getLives(){return lives;}

        @Override
        protected void colidedWithBullet(EnemyBullet enemyBullet){
            loseLife();
            Vector2 bulletDirection = new Vector2(getPosition()).sub(enemyBullet.getPosition()).nor();
            pushBackDirection.set(bulletDirection);
            pushBackTime = 0.5f;
            enemyBullet.setAlive(false);
        }

        public void setCollisionRectangles(Array<Rectangle> rectangles) {
            this.collisionRectangles = rectangles;
        }

    }