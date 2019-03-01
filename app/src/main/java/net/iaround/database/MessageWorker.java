
package net.iaround.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import net.iaround.model.im.TransportMessage;
import net.iaround.tools.CommonFunction;


/**
 * 消息操作实例（私信，评论，通知）
 * 
 * @author linyg
 * 
 */
public class MessageWorker extends ITableWorker
{
	private static final String TB_NAME = "tb_message"; // 表名
	public static final String ID = "id"; // 本地id
	public static final String TYPE = "type"; // 消息类型id
	public static final String M_UID = "m_uid"; // 所属空间的uid
	public static final String M_MBODY = "m_mbody"; // 消息体
	public static final String M_STATUS = "m_status"; // 消息状态 4
	
	public static final String[ ] selectors =
		{ ID , TYPE , M_UID , M_MBODY , M_STATUS };
	
	/** 消息表创建sql **/
	public static final String tableSql = "CREATE TABLE IF NOT EXISTS " + TB_NAME + " ( " + ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + TYPE + " INTEGER, " + M_UID
			+ " VERCHAR(15)," + M_MBODY + " TEXT," + M_STATUS + " CHAR(1));";
	
	protected MessageWorker(Context context )
	{
		super( context , ID , TB_NAME );
	}
	
	/**
	 * 按照分页的模式查询
	 * 
	 * @param uid
	 * @param methodid
	 * @param start
	 * @param amount
	 * @return
	 * @time 2011-6-24 下午01:21:05
	 * @author:linyg
	 */
	public Cursor onSelectPage(String uid , int methodid , int start , int amount )
	{
		String where = M_UID + " = " + uid + " AND " + TYPE + " = " + methodid + " ORDER BY "
				+ M_STATUS + " ASC, " + ID + " DESC LIMIT " + start + "," + amount;
		Cursor cursor = onSelect( selectors , where );
		return cursor;
	}
	
	/**
	 * 统计相应methodId的未读消息数量
	 * 
	 * @param uid
	 * @param methodId
	 * @return
	 * @time 2011-6-20 上午11:38:35
	 * @author:linyg
	 */
	public int countMethodid(String uid , int methodId , int status )
	{
		int count = 0;
		String where = M_UID + " = " + uid + " AND " + TYPE + " = " + methodId + " AND "
				+ M_STATUS + " = " + status;
		String[ ] strs =
			{ "count(*)" };
		Cursor cursor = onSelect( strs , where );
		cursor.moveToFirst( );
		try
		{
			if ( !cursor.isAfterLast( ) && ( cursor.getString( 0 ) != null ) )
			{
				count = cursor.getInt( 0 );
			}
		}
		catch ( Exception e )
		{
		}
		finally
		{
			if ( cursor != null )
				cursor.close( );
		}
		CommonFunction.log( "************cursor" , "methodId:" + count );
		return count;
	}
	
	/**
	 * 一种类型的消息数量
	 * 
	 * @param uid
	 * @param methodId
	 * @param status
	 * @return
	 * @time 2011-6-29 下午02:17:18
	 * @author:linyg
	 */
	public int countMethodid(String uid , int methodId )
	{
		int count = 0;
		String where = M_UID + " = " + uid + " AND " + TYPE + " = " + methodId;
		String[ ] strs =
			{ "count(*)" };
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
		}
		finally
		{
			if ( cursor != null )
				cursor.close( );
		}
		return count;
	}
	
	/**
	 * 插入消息
	 * 
	 * @param msg
	 * @param uid
	 * @param status
	 * @return
	 * @time 2011-7-18 上午11:38:59
	 * @author:linyg
	 */
	public long onInsert(TransportMessage msg , String uid , int status )
	{
		ContentValues values = new ContentValues( );
		values.put( M_UID , uid );
		values.put( TYPE , msg.getMethodId( ) );
		values.put( M_MBODY , msg.getContentBody( ) );
		values.put( M_STATUS , status );
		return onInsert( values );
	}
	
	/**
	 * 更改消息状态
	 * 
	 * @param ids
	 * @time 2011-6-20 下午03:31:29
	 * @author:linyg
	 */
	public void onModifyStatus( String ids )
	{
		String where = ID + " IN (" + ids + " )";
		onExcute( "UPDATE " + TB_NAME + " SET " + M_STATUS + " = 1 WHERE " + where );
	}
}
