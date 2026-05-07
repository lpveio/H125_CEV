package br.cta.ipev.h125.charts;

import com.scichart.data.model.DoubleRange;

import br.cta.ipev.commom.acquisition.Parameter;

abstract class ChartRef implements iChartReference {
    protected Parameter ref;
    protected DoubleRange range;
    protected double deltaRange = 3.0;

    public ChartRef(Parameter ref) {
        this.ref = ref;
        this.range = new DoubleRange(ref.getValue() - deltaRange, ref.getValue() + deltaRange);

    }

    @Override
    public DoubleRange getRange() {
        return this.range;
    }

    @Override
    public Parameter getParameter() {
        return this.ref;
    }
}
