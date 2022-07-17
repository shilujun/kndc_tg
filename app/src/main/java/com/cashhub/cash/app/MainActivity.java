package com.cashhub.cash.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.alibaba.fastjson.JSONObject;
import com.cashhub.cash.app.model.Config;
import com.cashhub.cash.common.CommonApi;
import com.cashhub.cash.common.Host;
import com.cashhub.cash.common.KndcStorage;
import com.cashhub.cash.common.TrackData;
import com.cashhub.cash.common.utils.CommonUtil;
import com.cashhub.cash.common.utils.DeviceUtils;
//import com.cashhub.cash.common.utils.StatusBarUtils;
//import com.tencent.sonic.sdk.SonicConfig;
//import com.tencent.sonic.sdk.SonicEngine;
//import com.tencent.sonic.sdk.SonicSessionConfig;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends BaseActivity implements View.OnClickListener {

  private String TAG = "MainActivity";

  private String mDemoUrl;
  private Uri mImageUri;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    bindClickEvent();

    final UrlListAdapter urlListAdapter = new UrlListAdapter(MainActivity.this);
    mDemoUrl = urlListAdapter.getCheckedUrl();
  }

  @RequiresApi(api = VERSION_CODES.N)
  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.btn_reset) {
//      SonicEngine.getInstance().cleanCache();
    } else if (v.getId() == R.id.btn_default_mode) {
      startBrowserActivity(MODE_DEFAULT);
    } else if (v.getId() == R.id.btn_device_info) {
      TrackData.getInstance().report(this, null);
      getDeviceInfo();
    } else if (v.getId() == R.id.btn_check_permisson_ok) {
      CommonApp.beginPermission();
    } else if (v.getId() == R.id.btn_toast) {
      showToastShort1(MainActivity.this, "我是测试弹窗");
    } else if (v.getId() == R.id.btn_camera) {
      KndcStorage.getInstance().setData(BaseActivity.LINE_TYPE, "living");
      KndcStorage.getInstance().setData(BaseActivity.UPLOAD_TYPE, "1");
      openCamera();
    } else if (v.getId() == R.id.btn_gallery) {
      KndcStorage.getInstance().setData(BaseActivity.LINE_TYPE, "ocr");
      KndcStorage.getInstance().setData(BaseActivity.UPLOAD_TYPE, "3");
      openPicture();
    } else if (v.getId() == R.id.btn_open_login) {
      Intent intent = new Intent();
      intent.setClassName(this, "com.cashhub.cash.app.LoginActivity");
      startActivity(intent);
    } else if (v.getId() == R.id.btn_logout) {
      CommonApi commonApi = new CommonApi();
      commonApi.userLogout(this, KndcStorage.getInstance().getData(KndcStorage.USER_TOKEN));
    } else if (v.getId() == R.id.btn_open_code) {
      Intent intent = new Intent();
      intent.setClassName(this, "com.cashhub.cash.app.CheckActivity");
      startActivity(intent);
    } else if (v.getId() == R.id.btn_track_data) {
      CommonApi commonApi = new CommonApi();
      commonApi.trackData(this, "");
    } else if (v.getId() == R.id.btn_get_storage_data) {
      Map<String, String> storageData = KndcStorage.getInstance().getDataMap();
      if (storageData == null || storageData.isEmpty()) {
        Log.d(TAG, "storageData is Null ");
        return;
      }
      storageData.forEach((key, value) -> {
        Log.d(TAG, "storageData " + key + ":" + value);
      });
    } else if (v.getId() == R.id.btn_sonic_preload) {
//      SonicSessionConfig.Builder sessionConfigBuilder = new SonicSessionConfig.Builder();
//      sessionConfigBuilder.setSupportLocalServer(true);
//      // preload session
//      boolean preloadSuccess = SonicEngine.getInstance().preCreateSession(mDemoUrl, sessionConfigBuilder.build());
//      Toast.makeText(getApplicationContext(), preloadSuccess ? "Preload start up success!" : "Preload start up fail!", Toast.LENGTH_LONG).show();
    } else if (v.getId() == R.id.btn_local) {
      mDemoUrl = "http://johnnyshi.com/test.html";
      startBrowserActivity(MODE_SONIC);
    } else if (v.getId() == R.id.btn_insert) {
//      dataOpt(0);
    } else if (v.getId() == R.id.btn_query) {
      dataOpt(1);
    } else if (v.getId() == R.id.btn_sonic) {
      // sonic mode load btn
      startBrowserActivity(MODE_SONIC);
    } else if (v.getId() == R.id.btn_sonic_with_offline) {
      // load sonic with offline cache
      startBrowserActivity(MODE_SONIC_WITH_OFFLINE_CACHE);
    } else if (v.getId() == R.id.btn_fab) {
    }
  }

  private void bindClickEvent() {
    // clean up cache btn
    Button btnReset = (Button) findViewById(R.id.btn_reset);
    btnReset.setOnClickListener(this);

    // default btn
    Button btnDefault = (Button) findViewById(R.id.btn_default_mode);
    btnDefault.setOnClickListener(this);

    // clean up cache btn
    Button btnCheckOK = (Button) findViewById(R.id.btn_check_permisson_ok);
    btnCheckOK.setOnClickListener(this);

    // clean up cache btn
    Button btnToast = (Button) findViewById(R.id.btn_toast);
    btnToast.setOnClickListener(this);

    // device btn
    Button btnDeviceInfo = (Button) findViewById(R.id.btn_device_info);
    btnDeviceInfo.setOnClickListener(this);

    //camera btn
    Button btnCamera = (Button) findViewById(R.id.btn_camera);
    btnCamera.setOnClickListener(this);

    //gallery btn
    Button btnGallery = (Button) findViewById(R.id.btn_gallery);
    btnGallery.setOnClickListener(this);

    //打开登录页
    Button btnOpenLogin = (Button) findViewById(R.id.btn_open_login);
    btnOpenLogin.setOnClickListener(this);

    //退出登录
    Button btnLogout = (Button) findViewById(R.id.btn_logout);
    btnLogout.setOnClickListener(this);

    //打开验证码页
    Button btnOpenCode = (Button) findViewById(R.id.btn_open_code);
    btnOpenCode.setOnClickListener(this);

    //数据上报
    Button btnTrackData = (Button) findViewById(R.id.btn_track_data);
    btnTrackData.setOnClickListener(this);

    //获取缓存数据
    Button btnGetStorageData = (Button) findViewById(R.id.btn_get_storage_data);
    btnGetStorageData.setOnClickListener(this);

    // preload btn
    Button btnSonicPreload = (Button) findViewById(R.id.btn_sonic_preload);
    btnSonicPreload.setOnClickListener(this);

    // sonic mode load btn
    Button btnLocal = (Button) findViewById(R.id.btn_local);
    btnLocal.setOnClickListener(this);

    Button btnInsert = (Button) findViewById(R.id.btn_insert);
    btnInsert.setOnClickListener(this);

    Button btnQuery = (Button) findViewById(R.id.btn_query);
    btnQuery.setOnClickListener(this);

    // sonic mode load btn
    Button btnSonic = (Button) findViewById(R.id.btn_sonic);
    btnSonic.setOnClickListener(this);

    // load sonic with offline cache
    Button btnSonicWithOfflineCache = (Button) findViewById(R.id.btn_sonic_with_offline);
    btnSonicWithOfflineCache.setOnClickListener(this);

    Button btnFab = (Button) findViewById(R.id.btn_fab);
    btnFab.setOnClickListener(this);
  }

  private void init() {
    // init sonic engine
//    if (!SonicEngine.isGetInstanceAllowed()) {
//      SonicEngine.createInstance(new SonicRuntimeImpl(getApplication()), new SonicConfig.Builder().build());
//    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Log.d(TAG, "onActivityResult, requestCode:" + requestCode + ",resultCode:" + resultCode);
  }

  private void startBrowserActivity(int mode) {
//    Intent intent = new Intent(this, BrowserActivity.class);
//    intent.putExtra(BrowserActivity.PARAM_URL, mDemoUrl);
//    intent.putExtra(BrowserActivity.PARAM_MODE, mode);
//    intent.putExtra(SonicJavaScriptInterface.PARAM_CLICK_TIME, System.currentTimeMillis());
//    startActivity(intent);
  }


  @RequiresApi(api = Build.VERSION_CODES.M)
  public void btn_upload_activity(View view) {
    Intent intent = new Intent();
    intent.setClassName(this, "com.cashhub.cash.app.UploadActivity");
    startActivity(intent);
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  public void btn_web_view(View view) {
//    Intent intent = new Intent();
//    intent.setClassName(this, "com.cashhub.cash.app.WebviewActivity");
//    startActivity(intent);
    CommonApp.navigateTo(this, Host.getH5Host(this, "/#/pages/index/index"));
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  public void dataOpt(int type) {
    if(type == 1) {
      List<Config> configList = getDaoConfig().queryBuilder().build().list();
      if(configList == null || configList.isEmpty()) {
        Log.d(TAG, "dataOpt: configList is null or empty");
      }

      for ( Config config: configList ) {
        Log.d(TAG,
            "CONFIG DATA, " + config.getId() + ":" + config.getConfigKey() +
                ", value:" + config.getConfigValue());
      }
    } else {
      getDaoConfig().deleteAll();

      List<Config> configList = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
        Config config = new Config();
        config.setId((long) i);
        config.setConfigKey("test_" + i);
        // 随机生成汉语名称
        config.setConfigValue(generateRandomStr(10));

        configList.add(config);
        Log.d(TAG, "dataOpt, config insert:" + i);
      }
      getDaoConfig().insertInTx(configList);
    }
  }

  /**
   * 获取随机字符串
   */
  private String generateRandomStr(int length) {
    ArrayList<String> strList = new ArrayList<String>();
    Random random = new Random();

    //将0-9的数字加入集合
    for (int i = 0; i < 10; i++) {
      strList.add(i + "");
    }

    StringBuffer sb = new StringBuffer();
    int size = strList.size();
    for (int i = 0; i < length; i++) {
      String randomStr = strList.get(random.nextInt(size));
      sb.append(randomStr);
    }
    return sb.toString();
  }

  private void getDeviceInfo() {
    Log.d(TAG, "开始获取设备信息======");

    Log.d(TAG, "系统信息:" + String.valueOf(DeviceUtils.getSystemInfo()));
    Log.d(TAG, "系统信息 Context:" + String.valueOf(DeviceUtils.getSystemInfo(this)));
    Log.d(TAG, "getApplicationId:" + String.valueOf(CommonUtil.getApplicationId(this)));

    Log.d(TAG, "getVersionName:" + String.valueOf(CommonUtil.getVersionName(this)));

    Log.d(TAG,  "getVersionCode:" + String.valueOf(CommonUtil.getVersionCode(this)));

    Log.d(TAG, "getUniqueID:" + String.valueOf(CommonUtil.getUniqueID(this)));

//    Log.d(TAG, "SystemInfo:");
//    Log.d(TAG, String.valueOf(DeviceUtils.getSystemInfo(this)));
    Log.d(TAG, "状态栏高度:" + String.valueOf(CommonUtil.getStatusBarHeight(this)));

    Log.d(TAG, "状态栏高度Dp:" + String.valueOf(CommonUtil.getStatusBarHeightDp(this)));
    Log.d(TAG, "显示高度:" + String.valueOf(DeviceUtils.getDisplayHeight(this)));

    Log.d(TAG, "DeviceId:" + DeviceUtils.getDeviceId(this));
    Log.d(TAG, "titleBarHeight:" + String.valueOf(CommonUtil.getTitleBarHeight(this)));

    Log.d(TAG, "=======VERSION.RELEASE:" + Build.VERSION.RELEASE);
    Log.d(TAG, "=======VERSION.SDK_INT:" + Build.VERSION.SDK_INT);
    Log.d(TAG, "=======BOARD:" + Build.BOARD);
    Log.d(TAG, "=======BOOTLOADER:" + Build.BOOTLOADER);
    Log.d(TAG, "=======BRAND:" + Build.BRAND);
    Log.d(TAG, "=======CPU_ABI:" + Build.CPU_ABI);
    Log.d(TAG, "=======CPU_ABI2:" + Build.CPU_ABI2);
    Log.d(TAG, "=======DEVICE:" + Build.DEVICE);
    Log.d(TAG, "=======DISPLAY:" + Build.DISPLAY);
    Log.d(TAG, "=======FINGERPRINT:" + Build.FINGERPRINT);
    Log.d(TAG, "=======HARDWARE:" + Build.HARDWARE);
    Log.d(TAG, "=======HOST:" + Build.HOST);
    Log.d(TAG, "=======ID:" + Build.ID);
    Log.d(TAG, "=======MODEL:" + Build.MODEL);
    Log.d(TAG, "=======MANUFACTURER:" + Build.MANUFACTURER);
    Log.d(TAG, "=======PRODUCT:" + Build.PRODUCT);
    Log.d(TAG, "=======RADIO:" + Build.RADIO);
    Log.d(TAG, "=======TAGS:" + Build.TAGS);
    Log.d(TAG, "=======TIME:" + Build.TIME);
    Log.d(TAG, "=======TYPE:" + Build.TYPE);
    Log.d(TAG, "=======USER:" + Build.USER);
    Log.d(TAG, "=======VERSION.CODENAME:" + Build.VERSION.CODENAME);
    Log.d(TAG, "=======VERSION.INCREMENTAL:" + Build.VERSION.INCREMENTAL);
    Log.d(TAG, "结束获取设备信息======");
  }
}