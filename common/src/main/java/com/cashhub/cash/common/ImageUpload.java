package com.cashhub.cash.common;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import com.cashhub.cash.common.utils.ImageUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Map;

public class ImageUpload {

  private static final String TAG = "ImageUpload";

  private static ImageUpload mInstance;

  public static synchronized ImageUpload getInstance() {
    if (mInstance == null) {
      mInstance = new ImageUpload();
    }
    return mInstance;
  }

  /**
   * 保存图片
   */
  public String saveImageToGallery(Context context, Bitmap inBitmap) {
    //首先压缩图片到置指定打下之下
    Bitmap bmp = ImageUtils.compressImage(inBitmap, 800, false);
    Log.d(TAG, "saveImageToGallery Start!!!");
    // 首先保存图片 创建文件夹
    File appDir = new File(Environment.getExternalStorageDirectory(), "oasystem");
    if (!appDir.exists()) {
      Log.d(TAG, "创建");
      if (!appDir.mkdir()) {
        Log.e("文件路径", "创建文件夹失败");
        return "";
      }
    }
    //图片文件名称
    String fileName = "living_ocr_.png";
    File file = new File(appDir, fileName);
    try {
      FileOutputStream fos = new FileOutputStream(file);
      bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
      fos.flush();
      fos.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    // 其次把文件插入到系统图库
    String path = file.getAbsolutePath();
    Log.d(TAG, path+' '+fileName);
    try {
      MediaStore.Images.Media.insertImage(context.getContentResolver(), path, fileName, null);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    // 最后通知图库更新
//    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//    Uri uri = Uri.fromFile(file);
//    intent.setData(uri);
//    context.sendBroadcast(intent);

    return file.getPath();
  }

}
