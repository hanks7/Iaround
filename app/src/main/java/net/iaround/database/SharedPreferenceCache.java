
package net.iaround.database;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import net.iaround.tools.AES;


/**
 * 以shareprefence方式存储数据
 * 
 * @author shifengxiong
 * 
 */
public class SharedPreferenceCache
{
	
	public static final String TOPIC_CACHED = "topiclist" ; //第一页缓存的话题数据
	public static final String TOPIC_SENDING_LIST = "topic_list_send" ; //正在发送中或发送失败的话题列表
	public static final String TOPIC_HOT_LIST = "topic_list_hot" ;//热门话题列表数据
	
	public static final String USER_RECOMMEND ="user_recommend"; //推荐用户
	public static final String GROUP_RECOMMEND ="group_recommend"; //推荐圈子
	
	public final static String DYNAMIC_LIKE_FAIL = "dynamic_like_fail";//动态点赞失败数据
	//是否开启不再提示顶部黄色温馨提示
	public final static String RECOMMEND_GIFTS_ISCHECKTIPSOFF = "recommend_gifts_ischecktipsoff";
	public final static String RECOMMEND_GIFTS_DATA = "recommend_gifts_data";

	private static SharedPreferenceCache sharedPreferenceCache;
	private static SharedPreferences sharedPreferences;
	private String SAVE_PUBLIC_KEY = "snPE6LHB8S8GLN96";
	
	private final static String KEY = "cache_sharepreferences";
	
	public final static String LOGIN_SUCCESS_DATA = "success_login_data" ;
	
	
	/** Deprecated End **/
	
	
	
	private SharedPreferenceCache(Context context )
	{
		sharedPreferences = context.getSharedPreferences( KEY , Context.MODE_PRIVATE );
	}
	
	public static SharedPreferenceCache getInstance(Context context )
	{
		if ( sharedPreferenceCache == null )
		{
			sharedPreferenceCache = new SharedPreferenceCache( context );
		}
		return sharedPreferenceCache;
	}
	
	/**
	 * 设置String类型值
	 * 
	 * @param key
	 * @param value
	 * @time 2011-5-30 上午09:26:38
	 * @author:linyg
	 */
	public void putString(String key , String value )
	{
		Editor editor = sharedPreferences.edit( );
		editor.putString( key , value );
		editor.commit( );
	}
	
	/**
	 * 设置long类型值
	 * 
	 * @param key
	 * @param value
	 * @time 2011-5-30 上午09:28:25
	 * @author:linyg
	 */
	public void putLong(String key , long value )
	{
		Editor editor = sharedPreferences.edit( );
		editor.putLong( key , value );
		editor.commit( );
	}
	
	/**
	 * 设置int类型值
	 * 
	 * @param key
	 * @param value
	 * @time 2011-5-30 上午09:30:06
	 * @author:linyg
	 */
	public void putInt(String key , int value )
	{
		Editor editor = sharedPreferences.edit( );
		editor.putInt( key , value );
		editor.commit( );
	}
	
	/**
	 * 设置Boolean类型值
	 * 
	 * @param key
	 * @param value
	 * @time 2011-5-30 上午09:30:56
	 * @author:linyg
	 */
	public void putBoolean(String key , boolean value )
	{
		Editor editor = sharedPreferences.edit( );
		editor.putBoolean( key , value );
		editor.commit( );
	}
	
	/**
	 * 设置Float类型值
	 * 
	 * @param key
	 * @param value
	 * @time 2011-5-30 上午09:31:42
	 * @author:linyg
	 */
	public void putFloat(String key , float value )
	{
		Editor editor = sharedPreferences.edit( );
		editor.putFloat( key , value );
		editor.commit( );
	}
	
	/**
	 * 获取key相对应的value，如果不设默认参数，默认值为""
	 * 
	 * @param key
	 * @return
	 */
	public String getString(String key )
	{
		return getString( key , "" );
	}
	
	/**
	 * 获取key相对应的value，如果不设默认参数，默认值为""
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getString(String key , String defaultValue )
	{
		if(sharedPreferences!=null) {
			return sharedPreferences.getString(key, defaultValue);
		}
		return null;
	}
	
	/**
	 * 获取key相对应的value，如果不设默认参数，默认值为false
	 * 
	 * @param key
	 * @return
	 */
	public boolean getBoolean( String key )
	{
		return getBoolean( key , false );
	}
	
	/**
	 * 获取key相对应的value，如果不设默认参数，默认值为false
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public boolean getBoolean(String key , boolean defaultValue )
	{
		return sharedPreferences.getBoolean( key , defaultValue );
	}
	
	/**
	 * 获取key相对应的value，如果不设默认参数，默认值为0
	 * 
	 * @param key
	 * @return
	 */
	public int getInt( String key )
	{
		return getInt( key , 0 );
	}
	
	/**
	 * 获取key相对应的value，如果不设默认参数，默认值为0
	 * 
	 * @param key
	 * @return
	 */
	public int getInt(String key , int defaultValue )
	{
		return sharedPreferences.getInt( key , defaultValue );
	}
	
	/**
	 * 获取key相对应的value，如果不设默认参数，默认值为0
	 * 
	 * @param key
	 * @return
	 */
	public long getLong( String key )
	{
		return getLong( key , 0L );
	}
	
	/**
	 * 获取key相对应的value，如果不设默认参数，默认值为0
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public long getLong(String key , Long defaultValue )
	{
		return sharedPreferences.getLong( key , defaultValue );
	}
	
	/**
	 * 获取key相对应的value，如果不设默认参数，默认值为0
	 * 
	 * @param key
	 * @return
	 */
	public float getFloat( String key )
	{
		return getFloat( key , 0f );
	}
	
	/**
	 * 获取key相对应的value，如果不设默认参数，默认值为0
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public float getFloat(String key , Float defaultValue )
	{
		return sharedPreferences.getFloat( key , defaultValue );
	}
	
	/** 判断是否存在此字段 */
	public boolean contains( String key )
	{
		return sharedPreferences.contains( key );
	}
	
	/** 判断是否存在此字段 */
	public boolean has( String key )
	{
		return sharedPreferences.contains( key );
	}
	
	/** 删除sharedPreferences文件中对应的Key和value */
	public boolean remove( String key )
	{
		Editor editor = sharedPreferences.edit( );
		editor.remove( key );
		return editor.commit( );
	}
	
	/**
	 * 通过加密保存
	 * 
	 * @param key
	 * @param value
	 */
	public void setEncodeByAES(String key , String value )
	{
		try
		{
			putString( key , AES.encode( value , SAVE_PUBLIC_KEY ) );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
	}
	
	/**
	 * 通过加密的方式保存
	 * 
	 * @param key
	 * @return
	 */
	public String getDecodeByAES(String key )
	{
		if ( has( key ) )
		{
			try
			{
				return AES.decode( getString( key ) , SAVE_PUBLIC_KEY );
			}
			catch ( Exception e )
			{
				e.printStackTrace( );
			}
		}
		return "";
	}
	
	
	 
}
