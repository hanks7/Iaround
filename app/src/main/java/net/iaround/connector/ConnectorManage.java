package net.iaround.connector;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import net.android.volley.ConnectorConfig;
import net.android.volley.Request;
import net.android.volley.Response.ErrorListener;
import net.android.volley.Response.Listener;
import net.android.volley.VolleyError;
import net.iaround.BaseApplication;
import net.iaround.BuildConfig;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.ErrorCode;
import net.iaround.conf.MessageID;
import net.iaround.connector.protocol.LoginHttpProtocol;
import net.iaround.model.im.TransportMessage;
import net.iaround.privat.library.VolleyUtil;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.tools.TimeFormat;
//import net.iaround.utils.NativeLibUtil;
import net.iaround.utils.YJConnectorConfig;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;


/**
 * 通信模块：通信的公共模块，所有的网络消息发送出去，以及接收返回的消息都将经过
 * 1、当需要发送网络请求时，将top的参数进行签名封装之后，请求http发送出去，并且每次请求都将返回生成一个请求标识，作为返回时的筛选标识；
 * 2、当为http请求数据
 * ，若需要使用缓存数据的模块，若请求的是第0页，则首先从数据库中查找，若没有数据，则将页码设置为1，之后才请求网络获取数据。数据返回则保存到数据库中。
 *
 * @author u6 by linyg
 * @ClassName: ConnectorCore
 * @Description: 通信模块
 * @date 2011-5-10 下午04:46:55
 */
public class ConnectorManage {

    private final String TAG = "ConnectorManage";
    /**
     * 自定义完整Url
     */
    public final static int HTTP_OTHER = -1;
    /**
     * 登录服
     */
    public final static int HTTP_LOGIN = 1;
    /**
     * 业务服
     */
    public final static int HTTP_BUSINESS = 2;
    /**
     * 圈子服
     */
    public final static int HTTP_GROUP = 3;
    /**
     * 支付服
     */
    public final static int HTTP_PAY = 4;
    /**
     * 用户服
     */
    public final static int HTTP_USER = 5;
    /**
     * 金币模块
     **/
    public final static int HTTP_GOLD = 6;
    /**
     * 推送
     **/
    public final static int HTTP_PUSH = 7;
    /**
     * 图片业务
     **/
    public final static int HTTP_PHOTO = 8;
    /**
     * 好友关系
     **/
    public final static int HTTP_FRIEND = 9;
    /**
     * 附近
     **/
    public final static int HTTP_NEAR = 10;
    /**
     * 推荐
     **/
    public final static int HTTP_RECOMMAND = 11;
    /**
     * 游戏服
     */
    public final static int HTTP_GAME = 12;
    /**
     * 搭讪游戏-真心话大冒险
     ***/
    public final static int HTTP_SOCIALGAME = 13;
    /**
     * 动态模块
     ***/
    public final static int HTTP_DYNAMIC = 14;
    /**
     * 关系模块
     */
    public final static int HTTP_RELATION = 15;
    /**
     * 贴吧模块
     */
    public final static int HTTP_POSTBAR = 16;
    /**
     * 游戏中心模块
     */
    public final static int HTTP_GAME_CENTER = 17;
    /**
     * 聊天室模块
     */
    public final static int HTTP_ROOMBIZ_CENTER = 18;
    /**
     * 聊吧模块
     */
    public final static int HTTP_CHATBAR = 19;

    /**
     * 首页
     */
    public final static int HTTP_HOME_PAGE = 20;

    /**
     * 主机地址
     */
    private static final String BASE_HOST_URL = "http://portal.iaround.com";

    /**
     * 技能地址
     */
    private static final String BASE_SKILL_URL = "http://skill.iaround.com";

    private static Map<String, String> mapfilterRetry;

    // 单例
    private static ConnectorManage sInstance;
    // session服务器连接
    private static SocketConnection sessionSocket = null;

    // 群组服务器
    private static SocketConnection groupSocket = null;

    // 聊吧服务器
    private static SocketConnection chatbarSocket = null;

    // 常规http连接数
    private AtomicLong mHttpCount = new AtomicLong(0);

    // 线程池，作为网络请求
    private final int DEFAULT_THREAD_POOL_SIZE = 3;
    private ThreadPoolExecutor executor;

    private WeakReference<CallBackNetwork> mCallBack = null;// 普通回调和地图回调
    private CallBackNetwork mWorldMsgCallBack;// 世界消息列表回调
    private CallBackNetwork mSkillMsgCallBack;// 技能消息列表回调
    private CallBackNetwork mChatbarZoomWindowCallBack;// 聊吧最小化回调
    private static CallBackNetwork mServiceCallBack;//聊吧服务的回调

    private Context mContext;

    private VolleyUtil mVolleyUtil;

    private String mKeyCommon;

    private ChatReceivedHandler chatReceivedHandler;

    // 5.1用于网络错误测试
    private AtomicLong error101Times = new AtomicLong();
    private AtomicLong error107Times = new AtomicLong();

    private int loginRetryTime = 0;

    private ConnectorManage(Context context) {
        ConnectorConfig.setServer(Config.TEST_SERVER);
        YJConnectorConfig.setServer(Config.TEST_SERVER);
        mVolleyUtil = VolleyUtil.getDefault();
        VolleyUtil.setDebug(Config.DEBUG);
        if (context == null) {
            mContext = BaseApplication.appContext;
        } else {
            mContext = context.getApplicationContext();
        }
        chatReceivedHandler = ChatReceivedHandler.getInstance(mContext, this);
        if (Config.isTestServer) {
            VolleyUtil.setEncry("ok3000000");
        }
        initMapFilterRetry();
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);

    }

    /*
     * 过滤掉不需要超时重发的协议
     */
    private void initMapFilterRetry() {
        mapfilterRetry = new LinkedHashMap<String, String>();
        mapfilterRetry.put("/user/dynamic/add_5_6", "/user/dynamic/add_5_6");
        mapfilterRetry.put("/topic/add_5_6", "/topic/add_5_6");
        mapfilterRetry.put("/postbar/topic/add", "/postbar/topic/add");
        mapfilterRetry.put(LoginHttpProtocol.LOGIN, LoginHttpProtocol.LOGIN);

    }

    public static ConnectorManage getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ConnectorManage(context);
        }

        return sInstance;
    }

    /**
     * 设置当前的监听的页面action
     *
     * @param callBack
     */
    public void setCallBackAction(CallBackNetwork callBack) {
        if (callBack != null) {
            mCallBack = new WeakReference<CallBackNetwork>(callBack);
        } else {
            if (mCallBack != null) {
                mCallBack.clear();
            }
        }
    }

    public void setmSkillMsgCallBack(CallBackNetwork mSkillMsgCallBack) {
        this.mSkillMsgCallBack = mSkillMsgCallBack;
    }

    public void setmWorldMsgCallBack(CallBackNetwork mWorldMsgCallBack) {
        this.mWorldMsgCallBack = mWorldMsgCallBack;
    }

    public void setChatbarZoomWindowCallBack(CallBackNetwork callBack) {
        this.mChatbarZoomWindowCallBack = callBack;
    }

    public void setServiceCallBack(CallBackNetwork callBack) {
        mServiceCallBack = callBack;
    }

    /**
     * 获得当前的socket实例
     *
     * @param type 1为session ,2为group，3为chatbar
     * @return
     */
    public SocketConnection getSocketInstance(int type) {
        if (type == 1) {
            return sessionSocket;
        } else if (type == 2) {
            return groupSocket;
        } else if (type == 3) {
            return chatbarSocket;
        }
        return null;
    }

    /**
     * 连接session
     */
    public synchronized void connetSession() {
        if (sessionSocket == null) {
            String address = ConnectSession.getSessionAddress();
            if (!CommonFunction.isEmptyOrNullStr(address)) {
                try {
                    CommonFunction.log(TAG, "connetSession() address===" + address);
                    sessionSocket = new SocketConnection(address, sInstance, MessageID.SESSION, mContext);

                    sessionSocket.startConnect();
                } catch (IOException e) {
                    CommonFunction.log(TAG, "connetSession() connect fail address=" + address);
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 关闭session连接
     */
    public synchronized void closeSession() {
        ConnectSession.getInstance(mContext)
                .setSessionStatus(ConnectSession.SessionStatus.FAILURE);
        if (sessionSocket != null) {
            CommonFunction.log(TAG, "closeSession() sessionSocket stop!");
            sessionSocket.stop();
            sessionSocket = null;

        }
    }

    /**
     * 连接group
     */
    public void connetGroup() {
        if (groupSocket == null) {
            String address = ConnectGroup.getGroupAddress();
            if (!CommonFunction.isEmptyOrNullStr(address)) {
                try {
                    CommonFunction.log(TAG, "connetGroup()() address===" + address);
                    groupSocket = new SocketConnection(address, sInstance, MessageID.GROUP, mContext);

                    groupSocket.startConnect();
                } catch (IOException e) {
                    CommonFunction.log(TAG, "connetSession() connect fail address=" + address);
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 关闭group连接
     */
    public void closeGroup() {
        ConnectGroup.getInstance(mContext).setStatus(ConnectGroup.GroupStatus.FAILURE);
        if (groupSocket != null) {
            groupSocket.stop();
            groupSocket = null;
        }
    }

    /**
     * 发送业务服务消息
     */
    public long sendSessionMessage(final TransportMessage message,
                                   final long randomFlag) throws IOException {

        return sendSessionMessage(message, randomFlag, mCallBack);
    }


    public long sendSessionMessage(final TransportMessage message,
                                   final long randomFlag, final WeakReference<CallBackNetwork> callback) throws IOException {
        if (sessionSocket == null) {
            String address = ConnectSession.getSessionAddress();
            if (!CommonFunction.isEmptyOrNullStr(address)) {
                //gh
                CommonFunction.log(TAG, "sendSessionMessage() address:" + address);
                sessionSocket = new SocketConnection(address, sInstance, MessageID.SESSION, mContext);
                // 如果是私聊发不出去，则立即改写数据库和聊天界面
                if (message.getMethodId() == MessageID.SESSION_SEND_PRIVATE_CHAT ||
                        message.getMethodId() == MessageID.SESSION_PRIVATE_AUDIO_BEGIN ||
                        message.getMethodId() == MessageID.SESSION_PRIVATE_AUDIO_END) {
                    chatReceivedHandler.handleSendError(0, randomFlag);
                }
            } else {
                return -3;
            }
        } else {
            // 检查心跳是否停止
            if (!ConnectSession.getInstance(mContext).checkSessionHeart()) {
                // 第一重连
                sessionSocket.stop();
                // 第二告诉页面失败,也就是告诉消息列表失败
                chatReceivedHandler.handleSendError(0, randomFlag);
                return randomFlag;
            }
        }
        executor.execute(new Runnable() {
            public void run() {
                try {
                    CommonFunction.log(TAG, "sendSessionMessage() message:" + message.getContentBody());
                    sessionSocket.sendMessage(message);
//                    if (mCallBack == null) {
//                        CommonFunction.toastMsg(mContext, "null");
//                    }
                } catch (Exception e) {
                    // 如果是私聊发不出去，则立即改写数据库和聊天界面
                    if (message.getMethodId() == MessageID.SESSION_SEND_PRIVATE_CHAT ||
                            message.getMethodId() == MessageID.SESSION_PRIVATE_AUDIO_BEGIN ||
                            message.getMethodId() == MessageID.SESSION_PRIVATE_AUDIO_END) {
                        chatReceivedHandler.handleSendError(0, randomFlag);
                    }
                }
            }
        });
        return randomFlag;
    }


    /**
     * 发送group消息
     */
    public long sendGroupMessage(final TransportMessage message,
                                 final long randomFlag) throws IOException {
        if (groupSocket == null) {
            CommonFunction.log(TAG, "sendGroupMessage() groupSocket == null");
            String address = ConnectGroup.getGroupAddress();
            if (!CommonFunction.isEmptyOrNullStr(address)) {

                //gh
                CommonFunction.log(TAG, "sendGroupMessage() address:" + address);
                groupSocket = new SocketConnection(address, sInstance, MessageID.GROUP, mContext);
                chatReceivedHandler.handleSendError(0, randomFlag);
            } else {
                return randomFlag;
            }
        } else {
            // 检查心跳是否停止
            if (!ConnectGroup.getInstance(mContext).checkGroupHeart()) {
                // 第一重连
                CommonFunction.log(TAG, "sendGroupMessage() groupSocket ==心跳已经停止 groupSocket.stop");
                groupSocket.stop();
                // 第二告诉页面失败,也就是告诉消息列表失败
                return randomFlag;
            }
        }

        executor.execute(new Runnable() {
            public void run() {
                try {
                    CommonFunction.log(TAG, "sendGroupMessage() message[" + message.getMethodId() + "]:" + message.getContentBody());
                    groupSocket.sendMessage(message);
                } catch (Exception e) {
                    if (message.getMethodId() == MessageID.GROUP_SEND_MESSAGE) {
                        chatReceivedHandler.handleSendError(0, randomFlag);
                    }
                }
            }

        });
        return randomFlag;
    }

    /**
     * 接收消息处理分类: <br>
     * 1.[只需要直接处理,不需要传到页面]的消息(eg:重新登录session服务器 ) <br>
     * (直接处理的情况也分为两种)<br>
     * ----直接走FilterUtil,处理数据的问题<br>
     * ----如果涉及的UI处理的情况,例如弹窗,Toast或者有声音,那么这个时候需要交给UIMainHandler来处理<br>
     * 2.[既要在处理,处理之后传到页面]的消息(eg:发送消息返回的结果消息)<br>
     */
    public void onReceiveMessage(TransportMessage message) {

        handleSharedPrefrenceParam();

        int id = message.getMethodId();
        String contentJson = message.getContentBody();


        if (id == MessageID.SESSION_RELOGIN) {
            // 重新登录session服务器
            ConnectSession.getInstance(mContext).loginSession(mContext, false);
        } else if (id == MessageID.GROUP_RELOGIN) {
            // 重新登录group服务器
            ConnectGroup.getInstance(mContext).loginGroup(mContext, false);
        } else {

            boolean isNeedDispatch = true;
            boolean isHandled = chatReceivedHandler.handleMessage(id, contentJson);

            if (!isHandled) {
                isNeedDispatch = FilterUtil.filterReceiveSocket(mContext, message);
            }
            if (mCallBack != null && mCallBack.get() != null && isNeedDispatch) {
                // 消息分发到各个页面
                mCallBack.get().onReceiveMessage(message);
            }
            if (mWorldMsgCallBack != null && isNeedDispatch) {
                // 消息分发世界消息列表
                mWorldMsgCallBack.onReceiveMessage(message);
            }
            if (mSkillMsgCallBack != null && isNeedDispatch) {
                // 消息分发到技能消息列表
                mSkillMsgCallBack.onReceiveMessage(message);
            }
            if (mChatbarZoomWindowCallBack != null && isNeedDispatch) {
                //最小化聊吧窗口需要接受用户被动上下麦消息
                mChatbarZoomWindowCallBack.onReceiveMessage(message);
            }
            if (mServiceCallBack != null && isNeedDispatch) {
                // 消息分发到服务回调
                mServiceCallBack.onReceiveMessage(message);
            }

        }

    }


    /**
     * 发送回调消息
     *
     * @param e    1表示发送成功，0表示不成功
     * @param flag 本次请求的标识
     * @time 2011-5-26 下午04:50:11
     * @author:linyg
     */
    public void onSendCallBack(int e, long flag) {
        // 只处理发送协议层面的,既能不能发送,
        if (mCallBack != null && mCallBack.get() != null) {
            mCallBack.get().onSendCallBack(e, flag);
        }
        if (mServiceCallBack != null) {
            mServiceCallBack.onSendCallBack(e, flag);
        }
    }

    /**
     * 重连成功回调
     */
    public void onConnected() {
        if (mCallBack != null && mCallBack.get() != null) {
            mCallBack.get().onConnected();
        }
        if (mServiceCallBack != null) {
            mServiceCallBack.onConnected();
        }
    }

    public static void loadImage(final String path, final ImageView iv) {

        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                return getNetWorkBitmap(path);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                //对返回的图像判断是否为空
                if (bitmap != null) {
                    //如果不为空则显示图片
                    iv.setImageBitmap(bitmap);
                }
            }
        }.execute();
    }


    /**
     * 获取网络图片
     *
     * @param urlString
     * @return
     */
    public static Bitmap getNetWorkBitmap(String urlString) {
        URL imgUrl = null;
        Bitmap bitmap = null;
        try {
            imgUrl = new URL(urlString);
            // 使用HttpURLConnection打开连接
            HttpURLConnection urlConn = (HttpURLConnection) imgUrl.openConnection();
            urlConn.setDoInput(true);
            urlConn.connect();
            // 将得到的数据转化成InputStream
            InputStream is = urlConn.getInputStream();
            // 将InputStream转换成Bitmap
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            System.out.println("[getNetWorkBitmap->]MalformedURLException");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("[getNetWorkBitmap->]IOException");
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 读取网络图片
     *
     * @param url
     * @param saveFolder 文件保存文件夹
     * @return Bitmap
     * @throws Exception
     */
    public Bitmap getBitmap(String url, String saveFolder) throws Exception {
        return getBitmap(url, saveFolder, null, 0, true);
    }

    public Bitmap getBitmap(String url, String saveFolder, String imageExt, int round,
                            boolean saveSD) throws Exception {
        DownLoadBitmap downBitmap = new DownLoadBitmap(mContext, url, imageExt, round);
        return downBitmap.getBitmap(saveFolder, saveSD);
    }

    /**
     * @param url      地址
     * @param params   参数
     * @param callback 接口回调
     * @return
     * @throws ConnectionException
     * @Title: simplePost
     * @Description: 普通url请求用的post方法
     */
    public long simplePost(final String url, final String params,
                           final HttpCallBack callback) throws ConnectionException {
        HttpType httpType = new HttpType();
        httpType.url = url;
        httpType.type = 0;
        return asynPost(httpType, params, callback);
    }

    /**
     * @param url    url地址
     * @param params 参数
     * @return
     * @Title: asynGet
     * @Description: 普通url用的同步Get请求
     */
    public String syncGet(final String url, String params) {
        String result = "";
        try {
            result = mVolleyUtil.syncGetStringWithBodyParams(url, params);
        } catch (VolleyError e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param httptype
     * @param params
     * @Title: asynPost
     * @Description: 普通url用，异步post请求
     */
    public long asynPost(final HttpType httptype, String params, final HttpCallBack callback) {

        final long flag = mHttpCount.incrementAndGet();

        int isRetry = 0;

        if (mapfilterRetry.containsValue(httptype.url)) {
            isRetry = 2;
        }

        // 临时接口修改
        if (httptype.url.contains("v1/chatbar/user/info")) {
            httptype.url = "http://portal.iaround.com/v1/chatbar/user/info";
        } else if (httptype.url.contains("v1/chatbar/group/adduser_2_5")) {
            httptype.url = "http://portal.iaround.com/v1/chatbar/group/adduser_2_5";
        } else if (httptype.url.contains("/v1/chatbar/simple")) {
            httptype.url = "http://portal.iaround.com/v1/chatbar/simple";
        } else if (httptype.url.contains("v1/chatbar/top/hot")) {//热门
            httptype.url = "http://portal.iaround.com/v1/chatbar/top/hot";
        } else if (httptype.url.contains("v1/chatbar/top/my")) {//家族
            httptype.url = "http://portal.iaround.com/v1/chatbar/top/my";
        } else if (httptype.url.contains("v1/top/index")) {//魅力榜,富豪榜
            httptype.url = "http://portal.iaround.com/v1/top/index";
        } else if (httptype.url.contains("v1/chatbar/online")) {//在线用户
            httptype.url = "http://portal.iaround.com/v1/chatbar/online";
        } else if (httptype.url.contains("v1/gift/store/list"))//商店礼物列表
        {
            httptype.url = "http://portal.iaround.com/v1/gift/store/list";
        } else if (httptype.url.contains("v1/gift/bag/list"))//背包礼物列表
        {
            httptype.url = "http://portal.iaround.com/v1/gift/bag/list";
        } else if (httptype.url.contains("v1/gift/bag/send"))//发送背包礼物
        {
            httptype.url = "https://portal.iaround.com/v1/gift/bag/send";
        } else if (httptype.url.contains("v1/gift/store/send"))//发送商店礼物
        {
            httptype.url = "http://portal.iaround.com/v1/gift/store/send";
        } else if (httptype.url.contains("v1/gift/receive/list")) {
            httptype.url = "http://portal.iaround.com/v1/gift/receive/list";
        } else if (httptype.url.contains("v1/chatbar/detail")) {//聊吧资料详情

            httptype.url = "http://portal.iaround.com/v1/chatbar/detail";

        } else if (httptype.url.contains("v1/chatbar/group/follow")) {//聊吧关注

            httptype.url = "http://portal.iaround.com/v1/chatbar/group/follow";

        } else if (httptype.url.contains("v1/chatbar/group/cancelfollow")) {//取消关注

            httptype.url = "http://portal.iaround.com/v1/chatbar/group/cancelfollow";

        } else if (httptype.url.contains("v1/chatbar/group/join")) {//加入聊吧

            httptype.url = "http://portal.iaround.com/v1/chatbar/group/join";

        } else if (httptype.url.contains("v1/chatbar/group/fanslist")) {//聊吧粉丝列表

            httptype.url = "http://portal.iaround.com/v1/chatbar/group/fanslist";

        } else if (httptype.url.contains("v1/chatbar/group/requestjoin")) {
            //请求加入

            httptype.url = "http://portal.iaround.com/v1/chatbar/group/requestjoin";

        } else if (httptype.url.contains("v1/chatbar/group/agree")) {
            //同意加入

            httptype.url = "http://portal.iaround.com/v1/chatbar/group/agree";

        } else if (httptype.url.contains("v1/chatbar/group/refuse")) {//拒绝加入

            httptype.url = "http://portal.iaround.com/v1/chatbar/group/refuse";

        } else if (httptype.url.contains("v1/chatbar/group/freshman")) {//获取推荐列表

            httptype.url = "http://portal.iaround.com/v1/chatbar/group/freshman";

        } else if (httptype.url.contains("v1/chatbar/word/send")) {
            httptype.url = "http://portal.iaround.com/v1/chatbar/word/send";
        } else if (httptype.url.contains("v1/chatbar/group/invite/join")) {

            httptype.url = "http://portal.iaround.com/v1/chatbar/group/invite/join";

        } else if (httptype.url.contains("v1/chatbar/group/invite/chat")) {

            httptype.url = "http://portal.iaround.com/v1/chatbar/group/invite/chat";

        } else if (httptype.url.contains("v1/chatbar/group/homecall")) {

            httptype.url = "http://portal.iaround.com/v1/chatbar/group/homecall";

        } else if (httptype.url.contains("/v1/chatbar/word/send")) {//聊吧内发送世界消息
            httptype.url = "http://portal.iaround.com/v1/chatbar/word/send";

        } else if (httptype.url.contains("/users/nearlist_7_0")) { //附近的人
            //JAVA接口换成PHP接口
            httptype.url = "http://portal.iaround.com/v1/near/getlist";

        } else if (httptype.url.contains("/v1/task/getlotterynum")) { //分享成功获得免费抽奖次数

            httptype.url = "http://task.iaround.com/v1/task/getlotterynum";

        } else if (httptype.url.contains("/v1/task/getlottery")) { //抽奖接口

            httptype.url = "http://task.iaround.com/v1/task/getlottery";

        } else if (httptype.url.contains("/v1/chatbar/setshare")) { //分享成功获得免费抽奖次数
            httptype.url = "http://portal.iaround.com/v1/chatbar/setshare";
        }

        CommonFunction.log(TAG, "asynPost() request url===" + httptype.url + "?" + params);
//        //测试接口
//        if (httptype.url.contains("portal.iaround.com") || httptype.url.contains("skill.iaround.com") || httptype.url.contains("task.iaround.com")) {
//            httptype.url = "http://test.task.iaround.com" + httptype.url.substring(httptype.url.indexOf(".com") + 4);
//        }
//        CommonFunction.log(TAG, "test asynPost() request url===" + httptype.url + "?" + params);

        SharedPreferenceUtil shareDate = SharedPreferenceUtil.getInstance(mContext);
        mVolleyUtil.postStringWithBodyParams(httptype.url, params, new Listener<String>() {
            @Override
            public void onResponse(String result, Request<String> request) {
                if (result == null || TextUtils.isEmpty(result)) {
                    // 服务器返回空值异常
                    StackTraceElement[] items = Thread.currentThread().getStackTrace();
                    String logContent = "ConnectorManage-E_107-url==" + httptype.url + "--";
                    logContent += "times==" + error107Times.incrementAndGet();
                    logContent += "items==" + CommonFunction.StackTraceLog2String(items);
                    CommonFunction.NetWorkErrorLog(logContent);

                    CommonFunction.log(TAG, "asynPost() error response url===" + httptype.url + ", result: " + result);
                    // 获取失败
                    FilterUtil.filterReceiveHttpError(mContext, ErrorCode.E_107, flag);
                    if (callback != null) {
                        callback.onGeneralError(ErrorCode.E_107, flag);
                    }
                } else {
                    CommonFunction.log(TAG, "asynPost() success response url===" + httptype.url + ", result: " + result);
                    // 获取成功
                    FilterUtil.filterReceiveHttp(mContext, httptype.url, result, flag, request);
                    if (callback != null) {
                        callback.onGeneralSuccess(result, flag);
                    } else {
                        CommonFunction.log(TAG, "asynPost() success response url===" + httptype.url + ", but weakref callback null");
                    }
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                String s = error.toString();
                Log.e("tag", "s==" + s);
                // 网络异常和网络超时走这里
                StackTraceElement[] items = Thread.currentThread().getStackTrace();
                String logContent = "ConnectorManage-E_101-url==" + httptype.url + "--";
                logContent += "times==" + error101Times.incrementAndGet() + "--";
                logContent += "items==" + CommonFunction.StackTraceLog2String(items);
                CommonFunction.NetWorkErrorLog(logContent);
                error.printStackTrace();

                if (callback != null) {
                    callback.onGeneralError(ErrorCode.E_101, flag);
                }
            }
        }, isRetry + httptype.type, shareDate.getString(SharedPreferenceUtil.FLAG_NET));
        return flag;
    }

    /**
     * 异步post
     *
     * @param url  请求url
     * @param map  请求参数
     * @param type 1为登录请求，2为业务请求，3为群组求情，4为支付请求,5用户资料
     * @return long
     */
    public long asynPost(final String url, final LinkedHashMap<String, Object> map, final int type, final HttpCallBack callback) {

        HttpType httptype = getHttpUrl(url, type);

        if (TextUtils.isEmpty(httptype.url)) {
            return 0;
        }
        //gh kek加密
        String params = ConnectorConfig.getSigniture(map, getKey());

        if (mapfilterRetry.containsKey(url)) {
            mapfilterRetry.put(url, httptype.url);
        }
        return asynPost(httptype, params, callback);
    }


    public long asynPost(final String url, final LinkedHashMap<String, Object> map, final HttpCallBack callback) {
        HttpType httpType = new HttpType();
        httpType.url = BASE_SKILL_URL + url;
        httpType.type = 1;
        if (TextUtils.isEmpty(httpType.url)) {
            return 0;
        }
        //gh kek加密
        String params = ConnectorConfig.getSigniture(map, getKey());
        if (mapfilterRetry.containsKey(url)) {
            mapfilterRetry.put(url, httpType.url);
        }
        return asynPost(httpType, params, callback);
    }

    public static String transMapToString(Map map) {
        java.util.Map.Entry entry;
        StringBuffer sb = new StringBuffer();
        for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); ) {
            entry = (java.util.Map.Entry) iterator.next();
            sb.append(entry.getKey().toString()).append("'").append(null == entry.getValue() ? "" :
                    entry.getValue().toString()).append(iterator.hasNext() ? "^" : "");
        }
        return sb.toString();
    }

    /**
     * 同步post
     *
     * @param url
     * @param map
     * @param @return
     * @return String
     * @throws ConnectionException
     */
    public String syncPost(final String url, final LinkedHashMap<String, Object> map, int type) {
        HttpType httptype = getHttpUrl(url, type);
        if (CommonFunction.isEmptyOrNullStr(httptype.url)) {
            return null;
        }
        String signiture = ConnectorConfig.getSigniture(map, getKey());
        CommonFunction.log(TAG, "syncPost() url==" + httptype.url + "?" + signiture);


        //测试接口
//        if(httptype.url.contains(".com")){
//            httptype.url = "http://192.168.2.204"+httptype.url.substring(httptype.url.indexOf(".com")+4);
//        }


        try {
            //gh kek 加密
            SharedPreferenceUtil shareDate = SharedPreferenceUtil.getInstance(mContext);
            String flagNet = shareDate.getString(SharedPreferenceUtil.FLAG_NET);
            String result = mVolleyUtil.syncPostStringWithBodyParams(httptype.url, signiture, httptype.type, flagNet);
            CommonFunction.log(TAG, "syncPost() url==" + httptype.url + "?" + signiture + ",  result===" + result);
            return result;
        } catch (VolleyError e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            // java.lang.IllegalStateException Content has not been provided net.iaround.privat.library.VolleyUtil.syncPostStringWithBodyParams(Unknown Source)
            e.printStackTrace();
            return null;
        }
    }

    private HttpType getHttpUrl(String url, int type) {
        HttpType httpType = new HttpType();
        String httpUrl = "";
        httpType.type = 0;
        switch (type) {
            case HTTP_OTHER: // 自定义链接
                httpUrl = url;
                break;
            case HTTP_LOGIN: // 登录服
                if (!CommonFunction.isEmptyOrNullStr(Config.loginUrl))
                    httpUrl = Config.loginUrl + url;
                httpType.type = 1;
                break;
            case HTTP_BUSINESS: // 业务服  //加密
                if (!CommonFunction.isEmptyOrNullStr(Config.sBusinessHost))
                    httpUrl = Config.sBusinessHost + url;
                httpType.type = 1;

                break;
            case HTTP_GROUP: // 群组服
                if (!CommonFunction.isEmptyOrNullStr(Config.sGroupHost))
                    httpUrl = Config.sGroupHost + url;
                httpType.type = 1;
                break;
            case HTTP_PAY: // 支付服
                if (!CommonFunction.isEmptyOrNullStr(Config.sPayHost))
                    httpUrl = Config.sPayHost + url;
//                    httpUrl = "http://test.pay.iaround.com" + url;
                break;
            case HTTP_USER:
                if (!CommonFunction.isEmptyOrNullStr(Config.sBusinessHost))
                    httpUrl = Config.sBusinessHost + url;
                httpType.type = 1;
                break;
            case HTTP_GOLD:
                if (!CommonFunction.isEmptyOrNullStr(Config.sGoldHost))
                    httpUrl = Config.sGoldHost + url;

                break;
            case HTTP_PUSH: //已经弃用
                if (!CommonFunction.isEmptyOrNullStr(Config.pushUrl))
                    httpUrl = Config.pushUrl + url;
                break;
            case HTTP_PHOTO:
                if (!CommonFunction.isEmptyOrNullStr(Config.sPhotoHost))
                    httpUrl = Config.sPhotoHost + url;
                break;
            case HTTP_FRIEND://Config.sFriendHost
                if (!CommonFunction.isEmptyOrNullStr(Config.sFriendHost))
                    httpUrl = Config.sFriendHost + url;
                httpType.type = 1;
                break;
            case HTTP_NEAR:
                if (!CommonFunction.isEmptyOrNullStr(Config.sNearHost))
                    httpUrl = Config.sNearHost + url;
                httpType.type = 1;
                break;
            case HTTP_RECOMMAND:
//				if ( !CommonFunction.isEmptyOrNullStr( Config.sRecommendHost ) )
//					httpUrl = Config.sRecommendHost + url;
                break;
            case HTTP_GAME:
                if (!CommonFunction.isEmptyOrNullStr(Config.sGameHost))
                    httpUrl = Config.sGameHost + url;
                break;
            case HTTP_SOCIALGAME:
                if (!CommonFunction.isEmptyOrNullStr(Config.sSocialgame))
                    httpUrl = Config.sSocialgame + url;
                break;
            case HTTP_DYNAMIC:// 动态业务
                if (!CommonFunction.isEmptyOrNullStr(Config.sDynamic))
                    httpUrl = Config.sDynamic + url;
                httpType.type = 1;
                break;
            case HTTP_RELATION:// 关系业务
                if (!CommonFunction.isEmptyOrNullStr(Config.sRelaction))
                    httpUrl = Config.sRelaction + url;
                httpType.type = 1;
                break;
            case HTTP_POSTBAR:// 贴吧业务
//				if ( !CommonFunction.isEmptyOrNullStr( Config.sPostbar ) )
//					httpUrl = Config.sPostbar + url;
//				httpType.type = 1;
                break;
            case HTTP_GAME_CENTER:// 游戏中心业务
                if (!CommonFunction.isEmptyOrNullStr(Config.sGameCenter))
                    httpUrl = Config.sGameCenter + url;
                break;
            case HTTP_ROOMBIZ_CENTER:// 聊天室业务
                if (!CommonFunction.isEmptyOrNullStr(Config.sRoombiz))
                    httpUrl = Config.sRoombiz + url;
                break;
            case HTTP_CHATBAR:// 聊吧业务
                if (!CommonFunction.isEmptyOrNullStr(Config.sChatBar))
                    httpUrl = Config.sChatBar + url;
                httpType.type = 1;
                break;
            case HTTP_HOME_PAGE:// 首页
                if (!CommonFunction.isEmptyOrNullStr(Config.sGetGameChatInfo))
                    httpUrl = Config.sGetGameChatInfo + url;
                httpType.type = 1;
                break;
        }
        httpType.url = httpUrl;

        return httpType;
    }

    // 关闭本次请求
    public void closeGeneralHttp(long flag) {
        mVolleyUtil.stop(flag);
    }

    public long getHttpRequestCount() {
        return mHttpCount.get();
    }

    /**
     * 关闭所有socket连接
     */
    public void close() {
        closeSession();
        closeGroup();
    }

    /**
     * 生成key,登陆之前使用
     *
     * @return String
     */
    private String createKey() {
        Random rand = new Random(System.currentTimeMillis());
        this.mKeyCommon = Integer.toString(1000 + rand.nextInt(10000000));
        return this.mKeyCommon;
    }

    /**
     * 获取key，若本地从未获取过，则自动生成
     *
     * @return String
     */
    public String getKey() {
        if (CommonFunction.isEmptyOrNullStr(this.mKeyCommon)) {
            this.mKeyCommon = createKey();
        }
        CommonFunction.log("getKey() key===", this.mKeyCommon);
        return this.mKeyCommon;

    }

    /**
     * 从服务端获取key,并生成本地的kek
     *
     * @param _key
     * @return void
     */
    public void setKey(String _key) {
        this.mKeyCommon = _key;
        SharedPreferenceUtil.getInstance(mContext).putString(SharedPreferenceUtil.IAROUND_SESSIONKEY, this.mKeyCommon);
    }

    /**
     * 检查网络
     * <p/>
     * return boolean
     */
    public boolean CheckNetwork(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                // 获得网络连接管理的对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 重置
    public void reset() {
        close();
        ConnectLogin.reset();
        ConnectSession.getInstance(mContext).reset();
        ConnectGroup.getInstance(mContext).reset();
        sInstance = null;
    }

    /**
     * 处理保存在SharedPrefrence中的参数
     */
    private void handleSharedPrefrenceParam() {

        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(mContext);
        long uid = Common.getInstance().loginUser.getUid();
        String dndKey = SharedPreferenceUtil.DND_SETTING + uid;
        String startTimeKey = SharedPreferenceUtil.REC_START_TIME + uid;
        String endTimeKey = SharedPreferenceUtil.REC_END_TIME + uid;

        String notificKey = SharedPreferenceUtil.NOTFIC_SETTING + uid;
        String spKey = SharedPreferenceUtil.VOICE_SETTINGS + uid;
        String vibrateKey = SharedPreferenceUtil.VIBRATE_ENABLE + uid;
        String vibrate_enable_back = SharedPreferenceUtil.VIBRATE_ENABLE_BACK + uid;

        String voiceChatKey = SharedPreferenceUtil.VOICE_CHAT + uid;
        String voiceDynamicKey = SharedPreferenceUtil.VOICE_DYNAMIC + uid;
        boolean dnd_flag = sp.getBoolean(dndKey);
        int start = sp.getInt(startTimeKey);
        int end = sp.getInt(endTimeKey);
        Date date = new Date();
        Date startTime = TimeFormat.getTime(start);
        Date endTime = TimeFormat.getTime(end);
        if (startTime.after(endTime)) {
            endTime.setDate(endTime.getDate() + 1);
        }
        boolean topActivity = CommonFunction.isTopActivity(mContext);
        /**
         * topAcyivity判断是否在应用程序中 dnd_flag免打扰设置状态 startTime、endTime免打扰时间段
         */
        if (!topActivity && dnd_flag && date.after(startTime) && endTime.after(date)) {
            sp.putString(spKey, "false%false%false%false");
            sp.putBoolean(vibrateKey, false);
            sp.putBoolean(notificKey, false);
        } else if (topActivity) {
            if (sp.has(voiceChatKey) && sp.has(voiceDynamicKey)) {
                sp.putBoolean(notificKey, true);
                boolean boolean1 = sp.getBoolean(voiceChatKey);
                boolean boolean2 = sp.getBoolean(voiceDynamicKey);
                sp.putString(spKey, "false%" + boolean1 + "%" + boolean2 + "%true");
            } else if (sp.has(voiceChatKey)) {
                sp.putBoolean(notificKey, true);
                boolean boolean1 = sp.getBoolean(voiceChatKey);
                sp.putString(spKey, "false%" + boolean1 + "%true%true");
            } else if (sp.has(voiceDynamicKey)) {
                sp.putBoolean(notificKey, true);
                boolean boolean2 = sp.getBoolean(voiceDynamicKey);
                sp.putString(spKey, "false%true%" + boolean2 + "%true");
            } else {
                sp.putBoolean(notificKey, true);
                sp.putString(spKey, "false%true%true%true");
            }
            if (sp.has(vibrate_enable_back)) {
                boolean boolean1 = sp.getBoolean(vibrate_enable_back);
                sp.putBoolean(vibrateKey, boolean1);
            } else {
                sp.putBoolean(vibrateKey, true);
            }
        } else {
            if (!sp.has(spKey)) {
                sp.putBoolean(notificKey, true);
                sp.putString(spKey, "false%true%true%true");
            }
        }
    }

    public class HttpType {
        public String url;
        public int type;
    }


    /* 不对内容 encode
     * */

    /**
     * 异步post
     *
     * @param url  请求url
     * @param map  请求参数
     * @param type 1为登录请求，2为业务请求，3为群组求情，4为支付请求,5用户资料
     * @return long
     */
    public long asynPostNoEncode(final String url, final LinkedHashMap<String, Object> map,
                                 final int type, final HttpCallBack callback) {

        HttpType httptype = getHttpUrl(url, type);

        if (TextUtils.isEmpty(httptype.url)) {
            return 0;
        }
        //gh kek加密
        String params = YJConnectorConfig.getSigniture(map, getKey());

        if (mapfilterRetry.containsKey(url)) {
            mapfilterRetry.put(url, httptype.url);
        }
        return asynPost(httptype, params, callback);
    }


    public long asynPostNoEncode(final String url, final LinkedHashMap<String, Object> map, final HttpCallBack callback) {
        HttpType httpType = new HttpType();
        httpType.url = BASE_HOST_URL + url;
        httpType.type = 1;
        if (TextUtils.isEmpty(httpType.url)) {
            return 0;
        }
        //gh kek加密
        String params = YJConnectorConfig.getSigniture(map, getKey());
        if (mapfilterRetry.containsKey(url)) {
            mapfilterRetry.put(url, httpType.url);
        }
        return asynPost(httpType, params, callback);
    }

}
