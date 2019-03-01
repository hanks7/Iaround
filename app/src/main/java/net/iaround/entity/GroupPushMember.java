package net.iaround.entity;
/**
 * @ClassName GroupPushMember.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-7-10 下午4:20:37
 * @Description: 推送圈子新成员数量和未审核成员数量消息 协议：81056
 */

public class GroupPushMember
{
	public int groupid;//圈子ID
	public int num;//数量
	public int type;//类型type 类型:1 新成员,2 未审核成员
	
	/**
	 * 判断是否新成员类型
	 * @return true：为新成员类型，
	 * 			false： 为未审核成员类型
	 */
	public boolean isNewMemberType()
	{
		return type == 1;
	}
}
