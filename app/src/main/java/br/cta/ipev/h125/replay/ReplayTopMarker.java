package br.cta.ipev.h125.replay;

public class ReplayTopMarker {

    public int frameIndex;

    public int topValue;

    public double time;

    public ReplayTopMarker(int frameIndex, int top, double time) {
        this.time = time;
        this.topValue = top;
        this.frameIndex = frameIndex;
    }

    @Override
    public String toString() {
        return ("TOP " + topValue);

    }

}
