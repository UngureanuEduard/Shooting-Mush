package com.mygdx.game;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class CharacterBullet extends Bullet{

    public CharacterBullet(Vector2 position, Vector2 velocity, float damage, Assets assets, Integer soundVolume) {
        super(position, velocity, damage, assets);
        Texture bulletAppleTexture = this.assets.getAssetManager().get(Assets.bulletTexture);
        Sound soundCharacter = assets.getAssetManager().get(Assets.throwSound);
        texture = new TextureRegion(bulletAppleTexture);
        soundCharacter.play(soundVolume/100f);
    }
}
