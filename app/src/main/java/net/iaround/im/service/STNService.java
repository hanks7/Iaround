package net.iaround.im.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.tencent.mars.BaseEvent;
import com.tencent.mars.Mars;
import com.tencent.mars.app.AppLogic;
import com.tencent.mars.sdt.SdtLogic;
import com.tencent.mars.stn.StnLogic;
import com.tencent.mars.xlog.Xlog;

import net.iaround.BuildConfig;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.im.aidl.IPushMessageFilter;
import net.iaround.im.aidl.ISTNService;
import net.iaround.im.aidl.ITaskWrapper;
import net.iaround.im.proto.Iachat;
import net.iaround.im.proto.Iavchat;
import net.iaround.im.push.PushMessage;
import net.iaround.im.task.TaskProperty;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.activity.SplashActivity;
import net.iaround.utils.logger.Logger;
import net.iaround.videochat.task.LoginTaskWrapper;
import net.iaround.videochat.task.TaskWrapperManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * STN 服务
 */

public class STNService extends Service implements AppLogic.ICallBack, StnLogic.ICallBack, SdtLogic.ICallBack {
    private static final String TAG = "STNService";

    private final IBinder mBinder = new STNServiceBinder();
    private HandlerThread mThread = null;
    private STNHandler mHandler = null;
    private boolean mStart = false;
    private Context mContext;
    private AppLogic.AccountInfo mAccountInfo = null;
    private AppLogic.DeviceInfo mDeviceInfo = null;
    private Map<Integer, ITaskWrapper> TASK_ID_TO_WRAPPER = new ConcurrentHashMap<>();
    private ConcurrentLinkedQueue<IPushMessageFilter> filters = new ConcurrentLinkedQueue<>();
    private int mLongLinkStatus = -99; //长连接状态

    private long mUserID = 0;   //用户参数
    private String mUserName = "zero";   //用户参数
    private String mDeviceName = "zero";   //设备名
    private String mDeviceType = "android"; //设备类型
    private String mHostName = "vchat.iaround.com"; //服务器域名
    private String mHostIP = "118.89.21.145";//服务器IP
    private int mLongLinkPort = 8101; //服务器端口
    private int mShortLinkPort = 8080; //服务器短链接端口
    private int mVersion = 100; //通讯版本号
    private String mToken = ""; //连接服务器的 token
    private int mIsAnchor = 0; //是不是主播

    private LoginTaskWrapper mLoginTask = null; //如果需要重登陆，但主进程已经死忙，服务需要自动登陆
    private ConcurrentLinkedQueue<PushMessage> cachePushMessages = new ConcurrentLinkedQueue<PushMessage>();

    private long[] vibratePattern = {0, 1000, 3000, 1000, 3000, 1000, 3000, 1000, 3000, 1000, 3000, 1000, 3000, 1000, 3000, 1000, 3000, 1000, 3000, 1000, 3000, 1000, 3000, 1000, 3000, 1000, 3000, 1000, 3000, 1000, 3000};

    /********************************** 服务提供的接口 ***************************
    * */

     /* 获得服务对象使用其接口
    * */
    public class STNServiceBinder extends ISTNService.Stub {

        /* App 切到后台
        * */
        @Override
        public void onAppBackground() throws RemoteException{
            if(null==mHandler || false==mStart) {
                Logger.w(TAG,"onAppBackground() service not start");
                return;
            }

            Message msg = mHandler.obtainMessage();
            msg.what = STNHandler.HANDLER_APP_ON_BACKGROUND;
            mHandler.sendMessage(msg);
        }

        /* App 切到前台
        * */
        @Override
        public void onAppForeground() throws RemoteException {
            if(null==mHandler || false==mStart) {
                Logger.w(TAG,"onAppForeground() service not start");
                return;
            }

            Message msg = mHandler.obtainMessage();
            msg.what = STNHandler.HANDLER_APP_ON_FOREGROUND;
            mHandler.sendMessage(msg);
        }

        /* 账号信息更改
        * */
        @Override
        public void onUserInfoChange() throws RemoteException {
            if(null==mHandler || false==mStart) {
                Logger.w(TAG,"onUserInfoChange() service not start");
                return;
            }

            Message msg = mHandler.obtainMessage();
            msg.what = STNHandler.HANDLER_USER_INFO_CHANGE;
            mHandler.sendMessage(msg);
        }

        /* 开始一个任务
        * return 任务ID
        * */
        @Override
        public int startTask(ITaskWrapper task) throws RemoteException{
            if(null==mHandler || false==mStart) {
                Logger.w(TAG,"startTask() service not start");
                return -1;
            }

            Message msg = mHandler.obtainMessage();
            msg.what = STNHandler.HANDLER_START_TASK;
            msg.obj = task;
            mHandler.sendMessage(msg);

            return task.getTaskID();
        }

        /* 停止一个任务
        * */
        @Override
        public void stopTask(final int taskID) throws RemoteException{
            if(null==mHandler || false==mStart) {
                Logger.w(TAG,"stopTask() service not start");
                return;
            }

            Message msg = mHandler.obtainMessage();
            msg.what = STNHandler.HANDLER_STOP_TASK;
            msg.arg1 = taskID;
            mHandler.sendMessage(msg);
        }

        /* 注册推送消息处理过滤器
        * */
        @Override
        public void registerPushMessageFilter(IPushMessageFilter filter) throws RemoteException{
            try {
                NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mNotifyMgr.cancel(99999);
            }catch (Exception e){
                e.printStackTrace();
            }

            if(filters!=null) {
                filters.remove(filter);
                filters.add(filter);
                filter.asBinder().linkToDeath( new RemoteBinderDeathRecipient(STNService.this, filter),0);
            }
            if(cachePushMessages!=null && cachePushMessages.size()>0){
                Message msg = mHandler.obtainMessage();
                msg.what = STNHandler.HANDLER_APPEND_PUSH_MESSAGE;
                mHandler.sendMessage(msg);
            }
            Logger.d(TAG, "registerPushMessageFilter() filter size=" + filters.size());
        }

        /* 注销推送消息处理过滤器
       * */
        @Override
        public void unregisterPushMessageFilter(IPushMessageFilter filter) throws RemoteException{
            if(filters!=null) {
                filters.remove(filter);
            }
            Logger.d(TAG, "unregisterPushMessageFilter() filter size=" + filters.size());
        }

    }



    /*************************** 服务的生命周期 ***************************
    * */

    @Override
    public void onCreate() {
        Logger.i(TAG,"onCreate() into");
        super.onCreate();

        mContext = this;
        mThread = new HandlerThread("STN Service");
        mThread.start();
        mHandler = new STNHandler(this,mThread.getLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.i(TAG,"onStartCommand() flags="+flags+", startId: " + startId );

        if(mStart==true){
            Logger.i(TAG,"onStartCommand() stn service already start");

            //如果参数发生变化，需要重启动
            if(isTheSameParam(intent)==false){
                Logger.i(TAG,"onStartCommand() 参数不一致，需要重起 STNService");
                if( false == readParam(intent) ){
                    Logger.i(TAG,"onStartCommand() read intent fail");
                    restoreParam();
                }

                printParam();

                stopSTN();

                if(mUserID!=0) {
                    startSTN();
                }else{
                    Logger.i(TAG,"onStartCommand() param error, stn not start!");
                }
            }
            return START_STICKY;
        }
        mStart = true;

        //读取 intent的数据
        if(intent != null){
            if( readParam(intent)==false ){
                Logger.i(TAG,"onStartCommand() read intent fail");
                restoreParam();
            }
        }else {
            Logger.i(TAG,"onStartCommand() intent null");
            //从存储文件中恢复参数
            restoreParam();
        }
        printParam();

        if(mUserID != 0) {
            //启动 STN
            startSTN();
        }else{
            Logger.i(TAG,"onStartCommand() param error, stn not start!");
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Logger.i(TAG,"onBind() into");

        if(mStart==false){
            Logger.i(TAG,"onBind() stn not start yet");
            mStart = true;

            //读取 intent的数据
            if(intent != null){
                if(readParam(intent)==false) {
                    Logger.i(TAG,"readParam() read intent fail");
                    restoreParam();
                }
            }else {
                //从存储文件中恢复参数
                restoreParam();
            }

            printParam();

            //启动 STN
            if(mUserID!=0) {
                startSTN();
            }else{
                Logger.i(TAG,"onBind() param error, stn not start!");
            }
        }

        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Logger.i(TAG,"onUnbind() into");
        if(filters!=null){
            Logger.i(TAG,"onUnbind() clear filters");
            filters.clear();
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Logger.i(TAG,"onDestroy() into");
        super.onDestroy();

        mThread.quit();
        mHandler = null;
        mStart = false;
        mContext = null;

        stopSTN();
    }

    /* 服务Handler（在服务创建的线程中使用）
     * */
    static class STNHandler extends Handler {
        public static final int HANDLER_APP_ON_FOREGROUND = 1001; //APP切回前台
        public static final int HANDLER_APP_ON_BACKGROUND = 1002; //APP切到后台
        public static final int HANDLER_USER_INFO_CHANGE = 1003; //用户信息变更
        public static final int HANDLER_START_TASK = 1004; //开启任务
        public static final int HANDLER_STOP_TASK = 1005; //停止任务
        public static final int HANDLER_APPEND_PUSH_MESSAGE = 1006; //当消息处理注册时如果有未处理消息需要处理
        public static final int HANDLER_PING_TASK = 1007; //发饱和包

        public static final int STN_PING_TASK_COMMAND_ID = 8; //饱和包 command id
        public static final int STN_PING_TASK_TASK_ID = 0xfffffff0; //饱和包 task id
        public static final int STN_PING_TASK_TIME_INTERNAL = 15*1000; //饱和包时间间隔 15秒

        private WeakReference<STNService> mService;
        private StnLogic.Task mPingTask = null;

        public STNHandler(STNService service, Looper looper){
            super(looper);
            mService = new WeakReference<STNService>(service);
            mPingTask = createPingTask();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANDLER_APP_ON_FOREGROUND:
                    handlerAppOnForeground();
                    break;
                case HANDLER_APP_ON_BACKGROUND:
                    handlerAppOnBackground();
                    break;
                case HANDLER_USER_INFO_CHANGE:
                    handlerUserInfoChanged();
                    break;
                case HANDLER_START_TASK:
                    handlerStartTask((ITaskWrapper) msg.obj);
                    break;
                case HANDLER_STOP_TASK:
                    handlerStopTask(msg.arg1);
                    break;
                case HANDLER_APPEND_PUSH_MESSAGE:
                    handlerAppendPushMessage();
                    break;
                case HANDLER_PING_TASK:
                    handlerPingTask();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }

        /*APP 切回前台
        * */
        private void handlerAppOnForeground(){
            Logger.i(TAG,"handlerAppOnForeground() into");

            BaseEvent.onForeground(true);
        }

        /*APP 切到后台
        * */
        private void handlerAppOnBackground(){
            Logger.i(TAG,"handlerAppOnBackground() into");

            BaseEvent.onForeground(false);
        }

        /*处理用户信息变化
        * */
        private void handlerUserInfoChanged(){
            Logger.i(TAG,"handlerUserInfoChanged() into");

            StnLogic.reset();
        }

        /*开启一个任务
        * */
        private void handlerStartTask(ITaskWrapper task){
            try {
                int taskid = task.getTaskID();
                Logger.i(TAG, "handlerStartTask() into, taskID=" + taskid);

                final StnLogic.Task _task = new StnLogic.Task(StnLogic.Task.ELong, 0, "", null);
                Bundle taskProperties = task.getProperties();
                // Set host & cgi path
                final String host = taskProperties.getString(TaskProperty.OPTIONS_HOST);
                final String cgiPath = taskProperties.getString(TaskProperty.OPTIONS_CGI_PATH);
                _task.shortLinkHostList = new ArrayList<>();
                _task.shortLinkHostList.add(host);
                _task.cgi = cgiPath;
                _task.needAuthed = false;

                final boolean shortSupport = taskProperties.getBoolean(TaskProperty.OPTIONS_CHANNEL_SHORT_SUPPORT, false);
                final boolean longSupport = taskProperties.getBoolean(TaskProperty.OPTIONS_CHANNEL_LONG_SUPPORT, true);
                if (shortSupport && longSupport) {
                    _task.channelSelect = StnLogic.Task.EBoth;

                } else if (shortSupport) {
                    _task.channelSelect = StnLogic.Task.EShort;

                } else if (longSupport) {
                    _task.channelSelect = StnLogic.Task.ELong;
                } else {
                    Logger.e(TAG, "handlerStartTask() invalid channel strategy");
                    return;
                }

                // Set cmdID if necessary
                int cmdID = taskProperties.getInt(TaskProperty.OPTIONS_CMD_ID, -1);
                if (cmdID != -1) {
                    _task.cmdID = cmdID;
                }
                _task.taskID = taskid;

                STNService stnService = mService.get();
                if (null == stnService) {
                    Logger.w(TAG, "handlerStartTask() stn service null");
                    return;
                }
                stnService.addTaskWrapper(task);

                // Send
                Logger.i(TAG, "handlerStartTask() now start task with id " + _task.taskID);
                StnLogic.startTask(_task);
                if (StnLogic.hasTask(_task.taskID)) {
                    Logger.i(TAG, "handlerStartTask() stn task started with id " + _task.taskID);
                } else {
                    Logger.w(TAG, "handlerStartTask() stn task start failed with id " + _task.taskID);
                }
            }catch (Exception e){
                e.printStackTrace();
                Logger.w(TAG, "handlerStartTask() stn task start error: " + e.toString() );
            }

            return ;
        }

        /*停止一个任务
        * */
        private void handlerStopTask(final int taskID){
            Logger.i(TAG,"handlerStopTask() into, taskID="+taskID);

            StnLogic.stopTask(taskID);

            STNService stnService = mService.get();
            if(null==stnService){
                Logger.w(TAG,"stn service null");
                return;
            }
            stnService.removeTaskWrapper(taskID);
        }

        /*处理挂起来的视频邀请推送消息
        * */
        private void handlerAppendPushMessage(){
            STNService stnService = mService.get();
            if(null==stnService){
                Logger.w(TAG,"stn service null");
                return;
            }

            while (true){
                boolean quit = false;
                if(stnService.cachePushMessages.isEmpty()){
                    quit = true;
                }
                PushMessage msg = stnService.cachePushMessages.poll();
                if(null == msg){
                    quit = true;
                }
                if(quit==true){
                    Logger.d(TAG, "cache push messages reach empty");
                    for (IPushMessageFilter filter : stnService.filters) {
                        try {
                            filter.onReceiveCache(0, null);
                        }catch (Exception e){
                            Logger.e(TAG,"filter handle exception happen! " + e.toString());
                            e.printStackTrace();
                        }
                    }
                    break;
                }

                for (IPushMessageFilter filter : stnService.filters) {
                    try {
                        filter.onReceiveCache(msg.cmdId, msg.buffer);
                    }catch (Exception e){
                        Logger.e(TAG,"filter handle exception happen! " + e.toString());
                        e.printStackTrace();
                    }
                }
            }
        }

        /*创建 自定义饱和包
        * */
        private StnLogic.Task createPingTask(){
            StnLogic.Task _task = new StnLogic.Task(StnLogic.Task.ELong, 0, "", null);
            _task.channelSelect = StnLogic.Task.ELong;
            _task.cmdID = STN_PING_TASK_COMMAND_ID;
            _task.taskID = STN_PING_TASK_TASK_ID;
            _task.limitFrequency = false;
            _task.limitFlow = false;
            _task.needAuthed = false;
            return _task;
        }

        /*发送自定义饱和包
        * */
        private void handlerPingTask(){
            if(mPingTask!=null){
                StnLogic.startTask(mPingTask);
                if (StnLogic.hasTask(mPingTask.taskID)) {
                    Logger.d(TAG, "handlerPingTask() stn ping task to start" );
                } else {
                    Logger.w(TAG, "handlerPingTask() stn ping task start failed");
                }
            }
        }
    }


    /*************************** STN回调 ***************************
     * */
    /**
     * STN 会将配置文件进行存储，如连网IPPort策略、心跳策略等，此类信息将会被存储在客户端上层指定的目录下
     * @return APP目录
     */
    @Override
    public String getAppFilePath(){
        if (null == mContext) {
            return null;
        }
        try {
            if(BuildConfig.DEBUG==true) {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    String path = "/iaround/im/";
                    File dir = new File(Environment.getExternalStorageDirectory(), path);
                    if (!dir.exists()) {
                        boolean boo = dir.mkdirs();
                        if (boo == false) {
                            File file = mContext.getFilesDir();
                            if (!file.exists()) {
                                file.createNewFile();
                            }
                            return file.toString();
                        }
                    }
                    path = dir.getAbsolutePath();
                    return path.toString();
                } else {
                    File file = mContext.getFilesDir();
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    return file.toString();
                }
            }else{
                File file = mContext.getFilesDir();
                if (!file.exists()) {
                    file.createNewFile();
                }
                return file.toString();
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }

        return null;
    }

    /**
     * STN 会根据客户端的登陆状态进行网络连接策略的动态调整，当用户非登陆态时，网络会将连接的频率降低
     * 所以需要获取用户的帐号信息，判断用户是否已登录
     * @return 用户帐号信息
     */
    @Override
    public AppLogic.AccountInfo getAccountInfo(){
        if(mAccountInfo == null){
            mAccountInfo = new AppLogic.AccountInfo(mUserID, mUserName);
        }
        return mAccountInfo;
    }

    /**
     * 客户端版本号能够帮助 STN 清晰区分存储的网络策略配置文件。
     * @return 客户端版本号
     */
    @Override
    public int getClientVersion(){
        return mVersion;
    }

    /**
     * 客户端通过获取设备类型，加入到不同的上报统计回调中，供客户端进行数据分析
     * @return
     */
    @Override
    public AppLogic.DeviceInfo getDeviceType(){
        if(null==mDeviceInfo){
            mDeviceInfo = new AppLogic.DeviceInfo(mDeviceName, mDeviceType);
        }
        return mDeviceInfo;
    }


    /**
     * 信令探测回调接口，启动信令探测
     */
    @Override
    public void reportSignalDetectResults(String resultsJson){
        Logger.d(TAG,"reportSignalDetectResults() into, resultsJson=" +resultsJson);
    }

    /**
     * SDK要求上层做认证操作(可能新发起一个AUTH CGI)
     * @return
     */
    @Override
    public boolean makesureAuthed(){
        Logger.d(TAG,"makesureAuthed() into");
        return true;
    }

    /**
     * SDK要求上层做域名解析.上层可以实现传统DNS解析,或者自己实现的域名/IP映射
     * @param host
     * @return
     */
    @Override
    public String[] onNewDns(final String host){
        //log("onNewDns() into, host="+host);
        return new String[]{mHostIP};
    }

    /**
     * 收到SVR PUSH下来的消息
     * @param cmdid
     * @param data
     */
    @Override
    public void onPush(final int cmdid, final byte[] data){
        Logger.i(TAG,"onPush() into, cmdid="+cmdid + ", byte length=" + (data==null?0:data.length));
        //推送消息监听过滤器
        if(filters!=null) {
            if (filters.size() > 0) {
                boolean error = false;
                for (IPushMessageFilter filter : filters) {
                    try {
                        if (filter.onReceiveMessage(cmdid, data)) {
                            break;
                        }
                    } catch (Exception e){
                        Logger.e(TAG, "onPush() error happen, filter=" + filter);
                        filters.remove(filter);
                        error = true;
                    }
                }
                if(error==true) {
                    handleNoPushMessageFilters(cmdid, data);
                }
            } else {
                //UI进程可能死了
                Logger.i(TAG,"onPush() filters size null");
                handleNoPushMessageFilters(cmdid, data);
            }
        }

        //检查服务是否挂掉，如果挂掉则需要重启动
        //应该由主进程来启动服务
        //STNManager.checkNeedRestart();
    }

    /**
     * SDK要求上层对TASK组包
     * @param taskID    任务标识
     * @param userContext
     * @param reqBuffer 组包的BUFFER
     * @param errCode   组包的错误码
     * @return
     */
    @Override
    public boolean req2Buf(final int taskID, Object userContext, ByteArrayOutputStream reqBuffer, int[] errCode, int channelSelect){
        if(taskID == STNHandler.STN_PING_TASK_TASK_ID){
            //如果是 PING 任务 不需要消息内容
            //Logger.d(TAG,"req2Buf() for ping task");
            return true;
        }
        final ITaskWrapper wrapper = TASK_ID_TO_WRAPPER.get(taskID);
        if (wrapper == null) {
            Logger.w(TAG,"invalid req2Buf for task, taskID="+taskID);
            return false;
        }
        try {
            reqBuffer.write(wrapper.req2buf());
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(TAG,"task wrapper req2buf failed for short, check your encode process");
        }
        return false;
    }

    /**
     * SDK要求上层对TASK解包
     * @param taskID        任务标识
     * @param userContext
     * @param respBuffer    要解包的BUFFER
     * @param errCode       解包的错误码
     * @return  int
     */
    @Override
    public int buf2Resp(final int taskID, Object userContext, final byte[] respBuffer, int[] errCode, int channelSelect){
        if(taskID == STNHandler.STN_PING_TASK_TASK_ID){
            //如果是 PING 任务 不需要转换消息内容
            //Logger.d(TAG,"buf2Resp() for ping task");
            return StnLogic.RESP_FAIL_HANDLE_NORMAL;
        }
        final ITaskWrapper wrapper = TASK_ID_TO_WRAPPER.get(taskID);
        if (wrapper == null) {
            Logger.w(TAG,"buf2Resp() wrapper not found for stn task, taskID="+taskID);
            return StnLogic.RESP_FAIL_HANDLE_TASK_END;
        }

        try {
            return wrapper.buf2resp(respBuffer);
        } catch (Exception e) {
            Logger.e(TAG,"buf2Resp() error happen, clean this context, taskID="+ taskID);
            TASK_ID_TO_WRAPPER.remove(taskID);
        }
        return StnLogic.RESP_FAIL_HANDLE_TASK_END;
    }

    /**
     * 任务结束回调
     * @param taskID            任务标识
     * @param userContext
     * @param errType           错误类型
     * @param errCode           错误码
     * @return
     */
    @Override
    public int onTaskEnd(final int taskID, Object userContext, final int errType, final int errCode){
        if(taskID == STNHandler.STN_PING_TASK_TASK_ID){
            //Logger.d(TAG,"onTaskEnd() for ping task");
            if(errType!=StnLogic.ectOK){
                Logger.w(TAG,"onTaskEnd() ping task network fail, errType=" + errType + ", errCode="+errCode);
            }
            if(mHandler!=null) {
                Message msg = mHandler.obtainMessage();
                msg.what = STNHandler.HANDLER_PING_TASK;
                mHandler.sendMessageDelayed(msg,STNHandler.STN_PING_TASK_TIME_INTERNAL);
            }
            return 0;
        }
        Logger.i(TAG,"onTaskEnd() into, taskID=" + taskID + ", userContext=" + userContext + ", errType="+errType + ", errCode=" +errCode);
        final ITaskWrapper wrapper = TASK_ID_TO_WRAPPER.remove(taskID);
        if (wrapper == null) {
            Logger.w(TAG,"onTaskEnd() stn task onTaskEnd callback may fail, null wrapper, taskID=%d"+taskID);
            return 0;
        }

        try {
            wrapper.onTaskEnd(errType, errCode);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(TAG,"onTaskEnd() error happen");
        }

        return 0;
    }

    /**
     * 流量统计
     * @param send
     * @param recv
     */
    @Override
    public void trafficData(final int send, final int recv){
        Logger.d(TAG,"trafficData() into, send="+send + ", recv=" + recv);
    }

    /**
     * 连接状态通知
     * @param status    综合状态，即长连+短连的状态
     * @param longlinkstatus    仅长连的状态
     */
    @Override
    public void reportConnectInfo(int status, int longlinkstatus){
        if(mLongLinkStatus!=longlinkstatus) {
            Logger.i(TAG,"reportConnectInfo() into, status=" + status + ", longlinkstatus=" + longlinkstatus);
            mLongLinkStatus = longlinkstatus;
        }

        //处理长链接连接和断裂状态
//        for (IPushMessageFilter filter : filters) {
//            try {
//                int cmd = (longlinkstatus==4)?STNManager.PUSH_COMMAND_LONG_LINK_CONNECTED:STNManager.PUSH_COMMAND_LONG_LINK_DISCONNECTED;
//                if (filter.onReceiveMessage(cmd, null)) {
//                    break;
//                }
//            } catch (Exception e) {
//                log("onPush() error happen, filter=" + filter);
//            }
//        }
    }

    /**
     * SDK要求上层生成长链接数据校验包,在长链接连接上之后使用,用于验证SVR身份
     * @param identifyReqBuf    校验包数据内容
     * @param hashCodeBuffer    校验包的HASH
     * @param reqRespCmdID      数据校验的CMD ID
     * @return  ECHECK_NOW(需要校验), ECHECK_NEVER(不校验), ECHECK_NEXT(下一次再询问)
     */
    @Override
    public int getLongLinkIdentifyCheckBuffer(ByteArrayOutputStream identifyReqBuf, ByteArrayOutputStream hashCodeBuffer, int[] reqRespCmdID){
        Logger.d(TAG,"getLongLinkIdentifyCheckBuffer() into");
        return com.tencent.mars.stn.StnLogic.ECHECK_NEVER;
    }

    /**
     * SDK要求上层解连接校验回包.
     * @param buffer            SVR回复的连接校验包
     * @param hashCodeBuffer    CLIENT请求的连接校验包的HASH值
     * @return
     */
    @Override
    public boolean onLongLinkIdentifyResp(final byte[] buffer, final byte[] hashCodeBuffer){
        Logger.d(TAG,"onLongLinkIdentifyResp() into");
        return true;
    }

    /**
     * 请求做sync
     */
    @Override
    public void requestDoSync(){
        Logger.d(TAG,"requestDoSync() into");
    }

    @Override
    public String[] requestNetCheckShortLinkHosts(){
        Logger.d(TAG,"requestNetCheckShortLinkHosts() into");
        return new String[0];
    }

    /**
     * 是否登录
     * @return true 登录 false 未登录
     */
    @Override
    public boolean isLogoned(){
        Logger.d(TAG,"isLogoned() into");
        return true;
    }

    @Override
    public void reportTaskProfile(String taskString){
        Logger.d(TAG,"reportTaskProfile() into, taskString=" + taskString);
    }


    /*************************** 内部私有方法 ***************************
     * */
    private void startSTN(){
        Logger.i(TAG,"startSTN() into");
        // set callback
        AppLogic.setCallBack(this);
        StnLogic.setCallBack(this);
        SdtLogic.setCallBack(this);

        // Initialize the Mars PlatformComm
        Mars.init(getApplicationContext(), new Handler(Looper.getMainLooper()));

        // Initialize the Mars
        StnLogic.setLonglinkSvrAddr(mHostName, new int[]{mLongLinkPort});
        StnLogic.setShortlinkSvrAddr(mShortLinkPort);
        StnLogic.setClientVersion(mVersion);
        Mars.onCreate(true);
        BaseEvent.onForeground(true);

        StnLogic.makesureLongLinkConnected();

        if(mHandler!=null) {
            Message msg = mHandler.obtainMessage();
            msg.what = STNHandler.HANDLER_PING_TASK;
            mHandler.sendMessageDelayed(msg,STNHandler.STN_PING_TASK_TIME_INTERNAL);
        }
    }

    private void stopSTN(){
        Logger.i(TAG,"stopSTN() into");

        Mars.onDestroy();
    }

    private void addTaskWrapper(ITaskWrapper taskWrapper){
        try {
            int taskid = taskWrapper.getTaskID();
            TASK_ID_TO_WRAPPER.put(taskid, taskWrapper);
        }catch (Exception e){

        }
    }

    private void removeTaskWrapper(final int taskID){
        TASK_ID_TO_WRAPPER.remove(taskID);
    }

    private void restoreParam(){
        Logger.d(TAG,"restoreParam() into");
        try {
            mUserID = SPTool.getInstance(this).getLong("STN_SERVICE_CONFIG_USER_ID",0);
            mUserName = SPTool.getInstance(this).getString("STN_SERVICE_CONFIG_USER_NAME","");
            mToken = SPTool.getInstance(this).getString("STN_SERVICE_CONFIG_USER_TOKEN","");
            mDeviceName = SPTool.getInstance(this).getString("STN_SERVICE_CONFIG_DEV_NAME","");
            mDeviceType = SPTool.getInstance(this).getString("STN_SERVICE_CONFIG_DEV_TYPE","");
            mHostName = SPTool.getInstance(this).getString("STN_SERVICE_CONFIG_HOST_NAME","");
            mHostIP = SPTool.getInstance(this).getString("STN_SERVICE_CONFIG_HOST_IP","");
            mLongLinkPort = SPTool.getInstance(this).getInt("STN_SERVICE_CONFIG_LONG_LINK_PORT",0);
            mShortLinkPort = SPTool.getInstance(this).getInt("STN_SERVICE_CONFIG_SHORT_LINK_PORT",0);
            mVersion = SPTool.getInstance(this).getInt("STN_SERVICE_CONFIG_VERSION",0);
            mIsAnchor = SPTool.getInstance(this).getInt("STN_SERVICE_CONFIG_ANCHOR",0);
        }catch (Exception e){
            e.printStackTrace();
            Logger.e(TAG,"restoreParam() restore config error: " + e.toString());
        }
    }

    /* 从 intent 读取参数
    * */
    private boolean readParam(Intent intent){
        Logger.d(TAG,"readParam() into");
        if(intent == null){
            return false;
        }

        mUserID = intent.getLongExtra("userid",0);
        if(mUserID == 0){
            return false;
        }
        mUserName = intent.getStringExtra("username");
        mToken = intent.getStringExtra("usertoken");
        mDeviceName = intent.getStringExtra("devicename");
        mDeviceType = intent.getStringExtra("devicetype");
        mHostName = intent.getStringExtra("hostname");
        mHostIP = intent.getStringExtra("hostip");
        mLongLinkPort = intent.getIntExtra("longlinkport",8101);
        mShortLinkPort = intent.getIntExtra("shortlinkport",8080);
        mVersion = intent.getIntExtra("version",100);
        mIsAnchor = intent.getIntExtra("anchor",0);

        try {
            SPTool.getInstance(this).putLong("STN_SERVICE_CONFIG_USER_ID", mUserID);
            SPTool.getInstance(this).putString("STN_SERVICE_CONFIG_USER_NAME", mUserName);
            SPTool.getInstance(this).putString("STN_SERVICE_CONFIG_USER_TOKEN", mToken);
            SPTool.getInstance(this).putString("STN_SERVICE_CONFIG_DEV_NAME", mDeviceName);
            SPTool.getInstance(this).putString("STN_SERVICE_CONFIG_DEV_TYPE", mDeviceType);
            SPTool.getInstance(this).putString("STN_SERVICE_CONFIG_HOST_NAME", mHostName);
            SPTool.getInstance(this).putString("STN_SERVICE_CONFIG_HOST_IP", mHostIP);
            SPTool.getInstance(this).putInt("STN_SERVICE_CONFIG_LONG_LINK_PORT", mLongLinkPort);
            SPTool.getInstance(this).putInt("STN_SERVICE_CONFIG_SHORT_LINK_PORT", mShortLinkPort);
            SPTool.getInstance(this).putInt("STN_SERVICE_CONFIG_VERSION", mVersion);
            SPTool.getInstance(this).putInt("STN_SERVICE_CONFIG_ANCHOR", mIsAnchor);
        }catch (Exception e){
            e.printStackTrace();
            Logger.e(TAG,"readParam() save config error: " + e.toString());
        }
        return true;
    }


    private boolean isTheSameParam(Intent intent){
        Logger.d(TAG,"isTheSameParam() into");
        if(intent == null){
            return true;
        }
        long userID = intent.getLongExtra("userid",0);
        if(userID!=mUserID){
            Logger.i(TAG,"isTheSameParam() user id not the same");
            return false;
        }

        String userName = intent.getStringExtra("username");
        if(false!=TextUtils.isEmpty(userName) && false==userName.equals(mUserName)){
            Logger.i(TAG,"isTheSameParam() user name not the same");
            return false;
        }

        String token = intent.getStringExtra("usertoken");
        if(false!=TextUtils.isEmpty(token) && false==token.equals(mToken)){
            Logger.i(TAG,"isTheSameParam() user token not the same");
            return false;
        }

        String hostName = intent.getStringExtra("hostname");
        if(false!=TextUtils.isEmpty(hostName) && false==hostName.equals(mHostName)){
            Logger.i(TAG,"isTheSameParam() host name not the same");
            return false;
        }

        String hostIP = intent.getStringExtra("hostip");
        if(false!=TextUtils.isEmpty(hostIP) && false==hostIP.equals(mHostIP)) {
            Logger.i(TAG, "isTheSameParam() host ip not the same");
            return false;
        }


        int longLinkPort = intent.getIntExtra("longlinkport",8101);
        if(longLinkPort!=mLongLinkPort){
            Logger.i(TAG, "isTheSameParam() long port not the same");
            return false;
        }

        int shortLinkPort = intent.getIntExtra("shortlinkport",8080);
        if(shortLinkPort!=mShortLinkPort){
            Logger.i(TAG, "isTheSameParam() short port not the same");
            return false;
        }

        return true;
    }

    private void printParam(){
        if(mIsAnchor==0) {
            Logger.d(TAG, "*** STN service config ***");
            Logger.d(TAG, "user id: " + mUserID);
            Logger.d(TAG, "user name: " + mUserName);
            Logger.d(TAG, "user token: " + mToken);
            Logger.d(TAG, "device name: " + mDeviceName);
            Logger.d(TAG, "device type: " + mDeviceType);
            Logger.d(TAG, "host name: " + mHostName);
            Logger.d(TAG, "host ip: " + mHostIP);
            Logger.d(TAG, "long link port: " + mLongLinkPort);
            Logger.d(TAG, "short link port: " + mShortLinkPort);
            Logger.d(TAG, "version: " + mVersion);
            Logger.d(TAG, "anchor: " + mIsAnchor);
        }else{
            Logger.i(TAG, "*** STN service config ***");
            Logger.i(TAG, "user id: " + mUserID);
            Logger.i(TAG, "user name: " + mUserName);
            Logger.i(TAG, "user token: " + mToken);
            Logger.i(TAG, "device name: " + mDeviceName);
            Logger.i(TAG, "device type: " + mDeviceType);
            Logger.i(TAG, "host name: " + mHostName);
            Logger.i(TAG, "host ip: " + mHostIP);
            Logger.i(TAG, "long link port: " + mLongLinkPort);
            Logger.i(TAG, "short link port: " + mShortLinkPort);
            Logger.i(TAG, "version: " + mVersion);
            Logger.i(TAG, "anchor: " + mIsAnchor);
        }
    }

    private void handleNoPushMessageFilters(final int cmdid, final byte[] data) {
        if(cmdid == Iachat.CMD_ID_PUSH_LOGIN_VIDEO){
            //如果收到登陆消息：重登陆
            try {
                Logger.i(TAG, "收到重登陆推送消息");
                //登陆视频服务
                if(mLoginTask == null) {
                    TaskWrapperManager.getInstance().setAccessToken(mToken);
                    Logger.i(TAG, "发送登陆任务, user=" + mUserID);
                    mLoginTask = new LoginTaskWrapper(null);
                    mLoginTask.getProperties().putLong("uid", mUserID);
                    mLoginTask.getProperties().putInt("authType", Iachat.AUTH_TOKEN);
                }
                Message msg = mHandler.obtainMessage();
                msg.what = STNHandler.HANDLER_START_TASK;
                msg.obj = mLoginTask;
                mHandler.sendMessage(msg);
            }catch (Exception e){
                e.printStackTrace();
                Logger.e(TAG, "处理重登陆推送消息异常");
            }

        }else if(cmdid == Iachat.CMD_ID_PUSH_INVITE_VIDEO){
            try {
                Logger.i(TAG, "收到视频邀请推送消息");
                final Iavchat.PushInviteVideo pushMsg = Iavchat.PushInviteVideo.parseFrom(data);
                if(null!=pushMsg){
                    //保存消息等待UI进程连接时再传递
                    PushMessage msg = new PushMessage(cmdid, data);
                    Logger.i(TAG, "收到视频邀请推送消息, from="+pushMsg.from + ", to=" + pushMsg.to+" roomid=" + pushMsg.roomid);
                    cachePushMessages.add(msg);

                    //如果收到视频会话请求消息：发送状态栏通知
                    sendNotification(pushMsg);
                }else{
                    Logger.e(TAG, "转换视频邀请推送消息异常");
                }
            }catch (Exception e){
                e.printStackTrace();
                Logger.e(TAG, "处理重登陆推送消息异常");
            }
        }else{
            try {
                Logger.w(TAG, "收到其他推送消息：cmdid=" + cmdid);
                //保存消息等待UI进程连接时再传递
                PushMessage msg = new PushMessage(cmdid, data);
                cachePushMessages.add(msg);
            }catch (Exception e){
                e.printStackTrace();
                Logger.e(TAG, "处理重登陆推送消息异常");
            }
        }
    }

    private void sendNotification(final Iavchat.PushInviteVideo pushMsg){
        try {
            NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Intent intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

            Notification notification = null;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                notification = new Notification();
                notification.icon = R.mipmap.icon;
                notification.flags |= Notification.FLAG_AUTO_CANCEL;

                Class<?> classType = Notification.class;
                Method method;
                method = classType.getMethod("setLatestEventInfo", new Class[]{Context.class, CharSequence.class, CharSequence.class, PendingIntent.class});
                method.invoke(notification, new Object[]{mContext, "视频邀请通知", pushMsg.name+ "邀请你视频", contentIntent});
            } else if (Build.VERSION.SDK_INT >= 23) {
                RemoteViews notificationView = new RemoteViews(mContext.getPackageName(), R.layout.notification_videochat);
                notificationView.setOnClickPendingIntent(R.id.notification_container, contentIntent);
                notificationView.setImageViewBitmap(R.id.notification_icon, BitmapFactory.decodeResource(getResources(), R.mipmap.icon));
                notificationView.setTextViewText(R.id.notification_title, "视频邀请通知");
                notificationView.setTextViewText(R.id.notification_content, pushMsg.name+ "邀请你视频");
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String str = sdf.format(new Date());
                notificationView.setTextViewText(R.id.notification_time, str);

                notification = new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.mipmap.icon)
                        .setContent(notificationView)
                        .setAutoCancel(true)
                        .setVibrate(vibratePattern)
                        .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.videochat ))
                        .build();

            } else if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
                RemoteViews notificationView = new RemoteViews(mContext.getPackageName(), R.layout.notification_videochat);
                notificationView.setOnClickPendingIntent(R.id.notification_container, contentIntent);
                notificationView.setImageViewBitmap(R.id.notification_icon, BitmapFactory.decodeResource(getResources(), R.mipmap.icon));
                notificationView.setTextViewText(R.id.notification_title, "视频邀请通知");
                notificationView.setTextViewText(R.id.notification_content, pushMsg.name+ "邀请你视频");
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String str = sdf.format(new Date());
                notificationView.setTextViewText(R.id.notification_time, str);

                notification = new Notification.Builder(mContext)
                        .setSmallIcon(R.mipmap.icon)
                        .setContent(notificationView)
                        .setAutoCancel(true)
                        .setVibrate(vibratePattern)
                        .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.videochat ))
                        .build();

            }
            mNotifyMgr.cancel(99999);
            mNotifyMgr.notify(99999, notification);
            Logger.i(TAG, "提示用户收到视频邀请");
        }catch (Exception ex){
            Logger.e(TAG, "提示视频邀请消息通知异常：" + ex.toString());
        }
    }

    static class SPTool{
        private static SPTool sSPTool = null;

        private SharedPreferences mSharedPreferences = null;

        private SPTool(Context context){
            try {
                mSharedPreferences = context.getSharedPreferences("STNService_SharedPreferences", Context.MODE_PRIVATE);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public static SPTool getInstance(Context context){
            if(sSPTool == null){
                sSPTool = new SPTool(context);
            }
            return sSPTool;
        }

        public void putString( String key , String value ) {
            if(mSharedPreferences!=null) {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString(key, value);
                editor.commit();
            }
        }

        public void putLong( String key , long value )
        {
            if(mSharedPreferences!=null) {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putLong(key, value);
                editor.commit();
            }
        }

        public void putInt( String key , int value ) {
            if(mSharedPreferences!=null) {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putInt(key, value);
                editor.commit();
            }
        }

        public String getString( String key , String defaultValue ) {
            if(mSharedPreferences!=null) {
                return mSharedPreferences.getString(key, defaultValue);
            }
            return "";
        }

        public int getInt( String key , int defaultValue ) {
            if(mSharedPreferences!=null) {
                return mSharedPreferences.getInt(key, defaultValue);
            }
            return 0;
        }

        public long getLong( String key , long defaultValue ) {
            if(mSharedPreferences!=null) {
                return mSharedPreferences.getLong(key, defaultValue);
            }
            return 0;
        }

    }

    //远端的 IPushHandlerFilter 死忙
    static class RemoteBinderDeathRecipient implements IBinder.DeathRecipient{
        private WeakReference<IPushMessageFilter> mFilter;
        private WeakReference<STNService> mService;

        public RemoteBinderDeathRecipient(STNService service, IPushMessageFilter filter){
            mService = new WeakReference<STNService>(service);
            mFilter = new WeakReference<IPushMessageFilter>(filter);
        }

        @Override
        public void binderDied() {
            Logger.i(TAG, "binderDied() filter died");
            IPushMessageFilter filter = mFilter.get();
            if(filter!=null) {
                filter.asBinder().unlinkToDeath(this, 0);

                STNService service = mService.get();
                if(service!=null){
                    Logger.i(TAG, "binderDied() remove a filter");
                    service.filters.remove(filter);
                }
            }
        }
    }
}
