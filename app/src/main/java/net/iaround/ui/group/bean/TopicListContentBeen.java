
package net.iaround.ui.group.bean;


import net.iaround.conf.Common;
import net.iaround.model.im.DynamicDetailBaseBean;
import net.iaround.model.im.SyncInfo.SyncItemBean;
import net.iaround.ui.datamodel.BaseUserInfo;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * 话题列表
 * 
 * @author
 * 
 */
public class TopicListContentBeen implements Serializable,DynamicDetailBaseBean,Comparable<TopicListContentBeen>
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7715887021435004448L;

	public int sendStatus;//动态的发送状态 0:表示发送成功 1:表示发送中 2:表示发送失败 3:热门话题列表
	
	public BaseUserInfo user; // /用户信息
	
	public TopicBeen topic;// 话题内容
	
	public int grouprole;// 0 圈主  1 管理员  2 圈成员
	
	public int likecount;// 喜欢次数
	public int reviewcount; // 评论数
	public int curruserlike;// 当前用户是否点赞（0-否，1-是）
	
	public TopicLikeInfBeen likeinfo;
	
	public GroupTopicReviewInfo reviewinfo;
	public TopicSyncInf syncinfo;
	public ArrayList< String> hotTopic;
	
	public GroupTopicPublishBean sendTopicBeen;
	
	public boolean isCurrentHanleView = false;
	public long getTopicId( )
	{
		if ( topic != null )
		{
			return topic.topicid;
		}
		return -1;
	}
	
	public void setTopicId(long id)
	{
		topic.topicid = id;
	}
	
	public ArrayList<String > getPhotoList()
	{
		if(topic.photos==null) topic.photos = new ArrayList< String >( );
		return topic.photos;
	}
	
	public class TopicSyncInf
	{
		public int synctype; //1,//类型(int)（1-同步到，2-同步来自）    
		
		public ArrayList<SyncItemBean> list;
	}
	
	public void setSendTopicBeen(GroupTopicPublishBean been)
	{
		this.sendTopicBeen = been;
		
		setSendTopicFailViewData();
	}
	
	public void setSendTopicFailViewData()
	{
		if(sendTopicBeen!=null)
		{
			
			sendStatus = 1; 
			if(user ==null) user = new BaseUserInfo( );
			user.nickname = Common.getInstance( ).loginUser.getNickname( );
			user.age = Common.getInstance( ).loginUser.getAge( );
			user.userid = Common.getInstance( ).loginUser.getUid( );
			user.gender = Common.getInstance( ).loginUser.getGender( );
			user.icon= Common.getInstance( ).loginUser.getIcon( );
			user.viplevel = Common.getInstance( ).loginUser.getViplevel( );
			user.svip = Common.getInstance().loginUser.getSVip();
			
			if(topic ==null) topic = new TopicBeen( ) ;
			topic.address = sendTopicBeen.getAddress( );
			topic.content = sendTopicBeen.getContent( );
			topic.datetime = sendTopicBeen.datetime ;
			topic.distance = 0;
			topic.photos = sendTopicBeen.getPhotoList( ) ;
			topic.sync = sendTopicBeen.getSync( );
			topic.syncvalue =sendTopicBeen.getSyncvalue( ) ;
			topic.topicid = sendTopicBeen.datetime ;
			if(sendTopicBeen.getPhotoList( ).size( )>0)
			{
				topic.type = 2;
			}
		}
	}
	
	@Override
	public int compareTo(TopicListContentBeen arg0) {
		//倒序排列
		return -Long.valueOf(this.topic.datetime).compareTo(Long.valueOf(arg0.topic.datetime));
	}
}
