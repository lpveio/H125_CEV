package br.cta.ipev.h125.charts;

import android.content.Context;
import android.util.Log;

import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.data.model.DateRange;
import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.common.SolidPenStyle;

import java.util.Calendar;
import java.util.Date;

public class StripChart extends iStripChart{
    private static final String TAG = "iStripChart";
    protected int xAxisVisibleRangeInSecs;
    protected DateRange xVis;


    public StripChart(Context context, int xAxisVisibleRangeInSecs) {
        super(context);
        this.xAxisVisibleRangeInSecs = xAxisVisibleRangeInSecs;
    }

    public StripChart(Context context, SolidPenStyle penStyle, int xAxisVisibleRangeInSecs) {
        super(context, penStyle);
        this.xAxisVisibleRangeInSecs = xAxisVisibleRangeInSecs;
    }

    public StripChart(Context context, DoubleRange yAxisRange, SolidPenStyle penStyle, int xAxisVisibleRangeInSecs) {
        super(context, yAxisRange, penStyle);
        this.xAxisVisibleRangeInSecs = xAxisVisibleRangeInSecs;
    }



    @Override
    protected IAxis createXAxis() {
        long now = System.currentTimeMillis();
        Date dateMin, dateMax;

        dateMin = new Date(now);
        dateMax = new Date(now + (xAxisVisibleRangeInSecs*1000));

        xVis = new DateRange(dateMin,dateMax);
        IAxis axis = builder.newDateAxis().withVisibleRange(xVis).withAutoRangeMode(AutoRange.Never).build();
        axis.setTextFormatting("H:mm:ss");
        return axis;
    }

    @Override
    protected IAxis createYAxis() {
        if(yAxisRange != null) {
            return (builder.newNumericAxis().withGrowBy(0.1d, 0.1d).withVisibleRange(yAxisRange).build());
        }
        return (builder.newNumericAxis().withGrowBy(0.1d,0.1d).withAutoRangeMode(AutoRange.Always).build());

    }

    @Override
    public void plot(Double x, Double y) {
        try {
            long timeinMilis = getDateMilis(x);
            Log.d(TAG, "now: " + timeinMilis + "| until: " + xVis.getMax().getTime());
            if (dataSeries.getCount() == 0) {
                xVis.setMinMax(new Date(timeinMilis), new Date(timeinMilis + (xAxisVisibleRangeInSecs * 1000)));
            }
            dataSeries.append((double) timeinMilis, y);
            if (timeinMilis > xVis.getMax().getTime()) {
                long min = dataSeries.getXValues().get(1).longValue();
                xVis.setMinMax(new Date(min), new Date(min + (xAxisVisibleRangeInSecs * 1000)));
                dataSeries.removeAt(0);
            }
        }
        catch (Exception err){
            Log.e("SCICHART",err.getMessage());
        }
    }

    protected long getDateMilis(double utc){
        int dia = (int)(utc/(24*3600));
        int tempo = (int)(utc - (dia*24*3600));
        int hora = (int)(tempo/3600);
        int minuto = (int) ((tempo%3600)/60);
        int segundo = (int) ((tempo%3600)%60);
        int milissegundo = (int)((utc - ((int)utc))*1000);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hora);
        cal.set(Calendar.MINUTE, minuto);
        cal.set(Calendar.SECOND, segundo);
        cal.set(Calendar.MILLISECOND, milissegundo);
        return cal.getTimeInMillis();
    }
}
