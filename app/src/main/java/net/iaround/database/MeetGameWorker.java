
package net.iaround.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;


/**
 * 关键字表
 * 
 * @author Administrator
 * 
 */
public class MeetGameWorker extends ITableWorker
{
	public static final String TB_NAME = "tb_meetgame"; // 表名
	public static final String ID = "id"; // 本地id 0
	public static final String M_UID = "uid"; // 当前登录者ID
	public static final String M_MID = "mid"; // 相见人的id
	public static final String M_UPDATETIME = "updatetime"; // 更新时间
	public static final String M_HASMEETED = "hasmeeted"; // 是否读取
	public static final String M_CONTENT = "content"; // json
	
	
	public static final String[ ] selectors =
		{ ID , M_UID , M_MID , M_UPDATETIME , M_HASMEETED , M_CONTENT };
	
	/** 关键字表创建sql **/
	public static final String tableSql = "CREATE TABLE IF NOT EXISTS " + TB_NAME + " ( " + ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + M_UID + " INTEGER, " + M_MID
			+ " INTEGER, " + M_UPDATETIME + " INTEGER, " + M_HASMEETED + " CHAR(1), "
			+ M_CONTENT + " TEXT );";
	public static final String deleteAll = "DELETE FROM TB_NAME"; // 清楚本地数据
	
	protected MeetGameWorker(Context context )
	{
		super( context , ID , TB_NAME );
	}
	
	/**
	 * 插入数据
	 * 
	 * @param uid
	 *            用户id
	 * @param content
	 */
	public long insetMeetData(long uid , long muid , String content , long time )
	{
		ContentValues values = new ContentValues( );
		values.put( M_UID , uid );
		values.put( M_MID , muid );
		values.put( M_UPDATETIME , time );
		values.put( M_HASMEETED , 0 );
		values.put( M_CONTENT , content );
		return onInsert( values );
	}
	
	/**
	 * 移除登录者所有的邂逅数据
	 * 
	 * @param uid
	 */
	public int removeAll( long uid )
	{
		String where = M_UID + " = " + uid;
		return delete( where );
	}
	
	/**
	 * 更新邂逅的读取状态
	 * 
	 * @param uid
	 * @param muid
	 * @return
	 */
	public int updateMeetData( long uid , long muid )
	{
		String where = M_UID + " = " + uid + " AND " + M_MID + " = " + muid;
		ContentValues values = new ContentValues( );
		values.put( M_HASMEETED , 1 );
		return onUpdate( values , where );
	}
	
	/**
	 * 查找返回所有的未读取数据
	 * 
	 * @param uid
	 *            登录者uid
	 * @param status
	 *            1是已看，0是未看
	 * @return
	 */
	public Cursor selectData(long uid , int status )
	{
		String where = M_UID + " = " + uid + " AND " + M_HASMEETED + " = " + status;
		return onSelect( selectors , where );
	}
}
