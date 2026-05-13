package br.cta.ipev.h125.charts;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;

import com.scichart.charting.model.AnnotationCollection;
import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.annotations.HorizontalLineAnnotation;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.data.model.DoubleRange;
import com.scichart.drawing.common.PenStyle;
import com.scichart.drawing.common.SolidPenStyle;
import com.scichart.extensions.builders.SciChartBuilder;

import java.util.Collections;

public abstract class iStripChart {
    private static final String TAG = "iStripChart";
    protected SciChartBuilder builder;
    protected IXyDataSeries<Double,Double> dataSeries;
    protected DoubleRange yAxisRange;
    private SciChartSurface surface;
    protected HorizontalLineAnnotation highLimit, lowLimit;
    private Context context;
    private AnnotationCollection annotationCollection;

    protected abstract IAxis createXAxis();
    protected abstract IAxis createYAxis();
    public abstract void plot(Double x, Double y);

    public iStripChart(Context context) {
        this.context = context;
        Log.d("", "criando chars");
        createChart();
    }


    public iStripChart(Context context, SolidPenStyle penStyle) {
        this.context = context;
        createChart();
        createLimits(penStyle);
    }

    public iStripChart(Context context, DoubleRange yAxisRange, SolidPenStyle penStyle) {
        this.context = context;
        this.yAxisRange = yAxisRange;
        createChart();
        createLimits(penStyle);
    }

    public void setyAxisRange(DoubleRange yAxisRange) {
        this.yAxisRange.setMinMax(yAxisRange.getMin(),yAxisRange.getMax());
    }

    private void createChart(){
        try {
            SciChartSurface.setRuntimeLicenseKey("ViNyzERxPQM4lb1CB+UmHj3aXhNzxR+Z7+G4FkSWoMcMtu4LvgFcURYXPPLh6a0WWTpCIbaQ0WBC5ygzirvnKUb+AezEW3xiknIClX9A0MI65MN3VMvuNUlOtzGArZVTr9Dknls2RYBd0lTt4gDC6oo1CU4N73aX2pRPmj9pEICzmHeYyoicNQcswijV0KUyOtSXUXgB2OU5ND2t6LzF3zS/p3YYaYXYpV7nCgqReXCfa1bj8P16gFwIf42oTiZDoR6188586f+mD4Muy3jCuD4sV6iN3y5PDtuyiOq6CG2dWBTskPOria36VjFuVwqDMDs1reOC82/fEWP9j6KZnKmaC4g3uU92Pfh54U3+IWD9jTaLW9/4S6cMqkvg2lhFuxpPJmPeTMWJk751Jcv54xelk4zT2xviaNryt7Sr6PA0YYrZCc4Y16GgHiOXZqJDFUEVvrQKusWi7rR8uJiVZe9uXjWfXdGqazHgmzfgdKw3Tg4ybKDKEEgVWa8xfWEQyUtUD65iRcMmz2U+nlL3f64E6YcWNDvBDdSl4I50qCJqcZid3xosHSswXHUUiwolGMD10i2LII1x3dJdd9j5egpqIvw=");
            surface = new SciChartSurface(this.context);
        }
        catch(Exception e){
            Log.e("SciChart", "Error when setting the license ", e);
        }
    }




    protected void createAxis() {
        IAxis xAxis = createXAxis();
        IAxis yAxis = createYAxis();
        IRenderableSeries renderableSeries = builder.newLineSeries().withDataSeries(dataSeries).withStrokeStyle(0xFF4083B7,2f,true).build();
        updateAxis(xAxis,yAxis,renderableSeries);
    }

    public void addSeries(IXyDataSeries<Double,Double> series, PenStyle penStyle){
        IRenderableSeries rendSeries = builder.newLineSeries().withDataSeries(series).withStrokeStyle(penStyle).build();
        rendSeries.setDataSeries(series);
        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getRenderableSeries(),rendSeries);
            }
        });
    }

    public void addSeries(IRenderableSeries series){
        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getRenderableSeries(),series);
            }
        });
    }

    public SciChartSurface getSurface() {
        return surface;
    }

    private void createLimits(SolidPenStyle limitsStyle){
        highLimit = new HorizontalLineAnnotation(this.context);
        highLimit.setStroke(limitsStyle);
        highLimit.setIsEditable(false);
        highLimit.setHorizontalGravity(Gravity.FILL_HORIZONTAL);

        lowLimit = new HorizontalLineAnnotation(this.context);
        lowLimit.setStroke(limitsStyle);
        lowLimit.setIsEditable(false);
        lowLimit.setHorizontalGravity(Gravity.FILL_HORIZONTAL);


        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getAnnotations(),highLimit);
                Collections.addAll(surface.getAnnotations(),lowLimit);
            }
        });




        highLimit.hide();
        lowLimit.hide();
    }

    public void updateAxis(IAxis xAxis, IAxis yAxis, IRenderableSeries renderableSeries){
        UpdateSuspender.using(surface, new Runnable() {
            @Override
            public void run() {
                Collections.addAll(surface.getXAxes(),xAxis);
                Collections.addAll(surface.getYAxes(),yAxis);
                Collections.addAll(surface.getRenderableSeries(),renderableSeries);
            }
        });
    }

    public void build(){
        SciChartBuilder.init(this.context);
        builder = SciChartBuilder.instance();
        dataSeries = builder.newXyDataSeries(Double.class,Double.class).build();
        surface.setPadding(16,16,16,16);
        createAxis();
    }

    public XyDataSeries<Double,Double> createSeries(boolean unsortedData){
        XyDataSeries<Double,Double> series = builder.newXyDataSeries(Double.class,Double.class).build();
        series.setAcceptsUnsortedData(unsortedData);
        return series;
    }

    public void clear(){
        if(dataSeries.getCount()>0){
            dataSeries.clear();
            this.removeLimit();
        }
    }

    public void plotLimit(Double ref, Double yHigh, Double yLow){

        if(yHigh>0)
            plotLimitHigh(ref, yHigh);
        else {
            highLimit.hide();
        }
        if(yLow >0)
            plotLimitLow(ref, yLow);
        else{
            lowLimit.hide();
        }

    }


    public void plotLimit2(Double ref, Double yHigh, Double yLow){

        if(yHigh>0)
            plotLimitHigh2(ref, yHigh);
        else {
            highLimit.hide();
        }
        if(yLow >0)
            plotLimitLow2(ref, yLow);
        else{
            lowLimit.hide();
        }

    }

    public void plotLimitHigh(Double ref, Double yHigh){
        highLimit.setY1(ref + yHigh );
        highLimit.show();
        this.yAxisRange.setMax((ref+yHigh) * 1.01);
    }

    public void plotLimitLow(Double ref, Double yLow){
        lowLimit.setY1(ref-yLow);
        lowLimit.show();
        this.yAxisRange.setMin((ref-yLow) * 0.99);


    }

    public void plotLimitHigh2(Double ref, Double yHigh){
        highLimit.setY1(ref + yHigh );
        highLimit.show();
        this.yAxisRange.setMax((ref+yHigh) * 1.03);
    }

    public void plotLimitLow2(Double ref, Double yLow){
        lowLimit.setY1(ref-yLow);
        lowLimit.show();
        this.yAxisRange.setMin((ref-yLow) * 0.97);


    }

    public void removeLimit(){
        getSurface().getAnnotations().get(0).hide();
        getSurface().getAnnotations().get(1).hide();
        //highLimit.hide();
        //lowLimit.hide();
        Log.d(TAG, "removeLimit: ");
    }
}
