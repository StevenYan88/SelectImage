package com.steven.selectimage.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import com.steven.selectimage.R;
import com.steven.selectimage.model.ImageFolder;
import com.steven.selectimage.widget.recyclerview.OnItemClickListener;
import com.steven.selectimage.ui.adapter.ImageFolderAdapter;

import java.util.List;

/**
 * Description:
 * Data：9/5/2018-1:12 PM
 *
 * @author yanzhiwen
 */
public class ImageFolderView extends FrameLayout implements OnItemClickListener {
    private View mShadowView;
    private String mShadowViewColor = "#50000000";
    private RecyclerView mImageFolderRv;
    private List<ImageFolder> mImageFolders;
    private ImageFolderViewListener mListener;
    private int mImageFolderHeight;
    private boolean mShow;

    public ImageFolderView(Context context) {
        this(context, null);
    }

    public ImageFolderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageFolderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mShadowView = new View(context);
        mShadowView.setBackgroundColor(Color.parseColor(mShadowViewColor));
        mImageFolderRv = (RecyclerView) inflate(context, R.layout.image_folder_layout, null);
        //设置LayoutParams
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.BOTTOM;
        mImageFolderRv.setLayoutParams(layoutParams);
        //设置布局管理器setLayoutManager
        mImageFolderRv.setLayoutManager(new LinearLayoutManager(context));
        addView(mShadowView);
        addView(mImageFolderRv);
        //开始不显示阴影
        mShadowView.setAlpha(0f);
        mShadowView.setVisibility(GONE);

    }

    public void setImageFolders(List<ImageFolder> imageFolders) {
        mImageFolders = imageFolders;
    }

    public void setAdapter(ImageFolderAdapter adapter) {
        if (adapter == null) {
            throw new NullPointerException("adapter not null！");
        }
        mImageFolderRv.setAdapter(adapter);
        adapter.setItemClickListener(this);
    }

    public void setListener(ImageFolderViewListener listener) {
        this.mListener = listener;
    }

    /**
     * 显示
     */
    public void show() {
        if (mShow) {
            return;
        }
        if (mListener != null) {
            mListener.onShow();
        }
        mShow = true;
        mShadowView.setVisibility(VISIBLE);
        ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(mImageFolderRv, "translationY", mImageFolderHeight, 0);
        translationYAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mShadowView, "alpha", 0f, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translationYAnimator, alphaAnimator);
        animatorSet.setDuration(388);
        animatorSet.start();

    }

    /**
     * 隐藏
     */
    public void hide() {
        if (!mShow) {
            return;
        }
        if (mListener != null) {
            mListener.onDismiss();
        }
        ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(mImageFolderRv,
                "translationY", 0, mImageFolderHeight);
        translationYAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mShadowView, "alpha", 1f, 0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translationYAnimator, alphaAnimator);
        animatorSet.setDuration(388);
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mShow = false;
                mShadowView.setVisibility(GONE);
            }
        });
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取高度
        int height = MeasureSpec.getSize(heightMeasureSpec);
        mImageFolderHeight = (int) (height * 0.9f);
        ViewGroup.LayoutParams params = mImageFolderRv.getLayoutParams();
        params.height = mImageFolderHeight;
        mImageFolderRv.setLayoutParams(params);
        measureChild(mImageFolderRv, widthMeasureSpec, heightMeasureSpec);
        //开始的时候，移下去
        mImageFolderRv.setTranslationY(mImageFolderHeight);
    }

    public boolean isShowing() {
        return mShow;
    }


    @Override
    public void onItemClick(int position) {
        if (mListener != null) {
            mListener.onSelectFolder(this, mImageFolders.get(position));
            hide();
        }
    }

    public interface ImageFolderViewListener {
        void onSelectFolder(ImageFolderView imageFolderView, ImageFolder imageFolder);

        void onDismiss();

        void onShow();
    }

}
