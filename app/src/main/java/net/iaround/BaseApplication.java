package net.iaround;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.alivc.player.AliVcMediaPlayer;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.stetho.Stetho;
import com.ishumei.smantifraud.SmAntiFraud;
import com.migu.sdk.api.MiguSdk;
import com.peng.one.push.OnePush;
import com.peng.one.push.core.OnOnePushRegisterListener;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.tinker.lib.service.PatchResult;
import com.tencent.tinker.loader.app.ApplicationLike;
import com.tinkerpatch.sdk.TinkerPatch;
import com.tinkerpatch.sdk.loader.TinkerPatchApplicationLike;
import com.tinkerpatch.sdk.server.callback.RollbackCallBack;
import com.tinkerpatch.sdk.tinker.callback.ResultCallBack;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import net.iaround.conf.Config;
import net.iaround.floatwindow.ChatBarZoomWindowHelper;
import net.iaround.floatwindow.VideoChatFloatWindowHelper;
import net.iaround.floatwindow.WindowManagerHelper;
import net.iaround.im.STNManager;
import net.iaround.model.im.DynamicNewNumberBean;
import net.iaround.privat.library.VolleyUtil;
import net.iaround.service.NetworkChangedReceiver;
import net.iaround.statistics.Statistics;
import net.iaround.tools.CommonFunction;
import net.iaround.manager.CrashHandler;
import net.iaround.tools.picture.glide.GlideImageLoader;
import net.iaround.tools.picture.glide.GlidePauseOnScrollListener;
import net.iaround.ui.comon.MenuBadgeHandle;
import net.iaround.ui.datamodel.DynamicModel;
import net.iaround.utils.DeviceUtils;
import net.iaround.utils.ImageViewUtil;
import net.iaround.utils.logger.Logger;

import cn.finalteam.galleryfinal.GalleryUtils;
import me.leolin.shortcutbadger.ShortcutBadger;


public class BaseApplication extends MultiDexApplication {

    private static final String TAG = "BaseApplication";

    public static volatile Context appContext;
    public int count = 0;

    public boolean isForeground = false;

    /**
     * 单例对象实例
     */
    private static BaseApplication instance = null;

    public synchronized static BaseApplication getInstance() {
        if (instance == null) {
            instance = new BaseApplication();
        }
        return instance;
    }

    private ApplicationLike tinkerApplicationLike;

    @Override
    public void onCreate() {
        super.onCreate();
        initTinkerPatch();
        //targetSdkVersion : 26 不兼容，故注释掉 按照业务需求，如果有必要打开咪咕动漫支付逻辑，需要升级咪咕最新版本
        //初始化咪咕动漫SDK
        //System.loadLibrary("mg20pbase");
        /*开启网络广播监听*/
        NetworkChangedReceiver.registerNetworkStateReceiver(this);
        appContext = getApplicationContext();
        //推送服务所在的进程非主进程，不需要初始化其他模块
        String proc = getCurrentProcessName(this);
        if (proc != null && false == proc.equals("net.iaround")) {
            //初始化日志模块，能打到控制台和文件
            Logger.init(false);
            return;
        }

        //初始化日志模块，能打到控制台和文件
        Logger.init(true);

        CommonFunction.log(TAG, "init crash...");
        //crash崩溃本地捕捉
        CrashHandler.getInstance().init(getApplicationContext());
        //crash上报模块初始化
        if (Config.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            //debug模式下利用BlockCanary性能检测
//            BlockCanary.install(this, new AppBlockCanaryContext()).start();
            //debug模式下利用LeakCanary检查内存泄漏
//            if (LeakCanary.isInAnalyzerProcess(this)){
//                return;
//            }
//            LeakCanary.install(this);
            //查看数据库
            Stetho.initializeWithDefaults(this);
        } else {
            CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(appContext); // App的策略Bean
            strategy.setAppChannel(CommonFunction.getPackageMetaData(appContext));// 设置渠道
            strategy.setAppVersion(Config.APP_VERSION); // App的版本
            strategy.setAppReportDelay(5000); // 设置SDK处理延时，毫秒
            CrashReport.initCrashReport(getApplicationContext(), "900001692", Config.DEBUG, strategy); // 自定义策略生效，必须在初始化SDK前调用
        }

        CommonFunction.log(TAG, "init network...");
        Fresco.initialize(this);
        VolleyUtil.initDefault(this);// 网络模块
        VolleyUtil.setDebug(Config.DEBUG);
        //图片加载模块
        ImageViewUtil.initDefault(getApplicationContext());

        isCurrentRunningForeground();

        //数美初始化
        initDevice();

        //网络加载模块初始化
        GalleryUtils.getInstance().setImageLoader(new GlideImageLoader());
        GalleryUtils.getInstance().setPauseOnScrollListener(new GlidePauseOnScrollListener(false, true));

        //统计模块初始化
        Statistics.start(this);

        //悬浮窗模块初始化
        WindowManagerHelper.getInstance().init(appContext);
        ChatBarZoomWindowHelper.getInstance().init(appContext);

        /**去掉一对一视频*/
//        VideoChatFloatWindowHelper.getInstance().init(appContext);

        // 极光推送初始化
//        CommonFunction.log("BaseApplication", "init jpush...");
//        JPushInterface.setDebugMode(Config.DEBUG);
//        JPushInterface.init(getApplicationContext());

        // 整合第三方推送
        //String currentProcessName = getCurrentProcessName(appContext);
        //只在主进程中注册(注意：umeng推送，除了在主进程中注册，还需要在channel中注册)
        if (BuildConfig.APPLICATION_ID.equals(proc) || BuildConfig.APPLICATION_ID.concat(":channel").equals(proc)) {
            //platformCode和platformName就是在<meta/>标签中，对应的"平台标识码"和平台名称
            OnePush.init(this, new OnOnePushRegisterListener() {
                @Override
                public boolean onRegisterPush(int platformCode, String platformName) {
                    boolean result = false;
                    if (DeviceUtils.isXiaomi()) {
                        result = platformCode == 101;
                    } else if (DeviceUtils.isHuawei()) {
                        result = platformCode == 107;
                    } else if (DeviceUtils.isMeizu()) {
                        result = platformCode == 105;
                    } else {
                        result = platformCode == 106;
                    }
                    CommonFunction.log(TAG, "Register-> code: " + platformCode + " name: " + platformName + " result: " + result);
                    return result;
                    //return platformCode == 105;
                }
            });
            OnePush.register();
        }

        AliVcMediaPlayer.init(appContext);

        /**
         * 友盟统计初始化
         * 初始化common库
         * 参数1:上下文，不能为空
         * 参数2:设备类型，UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子，默认为手机
         * 参数3:Push推送业务的secret 需要集成Push功能时必须传入Push的secret，否则传空。
         */
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, null);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        //要统计fragment页面，则需要禁止默认的页面统计方式
        MobclickAgent.openActivityDurationTrack(false);
    }

    private void initTinkerPatch() {
        // 我们可以从这里获得Tinker加载过程的信息
        if (BuildConfig.TINKER_ENABLE) {
            tinkerApplicationLike = TinkerPatchApplicationLike.getTinkerPatchApplicationLike();
            // 初始化TinkerPatch SDK
            TinkerPatch tinkerPatch = TinkerPatch.init(tinkerApplicationLike)
                    //是否自动反射Library路径,无须手动加载补丁中的So文件
                    //注意,调用在反射接口之后才能生效,你也可以使用Tinker的方式加载Library
                    .reflectPatchLibrary()
                    //向后台获取是否有补丁包更新,默认的访问间隔为3个小时
                    //若参数为true,即每次调用都会真正的访问后台配置
                    .fetchPatchUpdate(true)
                    //设置访问后台补丁包更新配置的时间间隔,默认为3个小时
                    .setFetchPatchIntervalByHours(3)
                    //设置补丁合成成功后,锁屏重启程序
                    //默认是等应用自然重启
//                    .setPatchRestartOnSrceenOff(true)
                    //设置收到后台回退要求时,锁屏清除补丁
                    //默认是等主进程重启时自动清除
//                    .setPatchRollbackOnScreenOff(true)
                    //设置当前渠道号,对于某些渠道我们可能会想屏蔽补丁功能；设置渠道后,我们就可以使用后台的条件控制渠道更新
                    .setAppChannel(BuildConfig.FLAVOR)
                    //我们可以通过RollbackCallBack设置对回退时的回调
//                    .setPatchRollBackCallback(new RollbackCallBack() {
//                        @Override
//                        public void onPatchRollback() {
//                            Log.i(TAG, "onPatchRollback callback here");
//                        }
//                    })
                    //我们可以通过ResultCallBack设置对合成后的回调
                    //例如弹框什么
                    .setPatchResultCallback(new ResultCallBack() {
                        @Override
                        public void onPatchResult(PatchResult patchResult) {
                            Log.i(TAG, "onPatchResult callback here");
                        }
                    });
            if(Config.isShowGoogleApp){
                //屏蔽部分渠道的补丁功能  屏蔽谷歌渠道
                tinkerPatch.addIgnoreAppChannel(BuildConfig.FLAVOR);
            }
            // 获取当前的补丁版本
            Log.d(TAG, "Current patch version is " + TinkerPatch.with().getPatchVersion());

            // fetchPatchUpdateAndPollWithInterval 与 fetchPatchUpdate(false)
            // 不同的是，会通过handler的方式去轮询
            // 每隔3个小时(通过setFetchPatchIntervalByHours设置)去访问后台时候有更新,通过handler实现轮训的效果
            TinkerPatch.with().fetchPatchUpdateAndPollWithInterval();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        MiguSdk.exitApp(appContext);
    }

    /**
     * 获取设备信息
     */
    private void initDevice() {
        SmAntiFraud.SmOption option = new SmAntiFraud.SmOption();
        option.setOrganization("52QgIc9V8TRans6VBAqB");
        SmAntiFraud.create(BaseApplication.appContext, option);
    }

    /**
     * 监测切换前后台上标数据设置
     */
    public void isCurrentRunningForeground() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityStopped(Activity activity) {
                //切到后台
                count--;
                if (count == 0) {
                    //gh 私聊 获取消息和发送者数量
                    Object[] objects = MenuBadgeHandle.getInstance(appContext).countNewMessageNum(appContext);
                    int accostAll = (Integer) objects[0];

                    //gh 动态
                    DynamicNewNumberBean bean = DynamicModel.getInstent().getNewNumBean();


                    int allCount = accostAll + bean.getCommentNum() + bean.getLikenum();
                    //gh 设置未读消息数量
                    ShortcutBadger.applyCount(appContext, allCount);

                    Statistics.onAppStopped();
                    WindowManagerHelper.getInstance().onAppBackground();
                    ChatBarZoomWindowHelper.getInstance().onAppBackground();
                    /**去掉一对一视频*/
//                    STNManager.onAppBackground();
                }
                isForeground = true;
            }

            @Override
            public void onActivityStarted(Activity activity) {
                //切到前台
                if (count == 0) {
                    Statistics.onAppStarted();
                    WindowManagerHelper.getInstance().onAppForeground();
                    ChatBarZoomWindowHelper.getInstance().onAppForeground();

                    /**去掉一对一视频*/
//                    STNManager.onAppForeground();
                }
                count++;
                isForeground = false;
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }
        });
    }

    private String getCurrentProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        //低内存的时候回收掉
        NetworkChangedReceiver.unRegisterNetworkStateReceiver(this);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
