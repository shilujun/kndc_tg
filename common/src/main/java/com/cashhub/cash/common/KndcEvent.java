package com.cashhub.cash.common;

public class KndcEvent {
  public static String LOGIN = "user_login";
  public static String LOGOUT = "user_logout";
  public static String OPEN_CAMARA = "open_camera";
  public static String OPEN_IMAGE_CAPTURE = "open_image_capture";
  private String mEventName = "";
  private String mToken = "";
  private String mDeviceId = "";
  private String mVerifyCode = "";
  private String mPhone = "";
  private String mCommonRet = "";

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
}
