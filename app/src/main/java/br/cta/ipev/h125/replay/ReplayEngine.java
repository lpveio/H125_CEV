package br.cta.ipev.h125.replay;

import java.util.ArrayList;
import java.util.List;

public class ReplayEngine {

    private final List<ReplayFrame> frames;

    private final List<ReplayDisplay> displays =
            new ArrayList<>();

    private boolean playing = false;

    private int currentIndex = 0;

    private double speed = 1.0;

    public ReplayEngine(List<ReplayFrame> frames) {
        this.frames = frames;
    }

    public void addDisplay(ReplayDisplay display) {
        displays.add(display);
    }

    public void play() {

        playing = true;

        new Thread(() -> {

            while (playing && currentIndex < frames.size()) {

                try {

                    ReplayFrame frame =
                            frames.get(currentIndex);

                    for (ReplayDisplay d : displays) {
                        d.onReplayFrame(frame.values);
                    }

                    currentIndex++;

                    Thread.sleep((long)(125 / speed));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    public void pause() {
        playing = false;
    }

    public void seek(int index) {
        currentIndex = index;
    }



    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }
}
