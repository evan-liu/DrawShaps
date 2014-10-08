package com.evancoding.drawshapes.checkers;

import android.content.Context;
import android.graphics.Canvas;

import com.evancoding.drawshapes.R;
import com.evancoding.drawshapes.model.DrawModel;
import com.evancoding.drawshapes.shapes.Shape;
import com.evancoding.drawshapes.utils.Painter;

public class ShapeChecker extends CheckerView implements DrawModel.OnColorChangeListener {

    public ShapeChecker(Context context, Shape shape, Painter painter) {
        super(context);
        this.shape = shape;
        this.painter = painter;
    }

    private Painter painter;
    private Shape shape;

    public Shape getShape() {
        return shape;
    }

    private int color;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int size = (int) getResources().getDimension(R.dimen.tools_button_size);

        shape.getDrawer().draw(centerX, centerY, Math.PI * 1.5, size, canvas, painter.getStrokePaint(),
                checked ? painter.getFillPaint(color) : null);

    }

    @Override
    public void onColorChanged(int color) {
        this.color = color;
        if (checked) {
            invalidate();
        }
    }
}