package net.iaround.mic;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.callback.IZegoLiveEventCallback;
import com.zego.zegoliveroom.callback.IZegoLivePlayerCallback;
import com.zego.zegoliveroom.callback.IZegoLivePublisherCallback;
import com.zego.zegoliveroom.callback.IZegoLoginCompletionCallback;
import com.zego.zegoliveroom.callback.IZegoRoomCallback;
import com.zego.zegoliveroom.constants.ZegoAvConfig;
import com.zego.zegoliveroom.constants.ZegoConstants;
import com.zego.zegoliveroom.entity.AuxData;
//import com.zego.zegoliveroom.entity.ZegoCompleteMixStreamInfo;
//import com.zego.zegoliveroom.entity.ZegoMixStreamInfo;
import com.zego.zegoliveroom.entity.ZegoStreamInfo;
import com.zego.zegoliveroom.entity.ZegoStreamQuality;

import net.iaround.BaseApplication;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.Constants;
import net.iaround.tools.CommonFunction;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class: 连麦封装
 * Author：gh
 * Date: 2017/7/6 20:00
 * Email：jt_gaohang@163.com
 */
public class FlyAudioRoom {
    private static final String TAG = "FlyAudioRoom";
    private static FlyAudioRoom instance = null;

    private static ZegoLiveRoom sZegoLiveRoom = null;
    private ZegoLiveRoom mZegoLiveRoom = null;
    private boolean DEBUG = true; //调试日志
    private boolean hasPublish = false;  /** 是否已经推流 */
    private String mPublishStreamID = null;
    private String mMixStreamID = null;
    private boolean enableSpeaker = false;     /** 声音状态*/
    private boolean enableRecorder = false; /** 上麦状态*/
    private long manageId;
    private int position;  /** 上麦位置*/
    private IFlyMediaCallback iFlyMediaCallback;
    //private Map<String,ZegoMixStreamInfo> mixStreamInfoMap = new HashMap<>();
    private int isFlag;     /** 麦位的版本 */
    private boolean isLogin;    /** 登陆状态*/
    private List<ZegoStreamInfo> mListStreamOfRoom = new ArrayList<>();
    public int mixStreamRequestSeq = 1;

    private Timer mSoundWaveTimer = null; //声音波浪定时器
    private GetSoundLevelTask mSoundWaveTimerTask = null; //声音波浪定时任务

    private FlyAudioRoom() {
    }

    public static FlyAudioRoom getInstance() {
        if (instance == null) {
            synchronized (FlyAudioRoom.class) {
                if (instance == null) {
                    instance = new FlyAudioRoom();
                }
            }
        }
        return instance;
    }

//    public static synchronized void initZego(){
//        CommonFunction.log(TAG, "initZego() into");
//        FlyVideoRoom.unInitZego();
//        if(sZegoLiveRoom==null) {
//            sZegoLiveRoom = new ZegoLiveRoom();
//
//            //业务类型
//            ZegoLiveRoom.setBusinessType(0); //0 纯音频走CDN 2一对一视频+纯音频
//
//            // 设置是否开启“测试环境”, true:开启 false:关闭
//            ZegoLiveRoom.setTestEnv(false);
//
//            // 设置设置调试模式
//            if (Config.DEBUG == true) {
//                ZegoLiveRoom.setVerbose(true);
//            } else {
//                ZegoLiveRoom.setVerbose(false);
//            }
//
//            // ！！！注意：这个Appid和signKey需要从server下发到App，避免在App中存储，防止盗用
//            byte[] signKey = {(byte) 0x26, (byte) 0x40, (byte) 0x9e, (byte) 0xad, (byte) 0x23, (byte) 0xfa, (byte) 0x83, (byte) 0x03,
//                    (byte) 0x34, (byte) 0xe8, (byte) 0xb8, (byte) 0x05, (byte) 0x4f, (byte) 0x83, (byte) 0xca, (byte) 0xf1,
//                    (byte) 0x31, (byte) 0xf1, (byte) 0x5d, (byte) 0xa6, (byte) 0x5c, (byte) 0x18, (byte) 0x94, (byte) 0xf7,
//                    (byte) 0x45, (byte) 0x4d, (byte) 0x87, (byte) 0x5b, (byte) 0x43, (byte) 0x8f, (byte) 0x68, (byte) 0x44};
//
//            long appID = 2815417840L;
//            ZegoLiveRoom.setAudioDeviceMode(2);
//
//            // 初始化sdk
//            boolean appId = sZegoLiveRoom.initSDK(appID, signKey, BaseApplication.appContext);
//
//            // 初始化设置级别为"High"
//            ZegoAvConfig zegoAvConfig = new ZegoAvConfig(ZegoAvConfig.Level.High);
//            sZegoLiveRoom.setAVConfig(zegoAvConfig);
//        }
//
//        CommonFunction.log(TAG, "initZego() out");
//    }
//
//
//    public static synchronized void unInitZego(){
//        CommonFunction.log(TAG,"unInitZego() into");
//        if (sZegoLiveRoom != null){
//            sZegoLiveRoom.unInitSDK();
//            sZegoLiveRoom = null;
//        }
//        CommonFunction.log(TAG,"unInitZego() out");
//    }
//
//    public static synchronized ZegoLiveRoom getZegoLiveRoom(){
//        if(sZegoLiveRoom==null){
//            initZego();
//        }
//        return sZegoLiveRoom;
//    }

    public synchronized void init(){
        CommonFunction.log(TAG, "init() into");

        // 创建ZegoAVKit对象, 整个项目中创建一次即可，建议封装成单例使用
        mZegoLiveRoom = InitZegoLiveRoom.getZegoLiveRoom();

        mZegoLiveRoom.setAudioBitrate(32000);
        mZegoLiveRoom.enableAux(true);
        handleMuteState1(true); //mZegoLiveRoom.enableSpeaker(true); //解决有时候没有播放声音

        mZegoLiveRoom.setZegoLivePublisherCallback(new IZegoLivePublisherCallback() {
            @Override
            public void onPublishStateUpdate(int stateCode, String streamId, HashMap<String, Object> info) {
                log(TAG, "IZegoLivePublisherCallback() onPublishStateUpdate()  stateCode=" + stateCode + ", streamId=" + streamId + ", info=" + info);

                if (stateCode == 0) {
                    if (iFlyMediaCallback != null) {
                        iFlyMediaCallback.engineSuccess();
                    }
                    hasPublish = true;

                    //handlePublishSuccMix(streamId, info); 新的SDK没有混流？
                }else{
                    if (iFlyMediaCallback != null) {
                        iFlyMediaCallback.engineError();
                    }
                }
            }

            @Override
            public void onJoinLiveRequest(int i, String s, String s1, String s2) {
                log(TAG, "IZegoLivePublisherCallback() onJoinLiveRequest()  i=" + i + ", s=" + s + ", s1=" + s1 + ", s2="+ s2);

            }

            @Override
            public void onPublishQualityUpdate(String s, ZegoStreamQuality zegoStreamQuality) {
                // 无论推流和拉流 声音的综合质量用 quality 可以反应
                // zegoStreamQuality.quality 0～3 优 良 中 差
                // 差的时候应该向用户提示 提示的间隔要合理不要太频繁 同时应该记录到异常日志以供后台数据分析
                log(TAG,"IZegoLivePublisherCallback() onPublishQualityUpdate() s=" + s  + ", audioBitrate=" + zegoStreamQuality.audioBitrate + ", quality=" + zegoStreamQuality.quality + ", rtt=" + zegoStreamQuality.rtt + ", pktLostRate="+zegoStreamQuality.pktLostRate);

            }

            @Override
            public AuxData onAuxCallback(int dataLen) {
                return null;
            }

            @Override
            public void onCaptureVideoSizeChangedTo(int i, int i1) {

            }

            @Override
            public void onMixStreamConfigUpdate(int i, String s, HashMap<String, Object> hashMap) {
                log(TAG,"IZegoLivePublisherCallback() onMixStreamConfigUpdate() into");
                int seq = -1;
                if (hashMap != null) {
                    seq = (int)hashMap.get(ZegoConstants.StreamKey.MIX_CONFIG_SEQ);
                }
                if (i == 0) {

                    List<String> listUrls = getShareUrlList(hashMap);

                    if(listUrls.size() == 0){
                        log(TAG,"IZegoLivePublisherCallback() onMixStreamConfigUpdate() 混流失败...errorCode: "+ i +" seq: "+ seq);
                    }

                    if(listUrls.size() >= 2){
                        log(TAG,"IZegoLivePublisherCallback() onMixStreamConfigUpdate() 混流地址: "+ listUrls.get(1)+ " seq: "+ seq);

                        // 将混流ID通知观众
                        Map<String, String> mapUrls = new HashMap<>();
                        mapUrls.put(Constants.FIRST_ANCHOR, String.valueOf(true));
                        mapUrls.put(Constants.KEY_MIX_STREAM_ID, s);
                        mapUrls.put(Constants.KEY_HLS, listUrls.get(0));
                        mapUrls.put(Constants.KEY_RTMP, listUrls.get(1));

                        Gson gson = new Gson();
                        String json = gson.toJson(mapUrls);
                        mZegoLiveRoom.updateStreamExtraInfo(json);
                    }
                } else {
                    log(TAG,"IZegoLivePublisherCallback() onMixStreamConfigUpdate() 混流失败...errorCode: "+ i+" seq: "+ seq);
                }
            }
        });

        mZegoLiveRoom.setZegoLivePlayerCallback(new IZegoLivePlayerCallback() {
            @Override
            public void onPlayStateUpdate(int stateCode, String StreamId) {
                log(TAG,"IZegoLivePlayerCallback() onPlayStateUpdate() stateCode="+stateCode+", StreamId="+StreamId);
            }

            @Override
            public void onPlayQualityUpdate(String s, ZegoStreamQuality zegoStreamQuality) {
                // 无论推流和拉流 声音的综合质量用 quality 可以反应
                // zegoStreamQuality.quality 0～3 优 良 中 差
                // 差的时候应该向用户提示 提示的间隔要合理不要太频繁 同时应该记录到异常日志以供后台数据分析
                //log(TAG,"IZegoLivePlayerCallback() onPlayQualityUpdate() s=" + s  + ", audioBitrate=" + zegoStreamQuality.audioBitrate + ", quality=" + zegoStreamQuality.quality + ", rtt=" + zegoStreamQuality.rtt + ", pktLostRate="+zegoStreamQuality.pktLostRate);

            }

            @Override
            public void onInviteJoinLiveRequest(int i, String s, String s1, String s2) {
                log(TAG,"IZegoLivePlayerCallback() onInviteJoinLiveRequest() i="+i+", s="+s+ ", s1=" + s1 +", s2="+s2);
            }

            @Override
            public void onRecvEndJoinLiveCommand(String s, String s1, String s2) {
                log(TAG,"IZegoLivePlayerCallback() onRecvEndJoinLiveCommand() s="+s+", s1="+ s1 + ", s2="+s2);
            }

            @Override
            public void onVideoSizeChangedTo(String s, int i, int i1) {
                log(TAG,"IZegoLivePlayerCallback() onVideoSizeChangedTo() s="+s+", i="+i+", i1="+i1);
            }
        });

        mZegoLiveRoom.setZegoLiveEventCallback(new IZegoLiveEventCallback() {
            @Override
            public void onLiveEvent(int zegoAudioLiveEvent, HashMap<String, String> info) {
                log(TAG,"IZegoLiveEventCallback() onLiveEvent() event="+zegoAudioLiveEvent+", info="+info);
            }
        });

        mZegoLiveRoom.setZegoRoomCallback(new IZegoRoomCallback() {
            @Override
            public void onKickOut(int i, String s) {
                log(TAG,"IZegoRoomCallback() onKickOut()   i="+i+", s="+s);
            }

            @Override
            public void onDisconnect(int i, String s) {
                isLogin = false;
                log(TAG,"IZegoRoomCallback() onDisconnect()   i="+i+", s="+s);
            }

            @Override
            public void onStreamUpdated(int type, ZegoStreamInfo[] zegoStreamInfos, String s) {
                log(TAG, "IZegoRoomCallback() onStreamUpdated stateCode=" + type + ", streamId=" + s + ", zegoStreamInfos.length=" + zegoStreamInfos.length);
                if (zegoStreamInfos != null && zegoStreamInfos.length > 0) {
                    switch (type) {
                        case ZegoConstants.StreamUpdateType.Added:
                            // 新增流
                            handleStreamAdded(zegoStreamInfos, s);
                            break;
                        case ZegoConstants.StreamUpdateType.Deleted:
                            // 移除流
                            handleStreamDeleted(zegoStreamInfos, s);
                            break;
                    }
                }
            }

            @Override
            public void onStreamExtraInfoUpdated(ZegoStreamInfo[] zegoStreamInfos, String s) {
                log(TAG, "IZegoRoomCallback() onStreamExtraInfoUpdated  "+ " streamId=" + s + ", info" + zegoStreamInfos.length);
            }

            @Override
            public void onRecvCustomCommand(String s, String s1, String s2, String s3) {
                log(TAG,"IZegoRoomCallback() onRecvCustomCommand()   s="+s+", s1="+s1+", s2="+s2+", s3="+s3);
            }

            @Override
            public void onTempBroken(int i, String s) {
                log(TAG,"IZegoRoomCallback() onTempBroken()   i="+i+", s="+s);
            }

            @Override
            public void onReconnect(int i, String s) {
                log(TAG,"IZegoRoomCallback() onReconnect()   i="+i+", s="+s);
            }
        });

        CommonFunction.log(TAG, "init() out");
    }

    /**
     * 房间内用户创建流.
     */
    protected synchronized void handleStreamAdded(final ZegoStreamInfo[] listStream, final String room) {
        if (listStream != null && listStream.length > 0) {
            for (ZegoStreamInfo streamInfo : listStream) {
                mListStreamOfRoom.add(streamInfo);
                startPlay(streamInfo.streamID);
                if(mSoundWaveTimerTask!=null){
                    mSoundWaveTimerTask.addUser(streamInfo.streamID);
                }
            }
        }

    }

    /**
     * 房间内用户删除流.
     */
    protected synchronized void handleStreamDeleted(final ZegoStreamInfo[] listStream, final String room) {
        if (listStream != null && listStream.length > 0) {
            for (ZegoStreamInfo streamInfo : listStream) {
                for (ZegoStreamInfo info : mListStreamOfRoom) {
                    if (streamInfo.streamID.equals(info.streamID)) {
                        mListStreamOfRoom.remove(info);
                        break;
                    }
                }
                if(null!=mZegoLiveRoom) {
                    mZegoLiveRoom.stopPlayingStream(streamInfo.streamID);
                }
                if(mSoundWaveTimerTask!=null){
                    mSoundWaveTimerTask.delUser(streamInfo.streamID);
                }
            }
        }
    }

    /**
     * 获取流地址.
     */
    private List<String> getShareUrlList(HashMap<String, Object> info){
        List<String> listUrls = new ArrayList<>();

        if(info != null){
            String[] hlsList = (String[]) info.get(ZegoConstants.StreamKey.HLS_URL_LST);
            if (hlsList != null && hlsList.length > 0) {
                listUrls.add(hlsList[0]);
            }

            String[] rtmpList = (String[]) info.get(ZegoConstants.StreamKey.RTMP_URL_LIST);
            if (rtmpList != null && rtmpList.length > 0) {
                listUrls.add(rtmpList[0]);
            }
        }
        return listUrls;
    }

    protected void handlePublishSuccMix(String streamID, HashMap<String, Object> info) {
//        ZegoMixStreamInfo mixStreamInfo = new ZegoMixStreamInfo();
//        mixStreamInfo.streamID = mPublishStreamID;
//        mixStreamInfo.top = 0;
//        mixStreamInfo.bottom = 1;
//        mixStreamInfo.left = 0;
//        mixStreamInfo.right = 1;
//        mixStreamInfoMap.put(mPublishStreamID,mixStreamInfo);

        //startMixStream();
    }


    private void startMixStream() {
//        ZegoMixStreamInfo[] inputStreamList = new ZegoMixStreamInfo[mixStreamInfoMap.size()];
//        int i = 0;
//        for (Map.Entry<String, ZegoMixStreamInfo> entry : mixStreamInfoMap.entrySet()) {
//            inputStreamList[i] = entry.getValue();
//            i++;
//        }
//        CommonFunction.log(TAG,"startMixStream() mMixStreamID=" + mMixStreamID);
//        ZegoCompleteMixStreamInfo mixStreamConfig = new ZegoCompleteMixStreamInfo();
//        mixStreamConfig.inputStreamList = inputStreamList;
//        mixStreamConfig.outputStreamId = mMixStreamID;
//        mixStreamConfig.outputIsUrl = false;
//        mixStreamConfig.outputWidth = 0;
//        mixStreamConfig.outputHeight = 0;
//        mixStreamConfig.outputFps = 15;
//        mixStreamConfig.outputBitrate = 32000;
//        if(null!=mZegoLiveRoom) {
//            mZegoLiveRoom.mixStream(mixStreamConfig, mixStreamRequestSeq++);
//        }
    }

    public boolean setRoomId(final String roomId){
        this.mMixStreamID = roomId;
        //不设置用户ID会导致拉流失败
        if(null!=mZegoLiveRoom){
            //设置用户ID
            ZegoLiveRoom.setUser(String.valueOf(Common.getInstance().loginUser.getUid()),String.valueOf(Common.getInstance().loginUser.getNoteName(true)));

            //登陆房间
            isLogin = mZegoLiveRoom.loginRoom(String.valueOf(roomId), ZegoConstants.RoomRole.Anchor, new IZegoLoginCompletionCallback() {
                @Override
                public void onLoginCompletion(int type, ZegoStreamInfo[] zegoStreamInfos) {
                    CommonFunction.log(TAG, "IZegoLoginCompletionCallback() onLoginCompletion() type= " + type + ", roomId=" + roomId +", zegoStreamInfos.length="+ zegoStreamInfos.length);
                    handleLoginComplete(type, zegoStreamInfos);
                }
            });
        }
        CommonFunction.log(TAG, "setRoomId() roomId=" + roomId + ", isLogin=" + isLogin) ;
        return isLogin;
    }

    private synchronized void handleLoginComplete(int type, ZegoStreamInfo[] zegoStreamInfos){
        if (type == 0) {
            if (zegoStreamInfos != null && zegoStreamInfos.length > 0) {
                // 新增流
                for (int i = 0; i < zegoStreamInfos.length; i++) {
                    //播放新增流
                    startPlay(zegoStreamInfos[i].streamID);
                    //处理用户声音波形
                    if(mSoundWaveTimerTask!=null){
                        mSoundWaveTimerTask.addUser(zegoStreamInfos[i].streamID);
                    }
                }
            }
        }
    }
    /**
     * 开始拉流
     * @param streamID
     */
    public void startPlay(String streamID){
        CommonFunction.log(TAG,"startPlay() into, streamID=" +streamID);
        if (mZegoLiveRoom != null){
            handleMuteState1(true);
            mZegoLiveRoom.startPlayingStream(streamID,null);
        }
        CommonFunction.log(TAG,"startPlay() out");
    }

    /**
     * 暂停拉流
     * @param streamID
     */
    public void stopPlay(String streamID){
        CommonFunction.log(TAG,"stopPlay() into, streamID=" +streamID);
        if (mZegoLiveRoom != null){
            handleMuteState1(false);
            mZegoLiveRoom.stopPlayingStream(streamID);
        }
        CommonFunction.log(TAG,"stopPlay() out");
    }

    /**
     * 开始推流
     */
    public synchronized void startPublish(String streamID,String groupId){
        CommonFunction.log(TAG,"startPublish() into");
        if (mZegoLiveRoom != null){
            mPublishStreamID = streamID;
            mMixStreamID = groupId;
            CommonFunction.log(TAG,"startPublish() mMixStreamID=" + mMixStreamID);
            CommonFunction.log(TAG,"startPublish() Config.sMicT= " + Config.sMicT+"/"+streamID+"/"+groupId);
            mZegoLiveRoom.enableCamera(false);
            //mZegoLiveRoom.setPreviewView(null);
            //mZegoLiveRoom.startPreview();
            handleMicState(true);//mZegoLiveRoom.enableMic(true);
            if (isFlag == 1){
                mZegoLiveRoom.startPublishing(mPublishStreamID,"",ZegoConstants.PublishFlag.MixStream);
            }else{
                mZegoLiveRoom.startPublishing(mMixStreamID,"",ZegoConstants.PublishFlag.SingleAnchor);
            }
            if(mSoundWaveTimerTask!=null){
                mSoundWaveTimerTask.addUser(streamID);
            }
        }
        CommonFunction.log(TAG,"startPublish() out");
    }

    /*恢复推流
    * */
    public void resumePublish(){
        CommonFunction.log(TAG,"resumePublish() into");
        if(isPublishing()==false){
            if (mZegoLiveRoom != null){
                mZegoLiveRoom.enableCamera(false);
                //mZegoLiveRoom.setPreviewView(null);
                //mZegoLiveRoom.startPreview();
                handleMicState(true); //mZegoLiveRoom.enableMic(true);
                if (isFlag == 1){
                    mZegoLiveRoom.startPublishing(mPublishStreamID,"",ZegoConstants.PublishFlag.MixStream);
                }else{
                    mZegoLiveRoom.startPublishing(mMixStreamID,"",ZegoConstants.PublishFlag.SingleAnchor);
                }
                if(mSoundWaveTimerTask!=null){
                    mSoundWaveTimerTask.addUser(mPublishStreamID);
                }
            }
        }
    }

    /**
     * 暂停推流
     */
    public synchronized void stopPublish(){
        CommonFunction.log(TAG,"stopPublish into");
        if (mZegoLiveRoom != null){
            hasPublish = false;
            //mZegoLiveRoom.stopPreview();
            handleMicState(false); //mZegoLiveRoom.enableMic(false);
            boolean stopPublishing = mZegoLiveRoom.stopPublishing();
            CommonFunction.log(TAG,"stopPublish ="+stopPublishing);
            if(mSoundWaveTimerTask!=null){
                mSoundWaveTimerTask.delUser(mPublishStreamID);
            }
        }
        CommonFunction.log(TAG,"stopPublish out");
    }

    /**
     * 当前吧内所有的流
     * @return
     */
    public String getPushflows() {
        String pushflows = "";
        if (mListStreamOfRoom.size() > 0){
            for (int i= 0; i < mListStreamOfRoom.size(); i++){
                ZegoStreamInfo zegoStreamInfo = mListStreamOfRoom.get(i);
                if (i == mListStreamOfRoom.size() - 1){
                    pushflows += zegoStreamInfo.streamID;
                }else{
                    pushflows += zegoStreamInfo.streamID + ",";
                }
            }
        }
        return pushflows;
    }

    /**
     * 声音
     * @param mute
     */
    public void handleMuteState1(boolean mute) {
        CommonFunction.log(TAG,"handleMuteState1() into, mute=" + mute);
        enableSpeaker = mute;
        if(null!=mZegoLiveRoom) {
            mZegoLiveRoom.enableSpeaker(mute);
        }
    }

    /**
     * 麦克风状态
     * @param mute
     */
    public void handleMicState(boolean mute) {
        CommonFunction.log(TAG,"handleMicState() mute = "+mute);
        enableRecorder = mute;
        if(null!=mZegoLiveRoom) {
            mZegoLiveRoom.enableMic(mute);
        }
    }

    /**
     * 声音状态
     * @return
     */
    public boolean isEnableSpeaker() {
        return enableSpeaker;
    }

    /**
     * 上麦状态
     * @return
     */
    public boolean isEnableRecorder() {
        return enableRecorder;
    }

    public synchronized void clearAudio(){
        CommonFunction.log(TAG,"clearAudio() into");
        if (mZegoLiveRoom != null){
            // 在离开直播或者观看页面时，请将sdk的callback置空，避免内存泄漏
            // 清空回调, 避免内存泄漏
            mZegoLiveRoom.setZegoLivePublisherCallback(null);
            mZegoLiveRoom.setZegoLivePlayerCallback(null);
            mZegoLiveRoom.setZegoLiveEventCallback(null);
            mZegoLiveRoom.setZegoRoomCallback(null);

            iFlyMediaCallback = null;

            isLogin = false;
            // 退出房间
            //mZegoLiveRoom.stopPreview();
            //mZegoLiveRoom.setPreviewView(null);
            mZegoLiveRoom.stopPublishing();
            mZegoLiveRoom.enableMic(false);
            mZegoLiveRoom.enableSpeaker(false);
            mZegoLiveRoom.logoutRoom();
            mZegoLiveRoom = null;
            enableRecorder = false;
            enableSpeaker = false;

            hasPublish = false;
        }
        CommonFunction.log(TAG,"clearAudio() out");
    }

    public long getManageId() {
        return manageId;
    }

    public void setManageId(long manageId) {
        this.manageId = manageId;
    }

    public String getRoomId(){
        return mMixStreamID;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setIsFlag(int isFlag) {
        this.isFlag = isFlag;
    }

    public void setiFlyMediaCallback(IFlyMediaCallback iFlyMediaCallback) {
        log(TAG,"setiFlyMediaCallback() into");
        this.iFlyMediaCallback = iFlyMediaCallback;
    }

    public boolean isPublishing(){
        return hasPublish;
    }

    public synchronized boolean isDestroy(){
        return mZegoLiveRoom == null;
    }

    public interface IFlyMediaCallback {
        void engineSuccess();
        void engineError();
    }

    private void log(String tag, String content){
        if(true == DEBUG){
            CommonFunction.log(tag,content);
        }
    }

    public synchronized void updateSoundWaveUser(String user1, String user2){
        if(mSoundWaveTimerTask!=null){
            if(user1!=null){
                mSoundWaveTimerTask.addUser(user1);
            }
            if(user2!=null){
                mSoundWaveTimerTask.addUser(user2);
            }
        }
    }

    /*
     */
    public interface UpdateSoundWaveListener{
        void onUpdateSoundWave(String user1, boolean speaking1, String user2, boolean speaking2);
    }
    public synchronized void setUpdateSoundWaveListener(UpdateSoundWaveListener listener){
        if(listener!=null){ //清空
            if(mSoundWaveTimer!=null){
                mSoundWaveTimer.cancel();
                mSoundWaveTimerTask.setUpdateSoundWaveListener(null);
                mSoundWaveTimerTask = null;
                mSoundWaveTimer = null;
            }
            mSoundWaveTimer = new Timer();
            mSoundWaveTimerTask =new GetSoundLevelTask();
            mSoundWaveTimerTask.setUpdateSoundWaveListener(listener);
            mSoundWaveTimer.schedule(mSoundWaveTimerTask, 1000,1000);
        }else{   //清空
            if(mSoundWaveTimer!=null){
                mSoundWaveTimer.cancel();
                mSoundWaveTimerTask.setUpdateSoundWaveListener(null);
                mSoundWaveTimerTask = null;
                mSoundWaveTimer = null;
            }
        }
    }


    /*获取麦上用户声音大小定时器
    * */
    static class GetSoundLevelTask extends TimerTask {
        private static final float SPEAKING_SOUND_LEVEL = 1.0f;
        private ArrayList<String> mUsers = new ArrayList<String>();
        private Object mLock = new Object();
        private WeakReference<ZegoLiveRoom> mLiveRoom;
        private WeakReference<UpdateSoundWaveListener> mUpdateSoundWaveListener = null;

        public GetSoundLevelTask(){
            mLiveRoom = new WeakReference<ZegoLiveRoom>(InitZegoLiveRoom.getZegoLiveRoom());
        }

        public void setUpdateSoundWaveListener(UpdateSoundWaveListener listener){
            synchronized (mLock) {
                if(mUpdateSoundWaveListener!=null){
                    mUpdateSoundWaveListener.clear();
                    mUpdateSoundWaveListener = null;
                }
                mUpdateSoundWaveListener = new WeakReference<UpdateSoundWaveListener>(listener);
            }
        }

        public void addUser(String user){
            CommonFunction.log(TAG, "sound wave update add user:" + user);
            synchronized (mLock) {
                if (user != null) {
                    for (int i=0;i<mUsers.size();i++){
                        if( user.equals(mUsers.get(i)) ){
                            return;
                        }
                    }
                    if (mUsers.size() >= 2) {
                        mUsers.remove(0);
                    }
                    mUsers.add(user);
                }
            }
        }

        public void delUser(String user){
            CommonFunction.log(TAG, "sound wave update delete user:" + user);
            synchronized (mLock) {
                if (user != null) {
                    for (int i = 0; i < mUsers.size(); i++) {
                        if (mUsers.get(i).equals(user)) {
                            mUsers.remove(i);
                            break;
                        }
                    }
                }
            }
        }

        @Override
        public void run() {
            //CommonFunction.log(TAG,"GetSoundLevelTask() time bingo");
            if(mLiveRoom!=null){
                synchronized (mLock) {
                    String user1 = null, user2 = null;
                    float level1 = 0, level2 = 0;
                    if(mUsers.size()==1) {
                        user1 = mUsers.get(0);
                        ZegoLiveRoom zegoLiveRoom = mLiveRoom.get();
                        if(zegoLiveRoom!=null) {
                            level1 = zegoLiveRoom.getSoundLevelOfStream(user1);
                        }
                        //CommonFunction.log(TAG, "GetSoundLevelTask() sound level of user=" + user1 + ", level=" + level1);
                    }else if(mUsers.size()==2){
                        user1 = mUsers.get(0);
                        ZegoLiveRoom zegoLiveRoom = mLiveRoom.get();
                        if(zegoLiveRoom!=null) {
                            level1 = zegoLiveRoom.getSoundLevelOfStream(user1);
                        }
                        //CommonFunction.log(TAG, "GetSoundLevelTask() sound level of user=" + user1 + ", level=" + level1);

                        user2 = mUsers.get(1);
                        if(zegoLiveRoom!=null) {
                            level2 = zegoLiveRoom.getSoundLevelOfStream(user2);
                        }
                        //CommonFunction.log(TAG, "GetSoundLevelTask() sound level of user=" + user2 + ", level=" + level2);
                    }

                    UpdateSoundWaveListener listener = mUpdateSoundWaveListener.get();
                    if (listener != null) {
                        boolean speaking1 = false, speaking2 = false;
                        if(level1>SPEAKING_SOUND_LEVEL){
                            speaking1 = true;
                        }
                        if(level2>SPEAKING_SOUND_LEVEL){
                            speaking2 = true;
                        }
                        listener.onUpdateSoundWave(user1, speaking1, user2, speaking2);
                    }
                }
            }
        }
    }
}
