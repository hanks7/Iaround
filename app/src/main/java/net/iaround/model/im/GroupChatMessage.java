package net.iaround.model.im;

import net.iaround.model.entity.Item;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-12-1 下午2:43:27
 * @Description: 圈聊的单条消息实体
 */
public class GroupChatMessage {

	public long groupid;//圈子id
	public int type;//消息类型-@ChatRecordViewFactory
	public long msgid;//消息id
	public long incmsgid;//自增id
	public long datetime;//发送时间
	private String data;	//语音内容（Base64编码）
	private String reply;//At的用户id, "12365,25652"
	private String attachment; //附件URL
	public UserTypeOne user; //发送者详情
	private GroupInfo group;//圈子信息 [71027协议使用]
	
	public Object content;//不同type，content的内容不同，具体请参照协议文档 @ContentBase
	public int recruit;//0代表不招募1可招募
	public Item item;
	
	
	
	/** 获取语音的数据 */
	public String getData() {
		return data == null ? "" : data;
	}
	
	/** 设置语音的数据 */
	public void setData(String value)
	{
		data = value;
	}
	
	/** 获取附件URL */
	public String getAttachment() {
		return attachment == null ? "" : attachment;
	}
	
	/** 设置附件URL */
	public void setAttachment(String value)
	{
		attachment = value;
	}

	public String getReply() {
		return reply == null ? "" : reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public String getGroupname() {
		if(getGroup() == null){
			setGroup(new GroupInfo());
		}
		return getGroup().getGroupname() == null ? "" : getGroup().getGroupname();
	}

	public void setGroupname(String groupname) {
		if(getGroup() == null){
			setGroup(new GroupInfo());
		}
		getGroup().setGroupname(groupname);
	}

	public String getGroupicon() {
		if(getGroup() == null){
			setGroup(new GroupInfo());
		}
		return getGroup().getGroupicon() == null ? "" : getGroup().getGroupicon();
	}

	public void setGroupicon(String groupicon) {
		if(getGroup() == null){
			setGroup(new GroupInfo());
		}
		getGroup().setGroupname(groupicon);
	}
	
	private GroupInfo getGroup() {
		return group;
	}

	public void setGroup(GroupInfo group) {
		this.group = group;
	}

	class GroupInfo{
		private long groupid;//圈子id
		private String groupname;//圈子名称
		private String groupicon;//圈子icon的url
		public long getGroupid() {
			return groupid;
		}
		public void setGroupid(long groupid) {
			this.groupid = groupid;
		}
		public String getGroupname() {
			return groupname;
		}
		public void setGroupname(String groupname) {
			this.groupname = groupname;
		}
		public String getGroupicon() {
			return groupicon;
		}
		public void setGroupicon(String groupicon) {
			this.groupicon = groupicon;
		}
	}
}
