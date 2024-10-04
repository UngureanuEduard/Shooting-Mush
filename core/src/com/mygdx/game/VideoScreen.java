package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;

import java.io.FileNotFoundException;

public class VideoScreen implements Screen {
    private final MyGdxGame game;
    private final SpriteBatch batch;
    private VideoPlayer videoPlayer;
    private Sound audio;

    public VideoScreen(MyGdxGame game) {
        this.game = game;
        this.batch = new SpriteBatch();
    }

    @Override
    public void show() {
        videoPlayer = VideoPlayerCreator.createVideoPlayer();
        audio = Gdx.audio.newSound(Gdx.files.local("assets/intro.wav"));
        videoPlayer.setOnCompletionListener(file -> {
            audio.stop();
            game.setScreen(new GameScene(game, 100, 100, GameScene.GameMode.STORY));
        });

        try {
            videoPlayer.play(Gdx.files.local("assets/intro.webm"));
            audio.play();
        } catch (FileNotFoundException e) {
            Gdx.app.error("gdx-video", "Oh no!");
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        videoPlayer.update();

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
            batch.end();
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
    }

    @Override
    public void dispose() {
    }
}