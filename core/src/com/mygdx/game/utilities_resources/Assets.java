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

    public static final AssetDescriptor<Skin> skin = new AssetDescriptor<>("Font/menu.json", Skin.class);
    public static final AssetDescriptor<TiledMap> arenaTiledMap = new AssetDescriptor<>("arenaMap.tmx", TiledMap.class);
    public static final AssetDescriptor<TiledMap> cutscene1Map = new AssetDescriptor<>("cutSceen1.tmx", TiledMap.class);
    public static final AssetDescriptor<TiledMap> storyTiledMap = new AssetDescriptor<>("storyMap1.tmx", TiledMap.class);
    public static final AssetDescriptor<TiledMap> storyTiledMap_2 = new AssetDescriptor<>("Tiled_files/Dungeon1.tmx", TiledMap.class);
    public static final AssetDescriptor<Texture> walkTexture = new AssetDescriptor<>("Character/Character Walking Side.png", Texture.class);
    public static final AssetDescriptor<Texture> idleTexture = new AssetDescriptor<>("Character/Character Idle Side.png", Texture.class);
    public static final AssetDescriptor<Texture> idleTextureGrandpa = new AssetDescriptor<>("Character/Character Idle Side Old.png", Texture.class);
    public static final AssetDescriptor<Texture> walkFrontTexture = new AssetDescriptor<>("Character/Character Walking Front.png", Texture.class);
    public static final AssetDescriptor<Texture> walkBackTexture = new AssetDescriptor<>("Character/Character Walking Back.png", Texture.class);
    public static final AssetDescriptor<Texture> heartTexture = new AssetDescriptor<>("Environment/heart.png", Texture.class);
    public static final AssetDescriptor<Texture> emptyHeartTexture = new AssetDescriptor<>("Environment/border.png", Texture.class);
    public static final AssetDescriptor<Texture> bulletTexture = new AssetDescriptor<>("Environment/apple.png", Texture.class);
    public static final AssetDescriptor<Sound> throwSound = new AssetDescriptor<>("mp3/throw.mp3",Sound.class);
    public static final AssetDescriptor<Sound> throwExplosionSound = new AssetDescriptor<>("mp3/8-bit-fireball.mp3",Sound.class);
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
    public static final AssetDescriptor<Music> dungeonMusic = new AssetDescriptor<>("mp3/dungeonSong.wav", Music.class);
    public static final AssetDescriptor<Music> introMusic = new AssetDescriptor<>("mp3/Eldertide.mp3", Music.class);
    public static final AssetDescriptor<Music> trainMusic = new AssetDescriptor<>("mp3/Survivor.mp3", Music.class);
    public static final AssetDescriptor<Texture> fallingLeafTexture = new AssetDescriptor<>("Environment/Leaf.png", Texture.class);
    public static final AssetDescriptor<Texture> dialogTexture = new AssetDescriptor<>("Environment/Dialog.png", Texture.class);
    public static final AssetDescriptor<Texture> goldTrophyTexture = new AssetDescriptor<>("Environment/Golden Trophy Large.png", Texture.class);
    public static final AssetDescriptor<Texture> silverTrophyTexture = new AssetDescriptor<>("Environment/Silver Trophy Large.png", Texture.class);
    public static final AssetDescriptor<Texture> bronzeTrophyTexture = new AssetDescriptor<>("Environment/Bronze Trophy Large.png", Texture.class);
    public static final AssetDescriptor<Texture> fireworkExplosionTexture = new AssetDescriptor<>("Environment/FireWorksExplosion.png", Texture.class);
    public static final AssetDescriptor<Texture> cloudTexture = new AssetDescriptor<>("Environment/cloud.png", Texture.class);
    public static final AssetDescriptor<Texture> fireworkRocketTexture = new AssetDescriptor<>("Environment/FireWorksRocket.png", Texture.class);
    public static final AssetDescriptor<Texture> co_opButtonTexture = new AssetDescriptor<>("co_op.png", Texture.class);
    public static final AssetDescriptor<Texture> backButtonTexture = new AssetDescriptor<>("backButton.png", Texture.class);
    public static final AssetDescriptor<Texture> skeletonIdleTexture = new AssetDescriptor<>("Environment/skeleton-idle.png", Texture.class);
    public static final AssetDescriptor<Texture> skeletonWalkTexture = new AssetDescriptor<>("Environment/skeleton-walk.png", Texture.class);
    public static final AssetDescriptor<Texture> boneTexture = new AssetDescriptor<>("Environment/Bone.png", Texture.class);
    public static final AssetDescriptor<Texture> dialogBoxTexture = new AssetDescriptor<>("Dialog.png", Texture.class);
    public static final AssetDescriptor<Texture> grandpaPortraitTexture = new AssetDescriptor<>("portraits/Grandpa.png", Texture.class);
    public static final AssetDescriptor<Texture> myloPortraitTexture = new AssetDescriptor<>("portraits/Mylo.png", Texture.class);
    public static final AssetDescriptor<Texture> blackPixelTexture = new AssetDescriptor<>("black.png", Texture.class);
    public static final AssetDescriptor<Texture> fireBallTexture = new AssetDescriptor<>("FireBall.png", Texture.class);

    public Assets() {
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
    }

    public void loadGameAssets() {
        AssetDescriptor<?>[] assetsToLoad = {
                skin, menuBackgroundTexture, duckTexture, menuMusic, arenaTiledMap, storyTiledMap , storyTiledMap_2, walkTexture,
                idleTexture, walkFrontTexture, walkBackTexture, heartTexture, emptyHeartTexture, bulletTexture,
                throwSound, explosionParticleEffect, skullTexture, idleShoomTexture, lightningBoltTexture,
                candyCornTexture, duckSound, gameMusic, bossTexture, EnemyHealthTexture, duckShootSound,
                idleBossTexture, idleEnemyTexture, BossHealthBarTexture, EnemyHealthBarTexture, HealthTexture,
                bossMusic, fallingLeafTexture, dialogTexture ,
                goldTrophyTexture , silverTrophyTexture , bronzeTrophyTexture , fireworkExplosionTexture , fireworkRocketTexture
                , co_opButtonTexture , backButtonTexture  , dungeonMusic ,
                skeletonIdleTexture , skeletonWalkTexture , boneTexture , cloudTexture ,cutscene1Map ,idleTextureGrandpa ,
                dialogBoxTexture , grandpaPortraitTexture , blackPixelTexture , introMusic,myloPortraitTexture ,trainMusic ,
                throwExplosionSound , fireBallTexture
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
