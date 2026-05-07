package br.cta.ipev.h125.charts;

import android.content.Context;

import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.common.SolidPenStyle;

public class XYChart extends StripChart {
    private boolean enableUnsortedData = false;
    public XYChart(Context context, int xAxisVisibleRangeInSecs) {
        super(context, xAxisVisibleRangeInSecs);
    }

    public XYChart(Context context, SolidPenStyle penStyle, int xAxisVisibleRangeInSecs) {
        super(context, penStyle, xAxisVisibleRangeInSecs);
    }

    public XYChart(Context context, DoubleRange yAxisRange, SolidPenStyle penStyle, int xAxisVisibleRangeInSecs) {
        super(context, yAxisRange, penStyle, xAxisVisibleRangeInSecs);
    }

    private void enableUnsortedData(){
        dataSeries.setAcceptsUnsortedData(true);
    }

    @Override
    public void build() {
        super.build();
        this.enableUnsortedData();
    }

    @Override
    protected IAxis createXAxis() {

        return builder.newNumericAxis().withGrowBy(0.1d,0.1d).withAutoRangeMode(AutoRange.Always).build();
    }

    @Override
    public void plot(Double x, Double y) {
        this.dataSeries.append(x,y);
    }
}
