
package net.iaround.ui.datamodel;


import android.content.Context;
import android.text.TextUtils;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.im.WebSocketManager;
import net.iaround.model.audiochat.AudioChatBean;
import net.iaround.model.entity.GeoData;
import net.iaround.model.im.ChatRecord;
import net.iaround.model.im.ChatRecordStatus;
import net.iaround.model.im.Me;
import net.iaround.model.im.PrivateChatMessage;
import net.iaround.model.im.TransportMessage;
import net.iaround.model.im.UserTypeOne;
import net.iaround.model.im.type.MessageBelongType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.chat.view.ChatRecordViewFactory;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * @Description: 语音聊天
 */
public class AudioChatModel extends Model {
    private static final String TAG = "AudioChatModel";
    private static AudioChatModel model;
    private Context mContext;
    private String[] mStatus;
    private String mOldRoomId;

    private AudioChatModel() {
        mContext = BaseApplication.appContext;
        mStatus = mContext.getResources().getStringArray(R.array.video_chat_state);

    }

    public static AudioChatModel getInstance() {
        if (model == null)
            model = new AudioChatModel();
        return model;
    }

    /**
     * 插入一条记录
     *
     * @param bean 数据
     */
    public synchronized void insertRecord(AudioChatBean bean) {
        if (bean != null) {
            CommonFunction.log(TAG, "insertRecord: Code:" + bean.Code + ", Status:" + bean.Status);
            if (bean.Code == 200 && (bean.Status == WebSocketManager.TAG_CANCEL_CALL || bean.Status == WebSocketManager.TAG_ANCHOR_REFUSE || bean.Status == WebSocketManager.TAG_LINE_BUSY
                    || bean.Status == WebSocketManager.TAG_CALL_TIMEOUT || bean.Status == WebSocketManager.TAG_TARGET_HEARTBEAT_TIMEOUT || bean.Status == WebSocketManager.TAG_ANCHOR_NOT_RECEIVE_PAY
                    || bean.Status == WebSocketManager.TAG_CLOSE)) {
                if (bean.Status != WebSocketManager.TAG_LINE_BUSY && !TextUtils.isEmpty(bean.RoomID) && bean.RoomID.equals(mOldRoomId)) {
                    return;
                }
                mOldRoomId = bean.RoomID;
                Me me = Common.getInstance().loginUser;
                User fuser = new User();//对方的信息
                /**记录语音聊天消息的最新时间*/
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong(me.getUid() + "audio_send_date", bean.SendTime * 1000);

                String contentStr = "";
                switch (bean.Status) {
                    case WebSocketManager.TAG_CANCEL_CALL://取消拨打
                        if (bean.CurrentSendUserID == me.getUid()) {
                            contentStr = mStatus[0];
                        } else {
                            contentStr = mStatus[1];
                        }
                        break;
                    case WebSocketManager.TAG_ANCHOR_REFUSE://主播拒绝
                        if (bean.CurrentSendUserID == me.getUid()) {
                            contentStr = mStatus[3];
                        } else {
                            contentStr = mStatus[2];
                        }
                        break;
                    case WebSocketManager.TAG_LINE_BUSY://主播忙线中有其他用户打过来（只有主播接收)
//                        contentStr = mContext.getString(R.string.voice_line_busy);
                        contentStr = mStatus[3];
                        break;
                    case WebSocketManager.TAG_CALL_TIMEOUT://拨打超时
                        if (bean.CurrentSendUserID == me.getUid()) {
                            contentStr = mStatus[4];
                        } else {
                            contentStr = mStatus[1];
                        }
                        break;
                    case WebSocketManager.TAG_TARGET_HEARTBEAT_TIMEOUT://通话中对方心跳超时
                    case WebSocketManager.TAG_ANCHOR_NOT_RECEIVE_PAY://主播未收到扣费消息
                    case WebSocketManager.TAG_CLOSE://关闭通话
                        contentStr = mContext.getString(R.string.duration) + TimeFormat.secondsToString(bean.Seconds);
                        break;
                }
                //给对方信息赋值
                if (bean.SendUserID == me.getUid()) {
                    fuser.setUid(bean.ReceiveUserID);
                    fuser.setNickname(bean.ReceiveUserNickName);
                    fuser.setNoteName(bean.ReceiveUserNickName);
                    fuser.setIcon(bean.ReceiveUserICON);
                } else {
                    fuser.setUid(bean.SendUserID);
                    fuser.setNickname(bean.SendUserNickName);
                    fuser.setNoteName(bean.SendUserNickName);
                    fuser.setIcon(bean.SendUserICON);
                }
//                fuser.setSVip(fsvip);
//                fuser.setViplevel(fvipleave);
//                fuser.setLat(flat);
//                fuser.setLng(flng);
//                fuser.setUserType(fanchor);

                //消息是自己发的
                if (bean.CurrentSendUserID == me.getUid()) {
                    ChatRecord record = new ChatRecord();

                    record.setId(System.currentTimeMillis()); // 消息id
                    record.setUid(me.getUid());
                    record.setNickname(me.getNickname());
                    record.setIcon(me.getIcon());
                    record.setVip(me.getViplevel());
                    record.setDatetime(bean.SendTime * 1000);
                    record.setType(Integer.toString(ChatRecordViewFactory.CHCAT_AUDIO));
                    record.setStatus(ChatRecordStatus.ARRIVED); // TODO：发送中改为送达
                    record.setContent(contentStr);
                    record.setUpload(false);

                    record.setfLat(me.getLat());
                    record.setfLng(me.getLng());
                    GeoData geo = LocationUtil.getCurrentGeo(BaseApplication.appContext);
                    record.setDistance(LocationUtil.calculateDistance(me.getLng(), me.getLat(), geo.getLng(), geo.getLat()));
                    ChatPersonalModel.getInstance().insertOneRecord(BaseApplication.appContext, fuser, record, MessageBelongType.SEND, 1);
                    EventBus.getDefault().post("audio_chat_record");
                    EventBus.getDefault().post("refersh_message");
                } else {//消息是对方发的
                    PrivateChatMessage message = new PrivateChatMessage();
                    message.msgid = System.currentTimeMillis();
                    message.type = ChatRecordViewFactory.FRIEND_CHCAT_AUDIO;
                    message.mtype = 0;
                    message.content = contentStr;
                    message.flag = TimeFormat.getCurrentTimeMillis();
                    message.from = 0;
                    message.relation = 0;//关系
                    message.datetime = bean.SendTime * 1000;
                    UserTypeOne userTypeOne = new UserTypeOne();
                    userTypeOne.convertUserToBaseUserInfor(fuser);
                    message.user = userTypeOne;

                    TransportMessage transportMessage = new TransportMessage();
                    transportMessage.setContentBody(GsonUtil.getInstance().getStringFromJsonObject(message));
//                    transportMessage.setId();
                    transportMessage.setContentLength(transportMessage.getContentBody().length());
                    ChatPersonalModel.getInstance().insertOneRecord(BaseApplication.appContext, "" + me.getUid(), transportMessage, ChatRecordStatus.NONE);
                    EventBus.getDefault().post("audio_chat_record");
                    EventBus.getDefault().post("refersh_message");
                }

            } else {
                switch (bean.Code) {
                    case 304://对方正在通话中
                    case 306://主播忙线中
                        Me me = Common.getInstance().loginUser;
                        User fuser = new User();//对方的信息

                        /**记录语音聊天消息的最新时间*/
                        SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong(me.getUid() + "audio_send_date", bean.SendTime * 1000);

                        //给对方信息赋值
                        if (bean.SendUserID == me.getUid()) {
                            fuser.setUid(bean.ReceiveUserID);
                            fuser.setNickname(bean.ReceiveUserNickName);
                            fuser.setNoteName(bean.ReceiveUserNickName);
                            fuser.setIcon(bean.ReceiveUserICON);
                        } else {
                            fuser.setUid(bean.SendUserID);
                            fuser.setNickname(bean.SendUserNickName);
                            fuser.setNoteName(bean.SendUserNickName);
                            fuser.setIcon(bean.SendUserICON);
                        }

                        ChatRecord record = new ChatRecord();

                        record.setId(System.currentTimeMillis()); // 消息id
                        record.setUid(me.getUid());
                        record.setNickname(me.getNickname());
                        record.setIcon(me.getIcon());
                        record.setVip(me.getViplevel());
                        record.setDatetime(bean.SendTime * 1000);
                        record.setType(Integer.toString(ChatRecordViewFactory.CHCAT_AUDIO));
                        record.setStatus(ChatRecordStatus.ARRIVED); // TODO：发送中改为送达
                        record.setContent(mStatus[5]);
                        record.setUpload(false);

                        record.setfLat(me.getLat());
                        record.setfLng(me.getLng());
                        GeoData geo = LocationUtil.getCurrentGeo(BaseApplication.appContext);
                        record.setDistance(LocationUtil.calculateDistance(me.getLng(), me.getLat(), geo.getLng(), geo.getLat()));
                        ChatPersonalModel.getInstance().insertOneRecord(BaseApplication.appContext, fuser, record, MessageBelongType.SEND, 1);
                        EventBus.getDefault().post("audio_chat_record");
                        EventBus.getDefault().post("refersh_message");
                        break;
                }
            }
        }

    }
}
