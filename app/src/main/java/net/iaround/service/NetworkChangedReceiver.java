package net.iaround.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;
import net.iaround.model.entity.InitBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.OkhttpDownload;
import net.iaround.tools.OkhttpDownloadStatus;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：zx on 2017/7/20 14:25
 */
public class NetworkChangedReceiver extends BroadcastReceiver {
    private final static String TAG = NetworkChangedReceiver.class.getSimpleName();
    public final static String CUSTOM_ANDROID_NET_CHANGE_ACTION = "com.eeepay.cn.api.netstatus.CONNECTIVITY_CHANGE";
    private final static String ANDROID_NET_CHANGE_ACTION = ConnectivityManager.CONNECTIVITY_ACTION;//"android.net.conn.CONNECTIVITY_CHANGE";

    private static boolean isNetAvailable = false;
    private static CommonFunction.NetType mNetType;
    private static ArrayList<NetChangeObserver> mNetChangeObservers = new ArrayList<NetChangeObserver>();
    private static BroadcastReceiver mBroadcastReceiver;

    /**
     * 获取 BroadcastReceiver 实例
     * @return
     */
    private static BroadcastReceiver getReceiverInstance() {
        if (null == mBroadcastReceiver) {
            synchronized (NetworkChangedReceiver.class) {
                if (null == mBroadcastReceiver) {
                    mBroadcastReceiver = new NetworkChangedReceiver();
                }
            }
        }
        return mBroadcastReceiver;
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        //通讯服务连接
        //handleSTNNetwork(context, intent);

        //WIFI环境下下载礼物
//        handleGitDownload(context, intent);

        //添加网络监听的变化
        netWorkChangeListenering(context,intent);
    }

    /*网络发送变化时，处理下载礼物
     * */
    private void handleGitDownload(Context context, Intent intent) {
        Log.e(TAG, "handleGitDownload() 网络状态改变1");

        //1.判断是否切换到wifi，否退出
        if (!WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            return;
        }
        int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
        //2.判断wifi是否连接，否退出
        Log.e(TAG, "handleGitDownload() 网络状态改变2");
        if (wifiState != WifiManager.WIFI_STATE_ENABLED) {
            return;
        }
        Log.e(TAG, "handleGitDownload() 网络状态改变3");
        //3.判断当前wifi是否是有效的连接，否退出
        if (!CommonFunction.isNetworkConnected(context.getApplicationContext())) {
            return;
        }
        Log.e(TAG, "handleGitDownload() 网络状态改变4");
        //4.下载礼物包
        OkhttpDownload downloadManager = new OkhttpDownload(context.getApplicationContext());
        List<InitBean.GiftPackage> failureGifts = OkhttpDownloadStatus.getDownloadFailureGift(context.getApplicationContext());
        if (null != failureGifts && failureGifts.size() > 0) {
            for (InitBean.GiftPackage giftPackage : failureGifts) {
                downloadManager.downLoad(giftPackage.downloadUrl, giftPackage.animationId);
            }
        }
    }


    private void handleSTNNetwork(Context context, Intent intent) {
        //判断当前是否是有效的连接，否退出
        if (!CommonFunction.isNetworkConnected(context.getApplicationContext())) {
            Log.e(TAG, "handleSTNNetwork() 网络状态无连接");
            return;
        } else {
            Log.e(TAG, "handleSTNNetwork() 网络状态已连接");
            return;
        }
    }

    private void netWorkChangeListenering(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(ANDROID_NET_CHANGE_ACTION) || intent.getAction().equalsIgnoreCase(CUSTOM_ANDROID_NET_CHANGE_ACTION)) {
            if (mNetType==CommonFunction.getAPNType(context)) return;// 如果回调回来跟上次的一样；直接就return了； 避免有些情况回调2次网络监听
            boolean networkAvailable = CommonFunction.isNetworkConnected(context);
            Log.e(TAG,"networkAvailable="+networkAvailable);
            if (!networkAvailable) {
                Log.e(TAG, "<--- network disconnected --->");
                isNetAvailable = networkAvailable;
                mNetType = CommonFunction.getAPNType(context);
            } else {
                Log.e(TAG, "<--- network connected --->");
                isNetAvailable = networkAvailable;
                mNetType = CommonFunction.getAPNType(context);
                //判断wifi是否连接并且网络有效  WIFI环境下下载礼物
                if(mNetType == CommonFunction.NetType.WIFI){
                    //下载礼物包
                    OkhttpDownload downloadManager = new OkhttpDownload(context.getApplicationContext());
                    List<InitBean.GiftPackage> failureGifts = OkhttpDownloadStatus.getDownloadFailureGift(context.getApplicationContext());
                    if (null != failureGifts && failureGifts.size() > 0) {
                        for (InitBean.GiftPackage giftPackage : failureGifts) {
                            downloadManager.downLoad(giftPackage.downloadUrl, giftPackage.animationId);
                        }
                    }
                }
            }
            notifyObserver();//发出通知
        }
    }

    private void notifyObserver() {
        if (!mNetChangeObservers.isEmpty()) {
            int size = mNetChangeObservers.size();
            for (int i = 0; i < size; i++) {
                NetChangeObserver observer = mNetChangeObservers.get(i);
                if (observer != null) {
                    if (isNetworkAvailable()) {
                        observer.onNetConnected(mNetType);
                    } else {
                        observer.onNetDisConnect();
                    }
                }
            }
        }
    }

    public static boolean isNetworkAvailable() {
        return isNetAvailable;
    }

    public static CommonFunction.NetType getAPNType() {
        return mNetType;
    }

    /**
     * 添加网络监听
     *
     * @param observer
     */
    public static void addObserver(NetChangeObserver observer) {
        if (mNetChangeObservers == null) {
            mNetChangeObservers = new ArrayList<NetChangeObserver>();
        }
        if(mNetChangeObservers.contains(observer)){
            mNetChangeObservers.remove(observer);
        }
        mNetChangeObservers.add(observer);
    }

    /**
     * 移除网络监听
     *
     * @param observer
     */
    public static void removeRegisterObserver(NetChangeObserver observer) {
        if (mNetChangeObservers != null) {
            if (mNetChangeObservers.contains(observer)) {
                mNetChangeObservers.remove(observer);
            }
        }
    }

    /**
     * 注册
     *
     * @param context
     */
    public static void registerNetworkStateReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CUSTOM_ANDROID_NET_CHANGE_ACTION);
        filter.addAction(ANDROID_NET_CHANGE_ACTION);
        context.getApplicationContext().registerReceiver(getReceiverInstance(), filter);
    }
    /**
     * 反注册
     *
     * @param context
     */
    public static void unRegisterNetworkStateReceiver(Context context) {
        if (mBroadcastReceiver != null) {
            try {
                context.getApplicationContext().unregisterReceiver(mBroadcastReceiver);
            } catch (Exception e) {

            }
        }
    }

}
