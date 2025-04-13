package com.mygdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.utilities_resources.Assets;

public class GuestCharacter extends BasicCharacter {

    public GuestCharacter(Vector2 initialPosition , Assets assets)  {
        super(initialPosition, assets);
    }

    public void update(  Boolean isPaused) {
        if (!isPaused) {
            float deltaTime = Gdx.graphics.getDeltaTime();
            setStateTime(getStateTime() + deltaTime);
            setTimeSinceLastLifeLost(getTimeSinceLastLifeLost() + deltaTime);
            if(getIsWalking() .equals("left") && !getIsFlipped()) {
                    flipAnimations();
                    setIsFlipped(true);
            }
            else  if(getIsWalking() .equals("right") && getIsFlipped()) {
                flipAnimations();
                setIsFlipped(false);
            }

        }
    }

}
