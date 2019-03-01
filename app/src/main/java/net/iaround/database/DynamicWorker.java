
package net.iaround.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;


/**
 * 动态表，暂时只保存sorttype为2（关于我的）的记录
 * */
public class DynamicWorker extends ITableWorker
{
	
	public static final String TB_NAME = "tb_dynamic"; // 表名
	public static final String ID = "id"; // 本地id 0
	public static final String MUID = "muid"; // 所属id
	public static final String FUID = "fuid"; // 好友uid
	public static final String SORT_TYPE = "sorttype";// 动态信息类型,1-来访记录,2-有关于我的,3-关于别人的,4-所有来访记录
	public static final String SUB_TYPE = "subtype";// “关于我的”类型,5-照片被赞,6-照片被评论,7-评论被回复
	public static final String UNREAD = "unread"; // 未读数
	public static final String FUSER = "fuser"; // 好友json字符串
	
	
	/** 消息表创建sql **/
	public static final String tableSql = "CREATE TABLE IF NOT EXISTS " + TB_NAME + " ( " + ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + MUID + " VERCHAR(15)," + FUID
			+ " VERCHAR(15)," + SORT_TYPE + " INTEGER," + SUB_TYPE + " INTEGER," + UNREAD
			+ " INTEGER," + FUSER + " TEXT);";
	
	public static final String[ ] selectors =
		{ ID , MUID , FUID , SORT_TYPE , SUB_TYPE , UNREAD , FUSER };
	
	protected DynamicWorker(Context context )
	{
		super( context , ID , TB_NAME );
	}
	
	
	/** ------------ 增 ------------ **/
	
	/**
	 * 插入一条数据
	 * */
	public long insertOneRecord(String muid , String fuid , int sorttype , int subtype ,
								int unread , String fuser )
	{
		ContentValues values = new ContentValues( );
		values.put( MUID , muid );
		values.put( FUID , fuid );
		values.put( SORT_TYPE , sorttype );
		values.put( SUB_TYPE , subtype );
		values.put( UNREAD , unread );
		values.put( FUSER , fuser );
		
		return onInsert( values );
	}
	
	
	
	/** ------------ 删 ------------ **/
	
	/**
	 * 删除记录
	 * 
	 * @param mUid
	 * @param fUid
	 * @return
	 */
	public int deleteRecord(String mUid , String fUid )
	{
		String where = MUID + " = " + mUid + " AND " + FUID + " = " + fUid;
		return delete( where );
	}
	
	/**
	 * 删除所有消息
	 * 
	 * @param uid
	 */
	public int deleteAll( String uid )
	{
		String where = MUID + " = " + uid;
		return delete( where );
	}
	
	
	/** ------------ 改 ------------ **/
	
	/**
	 * 修改“关于我的”未读数，用户json字符串和最后下发的子类型
	 * */
	public void updateUnread(String muid , String fuid , String fuser , int subtype , int unread )
	{
		String where = MUID + " = " + muid + " AND " + FUID + " = " + fuid + " AND " + SORT_TYPE
				+ " =2 ";
		ContentValues values = new ContentValues( );
		values.put( FUSER , fuser );
		values.put( SUB_TYPE , subtype );
		values.put( UNREAD , unread );
		onUpdate( values , where );
	}
	
	
	
	/** ------------ 查 ------------ **/
	
	/**
	 * 查找某用户发来的“关于我的”未读信息数
	 * */
	public int countOneUserUnread(String muid , String fuid )
	{
		int count = 0;
		String where = MUID + " = " + muid + " AND " + FUID + " = " + fuid + " AND " + SORT_TYPE
				+ " = 2 ";
		String[ ] strs =
			{ "SUM(" + UNREAD + ")" };
		Cursor cursor = onSelect( strs , where );
		try
		{
			cursor.moveToFirst( );
			if ( !cursor.isAfterLast( ) && ( cursor.getString( 0 ) != null ) )
			{
				count = cursor.getInt( 0 );
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
		finally
		{
			if ( cursor != null )
				cursor.close( );
		}
		return count;
	}
	
	
	
	/**
	 * 查看用户“关于我的”未读动态总数
	 * */
	public int countAllUnread( String muid )
	{
		int count = 0;
		String where = MUID + " = " + muid + " AND " + SORT_TYPE + " = 2 ";
		String[ ] strs =
			{ "SUM(" + UNREAD + ")" };
		Cursor cursor = onSelect( strs , where );
		try
		{
			cursor.moveToFirst( );
			if ( !cursor.isAfterLast( ) && ( cursor.getString( 0 ) != null ) )
			{
				count = cursor.getInt( 0 );
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
		finally
		{
			if ( cursor != null )
				cursor.close( );
		}
		return count;
	}
	
	/**
	 * 查看“我的动态”来自多少用户
	 * */
	public int countMyDynamicSender( String muid )
	{
		int count = 0;
		String where = MUID + " = " + muid + " AND " + SORT_TYPE + " = 2 " + " AND " + UNREAD
				+ " > 0 ";
		String[ ] strs =
			{ UNREAD };
		Cursor cursor = onSelect( strs , where );
		try
		{
			count = cursor.getCount( );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
		finally
		{
			if ( cursor != null )
				cursor.close( );
		}
		
		return count;
	}
	
	/**
	 * 查看“我的动态”和私聊共来自多上用户
	 * */
	public int countUnionChatMyDynamicSender( String muid )
	{
		String[ ] selectionArgs = { };
		String sql = "select tb_near_contact.fuid from tb_near_contact where muid = " + muid
				+ " and tb_near_contact.none_read > 0 union"
				+ " select tb_dynamic.fuid from tb_dynamic where muid = " + muid
				+ " and tb_dynamic.unread > 0;";
		Cursor c = onSelectBySql( selectionArgs , sql );
		return c.getCount( );
		
		
	}
}
