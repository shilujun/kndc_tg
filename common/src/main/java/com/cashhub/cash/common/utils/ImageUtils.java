package com.cashhub.cash.common.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ImageUtils {

  /**
   * 按比例压缩图片分辨率
   * @param inBitmap
   * @param outHeight 输出图片高度，可据此保持比例计算输出宽度
   * @param needRecycled 是否回收inBitmap
   * @return
   */
  public static Bitmap createScaledBitmapByOutHeight(Bitmap inBitmap, int outHeight, boolean needRecycled) {
    int bitmapHeight = inBitmap.getHeight();
    int bitmapWidth = inBitmap.getWidth();
    int outWidth = bitmapWidth * outHeight / bitmapHeight;

    return createScaledBitmap(inBitmap, outWidth, outHeight, needRecycled);
  }

  /**
   * 按比例压缩图片分辨率
   * @param inBitmap
   * @param outWidth 输出图片宽度，可据此保持比例计算输出高度
   * @param needRecycled 是否回收inBitmap
   * @return
   */
  public static Bitmap createScaledBitmapByOutWidth(Bitmap inBitmap, int outWidth, boolean needRecycled) {
    int bitmapHeight = inBitmap.getHeight();
    int bitmapWidth = inBitmap.getWidth();
    int outHeight = bitmapHeight * outWidth / bitmapWidth;

    return createScaledBitmap(inBitmap, outWidth, outHeight, needRecycled);
  }

  /**
   * 指定输出的宽高缩放图片
   * @param inBitmap
   * @param outWidth
   * @param outHeight
   * @param needRecycled
   * @return
   */
  public static Bitmap createScaledBitmap(Bitmap inBitmap, int outWidth, int outHeight, boolean needRecycled) {

    Bitmap thumbBmp = Bitmap.createScaledBitmap(inBitmap, outWidth, outHeight, true);
    if (needRecycled) {
      inBitmap.recycle();
    }

    return thumbBmp;
  }

  /**
   * 压缩图片质量，把图片压缩到outSize以内
   * @param inBitmap 原始bitmap
   * @param outSize 压缩到的大小
   * @param needRecycled 是否回收bitmap
   * @return
   */
  public static Bitmap compressImage(Bitmap inBitmap, int outSize, boolean needRecycled) {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    inBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
    int quality = 100;
    while (baos.toByteArray().length / 1024 > outSize) {
      if (quality <= 0) {
        ByteArrayInputStream outBais = new ByteArrayInputStream(baos.toByteArray());
        return BitmapFactory.decodeStream(outBais, null, null);// 如果quaLity为0时还未达到32k以内，返回得到的最小值;如需要可结合分辨率压缩
      }
      baos.reset();
      //PNG格式下，这种压缩不起作用（quality:0-100,如果目标大小太小，有时候质量压缩不一定能达到效果，需结合分辨率压缩）
      inBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
      Log.e("AN", "bitmap size:"+ baos.toByteArray().length / 1024 + "k");
      quality -= 10;
    }
    if (needRecycled) {
      inBitmap.recycle();
    }


    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    Bitmap outBitmap= BitmapFactory.decodeStream(bais, null, null);//ByteArrayInputStream转成bitmap

    return outBitmap;
  }

  /**
   * 从资源加载并压缩图片
   * @param res
   * @param resId
   * @param outWidth 目标宽度
   * @param outHeight 目标高度
   * @return
   */
  public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
      int outWidth, int outHeight) {
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true; // 假解,来获取图片大小
    BitmapFactory.decodeResource(res, resId, options);
    options.inSampleSize = calculateInSampleSize(options, outWidth, outHeight);
    // 使用获取到的inSampleSize值再次解析图片
    options.inJustDecodeBounds = false;
    //options.inPreferredConfig = Config.RGB_565;
    return BitmapFactory.decodeResource(res, resId, options);
  }

  /**
   * 从文件中加载并压缩图片
   * @param outWidth 目标宽度
   * @param outHeight 目标高度
   * @return
   */
  public static Bitmap decodeSampledBitmapFromFile(String pathName, int outWidth, int outHeight) {
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true; // 假解,来获取图片大小
    BitmapFactory.decodeFile(pathName, options);
    options.inSampleSize = calculateInSampleSize(options, outWidth, outHeight);
    // 使用获取到的inSampleSize值再次解析图片
    options.inJustDecodeBounds = false;
    //options.inPreferredConfig = Config.RGB_565;
    return BitmapFactory.decodeFile(pathName, options);
  }

  /**
   * 计算options.inSampleSize
   * @param options
   * @param reqWidth
   * @param reqHeight
   * @return
   */
  public static int calculateInSampleSize(BitmapFactory.Options options,
      int reqWidth, int reqHeight) {
    // 源图片的高度和宽度
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;
    if (height > reqHeight || width > reqWidth) {
      // 计算出实际宽高和目标宽高的比率
      final int heightRatio = Math.round((float) height / (float) reqHeight);
      final int widthRatio = Math.round((float) width / (float) reqWidth);
      // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
      // 一定都会大于等于目标的宽和高。
      inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    }
    return inSampleSize;
  }

}
