package com.mygdx.game.ui_screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.utilities_resources.Assets;

public class OptionsTable extends Table {
    private int musicVolume;
    private int soundVolume;

    public OptionsTable(Assets assets, final MainMenuScreen.MainMenuCallback callback, int value1, int value2) {
        Skin skin=assets.getAssetManager().get(Assets.skin);
        final Slider firstSlider = new Slider(0, 100, 10, false, skin, "default-horizontal");
        final Slider secondSlider = new Slider(0, 100, 10, false, skin, "default-horizontal");
        Label label1 = new Label("Music:",skin);
        Label label2 = new Label("Sound Effects:      ",skin);
        TextButton textButton = new TextButton("Save" , skin);


        firstSlider.setValue(value1);
        secondSlider.setValue(value2);

        Container<Slider> container1= new Container<>(firstSlider);
        container1.setTransform(true);
        container1.size((float) Gdx.graphics.getWidth() /4, (float) Gdx.graphics.getHeight() /8);
        Container<Slider> container2= new Container<>(secondSlider);
        container2.setTransform(true);
        container2.size((float) Gdx.graphics.getWidth() /4, (float) Gdx.graphics.getHeight() /8);

        textButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                musicVolume = (int) firstSlider.getValue();
                soundVolume = (int) secondSlider.getValue();
                callback.backToMainMenu();
            }

        });

        // Add the sliders to the table
        add(label1).center();
        add(container1).center();
        row();
        add(label2).center();
        add(container2).center();
        row();
        add(textButton).colspan(3);
        center();
    }
    public int getMusicVolume() {
        return musicVolume;
    }

    public int getSoundVolume() {
        return soundVolume;
    }
}