package com.steven.selectimage.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Description:正方形的FrameLayout容器
 * Data：9/4/2018-3:14 PM
 *
 * @author yanzhiwen
 */
public class SquareFrameLayout extends FrameLayout {
    public SquareFrameLayout(Context context) {
        this(context,null);
    }

    public SquareFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SquareFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);

    }
}
