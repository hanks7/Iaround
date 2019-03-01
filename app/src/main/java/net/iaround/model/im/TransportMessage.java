
package net.iaround.model.im;


/**
 * 消息体接口类
 * 
 * @author Administrator
 * 
 */
public class TransportMessage
{
	private long id = 0; // 本地id
	private int methodId = 0; // 功能号
	private long contentLength = 0; // 数据长度
	private String contentBody = ""; // 消息体
	
	public long getId( )
	{
		return id;
	}
	
	public void setId( long _id )
	{
		this.id = _id;
	}
	
	
	public int getMethodId( )
	{
		return methodId;
	}
	
	public void setMethodId( int methodId )
	{
		this.methodId = methodId;
	}
	
	public void setContentLength( long length )
	{
		this.contentLength = length;
	}
	
	public long getContentLength( )
	{
		return this.contentLength;
	}
	
	public String getContentBody( )
	{
		return contentBody;
	}
	
	public void setContentBody( String contentBody )
	{
		this.contentBody = contentBody;
	}
}
