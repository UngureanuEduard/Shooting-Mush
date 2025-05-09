package com.mygdx.game.cutscene;

import com.mygdx.game.entities.character.Sprite;

public class FlipCharacterEvent implements CutsceneEvent {

    private final Sprite character;
    private boolean complete = false;

    public FlipCharacterEvent(Sprite character) {
        this.character = character;
    }

    @Override
    public boolean update(float delta) {
        if (!complete) {
            character.setFlipped(!character.isFlipped());
            complete = true;
        }
        return true;
    }

    @Override
    public boolean isComplete() {
        return complete;
    }
}
