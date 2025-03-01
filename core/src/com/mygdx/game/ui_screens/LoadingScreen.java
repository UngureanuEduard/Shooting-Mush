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
        TextureRegion[][] tmp = TextureRegion.split(duckTexture, 128, 128);
        TextureRegion[] characterFrames = new TextureRegion[4];
        System.arraycopy(tmp[0], 0, characterFrames, 0, 4);
        duckAnimation = new Animation<>(0.1f, characterFrames);
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
        float duckX = Gdx.graphics.getWidth() / 2f - 100;
        float duckY = Gdx.graphics.getHeight() / 2f;
        float appleSize = 80;
        float spacing = 10;
        float startX = duckX - (2 * (appleSize + spacing));
        float appleY = duckY - 120;

        float progress = assets.getAssetManager().getProgress();
        int totalApples = 6;
        int coloredApples = (int) (progress * totalApples);

        batch.begin();
        batch.draw(currentFrame, duckX, duckY, 200, 200);

        for (int i = 0; i < totalApples; i++) {
            if (i < coloredApples) {
                batch.setColor(1, 1, 1, 1);
            } else {
                batch.setColor(0.5f, 0.5f, 0.5f, 1);
            }
            batch.draw(appleTexture, startX + i * (appleSize + spacing), appleY, appleSize, appleSize);
        }

        batch.setColor(1, 1, 1, 1);
        batch.end();
        if (assets.getAssetManager().update()) {
            game.setScreen(new MainMenuScreen(game , assets));
        }
    }


    @Override
    public void resize(int i, int i1) {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        duckTexture.dispose();
        appleTexture.dispose();
    }
}
