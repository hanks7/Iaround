
package net.iaround.model.entity;


import android.text.TextUtils;


import net.iaround.share.utils.Hashon;
import net.iaround.tools.CommonFunction;

import java.lang.reflect.Field;
import java.util.HashMap;


public class GeoData
{
	private int locusId;
	private long updateTime;
	private int lat; // 纬度	39.389885转化为39389885
	private int lng; // 经度
	private String address = ""; // 该经纬度对应的地址
	private String simpleAddress = ""; // 该经纬度对应的简化地址
	private String shortAddress ="";
	private long currentTime; // 更新的最新时间(毫秒)
	private boolean isoffset = false; // 是否校正过
	
	private String country="";
	private String province="";
	private String city="";
	private String region="";
	private String name="";
	
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
	
	public boolean isIsoffset( )
	{
		return isoffset;
	}
	
	public void setIsoffset( boolean isoffset )
	{
		this.isoffset = isoffset;
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
	
	public String getAddress( )
	{
		return address;
	}
	
	public void setAddress( String address )
	{
		this.address = address;
	}
	
	public int getLocusId( )
	{
		return locusId;
	}
	
	public void setLocusId( int locusId )
	{
		this.locusId = locusId;
	}
	
	public long getUpdateTime( )
	{
		return updateTime;
	}
	
	public void setUpdateTime( long updateTime )
	{
		this.updateTime = updateTime;
	}
	
	public boolean equals( Object o )
	{
		if ( o instanceof GeoData )
		{
			GeoData gd = ( GeoData ) o;
			return ( locusId == gd.getLocusId( ) && updateTime == gd.getUpdateTime( )
					&& lat == gd.getLat( ) && lng == gd.getLng( ) && address.equals( gd
					.getAddress( ) ) );
		}
		return false;
	}
	
	public long getCurrentTime( )
	{
		return currentTime;
	}
	
	public void setCurrentTime( long currentTime )
	{
		this.currentTime = currentTime;
	}
	
	public String getSimpleAddress( )
	{
		if(TextUtils.isEmpty( simpleAddress ))
		{
			simpleAddress = getCity( ) + getName( );
		}
		return simpleAddress;
	}
	
	public void setSimpleAddress( String simpleAddress )
	{
		this.simpleAddress = simpleAddress;
	}
	
	public void setShortAddress( String simpleAddress )
	{
		this.simpleAddress = simpleAddress;
	}
	
	public String getShortAddress( )
	{
		return simpleAddress;
	}
	
	public String getProvince()
	{
		if(!TextUtils.isEmpty( city )&&!TextUtils.isEmpty( province ))
		{
			if(city.contains( province ))
			{
				province = "";
			}
		}
		
		return province;
	}
	
	public void setProvince(String strprovince)
	{
		province =strprovince;
	}

	public void setCountry(String country)
	{
		this.country = country;
	}
	public String getCountry()
	{
		return country;
	}

	public void setCity(String strCity)
	{
		city =strCity;

		if(!TextUtils.isEmpty( city ))
		{
			city = city.replace( "市","" );
		}


	}
	
	public String getCity()
	{
		return city;
	}
	
	public String getRegion()
	{
		return region ;
	}
	
	public void setRegion(String strRegion)
	{
		region =strRegion;
	}
	
	public String getName()
	{
		return name ;
	}
	
	public void setName(String strName)
	{
		name =strName;
	}
}
