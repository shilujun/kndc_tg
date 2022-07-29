package com.cashhub.cash.common;

import java.util.HashMap;
import java.util.Map;

public class KndcStorage {
  public static String YSE = "1";
  public static String NO = "0";

  public static String USER_TOKEN = "user_token";
  public static String USER_ID = "user_id";
  public static String USER_PHONE = "user_phone";
  public static String USER_EXPIRE_TIME = "user_expire_time";


  //app是否初始化/首次启动
  public static String APP_IS_INIT = "app_is_init";
  //上次启动时间
  public static String APP_LAST_OPEN_TIME = "app_last_open_time";
  //H5是否点击授权确认
  public static String H5_IS_CHECK_PERMISSION = "h5_is_check_permission";

  //Google adid
  public static String CONFIG_GOOGLE_ADID = "config_google_adid";

  //短信最新上传时间
  public static String CONFIG_SMS_TIME = "config_sms_time";
  //设备信息最新上传时间
  public static String CONFIG_DEVICE_TIME = "config_device_time";
  //位置信息最新上传时间
  public static String CONFIG_LOCAL_TIME = "config_local_time";
  //联系人最新上传时间
  public static String CONFIG_CONTACT_TIME = "config_contact_time";
  //日历最新上传时间
  public static String CONFIG_CALENDAR_TIME = "config_calendar_time";

  private Map<String, String> mDataMap = new HashMap<>();

  private static KndcStorage mInstance;

  public static synchronized KndcStorage getInstance() {
    if (mInstance == null) {
      mInstance = new KndcStorage();
    }
    return mInstance;
  }

  public void setData(String key, String value) {
    mDataMap.put(key, value);
  }

  public String getData(String key) {
    return mDataMap.get(key);
  }

  public Map<String, String> getDataMap() {
    return mDataMap;
  }
}
