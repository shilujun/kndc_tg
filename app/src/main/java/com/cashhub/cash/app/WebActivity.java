package com.cashhub.cash.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WebActivity extends BaseActivity implements View.OnClickListener {

  private static final String TAG = "WebActivity";
  public final static String PARAM_URL = "param_url";

  public final static String PARAM_TITLE = "param_title";

  private WebView mWebView;

  private LinearLayout lltBack;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_web);
    Intent intent = getIntent();
    String url = intent.getStringExtra(PARAM_URL);
    String title = intent.getStringExtra(PARAM_TITLE);
    if (TextUtils.isEmpty(url)) {
      finish();
      return;
    }

    lltBack = findViewById(R.id.llt_back);
    lltBack.setOnClickListener(this);

    mWebView = findViewById(R.id.wv_web_view);
    mWebView.loadUrl(url);

    if(TextUtils.isEmpty(title)) {
      title = "";
    }
    TextView tvTitle = findViewById(R.id.tv_title);
    tvTitle.setText(title);

    getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
    mWebView.setWebViewClient(new WebViewClient() {
      @Override
      public void onPageFinished(WebView view, String url) {
        Log.d(TAG, "mWebView onPageFinished");
        super.onPageFinished(view, url);
      }

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
      public void onPageStarted(WebView view, String url, Bitmap favicon) {
        //设定加载开始的操作
        Log.d(TAG, "mWebView onPageStarted");
      }

      @Override
      public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return null;
      }

      @Override
      public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        /* 不要使用super，否则有些手机访问不了，因为包含了一条 handler.cancel()
                   super.onReceivedSslError(view, handler, error);
                   接受所有网站的证书，忽略SSL错误，执行访问网页 */
        handler.proceed();
      }
    });

    WebSettings webSettings = mWebView.getSettings();

    // add java script interface
    // note:if api level lower than 17(android 4.2), addJavascriptInterface has security
    // issue, please use x5 or see https://developer.android.com/reference/android/webkit/
    // WebView.html#addJavascriptInterface(java.lang.Object, java.lang.String)
    mWebView.removeJavascriptInterface("searchBoxJavaBridge_");

//    Intent intent = getIntent();
//    mWebView.addJavascriptInterface(new JSInterface(this, mWebView), "jsInterface");

    // init webview settings
    webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
    webSettings.setBlockNetworkImage(false);
    webSettings.setAllowContentAccess(true);
    webSettings.setDatabaseEnabled(true);
    webSettings.setDomStorageEnabled(true);
    webSettings.setAppCacheEnabled(true);
    webSettings.setSaveFormData(false);
    webSettings.setUseWideViewPort(true);
    webSettings.setLoadWithOverviewMode(true);
    webSettings.setJavaScriptEnabled(true);
    webSettings.setAllowFileAccess(true); //设置可以访问文件
    webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
    webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
  }

  // 程序退出销毁
  @Override
  protected void onDestroy() {
    if (this.mWebView != null) {
      mWebView.removeAllViews();
      mWebView.destroy();
    }
    super.onDestroy();
  }

  long exitTime = 0;
  @Override
  public void onBackPressed() {
    if (mWebView.canGoBack()) {
      mWebView.goBack();//返回上一页面
      return;
    } else {
      if (System.currentTimeMillis() - exitTime > 2000) {
        Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
        exitTime = System.currentTimeMillis();
      } else {
        moveTaskToBack(true); // 返回主页面，也可以完全退出程序
        // finish();
        // System.exit(0);
        // android.os.Process.killProcess(android.os.Process.myPid());
      }
    }
  }
  @Override
  public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.llt_back) {
      //后退按钮点击
      this.finish();
    }
  }
}