package com.evancoding.drawshapes.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class TriangleDrawer implements ShapeDrawer {

    @Override
    public void draw(int x, int y, double angle, int size, Canvas canvas, Paint strokePaint, Paint fillPaint) {
        int radius = size / 2;
        double rA = radius;
        double rBC = radius / Math.sin(Math.PI * 0.25);

        double angleA = angle;
        int xA = (int) (rA * Math.cos(angleA));
        int yA = (int) (rA * Math.sin(angleA));

        double angleB = Math.PI * 0.75 + angle;
        int xB = (int) (rBC * Math.cos(angleB));
        int yB = (int) (rBC * Math.sin(angleB));

        double angleC = Math.PI * 1.25 + angle;
        int xC = (int) (rBC * Math.cos(angleC));
        int yC = (int) (rBC * Math.sin(angleC));

        Path path = new Path();
        path.moveTo(x + xA, y + yA);
        path.lineTo(x + xB, y + yB);
        path.lineTo(x + xC, y + yC);
        path.lineTo(x + xA, y + yA);

        if (fillPaint != null) {
            canvas.drawPath(path, fillPaint);
        }

        if (strokePaint != null) {
            canvas.drawPath(path, strokePaint);
        }
    }
}