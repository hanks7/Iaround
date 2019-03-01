
package net.iaround.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;


/**
 * @Description: 圈通知表,muid+groupid+targetUid+type 能确定一条记录
 * @author tanzy
 * @date 2015-4-7
 */
public class GroupNoticeWorker extends ITableWorker
{
	public static final String TB_NAME = "tb_group_notice"; // 表名
	public static final String ID = "id"; // 本地id 0
	public static final String MUID = "muid"; // 所属id
	public static final String GROUPID = "groupid"; // 圈子id
	public static final String TARGET_UID = "target_uid"; // 目标用户id，即被处理者id
	public static final String TYPE = "type";// 通知类型，1：新成员申请加入，2：同意加入，3：拒绝加入，4：用户退出，5：管理员移除成员，6：设置管理员，7取消管理员（其中1、2、3为互斥事件）
	public static final String DATETIME = "datetime"; // 处理时间
	public static final String CONTENT = "content"; // 协议下发原文
	public static final String UNREAD = "unread"; // 未读状态，1：未读，0：已读
	
	/** 消息表创建sql **/
	public static final String tableSql = "CREATE TABLE IF NOT EXISTS " + TB_NAME + " ( " + ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + MUID + " VERCHAR(15)," + GROUPID
			+ " VERCHAR(15)," + TARGET_UID + " VERCHAR(15), " + TYPE + " INTEGER," + DATETIME
			+ " VERCHAR(20)," + CONTENT + " TEXT," + UNREAD + " INTEGER " + ");";
	
	public static final String[ ] selectors =
		{ ID , MUID , GROUPID , TARGET_UID , TYPE , DATETIME , CONTENT , UNREAD };
	
	protected GroupNoticeWorker(Context context )
	{
		super( context , ID , TB_NAME );
	}
	
	/********************* 增 **********************/
	
	/** 插入一条数据 */
	public long insertOneRecord(String muid , String groupid , String targetUid , int type ,
								String datetime , String content , int unread )
	{
		ContentValues values = new ContentValues( );
		values.put( MUID , muid );
		values.put( GROUPID , groupid );
		values.put( TARGET_UID , targetUid );
		values.put( TYPE , type );
		values.put( DATETIME , datetime );
		values.put( CONTENT , content );
		values.put( UNREAD , unread );
		return onInsert( values );
	}
	
	/********************* 删 **********************/
	
	/** 删除一条数据 */
	public int deleteRecord(String mUid , String groupid , String targetUid , int type )
	{
		String where = MUID + " = " + mUid + " AND " + GROUPID + " = " + groupid + " AND "
				+ TARGET_UID + " = " + targetUid + " AND " + TYPE + " = " + type;
		return delete( where );
	}
	
	/** 删除某用户的所有圈通知消息 */
	public int deleteOneUserAll( String mUid )
	{
		String where = MUID + " = " + mUid;
		return delete( where );
	}
	
	/********************* 改 **********************/
	
	/** 修改一条记录 */
	public long updateOneRecord(String muid , String groupid , int type , String targetUid ,
								int newType , String newDatetime , String newContent , int newUnread)
	{
		String where = MUID + " = " + muid + " AND " + GROUPID + " = " + groupid + " AND "
				+ TYPE + " = " + type + " AND " + TARGET_UID + " = " + targetUid;
		ContentValues values = new ContentValues( );
		values.put( TYPE , newType );
		values.put( DATETIME , newDatetime );
		values.put( CONTENT , newContent );
		values.put( UNREAD , newUnread );
		return onUpdate( values , where );
	}
	
	/** 标记某用户的全部圈通知为已读 */
	public long updateAllUnread( String muid )
	{
		String where = MUID + " = " + muid;
		ContentValues values = new ContentValues( );
		values.put( UNREAD , 0 );
		return onUpdate( values , where );
	}
	
	/********************* 查 **********************/
	
	/** 查询某用户的所有圈通知消息 */
	public Cursor selectPage(String mUid , int start , int amount )
	{
		String where = MUID + " = " + mUid + " ORDER BY " + DATETIME + " DESC LIMIT " + start
				+ "," + amount;
		return onSelect( selectors , where );
	}
	
	/** 查询某用户对于某圈子的某类型的圈通知消息 */
	public Cursor selectGroupTypeMessage(String mUid , long groupid , long targetUid ,
										 int type )
	{
		String where = MUID + " = " + mUid + " AND " + GROUPID + " = " + groupid + " AND "
				+ TARGET_UID + " = " + targetUid + " AND " + TYPE + " = " + type
				+ " ORDER BY " + DATETIME + " DESC";
		return onSelect( selectors , where );
	}
	
	/** 查询某用户未读的圈通知消息 */
	public Cursor selectUnreadMessage(String muid )
	{
		String where = MUID + " = " + muid + " AND " + UNREAD + " = 1 " + " ORDER BY "
				+ DATETIME + " DESC";
		return onSelect( selectors , where );
	}
	
}
