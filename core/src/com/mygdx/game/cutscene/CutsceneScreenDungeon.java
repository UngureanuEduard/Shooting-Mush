package com.mygdx.game.cutscene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.game.GameScene;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.entities.character.Character;
import com.mygdx.game.entities.character.Sprite;
import com.mygdx.game.game_modes.StoryMode;
import com.mygdx.game.utilities_resources.Assets;

public class CutsceneScreenDungeon implements Screen {

    private final MyGdxGame game;
    private final int musicVolume;
    private final int soundVolume;
    private final Assets assets;
    private final Stage stage;
    private final CutsceneManager cutsceneManager;
    private final SpriteBatch batch = new SpriteBatch();
    private Sprite c1;
    OrthographicCamera camera ;
    private final OrthogonalTiledMapRenderer tiledMapRenderer;
    private final Music cutsceneMusic;
    private final Music portalMusic;
    private final TiledMap tiledMap;
    private final Character character;

    public CutsceneScreenDungeon(MyGdxGame game, int musicVolume, int soundVolume, Assets assets  , Character character) {
        this.game = game;
        this.musicVolume = musicVolume;
        this.soundVolume = soundVolume;
        this.assets = assets;
        this.stage = new Stage(new ExtendViewport(1920, 1080));
        this.cutsceneManager = new CutsceneManager();
        this.character = character;

        portalMusic = assets.getAssetManager().get(Assets.portalMusic);
        portalMusic.setLooping(true);
        portalMusic.setVolume(musicVolume / 100f);

        cutsceneMusic = assets.getAssetManager().get(Assets.introMusic);
        cutsceneMusic.setLooping(true);
        cutsceneMusic.setVolume(musicVolume / 100f);
        cutsceneMusic.play();

        camera = (OrthographicCamera) stage.getCamera();

        tiledMap = assets.getAssetManager().get(Assets.cutscene2Map);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        setupCutscene();
    }

    private void setupCutscene() {
        camera.zoom = 0.3f;
        camera.position.set(348, 315, 0);
        c1 = new Sprite(new Vector2(342, 100), assets.getAssetManager().get(Assets.idleTexture) , assets.getAssetManager().get(Assets.walkTexture) , assets.getAssetManager().get(Assets.walkBackTexture) , 9, 3 ,4, 48);
        cutsceneManager.addEvent(new FadeOutTextEvent(
                stage,
                assets.getAssetManager().get(Assets.skin),
                "Mylo managed to reach the portal....",
                assets.getAssetManager().get(Assets.blackPixelTexture),
                1f, 2f, 1f
        ));
        cutsceneManager.addEvent(new MoveCharacterEvent(c1, new Vector2(c1.getPosition().x,c1.getPosition().y+150), 40));
        cutsceneManager.addEvent(new WaitEvent(0.2f));
        cutsceneManager.addEvent(new SpeakEvent( stage, assets.getAssetManager().get(Assets.skin), " This should take me to the Duck Empire. "
                , assets.getAssetManager().get(Assets.dialogBoxTexture) , assets.getAssetManager().get(Assets.myloPortraitTexture)));
        cutsceneManager.addEvent(new WaitEvent(0.2f));
        cutsceneManager.addEvent(new MoveCharacterEvent(c1, new Vector2(c1.getPosition().x,c1.getPosition().y+320), 40));
        cutsceneManager.addEvent(new HideMapLayerEvent(tiledMap, "Portal" , assets.getAssetManager().get(Assets.thunderSound) ));
        cutsceneManager.addEvent(new FadeToNightEvent(
                stage,
                assets.getAssetManager().get(Assets.blackPixelTexture),
                1.5f
        ));
        cutsceneManager.addEvent(new ChangeMusicEvent(
                cutsceneMusic,
                portalMusic,
                musicVolume / 100f
        ));
        cutsceneManager.addEvent(new SpeakEvent( stage, assets.getAssetManager().get(Assets.skin), " This can't be good. "
                , assets.getAssetManager().get(Assets.dialogBoxTexture) , assets.getAssetManager().get(Assets.myloPortraitTexture)));
        cutsceneManager.addEvent(new PortalSpawnEvent(
                stage,
                assets.getAssetManager().get(Assets.portalTexture),
                305, 385
        ));

        cutsceneManager.addEvent(new WaitEvent(0.2f));


        cutsceneManager.addEvent(new FadeEndEvent(
                stage,
                assets.getAssetManager().get(Assets.skin),
                assets.getAssetManager().get(Assets.blackPixelTexture),
                1.4f,
                1f,
                1f,
                "The journey continues..."
        ));

    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.justTouched()) {
            cutsceneManager.skipEvent();
        }

        ScreenUtils.clear(0, 0, 0, 1);
        cutsceneManager.update(delta);

        camera.update();

        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        stage.act(delta);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        c1.setStateTime(c1.getStateTime() + delta);
        c1.render(batch);
        batch.end();

        stage.draw();

        if (cutsceneManager.isFinished()) {
            portalMusic.stop();
            dispose();
            StoryMode storyMode = new StoryMode(assets, soundVolume, musicVolume);
            storyMode.setCurrentMapIndex(1);

            character.setPosition(new Vector2(243, 450));
            storyMode.setCharacter(character);

            storyMode.show(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            game.setScreen(new GameScene(game, storyMode));
        }
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        tiledMapRenderer.dispose();
        tiledMap.dispose();
        cutsceneMusic.dispose();
        portalMusic.dispose();
    }
}
