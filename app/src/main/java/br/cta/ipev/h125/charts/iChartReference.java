package br.cta.ipev.h125.charts;

import com.scichart.data.model.DoubleRange;

import br.cta.ipev.commom.acquisition.Parameter;

public interface iChartReference {
    public DoubleRange getRange();
    public Parameter getParameter();
}

