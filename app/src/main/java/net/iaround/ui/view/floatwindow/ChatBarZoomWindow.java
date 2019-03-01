package net.iaround.ui.view.floatwindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.MessageID;
import net.iaround.connector.CallBackNetwork;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.protocol.SocketGroupProtocol;
import net.iaround.floatwindow.ChatBarZoomWindowHelper;
import net.iaround.floatwindow.FloatWindowView;
import net.iaround.floatwindow.WindowManagerHelper;
import net.iaround.mic.FlyAudioRoom;
import net.iaround.model.im.GroupChatMicUserMessage;
import net.iaround.model.im.GroupUser;
import net.iaround.model.im.TransportMessage;
import net.iaround.model.skill.SkillAttackResult;
import net.iaround.service.NetChangeObserver;
import net.iaround.service.NetworkChangedReceiver;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.PermissionUtils;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.activity.BaseActivity;
import net.iaround.ui.activity.BaseFragmentActivity;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.chat.ChatPersonal;
import net.iaround.ui.datamodel.GroupChatEntrySuccess;
import net.iaround.ui.datamodel.GroupModel;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.group.activity.GroupChatTopicActivity;
import net.iaround.ui.group.activity.GroupInfoActivity;
import net.iaround.ui.view.CircleImageView;
import net.iaround.ui.view.HeadPhotoView;

import org.w3c.dom.Text;

import java.lang.ref.WeakReference;

/**
 * Class: 聊吧最小化窗口
 */
public class ChatBarZoomWindow implements CallBackNetwork {
    private static final String TAG = "ChatBarZoomWindow";
    private static ChatBarZoomWindow sInstance = null;

    private boolean mInitView = false;
    private FloatWindowView mView = null;
    private CircleImageView mChatBarView = null;
    private FrameLayout mCloseChatBarView = null;
    private TextView mTitle = null;
    private TextView mHot = null;
    private ImageView mWave = null;
    private boolean mShowing = false; //是否正在显示悬浮窗
    private String mChatBarID = null; //聊吧ID
    private boolean mInChatbar = false;  //是否还在聊吧（用于发送退出聊吧消息）\
    //    private PermissionUtils.PermissionGrant mPermissionGrant = null;
    private boolean mKickoffChatbar = false;  //是否被踢出聊吧
    private boolean mDissolveChatbar = false;  //是否解散聊吧
    private ChatbarZoomWindowMessageHandler mHandler = null;
    private boolean mNeedResumePublishing = false; //来电断了之后是否需要恢复推流
    private boolean mOnMicRequset = false; //上麦申请

    /*
     * context
     * */
    public static ChatBarZoomWindow getInstance() {
        if (sInstance == null) {
            synchronized (ChatBarZoomWindow.class) {
                if (sInstance == null) {
                    sInstance = new ChatBarZoomWindow();
                }
            }
        }
        return sInstance;
    }

    private ChatBarZoomWindow() {

    }

    public synchronized void init() {
        CommonFunction.log(TAG, "init() into");
        if (true == mInitView) {
            CommonFunction.log(TAG, "init() already init");
            return;
        }
        mView = (FloatWindowView) LayoutInflater.from(BaseApplication.appContext).inflate(R.layout.view_charbar_zoom, null);

        mChatBarView = (CircleImageView) mView.findViewById(R.id.zoom_chatbar_image);
        mCloseChatBarView = (FrameLayout) mView.findViewById(R.id.zoom_chatbar_close);
        mWave = (ImageView) mView.findViewById(R.id.zoom_chatbar_wave);
        mWave.setImageResource(R.drawable.chatbar_zoom_wave);
        mTitle = (TextView) mView.findViewById(R.id.zoom_chatbar_title);
        mHot = (TextView) mView.findViewById(R.id.zoom_chatbar_hot);

        //跳回聊吧页面
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open();
            }
        });

        //关闭悬浮窗口
        mCloseChatBarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        mInitView = true;
//        mPermissionGrant = new ChatbarZoomPermissionGrant(this);
        mHandler = new ChatbarZoomWindowMessageHandler(this);

        //开启广播去监听 网络 改变事件
        NetworkChangedReceiver.addObserver(mNetChangeObserver);
        CommonFunction.log(TAG, "init() out");
    }

    /**
     * 显示最小化窗口
     */
    public synchronized void show(String charbarID, Bitmap bitmap, String title, String hot) {
        CommonFunction.log(TAG, "show() into, charbarID=" + charbarID);
        if (charbarID == null) {
            CommonFunction.log(TAG, "show() charbarID null");
            return;
        }
        if (mShowing == true) {
            CommonFunction.log(TAG, "show() showing...");
            return;
        }
        mShowing = true;
        mChatBarID = charbarID;
        mInChatbar = true;
        if (null != mChatBarView && bitmap != null) {
            mChatBarView.setImageBitmap(bitmap);
        }
        if (mWave != null) {
            AnimationDrawable wave = (AnimationDrawable) mWave.getDrawable();
            if (null != wave) wave.start();
        }
        if (mTitle != null && title != null) {
            mTitle.setText(title);
        }
        if (mHot != null && hot != null) {
            mHot.setText(hot);
        }
        if (null != mView) {
            ChatBarZoomWindowHelper.getInstance().createWindow(mView);
        }

        //接受上下麦消息
        ConnectorManage.getInstance(BaseApplication.appContext).setChatbarZoomWindowCallBack(this);

        CommonFunction.log(TAG, "show() out");
    }

    /* 关闭最小化窗口但不销毁销毁view
     * group 当前进入聊吧的聊吧ID
     * */
    public synchronized boolean dismiss(String group) {
        CommonFunction.log(TAG, "dismiss() into, new chatbar=" + group + ", old chatbar=" + mChatBarID);
        if ((group == null && mChatBarID != null) || (group != null && mChatBarID == null) || (group != null && mChatBarID != null && false == group.equals(mChatBarID))) {
            CommonFunction.log(TAG, "dismiss() release chatbar");
            releaseChatbar();
        }

        boolean thesame = false;
        if (group != null && mChatBarID != null && true == group.equals(mChatBarID)) {
            thesame = true;
        }

        //隐藏悬浮窗
        removeWindow();

        if (mWave != null) {
            AnimationDrawable wave = (AnimationDrawable) mWave.getDrawable();
            if (null != wave) wave.stop();
        }

        mChatBarID = null;
        mInChatbar = false;
        mKickoffChatbar = false;
        mDissolveChatbar = false;
        mNeedResumePublishing = false;
        mOnMicRequset = false;

        //接受SOCKET上下麦消息
        ConnectorManage.getInstance(BaseApplication.appContext).setChatbarZoomWindowCallBack(null);

        CommonFunction.log(TAG, "dismiss() out=" + thesame);

        return thesame;
    }

    /*打开最小化聊吧
     * */
    public synchronized void open() {
        CommonFunction.log(TAG, "open() into");
        String chatbar = mChatBarID;
        if (mDissolveChatbar == true) {
            CommonFunction.log(TAG, "open() chatbar cannot goto");
            return;
        }

        //隐藏悬浮窗
        removeWindow();

        if (null == chatbar) {
            return;
        }

        if (mWave != null) {
            AnimationDrawable wave = (AnimationDrawable) mWave.getDrawable();
            if (null != wave) wave.stop();
        }

        //跳去聊吧页面
        Activity activity = CloseAllActivity.getInstance().getTopActivity();
        Intent intent = new Intent(activity, GroupChatTopicActivity.class);
        intent.putExtra("fromchatbarwindowzoom", 1);
        intent.putExtra("id", chatbar);
        intent.putExtra("isChat", true);
        activity.startActivity(intent);
        CommonFunction.log(TAG, "open() out");
    }

    /* 关闭关闭最小化窗口且销毁view
     * */
    public synchronized void close() {
        CommonFunction.log(TAG, "close() into");

        //销毁聊吧资源
        releaseChatbar();

        //隐藏悬浮窗
        removeWindow();

        //销毁自己的资源
        destroy();

        CommonFunction.log(TAG, "close() out");
    }


    public synchronized FloatWindowView getView() {
        if (mChatBarID != null) {
            return mView;
        }
        return null;
    }

    /*处理被踢出聊吧消息事件
     * 发送退出聊吧消息，停止拉流推流
     * */
    public synchronized void onKickOffChatbar(String groupid) {
        CommonFunction.log("ChatBarZoomWindow", "onKickOffChatbar() into,groupid=" + groupid + ", mChatBarID=" + mChatBarID);
        if (groupid != null && mChatBarID != null && groupid.equals(mChatBarID)) {
            mKickoffChatbar = true;

            releaseChatbar();

            Common.groupKickDisbandedMap.remove(mChatBarID);
            GroupModel.getInstance().isNeedRefreshGroupList = true;
            GroupModel.getInstance().removeGroupAndAllMessage(BaseApplication.appContext, String.valueOf(Common.getInstance().loginUser.getUid()), groupid);

            //显示被踢出聊吧图标
            CommonFunction.log("ChatBarZoomWindow", "onKickOffChatbar() showing kickoff...");
        }
    }

    /*处理聊吧解散消息事件
     * 发送退出聊吧消息，停止拉流推流
     * */
    public synchronized void onDissolveChatbar(String groupid) {
        CommonFunction.log("ChatBarZoomWindow", "onDissolveChatbar() into");
        if (groupid != null && mChatBarID != null && groupid.equals(mChatBarID)) {
            mDissolveChatbar = true;

            releaseChatbar();

            Common.groupKickDisbandedMap.remove(mChatBarID);
            GroupModel.getInstance().isNeedRefreshGroupList = true;
            GroupModel.getInstance().removeGroupAndAllMessage(BaseApplication.appContext, String.valueOf(Common.getInstance().loginUser.getUid()), groupid);

            //显示聊吧解散图标
            CommonFunction.log("ChatBarZoomWindow", "onKickOffChatbar() showing dissolve...");
        }
    }

    /*处理上麦消息事件
     * 开始推流
     * */
    public synchronized void onOnMic(GroupChatMicUserMessage message) {
        CommonFunction.log("ChatBarZoomWindow", "onOnMic() into");
        if (message == null) {
            return;
        }
        String group = String.valueOf(message.groupid);
        if (group != null && mChatBarID != null && group.equals(mChatBarID)) {
            FlyAudioRoom flyAudioRoom = FlyAudioRoom.getInstance();
            if (flyAudioRoom != null) {
                flyAudioRoom.setManageId(message.managerid);
                flyAudioRoom.setPosition(message.slot);
            }
            mOnMicRequset = true;
            //没有录音权限需要申请
            startPush();
        }
    }

    /*处理下麦消息事件
     * 停止推流
     * */
    public synchronized void onOffMic(GroupChatMicUserMessage message) {
        CommonFunction.log("ChatBarZoomWindow", "onOffMic() into");
        if (message == null) {
            return;
        }
        String group = String.valueOf(message.groupid);
        if (group != null && mChatBarID != null && group.equals(mChatBarID)) {
            stopPush();
            SocketGroupProtocol.offMicSuccess(BaseApplication.appContext, group, message.managerid);
            CommonFunction.toastMsg(BaseApplication.appContext, BaseApplication.appContext.getString(R.string.group_chat_manage_off_mic_success));
        }
    }

    /*处理来电接听
     * */
    public synchronized void onCallStateRinging() {
        if (mShowing == true && mHandler != null) {
            Message msg = mHandler.obtainMessage(ChatbarZoomWindowMessageHandler.CALL_RINGING);
            mHandler.sendMessage(msg);
        }
    }

    /*处理来电挂断
     * */
    public synchronized void onCallStateIdle() {
        if (mShowing == true && mHandler != null) {
            Message msg = mHandler.obtainMessage(ChatbarZoomWindowMessageHandler.CALL_HANGUP);
            mHandler.sendMessage(msg);
        }
    }

    /**
     * 隐藏最小化窗口
     */
    private void removeWindow() {
        CommonFunction.log("ChatBarZoomWindow", "hide() into");
        if (mShowing == false) {
            return;
        }
        mShowing = false;

        if (null != mView) {
            ChatBarZoomWindowHelper.getInstance().removeWindow(mView);
        }
        if (mNetChangeObserver != null) {
            NetworkChangedReceiver.removeRegisterObserver(mNetChangeObserver);
        }
    }

    /*销毁view
     * */
    private void destroy() {
        mChatBarView = null;
        if (mCloseChatBarView != null) {
            mCloseChatBarView.setOnClickListener(null);
        }
        mCloseChatBarView = null;
        if (null != mView)
            mView.setOnClickListener(null);
        mView = null;
        mWave = null;
        mTitle = null;
        mHot = null;
        mChatBarID = null;
        mShowing = false;
        mInitView = false;
        mInChatbar = false;
//        mPermissionGrant = null;
        mKickoffChatbar = false;
        mDissolveChatbar = false;
        mHandler = null;
        mNeedResumePublishing = false;
        mOnMicRequset = false;
    }

    /*开始推流
     * */
    public void startPush() {
        requestMicPermission();
    }

    /*停止推流
     * */
    public void stopPush() {
        final FlyAudioRoom flyAudioRoom = FlyAudioRoom.getInstance();
        if (flyAudioRoom != null) {
            flyAudioRoom.stopPublish();
        }
    }


    /* 是否正在推流
     * */
    public synchronized boolean isPublishing() {
        if (mShowing == true) {
            final FlyAudioRoom flyAudioRoom = FlyAudioRoom.getInstance();
            if (flyAudioRoom != null && flyAudioRoom.isPublishing()) {
                return true;
            }
        }

        return false;
    }

    /**
     * 是否正在显示聊吧最小化窗口
     *
     * @return
     */
    public synchronized boolean isShowing() {
        return mShowing;
    }

    /**
     * 获取麦克风
     */
    private void requestMicPermission() {
        Activity activity = CloseAllActivity.getInstance().getTopActivity();
        if (null != activity) {
            if (activity instanceof BaseFragmentActivity) {
                ((BaseFragmentActivity) activity).requestMicshowPermissions();
            } else if (activity instanceof BaseActivity) {
                ((BaseActivity) activity).requestMicshowPermissions();
            } else if (activity instanceof ChatPersonal) {
                ((ChatPersonal) activity).requestMicshowPermissions();
            } else if (activity instanceof GroupInfoActivity) {
                ((GroupInfoActivity) activity).requestMicshowPermissions();
            }
        }
    }

    /*来电接听处理
     * */
    private void handleCallRinging() {
        final FlyAudioRoom flyAudioRoom = FlyAudioRoom.getInstance();
        if (flyAudioRoom != null) {
            flyAudioRoom.handleMicState(false);
            flyAudioRoom.handleMuteState1(false);
            if (flyAudioRoom.isPublishing()) {
                mNeedResumePublishing = true;
                flyAudioRoom.stopPublish();
            }
        }
    }

    /*来电挂断处理
     * */
    private void handleCallHangup() {
        final FlyAudioRoom flyAudioRoom = FlyAudioRoom.getInstance();
        if (flyAudioRoom != null) {
            flyAudioRoom.handleMicState(true);
            flyAudioRoom.handleMuteState1(true);
            if (mNeedResumePublishing == true) {
                mNeedResumePublishing = false;
                flyAudioRoom.resumePublish();
            }
        }
    }

    /*申请录音权限成功
     * */
    public void onRequestMicPermissionSuccess() {
        CommonFunction.log("ChatBarZoomWindow", "onRequestMicPermissionSuccess() into");
        final long currentUserid = Common.getInstance().loginUser.getUid();
        String url = "" + currentUserid;
        final String chatbarid = mChatBarID;
        if (chatbarid == null || currentUserid == 0 || currentUserid == -1) {
            CommonFunction.log("ChatBarZoomWindow", "onRequestMicPermissionSuccess() param null, chatbarid=" + chatbarid + ", currentUserid=" + currentUserid);
            return;
        }
        final FlyAudioRoom flyAudioRoom = FlyAudioRoom.getInstance();
        if (null != flyAudioRoom) {
            flyAudioRoom.setiFlyMediaCallback(new FlyAudioRoom.IFlyMediaCallback() {
                @Override
                public void engineSuccess() {
                    SharedPreferenceUtil.getInstance(BaseApplication.appContext).putBoolean(Common.getInstance().loginUser.getUid() + "/" + SharedPreferenceUtil.SYSTEM_MIC, true);
                    SocketGroupProtocol.onMicSuccess(BaseApplication.appContext, chatbarid, flyAudioRoom.getManageId(), flyAudioRoom.getPosition(), flyAudioRoom.getPushflows());
                }

                @Override
                public void engineError() {
                    if (currentUserid == flyAudioRoom.getManageId()) {
                        CommonFunction.toastMsg(BaseApplication.appContext, BaseApplication.appContext.getString(R.string.se_10073035));
                    }
                    SharedPreferenceUtil.getInstance(BaseApplication.appContext).putBoolean(Common.getInstance().loginUser.getUid() + "/" + SharedPreferenceUtil.SYSTEM_MIC, false);
                    SocketGroupProtocol.onMicError(BaseApplication.appContext, chatbarid, flyAudioRoom.getManageId(), flyAudioRoom.getPosition());
                }
            });
            flyAudioRoom.startPublish(url, mChatBarID);
            if (mOnMicRequset == true) {
                CommonFunction.toastMsg(BaseApplication.appContext, BaseApplication.appContext.getString(R.string.group_chat_manage_on_mic_success));
                mOnMicRequset = false;
            }
        }
    }


    private void releaseChatbar() {
        CommonFunction.log("ChatBarZoomWindow", "releaseChatbar() into");
        if (true == mInChatbar) {
            mInChatbar = false;
            SocketGroupProtocol.groupLeave(BaseApplication.appContext, mChatBarID, Common.getInstance().loginUser.getUid());
        }
        Common.getInstance().setmCurrentGroupId(0);
        GroupModel.getInstance().setInGroupChat(false);
        GroupModel.getInstance().releaseBuffer();// 释放群组的缓存
        SharedPreferenceUtil.getInstance(BaseApplication.appContext).putBoolean(Common.getInstance().loginUser.getUid() + "/" + SharedPreferenceUtil.SYSTEM_MIC, false);

        //关闭声音
        FlyAudioRoom flyAudioRoom = FlyAudioRoom.getInstance();
        flyAudioRoom.clearAudio();

        //接受上下麦消息
        ConnectorManage.getInstance(BaseApplication.appContext).setChatbarZoomWindowCallBack(null);
    }

    /*SOCKET 消息回调开始
     * */
    @Override
    public void onReceiveMessage(TransportMessage message) {
        switch (message.getMethodId()) {
            case MessageID.SESSION_GROUP_PREPAREONMI_RESULT://用户被抱上麦
            {
                String json = message.getContentBody();
                GroupChatMicUserMessage bean = GsonUtil.getInstance().getServerBean(json, GroupChatMicUserMessage.class);
                if (bean != null) {
                    String groupIdStr = String.valueOf(bean.groupid);
                    if (groupIdStr != null && mChatBarID != null && groupIdStr.equals(mChatBarID)) {
                        if (null != mHandler) {
                            Message msg = mHandler.obtainMessage(ChatbarZoomWindowMessageHandler.CURRENT_USER_ON_MIC, bean);
                            mHandler.sendMessage(msg);
                        }
                    }
                }
            }
            break;
            case MessageID.SESSION_GROUP_PREPARE_OFFMIC_RESULT://用户被抱下麦
            {
                String json = message.getContentBody();
                GroupChatMicUserMessage bean = GsonUtil.getInstance().getServerBean(json, GroupChatMicUserMessage.class);
                if (null != bean) {
                    String groupIdStr = String.valueOf(bean.groupid);
                    if (groupIdStr != null && mChatBarID != null && groupIdStr.equals(mChatBarID)) {
                        if (null != mHandler) {
                            Message msg = mHandler.obtainMessage(ChatbarZoomWindowMessageHandler.CURRENT_USER_OFF_MIC, bean);
                            mHandler.sendMessage(msg);
                        }
                    }
                }
            }
            break;
            case MessageID.GROUP_COME_IN_Y:// 进入群成功
                receiveJoinInSuccess(message);
                break;
        }
    }

    /**
     * 进入群成功
     *
     * @param message
     */
    private void receiveJoinInSuccess(TransportMessage message) {
        String json = message.getContentBody();
        CommonFunction.log(TAG, "receiveJoinInSuccess() join success***" + json);
        GroupChatEntrySuccess bean = GsonUtil.getInstance().getServerBean(json, GroupChatEntrySuccess.class);

        Message msg = mHandler.obtainMessage(ChatbarZoomWindowMessageHandler.MSG_JOIN_IN_SUCCESS, bean);
        mHandler.sendMessage(msg);
    }

    @Override
    public void onSendCallBack(int e, long flag) {

    }

    @Override
    public void onConnected() {

    }
    //SOCKET 消息回调结束

    public static class ChatbarZoomWindowMessageHandler extends Handler {
        public static final int CURRENT_USER_ON_MIC = 1000;
        public static final int CURRENT_USER_OFF_MIC = 1001;
        public static final int CALL_RINGING = 1002;
        public static final int CALL_HANGUP = 1003;
        public static final int MSG_JOIN_IN_SUCCESS = 1004;//进群成功

        private WeakReference<ChatBarZoomWindow> mChatBarZoomWindow = null;

        public ChatbarZoomWindowMessageHandler(ChatBarZoomWindow chatBarZoomWindow) {
            mChatBarZoomWindow = new WeakReference<ChatBarZoomWindow>(chatBarZoomWindow);
        }

        @Override
        public void handleMessage(Message msg) {
            ChatBarZoomWindow chatBarZoomWindow = mChatBarZoomWindow.get();
            if (chatBarZoomWindow == null) {
                return;
            }
            switch (msg.what) {
                case CURRENT_USER_ON_MIC: //上麦
                {
                    GroupChatMicUserMessage bean = (GroupChatMicUserMessage) msg.obj;
                    chatBarZoomWindow.onOnMic(bean);
                }
                break;
                case CURRENT_USER_OFF_MIC: //下麦
                {
                    GroupChatMicUserMessage bean = (GroupChatMicUserMessage) msg.obj;
                    chatBarZoomWindow.onOffMic(bean);
                }
                break;
                case CALL_RINGING: {
                    chatBarZoomWindow.handleCallRinging();
                }
                break;
                case CALL_HANGUP: {
                    chatBarZoomWindow.handleCallHangup();
                }
                break;
                case MSG_JOIN_IN_SUCCESS: {
                    GroupChatEntrySuccess bean = (GroupChatEntrySuccess) msg.obj;

                    long userId = Common.getInstance().loginUser.getUid();
                    CommonFunction.log(TAG, "join in success");


                    //麦上用户
                    GroupUser micUser1 = bean.micuserid;
                    //麦上用户
                    GroupUser micUser2 = bean.micuserid2;

                    if ((micUser1 != null && micUser1.getUserid() == userId) || (micUser2 != null && micUser2.getUserid() == userId)) {
                        chatBarZoomWindow.startPush();
                    }


                    if ((micUser1 == null || micUser1.getUserid() != userId) && (micUser2 == null || micUser2.getUserid() != userId)) {
                        CommonFunction.log(TAG, "自己不在麦上，停止推流");
                        chatBarZoomWindow.stopPush();
                    }
                }
                break;
            }

        }
    }

    /**
     * 网络观察者
     */
    protected final NetChangeObserver mNetChangeObserver = new NetChangeObserver() {
        @Override
        public void onNetConnected(CommonFunction.NetType type) {
            if (type == CommonFunction.NetType.WIFI) {
                CommonFunction.log(TAG, "wifi网络已连接");
            } else if (type == CommonFunction.NetType.TYPE_MOBILE) {
                CommonFunction.log(TAG, "移动网络已连接");
            }
//            callStateIdle();
//            stopPush();
        }

        @Override
        public void onNetDisConnect() {
            CommonFunction.log(TAG, "网络已经断开");
            stopPush();
        }
    };

}
