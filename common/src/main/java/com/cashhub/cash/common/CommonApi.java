package com.cashhub.cash.common;

import android.content.Context;
import android.util.Log;
import com.alibaba.fastjson.JSONObject;
import com.cashhub.cash.common.utils.DeviceUtils;
import com.cashhub.cash.common.utils.Md5Util;
import java.util.HashMap;
import java.util.Map;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class CommonApi {

  private static final String TAG = "CommonApi";

  private static CommonApi mInstance;

  public static synchronized CommonApi getInstance() {
    if (mInstance == null) {
      mInstance = new CommonApi();
    }
    return mInstance;
  }

  /**
   * 获取验证码
   */
  public void getCheckCode(Context context, String phone) {
    CommonResult comRet = new CommonResult();
    if(phone == null || phone.isEmpty()) {
      Log.d(TAG, "phone is Null or empty!!! ");
      comRet.setCode(-1);
      comRet.setMsg("参数错误");
      return;
    }
    Log.d(TAG, "phone num is:" + phone);
    String url = Host.getApiHost(context) + "/api/v1/util/verify-code";
    JSONObject requestJson = new JSONObject();
    requestJson.put("phone", phone);
    requestJson.put("type", 1);
    requestJson.put("sign", Md5Util.md5Hex(phone + "&1"));
    RequestBody requestBody = FormBody.create(requestJson.toString(), MediaType.parse("application/json"));

    Request request = new Request.Builder()
        .addHeader("Content-Type", "application/json")
        .url(url)
        .post(requestBody)
        .build();

    Log.d(TAG, "getCheckCode url: " + url);

    HttpsUtils.sendRequest(phone, url, request, "");
  }

  /**
   * 登入
   */
  public void userLogin(Context context, String phone, String verifyCode) {
    CommonResult comRet = new CommonResult();
    if(phone == null || phone.isEmpty()) {
      Log.d(TAG, "phone is Null or empty!!! ");
      comRet.setCode(-1);
      comRet.setMsg("参数错误");
      return;
    }

    String url = Host.getApiHost(context) + "/api/v1/user/sms-login";
    JSONObject requestJson = new JSONObject();
    requestJson.put("phone", phone);
    requestJson.put("verify_code", verifyCode);
    requestJson.put("device_key", DeviceUtils.getDeviceId(context));
    requestJson.put("channel_code", "");
    RequestBody requestBody = FormBody.create(requestJson.toString(), MediaType.parse("application/json"));

    Request request = new Request.Builder()
        .addHeader("Content-Type", "application/json")
        .url(url)
        .post(requestBody)
        .build();

    Log.d(TAG, "userLogin url: " + url);

    HttpsUtils.sendRequest(phone, url, request, KndcEvent.LOGIN);
  }

  /**
   * 登出
   * @param context
   * @param token
   */
  public void userLogout(Context context, String token) {
    String url = Host.getApiHost(context) + "/api/v1/user/logout";

    HashMap<String, String> headParams = new HashMap<>();
    headParams.put("Content-Type", "application/json");
    headParams.put("Authorization", token);

    Headers headers = setHeaderParams(headParams);
    Request request = new Request.Builder()
        .url(url)
        .get()
        .headers(headers)
        .build();

    Log.d(TAG, "userLogout token: " + token);
    Log.d(TAG, "userLogout url: " + url);

    HttpsUtils.sendRequest("", url, request, KndcEvent.LOGOUT);
  }

  /**
   * 清理登录信息
   */
  public void clearLoginInfo(Context context) {
    CommonApi commonApi = new CommonApi();
    commonApi.userLogout(context, KndcStorage.getInstance().getData(KndcStorage.USER_TOKEN));
  }

  /**
   * 埋点
   * @param context
   * @param requestJson
   * @param token
   */
  public void trackData(Context context, JSONObject requestJson, String token) {
    if(requestJson == null) {
      return;
    }
    String url = Host.getApiHost(context) + "/api/v1/track/data";
    RequestBody requestBody = FormBody.create(requestJson.toString(), MediaType.parse("application/json"));

    Request request = new Request.Builder()
        .addHeader("Content-Type", "application/json")
        .addHeader("Access-Control-Allow-Origin", "*")
        .url(url)
        .post(requestBody)
        .build();

    Log.d(TAG, "postOssSign device url: " + url);

    HttpsUtils.sendRequest("", url, request, "");
  }

  //添加参数
  private String getBodyParams(Map<String, String> bodyParams) {
    //1.添加请求参数
    //遍历map中所有参数到builder
    if (bodyParams != null && bodyParams.size() > 0) {
      StringBuffer stringBuffer = new StringBuffer("?");
      for (String key : bodyParams.keySet()) {
        if (bodyParams.get(key) != null) {//如果参数不是null，就拼接起来
          stringBuffer.append("&");
          stringBuffer.append(key);
          stringBuffer.append("=");
          stringBuffer.append(bodyParams.get(key));
        }
      }

      return stringBuffer.toString();
    } else {
      return "";
    }
  }

  //添加headers
  private Headers setHeaderParams(Map<String, String> headerParams) {
    Headers headers = null;
    Headers.Builder headliners = new Headers.Builder();
    if (headerParams != null && headerParams.size() > 0) {
      for (String key : headerParams.keySet()) {
        if (headerParams.get(key) != null) {//如果参数不是null，就拼接起来
          headliners.add(key, headerParams.get(key));
        }
      }
    }

    headers = headliners.build();
    return headers;
  }
}
