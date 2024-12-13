package com.mygdx.game.animations_effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.utilities_resources.Assets;

public class LeafFallingAnimation {

    private static final int FRAME_COLS = 6;
    private static final int FRAME_ROWS = 1;

    private final Animation<TextureRegion> leafAnimation;
    private final Array<Leaf> leaves;
    private final Array<Leaf> leafPool;
    private final Texture leafSheet;
    Assets assets;

    public LeafFallingAnimation(Assets assets) {
        this.assets = assets;
        leafSheet = assets.getAssetManager().get(Assets.fallingLeafTexture);
        TextureRegion[][] tmp = TextureRegion.split(leafSheet, leafSheet.getWidth() / FRAME_COLS, leafSheet.getHeight() / FRAME_ROWS);
        TextureRegion[] leafFrames = new TextureRegion[FRAME_COLS];
        System.arraycopy(tmp[0], 0, leafFrames, 0, FRAME_COLS);
        leafAnimation = new Animation<>(0.2f, leafFrames);
        leaves = new Array<>();
        leafPool = new Array<>();
    }

    public void updateAndRender(SpriteBatch batch , Camera camera) {

        if (MathUtils.random(0, 100) > 92) {
            createLeaf(camera);
        }

        for (int i = leaves.size - 1; i >= 0; i--) {
            Leaf leaf = leaves.get(i);
            leaf.update();
            TextureRegion currentFrame = leafAnimation.getKeyFrame(leaf.getAnimationTime(), true);
            batch.draw(currentFrame, leaf.getPosition().x, leaf.getPosition().y);

            if (leaf.isOffScreen()) {
                leaves.removeIndex(i);
                leafPool.add(leaf);
            }
        }
    }

    private void createLeaf(Camera camera) {
        Leaf leaf;
        if (leafPool.size > 0) {
            leaf = leafPool.pop();
            leaf.reset(MathUtils.random(0, Gdx.graphics.getWidth()), Gdx.graphics.getHeight());
        } else {
            float x = MathUtils.random(0, Gdx.graphics.getWidth());
            float y = camera.position.y + camera.viewportHeight/8;
            float speed = MathUtils.random(50, 120);
            leaf = new Leaf(x, y, speed);
        }
        leaves.add(leaf);
    }

    public void dispose() {
        leafSheet.dispose();
    }

    private class Leaf {
        private final Vector2 position;
        private final float speed;
        private float animationTime;

        public Leaf(float x, float y, float speed) {
            this.position = new Vector2(x, y);
            this.speed = speed;
            this.animationTime = MathUtils.random(1f, 2f);
        }

        public void update() {
            position.y -= speed * Gdx.graphics.getDeltaTime();
            animationTime += Gdx.graphics.getDeltaTime();
        }

        public Vector2 getPosition() {
            return position;
        }

        public float getAnimationTime() {
            return animationTime;
        }

        public boolean isOffScreen() {
            return position.y < (float) -leafSheet.getHeight() / FRAME_ROWS;
        }

        public void reset(float x, float y) {
            position.set(x, y);
            this.animationTime = MathUtils.random(0f, 2f);
        }
    }
}