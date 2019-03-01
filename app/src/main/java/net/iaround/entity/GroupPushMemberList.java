package net.iaround.entity;

import java.util.ArrayList;

/**
 * @ClassName GroupPushMemberList.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-7-10 下午4:23:48
 * @Description: 推送圈子新成员数量和未审核成员数量消息 协议：81056
 */

public class GroupPushMemberList
{
	public ArrayList< GroupPushMemberGroup > groups;
	
	public class GroupPushMemberGroup
	{
		public GroupPushMember group;
	}
}
