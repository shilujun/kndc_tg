package com.cashhub.cash.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import com.cashhub.cash.app.model.Config;
import com.cashhub.cash.common.CommonApi;
import com.cashhub.cash.common.Host;
import com.cashhub.cash.common.utils.DeviceUtils;
import com.cashhub.cash.common.utils.StatusBarUtils;
import com.tencent.sonic.sdk.SonicConfig;
import com.tencent.sonic.sdk.SonicEngine;
import com.tencent.sonic.sdk.SonicSessionConfig;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends BaseActivity implements View.OnClickListener {

  private String TAG = "MainActivity";

  private String mDemoUrl;
  private Uri mImageUri;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    Log.d(TAG, "onCreate, DeviceId:" + DeviceUtils.getDeviceId(this));
    Log.d(TAG, "onCreate, StatusBarHeight:" + StatusBarUtils.getStatusBarHeight(this));
    Log.d(TAG, "onCreate, Host isDebug:" + Host.getApiHost(this));

    if (hasPermission()) {
      init();
    } else {
      requestPermission();
    }
    bindClickEvent();

    final UrlListAdapter urlListAdapter = new UrlListAdapter(MainActivity.this);
    mDemoUrl = urlListAdapter.getCheckedUrl();
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.btn_reset) {
      SonicEngine.getInstance().cleanCache();
    } else if (v.getId() == R.id.btn_default_mode) {
      startBrowserActivity(MODE_DEFAULT);
    } else if (v.getId() == R.id.btn_camera) {
      openCameraOrGallery("camera");
    } else if (v.getId() == R.id.btn_gallery) {
      openCameraOrGallery("gallery");
    } else if (v.getId() == R.id.btn_open_login) {
//      Intent intent = new Intent();
//      intent.setClassName(this, "com.cashhub.cash.app.LoginActivity");
//      startActivity(intent);

      CommonApi commonApi = new CommonApi();
      commonApi.userLogin(this, this.userPhoneNumber, this.verifyCode);
    } else if (v.getId() == R.id.btn_open_code) {
//      Intent intent = new Intent();
//      intent.setClassName(this, "com.cashhub.cash.app.CheckActivity");
//      startActivity(intent);
      CommonApi commonApi = new CommonApi();
      commonApi.getCheckCode(this, this.userPhoneNumber);
    } else if (v.getId() == R.id.btn_sonic_preload) {
      SonicSessionConfig.Builder sessionConfigBuilder = new SonicSessionConfig.Builder();
      sessionConfigBuilder.setSupportLocalServer(true);
      // preload session
      boolean preloadSuccess = SonicEngine.getInstance().preCreateSession(mDemoUrl, sessionConfigBuilder.build());
      Toast.makeText(getApplicationContext(), preloadSuccess ? "Preload start up success!" : "Preload start up fail!", Toast.LENGTH_LONG).show();
    } else if (v.getId() == R.id.btn_local) {
      mDemoUrl = "http://johnnyshi.com/test.html";
      startBrowserActivity(MODE_SONIC);
    } else if (v.getId() == R.id.btn_insert) {
      dataOpt(0);
    } else if (v.getId() == R.id.btn_query) {
      dataOpt(1);
    } else if (v.getId() == R.id.btn_sonic) {
      // sonic mode load btn
      startBrowserActivity(MODE_SONIC);
    } else if (v.getId() == R.id.btn_sonic_with_offline) {
      // load sonic with offline cache
      startBrowserActivity(MODE_SONIC_WITH_OFFLINE_CACHE);
    } else if (v.getId() == R.id.btn_fab) {
//      UrlSelector.launch(MainActivity.this, urlListAdapter, new UrlSelector.OnUrlChangedListener() {
//        @Override
//        public void urlChanged(String url) {
//          DEMO_URL = url;
//        }
//      });
    }
  }

  private void bindClickEvent() {
    // clean up cache btn
    Button btnReset = (Button) findViewById(R.id.btn_reset);
    btnReset.setOnClickListener(this);

    // default btn
    Button btnDefault = (Button) findViewById(R.id.btn_default_mode);
    btnDefault.setOnClickListener(this);

    //camera btn
    Button btnCamera = (Button) findViewById(R.id.btn_camera);
    btnCamera.setOnClickListener(this);

    //gallery btn
    Button btnGallery = (Button) findViewById(R.id.btn_gallery);
    btnGallery.setOnClickListener(this);

    //打开登录页
    Button btnOpenLogin = (Button) findViewById(R.id.btn_open_login);
    btnOpenLogin.setOnClickListener(this);

    //打开验证码页
    Button btnOpenCode = (Button) findViewById(R.id.btn_open_code);
    btnOpenCode.setOnClickListener(this);

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
    if (!SonicEngine.isGetInstanceAllowed()) {
      SonicEngine.createInstance(new SonicRuntimeImpl(getApplication()), new SonicConfig.Builder().build());
    }
  }


  private boolean hasPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    return true;
  }

  private void requestPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE_STORAGE);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (PERMISSION_REQUEST_CODE_STORAGE == requestCode) {
      if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
        requestPermission();
      } else {
        init();
      }
      return;
    }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  private void startBrowserActivity(int mode) {
    Intent intent = new Intent(this, BrowserActivity.class);
    intent.putExtra(BrowserActivity.PARAM_URL, mDemoUrl);
    intent.putExtra(BrowserActivity.PARAM_MODE, mode);
    intent.putExtra(SonicJavaScriptInterface.PARAM_CLICK_TIME, System.currentTimeMillis());
    startActivity(intent);
  }


  @RequiresApi(api = Build.VERSION_CODES.M)
  public void btn_upload_activity(View view) {
    Intent intent = new Intent();
    intent.setClassName(this, "com.cashhub.cash.app.UploadActivity");
    startActivity(intent);
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  public void btn_web_view(View view) {
    Intent intent = new Intent();
    intent.setClassName(this, "com.cashhub.cash.app.WebviewActivity");
    startActivity(intent);
  }


  @RequiresApi(api = Build.VERSION_CODES.M)
  public void openCameraOrGallery(String type) {
    if(type == null || type.isEmpty()) {
      return;
    }
    if("camera".equals(type)) {
      toCamera();
    } else if("gallery".equals(type)) {
      toPicture();
    } else {
      return;
    }
  }

  //跳转相册
  private void toPicture() {
    Intent intent = new Intent(Intent.ACTION_PICK);  //跳转到 ACTION_IMAGE_CAPTURE
    intent.setType("image/*");
    startActivityForResult(intent, TAKE_CAMARA);
    Log.i(TAG, "跳转相册成功");
  }

  //跳转相机
  private void toCamera() {
    //将button的点击事件改成startActivityForResult启动相机
    Intent camera = new Intent();
    camera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
    startActivityForResult(camera, TAKE_PHOTO);
  }

  public void dataOpt(int type) {
    if(type == 1) {
      List<Config> configList = getDaoConfig().queryBuilder().build().list();
      if(configList == null || configList.isEmpty()) {
        Log.d(TAG, "dataOpt: configList is null or empty");
      }

      for ( Config config: configList ) {
        Log.d(TAG,
            "dataOpt, config id:" + config.getId() + ", configKey:" + config.getConfigKey() + ", "
                + "configValue:" + config.getConfigValue());
      }
    } else {
//      DaoUtilsStore.getInstance().getConfigDaoUtils().deleteAll();
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
}