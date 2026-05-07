package br.cta.ipev.h125.classes;

import br.cta.ipev.h125.setup.Index;
import br.cta.isad.iCounts2UE;
import br.cta.isad.EV;

public class CoefsSAD1 extends CoefsSAD implements iCounts2UE {

    public static int ARINCDataFieldSize = 19;

    public static final int TOP_LO = 234 - OFFSET_IENA;

    public static final int TCG102D_0_J4_HI_TI_0 = 7 - OFFSET_IENA;
    public static final int TCG102D_0_J4_LO_TI_0 = 8 - OFFSET_IENA;
    public static final int TCG102D_0_J4_MI_TI_0=  9 - OFFSET_IENA;

    public static final int LAT = 7 - OFFSET_IENA;

    public static final int LONG = 8 - OFFSET_IENA;

    public static final int ALT_MSL = 10 - OFFSET_IENA;

    public static final int GS_KN = 239 - OFFSET_IENA;


    public static final int GS_KPH = 240 - OFFSET_IENA;

    public static final int HDG = 23 - OFFSET_IENA;

    public static final int VERT_VEL = 23 - OFFSET_IENA;

    public static final int FLI = 146 - OFFSET_IENA;

    public static final int FF = 166 - OFFSET_IENA;

    public static final int AUW = 25 - OFFSET_IENA;

    public static final int NR = 168 - OFFSET_IENA;

    public static final int N1 = 174 - OFFSET_IENA;

    public static final int EAST_VEL_3 = 21 - OFFSET_IENA;

    public static final int FQTY = 178 - OFFSET_IENA;

    public static final int TOT = 182 - OFFSET_IENA;

    public static final int OIL_PRESS = 202 - OFFSET_IENA;

    public static final int TQ = 204 - OFFSET_IENA;

    public static final int N2 = 212 - OFFSET_IENA;

    public static final int RALT = 108 - OFFSET_IENA;

    public static final int ALT_203 = 20 - OFFSET_IENA;

    public static final int MEM = 224 - OFFSET_IENA;

    public static final int Alt_Rate_212 = 22 - OFFSET_IENA;

    public static final int CAS = 30 - OFFSET_IENA;

    public static final int IMP_PRESS = 34 - OFFSET_IENA;

    public static final int HDG_014 = 31 - OFFSET_IENA;

    public static final int P = 32 - OFFSET_IENA;

    public static final int Q = 33 - OFFSET_IENA;

    public static final int R = 34 - OFFSET_IENA;


    public static final int NX = 36 - OFFSET_IENA;

    public static final int NY = 37 - OFFSET_IENA;

    public static final int NZ = 38 - OFFSET_IENA;

    public static final int THETA = 74 - OFFSET_IENA;

    public static final int XPA = 237 - OFFSET_IENA;

    public static final int XPC = 154 - OFFSET_IENA;

    public static final int PHI = 52 - OFFSET_IENA;

    public static final int SAT = 60 - OFFSET_IENA;

    public static final int STT_PRESS = 64 - OFFSET_IENA;

    public static final int TAS = 66 - OFFSET_IENA;

    public static final int TOTAL_PRESS = 76 - OFFSET_IENA;

    public static final int HDG_MAG = 94 - OFFSET_IENA;

    public static final int HDG_TRUE = 44 - OFFSET_IENA;

    private int bitSinal = 1;





    @Override
    public double[] convert(char[] counts) {

        double[] result = new double[Index.values().length];
        double[] CV;
        int arincH125 = 0;
        int numBits = 0;
        double hdg;

        //TEMPO
        result[Index.TEMPO.ordinal()] = EV.sadtime2secs(0xffff & counts[TCG102D_0_J4_HI_TI_0], 0xffff & counts[TCG102D_0_J4_LO_TI_0], 0xffff & counts[TCG102D_0_J4_MI_TI_0]);
        // TOP
        result[Index.TOP.ordinal()] = counts[TOP_LO];

        // MEM
        result[Index.MEM.ordinal()] = counts[MEM];

        //XPA
        numBits = 17 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[XPA], counts[XPA + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.XPA.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.0004863 * 4;

        //XPC
        numBits = 17 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[XPC], counts[XPC + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.XPC.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.0004928 * 4;

        //LAT
        result[Index.LAT.ordinal()] =  Conversion.tcglong(counts[LAT]);

        //LONG
        result[Index.LONG.ordinal()] =  Conversion.tcglong(counts[LONG]);

        //ALT_MSL
        numBits = 19 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[ALT_MSL], counts[ALT_MSL + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.ALT_RATE.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.456;

        //GS_KN
        result[Index.GS_KN.ordinal()] =  Conversion.tcgvel(counts[GS_KN]);

        //HDG
        numBits = 17 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[HDG], counts[HDG + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.HDG.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.456 * 4;

        //FLI
        numBits = 17 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[FLI], counts[FLI + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.FLI.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.0000468 * 4;


        //AUW
        numBits = 17 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[AUW], counts[AUW + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.AUW.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.0312315* 4;

        //FF
        numBits = 17 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[FF], counts[FF + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.FF.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.0009838 * 4;

        //NR
        numBits = 17 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[NR], counts[NR + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.NR.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.0018808 * 4;

        //N1
        numBits = 17 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[N1], counts[N1 + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.N1.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.00048811 * 4;

        //FQTY
        numBits = 17 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[FQTY], counts[FQTY + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        double fuel = Conversion.TwosComplement(arincH125, numBits) * 0.0078 * 4;
        result[Index.FQTY.ordinal()] = fuel;

        //FQTY
        result[Index.FQTYP.ordinal()] = (fuel / 427 ) * 100;

        //TOT
        numBits = 17 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[TOT], counts[TOT + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.TOT.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.0039086 * 4;

        //OIL_PRESS
        numBits = 17 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[OIL_PRESS], counts[OIL_PRESS + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.OIL_PRESS.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.00007804 * 4;


        //TQ
        numBits = 17 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[TQ], counts[TQ + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.TQ.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.0004911 * 4;

        //N2
        numBits = 17 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[N2], counts[N2 + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.N2.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.001953 * 4;




        //Alt_203 (ZBP)
        numBits = 17 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[ALT_203], counts[ALT_203 + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.ZPB.ordinal()] = Conversion.TwosComplement(arincH125, numBits);

        //ZPBMax
        if (result[Index.ZPB.ordinal()] > ZPBMax) {
            ZPBMax = result[Index.ZPB.ordinal()];
        }
        result[Index.ZPBMax.ordinal()] = ZPBMax;

        //ZPBMin
        if (result[Index.ZPB.ordinal()] < ZPBMin) {
            ZPBMin = result[Index.ZPB.ordinal()];
        }
        result[Index.ZPBMin.ordinal()] = ZPBMin;

        //Alt_Rate_212
        numBits = 11 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[Alt_Rate_212], counts[Alt_Rate_212 + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.ALT_RATE.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 16;

        //CAS_206 ( VB)
        numBits = 14 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[CAS], counts[CAS + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.VB.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.0625;

        //VBMax
        if (result[Index.VB.ordinal()] > VBMax) {
            VBMax = result[Index.VB.ordinal()];
        }
        result[Index.VBMax.ordinal()] = VBMax;

        //VbMin
        if (result[Index.VB.ordinal()] < VBMin) {
            VBMin = result[Index.VB.ordinal()];
        }
        result[Index.VBMin.ordinal()] = VBMin;

        //HDG_014
        numBits = 19 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[HDG], counts[HDG + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.HDG_014.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 4;


        //Imp_Press_215
        numBits = 14 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[IMP_PRESS], counts[IMP_PRESS + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.IMP_PRESS.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.03125;


        //P
        numBits = 13 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[P], counts[P + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.P.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.015625;

        //Q
        numBits = 19 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[Q], counts[Q + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.Q.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.456;

        //R
        numBits = 19 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[R], counts[R + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.R.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.456;

        //VERT_VEL
        numBits = 19 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[VERT_VEL], counts[VERT_VEL + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.VERT_VEL.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.456;

        //NX
        numBits = 12 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[NX], counts[NX + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.NX.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.0009765625;

        //NY
        numBits = 12 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[NY], counts[NY + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.NY.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.0009765625;

        //NZ
        numBits = 12 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[NZ], counts[NZ + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.NZ.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.0009765625;

        //THETA
        numBits = 14 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[THETA], counts[THETA + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.THETA.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.010986328125;

        //PHI
        numBits = 14 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[PHI], counts[PHI + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.PHI.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.010986328125;

        //SAT
        numBits = 11 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[SAT], counts[SAT + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.SAT.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.25;

        //STT_PRESS
        numBits = 16 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[STT_PRESS], counts[STT_PRESS + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.STT_PRESS.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.0009765625;

        //TOTAL_PRESS
        numBits = 16 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[TOTAL_PRESS], counts[TOTAL_PRESS + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.TOTAL_PRESS.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.03125;

        //TAS
        numBits = 15 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[TAS], counts[TAS + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.TAS.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.0625;

        //HDG_MAG
        numBits = 15 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[HDG_MAG], counts[HDG_MAG + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        hdg = Conversion.TwosComplement(arincH125, numBits) * 0.0054931640625;
        hdg  = (hdg + 360) % 360;
        result[Index.HDG_MAG.ordinal()] = hdg;

        //HDG_TRUE
        numBits = 19 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[HDG_TRUE], counts[HDG_TRUE + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.HDG_TRUE.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.456;

        //RALT
        numBits = 16 + bitSinal;
        arincH125 = Conversion.extrairArincC105(mergeWords(counts[RALT], counts[RALT + 1]));
        arincH125 = arincH125 >> ( ARINCDataFieldSize - numBits);
        result[Index.RALT.ordinal()] = Conversion.TwosComplement(arincH125, numBits) * 0.125;

        this._currentCVT = result;
        return this._currentCVT;
    }


}
