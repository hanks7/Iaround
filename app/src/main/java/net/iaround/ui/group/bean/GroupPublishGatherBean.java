
package net.iaround.ui.group.bean;


import java.util.ArrayList;


/**
 * @author zhengshaota
 * @version 创建时间：2014-11-24 下午 16:04
 * @Description: 发布圈聚会的实体
 */
public class GroupPublishGatherBean
{
	
	private String content;// 内容
	private ArrayList< String > photos;// 照片url
	private String address;// 地址
	private long jointime;
	private String phone;
	private String cost;
	private String groupid;
	private String partyid;
	
	/** 设置圈聚会ID */
	public void setPartyid( String value )
	{
		partyid = value;
	}
	
	/** 获取圈聚会ID */
	public String getPartyid( )
	{
		return partyid == null ? "" : partyid;
	}
	
	/** 设置圈聚会布内容 */
	public void setContent( String value )
	{
		content = value;
	}
	
	/** 获取圈聚会内容 */
	public String getContent( )
	{
		return content == null ? "" : content;
	}
	
	/** 设置参加时间 */
	public void setJointime( long value )
	{
		jointime = value;
	}
	
	/** 获取参加时间 */
	public long getJointime( )
	{
		return jointime;
	}
	
	
	/** 设置图片List */
	public void setPhotoList( ArrayList< String > value )
	{
		if ( photos == null )
		{
			photos = new ArrayList< String >( );
		}
		photos.clear( );
		photos.addAll( value );
	}
	
	/** 获取图片List */
	public ArrayList< String > getPhotoList( )
	{
		if ( photos == null )
		{
			photos = new ArrayList< String >( );
		}
		return photos;
	}
	
	
	/** 设置地址 */
	public void setAddress( String value )
	{
		address = value;
	}
	
	/** 获取地址 */
	public String getAddress( )
	{
		return address == null ? "" : address;
	}
	
	/** 设置手机号码 */
	public void setPhone( String value )
	{
		phone = value;
	}
	
	/** 获取手机号码 */
	public String getPhone( )
	{
		return phone == null ? "" : phone;
	}
	
	/** 设置参加费用 */
	public void setCost( String value )
	{
		cost = value;
	}
	
	/** 获取参加费用 */
	public String getCost( )
	{
		return cost == null ? "" : cost;
	}
	
	/** 设置圈Id */
	public void setGroupid( String value )
	{
		groupid = value;
	}
	
	/** 获取圈ID用 */
	public String getGroupid( )
	{
		return groupid;
	}
	
	
}
