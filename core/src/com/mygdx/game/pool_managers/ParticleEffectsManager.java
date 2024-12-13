package com.mygdx.game.pool_managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.utilities_resources.Assets;

public class ParticleEffectsManager {
    private final Array<ParticleEffect> activeParticleEffects = new Array<>();

    private final Pool<ParticleEffect> particleEffectPool;
    ParticleEffect particleEffect;

    public ParticleEffectsManager(Assets assets) {
        particleEffect = assets.getAssetManager().get(Assets.explosionParticleEffect);
        particleEffectPool = new Pool<>() {
            @Override
            protected ParticleEffect newObject() {
                return new ParticleEffect(particleEffect);
            }
        };
    }

    public void update(){
        for (int i = 0; i < activeParticleEffects.size; i++) {
            ParticleEffect particleEffect = activeParticleEffects.get(i);
            particleEffect.update(Gdx.graphics.getDeltaTime());

            if (particleEffect.isComplete()) {
                activeParticleEffects.removeIndex(i);
                particleEffectPool.free(particleEffect);
                i--;
            }
        }
    }

    public void draw(SpriteBatch batch){
        for (ParticleEffect particleEffect : activeParticleEffects) {
            particleEffect.draw(batch);
        }
    }

    public void DeathParticles(Vector2 position, float scale  , Boolean scaled) {
        ParticleEffect particleEffect = particleEffectPool.obtain();
        ParticleEmitter emitter = particleEffect.getEmitters().first();
        emitter.setPosition(position.x, position.y);
        if (!scaled) {
            emitter.scaleSize(scale);
        }
        particleEffect.start();
        activeParticleEffects.add(particleEffect);
    }

    public void fillPool(int amount){
        particleEffectPool.fill(amount);
    }
    public void dispose() {
        activeParticleEffects.clear();
        particleEffectPool.clear();
    }

}
