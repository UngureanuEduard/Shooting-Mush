package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class EnemyBoss extends Enemy{

    private final Vector2 position;
    private final Vector2 playerPosition;
    private final Animation<TextureRegion> walkAnimation;

    private final Animation<TextureRegion> idleAnimation;
    private float stateTime;
    private float shootTimer = 0.0f;

    private float bossMovementTimer = 0.0f;
    private boolean isBossMoving = true;
    private final Integer soundVolume;
    Assets assets;
    private boolean isFlipped = false;

    private final Rectangle bodyHitbox;
    private final Circle headHitbox;
    private final ShapeRenderer shapeRenderer;


    public EnemyBoss(Vector2 position, Vector2 playerPosition,float health,Assets assets,Integer soundVolume,Integer critRate) {
        super(position, playerPosition,health,assets,soundVolume,critRate);
        this.position=position;
        this.playerPosition=playerPosition;
        this.soundVolume=soundVolume;
        this.assets=assets;
        bodyHitbox = new Rectangle();
        headHitbox = new Circle();
        shapeRenderer = new ShapeRenderer();

        Texture duckTexture;
        Texture duckIdleTexture;
        duckTexture =assets.getAssetManager().get(Assets.bossTexture);
        duckIdleTexture=assets.getAssetManager().get(Assets.idleBossTexture);
        TextureRegion[] duckIdleFrame=splitEnemyTexture(duckIdleTexture,4);
        idleAnimation = new Animation<>(0.1f, duckIdleFrame);
        TextureRegion[] duckFrames = splitEnemyTexture(duckTexture,6);
        walkAnimation = new Animation<>(0.1f, duckFrames);

        stateTime = 0.0f; // Initialize the animation time
        System.out.println(healthScale);
    }

    @Override
    public Vector2 update(float deltaTime,Array<EnemyBullet> enemyBullets, Array<CharacterBullet> Characterbullets, Array<Enemy> enemies, boolean isPaused) {
        if(!isPaused) {
            // Calculate the direction from the enemy to the player
            Vector2 direction = playerPosition.cpy().sub(position).nor();

            float movementSpeed = 60.0f; // Adjust the speed

            if (isBossMoving) {
                bossMovementTimer += deltaTime;
                if (bossMovementTimer >= 3.0f) {
                    isBossMoving = false;
                    bossMovementTimer = 0.0f;
                }
                position.add(direction.x * movementSpeed * deltaTime, direction.y * movementSpeed * deltaTime);
            } else{
                bossMovementTimer += deltaTime;
                if (bossMovementTimer >= 11.0f) {
                    isBossMoving = true;
                    bossMovementTimer = 0.0f;
                }
            }

            bodyHitbox.set((float) (position.x+getWidth()/3.8*healthScale), position.y+getHeight()/10*healthScale, (float) (getWidth()/2.3)*healthScale, (float) (getHeight()/2.7*healthScale)); // Body hitbox (rectangle)
            headHitbox.set(position.x+getWidth()/2*healthScale, (float) (position.y+getHeight()/1.5*healthScale), getHeight()/5*healthScale); // Head hitbox (circle)

            // Update animation stateTime
            stateTime += deltaTime;

            isFlipped = direction.x < 0;

            // Determine if the enemy should be flipped

            Vector2 CollisionPosition = CheckBulletCollisions(Characterbullets, enemies);
            if(CollisionPosition.x!=-1&&CollisionPosition.y!=-1)
                return CollisionPosition;

            shootTimer += deltaTime;
            float shootInterval = 1.0f;
            if(!isBossMoving&&shootTimer >= shootInterval){

                shootBulletsInAllDirections(enemyBullets);
                shootTimer=0f;
            }


        }
        return new Vector2(-1, -1);
    }

    @Override
    public void render(SpriteBatch batch, OrthographicCamera camera) {
        TextureRegion currentFrame = getCurrentFrame();
        float scaledWidth = calculateScaledDimension(getWidth());
        float scaledHeight = calculateScaledDimension(getHeight());

        drawCurrentFrame(batch, currentFrame, scaledWidth, scaledHeight ,isFlipped);
        renderDamageTexts(batch, Gdx.graphics.getDeltaTime());
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

    private TextureRegion getCurrentFrame() {
        if (isBossMoving) {
            return walkAnimation.getKeyFrame(stateTime, true);
        } else {
            return idleAnimation.getKeyFrame(stateTime, true);
        }
    }

    private void shootBulletsInAllDirections(Array<EnemyBullet> bullets) {
        float bulletSpeed = 450.0f;
        float angle= MathUtils.random(0,3);

        for (; angle < 360; angle += 15) {
            Vector2 direction = new Vector2(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle));
            EnemyBullet bullet = new EnemyBullet(new Vector2(position.x+getWidth(),position.y), direction.cpy().scl(bulletSpeed), 1, assets,soundVolume);
            bullets.add(bullet);
        }
    }


}
