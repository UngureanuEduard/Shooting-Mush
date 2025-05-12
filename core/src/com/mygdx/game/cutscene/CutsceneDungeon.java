package com.mygdx.game.cutscene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.game.GameScene;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.entities.character.Character;
import com.mygdx.game.entities.character.Sprite;
import com.mygdx.game.game_modes.StoryMode;
import com.mygdx.game.utilities_resources.Assets;

public class CutsceneDungeon extends  BaseCutsceneScreen{

    private final MyGdxGame game;
    private final int musicVolume;
    private final int soundVolume;
    private final Assets assets;
    private Sprite c1;
    private Sprite c2;
    private final Music cutsceneMusic;
    private final Music plotTwistMusic;
    private final Character character;
    private final float timePlayed;

    public CutsceneDungeon(MyGdxGame game, int musicVolume, int soundVolume, Assets assets , Character character , float timePlayed) {
        super(new Stage(new ExtendViewport(1920, 1080)), new CutsceneManager(), assets.getAssetManager().get(Assets.cutscene3Map));
        this.game = game;
        this.musicVolume = musicVolume;
        this.soundVolume = soundVolume;
        this.assets = assets;
        this.character = character;
        this.timePlayed = timePlayed;


        plotTwistMusic = assets.getAssetManager().get(Assets.plotTwistMusic);
        plotTwistMusic.setLooping(true);
        plotTwistMusic.setVolume(musicVolume / 100f);

        cutsceneMusic = assets.getAssetManager().get(Assets.introMusic);
        cutsceneMusic.setLooping(true);
        cutsceneMusic.setVolume(musicVolume / 100f);
        cutsceneMusic.play();


        setupCutscene();
    }

    private void setupCutscene() {
        camera.zoom = 0.3f;
        camera.position.set(315, 335, 0);
        c1 = new Sprite(new Vector2(300, 100), assets.getAssetManager().get(Assets.idleTexture) , assets.getAssetManager().get(Assets.walkTexture) , assets.getAssetManager().get(Assets.walkBackTexture) , 9, 3 ,4, 48);
        c2 = new Sprite(new Vector2(650, 400), assets.getAssetManager().get(Assets.pigKingIdleTexture) , assets.getAssetManager().get(Assets.pigKingWalkTexture) , assets.getAssetManager().get(Assets.walkBackTexture) , 5, 4 ,4, 32);

                cutsceneManager.addEvent(new FadeOutTextEvent(
                stage,
                assets.getAssetManager().get(Assets.skin),
                "Mylo reached the treasure room....",
                assets.getAssetManager().get(Assets.blackPixelTexture),
                1f, 2f, 1f
        ));

        cutsceneManager.addEvent(new MoveCharacterEvent(c1, new Vector2(c1.getPosition().x,c1.getPosition().y+200), 40));
        cutsceneManager.addEvent(new SpeakEvent( stage, assets.getAssetManager().get(Assets.skin), " What's with that painting? "
                , assets.getAssetManager().get(Assets.dialogBoxTexture) , assets.getAssetManager().get(Assets.myloPortraitTexture)));
        cutsceneManager.addEvent(new MoveCharacterEvent(c1, new Vector2(c1.getPosition().x,c1.getPosition().y+300), 40));
        cutsceneManager.addEvent(new ImageDisplayEvent(
                stage,
                assets.getAssetManager().get(Assets.portraitTexture),
                1.0f,
                2.0f,
                1.0f
        ));
        cutsceneManager.addEvent(new ChangeMusicEvent(
                cutsceneMusic,
                plotTwistMusic,
                musicVolume / 100f
        ));

        cutsceneManager.addEvent(new SpeakEvent( stage, assets.getAssetManager().get(Assets.skin), " No! This can't be , this is... "
                , assets.getAssetManager().get(Assets.dialogBoxTexture) , assets.getAssetManager().get(Assets.myloPortraitTexture)));
        cutsceneManager.addEvent(new SpeakEvent( stage, assets.getAssetManager().get(Assets.skin), " Your Grandfather. Oink ! Oink! "
                , assets.getAssetManager().get(Assets.dialogBoxTexture) , assets.getAssetManager().get(Assets.pigKingPortraitTexture)));
        cutsceneManager.addEvent(new MoveCharacterEvent(c2, new Vector2(c2.getPosition().x-300,c2.getPosition().y), 40));
        cutsceneManager.addEvent(new SpeakEvent( stage, assets.getAssetManager().get(Assets.skin), " Your grandfather used the apple to enslave the animals.  Oink ! Oink! "
                , assets.getAssetManager().get(Assets.dialogBoxTexture) , assets.getAssetManager().get(Assets.pigKingPortraitTexture)));
        cutsceneManager.addEvent(new SpeakEvent( stage, assets.getAssetManager().get(Assets.skin), " He never lost the apple  Mylo, he just wanted you to learn to hate us.  Oink ! Oink! "
                , assets.getAssetManager().get(Assets.dialogBoxTexture) , assets.getAssetManager().get(Assets.pigKingPortraitTexture)));
        cutsceneManager.addEvent(new SpeakEvent( stage, assets.getAssetManager().get(Assets.skin), " I will stop him and set you free. "
                , assets.getAssetManager().get(Assets.dialogBoxTexture) , assets.getAssetManager().get(Assets.myloPortraitTexture)));
        cutsceneManager.addEvent(new SpeakEvent( stage, assets.getAssetManager().get(Assets.skin), " I will show you the way back. Oink ! Oink! "
                , assets.getAssetManager().get(Assets.dialogBoxTexture) , assets.getAssetManager().get(Assets.pigKingPortraitTexture)));
        cutsceneManager.addEvent(new WaitEvent(0.3f));
        cutsceneManager.addEvent(new FadeEndEvent(
                stage,
                assets.getAssetManager().get(Assets.skin),
                assets.getAssetManager().get(Assets.blackPixelTexture),
                1f,
                1f,
                1f,
                "Mylo rushed back , he needed to make things right..."
        ));
    }

    @Override
    public void render(float delta) {

        super.render(delta);

        if (cutsceneManager.isFinished()) {
            plotTwistMusic.stop();
            dispose();
            StoryMode storyMode = new StoryMode(assets, soundVolume, musicVolume);
            storyMode.setCurrentMapIndex(2);
            storyMode.setCharacter(character);
            storyMode.setTimePlayed(timePlayed);

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
        plotTwistMusic.dispose();
    }
}
