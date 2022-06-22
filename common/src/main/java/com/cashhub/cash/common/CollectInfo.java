//package com.cashhub.cash.common;
//
//import android.content.Context;
//import android.os.Build;
//import android.util.Log;
//import androidx.annotation.RequiresApi;
//import com.alibaba.fastjson.JSONObject;
//
//public class CollectInfo {
//
//    private static final String TAG = "CollectInfo";
//    private com.cashhub.cash.common.UploadData mUploadData;
//
//    /**
//     * 手机上报设备信息
//     * @param systemInfo
//     * @param token
//     * @param domain
//     * @param timeStamp
//     * @param deviceKey
//     */
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    public void getAndSendDevice(JSONObject systemInfo, String token, String domain,
//        long timeStamp, String deviceKey) {
//        new Thread(() -> {
//            try {
//                //初始化数据
//                if (this.mUploadData == null) {
//                    Context context = mUniSDKInstance.getContext();
//                    this.mUploadData = new com.cashhub.cash.common.UploadData(context);
//                }
//
//                this.mUploadData.getAndSendDevice(systemInfo, token, domain, timeStamp, deviceKey);
//            } catch (Exception e) {
//                Log.d(TAG, e.getMessage());
//            }
//        }).start();//启动线程
//    }
//
//    /**
//     * 手机上报通讯录
//     * @param systemInfo
//     * @param token
//     * @param domain
//     * @param timeStamp
//     * @param deviceKey
//     */
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    public void getAndSendContact(JSONObject systemInfo, String token, String domain,
//        long timeStamp, String deviceKey) {
//        new Thread(() -> {
//            try {
//                //初始化数据
//                if (this.mUploadData == null) {
//                    Context context = mUniSDKInstance.getContext();
//                    this.mUploadData = new com.cashhub.cash.common.UploadData(context);
//                }
//
//                this.mUploadData.getAndSendContact(systemInfo, token, domain, timeStamp, deviceKey);
//            } catch (Exception e) {
//                Log.d(TAG, e.getMessage());
//            }
//        }).start();//启动线程
//    }
//
//    /**
//     * 手机上报短信信息
//     * @param systemInfo
//     * @param token
//     * @param domain
//     * @param timeStamp
//     * @param deviceKey
//     */
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    public void getAndSendSms(JSONObject systemInfo, String token, String domain,
//        long timeStamp, String deviceKey) {
//        new Thread(() -> {
//            try {
//                //初始化数据
//                if (this.mUploadData == null) {
//                    Context context = mUniSDKInstance.getContext();
//                    this.mUploadData = new com.cashhub.cash.common.UploadData(context);
//                }
//
//                this.mUploadData.getAndSendSms(systemInfo, token, domain, timeStamp, deviceKey);
//            } catch (Exception e) {
//                Log.d(TAG, e.getMessage());
//            }
//        }).start();//启动线程
//    }
//
//    /**
//     * 手机上报日历
//     * @param systemInfo
//     * @param token
//     * @param domain
//     * @param timeStamp
//     * @param deviceKey
//     */
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    public void getAndSendCalendar(JSONObject systemInfo, String token, String domain,
//        long timeStamp, String deviceKey) {
//        new Thread(() -> {
//            try {
//                //初始化数据
//                if (this.mUploadData == null) {
//                    Context context = mUniSDKInstance.getContext();
//                    this.mUploadData = new com.cashhub.cash.common.UploadData(context);
//                }
//
//                this.mUploadData.getAndSendCalendar(systemInfo, token, domain, timeStamp, deviceKey);
//            } catch (Exception e) {
//                Log.d(TAG, e.getMessage());
//            }
//        }).start();//启动线程
//    }
//
//    /**
//     * 手机上报定位
//     * @param systemInfo
//     * @param token
//     * @param domain
//     * @param timeStamp
//     * @param deviceKey
//     */
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    public void getAndSendMap(JSONObject systemInfo, String token, String domain,
//        long timeStamp, String deviceKey) {
//        new Thread(() -> {
//            try {
//                //初始化数据
//                if (this.mUploadData == null) {
//                    Context context = mUniSDKInstance.getContext();
//                    this.mUploadData = new com.cashhub.cash.common.UploadData(context);
//                }
//
//                this.mUploadData.getAndSendLocation(systemInfo, token, domain, timeStamp, deviceKey);
//            } catch (Exception e) {
//                Log.d(TAG, e.getMessage());
//            }
//        }).start();//启动线程
//    }
//
//    /**
//     * 手机上报定位
//     * @param systemInfo
//     * @param token
//     * @param domain
//     * @param timeStamp
//     * @param deviceKey
//     */
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    public void getAndSendLocation(JSONObject systemInfo, String token, String domain,
//        long timeStamp, String deviceKey) {
//        new Thread(() -> {
//            try {
//                //初始化数据
//                if (this.mUploadData == null) {
//                    Context context = mUniSDKInstance.getContext();
//                    this.mUploadData = new com.cashhub.cash.common.UploadData(context);
//                }
//
//                this.mUploadData.getAndSendLocation2(systemInfo, token, domain, timeStamp,
//                    deviceKey);
//            } catch (Exception e) {
//                Log.d(TAG, e.getMessage());
//            }
//        }).start();//启动线程
//    }
//}