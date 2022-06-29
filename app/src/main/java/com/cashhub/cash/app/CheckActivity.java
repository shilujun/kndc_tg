package com.cashhub.cash.app;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import com.blankj.utilcode.util.Utils;
import com.cashhub.cash.app.widget.SecurityCodeView;
import com.cashhub.cash.common.CommonApi;

public class CheckActivity extends BaseActivity implements View.OnClickListener {

  private static final String TAG = "CheckActivity";
  public static final String LOGIN_PHONE_NUM = "login_phone_num";

  private SecurityCodeView scvVerifyCode;
  private LinearLayout lltLogin;
  private String mPhoneNum = "";
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_check);

    scvVerifyCode = findViewById(R.id.scv_verify_code);

    lltLogin = findViewById(R.id.lyt_login);
    lltLogin.setOnClickListener(this);

    Intent intent = getIntent();
    mPhoneNum = intent.getStringExtra(LOGIN_PHONE_NUM);
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.lyt_login) {
      String verifyCode = scvVerifyCode.getEditContent();
      if(TextUtils.isEmpty(verifyCode) || TextUtils.isEmpty(mPhoneNum)) {
        Log.d(TAG,"verifyCode or phoneNum is NULL!!!");
        return;
      }
      Log.d(TAG,"verifyCode:" + verifyCode + ",phoneNum:" + mPhoneNum);
      CommonApi.getInstance().userLogin(this, mPhoneNum, verifyCode);
    }
  }
}