package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.utilities_resources.*;

public class GameScene extends ScreenAdapter {

    Viewport viewport = new ExtendViewport(1920, 1080);
    private final Stage stage = new Stage(viewport);
    SpriteBatch batch;
    Assets assets;
    MyGdxGame game;

    public enum GameMode {
        ARENA,
        STORY
    }

    private final GameMode gameMode;

    private final ArenaMode arenaMode;
    private final StoryMode storyMode;


    public GameScene(MyGdxGame game, Integer musicVolume, Integer soundVolume, GameMode gameMode ,Assets assets) {
        this.game = game;
        this.gameMode = gameMode;
        this.assets = assets;
        arenaMode = new ArenaMode(assets,soundVolume,musicVolume);
        storyMode = new StoryMode(assets ,soundVolume ,musicVolume);
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        if (gameMode == GameMode.ARENA) {
            arenaMode.show();
        } else {
            storyMode.show();
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1, 0, 0, 1);
        batch.begin();

        if (gameMode == GameMode.ARENA) {
            arenaMode.render(delta,batch,game,stage);
        }
        else if (gameMode == GameMode.STORY) {
            storyMode.Render(delta,batch,game,stage);
        }

        batch.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        assets.dispose();
        arenaMode.dispose();
        storyMode.dispose();
    }

}