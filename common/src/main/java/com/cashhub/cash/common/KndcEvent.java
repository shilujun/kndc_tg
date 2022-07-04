package com.cashhub.cash.common;

import android.graphics.Bitmap;

public class KndcEvent {
  public static String LOGIN = "user_login";
  public static String LOGOUT = "user_logout";
  public static String OPEN_CAMARA = "open_camera";
  public static String OPEN_CAMARA_SUCCESS = "open_camera_success";
  public static String OPEN_CAMARA_FAILURE = "open_camera_failure";
  public static String OPEN_IMAGE_CAPTURE = "open_image_capture";
  public static String OPEN_IMAGE_CAPTURE_SUCCESS = "open_image_capture_success";
  public static String OPEN_IMAGE_CAPTURE_FAILURE = "open_image_capture_failure";
  public static String GET_POLICY_SIGN = "get_policy_sign";
  public static String UPLOAD_IMAGE_SUCCESS = "upload_image_success";
  public static String UPLOAD_REPORT_SUCCESS = "upload_report_success";
  private String mEventName = "";
  private String mToken = "";
  private String mDeviceId = "";
  private String mVerifyCode = "";
  private String mPhone = "";
  private String mCommonRet = "";
  private String mLineType = "";
  private String mUploadType = "";
  private Bitmap mBitmap;

  public KndcEvent() {
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
}
