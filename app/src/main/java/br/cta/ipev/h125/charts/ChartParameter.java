package br.cta.ipev.h125.charts;

import br.cta.ipev.h125.setup.Index;

public enum ChartParameter {

    VI("Vi", Index.VI),

    ZPI("ZPi", Index.ZPI),

    RA("RA", Index.RALT),

    TRQ("TRQ", Index.TRQ),

    VZI("VZi", Index.VZI),

    PROA("PROA", Index.HDG_MAG),

    NR("NR", Index.NR);

    public final String label;

    public final Index valueIndex;

    ChartParameter(String label,
                   Index valueIndex) {

        this.label = label;
        this.valueIndex = valueIndex;
    }

    @Override
    public String toString() {
        return label;
    }
}