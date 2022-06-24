package com.cashhub.cash.common.utils;

import android.text.TextUtils;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetUtil {
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
