package net.iaround.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * 列表本地数据
 * 
 * @author ZengJ
 * 
 */
public class GroupContactWorker extends ITableWorker {
	public static final String TB_NAME = "tb_group_contact"; // 表名
	public static final String ID = "id"; // 本地id 0
	public static final String MUID = "muid"; // 所属id
	public static final String GROUPID = "groupid"; // 圈子id
	public static final String GROUPNAME = "groupname"; // 圈子昵称
	public static final String GROUPICON = "groupicon"; // 圈子icon
	public static final String NONEREAD = "none_read"; // 设置默认数量
	public static final String LASTCONTENT = "last_content"; // 最后一条消息数
	public static final String TIME = "time";// 最后一条推送的时间
	public static final String STATUS = "status"; // 消息状态 1为正在发送,2为已达,4为发送失败
	public static final String ATFLAG = "atflag"; // 是否有人At我的标志,0为没被人AT,1为被人AT

	/** 消息表创建sql **/
	public static final String tableSql = "CREATE TABLE IF NOT EXISTS "
			+ TB_NAME + " ( " + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ MUID + " VERCHAR(15)," + GROUPID + " VERCHAR(15)," + GROUPNAME
			+ " VERCHAR(50)," + GROUPICON + " VERCHAR(100)," + NONEREAD
			+ " INTEGER," + LASTCONTENT + " TEXT," + TIME + " VERCHAR(20),"
			+ STATUS + " VERCHAR(15) DEFAULT 0," + ATFLAG
			+ " INTEGER DEFAULT 0);";

	public static final String[] selectors = { ID, MUID, GROUPID, GROUPNAME,
			GROUPICON, NONEREAD, LASTCONTENT, TIME, STATUS, ATFLAG };

	protected GroupContactWorker(Context context) {
		super(context, ID, TB_NAME);
	}

	/** ------------ 增 ------------ **/

	/**
	 * 插入数据,如果数据不存在就插入
	 * 
	 * @param groupId
	 */
	public long insertData(String muid, String groupId, String groupName,
						   String groupIcon, String content, String time, int noReadNum) {
		String where = MUID + " = " + muid + " AND " + GROUPID + " = "
				+ groupId;
		Cursor cursor = onSelect(selectors, where);
		if (cursor.moveToFirst() && cursor.getCount() > 0) {
			int count = cursor.getInt(cursor.getColumnIndex(NONEREAD));
			// 更新
			ContentValues values = new ContentValues();
			// 没有消息的时候才更新最后一条的时间
			if (count == 0) {
				values.put(TIME, time);
			}
			values.put(MUID, muid);
			values.put(NONEREAD, count + noReadNum);
			values.put(LASTCONTENT, content);
			values.put(STATUS, "0");

			cursor.close();
			return onUpdate(values, where);
		} else {
			ContentValues values = new ContentValues();
			values.put(MUID, muid);
			values.put(GROUPID, groupId);
			values.put(GROUPNAME, groupName);
			values.put(GROUPICON, groupIcon);
			values.put(TIME, time);
			values.put(LASTCONTENT, content);
			values.put(NONEREAD, noReadNum);
			values.put(STATUS, "0");

			cursor.close();
			return onInsert(values);
		}
	}

	/**
	 * 插入一条数据，如果存在就更新
	 * 
	 * @param muid
	 * @param groupId
	 * @param groupName
	 * @param groupIcon
	 * @param content
	 * @param time
	 * @param noReadNum
	 * @param status
	 * @return 返回数据库记录的id
	 * @author kevinSu
	 */
	public long onInsertRecord(String muid, String groupId, String groupName,
							   String groupIcon, String content, String time, int noReadNum,
							   int status, int atFlag) {
		String where = MUID + " = " + muid + " AND " + GROUPID + " = " + groupId;
		
		Cursor cursor = onSelect(selectors, where);

		ContentValues values = new ContentValues();
		values.put(MUID, muid);
		values.put(GROUPID, groupId);
		values.put(GROUPNAME, groupName);
		values.put(GROUPICON, groupIcon);
		values.put(TIME, time);
		values.put(NONEREAD, noReadNum);
		values.put(LASTCONTENT, content);
		values.put(STATUS, status);
		if(atFlag == 1){
			values.put(ATFLAG, atFlag);
		}
		long localID = 0;
		try {
			if (cursor.moveToFirst() && cursor.getCount() > 0) {
				localID = onUpdate(values, where);
			} else {
				localID = onInsert(values);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return localID;
	}

	/** ------------ 删 ------------ **/

	/**
	 * 删除圈子聊天记录
	 * 
	 * @param mUid
	 * @param fUid
	 * @return
	 */
	public int deleteRecord(String mUid, String groupid) {
		String where = MUID + " = " + mUid + " AND " + GROUPID + " = "
				+ groupid;
		return delete(where);
	}

	/**
	 * 删除所有消息
	 * 
	 * @param uid
	 */
	public int deleteAll(long uid) {
		String where = MUID + " = " + uid;
		return delete(where);
	}

	/** ------------ 改 ------------ **/


	/**
	 * 更新最近对应关系的未读数为0
	 * 
	 * @param mUid
	 * @param fUid
	 * @time
	 */
	public void updateStatus(long mUid, long GroupID) {
		String where = MUID + " = " + mUid + " AND " + GROUPID + " = "
				+ GroupID;
		ContentValues values = new ContentValues();
		values.put(NONEREAD, 0);
		values.put(ATFLAG, 0);
		onUpdate(values, where);
	}

	/**
	 * 忽略所有信息
	 * 
	 * @param uid
	 * @return
	 */
	public int updateIgnore(long uid) {
		ContentValues values = new ContentValues();
		values.put(NONEREAD, 0);
		values.put(ATFLAG, 0);
		String upWhere = MUID + "=" + uid + " AND " + NONEREAD + " >0 ";
		return onUpdate(values, upWhere);
	}

	/**
	 * 更新记录的状态
	 * 
	 * @return
	 */
	public long onUpdateRecordStatus(long dateTime, String status) {
		String where = TIME + " = " + dateTime;
		Cursor cursor = onSelect(selectors, where);

		long returnID = 0;
		if (cursor != null && cursor.moveToFirst()) {
			ContentValues values = new ContentValues();
			values.put(STATUS, status);
			returnID = onUpdate(values, where);
		}

		cursor.close();
		return returnID;
	}

	/**
	 * 更新记录的状态
	 * 
	 * @return
	 */
	public long onUpdateRecordContent(String content, String groupid,
									  String time, String status) {
		String where = GROUPID + " = " + groupid;
		Cursor cursor = onSelect(selectors, where);

		long returnID = -1l;
		if (cursor != null && cursor.moveToFirst()) {
			ContentValues values = new ContentValues();
			values.put(LASTCONTENT, content);
			values.put(TIME, time);
			values.put(STATUS, status);
			returnID = onUpdate(values, where);
		}

		cursor.close();
		return returnID;
	}

	public void onUpdateNoRead(String mUid, String groupID, int noRead) {
		String where = MUID + " = " + mUid + " AND " + GROUPID + " = "
				+ groupID;
		ContentValues values = new ContentValues();
		values.put(NONEREAD, noRead);
		onUpdate(values, where);
	}

	/**
	 * 对特定圈子的lastcontent和time作更新
	 */
	public void updateContentAndTime(String muid, String groupId,
									 String content, String time) {
		String where = MUID + " = " + muid + " AND " + GROUPID + " = "
				+ groupId;
		Cursor cursor = onSelect(selectors, where);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.close();
			// 更新
			ContentValues values = new ContentValues();
			values.put(MUID, muid);
			values.put(TIME, time);
			values.put(LASTCONTENT, content);
			onUpdate(values, where);
		}
	}

	/** ------------ 查 ------------ **/

	public int onSelect(String muid, String groupId) {
		String where = MUID + " = " + muid + " AND " + GROUPID + " = "
				+ groupId;
		int count = 0;
		Cursor cursor = onSelect(selectors, where);
		try {
			if (cursor != null && cursor.getCount() > 0) {
				count = cursor.getCount();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}

		return count;
	}

	/**
	 * 读取未读圈子消息的数量总和
	 * 
	 * @param uid
	 * @return
	 */
	public int countNoRead(Context context , String uid )
	{
		int count = 0;
		String where = MUID + " = " + uid + " ORDER BY " + TIME + " DESC LIMIT 0,100";
		String[ ] strs = selectors;
		Cursor cursor = onSelect( strs , where );
		try
		{
			for ( cursor.moveToFirst( ) ; !cursor.isAfterLast( ) ; cursor.moveToNext( ) )
			{
				count += cursor.getInt( cursor.getColumnIndex( NONEREAD ) );
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
		
		if ( count > 0 )
			return count;
		else
			return 0;
	}

	/**
	 * 
	 * 
	 * @param mUid
	 * @return
	 */
	public Cursor selectPage(String mUid) {
		String where = MUID + " = '" + mUid + "'";
		return onSelect(selectors, where);
	}

	public Cursor selectUserGroup(long uid, int groupID) {
		String where = MUID + " = " + uid + " AND " + GROUPID + " = " + groupID;
		return onSelect(selectors, where);
	}

	/**
	 * 读取某个圈子未读消息的数量
	 * 
	 * @param uid
	 * @return
	 */
	public int countGroupNoRead(long uid, int groupID) {
		int count = 0;
		String where = MUID + " = " + uid + " AND " + GROUPID + " = " + groupID;
		Cursor cursor = onSelect(selectors, where);

		if (cursor != null && cursor.moveToFirst()) {
			count = cursor.getInt(cursor.getColumnIndex(NONEREAD));

			cursor.close();
		}
		return count;
	}
}
