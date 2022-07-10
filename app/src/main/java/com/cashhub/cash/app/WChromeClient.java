package com.cashhub.cash.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class WChromeClient extends WebChromeClient {
  private static final String TAG = "WChromeClient：";
  private Context _c;
  public WChromeClient(Context context) {
    super();
    _c = context;
  }

  @Override
  public void onProgressChanged(WebView view, int newProgress) {
    super.onProgressChanged(view, newProgress);
    Log.d(TAG,"当前加载进度：" + newProgress);
  }

  @Override
  public void onReceivedTitle(WebView view, String title) {
    super.onReceivedTitle(view, title);
    Log.d(TAG,"网站标题："+ title);
  }

  // 响应 js 的 alert() 函数
  @Override
  public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
    AlertDialog.Builder b = new AlertDialog.Builder(_c);
    b.setTitle("");
    b.setMessage(message);
    b.setPositiveButton(android.R.string.ok, (dialog, which) -> result.confirm());
    b.setCancelable(false);
    b.create().show();
    return true;
  }

  // 响应 js 的 confirm() 函数
  @Override
  public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
    AlertDialog.Builder b = new AlertDialog.Builder(_c);
    b.setTitle("");
    b.setMessage(message);
    b.setPositiveButton(android.R.string.ok, (dialog, which) -> result.confirm());
    b.setNegativeButton(android.R.string.cancel, (dialog, which) -> result.cancel());
    b.create().show();
    return true;
  }

  // 响应 js 的 prompt() 函数
  @Override
  public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
      final JsPromptResult result) {
    result.confirm();
    return super.onJsPrompt(view, url, message, message, result);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  @Override
  public void onPermissionRequest(PermissionRequest request) {
    request.grant(request.getResources());
  }

  // 获取 js 的 console 消息
  @Override
  public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
    Log.w(TAG,consoleMessage.message());
    return true;
  }
}
