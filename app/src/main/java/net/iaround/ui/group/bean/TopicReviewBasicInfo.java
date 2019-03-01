package net.iaround.ui.group.bean;


import net.iaround.model.im.DynamicDetailBaseBean;

public class TopicReviewBasicInfo implements DynamicDetailBaseBean
{
	/** 评论ID */
	public long reviewid;
	
	/** 楼层 */
	public int floor;
	
	/** 角色 0-别人 1-自己（楼主） */
	public int role;
	
	/** 评论者信息 */
	public PostbarUserInfo user;
	
	/** 评论发送时间 */
	public long datetime;
	
	/** 评论的内容 */
	public String content;
	
	/** 距离 单位（m） */
	public long distance;
	
	/** 地址 */
	public String address;
	
	/** 用来判断是否可以回复 ，0-可用，1-已删除状态 */
	public int status;
	
	/** 回复 */
	public TopicReplyBasicInfo reply;

	public PostbarUserInfo initReviewUser()
	{
		return 	new PostbarUserInfo( );
	}

	
	public PostbarUserInfo initReviewUser(long userid , String nickname , String icon ,
										  int vip , String gender , int age )
	{
		user = new PostbarUserInfo( );
		user.userid = userid;
		user.nickname = nickname;
		user.icon = icon;
		user.viplevel = vip;
		user.gender = gender;
		user.age = age;
		
		return user;
	}
	
}
