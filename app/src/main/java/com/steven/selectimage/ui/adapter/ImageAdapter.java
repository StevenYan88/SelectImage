package com.steven.selectimage.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.steven.selectimage.GlideApp;
import com.steven.selectimage.R;
import com.steven.selectimage.model.Image;
import com.steven.selectimage.widget.recyclerview.CommonRecycleAdapter;
import com.steven.selectimage.widget.recyclerview.CommonViewHolder;
import com.steven.selectimage.widget.recyclerview.MultiTypeSupport;
import com.steven.selectimage.ui.SelectImageActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Data：9/4/2018-3:14 PM
 *
 * @author yanzhiwen
 */
public class ImageAdapter extends CommonRecycleAdapter<Image> {
    private Context mContext;
    private onSelectImageCountListener mSelectImageCountListener;
    private ArrayList<Image> mSelectImages;

    public ImageAdapter(Context context, List<Image> images, MultiTypeSupport typeSupport) {
        super(context, images, typeSupport);
        this.mContext = context;
        this.mSelectImages = new ArrayList<>();
    }

    @Override
    protected void convert(CommonViewHolder holder, final Image image, int position) {
        if (!TextUtils.isEmpty(image.getPath())) {
            final ImageView chb_selected = holder.getView(R.id.iv_selected);
            final View maskView = holder.getView(R.id.mask);
            ImageView iv_image = holder.getView(R.id.iv_image);
            GlideApp.with(mContext).load(image.getPath()).into(iv_image);
            chb_selected.setOnClickListener(v -> {
                if (image.isSelect()) {
                    image.setSelect(false);
                    mSelectImages.remove(image);
                    chb_selected.setSelected(false);
                } else if (mSelectImages.size() < SelectImageActivity.MAX_SIZE) {
                    image.setSelect(true);
                    mSelectImages.add(image);
                    chb_selected.setSelected(true);
                    maskView.setVisibility(image.isSelect() ? View.VISIBLE : View.GONE);
                }
                if (mSelectImageCountListener != null) {
                    mSelectImageCountListener.onSelectImageCount(mSelectImages.size());
                    mSelectImageCountListener.onSelectImageList(mSelectImages);
                }
            });
            chb_selected.setSelected(image.isSelect());
            maskView.setVisibility(image.isSelect() ? View.VISIBLE : View.GONE);
        }
    }

    public void setSelectImageCountListener(onSelectImageCountListener selectImageCountListener) {
        mSelectImageCountListener = selectImageCountListener;
    }

    public interface onSelectImageCountListener {
        void onSelectImageCount(int count);

        void onSelectImageList(ArrayList<Image> images);
    }
}
