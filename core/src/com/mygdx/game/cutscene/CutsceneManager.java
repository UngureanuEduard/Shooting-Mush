package com.mygdx.game.cutscene;

import java.util.LinkedList;
import java.util.Queue;

public class CutsceneManager {
    private final Queue<CutsceneEvent> events = new LinkedList<>();
    private CutsceneEvent currentEvent;

    public void addEvent(CutsceneEvent event) {
        events.add(event);
    }

    public void update(float delta) {
        if (currentEvent == null || currentEvent.isComplete()) {
            currentEvent = events.poll();
        }
        if (currentEvent != null) {
            currentEvent.update(delta);
        }
    }

    public void skipEvent() {
        if (currentEvent != null) {
            currentEvent.skip();
            int safeguard = 0;
            while (!currentEvent.isComplete() && safeguard++ < 100) {
                currentEvent.update(Float.MAX_VALUE);
            }
        }
    }

    public boolean isFinished() {
        return currentEvent == null && events.isEmpty();
    }
}


