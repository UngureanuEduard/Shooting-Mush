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

    private final Animation<TextureRegion> walkAnimation;
    private final Animation<TextureRegion> idleAnimation;

    private float bossMovementTimer = 0.0f;
    private boolean isBossMoving = true;

    private final ShapeRenderer shapeRenderer;


    public EnemyBoss(Vector2 position, Vector2 playerPosition,float health,Assets assets,Integer soundVolume,Integer critRate) {
        super(position, playerPosition,health,assets,soundVolume,critRate);

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
    @Override
    protected TextureRegion getCurrentFrame() {
        if (isBossMoving) {
            return walkAnimation.getKeyFrame(stateTime, true);
        } else {
            return idleAnimation.getKeyFrame(stateTime, true);
        }
    }


    @Override
    protected void shootBullet(Array<EnemyBullet> bullets , float SHOOT_INTERVAL) {
        if(shootTimer >= SHOOT_INTERVAL&&!isBossMoving) {
            shootTimer=0;
            float bulletSpeed = 450.0f;
            float angle = MathUtils.random(0, 3);

            for (; angle < 360; angle += 15) {
                Vector2 direction = new Vector2(MathUtils.cosDeg(angle), MathUtils.sinDeg(angle));
                EnemyBullet bullet = new EnemyBullet(new Vector2(position.x + getWidth(), position.y), direction.cpy().scl(bulletSpeed), 1, assets, soundVolume);
                bullets.add(bullet);
            }
        }
    }

    @Override
    protected void specialBehavior(float deltaTime,Vector2 direction, float movementSpeed){

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

    }


}
