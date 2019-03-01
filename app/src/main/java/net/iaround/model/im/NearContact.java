
package net.iaround.model.im;


import net.iaround.ui.datamodel.User;

/**
 * 最近联系人
 * 
 * @author linyg
 * 
 */
public class NearContact
{
	private long fUid;
	private User user;
	private int type;
	private String content;
	private int number; //私聊消息数量
	private boolean isFriendMsg; // 最后发的内容，是否好友所发的
	private int chatStatus;
	private int distance;//距离
	private int subGroup;//消息组类型，1：私聊组，2：发出搭讪组，3：收到搭讪组,，4：游戏中心，5：贴吧广场
	private int quietSeen;//是否已经悄悄查看过
	
	public int getChatStatus( )
	{
		return chatStatus;
	}
	
	public void setChatStatus( int chatStatus )
	{
		this.chatStatus = chatStatus;
	}
	
	public long getfUid( )
	{
		return fUid;
	}
	
	public void setfUid( long fUid )
	{
		this.fUid = fUid;
	}
	
	public int getType( )
	{
		return type;
	}
	
	public void setType( int type )
	{
		this.type = type;
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
	
	public int getNumber( )
	{
		return number;
	}
	
	public void setNumber( int number )
	{
		this.number = number;
	}
	
	public boolean isFriendMsg( )
	{
		return isFriendMsg;
	}
	
	public void setFriendMsg( boolean isFriendMsg )
	{
		this.isFriendMsg = isFriendMsg;
	}
	
	public void setSubGroup(int mSubGroup)
	{
		this.subGroup = mSubGroup;
	}
	
	public int getSubGroup()
	{
		return subGroup;
	}

	public int getDistance( )
	{
		return distance;
	}

	public void setDistance( int distance )
	{
		this.distance = distance;
	}

	public int getQuietSeen( )
	{
		return quietSeen;
	}

	public void setQuietSeen( int quietSeen )
	{
		this.quietSeen = quietSeen;
	}
	
}
