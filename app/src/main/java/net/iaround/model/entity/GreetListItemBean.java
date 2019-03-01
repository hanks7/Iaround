package net.iaround.model.entity;

import net.iaround.ui.datamodel.UserInfo;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-12-6 上午10:20:59
 * @Description: 点赞用户实体
 */
public class GreetListItemBean {

	private UserInfo user;//点赞用户
	public long datetime;//点赞时间

	/** 获取点赞用户 */
	public UserInfo getUser()
	{
		return user == null ? new UserInfo() : user;
	}

}
