package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.game_modes.ArenaMode;
import com.mygdx.game.game_modes.CoopMode;
import com.mygdx.game.game_modes.StoryMode;
import com.mygdx.game.utilities_resources.*;

public class GameScene extends ScreenAdapter {

    Viewport viewport = new ExtendViewport(1920, 1080);
    private final Stage stage = new Stage(viewport);
    SpriteBatch batch;
    Assets assets;
    MyGdxGame game;

    public enum GameMode {
        ARENA,
        STORY,
        CO_OP
    }

    private final GameMode gameMode;
    private  ArenaMode arenaMode;
    private  StoryMode storyMode;
    private  CoopMode coopMode;


    public GameScene(MyGdxGame game, Integer musicVolume, Integer soundVolume, GameMode gameMode ,Assets assets) {
        this.game = game;
        this.gameMode = gameMode;
        this.assets = assets;

        switch (gameMode) {
            case ARENA:
                arenaMode = new ArenaMode(assets,soundVolume,musicVolume);
                break;
            case STORY:
                storyMode = new StoryMode(assets ,soundVolume ,musicVolume);
                break;
            case CO_OP:
                coopMode = new CoopMode(assets, soundVolume, musicVolume);
                break;
        }
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        switch (gameMode) {
            case ARENA:
                arenaMode.show();
                break;
            case STORY:
                storyMode.show();
                break;
            case CO_OP:
                coopMode.show();
                break;
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1, 0, 0, 1);
        batch.begin();

        switch (gameMode) {
            case ARENA:
                arenaMode.render(delta, batch, game, stage);
                break;
            case STORY:
                storyMode.Render(delta, batch, game, stage);
                break;
            case CO_OP:
                coopMode.Render(delta, batch, game, stage);
                break;
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

    public CoopMode getCoopMode() {
        return coopMode;
    }
}