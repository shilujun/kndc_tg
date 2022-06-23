package com.cashhub.cash.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.view.View;
import com.housenkui.sdbridgejava.WebViewJavascriptBridge;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class WebviewActivity extends BaseActivity implements View.OnClickListener {

  private static final String TAG = "WebviewActivity";
  private WebViewJavascriptBridge bridge;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_webview);
    setupView();
//    //获得控件
//    WebView webView = (WebView) findViewById(R.id.wv_webview);
//    //访问网页
//    webView.loadUrl("https://www.baidu.com/");
//    //系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
//    webView.setWebViewClient(new WebViewClient(){
//      @Override
//      public boolean shouldOverrideUrlLoading(WebView view, String url) {
//        //使用WebView加载显示url
//        view.loadUrl(url);
//        //返回true
//        return true;
//      }
//    });
  }

  private void setupView() {
    WebView webView = findViewById(R.id.wv_webview);
    setAllowUniversalAccessFromFileURLs(webView);
    Button buttonSync = findViewById(R.id.buttonSync);
    Button buttonAsync = findViewById(R.id.buttonAsync);
    buttonSync.setOnClickListener(this);
    buttonAsync.setOnClickListener(this);

    bridge = new WebViewJavascriptBridge(this, webView);
    bridge.consolePipe(string -> {
      System.out.println("333333333");
      System.out.println(string);
    });
    bridge.register("DeviceLoadJavascriptSuccess", (map, callback) -> {
      System.out.println("Next line is javascript data->>>");
      System.out.println(map);
      HashMap<String,String> result = new HashMap<>();
      result.put("result","Android");
      callback.call(result);
    });

    WebSettings webSettings = webView.getSettings();

    // add java script interface
    // note:if api level lower than 17(android 4.2), addJavascriptInterface has security
    // issue, please use x5 or see https://developer.android.com/reference/android/webkit/
    // WebView.html#addJavascriptInterface(java.lang.Object, java.lang.String)
    webSettings.setJavaScriptEnabled(true);
    webView.loadUrl("https://www.jianshu.com/p/716b5bc40471");
    //系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
    webView.setWebViewClient(new WebViewClient(){
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        String url = request.getUrl().toString();

        if(url.startsWith("http:") || url.startsWith("https:") ) {
          view.loadUrl(url);
          return false;
        }else{
          Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
          startActivity(intent);
          return true;
        }
      }

      @Override
      public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        /* 不要使用super，否则有些手机访问不了，因为包含了一条 handler.cancel()
                   super.onReceivedSslError(view, handler, error);
                   接受所有网站的证书，忽略SSL错误，执行访问网页 */
        handler.proceed();
      }
      @Override
      public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Log.d(TAG,"onPageStarted");
        bridge.injectJavascript();
      }
      @Override
      public void onPageFinished(WebView view, String url) {
        Log.d(TAG,"onPageFinished");
      }
    });
  }

  //Allow Cross Domain
  private void setAllowUniversalAccessFromFileURLs (WebView webView){
    try {
      Class<?> clazz = webView.getSettings().getClass();
      Method method = clazz.getMethod(
          "setAllowUniversalAccessFromFileURLs", boolean.class);
      method.invoke(webView.getSettings(), true);
    } catch (IllegalArgumentException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onClick(View view) {
    if (view.getId() == R.id.buttonSync) {
      Log.d(TAG,"buttonSync onClick");
      HashMap<String, String> data = new HashMap<>();
      data.put("AndroidKey00","AndroidValue00");
      //call js sync function
      bridge.call("GetToken", data, map -> {
        System.out.println("Next line is javascript data->>>");
        System.out.println(map);
      });
    }else if(view.getId() == R.id.buttonAsync){
      Log.d(TAG,"buttonAsync onClick");
      HashMap<String, String> data = new HashMap<>();
      data.put("AndroidKey01","AndroidValue01");
      //call js Async function
      bridge.call("AsyncCall", data, map -> {
        System.out.println("Next line is javascript data->>>");
        System.out.println(map);
      });
    }
  }
}