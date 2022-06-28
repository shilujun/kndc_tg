package com.cashhub.cash.app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.cashhub.cash.common.CommonApi;

public class CheckActivity extends BaseActivity implements View.OnClickListener {

  private static final String TAG = "CheckActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_check);

    Button btnGetCode = findViewById(R.id.btn_get_code);
    btnGetCode.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.btn_get_code) {
      Log.d(TAG,"buttonSync onClick");
      CommonApi commonApi = new CommonApi();
      commonApi.getCheckCode(this, this.userPhoneNumber);
    }
  }
}