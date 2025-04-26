package com.mygdx.game.combat_system;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.utilities_resources.Assets;

public class CharacterBullet extends Bullet{

    private static final float BULLET_SIZE = 35f;
    private static final float HITBOX_RADIUS_SCALE = 0.25f;
    private  Circle hitBox;
    private boolean isFromHost;

    public CharacterBullet(){
        super();
    }


    public void init(Vector2 position , Vector2 velocity, float damage, Assets assets , Integer soundVolume , boolean fromHost) {
        super.init(position,velocity,damage);
        setDamageScale(DAMAGE_SCALE_BASE + damage / DAMAGE_SCALE_FACTOR);
        this.isFromHost = fromHost;
        Texture bulletAppleTexture = assets.getAssetManager().get(Assets.bulletTexture);
        Sound soundCharacter = assets.getAssetManager().get(Assets.throwSound);
        setTexture(new TextureRegion(bulletAppleTexture));
        soundCharacter.play(soundVolume/100f);
        hitBox=new Circle();
    }

    public void update(float deltaTime){
        updatePosition(deltaTime);
        updateAngle();

        hitBox.set(getPosition().x + getWidth() / 2, getPosition().y + getHeight() / 2, getWidth() *HITBOX_RADIUS_SCALE);
    }

    public void render(SpriteBatch batch){
        batch.draw(getTexture(), getPosition().x, getPosition().y, getWidth() / 2,
                getHeight() / 2, getWidth(), getHeight(), (float) (getDamageScale()-0.3),
                (float) (getDamageScale()-0.3), getAngle());
    }

    private float getWidth() {
        return BULLET_SIZE * getDamageScale() * 0.8f;
    }

    private float getHeight() {
        return BULLET_SIZE * getDamageScale() * 0.8f;
    }

    public Circle getHitBox(){return hitBox;}

    public boolean isFromHost() {
        return isFromHost;
    }

    @Override
    public void reset() {
        super.reset();
        isFromHost = false;
    }

}
