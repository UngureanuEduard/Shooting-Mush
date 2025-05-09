package com.mygdx.game.entities.character;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.combat_system.EnemyBullet;
import com.mygdx.game.utilities_resources.Assets;

public class BasicCharacter {
    private final Animation<TextureRegion> idleAnimationLeftAndRight;
    private final Animation<TextureRegion> walkLeftAnimation;
    private final Animation<TextureRegion> walkRightAnimation;
    private final Animation<TextureRegion> walkAnimationFront;
    private final Animation<TextureRegion> walkAnimationBack;
    private final Vector2 position;
    private float stateTime;
    private String isWalking;
    private float timeSinceLastLifeLost = 5.0f;
    private final Rectangle bodyHitbox;
    private final Circle headHitbox;

    public BasicCharacter(Vector2 position , Assets assets) {
        this.position = position;
        bodyHitbox = new Rectangle();
        headHitbox = new Circle();
        Texture walkTexture = assets.getAssetManager().get(Assets.walkTexture);
        Texture idleTexture = assets.getAssetManager().get(Assets.idleTexture);
        Texture walkFrontTexture = assets.getAssetManager().get(Assets.walkFrontTexture);
        Texture walkBackTexture = assets.getAssetManager().get(Assets.walkBackTexture);
        TextureRegion[] walkFrontFrames = splitCharacterTexture(walkFrontTexture,4);
        TextureRegion[] walkBackFrames = splitCharacterTexture(walkBackTexture,4);
        TextureRegion[] idleFrames = splitCharacterTexture(idleTexture,9);
        TextureRegion[] walkFrames = splitCharacterTexture(walkTexture, 4);

        TextureRegion[] walkFramesFlipped = new TextureRegion[walkFrames.length];
        for (int i = 0; i < walkFrames.length; i++) {
            walkFramesFlipped[i] = new TextureRegion(walkFrames[i]);
            walkFramesFlipped[i].flip(true, false);
        }
        walkLeftAnimation = new Animation<>(0.1f, walkFramesFlipped);
        walkRightAnimation = new Animation<>(0.1f, walkFrames);
        walkAnimationFront = new Animation<>(0.1f, walkFrontFrames);
        walkAnimationBack = new Animation<>(0.1f, walkBackFrames);
        idleAnimationLeftAndRight = new Animation<>(0.1f, idleFrames);
        isWalking = "";

    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = getCurrentFrame();

        float drawX = position.x;
        float drawWidth = getWidth();

        if (timeSinceLastLifeLost >= 5.0f) {
            batch.draw(currentFrame, drawX, position.y, drawWidth, getHeight());
        } else {
            float flashTime = 0.3f;
            if ((int) (timeSinceLastLifeLost / flashTime) % 2 == 0) {
                batch.draw(currentFrame, drawX, position.y, drawWidth, getHeight());
            }
        }
    }

    private TextureRegion[] splitCharacterTexture(Texture characterTexture, int n) {
        TextureRegion[][] tmp = TextureRegion.split(characterTexture, 48, 48);
        TextureRegion[] characterFrames = new TextureRegion[n];
        for (int i = 0; i < n; i++) {
            characterFrames[i] = new TextureRegion(tmp[0][i]);
        }
        return characterFrames;
    }

    protected TextureRegion getCurrentFrame(){
            switch (isWalking) {
                case "left":
                    return walkLeftAnimation.getKeyFrame(stateTime, true);
                case "right":
                    return walkRightAnimation.getKeyFrame(stateTime, true);
                case "up":
                    return walkAnimationBack.getKeyFrame(stateTime, true);
                case "down":
                    return walkAnimationFront.getKeyFrame(stateTime, true);
                default:
                    return idleAnimationLeftAndRight.getKeyFrame(stateTime, true);
            }

    }

    @SuppressWarnings("unchecked")
    public void dispose() {
        Array<Texture> disposed = new Array<>();

        for (Animation<TextureRegion> animation : new Animation[]{
                idleAnimationLeftAndRight,
                walkLeftAnimation,
                walkRightAnimation,
                walkAnimationFront,
                walkAnimationBack
        }) {
            for (TextureRegion region : animation.getKeyFrames()) {
                Texture texture = region.getTexture();
                if (!disposed.contains(texture, true)) {
                    texture.dispose();
                    disposed.add(texture);
                }
            }
        }
    }


    protected void checkBulletCollisions(Array<EnemyBullet> enemyBullets) {
        for (EnemyBullet enemyBullet : enemyBullets) {
            if (isCollidingWithBullet(enemyBullet) && getTimeSinceLastLifeLost() >= 5.0f) {
                colidedWithBullet(enemyBullet);
            }
        }
    }

    protected void colidedWithBullet(EnemyBullet enemyBullet){
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


    public float getWidth() {
        return 24;
    }

    public float getHeight() {
        return 24;
    }

    public float getTimeSinceLastLifeLost() {
        return timeSinceLastLifeLost;
    }

    public void setTimeSinceLastLifeLost(float timeSinceLastLifeLost) {
        this.timeSinceLastLifeLost = timeSinceLastLifeLost;
    }

    public String getIsWalking() {
        return isWalking;
    }

    public void setIsWalking(String isWalking) {
        this.isWalking = isWalking;
    }

    public float getStateTime() {
        return stateTime;
    }

    public void setStateTime(float stateTime) {
        this.stateTime = stateTime;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position.set(position);
    }

    public Animation<TextureRegion> getIdleAnimationLeftAndRight() {
        return idleAnimationLeftAndRight;
    }

    public void updateHitboxes() {
        bodyHitbox.set(
                position.x + getWidth() * 0.25f,
                position.y+getHeight()*0.1f,
                getWidth() * 0.5f,
                getHeight() * 0.5f
        );

        headHitbox.set(
                position.x + getWidth() * 0.5f,
                position.y + getHeight() * 0.55f,
                getHeight() * 0.38f
        );
    }



}
