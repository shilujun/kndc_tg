package com.cashhub.cash.app;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import com.cashhub.cash.common.KndcEvent;
import org.greenrobot.eventbus.EventBus;

public class CommonApp {

  private static final String TAG = "CommonApp";
  public static void navigateTo(Context context, String url) {
    Log.d(TAG, "navigateTo url: " + url);
    Intent intent = new Intent();
    intent.setClass(context, BrowserActivity.class);
    intent.putExtra(BrowserActivity.PARAM_URL, url);
    intent.putExtra(BrowserActivity.PARAM_MODE, MainActivity.MODE_SONIC);
    intent.putExtra(SonicJavaScriptInterface.PARAM_CLICK_TIME, System.currentTimeMillis());
    context.startActivity(intent);
  }
  public static void navigateToInWebView(String url) {
    KndcEvent kndcEvent = new KndcEvent();
    kndcEvent.setEventName(KndcEvent.WEB_OPEN_NEW_LINK);
    kndcEvent.setUrl(url);
    EventBus.getDefault().post(kndcEvent);
  }

  /**
   * 打开登录页
   */
  public static void navigateToLogin(Context context) {
    Intent intent = new Intent();
    intent.setClass(context, LoginActivity.class);
    context.startActivity(intent);
  }

  /**
   * 前端通知授权
   */
  public static void beginPermission() {
    KndcEvent kndcEvent = new KndcEvent();
    kndcEvent.setEventName(KndcEvent.BEGIN_CHECK_PERMISSION);
    EventBus.getDefault().post(kndcEvent);
  }
}
