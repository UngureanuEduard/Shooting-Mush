package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

import java.util.ArrayList;
import java.util.List;

public class Npc {
    private Vector2 position;
    private final Animation<TextureRegion> idleAnimation;
    private float stateTime=0.0f;
    private final Rectangle npcRectangle;
    private final float width;
    private final float height;
    TypingLabel label;
    TypingLabel promptLabel;
    private Boolean inDialog = false;
    private final List<String> dialogueLines;
    private int currentLineIndex = 0;
    private final String dialogPrompt;
    private boolean isDialogPromptVisible = false;
    private final List<Sound> dialogueSounds;
    private final int soundVolume;

    public Npc(Vector2 position, Assets assets , int soundVolume) {
        this.position = position;
        this.soundVolume = soundVolume;
        Texture idleTexture = assets.getAssetManager().get(Assets.idleShoomTexture);
        TextureRegion[] frogFrames = splitTexture(idleTexture,6);
        idleAnimation = new Animation<>(0.15f, frogFrames);
        width = idleAnimation.getKeyFrame(stateTime, true).getRegionWidth();
        height = idleAnimation.getKeyFrame(stateTime, true).getRegionHeight();
        npcRectangle = new Rectangle(position.x-width, position.y-height, width*2, height*2);
        dialogueLines = new ArrayList<>();
        dialogueLines.add("{EASE}{SLOW} MYLO!                 ");
        dialogueLines.add("{EASE}{SLOW} The DUCKS!            ");
        dialogueLines.add("{EASE}{SLOW} You need to stop them!");
        dialogPrompt = "T";
        dialogueSounds = new ArrayList<>();
        dialogueSounds.add(assets.getAssetManager().get(Assets.DialogueNPC1Line1));
        dialogueSounds.add(assets.getAssetManager().get(Assets.DialogueNPC1Line2));
        dialogueSounds.add(assets.getAssetManager().get(Assets.DialogueNPC1Line3));
    }

    public void setPosition(Vector2 position) {
        this.position = position;
        npcRectangle.setPosition(position.x, position.y-50);
    }

    private TextureRegion[] splitTexture(Texture characterTexture , int n) {
        TextureRegion[][] tmp = TextureRegion.split(characterTexture, 64, 64);
        TextureRegion[] characterFrames = new TextureRegion[n];
        System.arraycopy(tmp[0], 0, characterFrames, 0, n);
        return characterFrames;
    }

    public void update(float deltaTime , boolean isPaused , Vector2 playerPosition , Stage stage , Skin skin) {
        if (!isPaused) {
            stateTime += deltaTime;
            if(npcRectangle.contains(playerPosition)){
            if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
                inDialog = true;
                updateDialogue(stage , skin);
                isDialogPromptVisible = false;
                promptLabel.remove();
            }
            if(!inDialog && !isDialogPromptVisible ) {
                showDialogPrompt(skin , stage);
                isDialogPromptVisible= true;
            }
            }
            else if(isDialogPromptVisible){
                isDialogPromptVisible = false;
                promptLabel.remove();
            }
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(idleAnimation.getKeyFrame(stateTime, true), position.x, position.y, (float) (width/1.5), (float) (height/1.5));
    }

    private void updateDialogue(Stage stage, Skin skin) {
        if (currentLineIndex < dialogueLines.size()) {
            String text = dialogueLines.get(currentLineIndex);

            if (label != null) {
                label.remove();
            }

            label = new TypingLabel(text, skin);
            label.setFontScale(0.60f);
            label.setPosition( (position.x + stage.getWidth() /2  - label.getPrefWidth()), (float) (position.y + stage.getHeight() / 2.3));
            stage.addActor(label);
            dialogueSounds.get(currentLineIndex).play(soundVolume / 100f);
            currentLineIndex++;

        } else {
            if (label != null) {
                label.remove();
                label = null;
            }
            inDialog = false;
            currentLineIndex = 0;
        }
    }

    private void showDialogPrompt(Skin skin , Stage stage) {
        promptLabel = new TypingLabel(dialogPrompt, skin);
        promptLabel.setFontScale(0.60f);
        promptLabel.setPosition((float) (position.x + stage.getWidth() / 3.75), (float) (position.y + stage.getHeight() /2.3 ));
        stage.addActor(promptLabel);
    }

    public Boolean getInDialog(){
        return inDialog;
    }
}
