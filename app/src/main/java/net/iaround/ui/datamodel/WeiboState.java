
package net.iaround.ui.datamodel;



import net.iaround.analytics.enums.WeiboVerifiedType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.Hashon;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;


/**
 * 微博简要
 * 
 * @author 余勋杰
 */
public class WeiboState implements Serializable
{
	public enum WeiboType
	{
		SinaWeibo( 12 ) ,
		TencentWeibo( 1 ) ,
		Qzone( 25 ) ,
		Facebook( 24 ) ,
		Twitter( 23 );
		
		private int value = 0;
		
		WeiboType(int value)
		{
			this.value = value;
		}
		
		// public static WeiboType valueOf( int value )
		// {
		// switch ( value )
		// {
		// case 12 :
		// return SinaWeibo;
		// case 1 :
		// return TencentWeibo;
		// case 25 :
		// return Qzone;
		// case 24 :
		// return Facebook;
		// case 23 :
		// return Twitter;
		// }
		// return null;
		// }
		
		public int value( )
		{
			return this.value;
		}
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8592614216159819763L;
	/**
	 * QQ空间( 25 ) ,新浪微博( 12 ) , 腾讯微博( 1 ) , Facebook( 24 ) , Twitter( 23
	 * )，圈主（981），小辣椒（982），真心话大冒险（992）
	 * */
	private int type; // 微博类型，type的具体数值可通过不同微博的工具类获取，即是其ID
	private String id; // 微博id
	private String verifiedReason; // 加v理由
	private boolean registered; // 受否已经注册
	private String name; // 微博显示昵称
	private boolean authed; // 是否已经加v
	private String sign; // 微博签名
	private int verifiedType; // 认证类型：0-名人（黄v）；1-政府；2-企业（蓝v）；3-媒体；4-校园；5-网站；6-应用；7-团体（机构）；8-待审企业;
								// 200-待审核达人；220-通过审核的达人（红星）；400-已故V用户;-1:普通用户;
								// -100-没有微博
	private Object tag;
	private String gender;
	
	private String openid;
	private String accesstoken;
	private long expires;
	
	public WeiboState( )
	{
		verifiedType = -100;
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
					
					if ( value.equals( tag ) )
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
	
	public Object getTag( )
	{
		return tag;
	}
	
	public void setTag( Object tag )
	{
		this.tag = tag;
	}
	
	public int getVerifiedType( )
	{
		return verifiedType;
	}
	
	public void setVerifiedType( int verifiedType )
	{
		this.verifiedType = verifiedType;
	}
	
	public int getType( )
	{
		return type;
	}
	
	public void setType( int type )
	{
		this.type = type;
	}
	
	public String getVerifiedReason( )
	{
		return verifiedReason;
	}
	
	public String getId( )
	{
		return id;
	}
	
	public void setId( String id )
	{
		this.id = id;
	}
	
	public void setVerifiedReason( String verifiedResean )
	{
		this.verifiedReason = verifiedResean;
	}
	
	public boolean isRegistered( )
	{
		return registered;
	}
	
	public void setRegistered( boolean registered )
	{
		this.registered = registered;
	}
	
	public String getName( )
	{
		return name == null ? "" : name;
	}
	
	public void setName( String name )
	{
		this.name = name;
	}
	
	public boolean isAuthed( )
	{
		return authed;
	}
	
	public void setAuthed( boolean authed )
	{
		this.authed = authed;
	}
	
	public String getSign( )
	{
		return sign == null ? "" : sign;
	}
	
	public void setSign( String sign )
	{
		this.sign = sign;
	}
	
	public boolean equals( WeiboState weibo )
	{
		if ( !super.equals( weibo ) )
		{
			return false;
		}
		if ( type != weibo.type )
		{
			return false;
		}
		if ( registered != weibo.registered )
		{
			return false;
		}
		if ( name == null && weibo.name != null )
		{
			return false;
		}
		if ( name != null && !name.equals( weibo.name ) )
		{
			return false;
		}
		if ( authed != weibo.authed )
		{
			return false;
		}
		if ( sign == null && weibo.sign != null )
		{
			return false;
		}
        return !(sign != null && !sign.equals(weibo.sign));

    }
	
	public String getGender( )
	{
		return gender == null ? "all" : gender;
	}
	
	public void setGender( String gender )
	{
		this.gender = gender;
	}
	
	public int getVerifiedTypeEnum( )
	{
		switch ( verifiedType )
		{
			case 0 :
			case 400 :
				return WeiboVerifiedType.V_PERSON;
			case 1 :
			case 2 :
			case 3 :
			case 4 :
			case 5 :
			case 6 :
			case 7 :
			case 8 :
				return WeiboVerifiedType.V_ORGANIZATION;
			case 200 :
			case 220 :
				return WeiboVerifiedType.DAREN;
			case -1 :
				return WeiboVerifiedType.COMMON_USER;
		}
		return WeiboVerifiedType.UNKONOWN;
	}
	
	public String getOpenid( )
	{
		return openid;
	}
	
	public void setOpenid( String openid )
	{
		this.openid = openid;
	}
	
	public String getAccesstoken( )
	{
		return accesstoken;
	}
	
	public void setAccesstoken( String accesstoken )
	{
		this.accesstoken = accesstoken;
	}
	
	public long getExpires( )
	{
		return expires;
	}
	
	public void setExpires( long expires )
	{
		this.expires = expires;
	}
	
}
