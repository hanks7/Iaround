package net.iaround.im.push.filter;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import net.iaround.im.aidl.IPushMessageFilter;
import net.iaround.im.push.ICacheMessageHandler;
import net.iaround.im.push.IPushMessageHandler;
import net.iaround.im.push.PushMessage;
import net.iaround.utils.logger.Logger;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by liangyuanhuan on 07/12/2017.
 */

public class PushMessageFilter extends IPushMessageFilter.Stub {
    public static final String TAG = "PushMsgFilter";
    private HashMap<Integer,ArrayList<IPushMessageHandler>> mCmdHandlerMap = null;
    private HandlerThread mThread = null;
    private PushMessageThreadHandler mHandler = null;

    private ArrayList<PushMessage> mCacheMessages; //连接到 STNService 后收到的缓存推送消息
    private ArrayList<ICacheMessageHandler> mCacheHandlerMap = null; //缓存推送消息

    public PushMessageFilter(){

    }

    public synchronized void create(){
        log("create() into");
        mThread = new HandlerThread("STN Service");
        mThread.start();
        mHandler = new PushMessageThreadHandler(this,mThread.getLooper());
    }

    public synchronized void destroy(){
        log("destroy() into");
        if(mThread!=null) {
            mThread.quit();
            mThread = null;
        }
        mHandler = null;
        if(mCmdHandlerMap!=null) {
            mCmdHandlerMap.clear();
            mCmdHandlerMap = null;
        }
        if(mCacheHandlerMap!=null) {
            mCacheHandlerMap.clear();
            mCacheHandlerMap = null;
        }
    }

    //添加消息处理器
    public synchronized void addPushMessageHandler(int cmd, IPushMessageHandler handler){
        if(mCmdHandlerMap==null){
            mCmdHandlerMap = new HashMap<Integer,ArrayList<IPushMessageHandler>>();
        }
        ArrayList<IPushMessageHandler> pushHandlers = mCmdHandlerMap.get(cmd);
        if(pushHandlers==null){
            pushHandlers = new ArrayList<IPushMessageHandler>(0);
            pushHandlers.add(handler);
            mCmdHandlerMap.put(cmd,pushHandlers);
        }else{
            for(int i=0;i<pushHandlers.size();i++){
                if( pushHandlers.get(i) == handler ){
                    log("addPushMessageHandler() handler already exist");
                    return;
                }
            }
            pushHandlers.add(handler);
        }
    }

    //移除消息处理器
    public synchronized void removePushMessageHandler(int cmd, IPushMessageHandler handler){
        if(mCmdHandlerMap==null){
            return;
        }
        ArrayList<IPushMessageHandler> pushHandlers = mCmdHandlerMap.get(cmd);
        if(pushHandlers!=null){
            for(int i=0;i<pushHandlers.size();i++){
                if(pushHandlers.get(i) == handler){
                    pushHandlers.remove(i);
                    break;
                }
            }
            if(pushHandlers.size()==0){
                mCmdHandlerMap.remove(cmd);
            }
        }
    }

    //移除消息处理器
    public synchronized void removeAllPushMessageHandler(){
        if(mCmdHandlerMap==null){
            return;
        }
        mCmdHandlerMap.clear();
    }

    //mCmdHandlerMap -> new Map
    public synchronized void copyToPushMessageHandler(HashMap<Integer,ArrayList<IPushMessageHandler>> dst){
        if(dst!=null){
            Iterator iterator = mCmdHandlerMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Integer key = (Integer) entry.getKey();
                ArrayList<IPushMessageHandler> val = (ArrayList<IPushMessageHandler>)entry.getValue();
                dst.put(key,val);
            }
        }
    }

    //new Map -> mCmdHandlerMap
    public synchronized void copyFromPushMessageHandler(HashMap<Integer,ArrayList<IPushMessageHandler>> src){
        if(src!=null){
            if(mCmdHandlerMap==null){
                mCmdHandlerMap = new HashMap<Integer,ArrayList<IPushMessageHandler>>();
            }
            Iterator iterator = src.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                Integer key = (Integer) entry.getKey();
                ArrayList<IPushMessageHandler> val = (ArrayList<IPushMessageHandler>)entry.getValue();
                mCmdHandlerMap.put(key,val);
            }
        }
    }

    /* 处理接收到的推送消息
    * */
    @Override
    public synchronized boolean onReceiveMessage(int cmdId, byte[] buffer) throws RemoteException {
        log("onReceiveMessage() into, cmdId=" +cmdId);
        if(null!=mCmdHandlerMap && mCmdHandlerMap.containsKey(cmdId)) {
            if(null!=mHandler) {
                PushMessage message = new PushMessage(cmdId, buffer);
                Message msg = mHandler.obtainMessage();
                msg.what = PushMessageThreadHandler.HANDLER_PUSH_MESSAGE;
                msg.obj = message;
                mHandler.sendMessage(msg);
            }
        }else{
            log("onReceiveMessage() no command handler");
        }
        return false;
    }

    //添加缓存消息处理器
    public synchronized void addCacheMessageHandler(ICacheMessageHandler handler){
        if(mCacheHandlerMap==null){
            mCacheHandlerMap = new ArrayList<ICacheMessageHandler>();
            mCacheHandlerMap.add(handler);
        }else{
            for(int i=0;i<mCacheHandlerMap.size();i++){
                if( mCacheHandlerMap.get(i) == handler ){
                    log("addCacheMessageHandler() handler already exist");
                    return;
                }
            }
            mCacheHandlerMap.add(handler);
        }

    }

    //移除缓存消息处理器
    public synchronized void removeCacheMessageHandler(ICacheMessageHandler handler){
        if(mCacheHandlerMap==null){
            return;
        }

        for(int i=0;i<mCacheHandlerMap.size();i++){
            if(mCacheHandlerMap.get(i) == handler){
                mCacheHandlerMap.remove(i);
                break;
            }
        }
    }


    //mCmdHandlerMap -> new Map
    public synchronized void copyToCacheMessageHandler(ArrayList<ICacheMessageHandler> dst){
        if(dst!=null){
            if(mCacheHandlerMap!=null) {
                for (int i = 0; i < mCacheHandlerMap.size(); i++) {
                    dst.add(mCacheHandlerMap.get(i));
                }
            }
        }
    }

    //new Map -> mCmdHandlerMap
    public synchronized void copyFromCacheMessageHandler(ArrayList<ICacheMessageHandler> src){
        if(src!=null){
            if(mCacheHandlerMap==null){
                mCacheHandlerMap = new ArrayList<ICacheMessageHandler>();
            }
            for(int i=0;i<src.size();i++){
                mCacheHandlerMap.add(src.get(i));
            }
        }
    }


    /* 处理缓存的推送消息
   * */
    @Override
    public synchronized boolean onReceiveCache(int cmdId, byte[] buffer) throws RemoteException {
        log("onReceiveCache() into, cmdId=" +cmdId);
        if(cmdId<=0){
            //缓存结束
            if(null!=mHandler) {
                Message msg = mHandler.obtainMessage();
                msg.what = PushMessageThreadHandler.HANDLER_CACHE_MESSAGE;
                mHandler.sendMessage(msg);
            }
        }else{
            //缓存开始
            if(mCacheMessages == null){
                mCacheMessages  = new ArrayList<>();
            }
            PushMessage msg = new PushMessage(cmdId, buffer);
            mCacheMessages.add(msg);
        }
        return false;
    }

    private synchronized void handleReceiveMessage(PushMessage message){
        if(message == null){
            log("handleReceiveMessage() into, message null");
            return;
        }
        log("handleReceiveMessage() into, cmdId=" +message.cmdId);
        if(mCmdHandlerMap!=null) {
            ArrayList<IPushMessageHandler> pushHandlers = mCmdHandlerMap.get(message.cmdId);
            if (pushHandlers != null) {
                if(pushHandlers.size() == 0){
                    log("handleReceiveMessage() no handler in command handler map");
                }else {
                    for (int i = 0; i < pushHandlers.size(); i++) {
                        IPushMessageHandler pushMessageHandler = pushHandlers.get(i);
                        boolean stop = pushMessageHandler.handleReceiveMessage(message);
                        if (stop == true) {
                            break;
                        }
                    }
                }
            }else{
                log("handleReceiveMessage() cannot find handlers from command handler map");
            }
        }else{
            log("handleReceiveMessage() command handler map null");
        }
    }

    private synchronized void handleCacheMessage(){
        log("handleCacheMessage() into");
        if(mCacheMessages == null){
            return;
        }

        for (int i = 0; i < mCacheHandlerMap.size(); i++) {
            ICacheMessageHandler handler = mCacheHandlerMap.get(i);
            mCacheMessages = handler.handleCacheMessage(mCacheMessages);
        }

        //处理剩余的 cache 消息
        //清空 cache message
        if(mCacheMessages!=null) {
            if(mCmdHandlerMap!=null) {
                for (int j = 0; j < mCacheMessages.size(); j++) {
                    PushMessage msg = mCacheMessages.get(j);
                    ArrayList<IPushMessageHandler> pushHandlers = mCmdHandlerMap.get(msg.cmdId);
                    if (pushHandlers != null) {
                        for (int i = 0; i < pushHandlers.size(); i++) {
                            IPushMessageHandler pushMessageHandler = pushHandlers.get(i);
                            boolean stop = pushMessageHandler.handleReceiveMessage(msg);
                            if (stop == true) {
                                break;
                            }
                        }
                    }
                }
            }
            mCacheMessages.clear();
        }
    }

    static class PushMessageThreadHandler extends Handler{
        public static final int HANDLER_PUSH_MESSAGE = 1000;
        public static final int HANDLER_CACHE_MESSAGE = 1001;

        private WeakReference<PushMessageFilter> mPushMessageFilter = null;
        public PushMessageThreadHandler(PushMessageFilter filter, Looper looper){
            super(looper);
            mPushMessageFilter = new WeakReference<PushMessageFilter>(filter);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANDLER_PUSH_MESSAGE:
                    handlePushMessage((PushMessage)msg.obj);
                    break;
                case HANDLER_CACHE_MESSAGE:
                    handleCasheMessage();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }

        private void handlePushMessage(PushMessage message){
            PushMessageFilter filter = mPushMessageFilter.get();
            if(filter!=null){
                filter.handleReceiveMessage(message);
            }else{
                Logger.i(TAG,"PushMessageFilter reference null");
            }
        }

        private void handleCasheMessage(){
            //处理缓存的推送消息
            PushMessageFilter filter = mPushMessageFilter.get();
            if(filter!=null){
                filter.handleCacheMessage();
            }

        }
    }

    public void log(String content){
        Logger.i(TAG,content);
    }
}
