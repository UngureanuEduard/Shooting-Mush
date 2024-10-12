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
    import com.badlogic.gdx.scenes.scene2d.Stage;
    import com.badlogic.gdx.scenes.scene2d.ui.Image;
    import com.badlogic.gdx.scenes.scene2d.ui.Label;
    import com.badlogic.gdx.scenes.scene2d.ui.Skin;
    import com.badlogic.gdx.utils.Array;
    import com.badlogic.gdx.utils.ScreenUtils;
    import com.badlogic.gdx.utils.viewport.ExtendViewport;
    import com.badlogic.gdx.utils.viewport.Viewport;
    import com.mygdx.game.poolmanagers.CharacterBulletsManager;
    import com.mygdx.game.poolmanagers.EnemyBulletsManager;
    import com.mygdx.game.poolmanagers.EnemyManager;
    import com.mygdx.game.poolmanagers.ParticleEffectsManager;

    public class GameScene extends ScreenAdapter {

        private static final float CAMERA_ZOOM = 0.25f;
        private static final float BULLET_SPEED = 300f;
        private static final float SHOOT_COOLDOWN = 0.2f;

        Viewport viewport = new ExtendViewport(1920, 1080);
        Skin skin;
        private final Stage stage = new Stage(viewport);
        SpriteBatch batch;
        TiledMap tiledMap;
        OrthogonalTiledMapRenderer tiledMapRenderer;
        OrthographicCamera camera;
        Character character;
        private float minCameraX;
        private float minCameraY;
        private float maxCameraX;
        private float maxCameraY;
        private float enemySpawnTimer = 0.0f;
        private float timeSinceLastShot = 0.0f;
        public float damage = 5;
        Array<Wave> waves;
        Assets assets;
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

        public enum GameMode {
            ARENA,
            STORY
        }

        private final GameMode gameMode;

        CharacterBulletsManager characterBulletsManager = new CharacterBulletsManager();
        EnemyBulletsManager enemyBulletsManager = new EnemyBulletsManager();
        EnemyManager enemyManager =new EnemyManager();
        ParticleEffectsManager particleEffectsManager;

        public GameScene(MyGdxGame game, Integer musicVolume, Integer soundVolume , GameMode gameMode) {
            this.game = game;
            this.soundVolume = soundVolume;
            this.musicVolume = musicVolume;
            this.gameMode = gameMode;
            assets = new Assets();
            assets.loadGameAssets();
            assets.getAssetManager().finishLoading();
            particleEffectsManager= new ParticleEffectsManager(assets);
            loadAssets();
        }

        @Override
        public void show() {

            batch = new SpriteBatch();

            if(gameMode == GameMode.ARENA)
                arenaGameModeInit();
            else {
                storyGameModeInit();
            }
        }

        private void loadAssets(){
            try {
                skin = assets.getAssetManager().get(Assets.skin);
                if(gameMode == GameMode.ARENA) {
                    tiledMap = assets.getAssetManager().get(Assets.tiledMap);
                }
                else{
                    tiledMap = assets.getAssetManager().get(Assets.storyTiledMap);
                }
                imageActor = new Image(assets.getAssetManager().get(Assets.skullTexture));
                gameMusic = assets.getAssetManager().get(Assets.gameMusic);
                bossMusic = assets.getAssetManager().get(Assets.bossMusic);
                healthBarTexture = assets.getAssetManager().get(Assets.BorderHealthTexture);
                healthFillTexture = assets.getAssetManager().get(Assets.HealthTexture);
            }catch (Exception e) {
                Gdx.app.error("Assets", "Failed to load assets", e);
            }
        }

        private void initCamera(){
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

        private void initGameModeElements(){
            characterBulletsManager.fillPool(20);
            enemyBulletsManager.fillPool(20);
            enemyManager.fillPool(10);
            particleEffectsManager.fillPool(5);

            if (gameMode == GameMode.ARENA) {
                character = new  Character(new Vector2(800, 800), assets);
                initArenaWaves();
            } else {
                character = new Character(new Vector2(120, 100), assets);
            }
        }
        private void initArenaWaves() {
            waves = new Array<>();
            waves.add(new Wave(1, 0, 1, 0.5f, 90, damage));
            waves.add(new Wave(2, 1, 0, 0.4f, 500, damage));
            imageActor.setPosition((float) Gdx.graphics.getWidth() / 2 - imageActor.getWidth(), (float) (Gdx.graphics.getHeight() - Gdx.graphics.getHeight() / 10) + imageActor.getHeight() / 3);
            imageActor.setSize(imageActor.getWidth() / 1.5f, imageActor.getHeight() / 1.5f);
            enemiesLeftToKill = waves.first().getNumEnemies();
            waveCompleteTable = new WaveCompleteTable(skin, assets, this);
            waveCompleteTable.center();
            waveCompleteTable.setPosition(Gdx.graphics.getWidth() / 2f - waveCompleteTable.getWidth() / 2f, Gdx.graphics.getHeight() / 2f - waveCompleteTable.getHeight() / 2f);
        }

        private void setupMusic() {
            gameMusic.setLooping(true);
            gameMusic.setVolume(musicVolume / 100f);
            gameMusic.play();
            bossMusic.setLooping(true);
        }

        @Override
        public void render(float delta) {

            // Clear the screen
            ScreenUtils.clear(1, 0, 0, 1);

            batch.begin();

            character.update(enemyManager.getActiveEnemies(), tiledMap, isPaused, enemyBulletsManager.getActiveEnemyBullets());
            updateCamera();
            handleShootLogic(delta);
            batch.setProjectionMatrix(camera.combined);
            particleEffectsManager.update();
            tiledMapRenderer.setView(camera);
            tiledMapRenderer.render();
            renderCommonElements(batch, camera);
            if(gameMode == GameMode.ARENA){
                arenaWaveRender();
            }
            batch.end();
            stage.act(Gdx.graphics.getDeltaTime());
            stage.draw();
            handleGameOver();
        }

        private void renderCommonElements(SpriteBatch batch, OrthographicCamera camera) {
            character.render(batch);
            enemyBulletsManager.updateAndRender(batch);
            characterBulletsManager.updateAndRender(batch);
            enemyManager.updateAndRender(batch, enemyBulletsManager, characterBulletsManager, isPaused, enemiesLeftToKill, particleEffectsManager);
            character.drawHearts(batch,camera);
            particleEffectsManager.draw(batch);
            if (shouldDrawBossHealthBar()) {
                drawBossHealthBar(camera);
            }
        }

        private boolean shouldDrawBossHealthBar() {
            return !enemyManager.getActiveEnemies().isEmpty() && !isPaused && enemyManager.getActiveEnemies().first() instanceof EnemyBoss;
        }

        private void handleShootLogic(float delta) {
            timeSinceLastShot += delta;
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && timeSinceLastShot >= SHOOT_COOLDOWN && !isPaused) {
                shootBullet();
                timeSinceLastShot = 0.0f;
            }
        }

        private void handleGameOver() {
            if (character.getLives() <= 0 || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                gameMusic.dispose();
                bossMusic.dispose();
                game.setScreen(new MainMenuScreen(game));
            }
        }

        private void updateCamera() {
            camera.position.x = MathUtils.clamp(character.getPosition().x + character.getWidth() / 2, minCameraX, maxCameraX);
            camera.position.y = MathUtils.clamp(character.getPosition().y + character.getHeight() / 2, minCameraY, maxCameraY);
            camera.update();
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
        }

        private void shootBullet() {
            // Calculate the starting position of the bullet at the center of the character.
            Vector2 bulletStartPosition = new Vector2(character.getPosition().x ,
                    character.getPosition().y );

            // Calculate the direction to the cursor.
            Vector2 directionToCursor = calculateDirectionToCursor(bulletStartPosition);

            // Normalize the direction vector and scale it to the bullet's speed.
            directionToCursor.nor().scl(BULLET_SPEED);

            characterBulletsManager.generateBullet(bulletStartPosition, directionToCursor, damage, assets, soundVolume);
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
            enemyManager.spawnEnemy(enemyPosition,character.getPosition(),health,assets,soundVolume,critRate);
        }

        private void spawnBoss(float health) {
            Vector2 enemyPosition = new Vector2(MathUtils.random(minCameraX, maxCameraX), MathUtils.random(minCameraY, maxCameraY));

            // Create an enemy instance and pass the player's position
            EnemyBoss enemy = new EnemyBoss(enemyPosition, character.getPosition(), health, assets, soundVolume, critRate);

            // Add the enemy to a list or array to manage multiple enemies
            enemyManager.getActiveEnemies().add(enemy);
        }

        private void drawWaveNumberAndScore() {

            if (!isPaused) {
                stage.clear();
                String Text = "Wave: " + waves.first().getWaveNumber();
                Label Label = new Label(Text, skin);
                float TextX = (float) Gdx.graphics.getWidth() / 2 - Label.getWidth() / 2;
                float TextY = Gdx.graphics.getHeight() - Label.getHeight();
                Label.setPosition(TextX, TextY);
                stage.addActor(Label);
                Text = "Score: " + enemyManager.getScore();
                Label = new Label(Text, skin);
                TextX = Gdx.graphics.getWidth() - (float) Gdx.graphics.getWidth() / 6;
                TextY = Gdx.graphics.getHeight() - Label.getHeight();
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

        private void drawBossHealthBar(OrthographicCamera camera) {
            Vector2 healthBarPosition = new Vector2(
                    camera.position.x - (healthBarWidth * camera.zoom) ,
                    camera.position.y + (Gdx.graphics.getHeight() * camera.zoom) / 3
            );
            float bossHealthPercentage = enemyManager.getActiveEnemies().first().getHealth() / maxBossHealth;
            float healthBarFillWidth = healthBarWidth * bossHealthPercentage;

            // Draw the border and the fill of the health bar
            batch.draw(healthBarTexture, healthBarPosition.x, healthBarPosition.y, healthBarWidth/2, healthBarHeight/2);
            batch.draw(healthFillTexture, healthBarPosition.x+healthBarWidth/90, healthBarPosition.y+healthBarHeight/25, healthBarFillWidth/2-healthBarWidth/50, healthBarHeight/2-healthBarHeight/10);
        }

        private void arenaGameModeInit(){
            tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

            initCamera();

            initGameModeElements();

            setupMusic();

            healthBarWidth = (float) Gdx.graphics.getWidth() / 5;
            healthBarHeight = (float) Gdx.graphics.getHeight() / 36;
            maxBossHealth = 500;

        }

        private void arenaWaveRender(){
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
                if (currentWave.getNumEnemies() == 0 && enemyManager.getActiveEnemies().isEmpty() && currentWave.getNumBossEnemies() == 0) {
                    waves.removeIndex(0);
                    enemyManager.setScaled(false);
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
            drawWaveNumberAndScore();
        }

        private void storyGameModeInit(){
            tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

            initCamera();

            initGameModeElements();

            setupMusic();

            healthBarWidth = (float) Gdx.graphics.getWidth() / 5;
            healthBarHeight = (float) Gdx.graphics.getHeight() / 36;
            maxBossHealth = 500;
        }
    }
