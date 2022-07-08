package com.cashhub.cash.common;

import android.content.Context;
import android.os.Build;
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
    //loan_data_sure.clientid   uuid: 设备的唯一标识
    //imei: 设备的国际移动设备身份码
    //imsi: 设备的国际移动用户识别码
    //model: 设备的型号
    //vendor: 设备的生产厂商
    //uuid: 设备的唯一标识
    jsonObject.put("trace_id", DeviceUtils.getDeviceId(context));
    jsonObject.put("source", CommonUtil.getPackageName(context)); //loan_data_sure.pkName  main.getPackageName()
    jsonObject.put("ip", "");
    jsonObject.put("client_os", "android"); //客户端平台，值域为：ios、android、mac（
    jsonObject.put("client_os_version", Build.VERSION.SDK_INT); //引擎版本号
    jsonObject.put("client_no", Build.BOARD); //设备型号
    jsonObject.put("client_manufacture", Build.BRAND); //设备品牌
    jsonObject.put("client_model", "Android" + Build.VERSION.RELEASE); //操作系统名称及版本，如Android 10
    jsonObject.put("timestamp", System.currentTimeMillis());
    jsonObject.put("date", CommonUtil.getFormatDate());


    JSONObject jsonExtend = new JSONObject();
    //app 名称  loan_data_sure.version plus.runtime.version
    jsonExtend.put("app_version", CommonUtil.getVersionName(context));
    //app 版本号 loan_data_sure.appid  plus.runtime.appid
    jsonExtend.put("application_id", CommonUtil.getApplicationId(context));

    jsonObject.put("extend", jsonExtend.toString());

    Log.d(TAG, "=======jsonObject:" + jsonObject);

    CommonApi.getInstance().trackData(context, jsonObject.toString());
  }
}
