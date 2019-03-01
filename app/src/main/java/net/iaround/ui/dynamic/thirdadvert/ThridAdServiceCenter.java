package net.iaround.ui.dynamic.thirdadvert;

import android.util.Log;

import com.qq.e.ads.cfg.BrowserType;
import com.qq.e.ads.cfg.DownAPPConfirmPolicy;
import com.qq.e.ads.nativ.NativeAD;
import com.qq.e.ads.nativ.NativeADDataRef;
import com.qq.e.comm.util.AdError;

import net.iaround.BaseApplication;
import net.iaround.conf.Config;
import net.iaround.tools.CommonFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 第三方广告服务中心，功能包括：
 *  1. 获取第三方的广告信息；
 *  2. 给应用提供单条广告信息
 *
 * Created by fireant on 16/8/4.
 */
public class ThridAdServiceCenter implements NativeAD.NativeAdListener {

    private final String TAG = "GDT_LOG";

    public static volatile ThridAdServiceCenter instance;

    public static ThridAdServiceCenter getInstance() {
        ThridAdServiceCenter localInstance = instance;
        if (localInstance == null) {
            synchronized (ThridAdServiceCenter.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new ThridAdServiceCenter();
                }
            }
        }

        return localInstance;
    }

    private NativeAD gdtNativeAD;

    private CopyOnWriteArrayList<NativeADDataRef> adDatas = new CopyOnWriteArrayList();
    private AtomicInteger adLastIndex;

    // 上次拉取广告成功的时间
    private long lastLoadDataSuccTime;

    // 每次拉取广告资源的时间间隔（单位为：分钟）
    private final int LOAD_AD_INTERVAL = 20;

    // 广告是否关闭
    public boolean isLoadClose = true ;

    public ThridAdServiceCenter() {
        gdtNativeAD = new NativeAD(BaseApplication.appContext, Config.GDT_APPID,
                Config.GDT_NATIVE_POST_ID, this);
        gdtNativeAD.setBrowserType(BrowserType.Sys);
        gdtNativeAD.setDownAPPConfirmPolicy(DownAPPConfirmPolicy.NOConfirm);
        adLastIndex = new AtomicInteger();
    }

    /**
     * 触发拉广点通广告资源
     *
     */
    public void loadAdDatas() {

        if (!checkIsCanLoadAdDatas()) {
            return;
        }

        int count = 4;

        if (gdtNativeAD != null) {
            gdtNativeAD.loadAD(count);
        }
    }

    /**
     * 检查是否可以出发拉取广告
     *
     * @return
     */
    private boolean checkIsCanLoadAdDatas() {
        if (!isHaveAdDatas()) {
            return true;
        }
        if (lastLoadDataSuccTime == 0) {
            return true;
        }
        long currentTime = System.currentTimeMillis();

        long interval = (currentTime - lastLoadDataSuccTime) / 1000 / 60;

        return interval >= LOAD_AD_INTERVAL;
    }

    @Override
    public void onADLoaded(List<NativeADDataRef> list) {
        if (list != null && !list.isEmpty()) {
            addAdDatas(list);
        }
    }

    @Override
    public void onNoAD(AdError adError) {
        if (adError.getErrorCode() == 501) {
            isLoadClose = true;
        }
        CommonFunction.log(TAG, "没有请求到广告，错误码是：" + adError.getErrorCode());
    }

    private final int MAX_AD_DATAS_COUNT = 8;

    /**
     * 将请求的原生广告数据加入到容器中
     *
     * @param list
     */
    private synchronized void addAdDatas(List<NativeADDataRef> list) {
        lastLoadDataSuccTime = System.currentTimeMillis();
        CommonFunction.log(TAG, String.format("请求到%s条新的广告数据，数据内容为：%s", list.size(), list.toString()));
        if (adDatas.size() >= MAX_AD_DATAS_COUNT) {
            adDatas.clear();
        }
            for (NativeADDataRef nativeADDataRef : list){

                if (Config.isShowGoogleApp){
                    if (!nativeADDataRef.isAPP())
                        adDatas.add(nativeADDataRef);
                }else{
                    adDatas.add(nativeADDataRef);
                }
            }


        isLoadClose = false;
    }

    /**
     * 获取下一个原生广告
     *
     * @return
     */
    public synchronized NativeADDataRef getNextAdData() {
        NativeADDataRef nativeADDataRef = null;
        synchronized (NativeADDataRef.class){
            if (adDatas != null && !adDatas.isEmpty()) {
                int index = adLastIndex.getAndIncrement() % adDatas.size();

                if (index < adDatas.size()) {
                    nativeADDataRef = adDatas.get(index);
                }
            }
        }

        return nativeADDataRef;
    }

    /**
     * 判断广告服务中心是否有广告
     *
     * @return
     */
    public synchronized boolean isHaveAdDatas() {
        return adDatas != null && !adDatas.isEmpty();
    }

    @Override
    public void onADStatusChanged(NativeADDataRef nativeADDataRef) {
        CommonFunction.log(TAG, "广告状态发生改变");
    }

    @Override
    public void onADError(NativeADDataRef nativeADDataRef, AdError adError) {
        CommonFunction.log(TAG, "拉取广告出错，错误码：" + adError.getErrorCode());
    }

}
