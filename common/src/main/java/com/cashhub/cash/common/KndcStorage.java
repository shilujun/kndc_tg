package com.cashhub.cash.common;

import java.util.HashMap;
import java.util.Map;

public class KndcStorage {
  public static String USER_TOKEN = "user_token";
  public static String USER_ID = "user_id";
  public static String USER_PHONE = "user_phone";
  public static String USER_EXPIRE_TIME = "user_expire_time";

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
