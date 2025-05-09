package com.mygdx.game.cutscene;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.entities.character.Sprite;

public class MoveCharacterEvent implements CutsceneEvent {
    private final Sprite character;
    private final Vector2 targetPosition;
    private final float speed;
    private boolean complete = false;

    public MoveCharacterEvent(Sprite character, Vector2 targetPosition, float speed) {
        this.character = character;
        this.targetPosition = targetPosition;
        this.speed = speed;
    }

    @Override
    public boolean update(float delta) {
        if (complete) return true;

        character.setStateTime(character.getStateTime() + delta);
        Vector2 current = character.getPosition();
        Vector2 direction = new Vector2(targetPosition).sub(current);
        float distance = direction.len();

        if (distance < speed * delta) {
            character.setPosition(targetPosition);
            character.setIsWalking("");
            complete = true;
        } else {
            direction.nor().scl(speed * delta);
            character.setPosition(current.add(direction));
            if (Math.abs(direction.x) > Math.abs(direction.y)) {
                character.setIsWalking(direction.x > 0 ? "right" : "left");
            }
        }

        return complete;
    }


    @Override
    public boolean isComplete() {
        return complete;
    }
}
