package com.mygdx.game.entities.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Client;
import com.mygdx.game.combat_system.EnemyBullet;
import com.mygdx.game.newtork.Network;
import com.mygdx.game.utilities_resources.Assets;

public class GuestCharacter extends BasicCharacter {

    private Client client;

    public GuestCharacter(Vector2 initialPosition , Assets assets)  {
        super(initialPosition, assets);
    }

    public void update(  Boolean isPaused , Array<EnemyBullet> enemyBullets , Client client) {
        this.client = client;
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

            checkBulletCollisions(enemyBullets);
        }

    }

    @Override
    protected void colidedWithBullet(EnemyBullet enemyBullet){
        enemyBullet.setAlive(false);
        if (client != null) {
            Network.BulletDeactivation deactivation = new Network.BulletDeactivation();
            deactivation.x = enemyBullet.getPosition().x;
            deactivation.y = enemyBullet.getPosition().y;
            client.sendTCP(deactivation);
        }
    }



}
