
package net.iaround.ui.datamodel;


import android.content.Context;
import android.database.Cursor;

import com.alibaba.fastjson.JSON;

import net.iaround.conf.Common;
import net.iaround.conf.MessageID;
import net.iaround.database.DatabaseFactory;
import net.iaround.database.GroupContactWorker;
import net.iaround.database.GroupMessageWorker;
import net.iaround.entity.GroupItem;
import net.iaround.entity.GroupManagerListBean;
import net.iaround.entity.GroupPushMemberList;
import net.iaround.entity.GroupPushMemberList.GroupPushMemberGroup;
import net.iaround.entity.type.GroupMsgReceiveType;
import net.iaround.model.im.ChatRecord;
import net.iaround.model.im.ChatRecordStatus;
import net.iaround.model.im.GroupChatMessage;
import net.iaround.model.im.TransportMessage;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.PathUtil;
import net.iaround.ui.chat.view.ChatRecordViewFactory;
import net.iaround.ui.group.bean.Group;
import net.iaround.ui.group.bean.GroupContact;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * 圈子管理数据管理操作,例如管理员的升降,圈子用户管理的等<br>
 * <b>另外:<b/><br>
 * --圈子消息管理见 @GroupChatListModel <br>
 * --圈话题管理 @TopicModel
 */
public class GroupModel extends Model {
    private final String TAG = GroupModel.class.getName() + "_";
    private static GroupModel groupModel;
    // 圈子ID
    private String groupId;
    // 圈子创建者的id
    private String groupOwnerId = "0";
    // 本人是否已被踢出群
    private boolean isKicked;
    // 本群是否已解散
    private boolean isDismissGroup;
    // 圈子管理员ID列表
    private ArrayList<String> managerIdList;

    // 是否为管理员
    private boolean isAdmin;

    //是否是粉丝
    private boolean isFans;

    /**是否是游客*/
    private boolean isTourists;

    // 是否依然在圈聊中,圈话题属于圈聊中,就是判断是否在GroupChatTopicActivity
    private boolean mIsInGroupChat;

    private GroupPushMemberList pushMemberBean;// 存放新成员推送
    private ArrayList<GroupItem> myGroupList; // 我的圈子列表

    /**
     * 我的圈子的缓存数据
     */
    private ArrayList<Group> mMyGroupList;

    /**
     * 附近圈子缓存数据
     */
    private ArrayList<Group> mGroupNearList;

    /**
     * 附近圈子缓存的当前页码
     */
    private int mCurGroupNearPage;
    /**
     * 附近圈子的总页数
     */
    private int mTotalGroupNearPage;

    private Map<String, Boolean> myGroupTempHasNew; // 我的圈子未读消息标识，当为获取我的圈子返回时，临时使用

    /**
     * 是否需要刷新我的圈子列表
     */
    public boolean isNeedRefreshGroupList = false;

    private GroupModel() {
        managerIdList = new ArrayList<String>();
        isKicked = false;
        isDismissGroup = false;
        groupId = null;
        isAdmin = false;

        myGroupList = new ArrayList<GroupItem>();
        myGroupTempHasNew = new HashMap<String, Boolean>();

        mMyGroupList = new ArrayList<Group>();

        mGroupNearList = new ArrayList<Group>();

        getGroupListBufferFromFile();
    }

    public static GroupModel getInstance() {
        if (groupModel == null) {
            groupModel = new GroupModel();
        }
        return groupModel;
    }

    /**
     * 释放资源
     */
    public void reset() {
        managerIdList.clear();
        managerIdList = null;
        groupModel = null;
        if (myGroupList != null)
            myGroupList.clear();
        if (mMyGroupList != null)
            mMyGroupList.clear();
        if (mGroupNearList != null)
            mGroupNearList.clear();
    }

    public void releaseBuffer() {
        if (managerIdList != null) {
            managerIdList.clear();
        }
        isKicked = false;
        isDismissGroup = false;
        groupId = null;
        isAdmin = false;
    }

    /**
     * 获取圈子创建者的id
     *
     * @return
     */
    public String getGroupOwnerId() {
        return groupOwnerId;
    }

    /**
     * 设置吧主ID
     * @param groupOwnerId
     */
    public void setGroupOwnerId(String groupOwnerId) {
        this.groupOwnerId = groupOwnerId;
    }

    /**
     * 是否为管理员
     *
     * @return true：为管理员
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    /**
     * 设置当前用户，是否为本圈子的管理员
     *
     * @param isAdmin true：为管理员
     */
    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean isKicked() {
        return isKicked;
    }

    public void setKicked(boolean isKicked) {
        this.isKicked = isKicked;
    }

    // 本群是否已解散
    public boolean isDismissGroup() {
        return isDismissGroup;
    }

    public void setDismissGroup(boolean isDismissGroup) {
        this.isDismissGroup = isDismissGroup;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * 是否依然在圈聊中,圈话题属于圈聊中,就是判断是否在GroupChatTopicActivity <br/>
     *
     * @return true 在
     */
    public boolean isInGroupChat() {
        return mIsInGroupChat;
    }

    /**
     * 设置是否依然在圈聊中 <br/>
     */
    public void setInGroupChat(boolean isInGroup) {
        this.mIsInGroupChat = isInGroup;
    }

    /**
     * 处理消息（用户进入群后，再进入到其他页面的情况下调用）
     *
     * @param message
     */
    public void handleMessage(TransportMessage message) {
        switch (message.getMethodId()) {
            case MessageID.GROUP_PUSH_MESSAGE: // 接收最新的群消息
                try {
                    String groupId = this.parseGroupId(message);
                    if (groupId.equals(this.groupId)) { // 判断属于本群的消息
                        GroupChatListModel.getInstance().parseGroupNewMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case MessageID.GROUP_PUSH_KICK: // 接收被踢消息(本人被群主踢)
                try {
                    String groupId = this.parseGroupId(message);
                    if (groupId.equals(this.groupId)) { // 判断属于本群的消息
                        this.isKicked = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case MessageID.GROUP_PUSH_DISSOLVE: // 解散群组
                try {
                    String groupId = this.parseGroupId(message);
                    removeGroupFromCache(groupId);
                    clearByGroupID(Integer.valueOf(groupId));

                    if (groupId.equals(this.groupId)) { // 判断属于本群的消息
                        this.isDismissGroup = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * 修正聊天记录的显示数目，并将多余的聊天记录从界面上移除 <br/>
     * 因为可能在加载群历史记录时，会接收到新的群聊天记录
     *
     * @param showList   用于显示的聊天记录列表
     * @param maxShowNum 聊天记录列表显示的最大记录数
     * @return 聊天记录列表显示的记录数
     */
    public int amendRecordShowList(ArrayList<ChatRecord> showList, int maxShowNum) {
        int showedNum = showList.size();

        if (showedNum > maxShowNum) {
            int num = showedNum - maxShowNum;

            while (0 != num) { // 清除列表头部多余的聊天记录
                showList.remove(0);

                num--;
            }

            showList.trimToSize();
            showedNum = showList.size();
        }

        return showedNum;
    }

    /**
     * 解析消息所属的群ID
     *
     * @param message
     * @return 群ID
     * @throws JSONException
     */
    public String parseGroupId(TransportMessage message) throws JSONException {
        String groupId = "";
        JSONObject jsonObj = new JSONObject(message.getContentBody());
        groupId = CommonFunction.jsonOptString(jsonObj, "groupid");

        return groupId;
    }

    /**
     * 更新GroupContactWorker的表
     *
     * @param context
     * @param groupId
     * @param groupName
     * @param groupIcon
     * @param content     最后的内容
     * @param time        更新最后插入的时间
     * @param noReadCount 只需要知道插入多少条未读的消息,会自动累加
     * @return
     */
    public long UpdateGroupContact(Context context, long groupId, String groupName,
                                   String groupIcon, String content, long time, int noReadCount, int status,
                                   boolean isBeAt) {
        GroupContactWorker db = DatabaseFactory.getGroupContactWorker(context);
        String uidStr = String.valueOf(Common.getInstance().loginUser.getUid());
        String groupIdStr = String.valueOf(groupId);
        String timeStr = String.valueOf(time);
        int atFlag = isBeAt ? 1 : 0;
        return db.onInsertRecord(uidStr, groupIdStr, groupName, groupIcon, content,
                timeStr, noReadCount, status, atFlag);
    }

    /**
     * 更新圈子联系人
     *
     * @author kevinSu
     */
    public long UpdateGroupContact(Context context, String muid, String groupId,
                                   String groupName, String groupIcon, String content, String time,
                                   int noReadNum, int status, boolean isBeAt) {
        GroupContactWorker db = DatabaseFactory.getGroupContactWorker(context);
        int atFlag = isBeAt ? 1 : 0;
        return db.onInsertRecord(muid, groupId, groupName, groupIcon, content, time,
                noReadNum, status, atFlag);
    }

    /**
     * 解析一条群组的消息
     *
     * @param strJson
     * @return
     * @throws JSONException
     * @time 2011-7-1 上午10:35:49
     * @author:linyg
     */
    public ChatRecord parseGroupRecord(String strJson) throws JSONException {
        ChatRecord record = null;

        if (strJson != null) {
            JSONObject json = new JSONObject(strJson);
            record = new ChatRecord();

            record.setId(json.optLong("msgid"));
            record.setType(CommonFunction.jsonOptString(json, "type"));
            JSONObject userJson = json.optJSONObject("user");
            if (userJson != null) {

                record.setUid(userJson.optLong("userid"));
                // record.setNickname( userJson.CommonFunction.jsonOptString(
                // "nickname" ) );
                // record.setNickname( userJson.CommonFunction.jsonOptString(
                // "notes" ) );
                record.setNickname(jsonOptString(userJson, "nickname"));
                record.setNoteName(jsonOptString(userJson, "notes"));

                // record.setIcon( userJson.CommonFunction.jsonOptString( "icon"
                // ) );
                record.setIcon(jsonOptString(userJson, "icon"));
                record.setVip(userJson.optInt("viplevel"));
                record.setfAge(userJson.optInt("age"));
                record.setfLat(userJson.optInt("lat"));
                record.setfLng(userJson.optInt("lng"));
                String gender = userJson.optString("gender", "all");
                if (CommonFunction.isEmptyOrNullStr(gender)) {
                    gender = "all";
                }
                record.setfSex(gender.trim().equals("all") ? 0 : (gender.trim().equals(
                        "m") ? 1 : 2)); // 1--男 // 2--女 // 0--其他
            }
            record.setDatetime(json.optLong("datetime"));
            record.setContent(CommonFunction.jsonOptString(json, "content"));
            record.setAttachment(CommonFunction.jsonOptString(json, "attachment"));
        }
        return record;
    }

    /**
     * 处理推送删除群组历史消息
     *
     * @param message
     * @return void
     */
    public void parseJsonDeleteGroupMessage(Context context, TransportMessage message) {
        try {
            JSONObject json = new JSONObject(message.getContentBody());
            JSONArray strArray = json.optJSONArray("msgids");
            if (strArray != null && strArray.length() > 0) {
                for (int i = 0; i < strArray.length(); i++) {
                    long msgId = 0;
                    try {
                        msgId = strArray.optLong(i);
                    } catch (Exception e) {
                    }
                    if (msgId > 0) {
                        GroupMessageWorker db = DatabaseFactory
                                .getGroupMessageWorker(context);
                        db.deleteGroupMessageByMessageId(msgId);
                        db.onClose();
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析群组的管理员列表
     *
     * @param bean
     * @throws JSONException
     */
    public void parseGroupManagerList(GroupManagerListBean bean) {
        if (bean == null) {
            return;
        }
        if (bean.isSuccess()) {
            groupOwnerId = bean.groupownerid;

            String userIds = bean.userids;
            if(bean.userids!=null){
                String[] idList = userIds.split(",");
                if (idList != null && idList.length > 0) {
                    managerIdList.clear();
                    for (String id : idList) {
                        managerIdList.add(id);
                    }
                }
            }
            // 检查用户本人是否为当前圈子的管理员
            String myUserId = String.valueOf(Common.getInstance().loginUser.getUid());
            if (isManager(myUserId)) { // 是管理员
                setAdmin(true);
            }
        }
    }

    /**
     * 根据用户ID判断用户是否为当前圈子的管理员
     *
     * @param userId
     */
    public boolean isManager(String userId) {
        boolean isManager = false;

        for (String id : managerIdList) {
            if (userId.equals(id)) { // 是管理员
                isManager = true;
                break;
            }
        }

        return isManager;
    }

    /**
     * 添加管理员ID
     *
     * @param userId
     */
    public void addToManagerIdList(String userId) {
        for (String id : managerIdList) {
            if (userId.equals(id)) { // 已存在，则不需添加
                return;
            }
        }
        managerIdList.add(userId);
    }

    /**
     * 删除管理员ID
     *
     * @param userId
     */
    public void delFromManagerIdList(String userId) {
        for (int i = 0; i < managerIdList.size(); i++) {
            String id = managerIdList.get(i);
            if (userId.equals(id)) { // 找到，则删除
                managerIdList.remove(i);
                break;
            }
        }
    }

    /**
     * 解析禁言用户的信息
     *
     * @param result
     * @throws JSONException
     */
    public HashMap<String, Object> parseForbidUserInfo(String result)
            throws JSONException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        JSONObject json = new JSONObject(result);
        User user = null;

        json = json.getJSONObject("userinfo");
        user = parseUser(json.getJSONObject("user"), 2);
        map.put("user", user);
        map.put("distance", CommonFunction.jsonOptString(json, "distance"));
        map.put("online", CommonFunction.jsonOptString(json, "online"));
        map.put("logintime", CommonFunction.jsonOptString(json, "logintime"));
        map.put("talktime", CommonFunction.jsonOptString(json, "talktime"));
        map.put("isforbid", CommonFunction.jsonOptString(json, "isforbid"));
        map.put("expiredtime", CommonFunction.jsonOptString(json, "expiredtime"));

        return map;
    }

    public String jsonOptString(JSONObject json, String key) {

        if (json.isNull(key)) {
            return "";
        }
        try {
            return json.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 把某个圈子的未读消息数量设置为0
     *
     * @param mContext
     */
    public void EraseGroupNoReadNum(Context mContext, long groupID) {
        GroupContactWorker db = DatabaseFactory.getGroupContactWorker(mContext);
        long uid = Common.getInstance().loginUser.getUid();
        String groupIdStr = String.valueOf(groupID);
        if (db.onSelect(String.valueOf(uid), groupIdStr) != 0) {
            db.updateStatus(uid, groupID);
        }
    }

    /**
     * 将某圈子最新消息存到groupContact中
     */
    public void getLatestMessageToContact(Context mContext, int groupID) {
        GroupContactWorker dbc = DatabaseFactory.getGroupContactWorker(mContext);
        GroupMessageWorker dbm = DatabaseFactory.getGroupMessageWorker(mContext);
        long uid = Common.getInstance().loginUser.getUid();
        Cursor cursor = dbm.onSelectPage(uid, groupID, 0, 1);
        String content = "";
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            content = cursor.getString(cursor.getColumnIndex(GroupMessageWorker.CONTENT));
        }
        cursor.close();
        if (CommonFunction.isEmptyOrNullStr(content))
            return;
        GroupChatMessage bean = JSON.parseObject(content,GroupChatMessage.class);
        dbc.updateContentAndTime(uid + "", groupID + "", content, bean.datetime + "");

    }

    public long updateGroupContact(Context mContext, String content, String groupid,
                                   String time, String status) {
        GroupContactWorker groupContactDB = DatabaseFactory.getGroupContactWorker(mContext);
        return groupContactDB.onUpdateRecordContent(content, groupid, time, status);
    }

    /**
     * 根据id删除圈子以及圈子里的所有消息
     */
    public void removeGroupAndAllMessage(Context mContext, String userid, String groupid) {
        GroupContactWorker gcWorkder = DatabaseFactory.getGroupContactWorker(mContext);
        gcWorkder.deleteRecord(userid, groupid);

        GroupMessageWorker gmWorker = DatabaseFactory.getGroupMessageWorker(mContext);
        gmWorker.deleteGroupMessage(Long.valueOf(userid), groupid);
    }

    /**
     * 删除特定消息接收类型的圈消息
     */
    public void removeGroupMsgByStatus(Context context, String uid, int status) {
        GroupContactWorker db = DatabaseFactory.getGroupContactWorker(context);
        Cursor cursor = db.selectPage(uid);

        try {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                String groupId = cursor.getString(cursor
                        .getColumnIndex(GroupContactWorker.GROUPID));
                if (GroupAffairModel.getInstance().getGroupMsgStatus(
                        Long.parseLong(groupId)) == status)
                    removeGroupAndAllMessage(context, uid, groupId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        cursor.close();

    }

    public ArrayList<GroupContact> getGroupContactList(Context context) {
        ArrayList<GroupContact> contacts = new ArrayList<GroupContact>();
        GroupContactWorker db = DatabaseFactory.getGroupContactWorker(context);
        Cursor cursor = db.selectPage(Common.getInstance().loginUser.getUid() + "");

        try {
            cursor.moveToFirst();
            while (!(cursor.isAfterLast())) {
                GroupContact contact = new GroupContact();
                try {
                    contact.groupIcon = cursor.getString(cursor
                            .getColumnIndex(GroupContactWorker.GROUPICON));
                    contact.groupID = cursor.getInt(cursor
                            .getColumnIndex(GroupContactWorker.GROUPID));
                    contact.groupName = cursor.getString(cursor
                            .getColumnIndex(GroupContactWorker.GROUPNAME));
                    contact.lastContent = cursor.getString(cursor
                            .getColumnIndex(GroupContactWorker.LASTCONTENT));
                    contact.noRead = cursor.getInt(cursor
                            .getColumnIndex(GroupContactWorker.NONEREAD));
                    contact.status = cursor.getInt(cursor
                            .getColumnIndex(GroupContactWorker.STATUS));
                    contact.time = cursor.getLong(cursor
                            .getColumnIndex(GroupContactWorker.TIME));
                    int atFlag = cursor.getInt(cursor
                            .getColumnIndex(GroupContactWorker.ATFLAG));
                    contact.isBeAt = atFlag == 1;
                    contacts.add(contact);
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
     * 获取接收且提醒的圈子的未读消息数
     */
    public int getReceiveUnreadCount(Context mContext) {
        int count = 0;

        GroupContactWorker db = DatabaseFactory.getGroupContactWorker(mContext);
        Cursor cursor = db.selectPage(Common.getInstance().loginUser.getUid() + "");
        try {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                String sgid = cursor.getString(cursor
                        .getColumnIndex(GroupContactWorker.GROUPID));
                int groupid = Integer.parseInt(sgid);
                if (GroupAffairModel.getInstance().getGroupMsgStatus(groupid) == GroupMsgReceiveType.RECEIVE_AND_NOTICE) {
                    count += cursor.getInt(cursor
                            .getColumnIndex(GroupContactWorker.NONEREAD));
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        cursor.close();

        return count;
    }

    public void clearByGroupID(int groupID) {

        if (pushMemberBean == null || pushMemberBean.groups == null) {
            return;
        }

        Iterator<GroupPushMemberGroup> it = pushMemberBean.groups.iterator();

        while (it.hasNext()) {
            GroupPushMemberGroup item = it.next();
            if (item.group.groupid == groupID) {
                it.remove();
                break;
            }
        }
    }

    public GroupPushMemberList getPushMemberBean() {
        return pushMemberBean;
    }

    /**
     * 获取所有的推送下来的人数
     */
    public int getPushMemberCount() {
        int count = 0;
        if (pushMemberBean != null) {
            Iterator<GroupPushMemberGroup> it = pushMemberBean.groups.iterator();

            while (it.hasNext()) {
                count += it.next().group.num;
            }
        }
        return count;
    }

    public void setNumByGroupID(int groupID, int num) {
        for (GroupPushMemberGroup item : pushMemberBean.groups) {
            if (item.group.groupid == groupID) {
                item.group.num = num;
            }
        }
    }

    public void setPushMemberBean(GroupPushMemberList serverBean) {
        if (pushMemberBean == null || pushMemberBean.groups.isEmpty()) {
            pushMemberBean = serverBean;
        } else {
            // 新推送的数据只有最新的，而不是全部的数据，所以不能直接替代。
            Iterator<GroupPushMemberGroup> serverIt = serverBean.groups.iterator();

            while (serverIt.hasNext()) {
                Iterator<GroupPushMemberGroup> localIt = pushMemberBean.groups.iterator();

                GroupPushMemberGroup serverItem = serverIt.next();
                while (localIt.hasNext()) {
                    GroupPushMemberGroup localItem = localIt.next();
                    // 如果本地有该圈子的信息，修改最新的数量
                    if (localItem.group.groupid == serverItem.group.groupid) {
                        localItem.group.num = serverItem.group.num;
                        break;
                    }

                    // 如果本地没有该圈子的信息，添加
                    if (!localIt.hasNext()) {
                        pushMemberBean.groups.add(serverItem);
                        pushMemberBean.groups.trimToSize();
                    }
                }
            }
        }
    }

    /**
     * 群组被解散，被迫退出,退出群组删除历史消息
     *
     * @param id 群id
     * @return void
     */
    public void dissolveGroup(Context context, String id) {
        if (myGroupList != null) {
            for (GroupItem group : myGroupList) {
                if (group.isIsjoin() && group.getId().equals(id)) {
                    myGroupList.remove(group);
                    String mUidStr = String
                            .valueOf(Common.getInstance().loginUser.getUid());
                    GroupModel.getInstance()
                            .removeGroupAndAllMessage(context, mUidStr, id);
                    break;
                }
            }
        }
        if (mMyGroupList != null) {
            for (Group group : mMyGroupList) {
                if (group.grouprole >= 0 && group.id.equals(id)) {
                    group.grouprole = -1;
                    String mUidStr = String
                            .valueOf(Common.getInstance().loginUser.getUid());
                    GroupModel.getInstance().removeGroupAndAllMessage(context, mUidStr,
                            group.id);
                    break;
                }
            }
        }
        saveGroupListBufferToFile();
    }

    /**
     * 更新我的圈子消息状态
     *
     * @param groupid
     * @param hasNew
     */
    public void updateMyGroupHasNew(String groupid, boolean hasNew) {
        if (myGroupList != null) {

            for (GroupItem group : myGroupList) {
                if (group.getId().equals(groupid)) {
                    group.setHasnew(hasNew);
                    break;
                }
            }
        }
        if (mMyGroupList != null) {

            for (Group group : mMyGroupList) {
                if (group.id.equals(groupid)) {
                    group.isHaveNewMsg = true;
                    break;
                }
            }
        }
        if (myGroupTempHasNew == null) {
            myGroupTempHasNew = new HashMap<String, Boolean>();
        }
        myGroupTempHasNew.put(groupid, hasNew);
    }

    /**
     * 接收推送的未读消息
     *
     * @param msg
     * @return void
     */
    public void pushNoReadMessage(Context context, TransportMessage msg) {
        // 5.6圈未读推送添加消息体的推送，将消息体去掉data字段后保存到tb_group_contact中
        GroupChatMessage bean = JSON.parseObject(msg.getContentBody(),GroupChatMessage.class);
        GroupModel.getInstance().updateMyGroupHasNew(bean.groupid + "", true);
        // 当没获取到我的圈子列表时，暂时保存
        if (myGroupList == null || myGroupList.size() == 0) {
            myGroupTempHasNew.put(bean.groupid + "", true);
        }


        int receiveGroupID = Integer.valueOf(String.valueOf(bean.groupid));
        int receiveStatus = GroupAffairModel.getInstance().getGroupMsgStatus(receiveGroupID);
        if (receiveStatus != GroupMsgReceiveType.NOT_RECEIVE) {
            long uid = Common.getInstance().loginUser.getUid();
            int count = 0;

            GroupContactWorker db = DatabaseFactory.getGroupContactWorker(context);
            Cursor cursor = db.selectUserGroup(uid, receiveGroupID);
            cursor.moveToFirst();
            if (cursor.getCount() > 0)
                count = cursor.getInt(cursor.getColumnIndex(GroupContactWorker.NONEREAD));
            if (cursor != null) {
                cursor.close();
            }
            String uIdStr = String.valueOf(uid);
            String groupdIdStr = String.valueOf(bean.groupid);
            String content = GsonUtil.getInstance().getStringFromJsonObject(bean);
            String dataTimeStr = String.valueOf(bean.datetime);
            int atFlag = bean.getReply().contains(uIdStr) ? 1 : 0;

            if (isInGroupChat() && !CommonFunction.isEmptyOrNullStr(groupId)) {
                int currentGroupID = Integer.valueOf(groupId);
                // 在圈聊中且圈id不是此消息的圈id
                if (receiveGroupID != currentGroupID)
                    count++;
            } else if (!isInGroupChat()) {// 不在圈聊中
                count++;
            }
            db.onInsertRecord(uIdStr, groupdIdStr, bean.getGroupname(),
                    bean.getGroupicon(), content, dataTimeStr, count,
                    ChatRecordStatus.NONE, atFlag);
        }
    }

    // /** 6.0屏蔽圈消息的圈子的消息不再下发
    // * groupContact对应圈子未读数+1 用于被屏蔽的圈子收到消息只显示有未读不显示新消息
    // * */
    // public void noReadAddOne(Context context, int groupID) {
    // long uid = Common.getInstance().loginUser.getUid();
    // GroupContactWorker db = DatabaseFactory.getGroupContactWorker(context);
    // int count = db.countGroupNoRead(uid, groupID);
    // db.onUpdateNoRead(uid + "", groupID + "", count + 1);
    // }

    /**
     * @param groupId
     * @return
     * @Title: isHaveNewMessage
     * @Description: 判断某个群组是否有新消息
     */
    public boolean isHaveNewMessage(String groupId) {
        return myGroupTempHasNew.containsKey(groupId);
    }

    /**
     * @param groupId
     * @Title: resetNewMsgFlag
     * @Description: 重置新消息flag
     */
    public void resetNewMsgFlag(String groupId) {
        if (myGroupTempHasNew.containsKey(groupId)) {
            myGroupTempHasNew.remove(groupId);
        }

        if (mMyGroupList == null) {
            return;
        }

        for (Group group : mMyGroupList) {
            if (group.id.equals(groupId)) {
                group.isHaveNewMsg = false;
            }
        }
    }

    /**
     * @param groupList
     * @Title: updateGroupMsgStatus
     * @Description: 更新圈子消息状态
     */
    public void updateGroupMsgStatus(ArrayList<Group> groupList) {
        for (Group group : groupList) {
            if (isHaveNewMessage(group.id)) {
                group.isHaveNewMsg = true;
            }
        }
    }

    /**
     * @Description: 获取圈聊中所有图片聊天记录的小图url
     * @author tanzy
     * @date 2015-8-12
     */
    public ArrayList<String> getGroupChatThumPicUrlList(Context context,
                                                        long groupid) {
        ArrayList<String> list = new ArrayList<String>();
        GroupMessageWorker db = DatabaseFactory.getGroupMessageWorker(context);
        Cursor cursor = db.selectGroupMsgType(groupid, ChatRecordViewFactory.IMAGE);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    String content = cursor.getString(cursor
                            .getColumnIndex(GroupMessageWorker.CONTENT));
                    GroupChatMessage bean = JSON.parseObject(content,GroupChatMessage.class);
                    if (bean != null && bean.type == ChatRecordViewFactory.IMAGE
                            && !CommonFunction.isEmptyOrNullStr(bean.getAttachment())) {
                        String url = bean.getAttachment();
                        list.add(CommonFunction.thumPicture(url));
                    }
                }
            }
            cursor.close();
        }
        return list;
    }

    /**
     * @param list
     * @Title: cacheGroupNearList
     * @Description: 缓存附近圈子数据
     */
    public void cacheGroupNearList(final ArrayList<Group> list, int currentPage,
                                   int totalPage) {
        if (list != null) {
            mGroupNearList = list;
            mCurGroupNearPage = currentPage;
            mTotalGroupNearPage = totalPage;
        }
    }

    /**
     * @param list
     * @Title: cacheMyGroupList
     * @Description: 缓存我的圈子数据
     */
    public void cacheMyGroupList(final ArrayList<Group> list) {
        if (list != null && list.size() > 0) {
            this.mMyGroupList = list;
        } else {
            if (this.mMyGroupList != null) {
                this.mMyGroupList.clear();
            }
        }
    }

    public void removeGroupFromCache(String groupID) {
        if (mMyGroupList != null) {
            for (Group item : mMyGroupList) {
                if (item.id.equals(String.valueOf(groupID))) {
                    mMyGroupList.remove(item);
                    break;
                }
            }
        }
        saveGroupListBufferToFile();
    }

    /**
     * @return
     * @Title: getGroupNearList
     * @Description: 获取缓存的附近圈子数据
     */
    public ArrayList<Group> getCacheGroupNearList() {
        return mGroupNearList;
    }

    /**
     * @return
     * @Title: getCacheMyGroupList
     * @Description: 获取缓存的我的圈子数据
     */
    public ArrayList<Group> getCacheMyGroupList() {
        return mMyGroupList;
    }

    /**
     * @return
     * @Title: getCacheGroupNearCurPage
     * @Description: 获取附近圈子缓存的当前页码
     */
    public int getCacheGroupNearCurPage() {
        return mCurGroupNearPage;
    }

    /**
     * @return
     * @Title: getCacheGroupNearTotalPage
     * @Description: 获取附近圈子缓存的总页数
     */
    public int getCacheGroupNearTotalPage() {
        return mTotalGroupNearPage;
    }

    /**
     * 把文件转成对象
     */
    @SuppressWarnings("unchecked")
    private void getGroupListBufferFromFile() {
        String path = PathUtil.getGroupListFilePath();
        mMyGroupList = (ArrayList<Group>) getBufferFromFile(path);
    }

    /**
     * 把对象保存成文件
     */
    public void saveGroupListBufferToFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = PathUtil.getGroupListFilePath();
                saveBufferToFile(path, mMyGroupList);
            }
        }).start();
    }
}
