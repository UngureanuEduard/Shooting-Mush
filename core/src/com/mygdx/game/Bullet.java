package com.mygdx.game;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    private final Vector2 position;
    private final Vector2 velocity;
    private final TextureRegion texture;

    public Bullet(Vector2 position, Vector2 velocity, TextureRegion texture) {
        this.position = position;
        this.velocity = velocity;
        this.texture = texture;
    }

    public void update(float deltaTime) {
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    public void dispose() {
        texture.getTexture().dispose();
    }


}
