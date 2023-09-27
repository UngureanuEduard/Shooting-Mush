package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainMenuScreen extends ScreenAdapter {

    private Stage stage;
    private Skin skin;
    private Table mainTable;
    MyGdxGame game;

    public MainMenuScreen(MyGdxGame game) {
        this.game = game;
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
        stage.addActor(mainTable);
        addButton("Play").addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScene());
            }
        });
        addButton("Options").addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
            }
        });
        addButton("Quit").addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }

    private Button addButton(String name) {
        TextButton button = new TextButton(name, skin);
        mainTable.add(button).width(Math.round(Gdx.graphics.getWidth()*0.3)).height(Math.round(Gdx.graphics.getHeight()*0.1)).padBottom(50);
        mainTable.row();
        return button;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.1f, .1f, .15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        stage.dispose();
    }
}
