package com.mygdx.game;

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
    public static final AssetDescriptor<TiledMap> arenaTiledMap = new AssetDescriptor<>("map.tmx", TiledMap.class);
    public static final AssetDescriptor<TiledMap> storyTiledMap = new AssetDescriptor<>("StoryMap.tmx", TiledMap.class);
    public static final AssetDescriptor<Texture> walkTexture = new AssetDescriptor<>("Character/Character Walking Side.png", Texture.class);
    public static final AssetDescriptor<Texture> idleTexture = new AssetDescriptor<>("Character/Character Idle Side.png", Texture.class);
    public static final AssetDescriptor<Texture> walkFrontTexture = new AssetDescriptor<>("Character/Character Walking Front.png", Texture.class);
    public static final AssetDescriptor<Texture> walkBackTexture = new AssetDescriptor<>("Character/Character Walking Back.png", Texture.class);
    public static final AssetDescriptor<Texture> heartTexture = new AssetDescriptor<>("Environment/heart.png", Texture.class);
    public static final AssetDescriptor<Texture> emptyHeartTexture = new AssetDescriptor<>("Environment/border.png", Texture.class);
    public static final AssetDescriptor<Texture> bulletTexture = new AssetDescriptor<>("Environment/apple.png", Texture.class);
    public static final AssetDescriptor<Sound> throwSound = new AssetDescriptor<>("mp3/throw.mp3",Sound.class);
    public static final AssetDescriptor<ParticleEffect> explosionParticleEffect = new AssetDescriptor<>("Environment/explosion/explosion.party", ParticleEffect.class);
    public Assets() {
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
    }
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

    public static final AssetDescriptor<Texture> BorderHealthTexture = new AssetDescriptor<>("Environment/BorderHealth.png", Texture.class);
    public static final AssetDescriptor<Texture> HealthTexture = new AssetDescriptor<>("Environment/Texture.png", Texture.class);
    public static final AssetDescriptor<Music> bossMusic = new AssetDescriptor<>("mp3/BossSong.mp3", Music.class);

    public void loadMenuAssets() {
        assetManager.load(skin);
        assetManager.load(menuBackgroundTexture);
        assetManager.load(duckTexture);
        assetManager.load(menuMusic);
    }

    public void loadGameAssets() {
        assetManager.load(skin);
        assetManager.load(arenaTiledMap);
        assetManager.load(storyTiledMap);
        assetManager.load(walkTexture);
        assetManager.load(idleTexture);
        assetManager.load(walkFrontTexture);
        assetManager.load(walkBackTexture);
        assetManager.load(heartTexture);
        assetManager.load(emptyHeartTexture);
        assetManager.load(bulletTexture);
        assetManager.load(throwSound);
        assetManager.load(explosionParticleEffect);
        assetManager.load(skullTexture);
        assetManager.load(duckTexture);
        assetManager.load(lightningBoltTexture);
        assetManager.load(candyCornTexture);
        assetManager.load(duckSound);
        assetManager.load(gameMusic);
        assetManager.load(bossTexture);
        assetManager.load(duckShootSound);
        assetManager.load(idleBossTexture);
        assetManager.load(idleEnemyTexture);
        assetManager.load(BorderHealthTexture);
        assetManager.load(HealthTexture);
        assetManager.load(bossMusic);
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public void dispose() {
        assetManager.dispose();
    }
}
