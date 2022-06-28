package com.cashhub.cash.app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import java.util.HashMap;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

  private static final String TAG = "LoginActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    Button btnLogin = findViewById(R.id.btn_login);
    btnLogin.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.btn_login) {
      Log.d(TAG,"buttonSync onClick");
    }
  }
}