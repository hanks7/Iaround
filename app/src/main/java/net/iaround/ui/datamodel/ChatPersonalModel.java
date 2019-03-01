package net.iaround.ui.datamodel;


import android.content.Context;
import android.database.Cursor;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import net.iaround.conf.Common;
import net.iaround.database.DatabaseFactory;
import net.iaround.database.NearContactWorker;
import net.iaround.database.PersonalMessageWorker;
import net.iaround.entity.PrivateChatInfo;
import net.iaround.model.im.AccostRelationes;
import net.iaround.model.im.ChatMessageBean;
import net.iaround.model.im.ChatRecord;
import net.iaround.model.im.ChatRecordStatus;
import net.iaround.model.im.NearContact;
import net.iaround.model.im.PrivateChatMessage;
import net.iaround.model.im.TransportMessage;
import net.iaround.model.im.UserTypeOne;
import net.iaround.model.im.type.MessageBelongType;
import net.iaround.model.im.type.SubGroupType;
import net.iaround.model.skill.SkillAttackResult;
import net.iaround.model.type.FileUploadType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.chat.MessagesSendManager;
import net.iaround.ui.chat.view.ChatRecordViewFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;


/**
 * 私聊数据模型,搭讪 用于所有与私聊相关的数据处理,对应的Activity:@ChatPersonal,@AccostGameActivity
 *
 * @author linyg
 * @Description Model层用于处理业务成与数据层（数据库、文件）之间的处理
 */
public class ChatPersonalModel extends Model {
    private static ChatPersonalModel model;

    // PersonalMessageWorker中未知距离
    public static int UNKNOWN_DISTANCE = -1;
    // 私聊中的对象id
    private long chatTargetId;
    // 第一个搭讪的人的第一条搭讪消息时为真，之后的搭讪消息为假，用于响铃振动
    public boolean newManAccost = true;
    // 是否处于私聊中,也就是ChatPerson中
    private boolean mIsInPersonalChat;
    // 是否为悄悄查看状态
    private boolean isQuietMode = false;
    // socket成功发送聊天记录后，聊天记录被插入到数据库的本地ID的map
    private HashMap<Long, Long> recordLocalIdMap;

    private HashMap<Long, Integer> anchorMap = new HashMap<>();//临时缓存主播用户ID，用来判断对方是不是主播

    private ChatPersonalModel() {
        mIsInPersonalChat = false;
        recordLocalIdMap = new HashMap<Long, Long>();
    }

    public static ChatPersonalModel getInstance() {
        if (model == null) {
            model = new ChatPersonalModel();
        }
        return model;
    }

    /**
     * 设置聊天草稿
     *
     * @param fuid
     * @param msg
     */
    public void setChatDraft(Context context, long fuid, String msg) {
        SharedPreferenceUtil.getInstance(context).putString("chat_personal_draft" + fuid, msg);
    }

    /**
     * 获取草稿内容
     *
     * @param fuid
     * @return
     */
    public String getChatDraft(Context context, long fuid) {
        return SharedPreferenceUtil.getInstance(context)
                .getString("chat_personal_draft" + fuid);
    }

    /**
     * 释放资源
     *
     * @time 2011-7-4 上午11:00:28
     * @author:linyg
     */
    public void reset() {
        recordLocalIdMap.clear();
        model = null;
    }

    /**
     * 是否离开私聊界面
     *
     * @return true: 离开
     */
    public boolean isInPersonalChat() {
        return mIsInPersonalChat;
    }

    /**
     * 是否离开私聊界面
     *
     * @param isInChat true: 离开
     */
    public void setIsInPersonalChat(boolean isInChat) {
        this.mIsInPersonalChat = isInChat;
    }

    public boolean isQuietMode() {
        return isQuietMode;
    }

    public void setQuietMode(boolean isQuietMode) {
        this.isQuietMode = isQuietMode;
    }

    /**
     * 获取私聊记录的本地ID
     *
     * @param flag
     * @return
     */
    public Long getRecordLocalId(long flag) {
        if (recordLocalIdMap == null) {
            return Long.valueOf(-1);
        }
        Long f = Long.valueOf(flag);
        if (f == null) {
            return Long.valueOf(-1);
        }
        return recordLocalIdMap.get(f); // 记录的本地ID
    }

    /**
     * 删除私聊记录的本地ID
     *
     * @param flag
     */
    public void removeRecordLocalId(long flag) {
        recordLocalIdMap.remove(Long.valueOf(flag));
    }

    /**
     * 保存该记录的Flag，用于后续服务器返回该记录的msgId后，查出该记录，更新记录的msgId
     *
     * @param flag
     * @param localId
     */
    public void saveRecordLocalId(long flag, long localId) {
        recordLocalIdMap.put(flag, localId);
    }

    /**
     * 反序显示聊天记录
     *
     * @param context
     * @param mUid
     * @param fUid
     * @return
     */
    public ArrayList<ChatRecord> getChatRecord(Context context, String mUid, String fUid,
                                               int start, int amount) {
        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);
        Cursor cursor = db.onSelectChatRecord(context, mUid, fUid, start, amount);
        ArrayList<ChatRecord> records = parseCursor(context, cursor);
        ListIterator<ChatRecord> it = records.listIterator(records.size());
        ArrayList<ChatRecord> reversRecords = new ArrayList<ChatRecord>(records.size());
        while (it.hasPrevious()) {
            reversRecords.add(it.previous());
        }
        return reversRecords;
    }

    /**
     * 正序显示聊天记录
     *
     * @param context
     * @param mUid
     * @param fUid
     * @param start
     * @param amount
     * @return
     */
    public ArrayList<ChatRecord> getChatRecordDESC(Context context, String mUid, String fUid,
                                                   int start, int amount) {
        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);
        Cursor cursor = db.onSelectAccostChatRecordDESC(context, mUid, fUid, start, amount);

        ArrayList<ChatRecord> records = parseCursor(context, cursor);
        return records;
    }

    /**
     * 解析从数据库查找出来的记录cursor
     *
     * @param cursor
     * @return
     */
    private ArrayList<ChatRecord> parseCursor(Context context, Cursor cursor) {
        ArrayList<ChatRecord> records = new ArrayList<ChatRecord>();
        try {
            cursor.moveToFirst();
            while (!(cursor.isAfterLast())) {

                int contentIndex = cursor.getColumnIndex(PersonalMessageWorker.CONTENT);
                String strJson = cursor.getString(contentIndex);

                int fuidIndex = cursor.getColumnIndex(PersonalMessageWorker.F_UID);
                long friendUserId = cursor.getLong(fuidIndex);

                ChatRecord record = parsePersonalRecord(strJson, friendUserId);

                if (record != null) {

                    int idIndex = cursor.getColumnIndex(PersonalMessageWorker.ID);
                    long localId = cursor.getLong(idIndex);

                    // 5.7新增如果消息是场景礼物时,不展示,6.0真心话大冒险不显示
                    if (!bIsFilterOut(record.getType())) {

                        record.setLocid(localId);

                        // 距离不能取content中的,因为返回成功后,修改距离的时候没有修改content中的距离
                        int distanceIndex = cursor.getColumnIndex(PersonalMessageWorker.DISTANCE);
                        record.setDistance(cursor.getInt(distanceIndex));

                        int serverIdIndex = cursor
                                .getColumnIndex(PersonalMessageWorker.SERVER_ID);
                        record.setId(cursor.getLong(serverIdIndex));

                        int statusIndex = cursor.getColumnIndex((PersonalMessageWorker.STATUS));
                        record.setStatus(cursor.getInt(statusIndex));

                        int giftStatusIndex = cursor
                                .getColumnIndex((PersonalMessageWorker.GIFTSTATUS));
                        record.setGiftStatus(cursor.getInt(giftStatusIndex));

                        records.add(record);

                    } else {
                        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);
                        db.deleteMsgByLocalId(localId);
                    }
                }
                cursor.moveToNext();
            }
        } catch (Exception e) {
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return records;
    }

    /**
     * 从数据库取出content字段的数据,转换成一条ChatRecord记录
     */
    public ChatRecord parsePersonalRecord(String strJson, long friendUserId) {

        ChatRecord record = new ChatRecord();

        PrivateChatMessage chatMessage = JSON.parseObject(strJson, PrivateChatMessage.class);
        record.setId(chatMessage.msgid);
        record.setRelationship(chatMessage.relation);

        //获取到groupid
        record.setGroupid(chatMessage.groupid);

        // 这里要转成json字符串,不能直接用chatMessage.content,
        // 因为chatMessage的content是一个Object对象,toString方法会将其转成一个非json字符串
        //Log.d("System.err","No value for content = "+strJson);
        String content = "";
        try {
            content = new JSONObject(strJson).getString("content");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        record
                .initBaseInfo(chatMessage.datetime, chatMessage.type, chatMessage.attachment, content,
                        chatMessage.from);

        User me = Common.getInstance().loginUser;
        record.initMineInfo(me);

        if (chatMessage.user.userid == me.getUid()) {
            record.setSendType(MessageBelongType.SEND);
            // 这里只能初始化对方用户的uid,其他信息拿不到
            record.setFuid(friendUserId);
        } else {
            record.setSendType(MessageBelongType.RECEIVE);
            record.initFriendInfo(convertToUser(chatMessage.user));
        }
        return record;
    }


    /**
     * 用于更新委托数据类型的已点击状态
     *
     * @param status 点击状态，0：未点击，1：已点击
     */
    public void updateDelegationClickedStatus(Context mContext, long msgid, int status) {
        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(mContext);

        Cursor cursor = db.onSelectByMsgid(String.valueOf(msgid));
        if (cursor == null || cursor.getCount() <= 0)
            return;

        cursor.moveToFirst();
        String tbContent = cursor
                .getString(cursor.getColumnIndex(PersonalMessageWorker.CONTENT));
        PrivateChatMessage chatMessage = JSON.parseObject(tbContent, PrivateChatMessage.class);
        String content = "";
        try {
            content = new JSONObject(tbContent).getString("content");
        } catch (JSONException e) {
            e.printStackTrace();
        }
//		ChatBarDelegationNoticeBean contentObj = null;
//		if ( !CommonFunction.isEmptyOrNullStr( content ) )
//			contentObj = GsonUtil.getInstance( )
//				.getServerBean( content, ChatBarDelegationNoticeBean.class );
//
//		if ( contentObj != null )
//		{
//			contentObj.clickSuccess = status;
//			chatMessage.content = contentObj;
//			String newTbContent = GsonUtil.getInstance( ).getStringFromJsonObject( chatMessage );
//			db.onModifyContentByMsgid( String.valueOf( msgid ), newTbContent );
//		}

    }

    private User convertToUser(UserTypeOne userTypeOne) {
        User fUser = new User();
        fUser.setUid(userTypeOne.userid);
        fUser.setIcon(userTypeOne.getIcon());

        // 1--男 2--女 0--其他
        String genderStr = userTypeOne.getGender().trim();
        fUser.setSex(genderStr.equals("all") ? 0 : (genderStr.equals("m") ? 1 : 2));
        fUser.setAge(userTypeOne.age);
        fUser.setLat(userTypeOne.lat);
        fUser.setLng(userTypeOne.lng);
        fUser.setNoteName(userTypeOne.getNotes());
        fUser.setNickname(userTypeOne.getNickname());
        fUser.setViplevel(userTypeOne.viplevel);
        fUser.setPhotoNum(userTypeOne.photonum);
        fUser.setSVip(userTypeOne.svip);
        fUser.setUserType(userTypeOne.userType);

        return fUser;
    }

    private UserTypeOne convertToUserTypeOne(User user) {
        UserTypeOne userTypeOne = new UserTypeOne();
        userTypeOne.userid = user.getUid();
        userTypeOne.setIcon(user.getIcon());
        userTypeOne.setGender(user.getGender());
        userTypeOne.age = user.getAge();
        userTypeOne.lat = user.getLat();
        userTypeOne.lng = user.getLng();
        userTypeOne.setNotes(user.getNoteName(false));
        userTypeOne.setNickname(user.getNickname());
        userTypeOne.viplevel = user.getViplevel();
        userTypeOne.photonum = user.getPhotoNum();

        return userTypeOne;
    }

    /**
     * 发送信息插入数据库
     *
     * @param context
     * @param fUser
     * @param record
     * @param sendType
     * @param subGroupType
     * @param from
     * @param newFlag
     * @return
     */
    public long insertOneRecord(Context context, User fUser, ChatRecord record, int sendType,
                                int subGroupType, int from, int newFlag) {

        PrivateChatMessage chatMessage = new PrivateChatMessage();

        UserTypeOne user = convertToUserTypeOne(Common.getInstance().loginUser);
        chatMessage.user = user;

        chatMessage.type = Integer.valueOf(record.getType());
        chatMessage.data = null;
        chatMessage.msgid = record.getId();
        chatMessage.datetime = record.getDatetime();
        chatMessage.relation = record.getRelationship();
        chatMessage.attachment = record.getAttachment();
        chatMessage.from = from;
        chatMessage.distance = record.getDistance();
        chatMessage.flag = record.getFlag();
        if (subGroupType == SubGroupType.ReceiveAccost || subGroupType == SubGroupType.SendAccost) {
            chatMessage.mtype = 1;// 搭讪
        } else {
            chatMessage.mtype = 0;// 正常私聊
        }
        chatMessage.content = record.getContent();

        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);

        String mUid = String.valueOf(Common.getInstance().loginUser.getUid());
        String fUid = String.valueOf(fUser.getUid());
        String serverId = String.valueOf(record.getId());
        String content = GsonUtil.getInstance().getStringFromJsonObject(chatMessage);
        String dateTime = String.valueOf(record.getDatetime());
        int status = record.getStatus();
        int distance = record.getDistance();
        int messageType = Integer.valueOf(record.getType());
        long flag = chatMessage.flag;

        // 如果接收到的是真心话大冒险的问题的话，不需要更新最近联系人列表
        if (messageType != ChatRecordViewFactory.ACCOST_GAME_QUE) {
            CommonFunction.log("sherlock", "ChatPersonalModel.insertOneRecord fuser == " +
                    GsonUtil.getInstance().getStringFromJsonObject(fUser));
            NearContactWorker ncdb = DatabaseFactory.getNearContactWorker(context);
            ncdb.updateContact(mUid, fUser, content, status, dateTime, sendType, subGroupType);
        }

        return db.onInsert(context, mUid, fUid, serverId, content, status, sendType, distance,
                messageType, from, flag, newFlag);
    }

    /**
     * 从网络接收到消息，插入一条数据
     *
     * @param mUid
     * @param msg
     * @return
     * @throws JSONException
     * @time 2011-7-1 上午11:00:03
     * @author:linyg
     */
    public long insertOneRecord(Context context, String mUid, TransportMessage msg, int status) {

        PrivateChatMessage bean = JSON.parseObject(msg.getContentBody(), PrivateChatMessage.class);

        int distance = bean.distance;
        int messageType = bean.type;
        int mtype = bean.mtype;
        int from = bean.from;
        String dateTime = String.valueOf(bean.datetime);
        String content = msg.getContentBody();

        // 判断需要把联系人消息分在正常私聊分组还是搭讪分组
        // 在判断应该插入哪个分组是，应该先判断是否缓存了双方的关系。因为，当双方关系通过收到一条消息打破时，
        // 有可能先收到打破关系的消息，再收到私聊消息，导致分组出错。
        int subGroupType = mtype == 0 ? SubGroupType.NormalChat : SubGroupType.ReceiveAccost;
        long uid = Common.getInstance().loginUser.getUid();
        int ischat = ChatPersonalModel.getInstance()
                .getAccostRelation(context, uid, bean.user.userid);
        if (ischat == 1)// 已经聊过
        {
            subGroupType = SubGroupType.NormalChat;
        }

        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);
        String fUid = String.valueOf(bean.user.userid);
        String msgID = String.valueOf(bean.msgid);
        long flag = bean.flag;

        // 防止服务端下发两条相同flag的消息
        if (flag > 0 && db.getCountByFlag(mUid, fUid, flag) > 0) {
            return 0;
        } else {
            int sendtype = MessageBelongType.RECEIVE;

            // 如果接收到的是真心话大冒险的问题的话，不需要更新最近联系人列表
            if (messageType != ChatRecordViewFactory.ACCOST_GAME_QUE) {
                NearContactWorker ncdb = DatabaseFactory.getNearContactWorker(context);
                User fUser = convertToUser(bean.user);
                ncdb.updateContact(mUid, fUser, content, status, dateTime, sendtype,
                        subGroupType);

            }

            int newFlag = 1;
            if (isInPersonalChat() && !isQuietMode && bean.user.userid == chatTargetId) {// 当在私聊界面且不是悄悄模式的时候收到消息才是旧消息
                newFlag = 0;
            }
            return db.onInsert(context, mUid, fUid, msgID, content, status, sendtype, distance,
                    messageType, from, flag, newFlag);
        }
    }

    /**
     * 私聊时，发送出去的消息。插入发送的数据
     *
     * @param context
     * @param fUser        对方的个人信息
     * @param record       消息实体
     * @param subGroupType 分组@SubGroupType
     * @param from         从哪里聊天@hatFromType
     * @return
     */
    public long insertOneRecord(Context context, User fUser, ChatRecord record, int subGroupType,
                                int from) {
        if (anchorMap.get(fUser.getUid()) != null)
            fUser.setUserType(1);
        return insertOneRecord(context, fUser, record, MessageBelongType.SEND, subGroupType, from,
                0);
    }

    /**
     * 根据本地id，修改数据库(聊天消息与最近聊天联系列表)的状态
     *
     * @param context
     * @param locid   本地id
     * @param status  消息状态1发送中, 2已送达，3已阅读, 4失败
     * @return
     * @throws JSONException
     */
    private long modifyRecordStatus(Context context, String locid,
                                    String status) throws JSONException {
        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);

        Cursor cursor = db.onSelectRecord(locid);
        if (cursor != null && cursor.moveToFirst()) {
            int contentIndex = cursor.getColumnIndex(PersonalMessageWorker.CONTENT);
            int mUidIndex = cursor.getColumnIndex(PersonalMessageWorker.M_UID);
            int fUidIndex = cursor.getColumnIndex(PersonalMessageWorker.F_UID);

            String content = cursor.getString(contentIndex);
            JSONObject jsonObj = new JSONObject(content);
            // 用dateTime来确定与最近联系人的数据中的数据对比
            String dateTime = jsonObj.getString("datetime");
            String mUID = cursor.getString(mUidIndex);
            String fUID = cursor.getString(fUidIndex);

            modifyNearContactStatus(context, mUID, fUID, dateTime, status);
        }

        return db.onModify(context, locid, status);
    }

    // 更新对应消息的状态
    private void modifyNearContactStatus(Context context, String mUid, String fUid,
                                         String dateTime, String status) {
        NearContactWorker db = DatabaseFactory.getNearContactWorker(context);
        db.updateStatus(mUid, fUid, dateTime, status);
    }

    /**
     * 更改message的消息id
     *
     * @param locid
     * @param server_id
     * @param status
     * @return long
     * @throws JSONException
     */
    public long modifyMessageId(Context context, String locid, String server_id, String status,
                                int distance) throws JSONException {
        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);

        Cursor cursor = db.onSelectRecord(locid);
        if (cursor != null && cursor.moveToFirst()) {
            int contentIndex = cursor.getColumnIndex(PersonalMessageWorker.CONTENT);
            int mUidIndex = cursor.getColumnIndex(PersonalMessageWorker.M_UID);
            int fUidIndex = cursor.getColumnIndex(PersonalMessageWorker.F_UID);

            String content = cursor.getString(contentIndex);
            JSONObject jsonObj = new JSONObject(content);
            // 用dateTime来确定与最近联系人的数据中的数据对比
            String dateTime = jsonObj.getString("datetime");
            String mUID = cursor.getString(mUidIndex);
            String fUID = cursor.getString(fUidIndex);

            db.readAllMsg(Long.parseLong(mUID), Long.parseLong(fUID));
            modifyNearContactStatus(context, mUID, fUID, dateTime, status);
        }

        cursor.close();
        return db.onModifyMessageIdStatusDistance(locid, server_id, status, distance);
    }

    /**
     * 修改消息的attachment的URL
     *
     * @param context
     * @param locid
     * @param url
     */
    public void modifyMessageAttachment(Context context, String locid, String url) {
        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);

        Cursor cursor = db.onSelectRecord(locid);
        if (cursor != null && cursor.moveToFirst()) {
            int contentIndex = cursor.getColumnIndex(PersonalMessageWorker.CONTENT);
            String conString = cursor.getString(contentIndex);

            JSONObject json = null;
            try {
                json = new JSONObject(conString);
                json.put("attachment", url);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String modifyContent = json.toString();

            db.onModifyMessageContent(locid, modifyContent);
        }
    }

    /**
     * 根据msg id 修改消息的attachment
     *
     * @param context
     * @param msgId
     * @param attachment
     */
    public void modifyMessageAttachmentByMsgId(Context context, String msgId, String attachment) {
        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);

        Cursor cursor = db.onSelectByMsgid(msgId);
        if (cursor != null && cursor.moveToFirst()) {
            int contentIndex = cursor.getColumnIndex(PersonalMessageWorker.CONTENT);
            String conString = cursor.getString(contentIndex);

            JSONObject json = null;
            try {
                json = new JSONObject(conString);
                json.put("attachment", attachment);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String modifyContent = json.toString();

            db.onModifyContentByMsgid(msgId, modifyContent);
        }
    }

    /**
     * 根据msgID得到消息实体
     *
     * @param context
     * @param msgId
     */
    public PrivateChatMessage getMessageAttachmentByMsgId(Context context, String msgId) {
        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);

        Cursor cursor = db.onSelectByMsgid(msgId);
        if (cursor != null && cursor.moveToFirst()) {
            int contentIndex = cursor.getColumnIndex(PersonalMessageWorker.CONTENT);
            String conString = cursor.getString(contentIndex);

            PrivateChatMessage message = GsonUtil.getInstance().getServerBean(conString, PrivateChatMessage.class);
            return message;
        }
        return null;
    }

    /**
     * 修改消息的内容
     *
     * @param context
     * @param locid
     * @param content
     */
    public void modifyMessageContent(Context context, String locid, String content) {
        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);

        Cursor cursor = db.onSelectRecord(locid);
        if (cursor != null && cursor.moveToFirst()) {
            int contentIndex = cursor.getColumnIndex(PersonalMessageWorker.CONTENT);
            String conString = cursor.getString(contentIndex);

            JSONObject json = null;
            try {
                json = new JSONObject(conString);
                json.put("content", content);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String modifyContent = json.toString();

            db.onModifyMessageContent(locid, modifyContent);
        }
        if (null != cursor) {
            cursor.close();
        }
    }

    /**
     * 获取与某个用户的聊天记录
     *
     * @param mUid
     * @param fUid
     * @param start
     * @param amount
     * @return
     * @time 2011-7-1 上午11:19:04
     * @author:linyg
     */
    public ArrayList<ChatRecord> selectPageRecord(Context context, String mUid, String fUid,
                                                  int start, int amount) {
        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);
        Cursor cursor = db.onSelectPage(context, mUid, fUid, start, amount);
        return parseCursor(context, cursor);
    }

    /**
     * 读取私聊未读消息的总数 不加上搭讪消息数或搭讪人数
     *
     * @param mUid
     * @return
     * @time 2011-7-12 上午10:21:48
     * @author:linyg
     */
    public int countNoRead(Context context, String mUid) {
        NearContactWorker db = DatabaseFactory.getNearContactWorker(context);
        return db.countNoRead(mUid);
    }

    /**
     * 获取私聊未读信息数加上未读搭讪人数
     *
     * @author tanzy
     */
    public int countNoReadAndAccostSender(Context context, String mUid) {
        int count = 0;
        NearContactWorker db = DatabaseFactory.getNearContactWorker(context);
        count = db.countNoRead(mUid);

        Cursor cursor = db.selectRecieveAccostPage(mUid);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            if (cursor.getInt(cursor.getColumnIndex(NearContactWorker.NONEREAD)) > 0) {
                count++;
            }
        }
        if (cursor != null)
            cursor.close();
        return count;
    }

    /**
     * 读取私聊未读消息的发出者数量
     */
    public int countNoReadSender(Context context, String mUid) {
        NearContactWorker db = DatabaseFactory.getNearContactWorker(context);
        return db.countNoReadSender(mUid);
    }

    /**
     * 删除我与好友之间的消息
     *
     * @param mUid
     * @param fUid
     * @return
     * @time 2011-7-7 上午10:50:10
     * @author:linyg
     */
    public int deleteRecord(Context context, String mUid, String fUid) {
        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);
        return db.deleteMsg(mUid, fUid);
    }

    /**
     * 删除消息根据服务端id
     *
     * @param @param  mUid
     * @param @param  fUid
     * @param @param  serverId
     * @param @return
     * @return int
     */
    public int deleteRecordByServerId(Context context, String mUid, String fUid, String serverId) {
        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);
        return db.deleteMsg(mUid, fUid, serverId);
    }

    /**
     * 删除消息根据服务端id
     *
     * @param @param  mUid
     * @param @param  fUid
     * @param @param  serverId
     * @param @return
     * @return int
     */
    public int deleteRecordByLocalId(Context context, long localId) {
        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);
        return db.deleteMsgByLocalId(localId);
    }

    /**
     * 更改消息状态
     *
     * @param fUid
     * @time 2011-9-27 上午11:27:53
     * @author:linyg
     */
    public void modifyMessageBigId(Context context, String fUid, long bitId) {
        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);
        String mUid = String.valueOf(Common.getInstance().loginUser.getUid());
        db.onModifyBigId(mUid, fUid, bitId);
    }

    /**
     * 获取是否存在对方未读的消息（即记录的状态为：2 已发达）
     *
     * @param mUid
     * @param fUid
     */
    public boolean searchNoReaded(Context context, String mUid, String fUid) {
        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);
        return db.onSearchNoReaded(context, mUid, fUid);
    }

    /**
     * 获取最近联系人
     *
     * @param mUid
     * @return
     * @time 2011-7-6 下午05:42:40
     * @author:linyg
     */
    public ArrayList<NearContact> getNearContact(Context context, String mUid, int start,
                                                 int amount) {
        ArrayList<NearContact> contacts = new ArrayList<NearContact>();
        NearContactWorker db = DatabaseFactory.getNearContactWorker(context);

        // 先查找收到搭讪和发出搭讪
        int receiveAccost = db.getReceiveAccoustSnender(mUid);
        if (receiveAccost >= 0) {
            NearContact lastestRA = getLatesRecieveAccost(context, mUid, start, amount);
            NearContact n = new NearContact();
            n.setSubGroup(SubGroupType.ReceiveAccost);
            n.setNumber(receiveAccost);
            // long time = getLatestReceiveAccostTime( context );
            // User fUser = new User( );
            // if ( time != -1 )
            // {
            // fUser.setLastSayTime( time );
            // n.setUser( fUser );
            // }
            n.setContent(lastestRA.getContent());
            n.setType(lastestRA.getType());
            n.setUser(lastestRA.getUser());
            contacts.add(n);
        }
        int sendAccost = db.getSendAccoustCount(mUid);
        if (sendAccost >= 0) {
            ArrayList<NearContact> SAs = getSendAccost(context, mUid, start, amount);
            NearContact latestSA = SAs.get(0);
            NearContact n = new NearContact();
            n.setSubGroup(SubGroupType.SendAccost);
            n.setNumber(sendAccost);
            // long time = db.getLatestTimeInSendAccost( mUid );
            // User fUser = new User( );
            // if ( time != -1 )
            // {
            // fUser.setLastSayTime( time );
            // n.setUser( fUser );
            // }
            n.setContent(latestSA.getContent());
            n.setType(latestSA.getType());
            n.setUser(latestSA.getUser());
            contacts.add(n);
        }

        // 再查找私聊类型
        Cursor cursor = db.selectPage(mUid, start, amount);
        try {
            cursor.moveToFirst();
            while (!(cursor.isAfterLast())) {
                NearContact n = new NearContact();
                try {
                    int subgroup = cursor
                            .getInt(cursor.getColumnIndex(NearContactWorker.SUBGROUP));
                    if (subgroup == SubGroupType.NormalChat) {
                        // 对普通私聊类型操作
                        n.setSubGroup(subgroup);
                        String content = cursor
                                .getString(cursor.getColumnIndex(NearContactWorker.LASTCONTENT));
                        User fUser = new User();
                        String noteName = cursor
                                .getString(cursor.getColumnIndex(NearContactWorker.FNOTE));
                        fUser.setNoteName(noteName);
                        String userInfo = cursor
                                .getString(cursor.getColumnIndex(NearContactWorker.FUSERINFO));
                        if (!CommonFunction.isEmptyOrNullStr(userInfo)) {
                            JSONObject jsonObj = new JSONObject(userInfo);
                            try {
                                fUser.setUid(jsonObj.optLong("fuid"));
                                fUser.setNickname(
                                        CommonFunction.jsonOptString(jsonObj, "fnickname"));
                                fUser.setIcon(CommonFunction.jsonOptString(jsonObj, "ficon"));
                                fUser.setViplevel(jsonObj.optInt("fvip"));
                                fUser.setSVip(jsonObj.optInt("svip"));
                                fUser.setSex(jsonObj.optInt("fgender"));
                                fUser.setLat(jsonObj.optInt("flat"));
                                fUser.setLng(jsonObj.optInt("flng"));
                                fUser.setAge(jsonObj.optInt("fage"));
                                int anchor = jsonObj.optInt("fusertype");
                                fUser.setUserType(anchor);
                                if (anchor > 0)
                                    anchorMap.put(jsonObj.optLong("fuid"), anchor);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            fUser.setUid((cursor
                                    .getLong(cursor.getColumnIndex(NearContactWorker.FUID))));
                            fUser.setNickname(cursor.getString(
                                    cursor.getColumnIndex(NearContactWorker.FNICKNAME)));
                            fUser.setIcon(cursor
                                    .getString(cursor.getColumnIndex(NearContactWorker.FICON)));
                        }

                        n.setUser(fUser);

                        n.setFriendMsg(false);
                        ChatRecord record = parsePersonalRecord(content, fUser.getUid());
                        if (Common.getInstance().loginUser.getUid() != record.getUid()) { // 最后一条聊天记录为好友发的
                            n.setFriendMsg(true);
                            fUser.setUid(record.getUid());
                            fUser.setNickname(record.getNickname());
                            // fUser.setNoteName(record.getNoteName(true));
                            fUser.setIcon(record.getIcon());
                            fUser.setViplevel(record.getVip());
                            fUser.setSVip(record.getSVip());
                            fUser.setAge(record.getfAge());
                            fUser.setLat(record.getfLat());
                            fUser.setLng(record.getfLng());
                            fUser.setSex(record.getfSex());

                            // 保证能够即时更新
                            String tmpIcon = cursor
                                    .getString(cursor.getColumnIndex(NearContactWorker.FICON));
                            if (!CommonFunction.isEmptyOrNullStr(tmpIcon)) {
                                fUser.setIcon(tmpIcon);
                            }
                            String tmpNickname = cursor
                                    .getString(cursor.getColumnIndex(NearContactWorker.FNICKNAME));
                            if (!CommonFunction.isEmptyOrNullStr(tmpNickname)) {
                                fUser.setNickname(tmpNickname);
                            }
                        }
                        String status = cursor
                                .getString(cursor.getColumnIndex(NearContactWorker.CHAT_STATUS));

                        // 5.0版本中"tb_near_contact"表，在4.2的基础上加入chat_status字段
                        // 导致在4.2更新到5.0该字段为""，状态2为送达
                        // 会存在一种情况，原本状态为已读，但是现实为送达。消息列表和消息也的状态会不符合。
                        if (TextUtils.isEmpty(status)) {
                            n.setChatStatus(ChatRecordStatus.ARRIVED);
                        } else {
                            n.setChatStatus(Integer.valueOf(status));

                        }
                        n.setType(Integer.valueOf(record.getType()));
                        fUser.setLastSayTime(record.getDatetime());
                        n.setContent(record.getContent());
                        n.setfUid(
                                cursor.getInt(cursor.getColumnIndex(NearContactWorker.FUID)));
                        n.setQuietSeen(
                                cursor.getInt(cursor.getColumnIndex(NearContactWorker.QUIETSEEN)));

                        // 当悄悄进入私聊然后删掉新消息时返回消息列表要显示减掉删除后的数量
                        // 所以悄悄查看之后的未读数量要在私聊消息表中查找
                        if (n.getQuietSeen() == 1) {
                            int count = getUnReadFromMsgWorker(context, mUid,
                                    String.valueOf(n.getfUid()));
                            //从私聊消息表里面统计数量后存入私聊消息列表表
                            setNearContactUnread(context, Long.valueOf(mUid), n.getfUid(),
                                    count);
                            n.setNumber(count);
                        } else {
                            n.setNumber(cursor
                                    .getInt(cursor.getColumnIndex(NearContactWorker.NONEREAD)));
                        }

                        fUser.setRelationship(record.getRelationship());

                        PersonalMessageWorker pmw = DatabaseFactory.getChatMessageWorker(context);
                        Cursor c = pmw
                                .onSelectPage(context, mUid, String.valueOf(fUser.getUid()), 0, 1);
                        try {
                            c.moveToFirst();
                            int dis = c
                                    .getInt(c.getColumnIndex(PersonalMessageWorker.DISTANCE));
                            fUser.setDistance(dis);
                            n.setDistance(dis);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (c != null)
                                c.close();
                        }
                        contacts.add(n);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return contacts;
    }

    /**
     * 获取收到搭讪数量
     */
    public int getReceiveAccostCount(Context context, String mUid) {
        NearContactWorker db = DatabaseFactory.getNearContactWorker(context);
        return db.getReceiveAccoustCount(mUid);
    }

    /**
     * 获取发出搭讪列表
     */
    public ArrayList<NearContact> getSendAccost(Context context, String uid, int start,
                                                int amount) {
        ArrayList<NearContact> list = new ArrayList<NearContact>();
        NearContactWorker db = DatabaseFactory.getNearContactWorker(context);

        Cursor cursor = db.selectSendAccostPage(uid);
        try {
            cursor.moveToFirst();
            while (!(cursor.isAfterLast())) {
                NearContact n = new NearContact();
                try {
                    int subgroup = cursor
                            .getInt(cursor.getColumnIndex(NearContactWorker.SUBGROUP));
                    if (subgroup == 2) {// 只对发出搭讪类型操作
                        n.setSubGroup(subgroup);
                        String content = cursor
                                .getString(cursor.getColumnIndex(NearContactWorker.LASTCONTENT));
                        User fUser = new User();
                        String noteName = cursor
                                .getString(cursor.getColumnIndex(NearContactWorker.FNOTE));
                        fUser.setNoteName(noteName);
                        String userInfo = cursor
                                .getString(cursor.getColumnIndex(NearContactWorker.FUSERINFO));
                        if (!CommonFunction.isEmptyOrNullStr(userInfo)) {
                            JSONObject jsonObj = new JSONObject(userInfo);
                            try {
                                fUser.setUid(jsonObj.optLong("fuid"));
                                fUser.setNickname(
                                        CommonFunction.jsonOptString(jsonObj, "fnickname"));
                                fUser.setIcon(CommonFunction.jsonOptString(jsonObj, "ficon"));
                                fUser.setViplevel(jsonObj.optInt("fvip"));
                                fUser.setSVip(jsonObj.optInt("svip"));
                                fUser.setSex(jsonObj.optInt("fgender"));
                                fUser.setLat(jsonObj.optInt("flat"));
                                fUser.setLng(jsonObj.optInt("flng"));
                                fUser.setAge(jsonObj.optInt("fage"));
                                fUser.setNoteName(
                                        CommonFunction.jsonOptString(jsonObj, "fnote"));
                                int anchor = jsonObj.optInt("fusertype");
                                fUser.setUserType(anchor);
                                if (anchor > 0)
                                    anchorMap.put(jsonObj.optLong("fuid"), anchor);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            fUser.setUid((cursor
                                    .getLong(cursor.getColumnIndex(NearContactWorker.FUID))));
                            fUser.setNickname(cursor.getString(
                                    cursor.getColumnIndex(NearContactWorker.FNICKNAME)));
                            fUser.setIcon(cursor
                                    .getString(cursor.getColumnIndex(NearContactWorker.FICON)));
                        }
                        n.setUser(fUser);

                        n.setFriendMsg(false);
                        ChatRecord record = parsePersonalRecord(content, fUser.getUid());
                        String status = cursor
                                .getString(cursor.getColumnIndex(NearContactWorker.CHAT_STATUS));

                        // 5.0版本中"tb_near_contact"表，在4.2的基础上加入chat_status字段
                        // 导致在4.2更新到5.0该字段为""，状态2为送达
                        // 会存在一种情况，原本状态为已读，但是现实为送达。消息列表和消息也的状态会不符合。
                        if (TextUtils.isEmpty(status)) {
                            n.setChatStatus(ChatRecordStatus.ARRIVED);
                        } else {
                            n.setChatStatus(Integer.valueOf(status));

                        }
                        n.setType(Integer.valueOf(record.getType()));
                        fUser.setLastSayTime(record.getDatetime());
                        n.setContent(record.getContent());
                        n.setNumber(
                                cursor.getInt(cursor.getColumnIndex(NearContactWorker.NONEREAD)));
                        n.setfUid(
                                cursor.getInt(cursor.getColumnIndex(NearContactWorker.FUID)));
                        fUser.setRelationship(record.getRelationship());
                        fUser.setDistance(record.getDistance());

                        list.add(n);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return list;
    }

    public NearContact getLatesRecieveAccost(Context context, String mUid, int start, int amount) {
        NearContactWorker db = DatabaseFactory.getNearContactWorker(context);
        Cursor cursor = db.selectRecieveAccostPage(mUid);
        NearContact nc = new NearContact();
        cursor.moveToFirst();
        String content = cursor.getString(cursor.getColumnIndex(NearContactWorker.LASTCONTENT));
        long friendUserId = cursor.getLong(cursor.getColumnIndex(NearContactWorker.FUID));
        ChatRecord record = parsePersonalRecord(content, friendUserId);
        if (record != null) {
            nc.setContent(record.getContent());
            nc.setType(Integer.parseInt(record.getType()));

            User fUser = new User();
            String noteName = cursor.getString(cursor.getColumnIndex(NearContactWorker.FNOTE));
            fUser.setNoteName(noteName);
            String userInfo = cursor
                    .getString(cursor.getColumnIndex(NearContactWorker.FUSERINFO));
            if (!CommonFunction.isEmptyOrNullStr(userInfo)) {
                try {
                    JSONObject jsonObj = new JSONObject(userInfo);
                    fUser.setUid(jsonObj.optLong("fuid"));
                    fUser.setNickname(CommonFunction.jsonOptString(jsonObj, "fnickname"));
                    fUser.setIcon(CommonFunction.jsonOptString(jsonObj, "ficon"));
                    fUser.setViplevel(jsonObj.optInt("fvip"));
                    fUser.setSex(jsonObj.optInt("fgender"));
                    fUser.setLat(jsonObj.optInt("flat"));
                    fUser.setLng(jsonObj.optInt("flng"));
                    fUser.setAge(jsonObj.optInt("fage"));
                    fUser.setDistance(record.getDistance());
                    int anchor = jsonObj.optInt("fusertype");
                    fUser.setUserType(anchor);
                    if (anchor > 0)
                        anchorMap.put(jsonObj.optLong("fuid"), anchor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                fUser.setUid(
                        (cursor.getLong(cursor.getColumnIndex(NearContactWorker.FUID))));
                fUser.setNickname(
                        cursor.getString(cursor.getColumnIndex(NearContactWorker.FNICKNAME)));
                fUser.setIcon(
                        cursor.getString(cursor.getColumnIndex(NearContactWorker.FICON)));
            }
            fUser.setLastSayTime(
                    cursor.getLong(cursor.getColumnIndex(NearContactWorker.LASTDATETIME)));
            nc.setDistance(record.getDistance());
            nc.setUser(fUser);
        }
        if (cursor != null) {
            cursor.close();
        }
        return nc;
    }

    /**
     * 解析一条私聊
     *
     * @param result
     * @return
     * @time 2011-10-28 下午03:07:53
     * @author:linyg
     */
    public NearContact paramOneContact(String result) {
        NearContact n = null;
        if (result != null) {
            n = new NearContact();
            try {
                JSONObject json = new JSONObject(result);
                n.setType(json.optInt("type"));
                n.setContent(CommonFunction.jsonOptString(json, "content"));
                JSONObject userjson = json.optJSONObject("user");
                if (userjson != null) {
                    User user = parseUser(userjson, 1);
                    n.setUser(user);
                    n.setfUid(user.getUid());
                }
            } catch (JSONException e) {
                n = null;
            }
        }
        return n;
    }

    /**
     * 是否第一次私聊 通过是否存在私聊记录判断
     *
     * @return
     */
    public boolean isFirstChat(Context context, long fUid) {
        // 按照分页方式从数据库中读取私聊的聊天记录
        ArrayList<ChatRecord> list = ChatPersonalModel.getInstance()
                .selectPageRecord(context, String.valueOf(Common.getInstance().loginUser.getUid()),
                        String.valueOf(fUid), 0, 5);

        return list == null || list.size() == 0;

    }

    /**
     * 品尝约会道具之后，更新本地数据库
     *
     * @param context
     * @param serverid 服务端消息ID
     * @param status   1品尝，0未品尝
     */
    public void updateGiftStatus(Context context, String serverid, int status) {
        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);
        db.updateReadGift(context, serverid, status);
    }

    /**
     * 获取数据库中我与朋友的聊天中，朋友的最大的消息id
     *
     * @param context
     * @param mUId
     * @param fUId
     * @return 如果没有找到相应Cursor，返回-1
     */
    public long getFriendMaxId(Context context, String mUId, String fUId) {
        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);
        Cursor cursor = db.getFriendMaxServerId(mUId, fUId);
        cursor.moveToLast();

        try {

            long result = cursor.getLong(1);
            return result;
        } catch (Exception e) {
            CommonFunction.log("can not find the result");
            return -1;
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public void getFailData2Map(Context context) throws JSONException {
        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);
        String failStatus = String.valueOf(ChatRecordStatus.FAIL);
        Cursor cursor = db.onSelectByStatus(failStatus);

        if (cursor != null) {
            if (!cursor.moveToFirst())
                return;
            do {
                int localIDIndex = cursor.getColumnIndex(PersonalMessageWorker.ID);
                int contentIndex = cursor.getColumnIndex(PersonalMessageWorker.CONTENT);
                long localId = cursor.getLong(localIDIndex);
                String content = cursor.getString(contentIndex);
                JSONObject json = new JSONObject(content);
                long flag = json.optLong("datetime");
                saveRecordLocalId(flag, localId);
            }
            while (cursor.moveToNext());
        }
    }

    /**
     * 获取数据库中我与朋友的聊天中,找出小于等于serverId的最大消息id。
     *
     * @param context
     * @param mUId
     * @param fUId
     * @return
     */
    public long getLocalMaxId(Context context, String mUId, String fUId, String serverId) {
        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);
        Cursor cursor = db.getMaxServerId(mUId, fUId, serverId);

        if (!cursor.moveToLast()) {
            if (cursor != null) {
                cursor.close();
            }
            return -1;
        }
        try {
            long result = cursor.getLong(0);
            return result;
        } catch (Exception e) {
            CommonFunction.log("can not find the result");
            return -1;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    /**
     * 获取数据库中我与朋友的聊天中，最后一条消息的内容
     *
     * @param context
     * @param mUId
     * @param fUId
     * @return
     */
    public String getLastContent(Context context, String mUId, String fUId) {
        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);
        // 获取最后一条消息
        Cursor cursor = db.onSelectChatRecord(context, mUId, fUId, 0, 1);

        if (cursor != null && cursor.moveToLast()) {
            String result = cursor.getString(4);
            cursor.close();
            return result;
        } else {
            cursor.close();
            return null;
        }
    }

    /**
     * 获取数据库中我与朋友的聊天中，最后一条消息的内容
     *
     * @param context
     * @param mUId
     * @param fUId
     * @return
     */
    public int getLastMessageStatus(Context context, String mUId, String fUId) {
        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);
        // 获取最后一条消息
        Cursor cursor = db.onSelectChatRecord(context, mUId, fUId, 0, 1);

        int result = 1;
        if (cursor != null && cursor.moveToLast()) {
            result = cursor.getInt(cursor.getColumnIndex(PersonalMessageWorker.STATUS));
            cursor.close();
        }


        return result;
    }

    public int getLastMessageSendType(Context context, String mUId, String fUId) {
        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);
        // 获取最后一条消息
        Cursor cursor = db.onSelectChatRecord(context, mUId, fUId, 0, 1);

        int result = 1;
        try {
            if (cursor != null && cursor.moveToLast()) {
                result = cursor.getInt(cursor.getColumnIndex(PersonalMessageWorker.SENDTYPE));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return result;

    }

    /**
     * localId对应的消息，并修改消息的状态
     *
     * @param mContext
     * @param e
     * @param flag
     */
    public void handleMessageInDataBase(Context mContext, int e, long flag) {
        int status = 0;
        if (e == 0) {
            // 表示失败
            status = ChatRecordStatus.FAIL;
        } else if (e == 1) {
            // 表示成功
            CommonFunction.log("kevin", "handleMessageInDataBase:::Arrived");
            status = ChatRecordStatus.ARRIVED;
        }
        // 先获取数据库中对应的消息的本地id
        long localID = getRecordLocalId(flag);
        if (localID > 0) {
            try {
                modifyRecordStatus(mContext, String.valueOf(localID), String.valueOf(status));
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } else {
            CommonFunction.log("kevin", "handleMessageInDataBase----localId==" + flag);
        }

    }

    public ChatRecord getRecordByLocalId(Context context, String localId) {
        ChatRecord record = null;
        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);
        Cursor cursor = db.onSelectRecord(localId);
        if (cursor != null && cursor.moveToFirst()) {
            ArrayList<ChatRecord> list = parseCursor(context, cursor);
            if (!list.isEmpty()) {
                record = list.get(0);
            }
        }
        if (null != cursor) {
            cursor.close();
        }

        return record;
    }

    public void updateNearChatStatus(Context context, String mUid, String fUid, long dateTime) {
        NearContactWorker db = DatabaseFactory.getNearContactWorker(context);
        db.updateRecordStatus(mUid, fUid, dateTime);
    }

    public void clearNoneReadCount(Context context, String mUid, String fUid) {
        NearContactWorker db = DatabaseFactory.getNearContactWorker(context);
        db.updateStatus(mUid, fUid);
    }

    public void readAllPersonalMsg(Context context, long muid, long fuid) {
        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);
        db.readAllMsg(muid, fuid);
    }

    public long getChatTargetId() {
        return chatTargetId;
    }

    public void setChatTargetId(long chatTargetId) {
        this.chatTargetId = chatTargetId;
    }

    /**
     * 判断数据库中时候有搭讪游戏的题目
     *
     * @return
     */
    public boolean isHasGameQue(Context context, long mUid, long fUid) {
        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);
        return db.getAccostQuestion(String.valueOf(mUid), String.valueOf(fUid)) > 0;
    }

    /**
     * 获取搭讪关系, 是否交流（0-否，1-有）
     *
     * @param context
     * @param mUid
     * @param fUid
     * @return -1表示数据没有记录在缓存
     */
    public int getAccostRelation(Context context, long mUid, long fUid) {
        // 当对方为系统用户时，必定为已交流
        if (fUid <= 1000)
            return 1;

        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(context);
        String content = sp.getString(SharedPreferenceUtil.ACCOST_RELATION + mUid);
        AccostRelationes bean = GsonUtil.getInstance()
                .getServerBean(content, AccostRelationes.class);

        if (bean != null && !bean.relationMap.isEmpty()) {
            AccostRelationes.Relation relation = bean.relationMap.get(fUid);
            if (relation != null) {
                return relation.ischat;
            }
        }

        return -1;
    }

    /**
     * 记录登录者与该用户的聊天关系
     *
     * @param context
     * @param userid  对方的用户id
     * @param ischat  是否聊过天，0为否，1为是
     */
    public void putAccostRelation(Context context, long userid, int ischat) {
        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(context);
        long mUid = Common.getInstance().loginUser.getUid();
        String key = SharedPreferenceUtil.ACCESS_TOKEN + mUid;
        String content = sp.getString(key);
        AccostRelationes relationBean = null;
        if (TextUtils.isEmpty(content)) {
            relationBean = new AccostRelationes();
        } else {
            relationBean = GsonUtil.getInstance().getServerBean(content, AccostRelationes.class);
        }
        if (relationBean.relationMap == null) {
            relationBean.relationMap = new HashMap<Long, AccostRelationes.Relation>();
        }
        AccostRelationes.Relation bean = relationBean.initRelation(userid, ischat);
        relationBean.relationMap.put(bean.userid, bean);
        content = GsonUtil.getInstance().getStringFromJsonObject(relationBean);
        sp.putString(key, content);
    }

    /**
     * 从tb_personal_message和tb_near_contact删除收到搭讪或发出搭讪
     *
     * @author tanzy
     */
    public void deleteAccostFromTwoTable(Context context, int subGroup) {
        NearContactWorker ndb = DatabaseFactory.getNearContactWorker(context);
        PersonalMessageWorker cdb = DatabaseFactory.getChatMessageWorker(context);

        // 根据最近联系获得的对方id来删除私聊表里面消息
        Cursor cursor = null;
        if (subGroup == 2)
            cursor = ndb.selectSendAccostPage(Common.getInstance().loginUser.getUid() + "");
        else if (subGroup == 3)
            cursor = ndb.selectRecieveAccostPage(Common.getInstance().loginUser.getUid() + "");
        if (cursor == null)
            return;
        try {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                String fuid = cursor.getString(cursor.getColumnIndex(NearContactWorker.FUID));
                cdb.deleteMsg(Common.getInstance().loginUser.getUid() + "", fuid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }

        ndb.deleteSubgroupRecord(Common.getInstance().loginUser.getUid() + "", subGroup);
    }

    /**
     * 根据收到搭讪发送人员判断是否需要添加未读数（用于filterutil）
     *
     * @author tanzy
     */
    public boolean needAddNoRead(Context context, String fuid) {
        newManAccost = true;
        NearContactWorker db = DatabaseFactory.getNearContactWorker(context);
        Cursor cursor = db
                .selectRecieveAccostPage(Common.getInstance().loginUser.getUid() + "");
        try {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                String tfuid = cursor.getString(cursor.getColumnIndex(NearContactWorker.FUID));
                if (fuid.equals(tfuid)) {
                    int count = cursor
                            .getInt(cursor.getColumnIndex(NearContactWorker.NONEREAD));
                    if (count > 0) {
                        newManAccost = false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return newManAccost;
    }

    /**
     * 修改对方所在分组类型
     *
     * @param fUid         对方的id
     * @param subgroupType 分组类型
     */
    public void changeContactSubgroup(Context context, long fUid, int subgroupType) {
        long mUid = Common.getInstance().loginUser.getUid();

        NearContactWorker ncWorker = DatabaseFactory.getNearContactWorker(context);
        ncWorker.onModifySubgroup(mUid, fUid, subgroupType);
    }

    // 更新消息列表的显示消息,因为删除某条消息后,需要更新最近联系人的显示消息
    public void updateLastMessage(Context context, User fUser) {
        String mUID = String.valueOf(Common.getInstance().loginUser.getUid());
        String fUID = String.valueOf(fUser.getUid());

        NearContactWorker db = DatabaseFactory.getNearContactWorker(context);
        // 更新最近联系人，把最后一条内容设置回消息列表页。
        String content = ChatPersonalModel.getInstance().getLastContent(context, mUID, fUID);

        try {
            if (content != null) {
                // 私聊消息表存在存在最后一条消息
                int sendType = ChatPersonalModel.getInstance()
                        .getLastMessageSendType(context, mUID, fUID);
                int status = ChatPersonalModel.getInstance()
                        .getLastMessageStatus(context, mUID, fUID);
                JSONObject json = new JSONObject(content);
                String dataTime = String.valueOf(json.getLong("datetime"));
                // 删除一条消息，不改变分组，把subgroup设置为0
                int noRead = 0;
                if (!isQuietMode) {// 如果不是悄悄模式，把对应的消息未读数量更新为0
                    noRead = 0;
                } else {
                    noRead = getUnReadFromMsgWorker(context, mUID, fUID);
                }
                db.delRecordUpdateContact(mUID, fUser, content, status, dataTime, sendType,
                        noRead);
            } else {
                // 不存在最后一条消息，把该对话从NearContact数据库中删除
                db.deleteRecord(mUID, fUID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否需要过滤掉一下类型的消息,true表示要过滤掉,false表示不要过滤掉
     */
    private boolean bIsFilterOut(String typeStr) {

        int type = 0;
        try {
            type = Integer.valueOf(typeStr);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        switch (type) {
            case ChatRecordViewFactory.THEME_GIFT:
            case ChatRecordViewFactory.ACCOST_GAME_ANS:
            case ChatRecordViewFactory.ACCOST_GAME_ANS_IMAGE:
            case ChatRecordViewFactory.ACCOST_GAME_ANS_TEXT:
            case ChatRecordViewFactory.ACCOST_GAME_QUE:
                return true;
            default:
                return false;
        }
    }

    public void resendSendingMessage(final Context context) {
        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);
        long userid = Common.getInstance().loginUser.getUid();
        Cursor cursor = db.onSelectSendingRecord(context, userid);

        ArrayList<ChatRecord> list = parseCursor(context, cursor);
        LongSparseArray<ChatMessageBean> array = MessagesSendManager.getManager(context)
                .getMessageBeanSparseArray();

        for (int i = 0; i < list.size(); i++) {
            ChatRecord record = list.get(i);

            PrivateChatInfo chatInfo = new PrivateChatInfo();
            chatInfo.targetUserId = record.getFuid();
            chatInfo.from = record.getFrom();
            int accostRelation = ChatPersonalModel.getInstance()
                    .getAccostRelation(context, userid, chatInfo.targetUserId);
            if (accostRelation == 1) {
                chatInfo.mtype = 0;
            } else {
                chatInfo.mtype = 1;
            }

            ChatMessageBean messageBean = new ChatMessageBean();
            messageBean.chatRecord = record;
            messageBean.chatType = ChatMessageBean.PRIVATE_CHAT;
            messageBean.targetInfo = chatInfo;

            if (record.getType() == String.valueOf(ChatRecordViewFactory.IMAGE)) {
                messageBean.resourceType = FileUploadType.PIC_PRIVATE_CHAT;
            } else if (record.getType() == String.valueOf(ChatRecordViewFactory.VIDEO)) {
                messageBean.resourceType = FileUploadType.VIDEO_PRIVATE_CHAT;
            }
            array.append(messageBean.chatRecord.getId(), messageBean);
        }

        cursor.close();
    }

    /**
     * 在私聊消息表中获取未读数
     */
    public int getUnReadFromMsgWorker(Context context, String muid, String fuid) {
        int count = 0;
        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);
        Cursor cursor = db.getNewMessages(muid, fuid);
        if (cursor != null && cursor.getCount() > 0)
            count = cursor.getCount();
        if (cursor != null)
            cursor.close();
        return count;
    }

    /**
     * 设置是否已经悄悄看过
     */
    public void setNearContactQuietSee(Context context, long muid, long fuid, int quietSee) {
        NearContactWorker db = DatabaseFactory.getNearContactWorker(context);
        db.updateQuietSeen(muid, fuid, quietSee);
    }

    /**
     * 修改私聊消息列表表的未读数
     */
    public void setNearContactUnread(Context context, long muid, long fuid, int unread) {
        NearContactWorker db = DatabaseFactory.getNearContactWorker(context);
        db.updateUnread(muid, fuid, unread);
    }

    /**
     * @Description: 获取与对方的私聊中图片聊天记录的小图url列表
     * @author tanzy
     * @date 2015-8-12
     */
    public ArrayList<String> getPrivateChatThumPicUrlList(Context context, long muid, long fuid) {
        ArrayList<String> pics = new ArrayList<String>();
        PersonalMessageWorker db = DatabaseFactory.getChatMessageWorker(context);
        Cursor cursor = db.getPicMsg(muid, fuid);
        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                String content = cursor
                        .getString(cursor.getColumnIndex(PersonalMessageWorker.CONTENT));
                ChatRecord record = parsePersonalRecord(content, fuid);
                if (String.valueOf(ChatRecordViewFactory.IMAGE).equals(record.getType()) &&
                        !CommonFunction.isEmptyOrNullStr(record.getAttachment())) {
                    String url = record.getAttachment();
                    pics.add(CommonFunction.thumPicture(url));
                }
            }
        }
        if (cursor != null)
            cursor.close();
        return pics;
    }
}
