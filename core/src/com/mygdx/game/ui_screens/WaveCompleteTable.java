package com.mygdx.game.ui_screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.ArenaMode;
import com.mygdx.game.combat_system.Wave;
import com.mygdx.game.utilities_resources.Assets;

import static com.badlogic.gdx.math.MathUtils.random;

public class WaveCompleteTable extends Table {

    ArenaMode arenaMode;

    public WaveCompleteTable(Skin skin, Assets assets , ArenaMode arenaMode) {

        this.arenaMode = arenaMode;

        Label label1 = new Label("Damage(+5%)", skin);
        TextureRegion myTextureRegion = new TextureRegion(assets.getAssetManager().get(Assets.bulletTexture));
        TextureRegionDrawable myTexRegionDrawable = new TextureRegionDrawable(myTextureRegion);
        myTexRegionDrawable.setMinSize((float) Gdx.graphics.getWidth() /10, (float) Gdx.graphics.getWidth() /10);
        ImageButton button1 = new ImageButton(myTexRegionDrawable);
        button1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                arenaMode.setIsPaused(false);
                arenaMode.setDamage(arenaMode.getDamage()+ arenaMode.getDamage()*5/100);
                for (Wave wave: arenaMode.getWaves()) {
                    wave.setBulletDamage(arenaMode.getDamage());
                }
            }
        });

        Label label2 = new Label("Health(+1)", skin);
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
                arenaMode.setIsPaused(false);
                arenaMode.getCharacter().GainLife();

            }
        });

        // Create the third row
        Label label3 = new Label("Speed(+10%)", skin);
        myTextureRegion = new TextureRegion(assets.getAssetManager().get(Assets.lightningBoltTexture));
        myTexRegionDrawable = new TextureRegionDrawable(myTextureRegion);
        myTexRegionDrawable.setMinSize((float) Gdx.graphics.getWidth() /10, (float) Gdx.graphics.getWidth() /10);
        ImageButton button3 = new ImageButton(myTexRegionDrawable);
        button3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                arenaMode.setIsPaused(false);
                arenaMode.getCharacter().GainSpeed();
            }
        });

        Stack stack = new Stack();
        stack.add(behindImage);
        stack.add(button2);

        Label label4 = new Label("Crit-Rate(+5%)", skin);
        myTextureRegion = new TextureRegion(assets.getAssetManager().get(Assets.bulletTexture));
        myTexRegionDrawable = new TextureRegionDrawable(myTextureRegion);
        myTexRegionDrawable.setMinSize((float) Gdx.graphics.getWidth() /10, (float) Gdx.graphics.getWidth() /10);
        ImageButton button4 = new ImageButton(myTexRegionDrawable);
        button4.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                arenaMode.setIsPaused(false);
                arenaMode.incrementCritRate();
            }
        });

        int randomNumber = random.nextInt(4) + 1;

        if (randomNumber != 1) {
            add(label1).pad(10, 0, 0, 50);
        }
        if (randomNumber != 2) {
            add(label2).pad(10, 0, 0, 50);
        }
        if (randomNumber != 3) {
            add(label3).pad(10, 0, 0, 50);
        }
        if (randomNumber != 4) {
            add(label4).pad(10, 0, 0, 50);
        }
        row();
        if (randomNumber != 1) {
            add(button1).pad(10, 0, 0, 50);
        }
        if (randomNumber != 2) {
            add(stack).pad(10, 0, 0, 50);
        }
        if (randomNumber != 3) {
            add(button3).pad(10, 0, 0, 50);
        }
        if (randomNumber != 4) {
            add(button4).pad(10, 0, 0, 50);
        }

    }

}
