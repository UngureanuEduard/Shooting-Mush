package com.mygdx.game.game_modes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.entities.character.Character;
import com.mygdx.game.GameScene;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.combat_system.Wave;
import com.mygdx.game.pool_managers.EnemyManager;
import com.mygdx.game.ui_screens.EndGameScreenArena;
import com.mygdx.game.ui_screens.WaveCompleteTable;
import com.mygdx.game.utilities_resources.Assets;

public class ArenaMode  extends BasicGameMode{


    private final Image imageActor;
    private Array<Wave> waves;
    private float enemySpawnTimer = 0.0f;
    private WaveCompleteTable waveCompleteTable;
    private Integer completedWaves=0;
    private EndGameScreenArena endGameScreenArena;



    public ArenaMode(Assets assets, Integer soundVolume, Integer musicVolume) {

        super(assets, soundVolume, musicVolume);
        setEnemyManager(new EnemyManager(GameScene.GameMode.ARENA));
        setTiledMap(assets.getAssetManager().get(Assets.arenaTiledMap));
        imageActor = new Image(assets.getAssetManager().get(Assets.skullTexture));
        setTiledMapRenderer(new OrthogonalTiledMapRenderer(getTiledMap()));
        loadCollisionObjects();
    }

    public void show(int cameraWidth , int cameraHeight){
        super.show(cameraWidth,cameraHeight);
        setCharacter(new Character(new Vector2(800, 800), getAssets()));
        getCharacter().setCollisionRectangles(getCollisionRectangles());
        initArenaWaves();
    }

    private void initArenaWaves() {
        waves = new Array<>();
        waves.add(new Wave(1, 0, 5, 0.5f, 90));
        waves.add(new Wave(2, 1, 0, 0.4f, 500));
        imageActor.setSize(imageActor.getWidth() / 1.5f, imageActor.getHeight() / 1.5f);
        setEnemiesLeftToKill(waves.first().getNumEnemies());
        waveCompleteTable = new WaveCompleteTable(getSkin(), getAssets() , this);
        waveCompleteTable.center();
    }

    public void render(float delta , SpriteBatch batch , MyGdxGame game, Stage stage){

        super.render(delta , batch , game , stage);

        getCharacter().drawHearts(batch,getCamera());

        if (!waves.isEmpty()) {
            Wave currentWave = waves.first();
            enemySpawnTimer += Gdx.graphics.getDeltaTime();
            if (currentWave.getNumEnemies() > 0 && enemySpawnTimer >= currentWave.getEnemySpawnInterval() && !getIsPaused()) {
                spawnArenaEnemy(currentWave.getEnemyHealth());
                enemySpawnTimer = 0.0f;
                currentWave.setNumEnemies(currentWave.getNumEnemies() - 1);
            }
            if (currentWave.getNumBossEnemies() > 0 && !getIsPaused()) {
                int health = 500;
                Vector2 bossPosition = new Vector2(MathUtils.random(getMinCameraX(), getMaxCameraX()), MathUtils.random(getMinCameraY(), getMaxCameraY()));
                spawnBoss(health, bossPosition , GameScene.GameMode.ARENA , 0);
                enemySpawnTimer = 0.0f;
                currentWave.setNumBossEnemies(currentWave.getNumBossEnemies() - 1);
            }

            if (currentWave.getNumEnemies() == 0 && getEnemyManager().getActiveEnemies().isEmpty() && currentWave.getNumBossEnemies() == 0) {
                waves.removeIndex(0);
                getEnemyManager().setScaled(false);
                if (!waves.isEmpty()){
                    setEnemiesLeftToKill(waves.first().getNumEnemies());
                }
                setIsPaused(true);
                completedWaves ++;
            }
            drawWaveNumberAndScore(stage);
        }

        if( ( (getCharacter().getLives() == 0) || waves.isEmpty() ) && getIsGameNotOver() ){
            handleGameOver(game);
        }

        if (endGameScreenArena != null) {
            Gdx.input.setInputProcessor(endGameScreenArena.getStage());
            endGameScreenArena.render(delta, batch);
        }
    }

    private void spawnArenaEnemy(float health) {
        Vector2 enemyPosition = new Vector2(MathUtils.random(getMinCameraX(), getMaxCameraX()), MathUtils.random(getMinCameraY(), getMaxCameraY()));
        getEnemyManager().spawnEnemy(enemyPosition, getCharacter().getPosition(), health, getAssets(), getSoundVolume(), getCritRate() , 0 );
    }

    private void drawWaveNumberAndScore( Stage stage) {
        if (!getIsPaused()) {

            stage.clear();
            String text = "Wave: " + waves.first().getWaveNumber();
            Label label = new Label(text, getSkin());
            float textX = getWorldWidth() / 2 - label.getWidth() / 2;
            float textY = getWorldHeight() - label.getHeight();
            label.setPosition(textX, textY);
            stage.addActor(label);
            text = "Score: " + getEnemyManager().getScore();
            label = new Label(text, getSkin());
            textX = getWorldWidth() - getWorldWidth() / 6;
            textY = getWorldHeight() - label.getHeight();
            label.setPosition(textX, textY);
            stage.addActor(label);
            text = ": " + getEnemiesLeftToKill();
            label = new Label(text, getSkin());
            textX = imageActor.getX() + label.getWidth();
            textY = imageActor.getY();
            label.setPosition(textX, textY);
            stage.addActor(label);

            if (super.shouldDrawBossHealthBar()) {
                imageActor.setColor(1, 0, 0, 1);
                setEnemiesLeftToKill(1);
            } else {
                imageActor.setColor(1, 1, 1, 1);
            }
            imageActor.setPosition(getWorldWidth() / 2 - imageActor.getWidth(),
                    getWorldHeight() - getWorldHeight() / 10 + imageActor.getHeight() / 3);
            stage.addActor(imageActor);
        } else  if(super.getIsGameNotOver() && waves.size > 0){
            waveCompleteTable.setPosition( getWorldWidth() / 2f - waveCompleteTable.getWidth() / 2f, getWorldHeight() / 2f - waveCompleteTable.getHeight() / 2f);
            stage.addActor(waveCompleteTable);
        }
        if(getIsGameNotOver()){
            Gdx.input.setInputProcessor(stage);
        }
    }

    private void handleGameOver(MyGdxGame game) {
        setIsGameOver(true);
        endGameScreenArena = new EndGameScreenArena(game , (int)(getTimePlayed() * completedWaves), getAssets() , getMusicVolume() ,getSoundVolume() , completedWaves);
        getGameMusic().stop();
        getBossMusic().stop();
    }

    public void incrementCritRate() {
        setCritRate(getCritRate()+getCritRate()*5/100);
    }

    public void dispose() {
        disposeBasicGameMode();
        if (waveCompleteTable != null) waveCompleteTable.remove();
        if (endGameScreenArena != null) endGameScreenArena.dispose();
        if (imageActor != null) imageActor.clear();
        if (waves != null) waves.clear();
    }

}
