package com.cashhub.cash.app;

import android.content.Context;
import android.content.Intent;

public class CommonApp {

  private static final String TAG = "CommonApp";
  public static void navigateTo(Context context, String url) {
    Intent intent = new Intent();
    intent.setClass(context, BrowserActivity.class);
    intent.putExtra(BrowserActivity.PARAM_URL, url);
    intent.putExtra(BrowserActivity.PARAM_MODE, MainActivity.MODE_SONIC);
    context.startActivity(intent);
  }

  /**
   * 打开登录页
   */
  public static void navigateToLogin(Context context) {
    Intent intent = new Intent();
    intent.setClass(context, LoginActivity.class);
    context.startActivity(intent);
  }
}
