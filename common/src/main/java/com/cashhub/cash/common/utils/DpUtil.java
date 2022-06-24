package com.cashhub.cash.common.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class DpUtil {

  public static DisplayMetrics sDisplayMetrics;
  public static float sBaseDp = -1;
  private static float sScreenDp = -1;

//  public static int dp2px(float dipValue) {
//    return dp2px(ApplicationContext.getInstance(), dipValue);
//  }

  public static float px2dp(int pxValue) {
    return (pxValue / Resources.getSystem().getDisplayMetrics().density);
  }

  public static int dp2px(Context context, float dipValue) {
    float baseDp = getBaseDp(context);
    float scale = getDisplayMetrics(context).density;
    dipValue = baseDp * dipValue;
    return (int) (dipValue * scale + 0.5F);
  }

  private static float getBaseDp(Context context) {
    if (sBaseDp < 0) {
      float deviceWidthDp = getScreenDp(context);
      if (deviceWidthDp < 450) {
        sBaseDp = deviceWidthDp / 375;
      } else if (deviceWidthDp < 750) {
        sBaseDp = (float) (1.2 + 4 * (deviceWidthDp - 450) / 750 / 16);
      } else if (deviceWidthDp < 1000) {
        sBaseDp = (float) (1.3 + 8 * (deviceWidthDp - 750) / 1250 / 16);
      } else {
        sBaseDp = (float) (1.4 + 8 * (deviceWidthDp - 1000) / 1500 / 16);
      }
    }
    return sBaseDp;
  }

  private static float getScreenDp(Context context) {
    if (sScreenDp < 0) {
      DisplayMetrics metrics = getDisplayMetrics(context);
      //取宽高最小值,防止异常情况下字体按照高度进行换算
      int widthPixels = Math.min(metrics.widthPixels, metrics.heightPixels);
      sScreenDp = widthPixels / metrics.density;
    }
    return sScreenDp;
  }

  private static DisplayMetrics getDisplayMetrics(Context context) {
    if (sDisplayMetrics == null) {
      sDisplayMetrics = context.getResources().getDisplayMetrics();
    }
    return sDisplayMetrics;
  }

}
