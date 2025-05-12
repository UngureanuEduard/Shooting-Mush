package com.mygdx.game.cutscene;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entities.character.Sprite;

public class RemoveCharacterEvent implements CutsceneEvent {
    private final Sprite character;
    private boolean complete = false;

    public RemoveCharacterEvent(Sprite character) {
        this.character = character;
    }

    @Override
    public boolean update(float delta) {
        if (character != null) {
            character.setPosition(new Vector2(-99,-99));
        }
        complete = true;
        return true;
    }

    @Override
    public void skip() {
        update(0);
    }

    @Override
    public boolean isComplete() {
        return complete;
    }
}
