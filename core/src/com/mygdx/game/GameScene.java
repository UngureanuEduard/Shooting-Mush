package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Iterator;

public class GameScene extends ScreenAdapter {

    Viewport viewport = new ExtendViewport(1920, 1080);
    Skin skin;
    private final Stage stage = new Stage(viewport);
    SpriteBatch batch;
    TiledMap tiledMap;
    OrthogonalTiledMapRenderer tiledMapRenderer;
    OrthographicCamera camera;
    Character character;
    Array<EnemyBullet> enemyBullets;
    Array<CharacterBullet> characterBullets;
    private float minCameraX;
    private float minCameraY;
    private float maxCameraX;
    private float maxCameraY;
    private float enemySpawnTimer = 0.0f;
    Array<Enemy> enemies;
    private float timeSinceLastShot = 0.0f;
    public float damage = 5;
    Array<Wave> waves;
    Assets assets;
    Array<ParticleEffect> particleEffects;
    Boolean scaled = false;
    private int score = 0;
    private int enemiesLeftToKill;
    Image imageActor;
    Music gameMusic;

    Music bossMusic;
    boolean isPaused = false;
    private WaveCompleteTable waveCompleteTable;

    private Texture healthBarTexture;
    private Texture healthFillTexture;
    private float healthBarWidth;
    private float healthBarHeight;
    private float maxBossHealth;

    private final Integer musicVolume;
    private final Integer soundVolume;

    Wave currentWave;

    public Integer critRate = 15;

    MyGdxGame game;

    public GameScene(MyGdxGame game, Integer musicVolume, Integer soundVolume) {
        this.game = game;
        this.soundVolume = soundVolume;
        this.musicVolume = musicVolume;
    }

    @Override
    public void show() {

        assets = new Assets();
        assets.loadGameAssets();
        assets.getAssetManager().finishLoading();

        skin = assets.getAssetManager().get(Assets.skin);

        batch = new SpriteBatch();

        // Load the TiledMap
        tiledMap = assets.getAssetManager().get(Assets.tiledMap);

        // Create a TiledMapRenderer
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        // Create the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        float zoomLevel;
        zoomLevel = 0.25f;
        camera.zoom = zoomLevel;

        // Create character
        character = new Character(new Vector2(800, 800), assets);

        // Initialize bullets arrays
        enemyBullets = new Array<>();
        characterBullets = new Array<>();

        // Initialize enemies array
        enemies = new Array<>();

        //Initialize waves add a few
        waves = new Array<>();
        waves.add(new Wave(1, 0, 2, 0.5f, 90, damage));
        waves.add(new Wave(2, 1, 0, 0.4f, 500, damage));

        minCameraX = camera.viewportWidth / 2 - 480;
        minCameraY = camera.viewportHeight / 2 - 268;

        // 3200 = map size (w x h)
        maxCameraX = 1920 * tiledMapRenderer.getUnitScale() - camera.viewportWidth / 2 + 480;
        maxCameraY = 1920 * tiledMapRenderer.getUnitScale() - camera.viewportHeight / 2 + 268;

        // Initialize particles array
        particleEffects = new Array<>();

        imageActor = new Image(assets.getAssetManager().get(Assets.skullTexture));
        imageActor.setPosition((float) Gdx.graphics.getWidth() / 2 - imageActor.getWidth(), (float) (Gdx.graphics.getHeight() - Gdx.graphics.getHeight() / 10) + imageActor.getHeight() / 3);
        imageActor.setSize(imageActor.getWidth() / 1.5f, imageActor.getHeight() / 1.5f);
        enemiesLeftToKill = waves.first().getNumEnemies();

        gameMusic = assets.getAssetManager().get(Assets.gameMusic);
        gameMusic.setLooping(true);
        gameMusic.setVolume(musicVolume / 100f);
        gameMusic.play();

        bossMusic = assets.getAssetManager().get(Assets.bossMusic);
        bossMusic.setLooping(true);

        waveCompleteTable = new WaveCompleteTable(skin, assets, this);

        waveCompleteTable.center();

        // Calculate the position to center the table on the screen
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;

        // Set the table's position
        waveCompleteTable.setPosition(centerX - waveCompleteTable.getWidth() / 2f, centerY - waveCompleteTable.getHeight() / 2f);

        healthBarTexture = assets.getAssetManager().get(Assets.BorderHealthTexture);
        healthFillTexture = assets.getAssetManager().get(Assets.HealthTexture);

        healthBarWidth = (float) Gdx.graphics.getWidth() / 5;
        healthBarHeight = (float) Gdx.graphics.getHeight() / 36;
        maxBossHealth = 500;
    }

    @Override
    public void render(float delta) {

        // Clear the screen
        ScreenUtils.clear(1, 0, 0, 1);

        // Update character and camera
        character.update(enemies, tiledMap, isPaused, enemyBullets);
        updateCamera();

        timeSinceLastShot += Gdx.graphics.getDeltaTime();

        // Handle shooting bullets if the cooldown has expired , 0.2f = cooldown

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && timeSinceLastShot >= 0.2f && !isPaused) {
            shootBullet();
            timeSinceLastShot = 0.0f;
        }

        // Set the camera's projection matrix
        batch.setProjectionMatrix(camera.combined);

        for (ParticleEffect particle : particleEffects) {
            particle.update(Gdx.graphics.getDeltaTime());
        }

        // Begin the batch
        batch.begin();

        // Render the map
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        // Render the character
        character.render(batch, camera);

        batch.begin();


        // Render the bullets
        for (Iterator<EnemyBullet> iter = enemyBullets.iterator(); iter.hasNext(); ) {
            EnemyBullet enemyBullet = iter.next();
            enemyBullet.update(Gdx.graphics.getDeltaTime());
            if (enemyBullet.isActive()) {
                iter.remove(); // Remove the inactive bullet
            } else {
                enemyBullet.render(batch, camera);
            }
        }

        // Render the bullets
        for (Iterator<CharacterBullet> iter = characterBullets.iterator(); iter.hasNext(); ) {
            CharacterBullet characterBullet = iter.next();
            characterBullet.update(Gdx.graphics.getDeltaTime());
            if (characterBullet.isActive()) {
                iter.remove(); // Remove the inactive bullet
            } else {
                characterBullet.render(batch, camera);
            }
        }


        if (!waves.isEmpty()) {

            currentWave = waves.first();
            enemySpawnTimer += Gdx.graphics.getDeltaTime();
            damage = currentWave.getBulletDamage();

            if (currentWave.getNumEnemies() > 0 && enemySpawnTimer >= currentWave.getEnemySpawnInterval() && !isPaused) {
                spawnEnemy(currentWave.getEnemyHealth());
                enemySpawnTimer = 0.0f;
                currentWave.setNumEnemies(currentWave.getNumEnemies() - 1);
            }

            if (currentWave.getNumBossEnemies() > 0 && !isPaused) {
                int health = 500;
                spawnBoss(health);
                enemySpawnTimer = 0.0f;
                currentWave.setNumBossEnemies(currentWave.getNumBossEnemies() - 1);
            }

            // Check if the current wave is completed
            if (currentWave.getNumEnemies() == 0 && enemies.isEmpty() && currentWave.getNumBossEnemies() == 0) {
                waves.removeIndex(0);
                scaled = false;
                if (!waves.isEmpty()) {
                    enemiesLeftToKill = waves.first().getNumEnemies();
                }
                isPaused = true;
                if (waves.size == 1) {
                    gameMusic.stop();
                    bossMusic.play();
                }
            }
        } else {
            game.setScreen(new MainMenuScreen(game));
        }

        // Update and render enemies
        for (Enemy enemy : enemies) {
            enemy.update(Gdx.graphics.getDeltaTime(), enemyBullets, characterBullets, isPaused, enemies);

            // Check if the enemy died
            if (enemy.getHealth() <= 0) {
                Vector2 poz = new Vector2(enemy.getPosition());
                DeathParticles(poz, enemy.getSizeScale());
                scaled = true;
                score += (int) waves.first().getEnemyHealth();
                enemies.removeValue(enemy, true);
                enemiesLeftToKill -= 1;
            }

            enemy.render(batch, camera);
            batch.begin();
        }

        if (!waves.isEmpty()) {
            drawWaveNumberAndScore();
        }

        //draw Hearts
        character.drawHearts(batch, camera);

        // Draw the particle effects
        for (ParticleEffect particle : particleEffects) {
            particle.draw(batch);
        }

        //Draw health bar for boss enemies
        if (!enemies.isEmpty() && !isPaused && enemies.first() instanceof EnemyBoss) {
            drawBossHealthBar(camera);
        }
        // End the batch
        batch.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        if (character.getLives() <= 0 || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gameMusic.dispose();
            bossMusic.dispose();
            game.setScreen(new MainMenuScreen(game));
        }

    }

    private void updateCamera() {
        // Center the camera on the character's position
        camera.position.x = MathUtils.clamp(character.getPosition().x + character.getWidth() / 2, minCameraX, maxCameraX);
        camera.position.y = MathUtils.clamp(character.getPosition().y + character.getHeight() / 2, minCameraY, maxCameraY);

        // Update the camera's matrices
        camera.update();
    }


    @Override
    public void dispose() {
        batch.dispose();
        tiledMap.dispose();
        tiledMapRenderer.dispose();
        character.dispose();
        for (CharacterBullet bullet : characterBullets) {
            bullet.dispose();
        }
        for (EnemyBullet bullet : enemyBullets) {
            bullet.dispose();
        }

        stage.dispose();
        assets.dispose();
        gameMusic.dispose();
        bossMusic.dispose();
    }

    private void shootBullet() {
        // Calculate the starting position of the bullet at the center of the character.
        Vector2 bulletStartPosition = new Vector2(character.getPosition().x ,
                character.getPosition().y );

        // Calculate the direction to the cursor.
        Vector2 directionToCursor = calculateDirectionToCursor(bulletStartPosition);

        // Normalize the direction vector and scale it to the bullet's speed.
        directionToCursor.nor().scl(300);

        // Create a new Bullet and set the damage.
        CharacterBullet bullet = new CharacterBullet(bulletStartPosition, directionToCursor, damage, assets, soundVolume);

        // Add the bullet to the array.
        characterBullets.add(bullet);
    }

    private Vector2 calculateDirectionToCursor(Vector2 startingPoint) {
        // Get the cursor position in screen coordinates.
        Vector3 cursorPositionScreen = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);

        // Convert the screen coordinates to world coordinates.
        Vector3 cursorPositionWorld = camera.unproject(cursorPositionScreen);

        // Create a 2D vector for the cursor's world position.
        Vector2 cursorPositionWorld2D = new Vector2(cursorPositionWorld.x, cursorPositionWorld.y);

        // Return the direction vector from the starting point to the cursor position.
        return cursorPositionWorld2D.sub(startingPoint).nor(); // Normalize the direction vector.
    }

    private void spawnEnemy(float health) {
        // Generate a random position for the enemy
        Vector2 enemyPosition = new Vector2(MathUtils.random(minCameraX, maxCameraX), MathUtils.random(minCameraY, maxCameraY));

        // Create an enemy instance and pass the player's position
        Enemy enemy = new Enemy(enemyPosition, character.getPosition(), health, assets, soundVolume, critRate);

        // Add the enemy to a list or array to manage multiple enemies
        enemies.add(enemy);
    }

    private void spawnBoss(float health) {
        Vector2 enemyPosition = new Vector2(MathUtils.random(minCameraX, maxCameraX), MathUtils.random(minCameraY, maxCameraY));

        // Create an enemy instance and pass the player's position
        EnemyBoss enemy = new EnemyBoss(enemyPosition, character.getPosition(), health, assets, soundVolume, critRate);

        // Add the enemy to a list or array to manage multiple enemies
        enemies.add(enemy);
    }

    private void drawWaveNumberAndScore() {

        if (!isPaused) {
            stage.clear();
            // Wave
            String Text = "Wave: " + waves.first().getWaveNumber();
            Label Label = new Label(Text, skin);
            float TextX = (float) Gdx.graphics.getWidth() / 2 - Label.getWidth() / 2;
            float TextY = Gdx.graphics.getHeight() - Label.getHeight() / 2;
            Label.setPosition(TextX, TextY);
            stage.addActor(Label);
            // Score
            Text = "Score: " + score;
            Label = new Label(Text, skin);
            TextX = Gdx.graphics.getWidth() - (float) Gdx.graphics.getWidth() / 6;
            TextY = Gdx.graphics.getHeight() - Label.getHeight() / 2;
            Label.setPosition(TextX, TextY);
            stage.addActor(Label);

            Text = ": " + enemiesLeftToKill;
            Label = new Label(Text, skin);
            TextX = imageActor.getX() + Label.getWidth();
            TextY = imageActor.getY();
            Label.setPosition(TextX, TextY);
            stage.addActor(Label);
            stage.addActor(imageActor);
        } else stage.addActor(waveCompleteTable);
        Gdx.input.setInputProcessor(stage);
    }

    private void DeathParticles(Vector2 position, float scale) {
        ParticleEffect particleEffect = assets.getAssetManager().get(Assets.explosionParticleEffect);
        ParticleEmitter emitter = particleEffect.getEmitters().first();
        emitter.setPosition(position.x, position.y);
        if (!scaled) {
            emitter.scaleSize(scale);
        }
        particleEffect.start();
        particleEffects.add(particleEffect);
    }

    private void drawBossHealthBar(OrthographicCamera camera) {
        Vector2 healthBarPosition = new Vector2(
                camera.position.x - (healthBarWidth * camera.zoom) ,
                camera.position.y + (Gdx.graphics.getHeight() * camera.zoom) / 3
        );
        float bossHealthPercentage = enemies.first().getHealth() / maxBossHealth;
        float healthBarFillWidth = healthBarWidth * bossHealthPercentage;

        // Draw the border and the fill of the health bar
        batch.draw(healthBarTexture, healthBarPosition.x, healthBarPosition.y, healthBarWidth/2, healthBarHeight/2);
        batch.draw(healthFillTexture, healthBarPosition.x+healthBarWidth/90, healthBarPosition.y+healthBarHeight/25, healthBarFillWidth/2-healthBarWidth/50, healthBarHeight/2-healthBarHeight/10);
    }

}
