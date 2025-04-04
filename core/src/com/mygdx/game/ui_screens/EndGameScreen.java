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
import com.mygdx.game.utilities_resources.MySQLHelper;

import java.util.List;

public class EndGameScreen extends ScreenAdapter {

    private static final float FIREWORK_INTERVAL = 0.4f;
    private static final int NAME_MAX_LENGTH = 7;
    private static final int AUTO_RETURN_TIME = 5;
    private static final int CHAR_OFFSET = 36;

    private final MyGdxGame game;
    private final Stage stage;
    private final Texture trophyTexture;
    private final SpriteBatch batch;
    private final int finalScore;
    private final Skin skin;
    private final Label inputLabel;
    private boolean cursorVisible = true;
    private boolean submitted = false;
    private float cursorTimer = 0;
    private float timeSinceSubmission = 0;
    private final Assets assets;
    private final StringBuilder playerName = new StringBuilder();
    private final Texture fireworksExplosionTexture;
    private final Texture fireworksRocketTexture;
    private float fireworkTimer = 0;
    private final Table inputTable;
    private final int musicVolume;
    private final int soundVolume;

    private final Array<Firework> fireworks;

    public EndGameScreen(MyGdxGame game, int score, Assets assets , int musicVolume , int soundVolume) {
        this.game = game;
        this.finalScore = score;
        this.assets = assets;
        this.musicVolume = musicVolume;
        this.soundVolume = soundVolume;
        this.stage = new Stage();
        this.batch = new SpriteBatch();
        this.trophyTexture = assets.getAssetManager().get(Assets.goldTrophyTexture);
        this.fireworksExplosionTexture = assets.getAssetManager().get(Assets.fireworkExplosionTexture);
        this.fireworksRocketTexture = assets.getAssetManager().get(Assets.fireworkRocketTexture);
        this.fireworks = new Array<>();


        Gdx.input.setInputProcessor(stage);

        this.skin = new Skin(Gdx.files.internal("Font/menu.json"));

        Image trophyImage = new Image(trophyTexture);
        trophyImage.setPosition(Gdx.graphics.getWidth() / 2f - trophyImage.getWidth() / 2, Gdx.graphics.getHeight() / 1.5f);
        stage.addActor(trophyImage);

        inputTable = new Table();
        inputTable.setSkin(skin);
        inputTable.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2.2f);
        inputTable.center();

        Label scoreLabel = new Label("Score: " + finalScore, skin);

        Label nameLabel = new Label("Name:", skin);

        inputLabel = new Label("_", skin);

        inputTable.add(scoreLabel).colspan(2).padBottom(20).row();
        inputTable.add(nameLabel).left().padRight(10);
        inputTable.add(inputLabel).left().row();

        stage.addActor(inputTable);
    }

    @Override
    public void render(float delta) {
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


        if (!submitted) {
            handleInput();
            cursorTimer += delta;
            if (cursorTimer >= 0.5f) {
                cursorVisible = !cursorVisible;
                cursorTimer = 0;
            }

            StringBuilder displayText = new StringBuilder(playerName.toString());
            if (playerName.length() < NAME_MAX_LENGTH) {
                if (cursorVisible) {
                    displayText.append("_");
                } else {
                    displayText.append(" ");
                }
            }

            while (displayText.length() < NAME_MAX_LENGTH  + 1) displayText.append(" ");

            inputLabel.setText(displayText.toString());


        } else {
            timeSinceSubmission += delta;
            if ( timeSinceSubmission >= AUTO_RETURN_TIME ) {
                game.setScreen(new MainMenuScreen(game,assets , musicVolume, soundVolume));
            }
        }

        stage.act();

        batch.begin();

        for (Firework f : fireworks) {
            f.render(batch);
        }

        batch.end();

        stage.draw();
    }

    private void handleInput() {

        if (submitted) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE) && playerName.length() > 0) {
            playerName.deleteCharAt(playerName.length() - 1);
        }

        for (int i = Input.Keys.A; i <= Input.Keys.Z; i++) {
            if (Gdx.input.isKeyJustPressed(i) && playerName.length() < NAME_MAX_LENGTH  ) {
                playerName.append((char) (i + CHAR_OFFSET ));
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && playerName.length() > 0) {
            MySQLHelper.insertScore(playerName.toString(), finalScore);
            updateTopScores();
            submitted = true;

            if (inputTable != null) {
                inputTable.remove();
            }
        }
    }



    private void updateTopScores() {
        List<String[]> scores = MySQLHelper.getScores();
        int maxEntries = Math.min(scores.size(), 10);
        Table scoreTable = createScoreTable(scores, maxEntries);
        stage.addActor(scoreTable);
    }
    private Table createScoreTable(List<String[]> scores, int maxEntries) {
        Table scoreTable = new Table();
        scoreTable.setPosition(Gdx.graphics.getWidth() / 2.7f, Gdx.graphics.getHeight() / 1.5f);
        scoreTable.top().left();
        scoreTable.padTop(50);
        scoreTable.setSkin(skin);

        Label nameTitle = new Label("Name", skin);
        Label timeTitle = new Label("Time", skin);
        scoreTable.add(nameTitle).left();
        scoreTable.add().width(210);
        scoreTable.add(timeTitle).left().row();

        for (int i = 0; i < maxEntries; i++) {
            String name = scores.get(i)[0];
            if (name.length() > 10) name = name.substring(0, 10);
            int timeInSeconds = Integer.parseInt(scores.get(i)[1]);

            String formattedTime = formatTime(timeInSeconds);

            Label nameLabel = new Label(name, skin);
            Label timeLabel = new Label(formattedTime, skin);

            scoreTable.add(nameLabel).left();
            scoreTable.add().width(200);
            scoreTable.add(timeLabel).left().row();
        }

        return scoreTable;
    }

    private String formatTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Override
    public void dispose() {
        batch.dispose();
        trophyTexture.dispose();
        fireworksExplosionTexture.dispose();
        stage.dispose();
        skin.dispose();
    }
}
