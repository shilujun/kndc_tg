package com.cashhub.cash.common.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class StatusBarUtils {
  /** oppo coloros 5.1系统该flag标记状态栏字符变深 */
  private static final int SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT = 0x00000010;

   /**
   * 设置状态栏沉浸式
   * 支持4.4以上的所有机型
   * @param activity
   * @return 设置成功true, 设置失败false
   */
  public static boolean setStatusBarTransparen(Activity activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Window window = activity.getWindow();
      window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
      window.setStatusBarColor(Color.TRANSPARENT);
      return true;
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      Window window = activity.getWindow();
      WindowManager.LayoutParams winParams = window.getAttributes();
      int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
      winParams.flags |= bits;
      window.setAttributes(winParams);
      return true;
    }
    return false;
  }

  /**
   * 获取状态栏颜色
   * 5.0以上所有机型可以正常获取,5.0以下机型统一返回黑色
   * @param activity
   * @return
   */
  public static int getStatusBarColor(Activity activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      int color = activity.getWindow().getStatusBarColor();
      return color;
    } else {
      return Color.BLACK;
    }
  }

  /**
   * 获取状态栏高度
   * @param context
   * @return
   */
  public static int getStatusBarHeight(Context context) {
    int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      return context.getResources().getDimensionPixelSize(resourceId);
    } else {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        return (int) (
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24.0f, context.getResources().getDisplayMetrics()) + 0.5f);
      } else {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25.0f, context.getResources().getDisplayMetrics()) + 0.5f);
      }
    }
  }
}