package net.iaround.model.entity;

import net.iaround.ui.datamodel.User;

/**
 * 评论
 * 
 * @author linyg
 * 
 */
public class Review
{
	private String id;
	private String content; // 评论内容
	private long date; // 评论时间
	private User user; // 评论者
	private Quote quote; // 引用
	private int status; // 0为有效，5为被管理员删除
	
	
	public int getStatus( )
	{
		return status;
	}
	
	public void setStatus( int status )
	{
		this.status = status;
	}
	
	public String getId( )
	{
		return id;
	}
	
	public void setId( String id )
	{
		this.id = id;
	}
	
	public String getContent( )
	{
		return content;
	}
	
	public void setContent( String content )
	{
		this.content = content;
	}
	
	public long getDate( )
	{
		return date;
	}
	
	public void setDate( long date )
	{
		this.date = date;
	}
	
	public User getUser( )
	{
		return user;
	}
	
	public void setUser( User user )
	{
		this.user = user;
	}
	
	public Quote getQuote( )
	{
		return quote;
	}
	
	public void setQuote( Quote quote )
	{
		this.quote = quote;
	}
}
