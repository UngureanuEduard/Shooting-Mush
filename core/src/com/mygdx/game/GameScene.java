package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.animations_effects.LeafFallingAnimation;
import com.mygdx.game.combat_system.Wave;
import com.mygdx.game.pool_managers.*;
import com.mygdx.game.ui_screens.EndGameScreen;
import com.mygdx.game.ui_screens.MainMenuScreen;
import com.mygdx.game.ui_screens.WaveCompleteTable;
import com.mygdx.game.utilities_resources.*;

public class GameScene extends ScreenAdapter {

    private static final float CAMERA_ZOOM = 0.25f;
    private static final float BULLET_SPEED = 300f;
    private static final float SHOOT_COOLDOWN = 0.2f;

    private final World box2dWorld;
    Viewport viewport = new ExtendViewport(1920, 1080);
    Skin skin;
    private final Stage stage = new Stage(viewport);
    SpriteBatch batch;
    TiledMap tiledMap;
    OrthogonalTiledMapRenderer tiledMapRenderer;
    OrthographicCamera camera;
    private Character character;
    Npc npc;
    private float minCameraX;
    private float minCameraY;
    private float maxCameraX;
    private float maxCameraY;
    private float enemySpawnTimer = 0.0f;
    private float timeSinceLastShot = 0.0f;
    private float bossHealthPercentage = 0.0f;
    public float damage = 5;
    private Array<Wave> waves;
    Assets assets;
    private int enemiesLeftToKill;
    Image imageActor;
    Music gameMusic;
    Music bossMusic;
    private boolean isPaused = false;
    private boolean inDialog = false;
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
    TransitionArea transitionArea;
    Array<MapDetails> maps = new Array<>();
    private int currentMapIndex = 0;
    private float timePlayed = 0f;

    public enum GameMode {
        ARENA,
        STORY
    }

    private final GameMode gameMode;

    CharacterBulletsManager characterBulletsManager = new CharacterBulletsManager();
    EnemyBulletsManager enemyBulletsManager = new EnemyBulletsManager();
    EnemyManager enemyManager;
    ParticleEffectsManager particleEffectsManager;

    private final LeafFallingAnimation leafFallingAnimation;
    private Boolean isGameOver;

    public GameScene(MyGdxGame game, Integer musicVolume, Integer soundVolume, GameMode gameMode ,Assets assets) {
        this.game = game;
        this.soundVolume = soundVolume;
        this.musicVolume = musicVolume;
        this.gameMode = gameMode;
        this.assets = assets;
        box2dWorld = new World(new Vector2(0, -10), true);
        enemyManager = new EnemyManager(gameMode);
        enemyManager.loadEnemiesFromJson("storyInfo.json");
        leafFallingAnimation = new LeafFallingAnimation(assets);
        particleEffectsManager = new ParticleEffectsManager(assets);
        isGameOver = false;
        loadAssets();
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        if (gameMode == GameMode.ARENA) {
            arenaGameModeInit();
        } else {
            storyGameModeInit();
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1, 0, 0, 1);
        batch.begin();
        updateCamera();
        character.update(enemyManager.getActiveEnemies(), tiledMap, isPaused , enemyBulletsManager.getActiveEnemyBullets(),inDialog||isGameOver);
        handleShootLogic(delta);
        batch.setProjectionMatrix(camera.combined);
        particleEffectsManager.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        if (gameMode == GameMode.ARENA) {
            arenaWaveRender();
        } else {
            spawnStoryEnemy();
            npc.update(delta , isPaused , character.getPosition() , stage , skin);
            npc.render(batch);
            if (transitionArea.isWithinArea(character.getPosition().x, character.getPosition().y) && enemyManager.getActiveEnemies().isEmpty()) {
                loadNextMap();
            }
            inDialog= npc.getInDialog();

        }

        if (character.getLives() <= 0) {
            handleGameOver();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            //game.setScreen(new MainMenuScreen(game , assets));
            handleGameOver();
        }

        renderCommonElements(batch, camera);

        if (!isPaused && !isGameOver) {
            timePlayed += delta;
        }

        batch.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        batch.dispose();
        tiledMap.dispose();
        tiledMapRenderer.dispose();
        character.dispose();
        characterBulletsManager.dispose();
        enemyBulletsManager.dispose();
        stage.dispose();
        assets.dispose();
        gameMusic.dispose();
        bossMusic.dispose();
        box2dWorld.dispose();
        leafFallingAnimation.dispose();
    }

    private void loadAssets() {
        try {
            skin = assets.getAssetManager().get(Assets.skin);
            if (gameMode == GameMode.ARENA) {
                tiledMap = assets.getAssetManager().get(Assets.arenaTiledMap);
            } else {
                initializeMaps();
            }
            imageActor = new Image(assets.getAssetManager().get(Assets.skullTexture));
            gameMusic = assets.getAssetManager().get(Assets.gameMusic);
            bossMusic = assets.getAssetManager().get(Assets.bossMusic);
            healthBarTexture = assets.getAssetManager().get(Assets.BossHealthBarTexture);
            healthFillTexture = assets.getAssetManager().get(Assets.HealthTexture);
        } catch (Exception e) {
            Gdx.app.error("Assets", "Failed to load assets", e);
        }
    }

    private void initCamera() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        camera.zoom = CAMERA_ZOOM;

        int mapWidthInTiles = tiledMap.getProperties().get("width", Integer.class);
        int mapHeightInTiles = tiledMap.getProperties().get("height", Integer.class);
        int tileWidth = tiledMap.getProperties().get("tilewidth", Integer.class);
        int tileHeight = tiledMap.getProperties().get("tileheight", Integer.class);

        float mapWidthInUnits = mapWidthInTiles * tileWidth * tiledMapRenderer.getUnitScale();
        float mapHeightInUnits = mapHeightInTiles * tileHeight * tiledMapRenderer.getUnitScale();

        minCameraX = camera.viewportWidth * camera.zoom / 2;
        minCameraY = camera.viewportHeight * camera.zoom / 2;
        maxCameraX = mapWidthInUnits - camera.viewportWidth * camera.zoom / 2;
        maxCameraY = mapHeightInUnits - camera.viewportHeight * camera.zoom / 2;
    }

    private void initGameModeElements() {
        characterBulletsManager.fillPool(20);
        enemyBulletsManager.fillPool(20);
        enemyManager.fillPool(10);
        particleEffectsManager.fillPool(5);
        if (gameMode == GameMode.ARENA) {
            character = new Character(new Vector2(800, 800), assets , box2dWorld);
            initArenaWaves();
        }
    }

    private void setupMusic() {
        gameMusic.setLooping(true);
        gameMusic.setVolume(musicVolume / 100f);
        gameMusic.play();
        bossMusic.setLooping(true);
    }

    private void arenaGameModeInit() {
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        initCamera();
        initGameModeElements();
        setupMusic();
        healthBarWidth = (float) Gdx.graphics.getWidth() / 5;
        healthBarHeight = (float) Gdx.graphics.getHeight() / 36;
        maxBossHealth = 500;
    }

    private void storyGameModeInit() {
        currentMapIndex = 0;
        character = new Character(new Vector2(120, 100) , assets , box2dWorld);
        npc = new Npc(enemyManager.getEnemyMapLocationsInfos().get(0).getNpcPosition(), assets , soundVolume);
        loadMap(currentMapIndex);
        healthBarWidth = (float) Gdx.graphics.getWidth() / 5;
        healthBarHeight = (float) Gdx.graphics.getHeight() / 36;
        maxBossHealth = 500;
    }

    private void updateCamera() {
        camera.position.x = MathUtils.clamp(character.getPosition().x + character.getWidth() / 2, minCameraX, maxCameraX);
        camera.position.y = MathUtils.clamp(character.getPosition().y + character.getHeight() / 2, minCameraY, maxCameraY);
        camera.update();
    }

    private void arenaWaveRender() {
        if (!waves.isEmpty()) {
            currentWave = waves.first();
            enemySpawnTimer += Gdx.graphics.getDeltaTime();
            damage = currentWave.getBulletDamage();

            if (currentWave.getNumEnemies() > 0 && enemySpawnTimer >= currentWave.getEnemySpawnInterval() && !isPaused) {
                spawnArenaEnemy(currentWave.getEnemyHealth());
                enemySpawnTimer = 0.0f;
                currentWave.setNumEnemies(currentWave.getNumEnemies() - 1);
            }

            if (currentWave.getNumBossEnemies() > 0 && !isPaused) {
                int health = 500;
                Vector2 bossPosition = new Vector2(MathUtils.random(minCameraX, maxCameraX), MathUtils.random(minCameraY, maxCameraY));
                spawnBoss(health, bossPosition);
                enemySpawnTimer = 0.0f;
                currentWave.setNumBossEnemies(currentWave.getNumBossEnemies() - 1);
            }

            if (currentWave.getNumEnemies() == 0 && enemyManager.getActiveEnemies().isEmpty() && currentWave.getNumBossEnemies() == 0) {
                waves.removeIndex(0);
                enemyManager.setScaled(false);
                if (!waves.isEmpty()) {
                    enemiesLeftToKill = waves.first().getNumEnemies();
                }
                isPaused = true;
            }
            drawWaveNumberAndScore();
        } else {
            game.setScreen(new MainMenuScreen(game , assets , musicVolume,soundVolume));
        }
    }

    private void spawnStoryEnemy() {
        if (enemyManager.getActiveEnemies().size < 9) {
            EnemyMapLocationsInfo enemyMapLocationsInfo = enemyManager.getEnemyMapLocationsInfos().get(currentMapIndex);
            for (EnemyBasicInfo enemyInfo : enemyMapLocationsInfo.getEnemies()) {
                Vector2 enemyPosition = enemyInfo.getPosition();
                String type = enemyInfo.getType();
                if (type.equals("normal")) {
                    enemyManager.spawnEnemy(enemyPosition, character.getPosition(), 100, assets, soundVolume, critRate);
                    enemyMapLocationsInfo.removeEnemy(enemyInfo);
                } else if (character.getPosition().x > transitionArea.getX() - 480 && character.getPosition().y > transitionArea.getY() - 90) {
                    spawnBoss(500, enemyPosition);
                    enemyMapLocationsInfo.removeEnemy(enemyInfo);
                }
            }
        }
    }

    private void renderCommonElements(SpriteBatch batch, OrthographicCamera camera) {
        character.render(batch);
        enemyBulletsManager.updateAndRender(batch);
        characterBulletsManager.updateAndRender(batch);
        enemyManager.updateAndRender(batch, enemyBulletsManager, characterBulletsManager, isPaused, enemiesLeftToKill, particleEffectsManager);
        character.drawHearts(batch, camera);
        particleEffectsManager.draw(batch);
        if (shouldDrawBossHealthBar()) {
            drawBossHealthBar(camera);
        }
        if(!isGameOver){
            leafFallingAnimation.updateAndRender(batch , camera);
        }
    }

    private void drawWaveNumberAndScore() {
        if (!isPaused) {
            stage.clear();
            String text = "Wave: " + waves.first().getWaveNumber();
            Label label = new Label(text, skin);
            float textX = (float) Gdx.graphics.getWidth() / 2 - label.getWidth() / 2;
            float textY = Gdx.graphics.getHeight() - label.getHeight();
            label.setPosition(textX, textY);
            stage.addActor(label);
            text = "Score: " + enemyManager.getScore();
            label = new Label(text, skin);
            textX = Gdx.graphics.getWidth() - (float) Gdx.graphics.getWidth() / 6;
            textY = Gdx.graphics.getHeight() - label.getHeight();
            label.setPosition(textX, textY);
            stage.addActor(label);
            text = ": " + enemiesLeftToKill;
            label = new Label(text, skin);
            textX = imageActor.getX() + label.getWidth();
            textY = imageActor.getY();
            label.setPosition(textX, textY);
            stage.addActor(label);
            stage.addActor(imageActor);
        } else {
            stage.addActor(waveCompleteTable);
        }
        Gdx.input.setInputProcessor(stage);
    }

    private void handleShootLogic(float delta) {
        timeSinceLastShot += delta;
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && timeSinceLastShot >= SHOOT_COOLDOWN && !isPaused) {
            shootBullet();
            timeSinceLastShot = 0.0f;
        }
    }

    private boolean shouldDrawBossHealthBar() {
        if(isGameOver)
            return false;

        if (!enemyManager.getActiveEnemies().isEmpty() && !isPaused) {
            for (Enemy enemy : enemyManager.getActiveEnemies()) {
                if (enemy instanceof EnemyBoss) {
                    bossHealthPercentage = enemy.getHealth() / maxBossHealth;
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private void shootBullet() {
        Vector2 bulletStartPosition = new Vector2(character.getPosition().x, character.getPosition().y);
        Vector2 directionToCursor = calculateDirectionToCursor(bulletStartPosition);
        directionToCursor.nor().scl(BULLET_SPEED);
        characterBulletsManager.generateBullet(bulletStartPosition, directionToCursor, 50, assets, soundVolume);
    }

    private Vector2 calculateDirectionToCursor(Vector2 startingPoint) {
        Vector3 cursorPositionScreen = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        Vector3 cursorPositionWorld = camera.unproject(cursorPositionScreen);
        Vector2 cursorPositionWorld2D = new Vector2(cursorPositionWorld.x, cursorPositionWorld.y);
        return cursorPositionWorld2D.sub(startingPoint).nor();
    }

    private void spawnArenaEnemy(float health) {
        Vector2 enemyPosition = new Vector2(MathUtils.random(minCameraX, maxCameraX), MathUtils.random(minCameraY, maxCameraY));
        enemyManager.spawnEnemy(enemyPosition, character.getPosition(), health, assets, soundVolume, critRate);
    }

    private void spawnBoss(float health, Vector2 bossPosition) {
        EnemyBoss enemy = new EnemyBoss(bossPosition, character.getPosition(), health, assets, soundVolume, critRate, gameMode);
        enemyManager.getActiveEnemies().add(enemy);
        gameMusic.stop();
        bossMusic.play();
    }

    private void drawBossHealthBar(OrthographicCamera camera) {
        Vector2 healthBarPosition = new Vector2(
                camera.position.x - (healthBarWidth * camera.zoom),
                camera.position.y + (Gdx.graphics.getHeight() * camera.zoom) / 3
        );
        float healthBarFillWidth = healthBarWidth * bossHealthPercentage;
        batch.draw(healthBarTexture, healthBarPosition.x, healthBarPosition.y, healthBarWidth / 2, healthBarHeight / 2);
        batch.draw(healthFillTexture, healthBarPosition.x + healthBarWidth / 90, healthBarPosition.y + healthBarHeight / 25, healthBarFillWidth / 2 - healthBarWidth / 50, healthBarHeight / 2 - healthBarHeight / 10);
    }

    private void initializeMaps() {
        maps.add(new MapDetails(Assets.storyTiledMap.fileName, new Vector2(120, 100), new TransitionArea(1380, 1290, 128, 192)));
    }

    private void loadMap(int index) {
        if (index < maps.size) {
            MapDetails mapDetails = maps.get(index);
            tiledMap = assets.getAssetManager().get(mapDetails.getMapAsset());
            tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
            character.setPosition(mapDetails.getSpawnPoint());
            transitionArea = mapDetails.getTransitionArea();
        }
        initCamera();
        initGameModeElements();
        setupMusic();
    }

    private void loadNextMap() {
        currentMapIndex++;
        bossMusic.stop();
        if (currentMapIndex < maps.size) {
            loadMap(currentMapIndex);
        } else {
            handleGameOver();
        }
    }

    private void initArenaWaves() {
        waves = new Array<>();
        waves.add(new Wave(1, 0, 3, 0.5f, 90, damage));
        waves.add(new Wave(2, 1, 0, 0.4f, 500, damage));
        imageActor.setPosition((float) Gdx.graphics.getWidth() / 2 - imageActor.getWidth(), (float) (Gdx.graphics.getHeight() - Gdx.graphics.getHeight() / 10) + imageActor.getHeight() / 3);
        imageActor.setSize(imageActor.getWidth() / 1.5f, imageActor.getHeight() / 1.5f);
        enemiesLeftToKill = waves.first().getNumEnemies();
        waveCompleteTable = new WaveCompleteTable(skin, assets, this);
        waveCompleteTable.center();
        waveCompleteTable.setPosition(Gdx.graphics.getWidth() / 2f - waveCompleteTable.getWidth() / 2f, Gdx.graphics.getHeight() / 2f - waveCompleteTable.getHeight() / 2f);
    }

    public Character getCharacter() {
        return character;
    }

    public Array<Wave> getWaves() {
        return waves;
    }

    public void setPaused(boolean isPaused) {
        this.isPaused = isPaused;
    }

    private void handleGameOver() {
        isGameOver = true;
        EndGameScreen endGameScreen = new EndGameScreen(game , (int)timePlayed, assets , musicVolume ,soundVolume) {
            @Override
            public void render(float delta) {
                GameScene.this.render(delta);
                super.render(delta);
            }
        };

        gameMusic.stop();
        game.setScreen(endGameScreen);
    }


}