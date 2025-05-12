package com.mygdx.game.cutscene;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.game.GameScene;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.entities.character.Sprite;
import com.mygdx.game.utilities_resources.Assets;

public class CutsceneScreenIntro extends BaseCutsceneScreen {
    private final MyGdxGame game;
    private final int musicVolume;
    private final int soundVolume;
    private final Assets assets;
    private Sprite c1;
    private Sprite c2;
    private Sprite c3;
    private final Music cutsceneMusic;
    private final Music trainMusic;


        public CutsceneScreenIntro(MyGdxGame game, int musicVolume, int soundVolume, Assets assets  ) {
            super(new Stage(new ExtendViewport(1920, 1080)), new CutsceneManager(), assets.getAssetManager().get(Assets.cutscene1Map));
        this.game = game;
        this.musicVolume = musicVolume;
        this.soundVolume = soundVolume;
        this.assets = assets;

        cutsceneMusic = assets.getAssetManager().get(Assets.introMusic);
        trainMusic = assets.getAssetManager().get(Assets.trainMusic);
        cutsceneMusic.setLooping(true);
        cutsceneMusic.setVolume(musicVolume / 100f);
        cutsceneMusic.play();

        setupCutscene();
    }

    private void setupCutscene() {

        camera.zoom = 0.2f;
        camera.position.set(490, 500, 0);

        c1 = new Sprite(new Vector2(0, 0), assets.getAssetManager().get(Assets.idleTexture) , assets.getAssetManager().get(Assets.walkTexture) , assets.getAssetManager().get(Assets.walkBackTexture) ,9, 3 ,4, 48);
        c2 = new Sprite(new Vector2(500, 470), assets.getAssetManager().get(Assets.idleTextureGrandpa) , assets.getAssetManager().get(Assets.walkTexture) , assets.getAssetManager().get(Assets.walkBackTexture) , 9, 3 ,4,48);
        c3 = new Sprite(new Vector2(700, 470), assets.getAssetManager().get(Assets.idleEnemyTexture) , assets.getAssetManager().get(Assets.duckTexture) , assets.getAssetManager().get(Assets.walkBackTexture) ,  4 , 6 ,4,32);
        c3.setFlipped(true);


        cutsceneManager.addEvent(new FadeOutTextEvent(
                stage,
                assets.getAssetManager().get(Assets.skin),
                "A long time ago....",
                assets.getAssetManager().get(Assets.blackPixelTexture),
                1f, 2f, 1f
        ));

        cutsceneManager.addEvent(new MoveCharacterEvent(c3, new Vector2(600,c3.getPosition().y), 40));

        cutsceneManager.addEvent(new HideMapLayerEvent(tiledMap, "GoldenApple" , assets.getAssetManager().get(Assets.throwSound) ));

        cutsceneManager.addEvent(new WaitEvent(0.2f));

        cutsceneManager.addEvent(new SpeakEvent( stage, assets.getAssetManager().get(Assets.skin), " Nooooooo! "
                        , assets.getAssetManager().get(Assets.dialogBoxTexture) , assets.getAssetManager().get(Assets.grandpaPortraitTexture) ));
        cutsceneManager.addEvent(new RemoveCharacterEvent(c3));
        cutsceneManager.addEvent(new ScreenFadeEvent(
                stage,
                assets.getAssetManager().get(Assets.blackPixelTexture),
                1f, 1f, 1f,
                c1, new Vector2(480, 470),
                c3, new Vector2(0, 0)
        ));


        cutsceneManager.addEvent(new FlipCharacterEvent(c2));

        cutsceneManager.addEvent(new SpeakEvent( stage, assets.getAssetManager().get(Assets.skin), " Mylo you need to save the world , get the apple back , I will train you. "
                , assets.getAssetManager().get(Assets.dialogBoxTexture) , assets.getAssetManager().get(Assets.grandpaPortraitTexture) ));

        cutsceneManager.addEvent(new WaitEvent(0.2f));

        cutsceneManager.addEvent(new SpeakEvent( stage, assets.getAssetManager().get(Assets.skin), " Ok grandpa , I will train. "
                         , assets.getAssetManager().get(Assets.dialogBoxTexture) , assets.getAssetManager().get(Assets.myloPortraitTexture)));

        cutsceneManager.addEvent(new WaitEvent(0.2f));

        cutsceneManager.addEvent(new ChangeMusicEvent(
                cutsceneMusic,
                trainMusic,
                musicVolume / 100f
        ));

        cutsceneManager.addEvent(new ScreenFadeEvent(
                stage,
                assets.getAssetManager().get(Assets.blackPixelTexture),
                1f, 1f, 1f,
                c1, new Vector2(480, 470),
                c2, new Vector2(500, 470)
        ));

        cutsceneManager.addEvent(new MoveCharacterWithSpeedEvent(c1, new Vector2(460  , 470), 100, 0.05f));

        cutsceneManager.addEvent(new WaitEvent(0.3f));

        cutsceneManager.addEvent(new ThrowAppleEvent(
                stage,
                new Vector2(460 , 470).add((float) c1.getWidth() /2, (float) c1.getHeight() /2),
                new Vector2(460 , 500).add(-80, -30),
                40,
                assets.getAssetManager().get(Assets.bulletTexture),
                assets.getAssetManager().get(Assets.throwSound)
        ));

        cutsceneManager.addEvent(new WaitEvent(0.2f));

        cutsceneManager.addEvent(new SpeakEvent( stage, assets.getAssetManager().get(Assets.skin), " Further! "
                , assets.getAssetManager().get(Assets.dialogBoxTexture) , assets.getAssetManager().get(Assets.grandpaPortraitTexture) ));

        cutsceneManager.addEvent(new ThrowAppleEvent(
                stage,
                new Vector2(460 , 470).add((float) c1.getWidth() /2, (float) c1.getHeight() /2),
                new Vector2(460 , 500).add(-110, -30),
                70,
                assets.getAssetManager().get(Assets.bulletTexture),
                assets.getAssetManager().get(Assets.throwSound)
        ));

        cutsceneManager.addEvent(new WaitEvent(0.2f));

        cutsceneManager.addEvent(new SpeakEvent( stage, assets.getAssetManager().get(Assets.skin), "Now, Mylo... channel everything you've got!"
                , assets.getAssetManager().get(Assets.dialogBoxTexture) , assets.getAssetManager().get(Assets.grandpaPortraitTexture) ));

        cutsceneManager.addEvent(new WaitEvent(0.8f));

        cutsceneManager.addEvent(new ThrowFireBallEvent(
                stage,
                new Vector2(460 ,470),
                new Vector2(460 ,470).add(-300, 0),
                200,
                assets.getAssetManager().get(Assets.fireBallTexture),
                assets.getAssetManager().get(Assets.throwExplosionSound)
        ));
        cutsceneManager.addEvent(new ShakeCameraEvent(stage, 0.8f, 10f));

        cutsceneManager.addEvent(new WaitEvent(0.2f));

        cutsceneManager.addEvent(new SpeakEvent( stage, assets.getAssetManager().get(Assets.skin), " You are ready. "
                , assets.getAssetManager().get(Assets.dialogBoxTexture) , assets.getAssetManager().get(Assets.grandpaPortraitTexture) ));

        cutsceneManager.addEvent(new WaitEvent(0.4f));

        cutsceneManager.addEvent(new FadeEndEvent(
                stage,
                assets.getAssetManager().get(Assets.skin),
                assets.getAssetManager().get(Assets.blackPixelTexture),
                1f,
                1f,
                1f,
                "And so , Mylo's journey stared..."
        ));
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (cutsceneManager.isFinished()) {
            trainMusic.stop();
            dispose();
            game.setScreen(new GameScene(game, musicVolume, soundVolume, GameScene.GameMode.STORY, assets));
        }
    }

    @Override
    protected void renderSprites(float delta) {
        c1.setStateTime(c1.getStateTime() + delta);
        c1.render(batch);
        c2.setStateTime(c2.getStateTime() + delta);
        c2.render(batch);
        c3.setStateTime(c3.getStateTime() + delta);
        c3.render(batch);
    }

    @Override
    public void dispose() {
        super.dispose();
        cutsceneMusic.dispose();
        trainMusic.dispose();
    }
}

