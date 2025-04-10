package com.mygdx.game.ui_screens;


import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.utilities_resources.Assets;
import com.mygdx.game.utilities_resources.MySQLHelper;

public class EndGameScreenArena extends EndGameScreen {

    private final int wavesCompleted;

    public EndGameScreenArena(MyGdxGame game, int score, Assets assets, int musicVolume,
                              int soundVolume, int wavesCompleted ) {
        super(game, score, assets, musicVolume, soundVolume );
        this.wavesCompleted = wavesCompleted;
    }

    @Override
    protected void insertScore() {
        MySQLHelper.insertArenaScore(playerName.toString(), finalScore, wavesCompleted);
    }

    @Override
    protected void updateTopScores() {
        java.util.List<String[]> scores = MySQLHelper.getArenaScores();
        int maxEntries = Math.min(scores.size(), 10);
        Table scoreTable = createScoreTable(scores, maxEntries);
        stage.addActor(scoreTable);
    }

    @Override
    protected Table createScoreTable(java.util.List<String[]> scores, int maxEntries) {
        Table scoreTable = new Table();
        scoreTable.setPosition(worldWidth / 2f - scoreTable.getWidth()/2f, worldHeight / 1.5f);
        scoreTable.top().left().padTop(50).setSkin(skin);

        scoreTable.add(new Label("Name", skin)).left();
        scoreTable.add(new Label("Score", skin)).padLeft(30).left();
        scoreTable.add(new Label("Waves", skin)).padLeft(30).left().row();

        for (int i = 0; i < maxEntries; i++) {
            String name = scores.get(i)[0];
            String score = scores.get(i)[1];
            String waves = scores.get(i)[2];

            if (name.length() > 10) name = name.substring(0, 10);

            scoreTable.add(new Label(name, skin)).left();
            scoreTable.add(new Label(score, skin)).padLeft(30).left();
            scoreTable.add(new Label(waves, skin)).padLeft(30).left().row();
        }

        return scoreTable;
    }
}

