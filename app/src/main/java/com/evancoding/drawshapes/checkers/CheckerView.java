package com.evancoding.drawshapes.checkers;

import android.content.Context;
import android.view.View;
import android.widget.Checkable;

public class CheckerView extends View implements Checkable {

    public CheckerView(Context context) {
        super(context);
    }

    protected boolean checked = false;

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void setChecked(boolean b) {
        checked = b;
        invalidate();
    }

    @Override
    public void toggle() {
        setChecked(!checked);
    }

}