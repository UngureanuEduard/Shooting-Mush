package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class WaveCompleteTable extends Table {

    GameScene gameScene;

    public WaveCompleteTable(Skin skin, Assets assets, final GameScene gameScene) {
        this.gameScene = gameScene;

        Label label1 = new Label("Damage", skin);
        TextureRegion myTextureRegion = new TextureRegion(assets.getAssetManager().get(Assets.bulletTexture));
        TextureRegionDrawable myTexRegionDrawable = new TextureRegionDrawable(myTextureRegion);
        myTexRegionDrawable.setMinSize((float) Gdx.graphics.getWidth() /10, (float) Gdx.graphics.getWidth() /10);
        ImageButton button1 = new ImageButton(myTexRegionDrawable);
        button1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameScene.isPaused = false;
                gameScene.damage+=gameScene.damage*5/100;
                for (Wave wave: gameScene.waves) {
                    wave.setBulletDamage(gameScene.damage);
                }
            }
        });

        Label label2 = new Label("Health", skin);
        myTextureRegion = new TextureRegion(assets.getAssetManager().get(Assets.heartTexture));
        myTexRegionDrawable = new TextureRegionDrawable(myTextureRegion);
        myTexRegionDrawable.setMinSize((float) Gdx.graphics.getWidth() /10, (float) Gdx.graphics.getWidth() /10);
        ImageButton button2 = new ImageButton(myTexRegionDrawable);
        TextureRegion behindTextureRegion = new TextureRegion(assets.getAssetManager().get(Assets.emptyHeartTexture));
        TextureRegionDrawable behindTexRegionDrawable = new TextureRegionDrawable(behindTextureRegion);
        behindTexRegionDrawable.setMinSize((float) Gdx.graphics.getWidth() / 10, (float) Gdx.graphics.getWidth() / 10);
        Image behindImage = new Image(behindTexRegionDrawable);
        button2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameScene.isPaused = false;
                gameScene.character.GainLife();
            }
        });

        // Create the third row
        Label label3 = new Label("Speed", skin);
        myTextureRegion = new TextureRegion(assets.getAssetManager().get(Assets.lightningBoltTexture));
        myTexRegionDrawable = new TextureRegionDrawable(myTextureRegion);
        myTexRegionDrawable.setMinSize((float) Gdx.graphics.getWidth() /10, (float) Gdx.graphics.getWidth() /10);
        ImageButton button3 = new ImageButton(myTexRegionDrawable);
        button3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameScene.isPaused = false;
                gameScene.character.GainSpeed();
            }
        });

        Stack stack = new Stack();
        stack.add(behindImage);
        stack.add(button2);

        // Add the elements to the table
        add(label1).pad(10,0,0,50);
        add(label2).pad(10,0,0,50);
        add(label3).pad(10,0,0,50);
        row();
        add(button1).pad(10,0,0,50);
        add(stack).pad(10,0,0,50);
        add(button3).pad(10,0,0,50);
    }
}
