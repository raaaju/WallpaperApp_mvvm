package com.georgcantor.wallpaperapp.ui.util;

import android.content.Context;
import android.util.AttributeSet;

public class DynamicHeightImageView extends android.support.v7.widget.AppCompatImageView {

    private float whRatio = 0;

    public DynamicHeightImageView(Context context) {
        super(context);
    }

    public DynamicHeightImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setRatio(float ratio) {
        whRatio = ratio;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (whRatio != 0) {
            int width = getMeasuredWidth();
            int heigth = (int) (whRatio * width);
            setMeasuredDimension(width, heigth);
        }
    }
}
