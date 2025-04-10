package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.mygdx.game.ui_screens.LoadingScreen;

public class MyGdxGame extends Game {
	@Override
	public void create() {
		setScreen(new LoadingScreen(this));
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void dispose() {
		super.dispose();
	}

}