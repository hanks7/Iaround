package net.iaround.ui.group.bean;


public class TopicReplyBasicInfo
{
	/** 回复者信息 */
	public PostbarUserInfo user;
	
	/** 被引用的评论发送时间 */
	public long datetime;
	
	/** 楼层 */
	public int floor;
	
	/** 被引用的评论的内容 */
	public String content;
}
