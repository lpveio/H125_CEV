package br.cta.ipev.h125.charts;

import android.content.Context;
import android.util.Log;

import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.common.SolidPenStyle;

public class StripChartInSecs  extends StripChart{
    private static final String TAG = "StripChartInSecs";
    private DoubleRange xVis;
    private double timeRef = 0;
    public StripChartInSecs(Context context, SolidPenStyle penStyle, int xAxisVisibleRangeInSecs) {
        super(context, penStyle, xAxisVisibleRangeInSecs);
    }

    public StripChartInSecs(Context context, DoubleRange yAxisRange, SolidPenStyle penStyle, int xAxisVisibleRangeInSecs) {
        super(context, yAxisRange, penStyle, xAxisVisibleRangeInSecs);
    }

    @Override
    protected IAxis createXAxis() {
        xVis = new DoubleRange(0d,(double)xAxisVisibleRangeInSecs);
        IAxis axis = builder.newNumericAxis().withVisibleRange(xVis).withAutoRangeMode(AutoRange.Never).build();;
        axis.setAutoTicks(false);
        axis.setMajorDelta(5d);
        axis.setMinorDelta(1d);

        return axis;
    }

    @Override
    public void build() {
        super.build();
        dataSeries.setAcceptsUnsortedData(true);

    }

    @Override
    public void plot(Double xTime, Double yValue) {
        try {
            if (timeRef == 0)
                timeRef = xTime;
            double time = xTime - timeRef;
            dataSeries.append(time, yValue);
            if (time > xVis.getMax()) {
                dataSeries.removeAt(0);
                double min = dataSeries.getXValues().get(0).doubleValue();
                xVis.setMinMax(min, min + xAxisVisibleRangeInSecs);

            }

            if ((this.yAxisRange!=null)){
                if (yValue > (this.yAxisRange.getMax())){
                    if (this.highLimit.isHidden())
                        this.yAxisRange.setMax(yValue);
                }
                if (yValue < (this.yAxisRange.getMin())){
                    if (this.lowLimit.isHidden())
                        this.yAxisRange.setMin(yValue);
                }
            }
        }
        catch (Exception err){
            Log.e(TAG,err.getMessage());
        }
    }

    @Override
    public void clear() {
        super.clear();
        this.timeRef = 0;
        xVis.setMinMax(timeRef, (double) xAxisVisibleRangeInSecs);
    }
}
