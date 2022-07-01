package com.cashhub.cash.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.blankj.utilcode.util.Utils;
import com.cashhub.cash.app.widget.PopWinBottomLayout;
import com.cashhub.cash.app.widget.SecurityCodeView;
import com.cashhub.cash.app.widget.SecurityCodeView.InputCompleteListener;
import com.cashhub.cash.common.CommonApi;
import com.cashhub.cash.common.KndcStorage;

public class CheckActivity extends BaseActivity implements View.OnClickListener {

  private static final String TAG = "CheckActivity";
  public static final String LOGIN_PHONE_NUM = "login_phone_num";

  private Context mContext;
  private SecurityCodeView scvVerifyCode;
  private LinearLayout lltLogin;
  private PopWinBottomLayout popWinBottomLayout;

  private TextView txtTimer;
  private String mPhoneNum = "";
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_check);
    mContext = this;

    scvVerifyCode = findViewById(R.id.scv_verify_code);
    scvVerifyCode.setInputCompleteListener(new VerifyCodeInputCompleteListener());

    lltLogin = findViewById(R.id.lyt_login);
    lltLogin.setOnClickListener(this);
    popWinBottomLayout = findViewById(R.id.pop_win_bottom);
    popWinBottomLayout.setBg(findViewById(R.id.fl_bg));

    txtTimer = findViewById(R.id.txt_timer);

    Intent intent = getIntent();
    mPhoneNum = intent.getStringExtra(LOGIN_PHONE_NUM);

    timer.start();
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    if(id == R.id.lyt_login) {
      if(popWinBottomLayout.getPopWinStatus()){
        popWinBottomLayout.hidePopView();
      } else {
        popWinBottomLayout.showPopView();
      }
    }
  }

  /** 倒计时60秒，一次1秒 */
  CountDownTimer timer = new CountDownTimer(60*1000, 1000) {
    @Override
    public void onTick(long millisUntilFinished) {
      // TODO Auto-generated method stub
      txtTimer.setText(millisUntilFinished/1000 + "");
    }

    @Override
    public void onFinish() {
      txtTimer.setText("");
    }
  };

  private class VerifyCodeInputCompleteListener implements
      InputCompleteListener {

    @Override
    public void inputComplete() {
      String verifyCode = scvVerifyCode.getEditContent();
      if(TextUtils.isEmpty(verifyCode) || TextUtils.isEmpty(mPhoneNum)) {
        Log.d(TAG,"verifyCode or phoneNum is NULL!!!");
        return;
      }
      Log.d(TAG,"verifyCode:" + verifyCode + ",phoneNum:" + mPhoneNum);
      CommonApi.getInstance().userLogin(mContext, mPhoneNum, verifyCode);
    };

    @Override
    public void deleteContent(boolean isDelete) {

    };
  }
}