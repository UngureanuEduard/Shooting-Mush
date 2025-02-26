package com.mygdx.game.ui_screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.utilities_resources.Assets;


public class LoadingScreen extends AbstractScreen {

    private final MyGdxGame game;
    private float timeElapsed = 0;
    private final float loadTime = 2.0f;
    private Assets assets;

    public LoadingScreen(MyGdxGame game) {
        super(game);
        this.game = game;
    }

    @Override
    public void show() {
        assets = new Assets();
        assets.loadGameAssets();
        assets.getAssetManager().finishLoading();

    }

    @Override
    public void render(float delta) {
        timeElapsed += delta;

        Gdx.gl.glClearColor(.1f, .1f, .15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (timeElapsed >= loadTime) {
            // After loading time, switch to MainMenuScreen
            game.setScreen(new MainMenuScreen(game , assets));
        }
    }

    @Override
    public void resize(int i, int i1) {
    }

    @Override
    public void hide() {
    }
}
