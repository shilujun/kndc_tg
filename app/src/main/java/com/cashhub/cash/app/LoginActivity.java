package com.cashhub.cash.app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

  private static final String TAG = "LoginActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    LinearLayout lytVerifyCode = findViewById(R.id.lyt_verify_code);
    lytVerifyCode.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.lyt_verify_code) {
      Log.d(TAG,"lytVerifyCode onClick");
    }
  }
}