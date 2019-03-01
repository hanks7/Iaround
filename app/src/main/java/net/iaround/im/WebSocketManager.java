package net.iaround.im;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import net.iaround.conf.Config;
import net.iaround.mic.AudioChatManager;
import net.iaround.conf.Common;
import net.iaround.im.service.WebSocketService;
import net.iaround.model.audiochat.AudioChatBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.CryptoUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.JsonUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.datamodel.AudioChatModel;
import net.iaround.ui.datamodel.User;

import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * webSocket 管理类
 */
public class WebSocketManager {
    private static final String TAG = "WebSocketManager";

    private static WebSocketManager sInstance;

    public static final int STATUS_NOT_CONNECT = 0;//未连接
    public static final int STATUS_CONNECT = 1;//已连接
    public static final int STATUS_FAIL = 2;//连接失败

    public static final int TAG_ONLIN = 0;//在线空闲
    public static final int TAG_CALL = 1;//拨打
    public static final int TAG_CANCEL_CALL = 2;//取消拨打
    public static final int TAG_ANCHOR_AGREE = 3;//主播同意
    public static final int TAG_ANCHOR_REFUSE = 4;//主播拒绝
    public static final int TAG_LOGIN_ROOM_FAIL = 5;//主播进入房间失败
    public static final int TAG_LINE_BUSY = 17;//主播忙线中有其他用户打过来（只有主播接收）
    public static final int TAG_PAY_SUCCESS = 18;//充值成功
    public static final int TAG_CALL_TIMEOUT = 20;//拨打超时
    public static final int TAG_TARGET_HEARTBEAT_TIMEOUT = 22;//通话中对方心跳超时
    public static final int TAG_EXCEPTION = 23;//异常断开，系统更新的状态(拉流之前非错误状态)
    public static final int TAG_ANCHOR_NOT_RECEIVE_PAY = 24;//主播未收到扣费消息
    public static final int TAG_AUDIO_PLAY_FAIL = 25;//拉流失败
    public static final int TAG_AUDIO_PLAY_SUCCESS = 26;//双方拉流成功
    public static final int TAG_START_PAY = 28;//开始通话计费
    public static final int TAG_CLOSE = 30;//关闭通话
    public static final int TAG_HEARTBEAT = 100;//语音心跳

    private WebSocket mWebSocket;
    private OkHttpClient mOkHttpClient;
    private Request mRequest;
    private IWebSocketListener mIWebSocketListener;
    private CheckCallStatusListener mCheckCallStatusListener;
    private ExecutorService mWriteExecutor = Executors.newSingleThreadExecutor();
    private Handler mHandler;
    private int mConnectStatus;//连接状态

    public User mSendUser;//用户信息（拨打）
    public User mReceiveUser;//主播信息（被叫）

    private AudioChatBean mAudioChatBean;

//    public long userid = 61001745;//主播 64609646， 用户 61001745

    private WebSocketManager() {
        mOkHttpClient = new OkHttpClient.Builder().build();//设置连接超时时间build();
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static WebSocketManager getsInstance() {
        if (null == sInstance) {
            synchronized (WebSocketManager.class) {
                if (null == sInstance) {
                    sInstance = new WebSocketManager();
                }
            }
        }
        return sInstance;
    }

    public void initWebSocket() {
        long time = TimeFormat.getCurrentTimeMillis() / 1000;//时间戳要跟服务器保持一致，否则连接不上
        long random = (long) (Math.random() * 100000);
        String content = Common.getInstance().getUid() + time + random + Common.getInstance().loginUser.getSecret();
        String url = "ws://" + Config.WS_AUDIO_CHAT_URL + "/" + "?UserID=" + Common.getInstance().getUid() + "&TimeStamp=" + time + "&Nonce=" + random + "&Sign=" + CryptoUtil.md5(content);
        mRequest = new Request.Builder().url(url).build();
        CommonFunction.log(TAG, "webSocket content: " + content);
    }

    public void startWebSocketService(Context context) {
        Intent intent = new Intent(context, WebSocketService.class);

        context.startService(intent);
    }

    public void stopWebSocketService(Context context) {
        logoutAudioRoom(true);
        Intent intent = new Intent(context, WebSocketService.class);

        context.stopService(intent);
    }

    public void setIWebServiceListener(IWebSocketListener mIWebSocketListener) {
        this.mIWebSocketListener = mIWebSocketListener;
    }

    public void setCheckCallStatusListener(CheckCallStatusListener checkCallStatusListener) {
        this.mCheckCallStatusListener = checkCallStatusListener;
    }

    /**
     * 设置连接状态
     *
     * @param connectStatus
     */
    public void setConnectStatus(int connectStatus) {
        mConnectStatus = connectStatus;
    }

    public int getStatusConnect() {
        return mConnectStatus;
    }

    /**
     * 检测是否可以拨打
     */
    public interface CheckCallStatusListener {
        void checkStatus(AudioChatBean bean);

        void onOpen();
    }

    /**
     * 建立webSocket连接
     */
    public void webSocketConnect() {
        initWebSocket();
        CommonFunction.log(TAG, "webSocketConnect ");
        if (mWriteExecutor == null || mWriteExecutor.isShutdown()) {
            mWriteExecutor = Executors.newSingleThreadExecutor();
        }
        mWebSocket = mOkHttpClient.newWebSocket(mRequest, new WebSocketListener() {

            @Override
            public void onOpen(final WebSocket webSocket, final Response response) {
                super.onOpen(webSocket, response);
                CommonFunction.log(TAG, "onOpen  Looper.myLooper(): " + Looper.myLooper() + ", Looper.getMainLooper(): " + Looper.getMainLooper());
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mCheckCallStatusListener != null) {
                                mCheckCallStatusListener.onOpen();
                            }
                            if (mIWebSocketListener != null) {
                                mIWebSocketListener.connectSuccess(webSocket, response);
                            }
                        }
                    });

                } else {
                    if (mIWebSocketListener != null) {
                        mIWebSocketListener.connectSuccess(webSocket, response);
                    }
                }
            }

            @Override
            public void onMessage(final WebSocket webSocket, final String text) {
                super.onMessage(webSocket, text);
                CommonFunction.log(TAG, "onMessage text: " + text);
                AudioChatBean bean = GsonUtil.getInstance().getServerBean(text, AudioChatBean.class);
                if (bean != null) {
                    AudioChatModel.getInstance().insertRecord(bean);
                }

                if (Looper.myLooper() != Looper.getMainLooper()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (mIWebSocketListener != null) {
                                mIWebSocketListener.handleReceiveMessage(webSocket, text);
                            }
                            if (mCheckCallStatusListener != null) {
                                AudioChatBean bean = GsonUtil.getInstance().getServerBean(text, AudioChatBean.class);
                                mCheckCallStatusListener.checkStatus(bean);
                            }
                        }
                    });
                } else {
                    if (mCheckCallStatusListener != null) {
                        mCheckCallStatusListener.checkStatus(bean);
                    }
                    if (mIWebSocketListener != null) {
                        mIWebSocketListener.handleReceiveMessage(webSocket, text);
                    }
                }


            }

            @Override
            public void onClosing(final WebSocket webSocket, final int code, final String reason) {
                super.onClosing(webSocket, code, reason);
                CommonFunction.log(TAG, "reason: " + reason);
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mIWebSocketListener != null) {
                                mIWebSocketListener.onClosing(webSocket, code, reason);
                            }
                        }
                    });
                } else {
                    if (mIWebSocketListener != null) {
                        mIWebSocketListener.onClosing(webSocket, code, reason);
                    }
                }


            }

            @Override
            public void onClosed(final WebSocket webSocket, final int code, final String reason) {
                super.onClosed(webSocket, code, reason);
                CommonFunction.log(TAG, "onClosed reason: " + reason);
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mIWebSocketListener != null) {
                                mIWebSocketListener.onClosed(webSocket, code, reason);
                            }
                        }
                    });
                } else {
                    if (mIWebSocketListener != null) {
                        mIWebSocketListener.onClosed(webSocket, code, reason);
                    }
                }


            }

            @Override
            public void onFailure(final WebSocket webSocket, final Throwable t, final Response response) {
                super.onFailure(webSocket, t, response);
                CommonFunction.log(TAG, "onFailure response: " + response);
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mCheckCallStatusListener != null) {
                                mCheckCallStatusListener.checkStatus(null);
                            }
                            if (mIWebSocketListener != null) {
                                mIWebSocketListener.onFailure(webSocket, t, response);
                            }

                        }
                    });
                } else {
                    if (mCheckCallStatusListener != null) {
                        mCheckCallStatusListener.checkStatus(null);
                    }
                    if (mIWebSocketListener != null) {
                        mIWebSocketListener.onFailure(webSocket, t, response);
                    }

                }
            }
        });
    }

    /**
     * 发送message
     *
     * @param message
     */
    public void sendMessage(final String message) {
        if (mWebSocket != null && mWriteExecutor != null) {
            mWriteExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    if (mWebSocket != null) {
                        boolean isSent = mWebSocket.send(message);
                        CommonFunction.log(TAG, "sedMessage  isSent: " + isSent);
                    } else {
                        CommonFunction.log(TAG, "sedMessage  message: " + message);

                    }

                }
            });
        }
    }

    /**
     * 发送message
     *
     * @param map
     */
    public void sendMessage(final LinkedHashMap map) {
        if (mWriteExecutor != null && mWebSocket != null) {
            mWriteExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    String message = JsonUtil.mapToJsonString(map);
                    if (mWebSocket != null) {
                        boolean isSent = mWebSocket.send(message);
                        CommonFunction.log(TAG, "sedMessage  isSent: " + isSent + ", status: " + map.get("Status"));
                    } else {
                        CommonFunction.log(TAG, "sedMessage  mWebSocket: " + mWebSocket);
                    }
                }
            });
        }
    }

    /**
     * 发送message
     *
     * @param status
     */
    public void sendMessage(final int status) {
        if (mWebSocket != null && mWriteExecutor != null) {
            final LinkedHashMap map = new LinkedHashMap();
            map.put("Status", status);
            map.put("CurrentSendUserID", Common.getInstance().loginUser.getUid());
            map.put("RoomID", AudioChatManager.getsInstance().getRoomId());
            mWriteExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    String message = JsonUtil.mapToJsonString(map);
                    if (mWebSocket != null) {
                        boolean isSent = mWebSocket.send(message);
                        CommonFunction.log(TAG, "sedMessage  status : " + status + ", isSent: " + isSent + ", CurrentSendUserID: " + Common.getInstance().loginUser.getUid() + " , RoomID: " + AudioChatManager.getsInstance().getRoomId());
                    } else {
                        CommonFunction.log(TAG, "sedMessage status" + status + ", mWebSocket: " + mWebSocket);
                    }
                }
            });
        }
    }

//    /***
//     * 设置用户/主播信息
//     * @param bean
//     */
//    public void setUserInfo(AudioChatBean bean) {
//        mSendUser = new User();
//        mSendUser.setNickname(bean.SendUserNickName);
//        mSendUser.setUid(bean.SendUserID);
//        mSendUser.setIcon(bean.SendUserICON);
//
//        mReceiveUser = new User();
//        mReceiveUser.setNickname(bean.ReceiveUserNickName);
//        mReceiveUser.setUid(bean.ReceiveUserID);
//        mReceiveUser.setIcon(bean.ReceiveUserICON);
//    }

    /**
     * 设置接收信息实体类
     *
     * @param bean
     */
    public void setAudioChateBean(AudioChatBean bean) {
        mAudioChatBean = bean;
    }

    public AudioChatBean getAudioChatBean() {
        return mAudioChatBean;
    }

//    /***
//     * 判断当前用户是否为用户
//     * @return true-用户（呼叫方），false-主播（被叫方）
//     */
//    public boolean isSendUser() {
//        if (64609646 == mSendUser.getUid()) {
//            return true;
//        }
//        if (Common.getInstance().loginUser.getUid() == mSendUser.getUid()) {
//            return true;
//        }
//        return false;
//    }

    /***
     * 判断当前用户是否为用户（呼叫方）
     * @return true-用户（呼叫方），false-主播（被叫方）
     */
    public boolean isSendUser() {
        if (Common.getInstance().loginUser.getUid() == mAudioChatBean.SendUserID) {
            return true;
        }

        return false;
    }

    /**
     * 退出语音聊天
     *
     * @param isSendClose 是否需要发送正常关闭请求
     */
    public void logoutAudioRoom(boolean isSendClose) {

        if (isSendClose && getStatusConnect() == WebSocketManager.STATUS_CONNECT && !TextUtils.isEmpty(AudioChatManager.getsInstance().getRoomId())) {
            sendMessage(WebSocketManager.TAG_CLOSE);
        }
        if (mIWebSocketListener != null) {
            mIWebSocketListener.closeAudioRoom();
        }
    }

    /**
     * 关闭 WebSocket
     */
    public void cancelWebSocket() {
        if (mWebSocket != null) {
            mWebSocket.close(1001, "close");
            mWebSocket = null;
            mWriteExecutor.shutdown();
        }
        mWriteExecutor = null;
    }

    public static void release() {
        sInstance = null;
    }
}
