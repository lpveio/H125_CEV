package br.cta.ipev.h125.classes;

import br.cta.ipev.h125.setup.Index;
import br.cta.isad.EV;
import br.cta.isad.iCounts2UE;

public class CoefsSAD1_counts extends CoefsSAD implements iCounts2UE {


    public static final int TOP_HI = 14 - OFFSET_IENA;
    public static final int TOP_LO = 58 - OFFSET_IENA;

    public static final int TCG102C_0_J3_HI_TI = 11 - OFFSET_IENA;
    public static final int TCG102C_0_J3_LO_TI = 12 - OFFSET_IENA;
    public static final int TCG102C_0_J3_MI_TI=  13 - OFFSET_IENA;

    public static final int DDC = 7 - OFFSET_IENA;

    public static final int DDL = 8 - OFFSET_IENA;

    public static final int DDN = 10 - OFFSET_IENA;

    public static final int DDM = 9 - OFFSET_IENA;


    public static final int DOWN_VEL_0 = 22 - OFFSET_IENA;

    public static final int DOWN_VEL_1 = 23 - OFFSET_IENA;

    public static final int DOWN_VEL_2 = 24 - OFFSET_IENA;

    public static final int DOWN_VEL_3 = 25 - OFFSET_IENA;

    public static final int EAST_VEL_0 = 18 - OFFSET_IENA;

    public static final int EAST_VEL_1 = 19 - OFFSET_IENA;

    public static final int EAST_VEL_2 = 20 - OFFSET_IENA;

    public static final int EAST_VEL_3 = 21 - OFFSET_IENA;

    public static final int NORTH_VEL_0 = 14 - OFFSET_IENA;

    public static final int NORTH_VEL_1 = 15 - OFFSET_IENA;

    public static final int NORTH_VEL_2 = 16 - OFFSET_IENA;

    public static final int NORTH_VEL_3 = 17 - OFFSET_IENA;

    public static final int PB = 56 - OFFSET_IENA;

    public static final int QB = 57 - OFFSET_IENA;

    public static final int X_0 = 28 - OFFSET_IENA;

    public static final int X_1 = 29 - OFFSET_IENA;

    public static final int X_2 = 30 - OFFSET_IENA;

    public static final int X_3 = 31 - OFFSET_IENA;

    public static final int Y_0 = 32 - OFFSET_IENA;

    public static final int Y_1 = 33 - OFFSET_IENA;

    public static final int Y_2 = 34 - OFFSET_IENA;

    public static final int Y_3 = 35 - OFFSET_IENA;

    public static final int Z_0 = 36 - OFFSET_IENA;

    public static final int Z_1 = 37 - OFFSET_IENA;

    public static final int Z_2 = 38 - OFFSET_IENA;

    public static final int Z_3 = 39 - OFFSET_IENA;

    public static final int PITCH_0 = 46 - OFFSET_IENA;

    public static final int PITCH_1 = 47 - OFFSET_IENA;

    public static final int PITCH_2 = 48 - OFFSET_IENA;

    public static final int PITCH_3 = 49 - OFFSET_IENA;

    public static final int ROLL_0 = 42 - OFFSET_IENA;

    public static final int ROLL_1 = 43 - OFFSET_IENA;

    public static final int ROLL_2 = 44 - OFFSET_IENA;

    public static final int ROLL_3 = 45 - OFFSET_IENA;

    public static final int YAW_0 = 50 - OFFSET_IENA;

    public static final int YAW_1 = 51 - OFFSET_IENA;

    public static final int YAW_2 = 52 - OFFSET_IENA;

    public static final int YAW_3 = 53 - OFFSET_IENA;


    @Override
    public double[] convert(char[] counts) {
        double[] result = new double[Index.values().length];


        //TEMPO
        result[Index.TEMPO.ordinal()] = EV.sadtime2secs(0xffff & counts[TCG102C_0_J3_HI_TI], 0xffff & counts[TCG102C_0_J3_LO_TI], 0xffff & counts[TCG102C_0_J3_MI_TI]);
        // TOP
        result[Index.TOP.ordinal()] = counts[TOP_LO];

        //DDL
        //result[Index.DDL.ordinal()] = counts[DDL];

        //DDM
        //result[Index.DDM.ordinal()] = counts[DDM];

        //DDN
        //result[Index.DDN.ordinal()] = counts[DDN];

        //DDC
        //result[Index.DDC.ordinal()] = counts[DDC];

        //DOWN
        /*
        result[Index.VZ_LORD.ordinal()] = Conversion.lordFloat(counts[DOWN_VEL_0], counts[DOWN_VEL_1], counts[DOWN_VEL_2], counts[DOWN_VEL_3]);

        //EAST
        result[Index.EAST.ordinal()] = Conversion.lordFloat(counts[EAST_VEL_0], counts[EAST_VEL_1], counts[EAST_VEL_2],counts[EAST_VEL_3]);

        //NORTH
        result[Index.NORTH.ordinal()] = Conversion.lordFloat(counts[NORTH_VEL_0], counts[NORTH_VEL_1], counts[NORTH_VEL_2], counts[NORTH_VEL_3]);

        //PB
        result[Index.PB.ordinal()] = counts[PB];

        //QB
        result[Index.QB.ordinal()] = counts[QB];

        //X
        result[Index.X.ordinal()] = Conversion.lordFloat(counts[X_0], counts[X_1], counts[X_2], counts[X_3]);

        //Y
        result[Index.Y.ordinal()] = Conversion.lordFloat(counts[Y_0], counts[Y_1], counts[Y_2], counts[Y_3]);

        //Z
        result[Index.Z.ordinal()] = Conversion.lordFloat(counts[Z_0], counts[Z_1],  counts[Z_2],  counts[Z_3]);

        //PITCH
        result[Index.PITCH.ordinal()] = Conversion.lordFloat(counts[PITCH_0], counts[PITCH_1], counts[PITCH_2], counts[PITCH_3]);

        //ROLL
        result[Index.ROLL.ordinal()] = Conversion.lordFloat(counts[ROLL_0], counts[ROLL_1], counts[ROLL_2], counts[ROLL_3]);

        //YAW
        result[Index.YAW.ordinal()] = Conversion.lordFloat(counts[YAW_0], counts[YAW_1], counts[YAW_2], counts[YAW_3]);


         */

        this._currentCVT = result;
        return this._currentCVT;
    }





}
