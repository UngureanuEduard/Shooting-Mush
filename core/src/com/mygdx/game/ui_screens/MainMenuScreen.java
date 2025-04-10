package com.mygdx.game.ui_screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.game.*;
import com.mygdx.game.newtork.Network;
import com.mygdx.game.utilities_resources.Assets;
import com.mygdx.game.utilities_resources.Settings;

import static com.mygdx.game.utilities_resources.Settings.*;


public class    MainMenuScreen extends ScreenAdapter {

    private static final float BACKGROUND_PADDING = 200f;

    private static final float BUTTON_WIDTH_PERCENT = 0.3f;
    private static final float BUTTON_HEIGHT_PERCENT = 0.1f;
    private static final float BUTTON_PADDING_BOTTOM = 50f;

    private static final int DUCK_FRAME_WIDTH = 32;
    private static final int DUCK_FRAME_HEIGHT = 32;
    private static final int DUCK_FRAME_COUNT = 6;
    private static final float DUCK_ANIMATION_FRAME_DURATION = 0.1f;
    private static final float DUCK_IMAGE_WIDTH = 80f;
    private static final float DUCK_IMAGE_HEIGHT = 80f;
    private static final float DUCK_IMAGE_Y_DIVISOR = 6f;

    private static final float MOVE_SPEED_DEFAULT = 50f;

    private static final float CLEAR_COLOR_R = 0.1f;
    private static final float CLEAR_COLOR_G = 0.1f;
    private static final float CLEAR_COLOR_B = 0.15f;
    private static final float CLEAR_COLOR_A = 1f;

    private Stage stage;
    private Skin skin;
    private Table mainTable;
    private final MyGdxGame game;

    private Image movingImage;
    private Animation<TextureRegion> movingAnimation;
    private float stateTime = 0;

    private final SpriteBatch batch;
    private float screenWidth;
    private boolean isFlipped = false;
    private boolean moveRight = true;
    private Music backgroundMusic;
    private OptionsTable optionsTable;
    private Image backgroundImage;
    private int musicVolume;
    private int soundVolume;
    private final Assets assets;
    float worldWidth;
    float worldHeight;
    private float duckScaleX;
    private float duckScaleY;

    public MainMenuScreen(MyGdxGame game, Assets assets , int musicVolume, int soundVolume) {
        this.game = game;
        this.assets = assets;
        this.musicVolume = musicVolume;
        this.soundVolume = soundVolume;
        batch = new SpriteBatch();
    }

    @Override
    public void show() {
        Viewport viewport = new ExtendViewport(Settings.fullScreenWidth, Settings.fullScreenHeight);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        worldWidth = stage.getViewport().getWorldWidth();
        worldHeight = stage.getViewport().getWorldHeight();

        if (windowed) {
            Gdx.graphics.setWindowedMode(windowedScreenWidth, windowedScreenHeight);
        } else {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }


        skin = assets.getAssetManager().get(Assets.skin);
        mainTable = new Table();
        mainTable.setFillParent(true);

        Texture backgroundTexture = assets.getAssetManager().get(Assets.menuBackgroundTexture);
        backgroundImage = new Image(backgroundTexture);
        backgroundImage.setSize(worldWidth + BACKGROUND_PADDING, worldHeight + BACKGROUND_PADDING);

        stage.addActor(backgroundImage);
        stage.addActor(mainTable);

        optionsTable = new OptionsTable(assets, () -> {
            updateVolumes();
            stage.clear();
            stage.addActor(backgroundImage);
            stage.addActor(mainTable);
            backgroundMusic.setVolume(musicVolume/100f);
        }, musicVolume, soundVolume);
        optionsTable.setFillParent(true);
        optionsTable.center();


        Table playRow = new Table();

        TextButton playButton = new TextButton("Play", skin);
        playRow.add(playButton)
                .width(Math.round(worldWidth * BUTTON_WIDTH_PERCENT/1.5))
                .height(Math.round(worldHeight * BUTTON_HEIGHT_PERCENT))
                .padBottom(BUTTON_PADDING_BOTTOM)
                .left();

        playRow.add().width(20f);

        Texture coopButtonTexture = assets.getAssetManager().get(Assets.co_opButtonTexture);
        TextButton.TextButtonStyle baseStyle = skin.get(TextButton.TextButtonStyle.class);

        Image coopImage = new Image(coopButtonTexture);
        Table coopButtonContent = new Table();
        coopButtonContent.add(coopImage).size(48);

        TextButton coopButton = new TextButton("", baseStyle);
        coopButton.clearChildren();
        coopButtonContent.center();
        coopButton.add(coopButtonContent).expand().center();

        playRow.add(coopButton)
                .width(Math.round(worldWidth * BUTTON_WIDTH_PERCENT * 0.25f))
                .height(Math.round(worldHeight * BUTTON_HEIGHT_PERCENT))
                .padBottom(BUTTON_PADDING_BOTTOM)
                .right();

        mainTable.add(playRow);
        mainTable.row();

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new VideoScreen(game, musicVolume, soundVolume, assets , worldWidth , worldHeight));
            }
        });

        coopButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainTable.clear();

                Texture backTexture = assets.getAssetManager().get(Assets.backButtonTexture);
                ImageButton backButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(backTexture)));

                TextButton joinButton = new TextButton("Join", skin);
                TextButton hostButton = new TextButton("Host", skin);

                backButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        stage.clear();
                        stage.addActor(backgroundImage);
                        stage.addActor(mainTable);
                        mainTable.clear();
                        show();
                    }
                });

                joinButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        mainTable.clear();

                        Texture backTexture = assets.getAssetManager().get(Assets.backButtonTexture);
                        ImageButton backButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(backTexture)));
                        backButton.addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                stage.clear();
                                stage.addActor(backgroundImage);
                                stage.addActor(mainTable);
                                show();
                            }
                        });

                        TextField ipInputField = new TextField("", skin);
                        ipInputField.setMessageText("  Enter IP address");

                        TextButton connectButton = new TextButton("Connect", skin);
                        connectButton.addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                String ipAddress = ipInputField.getText();
                                Client client = new Client();
                                Network.register(client);

                                client.addListener(new Listener() {
                                    public void connected(Connection connection) {
                                        System.out.println("Connected to server at IP: " + ipAddress);
                                        Gdx.app.postRunnable(() -> {
                                            GameScene gameScene = new GameScene(game, musicVolume, soundVolume, GameScene.GameMode.CO_OP, assets);

                                            gameScene.getCoopMode().setNetworkInfo(false, client, null);
                                            gameScene.getCoopMode().setClientListener();
                                            System.out.println("Client listener set!");
                                            game.setScreen(gameScene);


                                        });
                                    }

                                    public void disconnected(com.esotericsoftware.kryonet.Connection connection) {
                                        System.out.println("Disconnected from server");
                                    }
                                });

                                client.start();
                                new Thread(() -> {
                                    try {
                                        client.connect(5000, ipAddress, Network.PORT);
                                    } catch (Exception e) {
                                        ipInputField.setText(" Connection failed");
                                        System.err.println("Connection failed: " + e.getMessage());
                                    }
                                }).start();
                            }
                        });


                        Table inputTable = new Table();
                        inputTable.add(ipInputField)
                                .width(Math.round(worldWidth * BUTTON_WIDTH_PERCENT))
                                .height(Math.round(worldHeight * BUTTON_HEIGHT_PERCENT / 1.5))
                                .padBottom(20f);
                        inputTable.row();
                        inputTable.add(connectButton)
                                .width(Math.round(worldWidth * BUTTON_WIDTH_PERCENT * 0.6f))
                                .height(Math.round(worldHeight * BUTTON_HEIGHT_PERCENT));

                        Table coopRow = new Table();
                        coopRow.add(backButton).width(100).height(100).padRight(50).left();
                        coopRow.add(inputTable).center();

                        mainTable.add(coopRow).padBottom(BUTTON_PADDING_BOTTOM);
                        mainTable.row();
                    }
                });


                hostButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Server server = new Server();
                        Network.register(server);
                        server.start();
                        try {
                            server.bind(Network.PORT);
                            String ip = java.net.InetAddress.getLocalHost().getHostAddress();
                            System.out.println("Hosting server on IP: " + ip);
                        } catch (Exception e) {
                            //noinspection CallToPrintStackTrace
                            e.printStackTrace();
                        }

                        GameScene gameScene = new GameScene(game, musicVolume, soundVolume, GameScene.GameMode.CO_OP, assets);
                        gameScene.getCoopMode().setNetworkInfo(true, null, server); // isHost=true
                        game.setScreen(gameScene);


                    }
                });

                Table joinHostColumn = new Table();
                joinHostColumn.add(joinButton)
                        .width(Math.round(worldWidth * BUTTON_WIDTH_PERCENT))
                        .height(Math.round(worldHeight * BUTTON_HEIGHT_PERCENT))
                        .padBottom(30f);
                joinHostColumn.row();
                joinHostColumn.add(hostButton)
                        .width(Math.round(worldWidth * BUTTON_WIDTH_PERCENT))
                        .height(Math.round(worldHeight * BUTTON_HEIGHT_PERCENT));

                Table coopRow = new Table();
                coopRow.add(backButton).width(100).height(100).padRight(50).left();
                coopRow.add(joinHostColumn);

                mainTable.add(coopRow).padBottom(BUTTON_PADDING_BOTTOM);
                mainTable.row();
            }
        });

        addButton("Arena").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScene(game, musicVolume, soundVolume, GameScene.GameMode.ARENA, assets));
            }
        });

        addButton("Options").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.clear();
                stage.addActor(backgroundImage);
                stage.addActor(optionsTable);
            }
        });

        addButton("Quit").addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        screenWidth = Gdx.graphics.getWidth();

        Texture movingImageTexture = assets.getAssetManager().get(Assets.duckTexture);
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < DUCK_FRAME_COUNT; i++) {
            TextureRegion frame = new TextureRegion(movingImageTexture, i * DUCK_FRAME_WIDTH, 0, DUCK_FRAME_WIDTH, DUCK_FRAME_HEIGHT);
            frames.add(frame);
        }

        movingAnimation = new Animation<>(DUCK_ANIMATION_FRAME_DURATION, frames, Animation.PlayMode.LOOP);
        movingImage = new Image(movingAnimation.getKeyFrame(0));

        if(windowed){
            duckScaleX = windowedScreenWidth / 1920f;
            duckScaleY = windowedScreenHeight / 1080f;
        }
        else {
            duckScaleX = fullScreenWidth / 1920f;
            duckScaleY = fullScreenHeight / 1080f;
        }

        movingImage.setPosition(0, (Gdx.graphics.getHeight() / DUCK_IMAGE_Y_DIVISOR)*duckScaleY);
        movingImage.setSize(DUCK_IMAGE_WIDTH *duckScaleX , DUCK_IMAGE_HEIGHT * duckScaleY);

        backgroundMusic = assets.getAssetManager().get(Assets.menuMusic);
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(musicVolume);
        backgroundMusic.play();
    }

    private Button addButton(String name) {
        TextButton button = new TextButton(name, skin);
        mainTable.add(button)
                .width(Math.round(worldWidth * BUTTON_WIDTH_PERCENT))
                .height(Math.round(worldHeight * BUTTON_HEIGHT_PERCENT))
                .padBottom(BUTTON_PADDING_BOTTOM);
        mainTable.row();
        return button;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(CLEAR_COLOR_R, CLEAR_COLOR_G, CLEAR_COLOR_B, CLEAR_COLOR_A);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stateTime += delta;
        TextureRegion currentFrame = movingAnimation.getKeyFrame(stateTime);

        if (moveRight) {
            movingImage.setX(movingImage.getX() + MOVE_SPEED_DEFAULT * delta);
            if (movingImage.getX() + movingImage.getWidth() > screenWidth) {
                moveRight = false;
                isFlipped = true;
            }
        } else {
            movingImage.setX(movingImage.getX() - MOVE_SPEED_DEFAULT * delta);
            if (movingImage.getX() < 0) {
                moveRight = true;
                isFlipped = false;
            }
        }

        stage.act(delta);
        stage.draw();

        batch.begin();
        batch.draw(currentFrame, movingImage.getX(), movingImage.getY(), DUCK_IMAGE_WIDTH, DUCK_IMAGE_HEIGHT,
                isFlipped ? -DUCK_IMAGE_WIDTH : DUCK_IMAGE_WIDTH, DUCK_IMAGE_HEIGHT, duckScaleX, duckScaleY, 0);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        backgroundMusic.dispose();
        stage.dispose();
    }

    public interface MainMenuCallback {
        void backToMainMenu();
    }

    private void updateVolumes() {
        musicVolume = optionsTable.getMusicVolume();
        soundVolume = optionsTable.getSoundVolume();
    }


}
