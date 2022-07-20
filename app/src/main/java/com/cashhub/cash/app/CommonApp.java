package com.cashhub.cash.app;

import android.Manifest.permission;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import com.cashhub.cash.common.KndcEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.greenrobot.eventbus.EventBus;

public class CommonApp {

  public static Map<String, String> permissionsCallback = new HashMap<>();
  public static List<String> permissionsList = new ArrayList<>();

  private static final String TAG = "CommonApp";
  public static void navigateTo(Context context, String url) {
    Log.d(TAG, "navigateTo url: " + url);
    Intent intent = new Intent();
//    intent.setClass(context, BrowserActivity.class);
//    intent.putExtra(BrowserActivity.PARAM_URL, url);
//    intent.putExtra(BrowserActivity.PARAM_MODE, MainActivity.MODE_SONIC);
//    intent.putExtra(SonicJavaScriptInterface.PARAM_CLICK_TIME, System.currentTimeMillis());
    intent.setClass(context, WebviewActivity.class);
    intent.putExtra(WebviewActivity.PARAM_URL, url);
//    intent.putExtra(WebviewActivity.PARAM_MODE, MainActivity.MODE_SONIC);
//    intent.putExtra(SonicJavaScriptInterface.PARAM_CLICK_TIME, System.currentTimeMillis());
    context.startActivity(intent);
  }
  public static void navigateToInWebView(String url) {
    KndcEvent kndcEvent = new KndcEvent();
    kndcEvent.setEventName(KndcEvent.WEB_OPEN_NEW_LINK);
    kndcEvent.setUrl(url);
    EventBus.getDefault().post(kndcEvent);
  }

  public static void navigateToInWeb(Context context, String url, String title) {
    Log.d(TAG, "navigateTo url: " + url);
    Intent intent = new Intent();
    intent.setClass(context, WebActivity.class);
    intent.putExtra(WebActivity.PARAM_URL, url);
    intent.putExtra(WebActivity.PARAM_TITLE, title);
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

  /**
   * 前端通知授权
   */
  public static void beginPermission() {
    KndcEvent kndcEvent = new KndcEvent();
    kndcEvent.setEventName(KndcEvent.BEGIN_CHECK_PERMISSION);
    EventBus.getDefault().post(kndcEvent);
  }

  /**
   * 前端主动触发数据上报
   */
  public static void jsCallUploadData() {
    KndcEvent kndcEvent = new KndcEvent();
    kndcEvent.setEventName(KndcEvent.JS_CALL_UPLOAD_DATA);
    EventBus.getDefault().post(kndcEvent);
  }

  public static void initPermissions() {
    permissionsCallback = new HashMap<>();
//    CommonApp.permissionsCallback.put(permission.READ_PHONE_STATE, "device");
    CommonApp.permissionsCallback.put(permission.READ_CONTACTS, "contact");
    CommonApp.permissionsCallback.put(permission.READ_SMS, "message");
    CommonApp.permissionsCallback.put(permission.READ_CALENDAR, "calendar");
    CommonApp.permissionsCallback.put(permission.ACCESS_FINE_LOCATION, "map");
    CommonApp.permissionsCallback.put(permission.CAMERA, "camera");
    CommonApp.permissionsCallback.put(permission.WRITE_EXTERNAL_STORAGE, "storage"); //存储

    permissionsList = new ArrayList<>();
    CommonApp.permissionsList.add(permission.CHANGE_WIFI_STATE);
    CommonApp.permissionsList.add(permission.WRITE_EXTERNAL_STORAGE);
    CommonApp.permissionsList.add(permission.CAMERA);
    CommonApp.permissionsList.add(permission.ACCESS_NETWORK_STATE);
    CommonApp.permissionsList.add(permission.ACCESS_FINE_LOCATION);
    CommonApp.permissionsList.add(permission.ACCESS_COARSE_LOCATION);
    CommonApp.permissionsList.add(permission.READ_PHONE_STATE);
    CommonApp.permissionsList.add(permission.READ_SMS);
    CommonApp.permissionsList.add(permission.READ_CALENDAR);
    CommonApp.permissionsList.add(permission.READ_CONTACTS);
    CommonApp.permissionsList.add(permission.INTERNET);
  }
}