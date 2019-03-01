package net.iaround.connector;


import android.content.Context;

import net.iaround.conf.MessageID;
import net.iaround.model.im.AudioSendBack;
import net.iaround.model.im.ChatBarAudioEndSuccess;
import net.iaround.model.im.ChatBarAudioFail;
import net.iaround.model.im.ChatRecordStatus;
import net.iaround.model.im.GroupAudioEndFail;
import net.iaround.model.im.GroupAudioEndSuccess;
import net.iaround.model.im.GroupMessageSendFail;
import net.iaround.model.im.GroupMessageSendSuccess;
import net.iaround.model.im.PrivateAudioEndFail;
import net.iaround.model.im.PrivateAudioEndSuccess;
import net.iaround.model.im.SocketFailWithFlagResponse;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.chat.MessagesSendManager;
import net.iaround.ui.datamodel.ChatPersonalModel;
import net.iaround.ui.datamodel.GroupChatListModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;


/**
 * @author KevinSu kevinsu917@126.com
 * @create 2015年3月24日 下午7:17:34
 * @Description:处理关于聊天发消息(主动请求)的返回(Socket返回)-不处理推送和心跳
 */
public class ChatReceivedHandler {

    private final String TAG = this.getClass().getName();
    private Context context;
    private ConnectorManage manager;
    private static ChatReceivedHandler instance;

    private HashSet<Integer> handleMessageId = new HashSet<Integer>();

    private ChatReceivedHandler(Context context, ConnectorManage manager) {
        this.context = context;
        this.manager = manager;
        initIdSet();
    }

    /**
     * 初始化需要处理的协议set
     */
    private void initIdSet() {
        handleMessageId.add(MessageID.GROUP_SEND_MESSAGE_Y);
        handleMessageId.add(MessageID.GROUP_SEND_MESSAGE_N);

        handleMessageId.add(MessageID.SESSION_SEND_PRIVATE_CHAT_SUCC);
        handleMessageId.add(MessageID.SESSION_SEND_PRIVATE_CHAT_FAIL);

        handleMessageId.add(MessageID.SESSION_PRIVATE_AUDIO_BEGIN_SUCCESS);
        handleMessageId.add(MessageID.SESSION_PRIVATE_AUDIO_BEGIN_FAIL);

        handleMessageId.add(MessageID.SESSION_PRIVATE_AUDIO_SEND_SUCCESS);
        handleMessageId.add(MessageID.SESSION_PRIVATE_AUDIO_SEND_FAIL);

        handleMessageId.add(MessageID.SESSION_PRIVATE_AUDIO_END_SUCCESS);
        handleMessageId.add(MessageID.SESSION_PRIVATE_AUDIO_END_FAIL);

        handleMessageId.add(MessageID.SESSION_GROUP_AUDIO_BEGIN_SUCCESS);
        handleMessageId.add(MessageID.SESSION_GROUP_AUDIO_BEGIN_FAIL);

        handleMessageId.add(MessageID.SESSION_GROUP_AUDIO_SEND_SUCCESS);
        handleMessageId.add(MessageID.SESSION_GROUP_AUDIO_SEND_FAIL);

        handleMessageId.add(MessageID.SESSION_GROUP_AUDIO_END_SUCCESS);
        handleMessageId.add(MessageID.SESSION_GROUP_AUDIO_END_FAIL);

        handleMessageId.add(MessageID.CHATBAR_AUDIO_SEND_BEGIN_Y);
        handleMessageId.add(MessageID.CHATBAR_AUDIO_SEND_BEGIN_N);

        handleMessageId.add(MessageID.CHATBAR_AUDIO_SENDING_Y);
        handleMessageId.add(MessageID.CHATBAR_AUDIO_SENDING_N);

        handleMessageId.add(MessageID.CHATBAR_AUDIO_SEND_END_Y);
        handleMessageId.add(MessageID.CHATBAR_AUDIO_SEND_END_N);

    }

    public static ChatReceivedHandler getInstance(Context context, ConnectorManage manager) {
        if (instance == null) {
            instance = new ChatReceivedHandler(context, manager);
        }
        return instance;
    }

    /**
     * 处理消息发送的回复消息,在这里处理的[数据库,文件的]的操作,UI的放到页面去处理
     *
     * @param messageId
     * @param contentJson
     * @return 是否拦截处理, true为拦截, false为传递
     */
    public boolean handleMessage(int messageId, String contentJson) {

        if (!inIncludeHandleSet(messageId))
            return false;

        if (messageId == MessageID.GROUP_SEND_MESSAGE_Y) {
            GroupMessageSendSuccess bean = GsonUtil.getInstance()
                    .getServerBean(contentJson, GroupMessageSendSuccess.class);
            groupChatOnSendSuccess(bean.flag, bean.msgid, bean.incmsgid);
        } else if (messageId == MessageID.GROUP_SEND_MESSAGE_N) {
            GroupMessageSendFail bean = GsonUtil.getInstance()
                    .getServerBean(contentJson, GroupMessageSendFail.class);
            handleSendFailMessage(0, bean.flag);
        } else if (messageId == MessageID.SESSION_SEND_PRIVATE_CHAT_SUCC) {
            PrivateAudioEndSuccess bean = GsonUtil.getInstance()
                    .getServerBean(contentJson, PrivateAudioEndSuccess.class);
            privateChatOnSendSuccess(bean.flag, bean.msgid, bean.distance);
        } else if (messageId == MessageID.SESSION_SEND_PRIVATE_CHAT_FAIL) {
            PrivateAudioEndFail bean = GsonUtil.getInstance()
                    .getServerBean(contentJson, PrivateAudioEndFail.class);
            handleSendFailMessage(0, bean.flag);
        } else if (messageId == MessageID.SESSION_PRIVATE_AUDIO_BEGIN_SUCCESS) {
            AudioSendBack back = GsonUtil.getInstance()
                    .getServerBean(contentJson, AudioSendBack.class);
            MessagesSendManager.getManager(context).StartSendThread(back.flag);
        } else if (messageId == MessageID.SESSION_PRIVATE_AUDIO_BEGIN_FAIL) {
            SocketFailWithFlagResponse bean = GsonUtil.getInstance()
                    .getServerBean(contentJson, SocketFailWithFlagResponse.class);
            handleSendFailMessage(0, bean.flag);
        } else if (messageId == MessageID.SESSION_PRIVATE_AUDIO_END_SUCCESS) {
            PrivateAudioEndSuccess bean = GsonUtil.getInstance()
                    .getServerBean(contentJson, PrivateAudioEndSuccess.class);
            privateChatOnSendSuccess(bean.flag, bean.msgid, bean.distance);
        } else if (messageId == MessageID.SESSION_PRIVATE_AUDIO_END_FAIL) {
            PrivateAudioEndFail bean = GsonUtil.getInstance()
                    .getServerBean(contentJson, PrivateAudioEndFail.class);
            handleSendFailMessage(0, bean.flag);
        } else if (messageId == MessageID.SESSION_GROUP_AUDIO_BEGIN_SUCCESS) {
            AudioSendBack back = GsonUtil.getInstance()
                    .getServerBean(contentJson, AudioSendBack.class);
            MessagesSendManager.getManager(context).StartSendThread(back.flag);
        } else if (messageId == MessageID.SESSION_GROUP_AUDIO_BEGIN_FAIL) {
            GroupAudioEndFail bean = GsonUtil.getInstance()
                    .getServerBean(contentJson, GroupAudioEndFail.class);
            handleSendFailMessage(0, bean.flag);
        } else if (messageId == MessageID.SESSION_GROUP_AUDIO_END_SUCCESS) {
            GroupAudioEndSuccess bean = GsonUtil.getInstance()
                    .getServerBean(contentJson, GroupAudioEndSuccess.class);
            groupChatOnSendSuccess(bean.flag, bean.msgid, bean.incmsgid);
        } else if (messageId == MessageID.SESSION_GROUP_AUDIO_END_FAIL) {
            GroupAudioEndFail bean = GsonUtil.getInstance()
                    .getServerBean(contentJson, GroupAudioEndFail.class);
            handleSendFailMessage(0, bean.flag);
        } else if (messageId == MessageID.CHATBAR_AUDIO_SEND_BEGIN_Y)//聊吧
        {
            AudioSendBack back = GsonUtil.getInstance()
                    .getServerBean(contentJson, AudioSendBack.class);
            MessagesSendManager.getManager(context).StartSendThread(back.flag);
        } else if (messageId == MessageID.CHATBAR_AUDIO_SEND_BEGIN_N) {
            ChatBarAudioFail bean = GsonUtil.getInstance()
                    .getServerBean(contentJson, ChatBarAudioFail.class);
            handleSendFailMessage(0, bean.flag);
        } else if (messageId == MessageID.CHATBAR_AUDIO_SEND_END_Y) {
            ChatBarAudioEndSuccess bean = GsonUtil.getInstance()
                    .getServerBean(contentJson, ChatBarAudioEndSuccess.class);
            chatbarChatOnSendSuccess(bean.flag, bean.msgid);
        } else if (messageId == MessageID.CHATBAR_AUDIO_SEND_END_N) {
            ChatBarAudioFail bean = GsonUtil.getInstance()
                    .getServerBean(contentJson, ChatBarAudioFail.class);
            handleSendFailMessage(0, bean.flag);
        } else if (isReceiveData(messageId)) {
            CountSendDataReceive(contentJson);
        }

        return true;
    }

    private boolean isReceiveData(int messageId) {
        return messageId == MessageID.SESSION_PRIVATE_AUDIO_SEND_FAIL ||
                messageId == MessageID.SESSION_PRIVATE_AUDIO_SEND_SUCCESS ||
                messageId == MessageID.SESSION_GROUP_AUDIO_SEND_FAIL ||
                messageId == MessageID.SESSION_GROUP_AUDIO_SEND_SUCCESS ||
                messageId == MessageID.CHATBAR_AUDIO_SENDING_Y ||
                messageId == MessageID.CHATBAR_AUDIO_SENDING_N;
    }

    // 计数发送数据的返回的数量
    private void CountSendDataReceive(String result) {
        try {
            JSONObject json = new JSONObject(result);
            long flag = json.optLong("flag");
            MessagesSendManager.getManager(context).PutInReceiveCount(flag);
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }

    /**
     * 处理因为发送异常导致的失败情况-只和聊天相关的协议,需要处理相对应消息数据内容
     */
    public void handleSendError(int e, long flag) {
        manager.onSendCallBack(e, flag);
        handleSendFailMessage(e, flag);
    }

    /**
     * 圈聊、私聊失败对数据库的处理
     *
     * @param e
     * @param flag
     */
    private void handleSendFailMessage(int e, long flag) {
        // 这里判断回复的是私聊的，还是圈聊的。
        if (ChatPersonalModel.getInstance().getRecordLocalId(flag) != null) {
            ChatPersonalModel.getInstance().handleMessageInDataBase(context, e, flag);
            ChatPersonalModel.getInstance().removeRecordLocalId(flag);
            MessagesSendManager.getManager(context).getMessageBeanSparseArray().remove(flag);
        } else if (GroupChatListModel.getInstance().getRecordLocalId(flag) != null) {

            long localId = GroupChatListModel.getInstance().getRecordLocalId(flag);
            String statusStr = String.valueOf(ChatRecordStatus.FAIL);

            GroupChatListModel.getInstance()
                    .modifyGroupMessageStatus(context, localId, flag, statusStr, 0l);
            // GroupChatListModel.getInstance().removeRecordLocalId(flag);
            MessagesSendManager.getManager(context).getMessageBeanSparseArray().remove(flag);
        }
    }

    /**
     * 私聊成功对数据库的处理
     *
     * @param flag
     * @param msgid
     * @param distance
     */
    private void privateChatOnSendSuccess(long flag, long msgid, int distance) {

        MessagesSendManager.getManager(context).getMessageBeanSparseArray().remove(flag);

        Long localId = ChatPersonalModel.getInstance().getRecordLocalId(flag); // 记录的本地ID
        if (localId != null) {
            String arrived = String.valueOf(ChatRecordStatus.ARRIVED);// 2为已达
            String localIDStr = String.valueOf(localId);
            String msgIDStr = String.valueOf(msgid);

            try {
                ChatPersonalModel.getInstance()
                        .modifyMessageId(context, localIDStr, msgIDStr, arrived, distance);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ChatPersonalModel.getInstance().removeRecordLocalId(flag);

        } else {
            long id = Thread.currentThread().getId();
            CommonFunction
                    .log(TAG, "thread_id==" + id + "--privateChatOnSendSuccess----flag==" + flag);
        }
    }

    /**
     * 圈聊成功对数据库的处理
     *
     * @param flag
     * @param msgid
     */
    private void groupChatOnSendSuccess(long flag, long msgid, long incmsgid) {


        MessagesSendManager.getManager(context).getMessageBeanSparseArray().remove(flag);

        Long localId = GroupChatListModel.getInstance().getRecordLocalId(flag); // 记录的本地ID

        // CommonFunction.myWriteLog(TAG,
        // FilterUtil.getGroupMsgLog("modifyMessage",
        // localId,0,msgid,incmsgid));

        if (localId != null) {
            String success = String.valueOf(ChatRecordStatus.ARRIVED);// 0为成功

            GroupChatListModel.getInstance()
                    .modifyGroupMessageStatus(context, msgid, localId, flag, success, incmsgid);
            GroupChatListModel.getInstance().removeRecordLocalId(flag);
        } else {
            CommonFunction.log(TAG, "groupChatOnSendSuccess----flag==" + flag);
        }

    }

    /**
     * 聊吧聊天发送成功后的处理
     */
    private void chatbarChatOnSendSuccess(long flag, long msgid) {
        CommonFunction
                .log("sherlock", "ChatReceivedHandler.chatbarChatOnSendSuccess flag, msgid == " + flag,
                        msgid);
    }

    // 判断是否在需要处理的范围
    private boolean inIncludeHandleSet(int id) {
        return handleMessageId.contains(id);
    }

}
