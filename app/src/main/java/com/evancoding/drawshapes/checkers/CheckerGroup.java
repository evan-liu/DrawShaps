package com.evancoding.drawshapes.checkers;

import android.view.View;
import android.widget.Checkable;

import java.util.ArrayList;

public class CheckerGroup<T extends View & Checkable> implements View.OnClickListener {

    public static interface OnChangeListener<T extends View & Checkable> {
        void onChange(CheckerGroup<T> group);
    }

    private ArrayList<OnChangeListener> onChangeListeners = new ArrayList<OnChangeListener>();

    private ArrayList<T> items = new ArrayList<T>();

    public ArrayList<T> getItems() {
        return items;
    }

    private int checkedIndex = -1;

    public int getCheckedIndex() {
        return checkedIndex;
    }

    public void setCheckedIndex(int checkedIndex) {
        if (checkedIndex != this.checkedIndex && checkedIndex >= 0 && checkedIndex < items.size()) {
            this.setCheckedItem(items.get(checkedIndex));
        }
    }

    private T checkedItem = null;

    public T getCheckedItem() {
        return checkedItem;
    }

    public void setCheckedItem(T checkedItem) {
        if (this.checkedItem == checkedItem) {
            return;
        }
        if (this.checkedItem != null) {
            this.checkedItem.setChecked(false);
        }
        this.checkedItem = checkedItem;

        if (this.checkedItem == null) {
            checkedIndex = -1;
        } else {
            checkedIndex = items.indexOf(this.checkedItem);
            if (checkedIndex > -1) {
                this.checkedItem.setChecked(true);
            } else {
                this.checkedItem = null;
            }
        }

        for (OnChangeListener changeListener : onChangeListeners) {
            changeListener.onChange(this);
        }

    }

    public void add(T item) {
        if (items.indexOf(item) > -1) {
            return;
        }
        items.add(item);
        item.setOnClickListener(this);
    }

    public void setOnChangeListener(OnChangeListener listener) {
        onChangeListeners.add(listener);
    }

    @Override
    public void onClick(View view) {
        setCheckedItem((T) view);
    }

}