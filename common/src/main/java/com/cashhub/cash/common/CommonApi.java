package com.cashhub.cash.common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.auth.OSSAuthCredentialsProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.utils.OSSUtils;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.cashhub.cash.common.utils.DeviceUtils;
import com.cashhub.cash.common.utils.ImageUtils;
import com.cashhub.cash.common.utils.Md5Util;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.greenrobot.eventbus.EventBus;

public class CommonApi {

  private static final String TAG = "CommonApi";

  private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

  private static CommonApi mInstance;

  public static synchronized CommonApi getInstance() {
    if (mInstance == null) {
      mInstance = new CommonApi();
    }
    return mInstance;
  }

  /**
   * 获取验证码
   */
  public void getCheckCode(Context context, String phone) {
    CommonResult comRet = new CommonResult();
    if(phone == null || phone.isEmpty()) {
      Log.d(TAG, "phone is Null or empty!!! ");
      comRet.setCode(-1);
      comRet.setMsg("参数错误");
      return;
    }
    Log.d(TAG, "phone num is:" + phone);
    String url = Host.getApiHost(context) + "/api/v1/util/verify-code";
    JSONObject requestJson = new JSONObject();
    requestJson.put("phone", phone);
    requestJson.put("type", 1);
    requestJson.put("sign", Md5Util.md5Hex(phone + "&1"));
    RequestBody requestBody = FormBody.create(requestJson.toString(), MediaType.parse("application/json"));

    Request request = new Request.Builder()
        .addHeader("Content-Type", "application/json")
        .url(url)
        .post(requestBody)
        .build();

    Log.d(TAG, "getCheckCode url: " + url);

    HttpsUtils.sendRequest(phone, url, request, KndcEvent.GET_CHECK_CODE, null);
  }

  /**
   * 登入
   */
  public void userLogin(Context context, String phone, String verifyCode) {
    CommonResult comRet = new CommonResult();
    if(phone == null || phone.isEmpty()) {
      Log.d(TAG, "phone is Null or empty!!! ");
      comRet.setCode(-1);
      comRet.setMsg("参数错误");
      return;
    }

    String url = Host.getApiHost(context) + "/api/v1/user/sms-login";
    JSONObject requestJson = new JSONObject();
    requestJson.put("phone", phone);
    requestJson.put("verify_code", verifyCode);
    requestJson.put("device_key", DeviceUtils.getDeviceId(context));
    requestJson.put("channel_code", "");
    RequestBody requestBody = FormBody.create(requestJson.toString(), MediaType.parse("application/json"));

    Request request = new Request.Builder()
        .addHeader("Content-Type", "application/json")
        .url(url)
        .post(requestBody)
        .build();

    Log.d(TAG, "userLogin url: " + url);

    HttpsUtils.sendRequest(phone, url, request, KndcEvent.LOGIN, null);
  }

  /**
   * 登出
   * @param context
   * @param token
   */
  public void userLogout(Context context, String token) {
    String url = Host.getApiHost(context) + "/api/v1/user/logout";

    HashMap<String, String> headParams = new HashMap<>();
    headParams.put("Content-Type", "application/json");
    headParams.put("Authorization", token);

    Headers headers = setHeaderParams(headParams);
    Request request = new Request.Builder()
        .url(url)
        .get()
        .headers(headers)
        .build();

    Log.d(TAG, "userLogout token: " + token);
    Log.d(TAG, "userLogout url: " + url);

    HttpsUtils.sendRequest("", url, request, KndcEvent.LOGOUT, null);
  }

  /**
   * 清理登录信息
   */
  public void clearLoginInfo(Context context) {
    CommonApi commonApi = new CommonApi();
    commonApi.userLogout(context, KndcStorage.getInstance().getData(KndcStorage.USER_TOKEN));
  }

  /**
   * 埋点
   * @param context
   * @param requestJson
   */
  public void trackData(Context context, String requestJson) {
    if(requestJson == null) {
      return;
    }
    String url = Host.getApiHost(context) + "/api/v1/track/data";
    RequestBody requestBody = FormBody.create(requestJson, MediaType.parse("application/json"));

    Request request = new Request.Builder()
        .addHeader("Content-Type", "application/json")
        .addHeader("Access-Control-Allow-Origin", "*")
        .url(url)
        .post(requestBody)
        .build();

    Log.d(TAG, "postOssSign device url: " + url);

    HttpsUtils.sendRequest("", url, request, "", null);
  }

  /**
   * 获取上传图片的URL
   */
  public void getPolicySign(Context context, String token, String lineType, Bitmap bitmap) {
    if(TextUtils.isEmpty(lineType)) {
      Log.d(TAG, "lineType is Null or empty!!! ");
      return;
    }
    Log.d(TAG, "lineType is:" + lineType);
    String url = Host.getApiHost(context) + "/api/v1/oss/policy-sign";
    JSONObject requestJson = new JSONObject();
    requestJson.put("device_key", DeviceUtils.getDeviceId(context));
    requestJson.put("upload_dir", lineType);
    RequestBody requestBody = FormBody.create(requestJson.toString(), MediaType.parse("application/json"));

    Request request = new Request.Builder()
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", token)
        .addHeader("Access-Control-Allow-Origin", "*")
        .url(url)
        .post(requestBody)
        .build();

    Log.d(TAG, "getPolicySign url: " + url);

    HttpsUtils.sendRequest("", url, request, KndcEvent.GET_POLICY_SIGN, bitmap);
  }

  /**
   * 上传图片
   */
  public void uploadImage(Context context, Map<String, String> policySignRet, Bitmap bitmap) {
    Log.d(TAG, "policySignRet: " + policySignRet.toString());

    if(policySignRet == null || bitmap == null) {
      return;
    }
    String dir = policySignRet.get("dir");
    String url = policySignRet.get("host");
    String policy = policySignRet.get("policy");
    String accessId = policySignRet.get("access_id");
    String signature = policySignRet.get("signature");

    Log.d(TAG, "dir:" + dir);
    Log.d(TAG, "url:" + url);
    Log.d(TAG, "policy:" + policy);
    Log.d(TAG, "accessId:" + accessId);
    Log.d(TAG, "signature:" + signature);

    if(TextUtils.isEmpty(url) || TextUtils.isEmpty(dir)) {
      return;
    }
    try {
      //图片文件名称
      String fileName = "living_ocr_" + System.currentTimeMillis() + ".jpg";
      String filePath = "";

      //处理文件
      bitmap = ImageUtils.compressImage(bitmap, 800, true);
//      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

      if (Build.BRAND.equals("Xiaomi")) { // 小米手机
        filePath = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/";
      } else { // Meizu 、Oppo
        filePath = Environment.getExternalStorageDirectory().getPath() + "/DCIM/";
      }
      File file = new File(filePath + fileName);
      try {
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
      // form 表单形式上传
      MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);


      requestBody.addFormDataPart("key", dir + fileName);
      requestBody.addFormDataPart("policy", policy);
      requestBody.addFormDataPart("OSSAccessKeyId", accessId);
      requestBody.addFormDataPart("Signature", signature);
      requestBody.addFormDataPart("success_action_status", "200");
      //根据文件的后缀名，获得文件类型;
      requestBody.addFormDataPart(//给Builder添加上传的文件
          "file",//请求的名字
          fileName, //文件的文字，服务器端用来解析的
          RequestBody.create(MediaType.parse("image/png"), file));//创建RequestBody，把上传的文件放入


      Request request = new Request.Builder()
          .url(url)
          .post(requestBody.build()).tag(context)
          .build();

      //发起请求
      new Thread(new Runnable() {
        @Override
        public void run() {
          String uploadImageUrl = "";
          try {
            Response response = null;
            String bodyStr = "";
            HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory();
            OkHttpClient.Builder  builder=new  OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager)//添加信任证书
                .hostnameVerifier((hostname, session) -> true) //忽略host验证
                .followRedirects(false);  //禁制OkHttp的重定向操作，我们自己处理重定向

            OkHttpClient client = builder.build();
            response = client.newCall(request).execute();

            if(response.code() == 200) {
              uploadImageUrl = url + "/" + dir + fileName;
              bodyStr = response.body().string();
              Log.d(TAG, "sendNetRequest uploadImageUrl: " + uploadImageUrl);
            }
            response.body().close();
          } catch (Exception e) {
            Log.d(TAG, "sendNetRequest onFailure " + e.getMessage());
          }
          KndcEvent kndcEvent = new KndcEvent();
          kndcEvent.setEventName(KndcEvent.UPLOAD_IMAGE_SUCCESS);
          kndcEvent.setUrl(uploadImageUrl);
//          if(TextUtils.isEmpty(uploadImageUrl)) {
//            //上传失败
//          } else {
//            //上传成功
//          }
          EventBus.getDefault().post(kndcEvent);
        }
      }).start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Ocr/活体 上传上报
   */
  public void reportUploadSuccessData(Context context, String token, String imgUrl, String type) {
    Log.d(TAG, "reportUploadSuccessData token: " + token + ", imgUrl:" + imgUrl + ", type:" + type);

    if(TextUtils.isEmpty(token) || TextUtils.isEmpty(imgUrl) || TextUtils.isEmpty(type)) {
      return;
    }
    String url = "";
    String paramName = "";
    if("ocr".equals(type)) {
      paramName = "ocr_url";
      url = Host.getApiHost(context) + "/api/v1/ocr/image-info";
    } else {
      paramName = "face_url";
      url = Host.getApiHost(context) + "/api/v1/identity/face";
    }
    JSONObject requestJson = new JSONObject();
    requestJson.put(paramName, imgUrl);
    RequestBody requestBody = FormBody.create(requestJson.toString(), MediaType.parse("application/json"));

    Request request = new Request.Builder()
        .addHeader("Content-Type", "application/json")
        .addHeader("Access-Control-Allow-Origin", "*")
        .addHeader("Authorization", token)
        .url(url)
        .post(requestBody)
        .build();

    Log.d(TAG, "trackFaceData device url: " + url);

    HttpsUtils.sendRequest("", url, request, KndcEvent.UPLOAD_END_CALL_JS, null);
  }

  private String formUpload(String urlStr, Map<String, String> formFields, Bitmap bitmap)
      throws Exception {
    String res = "";
    HttpURLConnection conn = null;
    String boundary = "9431149156168";
    try {
      URL url = new URL(urlStr);
      conn = (HttpURLConnection) url.openConnection();
      conn.setConnectTimeout(5000);
      conn.setReadTimeout(30000);
      conn.setDoOutput(true);
      conn.setDoInput(true);
      conn.setRequestMethod("POST");
//      conn.setRequestProperty("User-Agent",
//          "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
//      // 设置MD5值。MD5值由整个Body计算得出。如果希望开启MD5校验，可参考MD5加密设置。
//      // conn.setRequestProperty("Content-MD5", contentMD5);
//      conn.setRequestProperty("Content-Type",
//          "multipart/form-data; boundary=" + boundary);
      OutputStream out = new DataOutputStream(conn.getOutputStream());
      // 遍历读取表单Map中的数据，将数据写入到输出流中。
      if (formFields != null) {
        StringBuffer strBuf = new StringBuffer();
        Iterator<Entry<String, String>> iter = formFields.entrySet().iterator();
        int i = 0;
        while (iter.hasNext()) {
          Map.Entry<String, String> entry = iter.next();
          String inputName = entry.getKey();
          String inputValue = entry.getValue();
          if (inputValue == null) {
            continue;
          }
          if (i == 0) {
            strBuf.append("--").append(boundary).append("\r\n");
            strBuf.append("Content-Disposition: form-data; name=\""
                + inputName + "\"\r\n\r\n");
            strBuf.append(inputValue);
          } else {
            strBuf.append("\r\n").append("--").append(boundary).append("\r\n");
            strBuf.append("Content-Disposition: form-data; name=\""
                + inputName + "\"\r\n\r\n");
            strBuf.append(inputValue);
          }
          i++;
        }
        out.write(strBuf.toString().getBytes());
      }
      // 读取文件信息，将要上传的文件写入到输出流中。
      //    第一步:将Bitmap压缩至字节数组输出流ByteArrayOutputStream
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//    bitmap = ImageUtils.compressImage(bitmap, 800, true);
    bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
    //第二步:利用Base64将字节数组输出流中的数据转换成字符串String
    byte[] uploadData = byteArrayOutputStream.toByteArray();

    String fileName = "living_ocr_" + System.currentTimeMillis() + ".png";
    String contentType = "application/image*";

    StringBuffer strBuf = new StringBuffer();
    strBuf.append("\r\n").append("--").append(boundary)
        .append("\r\n");
    strBuf.append("Content-Disposition: form-data; name=\"file\"; "
        + "filename=\"" + fileName + "\"\r\n");
    strBuf.append("Content-Type: " + contentType + "\r\n\r\n");
    out.write(strBuf.toString().getBytes());
//      DataInputStream in = new DataInputStream(new FileInputStream(file));
//      int bytes = 0;
//      byte[] bufferOut = new byte[1024];
//      while ((bytes = in.read(bufferOut)) != -1) {
//        out.write(bufferOut, 0, bytes);
//      }
//      in.close();
      out.write(uploadData, 0, uploadData.length);
      byte[] endData = ("\r\n--" + boundary + "--\r\n").getBytes();
      out.write(endData);
      out.flush();
      out.close();
      // 读取返回数据。
      strBuf = new StringBuffer();
      BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String line = null;
      while ((line = reader.readLine()) != null) {
        strBuf.append(line).append("\n");
      }
      res = strBuf.toString();
      reader.close();
      reader = null;
    } catch (Exception e) {
//            System.err.println("Send post request exception: " + e);
      Log.d(TAG, "formUpload exception: " + e.getMessage());
      e.printStackTrace();
    } finally {
      if (conn != null) {
        conn.disconnect();
        conn = null;
      }
    }
    return res;
  }
//  private static void setCallBack(Map<String, String> formFields, Callback callback) {
//    if (callback != null) {
//      String jsonCb = OSSUtils.jsonizeCallback(callback);
//      String base64Cb = BinaryUtil.toBase64String(jsonCb.getBytes());
//      formFields.put("callback", base64Cb);
//      if (callback.hasCallbackVar()) {
//        Map<String, String> varMap = callback.getCallbackVar();
//        for (Map.Entry<String, String> entry : varMap.entrySet()) {
//          formFields.put(entry.getKey(), entry.getValue());
//        }
//      }
//    }
//  }

  //添加参数
  private String getBodyParams(Map<String, String> bodyParams) {
    //1.添加请求参数
    //遍历map中所有参数到builder
    if (bodyParams != null && bodyParams.size() > 0) {
      StringBuffer stringBuffer = new StringBuffer("?");
      for (String key : bodyParams.keySet()) {
        if (bodyParams.get(key) != null) {//如果参数不是null，就拼接起来
          stringBuffer.append("&");
          stringBuffer.append(key);
          stringBuffer.append("=");
          stringBuffer.append(bodyParams.get(key));
        }
      }

      return stringBuffer.toString();
    } else {
      return "";
    }
  }

  //添加headers
  private Headers setHeaderParams(Map<String, String> headerParams) {
    Headers headers = null;
    Headers.Builder headliners = new Headers.Builder();
    if (headerParams != null && headerParams.size() > 0) {
      for (String key : headerParams.keySet()) {
        if (headerParams.get(key) != null) {//如果参数不是null，就拼接起来
          headliners.add(key, headerParams.get(key));
        }
      }
    }

    headers = headliners.build();
    return headers;
  }
}
