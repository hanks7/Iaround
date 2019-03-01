package net.iaround.statistics;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import net.iaround.tools.CommonFunction;

/**
 * Created by liangyuanhuan on 22/08/2017.
 * 统计功能模块
 * 作用：统计客户端行为
 * 注意点：
 * 1 统计开关接口（开关，上传周期）和 统计文件上传接口
 * 2 统计的信息有：设备激活 启动 页面 支付 充值 等
 * 3 zip 压缩
 * 4 有wifi情况下上传
 * 5 上传成功删除文件
 */

public class Statistics {
    //设备激活
    public static final int DEVICE_ACTIVATE = 1000;
    //app 启动
    public static final int APP_STARTUP = 1001;
    //点击页面
    public static final int PAGE_NEARBY = 1002;
    public static final int PAGE_DYNAMIC = 1003;
    public static final int PAGE_CHAT_HOT = 1004;
    public static final int PAGE_CHAT_MINE = 1005;
    public static final int PAGE_MESSAGE = 1006;
    public static final int PAGE_RANKING_CHARM = 1007;
    public static final int PAGE_RANKING_RICH = 1008;
    public static final int PAGE_USER = 1009;
    //进入会员支付页面
    public static final int PAGE_MEMBER_PAY = 1010;
    //进入钻石支付页面
    public static final int PAGE_DIAMOND_PAY = 1011;
    //会员支付成功
    public static final int MEMBER_PAY_SUCCESS = 1012;
    //钻石支付成功
    public static final int DIAMOND_PAY_SUCCESS = 1013;
    public static final int PAGE_RANKING_SKILL = 1014;
    //首页广告
    public static final int PAGE_INDEX_AD = 1015;

    private static Statistics sInstance = null;

    public static Statistics getsInstance(){
        if(null == sInstance){
            synchronized (Statistics.class){
                if(null == sInstance){
                    sInstance = new Statistics();
                }
            }
        }
        return sInstance;
    }

    public static void start(Context context){
        CommonFunction.log("Statistics", "start() into");
        getsInstance().startInternal(context);
    }

    public static void stop(Context context ){
        CommonFunction.log("Statistics", "stop() into");
        getsInstance().stopInternal(context);
    }

    /* App 进入
    * */
    public static void onAppCreated(){
        if(false == getsInstance().mBound)
            return;
        if(null == getsInstance().mService)
            return ;
        CommonFunction.log("Statistics", "onAppCreated() into");
        getsInstance().mService.onAppCreated();
    }

    /* App 切到后台
    * */
    public static void onAppStopped(){
        if(false == getsInstance().mBound)
            return;
        if(null == getsInstance().mService)
            return ;
        CommonFunction.log("Statistics", "onAppStopped() into");
        getsInstance().mService.onAppStopped();
    }

    /* App 切到前台
    * */
    public static void onAppStarted(){
        if(false == getsInstance().mBound)
            return;
        if(null == getsInstance().mService)
            return ;
        CommonFunction.log("Statistics", "onAppStarted() into");
        getsInstance().mService.onAppStarted();
    }

    /* 页面 点击
    * page 页面定义
    * */
    public static void onPageClick(int page){
        if(false == getsInstance().mBound)
            return;
        if(null == getsInstance().mService)
            return ;
        CommonFunction.log("Statistics", "onPageClick() page=" + page);
        getsInstance().mService.onPageClick(page);
    }

    /* 页面 切到前台
    * page 页面定义
    * */
    public static void onPageResume(int page){
        if(false == getsInstance().mBound)
            return;
        if(null == getsInstance().mService)
            return ;
        CommonFunction.log("Statistics", "onPageResume() page=" + page);
        getsInstance().mService.onPageResume(page);
    }

    /* 页面 切到后台
    * page 页面定义
    * */
    public static void onPagePause(int page){
        if(false == getsInstance().mBound)
            return;
        if(null == getsInstance().mService)
            return ;
        CommonFunction.log("Statistics", "onPagePause() page=" + page);
        getsInstance().mService.onPagePause(page);
    }

    /* 会员支付成功
    * */
    public static void onMemberPay(float money){
        if(false == getsInstance().mBound)
            return;
        if(null == getsInstance().mService)
            return ;
        CommonFunction.log("Statistics", "onMemberPay() money=" + money);
        getsInstance().mService.onMemberPay(money);
    }

    /* 钻石充值成功
    * */
    public static void onDiamondPay(float money){
        if(false == getsInstance().mBound)
            return;
        if(null == getsInstance().mService)
            return ;
        CommonFunction.log("Statistics", "onDiamondPay() money=" + money);
        getsInstance().mService.onDiamondPay(money);
    }

    private Statistics(){

    }

    private void startInternal(Context context){
        if(true == mBound)
            return;
        Intent intent = new Intent(context, StatisticsService.class);
        context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void stopInternal(Context context){
        if(false == mBound)
            return;
        mBound = false;
        context.unbindService(mConnection);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                StatisticsService.LocalBinder binder = (StatisticsService.LocalBinder) service;
                mService = binder.getService();
                mBound = true;
                //mEnable = ;
                CommonFunction.log("Statistics", "service connected");
            }catch (Exception e){
                CommonFunction.log("Statistics", "service connected fail, exception=" + e.toString());
                mService = null;
                mBound = false;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            mService = null;
            //mEnable = false;
            CommonFunction.log( "Statistics" , "service disconnected" );
        }

    };

    private boolean mBound = false;
    private StatisticsService mService = null;
    //private boolean mEnable = false;
}
