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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.animations_effects.firework.Firework;
import com.mygdx.game.utilities_resources.Assets;
import com.mygdx.game.utilities_resources.MySQLHelper;

import java.util.List;

public class EndGameScreen extends ScreenAdapter {
    private final MyGdxGame game;
    private final Stage stage;
    private final Texture trophyTexture;
    private final SpriteBatch batch;
    private final int finalScore;
    private final Skin skin;
    private final Label topScoresLabel;
    private final Label inputLabel;
    private final Label scoreLabel;
    private final Label nameLabel;
    private boolean cursorVisible = true;
    private boolean submitted = false;
    private float cursorTimer = 0;
    private float timeSinceSubmission = 0;
    private final Assets assets;
    private final StringBuilder playerName = new StringBuilder();
    private final Texture fireworksExplosionTexture;
    private final Texture fireworksRocketTexture;
    private float fireworkTimer = 0;

    private final Array<Firework> fireworks;

    public EndGameScreen(MyGdxGame game, int score, Assets assets) {
        this.game = game;
        this.finalScore = score;
        this.stage = new Stage();
        this.batch = new SpriteBatch();
        this.trophyTexture = assets.getAssetManager().get(Assets.goldTrophyTexture);
        this.assets = assets;
        this.fireworksExplosionTexture = assets.getAssetManager().get(Assets.fireworkExplosionTexture);
        this.fireworksRocketTexture = assets.getAssetManager().get(Assets.fireworkRocketTexture);
        this.fireworks = new Array<>();


        Gdx.input.setInputProcessor(stage);

        this.skin = new Skin(Gdx.files.internal("Font/menu.json"));

        Image trophyImage = new Image(trophyTexture);
        trophyImage.setPosition(Gdx.graphics.getWidth() / 2f - trophyImage.getWidth() / 2, Gdx.graphics.getHeight() / 1.5f);
        stage.addActor(trophyImage);

        scoreLabel = new Label("Score: " + finalScore, skin);
        scoreLabel.setFontScale(1.5f);
        scoreLabel.setPosition(
                Gdx.graphics.getWidth() / 2f - scoreLabel.getPrefWidth() / 2,
                Gdx.graphics.getHeight() / 2f
        );
        stage.addActor(scoreLabel);

        nameLabel = new Label("Name:", skin);
        nameLabel.setFontScale(1.5f);
        nameLabel.setAlignment(Align.left);
        nameLabel.setPosition(Gdx.graphics.getWidth() / 2f - 140, (float) Gdx.graphics.getHeight() / 3);
        stage.addActor(nameLabel);

        inputLabel = new Label("_", skin);
        inputLabel.setFontScale(1.5f);
        inputLabel.setAlignment(Align.left);
        inputLabel.setPosition(
                Gdx.graphics.getWidth() / 2f - inputLabel.getPrefWidth() / 2 + 110,
                nameLabel.getY()
        );
        stage.addActor(inputLabel);

        topScoresLabel = new Label("", skin);
        topScoresLabel.setFontScale(1.2f);
        topScoresLabel.setAlignment(Align.center);
        topScoresLabel.setPosition(
                Gdx.graphics.getWidth() / 2f - topScoresLabel.getPrefWidth() / 2,
                (float) ((float) Gdx.graphics.getHeight() / 2.5)
        );

        stage.addActor(topScoresLabel);
    }

    @Override
    public void render(float delta) {
        fireworkTimer += delta;
        if (fireworkTimer >= 0.4f) {
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

            String displayText = playerName + (cursorVisible ? "_" : " ");
            inputLabel.setText(displayText);

        } else {
            timeSinceSubmission += delta;
            if (timeSinceSubmission >= 5) {
                game.setScreen(new MainMenuScreen(game,assets));
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE) && playerName.length() > 0) {
            playerName.deleteCharAt(playerName.length() - 1);
        }

        for (int i = Input.Keys.A; i <= Input.Keys.Z; i++) {
            if (Gdx.input.isKeyJustPressed(i)) {
                playerName.append((char) (i + 36));
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && playerName.length() > 0) {
            MySQLHelper.insertScore(playerName.toString(), finalScore);
            updateTopScores();
            submitted = true;

            scoreLabel.remove();
            nameLabel.remove();
            inputLabel.remove();
        }
    }


    private void updateTopScores() {
        List<String[]> scores = MySQLHelper.getScores();
        StringBuilder scoreText = new StringBuilder("Top 10 Scores:\n");

        int maxEntries = Math.min(scores.size(), 10);
        for (int i = 0; i < maxEntries; i++) {
            scoreText.append(i + 1).append(". ").append(scores.get(i)[0]).append(": ").append(scores.get(i)[1]).append("\n");
        }

        topScoresLabel.setText(scoreText.toString());
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
