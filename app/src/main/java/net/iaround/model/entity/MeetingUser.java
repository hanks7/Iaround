
package net.iaround.model.entity;

import net.iaround.ui.datamodel.User;

public class MeetingUser
{
	
	public MeetingUser(User user , Basic basic , String content , int compositerate )
	{
		super( );
		this.user = user;
		this.basic = basic;
		this.content = content;
		this.compositerate = compositerate;
	}
	
	private User user;
	private Basic basic;
	
	public Basic getBasic( )
	{
		return basic;
	}
	
	public void setBasic( Basic basic )
	{
		this.basic = basic;
	}
	
	private String content;
	private int compositerate;
	
	public int getCompositerate( )
	{
		return compositerate;
	}
	
	public void setCompositerate( int compositerate )
	{
		this.compositerate = compositerate;
	}
	
	public User getUser( )
	{
		return user;
	}
	
	public void setUser( User user )
	{
		this.user = user;
	}
	
	public String getContent( )
	{
		return content;
	}
	
	public void setContent( String content )
	{
		this.content = content;
	}
	
	
}
