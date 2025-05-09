package com.mygdx.game.ui_screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.utilities_resources.Assets;
import com.mygdx.game.utilities_resources.Settings;

import static com.mygdx.game.utilities_resources.Settings.v_sync;

public class OptionsTable extends Table {

    private static final int SLIDER_MIN = 0;
    private static final int SLIDER_MAX = 100;
    private static final int SLIDER_STEP = 10;
    private static final float SLIDER_WIDTH_RATIO = 0.25f;
    private static final float SLIDER_HEIGHT_RATIO = 0.125f;
    private static final int SAVE_BUTTON_TOP_SPACE = 20;

    private static final String SLIDER_STYLE = "default-horizontal";
    private static final String MUSIC_LABEL_TEXT = "Music:";
    private static final String SOUND_LABEL_TEXT = "Sound Effects:      ";
    private static final String SAVE_BUTTON_TEXT = "Save";

    private int musicVolume;
    private int soundVolume;

    public OptionsTable(Assets assets, final MainMenuScreen.MainMenuCallback callback, int value1, int value2) {
        Skin skin = assets.getAssetManager().get(Assets.skin);

        final Slider firstSlider = new Slider(SLIDER_MIN, SLIDER_MAX, SLIDER_STEP, false, skin, SLIDER_STYLE);
        final Slider secondSlider = new Slider(SLIDER_MIN, SLIDER_MAX, SLIDER_STEP, false, skin, SLIDER_STYLE);
        Label label1 = new Label(MUSIC_LABEL_TEXT, skin);
        Label label2 = new Label(SOUND_LABEL_TEXT, skin);
        TextButton textButton = new TextButton(SAVE_BUTTON_TEXT, skin);

        final CheckBox fullscreenCheckBox = new CheckBox(" Fullscreen", skin);
        final CheckBox vsyncCheckBox = new CheckBox(" V-Sync", skin);

        Label resolutionLabel = new Label("Resolution:", skin);
        SelectBox<String> resolutionSelectBox = new SelectBox<>(skin);
        resolutionSelectBox.setItems("1920x1080", "1600x900", "1280x720" , "854x480" , "640x360");
        resolutionSelectBox.setSelected("1920x1080");

        fullscreenCheckBox.setChecked(!Settings.windowed);

        vsyncCheckBox.setChecked(v_sync);

        firstSlider.setValue(value1);
        secondSlider.setValue(value2);

        Container<Slider> container1 = new Container<>(firstSlider);
        container1.setTransform(true);
        container1.size(Gdx.graphics.getWidth() * SLIDER_WIDTH_RATIO, Gdx.graphics.getHeight() * SLIDER_HEIGHT_RATIO);

        Container<Slider> container2 = new Container<>(secondSlider);
        container2.setTransform(true);
        container2.size(Gdx.graphics.getWidth() * SLIDER_WIDTH_RATIO, Gdx.graphics.getHeight() * SLIDER_HEIGHT_RATIO);

        textButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                musicVolume = (int) firstSlider.getValue();
                soundVolume = (int) secondSlider.getValue();

                if(vsyncCheckBox.isChecked())
                {
                    Gdx.graphics.setVSync(true);
                    v_sync = true;
                }

                String selectedResolution = resolutionSelectBox.getSelected();
                String[] resParts = selectedResolution.split("x");

                int width = Integer.parseInt(resParts[0]);
                int height = Integer.parseInt(resParts[1]);

                if (fullscreenCheckBox.isChecked()) {
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                    Settings.windowed = false;
                } else {
                    Gdx.graphics.setWindowedMode(width, height);
                    Settings.windowed = true;
                    Settings.windowedScreenWidth = width;
                    Settings.windowedScreenHeight = height;
                }
                callback.backToMainMenu();
            }
        });


        add(fullscreenCheckBox).colspan(2).center();
        row();
        add(vsyncCheckBox).colspan(2).center();
        row();
        add(label1).center();
        add(container1).center();
        row();
        add(label2).center();
        add(container2).center();
        row();
        add(resolutionLabel);
        add(resolutionSelectBox).center();
        row();
        add(textButton).colspan(3).spaceTop(SAVE_BUTTON_TOP_SPACE);
        row();
        center();

    }

    public int getMusicVolume() {
        return musicVolume;
    }

    public int getSoundVolume() {
        return soundVolume;
    }
}
