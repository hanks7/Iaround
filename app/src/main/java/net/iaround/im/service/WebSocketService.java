package net.iaround.im.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.floatwindow.AudioChatFloatWindowHelper;
import net.iaround.mic.AudioChatManager;
import net.iaround.conf.Common;
import net.iaround.im.IWebSocketListener;
import net.iaround.im.WebSocketManager;
import net.iaround.model.audiochat.AudioChatBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.activity.AudioChatActivity;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.group.activity.GroupChatTopicActivity;
import net.iaround.ui.view.floatwindow.ChatBarZoomWindow;
import net.iaround.utils.logger.Logger;


import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Response;
import okhttp3.WebSocket;

/**
 * WebSocket服务
 */
public class WebSocketService extends Service implements IWebSocketListener {
    private static final String TAG = "WebSocketService";
    private Handler mHandler;

    private AudioChatServiceListener mAudioChatServiceListener;

    private static final int NO_RESPONSE_TIMER_INTERVAL = 1000; //无应答定时器间隔 1秒
    private static final int NO_RESPONSE_NOTIFY_SECONDS = 30000; //无应答提示 30秒
    private static final int NO_RESPONSE_CLOSE_VIDEO_CHAT_SECONDS = 60000; //无应答结束视频会话 60秒
    private static final int PAY_TIMER_INTERVAL = 60000; //扣费检测定时器（主播用）60s
    private static final int PAY_SUCCESS_TIME_INTERVAL = 15000; //15s 主播没有接收到扣费消息 断掉聊天
    private static final int PLAY_SUCCESS_INTERVAL = 15000; //15s 没有接收到双方拉流成功消息 断掉聊天
    private static final int RECONNECT_INTERVAL = 3000; //重连间隔 3s
    private static final int HEADER_TIME_INTERVAL = 3000; //主播WebSocket连接心跳间隔 3s
    //对方无应答提示
    private Timer mNoResponseTimer = null; //对方无应答20秒提示及60秒定时器
    private NoResponseTimerTask mNoResponseTimerTask;
    private int mNoResponseTime;//无响应时长

    private Timer mAudioChatTimer = null; //会话期间的全局定时器（会话时长）
    private AudioChatTimerTask mAudioChatTimerTask;
    private int mAudioChatTime;//当前会话时长

    private Timer mCountdownTimer = null; //倒计时定时器
    private CountdownTimerTask mCountdownTimerTask;
    private int mCountdownTime;//倒计时时长
    private boolean mIsShowCountdown = false;//是否显示倒计时

    private Timer mPayTimer = null; //通话中的心跳定时器
    private PayTimerTask mPayTimerTask;
    private long mPushTime;//心跳推送时间

    private Timer mHeaderTimer = null;//主播WebSocket心跳定时器
    private HeaderTimerTask mHeaderTimerTask;

    private int mReConnectTime;//重连次数，重连3次之后提示失败

    private String mRoomId;
    private double mCurrentPrice;//主播当前收入


    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        public WebSocketService getService() {
            return WebSocketService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        CommonFunction.log(TAG, "onUnbind ");
        mAudioChatServiceListener = null;
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mHandler = new Handler(Looper.getMainLooper());
        WebSocketManager.getsInstance().webSocketConnect();
        CommonFunction.log(TAG, "WebSocketService onCreate");
        WebSocketManager.getsInstance().setIWebServiceListener(this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        CommonFunction.log(TAG, "onDestroy ");

        mHandler.removeCallbacks(mReConnectSocket);
        if (AudioChatManager.getsInstance().isHasLogin()) {
            AudioChatManager.getsInstance().clearAudio();
        }
        AudioChatFloatWindowHelper.getInstance().destroy();
        stopHeaderTimer();
        stopAudioChatTimer();
        stopPayTimer();
        stopCountdown();
        stopNoResponseTimer();
        WebSocketManager.getsInstance().cancelWebSocket();
        WebSocketManager.release();
        super.onDestroy();
    }


    @Override
    public void connectSuccess(WebSocket webSocket, Response response) {
        CommonFunction.log(TAG, "connectSuccess response: " + response + "  UID: " + Common.getInstance().loginUser.getUid());
        mHandler.removeCallbacks(mReConnectSocket);
        if (Common.getInstance().loginUser.getUid() != 0) {

            mReConnectTime = 0;
            WebSocketManager.getsInstance().setConnectStatus(WebSocketManager.STATUS_CONNECT);
            startHeaderTimer();
        }
    }


    @Override
    public void handleReceiveMessage(WebSocket webSocket, String text) {
        AudioChatBean audioChatBean = GsonUtil.getInstance().getServerBean(text, AudioChatBean.class);
        if (audioChatBean != null && audioChatBean.Code == 200) {
//            if (audioChatBean.SendUserID != 0 && !TextUtils.isEmpty(audioChatBean.RoomID) && audioChatBean.Status != WebSocketManager.TAG_LINE_BUSY) {
//                WebSocketManager.getsInstance().setAudioChateBean(audioChatBean);
//                AudioChatManager.getsInstance().setRoomId(audioChatBean.RoomID);
//            }
            if (WebSocketManager.getsInstance().getAudioChatBean() != null) {
                CommonFunction.log("WebSocketManager", "WebSocketManager.getsInstance().getAudioChatBean().RoomID: " + WebSocketManager.getsInstance().getAudioChatBean().RoomID + ", audioChatBean.RoomID: " + audioChatBean.RoomID);
            }
            if (WebSocketManager.getsInstance().getAudioChatBean() != null && !WebSocketManager.getsInstance().getAudioChatBean().RoomID.equals(audioChatBean.RoomID)) {//&& audioChatBean.Status != WebSocketManager.TAG_LINE_BUSY
                return;
            }
            switch (audioChatBean.Status) {
                case WebSocketManager.TAG_CALL://用户拨打电话
                    WebSocketManager.getsInstance().setAudioChateBean(audioChatBean);
                    AudioChatManager.getsInstance().setRoomId(audioChatBean.RoomID);

                    startNoResponseTimer();
                    if (!WebSocketManager.getsInstance().isSendUser()) {
                        //开启主播等待接通界面
                        Intent intent = new Intent("android.intent.action.AudioChatActivity");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("status", AudioChatActivity.STATUS_CALLEE);
                        startActivity(intent);
                    } else {//如果当前用户是呼叫方，直接登陆房间
                        //如果主播在聊吧里,先关闭聊吧窗口
                        GroupChatTopicActivity chatbar = (GroupChatTopicActivity) CloseAllActivity.getInstance().getTargetActivityOne(GroupChatTopicActivity.class);
                        if (chatbar != null) {
                            CommonFunction.log(TAG, " 关闭聊吧窗口");
                            chatbar.isGroupIn = true;
                            CloseAllActivity.getInstance().closeTarget(GroupChatTopicActivity.class);
                        }
                        //先销毁聊吧悬浮窗和聊吧推拉流
                        ChatBarZoomWindow.getInstance().close();

                        if (mAudioChatServiceListener != null) {
                            mAudioChatServiceListener.updateIcon(audioChatBean);
                        }
                        AudioChatManager.getsInstance().init();
                        AudioChatManager.getsInstance().login(audioChatBean.RoomID);
                        mRoomId = audioChatBean.RoomID;
                    }
                    break;
                case WebSocketManager.TAG_CANCEL_CALL://取消拨打
                    //TODO
                    stopNoResponseTimer();
                    if (mAudioChatServiceListener != null && WebSocketManager.getsInstance().getAudioChatBean() != null && !WebSocketManager.getsInstance().isSendUser()) {
                        CommonFunction.toastMsg(BaseApplication.appContext, R.string.other_hangup_video_chat_end);
//                        mAudioChatServiceListener.updateAnswerState(AudioChatActivity.STATUS_CLOSE);
                    }
                    logoutAudioRoom();
                    break;
                case WebSocketManager.TAG_ANCHOR_AGREE://主播接通
                    //如果主播在聊吧里,先关闭聊吧窗口
                    GroupChatTopicActivity chatbar = (GroupChatTopicActivity) CloseAllActivity.getInstance().getTargetActivityOne(GroupChatTopicActivity.class);
                    if (chatbar != null) {
                        CommonFunction.log(TAG, " 关闭聊吧窗口");
                        chatbar.isGroupIn = true;
                        CloseAllActivity.getInstance().closeTarget(GroupChatTopicActivity.class);
                    }
                    //先销毁聊吧悬浮窗和聊吧推拉流
                    ChatBarZoomWindow.getInstance().close();

                    stopNoResponseTimer();
                    if (WebSocketManager.getsInstance().getAudioChatBean() != null) {
                        if (WebSocketManager.getsInstance().isSendUser()) {
                            if (mAudioChatServiceListener != null) {
                                mAudioChatServiceListener.updateAnswerState(AudioChatActivity.STATUS_CONNECT);
                            }
                        } else {//主播接通后进入房间
                            AudioChatManager.getsInstance().init();
                            AudioChatManager.getsInstance().login(audioChatBean.RoomID);
                        }
                    }
                    //15s 未收到拉流成功消息断开
                    mHandler.postDelayed(mNotPlaySuccess, PLAY_SUCCESS_INTERVAL);
                    break;
                case WebSocketManager.TAG_ANCHOR_REFUSE://主播拒绝
                    stopNoResponseTimer();
//                    if (mAudioChatServiceListener != null) {
//                        mAudioChatServiceListener.updateAnswerState(AudioChatActivity.STATUS_CLOSE);
//                    }
                    if (WebSocketManager.getsInstance().getAudioChatBean() != null && WebSocketManager.getsInstance().isSendUser()) {
                        CommonFunction.toastMsg(BaseApplication.appContext, R.string.video_chat_other_reject);
                    }

                    logoutAudioRoom();
                    break;
                case WebSocketManager.TAG_CLOSE://挂断
                    //TODO
                    stopPayTimer();
                    stopAudioChatTimer();
                    if (audioChatBean.CurrentSendUserID == Common.getInstance().loginUser.getUid()) {
                        CommonFunction.toastMsg(BaseApplication.appContext, R.string.video_chat_end);
                    } else {
                        CommonFunction.toastMsg(BaseApplication.appContext, R.string.other_hangup_video_chat_end);
                    }
                    logoutAudioRoom();
                    break;
                case WebSocketManager.TAG_AUDIO_PLAY_FAIL://拉流失败
                    AudioChatManager.getsInstance().setPlaySuccess(false);
                    stopPayTimer();
                    stopAudioChatTimer();
                    logoutAudioRoom();

                    break;
                case WebSocketManager.TAG_AUDIO_PLAY_SUCCESS://双方拉流成功
                    mHandler.removeCallbacks(mNotPlaySuccess);
                    AudioChatManager.getsInstance().setPlaySuccess(true);
                    startAudioChatTimer();
                    if (WebSocketManager.getsInstance().getAudioChatBean() != null && !WebSocketManager.getsInstance().isSendUser()) {
                        //主播开启定时检测是否扣费成功
                        startPayTimer();
                    }
                    break;
                case WebSocketManager.TAG_START_PAY://开始计费
                    mHandler.removeCallbacks(mNotPaySuccess);
                    if (WebSocketManager.getsInstance().getAudioChatBean() != null && !WebSocketManager.getsInstance().isSendUser()) {
                        //记录收费
                        mCurrentPrice = audioChatBean.UserSum;
                        if (mAudioChatServiceListener != null) {
                            mAudioChatServiceListener.updateCurrentIncome(audioChatBean.UserSum + "");
                        }
                    }

                    if (audioChatBean.TesidueTime >= 0 && audioChatBean.TesidueTime <= 180) {
                        if (mCountdownTime < audioChatBean.TesidueTime) {
                            showCountdown(audioChatBean.TesidueTime);
                        }
                    } else {
                        stopCountdown();
                        if (mAudioChatServiceListener != null) {
                            mAudioChatServiceListener.updateCountDown(null);
                        }
                    }
                    break;
                case WebSocketManager.TAG_PAY_SUCCESS://充值成功
                    if (WebSocketManager.getsInstance().getAudioChatBean() != null && !WebSocketManager.getsInstance().isSendUser()) {
                        //记录收费
                        mCurrentPrice = audioChatBean.UserSum;
                        if (mAudioChatServiceListener != null) {
                            mAudioChatServiceListener.updateCurrentIncome(audioChatBean.UserSum + "");
                        }
                    }

                    if (audioChatBean.TesidueTime >= 0 && audioChatBean.TesidueTime <= 180) {
                        if (mCountdownTime < audioChatBean.TesidueTime) {
                            showCountdown(audioChatBean.TesidueTime);
                        }
                    } else {
                        if (mAudioChatServiceListener != null) {
                            mAudioChatServiceListener.updateCountDown(null);
                        }
                        stopCountdown();
                    }
                    break;
                case WebSocketManager.TAG_HEARTBEAT://正常情况 10s间隔 收到心跳回调
                    mPushTime = System.currentTimeMillis();//重置
                    break;
                case WebSocketManager.TAG_CALL_TIMEOUT://超时
                    stopNoResponseTimer();
//                    if (WebSocketManager.getsInstance().getAudioChatBean() != null && !WebSocketManager.getsInstance().isSendUser()) {
//                        CommonFunction.toastMsg(BaseApplication.appContext, getString(R.string.other_hangup_video_chat_end));
//                    }

//                    if (AudioChatManager.getsInstance().isHasLogin()) {
//                        CommonFunction.toastMsg(BaseApplication.appContext, R.string.video_chat_end);
//                    }
//                    if (mAudioChatServiceListener != null) {
//                        mAudioChatServiceListener.updateAnswerState(AudioChatActivity.STATUS_CLOSE);
//                    }
                    logoutAudioRoom();
                    break;
                case WebSocketManager.TAG_TARGET_HEARTBEAT_TIMEOUT://通话中对方心跳超时
                    stopCountdown();
                    stopPayTimer();
                    stopAudioChatTimer();
                    if (AudioChatManager.getsInstance().isHasLogin()) {
                        CommonFunction.toastMsg(BaseApplication.appContext, R.string.video_chat_end);

//                        if (mAudioChatServiceListener != null) {
//                            mAudioChatServiceListener.updateAnswerState(AudioChatActivity.STATUS_CLOSE);
//                        }

                        logoutAudioRoom();
                    }
                case WebSocketManager.TAG_ANCHOR_NOT_RECEIVE_PAY://主播未收到扣费消息(此消息只有发起呼叫端（用户）才能收到)
                    stopCountdown();
                    stopAudioChatTimer();
                    if (AudioChatManager.getsInstance().isHasLogin()) {
                        CommonFunction.toastMsg(BaseApplication.appContext, R.string.video_chat_end);

//                        if (mAudioChatServiceListener != null) {
//                            mAudioChatServiceListener.updateAnswerState(AudioChatActivity.STATUS_CLOSE);
//                        }

                        logoutAudioRoom();
                    }
                    break;
//                case WebSocketManager.TAG_LINE_BUSY://主播忙线中，有用户打电话过来
//                    break;
            }
        } else {
            stopCountdown();
            stopPayTimer();
            stopAudioChatTimer();
            stopNoResponseTimer();
            logoutAudioRoom();
        }
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        CommonFunction.log(TAG, "reason: " + reason);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        WebSocketManager.getsInstance().setConnectStatus(WebSocketManager.STATUS_NOT_CONNECT);

    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        CommonFunction.log(TAG, "onFailure: " + mReConnectTime + "  UID: " + Common.getInstance().loginUser.getUid());
        if (WebSocketManager.getsInstance().getAudioChatBean() != null && WebSocketManager.getsInstance().isSendUser() && mReConnectTime > 5) {
            CommonFunction.toastMsg(BaseApplication.appContext, getString(R.string.network_error));
            logoutAudioRoom();
        }

        if (Common.getInstance().loginUser.getUid() == 0) {
            stopSelf();
            return;
        }
        stopHeaderTimer();
        WebSocketManager.getsInstance().cancelWebSocket();
        WebSocketManager.getsInstance().setConnectStatus(WebSocketManager.STATUS_FAIL);
        mHandler.postDelayed(mReConnectSocket, RECONNECT_INTERVAL);
    }

    @Override
    public void closeAudioRoom() {
        logoutAudioRoom();
    }

    /*重连**/
    private Runnable mReConnectSocket = new Runnable() {
        @Override
        public void run() {
            mReConnectTime++;
            WebSocketManager.getsInstance().webSocketConnect();
        }
    };

    /**
     * 双方未收到拉流成功消息断开房间
     */
    private Runnable mNotPlaySuccess = new Runnable() {
        @Override
        public void run() {
            //发送拉流失败
            WebSocketManager.getsInstance().sendMessage(WebSocketManager.TAG_AUDIO_PLAY_FAIL);
            if (AudioChatManager.getsInstance().isPlaySuccess()) {
                CommonFunction.toastMsg(BaseApplication.appContext, R.string.other_network_not_good_connection_break);
            }
            logoutAudioRoom();
        }
    };
    /**
     * 主播未收到扣费成功消息断开房间
     */
    private Runnable mNotPaySuccess = new Runnable() {
        @Override
        public void run() {
            WebSocketManager.getsInstance().sendMessage(WebSocketManager.TAG_ANCHOR_NOT_RECEIVE_PAY);
            logoutAudioRoom();
        }
    };


    /* 退出房间
     **/
    private void logoutAudioRoom() {
        AudioChatActivity audioChat = (AudioChatActivity) CloseAllActivity.getInstance().getTargetActivityOne(AudioChatActivity.class);

        if (audioChat != null) {
            mHandler.postDelayed(new Runnable() {//延时2s 关闭
                @Override
                public void run() {
                    CloseAllActivity.getInstance().closeTarget(AudioChatActivity.class);
                    AudioChatFloatWindowHelper.getInstance().destroy();
                    //如果不是主播关闭socket服务
                    if (Common.getInstance().loginUser.getVoiceUserType() == 0) {
                        stopSelf();
                    }
                }
            }, 2000);
        }
        stopCountdown();
        stopPayTimer();
        stopAudioChatTimer();
        stopNoResponseTimer();
        AudioChatFloatWindowHelper.getInstance().destroy();
        AudioChatManager.getsInstance().setRoomId(null);
        if (AudioChatManager.getsInstance().isHasLogin()) {
            AudioChatManager.getsInstance().clearAudio();
        }
        WebSocketManager.getsInstance().setAudioChateBean(null);
        if (mHandler != null) {
            mHandler.removeCallbacks(mNotPlaySuccess);
            mHandler.removeCallbacks(mNotPaySuccess);
        }
        //如果不是主播关闭socket服务
        if (Common.getInstance().loginUser.getVoiceUserType() == 0) {
            stopSelf();
        }
    }

    /**
     * 获取当前价格
     *
     * @return
     */
    public String getCurrentPrice() {
        return mCurrentPrice + "";
    }

    /**
     * 获取当前通话时长
     *
     * @return
     */
    public String getCurrentAudioChatTime() {
        return TimeFormat.secondsToString(mAudioChatTime);
    }

    /**
     * 获取当前倒计时时长
     *
     * @return
     */
    public String getCurrentCountdownTime() {
        return TimeFormat.secondsToString(mCountdownTime);
    }

    /**
     * 是否已经显示倒计时
     *
     * @return
     */
    public boolean isShowCountdown() {
        return mIsShowCountdown;
    }

    public void setAudioChatServiceListener(AudioChatServiceListener audioChatServiceListener) {
        this.mAudioChatServiceListener = audioChatServiceListener;
    }

    public interface AudioChatServiceListener {

        /**
         * 更新当前接听状态
         *
         * @param state 0-用户拨打中界面 1-主播等待接听界面 2-接通后 3-挂断
         */
        void updateAnswerState(int state);

        /**
         * 更新当前价格
         *
         * @param price
         */
        void updateCurrentIncome(String price);

        /**
         * 更新当前通话时长
         *
         * @param time
         */
        void updateCallDuration(String time, int second);

        /**
         * 更新倒计时
         *
         * @param time
         */
        void updateCountDown(String time);

        /**
         * 更新头像
         *
         * @param bean
         */
        void updateIcon(AudioChatBean bean);
    }

    /* 开启无应答定时器
     * */
    private void startNoResponseTimer() {
        CommonFunction.log(TAG, "startNoResponseTimer() into");
        mNoResponseTime = 0;
        if (mNoResponseTimer != null) {
            mNoResponseTimer.cancel();
            mNoResponseTimer = null;
        }
        mNoResponseTimer = new Timer();
        mNoResponseTimerTask = new NoResponseTimerTask(this);
        mNoResponseTimer.schedule(mNoResponseTimerTask, 0, NO_RESPONSE_TIMER_INTERVAL);
    }

    /* 关闭无应答定时器
     * */
    private void stopNoResponseTimer() {
        CommonFunction.log(TAG, "stopNoResponseTimer() into");
        if (mNoResponseTimer != null) {
            mNoResponseTimer.cancel();
            mNoResponseTimerTask = null;
            mNoResponseTimer = null;
        }
    }

    /*对方无应答定时器
     * */
    static class NoResponseTimerTask extends TimerTask {
        private WeakReference<WebSocketService> mService = null;

        public NoResponseTimerTask(WebSocketService activity) {
            mService = new WeakReference<WebSocketService>(activity);
        }

        @Override
        public void run() {
            CommonFunction.log(TAG, "NoResponseTimerTask() time bingo");
            final WebSocketService service = mService.get();
            if (service != null) {
                service.mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (service != null) {
//                            if (Build.VERSION.SDK_INT >= 17) {
//                                if (!CommonFunction.isServiceExisted(BaseApplication.appContext, WebSocketService.class.getName())) {
//                                    return;
//                                }
//                            }
                            service.doNoResponse();
                        }
                    }
                });
            }
        }
    }

    /* 处理发起的音频会话无应答
     * */
    private void doNoResponse() {
        if (mNoResponseTimer != null) {
            mNoResponseTime++;
            if (mNoResponseTime * NO_RESPONSE_TIMER_INTERVAL == NO_RESPONSE_NOTIFY_SECONDS && WebSocketManager.getsInstance().getAudioChatBean() != null && WebSocketManager.getsInstance().isSendUser()) {
                CommonFunction.log(TAG, "doNoResponse() 提示手机不在身边");
                //提示手机不在身边
                CommonFunction.toastMsgLong(BaseApplication.appContext, BaseApplication.appContext.getString(R.string.video_chat_noresponse_notify));
            } else if (mNoResponseTime * NO_RESPONSE_TIMER_INTERVAL == NO_RESPONSE_CLOSE_VIDEO_CHAT_SECONDS) {

                //取消会话,超时
                CommonFunction.log(TAG, "doNoResponse() 发送取消会话任务, roomid=" + mRoomId);

                Logger.i(TAG, "doNoResponse() 提示对方无应答");
                if (WebSocketManager.getsInstance().getAudioChatBean() != null && WebSocketManager.getsInstance().isSendUser()) {
                    //提示对方无应答
                    CommonFunction.toastMsg(BaseApplication.appContext, BaseApplication.appContext.getString(R.string.video_chat_noresponse_close));
                    WebSocketManager.getsInstance().sendMessage(WebSocketManager.TAG_CALL_TIMEOUT);
                }

//                if (mAudioChatServiceListener != null) {
//                    mAudioChatServiceListener.updateAnswerState(AudioChatActivity.STATUS_CLOSE);
//                }
                stopNoResponseTimer();
                logoutAudioRoom();

            }
        }

    }


    /* 开启语音会话定时器
     * */
    private void startAudioChatTimer() {
        CommonFunction.log(TAG, "startAudioChatTimer() into");
        mPushTime = System.currentTimeMillis();
        mAudioChatTime = 0;
        if (mAudioChatTimer != null) {
            mAudioChatTimer.cancel();
            mAudioChatTimer = null;
        }
        mAudioChatTimer = new Timer();
        mAudioChatTimerTask = new AudioChatTimerTask(this);
        mAudioChatTimer.schedule(mAudioChatTimerTask, 0, NO_RESPONSE_TIMER_INTERVAL);
    }

    /* 关闭语音会话定时器
     * */
    private void stopAudioChatTimer() {
        if (mAudioChatTimer != null) {
            mAudioChatTimer.cancel();
            mAudioChatTimer = null;
            mAudioChatTimerTask = null;
        }
    }

    /*会话时长计时任务
     * */
    static class AudioChatTimerTask extends TimerTask {
        private WeakReference<WebSocketService> mService = null;

        public AudioChatTimerTask(WebSocketService activity) {
            mService = new WeakReference<WebSocketService>(activity);
        }

        @Override
        public void run() {
            CommonFunction.log(TAG, "AudioChatTimerTask() time bingo");
            final WebSocketService service = mService.get();
            if (service != null) {
                service.mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (service != null) {
//                            if (Build.VERSION.SDK_INT >= 17) {
//                                if (!CommonFunction.isServiceExisted(BaseApplication.appContext, WebSocketService.class.getName())) {
//                                    return;
//                                }
//                            }
                            service.doAudioChatTime();
                        }
                    }
                });
            }
        }
    }

    /**
     * 记录通话时长，并且判断心跳是否超时
     */
    private void doAudioChatTime() {
        mAudioChatTime++;
        if (mAudioChatServiceListener != null) {
            mAudioChatServiceListener.updateCallDuration(TimeFormat.secondsToString(mAudioChatTime), mAudioChatTime);
        }
        if (AudioChatFloatWindowHelper.getInstance().isShowing()) {
            AudioChatFloatWindowHelper.getInstance().updateWindow(TimeFormat.secondsToString(mAudioChatTime));
        }
//        long current = System.currentTimeMillis();
//        if ((current - mPushTime) > PUSH_TIME_INTERVAL) {
//            CommonFunction.log(TAG, "大于15s未收到扣费心跳");
//            CommonFunction.toastMsg(BaseApplication.appContext, getString(R.string.other_network_not_good_connection_break));
//            stopAudioChatTimer();
//            WebSocketManager.getsInstance().sendMessage(WebSocketManager.TAG_CLOSE);
//        }
    }

    /*倒计时
     * */
    private void showCountdown(int seconds) {
        CommonFunction.log(TAG, "showCountdown() into");
        mIsShowCountdown = true;
        mCountdownTime = seconds;
        if (mCountdownTimer != null) {
            mCountdownTimer.cancel();
            mCountdownTimer = null;
        }
        mCountdownTimer = new Timer();
        mCountdownTimerTask = new CountdownTimerTask(this);
        mCountdownTimer.schedule(mCountdownTimerTask, 0, NO_RESPONSE_TIMER_INTERVAL);
    }

    /*停止倒计时计时器
     * */
    private void stopCountdown() {
        mIsShowCountdown = false;
        mCountdownTime = 0;
        if (mCountdownTimer != null) {
            mCountdownTimer.cancel();
            mCountdownTimer = null;
            mCountdownTimerTask = null;
        }
    }

    /*倒计时任务
     * */
    static class CountdownTimerTask extends TimerTask {
        private WeakReference<WebSocketService> mService = null;

        public CountdownTimerTask(WebSocketService activity) {
            mService = new WeakReference<WebSocketService>(activity);
        }

        @Override
        public void run() {
            CommonFunction.log(TAG, "CountdownTimerTask() time bingo");
            final WebSocketService service = mService.get();
            if (service != null) {
                service.mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (service != null) {
//                            if (Build.VERSION.SDK_INT >= 17) {
//                                if (!CommonFunction.isServiceExisted(BaseApplication.appContext, WebSocketService.class.getName())) {
//                                    return;
//                                }
//                            }
                            service.doCountdown();
                        }
                    }
                });
            }
        }
    }

    /*倒计时定时减秒数
     * */
    private void doCountdown() {
        mCountdownTime--;
        if (mCountdownTime <= 0) {
            stopCountdown();
            //关闭聊天
            WebSocketManager.getsInstance().sendMessage(WebSocketManager.TAG_CLOSE);
            logoutAudioRoom();
//            if (mAudioChatServiceListener != null) {
//                mAudioChatServiceListener.updateAnswerState(AudioChatActivity.STATUS_CLOSE);
//            }
        } else {
            if (mAudioChatServiceListener != null) {
                mAudioChatServiceListener.updateCountDown(TimeFormat.secondsToString(mCountdownTime));
            }
        }
    }


    /* 　扣费检测定时器,主播
     * */
    private void startPayTimer() {
        CommonFunction.log(TAG, "startPayTimer() into");
        if (mPayTimer != null) {
            mPayTimer.cancel();
            mPayTimer = null;
        }
        mPayTimer = new Timer();
        mPayTimerTask = new PayTimerTask(this);
        mPayTimer.schedule(mPayTimerTask, 0, PAY_TIMER_INTERVAL);
    }


    /* 　停止检测扣费定时器
     * */
    private void stopPayTimer() {
        mCurrentPrice = 0;
        if (mPayTimer != null) {
            mPayTimer.cancel();
            mPayTimer = null;
            mPayTimerTask = null;
        }
    }

    /*通话中的心跳任务
     * */
    static class PayTimerTask extends TimerTask {
        private WeakReference<WebSocketService> mService = null;

        public PayTimerTask(WebSocketService activity) {
            mService = new WeakReference<WebSocketService>(activity);
        }

        @Override
        public void run() {
            CommonFunction.log(TAG, "PayTimerTask() time bingo");
            final WebSocketService service = mService.get();
            if (service != null) {
                service.mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (service != null) {
//                            if (Build.VERSION.SDK_INT >= 17) {
//                                if (!CommonFunction.isServiceExisted(BaseApplication.appContext, WebSocketService.class.getName())) {
//                                    return;
//                                }
//                            }
                            service.doPayTimer();
                        }
                    }
                });
            }
        }
    }

    /**
     * 心跳逻辑，带付费信息
     */
    private void doPayTimer() {
//        LinkedHashMap map = new LinkedHashMap();
//        map.put("Status", WebSocketManager.TAG_HEARTBEAT);
//        map.put("CurrentSendUserID", Common.getInstance().loginUser.getUid());
//        map.put("RoomID", AudioChatManager.getsInstance().getRoomId());
//        if (AudioChatManager.getsInstance().isPlaySuccess() && WebSocketManager.getsInstance().isSendUser()) {
//            map.put("StarNum", 1);//用户
//        }
//        WebSocketManager.getsInstance().sendMessage(map);

        //15s未收到扣费消息断开
        mHandler.postDelayed(mNotPaySuccess, PAY_SUCCESS_TIME_INTERVAL);
    }

    /**
     * 开启主播连接socket心跳定时器
     */
    private void startHeaderTimer() {
        if (mHeaderTimer != null) {
            mHeaderTimer.cancel();
            mHeaderTimer = null;
        }
        mHeaderTimer = new Timer();
        mHeaderTimerTask = new HeaderTimerTask(this);
        mHeaderTimer.schedule(mHeaderTimerTask, 0, HEADER_TIME_INTERVAL);
    }

    /* 　停止主叫上报扣费定时器
     * */
    private void stopHeaderTimer() {
        if (mHeaderTimer != null) {
            mHeaderTimer.cancel();
            mHeaderTimer = null;
            mHeaderTimerTask = null;
        }
    }

    /*主播连接心跳任务
     * */
    static class HeaderTimerTask extends TimerTask {
        private WeakReference<WebSocketService> mService = null;

        public HeaderTimerTask(WebSocketService activity) {
            mService = new WeakReference<WebSocketService>(activity);
        }

        @Override
        public void run() {
            CommonFunction.log(TAG, "HeaderTimerTask() time bingo");
            final WebSocketService service = mService.get();
            if (service != null) {
                service.mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (service != null) {
//                            if (Build.VERSION.SDK_INT >= 17) {
//                                if (!CommonFunction.isServiceExisted(BaseApplication.appContext, WebSocketService.class.getName())) {
//                                    return;
//                                }
//                            }
                            service.doHeaderTimer();
                        }
                    }
                });
            }
        }
    }

    /*主播连接心跳任务
     */
    private void doHeaderTimer() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("Status", WebSocketManager.TAG_HEARTBEAT);
        map.put("CurrentSendUserID", Common.getInstance().loginUser.getUid());
        if (!TextUtils.isEmpty(AudioChatManager.getsInstance().getRoomId())) {
            map.put("RoomID", AudioChatManager.getsInstance().getRoomId());
        }
        if (AudioChatManager.getsInstance().isPlaySuccess() && WebSocketManager.getsInstance().getAudioChatBean() != null && WebSocketManager.getsInstance().isSendUser()) {
            map.put("StarNum", 1);//用户
            CommonFunction.log("WebSocketManager", "Status: " + WebSocketManager.TAG_HEARTBEAT + " ,CurrentSendUserID: " + Common.getInstance().loginUser.getUid() + ",  RoomID: " + AudioChatManager.getsInstance().getRoomId() + ", StarNum: " + 1);
        } else {
            CommonFunction.log("WebSocketManager", "Status: " + WebSocketManager.TAG_HEARTBEAT + " ,CurrentSendUserID: "
                    + Common.getInstance().loginUser.getUid() + ",  RoomID: " + AudioChatManager.getsInstance().getRoomId() + ", StarNum: ");
        }

        if (WebSocketManager.getsInstance().getAudioChatBean() != null) {
            CommonFunction.log("WebSocketManager", " SendUserID: " + WebSocketManager.getsInstance().getAudioChatBean().SendUserID);
        }
        WebSocketManager.getsInstance().sendMessage(map);
    }


}
