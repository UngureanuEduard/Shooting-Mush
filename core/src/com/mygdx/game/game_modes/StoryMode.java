package com.mygdx.game.game_modes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.entities.Character;
import com.mygdx.game.GameScene;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.entities.Npc;
import com.mygdx.game.pool_managers.EnemyManager;
import com.mygdx.game.pool_managers.EnemyMapLocationsInfo;
import com.mygdx.game.ui_screens.EndGameScreenStory;
import com.mygdx.game.utilities_resources.Assets;
import com.mygdx.game.utilities_resources.EnemyBasicInfo;
import com.mygdx.game.utilities_resources.MapDetails;
import com.mygdx.game.utilities_resources.TransitionArea;

public class StoryMode extends BasicGameMode {

    private final Array<MapDetails> maps = new Array<>();
    private int currentMapIndex = 0;
    private Npc npc;
    private TransitionArea transitionArea;
    private EndGameScreenStory endGameScreenStory;


    public StoryMode(Assets assets, Integer soundVolume, Integer musicVolume) {
        super(assets , soundVolume, musicVolume );
        setEnemyManager(new EnemyManager(GameScene.GameMode.STORY));
        getEnemyManager().loadEnemiesFromJson("storyInfo.json");
        initializeMaps();
    }

    private void initializeMaps() {
        maps.add(new MapDetails(Assets.storyTiledMap.fileName, new Vector2(120, 100), new TransitionArea(1380, 1290, 128, 192)));
    }

    public void show(int cameraWidth , int cameraHeight){
        setCharacter(new Character(new Vector2(120, 100) , getAssets()));
        loadMap(currentMapIndex);
        super.show(cameraWidth ,cameraHeight);
        currentMapIndex = 0;
        npc = new Npc(getEnemyManager().getEnemyMapLocationsInfos().get(0).getNpcPosition(), getAssets() , getSoundVolume());
    }

    private void loadMap(int index) {
        if (index < maps.size) {
            MapDetails mapDetails = maps.get(index);
            setTiledMap(getAssets().getAssetManager().get(mapDetails.getMapAsset()));
            setTiledMapRenderer(new OrthogonalTiledMapRenderer(getTiledMap()));
            getCharacter().setPosition(mapDetails.getSpawnPoint());
            transitionArea = mapDetails.getTransitionArea();
        }
    }

    public void Render(float delta , SpriteBatch batch , MyGdxGame game , Stage stage){
        super.render(delta , batch , game , stage);

        spawnStoryEnemy();

        npc.update(delta , getIsPaused() , getCharacter().getPosition() , stage , getSkin());
        npc.render(batch);
        if (transitionArea.isWithinArea(getCharacter().getPosition().x, getCharacter().getPosition().y) && getEnemyManager().getActiveEnemies().isEmpty()) {
            loadNextMap(game);
        }
        setInDialog(npc.getInDialog());

        if (endGameScreenStory != null) {
            Gdx.input.setInputProcessor(endGameScreenStory.getStage());
            endGameScreenStory.render(delta, batch);
        }

        if( (getCharacter().getLives() == 0) ){
            handleGameOver(game);
        }
    }

    private void loadNextMap(MyGdxGame game) {
        currentMapIndex++;
        getBossMusic().stop();
        if (currentMapIndex < maps.size) {
            loadMap(currentMapIndex);
        } else {
            handleGameOver(game);
        }
    }

    private void spawnStoryEnemy() {
        if (getEnemyManager().getActiveEnemies().size < 9) {
            EnemyMapLocationsInfo enemyMapLocationsInfo = getEnemyManager().getEnemyMapLocationsInfos().get(currentMapIndex);
            for (EnemyBasicInfo enemyInfo : enemyMapLocationsInfo.getEnemies()) {
                Vector2 enemyPosition = enemyInfo.getPosition();
                String type = enemyInfo.getType();
                if (type.equals("normal")) {
                    getEnemyManager().spawnEnemy(enemyPosition, getCharacter().getPosition(), 100, getAssets(), getSoundVolume(), getCritRate());
                    enemyMapLocationsInfo.removeEnemy(enemyInfo);
                } else if (getCharacter().getPosition().x > transitionArea.getX() - 480 && getCharacter().getPosition().y > transitionArea.getY() - 90) {
                    spawnBoss(500, enemyPosition , GameScene.GameMode.STORY);
                    enemyMapLocationsInfo.removeEnemy(enemyInfo);
                }
            }
        }
    }

    private void handleGameOver(MyGdxGame game) {
        setIsGameOver(true);
        endGameScreenStory = new EndGameScreenStory(game ,(int) getTimePlayed(), getAssets() , getMusicVolume() ,getSoundVolume());
        getGameMusic().stop();
        getBossMusic().stop();

    }

    public void dispose() {
        disposeBasicGameMode();
        if (npc != null) npc.dispose();
        if (endGameScreenStory != null) endGameScreenStory.dispose();
        if (maps != null) maps.clear();
    }



}
