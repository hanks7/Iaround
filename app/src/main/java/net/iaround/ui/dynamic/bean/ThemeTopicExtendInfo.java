
package net.iaround.ui.dynamic.bean;


import net.iaround.ui.datamodel.User;
import net.iaround.ui.group.bean.PostbarUserInfo;
import net.iaround.ui.postbar.bean.PostBarPublishBean;
import net.iaround.ui.postbar.bean.PostbarTopicDetailInfo;

import java.io.Serializable;


public class ThemeTopicExtendInfo implements Serializable, Comparable< ThemeTopicExtendInfo >
{


	/**
	 * 
	 */
	private static final long serialVersionUID = -7047325963172746919L;

	/** 用户信息 */
	public PostbarUserInfo user;
	
	public PostbarTopicDetailInfo topic;
	
	/** 当前用户是否点赞（0-否，1-是） */
	public int curruserlike;
	
	/** 点赞次数 */
	public int likecount;
	
	/** 评论数 */
	public int reviewcount;
	
	/** 用来保存发布话题时候的信息,以备重发 */
	private PostBarPublishBean publishInfo;
	
	/** 用来判断是否播放动画 */
	public int currentLikeStatus;
	
	public static final int SUCCESS = 0;
	public static final int SENDDING = 1;
	public static final int FAILURE = 2;
	
	private int sendStatus;// 话题的发送状态 0:表示发送成功 1:表示发送中 2:表示发送失败
	
	// 初始化贴吧话题发布者的信息
	public void initTopicPublisherInfo( User sourceUser )
	{
		user = new PostbarUserInfo( );
		user.userid = sourceUser.getUid( );
		user.nickname = sourceUser.getNickname( );
		user.age = sourceUser.getAge( );
		user.icon = sourceUser.getIcon( );
		user.viplevel = sourceUser.getViplevel( );
		user.gender = sourceUser.getGender( );
		user.svip = sourceUser.getSVip( );
	}
	
	public void initTopicContent( PostbarTopicDetailInfo topicInfo )
	{
		if ( topic == null )
		{
			topic = new PostbarTopicDetailInfo( );
		}
		
		topic.address = topicInfo.address;
		topic.content = topicInfo.content;
		topic.datetime = topicInfo.datetime;
		topic.distance = topicInfo.distance;
		topic.isessence = topicInfo.isessence;
		topic.ishot = topicInfo.ishot;
		topic.isnew = topicInfo.isnew;
		topic.istop = topicInfo.istop;
		topic.photos = topicInfo.photos;
		topic.postbarid = topicInfo.postbarid;
		topic.postbarname = topicInfo.postbarname;
		topic.sync = topicInfo.sync;
		topic.synctype = topicInfo.synctype;
		topic.syncvalue = topicInfo.syncvalue;
		topic.type = topicInfo.type;
		topic.userid = topicInfo.userid;
		topic.topicid = topicInfo.topicid;
	}
	
	public int getSendStatus( )
	{
		return sendStatus;
	}
	
	public void setSendStatus( int sendStatus )
	{
		this.sendStatus = sendStatus;
	}
	
	public PostBarPublishBean getPublishInfo( )
	{
		return publishInfo;
	}
	
	public void setPublishInfo( PostBarPublishBean publishInfo )
	{
		this.publishInfo = publishInfo;
	}
	
	/** 判断话题发送是否失败 */
	public boolean isSendFail( )
	{
		return sendStatus == FAILURE;
	}
	
	@Override
	public int compareTo( ThemeTopicExtendInfo another )
	{
		// TODO Auto-generated method stub
		//（不管发布成功还是发布失败），发送后直接显示在置顶帖下
		if ( another.topic.istop != topic.istop )
			return another.topic.istop - topic.istop;
		
		if ( another.topic.datetime != topic.datetime )
			return -Long.valueOf( topic.datetime ).compareTo(
					Long.valueOf( another.topic.datetime ) );
		
		return 0;
	}
}
