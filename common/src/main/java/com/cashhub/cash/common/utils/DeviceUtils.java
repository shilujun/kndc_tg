package com.cashhub.cash.common.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import com.alibaba.fastjson.JSONObject;
import com.cashhub.cash.common.KndcStorage;

public class DeviceUtils {
  /**
   * 获取设备号
   * @param context
   * @return
   */
  @SuppressLint("HardwareIds")
  public static String getDeviceId(Context context) {
    String deviceId;

    if (Build.VERSION.SDK_INT >= 29) {
      deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    } else {
      final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
      if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
        return "";
      }
      assert mTelephony != null;
      if (mTelephony.getDeviceId() != null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          deviceId = mTelephony.getImei();
        } else {
          deviceId = mTelephony.getDeviceId();
        }
      } else {
        deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
      }
    }
    return deviceId;
  }

    public static int getVersionCode(Context context) {
      int versionCode = 0;
      try {
        //获取软件版本号，对应AndroidManifest.xml下android:versionCode
        versionCode = context.getPackageManager().
            getPackageInfo(context.getPackageName(), 0).versionCode;
      } catch (PackageManager.NameNotFoundException e) {
        e.printStackTrace();
      }
      return versionCode;
    }

    /**
     * 获取版本号名称
     */
    public static String getVerName(Context context) {
      String verName = "";
      try {
        verName = context.getPackageManager().
            getPackageInfo(context.getPackageName(), 0).versionName;
      } catch (PackageManager.NameNotFoundException e) {
        e.printStackTrace();
      }
      return verName;
    }

  /**
   * 获取屏幕宽度
   */
  public static int getDisplayHeight(Context context) {
    //获取减去系统栏的屏幕的高度和宽度
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//      int width = displayMetrics.widthPixels;
    int height = displayMetrics.heightPixels;
    return height;
  }

  /**
   * 获取系统信息
   */
  public static JSONObject getSystemInfo() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("MODEL:", Build.MODEL);
    jsonObject.put("MANUFACTURER:", Build.MANUFACTURER);
    jsonObject.put("ID:", Build.ID);
    jsonObject.put("HOST:", Build.HOST);
    jsonObject.put("HARDWARE:", Build.HARDWARE);
    jsonObject.put("FINGERPRINT:", Build.FINGERPRINT);
    jsonObject.put("DISPLAY:", Build.DISPLAY);
    jsonObject.put("DEVICE:", Build.DEVICE);
    jsonObject.put("BOOTLOADER:", Build.BOOTLOADER);
    jsonObject.put("BRAND:", Build.BRAND);
    jsonObject.put("BOARD:", Build.BOARD);
    jsonObject.put("PRODUCT:", Build.PRODUCT);
    jsonObject.put("TAGS:", Build.TAGS);
    return jsonObject;
  }

  public static JSONObject getSystemInfo(Context context) {

    JSONObject jsonObject = new JSONObject();
//    jsonObject.put("key", KndcStorage.getInstance().getData(KndcStorage.USER_TOKEN));
//    jsonObject.put("user_uuid", KndcStorage.getInstance().getData(KndcStorage.USER_ID));
//    jsonObject.put("expire", KndcStorage.getInstance().getData(KndcStorage.USER_EXPIRE_TIME));
//    jsonObject.put("phone", KndcStorage.getInstance().getData(KndcStorage.USER_PHONE));
    jsonObject.put("clientid", DeviceUtils.getDeviceId(context));
    jsonObject.put("version", DeviceUtils.getVerName(context));
    jsonObject.put("appid", DeviceUtils.getVersionCode(context));
    jsonObject.put("statusBarHeight", CommonUtil.getStatusBarHeight(context));
    jsonObject.put("titleBarHeight", CommonUtil.getTitleBarHeight(context));
    jsonObject.put("windowHeight", DeviceUtils.getDisplayHeight(context));
    jsonObject.put("systemInfo", DeviceUtils.getSystemInfo());

    return jsonObject;
  }
}
