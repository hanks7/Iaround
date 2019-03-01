
package net.iaround.conf;


import android.content.Context;

import net.iaround.tools.CommonFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * 本地java代码保存一份敏感字符，每份敏感字符都对应一个版本； 第一次软件安装之后都将导入到数据库，并作为修改，增加的副本；
 * 每次启动程序都将数据库的敏感字导出到内存中，便于录入时检查；
 * 
 * @author linyg
 * 
 */
public class KeyWord
{
	private List< String > keyList = new ArrayList< String >( );
	private List< String > keyRankList = new ArrayList< String >( );
	
	private static KeyWord keyword;
	
	private static String[ ] emojiArray;
	private static Random random;
	
	private KeyWord(Context context )
	{
		emojiArray = new String[ ]
			{ "o_O" , "T_T" , "^_^" , ">_<" , "::>_<::" , "^0^" , "\\^0^/" , "Orz" };
		random = new Random( );
	}
	
	public static KeyWord getInstance(Context context )
	{
		if ( keyword == null )
		{
			keyword = new KeyWord( context );
		}
		return keyword;
	}
	
	/**
	 * 是否包含在关键词过滤里面
	 * 
	 * @param keyword
	 * @return
	 * @time 2011-7-27 上午10:36:17
	 * @author:linyg
	 */
	public String filterKeyWord( Context context , String keyword )
	{
		try
		{
			if ( keyList.size( ) == 0 )
			{
				exportKey( context );
			}
			CommonFunction.log( "keyword:" + keyList.size( ) , keyword );
			for ( String key : keyList )
			{
				if ( keyword.contains( key ) )
				{
					return key;
				}
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
		return "";
	}
	
	/**
	 * 过滤关键字并替换为 Emoji表情
	 * 
	 * @author huyunfeng E-mail:my519820363@gmail.com
	 * @version CreateTime：2013- 3-27 上午11:40:46
	 */
	public String filterKeyWordAndReplaceEmoji( Context context , String word )
	{
		try
		{
			if ( keyList.size( ) == 0 )
			{
				exportKey( context );
			}
			
			StringBuilder sb = new StringBuilder( word );// 基于StringBuilder实现，效率高


			for ( String key : keyRankList )
			{
			  if( replaceAllEmojiOnStringBuilder( sb , key ,
					emojiArray[ random.nextInt( emojiArray.length ) ] ))
			  {
				  return sb.toString( );
			  }
			}

			for ( String key : keyList )
			{
				replaceAllBaseOnStringBuilder( sb, key,
					emojiArray[ random.nextInt( emojiArray.length ) ] );
			}

			return sb.toString( );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
			return "";
		}
	}
	
	/**
	 * 将srcSb字符流中的指定oldStr字符串替换为新的newStr字符串
	 * 
	 * @author huyunfeng E -mail:my519820363@gmail.com
	 * @version CreateTime：2013- 3-27 下午12:04:30
	 */
	private static void replaceAllBaseOnStringBuilder( StringBuilder srcSb , String oldStr ,
			String newStr )
	{
		int i = srcSb.indexOf( oldStr );
		int oldLen = oldStr.length( );
		int newLen = newStr.length( );
		while ( i > -1 )
		{
			srcSb.delete( i, i + oldLen );
			srcSb.insert( i, newStr );
			i = srcSb.indexOf( oldStr , i + newLen );
		}
	}
/*
	如果包含等级为1 的关键字，整句和谐
 */
	private static boolean replaceAllEmojiOnStringBuilder( StringBuilder srcSb , String oldStr ,
		String newStr )
	{
		int i = srcSb.indexOf( oldStr );
		int oldLen = oldStr.length( );
		int newLen = newStr.length( );
		while ( i > -1 )
		{
			srcSb.delete( 0, oldLen );
			srcSb.replace( 0, srcSb.length(),newStr );
			return  true;
		}
		return false;
	}
	
	/**
	 * 返回原始关键字
	 * 
	 * @return
	 * @time 2011-7-27 下午01:01:33
	 * @author:linyg
	 */
	public List< String > getKeyList( )
	{
		return keyList;
	}
	
	/**
	 * 设置新的关键字列表
	 * 
	 * @param newList
	 * @time 2011-7-27 下午01:38:44
	 * @author:linyg
	 */
	public void setMap( List< String > newList )
	{
		if ( keyList != null )
		{
			keyList.clear( );
		}
		keyList.addAll( newList );
	}

	public void setRankMap( List< String > newList )
	{
		if ( keyRankList != null )
		{
			keyRankList.clear( );
		}
		keyRankList.addAll( newList );
	}

	// 添加关键字
	public void modifyKeyword( Context context , String value ,int rank )
	{
		try
		{
			if ( keyList.size( ) == 0 )
			{
				exportKey( context );
			}
			removeKeyword( value );

			if(rank !=0)
			{
				if ( !keyRankList.contains( value ) )
				{
					keyRankList.add( value );
				}
			}
			else
			{
				if ( !keyList.contains( value ) )
				{
					keyList.add( value );
				}
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
	}

	// 添加关键字
	public void addKeyword( Context context , String value ,int rank )
	{
		try
		{
			if ( keyList.size( ) == 0 )
			{
				exportKey( context );
			}

			if(rank !=0)
			{
				if ( !keyRankList.contains( value ) )
				{
					keyRankList.add( value );
				}
			}
			else
			{
				if ( !keyList.contains( value ) )
				{
					keyList.add( value );
				}
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
	}
	
	// 删除关键字
	public void removeKeyword( String value )
	{
		try
		{
			if ( keyList.contains( value ) )
			{
				keyList.remove( value );
			}
			if ( keyRankList.contains( value ) )
			{
				keyRankList.remove( value );
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
	}
	
	private void exportKey( Context context )
	{
	}
	
	//
	public void reset( )
	{
		keyword = null;
	}
}
