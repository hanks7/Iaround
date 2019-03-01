
package net.iaround.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;


/**
 * @Description: 新增粉丝表
 * @author tanzy
 * @date 2015-4-16
 */
public class NewFansWorker extends ITableWorker
{
	
	public static final String TB_NAME = "tb_new_fans"; // 表名
	public static final String ID = "id"; // 本地id 0
	public static final String MUID = "muid"; // 所属id
	public static final String FUID = "fuid"; // 好友id
	public static final String FUINFO = "fuinfo"; // 好友资料，其资料实体的json字符串
	public static final String DATETIME = "datetime"; // 处理时间
	public static final String RELATION = "relation"; // 关系状态，0:自己 ，1：好友 ，2：陌生人，3、关注，4、粉丝
	public static final String UNREAD = "unread"; // 未读状态，1：未读，0：已读
	
	//************************注意***************************/
	// fuinfo里面也有relation字段，但是不可信，以表中的relation为准
	
	/** 消息表创建sql **/
	public static final String tableSql = "CREATE TABLE IF NOT EXISTS " + TB_NAME + " ( " + ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + MUID + " VERCHAR(15)," + FUID
			+ " VERCHAR(15)," + FUINFO + " TEXT, " + DATETIME + " VERCHAR(20)," + RELATION
			+ " INTEGER ," + UNREAD + " INTEGER " + ");";
	
	public static final String[ ] selectors =
		{ ID , MUID , FUID , FUINFO , DATETIME , RELATION , UNREAD };
	
	protected NewFansWorker(Context context )
	{
		super( context , ID , TB_NAME );
	}
	
	
	/************************ 增 **********************************/
	
	/** 插入一条数据 */
	public long insertOneRecord(long fuid , String fuinfo , long datetime , int relation , int unread , long muid)
	{
		ContentValues values = new ContentValues( );
		values.put( MUID , muid );
		values.put( FUID , fuid );
		values.put( FUINFO , fuinfo );
		values.put( DATETIME , datetime );
		values.put( RELATION , relation );
		values.put( UNREAD , unread );
		return onInsert( values );
	}
	
	
	/************************ 删 **********************************/
	
	/** 删除一条数据 */
	public long deleteOneRecord( long fuid ,long muid)
	{
		String where = MUID + " = " + muid + " AND " + FUID + " = " + fuid;
		return delete( where );
	}
	
	/** 删除某用户所有数据 */
	public long deleteUserAllRecord( long muid)
	{
		String where = MUID + " = " + muid;
		return delete( where );
	}
	
	
	/************************ 改 **********************************/
	
	/** 修改一条记录 */
	public long updateOneRecord(long fuid , String newFuinfo , long newDatetime , int newStatus , int newUnread , long muid)
	{
		String where = MUID + " = " + muid + " AND " + FUID + " = " + fuid;
		ContentValues values = new ContentValues( );
		values.put( FUINFO , newFuinfo );
		values.put( DATETIME , newDatetime );
		values.put( RELATION , newStatus );
		values.put( UNREAD , newUnread );
		return onUpdate( values , where );
	}
	
	/** 修改与某人的好友关系状态 */
	public long updateOneRelation( long fuid , int relation ,long muid)
	{
		String where = MUID + " = " + muid + " AND " + FUID + " = " + fuid;
		ContentValues values = new ContentValues( );
		values.put( RELATION , relation );
		return onUpdate( values , where );
	}
	
	/** 将所有记录改为已读 */
	public long markAllRead( long muid)
	{
		String where = MUID + " = " + muid;
		ContentValues values = new ContentValues( );
		values.put( UNREAD , 0 );
		return onUpdate( values , where );
	}
	
	
	/************************ 查 **********************************/
	
	/** 查询某用户的新增粉丝消息 */
	public Cursor selectPage(long mUid )
	{
		String where = MUID + " = " + mUid + " ORDER BY " + DATETIME + " DESC";
		return onSelect( selectors , where );
	}
	
	/** 查询某时间之前的新增粉丝消息 */
	public Cursor selectPage(long mUid , long datetime )
	{
		String where = MUID + " = " + mUid + " AND " + DATETIME + " <= " + datetime
				+ " ORDER BY " + DATETIME + " DESC";
		return onSelect( selectors , where );
	}
	
	/** 查找一个用户的消息 */
	public Cursor selectOneMessage(long muid , long fuid )
	{
		String where = MUID + " = " + muid + " AND " + FUID + " = " + fuid;
		return onSelect( selectors , where );
	}
	
	/** 获取未读消息 */
	public Cursor getUnreadMsgs(long mUid )
	{
		String where = MUID + " = " + mUid + " AND " + UNREAD + " > 0 ";
		return onSelect( selectors , where );
	}
	
	
}
