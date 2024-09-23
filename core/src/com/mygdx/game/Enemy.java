package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

public  class Enemy {
    private final Vector2 position;
    private final Vector2 playerPosition;
    private final Animation<TextureRegion> walkAnimation;

    private float stateTime;
    private boolean isFlipped = false;
    private float health;
    private final float healthScale;
    private final Sound sound;
    private float shootTimer = 0.0f;

    private final Integer soundVolume;
    Array<DamageText> damageTexts = new Array<>();
    Assets assets;
    private final BitmapFont defaultFont;

    private final Integer critRate;


    public Enemy(Vector2 position, Vector2 playerPosition,float health,Assets assets ,Integer soundVolume,Integer critRate) {
        this.assets=assets;
        this.health = health;
        this.position = position;
        this.playerPosition = playerPosition;
        this.soundVolume=soundVolume;
        this.critRate=critRate;

        defaultFont = new BitmapFont();

        Texture duckTexture;
        duckTexture = assets.getAssetManager().get(Assets.duckTexture);

        TextureRegion[] duckFrames = splitEnemyTexture(duckTexture,6);
        walkAnimation = new Animation<>(0.1f, duckFrames);
        this.sound=assets.getAssetManager().get(Assets.duckSound);
        stateTime = 0.0f; // Initialize the animation time
        this.healthScale = 0.7f + health/ 300.0f;
    }

    public Vector2 update(float deltaTime, Array<Bullet> bullets, Array<Enemy> enemies, boolean isPaused) {
        if (!isPaused) {
            // Calculate the direction from the enemy to the player
            Vector2 direction = playerPosition.cpy().sub(position).nor();
            final float MOVEMENT_SPEED = 60.0f; // Adjust the speed
            position.add(direction.x * MOVEMENT_SPEED * deltaTime, direction.y * MOVEMENT_SPEED * deltaTime);

            // Update animation stateTime
            stateTime += deltaTime;
            // Determine if the enemy should be flipped
            isFlipped = direction.x < 0;

            // Check for bullet collisions
            Vector2 CollisionPosition = CheckBulletCollisions(bullets, enemies);
            if(CollisionPosition.x!=-1&&CollisionPosition.y!=-1)
                return CollisionPosition;

            shootTimer += deltaTime;
            final float SHOOT_INTERVAL = 1.0f;
            if (shootTimer >= SHOOT_INTERVAL) {
                shootBullet(bullets);
                shootTimer = 0f;
            }
        }
        return new Vector2(-1, -1);
    }

    public Vector2 CheckBulletCollisions(Array<Bullet> bullets ,Array<Enemy> enemies ){
        for (Bullet bullet : bullets) {
            if(!bullet.getType().equals("Enemy")){
                if (isCollidingWithBullet(bullet)) {
                    if (takeDamage(bullet.getDamage(), enemies))
                        return new Vector2(this.position.x + this.getWidth() / 2, this.position.y + this.getHeight() / 2);
                    bullet.setActive(false); // Deactivate the bullet
                }
            }
        }
        return new Vector2(-1, -1);
    }


    public boolean isCollidingWithBullet(Bullet bullet) {
        // Check if the enemy's bounding box intersects with the bullet's position
        if(position.x < bullet.getPosition().x + bullet.getWidth() &&
                position.x + getWidth() > bullet.getPosition().x &&
                position.y < bullet.getPosition().y + bullet.getHeight() &&
                position.y + getHeight() > bullet.getPosition().y)
        {
            Boolean isCrit=isCrit();
            if(isCrit)
            {
                bullet.setDamage(bullet.getDamage()*4);
            }
            damageTexts.add(new DamageText(bullet.getDamage(),bullet.getPosition(),1f,isCrit));
            return true;
        }
        else return false;
    }

    public Boolean takeDamage(float damage,Array<Enemy> enemies) {
        health -= damage;
        if (health <= 0) {
            enemies.removeValue(this, true);
            return true;
        }
        sound.play(soundVolume/100f);
        return false;
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = getCurrentFrame();
        float scaledWidth = calculateScaledDimension(getWidth());
        float scaledHeight = calculateScaledDimension(getHeight());

        drawCurrentFrame(batch, currentFrame, scaledWidth, scaledHeight);
        renderDamageTexts(batch, Gdx.graphics.getDeltaTime());
    }

    private TextureRegion getCurrentFrame() {
        return walkAnimation.getKeyFrame(stateTime, true);
    }

    public float calculateScaledDimension(float dimension) {
        return dimension * healthScale;
    }

    public void drawCurrentFrame(SpriteBatch batch, TextureRegion currentFrame, float scaledWidth, float scaledHeight) {
        batch.draw(currentFrame, position.x, position.y, scaledWidth / 2, scaledHeight / 2, scaledWidth, scaledHeight, isFlipped ? -1 : 1, 1, 0);
    }

    public TextureRegion[] splitEnemyTexture(Texture characterTexture,int n) {
        TextureRegion[][] tmp = TextureRegion.split(characterTexture, 32, 32);
        TextureRegion[] characterFrames = new TextureRegion[n];
        System.arraycopy(tmp[0], 0, characterFrames, 0, n);
        return characterFrames;
    }

    private void shootBullet(Array<Bullet> bullets) {
        // Calculate the direction from the enemy to the player
        Vector2 direction = playerPosition.cpy().sub(position).nor();

        // Create a new Bullet and set the damage
        Bullet bullet = new Bullet(position.cpy(), direction.cpy().scl(400), 1, assets,"Enemy",soundVolume);

        // Add bullet to array
        bullets.add(bullet);
    }

    public float getWidth()
    {
        return 32*healthScale;
    }

    public float getHeight()
    {
        return 35*healthScale;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getHealthScale(){return healthScale;}

    public float getHealth(){return health;}

    public void renderDamageTexts(SpriteBatch batch, float deltaTime) {
        Array<DamageText> textsToRemove = new Array<>();

        for (DamageText damageText : damageTexts) {
            damageText.update(deltaTime);
            float newY = damageText.getPosition().y + 20 * deltaTime;
            damageText.getPosition().set(damageText.getPosition().x, newY);

            if(!damageText.getIsCrit()){
                defaultFont.draw(batch, damageText.getText(), damageText.getPosition().x, damageText.getPosition().y);
            }
            else {
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

    public Boolean isCrit (){
        Random random = new Random();
        int randomNumber = random.nextInt(100) + 1;
        return randomNumber <= critRate;
    }
}
