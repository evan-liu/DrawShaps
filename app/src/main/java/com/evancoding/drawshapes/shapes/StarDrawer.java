package com.evancoding.drawshapes.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class StarDrawer implements ShapeDrawer {
    @Override
    public void draw(int x, int y, double angle, int size, Canvas canvas, Paint strokePaint, Paint fillPaint) {
        int radius = size / 2;

        int[] outX = new int[5];
        int[] outY = new int[5];
        for (int i = 0; i < 5; i++) {
            double pointAngle = angle + Math.PI * 0.4 * i;
            outX[i] = (int) (radius * Math.cos(pointAngle));
            outY[i] = (int) (radius * Math.sin(pointAngle));
        }

        int[] innerX = new int[5];
        int[] innerY = new int[5];
        int innerRadius = (int) (radius * 0.5);
        for (int j = 0; j < 5; j++) {
            double innerAngle = angle + Math.PI * 0.2 + Math.PI * 0.4 * j;
            innerX[j] = (int) (innerRadius * Math.cos(innerAngle));
            innerY[j] = (int) (innerRadius * Math.sin(innerAngle));
        }

        Path path = new Path();
        path.moveTo(x + outX[0], y + outY[0]);
        path.lineTo(x + innerX[0], y + innerY[0]);
        for (int k = 1; k < 5; k++) {
            path.lineTo(x + outX[k], y + outY[k]);
            path.lineTo(x + innerX[k], y + innerY[k]);
        }
        path.lineTo(x + outX[0], y + outY[0]);

        if (fillPaint != null) {
            canvas.drawPath(path, fillPaint);
        }

        if (strokePaint != null) {
            canvas.drawPath(path, strokePaint);
        }
    }
}
