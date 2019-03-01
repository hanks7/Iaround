
package net.iaround.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import net.iaround.entity.type.MessageBelongType;
import net.iaround.model.im.ChatRecordStatus;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.JsonUtil;
import net.iaround.ui.datamodel.ChatPersonalModel;
import net.iaround.ui.datamodel.User;

import java.util.LinkedHashMap;


/**
 * 最近联系人统计
 * 
 * @author linyg
 * 
 */
public class NearContactWorker extends ITableWorker
{
	public static final String TB_NAME = "tb_near_contact"; // 表名
	public static final String ID = "id"; // 本地id 0
	public static final String MUID = "muid"; // 所属id
	public static final String FUID = "fuid"; // 好友uid
	public static final String FNICKNAME = "fnickname"; // 好友昵称
	public static final String FICON = "ficon"; // 好友icon
	public static final String FVIP = "fvip"; // 好友vip
	public static final String FNOTE = "fnote"; // 备注
	public static final String FUSERINFO = "fuserinfo"; // 好友资料
	public static final String NONEREAD = "none_read"; // 未读数量
	public static final String LASTCONTENT = "last_content"; // 最后一条消息数
	public static final String LASTDATETIME = "last_datetime";// 最后更新时间
	public static final String CHAT_STATUS = "chat_status";// 聊天状况1为发送中，2为已送达，3为已读，4为失败
															// @ChatRecordStatus
	public static final String SUBGROUP = "subgroup";// @SubGroupType,分组字段，1.私聊列表，2.发出搭讪，3.收到搭讪
	public static final String QUIETSEEN = "quiet_seen";// 是否已经悄悄看过，0：没有悄悄看过，1：悄悄看过
	
	/** 消息表创建sql **/
	public static final String tableSql = "CREATE TABLE IF NOT EXISTS " + TB_NAME + " ( " + ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + MUID + " VERCHAR(15)," + FUID
			+ " VERCHAR(15)," + FNICKNAME + " VERCHAR(50)," + FICON + " VERCHAR(100), "
			+ FNOTE + " VERCHAR(50), " + FUSERINFO + " TEXT," + NONEREAD + " INTEGER,"
			+ LASTCONTENT + " TEXT," + LASTDATETIME + " VERCHAR(20)," + CHAT_STATUS
			+ " VERCHAR(15)," + SUBGROUP + " INTEGER(11) DEFAULT 1, " + QUIETSEEN
			+ " INTEGER DEFAULT 0);";
	
	public static final String[ ] selectors =
		{ ID , MUID , FUID , FNICKNAME , FICON , FNOTE , FUSERINFO , NONEREAD , LASTCONTENT ,
				LASTDATETIME , CHAT_STATUS , SUBGROUP , QUIETSEEN };
	
	protected NearContactWorker(Context context )
	{
		super( context , ID , TB_NAME );
	}
	
	
	/** ------------ 增 ------------ **/
	
	
	
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
	public int deleteAll( long uid )
	{
		String where = MUID + " = " + uid;
		return delete( where );
	}
	
	/** ------------ 改 ------------ **/
	
	/**
	 * 更新最近对应关系的未读数为0
	 * 
	 * @param mUid
	 * @param fUid
	 * @time 2011-7-6 下午04:59:29
	 * @author:linyg
	 */
	public void updateStatus(String mUid , String fUid )
	{
		String where = MUID + " = " + mUid + " AND " + FUID + " = " + fUid;
		ContentValues values = new ContentValues( );
		values.put( NONEREAD , 0 );
		onUpdate( values , where );
	}
	
	/** 更改未读数量 */
	public void updateUnread( long muid , long fuid , int unread )
	{
		String where = MUID + " = " + muid + " AND " + FUID + " = " + fuid;
		ContentValues values = new ContentValues( );
		values.put( NONEREAD , unread );
		onUpdate( values , where );
	}
	
	/**
	 * 更新最近联系人的消息状态
	 * 
	 * @param mUid
	 *            用户的id
	 * @param fUid
	 *            联系人的id
	 * @param dateTime
	 *            消息的本地时间戳
	 */
	public void updateStatus(String mUid , String fUid , String dateTime , String status )
	{
		String where = MUID + " = " + mUid + " AND " + FUID + " = " + fUid + " AND "
				+ LASTDATETIME + " = " + dateTime;
		ContentValues values = new ContentValues( );
		values.put( CHAT_STATUS , status );
		onUpdate( values , where );
	}
	
	/**
	 * 更新最近联系人的状态，如果传进来的dateTime大于则把消息的状态改为已读
	 * 
	 * @param mUid
	 * @param fUid
	 * @param dateTime
	 */
	public void updateRecordStatus(String mUid , String fUid , long dateTime )
	{
		String where = MUID + " = " + mUid + " AND " + FUID + " = " + fUid + " AND "
				+ CHAT_STATUS + " <> " + ChatRecordStatus.NONE;
		Cursor cursor = onSelect( selectors , where );
		
		if ( cursor != null && cursor.moveToFirst( ) )
		{
			long lastDataTime = cursor.getLong( 9 );// last_datatime
			int localId = cursor.getInt( 0 );
			String upDateWhere = ID + "=" + localId;
			if ( dateTime >= lastDataTime )
			{
				ContentValues values = new ContentValues( );
				values.put( CHAT_STATUS , "3" );
				onUpdate( values , upDateWhere );
			}
		}
		if(null!=cursor){
			cursor.close();
		}
	}
	
	/**
	 * 更新最近联系人的数据库，如果已经存在聊天,更新内容、时间、状态；不存在就插入
	 * 
	 * @param mUid
	 * @param fUser
	 * @param content
	 * @param status
	 * @param dataTime
	 *            时间戳
	 * @param sendType
	 *            消息的发送类型，1为自己发送，2为接收
	 */
	public void updateContact(String mUid , User fUser , String content , int status ,
							  String dataTime , int sendType , int subGroup )
	{
		String where = MUID + " = " + mUid + " AND " + FUID + " = " + fUser.getUid( );
		String upWhere = "";
		Cursor cursor = onSelect( selectors , where );
		int noneReadCount = 0;
		boolean recordExsit = false;
		if ( cursor != null && cursor.moveToFirst( ) )
		{
			// 消息存在
			noneReadCount = cursor.getInt( cursor.getColumnIndex( NONEREAD ) );
			recordExsit = true;
			upWhere = ID + "=" + cursor.getInt( cursor.getColumnIndex( ID ) );
		}
		else
		{
			// 消息不存在
		}
		
		if ( sendType == MessageBelongType.RECEIVE )
		{
			noneReadCount++;
		}
		else if ( sendType == MessageBelongType.SEND )
		{
			noneReadCount = 0;
		}
		
		int quietSee = 0;
		if ( ChatPersonalModel.getInstance( ).isInPersonalChat( )
				&& fUser.getUid( ) == ChatPersonalModel.getInstance( ).getChatTargetId( ) )
		{
			if ( ChatPersonalModel.getInstance( ).isQuietMode( ) )
				quietSee = 1;// 当为悄悄查看模式时，NearContact表还是保持悄悄看过
			else
				noneReadCount = 0;// 当在私聊内收到对方消息且不为悄悄模式时，更新内容但未读数清零
		}
		
		
		ContentValues values = new ContentValues( );
		values.put( MUID , mUid );
		values.put( FUID , String.valueOf( fUser.getUid( ) ) );
		values.put( FNICKNAME , String.valueOf( fUser.getNickname( ) ) );
		values.put( FICON , fUser.getIcon( ) );
		String noteName = fUser.getNoteName( false );
		values.put( FNOTE , noteName == null ? "" : noteName );
		
		// 好友基本信息
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "fuid" , String.valueOf( fUser.getUid( ) ) );
		map.put( "fnickname" , String.valueOf( fUser.getNickname( ) ) );
		map.put( "ficon" , fUser.getIcon( ) );
		map.put( "fvip" , String.valueOf( fUser.getViplevel( ) ) );
		map.put( "svip" , fUser.getSVip( ) );
		map.put( "fgender" , String.valueOf( fUser.getSexIndex( ) ) );
		map.put( "flat" , String.valueOf( fUser.getLat( ) ) );
		map.put( "flng" , String.valueOf( fUser.getLng( ) ) );
		map.put( "fage" , String.valueOf( fUser.getAge( ) ) );
		map.put( "fnote" , String.valueOf( fUser.getNoteName( false ) ) );
		map.put("fusertype",String.valueOf( fUser.getUserType()));
		String friendinfo = JsonUtil.mapToJsonString( map );
		values.put( FUSERINFO , friendinfo );
		values.put( NONEREAD , String.valueOf( noneReadCount ) );
		values.put( LASTCONTENT , content );
		values.put( LASTDATETIME , dataTime );
		values.put( CHAT_STATUS , status );
		values.put( QUIETSEEN , quietSee );
		
		if ( subGroup > 0 )
		{
			values.put( SUBGROUP , subGroup );
		}
		
		if ( recordExsit )
		{
			// 更新
			onUpdate( values , upWhere );
		}
		else
		{
			// 插入
			onInsert( values );
		}
		// cursor should close
		if ( cursor != null){
			cursor.close();
		}
		//
	}
	
	/** 删除对话后更新消息列表表 */
	public void delRecordUpdateContact(String mUid , User fUser , String content ,
									   int status , String dataTime , int sendType , int noRead )
	{
		String where = MUID + " = " + mUid + " AND " + FUID + " = " + fUser.getUid( );
		String upWhere = "";
		Cursor cursor = onSelect( selectors , where );
		boolean recordExsit = false;
		if ( cursor != null && cursor.moveToFirst( ) )
		{
			// 消息存在
			recordExsit = true;
			upWhere = ID + "=" + cursor.getInt( cursor.getColumnIndex( ID ) );
		}
		else
		{
			// 消息不存在
		}
		if(cursor!=null){
			cursor.close();
		}
		
		
		int quietSee = 0;
		if ( ChatPersonalModel.getInstance( ).isInPersonalChat( )
				&& fUser.getUid( ) == ChatPersonalModel.getInstance( ).getChatTargetId( )
				&& ChatPersonalModel.getInstance( ).isQuietMode( ) )
		{
			quietSee = 1;// 当为悄悄查看模式时，NearContact表还是保持悄悄看过
		}
		
		
		ContentValues values = new ContentValues( );
		values.put( MUID , mUid );
		values.put( FUID , String.valueOf( fUser.getUid( ) ) );
		values.put( FNICKNAME , String.valueOf( fUser.getNickname( ) ) );
		values.put( FICON , fUser.getIcon( ) );
		String noteName = fUser.getNoteName( false );
		values.put( FNOTE , noteName == null ? "" : noteName );
		
		// 好友基本信息
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "fuid" , String.valueOf( fUser.getUid( ) ) );
		map.put( "fnickname" , String.valueOf( fUser.getNickname( ) ) );
		map.put( "ficon" , fUser.getIcon( ) );
		map.put( "fvip" , String.valueOf( fUser.getViplevel( ) ) );
		map.put( "svip" , fUser.getSVip( ) );
		map.put( "fgender" , String.valueOf( fUser.getSexIndex( ) ) );
		map.put( "flat" , String.valueOf( fUser.getLat( ) ) );
		map.put( "flng" , String.valueOf( fUser.getLng( ) ) );
		map.put( "fage" , String.valueOf( fUser.getAge( ) ) );
		map.put( "fnote" , String.valueOf( fUser.getNoteName( false ) ) );
		String friendinfo = JsonUtil.mapToJsonString( map );
		values.put( FUSERINFO , friendinfo );
		values.put( NONEREAD , String.valueOf( noRead ) );
		values.put( LASTCONTENT , content );
		values.put( LASTDATETIME , dataTime );
		values.put( CHAT_STATUS , status );
		values.put( QUIETSEEN , quietSee );
		
		
		if ( recordExsit )
		{
			// 更新
			onUpdate( values , upWhere );
		}
		else
		{
			// 插入
			onInsert( values );
		}

	}
	
	/**
	 * 更改本地用户数据
	 * 
	 * @param mUid
	 * @param fUid
	 * @param note
	 * @param icon
	 */
	public void updateLocalUser(long mUid , long fUid , String note , String iconUrl )
	{
		String where = MUID + " = " + mUid + " AND " + FUID + " = " + fUid;
		ContentValues values = new ContentValues( );
		values.put( FNOTE , note );
		if ( !CommonFunction.isEmptyOrNullStr( iconUrl ) )
		{
			values.put( FICON , iconUrl );
		}
		CommonFunction.log( "updateNote" , "id:" + onUpdate( values , where ) );
	}
	
	/**
	 * 清空所有未读数量，即标记所有已读
	 * 
	 * @param uid
	 * @return
	 */
	public int onClearAllNoRead( long uid )
	{
		ContentValues values = new ContentValues( );
		values.put( NONEREAD , 0 );
		values.put( QUIETSEEN , 0 );
		String upWhere = MUID + "=" + uid + " AND " + NONEREAD + " >=0 ";
		return onUpdate( values , upWhere );
	}
	
	/**
	 * 更新与某人的
	 * 
	 * @param mUid
	 * @param fUid
	 * @param subgroupType
	 * @return
	 */
	public int onModifySubgroup( long mUid , long fUid , int subgroupType )
	{
		String where = MUID + " = " + mUid + " AND " + FUID + " = " + fUid;
		ContentValues values = new ContentValues( );
		values.put( SUBGROUP , subgroupType );
		return onUpdate( values , where );
	}
	
	/**
	 * 更新某用户接收搭讪的未读数为0
	 */
	public void updateReceiveAccost( String mUid )
	{
		String where = MUID + " = " + mUid + " AND " + SUBGROUP + " = " + "3";
		ContentValues values = new ContentValues( );
		values.put( NONEREAD , 0 );
		onUpdate( values , where );
	}
	
	/** 设置是否悄悄查看过，0：没有悄悄看过，1：悄悄看过 */
	public void updateQuietSeen( long muid , long fuid , int seen )
	{
		String where = MUID + " = " + muid + " AND " + FUID + " = " + fuid;
		ContentValues values = new ContentValues( );
		values.put( QUIETSEEN , seen );
		onUpdate( values , where );
	}
	
	/** ------------ 查 ------------ **/
	
	
	/**
	 * 按照分页模式查找
	 * 
	 * @param mUid
	 * @param start
	 * @param amount
	 * @return
	 */
	public Cursor selectPage(String mUid , int start , int amount )
	{
		String where = MUID + " = " + mUid + " ORDER BY " + NONEREAD + " DESC," + LASTDATETIME
				+ " DESC ";
//		String where = MUID + " = " + mUid + " ORDER BY " + NONEREAD + " DESC," + LASTDATETIME
//				+ " DESC LIMIT " + start + "," + amount;
		return onSelect( selectors , where );
	}
	
	/**
	 * 按页查找发出搭讪
	 * */
	public Cursor selectSendAccostPage(String mUid )
	{
		String where = MUID + " = " + mUid + " AND " + SUBGROUP + " = " + " 2 " + " ORDER BY "
				+ NONEREAD + " DESC," + LASTDATETIME + " DESC ";
		return onSelect( selectors , where );
	}
	
	/**
	 * 按页查找收到搭讪
	 * */
	public Cursor selectRecieveAccostPage(String mUid )
	{
		String where = MUID + " = " + mUid + " AND " + SUBGROUP + " = " + " 3 " + " ORDER BY "
				+ NONEREAD + " DESC," + LASTDATETIME + " DESC" ;
		return onSelect( selectors , where );
	}
	
	/**
	 * 读取未读私聊消息的数量总和
	 * 
	 * @param uid
	 * @return
	 */
	public int countNoRead( String uid )
	{
		int count = 0;
		// 获取未读私聊数
		String where = MUID + " = " + uid + " AND " + " ( " + SUBGROUP + " = " + " 1 "
				+ " OR " + SUBGROUP + " = " + " 4 " + " ) " + " ORDER BY " + LASTDATETIME
				+ " DESC LIMIT 0,100";
		String[ ] strs =
			{ "SUM(" + NONEREAD + ")" };
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
	 * 获取未读私聊信息是由多少个人发送过来的
	 * */
	public int countNoReadSender( String uid )
	{
		int count = 0;
		String where = MUID + " = " + uid + " AND " + NONEREAD + " > " + " 0 " + " AND "
				+ SUBGROUP + " = " + " 1 " + " ORDER BY " + LASTDATETIME + " DESC LIMIT 0,100";
		String[ ] strs =
			{ NONEREAD };
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
	 * 获取未读收到搭讪人数数目
	 * 
	 * @author tanzy
	 * @return -1：没有收到搭讪，0：有收到搭讪但无未读搭讪，1+：有收到搭讪且返回未读搭讪的人数
	 * */
	public int getReceiveAccoustSnender( String uid )
	{
		int count = 0;
		String where = MUID + " = " + uid + " AND " + SUBGROUP + " = " + " 3 " + " ORDER BY "
				+ LASTDATETIME + " DESC LIMIT 0,100";
		String[ ] strs =
			{ NONEREAD };
		Cursor cursor = onSelect( strs , where );
		try
		{
			int sender = cursor.getCount( );
			if ( sender <= 0 )
			{
				count = -1;
			}
			else if ( sender > 0 )
			{
				for ( cursor.moveToFirst( ) ; !cursor.isAfterLast( )
						&& ( cursor.getString( 0 ) != null ) ; cursor.moveToNext( ) )
				{
					if ( cursor.getInt( 0 ) > 0 )
					{
						count++;
					}
				}
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
	 * 获取未读收到搭讪数目
	 * 
	 * @author tanzy
	 * @return -1：没有收到搭讪，0：有收到搭讪但无未读搭讪，1+：有收到搭讪且返回未读搭讪数
	 * */
	public int getReceiveAccoustCount( String uid )
	{
		int count = 0;
		String where = MUID + " = " + uid + " AND " + SUBGROUP + " = " + " 3 " + " ORDER BY "
				+ LASTDATETIME + " DESC LIMIT 0,100";
		String[ ] strs =
			{ NONEREAD };
		Cursor cursor = onSelect( strs , where );
		try
		{
			int sender = cursor.getCount( );
			if ( sender <= 0 )
			{
				count = -1;
			}
			else if ( sender > 0 )
			{
				for ( cursor.moveToFirst( ) ; !cursor.isAfterLast( )
						&& ( cursor.getString( 0 ) != null ) ; cursor.moveToNext( ) )
				{
					count += cursor.getInt( 0 );
				}
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
	 * 获取发出搭讪数目
	 * 
	 * @author tanzy
	 * @return -1：没有发出搭讪，0+：有发出搭讪且返回发出数
	 * */
	public int getSendAccoustCount( String uid )
	{
		int count = 0;
		String where = MUID + " = " + uid + " AND " + SUBGROUP + " = " + " 2 " + " ORDER BY "
				+ LASTDATETIME + " DESC LIMIT 0,100";
		String[ ] strs =
			{ NONEREAD };
		Cursor cursor = onSelect( strs , where );
		try
		{
			int sender = cursor.getCount( );
			if ( sender <= 0 )
			{
				count = -1;
			}
			else if ( sender > 0 )
			{
				for ( cursor.moveToFirst( ) ; !cursor.isAfterLast( )
						&& ( cursor.getString( 0 ) != null ) ; cursor.moveToNext( ) )
				{
					count += cursor.getInt( 0 );
				}
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
	 * 获取最新的发出搭讪消息的时间
	 * 
	 * @author tanzy
	 * */
	public long getLatestTimeInSendAccost( String uid )
	{
		long time = -1;
		String where = MUID + " = " + uid + " AND " + SUBGROUP + " = " + " 2 " + " ORDER BY "
				+ LASTDATETIME + " DESC LIMIT 0,100";
		String[ ] strs =
			{ LASTDATETIME };
		Cursor cursor = onSelect( strs , where );
		try
		{
			int sender = cursor.getCount( );
			if ( sender <= 0 )
			{
				time = -1;
			}
			else if ( sender > 0 )
			{
				cursor.moveToFirst( );
				if ( cursor.getString( 0 ) != null )
				{
					time = cursor.getLong( 0 );
				}
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
		return time;
	}
	
	/**
	 * 删除某用户对应subgroup的记录
	 * */
	public int deleteSubgroupRecord(String uid , int subGroup )
	{
		String where = MUID + " = " + uid + " AND " + SUBGROUP + " = " + subGroup;
		return delete( where );
	}
	
	/**
	 * 删除某用户单条发出搭讪记录
	 * */
	public int deleteOneSendAccostRecord(String muid , String fuid )
	{
		String where = MUID + " = " + muid + " AND " + FUID + " = " + fuid + " AND "
				+ SUBGROUP + " = " + "2";
		return delete( where );
	}
	
}
