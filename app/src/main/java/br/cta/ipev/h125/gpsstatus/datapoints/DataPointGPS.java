package br.cta.ipev.h125.gpsstatus.datapoints;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import androidx.core.view.ViewCompat;

/* loaded from: classes.dex */
public class DataPointGPS {
    private float azimuth;
    private Paint backgroundPaint;
    private Paint borderPaint;
    private int borderWidth;
    private float elevation;
    private String id;
    private Rect rect;
    private Paint textPaint;
    int textSizePercentage = 50;
    private final float radius = 20.0f;

    private float calculateTextSize(Canvas canvas, int textSizePercentage) {
        if (textSizePercentage < 0 || textSizePercentage > 100) {
            textSizePercentage = 33;
        }
        return (textSizePercentage * 20.0f) / 50.0f;
    }

    public float scaleAndCenter(float value, float originValue, float scaleConstraint, float margin) {
        return originValue + ((((scaleConstraint / 2.0f) - margin) / 90.0f) * value);
    }

    public float getAzimuth() {
        return this.azimuth;
    }

    public float getElevation() {
        return this.elevation;
    }

    public String getId() {
        return this.id;
    }

    public void setValues(String id, float azimuth, float elevation) {
        this.id = id;
        this.azimuth = 180.0f - azimuth;
        this.elevation = elevation;
        initializePaints();
    }

    public float getX() {
        return elevationToPolar() * ((float) Math.sin(Math.toRadians(this.azimuth)));
    }

    public float getY() {
        return elevationToPolar() * ((float) Math.cos(Math.toRadians(this.azimuth)));
    }

    public float elevationToPolar() {
        return 90.0f - this.elevation;
    }

    public DataPointGPS(String id, float azimuth, float elevation) {
        setValues(id, azimuth, elevation);
        initializePaints();
    }

    private void initializePaints() {
        int color = Color.parseColor("#763c25");
        int color2 = Color.parseColor("#ffffff");
        Paint paint = new Paint();
        this.backgroundPaint = paint;
        paint.setAntiAlias(true);
        this.backgroundPaint.setColor(color);
        this.backgroundPaint.setStyle(Paint.Style.FILL);
        Paint paint2 = new Paint();
        this.borderPaint = paint2;
        paint2.setAntiAlias(true);
        this.borderPaint.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.borderPaint.setStyle(Paint.Style.STROKE);
        this.borderPaint.setStrokeWidth(this.borderWidth);
        Paint paint3 = new Paint();
        this.textPaint = paint3;
        paint3.setAntiAlias(true);
        this.textPaint.setColor(color2);
        this.textPaint.setTextSize(0.0f);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.textPaint.setTypeface(Typeface.create("sans-serif-light", 0));
        this.rect = new Rect();
    }

    public void setBackgroundColor(int color) {
        this.backgroundPaint.setColor(color);
    }

    public void drawDataPoint(Canvas canvas, int margin) {
        float width = canvas.getWidth();
        float height = canvas.getHeight();
        float f = width > height ? height : width;
        float f2 = margin;
        float fScaleAndCenter = scaleAndCenter(getX(), width / 2.0f, f, f2);
        float fScaleAndCenter2 = scaleAndCenter(getY(), height / 2.0f, f, f2);
        canvas.drawCircle(fScaleAndCenter, fScaleAndCenter2, 20.0f, this.backgroundPaint);
        int iRound = Math.round(20.0f) / 10;
        this.borderWidth = iRound;
        this.borderPaint.setStrokeWidth(iRound);
        canvas.drawCircle(fScaleAndCenter, fScaleAndCenter2, 20.0f, this.borderPaint);
        this.textPaint.setTextSize(calculateTextSize(canvas, this.textSizePercentage));
        Paint paint = this.textPaint;
        String str = this.id;
        paint.getTextBounds(str, 0, str.length(), this.rect);
        canvas.drawText(this.id, fScaleAndCenter, fScaleAndCenter2 + (Math.abs(this.rect.height()) / 2), this.textPaint);
    }
}