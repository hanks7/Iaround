package net.iaround.entity;

import net.iaround.model.im.ChatTargetInfo;

/**
 * @author KevinSu kevinsu917@126.com
 * @create 2015年3月30日 下午4:46:07
 * @Description: 圈聊消息发送需要的信息
 */
public class GroupChatInfo extends ChatTargetInfo {

	private long mGroupId;//圈子的id
	private String mGroupName;//圈子的名字
	private String mGroupIcon;//圈子的图标`
	private String mReply;//At某人的id,ps:"123456,123457,12358"
	
	public long getGroupId() {
		return mGroupId;
	}
	public void setGroupId(long groupId) {
		this.mGroupId = groupId;
	}
	public String getGroupName() {
		return mGroupName;
	}
	public void setGroupName(String groupName) {
		this.mGroupName = groupName;
	}
	public String getGroupIcon() {
		return mGroupIcon;
	}
	public void setGroupIcon(String groupIcon) {
		this.mGroupIcon = groupIcon;
	}
	public String getReply() {
		return mReply;
	}
	public void setReply(String mReply) {
		this.mReply = mReply;
	}
}
