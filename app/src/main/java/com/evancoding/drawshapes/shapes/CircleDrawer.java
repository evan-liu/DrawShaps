package com.evancoding.drawshapes.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;

public class CircleDrawer implements ShapeDrawer {

    @Override
    public void draw(int x, int y, double angle, int size, Canvas canvas, Paint strokePaint, Paint fillPaint) {
        float radius = size / 2;

        if (fillPaint != null) {
            canvas.drawCircle(x, y, radius, fillPaint);
        }
        if (strokePaint != null) {
            canvas.drawCircle(x, y, radius, strokePaint);
        }
    }

}
