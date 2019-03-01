
package net.iaround.model.im;

import net.iaround.tools.CommonFunction;
import net.iaround.tools.Hashon;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;


/**
 * 个人资料等中的设备信息数据体
 * 
 * @author 余勋杰
 */
public class Device implements Serializable
{
	private static final long serialVersionUID = 7080737532307304905L;
	private String[ ] manufacturer; // 已解析过的国际化产商名称，{英语，简体中文，繁体中文}
	private String logo; // 商标地址
	private String model; // 型号
	
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
					
					if ( value.equals( manufacturer ) )
					{
						value = Arrays.asList( manufacturer );
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
	
	public String getManufacturer(int index )
	{
		if ( manufacturer == null || manufacturer.length <= 0 )
		{
			return null;
		}
		if ( index < 0 || index >= manufacturer.length )
		{
			return manufacturer[ 0 ];
		}
		return manufacturer[ index ];
	}
	
	public void setManufacturer( String manufacturer )
	{
		this.manufacturer = manufacturer.split( "," );
	}
	
	public String getLogo( )
	{
		return logo;
	}
	
	public void setLogo( String icon )
	{
		this.logo = icon;
	}
	
	public String getModel( )
	{
		return model;
	}
	
	public void setModel( String model )
	{
		this.model = model;
	}
	
}
