
package net.iaround.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


/**
 * 聊吧浏览记录
 * 
 * @author linyg
 * 
 */
public class GroupHistoryWorker extends ITableWorker
{
	public static final String TB_NAME = "tb_group_history_order"; // 表名
	public static final String ID = "id"; // 本地id 0
	public static final String UID = "uid"; // 用户uid
	public static final String GROUPID = "groupid"; // 聊吧ID
	public static final String GROUPNAME = "groupname"; // 聊吧名字
	public static final String GROUPICON = "groupicon"; // 聊吧图标
	public static final String TIME = "time"; // 时间

	public static final String[ ] selectors =
		{ ID , UID , GROUPID , GROUPNAME , GROUPICON ,TIME};

	/** 表创建sql **/
	public static final String tableSql = "CREATE TABLE IF NOT EXISTS " + TB_NAME + " ( " + ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + UID + " VERCHAR(15), " + GROUPID
			+ " VERCHAR(15)," + GROUPNAME + " VERCHAR(50)," + TIME + " VERCHAR(50),"  + GROUPICON + " VERCHAR(100)" + ");";

	protected GroupHistoryWorker(Context context )
	{
		super( context , ID , TB_NAME );
	}
	
	
	
	/**------------ 增 ------------**/

	/**
	 * 插入访问聊吧
	 * @param uid
	 * @param groupId
	 * @param groupName
	 * @param groupIcon
	 * @return
	 */
	public long insetOrder( long uid , long groupId, String groupName, String groupIcon ,long time)
	{
		ContentValues values = new ContentValues( );
		values.put( UID , uid );
		values.put( GROUPID , groupId );
		values.put( GROUPNAME , groupName );
		values.put( GROUPICON , groupIcon );
		values.put( TIME , time );
		return onInsert( values );
	}


	
	
	/**------------ 删 ------------**/
	
	/**
	 * 删除某个聊吧信息
	 * 
	 * @param uid
	 * @param groupId
	 */
	public void delete(long uid, long groupId )
	{
		SQLiteDatabase db = getDb( );
		db.execSQL( "DELETE FROM " + TB_NAME + " WHERE " + UID + " = " + uid  + "AND" + GROUPID + " = " + groupId );
	}
	
	
	/**------------ 改 ------------**/
	
	
	
	
	/**------------ 查 ------------**/

	/**
	 * 查询历史聊吧
	 * @param uid
	 * @return
	 */
	public Cursor onSelectPage(long uid)
	{
		String where = UID + " = " + uid;
		Cursor cursor = onSelect( selectors , where );
		return cursor;
	}

	/**
	 * 删除旧数据
	 */
	public void deleteUsed(int max){
		String where= "delete from "+TB_NAME+" where id < "+max;
		SQLiteDatabase db = getDb( );
		db.execSQL(where);
	}


	/**
	 * 查询某个数据
	 */
	public Cursor selectObj(long uid, long groupId){
//		String where= "SELECT FROM " + TB_NAME + " WHERE "+UID + " = " + uid  + "AND" + GROUPID + " = " + groupId;
		String where= UID + " = " + uid  + " AND " + GROUPID + " = " + groupId;
		Cursor cursor = onSelect( selectors , where );
		return cursor;
	}

	/**
	 * 更新旧数据
	 */
	public long update(long uid, long groupId ,long time){
		String where = UID + " = " + uid  + " AND " + GROUPID + " = " + groupId;
		ContentValues values = new ContentValues( );
		values.put( TIME , time );
		return onUpdate( values , where );
	}

}
