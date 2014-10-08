package com.evancoding.drawshapes.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;

public interface ShapeDrawer {

    /**
     * Draw the shape
     * @param x             Center x of the shape.
     * @param y             Center y of the shape.
     * @param angle         Rotated angle of the shape.
     * @param size          Size of the shape.
     * @param canvas        Canvas to draw on.
     * @param strokePaint   Paint to draw the stroke. Set to null if stroke is not needed.
     * @param fillPaint     Paint to draw the fill color. Set to null if fill color is not needed.
     */
    public void draw(int x, int y, double angle, int size, Canvas canvas, Paint strokePaint, Paint fillPaint);

}