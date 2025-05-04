package com.mygdx.game.game_modes;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.entities.character.Character;
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
    private Npc npc;
    private TransitionArea transitionArea;
    private EndGameScreenStory endGameScreenStory;
    private RayHandler rayHandler;
    private PointLight playerLight;
    private World world;
    private int cameraWidth;
    private int cameraHeight;

    public StoryMode(Assets assets, Integer soundVolume, Integer musicVolume) {
        super(assets, soundVolume, musicVolume);
        setEnemyManager(new EnemyManager(GameScene.GameMode.STORY));
        getEnemyManager().loadEnemiesFromJson("storyInfo.json");
        initializeMaps();
    }

    private void initializeMaps() {
        maps.add(new MapDetails(Assets.storyTiledMap.fileName, new Vector2(120, 100), new TransitionArea(1380, 1290, 128, 192)));
        maps.add(new MapDetails(Assets.storyTiledMap_2.fileName, new Vector2(243, 450), new TransitionArea(274, 649, 100, 100)));
    }

    public void show(int cameraWidth, int cameraHeight) {
        this.cameraWidth = cameraWidth;
        this.cameraHeight = cameraHeight;
        setCharacter(new Character(new Vector2(120, 100), getAssets()));
        loadMap(getCurrentMapIndex());
        super.show(cameraWidth, cameraHeight);
        setCurrentMapIndex(0);
        npc = new Npc(getEnemyManager().getEnemyMapLocationsInfos().get(0).getNpcPosition(), getAssets(), getSoundVolume());
    }

    private void loadMap(int index) {
        if (index < maps.size) {
            MapDetails mapDetails = maps.get(index);
            setTiledMap(getAssets().getAssetManager().get(mapDetails.getMapAsset()));
            setTiledMapRenderer(new OrthogonalTiledMapRenderer(getTiledMap()));
            getCharacter().setPosition(mapDetails.getSpawnPoint());
            transitionArea = mapDetails.getTransitionArea();
            initCamera(cameraWidth, cameraHeight);
            loadCollisionObjects();
            getCharacter().setCollisionRectangles(getCollisionRectangles());

            if (index == 1) {
                world = new World(new Vector2(0, 0), true);
                rayHandler = new RayHandler(world);
                rayHandler.setAmbientLight(0.05f);
                rayHandler.setShadows(true);
                rayHandler.setBlurNum(3);
                RayHandler.useDiffuseLight(true);

                playerLight = new PointLight(rayHandler, 128, new Color(1f, 1f, 0.8f, 1f), 100,
                        getCharacter().getPosition().x, getCharacter().getPosition().y);
                playerLight.setSoft(true);
                playerLight.setSoftnessLength(10f);
                playerLight.setStaticLight(false);

                for (MapObject object : getTiledMap().getLayers().get("Light").getObjects()) {
                    if (object instanceof RectangleMapObject) {
                        Rectangle rect = ((RectangleMapObject) object).getRectangle();
                        float x = rect.x + rect.width / 2f;
                        float y = rect.y + rect.height / 2f;

                        PointLight light = new PointLight(rayHandler, 128, new Color(1f, 1f, 0.8f, 1f), 80, x, y);
                        light.setSoft(true);
                        light.setSoftnessLength(10f);
                        light.setStaticLight(true);
                    }
                }

                getGameMusic().stop();
                setGameMusic(getAssets().getAssetManager().get(Assets.dungeonMusic));
                getGameMusic().setLooping(true);
                getGameMusic().play();
                setEnableLeafs(false);

            } else {
                if (rayHandler != null) rayHandler.dispose();
                rayHandler = null;
                world = null;
            }
        }
    }

    public void Render(float delta, SpriteBatch batch, MyGdxGame game, Stage stage) {
        super.render(delta, batch, game, stage);

        spawnStoryEnemy();

        npc.update(delta, getIsPaused(), getCharacter().getPosition(), stage, getSkin());
        npc.render(batch);

        if (getCurrentMapIndex() == 1 && rayHandler != null && playerLight != null) {
            playerLight.setPosition(
                    getCharacter().getPosition().x + getCharacter().getWidth() / 2,
                    getCharacter().getPosition().y + getCharacter().getHeight() / 2
            );
            rayHandler.setCombinedMatrix(getCamera());
            rayHandler.updateAndRender();
        }

        if (transitionArea.isWithinArea(getCharacter().getPosition().x, getCharacter().getPosition().y)
                && getEnemyManager().getActiveEnemies().isEmpty()) {
            loadNextMap(game);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            loadNextMap(game);
        }

        setInDialog(npc.getInDialog());

        if (endGameScreenStory != null) {
            Gdx.input.setInputProcessor(endGameScreenStory.getStage());
            endGameScreenStory.render(delta, batch);
        }

        if (getCharacter().getLives() == 0) {
            handleGameOver(game);
        }
    }

    private void loadNextMap(MyGdxGame game) {
        setCurrentMapIndex( getCurrentMapIndex() + 1);
        getBossMusic().stop();
        if (getCurrentMapIndex() < maps.size) {
            loadMap(getCurrentMapIndex());
        } else {
            handleGameOver(game);
        }
    }

    public void spawnStoryEnemy() {
        int activeCount = getEnemyManager().getActiveEnemies().size;
        int mapIndex = getCurrentMapIndex();
        if (activeCount < 9) {
            EnemyMapLocationsInfo enemyMapLocationsInfo = getEnemyManager().getEnemyMapLocationsInfos().get(mapIndex);
            Array<EnemyBasicInfo> toRemove = new Array<>();
            for (EnemyBasicInfo enemyInfo : enemyMapLocationsInfo.getEnemies()) {
                Vector2 enemyPosition = enemyInfo.getPosition();
                String type = enemyInfo.getType();
                if (type.equals("normal")) {
                    getEnemyManager().spawnEnemy(enemyPosition, getCharacter().getPosition(), 100, getAssets(), getSoundVolume(), getCritRate(), mapIndex);
                    toRemove.add(enemyInfo);
                } else if (type.equals("boss") && (getCharacter().getPosition().x > transitionArea.getX() - 480
                        && getCharacter().getPosition().y > transitionArea.getY() - 90) ) {
                    spawnBoss(500, enemyPosition, GameScene.GameMode.STORY);
                    toRemove.add(enemyInfo);
                }
            }
            enemyMapLocationsInfo.getEnemies().removeAll(toRemove, true);
        }
    }



    private void handleGameOver(MyGdxGame game) {
        setIsGameOver(true);
        endGameScreenStory = new EndGameScreenStory(game, (int) getTimePlayed(), getAssets(), getMusicVolume(), getSoundVolume());
        getGameMusic().stop();
        getBossMusic().stop();
    }

    public void dispose() {
        disposeBasicGameMode();
        if (npc != null) npc.dispose();
        if (endGameScreenStory != null) endGameScreenStory.dispose();
        if (maps != null) maps.clear();
        if (rayHandler != null) rayHandler.dispose();
        if (world != null) world.dispose();
    }

}
