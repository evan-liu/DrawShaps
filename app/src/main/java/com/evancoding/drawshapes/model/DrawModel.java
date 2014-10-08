package com.evancoding.drawshapes.model;

import android.graphics.Bitmap;

import java.util.ArrayList;

import com.evancoding.drawshapes.shapes.Shape;

public class DrawModel {
    //==============================================================================================
    // Constructor
    //==============================================================================================
    public DrawModel(int[] colors) {
        this.colors = colors;
        color = colors[0];
    }

    //==============================================================================================
    // Color
    //==============================================================================================

    //--------------------------------------------
    // colors
    //--------------------------------------------
    private final int[] colors;
    public int[] getColors() {
        return colors;
    }

    //--------------------------------------------
    // OnColorChangeListener
    //--------------------------------------------
    public static interface OnColorChangeListener {
        void onColorChanged(int color);
    }
    private ArrayList<OnColorChangeListener> colorChangedListeners = new ArrayList<OnColorChangeListener>();
    public void setOnColorChangeListener(OnColorChangeListener listener) {
        colorChangedListeners.add(listener);
    }

    //--------------------------------------------
    // color
    //--------------------------------------------
    private int color;
    public int getColor() {
        return color;
    }
    public void setColor(int color) {
        if (color != this.color) {
            this.color = color;
            for (OnColorChangeListener listener : colorChangedListeners) {
                listener.onColorChanged(color);
            }
        }
    }

    //==============================================================================================
    // Shape
    //==============================================================================================

    //--------------------------------------------
    // shapes
    //--------------------------------------------
    private final Shape[] shapes = Shape.values();
    public Shape[] getShapes() {
        return shapes;
    }

    //--------------------------------------------
    // OnShapeChangeListener
    //--------------------------------------------
    public static interface OnShapeChangeListener {
        void onShapeChanged(Shape shape);
    }
    private ArrayList<OnShapeChangeListener> shapeChangedListeners = new ArrayList<OnShapeChangeListener>();
    public void setOnShapeChangeListener(OnShapeChangeListener listener) {
        shapeChangedListeners.add(listener);
    }

    //--------------------------------------------
    // shape
    //--------------------------------------------
    private Shape shape = shapes[0];
    public Shape getShape() {
        return shape;
    }
    public void setShape(Shape shape) {
        if (shape != this.shape) {
            this.shape = shape;
            for (OnShapeChangeListener listener : shapeChangedListeners) {
                listener.onShapeChanged(shape);
            }
        }

    }

    //==============================================================================================
    // Bitmap
    //==============================================================================================
    //--------------------------------------------
    // bitmap
    //--------------------------------------------
    private int bitmapBackground;

    private Bitmap bitmap;
    public Bitmap getBitmap() {
        return bitmap;
    }

    public void createBitmap(int width, int height, int background) {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmapBackground = background;
        resetBitmap();
    }

    public void resetBitmap() {
        bitmap.eraseColor(bitmapBackground);
    }

    //--------------------------------------------
    // Undo / Redo
    //--------------------------------------------
    private static final int UNDO_MAX = 5;

    private ArrayList<Bitmap> undoList = new ArrayList<Bitmap>();
    private ArrayList<Bitmap> redoList = new ArrayList<Bitmap>();

    public boolean hasUndo() {
        return undoList.size() > 0;
    }
    public boolean hasRedo() {
        return redoList.size() > 0;
    }

    public void markUndo() {
        if (undoList.size() == UNDO_MAX) {
            undoList.remove(0);
        }
        undoList.add(bitmap.copy(bitmap.getConfig(), true));
        if (redoList.size() > 0) {
            redoList.clear();
        }
    }
    public Bitmap undo() {
        if (hasUndo()) {
            redoList.add(bitmap);
            bitmap = undoList.remove(undoList.size() - 1);
        }
        return bitmap;
    }
    public Bitmap redo() {
        if (hasRedo()) {
            undoList.add(bitmap);
            bitmap = redoList.remove(redoList.size() - 1);
        }
        return bitmap;
    }
}