
package net.iaround.model.entity;

/**
 * 引用(一般在发表评论时使用到，就是评论的评论)
 * 
 * @author linyg
 * 
 */
public class Quote
{
	private long userid; // 被引用者id
	private String nickname;// 被引用者昵称
	private String quoteid; // 引用内容id
	private String content; // 引用内容
	private int status; // 0有效，5被管理员删除
	
	public int getStatus( )
	{
		return status;
	}
	
	public void setStatus( int status )
	{
		this.status = status;
	}
	
	public long getUserid( )
	{
		return userid;
	}
	
	public void setUserid( long userid )
	{
		this.userid = userid;
	}
	
	public String getNickname( )
	{
		return nickname;
	}
	
	public void setNickname( String nickname )
	{
		this.nickname = nickname;
	}
	
	public String getQuoteid( )
	{
		return quoteid;
	}
	
	public void setQuoteid( String quoteid )
	{
		this.quoteid = quoteid;
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
