package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;

import java.io.FileNotFoundException;

public class VideoScreen implements Screen {

    private static final Integer SKIP_TIME = 2;

    private final MyGdxGame game;
    private final SpriteBatch batch;
    private VideoPlayer videoPlayer;
    private Sound audio;
    private final int musicVolume;
    private final int soundVolume;
    private float spaceKeyHoldTime;
    private BitmapFont font;

    public VideoScreen(MyGdxGame game , int musicVolume , int soundVolume) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.musicVolume=musicVolume;
        this.soundVolume=soundVolume;
        this.spaceKeyHoldTime = 0;
    }

    @Override
    public void show() {
        videoPlayer = VideoPlayerCreator.createVideoPlayer();
        audio = Gdx.audio.newSound(Gdx.files.local("assets/intro.wav"));
        videoPlayer.setOnCompletionListener(file -> startGame());

        font = new BitmapFont();


        try {
            videoPlayer.play(Gdx.files.local("assets/intro.webm"));
            audio.play(musicVolume / 100f);
        } catch (FileNotFoundException e) {
            Gdx.app.error("gdx-video", "Oh no!");
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        videoPlayer.update();

        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
            spaceKeyHoldTime += delta;
            if (spaceKeyHoldTime >= SKIP_TIME) {
                startGame();
            }
        } else {
            spaceKeyHoldTime = 0;
        }

        batch.begin();
        Texture frame = videoPlayer.getTexture();
        if (frame != null) {
            int screenWidth = Gdx.graphics.getWidth();
            int screenHeight = Gdx.graphics.getHeight();
            int videoWidth = videoPlayer.getVideoWidth();
            int videoHeight = videoPlayer.getVideoHeight();
            float x = (screenWidth - videoWidth) / 2f;
            float y = (screenHeight - videoHeight) / 2f;
            batch.draw(frame, x, y, videoWidth, videoHeight);
        }

        String message = "Hold space to Skip";
        float messageX = Gdx.graphics.getWidth() - 150;
        float messageY = 30;
        font.draw(batch, message, messageX, messageY);

        batch.end();
    }

    private void startGame() {
        audio.stop();
        videoPlayer.stop();
        game.setScreen(new GameScene(game, musicVolume, soundVolume, GameScene.GameMode.STORY));
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
        batch.dispose();
        videoPlayer.dispose();
        audio.dispose();
        font.dispose();
    }

    @Override
    public void dispose() {
    }
}