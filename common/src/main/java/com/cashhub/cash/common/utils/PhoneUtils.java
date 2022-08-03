package com.cashhub.cash.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.alibaba.fastjson.JSONObject;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Create by tengtao
 * on 2022/4/27 20:36
 */
public class PhoneUtils {

    /**
     * SIM的状态信息：
     * SIM_STATE_UNKNOWN         未知状态 0
     SIM_STATE_ABSENT          没插卡 1
     SIM_STATE_PIN_REQUIRED     锁定状态，需要用户的PIN码解锁 2
     SIM_STATE_PUK_REQUIRED    锁定状态，需要用户的PUK码解锁 3
     SIM_STATE_NETWORK_LOCKED   锁定状态，需要网络的PIN码解锁 4
     SIM_STATE_READY           就绪状态 5
     */
    public static int getSimState(Context context){
       return getTelephonyManager(context).getSimState();

    }

  /**
   * @return
   */
  public static JSONObject getTelephony(Context context) {
    JSONObject telephony = new JSONObject();
    telephony.put("network_country_iso", getTelephonyManager(context).getNetworkCountryIso());
    telephony.put("network_operator", getTelephonyManager(context).getNetworkOperator());
    telephony.put("network_operator_name", getTelephonyManager(context).getNetworkOperatorName());
    telephony.put("phone_type", getTelephonyManager(context).getPhoneType());
    telephony.put("sim_country_iso", getTelephonyManager(context).getSimCountryIso());
    telephony.put("sim_operator", getTelephonyManager(context).getSimOperator());
    telephony.put("sim_operator_name", getTelephonyManager(context).getSimOperatorName());
    telephony.put("sim_state", getTelephonyManager(context).getSimState());

    Class<?> clazz = null;
    Method method = null;//(int slotId)
    String imei1 = "";
    String imei2 = "";
    try {
      clazz = Class.forName("android.os.SystemProperties");
      method = clazz.getMethod("get", String.class, String.class);
      String gsm = (String) method.invoke(null, "ril.gsm.imei", "");
      if (!TextUtils.isEmpty(gsm)) {
        //the value of gsm like:xxxxxx,xxxxxx
        String[] imeiArray = gsm.split(",");
        if (imeiArray != null && imeiArray.length > 0) {
          imei1 = imeiArray[0];
          if (imeiArray.length > 1) {
            imei2 = imeiArray[1];
          } else {
            imei2 = getTelephonyManager(context).getDeviceId(1);
          }
        } else {
          imei1 = getTelephonyManager(context).getDeviceId(0);
          imei2 = getTelephonyManager(context).getDeviceId(1);
        }
      } else {
        imei1 = getTelephonyManager(context).getDeviceId(0);
        imei2 = getTelephonyManager(context).getDeviceId(1);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    if(TextUtils.isEmpty(imei1)) {
      imei1 = getIMEI_1(context);
    }

    if(TextUtils.isEmpty(imei2)) {
      imei2 = getIMEI_2(context);
    }

    telephony.put("imei1", imei1);
    telephony.put("imei2", imei2);

    return telephony;
  }

    private static TelephonyManager getTelephonyManager(Context context){
      return (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
    }

  /**
   * IMEI 1号
   * @param context
   * @return
   */
  @SuppressLint("HardwareIds")
  public static String getIMEI_1(Context context){
    TelephonyManager tm = getTelephonyManager(context);
    return  tm != null ? tm.getDeviceId() : "";
  }

  /**
   * IMEI 2号
   * @param context
   * @return
   */
  public static String getIMEI_2(Context context) {
    TelephonyManager tm = getTelephonyManager(context);
    Class<? extends TelephonyManager> clazz = tm.getClass();
    try {
      Method getImei = clazz.getDeclaredMethod("getImei", int.class);
      return Objects.requireNonNull(getImei.invoke(tm, 1)).toString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }

}