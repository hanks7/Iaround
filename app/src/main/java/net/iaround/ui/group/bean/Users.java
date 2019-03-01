package net.iaround.ui.group.bean;

import net.iaround.ui.datamodel.User;

/**
 * @ClassName Users
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-4-21 下午10:14:53
 * @Description: 对应http协议的/topic/likeusers中的users对象
 */

public class Users
{
	public long getDatetime( )
	{
		return datetime;
	}

	public void setDatetime( long datetime )
	{
		this.datetime = datetime;
	}

	public int getDistance( )
	{
		return distance;
	}

	public void setDistance( int distance )
	{
		this.distance = distance;
	}

	public User getUser( )
	{
		return user;
	}

	public void setUser( User user )
	{
		this.user = user;
	}
	
	private User user;
	private int distance;
	private long datetime;
}
