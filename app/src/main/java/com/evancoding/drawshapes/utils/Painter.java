package com.evancoding.drawshapes.utils;

import android.graphics.Color;
import android.graphics.Paint;

public class Painter {
    private Paint strokePaint;
    private Paint fillPaint;

    public Paint getStrokePaint() {
        return getStrokePaint(Color.BLACK, 0);
    }
    public Paint getStrokePaint(int color) {
        return getStrokePaint(color, 0);
    }

    public Paint getStrokePaint(int color, int width) {
        if (strokePaint == null) {
            strokePaint = new Paint();
            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setAntiAlias(true);
        }
        strokePaint.setColor(color);
        strokePaint.setStrokeWidth(width);
        return strokePaint;
    }

    public Paint getFillPaint(int color) {
        if (fillPaint == null) {
            fillPaint = new Paint();
            fillPaint.setStyle(Paint.Style.FILL);
            fillPaint.setAntiAlias(true);
        }
        fillPaint.setColor(color);
        return fillPaint;
    }
}