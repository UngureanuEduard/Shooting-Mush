package com.mygdx.game.entities.enemy;

import box2dLight.RayHandler;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.GameScene;
import com.mygdx.game.pool_managers.EnemyBulletsManager;
import com.mygdx.game.utilities_resources.Assets;

import java.util.Random;

public class Enemy implements Pool.Poolable{

    public static final float MOVEMENT_SPEED = 15.0f;
    private static final float BULLET_COOLDOWN = 5.0f;
    private static final float SCALE = 0.8f;
    private static final Random RANDOM = new Random();

    private Vector2 position;
    private Vector2 playerPosition;
    private Texture walkTexture;
    private Texture idleTexture;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> idleAnimation;
    private float stateTime;
    private boolean isFlipped = false;
    private float health;
    private boolean isDamaged = false;
    private float maxHealth;
    private float sizeScale;
    private Sound sound;
    private float shootTimer = 5.0f;
    private Integer soundVolume;
    private Assets assets;
    private Integer critRate;
    private float damagedDelay = 0.0f;
    private Rectangle bodyHitbox;
    private Circle headHitbox;
    private Vector2 spawnPosition;
    public enum BehaviorStatus {
        MOVING,
        IDLE
    }
    private BehaviorStatus behaviorStatus;
    private Texture healthBarBackgroundTexture;
    private Texture healthBarForegroundTexture;
    private boolean alive;
    private boolean isAttacked;
    private final Vector2 pushBackDirection = new Vector2(0, 0);
    private float pushBackTime = 0f;
    private float PUSH_BACK_FORCE;
    private static int nextId = 0;
    private int id;
    private boolean markedForRemoval = false;
    private boolean lastHitByHost;
    public enum EnemyState {
        IDLE, WANDERING, MOVING_TO_PLAYER, SHOOTING
    }
    private GameScene.GameMode gameMode;
    private EnemyState currentState;

    public Enemy(){
        alive=false;
        isAttacked=false;
        position=new Vector2();
    }

    @Override
    public void reset() {
        position.set(-1,-1);
        alive = false;
        isAttacked = false;
    }

    public void init(Vector2 position, Vector2 playerPosition, float health, Assets assets, Integer soundVolume, Integer critRate , GameScene.GameMode gameMode , int mapIndex){
        this.assets = assets;
        this.health = health;
        this.maxHealth = health;
        this.position = position;
        this.spawnPosition = position.cpy();
        this.playerPosition = playerPosition;
        this.soundVolume = soundVolume;
        this.critRate = critRate;
        this.gameMode = gameMode;
        this.currentState = (gameMode == GameScene.GameMode.STORY) ? EnemyState.WANDERING : EnemyState.MOVING_TO_PLAYER;
        alive = true;
        isAttacked = false;
        sizeScale = SCALE;
        bodyHitbox = new Rectangle();
        headHitbox = new Circle();
        loadEnemyTextures(mapIndex);
        stateTime = 0.0f;
        damagedDelay = 0.0f;
        PUSH_BACK_FORCE = 50.0f;
        id = nextId++;
        markedForRemoval = false;
    }

    protected void loadEnemyTextures(int mapIndex){
        if(mapIndex == 1){
            walkTexture = assets.getAssetManager().get(Assets.skeletonWalkTexture);
            idleTexture = assets.getAssetManager().get(Assets.skeletonIdleTexture);
            TextureRegion[] walkingFrames = splitEnemyTexture(walkTexture, 4 ,48 ,48);
            TextureRegion[] idleFrames = splitEnemyTexture(idleTexture, 6,48,48);
            walkAnimation = new Animation<>(0.1f, walkingFrames);
            idleAnimation = new Animation<>(0.1f, idleFrames);
            sound = assets.getAssetManager().get(Assets.skeletonSound);
            sizeScale += 0.2f;
        } else if(mapIndex ==0 ){
            walkTexture = assets.getAssetManager().get(Assets.duckTexture);
            idleTexture = assets.getAssetManager().get(Assets.idleEnemyTexture);
            TextureRegion[] walkingFrames = splitEnemyTexture(walkTexture, 6 ,32 ,32);
            TextureRegion[] idleFrames = splitEnemyTexture(idleTexture, 4,32,32);
            walkAnimation = new Animation<>(0.1f, walkingFrames);
            idleAnimation = new Animation<>(0.1f, idleFrames);
            sound = assets.getAssetManager().get(Assets.duckSound);
        }
        else {
            walkTexture = assets.getAssetManager().get(Assets.zombieWalkTexture);
            idleTexture = assets.getAssetManager().get(Assets.zombieIdleTexture);
            TextureRegion[] walkingFrames = splitEnemyTexture(walkTexture, 4 ,48 ,48);
            TextureRegion[] idleFrames = splitEnemyTexture(idleTexture, 7,48,48);
            walkAnimation = new Animation<>(0.1f, walkingFrames);
            idleAnimation = new Animation<>(0.1f, idleFrames);
            sound = assets.getAssetManager().get(Assets.zombieSound);
            sizeScale += 0.2f;
        }
        healthBarBackgroundTexture = assets.getAssetManager().get(Assets.EnemyHealthBarTexture);
        healthBarForegroundTexture = assets.getAssetManager().get(Assets.EnemyHealthTexture);
    }

    public void update(float deltaTime, EnemyBulletsManager enemyBulletsManager, boolean isPaused, Array<Enemy> enemies
    , int mapIndex  , RayHandler rayHandler ) {
        if (!isPaused) {
            boolean isColliding = isCollidingWithEnemy(enemies);

            if (!isColliding) {
                simpleAI(deltaTime , enemyBulletsManager , mapIndex , rayHandler);
            }

            updateHitboxes();

            stateTime += deltaTime;

            shootTimer += deltaTime;

            damagedDelay += deltaTime;

            if (pushBackTime < 0.5f) {
                pushBackTime += deltaTime;
                position.add(pushBackDirection.scl(PUSH_BACK_FORCE * deltaTime));
            }
        }
    }

    protected void simpleAI(float deltaTime , EnemyBulletsManager enemyBulletsManager , int mapIndex , RayHandler rayHandler) {
        float distanceToPlayer = playerPosition.dst(position);

        switch (gameMode) {
            case ARENA:
                if (distanceToPlayer < 200f) {
                    currentState = EnemyState.SHOOTING;
                } else {
                    currentState = EnemyState.MOVING_TO_PLAYER;
                }
                break;

            case STORY:
                if (distanceToPlayer < 150f || isAttacked) {
                    isAttacked = true ;
                    currentState = (distanceToPlayer < 100f) ? EnemyState.SHOOTING : EnemyState.MOVING_TO_PLAYER;
                } else {
                    currentState = EnemyState.WANDERING;
                }
                break;
        }

        switch (currentState) {
            case SHOOTING:
                if (shootTimer >= BULLET_COOLDOWN) {
                    shootTimer = 0;
                    shootBullet(enemyBulletsManager , mapIndex , rayHandler);
                }
                setBehaviorStatus(BehaviorStatus.IDLE);
                break;
            case MOVING_TO_PLAYER:
                moveTowards(playerPosition, deltaTime);
                setBehaviorStatus(BehaviorStatus.MOVING);
                break;
            case WANDERING:
                wander();
                setBehaviorStatus(BehaviorStatus.MOVING);
                break;
            default:
                setBehaviorStatus(BehaviorStatus.IDLE);
        }
    }

    protected void moveTowards(Vector2 target, float deltaTime) {
        Vector2 direction = target.cpy().sub(position).nor();
        position.add(direction.scl(MOVEMENT_SPEED * deltaTime));
        setIsFlipped(target.x < position.x);
    }

    private void wander() {
        float wanderAmplitude = 10f;
        float wanderSpeed = 1.5f;
        float offsetX = (float) Math.sin(stateTime * wanderSpeed) * wanderAmplitude;
        float previousX = position.x;
        position.x = spawnPosition.x + offsetX;

        if (position.x < previousX) {
            setIsFlipped(true);
        } else if (position.x > previousX) {
            setIsFlipped(false);
        }
    }

    private void updateHitboxes() {
        bodyHitbox.set(position.x + getWidth() / 3.8f * sizeScale, position.y + getHeight() / 10.0f * sizeScale, getWidth() / 2.3f * sizeScale, getHeight() / 2.7f * sizeScale);
        headHitbox.set(position.x + getWidth() / 2.0f * sizeScale, position.y + getHeight() / 1.5f * sizeScale, getHeight() / 5.0f * sizeScale);
    }

    public boolean isCollidingWithEnemy(Array<Enemy> enemies) {
        Array<Enemy> enemiesCopy = new Array<>(enemies);
        for (Enemy otherEnemy : enemiesCopy) {
            if (otherEnemy != this) {
                if (Intersector.overlaps(bodyHitbox, otherEnemy.bodyHitbox) ||
                        Intersector.overlaps(headHitbox, otherEnemy.headHitbox)) {
                    resolveCollisionWithEnemy(otherEnemy);
                    return true;
                }
            }
        }
        return false;
    }

    private void resolveCollisionWithEnemy(Enemy otherEnemy) {
        Vector2 collisionVector = new Vector2();

        if (Intersector.overlaps(bodyHitbox, otherEnemy.bodyHitbox)) {
            collisionVector.set(otherEnemy.bodyHitbox.x - bodyHitbox.x, otherEnemy.bodyHitbox.y - bodyHitbox.y);
        } else {
            collisionVector.set(otherEnemy.headHitbox.x - headHitbox.x, otherEnemy.headHitbox.y - headHitbox.y);
        }

        collisionVector.nor().scl(0.5f);

        this.position.sub(collisionVector);
        otherEnemy.position.add(collisionVector);
    }

    public void takeDamage(float damage) {
        health -= damage;
        if(damagedDelay >= 2.0f)
        {
            sound.play(soundVolume / 100f);
            damagedDelay = 0;
        }
        if (health <= 0) {
            alive = false;
        }
    }

    public void render(SpriteBatch batch ) {
        TextureRegion currentFrame = getCurrentFrame();
        float scaledWidth = calculateScaledDimension(getWidth());
        float scaledHeight = calculateScaledDimension(getHeight());

        drawCurrentFrame(batch, currentFrame, scaledWidth, scaledHeight, isFlipped);

        if(isDamaged || isAttacked ){
            renderHealthBar(batch);
        }
    }

    protected void renderHealthBar(SpriteBatch batch) {
        float healthPercentage = Math.max(0, health) / maxHealth;
        float barX = position.x;
        float barY = position.y;
        float barWidth = getWidth() * 0.8f;
        float barHeight = getHeight() * 0.4f;

        batch.draw(healthBarBackgroundTexture, barX, barY+getHeight()*0.8f, barWidth, barHeight*0.8f);

        batch.draw(healthBarForegroundTexture, (float) (barX+1.8),barY+getHeight()*0.93f , barWidth * healthPercentage*0.83f, barHeight*0.25f);
    }


    public TextureRegion getCurrentFrame() {
        if(behaviorStatus == BehaviorStatus.IDLE)
        return idleAnimation.getKeyFrame(stateTime, true);
            else return walkAnimation.getKeyFrame(stateTime, true);
    }

    protected float calculateScaledDimension(float dimension) {
        return dimension * sizeScale;
    }

    protected void drawCurrentFrame(SpriteBatch batch, TextureRegion currentFrame, float scaledWidth, float scaledHeight, boolean isFlipped) {
        batch.draw(currentFrame, position.x, position.y, scaledWidth / 2, scaledHeight / 2, scaledWidth, scaledHeight, isFlipped ? -1 : 1, 1, 0);
    }

    protected TextureRegion[] splitEnemyTexture(Texture characterTexture, int n , int tileWidth , int tileHeight) {
        TextureRegion[][] tmp = TextureRegion.split(characterTexture, tileWidth, tileHeight);
        TextureRegion[] characterFrames = new TextureRegion[n];
        System.arraycopy(tmp[0], 0, characterFrames, 0, n);
        return characterFrames;
    }

    public void shootBullet( EnemyBulletsManager enemyBulletsManager , int mapIndex ,  RayHandler rayHandler) {
        Vector2 direction = playerPosition.cpy().sub(position).nor().scl(110f);
        enemyBulletsManager.generateBullet(position.cpy(), direction, 1, assets, soundVolume , mapIndex ,rayHandler);
    }

    public float getWidth() {
        return 32 * sizeScale;
    }

    public float getHeight() {
        return 35 * sizeScale;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getSizeScale() {
        return sizeScale;
    }

    public float getHealth() {
        return health;
    }

    public Boolean isCrit() {
        int randomNumber = RANDOM.nextInt(100) + 1;
        return randomNumber <= critRate;
    }

    public void setBehaviorStatus(BehaviorStatus behaviorStatus) {
        this.behaviorStatus = behaviorStatus;
    }

    public boolean isAlive(){
        return alive;
    }

    public boolean getIsAttacked() {
        return isAttacked;
    }

    public void setIsAttacked(boolean setAttacked) {
        isAttacked = setAttacked;
    }

    public void setIsFlipped(boolean setFlipped) {
        isFlipped = setFlipped;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public void setFlipped(boolean flipped) {
        isFlipped = flipped;
    }

    public boolean isDamaged() {
        return isDamaged;
    }

    public void setIsDamaged(boolean damaged) {
        isDamaged = damaged;
    }
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }

    public void setMarkedForRemoval(boolean value) {
        this.markedForRemoval = value;
    }

    public BehaviorStatus getBehaviorStatus() {
        return behaviorStatus;
    }

    public float getStateTime() {
        return stateTime;
    }

    public void updatePlayerPosition(Vector2 hostPos, Vector2 guestPos) {
        playerPosition = lastHitByHost ? hostPos : guestPos;
    }

    public Rectangle getBodyHitbox() {
        return bodyHitbox;
    }

    public Circle getHeadHitbox() {
        return headHitbox;
    }

    public void setLastHitByHost(boolean lastHitByHost) {
        this.lastHitByHost = lastHitByHost;
    }


    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public Texture getWalkTexture() {
        return walkTexture;
    }

    public void setWalkTexture(Texture walkTexture) {
        this.walkTexture = walkTexture;
    }

    public Texture getIdleTexture() {
        return idleTexture;
    }

    public void setIdleTexture(Texture idleTexture) {
        this.idleTexture = idleTexture;
    }

    public Animation<TextureRegion> getWalkAnimation() {
        return walkAnimation;
    }

    public void setWalkAnimation(Animation<TextureRegion> walkAnimation) {
        this.walkAnimation = walkAnimation;
    }

    public Animation<TextureRegion> getIdleAnimation() {
        return idleAnimation;
    }

    public void setIdleAnimation(Animation<TextureRegion> idleAnimation) {
        this.idleAnimation = idleAnimation;
    }

    public void setStateTime(float stateTime) {
        this.stateTime = stateTime;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public void setMaxHealth(float maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void setSizeScale(float sizeScale) {
        this.sizeScale = sizeScale;
    }

    public Integer getSoundVolume() {
        return soundVolume;
    }

    public void setSoundVolume(Integer soundVolume) {
        this.soundVolume = soundVolume;
    }

    public Assets getAssets() {
        return assets;
    }

    public void setAssets(Assets assets) {
        this.assets = assets;
    }

    public Vector2 getPushBackDirection() {
        return pushBackDirection;
    }

    public void setPushBackTime(float pushBackTime) {
        this.pushBackTime = pushBackTime;
    }

    public void setPUSH_BACK_FORCE(float PUSH_BACK_FORCE) {
        this.PUSH_BACK_FORCE = PUSH_BACK_FORCE;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }

    public Vector2 getPlayerPosition() {
        return playerPosition;
    }
}