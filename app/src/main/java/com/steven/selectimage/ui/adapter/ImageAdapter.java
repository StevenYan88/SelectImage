package com.steven.selectimage.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.steven.selectimage.GlideApp;
import com.steven.selectimage.R;
import com.steven.selectimage.recyclerview.CommonRecycleAdapter;
import com.steven.selectimage.recyclerview.CommonViewHolder;
import com.steven.selectimage.recyclerview.MultiTypeSupport;
import com.steven.selectimage.model.Image;

import java.util.List;

/**
 * Description:
 * Data：9/4/2018-3:14 PM
 *
 * @author yanzhiwen
 */
public class ImageAdapter extends CommonRecycleAdapter<Image> {
    private Context mContext;

    public ImageAdapter(Context context, List<Image> images, MultiTypeSupport typeSupport) {
        super(context, images, typeSupport);
        this.mContext = context;
    }

    @Override
    protected void convert(CommonViewHolder holder, final Image image, int position) {
        if (!TextUtils.isEmpty(image.getPath())) {
            final ImageView iv_selected = holder.getView(R.id.iv_selected);
            final View maskView = holder.getView(R.id.mask);
            iv_selected.setSelected(image.isSelect());
            maskView.setVisibility(image.isSelect() ? View.VISIBLE : View.GONE);
            ImageView iv_image = holder.getView(R.id.iv_image);
            GlideApp.with(mContext)
                    .load(image.getPath())
                    .into(iv_image);
            iv_selected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (iv_selected.isSelected()) {
                        image.setSelect(false);
                        iv_selected.setSelected(false);
                    } else {
                        image.setSelect(true);
                        iv_selected.setSelected(true);
                    }
                    maskView.setVisibility(image.isSelect() ? View.VISIBLE : View.GONE);
                }
            });
        }
    }
}
