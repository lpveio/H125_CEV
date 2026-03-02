package br.cta.ipev.seahawk;

public abstract class CoefsSAD {
    protected double[] _currentCVT = new double[Index.values().length];

    protected double E = 7.17E10;
    protected double v = 3.3E-1;

    public static final int OFFSET_IENA = 7;

    protected int mergeWords(int wHigh, int wLow){
        return ( (wHigh << 16) |  wLow);
    }

}
