package com.cashhub.cash.common;

import android.graphics.Bitmap;

public class KndcEvent {
  public static String LOGIN = "user_login";
  public static String LOGOUT = "user_logout";
  public static String COLLECTION_STATUS = "collection_status";
  public static String GET_CHECK_CODE = "get_check_code";
  public static String OPEN_CAMARA = "open_camera";
  public static String OPEN_CAMARA_SUCCESS = "open_camera_success";
  public static String OPEN_CAMARA_FAILURE = "open_camera_failure";
  public static String OPEN_IMAGE_CAPTURE = "open_image_capture";
  public static String OPEN_IMAGE_CAPTURE_SUCCESS = "open_image_capture_success";
  public static String OPEN_IMAGE_CAPTURE_FAILURE = "open_image_capture_failure";
  public static String GET_POLICY_SIGN = "get_policy_sign";
  public static String UPLOAD_IMAGE_SUCCESS = "upload_image_success";
  public static String REPORT_UPLOAD_SUCCESS = "report_upload_success";
  public static String WEB_OPEN_NEW_LINK = "web_open_new_link";
  public static String BEGIN_CHECK_PERMISSION = "begin_check_permission";
  public static String JS_CALL_UPLOAD_DATA = "js_call_upload_data";
  public static String JS_CALL_UPLOAD_DATA_EVERY_TIME = "js_call_upload_data_every_time";
  public static String UPLOAD_END_CALL_JS = "upload_end_call_js";
  public static String PERMISSION_END_CALL_JS = "permission_end_call_js";
  public static String CLOSE_LOGIN_ACTIVITY = "close_login_activity";
  public static String SYNC_USER_STATUS = "sync_user_status";
  private String mEventName = "";
  private String code = "";
  private String mToken = "";
  private String mDeviceId = "";
  private String mVerifyCode = "";
  private String mPhone = "";
  private String mCommonRet = "";
  private String mLineType = "";
  private String mUploadType = "";
  private Bitmap mBitmap;
  private String url;
  private String permission;
  private String type;
  private String jsonData;

  public KndcEvent() {
  }

  public String getCode() {
    return code;
  }

  public String getEventName() {
    return mEventName;
  }

  public String getToken() {
    return mToken;
  }

  public String getDeviceId() {
    return mDeviceId;
  }

  public String getVerifyCode() {
    return mVerifyCode;
  }

  public String getPhone() {
    return mPhone;
  }

  public String getCommonRet() {
    return mCommonRet;
  }

  public String getLineType() {
    return mLineType;
  }

  public String getUploadType() {
    return mUploadType;
  }

  public Bitmap getBitmap() {
    return mBitmap;
  }

  public String getUrl() {
    return url;
  }

  public String getPermission() {
    return permission;
  }

  public String getType() {
    return type;
  }

  public String getJsonData() {
    return jsonData;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public void setEventName(String eventName) {
    this.mEventName = eventName;
  }

  public void setToken(String token) {
    this.mToken = token;
  }

  public void setDeviceId(String deviceId) {
    this.mDeviceId = deviceId;
  }

  public void setVerifyCode(String verifyCode) {
    this.mVerifyCode = verifyCode;
  }

  public void setPhone(String phone) {
    this.mPhone = phone;
  }

  public void setCommonRet(String commonRet) {
    this.mCommonRet = commonRet;
  }

  public void setLineType(String lineType) {
    this.mLineType = lineType;
  }

  public void setUploadType(String uploadType) {
    this.mUploadType = mUploadType;
  }

  public void setBitmap(Bitmap bitmap) {
    this.mBitmap = bitmap;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setPermission(String permission) {
    this.permission = permission;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setJsonData(String jsonData) {
    this.jsonData = jsonData;
  }
}
