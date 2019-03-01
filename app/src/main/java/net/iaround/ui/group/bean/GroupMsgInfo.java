
package net.iaround.ui.group.bean;


import java.io.Serializable;


public class GroupMsgInfo implements Serializable
{
	
	private static final long serialVersionUID = 1L;
	public Group group;
	public int type;// 1-接收并提醒，2-收入圈助手且不提醒，3-屏蔽圈消息
	
	public class Group implements Serializable
	{
		private static final long serialVersionUID = 1L;
		public long id;
		public String name;
		public String icon;
	}
	
}
