package br.cta.ipev.h125.replay;

public class ReplayFrame {

    public double time;

    public double[] values;

    public ReplayFrame(double time, double[] values) {
        this.time = time;
        this.values = values;
    }
}
