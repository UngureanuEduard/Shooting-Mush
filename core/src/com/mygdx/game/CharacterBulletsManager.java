package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class CharacterBulletsManager {

    private final Array<CharacterBullet> activeCharacterBullets = new Array<>();

    private final Pool<CharacterBullet> characterBulletPool = new Pool<>() {
        @Override
        protected CharacterBullet newObject() {
            return new CharacterBullet();
        }
    };

    public CharacterBulletsManager() {
    }

    public void dispose() {
        characterBulletPool.clear();
        activeCharacterBullets.clear();
    }
    public void generateBullet(Vector2 bulletStartPosition, Vector2 directionToCursor, float damage, Assets assets, Integer soundVolume){
        CharacterBullet item = characterBulletPool.obtain();
        item.init(bulletStartPosition, directionToCursor, damage, assets, soundVolume);
        activeCharacterBullets.add(item);
    }

    public void fillPool(int amount){
        characterBulletPool.fill(amount);
    }

    public void updateAndRender(SpriteBatch batch , OrthographicCamera camera){
        for (CharacterBullet characterBullet : activeCharacterBullets) {
            characterBullet.update(Gdx.graphics.getDeltaTime());
            if (characterBullet.getAlive_n()) {
                activeCharacterBullets.removeValue(characterBullet, true);
                characterBulletPool.free(characterBullet);

            } else {
                characterBullet.render(batch, camera);
            }
        }
    }

    public Array<CharacterBullet> getActiveCharacterBullets() {
        return activeCharacterBullets;
    }

}
