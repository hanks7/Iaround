package net.iaround.connector;

import android.content.Context;
import android.text.TextUtils;

import net.iaround.conf.MessageID;
import net.iaround.connector.protocol.SocketGroupProtocol;
import net.iaround.model.im.TransportMessage;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.datamodel.GroupModel;

import java.util.Timer;
import java.util.TimerTask;

/**
 * <b>此类作为单例使用 groupSession服务器连接 ,groupSession管理器,管理连接,重连</b><br>
 * 1、登录连续两次登录时间差小于10秒时，不做请求<br>
 * 2、每15s检测当前session连接状态,如果连接失败就重连,否则就发送心跳包<br>
 * 3、连接失败会一直请求重新连接上去<br>
 * 4、连接成功之后会把timer作为心跳继续跟服务端保持连接<br>
 * 5、注销登录的时候通过调用reset()停止timer和ConnectGroup<br>
 *
 * @author linyg@iaround.net
 * @date 2012-2-27 上午10:28:30
 */
public class ConnectGroup {
    private final String TAG = this.getClass().getName();

    private static String mGroupAddress;
    private static ConnectGroup connect;
    private ConnectorManage connectorCore;

    private Timer mGroupTimer; // 登录房间定时器
    public int connectGroupCount = 0; // 登录房间连接次数
    private int groupConnectStatus;
    private long connect_time = 0; // 上次连接时间

    private int mTimerCheckPeriod = 15 * 1000;// 坚持网络连接时间间隔

    private ConnectGroup(Context context) {
        connectorCore = ConnectorManage.getInstance(context);
    }

    public static ConnectGroup getInstance(Context context) {
        if (connect == null) {
            connect = new ConnectGroup(context);
        }
        return connect;
    }

    // 设置当前连接状态
    public void setStatus(int status) {
        this.groupConnectStatus = status;
    }

    // 当前的group连接状态
    public int getStatus() {
        return this.groupConnectStatus;
    }

    /**
     * 登录群组
     *
     * @return void
     */
    public void loginGroup(final Context context, boolean focuseReconnect) {
        CommonFunction.log(TAG, "loginGroup() into, focuseReconnect=" + focuseReconnect);
        if (System.currentTimeMillis() - connect_time < 10 * 1000) {
            return;
        }
        connect_time = System.currentTimeMillis();
        setStatus(GroupStatus.CONNECTING);
        if (focuseReconnect) {
            connectGroupCount = 0;
        }
        // 当第一次连接时
        if (mGroupTimer == null) {
            mGroupTimer = new Timer();
            mGroupTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!checkGroupHeart() || getStatus() != GroupStatus.CONNECTED) {
                        connectGroup(context);
                    } else {
                        SocketConnection socket = connectorCore.getSocketInstance(2);
                        if (socket != null) {
                            socket.sentHeartBeatMsg();
                        } else {
                            setStatus(GroupStatus.FAILURE);
                        }
                    }
                }
            }, 0, mTimerCheckPeriod);
        }
    }

    // 连接并登录群组服务
    private void connectGroup(Context context) {
        CommonFunction.log(TAG, "connectGroup() into");
        connectGroupCount++;
        // 连接超过n次则认为时连接失败
        if (connectGroupCount > 100000000) {
            if (mGroupTimer != null) {
                mGroupTimer.cancel();
            }
            mGroupTimer = null;
            setStatus(GroupStatus.FAILURE);
            return;
        }
        CommonFunction.log(TAG, "connectGroup() to close group socket");
        connectorCore.closeGroup();
        CommonFunction.log(TAG, "connectGroup() to connect group socket");
        connectorCore.connetGroup();
    }

    /**
     * 登录群组响应
     *
     * @param @param msg
     * @return void
     */
    public void groupLoginResponse(Context context, TransportMessage message) {
        int id = message.getMethodId();
        if (id == MessageID.GROUP_LOGIN_Y) {

            connectGroupCount = 0;
            setStatus(GroupStatus.CONNECTED);
            // 当正在群组页面时,重进入群的页面
            GroupModel model = GroupModel.getInstance();
            if (model.isInGroupChat()) {
                String goupIdStr = model.getGroupId();
                if (!TextUtils.isEmpty(goupIdStr)) {
                    SocketGroupProtocol.groupComeIn(context, goupIdStr);
                }
            }
        } else if (id == MessageID.GROUP_LOGIN_N) {
            setStatus(GroupStatus.FAILURE);
        }
    }

    /**
     * 检查当前的是否还心跳
     *
     * @return
     */
    public boolean checkGroupHeart() {
        SocketConnection socket = connectorCore.getSocketInstance(2);
        if (socket != null) {
            return socket.checkHeart();
        }
        return false;
    }

    /**
     * 设置group地址
     *
     * @return
     */
    public static String getGroupAddress() {
        return mGroupAddress;
    }

    /**
     * 读取group地址
     *
     * @param groupAddress
     */
    public static void setGroupAddress(String groupAddress) {
        mGroupAddress = groupAddress;
    }

    /**
     * 释放
     *
     * @Title: reset
     */
    public void reset() {
        if (connectorCore != null) {
            setStatus(GroupStatus.FAILURE);
            connectorCore.closeGroup();
        }
        if (mGroupTimer != null) {
            mGroupTimer.cancel();
            mGroupTimer = null;
        }
        connect = null;
    }

    /**
     * 房间连接状态
     */
    public class GroupStatus {
        /**
         * 连接中
         */
        public final static int CONNECTING = 10;
        /**
         * 连接成功
         */
        public final static int CONNECTED = 11;
        /**
         * 连接失败
         */
        public final static int FAILURE = 12;
    }
}
