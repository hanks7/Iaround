package net.iaround.utils.eventbus;

import net.iaround.ui.group.bean.GroupSearchUser;

/**
 * Created by Ray on 2017/5/27.
 */

public class NickNameNotifyEvent {
    private String mMsg;
    private long mUserId;
    private int mAge;
    private String mUrl;
    private String skillTitle;

    /**
     * 聊吧成员操作需要通过EventBus发出通知并传递参数
     */
    private GroupSearchUser groupSearchUser;
    private String updateUser;

    public NickNameNotifyEvent()
    {}

    public NickNameNotifyEvent(String noteName, long uid) {
        // TODO Auto-generated constructor stub
        mMsg = noteName;
        mUserId = uid;
    }

    public GroupSearchUser getGroupSearchUser() {
        return groupSearchUser;
    }

    public void setGroupSearchUser(GroupSearchUser groupSearchUser) {
        this.groupSearchUser = groupSearchUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }
    public NickNameNotifyEvent(String skillTitle){
        this.skillTitle = skillTitle;

    }

    public String getMsg() {
        return mMsg;
    }

    public long getUserId() {
        return mUserId;
    }
    public int getUserAge() {
        return mAge;
    }

    public String getUserImageUrl() {
        return mUrl;
    }

    public String getSkillTitle() {
        return skillTitle;
    }

    public void setSkillTitle(String skillTitle) {
        this.skillTitle = skillTitle;
    }
}
