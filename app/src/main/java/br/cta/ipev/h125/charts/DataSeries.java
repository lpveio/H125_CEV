package br.cta.ipev.h125.charts;

import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.visuals.pointmarkers.IPointMarker;
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries;
import com.scichart.drawing.common.PenStyle;

public class DataSeries {
    private IRenderableSeries iRenderableSeries;
    private IXyDataSeries<Double,Double> dataSeries;

    public DataSeries(IRenderableSeries iRenderableSeries) {
        this.iRenderableSeries = iRenderableSeries;
        this.dataSeries = new XyDataSeries<>(Double.class,Double.class);
        this.iRenderableSeries.setDataSeries(this.dataSeries);

    }
    public DataSeries(IRenderableSeries iRenderableSeries,boolean unsorted) {
        this.iRenderableSeries = iRenderableSeries;
        this.dataSeries = new XyDataSeries<>(Double.class,Double.class);
        this.iRenderableSeries.setDataSeries(this.dataSeries);
        this.dataSeries.setAcceptsUnsortedData(unsorted);
    }

    public void setPointMarker(IPointMarker pointMarker) {
        this.iRenderableSeries.setPointMarker(pointMarker);
    }

    public void setPenStyle(PenStyle penStyle) {
        this.iRenderableSeries.setStrokeStyle(penStyle);
    }

    public void append(Double[]x, Double[]y){
        this.dataSeries.append(x,y);
    }

    public void append(Double x, Double y){
        this.dataSeries.append(x,y);
    }

    public void insert(int index,Double x, Double y){
        this.dataSeries.insert(index,x,y);
    }

    public void updateAt(int index, Double x, Double y){
        this.dataSeries.updateXyAt(index,x,y);
    }

    public IRenderableSeries getiRenderableSeries() {
        return iRenderableSeries;
    }

    public void clear(){
        if (this.dataSeries.getCount()>0)
            this.dataSeries.clear();
    }
}
