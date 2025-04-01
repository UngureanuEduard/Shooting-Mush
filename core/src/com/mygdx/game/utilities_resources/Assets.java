package com.mygdx.game.utilities_resources;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Assets {

    private final AssetManager assetManager = new AssetManager();

    // Asset descriptors
    public static final AssetDescriptor<Skin> skin = new AssetDescriptor<>("Font/menu.json", Skin.class);
    public static final AssetDescriptor<TiledMap> arenaTiledMap = new AssetDescriptor<>("arenaMap.tmx", TiledMap.class);
    public static final AssetDescriptor<TiledMap> storyTiledMap = new AssetDescriptor<>("storyMap1.tmx", TiledMap.class);
    public static final AssetDescriptor<Texture> walkTexture = new AssetDescriptor<>("Character/Character Walking Side.png", Texture.class);
    public static final AssetDescriptor<Texture> idleTexture = new AssetDescriptor<>("Character/Character Idle Side.png", Texture.class);
    public static final AssetDescriptor<Texture> walkFrontTexture = new AssetDescriptor<>("Character/Character Walking Front.png", Texture.class);
    public static final AssetDescriptor<Texture> walkBackTexture = new AssetDescriptor<>("Character/Character Walking Back.png", Texture.class);
    public static final AssetDescriptor<Texture> heartTexture = new AssetDescriptor<>("Environment/heart.png", Texture.class);
    public static final AssetDescriptor<Texture> emptyHeartTexture = new AssetDescriptor<>("Environment/border.png", Texture.class);
    public static final AssetDescriptor<Texture> bulletTexture = new AssetDescriptor<>("Environment/apple.png", Texture.class);
    public static final AssetDescriptor<Sound> throwSound = new AssetDescriptor<>("mp3/throw.mp3",Sound.class);
    public static final AssetDescriptor<ParticleEffect> explosionParticleEffect = new AssetDescriptor<>("Environment/explosion/explosion.party", ParticleEffect.class);
    public static final AssetDescriptor<Texture> skullTexture = new AssetDescriptor<>("Environment/skull.png", Texture.class);
    public static final AssetDescriptor<Texture> menuBackgroundTexture = new AssetDescriptor<>("Font/Background.jpg", Texture.class);
    public static final AssetDescriptor<Texture> duckTexture = new AssetDescriptor<>("Environment/Duck.png", Texture.class);
    public static final AssetDescriptor<Sound> duckSound = new AssetDescriptor<>("mp3/duck.mp3",Sound.class);
    public static final AssetDescriptor<Music> menuMusic = new AssetDescriptor<>("mp3/menuMusic.ogg", Music.class);
    public static final AssetDescriptor<Music> gameMusic = new AssetDescriptor<>("mp3/gameMusic.mp3", Music.class);
    public static final AssetDescriptor<Texture> lightningBoltTexture = new AssetDescriptor<>("Environment/lightningBolt.png", Texture.class);
    public static final AssetDescriptor<Texture> candyCornTexture = new AssetDescriptor<>("Environment/candyCorn.png", Texture.class);
    public static final AssetDescriptor<Sound> duckShootSound = new AssetDescriptor<>("mp3/duckShoot.mp3",Sound.class);
    public static final AssetDescriptor<Texture> bossTexture = new AssetDescriptor<>("Environment/boss.png", Texture.class);
    public static final AssetDescriptor<Texture> idleBossTexture = new AssetDescriptor<>("Environment/IdleDuckBoss.png", Texture.class);
    public static final AssetDescriptor<Texture> idleEnemyTexture = new AssetDescriptor<>("Environment/IdleEnemy.png", Texture.class);
    public static final AssetDescriptor<Texture> idleShoomTexture = new AssetDescriptor<>("Environment/Shoom_Idle.png", Texture.class);
    public static final AssetDescriptor<Texture> BossHealthBarTexture = new AssetDescriptor<>("Environment/BorderHealth.png", Texture.class);
    public static final AssetDescriptor<Texture> HealthTexture = new AssetDescriptor<>("Environment/Texture.png", Texture.class);
    public static final AssetDescriptor<Texture> EnemyHealthTexture = new AssetDescriptor<>("Environment/EnemyHealthTexture.png", Texture.class);
    public static final AssetDescriptor<Texture> EnemyHealthBarTexture = new AssetDescriptor<>("Environment/HealthBarEmpty.png", Texture.class);
    public static final AssetDescriptor<Music> bossMusic = new AssetDescriptor<>("mp3/BossSong.mp3", Music.class);
    public static final AssetDescriptor<Sound> DialogueNPC1Line1 = new AssetDescriptor<>("mp3/Mylo 1.wav",Sound.class);
    public static final AssetDescriptor<Sound> DialogueNPC1Line2 = new AssetDescriptor<>("mp3/The Ducks 1.wav",Sound.class);
    public static final AssetDescriptor<Sound> DialogueNPC1Line3 = new AssetDescriptor<>("mp3/You need to stop them 1.wav",Sound.class);
    public static final AssetDescriptor<Texture> fallingLeafTexture = new AssetDescriptor<>("Environment/Leaf.png", Texture.class);
    public static final AssetDescriptor<Texture> dialogTexture = new AssetDescriptor<>("Environment/Dialog.png", Texture.class);
    public static final AssetDescriptor<Texture> goldTrophyTexture = new AssetDescriptor<>("Environment/Golden Trophy Large.png", Texture.class);
    public static final AssetDescriptor<Texture> silverTrophyTexture = new AssetDescriptor<>("Environment/Silver Trophy Large.png", Texture.class);
    public static final AssetDescriptor<Texture> bronzeTrophyTexture = new AssetDescriptor<>("Environment/Bronze Trophy Large.png", Texture.class);
    public static final AssetDescriptor<Texture> fireworkExplosionTexture = new AssetDescriptor<>("Environment/FireWorksExplosion.png", Texture.class);
    public static final AssetDescriptor<Texture> fireworkRocketTexture = new AssetDescriptor<>("Environment/FireWorksRocket.png", Texture.class);
    public Assets() {
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
    }

    public void loadGameAssets() {
        AssetDescriptor<?>[] assetsToLoad = {
                skin, menuBackgroundTexture, duckTexture, menuMusic, arenaTiledMap, storyTiledMap, walkTexture,
                idleTexture, walkFrontTexture, walkBackTexture, heartTexture, emptyHeartTexture, bulletTexture,
                throwSound, explosionParticleEffect, skullTexture, idleShoomTexture, lightningBoltTexture,
                candyCornTexture, duckSound, gameMusic, bossTexture, EnemyHealthTexture, duckShootSound,
                idleBossTexture, idleEnemyTexture, BossHealthBarTexture, EnemyHealthBarTexture, HealthTexture,
                bossMusic, DialogueNPC1Line1, DialogueNPC1Line2, DialogueNPC1Line3, fallingLeafTexture, dialogTexture ,
                goldTrophyTexture , silverTrophyTexture , bronzeTrophyTexture , fireworkExplosionTexture , fireworkRocketTexture
        };

        for (AssetDescriptor<?> asset : assetsToLoad) {
            assetManager.load(asset);
        }
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public void dispose() {
        assetManager.dispose();
    }
}
