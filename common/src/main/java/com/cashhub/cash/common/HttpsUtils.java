package com.cashhub.cash.common;

import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.greenrobot.eventbus.EventBus;

public class HttpsUtils {

    private static final String TAG = "HttpsUtils";
    public static class SSLParams {
        public SSLSocketFactory sSLSocketFactory;
        public X509TrustManager trustManager;
    }

    public static SSLParams getSslSocketFactory() {
        return getSslSocketFactory(null, null, null);
    }

    public static SSLParams getSslSocketFactory(InputStream[] certificates, InputStream bksFile, String password) {
        SSLParams sslParams = new SSLParams();
        try {
            TrustManager[] trustManagers = prepareTrustManager(certificates);
            KeyManager[] keyManagers = prepareKeyManager(bksFile, password);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            X509TrustManager trustManager;
            if (trustManagers != null) {
                trustManager = new MyTrustManager(chooseTrustManager(trustManagers));
            } else {
                trustManager = new UnSafeTrustManager();
            }
            sslContext.init(keyManagers, new TrustManager[]{trustManager}, null);
            sslParams.sSLSocketFactory = sslContext.getSocketFactory();
            sslParams.trustManager = trustManager;
            return sslParams;
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            throw new AssertionError(e);
        }
    }

    private static class UnSafeTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    private static TrustManager[] prepareTrustManager(InputStream... certificates) {
        if (certificates == null || certificates.length <= 0) return null;
        try {

            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                try {
                    if (certificate != null)
                        certificate.close();
                } catch (IOException e) {
                }
            }
            TrustManagerFactory trustManagerFactory = null;

            trustManagerFactory = TrustManagerFactory.
                getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            return trustManagerFactory.getTrustManagers();
        } catch (NoSuchAlgorithmException | CertificateException | KeyStoreException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private static KeyManager[] prepareKeyManager(InputStream bksFile, String password) {
        try {
            if (bksFile == null || password == null) return null;

            KeyStore clientKeyStore = KeyStore.getInstance("BKS");
            clientKeyStore.load(bksFile, password.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, password.toCharArray());
            return keyManagerFactory.getKeyManagers();

        } catch (KeyStoreException | UnrecoverableKeyException | NoSuchAlgorithmException | CertificateException | IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static X509TrustManager chooseTrustManager(TrustManager[] trustManagers) {
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        return null;
    }


    private static class MyTrustManager implements X509TrustManager {
        private X509TrustManager defaultTrustManager;
        private X509TrustManager localTrustManager;

        public MyTrustManager(X509TrustManager localTrustManager) throws NoSuchAlgorithmException, KeyStoreException {
            TrustManagerFactory var4 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            var4.init((KeyStore) null);
            defaultTrustManager = chooseTrustManager(var4.getTrustManagers());
            this.localTrustManager = localTrustManager;
        }


        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                defaultTrustManager.checkServerTrusted(chain, authType);
            } catch (CertificateException ce) {
                localTrustManager.checkServerTrusted(chain, authType);
            }
        }


        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    public static void sendRequest(final String phone, final String url, final Request request,
        final String type) {
        //发起请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sendNetRequest(phone, url, request, type);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void sendNetRequest(final String phone, final String url,
        final Request request, String type) {
        Response response = null;
        String bodyStr = "";

        Pattern p = Pattern.compile("((https)://)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(url);
        boolean isMatcher = matcher.find();
        if (isMatcher) {
            Log.d(TAG, "sendNetRequest https");
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
                    Log.d(TAG, "sendNetRequest response is Null or code not 200" + response.code() + " " + response.message());
                    response.body().close();
                    return;
                }
                bodyStr = response.body().string();
                Log.d(TAG, "sendNetRequest bodyStr: " + bodyStr);
            } catch (Exception e) {
                Log.d(TAG, "sendNetRequest onFailure " + e.getMessage());
                return;
            }

        } else {
            Log.d(TAG, "sendNetRequest device http");

            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                response = okHttpClient.newCall(request).execute();

                if(response == null || response.code() != 200) {
                    Log.d(TAG, "sendNetRequest response is Null or code not 200");
                    response.body().close();
                    return;
                }
                bodyStr = response.body().string();

                Log.d(TAG, "sendNetRequest bodyStr: " + bodyStr);
            } catch (Exception e) {
                Log.d(TAG, "sendNetRequest onFailure " + e.getMessage());
            }
        }

        if (TextUtils.isEmpty(type)) {
            return;
        }
        if(KndcEvent.LOGIN.equals(type) || KndcEvent.LOGOUT.equals(type)) {
            KndcEvent kndcEvent = new KndcEvent();
            kndcEvent.setEventName(type);
            kndcEvent.setPhone(phone);
            kndcEvent.setCommonRet(bodyStr);
            EventBus.getDefault().post(kndcEvent);
        }
    }
}