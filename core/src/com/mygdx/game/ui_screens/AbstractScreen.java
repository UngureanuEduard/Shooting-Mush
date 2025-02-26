package com.mygdx.game.ui_screens;

import com.badlogic.gdx.Screen;
import com.mygdx.game.MyGdxGame;

public abstract class AbstractScreen implements Screen {

    protected MyGdxGame game;

    public AbstractScreen(MyGdxGame game) {
        this.game = game;
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}