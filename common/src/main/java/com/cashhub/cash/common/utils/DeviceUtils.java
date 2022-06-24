package com.cashhub.cash.common.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

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
}
