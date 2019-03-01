
package net.iaround.ui.datamodel;


import net.iaround.model.entity.Review;

import java.util.ArrayList;


/**
 * 帖子
 * 
 * @author linyg
 * 
 */
public class Blog
{
	private String phontoid;
	private String content; // 内容
	private String icon; // 图标
	private String image; // 图片
	private String hdimage; // 高清图
	private boolean ishdimage; // 是否为高清图
	
	private long date; // 发布时间
	private String address; // 地址
	private int likecount; // 喜欢的次数
	private String curruserlike;// 这个帖子我之前做过的评论
	private int reviewcount; // 评论数量
	private String plat; // 评论来自平台
	private String browsetimes;// 浏览次数
	
	private User user;
	private ArrayList<Review> reviews;
	private ArrayList< User > likeUsers;
	
	public ArrayList< User > getLikeUsers( )
	{
		if ( likeUsers == null )
		{
			likeUsers = new ArrayList< User >( );
		}
		return likeUsers;
	}
	
	public void setLikeUsers( ArrayList< User > likeUsers )
	{
		this.likeUsers = likeUsers;
	}
	
	public String getPhontoid( )
	{
		return phontoid;
	}
	
	public void setPhontoid( String phontoid )
	{
		this.phontoid = phontoid;
	}
	
	public String getIcon( )
	{
		return icon;
	}
	
	public void setIcon( String icon )
	{
		this.icon = icon;
	}
	
	public long getDate( )
	{
		return date;
	}
	
	public void setDate( long date )
	{
		this.date = date;
	}
	
	public String getContent( )
	{
		return content;
	}
	
	public void setContent( String content )
	{
		this.content = content;
	}
	
	public String getImage( )
	{
		return image;
	}
	
	public void setImage( String image )
	{
		this.image = image;
	}
	
	public String getAddress( )
	{
		return address;
	}
	
	public void setAddress( String address )
	{
		this.address = address;
	}
	
	public int getLikecount( )
	{
		return likecount;
	}
	
	public void setLikecount( int likecount )
	{
		this.likecount = likecount;
	}
	
	public String getCurruserlike( )
	{
		return curruserlike;
	}
	
	public void setCurruserlike( String curruserlike )
	{
		this.curruserlike = curruserlike;
	}
	
	public int getReviewcount( )
	{
		return reviewcount;
	}
	
	public void setReviewcount( int reviewcount )
	{
		this.reviewcount = reviewcount;
	}
	
	public String getPlat( )
	{
		return plat;
	}
	
	public void setPlat( String plat )
	{
		this.plat = plat;
	}
	
	public User getUser( )
	{
		return user;
	}
	
	public void setUser( User user )
	{
		this.user = user;
	}
	
	public ArrayList< Review > getReviews( )
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
	
	public String getHdimage( )
	{
		return hdimage;
	}
	
	public void setHdimage( String hdimage )
	{
		this.hdimage = hdimage;
	}
	
	public boolean isIshdimage( )
	{
		return ishdimage;
	}
	
	public void setIshdimage( boolean ishdimage )
	{
		this.ishdimage = ishdimage;
	}
	
	public String getBrowsetimes( )
	{
		return browsetimes;
	}
	
	public void setBrowsetimes( String browsetimes )
	{
		this.browsetimes = browsetimes;
	}
	
	@Override
	public boolean equals( Object o )
	{
		if ( o != null && o instanceof Blog )
		{
			if ( ( ( Blog ) o ).getPhontoid( ) == phontoid )
			{
				return true;
			}
		}
		return false;
	}
}
