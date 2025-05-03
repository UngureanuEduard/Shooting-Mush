    package com.mygdx.game.game_modes;

    import com.badlogic.gdx.Gdx;
    import com.badlogic.gdx.Input;
    import com.badlogic.gdx.graphics.g2d.SpriteBatch;
    import com.badlogic.gdx.math.Vector2;
    import com.badlogic.gdx.scenes.scene2d.Stage;
    import com.badlogic.gdx.utils.Array;
    import com.esotericsoftware.kryonet.Client;
    import com.esotericsoftware.kryonet.Connection;
    import com.esotericsoftware.kryonet.Listener;
    import com.esotericsoftware.kryonet.Server;
    import com.mygdx.game.MyGdxGame;
    import com.mygdx.game.combat_system.EnemyBullet;
    import com.mygdx.game.entities.character.GuestCharacter;
    import com.mygdx.game.entities.enemy.DummyEnemy;
    import com.mygdx.game.entities.enemy.Enemy;
    import com.mygdx.game.newtork.Network;
    import com.mygdx.game.utilities_resources.Assets;

    import java.util.HashMap;
    import java.util.Map;

    public class CoopMode extends StoryMode {

        private final static float SEND_INTERVAL = 0.01f;
        private final static int BULLET_DAMAGE = 50;

        private final GuestCharacter guestCharacter;
        private boolean isHost;
        private Client client;
        private Server server;
        private float sendTimer = 0f;
        Map<Integer, DummyEnemy> dummyEnemies = new HashMap<>();

        Array<Enemy> toActuallyRemove = new Array<>();


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
            pos.timeSinceLastLifeLost = getCharacter().getTimeSinceLastLifeLost();

            if (isHost && server != null && sendTimer >= SEND_INTERVAL) {
                sendTimer = 0f;
                server.sendToAllTCP(pos);

                for (Enemy enemy : getEnemyManager().getActiveEnemies()) {
                    Network.EnemyState enemyState = new Network.EnemyState();
                    enemyState.id = enemy.getId();
                    enemyState.x = enemy.getPosition().x;
                    enemyState.y = enemy.getPosition().y;
                    enemyState.health = enemy.getHealth();
                    enemyState.isFlipped = enemy.isFlipped();
                    enemyState.isDamaged = enemy.isDamaged();
                    enemyState.isAttacked = enemy.getIsAttacked();
                    enemyState.isAlive = enemy.isAlive();
                    enemyState.stateTime = enemy.getStateTime();
                    enemyState.isIdle = enemy.getBehaviorStatus() == Enemy.BehaviorStatus.IDLE;
                    server.sendToAllTCP(enemyState);

                    if (enemy.isMarkedForRemoval()) {
                        toActuallyRemove.add(enemy);
                    }
                }

                for (EnemyBullet bullet : getEnemyBulletsManager().getActiveEnemyBullets()) {

                    if (bullet.isSent()) continue;

                    Vector2 bulletPos = bullet.getPosition();
                    Vector2 bulletVel = bullet.getVelocity();
                    float bulletDmg = bullet.getDamage();

                    Network.EnemyBulletData data = new Network.EnemyBulletData();
                    data.x = bulletPos.x;
                    data.y = bulletPos.y;
                    data.vx = bulletVel.x;
                    data.vy = bulletVel.y;
                    data.damage = bulletDmg;

                    server.sendToAllTCP(data);

                    bullet.setSent(true);
                }

            }

            for (Enemy e : toActuallyRemove) {
                getEnemyManager().getActiveEnemies().removeValue(e, true);
            }


            if (!isHost) {
                dummyEnemies.entrySet().removeIf(entry -> {
                    DummyEnemy dummy = entry.getValue();
                    dummy.update(delta);
                    dummy.render(batch);
                    return dummy.shouldBeRemoved();
                });
            }

            if (!isHost && client != null && sendTimer >= SEND_INTERVAL) {
                sendTimer = 0f;
                client.sendTCP(pos);
            }
            guestCharacter.update(getIsPaused() ,getEnemyBulletsManager().getActiveEnemyBullets() , client);
            guestCharacter.render(batch);

            Vector2 hostPos = getCharacter().getPosition();
            Vector2 guestPos = guestCharacter.getPosition();

            for (Enemy enemy : getEnemyManager().getActiveEnemies()) {
                enemy.updatePlayerPosition(hostPos, guestPos);
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
                getCharacterBulletsManager().printActiveBullets();
            }

            
        }


        public void setClientListener() {
            if (client == null) return;
            client.addListener(createNetworkListener());
        }

        private void sendBullet(Vector2 pos, Vector2 vel , boolean isFromHost) {
            Network.BulletData data = new Network.BulletData();
            data.x = pos.x;
            data.y = pos.y;
            data.vx = vel.x;
            data.vy = vel.y;
            data.damage = BULLET_DAMAGE;
            data.isFromHost = isFromHost;

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



            getCharacterBulletsManager().generateBullet(bulletStart, direction, BULLET_DAMAGE, getAssets(), getSoundVolume() ,isHost);

            sendBullet(bulletStart, direction ,isHost);

        }

        private Listener createNetworkListener() {
            return new Listener() {
                @Override
                public void received(Connection connection, Object object) {
                    if (object instanceof Network.PlayerPosition) {
                        Network.PlayerPosition pos = (Network.PlayerPosition) object;
                        guestCharacter.setPosition(new Vector2(pos.x, pos.y));
                        guestCharacter.setIsWalking(pos.isWalking);
                        guestCharacter.setTimeSinceLastLifeLost(pos.timeSinceLastLifeLost);
                    } else if (object instanceof Network.BulletData) {
                        Network.BulletData data = (Network.BulletData) object;
                        getCharacterBulletsManager().generateBullet(
                                new Vector2(data.x, data.y),
                                new Vector2(data.vx, data.vy),
                                data.damage,
                                getAssets(),
                                getSoundVolume(),
                                data.isFromHost
                        );
                    } else if (object instanceof Network.EnemyState) {
                        Network.EnemyState state = (Network.EnemyState) object;
                        DummyEnemy dummy = dummyEnemies.get(state.id);

                        if (dummy == null) {
                            dummy = new DummyEnemy(new Vector2(state.x, state.y), state.health, state.isFlipped, getAssets());
                            dummy.setId(state.id);
                            dummyEnemies.put(state.id, dummy);
                        } else {
                            dummy.updateFromNetwork(new Vector2(state.x, state.y), state.health, state.isFlipped);
                        }
                        dummy.setIsDamaged(state.isDamaged);
                        dummy.setIsAttacked(state.isAttacked);
                        dummy.setAlive(state.isAlive);
                        dummy.setStateTime(state.stateTime);
                        dummy.setIdle(state.isIdle);
                    }
                    else if (object instanceof Network.EnemyBulletData) {
                        Network.EnemyBulletData data = (Network.EnemyBulletData) object;
                        getEnemyBulletsManager().generateBullet(
                                new Vector2(data.x, data.y),
                                new Vector2(data.vx, data.vy),
                                data.damage,
                                getAssets(),
                                getSoundVolume()
                        );
                    }
                    else if (object instanceof Network.BulletDeactivation) {
                        Network.BulletDeactivation data = (Network.BulletDeactivation) object;
                        for (EnemyBullet bullet : getEnemyBulletsManager().getActiveEnemyBullets()) {
                            if (Math.abs(bullet.getPosition().x - data.x) < 2f &&
                                    Math.abs(bullet.getPosition().y - data.y) < 2f) {
                                bullet.setAlive(false);
                                break;
                            }
                        }
                    }

                }
            };
        }

        @Override
        public void spawnStoryEnemy() {
            if(isHost)
                super.spawnStoryEnemy();
        }

        @Override
        public void dispose() {
            super.dispose();
            if (guestCharacter != null) guestCharacter.dispose();
        }





    }
