package br.cta.ipev.h125.gpsstatus;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import br.cta.ipev.h125.gpsstatus.datapoints.DataPointBeidou;
import br.cta.ipev.h125.gpsstatus.datapoints.DataPointGPS;
import br.cta.ipev.h125.gpsstatus.datapoints.DataPointGalileo;
import br.cta.ipev.h125.gpsstatus.datapoints.DataPointGlonass;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes.dex */
public class SkyPlotView extends View {
    private final int MARGIN;
    private HashMap<String, DataPointGPS> dataPoints;
    private HashMap<String, DataPointBeidou> dataPointsBeidou;
    private HashMap<String, DataPointGalileo> dataPointsGalileo;
    private HashMap<String, DataPointGlonass> dataPointsGlonass;
    private SkyPlotGrid skyPlotGrid;

    public SkyPlotView(Context context) {
        super(context);
        this.skyPlotGrid = new SkyPlotGrid();
        this.dataPoints = new HashMap<>();
        this.dataPointsGlonass = new HashMap<>();
        this.dataPointsGalileo = new HashMap<>();
        this.dataPointsBeidou = new HashMap<>();
        this.MARGIN = 80;
        init();
    }

    public SkyPlotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.skyPlotGrid = new SkyPlotGrid();
        this.dataPoints = new HashMap<>();
        this.dataPointsGlonass = new HashMap<>();
        this.dataPointsGalileo = new HashMap<>();
        this.dataPointsBeidou = new HashMap<>();
        this.MARGIN = 80;
        init();
    }

    public SkyPlotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.skyPlotGrid = new SkyPlotGrid();
        this.dataPoints = new HashMap<>();
        this.dataPointsGlonass = new HashMap<>();
        this.dataPointsGalileo = new HashMap<>();
        this.dataPointsBeidou = new HashMap<>();
        this.MARGIN = 80;
        init();
    }

    public SkyPlotView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.skyPlotGrid = new SkyPlotGrid();
        this.dataPoints = new HashMap<>();
        this.dataPointsGlonass = new HashMap<>();
        this.dataPointsGalileo = new HashMap<>();
        this.dataPointsBeidou = new HashMap<>();
        this.MARGIN = 80;
        init();
    }

    private void init() {
        this.skyPlotGrid.setMargin(80);
    }

    public void addDataPointGPS(int constellation, DataPointGPS dataPoint) {
        this.dataPoints.put(constellation + dataPoint.getId(), dataPoint);
        invalidate();
    }

    public void addDataPointGlonass(int constellation, DataPointGlonass dataPoint) {
        this.dataPointsGlonass.put(constellation + dataPoint.getId(), dataPoint);
        invalidate();
    }

    public void addDataPointGalileo(int constellation, DataPointGalileo dataPoint) {
        this.dataPointsGalileo.put(constellation + dataPoint.getId(), dataPoint);
        invalidate();
    }

    public void addDataPointBeidou(int constellation, DataPointBeidou dataPoint) {
        this.dataPointsBeidou.put(constellation + dataPoint.getId(), dataPoint);
        invalidate();
    }

    public DataPointGPS removeDataPoint(int constellation, String id) {
        DataPointGPS dataPointGPSRemove = this.dataPoints.remove(constellation + id);
        invalidate();
        return dataPointGPSRemove;
    }

    public DataPointGlonass removeDataPointGlonass(int constellation, String id) {
        DataPointGlonass dataPointGlonassRemove = this.dataPointsGlonass.remove(constellation + id);
        invalidate();
        return dataPointGlonassRemove;
    }

    public DataPointGalileo removeDataPointGalileo(int constellation, String id) {
        DataPointGalileo dataPointGalileoRemove = this.dataPointsGalileo.remove(constellation + id);
        invalidate();
        return dataPointGalileoRemove;
    }

    public DataPointBeidou removeDataPointBeidou(int constellation, String id) {
        DataPointBeidou dataPointBeidouRemove = this.dataPointsBeidou.remove(constellation + id);
        invalidate();
        return dataPointBeidouRemove;
    }

    public void removeAll() {
        this.dataPoints.clear();
        this.dataPointsGlonass.clear();
        this.dataPointsGalileo.clear();
        this.dataPointsBeidou.clear();
        invalidate();
    }

    public void removeGPS() {
        this.dataPoints.clear();
        invalidate();
    }

    public void removeGlonass() {
        this.dataPointsGlonass.clear();
        invalidate();
    }

    public void removeGalileo() {
        this.dataPointsGalileo.clear();
        invalidate();
    }

    public void removeBeidou() {
        this.dataPointsBeidou.clear();
        invalidate();
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.skyPlotGrid.drawGraph(canvas);
        Iterator<Map.Entry<String, DataPointGPS>> it = this.dataPoints.entrySet().iterator();
        while (it.hasNext()) {
            it.next().getValue().drawDataPoint(canvas, 80);
        }
        Iterator<Map.Entry<String, DataPointGlonass>> it2 = this.dataPointsGlonass.entrySet().iterator();
        while (it2.hasNext()) {
            it2.next().getValue().drawDataPoint(canvas, 80);
        }
        Iterator<Map.Entry<String, DataPointGalileo>> it3 = this.dataPointsGalileo.entrySet().iterator();
        while (it3.hasNext()) {
            it3.next().getValue().drawDataPoint(canvas, 80);
        }
        Iterator<Map.Entry<String, DataPointBeidou>> it4 = this.dataPointsBeidou.entrySet().iterator();
        while (it4.hasNext()) {
            it4.next().getValue().drawDataPoint(canvas, 80);
        }
    }
}