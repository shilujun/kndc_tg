package com.cashhub.cash.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cashhub.cash.common.CommonApi;
import com.cashhub.cash.common.utils.CommonUtil;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

  private static final String TAG = "LoginActivity";
  private LinearLayout lytVerifyCode;
  private EditText editPhoneTxt;
  private ImageView ivClear;
  private ImageView ivError;
  private ImageView ivGap;
  private CheckBox chxProtocol;
  private TextView tvTips;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    lytVerifyCode = findViewById(R.id.lyt_verify_code);
    lytVerifyCode.setOnClickListener(this);

    editPhoneTxt = findViewById(R.id.edit_phone_num);
    ivClear = findViewById(R.id.iv_clear);
    initEditClearListener(editPhoneTxt, ivClear);
    ivClear.setOnClickListener(this);

    ivError = findViewById(R.id.iv_error);
    chxProtocol = findViewById(R.id.chx_protocol);
    tvTips = findViewById(R.id.tv_tips);
    ivGap = findViewById(R.id.iv_gap);
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.lyt_verify_code) {
      if(!chxProtocol.isChecked()) {
        chxProtocol.didTouchFocusSelect();
        return;
      }
      if(editPhoneTxt != null) {
        String phoneNum = editPhoneTxt.getText().toString();
        verifyCodeSubmit(phoneNum);
        return;
      }
      Log.d(TAG,"lytVerifyCode onClick");
    } else if(v.getId() == R.id.iv_clear) {
      Log.d(TAG, "onClick: iv_clear");
      if(editPhoneTxt == null) {
        Log.d(TAG, "onClick: editPhoneTxt is NULL");
        return;
      }
      editPhoneTxt.setText("");
    }
  }

  //获取验证码提交事件
  private void verifyCodeSubmit(String phoneNum) {
    Log.d(TAG,"verifyCodeSubmit phoneNum:" + phoneNum);
    if(!CommonUtil.isVietnamMobile(phoneNum)) {
      Log.d(TAG,"is Not phone num");
      if(ivError != null) {
        ivError.setVisibility(View.VISIBLE);
      }

      if(tvTips != null) {
        tvTips.setTextColor(getResources().getColor(R.color.colorRed, this.getTheme()));
      }

      if(ivGap != null) {
        ivGap.setImageDrawable(getDrawable(R.drawable.bg_color_ff1111));
      }
      return;
    }
    CommonApi.getInstance().getCheckCode(this, phoneNum);
    Intent intent = new Intent();
    intent.setClassName(this, "com.cashhub.cash.app.CheckActivity");
    startActivity(intent);
  }

  //重置View
  private void viewReset() {
    Log.d(TAG,"verifyCodeSubmit");
    if(ivError != null) {
      ivError.setVisibility(View.GONE);
    }

    if(tvTips != null) {
      tvTips.setTextColor(getResources().getColor(R.color.btn_light, this.getTheme()));
    }

    if(ivGap != null) {
      ivGap.setImageDrawable(getDrawable(R.drawable.bg_color));
    }
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
}