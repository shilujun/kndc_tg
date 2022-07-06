package com.cashhub.cash.common;

import android.content.Context;
import android.util.Log;
import com.alibaba.fastjson.JSONObject;
import com.cashhub.cash.common.utils.CommonUtil;
import com.cashhub.cash.common.utils.DeviceUtils;

public class TrackData {

  private static final String TAG = "TrackData";

  private static TrackData mInstance;

  public static synchronized TrackData getInstance() {
    if (mInstance == null) {
      mInstance = new TrackData();
    }
    return mInstance;
  }

  /**
   * 收不到验证码
   */
  public void notGetCode(Context context) {
    JSONObject trackJson = new JSONObject();
    trackJson.put("scene_type", "sms");
    trackJson.put("scene_name", "'获取不到验证码'");
    trackJson.put("process_type", "sms");
    trackJson.put("process_name", "获取不到验证码");
    trackJson.put("event_type", "sms");
    trackJson.put("event_name", "'获取不到验证码'");
    trackJson.put("url", "/pages/login/code");
    trackJson.put("refer", "/pages/index/index");
    trackJson.put("out_url", "");
    trackJson.put("out_date", "");
    report(context, trackJson);
  }

  /**
   * 重新发送验证码
   */
  public void resendCode(Context context) {
    JSONObject trackJson = new JSONObject();
    trackJson.put("scene_type", "sms");
    trackJson.put("scene_name", "'重新发送'");
    trackJson.put("process_type", "sms");
    trackJson.put("process_name", "重新发送");
    trackJson.put("event_type", "sms");
    trackJson.put("event_name", "'重新发送'");
    trackJson.put("url", "/pages/login/code");
    trackJson.put("refer", "/pages/index/index");
    trackJson.put("out_url", "");
    trackJson.put("out_date", "");
    report(context, trackJson);
  }

  /**
   * 电话号码校验失败
   */
  public void checkPhoneFail(Context context) {
    JSONObject trackJson = new JSONObject();
    trackJson.put("scene_type", "sms");
    trackJson.put("scene_name", "'获取验证码'");
    trackJson.put("process_type", "sms");
    trackJson.put("process_name", "获取验证码");
    trackJson.put("event_type", "sms");
    trackJson.put("event_name", "'获取验证码'");
    trackJson.put("url", "/pages/login/login");
    trackJson.put("refer", "/pages/login/code");
    trackJson.put("out_url", "");
    trackJson.put("out_date", "");
    report(context, trackJson);
  }

  /**
   * 验证码获取失败
   */
  public void getCodeFail(Context context) {
    JSONObject trackJson = new JSONObject();
    trackJson.put("scene_type", "sms");
    trackJson.put("scene_name", "'获取验证码'");
    trackJson.put("process_type", "sms");
    trackJson.put("process_name", "获取验证码");
    trackJson.put("event_type", "sms");
    trackJson.put("event_name", "'获取验证码'");
    trackJson.put("url", "/pages/login/login");
    trackJson.put("refer", "/pages/login/code");
    trackJson.put("out_url", "");
    trackJson.put("out_date", "");
    TrackData.getInstance().report(context, trackJson);
  }

  /**
   * 埋点数据上报
   */
  public void report(Context context, JSONObject jsonObject) {
    if(jsonObject == null) {
      jsonObject = new JSONObject();
    }
    jsonObject.put("uid", KndcStorage.getInstance().getData(KndcStorage.USER_ID));
    jsonObject.put("trace_id", DeviceUtils.getDeviceId(context));
//    jsonObject.put("source", CommonUtil.get(context));
    jsonObject.put("ip", "");
//    jsonObject.put("client_os", DeviceUtils.get.getDeviceId(context));
//    jsonObject.put("client_os_version", DeviceUtils.getDeviceId(context));
//    jsonObject.put("client_no", DeviceUtils.getDeviceId(context));
//    jsonObject.put("client_manufacture", DeviceUtils.getDeviceId(context));
//    jsonObject.put("client_model", DeviceUtils.getDeviceId(context));
    jsonObject.put("timestamp", System.currentTimeMillis());
//    jsonObject.put("date", DeviceUtils.getDeviceId(context));


    JSONObject jsonExtend = new JSONObject();
    jsonExtend.put("app_version", DeviceUtils.getVersionCode(context));
//    jsonExtend.put("application_id", DeviceUtils.getVerName(context)); //TODO

    CommonApi.getInstance().trackData(context, jsonObject.toString());
  }
}
