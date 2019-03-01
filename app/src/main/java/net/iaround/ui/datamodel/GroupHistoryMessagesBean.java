package net.iaround.ui.datamodel;


import net.iaround.model.im.GroupChatMessage;

import java.util.ArrayList;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-12-1 下午3:28:07
 * @Description: 71027推送圈子历史消息
 */
public class GroupHistoryMessagesBean {

	public long groupid;//圈子id,messages中的数据都是同一个圈子的
	private String groupname;//圈名
	private String icon;//圈icon
	public ArrayList<GroupChatMessage> messages;//圈聊历史消息list
	private int amount;//消息下发的数量
	
	public ArrayList<GroupChatMessage> getMessageList()
	{
		if(messages == null)
		{
			messages = new ArrayList<GroupChatMessage>();
		}
		return messages;
	}
	
	/** 设置圈名 */
	public void setGroupName(String value)
	{
		groupname = value;
	}
	
	/** 获取圈名 */
	public String getGroupName()
	{
		return groupname == null ? "" : groupname;
	}
	
	/** 设置圈icon */
	public void setIcon(String value)
	{
		icon = value;
	}
	
	/** 获取圈icon */
	public String getIcon()
	{
		return icon == null ? "" : icon;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
}
