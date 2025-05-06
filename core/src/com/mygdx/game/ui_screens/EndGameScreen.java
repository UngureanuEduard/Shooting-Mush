package com.mygdx.game.ui_screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.animations_effects.firework.Firework;
import com.mygdx.game.utilities_resources.Assets;
import com.mygdx.game.utilities_resources.Settings;

public abstract class EndGameScreen extends ScreenAdapter {

    protected static final int NAME_MAX_LENGTH = 7;
    protected static final int CHAR_OFFSET = 36;
    protected static final int AUTO_RETURN_TIME = 5;
    protected static final float FIREWORK_INTERVAL = 0.4f;

    protected final MyGdxGame game;
    protected final Stage stage;
    protected final Texture trophyTexture;
    protected final Texture fireworksExplosionTexture;
    protected final Texture fireworksRocketTexture;
    protected final Skin skin;
    protected final Assets assets;
    protected final int finalScore;
    protected final int musicVolume;
    protected final int soundVolume;

    protected final StringBuilder playerName = new StringBuilder();
    protected final Array<Firework> fireworks = new Array<>();

    protected float fireworkTimer = 0;
    protected float cursorTimer = 0;
    protected float timeSinceSubmission = 0;
    protected boolean cursorVisible = true;
    protected boolean submitted = false;

    protected Table inputTable;
    protected Label inputLabel;

    protected float worldWidth ;
    protected float worldHeight ;

    public EndGameScreen(MyGdxGame game, int score, Assets assets, int musicVolume, int soundVolume ) {
        this.game = game;
        this.finalScore = score;
        this.assets = assets;
        this.musicVolume = musicVolume;
        this.soundVolume = soundVolume;
        this.stage = new Stage();
        worldWidth = stage.getViewport().getWorldWidth();
        worldHeight = stage.getViewport().getWorldHeight();

        this.trophyTexture = assets.getAssetManager().get(Assets.goldTrophyTexture);
        this.fireworksExplosionTexture = assets.getAssetManager().get(Assets.fireworkExplosionTexture);
        this.fireworksRocketTexture = assets.getAssetManager().get(Assets.fireworkRocketTexture);

        Gdx.input.setInputProcessor(stage);

        this.skin = new Skin(Gdx.files.internal("Font/menu.json"));
        setupUI();
    }

    private void setupUI() {
        Image trophyImage = new Image(trophyTexture);
        if(Settings.windowed){
            trophyImage.setScale((float) Settings.windowedScreenWidth /Settings.fullScreenWidth , (float) Settings.windowedScreenHeight /Settings.fullScreenHeight);
        }
        trophyImage.setPosition(worldWidth / 2f - trophyImage.getWidth() / 2, worldHeight / 1.7f);
        stage.addActor(trophyImage);

        inputTable = new Table();
        inputTable.setSkin(skin);
        inputTable.setPosition(worldWidth / 2f, worldHeight / 2.2f);
        inputTable.center();

        Label scoreLabel = new Label("Score: " + finalScore, skin);
        Label nameLabel = new Label("Name:", skin);
        inputLabel = new Label("_", skin);

        inputTable.add(scoreLabel).colspan(2).padBottom(20).row();
        inputTable.add(nameLabel).left().padRight(10);
        inputTable.add(inputLabel).left().width(200).row();

        stage.addActor(inputTable);
    }


    public void render(float delta , SpriteBatch batch) {
        updateFireworks(delta);

        if (!submitted) {
            handleNameInput(delta);
        } else {
            timeSinceSubmission += delta;
            if (timeSinceSubmission >= AUTO_RETURN_TIME) {
                dispose();
                game.setScreen(new MainMenuScreen(game, assets, musicVolume, soundVolume));
            }
        }

        batch.setProjectionMatrix(stage.getCamera().combined);

        for (Firework firework : fireworks) {
            firework.render(batch);
        }

        stage.draw();
    }

    private void updateFireworks(float delta) {
        fireworkTimer += delta;
        if (fireworkTimer >= FIREWORK_INTERVAL) {
            fireworkTimer = 0;
            float x = MathUtils.random(0, Gdx.graphics.getWidth() - 256);
            fireworks.add(new Firework(fireworksRocketTexture, fireworksExplosionTexture, new Vector2(x, 0)));
        }

        for (int i = 0; i < fireworks.size; i++) {
            Firework f = fireworks.get(i);
            f.update(delta);
            if (f.isFinished()) {
                fireworks.removeIndex(i);
                i--;
            }
        }
    }

    private void handleNameInput(float delta) {
        cursorTimer += delta;
        if (cursorTimer >= 0.5f) {
            cursorVisible = !cursorVisible;
            cursorTimer = 0;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE) && playerName.length() > 0) {
            playerName.deleteCharAt(playerName.length() - 1);
        }

        for (int i = Input.Keys.A; i <= Input.Keys.Z; i++) {
            if (Gdx.input.isKeyJustPressed(i) && playerName.length() < NAME_MAX_LENGTH) {
                playerName.append((char) (i + CHAR_OFFSET));
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && playerName.length() > 0) {
            insertScore();
            updateTopScores();
            submitted = true;
            inputTable.remove();
        }

        StringBuilder displayText = new StringBuilder(playerName);
        if (playerName.length() < NAME_MAX_LENGTH) {
            displayText.append(cursorVisible ? "_" : " ");
        }
        while (displayText.length() < NAME_MAX_LENGTH + 1) displayText.append(" ");
        inputLabel.setText(displayText.toString());
    }

    protected abstract void insertScore();

    protected abstract void updateTopScores();

    protected abstract Table createScoreTable(java.util.List<String[]> scores, int maxEntries);

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    public Stage getStage() {
        return stage;
    }
}
