package com.cashhub.cash.common;

import android.content.Context;
import android.util.Log;
import com.alibaba.fastjson.JSONObject;
import com.cashhub.cash.common.utils.Md5Util;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class CommonApi {

  private static final String TAG = "CommonApi";

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
    String url = Host.getApiHost(context) + "/v1/util/verify-code";
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

    HttpsUtils.sendRequest(url, request, "");
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
    String url = Host.getApiHost(context) + "/v1/user/sms-login";
    JSONObject requestJson = new JSONObject();
    requestJson.put("phone", phone);
    requestJson.put("verify_code", verifyCode);
    requestJson.put("device_key", 1);
    requestJson.put("channel_code", "");
    RequestBody requestBody = FormBody.create(requestJson.toString(), MediaType.parse("application/json"));

    Request request = new Request.Builder()
        .addHeader("Content-Type", "application/json")
        .url(url)
        .post(requestBody)
        .build();

    Log.d(TAG, "userLogin url: " + url);

    HttpsUtils.sendRequest(url, request, KndcEvent.LOGIN);
  }

  /**
   * 登出
   * @param context
   * @param token
   */
  public void userLogout(Context context, String token) {
    String url = Host.getApiHost(context) + "/v1/user/logout";

    Request request = new Request.Builder()
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", token)
        .url(url)
        .get()
        .build();

    Log.d(TAG, "userLogout url: " + url);



    HttpsUtils.sendRequest(url, request, KndcEvent.LOGOUT);
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
    String url = Host.getApiHost(context) + "/v1/track/data";
    RequestBody requestBody = FormBody.create(requestJson.toString(), MediaType.parse("application/json"));

    Request request = new Request.Builder()
        .addHeader("Content-Type", "application/json")
        .addHeader("Access-Control-Allow-Origin", "*")
        .url(url)
        .post(requestBody)
        .build();

    Log.d(TAG, "postOssSign device url: " + url);

    HttpsUtils.sendRequest(url, request, "");
  }
}
