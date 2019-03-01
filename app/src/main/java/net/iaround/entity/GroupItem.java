
package net.iaround.entity;


import android.text.TextUtils;

import net.iaround.ui.datamodel.User;


/**
 * 群组列表
 * 
 * @ClassName: GroupItem
 * @Description: TODO
 * @author linyg@iaround.net
 * @date 2012-2-29 下午1:42:16
 * 
 */
public class GroupItem
{
	private String id;
	private String icon; // 图标
	
	private String title; // 标题
	private int type; // item分组：1广告、2我的聊天室title，3邂逅聊天室title，4群组item
	private String distance; // 距离
	private String usercount; // 成员数量
	private String rang; // 范围
	private int flag; // 效果 ：2为热门，1为最新，0为啥都不是
	private int level; // 等级
	private boolean hasnew; // 是否有新消息
	private User user; // 创建者
	private boolean isjoin; // 是否加入
	private long datetime;
	private String address; // 地址
	private String categoryId; // 群类别分类ID
	private String categoryStr; // 群类别分类名称
	private String subCategoryId; // 群类别子分类ID
	private String subCategoryStr; // 群类别子分类名称
	private int lat;
	private int lng;
	private String adlink; // 连接地址
	private int connectstatus;
	private boolean isDefaultCache; // 是否为默认的缓存数据
	private int iconResId; // 默认的缓存数据的图标资源ID
	
	private int topicCount; // 话题数
	private double oldMaxRange; // 历史最大范围
	
	private int grouprole; // 群成员角色
	
	public static final int ADITEM = 1;
	public static final int MYGROUPTITLEITEM = 2;
	public static final int NEARGROUPTITLEITEM = 3;
	public static final int GROUPITEM = 4;
	
	private int order; // 用于比较
	
	/** 用于我的圈子列表展现的字段，仅用作判断是否为添加圈子项 */
	public boolean isAddGroup = false;
	
	public boolean isOutOfRange( )
	{
		double dis , rng = 0;
		
		try
		{
			if ( TextUtils.isEmpty( distance ) )
			{
				dis = 0;
			}
			else
			{
				dis = Double.parseDouble( distance.substring( 0 , distance.indexOf( "km" ) ) );
			}
			if ( TextUtils.isEmpty( rang ) )
			{
				rng = 0;
			}
			else
			{
				rng = Double.parseDouble( rang );
			}
			if ( rng > dis )
			{
				return false;
			}
		}
		catch ( RuntimeException e )
		{
			
		}
		return true;
	}
	
	public String getId( )
	{
		return id;
	}
	
	public void setId( String id )
	{
		this.id = id;
	}
	
	public String getIcon( )
	{
		return icon;
	}
	
	public void setIcon( String icon )
	{
		this.icon = icon;
	}
	
	public String getTitle( )
	{
		return title;
	}
	
	public void setTitle( String title )
	{
		this.title = title;
	}
	
	public int getType( )
	{
		return type;
	}
	
	public void setType( int type )
	{
		this.type = type;
	}
	
	public String getDistance( )
	{
		return distance;
	}
	
	public void setDistance( String distance )
	{
		this.distance = distance;
	}
	
	public String getUsercount( )
	{
		return usercount;
	}
	
	public void setUsercount( String usercount )
	{
		this.usercount = usercount;
	}
	
	public String getRang( )
	{
		return rang;
	}
	
	public void setRang( String rang )
	{
		this.rang = rang;
	}
	
	public int getFlag( )
	{
		return flag;
	}
	
	public void setFlag( int flag )
	{
		this.flag = flag;
	}
	
	public int getLevel( )
	{
		return level;
	}
	
	public void setLevel( int level )
	{
		this.level = level;
	}
	
	public boolean isHasnew( )
	{
		return hasnew;
	}
	
	public void setHasnew( boolean hasnew )
	{
		this.hasnew = hasnew;
	}
	
	public User getUser( )
	{
		return user;
	}
	
	public void setUser( User user )
	{
		this.user = user;
	}
	
	public long getDatetime( )
	{
		return datetime;
	}
	
	public void setDatetime( long datetime )
	{
		this.datetime = datetime;
	}
	
	public boolean isIsjoin( )
	{
		return isjoin;
	}
	
	public void setIsjoin( boolean isjoin )
	{
		this.isjoin = isjoin;
	}
	
	public String getAddress( )
	{
		return address;
	}
	
	public void setAddress( String address )
	{
		this.address = address;
	}
	
	public String getCategoryId( )
	{
		return categoryId;
	}
	
	public void setCategoryId( String categoryId )
	{
		this.categoryId = categoryId;
	}
	
	public String getCategoryStr( )
	{
		return categoryStr;
	}
	
	public void setCategoryStr( String categoryStr )
	{
		this.categoryStr = categoryStr;
	}
	
	public String getSubCategoryId( )
	{
		return subCategoryId;
	}
	
	public void setSubCategoryId( String subCategoryId )
	{
		this.subCategoryId = subCategoryId;
	}
	
	public String getSubCategoryStr( )
	{
		return subCategoryStr;
	}
	
	public void setSubCategoryStr( String subCategoryStr )
	{
		this.subCategoryStr = subCategoryStr;
	}
	
	public int getLat( )
	{
		return lat;
	}
	
	public void setLat( int lat )
	{
		this.lat = lat;
	}
	
	public int getLng( )
	{
		return lng;
	}
	
	public void setLng( int lng )
	{
		this.lng = lng;
	}
	
	public String getAdlink( )
	{
		return adlink;
	}
	
	public void setAdlink( String adlink )
	{
		this.adlink = adlink;
	}
	
	public int getConnectstatus( )
	{
		return connectstatus;
	}
	
	public void setConnectstatus( int connectstatus )
	{
		this.connectstatus = connectstatus;
	}
	
	public boolean isDefaultCache( )
	{
		return isDefaultCache;
	}
	
	public void setDefaultCache( boolean isDefaultCache )
	{
		this.isDefaultCache = isDefaultCache;
	}
	
	public int getIconResId( )
	{
		return iconResId;
	}
	
	public void setIconResId( int iconResId )
	{
		this.iconResId = iconResId;
	}
	
	public int getTopicCount( )
	{
		return topicCount;
	}
	
	public void setTopicCount( int topicCount )
	{
		this.topicCount = topicCount;
	}
	
	public double getOldMaxRange( )
	{
		return oldMaxRange;
	}
	
	public void setOldMaxRange( double oldMaxRange )
	{
		this.oldMaxRange = oldMaxRange;
	}
	
	public int getOrder( )
	{
		return order;
	}
	
	public int getGrouprole( )
	{
		return grouprole;
	}
	
	public void setGrouprole( int grouprole )
	{
		this.grouprole = grouprole;
	}
	
	public void setOrder( int order )
	{
		this.order = order;
	}
	
}
