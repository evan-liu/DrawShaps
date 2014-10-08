package com.evancoding.drawshapes.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class SquareDrawer implements ShapeDrawer {

    @Override
    public void draw(int x, int y, double angle, int size, Canvas canvas, Paint strokePaint, Paint fillPaint) {
        double radius = size / 2 / Math.sin(Math.PI * 0.25);

        double[] as = {0.25, 0.75, 1.25, 1.75};
        int[] xs = new int[4];
        int[] ys = new int[4];
        for (int i = 0; i < 4; i++) {
            double a = Math.PI * as[i] + angle;
            xs[i] = (int) (radius * Math.cos(a));
            ys[i] = (int) (radius * Math.sin(a));
        }

        Path path = new Path();
        path.moveTo(x + xs[3], y + ys[3]);
        for (int j = 0; j < 4; j++) {
            path.lineTo(x + xs[j], y + ys[j]);
        }

        if (fillPaint != null) {
            canvas.drawPath(path, fillPaint);
        }

        if (strokePaint != null) {
            canvas.drawPath(path, strokePaint);
        }
    }

}
