package com.cashhub.cash.common;

import android.content.Context;
import android.util.Log;
import com.alibaba.fastjson.JSONObject;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommonApi {

  private static final String TAG = "CommonApi";

  /**
   * 登入
   * @param context
   * @param phone
   */
  public void userLogin(Context context, String phone) {
    if(phone == null || phone.isEmpty()) {
      Log.d(TAG, "phone is Null or empty!!! ");
      return;
    }
    String url = Host.getApiHost(context) + "/v1/util/verify-code";
    JSONObject requestJson = new JSONObject();
    requestJson.put("phone", phone);
    requestJson.put("type", 1);
    requestJson.put("sign", phone);
    RequestBody requestBody = FormBody.create(requestJson.toString(), MediaType.parse("application/json"));

    Request request = new Request.Builder()
        .addHeader("Content-Type", "application/json")
        .url(url)
        .post(requestBody)
        .build();

    Log.d(TAG, "userLogin url: " + url);

    Response response = null;
    String bodyStr = "";

    Pattern p = Pattern.compile("((https)://)", Pattern.CASE_INSENSITIVE);
    Matcher matcher = p.matcher(url);
    boolean isMatcher = matcher.find();
    if (isMatcher) {
      Log.d(TAG, "userLogin https");
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
          Log.d(TAG, "userLogin response is Null or code not 200" + response.code() + " " + response.message());
          response.body().close();
          return;
        }
        bodyStr = response.body().string();
      } catch (Exception e) {
        Log.d(TAG, "userLogin onFailure " + e.getMessage());
      }

    } else {
      Log.d(TAG, "userLogin device http");

      try {
        OkHttpClient okHttpClient = new OkHttpClient();
        response = okHttpClient.newCall(request).execute();

        if(response == null || response.code() != 200) {
          Log.d(TAG, "userLogin response is Null or code not 200");
          response.body().close();
          return;
        }
        bodyStr = response.body().string();

      } catch (Exception e) {
        Log.d(TAG, "userLogin onFailure " + e.getMessage());
      }

    }
  }

  /**
   * 登出
   * @param context
   * @param token
   */
  public void userLogout(Context context, String token) {
    String url = Host.getApiHost(context) + "/v1/user/logout";

    Request request = new Request.Builder()
        .addHeader("Authorization", token)
        .addHeader("Content-Type", "application/json")
        .url(url)
        .get()
        .build();

    Log.d(TAG, "userLogout url: " + url);

    Response response = null;
    String bodyStr = "";

    Pattern p = Pattern.compile("((https)://)", Pattern.CASE_INSENSITIVE);
    Matcher matcher = p.matcher(url);
    boolean isMatcher = matcher.find();
    if (isMatcher) {
      Log.d(TAG, "userLogout https");
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
          Log.d(TAG, "userLogout response is Null or code not 200" + response.code() + " " + response.message());
          response.body().close();
          return;
        }
        bodyStr = response.body().string();
      } catch (Exception e) {
        Log.d(TAG, "userLogout onFailure " + e.getMessage());
      }

    } else {
      Log.d(TAG, "userLogout device http");

      try {
        OkHttpClient okHttpClient = new OkHttpClient();
        response = okHttpClient.newCall(request).execute();

        if(response == null || response.code() != 200) {
          Log.d(TAG, "userLogout response is Null or code not 200");
          response.body().close();
          return;
        }
        bodyStr = response.body().string();

      } catch (Exception e) {
        Log.d(TAG, "userLogout onFailure " + e.getMessage());
      }
    }
  }

  /**
   * 埋点
   * @param context
   * @param requestJson
   * @param token
   */
  public void trackData(Context context, JSONObject requestJson, String token) {
    if(requestJson == null) {
      return;
    }
    String url = Host.getApiHost(context) + "/v1/track/data";
    RequestBody requestBody = FormBody.create(requestJson.toString(), MediaType.parse("application/json"));

    Request request = new Request.Builder()
        .addHeader("Authorization", token)
        .url(url)
        .post(requestBody)
        .build();

    Log.d(TAG, "postOssSign device url: " + url);

    Response response = null;
    String bodyStr = "";

    Pattern p = Pattern.compile("((https)://)", Pattern.CASE_INSENSITIVE);
    Matcher matcher = p.matcher(url);
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
  }
}
