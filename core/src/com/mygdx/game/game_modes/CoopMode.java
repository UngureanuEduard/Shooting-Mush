package com.mygdx.game.game_modes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.entities.Character;
import com.mygdx.game.newtork.Network;
import com.mygdx.game.utilities_resources.Assets;

public class CoopMode extends StoryMode {

    private final static float SEND_INTERVAL = 0.01f;

    private Character guestCharacter;
    private boolean isHost;
    private Client client;
    private Server server;
    private float sendTimer = 0f;



    public CoopMode(Assets assets, Integer soundVolume, Integer musicVolume) {
        super(assets, soundVolume, musicVolume);
    }

    public void setNetworkInfo(boolean isHost, Client client, Server server) {
        this.isHost = isHost;
        this.client = client;
        this.server = server;

        if (isHost && server != null) {
            server.addListener(new Listener() {
                @Override
                public void received(Connection connection, Object object) {
                    if (object instanceof Network.PlayerPosition) {
                        Network.PlayerPosition pos = (Network.PlayerPosition) object;
                        guestCharacter.setPosition(new Vector2(pos.x, pos.y));
                    }
                }
            });
        }

    }

    @Override
    public void show() {
        super.show();
        guestCharacter = new Character(new Vector2(200, 100), getAssets());
    }

    @Override
    public void Render(float delta, SpriteBatch batch, MyGdxGame game, Stage stage) {
        super.Render(delta, batch, game, stage);

        sendTimer += delta;

        Network.PlayerPosition pos = new Network.PlayerPosition();

        if (isHost && server != null && sendTimer >= SEND_INTERVAL) {
            sendTimer = 0f;
            pos.x = getCharacter().getPosition().x;
            pos.y = getCharacter().getPosition().y;
            server.sendToAllTCP(pos);
        }

        if (!isHost && client != null && sendTimer >= SEND_INTERVAL) {
            sendTimer = 0f;
            pos.x = getCharacter().getPosition().x;
            pos.y = getCharacter().getPosition().y;
            client.sendTCP(pos);
        }

        guestCharacter.render(batch);
    }

    public void setClientListener() {
        if (client == null) return;
        client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof Network.PlayerPosition) {
                    Network.PlayerPosition pos = (Network.PlayerPosition) object;
                    guestCharacter.setPosition(new Vector2(pos.x, pos.y));
                    System.out.println("Guest received host position: " + pos.x + ", " + pos.y);
                }
            }
        });
    }


    @Override
    public void dispose() {
        super.dispose();
        if (guestCharacter != null) guestCharacter.dispose();
    }

}
