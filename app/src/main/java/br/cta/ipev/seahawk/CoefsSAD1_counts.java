package br.cta.ipev.seahawk;

import br.cta.isad.EV;
import br.cta.isad.iCounts2UE;

public class CoefsSAD1_counts extends CoefsSAD implements iCounts2UE {



    public static final int TOP_HI = 14 - OFFSET_IENA;
    public static final int TOP_LO = 15 - OFFSET_IENA;

    public static final int TCG102C_0_J3_HI_TI = 19 - OFFSET_IENA;
    public static final int TCG102C_0_J3_LO_TI = 26 - OFFSET_IENA;
    public static final int TCG102C_0_J3_MI_TI=  27 - OFFSET_IENA;

    public static final int DDC = 7 - OFFSET_IENA;

    public static final int DDL = 8 - OFFSET_IENA;

    public static final int DDN = 10 - OFFSET_IENA;

    public static final int DDM = 9 - OFFSET_IENA;


    public static final int NX_1 = 36 - OFFSET_IENA;

    public static final int NX_2 = 37 - OFFSET_IENA;

    public static final int NY_1 = 38 - OFFSET_IENA;

    public static final int NY_2 = 39 - OFFSET_IENA;

    public static final int NZ_1 = 40 - OFFSET_IENA;

    public static final int NZ_2 = 41 - OFFSET_IENA;




    @Override
    public double[] convert(char[] counts) {
        double[] result = new double[Index.values().length];



        //TEMPO
        result[Index.TEMPO.ordinal()] = EV.sadtime2secs(0xffff & counts[TCG102C_0_J3_HI_TI], 0xffff & counts[TCG102C_0_J3_LO_TI], 0xffff & counts[TCG102C_0_J3_MI_TI]);
        // TOP
        result[Index.TOP.ordinal()] = counts[TOP_LO];

        //DDL
        result[Index.DDL.ordinal()] = counts[DDL];

        //DDM
        result[Index.DDM.ordinal()] = counts[DDM];

        //DDN
        result[Index.DDN.ordinal()] = counts[DDN];

        //DDC
        result[Index.DDC.ordinal()] = counts[DDC];

        result[Index.NX_1.ordinal()] = counts[NX_1];
        result[Index.NX_2.ordinal()] = counts[NX_2];
        result[Index.NY_1.ordinal()] = counts[NY_1];
        result[Index.NY_2.ordinal()] = counts[NY_2];
        result[Index.NZ_1.ordinal()] = counts[NZ_1];
        result[Index.NZ_2.ordinal()] = counts[NZ_2];


        this._currentCVT = result;
        return this._currentCVT;
    }



}
