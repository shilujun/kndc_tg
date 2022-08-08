package com.cashhub.cash.common.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import com.alibaba.fastjson.JSONObject;
import com.cashhub.cash.common.KndcStorage;
import java.io.IOException;
import java.io.InputStream;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Random;

public class DeviceUtils {

  private static final String TAG = "DeviceUtils";

  public static String getMac(Context context) {
    String mac = "";
    if (context == null) {
      return mac;
    }
    mac = getMacByJavaAPI();
    if (TextUtils.isEmpty(mac)) {
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
    if (TextUtils.isEmpty(deviceId)) {
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


  // 获取CPU最大频率
  // "/system/bin/cat" 命令行
  // "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" 存储最大频率的文件的路径
  public static String getMaxCpuFreq() {
    String result = "";
    ProcessBuilder cmd;
    try {
      String[] args = {"/system/bin/cat",
          "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"};
      cmd = new ProcessBuilder(args);
      Process process = cmd.start();
      InputStream in = process.getInputStream();
      byte[] re = new byte[24];
      while (in.read(re) != -1) {
        result = result + new String(re);
      }
      in.close();
    } catch (IOException ex) {
      ex.printStackTrace();
      result = "N/A";
    }
    return result.trim();
  }


  // 获取CPU最小频率
  public static String getMinCpuFreq() {
    String result = "";
    ProcessBuilder cmd;
    try {
      String[] args = {"/system/bin/cat",
          "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"};
      cmd = new ProcessBuilder(args);
      Process process = cmd.start();
      InputStream in = process.getInputStream();
      byte[] re = new byte[24];
      while (in.read(re) != -1) {
        result = result + new String(re);
      }
      in.close();
    } catch (IOException ex) {
      ex.printStackTrace();
      result = "N/A";
    }
    return result.trim();
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

  /**
   * 获取系统信息
   */
  public static JSONObject getSystemInfoByJs(Context context) {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("clientid", DeviceUtils.getDeviceId(context));
    jsonObject.put("statusBarHeight", CommonUtil.getStatusBarHeightDp(context));
    jsonObject.put("titleBarHeight", CommonUtil.getTitleBarHeight(context));

    Log.d(TAG, "getSystemInfoByJs: " + jsonObject.toString());

    return jsonObject;
  }

  /**
   * 这个方法是耗时的，不能在主线程调用
   */
  public static JSONObject getSystemInfoReport(Context context) {
    Log.d(TAG, "getSystemInfoReport  Start!!!");
    String jsonData = KndcStorage.getInstance().getData(KndcStorage.DEVICE_INFO_FROM_JS);
    Log.d(TAG, "getSystemInfoReport  jsonData: " + jsonData);

    JSONObject jsonObject = new JSONObject();
    if (!TextUtils.isEmpty(jsonData)) {
      try {
        jsonObject = JSONObject.parseObject(jsonData);
      } catch (Exception e) {
        Log.d(TAG, "JSONObject.parseObject err: " + e.getMessage());
      }

    }
    if (jsonData == null) {
      jsonObject = new JSONObject();
    }

    try {
      jsonObject.put("brand", Build.BRAND);
      jsonObject.put("appId", CommonUtil.getApplicationId(context));
      jsonObject.put("appVersion", CommonUtil.getVersionName(context));
      jsonObject.put("appVersionCode", CommonUtil.getVersionCode(context));
      jsonObject.put("deviceId", DeviceUtils.getDeviceId(context));
      jsonObject.put("fontSizeSetting", getFontSize(context));
      //cpu 指定指令集
      jsonObject.put("cpu_abi", getABIs());
      //cpu  核数、cpu 最小频率、cpu 最大频率
      jsonObject.put("cpu", getCPU());
      jsonObject.put("memory", getMemory());
      //sim卡状态、 imei1、 imei2
      jsonObject.put("telephony", PhoneUtils.getTelephony(context));

      try {
        jsonObject.put("wifi", WifiUtils.getWifiInfo(context));
      } catch (Exception e) {
        Log.d(TAG, "WifiUtils.getWifiInfo err: " + e.getMessage());
      }
      jsonObject.put("bluetooth", getBtAddressMacJson(context));
      try {
//      com.google.android.gms.ads.identifier.AdvertisingIdClient.Info adInfo =
//          com.google.android.gms.ads.identifier.AdvertisingIdClient.getAdvertisingIdInfo(context);
//      jsonObject.put("adid", adInfo.getId());
        jsonObject.put("adid", AdvertisingIdClient.getGoogleAdId(context));
      } catch (Exception e) {
        jsonObject.put("adid", "");
        Log.d(TAG, "AdvertisingIdClient.getGoogleAdId err: " + e.getMessage());
      }
    } catch (Exception e) {

    }

//    JSONObject jsonDevice = new JSONObject();
//    jsonDevice.put("device", jsonObject);

    JSONObject jsonReportData = new JSONObject();
    jsonReportData.put("device_info", jsonObject);

    Log.d(TAG, "getSystemInfoReport: " + jsonReportData);
    return jsonReportData;
  }

  public static float getFontSize(Context context) {

    Configuration mCurConfig = new Configuration();

    try {

      mCurConfig.updateFrom(context.getResources().getConfiguration());

    } catch (Exception e) {

      Log.w(TAG, "Unable to retrieve font size");

    }



    Log.w(TAG, "getFontSize(), Font size is " + mCurConfig.fontScale);

    return mCurConfig.fontScale;



  }

  /**
   * 获取 bluetooth Mac
   * @return
   */
  private static JSONObject getBtAddressMacJson(Context context) {
    JSONObject btAddress = new JSONObject();
    btAddress.put("mac", getBtAddressMac(context));
    return btAddress;
  }

  /**
   * 获取 bluetooth Mac
   * @return
   */
  private static String getBtAddressMac(Context context) {
    String bluetoothMacAddress = "";
    try {
      bluetoothMacAddress =
          android.provider.Settings.Secure.getString(context.getContentResolver(), "bluetooth_address");
    } catch (Exception e) {
    }
    if (TextUtils.isEmpty(bluetoothMacAddress)) {
      bluetoothMacAddress = "02:00:00:00:00:00";
    }
    return bluetoothMacAddress;
  }

  /**
   * 获取
   *
   * @return
   */
  private static String[] getABIs() {
    if (!TextUtils.isEmpty(Build.CPU_ABI2)) {
      return new String[]{Build.CPU_ABI, Build.CPU_ABI2};
    }
    return new String[]{Build.CPU_ABI};
  }

  /**
   * cpu  核数、cpu 最小频率、cpu 最大频率
   *
   * @return
   */
  private static JSONObject getCPU() {
    JSONObject cpu = new JSONObject();
    cpu.put("cpu_processors_count", Runtime.getRuntime().availableProcessors());
    cpu.put("cpu_min_freq", getMinCpuFreq());
    cpu.put("cpu_max_freq", getMaxCpuFreq());
    return cpu;
  }

  /**
   * ram 大小 单位kB
   * rom 大小 单位kB
   *
   * @return
   */
  private static JSONObject getMemory() {
    JSONObject cpu = new JSONObject();
    cpu.put("ram_total", MemoryUtils.getTotalMemory());

    try {
      long romTotal = MemoryUtils.getTotalInternalMemorySize() / 1024;
      cpu.put("rom_total", romTotal + "");
    } catch (Exception e) {
      cpu.put("rom_total", "0");

      e.printStackTrace();
    }
    return cpu;
  }

  /**
   * 获取随机字符串
   */
  public static String generateRandomStr(int length) {
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
