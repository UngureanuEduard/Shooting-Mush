package com.mygdx.game.cutscene;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class HideMapLayerEvent implements CutsceneEvent {

    private final TiledMap map;
    private final String layerName;
    private boolean complete = false;
    private final Sound sound;

    public HideMapLayerEvent(TiledMap map, String layerName , Sound sound) {
        this.map = map;
        this.layerName = layerName;
        this.sound = sound;
    }

    @Override
    public boolean update(float delta) {
        if (!complete) {
            TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(layerName);
            if (layer != null) {
                layer.setVisible(false);
                if(sound != null) {
                    sound.play();
                }
            }
            complete = true;
        }
        return true;
    }

    @Override
    public boolean isComplete() {
        return complete;
    }
}
