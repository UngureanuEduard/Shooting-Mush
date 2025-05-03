package com.mygdx.game.entities.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.GameScene;
import com.mygdx.game.animations_effects.DamageText;
import com.mygdx.game.combat_system.CharacterBullet;
import com.mygdx.game.pool_managers.EnemyBulletsManager;
import com.mygdx.game.utilities_resources.Assets;

import java.util.Random;

public class Enemy implements Pool.Poolable{

    public static final float MOVEMENT_SPEED = 15.0f;
    private static final float BULLET_COOLDOWN = 5.0f;
    protected static final float SCALE = 0.8f;
    private static final Random RANDOM = new Random();

    protected Vector2 position;
    protected Vector2 playerPosition;
    protected Texture duckTexture;
    protected Texture idleTexture;
    protected Animation<TextureRegion> walkAnimation;
    protected Animation<TextureRegion> idleAnimation;
    protected float stateTime;
    private boolean isFlipped = false;
    protected float health;
    private boolean isDamaged = false;
    protected float maxHealth;
    protected float sizeScale;
    protected Sound sound;
    protected float shootTimer = 5.0f;

    protected Integer soundVolume;
    Array<DamageText> damageTexts = new Array<>();
    protected Assets assets;
    protected BitmapFont defaultFont;

    protected Integer critRate;
    private float damagedDelay = 0.0f;
    protected Rectangle bodyHitbox;
    protected Circle headHitbox;
    protected Vector2 direction;
    private Vector2 spawnPosition;

    public enum BehaviorStatus {
        MOVING,
        IDLE
    }

    protected BehaviorStatus behaviorStatus;

    private Texture healthBarBackgroundTexture;
    private Texture healthBarForegroundTexture;
    private boolean alive;

    private boolean isAttacked;

    protected final Vector2 pushBackDirection = new Vector2(0, 0);
    protected float pushBackTime = 0f;
    protected float PUSH_BACK_FORCE;

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

    public void init(Vector2 position, Vector2 playerPosition, float health, Assets assets, Integer soundVolume, Integer critRate , GameScene.GameMode gameMode){
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
        defaultFont = new BitmapFont();
        loadEnemyTextures();
        TextureRegion[] duckFrames = splitEnemyTexture(duckTexture, 6);
        TextureRegion[] duckIdleFrames = splitEnemyTexture(idleTexture, 4);
        walkAnimation = new Animation<>(0.1f, duckFrames);
        idleAnimation = new Animation<>(0.1f, duckIdleFrames);
        sound = assets.getAssetManager().get(Assets.duckSound);
        stateTime = 0.0f;
        damagedDelay = 0.0f;
        PUSH_BACK_FORCE = 50.0f;
        id = nextId++;
    }

    protected void loadEnemyTextures(){
        duckTexture = assets.getAssetManager().get(Assets.duckTexture);
        idleTexture = assets.getAssetManager().get(Assets.idleEnemyTexture);
        healthBarBackgroundTexture = assets.getAssetManager().get(Assets.EnemyHealthBarTexture);
        healthBarForegroundTexture = assets.getAssetManager().get(Assets.EnemyHealthTexture);
    }

    public void update(float deltaTime, EnemyBulletsManager enemyBulletsManager, Array<CharacterBullet> characterBullets, boolean isPaused, Array<Enemy> enemies ) {
        if (!isPaused) {
            direction = playerPosition.cpy().sub(position).nor();
            boolean isColliding = isCollidingWithEnemy(enemies);

            if (!isColliding) {
                simpleAI(deltaTime , enemyBulletsManager);
            }

            updateHitboxes();

            stateTime += deltaTime;

            checkBulletCollisions(characterBullets);

            shootTimer += deltaTime;

            damagedDelay += deltaTime;

            if (pushBackTime < 0.5f) {
                pushBackTime += deltaTime;
                position.add(pushBackDirection.scl(PUSH_BACK_FORCE * deltaTime));
            }
        }
    }

    private void simpleAI(float deltaTime , EnemyBulletsManager enemyBulletsManager) {
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
                    shootBullet(enemyBulletsManager);
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

    private void moveTowards(Vector2 target, float deltaTime) {
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

    public void checkBulletCollisions(Array<CharacterBullet> bullets) {
        for (CharacterBullet bullet : bullets) {
            if (Intersector.overlaps(bullet.getHitBox(), headHitbox) || Intersector.overlaps(bullet.getHitBox(), bodyHitbox)) {
                boolean isCrit = isCrit();
                takeDamage(isCrit ? bullet.getDamage() * 4 : bullet.getDamage());
                damageTexts.add(new DamageText(bullet.getDamage(), bullet.getPosition().cpy(), 1f, isCrit));
                isDamaged = true;
                isAttacked = true;
                lastHitByHost = bullet.isFromHost();
                pushBackDirection.set(position).sub(bullet.getPosition()).nor();
                pushBackTime = 0f;
                bullet.setAlive(false);
            }
        }
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
        renderDamageTexts(batch, Gdx.graphics.getDeltaTime());

        if(isDamaged || isAttacked ){
            renderHealthBar(batch);
        }
    }

    protected void renderHealthBar(SpriteBatch batch) {
        float healthPercentage = Math.max(0, health) / maxHealth; // Assuming max health is 100
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

    protected TextureRegion[] splitEnemyTexture(Texture characterTexture, int n) {
        TextureRegion[][] tmp = TextureRegion.split(characterTexture, 32, 32);
        TextureRegion[] characterFrames = new TextureRegion[n];
        System.arraycopy(tmp[0], 0, characterFrames, 0, n);
        return characterFrames;
    }

    public void shootBullet( EnemyBulletsManager enemyBulletsManager ) {
        Vector2 direction = playerPosition.cpy().sub(position).nor().scl(110f);
        enemyBulletsManager.generateBullet(position.cpy(), direction, 1, assets, soundVolume);
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


    public void renderDamageTexts(SpriteBatch batch, float deltaTime) {
        Array<DamageText> textsToRemove = new Array<>();

        for (DamageText damageText : damageTexts) {
            damageText.update(deltaTime);
            float newY = damageText.getPosition().y + 20 * deltaTime;
            damageText.getPosition().set(damageText.getPosition().x, newY);
            float textScale = 0.5f;
            defaultFont.getData().setScale(textScale, textScale);

            if (!damageText.getIsCrit()) {
                defaultFont.draw(batch, damageText.getText(), damageText.getPosition().x, damageText.getPosition().y);
            } else {
                defaultFont.setColor(1, 0, 0, 1);
                defaultFont.draw(batch, damageText.getText(), damageText.getPosition().x, damageText.getPosition().y);
                defaultFont.setColor(1, 1, 1, 1);
            }


            if (damageText.isFinished()) {
                textsToRemove.add(damageText);
            }
        }

        damageTexts.removeAll(textsToRemove, true);
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


}