package com.steven.selectimage.ui.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.steven.selectimage.GlideApp;
import com.steven.selectimage.R;
import com.steven.selectimage.utils.BitmapUtil;
import com.steven.selectimage.widget.recyclerview.CommonRecycleAdapter;
import com.steven.selectimage.widget.recyclerview.CommonViewHolder;

import java.util.ArrayList;

/**
 * Description:
 * Data：9/4/2018-3:14 PM
 *
 * @author yanzhiwen
 */
public class SelectedImageAdapter extends CommonRecycleAdapter<String> {
    private Context mContext;

    public SelectedImageAdapter(Context context, ArrayList<String> data, int layoutId) {
        super(context, data, layoutId);
        this.mContext = context;
    }

    @Override
    protected void convert(CommonViewHolder holder, String path, int position) {
        ImageView iv = holder.getView(R.id.iv_selected_image);
        GlideApp.with(mContext)
                .load(BitmapUtil.decodeSampledBitmapFromResource(path, 360, 540))
                .into(iv);
    }
}