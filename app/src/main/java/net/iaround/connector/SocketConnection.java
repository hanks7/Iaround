package net.iaround.connector;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import net.android.volley.ValidData;
import net.iaround.BaseApplication;
import net.iaround.conf.Config;
import net.iaround.conf.MessageID;
import net.iaround.connector.protocol.SocketGroupProtocol;
import net.iaround.connector.protocol.SocketSessionProtocol;
import net.iaround.model.im.JSData;
import net.iaround.model.im.TransportMessage;
import net.iaround.privat.library.VolleyUtil;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.im.Bytes;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

/**
 * socket通信核心类: socket连接、心跳、接收、发送消息等模块 <br>
 * 1、其中每个socket连接都将开启一个心跳子线程、接收子线程；[session & groupSession]<br>
 * 2、其中心跳线程原理：心跳通过 @ConnectSession @ConnectGroup 中的sessionTimer
 * 来发送,每隔15s发送一次心跳,记录当前心跳的时间<br>
 * 3、重连没有次数限制，由心跳启动，除非心跳停止；<br>
 *
 * @author linyg
 */
public class SocketConnection {
    //	private String TAG = SocketConnection.class.getName();
    private static final String TAG = "socket";
    private static boolean DEBUG = false; //Config.DEBUG;

    private Context context;
    private Socket socket;// socket实体
    private String host;// 主机地址
    private int port;// 端口

    private Thread receiveThread = null;// 接收数据的线程
    private InputStream inStream = null;// 输入流
    private OutputStream outStream = null;// 输出流
    private ConnectorManage core; // socket与业务通信唯一出口
    private TransportMessage heartMsg = new TransportMessage();

    private int type = 0;// Socket的类型 [session: @MessageID.SESSION / groupSession: @MessageID.GROUP]
    private int heartId = 0;// 心跳的id @MessageID.SESSION_HEART @MessageID.GROUP_HEART

    private int reportcount = 0;// 重连的次数
    private boolean loopReceiver = false; // 是否停止循环接收消息

    private long currentHeartTime = 0; // 最近一次心跳的更新时间
    private int mHeartTimes = 0;//心跳次数,每次

    private int timeout = 1000 * 15; // 超时时间，初始值15秒
    //	private int timeout = 1000 * 3; // 超时时间，初始值3秒
    private long mConnectTime = 0; // 上一次连接的时间
    private Map<Long, CallBackNetwork> mReques = new HashMap<Long, CallBackNetwork>();

    /**
     * socket 连接类库
     *
     * @param address
     * @param type    连接类型 1为session服务器，2为圈子服务器
     * @throws ConnectionException
     */
    public SocketConnection(String address, ConnectorManage core, int type,
                            Context context) throws IOException {
        if (!CommonFunction.isEmptyOrNullStr(address)) {
            String[] hostPort = address.split(":");
            if (hostPort != null && hostPort.length > 1) {
                this.host = hostPort[0];
                this.port = Integer.parseInt(hostPort[1]);
            }
        }

        this.core = core;
        this.type = type;
        this.context = context;

        if (type == MessageID.SESSION) {
            heartId = MessageID.SESSION_HEART;
        } else if (type == MessageID.GROUP) {
            heartId = MessageID.GROUP_HEART;
        }
//		else if ( type == MessageID.CHATBAR )
//		{
//			heartId = MessageID.CHATBAR_HEARTBEAT;
//		}

        //初始化心跳包
        heartMsg.setContentLength(0);
        heartMsg.setMethodId(heartId);
        heartMsg.setContentBody("");
        currentHeartTime = System.currentTimeMillis();
    }

    /**
     * 初始化连接
     *
     * @throws ConnectionException
     * @throws IOException
     */
    private synchronized void connect() throws IOException {
        log(TAG, "connect() into");

        // 关闭并停止上一个socket
        stop();

        // 启动接收数据线程
        if (receiveThread == null) {
            receiveThread = new Thread(new ReceiveWorker());
            loopReceiver = true;
            receiveThread.start();
        }
    }

    /*
     * socket网络连接
     */
    private void socketNetConnect() throws IOException {
        try {
            InetSocketAddress addr = new InetSocketAddress(this.host, this.port);
            socket = new Socket();
            socket.connect(addr, timeout);
            // 设置网络连接参数
            if (socket != null) {
                socket.setTcpNoDelay(true);// 数据不作缓冲，立即发送
                socket.setSoLinger(true, 0);// socket关闭时，立即释放资源
                socket.setKeepAlive(true); // 长连接
                inStream = socket.getInputStream();
                outStream = socket.getOutputStream();
            }

            loopReceiver = true;
        } catch (ClassCastException e) {
            if (Build.VERSION.SDK_INT == 26) {
                Log.e(TAG, "Swallowed ClassCastException, see https://issuetracker.google.com/issues/63649622");
            } else {
                throw e;
            }
        } catch (Exception e) {
            throw e;
        }

    }

    /*
     * 发送心跳包
     */

    public void sentHeartBeatMsg() {
        currentHeartTime = System.currentTimeMillis();
        mHeartTimes++;
        try {
            heartMsg.setContentBody("");
            sendMessage(heartMsg);
        } catch (Exception e) {
            log(TAG, "send heartbeat fail, socket type:" + type + ", exception:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 重新连接 （一般是由心跳以及网络断线时，直接调用）
     */
    public synchronized void startConnect() {
        // 异常重连1次以上，则不再重连，让心跳间隔驱动重连
        if (reportcount > 0 && (System.currentTimeMillis() - mConnectTime < 5000)) {
            return;
        }
        mConnectTime = System.currentTimeMillis();
        reportcount++;
        try {
            connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息，网络断开连接时，等待5秒，仍无法连接上，则提示用户重连
     *
     * @param message
     * @throws IOException
     */
    public synchronized void sendMessage(final TransportMessage message) throws Exception {
        log(TAG, "sendMessage() id: " + message.getMethodId() + ", body=" + message.getContentBody());
        if (!loopReceiver) {
            connect();
        }

        String content = message.getContentBody();
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("pack", "006");
        String a = "";
        if (!TextUtils.isEmpty(content)) {
            a = VolleyUtil.getDefault().set006(content);
        }
        map.put("data", a);
        JSONObject json;
        json = new JSONObject(map);
        String sendBody = json.toString();
        message.setContentBody(sendBody);

        log(TAG, "sendMessage() after encode, send body====" + message.getContentBody());
        int len = message.getContentBody().getBytes().length;
        ByteBuffer buffer = ByteBuffer.allocate(8 + len);
        buffer.putInt(message.getMethodId());
        buffer.putInt(len);
        if (len > 0) {
            buffer.put(message.getContentBody().getBytes());
        }

        if (null != outStream) {
            outStream.write(buffer.array());
            outStream.flush();
        }
    }

    /**
     * 发送消息，网络断开连接时，等待5秒，仍无法连接上，则提示用户重连
     *
     * @param message
     * @throws IOException
     */
    public synchronized void sendMessageHeat(final TransportMessage message) throws Exception {
        log(TAG, "sendMessageHeat() id: " + message.getMethodId() + ";body=" + message.getContentBody());
        if (!loopReceiver) {
            connect();
        }
//		if ( !Config.isTestServer )
        {
            String content = message.getContentBody();
            LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
            map.put("pack", "001");
            map.put("data", content);
            JSONObject json;
            json = new JSONObject(map);
            String sendBody = json.toString();
            message.setContentBody(sendBody);
        }
        //		Log.d("socket", "send body====" + message.getContentBody());
        int len = message.getContentBody().getBytes().length;
        ByteBuffer buffer = ByteBuffer.allocate(8 + len);
        buffer.putInt(message.getMethodId());
        buffer.putInt(len);
        if (len > 0) {
            buffer.put(message.getContentBody().getBytes());
        }

        if (null != outStream) {
            outStream.write(buffer.array());
            outStream.flush();
        }

    }

    /**
     * 消息接收器
     */


    private class ReceiveWorker implements Runnable {
        public void run() {
            try {
                socketNetConnect();
            } catch (IOException e1) {
                String st = null;
                if (type == MessageID.SESSION) {
                    st = "session";
                } else if (type == MessageID.GROUP) {
                    st = "group";
                } else {
                    st = "unknown";
                }
                log(TAG, "socket[" + st + "] connect catch IOException: " + e1.getMessage());
                e1.printStackTrace();
            }

            TransportMessage message;
            ByteArrayInputStream bin;
            GZIPInputStream gzin;
            BufferedReader reader;
            String line = "";
            StringBuilder result;

            if (type == MessageID.SESSION) {
                SocketSessionProtocol.sessionLogin(getContext(), ConnectorManage.getInstance(getContext()).getKey());
            } else if (type == MessageID.GROUP) {
                SocketGroupProtocol.groupLogin(getContext(), ConnectorManage.getInstance(getContext()).getKey());
            }
//			else if ( type == MessageID.CHATBAR )
//			{
//			}

            loopReceiver = true;
            log(TAG, "************循环接受服务端的数据: key=" + ConnectorManage.getInstance(getContext()).getKey() + " socket type=" + type);
            while (loopReceiver) {
                try {
                    // 功能号(4位)
                    byte[] methodBytes = new byte[4];
                    if (inStream == null)
                        break;
                    inStream.read(methodBytes);
                    int method = Bytes.toInt(methodBytes);
                    if (method < 1) {
                        log(TAG, "************ receive: loop receive quit, " + " method=" + method);
                        loopReceiver = false;
                        break;
                    }
                    if (method > 100000) {
                        //Log.d( TAG, "************ receive: method=" + method );
                    }
                    log(TAG, "************ receive: method=" + method);
                    message = new TransportMessage();
                    message.setMethodId(method);

                    // 消息长度(4位)
                    byte[] lenBytes = new byte[4];
                    inStream.read(lenBytes);


                    int contentLength = Bytes.toInt(lenBytes);

                    log(TAG, "************ receive: content Length:" + contentLength);

                    // 是否解压标志位
                    int type = 0;
                    int n = 0;
                    int isSub = 0;
                    type = inStream.read();
                    if (type != 123) {
                        // 内容长度-1，是因为是否压缩的标志是包含在内容里面的
                        isSub = 1;
                        contentLength--;
                    } else {
                        n = 1;
                    }

                    if (contentLength > 4096000) {
                        log(TAG, "************ receive: content too long, not handle!");
                        continue;
                    }

                    message.setContentLength(contentLength);
                    // 消息内容 4M最大数据
                    byte[] content = new byte[contentLength];
                    if (n == 1) {
                        content[0] = 123;
                    }

                    String bytes = "";
                    while (n < contentLength) {
                        int count = inStream.read(content, 0 + n, contentLength - n);
                        if (count < 0)
                            continue;
                        n += count;
                    }

//					if ( !Config.isTestServer )
                    {
                        String srcData = "";
                        JSData jsbean = null;
                        if (type == 123) {
                            srcData = Bytes.toString(content);
                            jsbean = GsonUtil.getInstance()
                                    .getServerBean(srcData, JSData.class);
                        } else if (type == 0) {
                            bin = new ByteArrayInputStream(content);
                            gzin = new GZIPInputStream(bin);
                            reader = new BufferedReader(
                                    new InputStreamReader(gzin, "UTF-8"));
                            result = new StringBuilder();
                            while ((line = reader.readLine()) != null) {
                                result.append(line);
                            }
                            message.setContentBody(result.toString());
                        } else {
                            srcData = Bytes.toString(content);
                            jsbean = new JSData();
                            jsbean.pack = getType((byte) type);
                            jsbean.data = srcData;

                        }

                        if (jsbean != null) {
                            log(TAG, "************ receive: pack =" + jsbean.pack);
                            if ("001".equals(jsbean.pack)) {
                                message.setContentBody(jsbean.data);
                            } else if ("009".equals(jsbean.pack)) {
                                String lastData = ValidData.getSrc009(jsbean.data,
                                        VolleyUtil.getEncry());

                                message.setContentBody(lastData);
                            } else if ("008".equals(jsbean.pack)) {
                                String lastData = ValidData
                                        .getSrc008(jsbean.data, VolleyUtil.getEncry());
                                message.setContentBody(lastData);
                            } else if ("007".equals(jsbean.pack)) {
                                String lastData = ValidData.getSrc007(jsbean.data);
                                message.setContentBody(lastData);
                            } else if ("006".equals(jsbean.pack)) {
                                String lastData = ValidData
                                        .getSrc006(jsbean.data, VolleyUtil.getEncry());
                                message.setContentBody(lastData);
                            } else if ("005".equals(jsbean.pack)) {
                                String lastData = ValidData.getSrc005(jsbean.data);
                                message.setContentBody(lastData);
                            }
                        }
                    }
//					else
//					{
//						if ( type == 0 )
//						{ // 解压缩
//							if ( contentLength > 0 )
//							{
//								bin = new ByteArrayInputStream( content );
//								gzin = new GZIPInputStream( bin );
//								reader = new BufferedReader(
//									new InputStreamReader( gzin, "UTF-8" ) );
//								result = new StringBuilder( );
//								while ( ( line = reader.readLine( ) ) != null )
//								{
//									result.append( line );
//								}
//								message.setContentBody( result.toString( ) );
//							}
//						}
//						else
//						{ // 不需要解压缩
//							message.setContentBody( Bytes.toString( content ) );
//						}
//					}
                    // 接收到消息回调
                    int id = message.getMethodId();

//					if ( id != MessageID.SESSION_HEART_Y && id != MessageID.GROUP_HEART_Y &&
//							id != MessageID.CHATBAR_HEARTBEAT_Y )
                    if (id != MessageID.SESSION_HEART_Y && id != MessageID.GROUP_HEART_Y) {
                        try {
                            core.onReceiveMessage(message);
                        } catch (Exception e) {
                            // 业务异常捕获,不需要关闭Socket重连
                            log(TAG, "************ receive: " + "socket message business handling exception happen, Message Id=" + id);
                            e.printStackTrace();
                        }
                    } else {
                        mHeartTimes = 0;//重置心跳
                    }

                    reportcount = 0;
                    log(TAG, "************ receive: Message Id=" + id + ", content:" + message.getContentBody());
                } catch (Exception e) {
                    log(TAG, "************ receive: Exception happen ->" + e.getMessage());

                    ConnectSession.getInstance(getContext()).setSessionStatus(ConnectSession.SessionStatus.FAILURE);
                    ConnectGroup.getInstance(getContext()).setStatus(ConnectGroup.GroupStatus.FAILURE);
                    stop();
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 检查心跳是否正常,当心跳记录时间大于每次的心跳时间,则认为心跳停止
     *
     * @return true为心跳正常，false为心跳停止
     */
    public boolean checkHeart() {
        //log(TAG,"checkHeart() mHeartTimes >>>>>" +mHeartTimes);
        if (currentHeartTime > 0 && mHeartTimes > 3) {
            //log(TAG,"checkHeart() 心跳停止");
            return false;
        }
        //log(TAG,"checkHeart() 心跳在跳动");
        return true;
    }

    /**
     * 停止，关闭socket
     *
     * @return void
     */
    public void stop() {
        try {
            log(TAG, "stop() close socket and clear resource!");
            loopReceiver = false;
            clear();
            stopSocketThtread();
            closeInStream();
            closeOutStream();
            closeSocket();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void stopSocketThtread() {
        try {
            if (receiveThread != null)
                receiveThread.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            receiveThread = null;
        }
    }

    /*
     * 关闭输入流
     */
    private void closeInStream() {
        try {
            if (inStream != null)
                inStream.close();
        } catch (IOException e) {
        }
        inStream = null;
    }

    /*
     * 关闭输出流
     */
    private void closeOutStream() {
        try {
            if (outStream != null)
                outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        outStream = null;
    }

    /*
     * 关闭socket
     */
    private void closeSocket() {
        if (socket != null) {
            try {
                socket.close();
                log(TAG, "closeSocket() socket type:" + type);
            } catch (Exception e) {
                e.printStackTrace();
            }
            socket = null;
        }
    }

    /*
    对发送的数据放入map中，通过map进行回调 ，成功的时候从map中移除
     */
    public void put(long key, CallBackNetwork callback) {
        mReques.put(key, callback);

        final long flag = key;

        socketHandle.postDelayed(new Runnable() {
            @Override
            public void run() {
                CallBackNetwork call = mReques.get(flag);
                log(TAG, "socket send timeout >>>>>>" + flag);
                if (call != null) {
                    call.onSendCallBack(0, flag);
                }
            }
        }, 15000);

    }

    public void move(long key) {
        if (mReques.containsKey(key))
            mReques.remove(key);
    }

    public CallBackNetwork get(long key) {
        CallBackNetwork outCall = null;
        outCall = mReques.get(key);

        return outCall;
    }

    public void clear() {
        Iterator<Entry<Long, CallBackNetwork>> headersIterator = mReques.entrySet().iterator();
        while (headersIterator.hasNext()) {
            Entry<Long, CallBackNetwork> entry = headersIterator.next();
            log(TAG, "key CallBackNetwork ==" + entry.getKey());
            entry.getValue().onSendCallBack(0, entry.getKey());
        }
        mReques.clear();
    }

    private Handler socketHandle = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {

        }
    };


    String getType(byte type) {
        String result = "001";
        switch (type) {
            case 1:
                result = "001";
                break;
            case 2:
                result = "002";
                break;
            case 3:
                result = "003";
                break;
            case 4:
                result = "004";
                break;
            case 5:
                result = "005";
                break;
            case 6:
                result = "006";
                break;
            case 7:
                result = "007";
                break;
            case 8:
                result = "008";
                break;
            case 9:
                result = "009";
                break;
            case 10:
                result = "010";
                break;
        }
        return result;
    }

    private Context getContext() {
        if (context == null)
            context = BaseApplication.appContext;
        return context;
    }

    private static void log(String TAG, String content) {
        if (DEBUG) {
            CommonFunction.log(TAG, content);
        }
    }

}
