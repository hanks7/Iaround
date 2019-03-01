package net.iaround.model.entity;

import net.iaround.model.im.DynamicDetailBaseBean;
import net.iaround.ui.datamodel.BaseUserInfo;

import java.io.Serializable;


/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-9-30 下午4:56:58
 * @Description: 单条动态评论实体
 */
public class DynamicReviewItem implements Serializable, DynamicDetailBaseBean {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4189821576796912228L;
	public long reviewid;//评论ID
	public ReviewUser user;//评论者
	public long datetime;//评论发送的时间
	public String content;//评论的内容
	public String nickname;//评论的内容
	public ReplyUser reply;//回复某人
	
	public static class ReviewUser extends BaseUserInfo implements Serializable
	{
//		public int horoscope;

//		public int getHoroscope() {
//			return horoscope;
//		}
//
//		public void setHoroscope(int horoscope) {
//			this.horoscope = horoscope;
//		}
//
//		public long getBirthday() {
//			return birthday;
//		}
//
//		public void setBirthday(long birthday) {
//			this.birthday = birthday;
//		}

//		public long birthday;
		/**
		 * 
		 */
		private static final long serialVersionUID = 2454904705188429905L;
//		public long userid;//用户id
//		private String nickname;//用户昵称
//		private String icon;//用户头像
//		private String gender;//用户性别
//		public int age;//用户年龄
//		public int viplevel;//用户vip等级，0时为非VIP
//		public int svip;//svip标识(0-非svip 1（大于0）-svip
		
		/** 设置用户昵称 */
		public void setNickName(String value)
		{
			nickname = value;
		}
		
		/** 获取用户昵称 */
		public String getNickName()
		{
			return nickname == null ? "" : nickname;
		}
		
		/** 设置用户头像 */
		public void setIcon(String value)
		{
			icon = value;
		}
		
		/** 获取用户头像 */
		public String getIcon()
		{
			return icon == null ? "" : icon;
		}
		
		/** 设置用户性别 */
		public void setGender(String value)
		{
			gender = value;
		}
		
		/** 获取用户性别 */
		public String getGender()
		{
			return gender == null ? "" : gender;
		}
		
		/** 如果性别的字段为null 或者为 f表示是男性, flase 为女性*/
		public boolean isMale()
		{
			return (gender == null || gender.equals("m"));
		}
		
	}

	public ReviewUser initReviewUser()
	{
		return  new ReviewUser();
	}
	
	public ReviewUser initReviewUser(long userid, String nickname, String icon, int vip, String gender, int age)
	{
		user = new ReviewUser();
		user.userid = userid;
		user.nickname = nickname;
		user.icon = icon;
		user.viplevel = vip;
		user.gender = gender;
		user.age = age;
		
		return user;
	}
	
	public static class ReplyUser implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -3982982207968306510L;
		public long userid;//用户id
		public String nickname;//用户昵称
		public int viplevel;//0为非vip
	}
	
	public ReplyUser initReplyUser(long userid, String nickname)
	{
		reply = new ReplyUser();
		reply.userid = userid;
		reply.nickname = nickname;
		return reply;
	}
}
