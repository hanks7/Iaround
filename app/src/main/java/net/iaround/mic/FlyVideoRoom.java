package net.iaround.mic;

import android.view.View;

import com.google.gson.Gson;
import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.callback.IZegoLiveEventCallback;
import com.zego.zegoliveroom.callback.IZegoLivePlayerCallback;
import com.zego.zegoliveroom.callback.IZegoLivePublisherCallback;
import com.zego.zegoliveroom.callback.IZegoLoginCompletionCallback;
import com.zego.zegoliveroom.callback.IZegoRoomCallback;
import com.zego.zegoliveroom.constants.ZegoAvConfig;
import com.zego.zegoliveroom.constants.ZegoBeauty;
import com.zego.zegoliveroom.constants.ZegoConstants;
import com.zego.zegoliveroom.constants.ZegoVideoViewMode;
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
import net.iaround.utils.logger.Logger;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class: 视频一对一
 * Author：lyh
 * Date: 2017/12/1 20:00
 */
public class FlyVideoRoom {
    private static final String TAG = "FlyVideoRoom";
    private static boolean DEBUG = true; //调试日志
    private static FlyVideoRoom instance = null;
    private static ZegoLiveRoom sZegoLiveRoom = null;

    private ZegoLiveRoom mZegoLiveRoom; //即构SDK单例对象

    public static final int VIDEO_STATE_ROOM_DEFAULT = 0; //原始状态
    public static final int VIDEO_STATE_ROOM_INIT = 1; //初始化
    public static final int VIDEO_STATE_ROOM_LOGIN_ING = 2; //登陆房间中
    public static final int VIDEO_STATE_ROOM_LOGIN_SUCCESS = 4; //登陆房间成功
    public static final int VIDEO_STATE_ROOM_LOGIN_FAIL = 8; //登陆房间失败
    public static final int VIDEO_STATE_ROOM_PUBLISHING = 16; //正在推流
    public static final int VIDEO_STATE_ROOM_PLAYING = 32; //正在拉流
    public static final int VIDEO_STATE_ROOM_DESTROY = 64; //销毁
    private int mState = VIDEO_STATE_ROOM_DEFAULT; //房间状态机

    private Object mSmallView = null; //小视频视图
    private Object mBigView = null; //大视频视图

    private String mRoomID; //房间ID
    private String mLocalUser; //本地用户
    private String mRemoteUser; //远端用户

    private String mPublishStreamID = null;
    private boolean enableSpeaker = false;     //扬声器状态
    private boolean enableRecorder = false; //麦克风状态

    //private IFlyPublishCallback mFlyPublishCallback;
    private IFlyPullCallback mFlyPullCallback;

    private boolean isLogin;    // 登陆状态
    private boolean mEnableBeauty = true; //开启美颜
    private boolean mEnablePreview = false; //开始预览
    private boolean mEnableFront = true; //前置摄像头

    public static final int MAX_AUDIO_BITRATE_ZERO_COUNT = 3; //播放的流码率为0时的最大连续次数，超过次数通知接口断开视频会话
    private int mAudioBitrateZeroCount = 0; //播放的流码率为0时的连续次数
    private boolean mIsAnchor = false;

    public static FlyVideoRoom getInstance() {
        if (instance == null) {
            synchronized (FlyVideoRoom.class) {
                if (instance == null) {
                    instance = new FlyVideoRoom();
                }
            }
        }
        return instance;
    }

    public static synchronized void initZego(){
        CommonFunction.log(TAG, "initZego() into");
        FlyVideoRoom.unInitZego();
        if(sZegoLiveRoom==null) {
            sZegoLiveRoom = new ZegoLiveRoom();

            //业务类型
            ZegoLiveRoom.setBusinessType(2);

            // 设置是否开启“测试环境”, true:开启 false:关闭
            if(Config.DEBUG==true) {
                ZegoLiveRoom.setTestEnv(false);//ZegoLiveRoom.setTestEnv(true);
            }else{
                ZegoLiveRoom.setTestEnv(false);
            }

            // 设置设置调试模式
            if(Config.DEBUG==true){
                ZegoLiveRoom.setVerbose(true);
            }else{
                ZegoLiveRoom.setVerbose(false);
            }

            // ！！！注意：这个Appid和signKey需要从server下发到App，避免在App中存储，防止盗用
            byte[] signKey = {(byte) 0xd2,(byte) 0x5e,(byte) 0xea,(byte) 0x5a,(byte) 0xce,(byte) 0x66,(byte) 0x16,(byte) 0x67,(byte) 0x1f,(byte) 0xdb,(byte) 0xd9,(byte) 0x08,(byte) 0xfe,(byte) 0x0b,(byte) 0x94,(byte) 0x0b,(byte) 0x25,(byte) 0x7c,(byte) 0x74,(byte) 0xef,(byte) 0x4d,(byte) 0xee,(byte) 0xaf,(byte) 0x9f,(byte) 0x47,(byte) 0xff,(byte) 0xcf,(byte) 0x9f,(byte) 0x11,(byte) 0xfc,(byte) 0xe7,(byte) 0x7e};
            long appID = 991958264L;
            ZegoLiveRoom.setAudioDeviceMode(2);

            // 初始化sdk
            boolean appId = sZegoLiveRoom.initSDK(appID, signKey, BaseApplication.appContext);

            // 初始化设置级别为"High"
            ZegoAvConfig zegoAvConfig = new ZegoAvConfig(ZegoAvConfig.Level.Generic); //HIGH
            //设置分辨率 贞率 码率 640*360，15帧，800kbps
            zegoAvConfig.setVideoCaptureResolution(640,360);
            zegoAvConfig.setVideoBitrate(800000);
            zegoAvConfig.setVideoFPS(15);
            sZegoLiveRoom.setAVConfig(zegoAvConfig);

            CommonFunction.log(TAG,"version="+ZegoLiveRoom.version());

            //ZegoLiveRoom.requireHardwareDecoder(true);
            //ZegoLiveRoom.requireHardwareEncoder(true);
        }

        CommonFunction.log(TAG, "initZego() out");
    }


    public static synchronized void unInitZego(){
        CommonFunction.log(TAG,"unInitZego() into");
        if (sZegoLiveRoom != null){
            sZegoLiveRoom.unInitSDK();
            sZegoLiveRoom = null;
        }
        CommonFunction.log(TAG,"unInitZego() out");
    }

    public static synchronized ZegoLiveRoom getZegoLiveRoom(){
        if(sZegoLiveRoom==null){
            initZego();
        }
        return sZegoLiveRoom;
    }

    /* 初始化
    * */
    public synchronized void init(){
        CommonFunction.log(TAG, "init() into");
        if(mState != VIDEO_STATE_ROOM_DEFAULT && mState!=VIDEO_STATE_ROOM_DESTROY){
            CommonFunction.log(TAG, "init() state wrong, mState=" + mState);
            return;
        }
        // 创建ZegoAVKit对象, 整个项目中创建一次即可，建议封装成单例使用
        mZegoLiveRoom = FlyVideoRoom.getZegoLiveRoom();

        mZegoLiveRoom.setAudioBitrate(32000);
        mZegoLiveRoom.enableAux(true);
        //enableSpeaker(true);

        //推流回调
        mZegoLiveRoom.setZegoLivePublisherCallback(new IZegoLivePublisherCallback() {
            @Override
            public void onPublishStateUpdate(int stateCode, String streamId, HashMap<String, Object> info) {
                log(TAG, "IZegoLivePublisherCallback() onPublishStateUpdate()  stateCode=" + stateCode + ", streamId=" + streamId + ", info=" + info);

                if (stateCode == 0) {
                    //推流成功
                    Logger.i(TAG, "推流成功");
                }else{
                    //推流失败
                    Logger.i(TAG, "推流失败");
                    if(mFlyPullCallback!=null){
                        mFlyPullCallback.pullResult(0);
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
            }
        });

        //播放回调
        mZegoLiveRoom.setZegoLivePlayerCallback(new IZegoLivePlayerCallback() {
            @Override
            public void onPlayStateUpdate(int stateCode, String StreamId) {
                log(TAG,"IZegoLivePlayerCallback() onPlayStateUpdate() stateCode="+stateCode+", StreamId="+StreamId);
                if(mIsAnchor){
                    Logger.i(TAG, "IZegoLivePlayerCallback() onPlayStateUpdate() stateCode="+stateCode+", StreamId="+StreamId);
                }
                if(stateCode != 0 ) {
                    Logger.i(TAG, "播放流失败");
                    if (mFlyPullCallback != null) {
                        mFlyPullCallback.pullResult(0);
                    }
                }
            }

            @Override
            public void onPlayQualityUpdate(String s, ZegoStreamQuality zegoStreamQuality) {
                // 无论推流和拉流 声音的综合质量用 quality 可以反应
                // zegoStreamQuality.quality 0～3 优 良 中 差
                // 差的时候应该向用户提示 提示的间隔要合理不要太频繁 同时应该记录到异常日志以供后台数据分析
                //log(TAG,"IZegoLivePlayerCallback() onPlayQualityUpdate() s=" + s  + ", audioBitrate=" + zegoStreamQuality.audioBitrate + ", quality=" + zegoStreamQuality.quality + ", rtt=" + zegoStreamQuality.rtt + ", pktLostRate="+zegoStreamQuality.pktLostRate);
                int br = (int)zegoStreamQuality.audioBitrate;
                if(br == 0){
                    //log(TAG,"IZegoLivePlayerCallback() onPlayQualityUpdate() audio bitrate zero");
                    mAudioBitrateZeroCount++;
                    if(mAudioBitrateZeroCount>=MAX_AUDIO_BITRATE_ZERO_COUNT){
                        mAudioBitrateZeroCount = 0;
                        if(mFlyPullCallback!=null){
                            mFlyPullCallback.pullResult(-1);
                        }
                    }
                }else{
                    //log(TAG,"IZegoLivePlayerCallback() onPlayQualityUpdate() audio bitrate not zero");
                    mAudioBitrateZeroCount = 0;
                }
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
                //去掉背景图
            }
        });

        mZegoLiveRoom.setZegoLiveEventCallback(new IZegoLiveEventCallback() {
            @Override
            public void onLiveEvent(int zegoAudioLiveEvent, HashMap<String, String> info) {
                log(TAG,"IZegoLiveEventCallback() onLiveEvent() event="+zegoAudioLiveEvent+", info="+info);
            }
        });

        //流变化回调
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
                if(mIsAnchor) {
                    Logger.i(TAG, "IZegoRoomCallback() onStreamUpdated stateCode=" + type + ", streamId=" + s + ", zegoStreamInfos.length=" + zegoStreamInfos.length);
                }
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

        mState = VIDEO_STATE_ROOM_INIT;

        CommonFunction.log(TAG, "init() out");
    }

    public void setSmallView(Object view){
        CommonFunction.log(TAG, "setSmallView() into");
        if(mState != VIDEO_STATE_ROOM_INIT){
            CommonFunction.log(TAG, "setSmallView() state wrong, mState=" + mState);
            return;
        }
        mSmallView = view;
    }

    public void setBigView(Object view){
        CommonFunction.log(TAG, "setBigView() into");
        if(mState != VIDEO_STATE_ROOM_INIT){
            CommonFunction.log(TAG, "setSmallView() state wrong, mState=" + mState);
            return;
        }
        mBigView = view;
    }

    /*开启视频
    * roomId 房间ID
    * localUser 本地用户
    * remoteUser 远端用户
    * */
    public synchronized void startVideo(String roomId, String localUser, String remoteUser, boolean anchor){
        CommonFunction.log(TAG, "startVideo() into, roomId=" +roomId + ", localUser="+localUser + ", remoteUser="+remoteUser);
        Logger.i(TAG, "startVideo() roomId=" + roomId + ", localUser=" + localUser + ", remoteUser=" + remoteUser);
        if(mState != VIDEO_STATE_ROOM_INIT){
            CommonFunction.log(TAG, "startVideo() state wrong, mState=" + mState);
            return;
        }
        mRoomID = roomId;
        mLocalUser = localUser;
        mRemoteUser = remoteUser;
        mIsAnchor = anchor;
        setRoomId(roomId);
    }

    /*关闭视频
    * */
    public synchronized void stopVideo(){
        CommonFunction.log(TAG, "stopVideo() into");
        if(mState == VIDEO_STATE_ROOM_DEFAULT || mState == VIDEO_STATE_ROOM_DESTROY){
            CommonFunction.log(TAG, "stopVideo() state ideal, mState="+mState);
            return;
        }
        stopPublish();
        stopPlay(mRemoteUser);
        if(null!=mZegoLiveRoom){
            mZegoLiveRoom.logoutRoom();
        }
    }

    /**
     * 开始拉流
     * @param streamID
     */
    public void startPlay(String streamID){
        CommonFunction.log(TAG,"startPlay() into, streamID=" +streamID);
        if(mIsAnchor){
            Logger.i(TAG, "startPlay() into, 流ID="+streamID);
        }
        if( false==isState(VIDEO_STATE_ROOM_LOGIN_SUCCESS) ){
            CommonFunction.log(TAG, "startPlay() state wrong, mState="+mState);
            Logger.w(TAG, "房间未登陆成功");
            return;
        }
        if (mZegoLiveRoom != null){
            if(false == isState(VIDEO_STATE_ROOM_PLAYING)) {
                enableSpeaker(true);
                Logger.i(TAG, "startPlay() 开始播放流");
                mZegoLiveRoom.startPlayingStream(streamID, mBigView);
                mZegoLiveRoom.setViewMode(ZegoVideoViewMode.ScaleAspectFill, streamID);
                addState(VIDEO_STATE_ROOM_PLAYING);
            }else{
                Logger.w(TAG, "startPlay() 未能播放流，状态机不对, state=" + mState);
            }
        }else{
            Logger.w(TAG, "mZegoLiveRoom 为空");
        }
        CommonFunction.log(TAG,"startPlay() out");
    }

    /**
     * 暂停拉流
     * @param streamID
     */
    public void stopPlay(String streamID){
        CommonFunction.log(TAG,"stopPlay() into, streamID=" +streamID);
        if( false==isState(VIDEO_STATE_ROOM_LOGIN_SUCCESS) ){
            CommonFunction.log(TAG, "stopPlay() state wrong, mState="+mState);
            return;
        }
        if (mZegoLiveRoom != null){
            if( true==isState(VIDEO_STATE_ROOM_PLAYING) ) {
                enableSpeaker(false);
                mZegoLiveRoom.stopPlayingStream(streamID);
                delState(VIDEO_STATE_ROOM_PLAYING);
            }
        }
        CommonFunction.log(TAG,"stopPlay() out");
    }

    /**
     * 预览摄像头
     */
    public synchronized void startPreview(){
        CommonFunction.log(TAG,"startPreview() into");
        if(mState != VIDEO_STATE_ROOM_INIT){
            CommonFunction.log(TAG, "startPreview() state wrong, mState=" + mState);
            return;
        }
        if(mSmallView ==null){
            return;
        }
        if(mEnablePreview==false) {
            mEnablePreview = true;
            mZegoLiveRoom.enableCamera(true);
            mZegoLiveRoom.setPreviewView(mSmallView);
            mZegoLiveRoom.startPreview();
            mZegoLiveRoom.setPreviewViewMode(ZegoVideoViewMode.ScaleAspectFill);
        }
    }

    /**
     * 停止预览摄像头
     */
    public synchronized void stopPreview(){
        CommonFunction.log(TAG,"stopPreview() into");
        if(mState != VIDEO_STATE_ROOM_INIT){
            CommonFunction.log(TAG, "stopPreview() state wrong, mState=" + mState);
            return;
        }
        if(mEnablePreview==true) {
            mEnablePreview = false;
            mZegoLiveRoom.enableCamera(false);
            mZegoLiveRoom.stopPreview();
            mZegoLiveRoom.setPreviewView(null);
        }
    }

    /**
     * 开始推流
     */
    public synchronized void startPublish(String streamID){
        CommonFunction.log(TAG,"startPublish() into");
        if(mIsAnchor){
            Logger.i(TAG, "startPublish() into, 流ID="+streamID);
        }
        if( false==isState(VIDEO_STATE_ROOM_LOGIN_SUCCESS) ){
            CommonFunction.log(TAG, "startPublish() state wrong, mState="+mState);
            Logger.e(TAG, "startPublish 开始推流失败, 房间未登陆, mState=" + mState);
            return;
        }
        if (mZegoLiveRoom != null){
            if(mLocalUser==null){
                CommonFunction.log(TAG,"startPublish() local user null");
                Logger.e(TAG, "startPublish local user null");
                return;
            }
            if( false == isState(VIDEO_STATE_ROOM_PUBLISHING) ) {
                mPublishStreamID = streamID;
                if(mEnableBeauty==true) {
                    mZegoLiveRoom.enableBeautifying(ZegoBeauty.POLISH|ZegoBeauty.WHITEN|ZegoBeauty.SKIN_WHITEN|ZegoBeauty.SHARPEN);
                }else{
                    mZegoLiveRoom.enableBeautifying(ZegoBeauty.NONE);
                }
                if(mEnablePreview==false) {
                    mEnablePreview = true;
                    mZegoLiveRoom.enableCamera(true);
                    mZegoLiveRoom.setPreviewView(mSmallView);
                    mZegoLiveRoom.startPreview();
                    mZegoLiveRoom.setPreviewViewMode(ZegoVideoViewMode.ScaleAspectFill);
                }
                enableMic(true);
                Logger.i(TAG, "startPublish() 开始推流");
                mZegoLiveRoom.startPublishing(streamID, "", ZegoConstants.PublishFlag.JoinPublish);

                addState(VIDEO_STATE_ROOM_PUBLISHING);
            }else{
                Logger.w(TAG, "startPublish() 状态机不对, state=" + mState);
            }
        }else{
            Logger.w(TAG, "startPublish() mZegoLiveRoom 为空");
        }
        CommonFunction.log(TAG,"startPublish() out");
    }

    /*恢复推流
    * */
    public void resumePublish(){
        CommonFunction.log(TAG,"resumePublish() into");
        if(isPublishing()==false){
            if (mZegoLiveRoom != null){
                if( false == isState(VIDEO_STATE_ROOM_PUBLISHING) ) {
                    if(mEnablePreview == false) {
                        mEnablePreview = true;
                        mZegoLiveRoom.enableCamera(true);
                        mZegoLiveRoom.setPreviewView(mSmallView);
                        mZegoLiveRoom.startPreview();
                        mZegoLiveRoom.setPreviewViewMode(ZegoVideoViewMode.ScaleAspectFill);
                    }
                    enableMic(true);
                    mZegoLiveRoom.startPublishing(mPublishStreamID, "", ZegoConstants.PublishFlag.SingleAnchor);

                    addState(VIDEO_STATE_ROOM_PUBLISHING);
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
            if( isState(VIDEO_STATE_ROOM_PUBLISHING) ) {
                if(mEnablePreview == true) {
                    mEnablePreview = false;
                    mZegoLiveRoom.stopPreview();
                    mZegoLiveRoom.setPreviewView(null);
                    mZegoLiveRoom.enableCamera(false);
                }
                enableMic(false);
                mZegoLiveRoom.stopPublishing();
                delState(VIDEO_STATE_ROOM_PUBLISHING);
            }
        }
        CommonFunction.log(TAG,"stopPublish out");
    }


    /**
     * 声音
     * @param mute
     */
    public void enableSpeaker(boolean mute) {
        CommonFunction.log(TAG,"enableSpeaker() into, mute=" + mute);
        enableSpeaker = mute;
        if(null!=mZegoLiveRoom) {
            mZegoLiveRoom.enableSpeaker(mute);
        }
    }

    /**
     * 麦克风状态
     * @param mute
     */
    public void enableMic(boolean mute) {
        CommonFunction.log(TAG,"enableMic() mute = "+mute);
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

    public synchronized void clear(){
        CommonFunction.log(TAG,"clearAudio() into");
        if (mZegoLiveRoom != null){
            // 在离开直播或者观看页面时，请将sdk的callback置空，避免内存泄漏 清空回调, 避免内存泄漏
            mZegoLiveRoom.setZegoLivePublisherCallback(null);
            mZegoLiveRoom.setZegoLivePlayerCallback(null);
            mZegoLiveRoom.setZegoLiveEventCallback(null);
            mZegoLiveRoom.setZegoRoomCallback(null);

//            mFlyPublishCallback = null;
            mFlyPullCallback = null;

            mZegoLiveRoom.stopPlayingStream(mRemoteUser);
            mZegoLiveRoom.stopPreview();
            mZegoLiveRoom.setPreviewView(null);
            mZegoLiveRoom.stopPublishing();
            mZegoLiveRoom.enableCamera(false);
            mEnablePreview = false;
            mZegoLiveRoom.enableMic(false);
            mZegoLiveRoom.enableSpeaker(false);

            mZegoLiveRoom.logoutRoom();
            mZegoLiveRoom = null;

            enableRecorder = false;
            enableSpeaker = false;

            isLogin = false;
            mRemoteUser = null;
            mLocalUser = null;
            mRoomID = null;
            mPublishStreamID = null;
            mBigView = null;
            mSmallView = null;

            mState = VIDEO_STATE_ROOM_DESTROY;

            mAudioBitrateZeroCount = 0;
            mIsAnchor = false;
        }
        CommonFunction.log(TAG,"clearAudio() out");
    }

    public synchronized void unInitSDK(){
        CommonFunction.log(TAG,"unInitSDK() into");
        if (mZegoLiveRoom != null){
            mZegoLiveRoom.unInitSDK();
        }
        CommonFunction.log(TAG,"unInitSDK() out");
    }


    public String getRoomId(){
        return mRoomID;
    }

    public synchronized boolean isPublishing(){
        return isState(VIDEO_STATE_ROOM_PUBLISHING);
    }

    public synchronized boolean isDestroy(){
        if(mZegoLiveRoom == null){
            return true;
        }
        return false;
    }

//    /*推流成功和失败回调接口
//    * */
//    public interface IFlyPublishCallback {
//        public void publishSuccess();
//        public void publishError();
//    }

    /*推流成功和失败回调接口
    * */
    public interface IFlyPullCallback {
        public void pullResult(int result); //1 拉流成功 0 登陆房间失败,推流失败，拉流失败 -1 拉流过程中断流
    }

    /*美颜开启
    * */
    public synchronized void setEnableBeauty(boolean enable){
        mEnableBeauty = enable;
        if(mEnableBeauty==true) {
            if(mZegoLiveRoom!=null) {
                mZegoLiveRoom.enableBeautifying(ZegoBeauty.POLISH | ZegoBeauty.WHITEN | ZegoBeauty.SKIN_WHITEN | ZegoBeauty.SHARPEN);
            }
        }else{
            if(mZegoLiveRoom!=null) {
                mZegoLiveRoom.enableBeautifying(ZegoBeauty.NONE);
            }
        }
    }

    /**
     * 摄像头切换
     */
    public synchronized void cameraSwitch(){
        if(mZegoLiveRoom!=null) {
            mEnableFront = !mEnableFront;
            mZegoLiveRoom.setFrontCam(mEnableFront);
        }
    }

//    public synchronized void setFlyMediaCallback(IFlyPublishCallback callback) {
//        log(TAG,"setFlyMediaCallback() into");
////        this.mFlyPublishCallback = callback;
//    }

    public synchronized void setFlyPullCallback(IFlyPullCallback callback) {
        log(TAG,"setFlyPullCallback() into");
        this.mFlyPullCallback = callback;
    }


    private FlyVideoRoom() {

    }

    private boolean setRoomId(final String roomId){
        this.mRoomID = roomId;
        //不设置用户ID会导致拉流失败
        if(null!=mZegoLiveRoom){
            //设置用户ID
            mZegoLiveRoom.setUser(mLocalUser,mLocalUser);

            //登陆房间
            mState = mState | VIDEO_STATE_ROOM_LOGIN_ING;
            Logger.i(TAG, "即将登陆房间 room=" + roomId);
            isLogin = mZegoLiveRoom.loginRoom(roomId, ZegoConstants.RoomRole.Audience, new IZegoLoginCompletionCallback() {
                @Override
                public void onLoginCompletion(int type, ZegoStreamInfo[] zegoStreamInfos) {
                    CommonFunction.log(TAG, "IZegoLoginCompletionCallback() onLoginCompletion() type=" + type + ", roomId=" + roomId +", zegoStreamInfos.length="+ zegoStreamInfos.length);
                    handleLoginComplete(type, zegoStreamInfos);
                }
            });
        }
        CommonFunction.log(TAG, "setRoomId() roomId=" + roomId + ", isLogin=" + isLogin) ;
        return isLogin;
    }

    /**
     * 房间内用户创建流.
     */
    private synchronized void handleStreamAdded(final ZegoStreamInfo[] listStream, final String room) {
        if (listStream != null && listStream.length > 0) {
            for (ZegoStreamInfo streamInfo : listStream) {
                if(mRemoteUser!=null&& mRemoteUser.equals(streamInfo.streamID)) {
                    CommonFunction.log(TAG, "handleStreamAdded() start play stream=" + streamInfo.streamID);
                    Logger.i(TAG, "新增播放流");

                    startPlay(streamInfo.streamID);

                    //通知拉流成功
                    if(mFlyPullCallback!=null){
                        mFlyPullCallback.pullResult(1);
                    }else{
                        CommonFunction.log(TAG, "handleStreamAdded() mFlyPullCallback null");
                    }
                }
            }
        }else{
            Logger.i(TAG, "新增播放流为空");
        }
    }

    /**
     * 房间内用户删除流.
     */
    private synchronized void handleStreamDeleted(final ZegoStreamInfo[] listStream, final String room) {
        if (listStream != null && listStream.length > 0) {
            for (ZegoStreamInfo streamInfo : listStream) {
                if(null!=mZegoLiveRoom) {
                    if(mRemoteUser!=null&& mRemoteUser.equals(streamInfo.streamID)) {
                        CommonFunction.log(TAG, "handleStreamDeleted() stop play stream=" + streamInfo.streamID);
                        Logger.i(TAG, "移除播放流");

                        stopPlay(streamInfo.streamID);
//                        if(mFlyPullCallback!=null){
//                            mFlyPullCallback.pullResult(0);
//                        }
                    }
                }
            }
        }else {
            Logger.i(TAG, "移除播放流为空");
        }
    }

    /*处理登陆房间成功
    * */
    private synchronized void handleLoginComplete(int type, ZegoStreamInfo[] zegoStreamInfos){
        //播放已有流
        if (type == 0) {
            Logger.i(TAG, "登陆房间成功");

            //登陆房间成功标记
            CommonFunction.log(TAG, "handleLoginComplete() login room success");
            addState(VIDEO_STATE_ROOM_LOGIN_SUCCESS);

            //播放新增流
            if (zegoStreamInfos != null && zegoStreamInfos.length > 0) {
                for (int i = 0; i < zegoStreamInfos.length; i++) {
                    if(null!=mRemoteUser && mRemoteUser.equals(zegoStreamInfos[i].streamID)) {
                        CommonFunction.log(TAG, "handleLoginComplete() start play stream=" + zegoStreamInfos[i].streamID);
                        Logger.i(TAG, "登陆时新增播放流");

                        startPlay(zegoStreamInfos[i].streamID);

                        if(mFlyPullCallback!=null){
                            mFlyPullCallback.pullResult(1);
                        }else{
                            Logger.w(TAG, "推拉流状态回调接口为空");
                            CommonFunction.log(TAG, "handleLoginComplete() mFlyPullCallback null");
                        }
                    }
                }
            }else{
                Logger.i(TAG, "登陆房间回调接口流为空");
            }

            //推自己的流
            startPublish(mLocalUser);
        }else {
            Logger.i(TAG, "登陆房间失败");
            //登陆房间失败标记
            CommonFunction.log(TAG, "handleLoginComplete() login room fail");
            addState(VIDEO_STATE_ROOM_LOGIN_FAIL);

            if(mFlyPullCallback!=null){
                mFlyPullCallback.pullResult(0);
            }else{
                Logger.w(TAG, "推拉流状态回调接口为空");
                CommonFunction.log(TAG, "handleLoginComplete() mFlyPullCallback null");
            }
        }
    }

    private boolean isState(int flag){
        if( (flag & mState) == 0){
            return false;
        }
        return true;
    }

    private void delState(int flag){
        mState = mState & ~flag;
    }

    private void addState(int flag){
        mState = mState | flag;
    }


    private void log(String tag, String content){
        if(true == DEBUG){
            CommonFunction.log(tag,content);
        }
    }

}
