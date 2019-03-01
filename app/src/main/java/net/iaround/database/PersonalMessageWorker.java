package net.iaround.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import net.iaround.entity.type.MessageBelongType;
import net.iaround.model.im.ChatRecordStatus;
import net.iaround.ui.chat.view.ChatRecordViewFactory;

/**
 * 私聊消息数据库处理
 * 
 * @author linyg
 * 
 */
public class PersonalMessageWorker extends ITableWorker {
	public static final String TB_NAME = "tb_personal_message";
	public static final String ID = "id"; // 消息id(本地)
	public static final String SERVER_ID = "server_id"; // 服务端id
	public static final String M_UID = "m_uid"; // 本人uid
	public static final String F_UID = "f_uid"; // 对方uid
	public static final String CONTENT = "content"; // 消息内容
	public static final String GIFTSTATUS = "readgift"; // 当时礼物类型时，是否阅读
	public static final String SENDTYPE = "sendtype"; // 发送类型，1为登录者发，2为接收 @MessageBelongType
	public static final String STATUS = "status"; // 消息状态 1为发送中,2已送达，3已阅读, 4失败 @ChatRecordStatus
	public static final String DISTANCE = "distance";// 该消息双方的距离
	public static final String MESSAGE_TYPE = "message_type";// 消息的类型 @ChatRecordViewFactory
	public static final String FROM = "chat_from";// 通过哪里找到对方进行聊天的类型@ChatFromType
	public static final String TIMESTAMP = "timestamp";// 消息的时间戳,主要用于排除对方发送重复消息的情况
	public static final String NEWFLAG = "new_flag";//是否为新消息（即我没看过，主要用于悄悄查看）1：是，0：否

	public static final String tableSql = "CREATE TABLE IF NOT EXISTS " + TB_NAME + " ( " + ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + SERVER_ID + " INTEGER(11), " + M_UID
			+ " VERCHAR(15), " + F_UID + " VERCHAR(15), " + CONTENT + " TEXT," + GIFTSTATUS
			+ " CHAR(1) DEFAULT 0," + SENDTYPE + " CHAR(1) DEFAULT 0," + STATUS
			+ " CHAR(1) DEFAULT 0," + DISTANCE + " INTEGER(11) DEFAULT -1," + MESSAGE_TYPE
			+ " INTEGER(11) DEFAULT 1," + FROM + " INTEGER(11) DEFAULT 0," + TIMESTAMP
			+ " INTEGER(11) DEFAULT 0," + NEWFLAG + " INTEGER DEFAULT 0);";

	public static final String[] selectors = { ID, SERVER_ID, M_UID, F_UID,
			CONTENT, GIFTSTATUS, SENDTYPE, STATUS, DISTANCE, MESSAGE_TYPE, FROM,TIMESTAMP,NEWFLAG };

	protected PersonalMessageWorker(Context context) {
		super(context, ID, TB_NAME);
	}

	/** ------------ 增 ------------ **/

	/**
	 * 插入消息
	 * 
	 * @param context
	 * @param mUid
	 *            自己的用户id
	 * @param fUid
	 *            对方的用户id
	 * @param serverId
	 *            消息在服务端的id
	 * @param content
	 *            消息内容
	 * @param status
	 *            消息状态 1为未读，2已送达，3已阅读, 4为失败
	 * @param sendtype
	 *            发送类型 1为登录者发，2为接收 
	 * @param distance
	 *            距离
	 * @param dateTime
	 *            消息的时间戳
	 * @param messageType
	 *            @ChatRecordViewFactory
	 * @param subGroup
	 *            1.私聊列表，2.发出搭讪， 3.收到搭讪
	 * @param from
	 *            @ChatFromType
	 * @return
	 */
	public long onInsert(Context context, String mUid, String fUid,
						 String serverId, String content, int status, int sendtype,
						 int distance, int messageType, int from, long timestamp, int newflag) {
		ContentValues values = new ContentValues();
		values.put( SERVER_ID , serverId );
		values.put( M_UID , mUid );
		values.put( F_UID , fUid );
		values.put( CONTENT , content );
		values.put( SENDTYPE , sendtype );
		values.put( STATUS , status );
		values.put( DISTANCE , distance );
		values.put( MESSAGE_TYPE , messageType );
		values.put( FROM , from );
		values.put( TIMESTAMP , timestamp );
		values.put( NEWFLAG , newflag );
		
		return onInsert( values );
	}

	
	/**
	 * 插入一条完整的数据记录
	 * @param mUid 我的用户id
	 * @param fUid 朋友的用户id
	 * @param serverId 消息的服务端id
	 * @param content 消息的内容
	 * @param giftStatus 当属于礼物消息时,是否已阅读的状态
	 * @param sendtype 消息的属于发送还是接收类型
	 * @param status 消息的状态
	 * @param distance 双方的聊天距离
	 * @param messageType 消息的类型
	 * @param from 通过什么方式找到聊天
	 * @return 返回消息插入数据库的本地id
	 */
	public long onInsert(long mUid, long fUid, long serverId,
						 String content, int giftStatus, int sendtype, int status,
						 int distance, int messageType, int from){
		
		ContentValues values = new ContentValues();
		values.put(M_UID, mUid);
		values.put(F_UID, fUid);
		values.put(SERVER_ID, serverId);
		values.put(CONTENT, content);
		values.put(GIFTSTATUS, giftStatus);
		values.put(SENDTYPE, sendtype);
		values.put(STATUS, status);
		values.put(DISTANCE, distance);
		values.put(MESSAGE_TYPE, messageType);
		values.put(FROM, from);

		return onInsert(values);
	}
	
	
	
	
	
	
	
	/** ------------ 删 ------------ **/

	/**
	 * 删除登陆用户的所有消息
	 * @param mUid
	 * @return
	 */
	public int onDeleteAll(long mUid){
		String where = M_UID + " = " + mUid;
		return delete(where);
	}
	
	/**
	 * 删除与某个用户之间的所有消息
	 * @param mUid
	 * @param fUid
	 * @return
	 */
	public int onDeleteUserAll(long mUid, long fUid)
	{
		String where = M_UID + " = " + mUid + " AND " + F_UID + " = " + fUid;
        return delete(where);
	}
	
	/**
	 * 删除
	 * 
	 * @return int
	 */
	public int onDeleteByServerId(long mUid, long serverId) {
		String where = M_UID + " = " + mUid + " AND " + SERVER_ID + " = " + serverId;
		return delete(where);
	}
	
	/**
	 * 根据本地消息id,删除消息
	 * 
	 * @param localid
	 * @return
	 */
	public int onDeleteByLoaclId(long localId) {
		String where = ID + " = " + localId;
		return delete(where);
	}

	
/****旧******/
	
	/**
	 * 删除用户的所有私聊消息
	 * @param uid
	 */
	public int deleteAll(long uid) {
		String where = M_UID + " = " + uid;
		return delete(where);
	}

	
	/**
	 * 删除与某个用户之间的所有消息
	 * @param mUid
	 * @param fUid
	 * @return
	 */
	public int deleteMsg(String mUid, String fUid) {
		String where = M_UID + " = " + mUid + " AND " + F_UID + " = " + fUid;
		return delete(where);
	}

	
	/**
	 * 删除记录，根据服务器id
	 * 
	 * @return int
	 */
	public int deleteMsg(String mUid, String fUid, String serverId) {
		String where = M_UID + " = " + mUid + " AND " + F_UID + " = " + fUid
				+ " AND " + SERVER_ID + " = " + serverId;
		return delete(where);
	}

	/**
	 * 根据本地消息id,删除消息
	 * 
	 * @param localid
	 * @return
	 */
	public int deleteMsgByLocalId(long localid) {
		String where = ID + " = " + localid;
		return delete(where);
	}

	public int delteMsgByMessagesType(String mUid, String fUid, int messageType) {
		String where = M_UID + " = " + mUid + " AND " + F_UID + " = " + fUid
				+ " AND " + MESSAGE_TYPE + " = " + messageType;
		return delete(where);
	}

	
	
	
	
	
	/** ------------ 改 ------------ **/

//	/**
//	 * 更改消息状态
//	 * @param context
//	 * @param localId
//	 * @param status
//	 * @return
//	 */
//	public long onModifyMessageStatus(int localId, int status) {
//		String where = ID + " = " + localId;
//		ContentValues values = new ContentValues();
//		values.put(PersonalMessageWorker.STATUS, status);
//		return onUpdate(values, where);
//	}
	
	/**
	 * 更新消息的serverId
	 * @param localId
	 * @param serverId
	 * @return
	 */
	public long onModifyServerId(int localId, long serverId) {
		String where = ID + "=" + localId;
		ContentValues values = new ContentValues();
		values.put(SERVER_ID, serverId);
		return onUpdate(values, where);
	}
	
//	/**
//	 * 更新消息的状态
//	 * @param localId
//	 * @param status
//	 * @return
//	 */
//	public long onModifyStatus(int localId, int status) {
//		String where = ID + "=" + localId;
//		ContentValues values = new ContentValues();
//		values.put(STATUS, status);
//		return onUpdate(values, where);
//	}
	
	/**
	 * 更新消息的距离
	 * @param localId
	 * @param distance
	 * @return
	 */
	public long onModifyDistance(int localId, int distance) {
		String where = ID + "=" + localId;
		ContentValues values = new ContentValues();
		values.put(DISTANCE, distance);
		return onUpdate(values, where);
	}
	
	/**
	 * 更新消息的内容
	 * @param localId
	 * @param content
	 * @return
	 */
	public long onModifyContent(int localId, String content) {
		String where = ID + "=" + localId;
		ContentValues values = new ContentValues();
		values.put(CONTENT, content);
		return onUpdate(values, where);
	}
	
	/** 将目标用户的所有私聊消息设置为旧消息 */
	public long readAllMsg( long muid , long fuid )
	{
		String where = M_UID + " = " + muid + " AND " + F_UID + " = " + fuid;
		ContentValues values = new ContentValues( );
		values.put( NEWFLAG , 0 );
		return onUpdate( values , where );
	}
	
	/**将自己的所有私聊消息设置为旧消息*/
	public long readMyAllMsg(long muid)
	{
		String where = M_UID + " = " + muid ;
		ContentValues values = new ContentValues( );
		values.put( NEWFLAG , 0 );
		return onUpdate( values , where );
	}
	
/***********旧 ******/
	
	/**
	 * 更新消息中的礼物状态
	 * 
	 * @param context
	 * @param serverid
	 * @param status
	 *            1为已经品尝，0为未品尝
	 */
	public void updateReadGift(Context context, String serverid, int status) {
		String where = SERVER_ID + " = " + serverid;
		ContentValues values = new ContentValues();
		values.put(GIFTSTATUS, status);
		onUpdate(values, where);
	}

	/**
	 * 更改消息状态
	 * 
	 * @param mUid
	 * @param fUid
	 * @param locid
	 * @return
	 * @time 2011-7-4 下午04:20:58
	 * @author:linyg
	 */
//	public long onModify(Context context, String mUid, String fUid,
//			String locid, String status) {
//		String where = ID + " = " + locid;
//		ContentValues values = new ContentValues();
//		values.put(PersonalMessageWorker.STATUS, status);
//		if (!status.equals("1")) { // （最近联系人）将已读消息，更改为0
//			NearContactWorker db = DatabaseFactory
//					.getNearContactWorker(context);
//			db.updateStatus(mUid, fUid);
//		}
//		return onUpdate(values, where);
//	}

	public long onModify(Context context, String locid, String status) {
		String where = ID + " = " + locid;
		ContentValues values = new ContentValues();
		values.put(PersonalMessageWorker.STATUS, status);
		return onUpdate(values, where);
	}

	// 每次读取新列表时，将所有的发送出去的已读消息置为已读
	private void onModify(String mUid, String fUid, String ids) {
		String where = ID + " IN (" + ids + " )" + " AND " + STATUS + "=" + "2"
				+ " AND " + SENDTYPE + " = " + MessageBelongType.SEND;
		onExcute("UPDATE " + TB_NAME + " SET " + STATUS + " = 3 WHERE " + where);
	}

	/**
	 * 更改message的id和状态
	 * 
	 * @param locid
	 * @param server_id
	 * @param type
	 * @return long
	 */
	public long onModifyMessageIdAndStatus(String locid, String server_id,
										   String status) {
		String where = ID + "=" + locid;
		ContentValues values = new ContentValues();
		values.put(SERVER_ID, server_id);
		values.put( STATUS, status );
		return onUpdate(values, where);
	}

	/**
	 * 更改message的id、状态、距离
	 * 
	 * @param locid
	 * @param server_id
	 * @param status
	 * @param distance
	 * @return
	 */
	public long onModifyMessageIdStatusDistance(String locid, String server_id,
												String status, int distance) {
		String where = ID + "=" + locid;
		ContentValues values = new ContentValues();
		values.put(SERVER_ID, server_id);
		values.put(STATUS, status);
		values.put(DISTANCE, distance);

		return onUpdate(values, where);
	}

	public long onModifyMessageContent(String localID, String Content) {
		String where = ID + "=" + localID;
		ContentValues values = new ContentValues();
		values.put(CONTENT, Content);
		return onUpdate(values, where);
	}

	public long onModifyContentByMsgid(String msgid, String content)
	{
		String where = SERVER_ID + "=" + msgid;
		ContentValues values = new ContentValues();
		values.put(CONTENT, content);
		return onUpdate(values, where);
	}

	/**
	 * 将设置某一段id的消息为已读,排除发送中、发送失败的情况
	 * 
	 * @param fUid
	 * @param bigId
	 * @time 2011-9-27 上午11:24:52
	 * @author:linyg
	 */
	public void onModifyBigId(String mUid, String fUid, long bigId) {
		String where = M_UID + " = " + mUid + " AND " + F_UID + " = " + fUid
				+ " AND " + ID + "<=" + bigId + " AND " + STATUS + "=" + "2"
				+ " AND " + SENDTYPE + " <> " + MessageBelongType.RECEIVE;
		onExcute("UPDATE " + TB_NAME + " SET " + STATUS + " = 3 WHERE " + where);
	}

	/**
	 * 把发送中的消息，SERVER_ID作为时间戳，小于指定时间的发送中的消息状态改为失败
	 * 
	 * @param mUid
	 * @param fUid
	 * @param dataTime
	 */
	public void onModifySendingStatusMsg(String mUid, String fUid, long dateTime) {
		String where = M_UID + " = " + mUid + " AND " + F_UID + " = " + fUid
				+ " AND " + SERVER_ID + "<=" + dateTime + " AND " + STATUS
				+ " = 1";
		onExcute("UPDATE " + TB_NAME + " SET " + STATUS + " = 4 WHERE " + where);
	}

	/** ------------ 查 ------------ **/

	
	
	/**
	 * 按照分页的模式查询
	 * 
	 * @param mUid
	 * @param fUid
	 * @param start
	 * @param amount
	 * @return
	 * @time 2011-7-1 上午10:22:37
	 * @author:linyg
	 */
	public Cursor onSelectPage(Context context, String mUid, String fUid,
							   int start, int amount) {
		String where = M_UID + " = " + mUid + " AND " + F_UID + " = " + fUid
				+ " ORDER BY " + ID + " DESC LIMIT " + start + "," + amount;
		Cursor cursor = onSelect(selectors, where);
		return cursor;
	}

	/**
	 * 
	 * @param localId
	 *            数据库中的id
	 * @return 如果该记录没有返回null 如果有则返回一条{ID, SERVER_ID, M_UID, F_UID, CONTENT,
	 *         GIFTSTATUS, SENDTYPE, STATUS }记录
	 */
	public Cursor onSelectRecord(String localId) {
		String where = ID + "=" + localId;
		Cursor cursor = onSelect(selectors, where);
		return cursor;
	}

	public Cursor onSelectByStatus(String status) {
		String where = STATUS + "=" + status;
		Cursor cursor = onSelect(selectors, where);
		return cursor;
	}

	/** 根据消息id（表中的serverid）查找聊天记录 */
	public Cursor onSelectByMsgid(String msgid)
	{
		String where = SERVER_ID + "=" + msgid;
		Cursor cursor = onSelect(selectors, where);
		return cursor;
	}

	/**
	 * 取未读的消息
	 * 
	 * @param mUid
	 * @param fUid
	 * @return
	 * @time 2011-7-1 下午03:59:35
	 * @author:linyg
	 */
	public Cursor onSelectNoRead(Context context, String mUid, String fUid,
								 int type) {
		String where = "";
		if (type > 1) {
			where = M_UID + " = " + mUid + " AND " + F_UID + " = " + fUid
					+ " AND " + STATUS + " <>3 ORDER BY " + ID + " ASC ";
		} else {
			where = M_UID + " = " + mUid + " AND " + F_UID + " = " + fUid
					+ " AND " + STATUS + " == 1 ORDER BY " + ID + " ASC ";
		}
		Cursor cursor = onSelect(selectors, where);
		cursor.moveToFirst();
		String ids = "";
		while (!cursor.isAfterLast()) {
			try {
				int status = Integer.parseInt(cursor.getString(cursor
						.getColumnIndex(PersonalMessageWorker.STATUS)));
				if (status == 1) {
					int id = cursor.getInt(cursor
							.getColumnIndex(PersonalMessageWorker.ID));
					ids += id + ",";
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cursor.moveToNext();
			}
		}

		// 每次读取新列表时，将所有的发送中和已送达的消息设置为已读
		if (!ids.equals("")) {
			ids = ids.substring(0, ids.length() - 1);
			onModify(mUid, fUid, ids);
		}

		// 更新未读信息
//		NearContactWorker db = DatabaseFactory.getNearContactWorker(context);
//		db.updateStatus(mUid, fUid);
		return cursor;
	}

	/**
	 * 按id升序,获取聊天记录
	 * @param context
	 * @param mUid
	 * @param fUid
	 * @param start 开始的位置
	 * @param amount 获取的数量
	 * @return
	 */
	public Cursor onSelectAccostChatRecord(Context context, String mUid,
										   String fUid, int start, int amount) {
		String where = M_UID + " = " + mUid + " AND " + F_UID + " = " + fUid
				+ " ORDER BY " + ID + " ASC LIMIT " + start + "," + amount;
		Cursor cursor = onSelect(selectors, where);
		return cursor;
	}
	
	/**
	 * 按照id降序,获取聊天记录
	 * @param context
	 * @param mUid
	 * @param fUid
	 * @param start 开始的位置
	 * @param amount 获取的数量
	 * @return
	 */
	public Cursor onSelectAccostChatRecordDESC(Context context, String mUid,
											   String fUid, int start, int amount) {
		String where = M_UID + " = " + mUid + " AND " + F_UID + " = " + fUid
				+ " ORDER BY " + ID + " DESC LIMIT " + start + "," + amount;
		Cursor cursor = onSelect(selectors, where);
		return cursor;
	}

	/**
	 * 聊天页面的聊天记录,按照id的倒序
	 * 
	 * @param mUid
	 * @param fUid
	 * @return
	 * @time 2011-7-1 下午03:59:35
	 * @author:linyg
	 */
	public Cursor onSelectChatRecord(Context context , String mUid , String fUid , int start ,
									 int amount )
	{
		String where = M_UID + " = " + mUid + " AND " + F_UID + " = " + fUid + " ORDER BY "
				+ ID + " DESC LIMIT " + start + "," + amount;
		Cursor cursor = onSelect( selectors , where );
		cursor.moveToFirst( );
		String ids = "";
		while ( !cursor.isAfterLast( ) )
		{
			try
			{
				int status = Integer.parseInt( cursor.getString( cursor
						.getColumnIndex( PersonalMessageWorker.STATUS ) ) );
				if ( status == 1 )
				{
					int id = cursor.getInt( cursor.getColumnIndex( PersonalMessageWorker.ID ) );
					ids += id + ",";
				}
			}
			catch ( Exception e )
			{
				e.printStackTrace( );
			}
			finally
			{
				cursor.moveToNext( );
			}
		}
		
		// 每次读取新列表时，将所有的已读消息置为未读
		if ( !ids.equals( "" ) )
		{
			ids = ids.substring( 0 , ids.length( ) - 1 );
			onModify( mUid , fUid , ids );
		}
		
		// 更新未读信息
//		NearContactWorker db = DatabaseFactory.getNearContactWorker( context );
//		db.updateStatus( mUid , fUid );
		return cursor;
	}

	
	/**
	 * 筛选出发送中的情况
	 * @param context
	 * @param mUid
	 * @return
	 */
	public Cursor onSelectSendingRecord(Context context, long mUid) {
		String where = M_UID + " = " + mUid + " AND " + STATUS  + " = " + ChatRecordStatus.SENDING;
		Cursor cursor = onSelect(selectors, where);
		cursor.moveToFirst();
		return cursor;
	}
	
	/**
	 * 获取是否存在对方未读的消息（即记录的状态为：2 已发达）
	 * 
	 * @param mUid
	 * @param fUid
	 * @return
	 */
	public boolean onSearchNoReaded(Context context, String mUid, String fUid) {
		boolean isHas = false;
		Cursor cursor = onSelectNoRead(context, mUid, fUid, 2);
		try {
			if (cursor != null) {
				cursor.moveToFirst();

				if (cursor.getCount() != 0) {
					isHas = true;
				}

				if (!cursor.isClosed()) {
					cursor.close();
				}
			}
		} catch (Exception e) {
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return isHas;
	}

	/**
	 * 获取数据库中我与朋友的聊天中，接收消息最大的消息id
	 * 
	 * @param fUid
	 * @return
	 */
	public Cursor getFriendMaxServerId(String mUid, String fUid) {
		String where = M_UID + " = " + mUid + " AND " + F_UID + " = " + fUid
				+ " AND " + SENDTYPE + "=" + MessageBelongType.RECEIVE;
		return onSelect(selectors, where);
	}

	/**
	 * 获取数据库中我与朋友的聊天中，已发出去的消息中，最大的消息id。
	 * 
	 * @param fUid
	 * @return
	 */
	public Cursor getMaxServerId(String mUid, String fUid, String serverId) {
		String where = M_UID + " = " + mUid + " AND " + F_UID + " = " + fUid
				+ " AND " + SERVER_ID + " <= " + serverId + " ORDER BY "
				+ SERVER_ID + " ASC ";
		return onSelect(selectors, where);
	}

	public int getCountByFlag(String mUid , String fUid , long timestamp )
	{
		//小秘书推送的flag都为零，所以要求为零时不判断
		if ( timestamp == 0 )
			return 0;
		
		String where = M_UID + " = " + mUid + " AND " + F_UID + " = " + fUid + " AND "
				+ TIMESTAMP + " = " + timestamp;
		
		Cursor cursor = onSelect( selectors , where );
		int count = 0;
		if ( cursor != null )
		{
			count = cursor.getCount( );
		}
		
		cursor.close( );
		return count;
	}

	/**
	 * 获取真心话大冒险的题目在本地数据库表的id
	 * 
	 * @param mUid
	 * @param fUid
	 * @return 返回表中的本地ID，如果不存在就=0
	 */
	public int getAccostQuestion(String mUid, String fUid) {
		String where = M_UID + " = " + mUid + " AND " + F_UID + " = " + fUid
				+ " AND " + MESSAGE_TYPE + " = "
				+ ChatRecordViewFactory.ACCOST_GAME_QUE;

		Cursor cursor = onSelect(selectors, where);
		int localID = 0;

		if (cursor.moveToFirst()) {
			localID = cursor.getInt(cursor.getColumnIndex(ID));
		}

		cursor.close();
		return localID;
	}
	
	/**在消息表中获取新的消息*/
	public Cursor getNewMessages(String mUid, String fUid)
	{
		String where = M_UID + " = " + mUid + " AND " + F_UID + " = " + fUid
				+ " AND " + NEWFLAG + " = 1";
		Cursor cursor = onSelect(selectors, where);
		return cursor;
	}
	
	/**
	 * @Description: 获取与对方聊天中所有图片消息
* @author tanzy
* @date 2015-8-12
*/
	public Cursor getPicMsg(long muid , long fuid )
	{
		String where = M_UID + " = " + muid + " AND " + F_UID + " = " + fuid + " AND "
				+ MESSAGE_TYPE + " = " + ChatRecordViewFactory.IMAGE;
		Cursor cursor = onSelect( selectors , where );
		return cursor;
	}
}
