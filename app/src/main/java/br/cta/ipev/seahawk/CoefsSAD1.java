package br.cta.ipev.seahawk;

import br.cta.isad.iCounts2UE;
import br.cta.isad.EV;

public class CoefsSAD1 extends CoefsSAD implements iCounts2UE {

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
        double[] CV;

        //TEMPO
        result[Index.TEMPO.ordinal()] = EV.sadtime2secs(0xffff & counts[TCG102C_0_J3_HI_TI], 0xffff & counts[TCG102C_0_J3_LO_TI], 0xffff & counts[TCG102C_0_J3_MI_TI]);
        // TOP
        result[Index.TOP.ordinal()] = counts[TOP_LO];

        //DDL
        CV = new double[]{2.785714E-10, 7.852175E-03, -1.090192E02};
        result[Index.DDL.ordinal()] = EV.polyval(CV,counts[DDL]);

        //DDM
        CV = new double[]{1.141098E-13, -2.461363E-09, 6.232157E-03, -2.560274E00 };
        result[Index.DDM.ordinal()] = EV.polyval(CV,counts[DDM]);

        //DDN
        CV = new double[]{6.938457E-12, -2.361242E-03, 1.186321E02};
        result[Index.DDN.ordinal()] = EV.polyval(CV,counts[DDN]);

        //DDC
        CV = new double[]{-1.618350E-10, 6.407209E-03, -1.681349E02};
        result[Index.DDC.ordinal()] = EV.polyval(CV,counts[DDC]);

        //NX_1
        CV = new double[]{2.337699E-18, -3.546667E-13, 1.935331E-08, 1.072171E-03, -4.639768E01};
        result[Index.NX_1.ordinal()] = EV.polyval(CV,counts[NX_1]);

        //NX_2
        CV = new double[]{2.337699E-18, -3.546667E-13, 1.935331E-08, 1.072171E-03, -4.639768E01};
        result[Index.NX_2.ordinal()] = EV.polyval(CV,counts[NX_2]);

        //NY_1
        CV = new double[]{2.337699E-18, -3.546667E-13, 1.935331E-08, 1.072171E-03, -4.639768E01};
        result[Index.NY_1.ordinal()] = EV.polyval(CV,counts[NY_1]);

        //NY_2
        CV = new double[]{2.337699E-18, -3.546667E-13, 1.935331E-08, 1.072171E-03, -4.639768E01};
        result[Index.NY_2.ordinal()] = EV.polyval(CV,counts[NY_2]);

        //NZ_1
        CV = new double[]{2.337699E-18, -3.546667E-13, 1.935331E-08, 1.072171E-03, -4.639768E01};
        result[Index.NZ_1.ordinal()] = EV.polyval(CV,counts[NZ_1]);

        //NZ_2
        CV = new double[]{2.337699E-18, -3.546667E-13, 1.935331E-08, 1.072171E-03, -4.639768E01};
        result[Index.NZ_2.ordinal()] = EV.polyval(CV,counts[NZ_2]);

        this._currentCVT = result;
        return this._currentCVT;
    }



}
