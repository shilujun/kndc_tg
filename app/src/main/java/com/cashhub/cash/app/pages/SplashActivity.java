package com.cashhub.cash.app.pages;

import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.cashhub.cash.app.BaseActivity;
import com.cashhub.cash.app.MainActivity;

public class SplashActivity extends BaseActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    setFullScreen(this);
    super.onCreate(savedInstanceState);

    if (isLauncherStart()) {
      return;
    }
    //TODO 调试使用  需要去除
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    Intent intent = new Intent();
    intent.setClass(SplashActivity.this, MainActivity.class);
    startActivity(intent);
    finish();
    return;
  }

  private boolean isLauncherStart() {
    if (!this.isTaskRoot()) {
      Intent intent = getIntent();
      if (intent != null) {
        String action = intent.getAction();
        if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
          finish();
          return true;
        }
      }
    }
    return false;
  }

}