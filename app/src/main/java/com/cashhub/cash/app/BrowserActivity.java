package com.cashhub.cash.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.alibaba.fastjson.JSONObject;
import com.cashhub.cash.common.CommonResult;
import com.cashhub.cash.common.Host;
import com.cashhub.cash.common.KndcEvent;
import com.cashhub.cash.common.KndcStorage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.sonic.sdk.SonicCacheInterceptor;
import com.tencent.sonic.sdk.SonicConfig;
import com.tencent.sonic.sdk.SonicConstants;
import com.tencent.sonic.sdk.SonicEngine;
import com.tencent.sonic.sdk.SonicSession;
import com.tencent.sonic.sdk.SonicSessionConfig;
import com.tencent.sonic.sdk.SonicSessionConnection;
import com.tencent.sonic.sdk.SonicSessionConnectionInterceptor;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 *  A demo browser activity
 *  In this demo there are three modes,
 *  sonic mode: sonic mode means webview loads html by sonic,
 *  offline mode: offline mode means webview loads html from local offline packages,
 *  default mode: default mode means webview loads html in the normal way.
 *
 */

public class BrowserActivity extends BaseActivity {
  public final static String PARAM_URL = "param_url";

  public final static String PARAM_MODE = "param_mode";
  private static final String TAG = "BrowserActivity";

  private SonicSession sonicSession;
  WebView mWebView;

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (mWebView != null && keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
      mWebView.goBack();
      return true;
    }

    return super.onKeyDown(keyCode, event);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Intent intent = getIntent();
    String url = intent.getStringExtra(PARAM_URL);
    int mode = intent.getIntExtra(PARAM_MODE, -1);
    if (TextUtils.isEmpty(url) || -1 == mode) {
      finish();
      return;
    }

    getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

    // init sonic engine if necessary, or maybe u can do this when application created
    if (!SonicEngine.isGetInstanceAllowed()) {
      SonicEngine.createInstance(new SonicRuntimeImpl(getApplication()), new SonicConfig.Builder().build());
    }

    SonicSessionClientImpl sonicSessionClient = null;

    // if it's sonic mode , startup sonic session at first time
    if (MainActivity.MODE_DEFAULT != mode) { // sonic mode
      SonicSessionConfig.Builder sessionConfigBuilder = new SonicSessionConfig.Builder();
      sessionConfigBuilder.setSupportLocalServer(true);

      // if it's offline pkg mode, we need to intercept the session connection
      if (MainActivity.MODE_SONIC_WITH_OFFLINE_CACHE == mode) {
        sessionConfigBuilder.setCacheInterceptor(new SonicCacheInterceptor(null) {
          @Override
          public String getCacheData(SonicSession session) {
            return null; // offline pkg does not need cache
          }
        });

        sessionConfigBuilder.setConnectionInterceptor(new SonicSessionConnectionInterceptor() {
          @Override
          public SonicSessionConnection getConnection(SonicSession session, Intent intent) {
            return new OfflinePkgSessionConnection(BrowserActivity.this, session, intent);
          }
        });
      }

      // create sonic session and run sonic flow
      sonicSession = SonicEngine.getInstance().createSession(url, sessionConfigBuilder.build());
      if (null != sonicSession) {
        sonicSession.bindClient(sonicSessionClient = new SonicSessionClientImpl());
      } else {
        // this only happen when a same sonic session is already running,
        // u can comment following codes to feedback as a default mode.
        // throw new UnknownError("create session fail!");
        Toast.makeText(this, "create sonic session fail!", Toast.LENGTH_LONG).show();
      }
    }

    // start init flow ...
    // in the real world, the init flow may cost a long time as startup
    // runtime、init configs....
    setContentView(R.layout.activity_browser);

//    Button btnFab = (Button) findViewById(R.id.btn_refresh);
//    btnFab.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
//        if (sonicSession != null) {
//          sonicSession.refresh();
//        }
//      }
//    });

    // init webView
    mWebView = (WebView) findViewById(R.id.web_view);
    mWebView.setWebViewClient(new WebViewClient() {
      @Override
      public void onPageFinished(WebView view, String url) {
        Log.d(TAG, "mWebView onPageFinished");
        super.onPageFinished(view, url);
        if (sonicSession != null) {
          sonicSession.getSessionClient().pageFinish(url);
        }
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
        if (sonicSession != null) {
          return (WebResourceResponse) sonicSession.getSessionClient().requestResource(request.getUrl().toString());
        }
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

    //防止遇到重定向
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
    mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
    intent.putExtra(SonicJavaScriptInterface.PARAM_LOAD_URL_TIME, System.currentTimeMillis());
    mWebView.addJavascriptInterface(new SonicJavaScriptInterface(this, mWebView, sonicSessionClient,
        intent), "jsInterface");

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


    // webview is ready now, just tell session client to bind
    if (sonicSessionClient != null) {
      Log.d(TAG, "webView is load by sonic mode");
      sonicSessionClient.bindWebView(mWebView);
      sonicSessionClient.clientReady();
    } else { // default mode
      Log.d(TAG, "webView is load by default mode");
      mWebView.loadUrl(url);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    //用户登录信息
    String userPhone = KndcStorage.getInstance().getData(KndcStorage.USER_PHONE);
    String userToken = KndcStorage.getInstance().getData(KndcStorage.USER_TOKEN);
    String userID = KndcStorage.getInstance().getData(KndcStorage.USER_ID);
    String userExpire = KndcStorage.getInstance().getData(KndcStorage.USER_EXPIRE_TIME);
    mWebView.loadUrl("javascript:syncUserInfo('" + userPhone + "','" + userToken+ "','" + userID +
        "','" + userExpire  + "')");
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageEvent(KndcEvent event) {
    super.onMessageEvent(event);
    Log.d(TAG, "onMessageEvent: " + event.getEventName());
    if(KndcEvent.LOGIN.equals(event.getEventName())) {
      String phone = event.getPhone();
      String commonRet = event.getCommonRet();
      if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(commonRet)) {
        return;
      }
      Gson gson = new Gson();
      CommonResult commonResult = gson.fromJson(commonRet,
          new TypeToken<CommonResult>() {
          }.getType());
      if (commonResult == null || commonResult.getData() == null) {
        Log.d(TAG, "common result is null");
        return;
      }

      Map<String, String> retData = commonResult.getData();

      //用户登录信息
      String userToken = retData.get("token");
      String userId = retData.get("user_uuid");
      String userExpire = retData.get("expire");
      //等0.5秒待状态同步完成，再进行页面跳转
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      Log.d(TAG, "setUserInfo SUCCESS!!!");
      if (sonicSession != null) {
        Log.d(TAG, "sonicSession is not null!!!");
        sonicSession.getSessionClient().loadUrl("javascript:syncUserInfo('" + phone + "','" + userToken+ "','" + userId +
            "','" + userExpire  + "')", new Bundle());
      }
    } else if(KndcEvent.WEB_OPEN_NEW_LINK.equals(event.getEventName())) {
      String url = event.getUrl();
      if(TextUtils.isEmpty(url)) {
        return;
      }
      if(sonicSession != null) {
//        sonicSession.srcUrl = url;
        sonicSession.getSessionClient().loadUrl(url, new Bundle());
//        sonicSession.refresh();
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
      if (sonicSession != null && !TextUtils.isEmpty(gotoUrl)) {
        sonicSession.getSessionClient().loadUrl(Host.getH5Host(this, gotoUrl), new Bundle());
      }
    }
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }

  @Override
  protected void onDestroy() {
    if (null != sonicSession) {
      sonicSession.destroy();
      sonicSession = null;
    }
    if (null != mWebView) {
      mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
      mWebView.clearHistory(); ((ViewGroup)
      mWebView.getParent()).removeView(mWebView);
      mWebView.destroy();
      mWebView = null;
    }
    super.onDestroy();
  }


  private static class OfflinePkgSessionConnection extends SonicSessionConnection {

    private final WeakReference<Context> context;

    public OfflinePkgSessionConnection(Context context, SonicSession session, Intent intent) {
      super(session, intent);
      this.context = new WeakReference<Context>(context);
    }

    @Override
    protected int internalConnect() {
      Context ctx = context.get();
      if (null != ctx) {
        try {
          InputStream offlineHtmlInputStream = ctx.getAssets().open("sonic-demo-index.html");
          responseStream = new BufferedInputStream(offlineHtmlInputStream);
          return SonicConstants.ERROR_CODE_SUCCESS;
        } catch (Throwable e) {
          e.printStackTrace();
        }
      }
      return SonicConstants.ERROR_CODE_UNKNOWN;
    }

    @Override
    protected BufferedInputStream internalGetResponseStream() {
      return responseStream;
    }

    @Override
    protected String internalGetCustomHeadFieldEtag() {
      return SonicSessionConnection.CUSTOM_HEAD_FILED_ETAG;
    }

    @Override
    public void disconnect() {
      if (null != responseStream) {
        try {
          responseStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    @Override
    public int getResponseCode() {
      return 200;
    }

    @Override
    public Map<String, List<String>> getResponseHeaderFields() {
      return new HashMap<>(0);
    }

    @Override
    public String getResponseHeaderField(String key) {
      return "";
    }
  }
}