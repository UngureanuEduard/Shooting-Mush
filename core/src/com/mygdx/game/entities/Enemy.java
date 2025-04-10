package com.mygdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.BehaviorTree;
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
import com.mygdx.game.ai.ArenaEnemyBehaviorTree;
import com.mygdx.game.animations_effects.DamageText;
import com.mygdx.game.combat_system.CharacterBullet;
import com.mygdx.game.pool_managers.EnemyBulletsManager;
import com.mygdx.game.ai.StoryEnemyBehaviorTree;
import com.mygdx.game.utilities_resources.Assets;

import java.util.Random;

public class Enemy implements Pool.Poolable{

    public static final float MOVEMENT_SPEED = 10.0f;
    private static final float BULLET_COOLDOWN = 5.0f;
    private static final float SCALE = 0.8f;
    private static final Random RANDOM = new Random();
    private BehaviorTree<Enemy> behaviorTree;
    protected Vector2 position;
    protected Vector2 playerPosition;
    protected Texture duckTexture;
    protected Texture idleTexture;
    protected Animation<TextureRegion> walkAnimation;
    protected Animation<TextureRegion> idleAnimation;

    protected float stateTime;
    protected boolean isFlipped = false;
    protected float health;
    private boolean isDamaged = false;
    protected float maxHealth;
    protected float sizeScale;
    protected Sound sound;
    protected float shootTimer = 0.0f;

    protected Integer soundVolume;
    Array<DamageText> damageTexts = new Array<>();
    protected Assets assets;
    protected BitmapFont defaultFont;

    protected Integer critRate;
    private float damagedDelay = 0.0f;
    protected Rectangle bodyHitbox;
    protected Circle headHitbox;
    protected Vector2 direction;

    protected EnemyBulletsManager enemyBulletsManager;

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

    public Enemy(){
        this.alive=false;
        this.isAttacked=false;
        this.position=new Vector2();
    }

    @Override
    public void reset() {
        position.set(-1,-1);
        this.alive = false;
        this.isAttacked = false;
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
        this.alive = true;
        this.isAttacked = false;
        this.sizeScale = SCALE;
        this.bodyHitbox = new Rectangle();
        this.headHitbox = new Circle();
        this.defaultFont = new BitmapFont();
        loadEnemyTextures();
        TextureRegion[] duckFrames = splitEnemyTexture(duckTexture, 6);
        TextureRegion[] duckIdleFrames = splitEnemyTexture(idleTexture, 4);
        walkAnimation = new Animation<>(0.1f, duckFrames);
        idleAnimation = new Animation<>(0.1f, duckIdleFrames);
        this.sound = assets.getAssetManager().get(Assets.duckSound);
        stateTime = 0.0f; // Initialize the animation time
        damagedDelay = 0.0f;
        if(gameMode == GameScene.GameMode.STORY)
        behaviorTree = new StoryEnemyBehaviorTree(this);
        else behaviorTree = new ArenaEnemyBehaviorTree(this);
        PUSH_BACK_FORCE = 50.0f;
    }

    protected void loadEnemyTextures(){
        duckTexture = assets.getAssetManager().get(Assets.duckTexture);
        idleTexture = assets.getAssetManager().get(Assets.idleEnemyTexture);
        healthBarBackgroundTexture = assets.getAssetManager().get(Assets.EnemyHealthBarTexture);
        healthBarForegroundTexture = assets.getAssetManager().get(Assets.EnemyHealthTexture);
    }

    public void update(float deltaTime, EnemyBulletsManager enemyBulletsManager, Array<CharacterBullet> characterBullets, boolean isPaused, Array<Enemy> enemies ) {
        if (!isPaused) {
            this.enemyBulletsManager=enemyBulletsManager;
            // Calculate the direction from the enemy to the player
            direction = playerPosition.cpy().sub(position).nor();
            boolean isColliding = isCollidingWithEnemy(enemies);

            if (!isColliding) {
                behaviorTree.step();
            }

            updateHitboxes();

            // Update animation stateTime
            stateTime += deltaTime;

            // Check for bullet collisions
            CheckBulletCollisions(characterBullets);

            shootTimer += deltaTime;

            damagedDelay += deltaTime;

            if (pushBackTime < 0.5f) {
                pushBackTime += deltaTime;
                position.add(pushBackDirection.scl(PUSH_BACK_FORCE * deltaTime));
            }
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
                // Check for collision with body hitbox
                if (Intersector.overlaps(this.bodyHitbox, otherEnemy.bodyHitbox) ||
                        Intersector.overlaps(this.headHitbox, otherEnemy.headHitbox)) {
                    resolveCollisionWithEnemy(otherEnemy); // Resolve the collision
                    return true;
                }
            }
        }
        return false;
    }

    private void resolveCollisionWithEnemy(Enemy otherEnemy) {
        Vector2 collisionVector = new Vector2();

        // Determine the direction to resolve the collision
        if (Intersector.overlaps(this.bodyHitbox, otherEnemy.bodyHitbox)) {
            collisionVector.set(otherEnemy.bodyHitbox.x - this.bodyHitbox.x, otherEnemy.bodyHitbox.y - this.bodyHitbox.y);
        } else {
            collisionVector.set(otherEnemy.headHitbox.x - this.headHitbox.x, otherEnemy.headHitbox.y - this.headHitbox.y);
        }

        // Normalize and scale the collision vector
        collisionVector.nor().scl(0.5f);

        // Adjust positions to prevent overlapping
        this.position.sub(collisionVector);
        otherEnemy.position.add(collisionVector);
    }

    public void CheckBulletCollisions(Array<CharacterBullet> bullets) {
        for (CharacterBullet bullet : bullets) {
            if (isCollidingWithBullet(bullet)) {
                takeDamage(bullet.getDamage());
                isDamaged = true;
                isAttacked = true;
                Vector2 bulletDirection = new Vector2(position).sub(bullet.getPosition()).nor();
                pushBackDirection.set(bulletDirection);
                pushBackTime = 0f;
                bullet.setAlive(false);
            }
        }
    }

    public boolean isCollidingWithBullet(CharacterBullet bullet) {
        if (Intersector.overlaps(bullet.getHitBox(), headHitbox) || Intersector.overlaps(bullet.getHitBox(), bodyHitbox)) {
            Boolean isCrit = isCrit();
            if (isCrit) {
                bullet.setDamage(bullet.getDamage() * 4);
                PUSH_BACK_FORCE = PUSH_BACK_FORCE +10;
            }
            else PUSH_BACK_FORCE = 50.0f;
            damageTexts.add(new DamageText(bullet.getDamage(), new Vector2(bullet.getPosition()), 1f, isCrit));
            return true;
        } else return false;
    }

    public void takeDamage(float damage) {
        health -= damage;
        if(damagedDelay >= 2.0f)
        {
            sound.play(soundVolume / 100f);
            damagedDelay = 0;
        }
        if (health <= 0) {
            this.alive = false;
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


    protected TextureRegion getCurrentFrame() {
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

    public void shootBullet() {
        if (shootTimer >= BULLET_COOLDOWN) {
            shootTimer = 0;
            Vector2 direction = playerPosition.cpy().sub(position).nor();
            enemyBulletsManager.generateBullet(position.cpy(),direction.cpy().scl(200), 1, assets, soundVolume);
        }
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

    public Vector2 getPlayerPosition() {
        return playerPosition;
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
        return this.alive;
    }

    public Vector2 getSpawnPosition() {
        return spawnPosition;
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
}