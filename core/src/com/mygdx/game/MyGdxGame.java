package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.mygdx.game.ui_screens.MainMenuScreen;

public class MyGdxGame extends Game {
	@Override
	public void create() {
		MainMenuScreen mainMenuScreen = new MainMenuScreen(this);
		setScreen(mainMenuScreen);
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