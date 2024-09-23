package com.mygdx.game;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class EnemyBullet extends Bullet{
    public EnemyBullet(Vector2 position, Vector2 velocity, float damage, Assets assets, Integer soundVolume) {
        super(position, velocity, damage, assets);
        Texture bulletCornTexture = this.assets.getAssetManager().get(Assets.candyCornTexture);
        Sound soundEnemy = assets.getAssetManager().get(Assets.duckShootSound);
        texture = new TextureRegion(bulletCornTexture);
        soundEnemy.play(soundVolume/100f);
    }
}
