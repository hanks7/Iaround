package net.iaround.ui.view.floatwindow;


import android.view.LayoutInflater;


import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.MessageID;
import net.iaround.connector.CallBackNetwork;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.protocol.SocketGroupProtocol;
import net.iaround.floatwindow.FloatWindowView;
import net.iaround.floatwindow.VideoChatFloatWindowHelper;
import net.iaround.mic.FlyAudioRoom;
import net.iaround.model.im.GroupChatMicUserMessage;
import net.iaround.model.im.TransportMessage;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.activity.BaseActivity;
import net.iaround.ui.activity.BaseFragmentActivity;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.chat.ChatPersonal;
import net.iaround.ui.datamodel.GroupModel;
import net.iaround.ui.group.activity.GroupChatTopicActivity;
import net.iaround.ui.group.activity.GroupInfoActivity;
import net.iaround.ui.view.CircleImageView;

import java.lang.ref.WeakReference;

/**
 * Class: 视频会话最小化窗口
 */
public class VideoChatFloatWindow {
    private static final String TAG = "VideoChatFloatWindow";
    private static VideoChatFloatWindow sInstance = null;

    private boolean mInitView = false;
    private FloatWindowView mView = null;
    private boolean mShowing = false; //是否正在显示悬浮窗

    /*
    * context
    * */
    public static VideoChatFloatWindow getInstance() {
        if (sInstance == null) {
            synchronized (VideoChatFloatWindow.class) {
                if(sInstance == null) {
                    sInstance = new VideoChatFloatWindow();
                }
            }
        }
        return sInstance;
    }

    private VideoChatFloatWindow() {

    }

    public synchronized void init(){
        CommonFunction.log(TAG,"init() into");
        if(true == mInitView){
            CommonFunction.log(TAG,"init() already init");
            return;
        }
        mView = (FloatWindowView) LayoutInflater.from(BaseApplication.appContext).inflate(R.layout.view_videochat_floatwindow, null);

        mInitView = true;

        CommonFunction.log(TAG,"init() out");
    }

    /**
     * 显示最小化窗口
     */
    public synchronized void show(){
        CommonFunction.log(TAG,"show() into");
        if(mShowing == true){
            CommonFunction.log(TAG,"show() showing...");
            return;
        }
        mShowing = true;

        if(null!=mView) {
            VideoChatFloatWindowHelper.getInstance().createWindow(mView);
        }

        CommonFunction.log(TAG,"show() out");
    }

    /* 关闭最小化窗口但不销毁销毁view
    * group 当前进入聊吧的聊吧ID
    * */
    public synchronized boolean dismiss(){
        CommonFunction.log(TAG,"dismiss() into");

        //隐藏悬浮窗
        removeWindow();

        return true;
    }

    /*打开最小化聊吧
    * */
    public synchronized void open(){
        CommonFunction.log(TAG,"open() into");

        //隐藏悬浮窗
        removeWindow();

        CommonFunction.log(TAG,"open() out");
    }

    /* 关闭关闭最小化窗口且销毁view
    * */
    public synchronized void close(){
        CommonFunction.log(TAG,"close() into");

        //隐藏悬浮窗
        removeWindow();

        //销毁自己的资源
        destroy();

        CommonFunction.log(TAG,"close() out");
    }


    public synchronized FloatWindowView getView(){
        return mView;
    }

    /**
     * 隐藏最小化窗口
     */
    private void removeWindow(){
        CommonFunction.log("ChatBarZoomWindow","hide() into");
        if(mShowing == false){
            return;
        }
        mShowing = false;

        if(null!=mView) {
            VideoChatFloatWindowHelper.getInstance().removeWindow(mView);
        }
    }

    /*销毁view
    * */
    private void destroy(){
        if(null!=mView)
            mView.setOnClickListener(null);
        mView = null;
        mShowing = false;
        mInitView = false;
    }

}
