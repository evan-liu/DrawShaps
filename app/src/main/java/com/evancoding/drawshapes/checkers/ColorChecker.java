package com.evancoding.drawshapes.checkers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import com.evancoding.drawshapes.utils.Painter;
import com.evancoding.drawshapes.shapes.Shape;
import com.evancoding.drawshapes.shapes.ShapeDrawer;

public class ColorChecker extends CheckerView {

    private Painter painter;

    public ColorChecker(Context context, int color, Painter painter) {
        super(context);
        this.color = color;
        this.painter = painter;
    }

    private int color;

    public int getColor() {
        return color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        ShapeDrawer drawer = Shape.Circle.getDrawer();
        int x = getWidth() / 2;
        int y = getHeight() / 2;
        int size = (int) (getWidth() * 0.8);

        if (checked) {
            drawer.draw(x, y, 0, size, canvas, painter.getStrokePaint(Color.DKGRAY, 3), null);
            drawer.draw(x, y, 0, (int) (size * 0.7), canvas, null, painter.getFillPaint(color));
        } else {
            drawer.draw(x, y, 0, size, canvas, painter.getStrokePaint(Color.GRAY), painter.getFillPaint(color));
        }
    }

}