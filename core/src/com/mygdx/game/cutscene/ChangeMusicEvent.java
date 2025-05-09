package com.mygdx.game.cutscene;

import com.badlogic.gdx.audio.Music;

public class ChangeMusicEvent implements CutsceneEvent {
    private final Music cutsceneMusic;
    private final Music trainMusic;
    private final float volume;
    private boolean complete = false;

    public ChangeMusicEvent(Music cutsceneMusic, Music trainMusic, float volume) {
        this.cutsceneMusic = cutsceneMusic;
        this.trainMusic = trainMusic;
        this.volume = volume;
    }

    @Override
    public boolean update(float delta) {
        if (!complete) {
            if (cutsceneMusic != null && cutsceneMusic.isPlaying()) {
                cutsceneMusic.stop();
            }

            if (trainMusic != null) {
                trainMusic.setLooping(true);
                trainMusic.setVolume(volume);
                trainMusic.play();
            }

            complete = true;
        }
        return complete;
    }

    @Override
    public boolean isComplete() {
        return complete;
    }
}
