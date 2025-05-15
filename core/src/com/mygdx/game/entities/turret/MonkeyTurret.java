package com.mygdx.game.entities.turret;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.pool_managers.EnemyBulletsManager;
import com.mygdx.game.utilities_resources.Assets;

public class MonkeyTurret {
    private float angle;
    private final PIDController pid;
    private float timeSinceLastShot = 0f;

    private static final float RADAR_RANGE = 150f;
    private static final float RADAR_FOV = 35f;

    private final Vector2 position;
    private final Assets assets;
    private final Integer soundVolume;
    private final Animation<TextureRegion> animation;
    private float animationTime = 0f;
    private TextureRegion currentFrame;

    public MonkeyTurret(Vector2 position , Assets assets , Integer soundVolume) {
        this.position = position;
        this.assets = assets;
        this.soundVolume = soundVolume;

        TextureRegion[][] tmp = TextureRegion.split(assets.getAssetManager().get(Assets.turretMonkeyTexture), 32, 32);
        TextureRegion[] frames = new TextureRegion[18];
        System.arraycopy(tmp[0], 0, frames, 0, 18);
        animation = new Animation<>(0.1f, frames);

        pid = new PIDController(5f, 0f, 1.5f);
    }

    public void update(float deltaTime , Vector2 playerPosition , EnemyBulletsManager enemyBulletsManager) {
        animationTime += deltaTime;
        currentFrame = animation.getKeyFrame(animationTime, true);

        Vector2 toPlayer = new Vector2(playerPosition).sub(position);
        float desiredAngle = toPlayer.angleDeg();
        float rotationSpeed = pid.update(desiredAngle, angle, deltaTime);
        angle += rotationSpeed * deltaTime * 0.2f;
        angle = (angle + 360) % 360;

        timeSinceLastShot += deltaTime;
        // seconds
        float shootCooldown = 2f;
        if (isPlayerInRadarCone(playerPosition) && timeSinceLastShot >= shootCooldown) {
            shootAtPlayer(enemyBulletsManager ,playerPosition);
            timeSinceLastShot = 0f;
        }
    }

    private void shootAtPlayer(EnemyBulletsManager enemyBulletsManager , Vector2 playerPosition ) {
        Vector2 direction = new Vector2(playerPosition).sub(position).nor().scl(100f);
        enemyBulletsManager.generateBullet(position.cpy().add(new Vector2(6,6)), direction, 1, assets, soundVolume , 4 ,null);
    }

    private boolean isPlayerInRadarCone(Vector2 playerPosition) {
        Vector2 toPlayerDir = new Vector2(playerPosition).sub(position);
        float distance = toPlayerDir.len();
        if (distance > RADAR_RANGE) return false;

        float angleToPlayer = toPlayerDir.angleDeg();
        float angleDiff = Math.abs(MathUtils.atan2Deg(
                MathUtils.sinDeg(angleToPlayer - angle),
                MathUtils.cosDeg(angleToPlayer - angle)
        ));

        return angleDiff <= RADAR_FOV / 2f;
    }

    public void renderRadar(ShapeRenderer shapeRenderer) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setColor(0f, 1f, 0f, 0.15f);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.arc(
                position.x + 18,
                position.y + 19,
                RADAR_RANGE,
                angle - RADAR_FOV / 2,
                RADAR_FOV
        );
        shapeRenderer.end();
    }

    public void render(SpriteBatch batch) {
        batch.draw(currentFrame, position.x, position.y, 32, 32);
    }
}
