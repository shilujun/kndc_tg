package com.cashhub.cash.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
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
import androidx.annotation.NonNull;
import android.app.AlertDialog;
import com.alibaba.fastjson.JSONObject;
import com.cashhub.cash.app.model.Config;
import com.cashhub.cash.common.CommonApi;
import com.cashhub.cash.common.CommonResult;
import com.cashhub.cash.common.Host;
import com.cashhub.cash.common.KndcEvent;
import com.cashhub.cash.common.KndcStorage;
import com.cashhub.cash.common.utils.AdvertisingIdClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.bugly.crashreport.CrashReport;
import java.util.List;
import java.util.Map;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class WebviewActivity extends BaseActivity {

  private static final String TAG = "WebviewActivity";
  public final static String PARAM_URL = "param_url";

  private static boolean IS_INIT = false;

  private static boolean IS_SHOW_SSL_DIALOG = false;
  private Activity mActivity;

  private WebView mWebView;

  @SuppressLint("SetJavaScriptEnabled")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Intent intent = getIntent();
    String url = intent.getStringExtra(PARAM_URL);
    if (TextUtils.isEmpty(url)) {
      finish();
      return;
    }
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
    mActivity = this;

    setContentView(R.layout.activity_webview);
    mWebView = (WebView) findViewById(R.id.wv_web_view);

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
        //???????????????????????????
        Log.d(TAG, "mWebView onPageStarted");
      }

      @Override
      public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return null;
      }

      @Override
      public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
        Log.d(TAG, "onReceivedSslError");
        if(IS_SHOW_SSL_DIALOG) {
          return;
        }
        IS_SHOW_SSL_DIALOG = true;
        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage(R.string.notification_error_ssl_cert_invalid);
        builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Log.d(TAG, "onClick: continue");
            handler.proceed();
          }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Log.d(TAG, "onClick: cancel");
            handler.cancel();
            finish();
          }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
      }
    });

    //?????????????????????
    mWebView.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
          if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
          }
        }

        return false;
      }
    });

    WebSettings webSettings = mWebView.getSettings();

    // add java script interface
    // note:if api level lower than 17(android 4.2), addJavascriptInterface has security
    // issue, please use x5 or see https://developer.android.com/reference/android/webkit/
    // WebView.html#addJavascriptInterface(java.lang.Object, java.lang.String)
    mWebView.removeJavascriptInterface("jsInterface");

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
    webSettings.setAllowFileAccess(true); //????????????????????????
    webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //????????????JS???????????????
    webSettings.setLoadsImagesAutomatically(true); //????????????????????????


    mWebView.loadUrl(url);

    //??????????????????
    syncUserInfoToH5();


    initData();
  }

  /**
   * ?????????????????????
   */
  private void initData() {
    if(IS_INIT) {
      return;
    }
    IS_INIT = true;
    Log.d(TAG, "Begin initData");

    String appIsInit =
        KndcStorage.getInstance().getData(KndcStorage.APP_IS_INIT);
    Log.d(TAG, "appIsInit:" + appIsInit);

    new Thread(() -> {
      //?????????????????????????????????
      if (!TextUtils.isEmpty(appIsInit) && appIsInit.equals(KndcStorage.YSE)) {
        Log.d(TAG, "getCollectionStatus Start!");
        CommonApi.getInstance().getCollectionStatus(this);
      }
    }).start();
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (mWebView != null && keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
      mWebView.goBack();
      return true;
    }

    return super.onKeyDown(keyCode, event);
  }

  // ??????????????????
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
      mWebView.goBack();//??????????????????
      return;
    } else {
      if (System.currentTimeMillis() - exitTime > 2000) {
        Toast.makeText(getApplicationContext(), "????????????????????????", Toast.LENGTH_SHORT).show();
        exitTime = System.currentTimeMillis();
      } else {
        moveTaskToBack(true); // ?????????????????????????????????????????????
        // finish();
        // System.exit(0);
        // android.os.Process.killProcess(android.os.Process.myPid());
      }
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    Log.d(TAG,
        "onRequestPermissionsResult, requestCode:" + requestCode + ",permissions:" + permissions.toString() + ",grantResults:" + grantResults.toString());
  }

  @Override
  protected void onResume() {
    super.onResume();
    Log.d(TAG, Host.getH5Host(this, "/test.html"));
    //??????????????????
    syncUserInfoToH5();
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageEvent(KndcEvent event) {
    super.onMessageEvent(event);
    Log.d(TAG, "onMessageEvent: " + event.getEventName());
    if(KndcEvent.SYNC_USER_STATUS.equals(event.getEventName())) {
      //??????????????????
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
      Log.d(TAG, "gotoUrl: " + gotoUrl);

      if(mWebView != null) {
        String newUrl = parseRouter(Host.getH5Host(this, gotoUrl));
        Log.d(TAG, "newUrl: " + newUrl);
        mWebView.loadUrl(newUrl);
//        mWebView.loadUrl("https://www.163.com/gov/article/HC5TJGPT002398HK.html?clickfrom=w_yw_gov");
//        try {
//          Thread.sleep(500);
//        } catch (InterruptedException e) {
//          e.printStackTrace();
//        }
//        mWebView.reload();
      }
    } else if(KndcEvent.PERMISSION_END_CALL_JS.equals(event.getEventName())) {
      syncUserPermissionToH5(event.getPermission(), event.getType());
    } else if (KndcEvent.COLLECTION_STATUS.equals(event.getEventName())) {
      String commonRet = event.getCommonRet();
      if (TextUtils.isEmpty(commonRet)) {
        return;
      }
      Gson gson = new Gson();
      CommonResult commonResult = gson.fromJson(commonRet,
          new TypeToken<CommonResult>() {
          }.getType());
      if (commonResult == null || commonResult.getData() == null) {
        return;
      } else if(commonResult.getCode() != 0) {
        Log.d(TAG, "login fail");
        return;
      }

      Map<String, String> retData = commonResult.getData();

      //??????????????????
      String collection = retData.get("collection");
      Log.d(TAG, "collection:" + collection);
      if(!TextUtils.isEmpty(collection) && "true".equals(collection)) {
        //????????????
        Log.d(TAG, "onMessageEvent: collectDataAndUpload COLLECTION_STATUS");
        collectDataAndUpload();
//        //????????????????????? ?????????????????????
//        String appIsCheckPermission =
//        KndcStorage.getInstance().getData(KndcStorage.H5_IS_CHECK_PERMISSION);
//        if (!TextUtils.isEmpty(appIsCheckPermission) && appIsCheckPermission
//            .equals(KndcStorage.YSE)) {
//          //????????????
//          collectDataAndUpload();
//        }
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

  private void syncUserPermissionToH5(String permission, String type) {
    Log.d(TAG, "syncUserPermissionToH5, permission:" + permission + ",type:" + type);
    if(TextUtils.isEmpty(permission) || TextUtils.isEmpty(type)) {
      return;
    }
    Log.d(TAG, "permission:" + permission + ",type:" + type);
    if(mWebView != null) {
      mWebView
          .loadUrl("javascript:getPermissionActive('" + permission + "','" + type + "')");
    }
  }


  /**
   * ?????????#????????????????????????query???????????????????????????webView???????????????????????????
   * @param page ????????????
   * @return
   */
  private String parseRouter(String page) {
    String pageUrl = null;
    Uri uri = Uri.parse(page);
    String fragment = uri.getFragment();
    String query = uri.getQuery();
    if (fragment != null) {
      // ??????#????????????????????????(?) ??????&?????????
      String connector = fragment.contains("?") ? "&" : "?";
      if (query != null) {
        pageUrl = page.substring(0, page.indexOf("#")) + "&bnc_validate=true&time=" + System.currentTimeMillis() + "#" + fragment + connector;
      } else {
        pageUrl = page.substring(0, page.indexOf("#")) + "?bnc_validate=true&time=" + System.currentTimeMillis() + "#" + fragment + connector;
      }
    }
    return pageUrl;
  }
}