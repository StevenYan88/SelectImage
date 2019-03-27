package com.steven.selectimage.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Description:
 * Data：3/26/2019-5:43 PM
 *
 * @author yanzhiwen
 */
public class BitmapUtil {
    public static Bitmap calculateInSampleSize(String imagePath) {
        // 设置参数
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
        // 输出图像数据
        //orginBitmap=: size: 14745600 width: 1440 heigth:2560
//        Bitmap originBitmap = BitmapFactory.decodeFile(imagePath, options);
//        Log.w("originBitmap=", "size: " + originBitmap.getByteCount() +
//                " width: " + originBitmap.getWidth() + " height:" + originBitmap.getHeight());
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 2; // 默认像素压缩比例，压缩为原图的1/2
        int minLen = Math.min(height, width); // 原图的最小边长
        if (minLen > 100) { // 如果原始图像的最小边长大于100dp（此处单位我认为是dp，而非px）
            float ratio = (float) minLen / 100.0f; // 计算像素压缩比例
            inSampleSize = (int) ratio;
        }
        options.inSampleSize = inSampleSize; // 设置为刚才计算的压缩比例
        options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
        Bitmap bm = BitmapFactory.decodeFile(imagePath, options); // 解码文件
        //size: 74256 width: 102 heigth:182
        Log.w("bm=", "size: " + bm.getByteCount() + " width: " + bm.getWidth() + " heigth:" + bm.getHeight()); // 输出图像数据
        return bm;
    }
}
