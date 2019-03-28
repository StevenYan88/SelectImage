package com.steven.selectimage.widget;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.animation.AccelerateInterpolator;

/**
 * Description: PreViewImageView 预览图片 手指缩放 移动
 * Data：11/27/2018-2:11 PM
 *
 * @author yanzhiwen
 */
public class PreViewImageView extends AppCompatImageView {
    private static final String TAG = PreViewImageView.class.getSimpleName();
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private int mBoundWidth = 0;
    private int mBoundHeight = 0;
    private boolean isAutoScale = false;
    private float scale = 1.0f;
    private float translateLeft = 0.0f;
    private float translateTop = 0.0f;
    private static final float mMaxScale = 4.0f;
    private static final float mMinScale = 0.4f;

    private ValueAnimator mScaleAnimator;
    private ValueAnimator mHorizontalXAnimator;
    private ValueAnimator mVerticalYAnimator;

    private AccelerateInterpolator mAccelerateInterpolator = new AccelerateInterpolator();
    private FloatEvaluator mFloatEvaluator = new FloatEvaluator();

    public PreViewImageView(Context context) {
        this(context,null);
    }

    public PreViewImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PreViewImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mGestureDetector = new GestureDetector(getContext(), new GestureListener());
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureListener());
    }

    //手势处理类GestureDetector.SimpleOnGestureListener
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        /**
         * 手指滑动调用
         *
         * @param e1        horizontal event
         * @param e2        vertical event
         * @param distanceX previous X - current X, toward left , is position
         * @param distanceY previous Y - current Y, toward up, is position
         * @return true
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            final float mScaledWidth = mBoundWidth * scale;
            final float mScaledHeight = mBoundHeight * scale;
            if (mScaledHeight > getHeight()) {
                translateTop -= distanceY * 1.5;
                translateTop = getTranslateTop(translateTop);
            }
            boolean isReachBorder = false;
            if (mScaledWidth > getWidth()) {
                translateLeft -= distanceX * 1.5;
                final float t = getTranslateLeft(translateLeft);
                if (t != translateLeft) isReachBorder = true;
                translateLeft = t;
            } else {
                isReachBorder = true;
            }
            invalidate();
            return true;
        }

        /**
         * 手指双击调用
         *
         * @param e event
         * @return true
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            isAutoScale = true;
            ValueAnimator scaleAnimator = getResetScaleAnimator();
            if (scale == 1.0f) {
                //缩放
                scaleAnimator.setFloatValues(1.0f, 2.0f);
                //x轴平移
                ValueAnimator xAnimator = getHorizontalXAnimator();
                //y轴平移
                ValueAnimator yAnimator = getVerticalYAnimator();
                xAnimator.setFloatValues(translateLeft, (getWidth() - mBoundWidth * 2.f) / 2.f);//-540
                Log.i(TAG, "onDoubleTap: translateLeft " + (getWidth() - mBoundWidth * 2.f) / 2.f);
                yAnimator.setFloatValues(translateTop, getDefaultTranslateTop(getHeight(), mBoundHeight * 2));
                Log.i(TAG, "onDoubleTap: translateTop" + getDefaultTranslateTop(getHeight(), mBoundHeight * 2)); //0
                xAnimator.addUpdateListener(getOnTranslateXAnimationUpdate());
                yAnimator.addUpdateListener(getOnTranslateYAnimationUpdate());
                //开启动画
                xAnimator.start();
                yAnimator.start();
            } else {
                scaleAnimator.setFloatValues(scale, 1.0f);
                resetDefaultState();

            }
            scaleAnimator.addUpdateListener(getOnScaleAnimationUpdate());
            scaleAnimator.start();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    //手指缩放类ScaleGestureDetector.SimpleOnScaleGestureListener
    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        /**
         * 两个手指缩放调用
         *
         * @param detector ScaleGestureDetector
         * @return true
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            final float mOldScaledWidth = mBoundWidth * scale;
            final float mOldScaledHeight = mBoundHeight * scale;
            if (mOldScaledWidth > getWidth() && getDiffX() != 0 ||
                    (mOldScaledHeight > getHeight() && getDiffY() != 0)) return false;
            float factor = detector.getScaleFactor();
            Log.i(TAG, "factor=" + factor);
            float value = scale;
            value += (factor - 1) * 2;
            if (value == scale) return true;
            if (value <= mMinScale) return false;
            if (value > mMaxScale) return false;
            scale = value;
            final float mScaledWidth = mBoundWidth * scale;
            final float mScaledHeight = mBoundHeight * scale;

            // 走了些弯路, 不应该带入translateX计算, 因为二次放大之后计算就不正确了,它应该受scale的制约
            translateLeft = getWidth() / 2.f - (getWidth() / 2.f - translateLeft) * mScaledWidth / mOldScaledWidth;
            translateTop = getHeight() / 2.f - (getHeight() / 2.f - translateTop) * mScaledHeight / mOldScaledHeight;

            final float diffX = getDiffX();
            final float diffY = getDiffY();

            // 考虑宽图, 如果缩小的时候图片左边界到了屏幕左边界,停留在左边界缩小
            if (diffX > 0 && mScaledWidth > getWidth()) {
                translateLeft = 0;
            }
            // 右边界问题
            if (diffX < 0 && mScaledWidth > getWidth()) {
                translateLeft = getWidth() - mScaledWidth;
            }

            // 考虑到长图,上边界问题
            if (diffY > 0 && mScaledHeight > getHeight()) {
                translateTop = 0;
            }

            // 下边界问题
            if (diffY < 0 && mScaledHeight > getHeight()) {
                translateTop = getHeight() - mScaledHeight;
            }

            invalidate();
            return true;
        }
    }


    /**
     * @return 如果是正数, 左边有空隙, 如果是负数, 右边有空隙, 如果是0, 代表两边都没有空隙
     */
    private float getDiffX() {
        final float mScaledWidth = mBoundWidth * scale;
        return translateLeft >= 0
                ? translateLeft
                : getWidth() - translateLeft - mScaledWidth > 0
                ? -(getWidth() - translateLeft - mScaledWidth)
                : 0;
    }

    /**
     * @return 如果是正数, 上面有空隙, 如果是负数, 下面有空隙, 如果是0, 代表两边都没有空隙
     */
    private float getDiffY() {
        final float mScaledHeight = mBoundHeight * scale;
        return translateTop >= 0
                ? translateTop
                : getHeight() - translateTop - mScaledHeight > 0
                ? -(getHeight() - translateTop - mScaledHeight)
                : 0;
    }


    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        super.setFrame(l, t, r, b);
        Drawable drawable = getDrawable();
        if (drawable == null) return false;
        if (mBoundWidth != 0 && mBoundHeight != 0 && scale != 1) return false;
        adjustBounds(getWidth(), getHeight());
        return true;
    }


    private void adjustBounds(int width, int height) {
        Drawable drawable = getDrawable();
        if (drawable == null) return;
        mBoundWidth = drawable.getBounds().width();
        mBoundHeight = drawable.getBounds().height();
        float scale = ( float ) mBoundWidth / width;
        mBoundHeight /= scale;
        mBoundWidth = width;
        drawable.setBounds(0, 0, mBoundWidth, mBoundHeight);
        translateLeft = 0;
        translateTop = getDefaultTranslateTop(height, mBoundHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        adjustBounds(w, h);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Drawable drawable = getDrawable();
        if (drawable == null) return;
        //图片的宽和高
        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();
        if (drawableWidth == 0 || drawableHeight == 0) {
            return;
        }
        canvas.save();
        canvas.translate(translateLeft, translateTop);
        canvas.scale(scale, scale);
        //如果先scale,再translate,那么,真实translate的值是要与scale值相乘的
        drawable.draw(canvas);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            cancelAnimator();
        }
        mGestureDetector.onTouchEvent(event);
        mScaleGestureDetector.onTouchEvent(event);
        return true;
    }


    /**
     * 重置伸缩
     *
     * @return mScaleAnimator
     */
    private ValueAnimator getResetScaleAnimator() {
        if (mScaleAnimator != null) {
            mScaleAnimator.removeAllUpdateListeners();
        } else {
            mScaleAnimator = ValueAnimator.ofFloat();
        }
        mScaleAnimator.setDuration(150);
        mScaleAnimator.setInterpolator(mAccelerateInterpolator);
        mScaleAnimator.setEvaluator(mFloatEvaluator);
        return mScaleAnimator;
    }


    /**
     * 水平方向的动画 (X轴)
     *
     * @return mHorizontalAnimator
     */
    private ValueAnimator getHorizontalXAnimator() {
        if (mHorizontalXAnimator != null) {
            mHorizontalXAnimator.removeAllUpdateListeners();
        } else {
            mHorizontalXAnimator = ValueAnimator.ofFloat();
        }
        mHorizontalXAnimator.setDuration(150);
        mHorizontalXAnimator.setInterpolator(mAccelerateInterpolator);
        mHorizontalXAnimator.setEvaluator(mFloatEvaluator);
        return mHorizontalXAnimator;
    }


    /**
     * 垂直方向的动画
     *
     * @return resetYAnimator
     */
    private ValueAnimator getVerticalYAnimator() {
        if (mVerticalYAnimator != null) {
            mVerticalYAnimator.removeAllUpdateListeners();
        } else {
            mVerticalYAnimator = ValueAnimator.ofFloat();
        }
        mVerticalYAnimator.setDuration(150);
        mVerticalYAnimator.setInterpolator(mAccelerateInterpolator);
        mVerticalYAnimator.setEvaluator(mFloatEvaluator);
        return mVerticalYAnimator;
    }

    /**
     * 重置到初始的状态
     */
    private void resetDefaultState() {
        if (translateLeft != 0) {
            ValueAnimator mTranslateXAnimator = getHorizontalXAnimator();
            mTranslateXAnimator.setFloatValues(translateLeft, 0);
            mTranslateXAnimator.addUpdateListener(getOnTranslateXAnimationUpdate());
            mTranslateXAnimator.start();
        }

        ValueAnimator mTranslateYAnimator = getVerticalYAnimator();
        mTranslateYAnimator.setFloatValues(translateTop, getDefaultTranslateTop(getHeight(), mBoundHeight));
        mTranslateYAnimator.addUpdateListener(getOnTranslateYAnimationUpdate());
        mTranslateYAnimator.start();

    }


    private float getDefaultTranslateTop(int height, int boundHeight) {
        float top = (height - boundHeight) / 2.f;
        return top > 0 ? top : 0;
    }


    private ValueAnimator.AnimatorUpdateListener onScaleAnimationUpdate;

    /**
     * 重置伸缩动画的监听器
     *
     * @return onScaleAnimationUpdate
     */
    public ValueAnimator.AnimatorUpdateListener getOnScaleAnimationUpdate() {
        if (onScaleAnimationUpdate != null) return onScaleAnimationUpdate;
        onScaleAnimationUpdate = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scale = ( float ) animation.getAnimatedValue();
                invalidate();
            }
        };
        return onScaleAnimationUpdate;
    }

    /**
     * 水平动画的监听器
     *
     * @return
     */
    private ValueAnimator.AnimatorUpdateListener onTranslateXAnimationUpdate;

    public ValueAnimator.AnimatorUpdateListener getOnTranslateXAnimationUpdate() {
        if (onTranslateXAnimationUpdate != null) return onTranslateXAnimationUpdate;
        onTranslateXAnimationUpdate = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                translateLeft = ( float ) animation.getAnimatedValue();
                invalidate();
            }
        };
        return onTranslateXAnimationUpdate;
    }

    /**
     * 垂直动画的监听器
     *
     * @return onTranslateYAnimationUpdate
     */
    private ValueAnimator.AnimatorUpdateListener onTranslateYAnimationUpdate;

    public ValueAnimator.AnimatorUpdateListener getOnTranslateYAnimationUpdate() {
        if (onTranslateYAnimationUpdate != null) return onTranslateYAnimationUpdate;
        onTranslateYAnimationUpdate = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                translateTop = ( float ) animation.getAnimatedValue();
                invalidate();
            }
        };
        return onTranslateYAnimationUpdate;
    }

    /**
     * 向左平移的距离
     *
     * @param l translateLeft
     * @return l
     */
    private float getTranslateLeft(float l) {
        final float mScaledWidth = mBoundWidth * scale;
        if (l > 0) {
            l = 0;
        }
        if (-l + getWidth() > mScaledWidth) {
            l = getWidth() - mScaledWidth;
        }
        return l;
    }

    /**
     * 向上平移的距离
     *
     * @param t top
     * @return t
     */
    private float getTranslateTop(float t) {
        final float mScaledHeight = mBoundHeight * scale;
        if (t > 0) {
            t = 0;
        }
        if (-t + getHeight() > mScaledHeight) {
            t = getHeight() - mScaledHeight;
        }
        return t;
    }


    /**
     * 取消动画
     */
    private void cancelAnimator() {
        if (mHorizontalXAnimator != null && mHorizontalXAnimator.isRunning()) {
            mHorizontalXAnimator.cancel();
        }
        if (mVerticalYAnimator != null && mVerticalYAnimator.isRunning()) {
            mVerticalYAnimator.cancel();
        }
        if (mScaleAnimator != null && mScaleAnimator.isRunning()) {
            mScaleAnimator.cancel();
        }
    }
}
