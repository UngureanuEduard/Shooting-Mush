package com.mygdx.game.ui_screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.utilities_resources.Assets;

public class LoadingScreen extends AbstractScreen {
    private static final int DUCK_FRAME_WIDTH = 128;
    private static final int DUCK_FRAME_HEIGHT = 128;
    private static final int DUCK_FRAME_COUNT = 4;
    private static final float DUCK_ANIMATION_FRAME_DURATION = 0.1f;
    private static final float DUCK_DRAW_WIDTH = 200f;
    private static final float DUCK_DRAW_HEIGHT = 200f;
    private static final float DUCK_X_OFFSET = 100f;
    private static final float APPLE_SIZE = 80f;
    private static final float APPLE_SPACING = 10f;
    private static final float APPLE_Y_OFFSET = 120f;
    private static final int TOTAL_APPLES = 6;
    private static final int DEFAULT_VOLUME = 100;

    private final MyGdxGame game;
    private Assets assets;
    private final Texture duckTexture;
    private final Texture appleTexture;
    protected Animation<TextureRegion> duckAnimation;
    private final SpriteBatch batch;
    private float stateTime;

    public LoadingScreen(MyGdxGame game) {
        super(game);
        this.game = game;

        duckTexture = new Texture(Gdx.files.internal("loadingDuck.png"));
        appleTexture = new Texture(Gdx.files.internal("Environment/apple.png"));

        TextureRegion[][] tmp = TextureRegion.split(duckTexture, DUCK_FRAME_WIDTH, DUCK_FRAME_HEIGHT);
        TextureRegion[] characterFrames = new TextureRegion[DUCK_FRAME_COUNT];
        System.arraycopy(tmp[0], 0, characterFrames, 0, DUCK_FRAME_COUNT);

        duckAnimation = new Animation<>(DUCK_ANIMATION_FRAME_DURATION, characterFrames);

        stateTime = 0f;
        batch = new SpriteBatch();
    }

    @Override
    public void show() {
        assets = new Assets();
        assets.loadGameAssets();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stateTime += delta;

        TextureRegion currentFrame = duckAnimation.getKeyFrame(stateTime, true);
        float duckX = Gdx.graphics.getWidth() / 2f - DUCK_X_OFFSET;
        float duckY = Gdx.graphics.getHeight() / 2f;

        float startX = duckX - (2 * (APPLE_SIZE + APPLE_SPACING));
        float appleY = duckY - APPLE_Y_OFFSET;

        float progress = assets.getAssetManager().getProgress();
        int coloredApples = (int) (progress * TOTAL_APPLES);

        batch.begin();
        batch.draw(currentFrame, duckX, duckY, DUCK_DRAW_WIDTH, DUCK_DRAW_HEIGHT);

        for (int i = 0; i < TOTAL_APPLES; i++) {
            if (i < coloredApples) {
                batch.setColor(1, 1, 1, 1);
            } else {
                batch.setColor(0.5f, 0.5f, 0.5f, 1);
            }
            batch.draw(appleTexture, startX + i * (APPLE_SIZE + APPLE_SPACING), appleY, APPLE_SIZE, APPLE_SIZE);
        }

        batch.setColor(1, 1, 1, 1);
        batch.end();

        if (assets.getAssetManager().update()) {
            game.setScreen(new MainMenuScreen(game, assets , DEFAULT_VOLUME , DEFAULT_VOLUME));
        }
    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        batch.dispose();
        duckTexture.dispose();
        appleTexture.dispose();
    }
}
