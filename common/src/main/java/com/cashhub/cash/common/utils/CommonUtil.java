package com.cashhub.cash.common.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowInsets;
import androidx.core.app.ActivityCompat;
import com.alibaba.fastjson.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public class CommonUtil {

  private static final String TAG = "CommonUtil";
  //为了防止用户或者测试MM疯狂的点击某个button，写个方法防止按钮连续点击
  private static long lastClickTime;

  /**

   * 获取手机IMEI

   */

  private static String getIMEI(Context context) {

    try {

      TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

      if (telephonyManager ==null) {

        return null;

      }

      @SuppressLint({"MissingPermission", "HardwareIds"}) String imei = telephonyManager.getDeviceId();
      return imei;

    }catch (Exception e) {

      return null;
    }

  }
  /**
   * 得到全局唯一UUID,有权限时
   * @param context NameActivity.this
   * @return 返回UUID字符串
   */
  @SuppressLint("HardwareIds")
  public static String getUniqueID(Context context) {
    try {
      if (ActivityCompat
          .checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
        return "";
      }
      final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
      final String tmDevice, tmSerial, androidId;
      tmDevice = "" + tm.getDeviceId();
      tmSerial = "" + tm.getSimSerialNumber();

      androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
      UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
      return deviceUuid.toString();
    } catch (Exception e) {

    }
    return getUUID(context);
  }
  /**
   * 得到全局唯一UUID,无权限时通过UUID.randomUUID().toString()随机产生一个UUID
   */
  public static String getUUID(Context context) {
    return UUID.randomUUID().toString();
  }

  /**
   * 获取当前应用的PackageName
   * @param context
   * @return
   * @throws Exception
   */
  public static String getPackageName(Context context) {
    String packageName = "";
    try {
      packageName = context.getPackageName();
    } catch (Exception e) {

    }
    return packageName;
  }

  /**
   * 获取当前应用的applicationId
   * @param context
   * @return
   * @throws Exception
   */
  public static String getApplicationId(Context context) {
    String applicationId = "";
    try {
      applicationId = context.getPackageName();
    } catch (Exception e) {

    }
    return applicationId;
  }

  /**
   * 获取当前应用的版本号
   * @param context
   * @return
   * @throws Exception
   */
  public static String getVersionName(Context context) {
    String version = "";
    try {
      // 获取packagemanager的实例
      PackageManager packageManager = context.getPackageManager();
      // getPackageName()是你当前类的包名，0表明是获取版本信息
      PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(),0);
      version = packInfo.versionName;
    } catch (Exception e) {

    }
    return version;
  }

  public static int getVersionCode(Context context) {
    int versionCode = 0;
    try {
      //获取软件版本号，对应AndroidManifest.xml下android:versionCode
      versionCode = context.getPackageManager().
          getPackageInfo(context.getPackageName(), 0).versionCode;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return versionCode;
  }

  public static JSONObject getSafeAreaInsets(Activity activity) {
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("top", 0);
      jsonObject.put("right", 0);
      jsonObject.put("bottom", 0);
      jsonObject.put("left", 0);
      if (activity == null) {
        return jsonObject;
      }

      WindowInsets windowInsets = activity.getWindow().getDecorView().getRootWindowInsets();
      if (Build.VERSION.SDK_INT >= VERSION_CODES.P && windowInsets != null) {
        int top = windowInsets.getDisplayCutout().getSafeInsetTop();
        int right = windowInsets.getDisplayCutout().getSafeInsetRight();
        int bottom = windowInsets.getDisplayCutout().getSafeInsetBottom();
        int left = windowInsets.getDisplayCutout().getSafeInsetLeft();

        jsonObject.put("top", top);
        jsonObject.put("right", right);
        jsonObject.put("bottom", bottom);
        jsonObject.put("left", left);
      }
    } catch (Exception e) {

    }
    return jsonObject;
  }

  public static JSONObject getSafeArea(Activity activity, JSONObject oldJsonObject) {
    JSONObject jsonObject = new JSONObject();
    try {
      if(oldJsonObject == null) {
        jsonObject.put("left", 0);
        jsonObject.put("right", 0);
        jsonObject.put("top", 0);
        jsonObject.put("bottom", 0);
        jsonObject.put("width", 0);
        jsonObject.put("height", 0);
      } else {
        jsonObject = oldJsonObject;
      }
      if (activity == null) {
        return jsonObject;
      }

      WindowInsets windowInsets = activity.getWindow().getDecorView().getRootWindowInsets();
      if (Build.VERSION.SDK_INT >= VERSION_CODES.P && windowInsets != null) {
        int top = windowInsets.getDisplayCutout().getSafeInsetTop();
        int right = windowInsets.getDisplayCutout().getSafeInsetRight();
        int bottom = windowInsets.getDisplayCutout().getSafeInsetBottom();
        int left = windowInsets.getDisplayCutout().getSafeInsetLeft();
        if(top > 0) {
          jsonObject.put("top", top);
        }
//        if(right > 0) {
//          jsonObject.put("right", right);
//        }
//        if(bottom > 0) {
//          jsonObject.put("bottom", bottom);
//        }
        if(left > 0) {
          jsonObject.put("left", left);
        }
      }
    } catch (Exception e) {

    }
    return jsonObject;
  }

  public synchronized static boolean isFastClick() {
    long time = System.currentTimeMillis();
    if (time - lastClickTime >= 0
        && time - lastClickTime < 500) { //如果行的点击时间在老时间之前,也放过,因为用户可能修改手机时间到之前
      return true;
    }
    lastClickTime = time;
    return false;
  }

  public static int dip2px(Context context, float dipValue) {
    return DpUtil.dp2px(context, dipValue);
  }

  @Deprecated
  public static int px2dip(Context context, float pxValue) {
    float scale = context.getResources().getDisplayMetrics().density;
    return (int) (pxValue / scale + 0.5f);
  }

  public static Drawable getAttrDrawable(Context context, int attrId) {
    TypedArray a = context.obtainStyledAttributes(new int[]{attrId});
    Drawable drawable = a.getDrawable(0);
    a.recycle();
    return drawable;
  }

  public static int getAttrColor(Context context, int attrId) {
    TypedArray a = context.obtainStyledAttributes(new int[]{attrId});
    int color = a.getColor(0, 0);
    a.recycle();
    return color;
  }

  public static boolean isEquals(Object actual, Object expected) {
    return actual == expected || (actual == null ? expected == null : actual.equals(expected));
  }

  public static ArrayList<Rect> subList(ArrayList<Rect> srcList, int startIndex, int endIndex) {
    int index = startIndex;
    int len = endIndex - startIndex;
    ArrayList<Rect> arrayList = new ArrayList<Rect>();
    for (int i = 0; i < len; i++) {
      Rect rect = srcList.get(index);
      arrayList.add(rect);
      index++;
    }
    return arrayList;
  }

  /**
   * 判断是否是一个合法的越南手机号
   */
  public static boolean isVietnamMobile(String mobiles) {
    String telRegex = "^0[689]\\d{8}$";
    if(TextUtils.isEmpty(mobiles)) {
      return false;
    }
    return mobiles.matches(telRegex);
  }

  /**
   * 判断是否是一个合法的手机号
   */
  public static boolean isMobileNO(String mobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
//        String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
    if (TextUtils.isEmpty(mobiles)) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * <p> Title: isAbroad </p> <p> Description: 是否是境外手机号码 </p>
   */
  public static boolean isAbroad(String code) {
    if (code.equals("+86")) {
      return false;
    } else {
      return true;
    }
  }

  public static boolean diffDays(long oldTime, long currTime) {
    Date oldDate = new Date(oldTime);
    Date currDate = new Date(currTime);
    if (oldDate.getDate() != currDate.getDate()
        || oldDate.getMonth() != currDate.getMonth()
        || oldDate.getYear() != currDate.getYear()) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * 获取标题栏高度
   * @param context
   * @return
   */
  public static int getTitleBarHeight(Context context) {
    return 56;
  }

  /**
   * 判断底部是否有虚拟导航栏 （true：虚拟导航栏，false：物理导航栏）
   */
  @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
  public static boolean checkDeviceHasNavigationBar(Context activity) {
    // 魅族M460A不能控制底部虚拟导航栏
    if (Build.VERSION.SDK_INT < 19 || Build.MODEL.contains("M460A")) {
      return false;
    }

    // 通过判断设备是否有菜单键(不是虚拟键,是手机屏幕外的物理按键)来确定是否有navigation bar
    boolean hasMenuKey = ViewConfiguration.get(activity).hasPermanentMenuKey();
    if (!hasMenuKey) {
      return true;
    }
    return false;
  }

  /**
   * 获取Activity的ClassName
   */
  public static String getActivityClassName(Activity activity) {
    return activity.getClass().getName();
  }

  /**
   * 关闭应用
   */
  public static void closeApp() {
    System.exit(0);
  }

  /**
   * 获取系统语言
   */
  public static String getLanguage() {
    String l = Locale.getDefault().getCountry().toLowerCase();
    if ("tw".equals(l) || "hk".equals(l)) {
      return "tw";
    } else {
      return "cn";
    }
  }

  /**
   * 获取状态栏高度
   *该方法获取需要在onWindowFocusChanged方法回调之后
   * @param context
   * @return
   */
  public static int getStatusBarHeightDp(Context context) {
    int statusBarHeight = getStatusBarByResId(context);
    if (statusBarHeight <= 0) {
      statusBarHeight = getStatusBarByReflex(context);
    }
    if(statusBarHeight > 0) {
      statusBarHeight = px2dip(context, statusBarHeight);
    }
    return statusBarHeight;
  }

  /**
   * 获取状态栏高度
   *该方法获取需要在onWindowFocusChanged方法回调之后
   * @param context
   * @return
   */
  public static int getStatusBarHeight(Context context) {
    int statusBarHeight = getStatusBarByResId(context);
    if (statusBarHeight <= 0) {
      statusBarHeight = getStatusBarByReflex(context);
    }
    return statusBarHeight;
  }

  /**
   * 通过状态栏资源id来获取状态栏高度
   * @param context
   * @return
   */
  private static int getStatusBarByResId(Context context) {
    int height = 0;
    //获取状态栏资源id
    int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      try {
        height = context.getResources().getDimensionPixelSize(resourceId);
      } catch (Exception e) {
      }
    }
    return height;
  }

  /**
   * 通过Activity的内容距离顶部高度来获取状态栏高度，该方法获取需要在onWindowFocusChanged回调之后
   * @param activity
   * @return
   */
  public static int getStatusBarByTop(Activity activity) {
    Rect rect = new Rect();
    Window window = activity.getWindow();
    window.getDecorView().getWindowVisibleDisplayFrame(rect);
    return rect.top;
  }

  /**
   * 通过反射获取状态栏高度
   * @param context
   * @return
   */
  private static int getStatusBarByReflex(Context context) {
    int statusBarHeight = 0;
    try {
      Class<?> clazz = Class.forName("com.android.internal.R$dimen");
      Object object = clazz.newInstance();
      int height = Integer.parseInt(clazz.getField("status_bar_height")
          .get(object).toString());
      statusBarHeight = context.getResources().getDimensionPixelSize(height);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return statusBarHeight;
  }

  public static int getNavigationBarHeight(Activity activity) {
    Resources resources = activity.getResources();
    int resourceId = resources.getIdentifier("navigation_bar_height",
        "dimen", "android");
    //获取NavigationBar的高度
    int height = resources.getDimensionPixelSize(resourceId);
    return height;
  }

  public static boolean isNavigationBarShow(Activity activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      Display display = activity.getWindowManager().getDefaultDisplay();
      Point size = new Point();
      Point realSize = new Point();
      display.getSize(size);
      display.getRealSize(realSize);
      return realSize.y != size.y;
    } else {
      boolean menu = ViewConfiguration.get(activity).hasPermanentMenuKey();
      boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
      if (menu || back) {
        return false;
      } else {
        return true;
      }
    }
  }

  public static long getStartTimeOfDay(long now, String timeZone) {
    String tz = TextUtils.isEmpty(timeZone) ? "GMT+8" : timeZone;
    TimeZone curTimeZone = TimeZone.getTimeZone(tz);
    Calendar calendar = Calendar.getInstance(curTimeZone);
    calendar.setTimeInMillis(now);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTimeInMillis();
  }

  public static String getFormatDate() {
    String date = "";
    try {
      @SuppressLint("SimpleDateFormat")
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      date = sdf.format(new Date());
    } catch (Exception ignored) {

    }
    return date;
  }
}

