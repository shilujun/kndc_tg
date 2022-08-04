package com.cashhub.cash.common;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

public class Host {
  private static final String TAG = "Host";
  private static int sIsDebugMode = -1;

  //在线客服
  public static final String HOST_CUSTOMER_SERVICE = "https://lygapp.s5.udesk"
      + ".cn/im_client/?web_plugin_id=576&language=th&channel=CashHub&bnc_validate=true";
  //注册协议
  public static final String HOST_USER_AGREEMENT = "https://www.cashhubloan.com/register_agreement.html?bnc_validate=true";
  //隐私政策
  public static final String HOST_PRIVACY = "https://www.cashhubloan.com/privacy_new.html?bnc_validate=true";
  private static final String HOST_API_OA = "http://apishop.c99349d1eb3d045a4857270fb79311aa0.cn-shanghai.alicontainer.com";
  private static final String HOST_API = "https://api.cashhubloan.com";
  private static final String HOST_H5_OA = "http://kndc.junya.online/new_h5";
  private static final String HOST_H5 = "https://web.cashhubloan.com/new_h5";

  public static String getH5Host(Context context, String url) {
    return getCustomH5Host(context)  + url;
  }

  private static String getCustomH5Host(Context context) {
    String host = HOST_H5;
//    String host = HOST_H5_OA;
//    boolean isDebug = isDebugMode(context);
//    Log.d(TAG, "isDebug:" + isDebug);
//    if(isDebug) {
//      host = HOST_H5_OA;
//    }
    return host;
  }

  public static String getApiHost(Context context) {
    return getCustomApiHost(context);
  }

  private static String getCustomApiHost(Context context) {
    String host = HOST_API;
//    String host = HOST_API_OA;
//    boolean isDebug = isDebugMode(context);
//    Log.d(TAG, "isDebug:" + isDebug);
//    if(isDebug) {
//      host = HOST_API_OA;
//    }
    return host;
  }

  /**
   * 获取调试类型
   * @return debug包默认是0，包默认是 TYPE_RELEASE
   */
  public static boolean isDebugMode(Context context) {
    if (sIsDebugMode == -1) {
      boolean isDebug = context.getApplicationInfo() != null
          && (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
      sIsDebugMode = isDebug ? 1 : 0;
    }
    return sIsDebugMode == 1;
  }

}
