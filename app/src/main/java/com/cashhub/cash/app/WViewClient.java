package com.cashhub.cash.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.io.InputStream;

public class WViewClient extends WebViewClient {
  private static final String TAG = "WViewClient：";
  private Context _c;
  public WViewClient(Context context) {
    super();
    _c = context;
  }

  // ssl 证书错误
  @Override
  public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
    if (handler != null) {
      handler.proceed(); // 忽略证书的错误继续加载页面内容，不会变成空白页面
    }
  }

  @Override
  public void onReceivedError(WebView view, int errorCode,
      String description, String failingUrl) {
    Log.i(TAG, description);
  }

  @Override
  public void onPageFinished(WebView view, String url) {
    // 开始
    Log.e(TAG,"开始");
    super.onPageFinished(view, url);
  }

  @Override
  public void onPageStarted(WebView view, String url, Bitmap favicon) {
    // 结束
    Log.e(TAG,"结束");
    super.onPageStarted(view, url, favicon);
  }

  // 请求拦截
  @Override
  public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
    // 判断拦截资源的条件
    InterceptRes ir = interceptResources(url);
    if (ir != null) {
      try {
        // 获得需要替换的资源（存放在assets文件夹中，如何创建 assets 文件夹请看下文）
        InputStream inputStream = _c.getApplicationContext().getAssets().open(ir.assetsUrl);
        // 替换资源
        return new WebResourceResponse(ir.mimeType, "utf-8", inputStream);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return super.shouldInterceptRequest(view, url);
  }

  private InterceptRes interceptResources(String url) {
    InterceptRes interceptRes = null;
    switch (url){
      case "https://unpkg.com/element-ui/lib/theme-chalk/index.css":
        interceptRes = new InterceptRes("css/element-ui-index.css","text/css");
        break;
      case "https://unpkg.com/vue@2":
        interceptRes = new InterceptRes("js/vue.min.js","application/x-javascript");
        break;
      case "https://unpkg.com/vue-router@3":
        interceptRes = new InterceptRes("js/vue-router.js","application/x-javascript");
        break;
      case "https://unpkg.com/vuex@3":
        interceptRes = new InterceptRes("js/vuex.js","application/x-javascript");
        break;
      case "https://unpkg.com/element-ui/lib/index.js":
        interceptRes = new InterceptRes("js/element-ui-index.js","application/x-javascript");
        break;
      case "https://unpkg.com/axios/dist/axios.min.js":
        interceptRes = new InterceptRes("js/axios.min.js","application/x-javascript");
        break;
      default:
        break;
    }
    return interceptRes;
  }

  private class InterceptRes {
    String assetsUrl;
    String mimeType;
    InterceptRes(String assetsUrl,String mimeType) {
      this.assetsUrl = assetsUrl;
      this.mimeType = mimeType;
    }
  }
}