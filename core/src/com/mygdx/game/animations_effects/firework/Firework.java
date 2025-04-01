package com.mygdx.game.animations_effects.firework;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Firework {
    private final FireworkRocket rocket;
    private FireworkExplosion explosion;
    private final Texture explosionTexture;
    private boolean finished = false;
    private final Color color;

    public Firework(Texture rocketTexture, Texture explosionTexture, Vector2 startPosition) {
        this.color = new Color(
                MathUtils.random(0.5f, 1f),
                MathUtils.random(0.5f, 1f),
                MathUtils.random(0.5f, 1f),
                1f
        );
        this.rocket = new FireworkRocket(rocketTexture, startPosition);
        this.explosionTexture = explosionTexture;
    }

    public void update(float delta) {

        if (rocket.isFinished()) {
            rocket.update(delta);
        } else {
            if (explosion == null) {
                explosion = new FireworkExplosion(explosionTexture, rocket.getPosition().cpy());
            }
            explosion.update(delta);
            if (explosion.isFinished()) {
                finished = true;
            }
        }
    }

    public void render(SpriteBatch batch) {
        batch.setColor(color);
        if (rocket.isFinished()) {
            rocket.render(batch);
        } else if (explosion != null) {
            explosion.render(batch);
        }
        batch.setColor(Color.WHITE);
    }

    public boolean isFinished() {
        return finished;
    }
}
