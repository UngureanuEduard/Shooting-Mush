package com.mygdx.game.game_modes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.animations_effects.DamageText;
import com.mygdx.game.combat_system.CharacterBullet;
import com.mygdx.game.entities.character.Character;
import com.mygdx.game.entities.enemy.Enemy;
import com.mygdx.game.entities.enemy.EnemyBoss;
import com.mygdx.game.GameScene;
import com.mygdx.game.animations_effects.LeafFallingAnimation;
import com.mygdx.game.pool_managers.CharacterBulletsManager;
import com.mygdx.game.pool_managers.EnemyBulletsManager;
import com.mygdx.game.pool_managers.EnemyManager;
import com.mygdx.game.pool_managers.ParticleEffectsManager;
import com.mygdx.game.ui_screens.MainMenuScreen;
import com.mygdx.game.utilities_resources.Assets;

public class BasicGameMode {

    private static final float CAMERA_ZOOM = 0.25f;
    protected static final float BULLET_SPEED = 300f;
    private static final float SHOOT_COOLDOWN = 0.2f;


    private final CharacterBulletsManager characterBulletsManager;
    private final EnemyBulletsManager enemyBulletsManager;
    private final ParticleEffectsManager particleEffectsManager;
    private EnemyManager enemyManager;
    private final Assets assets;
    private Character character;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private final Integer soundVolume;
    private final Integer musicVolume;
    private boolean isGameOver;
    private final Skin skin;
    private final Texture healthBarTexture;
    private final Texture healthFillTexture;
    private float healthBarWidth;
    private float healthBarHeight;
    private float maxBossHealth;
    private Music gameMusic;
    private final Music bossMusic;
    private OrthographicCamera camera;
    private float minCameraX;
    private float minCameraY;
    private float maxCameraX;
    private float maxCameraY;
    private float timeSinceLastShot = 0.0f;
    private float bossHealthPercentage = 0.0f;
    private boolean isPaused = false;
    private boolean inDialog = false;
    private int enemiesLeftToKill;
    private final LeafFallingAnimation leafFallingAnimation;
    private float damage = 5;
    private Integer critRate = 15;
    private float timePlayed = 0f;
    private float worldWidth;
    private float worldHeight;
    private final Array<Rectangle> collisionRectangles = new Array<>();
    private boolean enableLeafs = true;
    private int currentMapIndex = 0;
    private final Array<DamageText> damageTexts = new Array<>();
    private final BitmapFont defaultFont;

    public BasicGameMode(Assets assets , Integer soundVolume, Integer musicVolume) {
        this.assets = assets;
        this.soundVolume = soundVolume;
        this.musicVolume = musicVolume;
        particleEffectsManager = new ParticleEffectsManager(assets);
        characterBulletsManager = new CharacterBulletsManager();
        enemyBulletsManager = new EnemyBulletsManager();
        leafFallingAnimation = new LeafFallingAnimation(assets);
        skin = assets.getAssetManager().get(Assets.skin);
        isGameOver = false;
        gameMusic = assets.getAssetManager().get(Assets.gameMusic);
        bossMusic = assets.getAssetManager().get(Assets.bossMusic);
        healthBarTexture = assets.getAssetManager().get(Assets.BossHealthBarTexture);
        healthFillTexture = assets.getAssetManager().get(Assets.HealthTexture);
        defaultFont = new BitmapFont();
    }

    protected void show(int cameraWidth, int cameraHeight){
        healthBarWidth = (float) Gdx.graphics.getWidth() / 5;
        healthBarHeight = (float) Gdx.graphics.getHeight() / 36;
        maxBossHealth = 500;
        initCamera(cameraWidth,cameraHeight);
        setupMusic();
        fillManagers();
    }

    protected void initCamera(int width, int height) {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);
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

    protected void render(float delta , SpriteBatch batch , MyGdxGame game , Stage stage){
        updateCamera();
        worldWidth = stage.getViewport().getWorldWidth();
        worldHeight = stage.getViewport().getWorldHeight();
        character.update(enemyManager.getActiveEnemies(), isPaused , enemyBulletsManager.getActiveEnemyBullets(),inDialog||isGameOver);
        handleShootLogic(delta);
        batch.setProjectionMatrix(camera.combined);
        particleEffectsManager.update();
        particleEffectsManager.draw(batch);
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        enemyBulletsManager.updateAndRender(batch);
        characterBulletsManager.updateAndRender(batch);

        for (Enemy enemy : enemyManager.getActiveEnemies()) {
            checkBulletCollisions(characterBulletsManager.getActiveCharacterBullets(), enemy);
        }
        renderDamageTexts(batch,delta);
        enemiesLeftToKill = enemyManager.updateAndRender(batch, enemyBulletsManager, isPaused, enemiesLeftToKill, particleEffectsManager ,currentMapIndex );
        character.render(batch);
        character.drawHearts(batch,camera);

        if (shouldDrawBossHealthBar()) {
            drawBossHealthBar(camera ,batch);
        }
        if(!isGameOver && enableLeafs){
            leafFallingAnimation.updateAndRender(batch , camera);
        }

        if (!getIsPaused() && getIsGameNotOver()) {
            timePlayed += delta;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isGameOver = true;
            gameMusic.stop();
            game.setScreen(new MainMenuScreen(game, assets, musicVolume, soundVolume));
        }
    }

    protected void setupMusic() {
        gameMusic.setLooping(true);
        gameMusic.setVolume(musicVolume / 100f);
        gameMusic.play();
        bossMusic.setLooping(true);
    }

    protected void fillManagers(){
        characterBulletsManager.fillPool(20);
        enemyBulletsManager.fillPool(20);
        particleEffectsManager.fillPool(5);
        enemyManager.fillPool(10);
    }

    private void updateCamera() {
        camera.position.x = MathUtils.clamp(character.getPosition().x + character.getWidth() / 2, minCameraX, maxCameraX);
        camera.position.y = MathUtils.clamp(character.getPosition().y + character.getHeight() / 2, minCameraY, maxCameraY);
        camera.update();
    }

    private void handleShootLogic(float delta) {
        timeSinceLastShot += delta;
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && timeSinceLastShot >= SHOOT_COOLDOWN && !isPaused) {
            shootBullet();
            timeSinceLastShot = 0.0f;
        }
    }

    protected void shootBullet() {
        Vector2 bulletStartPosition = new Vector2(character.getPosition().x, character.getPosition().y);
        Vector2 directionToCursor = calculateDirectionToCursor(bulletStartPosition);
        directionToCursor.nor().scl(BULLET_SPEED);
        characterBulletsManager.generateBullet(bulletStartPosition, directionToCursor, 25, assets, soundVolume ,true);
    }

    protected Vector2 calculateDirectionToCursor(Vector2 startingPoint) {
        Vector3 cursorPositionScreen = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        Vector3 cursorPositionWorld = camera.unproject(cursorPositionScreen);
        Vector2 cursorPositionWorld2D = new Vector2(cursorPositionWorld.x, cursorPositionWorld.y);
        return cursorPositionWorld2D.sub(startingPoint).nor();
    }


    protected boolean shouldDrawBossHealthBar() {
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

    private void drawBossHealthBar(OrthographicCamera camera , SpriteBatch batch) {
        Vector2 healthBarPosition = new Vector2(
                camera.position.x - (healthBarWidth * camera.zoom),
                camera.position.y + (worldHeight * camera.zoom) / 3
        );
        float healthBarFillWidth = healthBarWidth * bossHealthPercentage;
        batch.draw(healthBarTexture, healthBarPosition.x, healthBarPosition.y, healthBarWidth / 2, healthBarHeight / 2);
        batch.draw(healthFillTexture, healthBarPosition.x + healthBarWidth / 90, healthBarPosition.y + healthBarHeight / 25, healthBarFillWidth / 2 - healthBarWidth / 50, healthBarHeight / 2 - healthBarHeight / 10);
    }

    protected void spawnBoss(float health, Vector2 bossPosition , GameScene.GameMode gameMode) {
        EnemyBoss enemy = new EnemyBoss(bossPosition, character.getPosition(), health, assets, soundVolume, critRate, gameMode , 0);
        getEnemyManager().getActiveEnemies().add(enemy);
        getGameMusic().stop();
        getBossMusic().play();
    }

    protected void loadCollisionObjects() {
        getCollisionRectangles().clear();
        for (MapObject object : getTiledMap().getLayers().get("Collisions").getObjects()) {
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                getCollisionRectangles().add(rectObject.getRectangle());
            }
        }
    }

    public void checkBulletCollisions(Array<CharacterBullet> bullets , Enemy enemy) {
        for (CharacterBullet bullet : bullets) {
            if (Intersector.overlaps(bullet.getHitBox(), enemy.getHeadHitbox()) || Intersector.overlaps(bullet.getHitBox(), enemy.getBodyHitbox())) {
                boolean isCrit = enemy.isCrit();
                float damageTaken = isCrit ? bullet.getDamage() * 4 : bullet.getDamage();
                enemy.takeDamage(damageTaken);
                damageTexts.add(new DamageText(damageTaken, bullet.getPosition().cpy(), 1f, isCrit));
                enemy.setIsDamaged(true);
                enemy.setIsAttacked(true);
                enemy.setLastHitByHost(bullet.isFromHost());

                enemy.getPushBackDirection().set(enemy.getPosition()).sub(bullet.getPosition()).nor();
                enemy.setPushBackTime(0f);
                bullet.setAlive(false);
            }
        }
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

    public void setTiledMap(TiledMap tiledMap){
        this.tiledMap=tiledMap;
    }

    public TiledMap getTiledMap(){
        return tiledMap;
    }

    public void setTiledMapRenderer(OrthogonalTiledMapRenderer tiledMapRenderer){
        this.tiledMapRenderer = tiledMapRenderer;
    }

    public Character getCharacter(){
        return character;
    }

    public void setCharacter(Character character){
        this.character = character;
    }

    public Assets getAssets(){
        return assets;
    }

    public Skin getSkin(){
        return skin;
    }

    public void setEnemyManager(EnemyManager enemyManager){
        this.enemyManager=enemyManager;
    }

    public EnemyManager getEnemyManager() {
        return enemyManager;
    }

    public boolean getIsPaused(){
        return isPaused;
    }

    public void setIsPaused(boolean isPaused){
        this.isPaused = isPaused;
    }

    public Integer getSoundVolume() {
        return soundVolume;
    }

    public Integer getMusicVolume() {
        return musicVolume;
    }

    public float getMinCameraX() {
        return minCameraX;
    }

    public float getMinCameraY() {
        return minCameraY;
    }

    public float getMaxCameraX() {
        return maxCameraX;
    }

    public float getMaxCameraY() {
        return maxCameraY;
    }

    public Music getGameMusic() {
        return gameMusic;
    }

    public void setGameMusic(Music gameMusic) {
        this.gameMusic = gameMusic;
    }

    public Music getBossMusic() {
        return bossMusic;
    }

    public int getEnemiesLeftToKill() {
        return enemiesLeftToKill;
    }

    public void setEnemiesLeftToKill(int enemiesLeftToKill) {
        this.enemiesLeftToKill = enemiesLeftToKill;
    }

    public boolean getIsGameNotOver() {
        return !isGameOver;
    }

    public void setIsGameOver(boolean isGameOver){
        this.isGameOver = isGameOver;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setInDialog(boolean inDialog) {
        this.inDialog = inDialog;
    }

    public Integer getCritRate() {
        return critRate;
    }

    public void setCritRate(Integer critRate) {
        this.critRate = critRate;
    }

    public float getTimePlayed() {
        return timePlayed;
    }

    public float getWorldWidth() {
        return worldWidth;
    }

    public float getWorldHeight() {
        return worldHeight;
    }

    protected void disposeBasicGameMode() {
        if (tiledMapRenderer != null) tiledMapRenderer.dispose();
        if (tiledMap != null) tiledMap.dispose();
        characterBulletsManager.dispose();
        enemyBulletsManager.dispose();
        particleEffectsManager.dispose();
        if (gameMusic != null) gameMusic.dispose();
        if (bossMusic != null) bossMusic.dispose();
        leafFallingAnimation.dispose();
    }

    public CharacterBulletsManager getCharacterBulletsManager() {
        return characterBulletsManager;
    }

    public EnemyBulletsManager getEnemyBulletsManager() {
        return enemyBulletsManager;
    }

    public Array<Rectangle> getCollisionRectangles() {
        return collisionRectangles;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public void setEnableLeafs(boolean enableLeafs) {
        this.enableLeafs = enableLeafs;
    }

    public int getCurrentMapIndex() {
        return currentMapIndex;
    }

    public void setCurrentMapIndex(int currentMapIndex) {
        this.currentMapIndex = currentMapIndex;
    }
}
