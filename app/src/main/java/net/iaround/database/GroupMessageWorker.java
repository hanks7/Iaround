
package net.iaround.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.model.im.ChatRecordStatus;

import java.util.ArrayList;


/**
 * 群组消息表
 * 
 * @author linyg
 * 
 */
public class GroupMessageWorker extends ITableWorker
{
	public static final String TB_NAME = "tb_group_message"; // 表名
	public static final String ID = "id"; // 本地id
	public static final String USERID = "userid";
	public static final String GROUPID = "groupid"; // 群组id
	public static final String MESSAGEID = "messageid"; // 消息id，服务器id
	public static final String CONTENT = "content"; // 消息体
	public static final String STATUS = "status"; // 消息状态 1为正在发送,2为已达,4为发送失败 //见 @ChatRecordStatus
	public static final String INCREASEID = "increaseid";// 消息的自增id,保持消息的连续性
	public static final String DELETEFLAG = "deleteflag";// 消息的删除标记,0为正常情况,1为标记删除
	public static final String TIMESTAMP = "timestamp";// 消息的时间戳,主要用于排序
	public static final String MESSAGETYPE ="message_type";//消息类型，@ChatRecordViewFactory
	public static final String SENDERID = "senderid";//改消息发送者的id
	
	public static final String[ ] selectors =
		{ ID ,USERID, SENDERID , GROUPID , MESSAGEID , CONTENT , STATUS , INCREASEID , DELETEFLAG , TIMESTAMP ,MESSAGETYPE};
	
	/** 消息表创建sql **/
	public static final String tableSql = "CREATE TABLE IF NOT EXISTS " + TB_NAME + " ( " + ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + USERID + " INTEGER, " + SENDERID + " INTEGER, " + GROUPID
			+ " INTEGER, " + MESSAGEID + " INTEGER NOT NULL," + CONTENT + " TEXT," + STATUS
			+ " VERCHAR(15) DEFAULT 0," + INCREASEID + " INTEGER(11) DEFAULT 0," + DELETEFLAG
			+ " INTEGER DEFAULT 0," + TIMESTAMP + " INTEGER(11) DEFAULT 0 , "+MESSAGETYPE+" INTEGER );";
	
	protected GroupMessageWorker(Context context )
	{
		super( context , ID , TB_NAME );
	}
	
	/** ------------ 增 ------------ **/
	
	/**
	 * 插入数据
	 * 
	 * @param uid
	 * @param msg
	 * @return long
	 */
	public long onInsertMessage(long uid , long senderid, long groupid , long messageid ,
								String contentBody , long increaseId , long timeStamp , int messageType)
	{
		ContentValues values = new ContentValues( );
		if ( uid == Config.CUSTOM_SERVICE_UID )
		{
			messageid = System.currentTimeMillis( ) + Common.getInstance( ).serverToClientTime;
		}
		values.put( USERID , uid );
		values.put( SENDERID , senderid );
		values.put( GROUPID , groupid );
		values.put( MESSAGEID , messageid );
		values.put( CONTENT , contentBody );
		values.put( INCREASEID , increaseId );
		values.put( TIMESTAMP , timeStamp );
		values.put( MESSAGETYPE , messageType );
		try
		{
			long id = onInsert( values );
			return id;
		}
		catch ( Exception e )
		{
		}
		return 0;
	}
	
	public void onBatchInsertMessage( ArrayList< ContentValues > values )
	{
		onBatchInsert( values );
	}
	
	/**
	 * 本地向数据库插入一条记录,所以没有自增id
	 * 
	 * @param uid
	 * @param groupid
	 * @param messageid
	 * @param contentBody
	 * @param status
	 * @return 返回数据库id
	 * @author kevinSu
	 */
	public long onInsertRecord( long uid ,long senderid, long groupid , long messageid , String contentBody ,
			int status , long timeStamp ,int messageType )
	{
		ContentValues values = new ContentValues( );
		if ( uid == Config.CUSTOM_SERVICE_UID )
		{
			messageid = System.currentTimeMillis( ) + Common.getInstance( ).serverToClientTime;
		}
		values.put( USERID , uid );
		values.put( SENDERID , senderid );
		values.put( GROUPID , groupid );
		values.put( MESSAGEID , messageid );
		values.put( CONTENT , contentBody );
		values.put( STATUS , status );
		values.put( INCREASEID , 0 );
		values.put( TIMESTAMP , timeStamp );
		values.put( MESSAGETYPE , messageType );
		
		return onInsert( values );
		
	}
	
	/**
	 * 带自增id和时间戳地向本地向数据库插入一条记录，如果该自增id的数据本来就存在，则更新该条记录
	 */
	public long onInsertRecord( long uid ,long senderid, long groupid , long messageid , String contentBody ,
			int status,int increaseid , long timeStamp,int messageType )
	{
		
		ContentValues values = new ContentValues( );
		if ( uid == Config.CUSTOM_SERVICE_UID )
		{
			messageid = System.currentTimeMillis( ) + Common.getInstance( ).serverToClientTime;
		}
		values.put( USERID , uid );
		values.put( SENDERID , senderid );
		values.put( GROUPID , groupid );
		values.put( MESSAGEID , messageid );
		values.put( CONTENT , contentBody );
		values.put( STATUS , status );
		values.put( INCREASEID , increaseid );
		values.put( TIMESTAMP , timeStamp );
		values.put( MESSAGETYPE , messageType );
		

		String where = USERID + " = " + uid + " AND " + GROUPID + " = " + groupid+" AND "+INCREASEID+" = "+increaseid;
		Cursor cursor = onSelect( selectors , where );
		
		if(cursor!=null && cursor.getCount( )>0)
		{//存在该increaseid的数据
			return onUpdate( values , where );
		}
		else
		{
			return onInsert( values );
		}

	}
	
	// 删除某个用户的所有消息
	public int deleteAll( long uid )
	{
		String where = USERID + " = " + uid;
		ContentValues values = new ContentValues( );
		values.put( DELETEFLAG , 1 );
		int localId = onUpdate( values , where );
		onDeleteAllNoIncreaseIdMsg( );
		return localId;
	}
	
	// 删除某个用户的某个群的所有消息
	public int deleteGroupMessage( long uid , String groupid )
	{
		String where = USERID + " = " + uid + " AND " + GROUPID + " = " + groupid;
		ContentValues values = new ContentValues( );
		values.put( DELETEFLAG , 1 );
		int localId = onUpdate( values , where );
		onDeleteAllNoIncreaseIdMsg( );
		return localId;
	}
	
	// 删除
	public int deleteGroupMessageByMessageId( long msgid )
	{
		String where = MESSAGEID + " = " + msgid;
		ContentValues values = new ContentValues( );
		values.put( DELETEFLAG , 1 );
		int localId = onUpdate( values , where );
		onDeleteAllNoIncreaseIdMsg( );
		return localId;
	}
	
	// 删除
	public int deleteGroupMessageByMessageId( long groupid , long msgid )
	{
		long userId = Common.getInstance( ).loginUser.getUid( );
		String where = USERID + " = " + userId + " AND " + GROUPID + " = " + groupid + " AND "
				+ MESSAGEID + " = " + msgid;
		ContentValues values = new ContentValues( );
		values.put( DELETEFLAG , 1 );
		int localId = onUpdate( values , where );
		onDeleteAllNoIncreaseIdMsg( );
		return localId;
	}
	
	/**
	 * 删除一条记录
	 */
	public long onDeleteRecord( String localID )
	{
		String where = ID + " = " + localID;
		ContentValues values = new ContentValues( );
		values.put( DELETEFLAG , 1 );
		int localId = onUpdate( values , where );
		onDeleteAllNoIncreaseIdMsg( );
		return localId;
	}
	
	/** ------------ 删 ------------ **/
	public void onDeleteAllNoIncreaseIdMsg( )
	{
		String where = DELETEFLAG + " = 1" + " AND " + INCREASEID + " = 0";
		onExcute( where );
	}
	
	/** ------------ 改 ------------ **/
	
	/**
	 * 更新记录的状态
	 * 
	 * @return
	 */
	public long onUpdateRecordStatus( long localID , String status )
	{
		String where = ID + " = " + localID;
		Cursor cursor = onSelect( selectors , where );
		
		long returnID = 0;
		if ( cursor != null && cursor.moveToFirst( ) )
		{
			ContentValues values = new ContentValues( );
			values.put( STATUS , status );
			returnID = onUpdate( values , where );
		}
		
		cursor.close( );
		return returnID;
	}
	
	/**
	 * 更新记录的内容
	 * 
	 * @return
	 */
	public long onUpdateRecordContent( long localID , String content )
	{
		String where = ID + " = " + localID;
		Cursor cursor = onSelect( selectors , where );
		
		long returnID = 0;
		if ( cursor != null && cursor.moveToFirst( ) )
		{
			ContentValues values = new ContentValues( );
			values.put( CONTENT , content );
			returnID = onUpdate( values , where );
		}
		
		cursor.close( );
		return returnID;
	}
	
	public void onModifySendingStatusMsg(String userid , String groupid , long dateTime )
	{
		String where = USERID + " = " + userid + " AND " + GROUPID + " = " + groupid + " AND "
				+ MESSAGEID + "<=" + dateTime + " AND " + STATUS + " = 1";
		onExcute( "UPDATE " + TB_NAME + " SET " + STATUS + " = 4 WHERE " + where );
	}
	
	public void onModifyMessageIdByLocalId(String localId , String messageId , String status ,
										   long increaseId )
	{
		String where = ID + " = " + localId;
		ContentValues values = new ContentValues( );
		values.put( MESSAGEID , messageId );
		values.put( STATUS , status );
		values.put( INCREASEID , increaseId );
		onUpdate( values , where );
	}
	
	public void onModifyIncreaseidByMessage( long groupid , long messageId , long increaseId )
	{
		
		long userId = Common.getInstance( ).loginUser.getUid( );
		String where = USERID + " = " + userId + " AND " + GROUPID + " = " + groupid + " AND "
				+ MESSAGEID + " = " + messageId;
		
		ContentValues values = new ContentValues( );
		values.put( INCREASEID , increaseId );
		onUpdate( values , where );
	}
	
	
	
	/** ------------ 查 ------------ **/
	
	/**
	 * 获取群组消息列表（使用分页模式）
	 * 
	 * @param uid
	 *            当前登录用户的id
	 * @param groupid
	 *            群组id
	 * @param startid
	 *            该群组的起始查看id
	 * @param amount
	 *            查看条数
	 * @return Cursor
	 * @throws
	 */
	public Cursor onSelectPage(long uid , long groupid , long start , int amount )
	{
		String where = USERID + " = " + uid + " AND " + GROUPID + " = " + groupid + " AND "
				+ DELETEFLAG + " = " + 0 + " ORDER BY " + ID + " DESC LIMIT " + start + ","
				+ amount;
		return onSelect( selectors , where );
	}
	
	/**
	 * 获取群组消息列表（使用分页模式）
	 * 
	 * @param uid
	 *            当前登录用户的id
	 * @param groupid
	 *            群组id
	 * @param start
	 *            从第几条数据开始
	 * @param amount
	 *            查看条数
	 * @return Cursor
	 * @throws
	 */
	public Cursor onSelectPageByStart(long uid , long groupid , long start , int amount )
	{
		String where = USERID + " = " + uid + " AND " + GROUPID + " = " + groupid + " AND "
				+ DELETEFLAG + " = " + 0 + " ORDER BY " + ID + " DESC LIMIT " + start + ","
				+ amount;
		return onSelect( selectors , where );
	}
	
	public Cursor onSelectLocalID(long localID )
	{
		String where = ID + " = " + localID;
		return onSelect( selectors , where );
	}
	
	public Cursor onSelectRecord(String groupid )
	{
		long userId = Common.getInstance( ).loginUser.getUid( );
		String where = USERID + " = " + userId + " AND " + GROUPID + " = " + groupid + " AND "
				+ DELETEFLAG + " = " + 0 + " ORDER BY " + INCREASEID;
		return onSelect( selectors , where );
	}
	
	/**获取本地发送中或发送失败且未删除的数据*/
	public Cursor onSelectUnsendRecords(long groupid )
	{
		long userId = Common.getInstance( ).loginUser.getUid( );
		String where = USERID + " = " + userId + " AND " + GROUPID + " = " + groupid
				+ " AND (" + STATUS + " = " + ChatRecordStatus.SENDING + " OR " + STATUS
				+ " = " + ChatRecordStatus.FAIL + ")" + " AND " + DELETEFLAG + " = 0";
		return onSelect( selectors , where );
	}
	
	/**获取messageid为-1的数据，即系统用户下发的数据*/
	public Cursor onSelectSystemRecords(long groupid )
	{
		long userId = Common.getInstance( ).loginUser.getUid( );
		String where = USERID + " = " + userId + " AND " + GROUPID + " = " + groupid + " AND "
				+ MESSAGEID + " = -1";
		return onSelect( selectors , where );
	}
	
	/** 返回最大的自增id的记录或者完全没有自增id时最新的记录 */
	public Cursor onSelectMaxIncreaseId(long groupid )
	{
		long userId = Common.getInstance( ).loginUser.getUid( );
		String where = USERID + " = " + userId + " AND " + GROUPID + " = " + groupid
				+ " ORDER BY " + INCREASEID + " DESC, " + TIMESTAMP + " DESC LIMIT 1";
		
		Cursor cursor = onSelect( selectors , where );
		return cursor;
	}
	
	/** 获取自增id在 [minIncreaseId, maxIncreaseId] 范围的数据，包括已删除的数据 */
	public Cursor onSelectedIncreaseRangeOfAll(long groupid , long maxIncreaseId ,
											   long minIncreaseId , int amount )
	{
		long userId = Common.getInstance( ).loginUser.getUid( );
		
		String where = USERID + " = " + userId + " AND " + GROUPID + " = " + groupid
				+ " AND (" + STATUS + " <> " + ChatRecordStatus.SENDING + " AND " + STATUS
				+ " <> " + ChatRecordStatus.FAIL + ")" + " AND " + INCREASEID + " < "
				+ maxIncreaseId + " AND " + INCREASEID + " >= " + minIncreaseId + " ORDER BY "
				+ INCREASEID + " DESC, " + TIMESTAMP + " DESC LIMIT " + amount;
//		String where = USERID + " = " + userId + " AND " + GROUPID + " = " + groupid + " AND "
//				+ DELETEFLAG + " = " + 0 + " AND (" + STATUS + " <> "
//				+ ChatRecordStatus.SENDING + " AND " + STATUS + " <> " + ChatRecordStatus.FAIL
//				+ ")" + " AND " + INCREASEID + " < " + maxIncreaseId + " AND " + INCREASEID
//				+ " >= " + minIncreaseId + " ORDER BY " + INCREASEID + " DESC, " + TIMESTAMP
//				+ " DESC LIMIT " + amount;
		
		Cursor cursor = onSelect( selectors , where );
		return cursor;
	}
	
	/** 获取自增id在 [minIncreaseId, maxIncreaseId] 的范围 */
	public Cursor onSelectedIncreaseRange(long groupid , long maxIncreaseId ,
										  long minIncreaseId , int amount )
	{
		long userId = Common.getInstance( ).loginUser.getUid( );
		
		String where = USERID + " = " + userId + " AND " + GROUPID + " = " + groupid + " AND "
				+ INCREASEID + " < " + maxIncreaseId + " AND " + INCREASEID + " >= "
				+ minIncreaseId + " ORDER BY " + INCREASEID + " DESC, " + TIMESTAMP
				+ " DESC LIMIT " + amount;
		
		Cursor cursor = onSelect( selectors , where );
		return cursor;
	}
	
	/** 获取自增id为0且时间戳小于maxDataTime的范围 */
	public Cursor onSelectedDataTimeRange(long groupid , long maxDataTime , int amount )
	{
		long userId = Common.getInstance( ).loginUser.getUid( );
		
		String where = USERID + " = " + userId + " AND " + GROUPID + " = " + groupid + " AND "
				+ DELETEFLAG + " = " + 0 + " AND " + INCREASEID + " = 0" + " AND " + TIMESTAMP
				+ " < " + maxDataTime + " AND (" + STATUS + " <> " + ChatRecordStatus.SENDING
				+ " AND " + STATUS + " <> " + ChatRecordStatus.FAIL + ")" + " ORDER BY "
				+ TIMESTAMP + " DESC LIMIT " + amount;
		
		Cursor cursor = onSelect( selectors , where );
		return cursor;
	}
	
	public Cursor onSelectedChatRecordByMsgId(long groupid , long messageid )
	{
		long userId = Common.getInstance( ).loginUser.getUid( );
		
		String where = USERID + " = " + userId + " AND " + GROUPID + " = " + groupid + " AND "
				+ MESSAGEID + " = " + messageid;
		
		Cursor cursor = onSelect( selectors , where );
		return cursor;
	}
	
	public Cursor onSelectedChatRecordByIncreaseid(long groupid , long increaseid )
	{
		long userId = Common.getInstance( ).loginUser.getUid( );
		
		String where = USERID + " = " + userId + " AND " + GROUPID + " = " + groupid + " AND "
				+ INCREASEID + " = " + increaseid;
		
		Cursor cursor = onSelect( selectors , where );
		return cursor;
	}

	
	public Cursor onSelectRecordsByLocalId(long groupid , long localID , int amount )
	{
		long userId = Common.getInstance( ).loginUser.getUid( );
		
		String where = USERID + " = " + userId + " AND " + GROUPID + " = " + groupid + " AND "
				+ DELETEFLAG + " = " + 0 + " AND (" + STATUS + " <> "
				+ ChatRecordStatus.SENDING + " AND " + STATUS + " <> " + ChatRecordStatus.FAIL
				+ ")" + " AND " + ID + " < " + localID + " ORDER BY " + ID + " DESC LIMIT "
				+ amount;
		return onSelect( selectors , where );
	}
	
	public Cursor onSelectRecord(long groupid , int amount )
	{
		long userId = Common.getInstance( ).loginUser.getUid( );
		
		String where = USERID + " = " + userId + " AND " + GROUPID + " = " + groupid + " AND "
				+ DELETEFLAG + " = " + 0 + " AND (" + STATUS + " <> "
				+ ChatRecordStatus.SENDING + " AND " + STATUS + " <> " + ChatRecordStatus.FAIL
				+ ")" + " ORDER BY " + ID + " DESC LIMIT " + amount;
		return onSelect( selectors , where );
	}
	
	
	/**
	 * @Description: 查找特定消息类型的未删除的圈聊记录
	 * @author tanzy
	 * @date 2015-8-13
	 */
	public Cursor selectGroupMsgType(long groupid , int msgType )
	{
		long userId = Common.getInstance( ).loginUser.getUid( );
		String where = USERID + " = " + userId + " AND " + GROUPID + " = " + groupid + " AND "
				+ DELETEFLAG + " = " + 0 + " AND " + MESSAGETYPE + " = " + msgType +" ORDER BY "+TIMESTAMP;
		return onSelect( selectors , where );
	}
}
