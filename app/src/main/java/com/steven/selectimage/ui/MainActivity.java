package com.steven.selectimage.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.steven.selectimage.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 0;
    private static final int SELECT_IMAGE_REQUEST = 0x0011;

    private ImageView mImageView;
    private ArrayList<String> mSelectedImages;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = findViewById(R.id.iv);
        findViewById(R.id.btn_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void selectImage() {

        int isPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (isPermission == PackageManager.PERMISSION_GRANTED) {
            startActivity();
        } else {
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private void startActivity() {
        Intent intent = new Intent(this, SelectImageActivity.class);
        startActivityForResult(intent, SELECT_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity();
            } else {
                Toast.makeText(this, "需要您的存储权限!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_IMAGE_REQUEST && data != null) {
                mSelectedImages = data.getStringArrayListExtra(SelectImageActivity.EXTRA_RESULT);
                Log.i("MainActivity", "onActivityResult: " + mSelectedImages.toString() + "," + mSelectedImages.size());
                String imagePath = mSelectedImages.get(0);

                // 设置参数
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
                //orginBitmap=: size: 14745600 width: 1440 heigth:2560
                Bitmap orginBitmap = BitmapFactory.decodeFile(imagePath, options);
           //     Log.w("orginBitmap=", "size: " + orginBitmap.getByteCount() + " width: " + orginBitmap.getWidth() + " heigth:" + orginBitmap.getHeight()); // 输出图像数据
                int height = options.outHeight;
                int width = options.outWidth;
                int inSampleSize = 2; // 默认像素压缩比例，压缩为原图的1/2
                int minLen = Math.min(height, width); // 原图的最小边长
                if (minLen > 100) { // 如果原始图像的最小边长大于100dp（此处单位我认为是dp，而非px）
                    float ratio = ( float ) minLen / 100.0f; // 计算像素压缩比例
                    inSampleSize = ( int ) ratio;
                }
                options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
                options.inSampleSize = inSampleSize; // 设置为刚才计算的压缩比例
                Bitmap bm = BitmapFactory.decodeFile(imagePath, options); // 解码文件
                //size: 74256 width: 102 heigth:182
                Log.w("bm=", "size: " + bm.getByteCount() + " width: " + bm.getWidth() + " heigth:" + bm.getHeight()); // 输出图像数据
                mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                mImageView.setImageBitmap(bm);
            }
        }
    }

}
