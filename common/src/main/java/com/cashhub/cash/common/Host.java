package com.cashhub.cash.common;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

public class Host {
  private static final String TAG = "Host";
  private static int sIsDebugMode = -1;

  private static final String HOST_API_OA = "http://apishop.c99349d1eb3d045a4857270fb79311aa0.cn-shanghai.alicontainer.com/api";
  private static final String HOST_API = "https://api.cashhubloan.com/api";

  public static String getApiHost(Context context) {
    return getCustomApiHost(context);
  }

  private static String getCustomApiHost(Context context) {
    String host = HOST_API;
    boolean isDebug = isDebugMode(context);
    Log.d(TAG, "isDebug:" + isDebug);
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
