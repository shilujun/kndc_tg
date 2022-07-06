package com.cashhub.cash.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.NavUtils;
import com.blankj.utilcode.util.Utils;
import com.cashhub.cash.app.widget.PopWinBottomLayout;
import com.cashhub.cash.app.widget.SecurityCodeView;
import com.cashhub.cash.app.widget.SecurityCodeView.InputCompleteListener;
import com.cashhub.cash.common.CommonApi;
import com.cashhub.cash.common.Host;
import com.cashhub.cash.common.KndcStorage;
import com.cashhub.cash.common.TrackData;
import java.util.Timer;
import java.util.TimerTask;

public class CheckActivity extends BaseActivity implements View.OnClickListener {

  private static final String TAG = "CheckActivity";
  public static final String LOGIN_PHONE_NUM = "login_phone_num";

  private Context mContext;
  private SecurityCodeView scvVerifyCode;
  private LinearLayout lltBack;
  private LinearLayout lltCanNotGetCode;
  private PopWinBottomLayout popWinBottomLayout;
  private ImageView ivCustomerService;

  private TextView txtResendCode;
  private TextView txtTimer;
  private String mPhoneNum = "";
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_check);
    mContext = this;

    scvVerifyCode = findViewById(R.id.scv_verify_code);
    scvVerifyCode.setInputCompleteListener(new VerifyCodeInputCompleteListener());

    lltBack = findViewById(R.id.llt_back);
    lltBack.setOnClickListener(this);

    lltCanNotGetCode = findViewById(R.id.llt_can_not_get_code);
    lltCanNotGetCode.setOnClickListener(this);

    popWinBottomLayout = findViewById(R.id.pop_win_bottom);
    popWinBottomLayout.setBg(findViewById(R.id.fl_bg));

    ivCustomerService = findViewById(R.id.iv_customer_service);
    ivCustomerService.setOnClickListener(this);

    txtResendCode = findViewById(R.id.txt_resend_code);

    txtTimer = findViewById(R.id.txt_timer);

    Intent intent = getIntent();
    mPhoneNum = intent.getStringExtra(LOGIN_PHONE_NUM);

    TextView txtPhoneNum = findViewById(R.id.txtPhoneNum);
    txtPhoneNum.setText(mPhoneNum);

    //弹出数字键盘
    waitPopNumKeyboard(scvVerifyCode.findViewById(R.id.item_edittext));
    timer.start();
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    if(id == R.id.llt_back) {
      //后退按钮点击
      this.finish();
    } else if(id == R.id.llt_can_not_get_code) {
      //收不到验证
      if(popWinBottomLayout != null) {
        if (popWinBottomLayout.getPopWinStatus()) {
          popWinBottomLayout.hidePopView();
        } else {
          //第一步先收起键盘
          InputMethodManager manager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
          if (manager != null) {
            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
          }
          popWinBottomLayout.showPopView();
        }
      }
      //埋点收不到验证码
      TrackData.getInstance().notGetCode(this);
    } else if(id == R.id.iv_customer_service) {
      //人工客服
      Intent intent = new Intent(this, BrowserActivity.class);
      intent.putExtra(BrowserActivity.PARAM_URL, Host.HOST_CUSTOMER_SERVICE);
      intent.putExtra(BrowserActivity.PARAM_MODE, 1);
      intent.putExtra(SonicJavaScriptInterface.PARAM_CLICK_TIME, System.currentTimeMillis());
      startActivity(intent);
    }
  }

  /** 倒计时60秒，一次1秒 */
  CountDownTimer timer = new CountDownTimer(60*1000, 1000) {
    @Override
    public void onTick(long millisUntilFinished) {
      // TODO Auto-generated method stub
      txtTimer.setText("(" + millisUntilFinished/1000 + ")");
    }

    @Override
    public void onFinish() {
      txtTimer.setText("");
      txtResendCode.setTextColor(getResources().getColor(R.color.color_2681E3));
      txtResendCode.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          Log.d(TAG, "onClick: txtResendCode");
          txtResendCode.setTextColor(getResources().getColor(R.color.gray_400));
          CommonApi.getInstance().getCheckCode(mContext, mPhoneNum);
          txtResendCode.setOnClickListener(null);
          //埋点-重新发送验证码
          TrackData.getInstance().resendCode(mContext);
          timer.start();
        }
      });

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