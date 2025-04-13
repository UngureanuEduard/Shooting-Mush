package com.mygdx.game.game_modes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.entities.GuestCharacter;
import com.mygdx.game.newtork.Network;
import com.mygdx.game.utilities_resources.Assets;

public class CoopMode extends StoryMode {

    private final static float SEND_INTERVAL = 0.01f;
    private final static int BULLET_DAMAGE = 50;

    private final GuestCharacter guestCharacter;
    private boolean isHost;
    private Client client;
    private Server server;
    private float sendTimer = 0f;



    public CoopMode(Assets assets, Integer soundVolume, Integer musicVolume) {
        super(assets, soundVolume, musicVolume);
        guestCharacter = new GuestCharacter(new Vector2(200, 100), getAssets());
    }


    public void setNetworkInfo(boolean isHost, Client client, Server server) {
        this.isHost = isHost;
        this.client = client;
        this.server = server;

        if (isHost && server != null) {
            server.addListener(createNetworkListener());
        }
    }

    @Override
    public void show(int cameraWidth , int cameraHeight) {
        super.show(cameraWidth,cameraHeight);
    }

    @Override
    public void Render(float delta, SpriteBatch batch, MyGdxGame game, Stage stage) {
        super.Render(delta, batch, game, stage);

        sendTimer += delta;

        Network.PlayerPosition pos = new Network.PlayerPosition();
        pos.x = getCharacter().getPosition().x;
        pos.y = getCharacter().getPosition().y;
        pos.isWalking = getCharacter().getIsWalking();

        if (isHost && server != null && sendTimer >= SEND_INTERVAL) {
            sendTimer = 0f;
            server.sendToAllTCP(pos);
        }

        if (!isHost && client != null && sendTimer >= SEND_INTERVAL) {
            sendTimer = 0f;
            client.sendTCP(pos);
        }
        guestCharacter.update(getIsPaused());
        guestCharacter.render(batch);

    }

    public void setClientListener() {
        if (client == null) return;
        client.addListener(createNetworkListener());
    }

    private void sendBullet(Vector2 pos, Vector2 vel) {
        Network.BulletData data = new Network.BulletData();
        data.x = pos.x;
        data.y = pos.y;
        data.vx = vel.x;
        data.vy = vel.y;
        data.damage = BULLET_DAMAGE;

        if (isHost && server != null) {
            server.sendToAllTCP(data);
        } else if (client != null) {
            client.sendTCP(data);
        }
    }

    @Override
    protected void shootBullet() {
        Vector2 bulletStart = new Vector2(getCharacter().getPosition());
        Vector2 direction = calculateDirectionToCursor(bulletStart).nor().scl(BULLET_SPEED);
        getCharacterBulletsManager().generateBullet(bulletStart, direction, BULLET_DAMAGE, getAssets(), getSoundVolume());

        sendBullet(bulletStart, direction);

        if (isHost && server != null) {
            Network.BulletData data = new Network.BulletData();
            data.x = bulletStart.x;
            data.y = bulletStart.y;
            data.vx = direction.x;
            data.vy = direction.y;
            data.damage = BULLET_DAMAGE;
            server.sendToAllTCP(data);
        }
    }

    private Listener createNetworkListener() {
        return new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof Network.PlayerPosition) {
                    Network.PlayerPosition pos = (Network.PlayerPosition) object;
                    guestCharacter.setPosition(new Vector2(pos.x, pos.y));
                    guestCharacter.setIsWalking(pos.isWalking);
                } else if (object instanceof Network.BulletData) {
                    Network.BulletData data = (Network.BulletData) object;
                    getCharacterBulletsManager().generateBullet(
                            new Vector2(data.x, data.y),
                            new Vector2(data.vx, data.vy),
                            data.damage,
                            getAssets(),
                            getSoundVolume()
                    );
                }
            }
        };
    }

    @Override
    public void dispose() {
        super.dispose();
        if (guestCharacter != null) guestCharacter.dispose();
    }

}
