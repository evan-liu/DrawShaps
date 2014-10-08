package com.evancoding.drawshapes.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import com.evancoding.drawshapes.model.DrawModel;
import com.evancoding.drawshapes.shapes.Shape;
import com.evancoding.drawshapes.utils.Painter;

public class DrawingShapeView extends View {
    private DrawModel model;
    private Painter painter;

    public DrawingShapeView(Context context) {
        super(context);
    }

    public DrawingShapeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawingShapeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setup(DrawModel model, Painter painter) {
        this.model = model;
        this.painter = painter;
        invalidate();

        model.setOnColorChangeListener(new DrawModel.OnColorChangeListener() {
            @Override
            public void onColorChanged(int color) {
                invalidate();
            }
        });
        model.setOnShapeChangeListener(new DrawModel.OnShapeChangeListener() {
            @Override
            public void onShapeChanged(Shape shape) {
                invalidate();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int size = (int) (getHeight() * 0.7);
        int x = getWidth() / 2;
        int y = getHeight() / 2;
        model.getShape().getDrawer().draw(x, y, Math.PI * 1.5, size, canvas,
                painter.getStrokePaint(Color.GRAY), painter.getFillPaint(model.getColor()));
    }
}
