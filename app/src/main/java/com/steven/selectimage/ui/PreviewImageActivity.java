package com.steven.selectimage.ui;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.steven.selectimage.GlideApp;
import com.steven.selectimage.R;
import com.steven.selectimage.model.Image;
import com.steven.selectimage.widget.PreViewImageView;

import java.util.List;

import butterknife.BindView;

public class PreviewImageActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.tv_count)
    TextView mCount;
    private List<Image> mSelectedImages;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_preview_image;
    }

    @Override
    protected void init() {
        mSelectedImages = getIntent().getParcelableArrayListExtra("preview_images");
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
        mCount.setText(String.format("%s/%s", 1, mSelectedImages.size()));

    }

    private PagerAdapter mViewPagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return mSelectedImages.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            PreViewImageView imageView = new PreViewImageView(container.getContext());
            GlideApp.with(imageView.getContext())
                    .load(mSelectedImages.get(position).getPath())
                    .centerCrop()
                    .into(imageView);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    };


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCount.setText(String.format("%s/%s", (position + 1), mSelectedImages.size()));

    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
