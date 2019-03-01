
package net.iaround.ui.datamodel;


import net.iaround.tools.CommonFunction;
import net.iaround.tools.Hashon;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;


/**
 * 相片数据体
 * 
 * @author 余勋杰
 */
public class Photo implements Serializable
{
	private static final long serialVersionUID = -7352189061777698331L;
	private boolean dirty; // 数据体失效标记
	private String id; // 图片id
	private String uri; // 图片地址
	private String hdUrl; // hd图片地址
	private boolean hasHD; // 是否具备hd图片
	private String content;// 内容
	private int likecount;// 喜欢总数
	private int reviewcount;// 评论总数
	private int curruserlike;// 喜欢不喜欢
	
	public String getContent( )
	{
		return content;
	}
	
	public void setContent( String content )
	{
		this.content = content;
	}
	
	public int getLikecount( )
	{
		return likecount;
	}
	
	public void setLikecount( int likecount )
	{
		this.likecount = likecount;
	}
	
	public int getReviewcount( )
	{
		return reviewcount;
	}
	
	public void setReviewcount( int reviewcount )
	{
		this.reviewcount = reviewcount;
	}
	
	public int getCurruserlike( )
	{
		return curruserlike;
	}
	
	public void setCurruserlike( int curruserlike )
	{
		this.curruserlike = curruserlike;
	}
	
	public HashMap< String , Object > toMap( )
	{
		HashMap< String , Object > map = new HashMap< String , Object >( );
		try
		{
			Field[ ] flds = getClass( ).getDeclaredFields( );
			if ( flds != null )
			{
				for ( Field fld : flds )
				{
					fld.setAccessible( true );
					Object value = fld.get( this );
					if ( value == null )
					{
						continue;
					}
					map.put( fld.getName( ) , value );
				}
			}
			
			return map;
		}
		catch ( Throwable t )
		{
			CommonFunction.log( t );
		}
		
		return null;
	}
	
	public String toString( )
	{
		return new Hashon( ).fromHashMap( toMap( ) );
	}
	
	public String getHdUrl( )
	{
		return hdUrl;
	}
	
	public void setHdUrl( String hdUrl )
	{
		this.hdUrl = hdUrl;
	}
	
	public String getId( )
	{
		return id;
	}
	
	public void setId( String id )
	{
		this.id = id;
	}
	
	public String getUri( )
	{
		return uri;
	}
	
	public void setUri( String uri )
	{
		this.uri = uri;
	}
	
	public boolean isDirty( )
	{
		return dirty;
	}
	
	public void setDirty( )
	{
		this.dirty = true;
	}
	
	public void setHasHD( boolean hasHD )
	{
		this.hasHD = hasHD;
	}
	
	public boolean hasHD( )
	{
		return hasHD;
	}
	
	public boolean ensureHD( )
	{
		return hasHD && hdUrl != null && hdUrl.trim( ).length( ) > 0;
	}
}
