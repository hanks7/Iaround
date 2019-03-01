
package net.iaround.model.im;


import net.iaround.tools.CommonFunction;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;


/**
 * 私密资料
 * 
 * @author 余勋杰
 */
public class PrivacyInfor implements Serializable
{
	private static final long serialVersionUID = -9144048667944607759L;
	private int livingSituation;
	private int carSituation;
	private int dateSite;
	private int character;
	private int favoriteWine;
	private int smoking;
	private int consumption;
	private int shopping;
	private int pet;
	private int habitsOf;
	private int dressStyle;
	private int hopeTaAress;
	private int inTheEyesOfFriend;
	
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
	
	/** 输入协议顺序（网络使用） */
	public void set( String privacyInfo )
	{
		String[ ] privacyInfos = privacyInfo.split( "," );
		if ( privacyInfos != null && privacyInfos.length == 13 )
		{
			livingSituation = strToInt( privacyInfos[ 0 ] );
			carSituation = strToInt( privacyInfos[ 1 ] );
			dateSite = strToInt( privacyInfos[ 2 ] );
			character = strToInt( privacyInfos[ 3 ] );
			favoriteWine = strToInt( privacyInfos[ 4 ] );
			smoking = strToInt( privacyInfos[ 5 ] );
			consumption = strToInt( privacyInfos[ 6 ] );
			shopping = strToInt( privacyInfos[ 7 ] );
			pet = strToInt( privacyInfos[ 8 ] );
			habitsOf = strToInt( privacyInfos[ 9 ] );
			dressStyle = strToInt( privacyInfos[ 10 ] );
			hopeTaAress = strToInt( privacyInfos[ 11 ] );
			inTheEyesOfFriend = strToInt( privacyInfos[ 12 ] );
		}
	}
	
	/** 输出协议顺序（网络使用） */
	public String toString( )
	{
		StringBuilder sb = new StringBuilder( );
		sb.append( livingSituation ).append( ',' );
		sb.append( carSituation ).append( ',' );
		sb.append( dateSite ).append( ',' );
		sb.append( character ).append( ',' );
		sb.append( favoriteWine ).append( ',' );
		sb.append( smoking ).append( ',' );
		sb.append( consumption ).append( ',' );
		sb.append( shopping ).append( ',' );
		sb.append( pet ).append( ',' );
		sb.append( habitsOf ).append( ',' );
		sb.append( dressStyle ).append( ',' );
		sb.append( hopeTaAress ).append( ',' );
		sb.append( inTheEyesOfFriend );
		return sb.toString( );
	}
	
	/** 输入策划顺序（UI使用） */
	public void replace( int index , int value )
	{
		switch ( index )
		{
			case 0 :
				dateSite = value;
				break;
			case 1 :
				character = value;
				break;
			case 2 :
				favoriteWine = value;
				break;
			case 3 :
				smoking = value;
				break;
			case 4 :
				consumption = value;
				break;
			case 5 :
				shopping = value;
				break;
			case 6 :
				pet = value;
				break;
			case 7 :
				habitsOf = value;
				break;
			case 8 :
				dressStyle = value;
				break;
			case 9 :
				hopeTaAress = value;
				break;
			case 10 :
				inTheEyesOfFriend = value;
				break;
			case 11 :
				livingSituation = value;
				break;
			case 12 :
				carSituation = value;
				break;
		}
	}
	
	/** 输出策划顺序（UI使用） */
	public int get( int index )
	{
		switch ( index )
		{
			case 0 :
				return dateSite;
			case 1 :
				return character;
			case 2 :
				return favoriteWine;
			case 3 :
				return smoking;
			case 4 :
				return consumption;
			case 5 :
				return shopping;
			case 6 :
				return pet;
			case 7 :
				return habitsOf;
			case 8 :
				return dressStyle;
			case 9 :
				return hopeTaAress;
			case 10 :
				return inTheEyesOfFriend;
			case 11 :
				return livingSituation;
			case 12 :
				return carSituation;
		}
		return -1;
	}
	
	public int size( )
	{
		return 13;
	}
	
	public boolean isDefault( )
	{
		return ( dateSite == 0 ) && ( character == 0 ) && ( favoriteWine == 0 )
				&& ( smoking == 0 ) && ( consumption == 0 ) && ( shopping == 0 )
				&& ( pet == 0 ) && ( habitsOf == 0 ) && ( dressStyle == 0 )
				&& ( hopeTaAress == 0 ) && ( inTheEyesOfFriend == 0 )
				&& ( livingSituation == 0 ) && ( carSituation == 0 );
	}
	
	private int strToInt( String str )
	{
		try
		{
			return Integer.parseInt( str );
		}
		catch ( Throwable t )
		{
		}
		
		return 0;
	}
	
	public int getDateSite( )
	{
		return dateSite;
	}
	
	public int getCharacter( )
	{
		return character;
	}
	
	public int getFavoriteWine( )
	{
		return favoriteWine;
	}
	
	public int getSmoking( )
	{
		return smoking;
	}
	
	public int getConsumption( )
	{
		return consumption;
	}
	
	public int getShopping( )
	{
		return shopping;
	}
	
	public int getPet( )
	{
		return pet;
	}
	
	public int getHabitsOf( )
	{
		return habitsOf;
	}
	
	public int getDressStyle( )
	{
		return dressStyle;
	}
	
	public int getHopeTaAress( )
	{
		return hopeTaAress;
	}
	
	public int getInTheEyesOfFriend( )
	{
		return inTheEyesOfFriend;
	}
	
	public int getLivingSituation( )
	{
		return livingSituation;
	}
	
	public int getCarSituation( )
	{
		return carSituation;
	}
	
}
