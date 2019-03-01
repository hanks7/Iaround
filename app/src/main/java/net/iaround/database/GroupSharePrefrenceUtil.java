
package net.iaround.database;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


/**
 * @ClassName: GroupSharePrefrenceUtil
 * @Description: 圈子相关缓存
 * @author zhonglong kylin17@foxmail.com
 * @date 2014-1-11 上午10:47:16
 * 
 */
public class GroupSharePrefrenceUtil
{
	
	/** 是否为本人新创建的圈子 */
	public static final String NEW_GROUP_KEY = "IAROUND_GROUP";
	
	/** 圈成员首次进入某个圈聊是否需要弹出圈子使用需知 */
	public static final String FIRST_ENTER_GROUP = "FIRST_ENTER_GROUP";
	
	public GroupSharePrefrenceUtil( )
	{
		
	}
	
	public void putString(Context context , String mainKey , String key , String value )
	{
		SharedPreferences mSharedPreferences = context.getSharedPreferences( mainKey ,
				Context.MODE_PRIVATE );
		if(null!=mSharedPreferences) {
			Editor editor = mSharedPreferences.edit();
			editor.putString(key, value);
			editor.commit();
		}
	}
	
	public String getString(Context context , String mainKey , String key )
	{
		SharedPreferences mSharedPreferences = context.getSharedPreferences( mainKey ,
				Context.MODE_PRIVATE );
		if(null!=mSharedPreferences) {
			return mSharedPreferences.getString(key, "");
		}
		return "";
	}
	
	public void putInt(Context context , String mainKey , String key , int value )
	{
		SharedPreferences mSharedPreferences = context.getSharedPreferences( mainKey ,
				Context.MODE_PRIVATE );
		if(null!=mSharedPreferences) {
			Editor editor = mSharedPreferences.edit();
			editor.putInt(key, value);
			editor.commit();
		}
	}
	
	public int getInt(Context context , String mainKey , String key )
	{
		SharedPreferences mSharedPreferences = context.getSharedPreferences( mainKey ,
				Context.MODE_PRIVATE );
		if(null!=mSharedPreferences) {
			return mSharedPreferences.getInt(key, -1);
		}
		return -1;
	}
	
	public void putBoolean(Context context , String mainKey , String key , boolean value )
	{
		SharedPreferences mSharedPreferences = context.getSharedPreferences( mainKey ,
				Context.MODE_PRIVATE );
		if(null!=mSharedPreferences) {
			Editor editor = mSharedPreferences.edit();
			editor.putBoolean(key, value);
			editor.commit();
		}
	}
	
	public boolean getBoolean(Context context , String mainKey , String key , boolean defValue )
	{
		SharedPreferences mSharedPreferences = context.getSharedPreferences( mainKey ,
				Context.MODE_PRIVATE );
		if(null!=mSharedPreferences){
			return mSharedPreferences.getBoolean( key , defValue );
		}
		return false;
	}
	
	
}
