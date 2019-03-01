package net.iaround.im;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.tencent.mars.BaseEvent;
import com.tencent.mars.xlog.Xlog;

import net.iaround.BaseApplication;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.im.aidl.ISTNService;
import net.iaround.im.push.ICacheMessageHandler;
import net.iaround.im.push.IPushMessageHandler;
import net.iaround.im.push.filter.PushMessageFilter;
import net.iaround.im.service.STNService;
import net.iaround.im.task.AbstractTaskWrapper;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.PhoneInfoUtil;
import net.iaround.ui.view.ExpandLayout;
import net.iaround.ui.view.floatwindow.VideoChatFloatWindow;
import net.iaround.utils.DeviceUtils;
import net.iaround.utils.logger.Logger;
import net.iaround.videochat.VideoChatManager;
import net.iaround.videochat.task.TaskWrapperManager;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by liangyuanhuan on 22/08/2017.
 */

public class STNManager {
    public static final String TAG = "STNManager";
    public static String STN_HOST = Config.IM_HOST_NAME;
    public static int LONG_LINK_PORT = Config.IM_HOST_PORT;
    public static int SHORT_LINK_PORT = 8080;
    public static int STN_CLIENT_VERSION = 100;
    public static int PUSH_COMMAND_LONG_LINK_CONNECTED = 100; //长链接状态已连接
    public static int PUSH_COMMAND_LONG_LINK_DISCONNECTED = 101; //长链接状态未连接

    private static STNManager sInstance = null;

    //启动服务的状态机
    public static int SERVICE_START_DEFAULT = 0; //没有启动服务
    public static int SERVICE_START_ING = 1; //启动服务中
    public static int SERVICE_START_DONE = 2; //启动服务完成
    public static int SERVICE_START_FAIL = 3; //启动的服务失败
    public static int SERVICE_STOPPED = 4; //启动的服务被手动停止
    private int mStartState = SERVICE_START_DEFAULT;

    private boolean mForever = false; //是否 一直启动start service
    private ISTNService mService = null;
    private PushMessageFilter mMainPushMessageFilter = new PushMessageFilter(); //处理推送消息的主过滤器
    private ArrayList<AbstractTaskWrapper> mTasks = new ArrayList<AbstractTaskWrapper>();
    private HashMap<Integer,ArrayList<IPushMessageHandler>> mTempCmdHandlerMap = null; //临时存储推送消息处理器
    private ArrayList<ICacheMessageHandler> mTempCacheHandlerMap = null; //临时缓存推送消息处理器

    private boolean mAnchor = false; //是不是主播

    public static STNManager getsInstance(){
        if(null == sInstance){
            synchronized (STNManager.class){
                if(null == sInstance){
                    sInstance = new STNManager();
                }
            }
        }
        return sInstance;
    }

    /*开启服务
    * host IM服务域名或IP
    * port 端口
    * forever IM服务常驻
    * */
    public static void start(String host, int port, boolean forever, boolean anchor){
        Logger.i(TAG,"start() into");
        STN_HOST = host;
        LONG_LINK_PORT = port;

        getsInstance().startInternal(BaseApplication.appContext, forever, anchor);
    }

    public static void checkNeedRestart(){
        //Logger.d(TAG,"checkNeedRestart() into");
        if(SERVICE_START_DEFAULT != getsInstance().mStartState  && SERVICE_STOPPED != getsInstance().mStartState && null == getsInstance().mService){
            Logger.i(TAG,"checkNeedRestart() service null, restart now!");
            getsInstance().startInternal(BaseApplication.appContext, getsInstance().mForever, getsInstance().mAnchor);
        }
    }

    /*关闭服务
   * */
    public static void stop(){
        Logger.i(TAG,"stop() into");
        getsInstance().stopInternal(BaseApplication.appContext);
    }

    /* App 切到后台
    * */
    public static void onAppBackground(){
        Logger.d(TAG,"onAppBackground() into");
        if(null == getsInstance().mService) {
            Logger.d(TAG,"onAppBackground(), service not bound");
            return;
        }

        try {
            getsInstance().mService.onAppBackground();

            //主播显示悬浮窗
            if (VideoChatManager.getInstance().loginUserIsAnchor()) {
                VideoChatFloatWindow.getInstance().init();
                VideoChatFloatWindow.getInstance().show();
            }
        }catch (Exception e){
            e.printStackTrace();
            Logger.e(TAG,"onAppBackground() error happen:" + e.toString());
        }
    }

    /* App 切到前台
    * */
    public static void onAppForeground(){
        Logger.d(TAG,"onAppForeground() into");
        if(null == getsInstance().mService) {
            Logger.d(TAG,"onAppForeground(), service not bound");
            return;
        }
        try {
            getsInstance().mService.onAppForeground();

            //主播不显示悬浮窗
            if (VideoChatManager.getInstance().loginUserIsAnchor()) {
                VideoChatFloatWindow.getInstance().dismiss();
            }
        }catch (Exception e){
            e.printStackTrace();
            Logger.e(TAG,"onAppForeground() error happen:" + e.toString());
        }
    }

    /* 增加消息处理器到主消息过滤器
    * */
    public static void addPushMessageHandler(int cmdId, IPushMessageHandler handler){
        Logger.i(TAG,"addPushMessageHandler() into, cmdId=" + cmdId + ", handler=" + handler);

        if(getsInstance().mMainPushMessageFilter!=null) {
            getsInstance().mMainPushMessageFilter.addPushMessageHandler(cmdId, handler);
        }
    }

    /* 从主消息过滤器移除消息处理器
    * */
    public static void removePushMessageHandler(int cmdId, IPushMessageHandler handler){
        Logger.i(TAG,"removePushMessageHandler() into, cmdId=" + cmdId + ", handler=" + handler);

        if(getsInstance().mMainPushMessageFilter!=null) {
            getsInstance().mMainPushMessageFilter.removePushMessageHandler(cmdId, handler);
        }
    }

    /* 从主消息过滤器移除所有消息处理器
   * */
    public static void removeAllPushMessageHandler(){
        Logger.d(TAG,"removeAllPushMessageHandler() into");

        if(getsInstance().mMainPushMessageFilter!=null) {
            getsInstance().mMainPushMessageFilter.removeAllPushMessageHandler();
        }
    }

     /* 增加缓存消息处理器到主消息过滤器
    * */
    public static void addCacheMessageHandler(ICacheMessageHandler handler){
        Logger.i(TAG,"addCacheMessageHandler() into, handler=" + handler);

        if(getsInstance().mMainPushMessageFilter!=null) {
            getsInstance().mMainPushMessageFilter.addCacheMessageHandler(handler);
        }
    }

    /* 从主消息过滤器移除缓存消息处理器
    * */
    public static void removeCacheMessageHandler(ICacheMessageHandler handler){
        Logger.i(TAG,"removeCacheMessageHandler() into, handler=" + handler);

        if(getsInstance().mMainPushMessageFilter!=null) {
            getsInstance().mMainPushMessageFilter.removeCacheMessageHandler(handler);
        }
    }

    /* 发送任务
   * */
    public static int startTask(AbstractTaskWrapper taskWrapper){
        Logger.d(TAG,"startTask() into");
        if(SERVICE_STOPPED == getsInstance().mStartState){
            Logger.d(TAG,"startTask() service stopped");
            return -1;
        }
        if(SERVICE_START_DONE != getsInstance().mStartState) {
            Logger.d(TAG,"startTask(), service not bound yet");
            for(int i=0;i<getsInstance().mTasks.size();i++){
                if(getsInstance().mTasks.get(i) == taskWrapper){
                    Logger.d(TAG,"startTask(), task already have");
                    return -1;
                }
            }
            getsInstance().mTasks.add(taskWrapper);

            Logger.d(TAG,"startTask(), mStartState="+getsInstance().mStartState);

            if(SERVICE_START_FAIL == getsInstance().mStartState){
                Logger.i(TAG,"startTask(), service start fail");
                //启动服务
                getsInstance().startInternal(BaseApplication.appContext ,getsInstance().mForever, getsInstance().mAnchor);
                return -1;
            }
            //如果服务不存在 再启动服务
            if(false == CommonFunction.isServiceExisted(BaseApplication.appContext,STNService.class.getName())){
                Logger.i(TAG,"startTask(), service not exist");
                //启动服务
                getsInstance().startInternal(BaseApplication.appContext ,getsInstance().mForever, getsInstance().mAnchor);
                return -1;
            }else{
                Logger.d(TAG,"startTask(), service exist");
            }
            return -1;
        }

        if(getsInstance().mService!=null) {
            try {
                int taskid = getsInstance().mService.startTask(taskWrapper);
                return taskid;
            }catch (Exception e){
                e.printStackTrace();
                Logger.e(TAG,"startTask(), start task error happen:" + e.toString());
            }
        }
        return -1;
    }

    /* 停止任务
  * */
    public static void stopTask(int taskId){
        Logger.d(TAG,"stopTask() into");
        if(SERVICE_STOPPED == getsInstance().mStartState){
            Logger.d(TAG,"stopTask() service stopped");
            return ;
        }
        if(null != getsInstance().mService) {
            try {
                getsInstance().mService.stopTask(taskId);
            }catch (Exception e){
                e.printStackTrace();
                Logger.e(TAG,"stopTask(), stop task error happen");
            }
        }else{
            Logger.d(TAG,"stopTask(), service not bound");
        }
        return;
    }

    /* 网络发生变化
    * */
    public static void onNetworkChange(){
        Logger.d(TAG,"onNetworkChange() into");
        if(SERVICE_STOPPED == getsInstance().mStartState){
            Logger.d(TAG,"onNetworkChange() service stopped");
            return ;
        }
        if(null != getsInstance().mService) {
            BaseEvent.onNetworkChange();
        }else{
            Logger.d(TAG,"onNetworkChange(), service not bound");
        }

    }

    private STNManager(){

    }

    private synchronized void startInternal(Context context, boolean forever, boolean anchor){
        Logger.i(TAG,"startInternal() into");
        mAnchor = anchor;

        if(SERVICE_START_ING == mStartState || SERVICE_START_DONE == mStartState) {
            Logger.i(TAG,"service is starting or done, start state=" + mStartState);
            return;
        }

        //启动服务
        try {
            mStartState = SERVICE_START_ING;

            if(forever == true) {
                mForever = forever;

                Logger.i(TAG, "starting STN service");
                Intent startIntent = new Intent(context, STNService.class);
                startIntent.putExtra("userid", loginUserID());
                startIntent.putExtra("username", loginUserName());
                startIntent.putExtra("usertoken", loginUserToken());
                startIntent.putExtra("devicename", deviceName());
                startIntent.putExtra("devicetype", deviceType());
                startIntent.putExtra("hostname", STN_HOST);
                startIntent.putExtra("hostip", STN_HOST);
                startIntent.putExtra("longlinkport", LONG_LINK_PORT);
                startIntent.putExtra("shortlinkport", SHORT_LINK_PORT);
                startIntent.putExtra("version", STN_CLIENT_VERSION);
                startIntent.putExtra("anchor", mAnchor == true ? 1 : 0);

                context.startService(startIntent);
            }

            Logger.i(TAG,"binding STN service");
            Intent intent = new Intent(context, STNService.class);
            if(mForever==false){
                intent.putExtra("userid",loginUserID());
                intent.putExtra("username",loginUserName());
                intent.putExtra("devicename",deviceName());
                intent.putExtra("usertoken",loginUserToken());
                intent.putExtra("devicetype",deviceType());
                intent.putExtra("hostname",STN_HOST);
                intent.putExtra("hostip",STN_HOST);
                intent.putExtra("longlinkport",LONG_LINK_PORT);
                intent.putExtra("shortlinkport", SHORT_LINK_PORT);
                intent.putExtra("version",STN_CLIENT_VERSION);
                intent.putExtra("anchor",mAnchor==true?1:0);
            }
            context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }catch (Exception e){
            Logger.e(TAG,"start STN service fail");
            e.printStackTrace();
            mStartState = SERVICE_START_FAIL;
        }
    }

    private synchronized void stopInternal(Context context){
        Logger.i(TAG,"stopInternal() into");
        if(SERVICE_START_DEFAULT == mStartState || SERVICE_STOPPED == mStartState) {
            Logger.d(TAG,"service not start yet or stopped, mStartState="+mStartState);
            return;
        }
        try {
            Logger.i(TAG,"unbinding STN service");
            context.unbindService(mConnection);

            if(mForever == true) {
                mForever = false;
                Logger.i(TAG,"stopping STN service");
                Intent stopIntent = new Intent(context, STNService.class);
                context.stopService(stopIntent);
            }
            mTasks.clear();
            mService.unregisterPushMessageFilter(mMainPushMessageFilter);
            mMainPushMessageFilter.destroy();
            if(mTempCmdHandlerMap!=null){
                mTempCmdHandlerMap.clear();
                mTempCmdHandlerMap = null;
            }
            if(mTempCacheHandlerMap!=null){
                mTempCacheHandlerMap.clear();
                mTempCacheHandlerMap = null;
            }
            mService = null;

            mStartState = SERVICE_STOPPED;
            mAnchor = false;

            Logger.flush(false); //刷新缓存日志到文件中
        }catch (Exception e){
            Logger.e(TAG,"stop STN service fail");
            e.printStackTrace();
            mStartState = SERVICE_STOPPED;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                Logger.i(TAG,"service onServiceConnected() into");
                mService = ISTNService.Stub.asInterface(service);

                mMainPushMessageFilter.create();
                if(mTempCmdHandlerMap!=null){
                    Logger.i(TAG,"service copy temp push cmd handler, size="+mTempCmdHandlerMap.size());
                    mMainPushMessageFilter.copyFromPushMessageHandler(mTempCmdHandlerMap);
                    mTempCmdHandlerMap.clear();
                    mTempCmdHandlerMap = null;
                }
                if(mTempCacheHandlerMap!=null){
                    Logger.i(TAG,"service copy temp push cache cmd handler, size="+mTempCacheHandlerMap.size());
                    mMainPushMessageFilter.copyFromCacheMessageHandler(mTempCacheHandlerMap);
                    mTempCacheHandlerMap.clear();
                    mTempCacheHandlerMap = null;
                }
                mService.registerPushMessageFilter(mMainPushMessageFilter);
                mStartState = SERVICE_START_DONE;
                for(int i=0;i<mTasks.size();i++){
                    Logger.i(TAG,"service start hang task, task id="+mTasks.get(i).getTaskID());
                    mService.startTask(mTasks.get(i));
                }
                mTasks.clear();
                if(mService!=null) {
                    Logger.i(TAG, "service connected");
                }else{
                    Logger.i(TAG, "service connected, but null!");
                }
            }catch (Exception e){
                e.printStackTrace();
                Logger.e(TAG,"service connected fail, exception=" + e.toString());
                mService = null;
                mStartState = SERVICE_START_FAIL;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logger.i(TAG,"service onServiceDisconnected() into" );
            try {
                mTempCmdHandlerMap = new HashMap<Integer, ArrayList<IPushMessageHandler>>(0);
                mMainPushMessageFilter.copyToPushMessageHandler(mTempCmdHandlerMap);
                Logger.i(TAG, "copy temp push cmd handler, size=" + mTempCmdHandlerMap.size());

                mTempCacheHandlerMap = new ArrayList<ICacheMessageHandler>(0);
                mMainPushMessageFilter.copyToCacheMessageHandler(mTempCacheHandlerMap);
                Logger.i(TAG, "copy temp push cache cmd handler, size=" + mTempCacheHandlerMap.size());
                try {
                    mService.unregisterPushMessageFilter(mMainPushMessageFilter);
                }catch (Exception ex){
                    ex.printStackTrace();
                    Logger.i(TAG, "service unregister push message filter fail, error:" + ex.toString());
                }
                mMainPushMessageFilter.destroy();
                mService = null;
                mStartState = SERVICE_START_FAIL;
                Logger.i(TAG, "service disconnected");

                if(mForever==true) {
                    Logger.i(TAG, "restart service now");
                    checkNeedRestart();
                }
            }catch (Exception e){
                e.printStackTrace();
                Logger.e(TAG,"service disconnected fail, exception=" + e.toString());
            }
        }

    };

    /*获得用户名
    * */
    public String loginUserName(){
        if(Common.getInstance().loginUser!=null) {
            return Common.getInstance().loginUser.getNickname();
        }
        return "";
    }

    /*获得用户ID
    * */
    public long loginUserID(){
        if(Common.getInstance().loginUser!=null) {
            return Common.getInstance().loginUser.getUid();
        }
        return 0;
    }

    public String loginUserToken(){
        return TaskWrapperManager.getInstance().getAccessToken();
    }


    /*设备ID
   * */
    public String deviceName(){
        return PhoneInfoUtil.getInstance(BaseApplication.appContext).getDeviceId();
    }

    /*设备类型
    * */
    public String deviceType(){
        return PhoneInfoUtil.getInstance(BaseApplication.appContext).getModel();
    }


}
