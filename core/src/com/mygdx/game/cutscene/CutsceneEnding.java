package com.mygdx.game.cutscene;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.game.GameScene;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.entities.character.Sprite;
import com.mygdx.game.game_modes.StoryMode;
import com.mygdx.game.utilities_resources.Assets;

public class CutsceneEnding extends BaseCutsceneScreen{

    private final MyGdxGame game;
    private final int musicVolume;
    private final int soundVolume;
    private final Assets assets;
    private Sprite c1;
    private Sprite c2;
    private final Music cutsceneMusic;
    private final RayHandler rayHandler;
    TiledMap tiledMap;
    private final float timePlayed;

    public CutsceneEnding(MyGdxGame game, int musicVolume, int soundVolume, Assets assets , float timePlayed) {
        super(new Stage(new ExtendViewport(1920, 1080)), new CutsceneManager(), assets.getAssetManager().get(Assets.cutscene4Map));
        this.game = game;
        this.musicVolume = musicVolume;
        this.soundVolume = soundVolume;
        this.assets = assets;
        this.timePlayed = timePlayed;

        tiledMap = assets.getAssetManager().get(Assets.cutscene4Map);
        World world = new World(new Vector2(0, 0), true);
        rayHandler = new RayHandler(world);
        RayHandler.useDiffuseLight(true);
        rayHandler.setAmbientLight(0.55f);

        cutsceneMusic = assets.getAssetManager().get(Assets.plotTwistMusic);
        cutsceneMusic.setLooping(true);
        cutsceneMusic.setVolume(musicVolume / 100f);
        cutsceneMusic.play();


        setupCutscene();
    }

    private void setupCutscene() {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.zoom = 0.2f;
        camera.position.set(390, 400, 0);

        c1 = new Sprite(new Vector2(550, 360), assets.getAssetManager().get(Assets.idleTexture) , assets.getAssetManager().get(Assets.walkTexture) , assets.getAssetManager().get(Assets.walkBackTexture) ,9, 3 ,4, 48);
        c2 = new Sprite(new Vector2(350, 360), assets.getAssetManager().get(Assets.idleTextureGrandpa) , assets.getAssetManager().get(Assets.walkTexture) , assets.getAssetManager().get(Assets.walkBackTexture) , 9, 3 ,4,48);


        cutsceneManager.addEvent(new FadeOutTextEvent(
                stage,
                assets.getAssetManager().get(Assets.skin),
                "Mylo reached his grandpa....",
                assets.getAssetManager().get(Assets.blackPixelTexture),
                1f, 2f, 1f
        ));

        ShowImageEvent showImageEvent = new ShowImageEvent(stage, assets.getAssetManager().get(Assets.goldenAppleTexture), 351, 360,
                10, 10);
        cutsceneManager.addEvent(showImageEvent);

        cutsceneManager.addEvent(new MoveCharacterEvent(c1, new Vector2(400,360), 80));

        cutsceneManager.addEvent(new SpeakEvent( stage, assets.getAssetManager().get(Assets.skin), " Grandpa , You lied , you need to stop , the world is dying. "
                , assets.getAssetManager().get(Assets.dialogBoxTexture) , assets.getAssetManager().get(Assets.myloPortraitTexture)));

        cutsceneManager.addEvent(new WaitEvent(0.2f));

        cutsceneManager.addEvent(new SpeakEvent( stage, assets.getAssetManager().get(Assets.skin), " You are a fool! You will never understand true power! "
                , assets.getAssetManager().get(Assets.dialogBoxTexture) , assets.getAssetManager().get(Assets.evilGrandpaPortraitTexture)));

        cutsceneManager.addEvent(new WaitEvent(0.2f));

        cutsceneManager.addEvent(new SpeakEvent( stage, assets.getAssetManager().get(Assets.skin), " You leave me no choice .  "
                , assets.getAssetManager().get(Assets.dialogBoxTexture) , assets.getAssetManager().get(Assets.myloPortraitTexture)));

        cutsceneManager.addEvent(new WaitEvent(0.2f));

        cutsceneManager.addEvent(new SpeakEvent( stage, assets.getAssetManager().get(Assets.skin), " I'm sorry Grandpa .  "
                , assets.getAssetManager().get(Assets.dialogBoxTexture) , assets.getAssetManager().get(Assets.myloPortraitTexture)));

        cutsceneManager.addEvent(new WaitEvent(0.2f));

        cutsceneManager.addEvent(new ThrowFireBallEvent(
                stage,
                new Vector2(400,360),
                new Vector2(350, 360),
                20,
                assets.getAssetManager().get(Assets.fireBallTexture),
                assets.getAssetManager().get(Assets.throwExplosionSound)
        ));

        cutsceneManager.addEvent(new RemoveImageEvent(showImageEvent.getImage()));

        cutsceneManager.addEvent(new SpeakEvent( stage, assets.getAssetManager().get(Assets.skin), " I'm sorry grandpa ! .  "
                , assets.getAssetManager().get(Assets.dialogBoxTexture) , assets.getAssetManager().get(Assets.myloPortraitTexture)));

        cutsceneManager.addEvent(new WaitEvent(0.2f));

        cutsceneManager.addEvent(new FadeEndEvent(
                stage,
                assets.getAssetManager().get(Assets.skin),
                assets.getAssetManager().get(Assets.blackPixelTexture),
                1f,
                1f,
                1f,
                "Mylo saved the world , but at high cost."
        ));
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (cutsceneManager.isFinished()) {
            cutsceneMusic.stop();
            dispose();

            StoryMode storyMode = new StoryMode(assets, soundVolume, musicVolume);
            storyMode.setCurrentMapIndex(2);
            storyMode.setTimePlayed(timePlayed);
            storyMode.setFinishedGame(true);

            storyMode.show(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            game.setScreen(new GameScene(game, storyMode));
        }
    }

    @Override
    protected void renderSprites(float delta) {
        c1.setStateTime(c1.getStateTime() + delta);
        c1.render(batch);
        c2.setStateTime(c2.getStateTime() + delta);
        c2.render(batch);
    }


    @Override
    public void dispose() {
        super.dispose();
        cutsceneMusic.dispose();
    }
}
