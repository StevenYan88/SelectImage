package com.steven.selectimage.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Description:正方形的ImageView
 * Data：9/4/2018-3:15 PM
 *
 * @author yanzhiwen
 */
public class SquareImageView extends ImageView {
    public SquareImageView(Context context) {
        this(context,null);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 自定义View
        int width = MeasureSpec.getSize(widthMeasureSpec);
        // 设置宽高为一样
        setMeasuredDimension(width, width);
    }
}
