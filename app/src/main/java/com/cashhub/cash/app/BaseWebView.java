package com.cashhub.cash.app;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import androidx.annotation.NonNull;

public class BaseWebView extends WebView {
  private static final String TAG = "BaseWebView：";
  public WChromeClient wChromeClient;
  public BaseWebView(@NonNull Context context, String url) {
    super(context);
    wChromeClient = new WChromeClient(context);
    initWebView(context, url);
  }

  // 初始化
  private void initWebView(Context context, String url) {
    this.setBackgroundColor(0); // 设置背景
    this.setDrawingCacheEnabled(true); // 启用或禁用图形缓存
    this.setWebViewClient(new WViewClient(context)); // 处理各种通知、请求事件
    this.setWebChromeClient(wChromeClient); // 处理解析，渲染网页
    this.addJavascriptInterface(new JSInterface(context, this),"jsWebView"); // 设置 js 调用接口
    WebSettings settings = this.getSettings(); // webView 配置项
    settings.setUseWideViewPort(true); // 是否启用对视口元标记的支持
    settings.setJavaScriptEnabled(true); // 是否启用 JavaScript

    settings.setDomStorageEnabled(true); // 是否启用本地存储（允许使用 localStorage 等）
    settings.setAllowFileAccess(true); // 是否启用文件访问

    settings.setAppCacheEnabled(true); // 是否应启用应用程序缓存
    settings.setCacheMode(WebSettings.LOAD_DEFAULT);
    settings.setAppCacheMaxSize(1024*1024*8); // 设置应用程序缓存内容的最大大小
    String appCachePath = context.getApplicationContext().getCacheDir().getAbsolutePath(); // 缓存地址
    settings.setAppCachePath(appCachePath); // 设置缓存地址

    settings.setAllowContentAccess(true); // 是否启用内容 URL 访问
    settings.setJavaScriptCanOpenWindowsAutomatically(true); // 是否允许 JS 弹窗
    settings.setMediaPlaybackRequiresUserGesture(false); // 是否需要用户手势来播放媒体

    settings.setLoadWithOverviewMode(true); // 是否以概览模式加载页面，即按宽度缩小内容以适应屏幕
    settings.setBuiltInZoomControls(true); // 是否应使用其内置的缩放机制

    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
      // Hide the zoom controls for HONEYCOMB+
      settings.setDisplayZoomControls(false); // 是否应显示屏幕缩放控件
    }

    settings.setAllowFileAccessFromFileURLs(true); // 是否应允许在文件方案 URL 上下文中运行的 JavaScript 访问来自其他文件方案 URL 的内容
    settings.setAllowUniversalAccessFromFileURLs(true); // 是否应允许在文件方案URL上下文中运行的 JavaScript 访问任何来源的内容
    this.loadUrl(url); // 设置访问地址
  }

  // 注入 js 脚本
  public void injection(String js) {
    this.post(() -> this.loadUrl("javascript:" + js + ";",null));
  }

  // 执行 js 脚本
  public void executeMethod(String method, String data) {
    this.post(() -> this.loadUrl("javascript:" + method + "('" + data + "');",null));
  }
}
