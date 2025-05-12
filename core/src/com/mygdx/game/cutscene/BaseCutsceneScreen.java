package com.mygdx.game.cutscene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;

public abstract class BaseCutsceneScreen implements Screen {
    protected final Stage stage;
    protected final CutsceneManager cutsceneManager;
    protected final SpriteBatch batch = new SpriteBatch();
    protected final OrthographicCamera camera;
    protected final OrthogonalTiledMapRenderer tiledMapRenderer;
    protected final TiledMap tiledMap;

    public BaseCutsceneScreen(Stage stage, CutsceneManager cutsceneManager, TiledMap tiledMap) {
        this.stage = stage;
        this.cutsceneManager = cutsceneManager;
        this.tiledMap = tiledMap;
        this.camera = (OrthographicCamera) stage.getCamera();
        this.tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
    }

    protected abstract void renderSprites(float delta);

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
        renderSprites(delta);
        batch.end();

        stage.draw();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        tiledMapRenderer.dispose();
        tiledMap.dispose();
    }
}
