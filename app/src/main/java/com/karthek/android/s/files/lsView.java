package com.karthek.android.s.files;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

public class lsView extends LinearLayout implements Checkable {

    private boolean fChecked;

    public lsView(Context context) {
        super(context);
    }

    public lsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public lsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setChecked(boolean checked) {
        System.out.println("setchecked "+checked);
        fChecked=checked;
    }

    @Override
    public boolean isChecked() {
        return fChecked;
    }

    @Override
    public void toggle() {
        fChecked=!fChecked;
    }
}
