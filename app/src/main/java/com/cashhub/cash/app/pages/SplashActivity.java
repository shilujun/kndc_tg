package com.cashhub.cash.app.pages;

import android.content.Intent;
import android.util.Log;
import androidx.annotation.Nullable;
import android.os.Bundle;
import com.cashhub.cash.app.BaseActivity;
import com.cashhub.cash.app.CommonApp;
//import com.cashhub.cash.app.MainActivity;
import com.cashhub.cash.common.Host;

public class SplashActivity extends BaseActivity {

  private static final String TAG = "SplashActivity";

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (isLauncherStart()) {
      return;
    }
    setFullScreen(this);
    try {
      Thread.sleep(300);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

//    Intent intent = new Intent();
//    intent.setClass(SplashActivity.this, MainActivity.class);
//    startActivity(intent);
    String url = Host.getH5Host(this, "/#/pages/index/index");
//    String url = "http://johnnyshi.com/test.html";
//    String url = "http://kndc.junya.online";
    CommonApp.navigateTo(this, url);
    Log.d(TAG, "onCreate: " + url);
    finish();
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  private boolean isLauncherStart() {
    if (!this.isTaskRoot()) {
      Intent intent = getIntent();
      if (intent != null) {
        if ((intent.getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)> 0) {
          finish();
          return true;
        }
      }
    }
    return false;
  }

}