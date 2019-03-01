package net.iaround.ui.group.bean;

/** 保存圈子信息的实体，用于展示关系链的节点 */
public class GroupInfoForRelation
{
	public static long GROUP_ID = 0;
	public static String GROUP_NAME = "";
	public static String GROUP_ICON = "";
	
	public static void setGroupInfoForRelation(long groupid ,
											   String groupname , String groupicon )
	{
		GROUP_ID = groupid;
		GROUP_NAME = groupname;
		GROUP_ICON = groupicon;
	}
}
