
package net.iaround.ui.group.bean;


import net.iaround.model.im.BaseServerBean;
import net.iaround.ui.datamodel.User;

import java.util.ArrayList;


/**
 * @Description: /topic/message/mylist和/topic/message/history的消息实体
 * @author tanzy
 * @date 2015-1-21
 */
public class GroupTopicMessageListBean extends BaseServerBean
{
	public ArrayList< MsgListItem > msgs;// 消息列表
	public int pageno;// mylist没有
	public int pagesize;// mylist没有
	public int amount;// mylist没有
	
	public class MsgListItem
	{
		public TopicItem topic;// 话题
		public MsgItem msg;// 消息
	}
	
	public class TopicItem
	{
		public int topicid;// 话题id
		public String content;// 动态内容
		public String image;// 图片
		public long groupid;//圈id
	}
	
	public class MsgItem
	{
		public MsgUser user;// 评论者
		public int type;// 评论类型，1：评论，2：点赞
		public long datetime;// 评论发送时间
		public String content;// 评论内容
	}
	
	public class MsgUser
	{
		public long userid;// 用户id
		public String nickname;// 昵称
		public String icon;// 头像
		public int viplevel;// vip等级，0：非vip
		public int age;// 年龄
		public String gender;// 性别，f：女，m：男
	
		public User converToUser(MsgUser msgUser)
		{
			User user = new User( );
			user.setIcon( msgUser.icon );
			user.setUid( msgUser.userid );
			user.setNickname( msgUser.nickname );
			user.setAge( msgUser.age );
			return user;
		}
	}
	
	
	
}
