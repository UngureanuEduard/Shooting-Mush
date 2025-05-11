package com.mygdx.game.cutscene;

import com.badlogic.gdx.audio.Music;

public class ChangeMusicEvent implements CutsceneEvent {
    private final Music currentMusic;
    private final Music newMusic;
    private final float volume;
    private boolean complete = false;

    public ChangeMusicEvent(Music currentMusic, Music newMusic, float volume) {
        this.currentMusic = currentMusic;
        this.newMusic = newMusic;
        this.volume = volume;
    }

    @Override
    public boolean update(float delta) {
        if (!complete) {
            if (currentMusic != null && currentMusic.isPlaying()) {
                currentMusic.stop();
            }

            if (newMusic != null) {
                newMusic.setLooping(true);
                newMusic.setVolume(volume);
                newMusic.play();
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
