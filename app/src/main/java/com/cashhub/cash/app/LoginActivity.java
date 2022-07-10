package com.cashhub.cash.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSONObject;
import com.cashhub.cash.common.CommonApi;
import com.cashhub.cash.common.Host;
import com.cashhub.cash.common.KndcEvent;
import com.cashhub.cash.common.KndcStorage;
import com.cashhub.cash.common.TrackData;
import com.cashhub.cash.common.utils.CommonUtil;
import com.cashhub.cash.common.utils.DeviceUtils;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

  private static final String TAG = "LoginActivity";
  private Context mContext;
  private LinearLayout lltVerifyCode;
  private LinearLayout lltBack;
  private EditText editPhoneTxt;
  private ImageView ivClear;
  private ImageView ivError;
  private ImageView ivGap;
  private ImageView ivCustomerService;
  private CheckBox chxProtocol;
  private TextView tvTips;
  private TextView tvProtocol;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    mContext = this;

    lltVerifyCode = findViewById(R.id.llt_verify_code);
    lltVerifyCode.setOnClickListener(this);

    lltBack = findViewById(R.id.llt_back);
    lltBack.setOnClickListener(this);

    editPhoneTxt = findViewById(R.id.edit_phone_num);

    ivClear = findViewById(R.id.iv_clear);
    initEditClearListener(editPhoneTxt, ivClear);
    ivClear.setOnClickListener(this);

    ivCustomerService = findViewById(R.id.iv_customer_service);
    ivCustomerService.setOnClickListener(this);

    tvProtocol = findViewById(R.id.txt_protocol);
    tvProtocol.setMovementMethod(LinkMovementMethod.getInstance());
    tvProtocol.setText(generateSp(tvProtocol.getText().toString()));

    ivError = findViewById(R.id.iv_error);
    chxProtocol = findViewById(R.id.chx_protocol);
    tvTips = findViewById(R.id.tv_tips);
    ivGap = findViewById(R.id.iv_gap);



    setConfigInfo(KndcStorage.H5_IS_CHECK_PERMISSION, "1");

    //弹出数字键盘
    waitPopNumKeyboard(editPhoneTxt);
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.llt_verify_code) {
      if(!chxProtocol.isChecked()) {
        showToastShort("กรุณาอ่าน และรับการลงทะเบียน และนโยบายความเป็นส่วนตัว");
        return;
      }
      if(editPhoneTxt != null) {
        String phoneNum = editPhoneTxt.getText().toString();
        verifyCodeSubmit(phoneNum);
        return;
      }
      Log.d(TAG,"lytVerifyCode onClick");
    } else if(id == R.id.iv_clear) {
      Log.d(TAG, "onClick: iv_clear");
      if(editPhoneTxt == null) {
        Log.d(TAG, "onClick: editPhoneTxt is NULL");
        return;
      }
      editPhoneTxt.setText("");
    } else if(id == R.id.iv_customer_service) {
      //人工客服
      CommonApp.navigateToInWeb(this, Host.HOST_CUSTOMER_SERVICE);
//      finish();
      return;
    } else if(id == R.id.llt_back) {
      //后退按钮点击
      finish();
    }
  }

  @Override
  public void onMessageEvent(KndcEvent event) {
    super.onMessageEvent(event);
    Log.d(TAG, "onMessageEvent: " + event.getEventName());
    if (KndcEvent.CLOSE_LOGIN_ACTIVITY.equals(event.getEventName())) {
      finish();
    }
  }

  //获取验证码提交事件
  private void verifyCodeSubmit(String phoneNum) {
    Log.d(TAG,"verifyCodeSubmit phoneNum:" + phoneNum);
    if(!CommonUtil.isVietnamMobile(phoneNum)) {
      Log.d(TAG,"is Not phone num");
      ivError.setVisibility(View.VISIBLE);
      tvTips.setTextColor(getResources().getColor(R.color.colorRed, this.getTheme()));
      ivGap.setImageDrawable(getDrawable(R.drawable.bg_color_ff1111));
      lltVerifyCode.setBackground(getDrawable(R.drawable.button_round_bg_unable));
      //埋点手机号验证错误
      TrackData.getInstance().checkPhoneFail(this);
      return;
    }
    CommonApi.getInstance().getCheckCode(this, phoneNum);
  }

  //重置View
  private void viewReset() {
    Log.d(TAG,"verifyCodeSubmit");
    ivError.setVisibility(View.GONE);
    tvTips.setTextColor(getResources().getColor(R.color.btn_light, this.getTheme()));
    ivGap.setImageDrawable(getDrawable(R.drawable.bg_color));
    lltVerifyCode.setBackground(getDrawable(R.drawable.button_round_bg));
  }

  //EditText 失去焦点
  @SuppressLint("ResourceType")
  private void editTextNoFocus() {
    Log.d(TAG,"editTextNoFocus");
    if(!CommonUtil.isVietnamMobile(editPhoneTxt.getText().toString())) {
      Log.d(TAG,"is Not phone num");
      if(ivError != null) {
        ivError.setBackground(getResources().getDrawable(R.id.iv_error));
      }
    }
  }

  //输入内容时显示删除按钮
  protected void setVisibility(ImageView iv, CharSequence s) {
    iv.setVisibility(s.length() > 0 ? View.VISIBLE : View.INVISIBLE);
  }
  //获得焦点，以及有内容时，显示删除按钮
  protected void setVisibility(ImageView iv, boolean focus, int len) {
    iv.setVisibility((focus &&  len > 0)? View.VISIBLE : View.INVISIBLE);
  }
  //一个文本编辑框的监听
  protected void initEditClearListener(EditText et1, final ImageView iv1) {
    //焦点变化监听者
    et1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        EditText e= (EditText) v;
        setVisibility(iv1, hasFocus, e.getText().length());
        if(hasFocus) {

        } else {
//          editTextNoFocus();
        }
      }
    });
    //内容变化监听者
    et1.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        setVisibility(iv1, s);
        viewReset();
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });
  }
//  //两个文本编辑框的监听
//  protected void initEditClearListener(EditText et1, final ImageView iv1, EditText et2, final ImageView iv2) {
//    initEditClearListener(et1, iv1);
//    initEditClearListener(et2, iv2);
//  }
//  //三个文本编辑框的监听
//  protected void initEditClearListener(EditText et1, final ImageView iv1, EditText et2, final ImageView iv2, EditText et3, final ImageView iv3) {
//    initEditClearListener(et1, iv1, et2, iv2);
//    initEditClearListener(et3, iv3);
//  }

  private SpannableString generateSp(String text) {
    //定义需要操作的内容
    String high_light_1 = "ในการให้บริการ"; //用户协议
    String high_light_2 = "นโยบายความเป็นส่วนตัว"; //隐私政策
    SpannableString spannableString = new SpannableString(text);
    //初始位置
    int start = 0;
    //结束位置
    int end;
    int index;
    //indexOf(String str, int fromIndex): 返回从 fromIndex 位置开始查找指定字符在字符串中第一次出现处的索引，如果此字符串中没有这样的字符，则返回 -1。
    //简单来说，(index = text.indexOf(high_light_1, start))   -1这部分代码就是为了查找你的内容里面有没有high_light_1这个值的内容，并确定它的起始位置
    while ((index = text.indexOf(high_light_1, start)) > -1) {
    //结束的位置
    end = index + high_light_1.length();
    spannableString.setSpan(new QMUITouchableSpan(this.getResources().getColor(R.color.light_blue_400), this.getResources().getColor(R.color.light_blue_400),
      this.getResources().getColor(R.color.transparent),
      this.getResources().getColor(R.color.transparent)) {
      @Override
      public void onSpanClick(View widget) {
          //点击用户协议的相关操作，可以使用WebView来加载一个网页
        Log.d(TAG, "onSpanClick: HOST_USER_AGREEMENT!!!");
          CommonApp.navigateToInWeb(mContext, Host.HOST_USER_AGREEMENT);
          return;
//          finish();
        }
      }, index, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
      start = end;
    }
    start = 0;
    while ((index = text.indexOf(high_light_2, start)) > -1) {
      end = index + high_light_2.length();
      spannableString.setSpan(new QMUITouchableSpan(this.getResources().getColor(R.color.light_blue_400),
          this.getResources().getColor(R.color.light_blue_400),
          this.getResources().getColor(R.color.transparent), this.getResources().getColor(R.color.transparent)) {
        @Override
        public void onSpanClick(View widget) {
          //点击隐私政策的相关操作，可以使用WebView来加载一个网页
          CommonApp.navigateToInWeb(mContext, Host.HOST_PRIVACY);
          return;
//          finish();
        }
      }, index, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
      start = end;
    }
    //最后返回SpannableString
    return spannableString;
  }
}