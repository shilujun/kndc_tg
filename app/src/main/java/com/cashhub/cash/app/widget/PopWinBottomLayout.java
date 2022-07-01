package com.cashhub.cash.app.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.cashhub.cash.app.R;
import androidx.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class PopWinBottomLayout extends LinearLayout implements View.OnClickListener{

  private Context mContext;
  private TextView tvCancel, takePhoto, localPhoto;
  private PopupWindow window;
  private View view;
  private Boolean popWinIsShow = false;
  private ICallback mICallback;
  private FrameLayout bgView;
  private Handler handler = new Handler(new Handler.Callback(){
    @Override
    public boolean handleMessage(Message message) {
      if(message.what == 0x00) popWinIsShow = false;
      return false;
    }
  });

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    return true;
  }

  public PopWinBottomLayout(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    mContext = context;
    initView();
    initPop();
    initLisener();
  }

  private void initView(){
    view = LayoutInflater.from(mContext).inflate(R.layout.layout_pop_window_bottom, null);
//    tvCancel = view.findViewById(R.id.tv_cancel);
//    takePhoto = view.findViewById(R.id.tv_take_photo);
//    localPhoto = view.findViewById(R.id.tv_local_photo);
  }

  private void initLisener(){
    //消失监听听
    window.setOnDismissListener(new PopupWindow.OnDismissListener() {
      @Override
      public void onDismiss() {
        if(bgView != null) bgView.setVisibility(View.GONE);
        handler.sendEmptyMessageDelayed(0x00, 100);
      }
    });
//    tvCancel.setOnClickListener(this);
//    takePhoto.setOnClickListener(this);
//    localPhoto.setOnClickListener(this);
  }

  private void initPop(){
    //创建PopupWindow对象
    window = new PopupWindow(this);
    //给PopupWindow设置View
    window.setContentView(view);
    //设置宽和高
    window.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//    window.setWidth(WinManagerUtil.getWidth((Activity) mContext));
    window.setWidth(500);
    //点击弹窗外是否可消失
    window.setOutsideTouchable(true);
  }

  public void showPopView(){
    if(this != null && window != null){
      window.showAtLocation(this, Gravity.BOTTOM,0,0);
      popWinIsShow = true;
      if(null != bgView) bgView.setVisibility(View.VISIBLE);
    }
  }

  public void hidePopView(){
    if(window != null){
      window.dismiss();
      popWinIsShow = false;
      if(bgView != null) bgView.setVisibility(View.GONE);
    }
  }

  public Boolean getPopWinStatus(){
    return popWinIsShow;
  }

  public void setICallback(ICallback iCallback){
    mICallback = iCallback;
  }

  @Override
  public void onClick(View view) {
//    int id = view.getId();
//    if(id == R.id.tv_take_photo){
//      if(null != mICallback) mICallback.onItemClick(Click.TAKE_PHOTO.getCode(), Click.TAKE_PHOTO.getName());
//    }else if(id == R.id.tv_local_photo){
//      if(null != mICallback) mICallback.onItemClick(Click.LOCAL_UPLOAD.getCode(), Click.LOCAL_UPLOAD.getName());
//    }
//    if(id == R.id.tv_cancel)
      hidePopView();
  }

  public interface ICallback{
    void onItemClick(int pos, String name);
  }

  public void setBg(FrameLayout bg){
    bgView = bg;
  }

  /**
   * 按钮点击类型枚举
   */
  public enum Click {

    TAKE_PHOTO(0, "拍照"), LOCAL_UPLOAD(1, "本地上传");

    private final Integer code;

    private final String name;

    public Integer getCode() {
      return code;
    }

    public String getName() {
      return name;
    }

    Click(Integer code, String name) {
      this.code = code;
      this.name = name;
    }

    private static Map<Integer, Click> KEY_MAP = new HashMap<>();

    static {
      for (Click value : Click.values()) {
        KEY_MAP.put(value.getCode(), value);
      }
    }

    public static Click getType(Integer code) {
      return KEY_MAP.get(code);
    }
  }
}