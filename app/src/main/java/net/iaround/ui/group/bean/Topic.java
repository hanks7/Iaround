
package net.iaround.ui.group.bean;


import net.iaround.model.entity.Review;
import net.iaround.ui.datamodel.User;

import java.util.ArrayList;


/**
 * 话题
 * 
 * @author chenlb
 * 
 */
public class Topic
{
	private User user;
	private String topicId; // 话题ID
	private String content; // 话题内容
	private String imageUrl; // 话题照片
	private int type; // 附件类型：1：文本，2：图片，3：声音，4：视频，5.位置
	private String platname; // 客户端
	private int likeCount; // 喜欢数
	private int reviewCount; // 评论数
	private long publishTime; // 话题发表时间
	private boolean currUserLike; // true:喜欢，false:不喜欢
	private boolean isTop; // 话题是否置顶 true:是，false:否
	
	private ArrayList< Review > reviews; // 评论列表
	private ArrayList< Users > likeUsers; // 喜欢列表
	private String groupId; // 圈子ID
	private String groupName; // 圈子名称
	
	public User getUser( )
	{
		return user;
	}
	
	public void setUser( User user )
	{
		this.user = user;
	}
	
	public String getTopicId( )
	{
		return topicId;
	}
	
	public void setTopicId( String topicId )
	{
		this.topicId = topicId;
	}
	
	public String getContent( )
	{
		return content;
	}
	
	public void setContent( String content )
	{
		this.content = content;
	}
	
	public String getImageUrl( )
	{
		return imageUrl;
	}
	
	public int getType( )
	{
		return type;
	}
	
	public void setType( int type )
	{
		this.type = type;
	}
	
	public void setImageUrl( String imageUrl )
	{
		this.imageUrl = imageUrl;
	}
	
	public String getPlatname( )
	{
		return platname;
	}
	
	public void setPlatname( String platname )
	{
		this.platname = platname;
	}
	
	public int getLikeCount( )
	{
		return likeCount;
	}
	
	public void setLikeCount( int likeCount )
	{
		this.likeCount = likeCount;
	}
	
	public int getReviewCount( )
	{
		return reviewCount;
	}
	
	public void setReviewCount( int reviewCount )
	{
		this.reviewCount = reviewCount;
	}
	
	public long getPublishTime( )
	{
		return publishTime;
	}
	
	public void setPublishTime( long publishTime )
	{
		this.publishTime = publishTime;
	}
	
	public boolean isCurrUserLike( )
	{
		return currUserLike;
	}
	
	public void setCurrUserLike( boolean currUserLike )
	{
		this.currUserLike = currUserLike;
	}
	
	public boolean isTop( )
	{
		return isTop;
	}
	
	public void setTop( boolean isTop )
	{
		this.isTop = isTop;
	}
	
	public ArrayList<Review> getReviews( )
	{
		if ( reviews == null )
		{
			reviews = new ArrayList< Review >( );
		}
		return reviews;
	}
	
	public void setReviews( ArrayList< Review > reviews )
	{
		this.reviews = reviews;
	}
	
	public ArrayList< Users > getLikeUsers( )
	{
		if ( likeUsers == null )
		{
			likeUsers = new ArrayList< Users >( );
		}
		return likeUsers;
	}
	
	public void setLikeUsers( ArrayList< Users > likeUsers )
	{
		this.likeUsers = likeUsers;
	}
	
	public String getGroupId( )
	{
		return groupId;
	}
	
	public void setGroupId( String groupId )
	{
		this.groupId = groupId;
	}
	
	public String getGroupName( )
	{
		return groupName;
	}
	
	public void setGroupName( String groupName )
	{
		this.groupName = groupName;
	}
	
}
