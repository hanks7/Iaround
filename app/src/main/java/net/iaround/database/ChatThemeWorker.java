
package net.iaround.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;


/**
 * 聊天场景记录表
 * 
 * @author linyg
 * 5.7废弃
 */
@Deprecated
public class ChatThemeWorker extends ITableWorker
{
	public static final String TB_NAME = "tb_chattheme"; // 聊天场景记录
	public static final String ID = "id"; // id 0
	public static final String MUID = "muid"; // 登陆者uid
	public static final String FUID = "fuid"; // 好友id
	public static final String THEME = "theme"; // 场景id
	public static final String SENDER = "sender"; // 发送者 1为登陆者发，2为好友发
	public static final String EXPIRE = "expire"; // 是否过期
	public static final String[ ] selectors =
		{ ID , MUID , FUID , THEME , SENDER , EXPIRE };
	
	/** 关键字表创建sql **/
	public static final String tableSql = "CREATE TABLE IF NOT EXISTS " + TB_NAME + " ( " + ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + MUID + " INTEGER, " + FUID
			+ " INTEGER, " + THEME + " INTEGER, " + EXPIRE + " INTEGER, " + SENDER
			+ " INTEGER);";
	
	protected ChatThemeWorker(Context context )
	{
		super( context , ID , TB_NAME );
	}
	
	/**
	 * 添加好友之间的聊天场景
	 * 
	 * @param context
	 * @param mUid
	 * @param fUid
	 * @param themeId
	 * @param sender
	 *            主动更换 1为登陆者，2为对方
	 * @return
	 */
	public long addTheme(Context context , long mUid , long fUid , int themeId , int sender )
	{
		String where = MUID + " = " + mUid + " AND " + FUID + " = " + fUid;
		Cursor cursor = onSelect( selectors , where );
		// Date d = new Date();
		// Long nowTime = Common.getInstance().serverToClientTime + d.getTime();
		if ( cursor != null && cursor.getCount( ) > 0 )
		{
			cursor.close( );
			// 更新
			ContentValues values = new ContentValues( );
			values.put( THEME , themeId );
			values.put( SENDER , sender );
			values.put( EXPIRE , 0 );
			return onUpdate( values , where );
		}
		else
		{
			ContentValues values = new ContentValues( );
			values.put( MUID , mUid );
			values.put( FUID , fUid );
			values.put( THEME , themeId );
			values.put( SENDER , sender );
			values.put( EXPIRE , 0 );
			return onInsert( values );
		}
	}
	
	/**
	 * 查找两用户的聊天场景
	 * 
	 * @param context
	 * @param mUid
	 * @param fUid
	 * @return
	 */
	public Cursor onSelectTheme(Context context , long mUid , long fUid )
	{
		String where = MUID + " = " + mUid + " AND " + FUID + " = " + fUid + " AND " + EXPIRE
				+ " = 0 ";
		return onSelect( selectors , where );
	}
	
	/**
	 * 更新场景过期
	 * 
	 * @param mUid
	 * @param fUid
	 * @param expire
	 *            1过期
	 */
	public long updateExpire( long mUid , long fUid )
	{
		String where = MUID + " = " + mUid + " AND " + FUID + " = " + fUid;
		ContentValues values = new ContentValues( );
		values.put( EXPIRE , 1 );
		return onUpdate( values , where );
	}
}
