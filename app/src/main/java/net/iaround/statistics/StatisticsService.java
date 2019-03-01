package net.iaround.statistics;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;

import net.iaround.connector.ConnectorManage;
import net.iaround.connector.protocol.StatisticsHttpProtocol;
import net.iaround.privat.library.VolleyUtil;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.PhoneInfoUtil;
import net.iaround.tools.SharedPreferenceUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by liangyuanhuan on 22/08/2017.
 * 本地服务
 * 功能：
 * 1）记录统计日志
 * 2）上传统计日志
 */

public class StatisticsService extends Service {
    public static final int DEV_ACTIVATE = 1;
    public static final int APP_STARTUP = 2;
    public static final int APP_STARTED = 3;
    public static final int APP_STOPPED = 4;
    public static final int PAGE_CLICK = 5;
    public static final int PAGE_RESUME = 6;
    public static final int PAGE_PAUSE = 7;
    public static final int MEMBER_PAY = 8;
    public static final int DIAMOND_PAY = 9;

    public static final int CHECK_UPLOAD_FILE = 99; //检查是否有统计文件待上传
    public static final int CHECK_UPLOAD_CONFIG = 100; //更新文件上传接口配置

    public static final int SEND_DELAY_TIME = 10000; //60 秒

    private final IBinder mBinder = new LocalBinder();
    private HandlerThread mThread = null;
    private StatisticsHandler mHandler = null;
    private boolean mEnableService = true;

    public class LocalBinder extends Binder {
        StatisticsService getService() {
            return StatisticsService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mThread = new HandlerThread("Statistics Service");
        mThread.start();
        mHandler = new StatisticsHandler(this,mThread.getLooper());

        //设备激活 是否已经发送设备激活
        boolean isDevActivate = SharedPreferenceUtil.getInstance(this).getBoolean(SharedPreferenceUtil.DEVICE_ACTIVATE_SEND,false);
        if(false == isDevActivate){
            Message msg = mHandler.obtainMessage();
            msg.what = DEV_ACTIVATE;
            //mHandler.sendMessage(msg);
            mHandler.sendMessageDelayed(msg,SEND_DELAY_TIME);
        }
        //APP 启动
        Message msg = mHandler.obtainMessage();
        msg.what = APP_STARTUP;
        //mHandler.sendMessage(msg);
        mHandler.sendMessageDelayed(msg,SEND_DELAY_TIME);

        //服务一开启需要检查是否存在待上传文件
//        Message msg = mHandler.obtainMessage();
//        msg.what = CHECK_UPLOAD_FILE;
//        mHandler.sendMessage(msg);
        CommonFunction.log("StatisticsService", "onCreate() into");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        mThread.quit();
        mHandler = null;
        mEnableService = false;
        super.onDestroy();

        CommonFunction.log("StatisticsService", "onDestroy() into");
    }

    /* App 进入
            * */
    public void onAppCreated(){
        if(null==mHandler) return;
        if(false==mEnableService) return;
        Message msg = mHandler.obtainMessage();
        msg.what = APP_STARTUP;
        mHandler.sendMessage(msg);
    }

    /* App 切到后台
  * */
    public void onAppStopped() {
        if(null==mHandler) return;
        if(false==mEnableService) return;
        Message msg = mHandler.obtainMessage();
        msg.what = APP_STOPPED;
        mHandler.sendMessage(msg);
    }

    /* App 切到前台
    * */
    public void onAppStarted() {
        if(null==mHandler) return;
        if(false==mEnableService) return;
        Message msg = mHandler.obtainMessage();
        msg.what = APP_STARTED;
        mHandler.sendMessage(msg);
    }

    /* 页面 点击
    * page 页面定义
    * */
    public void onPageClick(int page){
        CommonFunction.log("StatisticsService", "onPageClick() into");
        if(null==mHandler) return;
        if(false==mEnableService) return;

        Message msg = mHandler.obtainMessage();
        msg.what = PAGE_CLICK;
        msg.arg1 = page;
        mHandler.sendMessage(msg);
    }

    /* 页面 切到前台
  * page 页面定义
  * */
    public void onPageResume(int page){
        if(null==mHandler) return;
        if(false==mEnableService) return;
        Message msg = mHandler.obtainMessage();
        msg.what = PAGE_RESUME;
        msg.arg1 = page;
        mHandler.sendMessage(msg);
    }

    /* 页面 切到后台
    * page 页面定义
    * */
    public void onPagePause(int page){
        if(null==mHandler) return;
        if(false==mEnableService) return;
        Message msg = mHandler.obtainMessage();
        msg.what = PAGE_PAUSE;
        msg.arg1 = page;
        mHandler.sendMessage(msg);
    }

    /* 会员支付成功
    * */
    public void onMemberPay(float money){
        if(null==mHandler) return;
        if(false==mEnableService) return;
        Message msg = mHandler.obtainMessage();
        msg.what = MEMBER_PAY;
        msg.obj = Float.valueOf(money);
        mHandler.sendMessage(msg);
    }

    /* 钻石充值成功
    * */
    public void onDiamondPay(float money){
        if(null==mHandler) return;
        if(false==mEnableService) return;
        Message msg = mHandler.obtainMessage();
        msg.what = MEMBER_PAY;
        msg.obj = Float.valueOf(money);
        mHandler.sendMessage(msg);
    }


    static class StatisticsHandler extends Handler {
        private boolean STATISTICS_SERVICE_ENABLE = true; //统计文件上传开关
        private long FILE_UPLOAD_INTERNAL = 24*60*60; //统计文件上传间隔 单位秒 默认一天
        private long APP_CREATED_INTERNAL = 3000; //app 重启间隔 3 seconds
        private long APP_STOP_START_INTERNAL = 30000; //app 后台切到前台间隔 30 seconds

        private long mLastAppCreated = 0; //app 上次重启间隔
        private long mLastAppStopped = 0; //app 切到后台的时间
        HashMap<Integer,Long> mLastPageResume = new HashMap<Integer,Long>(); //page 切到前台的时间

        private WeakReference<StatisticsService> mService;

        public StatisticsHandler(StatisticsService service, Looper looper){
            super(looper);
            mService = new WeakReference<StatisticsService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CHECK_UPLOAD_FILE:
                    handleCheckUploadFile();
                    break;
                case CHECK_UPLOAD_CONFIG:
                    handleCheckUploadConfig();
                    break;
                case DEV_ACTIVATE:
                    handleDevActivate();
                    break;
                case APP_STARTUP:
                    handleAppStartup();
                    break;
                case APP_STOPPED:
                    handleAppStopped();
                    break;
                case APP_STARTED:
                    handleAppStarted();
                    break;
                case PAGE_CLICK:
                    handlePageClick(msg.arg1);
                    break;
                case PAGE_RESUME:
                    handlePageResume(msg.arg1);
                    break;
                case PAGE_PAUSE:
                    handlePagePause(msg.arg1);
                    break;
                case MEMBER_PAY:
                    handleMemberPay((Float) msg.obj);
                    break;
                case DIAMOND_PAY:
                    handleDiamondPay((Float)msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }

        /* 检查是否有文件待上传
        * 当前版本不做日志
        * */
        private void handleCheckUploadFile(){
            if(false == STATISTICS_SERVICE_ENABLE){
                return;
            }
            String logPath = CommonFunction.getSDPath( ) + "statistics/";

            File[] files = null;
            ArrayList<File> uploads = new ArrayList<File>();
            try {
                File dir = new File( logPath );
                if(!dir.exists()){
                    return;
                }
                files = dir.listFiles();
                for (File file : files) {
                    if(file.isFile()){
                        long current = System.currentTimeMillis();
                        long create = file.lastModified();
                        if(FILE_UPLOAD_INTERNAL < (current-create) ){
                            uploads.add(file);
                        }
                    }
                }
            }catch (Exception e){

            }
            if(uploads.size()>0){
                //TODO 启动统计文件上传任务
            }
        }

        /* 更新文件上传配置
        * */
        private void handleCheckUploadConfig(){

        }

        private void handleDevActivate(){
            StatisticsService service = mService.get();
            if(null== service){
                return;
            }
            String user = PhoneInfoUtil.getInstance(service).getDeviceId();
            String json = StatisticsFormat.getDeviceActivateJson(service,user);
            //boolean result = sendStatistics( Statistics.DEVICE_ACTIVATE, user);
            boolean result = sendStatistics( json );
            if(result) {
                SharedPreferenceUtil.getInstance(service).putBoolean(SharedPreferenceUtil.DEVICE_ACTIVATE_SEND, true);
            }
        }

        private void handleAppStartup(){
            StatisticsService service = mService.get();
            if(null== service){
                return;
            }
            long current = System.currentTimeMillis();
            mLastAppCreated = SharedPreferenceUtil.getInstance(service).getLong(SharedPreferenceUtil.APPLICATION_CREATED_TIME,(long)0);
            if(0 == mLastAppCreated) {
                mLastAppCreated = current;
            } else{
                if( APP_CREATED_INTERNAL > (current - mLastAppCreated) ){
                    SharedPreferenceUtil.getInstance(service).putLong(SharedPreferenceUtil.APPLICATION_CREATED_TIME,current);
                    return ;
                }else{
                    mLastAppCreated = current;
                }
            }
            SharedPreferenceUtil.getInstance(service).putLong(SharedPreferenceUtil.APPLICATION_CREATED_TIME,mLastAppCreated);
            String user = SharedPreferenceUtil.getInstance(service).getString(SharedPreferenceUtil.USER_ID);
            if(null==user || user.equals("")){
                CommonFunction.log("StatisticsHandler", "handleAppStartup() user null");
                return;
            }
            String json = StatisticsFormat.getAppStartupJson(service,user);
            //sendStatistics( Statistics.APP_STARTUP, user );
            sendStatistics( json );
        }

        private void handleAppStopped(){
            mLastAppStopped = System.currentTimeMillis();
        }

        private void handleAppStarted(){
            StatisticsService service = mService.get();
            if(null== service){
                return;
            }
            long current = System.currentTimeMillis();
            if( (current - mLastAppStopped) > APP_STOP_START_INTERNAL){
                mLastAppStopped = current;

                SharedPreferenceUtil.getInstance(service).putLong(SharedPreferenceUtil.APPLICATION_CREATED_TIME,mLastAppCreated);
                String user = SharedPreferenceUtil.getInstance(service).getString(SharedPreferenceUtil.USER_ID);
                if(null==user || user.equals("")){
                    CommonFunction.log("StatisticsHandler", "handleAppStarted() user null");
                    return;
                }
                String json = StatisticsFormat.getAppStartupJson(service,user);
                //sendStatistics( Statistics.APP_STARTUP, user );
                sendStatistics( json );
            }
        }

        private void handlePageClick(int page){
            StatisticsService service = mService.get();
            if(null== service){
                return;
            }
            String user = SharedPreferenceUtil.getInstance(service).getString(SharedPreferenceUtil.USER_ID);
            String json = StatisticsFormat.getPageTimeJson(service,user,page, 0);

            //sendStatistics( page,user );
            sendStatistics( json );

        }

        private void handlePageResume(int page){
            long current = System.currentTimeMillis();
            mLastPageResume.put(page, current);
        }

        private void handlePagePause(int page){
            if(mLastPageResume.containsKey(page)){
                long resume = mLastPageResume.get(page);
                long current = System.currentTimeMillis();
                long time = resume - current;
                StatisticsService service = mService.get();
                if(null== service){
                    return;
                }
                String user = SharedPreferenceUtil.getInstance(service).getString(SharedPreferenceUtil.USER_ID);
                String json = StatisticsFormat.getPageTimeJson(service,user,page, time);

                //sendStatistics(page,user);
                sendStatistics( json );

            }
        }

        private void handleMemberPay(Float money){
            StatisticsService service = mService.get();
            if(null== service){
                return;
            }
            String user = SharedPreferenceUtil.getInstance(service).getString(SharedPreferenceUtil.USER_ID);
            String json = StatisticsFormat.getMemberPayJson(service,user, money);
            //sendStatistics( Statistics.MEMBER_PAY_SUCCESS, user );
            sendStatistics( json );
        }

        private void handleDiamondPay(Float money){
            StatisticsService service = mService.get();
            if(null== service){
                return;
            }
            String user = SharedPreferenceUtil.getInstance(service).getString(SharedPreferenceUtil.USER_ID);
            String json = StatisticsFormat.getDiamondPayJson(service,user, money);
            //sendStatistics( Statistics.DIAMOND_PAY_SUCCESS, user );
            sendStatistics( json );
        }

        private boolean sendStatistics(String content){
            StatisticsService service = mService.get();
            if(null== service){
                return false;
            }
            if (!CommonFunction.isWifi(service)){
                CommonFunction.log("StatisticsHandler", "sendStatistics() not in wifi");
                return false;
            }

            CommonFunction.log("StatisticsHandler", "sendStatistics() log content=" + content);


            int result = StatisticsHttpProtocol.getInstance().syncSendStatistics(content);

            CommonFunction.log("StatisticsHandler", "sendStatistics() result=" + result);

            //解析返回结果 获得统计开关
            if(result == -1){
                CommonFunction.log("StatisticsHandler", "sendStatistics() network exception");
                return false;
            }else if(result == 1){
                CommonFunction.log("StatisticsHandler", "sendStatistics() stop service");
                //停止统计服务
                service.mEnableService = false;
                service.stopSelf();
            }

            return true;
        }

        // part 统计消息的类型
//        private boolean sendStatistics(int statisticType, String user){
//            StatisticsService service = mService.get();
//            if(null== service){
//                return false;
//            }
//            if (!CommonFunction.isWifi(service)){
//                CommonFunction.log("StatisticsHandler", "sendStatistics() not in wifi");
//                return false;
//            }
//
//            CommonFunction.log("StatisticsHandler", "sendStatistics()  statistic type=" + statisticType + ", user=" + user);
//
//            //TODO
//
//            String result = StatisticsHttpProtocol.getInstance().syncGetStatistics(service,statisticType,user);
//
//            CommonFunction.log("StatisticsHandler", "sendStatistics() result=" + result);
//
//            //解析返回结果 获得统计开关
//            if(result!=null && result.equals("CLOSE")){
//                CommonFunction.log("StatisticsHandler", "sendStatistics() stop service");
//                //停止统计服务
//                service.mEnableService = false;
//                service.stopSelf();
//            }
//
//            return true;
//        }

    }
}
