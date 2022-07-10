package com.cashhub.cash.app;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import com.alibaba.fastjson.JSONObject;
import com.cashhub.cash.common.CommonApi;
import com.cashhub.cash.common.KndcEvent;
import com.cashhub.cash.common.KndcStorage;
import com.cashhub.cash.common.SystemInfo;
import com.cashhub.cash.common.UploadData;
import com.cashhub.cash.common.utils.CommonUtil;
import com.cashhub.cash.common.utils.DeviceUtils;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

public class JSInterface {
  private static final String TAG = "JSInterface";

  private Context mContext;
  private BaseWebView mWebView;

  public static final String PARAM_CLICK_TIME = "clickTime";

  public static final String PARAM_LOAD_URL_TIME = "loadUrlTime";

  private UploadData mUploadData;

  private CommonApi mCommonApi;
//  private Context _c;
//  private WebviewActivity _m;
//  private BaseWebView _w;
  public JSInterface(Context context, BaseWebView view) {
    mContext = context;
    mWebView = view;
  }

  // 注入js
  @JavascriptInterface
  public void testInject() {
    String js = "alert();";
    mWebView.injection(js);
  }

  // 执行操作
  @JavascriptInterface
  public void testExecute() {
    // TODO：在这里可以执行Android程序方法和操作
    mWebView.executeMethod("cbExecute", "test"); // 回调执行 js 方法
  }
    /**
   * 清理登录信息
   */
  @JavascriptInterface
  public void clearLoginInfo() {
    Log.d(TAG, "clearLoginInfo Start!!!");
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
  public String getSystemInfo() {
    JSONObject systemInfo = DeviceUtils.getSystemInfo(mContext);
    return systemInfo.toString();
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
  public void trackData(String requestJson) {
    Log.d(TAG, "params:" + requestJson);
    try {
      //初始化数据
      if (mCommonApi == null) {
        mCommonApi = new CommonApi();
      }
      mCommonApi.trackData(mContext, requestJson);
    } catch (Exception e) {
      Log.d(TAG, e.getMessage());
    }
  }

  /**
   * 获取联系人数据
   */
  @JavascriptInterface
  public String getAllContactInfo() {
    SystemInfo systemInfo = new SystemInfo(mContext);
    List<JSONObject> allContacts = systemInfo.getAllContacts();
    if(allContacts == null) {
      return "";
    }
    return allContacts.toString();
  }

  /**
   * 开始授权
   */
  @JavascriptInterface
  public void beginPermission() {
    CommonApp.beginPermission();
  }

  /**
   * 页面跳转
   */
  @JavascriptInterface
  public void jsNavigateTo(String url) {
    CommonApp.navigateToInWebView(url);
  }

  /**
   * 打开登录页
   */
  @JavascriptInterface
  public void jsNavigateToLogin() {
    CommonApp.navigateToLogin(mContext);
  }
}
