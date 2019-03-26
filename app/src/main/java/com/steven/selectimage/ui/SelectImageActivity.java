package com.steven.selectimage.ui;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.steven.selectimage.R;
import com.steven.selectimage.model.Image;
import com.steven.selectimage.model.ImageFolder;
import com.steven.selectimage.recyclerview.MultiTypeSupport;
import com.steven.selectimage.recyclerview.SpaceGridItemDecoration;
import com.steven.selectimage.ui.adapter.ImageAdapter;
import com.steven.selectimage.ui.adapter.ImageFolderAdapter;
import com.steven.selectimage.utils.StatusBarUtil;
import com.steven.selectimage.utils.TDevice;
import com.steven.selectimage.widget.ImageFolderView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SelectImageActivity extends BaseActivity implements ImageFolderView.ImageFolderViewListener {
    // 返回选择图片列表的EXTRA_KEY
    public static final String EXTRA_RESULT = "EXTRA_RESULT";
    public static final int MAX_SIZE = 9;
    @BindView(R.id.tv_back)
    TextView mTvBack;
    @BindView(R.id.tv_ok)
    TextView mTvSelectCount;
    @BindView(R.id.rv)
    RecyclerView mRvImage;
    @BindView(R.id.tv_photo)
    TextView mTvPhoto;
    @BindView(R.id.tv_preview)
    TextView mTvPreview;
    @BindView(R.id.image_folder_view)
    ImageFolderView mImageFolderView;
    private boolean mHasCamera = true;
    //被选中图片的集合
    private List<Image> mSelectedImages = new ArrayList<>();
    private List<Image> mImages = new ArrayList<>();
    private List<ImageFolder> mImageFolders = new ArrayList<>();
    private ImageAdapter mImageAdapter;
    private ImageFolderAdapter mImageFolderAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_image;
    }

    @Override
    protected void init() {
        //设置状态栏的颜色
        StatusBarUtil.statusBarTintColor(this, ContextCompat.getColor(this, R.color.color_black));
        mRvImage.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false));
        mRvImage.addItemDecoration(new SpaceGridItemDecoration((int) TDevice.dipToPx(getResources(), 1)));
        //异步加载图片
        getSupportLoaderManager().initLoader(0, null, mLoaderCallbacks);
        mImageFolderView.setListener(this);
    }

    @OnClick({R.id.tv_back, R.id.tv_ok, R.id.tv_photo, R.id.tv_preview})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_ok:
                ArrayList<String> paths = new ArrayList<>();
                for (Image selectedImage : mSelectedImages) {
                    paths.add(selectedImage.getPath());
                }
                Intent intent = new Intent();
                intent.putStringArrayListExtra(EXTRA_RESULT, paths);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.tv_photo:
                if (mImageFolderView.isShowing()) {
                    mImageFolderView.hide();
                } else {
                    mImageFolderView.show();
                }
                break;
            case R.id.tv_preview:
                break;
        }
    }


    private void addImageFoldersToAdapter() {
        if (mImageFolderAdapter == null) {
            mImageFolderAdapter = new ImageFolderAdapter(this, mImageFolders, R.layout.item_list_folder);
            mImageFolderView.setAdapter(mImageFolderAdapter);
        } else {
            mImageFolderAdapter.notifyDataSetChanged();
        }
    }

    private void addImagesToAdapter(ArrayList<Image> images) {
        mImages.clear();
        mImages.addAll(images);
        if (mImageAdapter == null) {
            mImageAdapter = new ImageAdapter(this, mImages, mMultiTypeSupport);
            mRvImage.setAdapter(mImageAdapter);
        } else {
            mImageAdapter.notifyDataSetChanged();
        }
        mImageAdapter.setSelectImageCountListener(mOnSelectImageCountListener);
    }

    private MultiTypeSupport<Image> mMultiTypeSupport = image -> {
        if (TextUtils.isEmpty(image.getPath())) {
            return R.layout.item_list_camera;
        }
        return R.layout.item_list_image;
    };
    private ImageAdapter.onSelectImageCountListener mOnSelectImageCountListener = new ImageAdapter.onSelectImageCountListener() {
        @Override
        public void onSelectImageCount(int count) {
            if (count == 0) {
                mTvPreview.setClickable(false);
                mTvPreview.setText("预览");
                mTvPreview.setTextColor(ContextCompat.getColor(SelectImageActivity.this, R.color.colorAccentGray));
            } else if (count > 0 && count <= MAX_SIZE) {
                mTvPreview.setClickable(true);
                mTvPreview.setText(String.format("预览(%d/9) ", count));
                mTvPreview.setTextColor(ContextCompat.getColor(SelectImageActivity.this, R.color.colorAccent));
            }

        }

        @Override
        public void onSelectImageList(ArrayList<Image> images) {
            mSelectedImages.addAll(images);
        }
    };
    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.MINI_THUMB_MAGIC,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        //创建一个CursorLoader，去异步加载相册的图片
        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
            return new CursorLoader(SelectImageActivity.this,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                    null, null, IMAGE_PROJECTION[2] + " DESC");
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                ArrayList<Image> images = new ArrayList<>();
                //是否显示照相图片
                if (mHasCamera) {
                    //添加到第一个的位置（默认）
                    images.add(new Image());
                }
                ImageFolder defaultFolder = new ImageFolder();
                defaultFolder.setName("全部照片");
                defaultFolder.setPath("");
                mImageFolders.add(defaultFolder);

                int count = data.getCount();
                if (count > 0) {
                    data.moveToFirst();
                    do {
                        String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                        int id = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
                        String thumbPath = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
                        String bucket = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[5]));

                        Image image = new Image();
                        image.setPath(path);
                        image.setName(name);
                        image.setDate(dateTime);
                        image.setId(id);
                        image.setThumbPath(thumbPath);
                        image.setFolderName(bucket);
                        images.add(image);
                        //如果是被选中的图片
                        if (mSelectedImages.size() > 0) {
                            for (Image i : mSelectedImages) {
                                if (i.getPath().equals(image.getPath())) {
                                    image.setSelect(true);
                                }
                            }
                        }
                        //设置图片分类的文件夹
                        File imageFile = new File(path);
                        File folderFile = imageFile.getParentFile();
                        ImageFolder folder = new ImageFolder();
                        folder.setName(folderFile.getName());
                        folder.setPath(folderFile.getAbsolutePath());
                        //ImageFolder复写了equal方法，equal方法比较的是文件夹的路径
                        if (!mImageFolders.contains(folder)) {
                            folder.getImages().add(image);
                            //默认相册封面
                            folder.setAlbumPath(image.getPath());
                            mImageFolders.add(folder);
                        } else {
                            ImageFolder imageFolder = mImageFolders.get(mImageFolders.indexOf(folder));
                            imageFolder.getImages().add(image);
                        }
                    } while (data.moveToNext());
                }
                addImagesToAdapter(images);
                //全部照片
                defaultFolder.getImages().addAll(images);
                if (mHasCamera) {
                    defaultFolder.setAlbumPath(images.size() > 1 ? images.get(1).getPath() : null);
                } else {
                    defaultFolder.setAlbumPath(images.size() > 0 ? images.get(0).getPath() : null);
                }
                //删除掉不存在的，在于用户选择了相片，又去相册删除
                if (mSelectedImages.size() > 0) {
                    List<Image> rs = new ArrayList<>();
                    for (Image i : mSelectedImages) {
                        File f = new File(i.getPath());
                        if (!f.exists()) {
                            rs.add(i);
                        }
                    }
                    mSelectedImages.removeAll(rs);
                }
            }
            mImageFolderView.setImageFolders(mImageFolders);
            addImageFoldersToAdapter();
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        }
    };


    @Override
    public void onSelectFolder(ImageFolderView imageFolderView, ImageFolder imageFolder) {
        addImagesToAdapter(imageFolder.getImages());
        mRvImage.scrollToPosition(0);
        mTvPhoto.setText(imageFolder.getName());
    }

    @Override
    public void onDismiss() {

    }

    @Override
    public void onShow() {

    }
}
