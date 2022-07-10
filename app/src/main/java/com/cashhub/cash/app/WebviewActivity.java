package com.cashhub.cash.app;

import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.text.TextUtils;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;
import com.alibaba.fastjson.JSONObject;
import com.cashhub.cash.common.CommonResult;
import com.cashhub.cash.common.Host;
import com.cashhub.cash.common.KndcEvent;
import com.cashhub.cash.common.KndcStorage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.Map;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class WebviewActivity extends BaseActivity {

  private static final String TAG = "WebviewActivity";
  public final static String PARAM_URL = "param_url";

  private BaseWebView mWebView;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Intent intent = getIntent();
    String url = intent.getStringExtra(PARAM_URL);
    if (TextUtils.isEmpty(url)) {
      finish();
      return;
    }
    mWebView = new BaseWebView(this, url);

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
    mWebView.addJavascriptInterface(new JSInterface(this, mWebView), "jsInterface");

    // init webview settings
    webSettings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
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


    setContentView(mWebView);

    //用户登录信息
    syncUserInfoToH5();
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
  protected void onResume() {
    super.onResume();
    Log.d(TAG, "onResume!!!!");
    //用户登录信息
    syncUserInfoToH5();
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageEvent(KndcEvent event) {
    super.onMessageEvent(event);
    Log.d(TAG, "onMessageEvent: " + event.getEventName());
    if(KndcEvent.SYNC_USER_STATUS.equals(event.getEventName())) {
      //用户登录信息
      syncUserInfoToH5();
    } else if(KndcEvent.WEB_OPEN_NEW_LINK.equals(event.getEventName())) {
      String url = event.getUrl();
      if(TextUtils.isEmpty(url)) {
        return;
      }

      if(mWebView != null) {
        mWebView.loadUrl(url);
      }
    } else if(KndcEvent.UPLOAD_END_CALL_JS.equals(event.getEventName())) {
      String lineType = KndcStorage.getInstance().getData(LINE_TYPE);
      String uploadType = KndcStorage.getInstance().getData(UPLOAD_TYPE);
      String commonRet = event.getCommonRet();
      Log.d(TAG, "lineType: " + lineType + ",uploadType:" + uploadType + ",commonRet:" + commonRet);

      if (TextUtils.isEmpty(commonRet)) {
        return;
      }
      Gson gson = new Gson();
      CommonResult commonResult = gson.fromJson(commonRet,
          new TypeToken<CommonResult>() {
          }.getType());
      if (commonResult == null) {
        showToastLong("RESULT IS NULL");
        return;
      }
      Map retData = commonResult.getData();
      String gotoUrl = "";
      if ("living" .equals(lineType) && commonResult.getCode() == 0 && retData != null &&
          "Y" .equals(retData.get("status"))) {
        showToastLong(commonResult.getMsg());
        gotoUrl = "/#/pagesB/pages/face_recog/face_result?result=success";
      } else if ("ocr" .equals(lineType) && commonResult.getCode() == 0) {
        showToastLong(commonResult.getMsg());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("/#/pagesB/pages/card_auth/suc_card?sign_url=");
        stringBuilder.append(Host.getApiHost(this));
        stringBuilder.append("/api/v1/ocr/image-info");
        stringBuilder.append("&upload_type=");
        stringBuilder.append(uploadType);
        stringBuilder.append("&card_data=");
        stringBuilder.append(new JSONObject(retData));
        gotoUrl = stringBuilder.toString();
      } else {
        showToastLong(commonResult.getMsg());
        if ("living".equals(lineType)) {
          gotoUrl = "/#/pagesB/pages/face_recog/face_result?result=error";
        } else {
          gotoUrl = "/#/pagesB/pages/card_auth/err_card";
        }
      }

      if(mWebView != null) {
        mWebView.loadUrl(Host.getH5Host(this, gotoUrl));
      }
    }
  }

  private void syncUserInfoToH5() {
    String userPhone = KndcStorage.getInstance().getData(KndcStorage.USER_PHONE);
    String userToken = KndcStorage.getInstance().getData(KndcStorage.USER_TOKEN);
    String userId = KndcStorage.getInstance().getData(KndcStorage.USER_ID);
    String userExpire = KndcStorage.getInstance().getData(KndcStorage.USER_EXPIRE_TIME);
    Log.d(TAG, "userPhone:" + userPhone + ",userToken:" + userToken + ",userId:" + userId
        + ",userExpire:" + userExpire);
    if(mWebView != null) {
      mWebView
          .loadUrl("javascript:syncUserInfo('" + userPhone + "','" + userToken + "','" + userId +
              "','" + userExpire + "')");
    }
  }
}