package com.mygdx.game.ui_screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;
import com.mygdx.game.GameScene;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.utilities_resources.Assets;

import java.io.FileNotFoundException;

import static com.mygdx.game.utilities_resources.Settings.*;

public class VideoScreen implements Screen {

    private static final int SKIP_TIME_SECONDS = 2;
    private static final float AUDIO_VOLUME_DIVISOR = 100f;
    private static final int SKIP_TEXT_OFFSET_X = 150;
    private static final int SKIP_TEXT_OFFSET_Y = 30;
    private static final float CLEAR_COLOR_R = 0f;
    private static final float CLEAR_COLOR_G = 0f;
    private static final float CLEAR_COLOR_B = 0f;
    private static final float CLEAR_COLOR_A = 1f;
    private static final String SKIP_MESSAGE = "Hold space to Skip";
    private static final String VIDEO_PATH = "assets/intro.webm";
    private static final String LOG_TAG = "gdx-video";
    private static final String VIDEO_NOT_FOUND_MSG = "Video file not found!";
    private static final String VIDEO_PLAY_ERROR_MSG = "An error occurred while playing the video.";

    private final MyGdxGame game;
    private final SpriteBatch batch;
    private VideoPlayer videoPlayer;
    private Sound audio;
    private final int musicVolume;
    private final int soundVolume;
    private float spaceKeyHoldTime;
    private BitmapFont font;
    private final Assets assets;
    private final float screenWidth ;
    private final float screenHeight ;

    public VideoScreen(MyGdxGame game, int musicVolume, int soundVolume, Assets assets ,float screenWidth ,float screenHeight ) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.musicVolume = musicVolume;
        this.soundVolume = soundVolume;
        this.spaceKeyHoldTime = 0;
        this.assets = assets;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    public void show() {
        videoPlayer = VideoPlayerCreator.createVideoPlayer();
        videoPlayer.setVolume(0);
        audio = assets.getAssetManager().get(Assets.introSound);
        videoPlayer.setOnCompletionListener(file -> startGame());
        font = new BitmapFont();

        try {
            videoPlayer.load(Gdx.files.local(VIDEO_PATH));
            videoPlayer.play();
            audio.play(musicVolume / AUDIO_VOLUME_DIVISOR);
        } catch (FileNotFoundException e) {
            Gdx.app.error(LOG_TAG, VIDEO_NOT_FOUND_MSG, e);
        } catch (Exception e) {
            Gdx.app.error(LOG_TAG, VIDEO_PLAY_ERROR_MSG, e);
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(CLEAR_COLOR_R, CLEAR_COLOR_G, CLEAR_COLOR_B, CLEAR_COLOR_A);
        videoPlayer.update();

        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
            spaceKeyHoldTime += delta;
            if (spaceKeyHoldTime >= SKIP_TIME_SECONDS) {
                startGame();
            }
        } else {
            spaceKeyHoldTime = 0;
        }

        batch.begin();
        Texture frame = videoPlayer.getTexture();
        if (frame != null) {

            int videoWidth = videoPlayer.getVideoWidth();
            int videoHeight = videoPlayer.getVideoHeight();

            float x = (screenWidth - videoWidth) / 2f;
            float y = (screenHeight - videoHeight) / 2f;

            if(windowed){
                batch.draw(frame, x, y, windowedScreenWidth, windowedScreenHeight);
            } else {
                batch.draw(frame, x, y, fullScreenWidth, fullScreenHeight);
            }
        }

        font.draw(batch, SKIP_MESSAGE, Gdx.graphics.getWidth() - SKIP_TEXT_OFFSET_X , (float) SKIP_TEXT_OFFSET_Y);
        batch.end();
    }

    private void startGame() {
        if (audio != null) {
            audio.stop();
        }
        if (videoPlayer != null) {
            videoPlayer.stop();
        }
        game.setScreen(new GameScene(game, musicVolume, soundVolume, GameScene.GameMode.STORY, assets));
    }

    @Override
    public void resize(int width, int height) {
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
        if (audio != null) {
            audio.stop();
            audio.dispose();
        }
        if (videoPlayer != null) {
            videoPlayer.dispose();
        }
        batch.dispose();
        if (font != null) {
            font.dispose();
        }
    }
}
