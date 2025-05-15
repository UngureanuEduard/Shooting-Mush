package com.mygdx.game.game_modes;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.animations_effects.CloudShadow;
import com.mygdx.game.cutscene.CutsceneDungeon;
import com.mygdx.game.cutscene.CutsceneEnding;
import com.mygdx.game.cutscene.CutsceneScreenPortal;
import com.mygdx.game.entities.character.Character;
import com.mygdx.game.GameScene;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.entities.Npc;
import com.mygdx.game.pool_managers.EnemyManager;
import com.mygdx.game.ui_screens.EndGameScreenStory;
import com.mygdx.game.utilities_resources.Assets;
import com.mygdx.game.utilities_resources.TransitionArea;

public class StoryMode extends BasicGameMode {

    private final static float CLOUD_SPAWN_INTERVAL = 5f;

    private final Array<TiledMap> tiledMaps = new Array<>();
    private Npc npc;
    private TransitionArea transitionArea;
    private TransitionArea bossSpawnBorder;
    private EndGameScreenStory endGameScreenStory;
    private PointLight playerLight;
    private int cameraWidth;
    private int cameraHeight;
    private final Array<CloudShadow> cloudShadows = new Array<>();
    private float cloudSpawnTimer = 0f;
    private Vector2 bossSpawnLocation;
    private boolean finishedGame;
    private final String language;

    public StoryMode(Assets assets, Integer soundVolume, Integer musicVolume , String language) {
        super(assets, soundVolume, musicVolume);
        this.language = language;
        setEnemyManager(new EnemyManager(GameScene.GameMode.STORY));
        finishedGame = false;
        initializeMaps();
    }

    private void initializeMaps() {
        tiledMaps.add(getAssets().getAssetManager().get(Assets.storyTiledMap));
        tiledMaps.add(getAssets().getAssetManager().get(Assets.storyTiledMap_2));
        tiledMaps.add(getAssets().getAssetManager().get(Assets.storyTiledMap_3));
    }

    public void show(int cameraWidth, int cameraHeight) {
        this.cameraWidth = cameraWidth;
        this.cameraHeight = cameraHeight;
        loadMap(getCurrentMapIndex());
        super.show(cameraWidth, cameraHeight);

        npc = null;
        if (getTiledMap().getLayers().get("NPC") != null) {
            for (MapObject object : getTiledMap().getLayers().get("NPC").getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    Vector2 npcPosition = new Vector2(rect.x, rect.y);
                    npc = new Npc(npcPosition, getAssets(), getSoundVolume());
                    break;
                }
            }
        }
    }

    private void loadMap(int index) {
        if (index < tiledMaps.size) {
            setTiledMap(tiledMaps.get(index));
            setTiledMapRenderer(new OrthogonalTiledMapRenderer(getTiledMap()));

            Vector2 spawnPoint = null;
            if (getTiledMap().getLayers().get("SpawnPoint") != null) {
                for (MapObject object : getTiledMap().getLayers().get("SpawnPoint").getObjects()) {
                    if (object instanceof RectangleMapObject) {
                        Rectangle rect = ((RectangleMapObject) object).getRectangle();
                        spawnPoint = new Vector2(rect.x, rect.y);
                        break;
                    }
                }
            }

            if (getCharacter() == null) {
                setCharacter(new Character(spawnPoint, getAssets()));
            }

            getCharacter().setPosition(spawnPoint);

            transitionArea = null;
            if (getTiledMap().getLayers().get("TransitionZone") != null) {
                for (MapObject object : getTiledMap().getLayers().get("TransitionZone").getObjects()) {
                    if (object instanceof RectangleMapObject) {
                        Rectangle rect = ((RectangleMapObject) object).getRectangle();
                        transitionArea = new TransitionArea(rect.x, rect.y, rect.width, rect.height);
                        break;
                    }
                }
            }

            bossSpawnBorder = null;
            if (getTiledMap().getLayers().get("BossArea") != null) {
                for (MapObject object : getTiledMap().getLayers().get("BossArea").getObjects()) {
                    if (object instanceof RectangleMapObject) {
                        Rectangle rect = ((RectangleMapObject) object).getRectangle();
                        bossSpawnBorder = new TransitionArea(rect.x, rect.y, rect.width, rect.height);
                        break;
                    }
                }
            }

            initCamera(cameraWidth, cameraHeight);

            loadCollisionObjects();

            getCharacter().setCollisionRectangles(getCollisionRectangles());

            spawnStoryEnemy();

            if (index == 1 || index == 2) {

                if (getWorld() == null) {
                    setWorld(new World(new Vector2(0, 0), true));
                }
                if (getRayHandler() == null) {
                    setRayHandler(new RayHandler(getWorld()));
                }
                getRayHandler().setAmbientLight(0.05f);
                getRayHandler().setShadows(true);
                getRayHandler().setBlurNum(3);
                RayHandler.useDiffuseLight(true);
                playerLight = new PointLight(getRayHandler(), 128, new Color(1f, 1f, 0.8f, 1f), 100,
                        getCharacter().getPosition().x, getCharacter().getPosition().y);
                playerLight.setSoft(true);
                playerLight.setSoftnessLength(10f);
                playerLight.setStaticLight(false);

                for (MapObject object : getTiledMap().getLayers().get("Light").getObjects()) {
                    if (object instanceof RectangleMapObject) {
                        Rectangle rect = ((RectangleMapObject) object).getRectangle();
                        float x = rect.x + rect.width / 2f;
                        float y = rect.y + rect.height / 2f;

                        PointLight light = new PointLight(getRayHandler(), 128, new Color(1f, 1f, 0.8f, 1f), 80, x, y);
                        light.setSoft(true);
                        light.setSoftnessLength(10f);
                        light.setStaticLight(true);
                    }
                }

                getGameMusic().stop();
                if(index == 1){
                    setGameMusic(getAssets().getAssetManager().get(Assets.dungeonMusic));
                }else{
                    setGameMusic(getAssets().getAssetManager().get(Assets.plotTwistMusic));
                }
                getGameMusic().setLooping(true);
                getGameMusic().play();
                setEnableLeafs(false);

            } else {
                if (getRayHandler() != null) getRayHandler().dispose();
                setRayHandler(null);
                setWorld(null);
            }
        }
    }

    public void Render(float delta, SpriteBatch batch, MyGdxGame game, Stage stage) {
        super.render(delta, batch, game, stage);

        if (npc != null && getCurrentMapIndex() == 0) {
            npc.update(delta, getIsPaused(), getCharacter().getPosition(), stage, getSkin());
            npc.render(batch);
            setInDialog(npc.getInDialog());
        } else {
            setInDialog(false);
        }

        if(finishedGame){
            handleGameOver(game);
        }

        if ((getCurrentMapIndex() == 1 || getCurrentMapIndex() ==2) && getRayHandler() != null && playerLight != null) {
            playerLight.setPosition(
                    getCharacter().getPosition().x + getCharacter().getWidth() / 2,
                    getCharacter().getPosition().y + getCharacter().getHeight() / 2
            );
            getRayHandler().setCombinedMatrix(getCamera());
            getRayHandler().updateAndRender();

        }

        if (getCurrentMapIndex() == 0) {
            cloudSpawnTimer += delta;

            if (cloudSpawnTimer >= CLOUD_SPAWN_INTERVAL) {
                cloudSpawnTimer = 0f;
                cloudShadows.add(new CloudShadow(getAssets()));
            }

            for (CloudShadow shadow : cloudShadows) {
                shadow.update(delta);
                shadow.render(batch);
            }

            for (int i = cloudShadows.size - 1; i >= 0; i--) {
                if (!cloudShadows.get(i).isActive()) {
                    cloudShadows.removeIndex(i);
                }
            }
        }


        if(bossSpawnBorder !=null)
        {
            if (bossSpawnBorder.isWithinArea(getCharacter().getPosition().x, getCharacter().getPosition().y)) {
                spawnBoss(500,bossSpawnLocation, GameScene.GameMode.STORY , getCurrentMapIndex());
                bossSpawnBorder = null;
            }
        }

        if (transitionArea.isWithinArea(getCharacter().getPosition().x, getCharacter().getPosition().y)
                && getEnemyManager().getActiveEnemies().isEmpty()) {
            if (getCurrentMapIndex() == 0) {
                getGameMusic().stop();
                getBossMusic().stop();
                game.setScreen(new CutsceneScreenPortal(game, getMusicVolume(), getSoundVolume(), getAssets(), getCharacter() , getTimePlayed() , language));
            }
            else if(getCurrentMapIndex() ==1) {
                getGameMusic().stop();
                getBossMusic().stop();
                game.setScreen(new CutsceneDungeon(game, getMusicVolume(), getSoundVolume(), getAssets() , getCharacter() , getTimePlayed() ,language));
            }
            else if(getCurrentMapIndex() ==2) {
                getGameMusic().stop();
                getBossMusic().stop();
                game.setScreen(new CutsceneEnding(game, getMusicVolume(), getSoundVolume(), getAssets() , getTimePlayed() ,language));
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            loadNextMap();
        }

        setInDialog(npc.getInDialog());

        if (endGameScreenStory != null) {
            Gdx.input.setInputProcessor(endGameScreenStory.getStage());
            endGameScreenStory.render(delta, batch);
        }

        if (getCharacter().getLives() == 0) {
            handleGameOver(game);
        }

        batch.end();
        batch.begin();

        if(getIsGameNotOver()) {
            getCharacter().drawHearts(batch, getCamera());
        }

        if (shouldDrawBossHealthBar()) {
            drawBossHealthBar(getCamera() ,batch);
        }

        batch.end();
        batch.begin();
    }

    private void loadNextMap() {
        setCurrentMapIndex( getCurrentMapIndex() + 1);
        getBossMusic().stop();
        loadMap(getCurrentMapIndex());

    }

    public void spawnStoryEnemy() {

        if (!getEnemyManager().getActiveEnemies().isEmpty()) return;

        if (getTiledMap().getLayers().get("Enemies") == null) return;

        for (MapObject object : getTiledMap().getLayers().get("Enemies").getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                Vector2 position = new Vector2(rect.x, rect.y);
                getEnemyManager().spawnEnemy(position, getCharacter().getPosition(), 100, getAssets(), getSoundVolume(), getCritRate(), getCurrentMapIndex());
            } else {
                float x = Float.parseFloat(object.getProperties().get("x").toString());
                float y = Float.parseFloat(object.getProperties().get("y").toString());
                bossSpawnLocation =  new Vector2(x, y);
            }
        }
    }

    private void handleGameOver(MyGdxGame game) {
        if(getIsGameNotOver()){
            setIsGameOver(true);
            endGameScreenStory = new EndGameScreenStory(game, (int) getTimePlayed(), getAssets(), getMusicVolume(), getSoundVolume());
            getGameMusic().stop();
            getBossMusic().stop();
        }
    }

    public void dispose() {
        disposeBasicGameMode();
        if (npc != null) npc.dispose();
        if (endGameScreenStory != null) endGameScreenStory.dispose();
        if (tiledMaps != null) tiledMaps.clear();
        if (getRayHandler() != null) getRayHandler().dispose();
        if (getWorld() != null) getWorld().dispose();
    }

    public void setFinishedGame(boolean finishedGame) {
        this.finishedGame = finishedGame;
    }
}
