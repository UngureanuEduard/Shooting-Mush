package com.mygdx.game.newtork;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;

public class Network {
    public static final int PORT = 54555;

    public static class Hello {
    }

    public static void register(Server server) {
        registerCommon(server.getKryo());
    }

    public static void register(Client client) {
        registerCommon(client.getKryo());
    }

    private static void registerCommon(com.esotericsoftware.kryo.Kryo kryo) {
        kryo.register(Hello.class);
        kryo.register(PlayerPosition.class);
        kryo.register(BulletData.class);
        kryo.register(EnemyState.class);
        kryo.register(EnemyBulletData.class);
        kryo.register(BulletDeactivation.class);
    }

    public static class PlayerPosition {
        public float x;
        public float y;
        public String isWalking;
        public float timeSinceLastLifeLost;
    }

    public static class BulletData {
        public float x;
        public float y;
        public float vx;
        public float vy;
        public float damage;
        public boolean isFromHost;
    }

    public static class EnemyState {
        public int id;
        public float x;
        public float y;
        public float health;
        public boolean isFlipped;
        public boolean isDamaged;
        public boolean isAttacked;
        public boolean isAlive;
        public float stateTime;
        public boolean isIdle;
    }

    public static class EnemyBulletData {
        public float x;
        public float y;
        public float vx;
        public float vy;
        public float damage;
    }

    public static class BulletDeactivation {
        public float x;
        public float y;
    }


}


