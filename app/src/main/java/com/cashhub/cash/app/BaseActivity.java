package com.cashhub.cash.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.blankj.utilcode.util.CollectionUtils;
import com.cashhub.cash.app.greendao.ConfigDao;
import com.cashhub.cash.app.greendao.DaoMaster;
import com.cashhub.cash.app.greendao.DaoSession;
import com.cashhub.cash.app.greendao.ReportInfoDao;
import com.cashhub.cash.app.model.Config;
import com.cashhub.cash.common.CommonApi;
import com.cashhub.cash.common.CommonResult;
import com.cashhub.cash.common.Host;
import com.cashhub.cash.common.ImageUpload;
import com.cashhub.cash.common.KndcEvent;
import com.cashhub.cash.common.KndcStorage;
import com.cashhub.cash.common.utils.ImageUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class BaseActivity extends AppCompatActivity {

  private static final String TAG = "BaseActivity";


  public static final String LINE_TYPE = "line_type";
  public static final String UPLOAD_TYPE = "upload_type";
  public static final String IMAGE_PATH = "image_path";

  public static final int MODE_DEFAULT = 0;

  public static final int MODE_SONIC = 1;

  public static final int MODE_SONIC_WITH_OFFLINE_CACHE = 2;

  public static final int PERMISSION_REQUEST_CODE_STORAGE = 1;

  public static final int IMAGE_MAX_SIZE = 800;

  public static final int TAKE_PHOTO = 101;
  public static final int TAKE_CAMARA = 100;

  private static final String DATABASE_NAME = "system_info.db";//数据库名称
  public DaoSession mDaoSession;
  public ConfigDao mConfigDao;
  public ReportInfoDao mReportInfoDao;
  public String mUserToken = "";

  public String userPhoneCode = "";

  public String userPhoneNumber = "06865 81435";
  public String verifyCode = "9999";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setFullScreen(this);

    initData();

    EventBus.getDefault().register(this);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if(null != EventBus.getDefault()) {
      EventBus.getDefault().unregister(this);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    String lineType = KndcStorage.getInstance().getData(LINE_TYPE);
    String uploadType = KndcStorage.getInstance().getData(UPLOAD_TYPE);
    Log.d(TAG, "onActivityResult requestCode: " + requestCode);
    Log.d(TAG, "onActivityResult lineType: " + lineType);
    Log.d(TAG, "onActivityResult uploadType: " + uploadType);
    switch (requestCode) {
      case TAKE_PHOTO:
        if (resultCode == RESULT_OK) {
          try {
            //将拍摄的照片上传
            Bitmap bitmap = data.getParcelableExtra("data");
//            String imagePath = ImageUpload.getInstance().saveImageToGallery(this, bitmap);
//            KndcStorage.getInstance().setData(IMAGE_PATH, imagePath);
            CommonApi.getInstance().getPolicySign(this, lineType, uploadType, bitmap);
          } catch (Exception e) {
            openErrorPage(lineType);
            e.printStackTrace();
          }
        } else {
          openErrorPage(lineType);
        }
        break;
      case TAKE_CAMARA:
        if (resultCode == RESULT_OK) {
          try {
            //将相册的照片上传
            Uri uri_photo = data.getData();
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri_photo));
//            String imagePath = ImageUpload.getInstance().saveImageToGallery(this, bitmap);
//            KndcStorage.getInstance().setData(IMAGE_PATH, imagePath);
            CommonApi.getInstance().getPolicySign(this, lineType, uploadType, bitmap);
          } catch (FileNotFoundException e) {
            openErrorPage(lineType);
            e.printStackTrace();
          }
        } else {
          openErrorPage(lineType);
        }
        break;
      default:
        break;
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageEvent(KndcEvent event) {
    Log.d(TAG, "onMessageEvent: " + event.getEventName());
    String lineType = KndcStorage.getInstance().getData(LINE_TYPE);
    String uploadType = KndcStorage.getInstance().getData(UPLOAD_TYPE);

    if(KndcEvent.LOGIN.equals(event.getEventName())) {
      String phone = event.getPhone();
      String commonRet = event.getCommonRet();
      if(TextUtils.isEmpty(phone) || TextUtils.isEmpty(commonRet)) {
        return;
      }
      Gson gson = new Gson();
      CommonResult commonResult = gson.fromJson(commonRet,
          new TypeToken<CommonResult>(){}.getType());
      if (commonResult == null || commonResult.getData() == null) {
        Log.d(TAG, "common result is null");
        return;
      }

      Map<String, String> retData = commonResult.getData();

      //用户登录信息
      String userToken = retData.get("token");
      String userId = retData.get("user_uuid");
      String userExpire = retData.get("expire");
      setUserInfo(phone, userToken, userId, userExpire);
      //等0.5秒待状态同步完成，再进行页面跳转
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      CommonApp.navigateTo(this, Host.getH5Host(this, "/#/pages/index/index"));
    } else if(KndcEvent.LOGOUT.equals(event.getEventName())) {
      clearUserInfo();
    } else if(KndcEvent.GET_POLICY_SIGN.equals(event.getEventName())) {
      uploadImage(event);
    } else if(KndcEvent.UPLOAD_IMAGE_SUCCESS.equals(event.getEventName())) {
      String commonRet = event.getCommonRet();
      if(TextUtils.isEmpty(commonRet)) {
        return;
      }
      Gson gson = new Gson();
      CommonResult commonResult = gson.fromJson(commonRet,
          new TypeToken<CommonResult>(){}.getType());
      if (commonResult == null || commonResult.getData() == null) {
        Log.d(TAG, "common result is null");
        return;
      }

      Map<String, String> retData = commonResult.getData();
      //TODO
      CommonApi.getInstance().uploadSuccessReportData(this, mUserToken, retData.get("downloadUrl"), lineType);
    } else if(KndcEvent.UPLOAD_REPORT_SUCCESS.equals(event.getEventName())) {
      String commonRet = event.getCommonRet();
      if(TextUtils.isEmpty(commonRet)) {
        return;
      }
      Gson gson = new Gson();
      CommonResult commonResult = gson.fromJson(commonRet,
          new TypeToken<CommonResult>(){}.getType());
      if (commonResult == null || commonResult.getData() == null) {
        showToastLong("返回内容为NULL");
        return;
      }
      Map<String, String> retData = commonResult.getData();
      String gotoUrl = "";
      if("ocr".equals(lineType) && commonResult.getCode() == 0 && retData != null &&
          "Y".equals(retData.get("status"))) {
        showToastLong(commonResult.getMsg());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("/#/pagesB/pages/card_auth/suc_card?sign_url=");
        stringBuilder.append(Host.getApiHost(this));
        stringBuilder.append("/api/v1/ocr/image-info");
        stringBuilder.append("&upload_type=");
        stringBuilder.append(uploadType);
        stringBuilder.append("&card_data=");
        stringBuilder.append(retData.toString());
        gotoUrl = Host.getH5Host(this, stringBuilder.toString());
      } else if("".equals(lineType) && commonResult.getCode() == 0) {
        showToastLong(commonResult.getMsg());
        gotoUrl = Host.getH5Host(this, "/#/pagesB/pages/face_recog/face_result?result=success");
      } else {
        showToastLong(commonResult.getMsg());
        if("ocr".equals(lineType)) {
          gotoUrl = Host.getH5Host(this, "/#/pagesB/pages/card_auth/err_card");
        } else {
          gotoUrl = Host.getH5Host(this, "/#/pagesB/pages/face_recog/face_result?result=error");
        }
      }
      if(!TextUtils.isEmpty(gotoUrl)) {

      }
    }
  }

  public DaoSession getDaoSession(){
    return  mDaoSession;
  }

  public ConfigDao getDaoConfig(){
    return  mConfigDao;
  }

  public ReportInfoDao getDaoReportInfo(){
    return  mReportInfoDao;
  }

  public String getUserToken() {
    return mUserToken;
  }

  /**
   * 通过设置全屏，设置状态栏透明
   */
  public void setFullScreen(Activity activity) {
    // full screen
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
//    Window window = activity.getWindow();
//    View decorView = window.getDecorView();
//    //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
//    int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//    decorView.setSystemUiVisibility(option);
//    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//    window.setStatusBarColor(Color.TRANSPARENT);
    //导航栏颜色也可以正常设置
    //window.setNavigationBarColor(Color.TRANSPARENT)
  }

  /**
   * 初始化启动数据
   */
  private void initData() {
    DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, DATABASE_NAME);
    SQLiteDatabase database = helper.getWritableDatabase();
    DaoMaster daoMaster = new DaoMaster(database);
    mDaoSession = daoMaster.newSession();

    mConfigDao = mDaoSession.getConfigDao();
    mReportInfoDao = mDaoSession.getReportInfoDao();

    //配置信息载入初始化
    List<Config> configList = getDaoConfig().queryBuilder().build().list();
    if(configList == null || configList.isEmpty()) {
      Log.d(TAG, "dataOpt: configList is null or empty");
    } else {
      for ( Config config: configList ) {
        KndcStorage.getInstance().setData(config.getConfigKey(), config.getConfigValue());
      }
    }
  }

  //跳转相册
  public void openPicture(String lineType, String uploadType) {
    KndcStorage.getInstance().setData(LINE_TYPE, lineType);
    KndcStorage.getInstance().setData(UPLOAD_TYPE, uploadType);
    Intent intent = new Intent(Intent.ACTION_PICK);  //跳转到 ACTION_IMAGE_CAPTURE
    intent.setType("image/*");
    startActivityForResult(intent, TAKE_CAMARA);
    Log.i(TAG, "跳转相册成功");
  }

  //跳转相机
  public void openCamera(String lineType, String uploadType) {
    //将button的点击事件改成startActivityForResult启动相机
    KndcStorage.getInstance().setData(LINE_TYPE, lineType);
    KndcStorage.getInstance().setData(UPLOAD_TYPE, uploadType);
    Intent intent = new Intent();
    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
    startActivityForResult(intent, TAKE_PHOTO);
  }

  /**
   * 登录后设置用户信息
   */
  public void setUserInfo(String phone, String token, String userId, String expire) {
    KndcStorage.getInstance().setData(KndcStorage.USER_PHONE, phone);
    KndcStorage.getInstance().setData(KndcStorage.USER_ID, userId);
    KndcStorage.getInstance().setData(KndcStorage.USER_TOKEN, token);
    KndcStorage.getInstance().setData(KndcStorage.USER_EXPIRE_TIME, expire);

    //登录信息 存入数据库和缓存，启动app的时候载入
    List<Config> configList = new ArrayList<>();
    Config configPhone = new Config();
    configPhone.setConfigKey(KndcStorage.USER_PHONE);
    configPhone.setConfigValue(phone);
    configList.add(configPhone);

    Config configToken = new Config();
    configToken.setConfigKey(KndcStorage.USER_TOKEN);
    configToken.setConfigValue(token);
    configList.add(configToken);

    Config configUserId = new Config();
    configUserId.setConfigKey(KndcStorage.USER_ID);
    configUserId.setConfigValue(userId);
    configList.add(configUserId);

    Config configExpire = new Config();
    configExpire.setConfigKey(KndcStorage.USER_EXPIRE_TIME);
    configExpire.setConfigValue(expire);
    configList.add(configExpire);

    mConfigDao.insertOrReplaceInTx(configList);
  }

  /**
   * 退出登录
   */
  public void clearUserInfo() {
    try {
      KndcStorage.getInstance().setData(KndcStorage.USER_PHONE, "");
      KndcStorage.getInstance().setData(KndcStorage.USER_ID, "");
      KndcStorage.getInstance().setData(KndcStorage.USER_TOKEN, "");
      KndcStorage.getInstance().setData(KndcStorage.USER_EXPIRE_TIME, "");

      //清理数据库信息
      List<Config> configList =
          mConfigDao.queryRaw("where CONFIG_KEY in ('user_token', 'user_id', 'user_phone', "
              + "'user_expire_time')");

      if(!CollectionUtils.isEmpty(configList)) {
        for (Config config : configList) {
          Log.d(TAG, config.toString());
        }
        mConfigDao.deleteInTx(configList);
      } else {
        Log.d(TAG, "configList is Null or Empty!!!");
      }
    } catch (Exception e) {
      Log.d(TAG, e.getMessage());
    }
  }

  /**
   * 上传图片
   */
  private void uploadImage(KndcEvent event) {
    String commonRet = event.getCommonRet();
    if(TextUtils.isEmpty(commonRet)) {
      showToastLong("获取上传地址失败");
      return;
    }
    Gson gson = new Gson();
    CommonResult commonResult = gson.fromJson(commonRet,
        new TypeToken<CommonResult>(){}.getType());
    if (commonResult == null) {
      showToastLong("获取上传地址失败");
      return;
    }

    if(commonResult.getCode() != 0) {
      showToastLong(commonResult.getMsg());
      return;
    }

    Map<String, String> retData = commonResult.getData();
    if (retData == null) {
      showToastLong("返回内容为NULL");
      return;
    }

    Bitmap bitmap = event.getBitmap();
    if(null == bitmap) {
      showToastLong("数据为NULL");
      return;
    }

    CommonApi.getInstance().uploadImage(this, retData, bitmap);
  }

  /**
   * 跳转到错误页
   * @param lineType 值有ocr和living，ocr为身份证上传页面，living为人脸识别页面
   */
  private void openErrorPage(String lineType) {
    if(TextUtils.isEmpty(lineType)) {
      return;
    }
    if(lineType.equals("ocr")) {
      CommonApp.navigateTo(this, Host.getH5Host(this, "/#/pagesB/pages/card_auth/err_card"));
    } else if(lineType.equals("living")) {
      CommonApp.navigateTo(this, Host.getH5Host(this, "/#/pagesB/pages/face_recog/face_result"));
    }
  }

  /**
   * 显示提示信息
   * @param message
   */
  public void showToastLong(String message) {
    Toast.makeText(this,message, Toast.LENGTH_LONG).show();
  }
}