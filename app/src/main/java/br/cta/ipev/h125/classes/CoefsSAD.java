package br.cta.ipev.h125.classes;


import br.cta.ipev.h125.setup.Index;
import br.cta.ipev.h125.setup.resetValues;

public abstract class CoefsSAD implements resetValues {

    protected double[] _currentCVT = new double[Index.values().length];

    public static  double VBMax = Double.NEGATIVE_INFINITY ;
    public static  double VBMin = Double.POSITIVE_INFINITY;
    public static  double ZPBMax = Double.NEGATIVE_INFINITY;
    public static  double ZPBMin = Double.POSITIVE_INFINITY;
    public static double RIMax;
    public static double RIMin;
    public static double FLIMax;
    public static double FLIMin;
    public static double N1Max;
    public static double N1Min;
    public static double TOTMax;
    public static double TOTMin;
    public static double TRQMax;
    public static double TRQMin;
    public static double N2Max;
    public static double N2Min;
    public static double NRMax;
    public static double NRMin;


    public static final int OFFSET_IENA = 7;

    protected int mergeWords(int wHigh, int wLow){
        return ( (wHigh << 16) |  wLow);
    }

    @Override
    public void resetVB_VZB() {
        VBMax = Double.NEGATIVE_INFINITY;;
        VBMin = Double.POSITIVE_INFINITY;;
        ZPBMax = Double.NEGATIVE_INFINITY;;
        ZPBMin = Double.POSITIVE_INFINITY;;
    }

    @Override
    public void resetDadosMotor() {

        NRMin = 0 ;
        NRMax = 0 ;
        N2Max = 0 ;
        N2Min = 0 ;
        TRQMax = 0 ;
        TRQMin = 0 ;
        TOTMax = 0 ;
        TOTMin = 0 ;
        N1Max = 0 ;
        N1Min = 0 ;
        FLIMax = 0 ;
        FLIMin = 0 ;
        RIMax = 0 ;
        RIMin = 0 ;

    }



}
