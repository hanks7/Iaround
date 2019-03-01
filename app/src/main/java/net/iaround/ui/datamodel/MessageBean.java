
package net.iaround.ui.datamodel;


public class MessageBean< T > implements Comparable< MessageBean< ? >>
{
	
	/**
	 * 详见 MessageListType
	 */
	public int type;
	
	/**
	 * 用户是否是vip只有type是4的时候处理
	 */
	public int vip;
	
	/**用户是否为SVIP（大于0为SVIP），只有type为4时处理*/
	public int svip;
	
	/**
	 * 纬度
	 */
	public int lat;
	/**
	 * 经度
	 */
	public int lng;
	
	/**
	 * 图标地址
	 */
	public String iconUrl;
	
	/**
	 * 标题
	 */
	public String title;
	
	/**
	 * 内容
	 */
	public String content;
	
	/**
	 * 距离
	 */
	public int distance;
	
	/**
	 * 时间
	 */
	public long time;
	
	/**
	 * 消息描述（如：送达、已读）
	 */
	public String messageDesc;
	
	/**
	 * 消息数
	 */
	public int messageNum;
	
	/**
	 * 消息类型
	 */
	public int messageType;
	
	/**
	 * 最后发的内容，是否好友所发的
	 */
	public boolean isFriendMsg;
	
	/**
	 * 私聊状态 如送达已读
	 */
	public int chatStatus;
	
	public int age;
	
	public int sex;

	public int userType;
	
	/**
	 * 附加数据
	 */
//	public T messageData;
	public Object messageData;

	@Override
	public int compareTo( MessageBean< ? > another )
	{
		if ( this.time < another.time )
		{
			return 1;
		}
		else if ( this.time > another.time )
		{
			return -1;
		}
		
		return 0;
	}
	
}
