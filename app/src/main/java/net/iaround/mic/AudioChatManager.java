package net.iaround.mic;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

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
import com.zego.zegoliveroom.entity.ZegoStreamInfo;
import com.zego.zegoliveroom.entity.ZegoStreamQuality;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.Constants;
import net.iaround.im.WebSocketManager;
import net.iaround.tools.CommonFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 即构音频推拉流管理类
 */
public class AudioChatManager {
    private static final String TAG = "AudioChatManager";
    private static AudioChatManager sInstance;

    private ZegoLiveRoom mZegoLiveRoom;
    private boolean mHasLogin = false;//是否登陆房间成功
    private boolean mPlaySuccess = false;//是否拉流成功
    private String mRoomId;
    private List<ZegoStreamInfo> mListStreamOfRoom = new ArrayList<>();
    private int mStreamQualityBadCount;//通话质量差检测次数

    private boolean isSpeakerOn = true;//是否开启扬声器
    private boolean isEnableMic = true;//是否开启麦克风

    private final int RESTART_PUBLSH_MSG = 1;
    private HandlerThread handlerThread;
    private int restartCount = 0;
    private boolean isPromptToast = true;
    private Handler handler;

    private boolean hasPublish;


    private AudioChatManager() {
    }

    public static AudioChatManager getsInstance() {
        if (null == sInstance) {
            synchronized (AudioChatManager.class) {
                if (null == sInstance) {
                    sInstance = new AudioChatManager();
                }
            }
        }
        return sInstance;
    }


    public synchronized void init() {
        CommonFunction.log(TAG, "init() into");

        // 创建ZegoAVKit对象, 整个项目中创建一次即可，建议封装成单例使用
        mZegoLiveRoom = InitZegoLiveRoom.getZegoLiveRoom();

        mZegoLiveRoom.setAudioBitrate(32000);
        mZegoLiveRoom.enableAux(true);
        mZegoLiveRoom.enableSpeaker(true); //mZegoLiveRoom.enableSpeaker(true); //解决有时候没有播放声音

        mZegoLiveRoom.setZegoLivePublisherCallback(new IZegoLivePublisherCallback() {
            @Override
            public void onPublishStateUpdate(int stateCode, String streamId, HashMap<String, Object> info) {
                CommonFunction.log(TAG, "推流 IZegoLivePublisherCallback() onPublishStateUpdate()  stateCode=" + stateCode + ", streamId=" + streamId + ", info=" + info);

                if (stateCode == 0) {
//                    if (iFlyMediaCallback != null) {
//                        iFlyMediaCallback.engineSuccess();
//                    }
                    hasPublish = true;

                    //handlePublishSuccMix(streamId, info); 新的SDK没有混流？
                } else {
//                    if (iFlyMediaCallback != null) {
//                        iFlyMediaCallback.engineError();
//                    }
                }
            }

            @Override
            public void onJoinLiveRequest(int i, String s, String s1, String s2) {
                CommonFunction.log(TAG, "IZegoLivePublisherCallback() onJoinLiveRequest()  i=" + i + ", s=" + s + ", s1=" + s1 + ", s2=" + s2);

            }

            @Override
            public void onPublishQualityUpdate(String s, ZegoStreamQuality zegoStreamQuality) {
                // 无论推流和拉流 声音的综合质量用 quality 可以反应
                // zegoStreamQuality.quality 0～3 优 良 中 差
                // 差的时候应该向用户提示 提示的间隔要合理不要太频繁 同时应该记录到异常日志以供后台数据分析
                CommonFunction.log(TAG, "IZegoLivePublisherCallback() onPublishQualityUpdate() s=" + s + ", audioBitrate=" + zegoStreamQuality.audioBitrate + ", quality=" + zegoStreamQuality.quality + ", rtt=" + zegoStreamQuality.rtt + ", pktLostRate=" + zegoStreamQuality.pktLostRate);

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
                CommonFunction.log(TAG, "IZegoLivePublisherCallback() onMixStreamConfigUpdate() into");
//                int seq = -1;
//                if (hashMap != null) {
//                    seq = (int) hashMap.get(ZegoConstants.StreamKey.MIX_CONFIG_SEQ);
//                }
//                if (i == 0) {
//
//                    List<String> listUrls = getShareUrlList(hashMap);
//
//                    if (listUrls.size() == 0) {
//                        CommonFunction.log(TAG, "IZegoLivePublisherCallback() onMixStreamConfigUpdate() 混流失败...errorCode: " + i + " seq: " + seq);
//                    }
//
//                    if (listUrls.size() >= 2) {
//                        CommonFunction.log(TAG, "IZegoLivePublisherCallback() onMixStreamConfigUpdate() 混流地址: " + listUrls.get(1) + " seq: " + seq);
//
//                        // 将混流ID通知观众
//                        Map<String, String> mapUrls = new HashMap<>();
//                        mapUrls.put(Constants.FIRST_ANCHOR, String.valueOf(true));
//                        mapUrls.put(Constants.KEY_MIX_STREAM_ID, s);
//                        mapUrls.put(Constants.KEY_HLS, listUrls.get(0));
//                        mapUrls.put(Constants.KEY_RTMP, listUrls.get(1));
//
//                        Gson gson = new Gson();
//                        String json = gson.toJson(mapUrls);
//                        mZegoLiveRoom.updateStreamExtraInfo(json);
//                    }
//                } else {
//                    CommonFunction.log(TAG, "IZegoLivePublisherCallback() onMixStreamConfigUpdate() 混流失败...errorCode: " + i + " seq: " + seq);
//                }
            }
        });

        mZegoLiveRoom.setZegoLivePlayerCallback(new IZegoLivePlayerCallback() {
            @Override
            public void onPlayStateUpdate(int stateCode, String StreamId) {
                CommonFunction.log(TAG, "拉流  IZegoLivePlayerCallback() onPlayStateUpdate() stateCode=" + stateCode + ", StreamId=" + StreamId);
                if (stateCode == 0) {
                    /**拉流成功 发消息给服务端*/
                    WebSocketManager.getsInstance().sendMessage(WebSocketManager.TAG_AUDIO_PLAY_SUCCESS);
                } else {
                    //拉流失败
                    mPlaySuccess = false;
                    WebSocketManager.getsInstance().sendMessage(WebSocketManager.TAG_AUDIO_PLAY_FAIL);

                }
            }

            @Override
            public void onPlayQualityUpdate(String s, ZegoStreamQuality zegoStreamQuality) {
                // 无论推流和拉流 声音的综合质量用 quality 可以反应
                // zegoStreamQuality.quality 0～3 优 良 中 差
                // 差的时候应该向用户提示 提示的间隔要合理不要太频繁 同时应该记录到异常日志以供后台数据分析
                //log(TAG,"IZegoLivePlayerCallback() onPlayQualityUpdate() s=" + s  + ", audioBitrate=" + zegoStreamQuality.audioBitrate + ", quality=" + zegoStreamQuality.quality + ", rtt=" + zegoStreamQuality.rtt + ", pktLostRate="+zegoStreamQuality.pktLostRate);
                if (zegoStreamQuality.quality == 3) {
                    mStreamQualityBadCount++;
                    if (mStreamQualityBadCount > 3) {
                        mStreamQualityBadCount = 0;
                        CommonFunction.toastMsg(BaseApplication.appContext, R.string.network_not_good);
                    }
                }
            }

            @Override
            public void onInviteJoinLiveRequest(int i, String s, String s1, String s2) {
                CommonFunction.log(TAG, "IZegoLivePlayerCallback() onInviteJoinLiveRequest() i=" + i + ", s=" + s + ", s1=" + s1 + ", s2=" + s2);
            }

            @Override
            public void onRecvEndJoinLiveCommand(String s, String s1, String s2) {
                CommonFunction.log(TAG, "IZegoLivePlayerCallback() onRecvEndJoinLiveCommand() s=" + s + ", s1=" + s1 + ", s2=" + s2);
            }

            @Override
            public void onVideoSizeChangedTo(String s, int i, int i1) {
                CommonFunction.log(TAG, "IZegoLivePlayerCallback() onVideoSizeChangedTo() s=" + s + ", i=" + i + ", i1=" + i1);
            }
        });

        mZegoLiveRoom.setZegoLiveEventCallback(new IZegoLiveEventCallback() {
            @Override
            public void onLiveEvent(int zegoAudioLiveEvent, HashMap<String, String> info) {
                CommonFunction.log(TAG, "IZegoLiveEventCallback() onLiveEvent() event=" + zegoAudioLiveEvent + ", info=" + info);
            }
        });

        mZegoLiveRoom.setZegoRoomCallback(new IZegoRoomCallback() {
            @Override
            public void onKickOut(int i, String s) {
                CommonFunction.log(TAG, "IZegoRoomCallback() onKickOut()   i=" + i + ", s=" + s);
            }

            @Override
            public void onDisconnect(int i, String s) {
                mHasLogin = false;
                CommonFunction.log(TAG, "IZegoRoomCallback() onDisconnect()   i=" + i + ", s=" + s);
            }

            @Override
            public void onStreamUpdated(int type, ZegoStreamInfo[] zegoStreamInfos, String s) {
                CommonFunction.log(TAG, "IZegoRoomCallback() onStreamUpdated stateCode=" + type + ", streamId=" + s + ", zegoStreamInfos.length=" + zegoStreamInfos.length);
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
                CommonFunction.log(TAG, "IZegoRoomCallback() onStreamExtraInfoUpdated  " + " streamId=" + s + ", info" + zegoStreamInfos.length);
            }

            @Override
            public void onRecvCustomCommand(String s, String s1, String s2, String s3) {
                CommonFunction.log(TAG, "IZegoRoomCallback() onRecvCustomCommand()   s=" + s + ", s1=" + s1 + ", s2=" + s2 + ", s3=" + s3);
            }

            @Override
            public void onTempBroken(int i, String s) {
                CommonFunction.log(TAG, "IZegoRoomCallback() onTempBroken()   i=" + i + ", s=" + s);
            }

            @Override
            public void onReconnect(int i, String s) {
                CommonFunction.log(TAG, "IZegoRoomCallback() onReconnect()   i=" + i + ", s=" + s);
            }
        });

        CommonFunction.log(TAG, "init() out");
    }

    public boolean login(final String roomId) {
        this.mRoomId = roomId;
        //不设置用户ID会导致拉流失败
        if (null != mZegoLiveRoom) {
            //设置用户ID
            ZegoLiveRoom.setUser(String.valueOf(Common.getInstance().loginUser.getUid()), String.valueOf(Common.getInstance().loginUser.getNoteName(true)));

            //登陆房间
            mHasLogin = mZegoLiveRoom.loginRoom(String.valueOf(roomId), ZegoConstants.RoomRole.Audience, new IZegoLoginCompletionCallback() {
                @Override
                public void onLoginCompletion(int type, ZegoStreamInfo[] zegoStreamInfos) {
                    CommonFunction.log(TAG, "IZegoLoginCompletionCallback() onLoginCompletion() type= " + type + ", roomId=" + roomId + ", zegoStreamInfos.length=" + zegoStreamInfos.length);
                    handleLoginComplete(type, zegoStreamInfos);
                }
            });
        }

        CommonFunction.log(TAG, "setRoomId() roomId=" + roomId + ", isLogin=" + mHasLogin);
        return mHasLogin;
    }


    private synchronized void handleLoginComplete(int type, ZegoStreamInfo[] zegoStreamInfos) {
        if (type == 0) {//登陆房间成功
            if (zegoStreamInfos != null && zegoStreamInfos.length > 0) {
                // 新增流
                for (int i = 0; i < zegoStreamInfos.length; i++) {
                    //播放新增流
                    startPlay(zegoStreamInfos[i].streamID);

                }
            }
            startPublish(Common.getInstance().getUid());
        } else { //登陆房间失败
            WebSocketManager.getsInstance().sendMessage(WebSocketManager.TAG_LOGIN_ROOM_FAIL);
        }
    }

    /**
     * 房间内用户创建流.
     */
    protected synchronized void handleStreamAdded(final ZegoStreamInfo[] listStream, final String room) {
        if (listStream != null && listStream.length > 0) {
            for (ZegoStreamInfo streamInfo : listStream) {
                mListStreamOfRoom.add(streamInfo);
                startPlay(streamInfo.streamID);
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
                if (null != mZegoLiveRoom) {
                    mZegoLiveRoom.stopPlayingStream(streamInfo.streamID);
                }
            }
        }
    }

    /**
     * 获取流地址.
     */
    private List<String> getShareUrlList(HashMap<String, Object> info) {
        List<String> listUrls = new ArrayList<>();

        if (info != null) {
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

    /**
     * 开始拉流
     *
     * @param streamID
     */
    public void startPlay(String streamID) {
        CommonFunction.log(TAG, "startPlay() into, streamID=" + streamID);
        if (mZegoLiveRoom != null) {
            mZegoLiveRoom.enableSpeaker(true);
            mZegoLiveRoom.startPlayingStream(streamID, null);
        }
        CommonFunction.log(TAG, "startPlay() out");
    }

    /**
     * 开始推流
     */
    public synchronized void startPublish(String streamID) {
        CommonFunction.log(TAG, "startPublish() into");
        if (mZegoLiveRoom != null) {
            mZegoLiveRoom.enableCamera(false);
            //mZegoLiveRoom.setPreviewView(null);
            //mZegoLiveRoom.startPreview();
            mZegoLiveRoom.enableMic(true);
            mZegoLiveRoom.startPublishing(streamID, "", ZegoConstants.PublishFlag.JoinPublish);

        }
        CommonFunction.log(TAG, "startPublish() out");
    }

    /**
     * 是否开启麦克风
     *
     * @param enable true为打开麦克风 false为关闭麦克风
     */
    public void enableMic(boolean enable) {
        if (null != mZegoLiveRoom) {
            isEnableMic = enable;
            mZegoLiveRoom.enableMic(enable);
        }

    }

    /**
     * 是否开启扬声器
     *
     * @param enable true为打开扬声器，false为关闭扬声器
     */
    public void builtinSpeakerOn(boolean enable) {
        if (mZegoLiveRoom != null) {
            isSpeakerOn = enable;
            mZegoLiveRoom.setBuiltInSpeakerOn(enable);
        }
    }


    public String getRoomId() {
        return mRoomId;
    }

    public void setRoomId(String roomId) {
        mRoomId = roomId;
    }

    /**
     * 是否已经登陆房间
     *
     * @return
     */
    public boolean isHasLogin() {
        return mHasLogin;
    }

    /**
     * 是否已经拉流成功
     *
     * @return
     */
    public boolean isPlaySuccess() {
        return mPlaySuccess;
    }

    public void setPlaySuccess(boolean playSuccess) {
        mPlaySuccess = playSuccess;
    }

//    /**
//     * 设置当前是否开启了麦克风
//     *
//     * @param enableMic
//     */
//    public void setEnableMic(boolean enableMic) {
//        isEnableMic = enableMic;
//    }

    /**
     * 是否开启了麦克风
     */
    public boolean isEnableMic() {
        return isEnableMic;
    }

//    /**
//     * 设置当前是否开启了扬声器
//     *
//     * @param speakerOn
//     */
//    public void setSpeakerOn(boolean speakerOn) {
//        isSpeakerOn = speakerOn;
//
//    }

    /**
     * 是否开启了扬声器
     *
     * @return
     */
    public boolean isSpeakerOn() {
        return isSpeakerOn;
    }


    public synchronized void clearAudio() {
        CommonFunction.log(TAG, "clearAudio() into");
        if (mZegoLiveRoom != null) {
            // 在离开直播或者观看页面时，请将sdk的callback置空，避免内存泄漏
            // 清空回调, 避免内存泄漏
            mZegoLiveRoom.setZegoLivePublisherCallback(null);
            mZegoLiveRoom.setZegoLivePlayerCallback(null);
            mZegoLiveRoom.setZegoLiveEventCallback(null);
            mZegoLiveRoom.setZegoRoomCallback(null);

            mHasLogin = false;
            // 退出房间
            mZegoLiveRoom.stopPublishing();
            mZegoLiveRoom.enableMic(false);
            mZegoLiveRoom.enableSpeaker(false);
            mZegoLiveRoom.logoutRoom();
            mZegoLiveRoom = null;
            isEnableMic = true;
            isSpeakerOn = true;
            mPlaySuccess = false;
            hasPublish = false;
            mRoomId = null;
        }
        CommonFunction.log(TAG, "clearAudio() out");
    }

}
