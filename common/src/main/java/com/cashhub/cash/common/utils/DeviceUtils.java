package com.cashhub.cash.common.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import com.alibaba.fastjson.JSONObject;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class DeviceUtils {

  private static final String TAG = "DeviceUtils";

  public static String getMac(Context context) {
    String mac = "";
    if (context == null) {
      return mac;
    }
    mac = getMacByJavaAPI();
    if (TextUtils.isEmpty(mac)){
      mac = getMacBySystemInterface(context);
    }
    return mac;

  }

  private static String getMacByJavaAPI() {
    try {
      Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements()) {
        NetworkInterface netInterface = interfaces.nextElement();
        if ("wlan0".equals(netInterface.getName()) || "eth0".equals(netInterface.getName())) {
          byte[] addr = netInterface.getHardwareAddress();
          if (addr == null || addr.length == 0) {
            return null;
          }
          StringBuilder buf = new StringBuilder();
          for (byte b : addr) {
            buf.append(String.format("%02X:", b));
          }
          if (buf.length() > 0) {
            buf.deleteCharAt(buf.length() - 1);
          }
          return buf.toString().toLowerCase(Locale.getDefault());
        }
      }
    } catch (Throwable e) {
    }
    return null;
  }

  private static String getMacBySystemInterface(Context context) {
    if (context == null) {
      return "";
    }
    try {
      WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
      if (context.checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) ==
          PackageManager.PERMISSION_GRANTED) {
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
      } else {
        return "";
      }
    } catch (Throwable e) {
      return "";
    }
  }

  /**
   * 获取设备号
   * @param context
   * @return
   */
  @SuppressLint("HardwareIds")
  public static String getDeviceId(Context context) {
    String deviceId = "";

    try {
      if (Build.VERSION.SDK_INT >= 29) {
        deviceId = Settings.Secure
            .getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
      } else {
        final TelephonyManager mTelephony = (TelephonyManager) context
            .getSystemService(Context.TELEPHONY_SERVICE);
        if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED) {
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
          deviceId = Settings.Secure
              .getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
      }
    } catch (Exception e) {

    }
    if(TextUtils.isEmpty(deviceId)) {
      deviceId = generateRandomStr(16);
    }
    return deviceId;
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
   * 手机系统版本
   */
  public static String getSdkVersion() {
    return android.os.Build.VERSION.RELEASE;
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
    jsonObject.put("getSdkVersion:", getSdkVersion());
    return jsonObject;
  }

  public static JSONObject getSystemInfo(Context context) {

    JSONObject jsonObject = new JSONObject();
//    jsonObject.put("key", KndcStorage.getInstance().getData(KndcStorage.USER_TOKEN));
//    jsonObject.put("user_uuid", KndcStorage.getInstance().getData(KndcStorage.USER_ID));
//    jsonObject.put("expire", KndcStorage.getInstance().getData(KndcStorage.USER_EXPIRE_TIME));
//    jsonObject.put("phone", KndcStorage.getInstance().getData(KndcStorage.USER_PHONE));
    jsonObject.put("clientid", DeviceUtils.getDeviceId(context));
//    jsonObject.put("version", DeviceUtils.getVerName(context));
//    jsonObject.put("appid", DeviceUtils.getVersionCode(context));
    jsonObject.put("statusBarHeight", CommonUtil.getStatusBarHeightDp(context));
    jsonObject.put("titleBarHeight", CommonUtil.getTitleBarHeight(context));
//    jsonObject.put("windowHeight", DeviceUtils.getDisplayHeight(context));
//    jsonObject.put("systemInfo", DeviceUtils.getSystemInfo());

    Log.d(TAG, "getSystemInfo: " + jsonObject.toString());

    return jsonObject;
  }

  /**
   * 获取随机字符串
   */
  private static String generateRandomStr(int length) {
    ArrayList<String> strList = new ArrayList<String>();
    Random random = new Random();

    //将0-9的数字加入集合
    for (int i = 0; i < 10; i++) {
      strList.add(i + "");
    }

    StringBuffer sb = new StringBuffer();
    int size = strList.size();
    for (int i = 0; i < length; i++) {
      String randomStr = strList.get(random.nextInt(size));
      sb.append(randomStr);
    }
    return sb.toString();
  }
}
