package com.mygdx.game.newtork;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;

public class Network {
    public static final int PORT = 54555;

    public static class Hello {
    }

    public static void register(Server server) {
        server.getKryo().register(Hello.class);
        server.getKryo().register(PlayerPosition.class);
        server.getKryo().register(BulletData.class);
    }

    public static void register(Client client) {
        client.getKryo().register(Hello.class);
        client.getKryo().register(PlayerPosition.class);
        client.getKryo().register(BulletData.class);
    }


    public static class PlayerPosition {
        public float x;
        public float y;
        public String isWalking;
    }

    public static class BulletData {
        public float x;
        public float y;
        public float vx;
        public float vy;
        public float damage;
    }


}


