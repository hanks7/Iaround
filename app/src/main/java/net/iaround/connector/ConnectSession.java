package net.iaround.connector;

import android.content.Context;
import android.util.Log;

import net.iaround.conf.Common;
import net.iaround.conf.MessageID;
import net.iaround.connector.protocol.SocketGroupProtocol;
import net.iaround.connector.protocol.SocketSessionProtocol;
import net.iaround.model.im.TransportMessage;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.chat.MessagesSendManager;
import net.iaround.ui.datamodel.StartModel;

import java.util.Timer;
import java.util.TimerTask;

/**
 * <b>此类作为单例使用 session服务器连接 ,session管理器,管理连接,重连</b><br>
 * 1、登录连续两次登录时间差小于10秒时，不做请求<br>
 * 2、每15s检测当前session连接状态,如果连接失败就重连,否则就发送心跳包<br>
 * 3、连接失败会一直请求重新连接上去<br>
 * 4、连接成功之后会把timer作为心跳继续跟服务端保持连接<br>
 * 5、注销登录的时候通过调用reset()停止timer和ConnectSession<br>
 *
 * @author linyg@iaround.net
 * @author shifengxiong
 * @ClassName: ConnectSession
 * @date 2012-2-27 上午10:22:40
 * @date 2014-5-8 modify
 */
public class ConnectSession {

    private final String TAG = "ConnectSession";

    private static String mSessionAddress;
    private static ConnectSession connect;
    private Timer mSessionTimer; // session连接定时器
    public int reconnectSeesionCount = 0; // 重连次数

    /**
     * 10连接session成功，11连接session失败
     */
    private int sessionStatus;
    private ConnectorManage connectorCore;
    private long connect_time = 0; // 上一次重连时间
    private boolean isupdateKeyWord = false;

    //	private int mTimerCheckPeriod = 15 * 1000;// 坚持网络连接时间间隔
    private int mTimerCheckPeriod = 3 * 1000;// 坚持网络连接时间间隔

    private ConnectSession(Context context) {
        connectorCore = ConnectorManage.getInstance(context);
    }

    public static ConnectSession getInstance(Context context) {
        if (connect == null) {
            connect = new ConnectSession(context);
        }
        return connect;
    }

    /**
     * 返回session当前的连接状态
     */
    public int getSessionStatus() {
        return this.sessionStatus;
    }

    /**
     * 设置session服务器的连接状态
     */
    public void setSessionStatus(int status) {
        this.sessionStatus = status;
    }

    /**
     * 请求连接session服务
     *
     * @param focuseReconnect 强制登录请求
     */
    public void loginSession(final Context context, boolean focuseReconnect) {
        CommonFunction.log(TAG, "loginSession() into, focuseReconnect=" + focuseReconnect);
        if (System.currentTimeMillis() - connect_time < 10 * 1000) {
            return;
        }
        connect_time = System.currentTimeMillis();
        setSessionStatus(SessionStatus.CONNECTING);
        if (focuseReconnect) {
            reconnectSeesionCount = 0;
        }

        // 当第一次连接时
        if (mSessionTimer == null) {
            mSessionTimer = new Timer();
            mSessionTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // 检测与session服务器连接状态,如果是连接的，发送心跳包,如果未连接，重新发起连接
                    if (!checkSessionHeart() || getSessionStatus() != SessionStatus.CONNECTED) {
                        connectSession(context);
                    } else {
                        SocketConnection socket = connectorCore.getSocketInstance(1);
                        if (socket != null) {
                            socket.sentHeartBeatMsg();
                        } else {
                            setSessionStatus(SessionStatus.FAILURE);
                        }
                    }
                }
            }, 0, mTimerCheckPeriod);
        }
    }

    /*
     * 登录session服务
     */
    private void connectSession(Context context) {
        CommonFunction.log(TAG, "connectSession() into");
        reconnectSeesionCount++;
        // 连接超过n次则认为时连接失败
        if (reconnectSeesionCount > 100000000) {
            if (mSessionTimer != null) {
                mSessionTimer.cancel();
            }
            mSessionTimer = null;
            setSessionStatus(SessionStatus.FAILURE);
            return;
        }
        CommonFunction.log(TAG, "connectSession() to close session Socket!");
        connectorCore.closeSession();
        CommonFunction.log(TAG, "connectSession() to connect session Socket!");
        connectorCore.connetSession();// 连接session服务器
    }

    /**
     * session连接响应
     */
    public void sessionResponse(Context context, TransportMessage message) {
        int id = message.getMethodId();
        if (id == MessageID.LOGINSESSION_Y) { // 成功登录session服务 连接成功后，清空动态消息
            Common.getInstance().setDynamicCount(0);

            reconnectSeesionCount = 0;
            Log.i(TAG, "sessionResponse() -------LOGINSESSION_Y=" + MessageID.LOGINSESSION_Y);
            setSessionStatus(SessionStatus.CONNECTED);

            if (!isupdateKeyWord) {
                StartModel.getInstance().checkKeyWord(context);
                isupdateKeyWord = true;
            }

            // 切换网络后，如果在聊吧内，Socket恢复后，发送进入聊吧消息
            long groupId = Common.getInstance().getmCurrentGroupId();
            if (groupId > 0) {
                SocketGroupProtocol.groupComeIn(context, "" + groupId);
            }

            // 更新国家地区和语言
            SocketSessionProtocol.sessionUpdateCountryAndLanguage(context);

            MessagesSendManager.getManager(context).ResendSendingMessage();

        } else if (id == MessageID.LOGINSESSION_N) { // 登录session服务失败
            setSessionStatus(SessionStatus.FAILURE);
            Log.i("-------LOGINSESSION_N", MessageID.LOGINSESSION_N + "");
        }
    }

    /**
     * 释放
     *
     * @return void
     * @Title: reset
     */
    public void reset() {
        if (connectorCore != null) {
            setSessionStatus(SessionStatus.FAILURE);
            connectorCore.closeSession();
        }
        if (mSessionTimer != null) {
            mSessionTimer.cancel();
            mSessionTimer = null;
        }
        connect = null;
    }

    /**
     * session连接状态
     */
    public class SessionStatus {
        /**
         * 10连接中, session成功11 ,连接 session失败 12
         */
        public final static int CONNECTING = 10;
        public final static int CONNECTED = 11;
        public final static int FAILURE = 12;
    }

    /**
     * 设置session地址
     *
     * @param session
     */
    public static void setSessionAddress(String session) {
        mSessionAddress = session;
    }

    /**
     * 获取session地址
     *
     * @return
     */
    public static String getSessionAddress() {
        return mSessionAddress;
    }

    /**
     * 检查心跳是否正常,当心跳记录时间大于每次的心跳时间,则认为心跳停止
     *
     * @return true为心跳正常，false为心跳停止
     */
    public boolean checkSessionHeart() {
        SocketConnection socket = connectorCore.getSocketInstance(1);
        if (socket != null) {
            return socket.checkHeart();
        }
        return true;
    }
}
