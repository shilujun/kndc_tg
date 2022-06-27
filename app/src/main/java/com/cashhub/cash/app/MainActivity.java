package com.cashhub.cash.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import com.blankj.utilcode.util.ImageUtils;
import com.cashhub.cash.app.db.ConfigDaoStore;
import com.cashhub.cash.app.db.DaoUtilsStore;
import com.cashhub.cash.app.model.Config;
import com.cashhub.cash.common.Host;
import com.cashhub.cash.common.ImageUpload;
import com.cashhub.cash.common.utils.CommonUtil;
import com.cashhub.cash.common.utils.DeviceUtils;
import com.cashhub.cash.common.utils.StatusBarUtils;
import com.tencent.sonic.sdk.SonicConfig;
import com.tencent.sonic.sdk.SonicEngine;
import com.tencent.sonic.sdk.SonicSessionConfig;
import android.os.Bundle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends BaseActivity {

  private String TAG = "MainActivity";

  public static final int MODE_DEFAULT = 0;

  public static final int MODE_SONIC = 1;

  public static final int MODE_SONIC_WITH_OFFLINE_CACHE = 2;

  private static final int PERMISSION_REQUEST_CODE_STORAGE = 1;

  private String DEMO_URL;

  public static final int TAKE_PHOTO = 101;
  public static final int TAKE_CAMARA = 100;
  private Uri mImageUri;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // full screen
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);

    setContentView(R.layout.activity_main);

    Log.d(TAG, "onCreate, DeviceId:" + DeviceUtils.getDeviceId(this));
    Log.d(TAG, "onCreate, StatusBarHeight:" + StatusBarUtils.getStatusBarHeight(this));
    Log.d(TAG, "onCreate, Host isDebug:" + Host.getApiHost(this));

    // clean up cache btn
    Button btnReset = (Button) findViewById(R.id.btn_reset);
    btnReset.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        SonicEngine.getInstance().cleanCache();
      }
    });

    // default btn
    Button btnDefault = (Button) findViewById(R.id.btn_default_mode);
    btnDefault.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startBrowserActivity(MODE_DEFAULT);
      }
    });

    //camera btn
    Button btnCamera = (Button) findViewById(R.id.btn_camera);
    btnCamera.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        openCameraOrGallery("camera");
      }
    });

    //gallery btn
    Button btnGallery = (Button) findViewById(R.id.btn_gallery);
    btnGallery.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        openCameraOrGallery("gallery");
      }
    });

    // preload btn
    Button btnSonicPreload = (Button) findViewById(R.id.btn_sonic_preload);
    btnSonicPreload.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        SonicSessionConfig.Builder sessionConfigBuilder = new SonicSessionConfig.Builder();
        sessionConfigBuilder.setSupportLocalServer(true);

        // preload session
        boolean preloadSuccess = SonicEngine.getInstance().preCreateSession(DEMO_URL, sessionConfigBuilder.build());
        Toast.makeText(getApplicationContext(), preloadSuccess ? "Preload start up success!" : "Preload start up fail!", Toast.LENGTH_LONG).show();
      }
    });
    // sonic mode load btn
    Button btnLocal = (Button) findViewById(R.id.btn_local);
    btnLocal.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        DEMO_URL = "http://johnnyshi.com/test.html";
        startBrowserActivity(MODE_SONIC);
      }
    });

    Button btnInsert = (Button) findViewById(R.id.btn_insert);
    btnInsert.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        dataOpt(0);
      }
    });

    Button btnQuery = (Button) findViewById(R.id.btn_query);
    btnQuery.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        dataOpt(1);
      }
    });

    // sonic mode load btn
    Button btnSonic = (Button) findViewById(R.id.btn_sonic);
    btnSonic.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startBrowserActivity(MODE_SONIC);
      }
    });

    // load sonic with offline cache
    Button btnSonicWithOfflineCache = (Button) findViewById(R.id.btn_sonic_with_offline);
    btnSonicWithOfflineCache.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startBrowserActivity(MODE_SONIC_WITH_OFFLINE_CACHE);
      }
    });

    if (hasPermission()) {
      init();
    } else {
      requestPermission();
    }

    final UrlListAdapter urlListAdapter = new UrlListAdapter(MainActivity.this);

    Button btnFab = (Button) findViewById(R.id.btn_fab);
    btnFab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        UrlSelector.launch(MainActivity.this, urlListAdapter, new UrlSelector.OnUrlChangedListener() {
          @Override
          public void urlChanged(String url) {
            DEMO_URL = url;
          }
        });
      }
    });

    DEMO_URL = urlListAdapter.getCheckedUrl();
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
            imageUpload.saveImageToGallery(MainActivity.this, bitmap);
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
            imageUpload.saveImageToGallery(MainActivity.this, bitmap);
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          }
        }
        break;
      default:
        break;
    }
  }

  private void startBrowserActivity(int mode) {
    Intent intent = new Intent(this, BrowserActivity.class);
    intent.putExtra(BrowserActivity.PARAM_URL, DEMO_URL);
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