package com.mygdx.game.game_modes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.game.entities.Character;
import com.mygdx.game.entities.Enemy;
import com.mygdx.game.entities.EnemyBoss;
import com.mygdx.game.GameScene;
import com.mygdx.game.animations_effects.LeafFallingAnimation;
import com.mygdx.game.pool_managers.CharacterBulletsManager;
import com.mygdx.game.pool_managers.EnemyBulletsManager;
import com.mygdx.game.pool_managers.EnemyManager;
import com.mygdx.game.pool_managers.ParticleEffectsManager;
import com.mygdx.game.utilities_resources.Assets;

public class BasicGameMode {

    private static final float CAMERA_ZOOM = 0.25f;
    private static final float BULLET_SPEED = 300f;
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
    private final Music gameMusic;
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
    }

    protected void show(){
        healthBarWidth = (float) Gdx.graphics.getWidth() / 5;
        healthBarHeight = (float) Gdx.graphics.getHeight() / 36;
        maxBossHealth = 500;
        initCamera();
        setupMusic();
        fillManagers();
    }

    protected void initCamera() {
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

    protected void render(float delta , SpriteBatch batch){
        updateCamera();
        character.update(enemyManager.getActiveEnemies(), tiledMap, isPaused , enemyBulletsManager.getActiveEnemyBullets(),inDialog||isGameOver);
        handleShootLogic(delta);
        batch.setProjectionMatrix(camera.combined);
        particleEffectsManager.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        character.render(batch);
        enemyBulletsManager.updateAndRender(batch);
        characterBulletsManager.updateAndRender(batch);
        enemiesLeftToKill = enemyManager.updateAndRender(batch, enemyBulletsManager, characterBulletsManager, isPaused, enemiesLeftToKill, particleEffectsManager);
        character.drawHearts(batch, camera);
        particleEffectsManager.draw(batch);
        if (shouldDrawBossHealthBar()) {
            drawBossHealthBar(camera ,batch);
        }
        if(!isGameOver){
            leafFallingAnimation.updateAndRender(batch , camera);
        }

        if (!getIsPaused() && getIsGameNotOver()) {
            timePlayed += delta;
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
                camera.position.y + (Gdx.graphics.getHeight() * camera.zoom) / 3
        );
        float healthBarFillWidth = healthBarWidth * bossHealthPercentage;
        batch.draw(healthBarTexture, healthBarPosition.x, healthBarPosition.y, healthBarWidth / 2, healthBarHeight / 2);
        batch.draw(healthFillTexture, healthBarPosition.x + healthBarWidth / 90, healthBarPosition.y + healthBarHeight / 25, healthBarFillWidth / 2 - healthBarWidth / 50, healthBarHeight / 2 - healthBarHeight / 10);
    }

    protected void spawnBoss(float health, Vector2 bossPosition , GameScene.GameMode gameMode) {
        EnemyBoss enemy = new EnemyBoss(bossPosition, character.getPosition(), health, assets, soundVolume, critRate, gameMode);
        getEnemyManager().getActiveEnemies().add(enemy);
        getGameMusic().stop();
        getBossMusic().play();
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

}
