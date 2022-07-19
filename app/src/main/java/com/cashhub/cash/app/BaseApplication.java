package com.cashhub.cash.app;

import android.app.Application;
import io.branch.referral.Branch;

public class BaseApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();

//    Branch.enableTestMode();
    // Branch logging for debugging
    Branch.enableLogging();

    // Branch object initialization
    Branch.getAutoInstance(this);
  }
}
