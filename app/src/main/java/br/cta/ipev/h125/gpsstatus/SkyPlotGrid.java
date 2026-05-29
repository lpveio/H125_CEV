package br.cta.ipev.h125.gpsstatus;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

/* loaded from: classes.dex */
public class SkyPlotGrid {
    private float centerX;
    private float centerY;
    private final Paint paint;
    private float radius;
    private final Paint textPaint;
    private final Rect bounds = new Rect();
    private int margin = 0;

    public SkyPlotGrid() {
        Paint paint = new Paint();
        this.paint = paint;
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#2e99d5"));
        paint.setStyle(Paint.Style.STROKE);
        Paint paint2 = new Paint();
        this.textPaint = paint2;
        paint2.setAntiAlias(true);
        paint2.setColor(Color.parseColor("#585857"));
        paint2.setTextSize(30.0f);
        paint2.setTextAlign(Paint.Align.CENTER);
        paint2.setTypeface(Typeface.create("sans-serif-light", 1));
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public void drawGraph(Canvas canvas) {
        Rect rect = this.bounds;
        int i = this.margin;
        rect.set(i, i, canvas.getWidth() - this.margin, canvas.getHeight() - this.margin);
        this.centerX = this.bounds.centerX();
        float fCenterY = this.bounds.centerY();
        this.centerY = fCenterY;
        float f = this.centerX;
        if (f < fCenterY) {
            this.radius = f - this.margin;
        } else {
            this.radius = fCenterY - this.margin;
        }
        this.paint.setStrokeWidth(this.radius * 0.01f);
        drawCircles(canvas);
        drawLines(canvas);
    }

    private void drawCircles(Canvas canvas) {
        float f = this.radius / 3;
        for (int i = 1; i <= 3; i++) {
            canvas.drawCircle(this.centerX, this.centerY, i * f, this.paint);
        }
    }

    private void drawLines(Canvas canvas) {
        double radians = Math.toRadians(360.0d) / 12;
        double d = 0.0d;
        for (int i = 0; i < 12; i++) {
            float fCos = (float) (this.centerX + (this.radius * Math.cos(d)));
            float fSin = (float) (this.centerY + (this.radius * Math.sin(d)));
            canvas.drawLine(this.centerX, this.centerY, fCos, fSin, this.paint);
            if (i % 3 == 0) {
                drawLabel(canvas, (float) d, fCos, fSin);
            }
            d += radians;
        }
    }

    private void drawLabel(Canvas canvas, float radians, float startX, float startY) {
        double degrees = Math.toDegrees(radians) + 90.0d;
        if (degrees > 360.0d) {
            degrees -= 360.0d;
        }
        canvas.drawText(String.format("%.0fº", Double.valueOf(degrees)), startX, startY, this.textPaint);
    }
}