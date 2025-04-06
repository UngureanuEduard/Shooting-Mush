package com.mygdx.game.ui_screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.utilities_resources.Assets;
import com.mygdx.game.utilities_resources.MySQLHelper;

public class EndGameScreenStory extends EndGameScreen {

    public EndGameScreenStory(MyGdxGame game, int score, Assets assets, int musicVolume, int soundVolume ) {
        super(game, score, assets, musicVolume, soundVolume );

    }

    @Override
    protected void insertScore() {
        MySQLHelper.insertScore(playerName.toString(), finalScore);
    }

    @Override
    protected void updateTopScores() {
        java.util.List<String[]> scores = MySQLHelper.getScores();
        int maxEntries = Math.min(scores.size(), 10);
        Table scoreTable = createScoreTable(scores, maxEntries);
        stage.addActor(scoreTable);
    }

    @Override
    protected Table createScoreTable(java.util.List<String[]> scores, int maxEntries) {
        Table scoreTable = new Table();
        scoreTable.setPosition(Gdx.graphics.getWidth() / 2.7f, Gdx.graphics.getHeight() / 1.5f);
        scoreTable.top().left().padTop(50).setSkin(skin);

        scoreTable.add(new Label("Name", skin)).left();
        scoreTable.add().width(210);
        scoreTable.add(new Label("Score", skin)).left().row();

        for (int i = 0; i < maxEntries; i++) {
            String name = scores.get(i)[0];
            String score = scores.get(i)[1];

            if (name.length() > 10) name = name.substring(0, 10);

            scoreTable.add(new Label(name, skin)).left();
            scoreTable.add().width(200);
            scoreTable.add(new Label(score, skin)).left().row();
        }

        return scoreTable;
    }
}

