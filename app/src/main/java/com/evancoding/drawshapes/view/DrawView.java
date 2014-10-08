package com.evancoding.drawshapes.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.evancoding.drawshapes.R;
import com.evancoding.drawshapes.model.DrawModel;
import com.evancoding.drawshapes.utils.Painter;

public class DrawView extends View implements View.OnTouchListener {

    final Paint bitmapPaint = new Paint();
    private Canvas offScreenCanvas;

    private DrawModel model;
    private Painter shapePainter;

    //==============================================================================================
    // Constructor
    //==============================================================================================
    public DrawView(Context context) {
        super(context);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //==============================================================================================
    // API
    //==============================================================================================
    public void setup(DrawModel model, Painter painter, Activity activity) {
        this.model = model;
        this.shapePainter = painter;
        drawSize = (int) getResources().getDimension(R.dimen.draw_size);

        setOnTouchListener(this);
        setupUndoRedoButtons(activity);
        setupZoomAndMoveButtons(activity);
    }

    public void reset() {
        if (model.getBitmap() != null) {
            model.markUndo();
            model.resetBitmap();
            invalidate();

            checkUndoButtons();
        }
    }

    //==============================================================================================
    // Touch & Draw
    //==============================================================================================

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (model.getBitmap() != null) {
            if (model.getBitmap().getWidth() < w) {
                scale = (float) w / model.getBitmap().getWidth();
            } else if (model.getBitmap().getHeight() < h) {
                scale = (float) h / model.getBitmap().getHeight();
            }
        }

        moveStep = (w > h ? h : w) / 10;
        checkZoomAndMoveButtons();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap bitmap = model.getBitmap();
        if (bitmap == null) return;

        Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect dst = new Rect(0, 0, getWidth(), getHeight());

        float w = bitmap.getWidth() * scale;
        float h = bitmap.getHeight() * scale;

        if (w < getWidth()) {
            dst.left = offsetX;
            dst.right = (int) (dst.left + w);
        } else {
            src.left = (int) (-offsetX / scale);
            src.right = (int) (src.left + getWidth() / scale);
        }

        if (h < getHeight()) {
            dst.top = offsetY;
            dst.bottom = (int) (offsetY + h);
        } else {
            src.top = (int) (-offsetY / scale);
            src.bottom = (int) (src.top + getHeight() / scale);
        }

        canvas.drawBitmap(bitmap, src, dst, bitmapPaint);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            onTouchBegin(motionEvent);
        } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            onTouchMove(motionEvent);
        }
        invalidate();
        return true;
    }

    private void onTouchBegin(MotionEvent motionEvent) {
        if (model.getBitmap() == null) {
            model.createBitmap(getWidth(), getHeight(), getResources().getColor(R.color.background_draw));
            resetOffScreenCanvas();
            checkZoomAndMoveButtons();
        } else if (offScreenCanvas == null) {
            resetOffScreenCanvas();
        }
        markUndo();

        for (int i = 0; i < motionEvent.getPointerCount(); i++) {
            int x = (int) motionEvent.getX(i);
            int y = (int) motionEvent.getY(i);
            drawShape(x, y, Math.PI * 1.5, getDrawSizeCalculator(motionEvent).getDrawSize(motionEvent, i));
        }
    }

    private void onTouchMove(MotionEvent motionEvent) {
        for (int i = 0; i < motionEvent.getPointerCount(); i++) {
            int x = (int) motionEvent.getX(i);
            int y = (int) motionEvent.getY(i);
            double angle;
            if (motionEvent.getHistorySize() > 0) {
                int lastPos = motionEvent.getHistorySize() - 1;
                float lastX = motionEvent.getHistoricalX(i, lastPos);
                float lastY = motionEvent.getHistoricalY(i, lastPos);
                if (Math.abs(x - lastX) < 1 && Math.abs(y - lastY) < 1) {
                    continue;
                }
                angle = Math.atan2(y - lastY, x - lastX);
            } else {
                angle = Math.PI * 1.5;
            }
            drawShape(x, y, angle, getDrawSizeCalculator(motionEvent).getDrawSize(motionEvent, i));
        }
    }

    private void drawShape(int x, int y, double angle, int drawSize) {
        model.getShape().getDrawer().draw(
                (int) ((x - offsetX) / scale),
                (int) ((y - offsetY) / scale),
                angle, drawSize, offScreenCanvas,
                shapePainter.getStrokePaint(),
                shapePainter.getFillPaint(model.getColor()));
    }

    //==============================================================================================
    // Draw Size
    //==============================================================================================
    private int drawSize;

    private interface DrawSizeCalculator {
        public int getDrawSize(MotionEvent motionEvent, int pos);
    }
    private DrawSizeCalculator drawSizeCalculator;
    private DrawSizeCalculator getDrawSizeCalculator(MotionEvent motionEvent) {
        if (drawSizeCalculator != null) {
            return drawSizeCalculator;
        }
        if (motionEvent.getPressure() > 0 && motionEvent.getPressure() < 1) {
            drawSizeCalculator = new DrawSizeTouchCalculator() {
                @Override
                public int getDrawSize(MotionEvent motionEvent, int pos) {
                    return addTouchSize(motionEvent.getPressure(pos));
                }
            };
        } else if (motionEvent.getSize() > 0 && motionEvent.getSize() < 1) {
            drawSizeCalculator = new DrawSizeTouchCalculator();
        } else {
            drawSizeCalculator = new DrawSizeCalculator() {
                @Override
                public int getDrawSize(MotionEvent motionEvent, int pos) {
                    return drawSize;
                }
            };
        }
        return drawSizeCalculator;
    }
    private class DrawSizeTouchCalculator implements DrawSizeCalculator {

        protected AverageFloat averageCalculator = new AverageFloat();

        @Override
        public int getDrawSize(MotionEvent motionEvent, int pos) {
            return addTouchSize(motionEvent.getSize(pos));
        }

        protected int addTouchSize(float touchSize) {
            float averageTouchSize = averageCalculator.add(touchSize);
            double drawScale = Math.pow(touchSize / averageTouchSize, 2);
            return (int) (drawSize * drawScale);
        }
    }

    private class AverageFloat {
        private float total = 0;
        private int count = 0;

        public float add(float number) {
            total += number;
            return total / ++count;
        }
    }

    //==============================================================================================
    // Undo / Redo
    //==============================================================================================
    private View undoButton;
    private View redoButton;

    private void setupUndoRedoButtons(Activity activity) {
        undoButton = activity.findViewById(R.id.undoButton);
        undoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                model.undo();
                resetOffScreenCanvas();
                invalidate();

                checkUndoButtons();
            }
        });

        redoButton = activity.findViewById(R.id.redoButton);
        redoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                model.redo();
                resetOffScreenCanvas();
                invalidate();

                checkUndoButtons();
            }
        });

        checkUndoButtons();
    }

    private void markUndo() {
        model.markUndo();
        checkUndoButtons();
    }

    private void checkUndoButtons() {
        setViewEnabled(undoButton, model.hasUndo());
        setViewEnabled(redoButton, model.hasRedo());
    }

    //==============================================================================================
    // Zoom / Move
    //==============================================================================================
    private static final float SCALE_STEP = 0.1f;
    private static final float SCALE_MIN = 0.5f;
    private static final float SCALE_MAX = 2.0f;
    private float scale = 1.0f;

    private int moveStep;
    private int offsetX = 0;
    private int offsetY = 0;

    private View zoomInButton;
    private View zoomOutButton;
    private View leftButton;
    private View rightButton;
    private View upButton;
    private View downButton;

    private void setupZoomAndMoveButtons(Activity activity) {
        zoomInButton = activity.findViewById(R.id.zoomInButton);
        zoomInButton.setOnTouchListener(new OnLongPressListener() {
            @Override
            protected void onPress() {
                zoomIn();
            }
        });
        zoomOutButton = activity.findViewById(R.id.zoomOutButton);
        zoomOutButton.setOnTouchListener(new OnLongPressListener() {
            @Override
            protected void onPress() {
                zoomOut();
            }
        });

        leftButton = activity.findViewById(R.id.leftButton);
        leftButton.setOnTouchListener(new OnLongPressListener() {
            @Override
            protected void onPress() {
                moveLeft();
            }
        });
        rightButton = activity.findViewById(R.id.rightButton);
        rightButton.setOnTouchListener(new OnLongPressListener() {
            @Override
            protected void onPress() {
                moveRight();
            }
        });
        upButton = activity.findViewById(R.id.upButton);
        upButton.setOnTouchListener(new OnLongPressListener() {
            @Override
            protected void onPress() {
                moveUp();
            }
        });
        downButton = activity.findViewById(R.id.downButton);
        downButton.setOnTouchListener(new OnLongPressListener() {
            @Override
            protected void onPress() {
                moveDown();
            }
        });
    }

    private void zoomIn() {
        if (model.getBitmap() != null && scale < SCALE_MAX) {
            float oldScale = scale;
            scale = Math.min(SCALE_MAX, scale + SCALE_STEP);
            checkOffsetAfterZoom(oldScale);
            checkZoomAndMoveButtons();
            invalidate();
        }
    }

    private void zoomOut() {
        if (model.getBitmap() != null && scale > SCALE_MIN) {
            float oldScale = scale;
            scale = Math.max(SCALE_MIN, scale - SCALE_STEP);
            checkOffsetAfterZoom(oldScale);
            checkZoomAndMoveButtons();
            invalidate();
        }
    }

    private void moveLeft() {
        if (model.getBitmap() != null) {
            int maxX = getMaxOffsetX();
            if (offsetX < maxX) {
                offsetX = Math.min(maxX, offsetX + moveStep);
                checkHMoveButtons();
                invalidate();
            }
        }
    }

    private void moveRight() {
        if (model.getBitmap() != null) {
            int minX = getMinOffsetX();
            if (offsetX > minX) {
                offsetX = Math.max(minX, offsetX - moveStep);
                checkHMoveButtons();
                invalidate();
            }
        }
    }

    private void moveUp() {
        if (model.getBitmap() != null) {
            int maxY = getMaxOffsetY();
            if (offsetY < maxY) {
                offsetY = Math.min(maxY, offsetY + moveStep);
                checkVMoveButtons();
                invalidate();
            }
        }
    }

    private void moveDown() {
        if (model.getBitmap() != null) {
            int minY = getMinOffsetY();
            if (offsetY > minY) {
                offsetY = Math.max(minY, offsetY - moveStep);
                checkVMoveButtons();
                invalidate();
            }
        }
    }

    private int getMinOffsetX() {
        int w = (int) (model.getBitmap().getWidth() * scale);
        return w > getWidth() ? getWidth() - w : 0;
    }

    private int getMaxOffsetX() {
        int w = (int) (model.getBitmap().getWidth() * scale);
        return w > getWidth() ? 0 : getWidth() - w;
    }

    private int getMinOffsetY() {
        int h = (int) (model.getBitmap().getHeight() * scale);
        return h > getHeight() ? getHeight() - h : 0;
    }

    private int getMaxOffsetY() {
        int h = (int) (model.getBitmap().getHeight() * scale);
        return h > getHeight() ? 0 : getHeight() - h;
    }

    private void checkOffsetAfterZoom(float oldScale) {
        float scaleDelta = scale - oldScale;
        offsetX -= model.getBitmap().getWidth() * scaleDelta / 2;
        offsetY -= model.getBitmap().getHeight() * scaleDelta / 2;
        offsetX = Math.max(getMinOffsetX(), Math.min(getMaxOffsetX(), offsetX));
        offsetY = Math.max(getMinOffsetY(), Math.min(getMaxOffsetY(), offsetY));
    }

    private void checkZoomAndMoveButtons() {
        checkZoomButtons();
        checkHMoveButtons();
        checkVMoveButtons();
    }

    private void checkZoomButtons() {
        setViewEnabled(zoomInButton, model.getBitmap() != null && scale < SCALE_MAX);
        setViewEnabled(zoomOutButton, model.getBitmap() != null && scale > SCALE_MIN);
    }

    private void checkHMoveButtons() {
        setViewEnabled(leftButton, model.getBitmap() != null && offsetX < getMaxOffsetX());
        setViewEnabled(rightButton, model.getBitmap() != null && offsetX > getMinOffsetX());
    }

    private void checkVMoveButtons() {
        setViewEnabled(upButton, model.getBitmap() != null && offsetY < getMaxOffsetY());
        setViewEnabled(downButton, model.getBitmap() != null && offsetY > getMinOffsetY());
    }


    private class OnLongPressListener implements View.OnTouchListener {
        private final int initialInterval = 500;
        private final int normalInterval = 100;

        private Handler handler = new Handler();
        private View view;

        private Runnable handlerRunnable = new Runnable() {
            @Override
            public void run() {
                if (view.isEnabled()) {
                    handler.postDelayed(this, normalInterval);
                    onPress();
                }
            }
        };

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            this.view = view;
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    handler.removeCallbacks(handlerRunnable);
                    handler.postDelayed(handlerRunnable, initialInterval);
                    onPress();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    handler.removeCallbacks(handlerRunnable);
                    break;
            }
            return true;
        }

        protected void onPress() {}
    }

    //==============================================================================================
    // Helpers
    //==============================================================================================
    private void resetOffScreenCanvas() {
        offScreenCanvas = new Canvas(model.getBitmap());
    }

    private void setViewEnabled(View view, Boolean enabled) {
        view.setEnabled(enabled);
        view.setAlpha(enabled ? 1.0f : 0.2f);
    }
}