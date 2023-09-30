package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainMenuScreen extends ScreenAdapter {

    private Stage stage;
    private Skin skin;
    private Table mainTable;
    private final MyGdxGame game;

    private Image movingImage;
    private Animation<TextureRegion> movingAnimation;
    private float stateTime = 0;

    private final SpriteBatch batch;
    private float screenWidth;
    boolean isFlipped = false;
    private boolean moveRight = true;
    private Music backgroundMusic;
    float moveSpeed = 50;
    public MainMenuScreen(MyGdxGame game) {
        this.game = game;
        batch = new SpriteBatch();
    }

    @Override
    public void show() {
        Assets assets = new Assets();
        assets.loadMenuAssets();
        assets.getAssetManager().finishLoading();
        Viewport viewport = new ExtendViewport(1920, 1080);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        skin = assets.getAssetManager().get(Assets.skin);
        mainTable = new Table();
        mainTable.setFillParent(true);
        Texture backgroundTexture = assets.getAssetManager().get(Assets.menuBackgroundTexture);
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setSize(Gdx.graphics.getWidth() + 200, Gdx.graphics.getHeight() + 200);
        stage.addActor(backgroundImage);
        stage.addActor(mainTable);
        addButton("Play").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScene());
            }

        });
        addButton("Options").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
            }
        });
        addButton("Quit").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // Get the screen width
        screenWidth = Gdx.graphics.getWidth();
        Texture movingImageTexture = assets.getAssetManager().get(Assets.duckTexture);
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < 6; i++) {
            TextureRegion frame = new TextureRegion(movingImageTexture, i * 32, 0, 32, 32);
            frames.add(frame);
        }
        movingAnimation = new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);
        movingImage = new Image(movingAnimation.getKeyFrame(0));
        movingImage.setPosition(0, (float) Gdx.graphics.getHeight() / 6);
        movingImage.setSize(80, 80);

        backgroundMusic = assets.getAssetManager().get(Assets.menuMusic);
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);
        backgroundMusic.play();
    }

    private Button addButton(String name) {
        TextButton button = new TextButton(name, skin);
        mainTable.add(button).width(Math.round(Gdx.graphics.getWidth() * 0.3)).height(Math.round(Gdx.graphics.getHeight() * 0.1)).padBottom(50);
        mainTable.row();
        return button;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.1f, .1f, .15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stateTime += delta;
        TextureRegion currentFrame = movingAnimation.getKeyFrame(stateTime);

        if (moveRight) {
            movingImage.setX(movingImage.getX() + moveSpeed * delta);
            if (movingImage.getX() + movingImage.getWidth() > screenWidth) {
                moveRight = false;
                isFlipped = true; // Set to true when you flip the image
            }
        } else {
            movingImage.setX(movingImage.getX() - moveSpeed * delta);
            if (movingImage.getX() < 0) {
                moveRight = true;
                isFlipped = false;
            }
        }
        stage.act(delta);
        stage.draw();
        batch.begin();
        batch.draw(currentFrame, movingImage.getX(), movingImage.getY(), 80, 80,
                isFlipped ? -80 : 80, 80, 1, 1, 0);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        backgroundMusic.dispose();
        stage.dispose();
    }
}