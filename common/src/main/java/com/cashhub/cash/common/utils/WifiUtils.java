package com.cashhub.cash.common.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import com.alibaba.fastjson.JSONObject;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * WIFI
 */
public class WifiUtils {

  /**
   * @return
   */
  public static JSONObject getWifiInfo(Context context) {
    JSONObject wifiInfo = new JSONObject();
    wifiInfo.put("networkid", getWifiManager(context).getConnectionInfo().getNetworkId());
    wifiInfo.put("ssid", getWifiManager(context).getConnectionInfo().getSSID());
    wifiInfo.put("bssid",getWifiManager(context).getConnectionInfo().getBSSID());
    wifiInfo.put("mac_address", getRealWifiMacAddress());

    return wifiInfo;
  }

  private static WifiManager getWifiManager(Context context){
    return (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
  }

  /**
   * 获取 wifi mac地址 安卓6.0以上getMacAddress 获取mac值为：02:00:00:00:00:00 需要从文件读取
   */
  private static String getRealWifiMacAddress() {
    String macAddress = "";
    try {
      Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      while (((Enumeration) interfaces).hasMoreElements()) {
        NetworkInterface iF = interfaces.nextElement();

        byte[] addr = iF.getHardwareAddress();
        if (addr == null || addr.length == 0) {
          continue;
        }

        StringBuilder buf = new StringBuilder();
        for (byte b : addr) {
          buf.append(String.format("%02X:", b));
        }
        if (buf.length() > 0) {
          buf.deleteCharAt(buf.length() - 1);
        }
        String mac = buf.toString();

        if (TextUtils.equals(iF.getName(), "wlan0")) {
          macAddress = mac;
        }
      }
    } catch (SocketException e) {
      e.printStackTrace();
    }

    return macAddress;
  }

}