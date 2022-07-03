package com.cashhub.cash.app;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.blankj.utilcode.util.ImageUtils;
import com.cashhub.cash.app.greendao.ConfigDao;
import com.cashhub.cash.app.greendao.DaoMaster;
import com.cashhub.cash.app.greendao.DaoSession;
import com.cashhub.cash.app.greendao.ReportInfoDao;
import com.cashhub.cash.app.model.Config;
import com.cashhub.cash.common.CommonResult;
import com.cashhub.cash.common.ImageUpload;
import com.cashhub.cash.common.KndcEvent;
import com.cashhub.cash.common.KndcStorage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class BaseActivity extends AppCompatActivity {

  private static final String TAG = "BaseActivity";

  public static final int MODE_DEFAULT = 0;

  public static final int MODE_SONIC = 1;

  public static final int MODE_SONIC_WITH_OFFLINE_CACHE = 2;

  public static final int PERMISSION_REQUEST_CODE_STORAGE = 1;

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
    switch (requestCode) {
      case TAKE_PHOTO:
        if (resultCode == RESULT_OK) {
          try {
            //将拍摄的照片显示出来
            Bitmap bitmap = data.getParcelableExtra("data");
            //bitmap = ImageUtils.compressByScale(bitmap, 0.5f, 0.5f);
            byte[] bitmapB = ImageUtils.compressByQuality(bitmap, 30);
            bitmap = ImageUtils.bytes2Bitmap(bitmapB);
//            camereIv.setImageBitmap(bitmap);  //TODO

            ImageUpload imageUpload = new ImageUpload();
            imageUpload.saveImageToGallery(BaseActivity.this, bitmap);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        break;
      case TAKE_CAMARA:
        if (resultCode == RESULT_OK) {
          try {
            //将相册的照片显示出来
            Uri uri_photo = data.getData();
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri_photo));
            //bitmap = ImageUtils.compressByScale(bitmap, 0.5f, 0.5f);
            byte[] bitmapB = ImageUtils.compressByQuality(bitmap, 30);
            bitmap = ImageUtils.bytes2Bitmap(bitmapB);
//            photoIv.setImageBitmap(bitmap); //TODO
            //EncodeUtils.base64Decode();
//                        String a = EncodeUtils.base64Encode2String(ImageUtils.bitmap2Bytes(bitmap));
//                        MainActivity.i(TAG, a);
            ImageUpload imageUpload = new ImageUpload();
            imageUpload.saveImageToGallery(BaseActivity.this, bitmap);
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          }
        }
        break;
      default:
        break;
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageEvent(KndcEvent event) {
    if(KndcEvent.LOGIN.equals(event.getEventName())) {
      setUserInfo(event);
      //等0.5秒待状态同步完成，再进行页面跳转
//      try {
//        Thread.sleep(500);
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }
//      CommonApp.navigateTo(this, Host.getH5Host(this, "/#/pages/index/index"));
//      Log.d(TAG, "eventName:" + KndcEvent.LOGIN);
    } else if(KndcEvent.LOGOUT.equals(event.getEventName())) {
      //TODO
      Log.d(TAG, "eventName:" + KndcEvent.LOGOUT);
    } else if(KndcEvent.OPEN_CAMARA.equals(event.getEventName())) {
      //将button的点击事件改成startActivityForResult启动相机
      Intent camera = new Intent();
      camera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
      startActivityForResult(camera, TAKE_PHOTO);
      Log.i(TAG, "打开相机成功");
    } else if(KndcEvent.OPEN_IMAGE_CAPTURE.equals(event.getEventName())) {
      Intent intent = new Intent(Intent.ACTION_PICK);  //跳转到 ACTION_IMAGE_CAPTURE
      intent.setType("image/*");
      startActivityForResult(intent, TAKE_CAMARA);
      Log.i(TAG, "跳转相册成功");
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

  /**
   * 登录后设置用户信息
   */
  private void setUserInfo(KndcEvent event) {
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

    KndcStorage.getInstance().setData(KndcStorage.USER_PHONE, phone);
    KndcStorage.getInstance().setData(KndcStorage.USER_ID, retData.get("user_uuid"));
    KndcStorage.getInstance().setData(KndcStorage.USER_TOKEN, retData.get("token"));
    KndcStorage.getInstance().setData(KndcStorage.USER_EXPIRE_TIME, retData.get("expire"));

    //登录信息 存入数据库和缓存，启动app的时候载入
    List<Config> configList = new ArrayList<>();
    Config configPhone = new Config();
    configPhone.setConfigKey(KndcStorage.USER_PHONE);
    configPhone.setConfigValue(phone);
    configList.add(configPhone);

    Config configUserId = new Config();
    configUserId.setConfigKey(KndcStorage.USER_ID);
    configUserId.setConfigValue(retData.get("user_uuid"));
    configList.add(configUserId);

    Config configToken = new Config();
    configToken.setConfigKey(KndcStorage.USER_TOKEN);
    configToken.setConfigValue(retData.get("token"));
    configList.add(configToken);

    Config configExpire = new Config();
    configExpire.setConfigKey(KndcStorage.USER_EXPIRE_TIME);
    configExpire.setConfigValue(retData.get("expire"));
    configList.add(configExpire);

    mConfigDao.insertInTx(configList);
  }
}