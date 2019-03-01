
package net.iaround.ui.datamodel;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.database.DatabaseFactory;
import net.iaround.database.GroupContactWorker;
import net.iaround.database.GroupMessageWorker;
import net.iaround.entity.type.ChatFromType;
import net.iaround.entity.type.MessageBelongType;
import net.iaround.model.entity.Item;
import net.iaround.model.im.ChatRecord;
import net.iaround.model.im.GroupChatMessage;
import net.iaround.model.im.GroupMessagesBean;
import net.iaround.model.im.TransportMessage;
import net.iaround.model.im.UserTypeOne;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.RankingTitleUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.chat.SuperChat;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.group.activity.GroupChatTopicActivity.HistoryRecorder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 用于圈聊消息的数据操作<br>
 * <b>另外:<b/><br>
 * --圈子管理 @GroupModel <br>
 * --圈话题管理 @TopicModel <br>
 */
public class GroupChatListModel extends Model
{
	
	private final String TAG = GroupChatListModel.class.getName( );
	
	private final String GROUP_DRAFT_KEY = "chat_group_draft";
	private static GroupChatListModel groupModel;
	private Map< String , ForbidItem > forbidGroupSay; // 禁言列表
	private HashMap< Long , Long > recordLocalIdMap;// socket成功发送聊天记录后，聊天记录被插入到数据库的本地ID的map
	
	private GroupChatListModel( )
	{
		forbidGroupSay = new HashMap< String , ForbidItem >( );
		recordLocalIdMap = new HashMap< Long , Long >( );
	}
	
	public static GroupChatListModel getInstance( )
	{
		if ( groupModel == null )
		{
			groupModel = new GroupChatListModel( );
		}
		return groupModel;
	}
	
	/**
	 * 设置聊天草稿
	 * 
	 * @param groupid
	 * @param msg
	 */
	public void setChatDraft(Context context , String groupid , String msg )
	{
		SharedPreferenceUtil.getInstance( context )
				.putString( GROUP_DRAFT_KEY + groupid , msg );
	}
	
	/**
	 * 获取草稿内容
	 * 
	 * @param groupid
	 * @return
	 */
	public String getChatDraft(Context context , String groupid )
	{
		return SharedPreferenceUtil.getInstance( context ).getString(
				GROUP_DRAFT_KEY + groupid );
	}
	
	/**
	 * 接收到圈子禁言情况
	 * 
	 * @param msg
	 */
	public synchronized void updateForbidSay(Context context , TransportMessage msg )
	{
		String content = msg.getContentBody( );
		ForbidItem item = new ForbidItem( );
		try
		{
			JSONObject json = new JSONObject( content );
			item.setGroupId( CommonFunction.jsonOptString( json , "groupid" ) );
			item.setGroupName( CommonFunction.jsonOptString( json , "groupname" ) );
			item.setForbidtype( json.optInt( "forbidtype" ) );
			item.setTime( json.optLong( "expiredtime" ) );
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
			return;
		}
		if ( item.getForbidtype( ) == 1 )
		{ // 解禁
			if ( forbidGroupSay.containsKey( item.getGroupId( ) ) )
			{
				forbidGroupSay.remove( item.getGroupId( ) );
			}
		}
		else
		{ // 禁言
			forbidGroupSay.put( item.getGroupId( ) , item );
		}
	}
	
	/**
	 * 检查是否被圈子禁言
	 * 
	 * @param groupid
	 *            return true 为已被禁言,false 为可以发言
	 */
	public boolean checkGroupSay(Context context , String groupid , HttpCallBack callback )
	{
		if ( forbidGroupSay.containsKey( groupid ) )
		{
			ForbidItem item = forbidGroupSay.get( groupid );
			long expiredTime = item.getTime( );
			Date d = new Date( );
			long nowTime = Common.getInstance( ).serverToClientTime + d.getTime( );
			// 当时间未到，则确实在禁言时间内
			if ( nowTime < expiredTime )
			{
				String tips = "";
				expiredTime -= nowTime;
				if ( expiredTime < 0 || expiredTime / 1000 > 30 * 24 * 60 * 60 )
				{ // 永久禁言
					tips = context.getString( R.string.you_forbided_forever );
				}
				else
				{
					long sec = expiredTime / 1000;
					int min = ( int ) ( sec / 60 );
					int hour = sec / 60 > 0 ? min / 60 : 0;
					min = min % 60 > 0 ? ( min % 60 ) : 1;
					tips = String.format( context.getString( R.string.group_forbid_say_tip ) ,
							hour , min );
				}
				CommonFunction.showToast( context , tips , 1 );
				
				return true;
				// 时间到了，则判断用户是否修改了时间
			}
			else
			{
				if ( context instanceof SuperActivity)
				{
					CommonFunction.showToast( context ,
							context.getString( R.string.group_user_forbid_timeout ) , 1 );
					// 请求服务器检查
					long uid = Common.getInstance( ).loginUser.getUid( );
					GroupHttpProtocol.groupCheckForbid( context , groupid , uid , callback );
				}
				else
				{
					return false;
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 检查该用户是否禁言的返回情况
	 * 
	 * @param result
	 */
	public void checkforbidCallBack(Context context , String result )
	{
		try
		{
			JSONObject json = new JSONObject( result );
			if ( json.optInt( "status" ) == 200 )
			{
				boolean isforbid = CommonFunction.jsonOptString(json, "isforbid").equals(
                        "y");
				String groupid = CommonFunction.jsonOptString( json , "groupid" );
				if ( isforbid )
				{ // 还在禁言时间内，不能发言
					long servertime = json.optLong( "servertime" );
					long clientTime = ( new Date( ) ).getTime( );
					Common.getInstance( ).serverToClientTime = servertime - clientTime;
					
					// 计算并提示，离禁言时间还有多长
					ForbidItem item = forbidGroupSay.get( groupid );
					long expiredTime = item.getTime( );
					Date d = new Date( );
					long nowTime = Common.getInstance( ).serverToClientTime + d.getTime( );
					
					if ( nowTime < expiredTime )
					{
						String tips = "";
						expiredTime -= nowTime;
						if ( expiredTime < 0 || expiredTime / 1000 > 30 * 24 * 60 * 60 )
						{ // 永久禁言
							tips = context.getString( R.string.you_forbided_forever );
						}
						else
						{
							long sec = expiredTime / 1000;
							int min = ( int ) sec / 60;
							int hour = min / 60 > 0 ? min / 60 : 0;
							min = min % 60 > 0 ? ( min % 60 ) : 1;
							tips = String.format(
									context.getString( R.string.group_forbid_say_tip ) , hour ,
									min );
						}
						
						CommonFunction.showToast( context , tips , 1 );
					}
				}
				else
				{ // 已经解禁
					forbidGroupSay.remove( groupid );
					CommonFunction.showToast( context ,
							context.getString( R.string.group_user_can_talk ) , 1 );
				}
			}
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
	}
	
	class ForbidItem
	{
		private String groupId;
		private String groupName;
		private long time;
		private int forbidtype;
		
		public int getForbidtype( )
		{
			return forbidtype;
		}
		
		public void setForbidtype( int forbidtype )
		{
			this.forbidtype = forbidtype;
		}
		
		public String getGroupId( )
		{
			return groupId;
		}
		
		public void setGroupId( String groupId )
		{
			this.groupId = groupId;
		}
		
		public String getGroupName( )
		{
			return groupName;
		}
		
		public void setGroupName( String groupName )
		{
			this.groupName = groupName;
		}
		
		public long getTime( )
		{
			return time;
		}
		
		public void setTime( long time )
		{
			this.time = time;
		}
	}
	
	public void reset( )
	{
		
		if ( forbidGroupSay != null )
			forbidGroupSay.clear( );
		
		groupModel = null;
	}
	
	public ArrayList<GroupMessagesBean> onSearchLastMessageTime(Context mContext )
	{
		long mUid = Common.getInstance( ).loginUser.getUid( );
		GroupContactWorker gcWorker = DatabaseFactory.getGroupContactWorker( mContext );
		Cursor cursor = gcWorker.selectPage( String.valueOf( mUid ) );
		
		ArrayList< GroupMessagesBean > list = new ArrayList< GroupMessagesBean >( );
		
		if ( cursor != null && cursor.moveToFirst( ) )
		{
			do
			{
				GroupMessagesBean bean = new GroupMessagesBean( );
				bean.groupId = cursor.getInt( cursor
						.getColumnIndex( GroupContactWorker.GROUPID ) );
				bean.lastTime = cursor.getLong( cursor
						.getColumnIndex( GroupContactWorker.TIME ) );
				bean.noreadNum = cursor.getInt( cursor
						.getColumnIndex( GroupContactWorker.NONEREAD ) );
				list.add( bean );
			}
			while ( cursor.moveToNext( ) );
		}
		
		cursor.close( );
		return list;
	}
	
	/**
	 * 修改数据库中消息的附件地址
	 * 
	 * @param mContext
	 * @param localID
	 * @param url
	 */
	public void modifyGroupMessageAttachment(Context mContext , long localID , String url )
	{
		GroupMessageWorker groupMessageDB = DatabaseFactory.getGroupMessageWorker( mContext );
		
		Cursor cursor = groupMessageDB.onSelectLocalID( localID );
		if ( cursor != null && cursor.moveToFirst( ) )
		{
			int contentIndex = cursor.getColumnIndex( GroupMessageWorker.CONTENT );
			String content = cursor.getString( contentIndex );
			
			// 把服务器下发的URL更新到数据库中
			JSONObject json = null;
			try
			{
				json = new JSONObject( content );
				json.put( "attachment" , url );
			}
			catch ( JSONException e )
			{
				e.printStackTrace( );
			}
			
			groupMessageDB.onUpdateRecordContent( localID , json.toString( ) );
		}
		cursor.close( );
	}
	
	/**
	 * 修改数据库中消息的内容
	 * 
	 * @param mContext
	 * @param localID
	 * @param content
	 */
	public void modifyGroupMessageContent(Context mContext , long localID , String content )
	{
		GroupMessageWorker groupMessageDB = DatabaseFactory.getGroupMessageWorker( mContext );
		
		Cursor cursor = groupMessageDB.onSelectLocalID( localID );
		if ( cursor != null && cursor.moveToFirst( ) )
		{
			int contentIndex = cursor.getColumnIndex( GroupMessageWorker.CONTENT );
			String jsonStr = cursor.getString( contentIndex );
			
			// 把服务器下发的URL更新到数据库中
			JSONObject json = null;
			try
			{
				json = new JSONObject( jsonStr );
				json.put( "content" , content );
			}
			catch ( JSONException e )
			{
				e.printStackTrace( );
			}
			
			groupMessageDB.onUpdateRecordContent( localID , json.toString( ) );
		}
		cursor.close( );
	}
	
	/**
	 * 根据本地数据库id删除一条圈聊记录
	 * 
	 * @param context
	 * @param localId
	 */
	public void removeGroupMessageByLoaclId(Context context , long localId )
	{
		GroupMessageWorker groupMessageDB = DatabaseFactory.getGroupMessageWorker( context );
		String localIdStr = String.valueOf( localId );
		groupMessageDB.onDeleteRecord( localIdStr );
	}
	
	/**
	 * 从本地数据库加载群的历史聊天记录
	 * 
	 * @param uid
	 *            起始msgid
	 * @param amount
	 *            每次读取几条数据
	 * @return ArrayList<ChatRecord>
	 */
	public ArrayList<ChatRecord> loadGroupHistoryMsgFromDB(Context context , long uid ,
														   long groupid , long start , int amount )
	{
		ArrayList< ChatRecord > records = new ArrayList< ChatRecord >( );
		GroupMessageWorker db = DatabaseFactory.getGroupMessageWorker( context );
		Cursor cursor = db.onSelectPage( uid , groupid , start , amount );
		
		try
		{
			cursor.moveToLast( );
			while ( ! ( cursor.isBeforeFirst( ) ) )
			{
				ChatRecord record;
				int contentIndex = cursor.getColumnIndex( GroupMessageWorker.CONTENT );
				String strJson = cursor.getString( contentIndex );
				
				String status = cursor.getString( cursor
						.getColumnIndex( GroupMessageWorker.STATUS ) );
				try
				{
					record = parseGroupRecord( strJson );
					record.setLocid( cursor.getLong( cursor
							.getColumnIndex( GroupMessageWorker.ID ) ) );
					record.setStatus( Integer.valueOf( status ) );
					records.add( record );
				}
				catch ( JSONException e )
				{
					e.printStackTrace( );
				}
				finally
				{
					cursor.moveToPrevious( );
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
			if ( db != null )
				db.onClose( );
		}
		return records;
	}
	
	public ArrayList< ChatRecord > getGroupChatRecordByLocalId(Context context ,
                                                               long groupid , long maxLocalId , int amount )
	{
		
		ArrayList< ChatRecord > records = new ArrayList< ChatRecord >( );
		GroupMessageWorker db = DatabaseFactory.getGroupMessageWorker( context );
		
		Cursor cursor;
		if ( maxLocalId == 0 )
		{
			cursor = db.onSelectRecord( groupid , amount );
		}
		else
		{
			cursor = db.onSelectRecordsByLocalId( groupid , maxLocalId , amount );
		}
		
		try
		{
			cursor.moveToLast( );
			while ( ! ( cursor.isBeforeFirst( ) ) )
			{
				ChatRecord record;
				int contentIndex = cursor.getColumnIndex( GroupMessageWorker.CONTENT );
				String strJson = cursor.getString( contentIndex );
				
				String status = cursor.getString( cursor
						.getColumnIndex( GroupMessageWorker.STATUS ) );
				try
				{
					record = parseGroupRecord( strJson );
					record.setLocid( cursor.getLong( cursor
							.getColumnIndex( GroupMessageWorker.ID ) ) );
					record.setStatus( Integer.valueOf( status ) );
					records.add( record );
				}
				catch ( JSONException e )
				{
					e.printStackTrace( );
				}
				finally
				{
					cursor.moveToPrevious( );
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
			if ( db != null )
				db.onClose( );
		}
		return records;
	}
	
	/**
	 * 取数据库的数据<br>
	 * 1. 如果给的increaseId,发现数据不连续,返回false,recorder的recordlist数据为空,返回false<br>
	 * 2. 如果给的increaseId是连续的,但是拿出来的数据,因为有删除的情况,这个时候拿出来的数据不够一页,所以,再去拿下一页的数据,
	 * 保证数据是大于一页的
	 * ,且把上一页的数据缓存在recorder中,修改recorder中的自增id.知道拿够一页,或者数据库已经没有数据了,return true<br>
	 * [有自增id的通过自增id排序获取,自增id为0的,通过timestamp来获取] 3.
	 * 如果请求服务端的那一段数据,返回的还是不连续的[服务端会出现先发的数据空缺],所以需要记录同一段数据不请求2次<br>
	 * 
	 * @param context
	 * @param groupid
	 * @param amount
	 * @param recorder
	 * @return true表示取到那一段的数据,false表示取不到那一段数据<br>
	 *         情况: <br>
	 *         1.返回true,但是数量不足一页[1.数据还有,2.数据已经没了] <br>
	 *         2.返回true,数据满一页<br>
	 *         3.返回false,数据不连续,需要请求服务端,<br>
	 */
	public boolean getRecordDatas(Context context , long groupid , int amount ,
                                  HistoryRecorder recorder )
	{
		GroupMessageWorker db = DatabaseFactory.getGroupMessageWorker( context );
		
		// 如果已经不用请求服务端,那么就不判断自增id的连续性
		long maxIncreaseId = recorder.getMaxRecordIncreaseId( );
		boolean isReqeustedServer = recorder.getServerRequestSet( ).contains(
				recorder.getMaxRecordMessageId( ) );
		// 服务端没有数据或者已经请求过该段数据的时候,拿本地数据
		if ( recorder.isServerNoData( ) || isReqeustedServer ||recorder.getCurrentBuffer( ).isEmpty())
		{
			long minIncreaseId = Math.max( maxIncreaseId - amount , 0 );
			Cursor cursor = db.onSelectedIncreaseRangeOfAll( groupid , maxIncreaseId ,
					minIncreaseId , amount );
			
			if ( cursor == null || cursor.getCount( ) <= 0 )
			{
				recorder.setLocalNoData( true );
				if ( isReqeustedServer )
				{
					recorder.setServerNoData( true );
				}
			}
			else
			{
				if ( isReqeustedServer )
				{
					recorder.setLocalNoData( false );
				}
				else
				{
					if ( cursor.getCount( ) < amount )
					{
						recorder.setLocalNoData( true );
					}
					else
					{
						recorder.setLocalNoData( false );
					}
				}
				
				
				cursor.moveToLast( );
				recorder.setMaxRecordMessageId( cursor.getLong( cursor
						.getColumnIndex( GroupMessageWorker.MESSAGEID ) ) );
				recorder.setMaxRecordIncreaseId( cursor.getLong( cursor
						.getColumnIndex( GroupMessageWorker.INCREASEID ) ) );
				recorder.setMaxRecordSendTime( cursor.getLong( cursor
						.getColumnIndex( GroupMessageWorker.TIMESTAMP ) ) );
				recorder.getCurrentBuffer( ).addAll( parseGroupRecordList( cursor ) );
				CommonFunction.log( "sherlock" , "call set maxTime in getRecordDatas == "+recorder.getMaxRecordSendTime( ) );
			}
			// cursor should close
			if ( cursor != null){
				cursor.close();
			}
			//
			return true;
		}
		else
		{
			// 还需要请求服务端
			
			if ( maxIncreaseId > 1 )
			{// 如果自增id大于一的话,说明还需要判断是否有连续的数据没请求下来
				long continueMinIncreaseId = Math.max( maxIncreaseId - amount , 1 );
				boolean isContinue = isContinuouslyIncreaseId( context , groupid ,
						maxIncreaseId , continueMinIncreaseId , amount );
				CommonFunction.log( "sherlock" , "isContunue == " + isContinue );
				if ( isContinue || isReqeustedServer )
				{// 连续的id,取本地的数据 或者
					// 不是连续的但已经请求过这段数据
					
					long getDataMinIncreaseId = Math.max( maxIncreaseId - amount , 0 );
					Cursor cursor = db.onSelectedIncreaseRangeOfAll( groupid , maxIncreaseId ,
							getDataMinIncreaseId , amount );
					
					if ( cursor == null )
					{
						recorder.setLocalNoData( true );
					}
					else
					{
						
						// 如果拿的数据不足一页,同时拿的数据是连续的,才可以说明本地已经没有数据了
						// 情况:数据少于一页,但数据不连续.服务端出现自增id不连续的情况下
						if ( cursor.getCount( ) < amount && isContinue )
						{
							recorder.setLocalNoData( true );
						}
						else
						{
							recorder.setLocalNoData( false );
						}
						
						if ( cursor.getCount( ) <= 0 )
						{
							CommonFunction.log( "sherlock" , "setMaxRecordIncreaseId == "
									+ getDataMinIncreaseId );
							recorder.setMaxRecordIncreaseId( getDataMinIncreaseId );
						}
						else
						{
							cursor.moveToLast( );
							recorder.setMaxRecordMessageId( cursor.getLong( cursor
									.getColumnIndex( GroupMessageWorker.MESSAGEID ) ) );
							recorder.setMaxRecordIncreaseId( cursor.getLong( cursor
									.getColumnIndex( GroupMessageWorker.INCREASEID ) ) );
							recorder.setMaxRecordSendTime( cursor.getLong( cursor
									.getColumnIndex( GroupMessageWorker.TIMESTAMP ) ) );
							recorder.getCurrentBuffer( )
									.addAll( parseGroupRecordList( cursor ) );
						}
						
					}
					if(null!=cursor){
						cursor.close();
					}
					return true;
					
				}
				else
				{
					// 返回请求服务端
					return false;
				}
				
			}
			else
			{
				// 取本地自增id为0的一段,通过timestamp来取
				Cursor cursor = db.onSelectedDataTimeRange( groupid ,
						recorder.getMaxRecordSendTime( ) , amount );
				
				if ( cursor == null || cursor.getCount( ) <= 0 )
				{
					recorder.setLocalNoData( true );
				}
				else
				{
					
					if ( cursor.getCount( ) < amount )
					{
						recorder.setLocalNoData( true );
					}
					else
					{
						recorder.setLocalNoData( false );
					}
					
					cursor.moveToLast( );
					recorder.setMaxRecordMessageId( cursor.getLong( cursor
							.getColumnIndex( GroupMessageWorker.MESSAGEID ) ) );
					recorder.setMaxRecordIncreaseId( cursor.getLong( cursor
							.getColumnIndex( GroupMessageWorker.INCREASEID ) ) );
					recorder.setMaxRecordSendTime( cursor.getLong( cursor
							.getColumnIndex( GroupMessageWorker.TIMESTAMP ) ) );
					recorder.getCurrentBuffer( ).addAll( parseGroupRecordList( cursor ) );
				}
				
				return true;
			}
		}
	}
	
	private ArrayList< ChatRecord > parseGroupRecordList(Cursor cursor )
	{
		ArrayList< ChatRecord > recordList = new ArrayList< ChatRecord >( );
		
		if ( cursor != null )
		{
			cursor.moveToFirst( );
			
			do
			{
				ChatRecord record;
				int contentIndex = cursor.getColumnIndex( GroupMessageWorker.CONTENT );
				String strJson = cursor.getString( contentIndex );
				String status = cursor.getString( cursor
						.getColumnIndex( GroupMessageWorker.STATUS ) );
				int deleteFlag = cursor.getInt( cursor
						.getColumnIndex( GroupMessageWorker.DELETEFLAG ) );
				int msgid = cursor.getInt( cursor.getColumnIndex( GroupMessageWorker.MESSAGEID ) );
				try
				{
					if ( deleteFlag == 0 && msgid > 0 )
					{
						record = parseGroupRecord( strJson );
						record.setLocid( cursor.getLong( cursor
								.getColumnIndex( GroupMessageWorker.ID ) ) );
						record.setStatus( Integer.valueOf( status ) );
						recordList.add( record );
					}
				}
				catch ( JSONException e )
				{
					e.printStackTrace( );
				}
			}
			while ( cursor.moveToNext( ) );
		}
		
		return recordList;
	}
	
	/** 判断minIncreaseId至maxIncreaseId之间的数据是否时连续的 */
	public boolean isContinuouslyIncreaseId(Context context , long groupid ,
                                            long maxIncreaseId , long minIncreaseId , int amount )
	{
		
		int recordNum = ( int ) Math.min( maxIncreaseId - minIncreaseId + 1 , amount );
		
		GroupMessageWorker db = DatabaseFactory.getGroupMessageWorker( context );
		Cursor cursor = db.onSelectedIncreaseRange( groupid , maxIncreaseId , minIncreaseId ,
				amount );
		
		if ( cursor != null )
		{
			if ( cursor.getCount( ) == recordNum )
			{
				cursor.close( );
				return true;
			}
			else
			{
				cursor.close( );
				return false;
			}
		}
		else
		{
			return false;
		}
		
	}
	
	public void getMaxIncreaseId(Context context , long groupid , HistoryRecorder recorder )
	{
		
		GroupMessageWorker db = DatabaseFactory.getGroupMessageWorker( context );
		
		long maxIncreaseId = 0;
		long lastMaxIncreaseId = recorder.getMaxRecordIncreaseId( );
		if ( lastMaxIncreaseId == 0 )
		{
			// 获取数据库中最大自增id记录或者没有自增id的最新记录
			Cursor cursor = db.onSelectMaxIncreaseId( groupid );
			if ( cursor != null && cursor.getCount( ) > 0 )
			{// 本地有该圈子的聊天记录
				cursor.moveToFirst( );
				recorder.setMaxRecordIncreaseId( cursor.getLong( cursor
						.getColumnIndex( GroupMessageWorker.INCREASEID ) ) + 1 );
				recorder.setMaxRecordMessageId( cursor.getLong( cursor
						.getColumnIndex( GroupMessageWorker.MESSAGEID ) ) + 1 );
				recorder.setMaxRecordSendTime( cursor.getLong( cursor
						.getColumnIndex( GroupMessageWorker.TIMESTAMP ) ) + 1 );
				CommonFunction.log( "sherlock" , "call set maxTime in getMaxIncreaseID == "+recorder.getMaxRecordSendTime( ) );
			}
			else
			{// 本地完全没有该圈子的聊天记录
				recorder.setLocalNoData( true );
			}
			cursor.close( );
		}
		else
		{
			Cursor cursor = db.onSelectedIncreaseRange( groupid , maxIncreaseId , 0 , 1 );
			if ( cursor != null && cursor.getCount( ) > 0 )
			{
				recorder.setMaxRecordIncreaseId( cursor.getLong( cursor
						.getColumnIndex( GroupMessageWorker.INCREASEID ) ) + 1 );
				recorder.setMaxRecordMessageId( cursor.getLong( cursor
						.getColumnIndex( GroupMessageWorker.MESSAGEID ) ) + 1 );
				recorder.setMaxRecordSendTime( cursor.getLong( cursor
						.getColumnIndex( GroupMessageWorker.TIMESTAMP ) ) + 1 );
			}
			else
			{
				recorder.setLocalNoData( true );
			}
			cursor.close( );
		}
	}
	
	public long getIncreaseIdByLocalId(Context context , long groupid , long localId )
	{
		
		long increaseId = 0;
		
		GroupMessageWorker db = DatabaseFactory.getGroupMessageWorker( context );
		Cursor cursor = db.onSelectLocalID( localId );
		if ( cursor != null )
		{
			
			if ( cursor.moveToFirst( ) )
			{
				increaseId = cursor.getLong( cursor
						.getColumnIndex( GroupMessageWorker.INCREASEID ) );
			}
			cursor.close( );
		}
		
		return increaseId;
	}
	
	/** 将数据库中发送中的和发送失败的组成ChatRecord返回 */
	public ArrayList< ChatRecord > getUnsendSuccessRecords(Context mContext , long groupId )
	{
		
		ArrayList< ChatRecord > records = new ArrayList< ChatRecord >( );
		
		GroupMessageWorker db = DatabaseFactory.getGroupMessageWorker( mContext );
		Cursor cursor = db.onSelectUnsendRecords( groupId );
		
		try
		{
			cursor.moveToLast( );
			while ( ! ( cursor.isBeforeFirst( ) ) )
			{
				ChatRecord record;
				int contentIndex = cursor.getColumnIndex( GroupMessageWorker.CONTENT );
				String strJson = cursor.getString( contentIndex );
				
				String status = cursor.getString( cursor
						.getColumnIndex( GroupMessageWorker.STATUS ) );
				try
				{
					record = parseGroupRecord( strJson );
					record.setLocid( cursor.getLong( cursor
							.getColumnIndex( GroupMessageWorker.ID ) ) );
					record.setStatus( Integer.valueOf( status ) );
					records.add( record );
				}
				catch ( JSONException e )
				{
					e.printStackTrace( );
				}
				finally
				{
					cursor.moveToPrevious( );
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
			if ( db != null )
				db.onClose( );
		}
		return records;
	}
	
	/** 获取系统用户发送的消息组成ChatRecord返回 */
	public ArrayList< ChatRecord > getSystemRecords(Context mContext , long groupId )
	{
		
		ArrayList< ChatRecord > records = new ArrayList< ChatRecord >( );
		
		GroupMessageWorker db = DatabaseFactory.getGroupMessageWorker( mContext );
		Cursor cursor = db.onSelectSystemRecords( groupId );
		
		try
		{
			cursor.moveToLast( );
			while ( ! ( cursor.isBeforeFirst( ) ) )
			{
				ChatRecord record;
				int contentIndex = cursor.getColumnIndex( GroupMessageWorker.CONTENT );
				String strJson = cursor.getString( contentIndex );
				
				String status = cursor.getString( cursor
						.getColumnIndex( GroupMessageWorker.STATUS ) );
				try
				{
					record = parseGroupRecord( strJson );
					record.setLocid( cursor.getLong( cursor
							.getColumnIndex( GroupMessageWorker.ID ) ) );
					record.setStatus( Integer.valueOf( status ) );
					records.add( record );
				}
				catch ( JSONException e )
				{
					e.printStackTrace( );
				}
				finally
				{
					cursor.moveToPrevious( );
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
			if ( db != null )
				db.onClose( );
		}
		return records;
	}
	
	/**
	 * 解析一条群组的消息
	 * 
	 * @param strJson
	 * @return
	 * @time 2011-7-1 上午10:35:49
	 * @author:linyg
	 * @throws JSONException
	 */
	public ChatRecord parseGroupRecord( String strJson ) throws JSONException
	{
		ChatRecord record = null;
		
		record = new ChatRecord( );
		GroupChatMessage bean = JSON.parseObject(strJson,GroupChatMessage.class);
		if(bean.item!=null){
			record.setItem(bean.item);
		}else {
			Item item = RankingTitleUtil.getInstance().getTitleItemFromChatBar(strJson);
			if (item != null) {
				record.setItem(item);
			}
		}

		record.setId( bean.msgid );
		record.setRecruit(bean.recruit);
		record.setGroupid(bean.groupid+"");
		// 这里要转成json字符串,不能直接用chatMessage.content,
		// 因为chatMessage的content是一个Object对象,toString方法会将其转成一个非json字符串
		String content = "";
		try
		{
			content = new JSONObject( strJson ).getString( "content" );
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
		
		record.initBaseInfo( bean.datetime , bean.type , bean.getAttachment( ) , content , ChatFromType.Group );
		
		User me = Common.getInstance( ).loginUser;
		//YC 从消息体中获取我的排名信息
		me.setTop(bean.user.mTop);
		me.setDatatype(bean.user.mType);
		me.setCat(bean.user.mCat);

		record.initMineInfo( me );
		
		if ( bean.user.userid == me.getUid( ) )
		{
			record.setSendType( MessageBelongType.SEND );
		}
		else
		{
			record.setSendType( MessageBelongType.RECEIVE );
			User friend = convertToUser( bean.user );
			record.initFriendInfo( friend );
		}
		return record;
	}
	
	public String jsonOptString(JSONObject json , String key )
	{
		
		if ( json.isNull( key ) )
		{
			return "";
		}
		try
		{
			return json.getString( key );
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
		return "";
	}
	
	/**
	 * 获取圈子聊天记录
	 * 
	 * @param uid
	 * @param groupid
	 * @param start
	 * @param amount
	 */
	public ArrayList< ChatRecord > loadGroupMessage(Context context , long uid ,
                                                    long groupid , long start , int amount )
	{
		ArrayList< ChatRecord > records = new ArrayList< ChatRecord >( );
		GroupMessageWorker db = DatabaseFactory.getGroupMessageWorker( context );
		Cursor cursor = db.onSelectPageByStart( uid , groupid , start , amount );
		try
		{
			cursor.moveToFirst( );
			while ( ! ( cursor.isAfterLast( ) ) )
			{
				ChatRecord record;
				String strJson = cursor.getString( cursor
						.getColumnIndex( GroupMessageWorker.CONTENT ) );
				try
				{
					record = parseGroupRecord( strJson );
					records.add( record );
				}
				catch ( JSONException e )
				{
					e.printStackTrace( );
				}
				finally
				{
					cursor.moveToNext( );
				}
			}
		}
		catch ( Exception e )
		{
		}
		finally
		{
			if ( cursor != null )
				cursor.close( );
			if ( db != null )
				db.onClose( );
		}
		return records;
	}
	
	/**
	 * 协议71007圈子消息推送,插入数据库
	 * 
	 * @param context
	 * @param msg
	 * @return 返回数据库的id
	 */
	public long InsertOneRecord(Context context , GroupChatMessage msg )
	{
		String content = GsonUtil.getInstance( ).getStringFromJsonObject( msg );
		GroupMessageWorker db = DatabaseFactory.getGroupMessageWorker( context );
		
		long userId = Common.getInstance( ).loginUser.getUid( );
		long dateBaseID = db.onInsertMessage( userId , msg.user.userid , msg.groupid , msg.msgid , content ,
				msg.incmsgid , msg.datetime ,msg.type);
		
		return dateBaseID;
	}
	
	/*
	 * 协议81068下发的圈子历史消息,插入数据库
	 * 
	 * @param context
	 * 
	 * @param msg
	 * 
	 * @return 返回数据库的id
	 */
	public void BatchInsertOneRecord(Context context , ArrayList< GroupChatMessage > list )
	{
		
		ArrayList< ContentValues > valuesList = new ArrayList< ContentValues >( );
		for ( int i = 0 ; i < list.size( ) ; i++ )
		{
			GroupChatMessage msg = list.get( i );
			String content = GsonUtil.getInstance( ).getStringFromJsonObject( msg );
			long userId = Common.getInstance( ).loginUser.getUid( );
			
			ContentValues values = new ContentValues( );
			values.put( GroupMessageWorker.USERID , userId );
			values.put( GroupMessageWorker.SENDERID , msg.user.userid );
			values.put( GroupMessageWorker.GROUPID , msg.groupid );
			values.put( GroupMessageWorker.MESSAGEID , msg.msgid );
			values.put( GroupMessageWorker.CONTENT , content );
			values.put( GroupMessageWorker.INCREASEID , msg.incmsgid );
			values.put( GroupMessageWorker.DELETEFLAG , 0 );
			values.put( GroupMessageWorker.TIMESTAMP , msg.datetime );
			values.put( GroupMessageWorker.MESSAGETYPE , msg.type );
			
			valuesList.add( values );
		}
		
		GroupMessageWorker db = DatabaseFactory.getGroupMessageWorker( context );
		db.onBatchInsertMessage( valuesList );
	}
	
	/**
	 * 向圈消息数据库插入一条消息
	 * 
	 * @param context
	 * @param groupid
	 * @param record
	 * @param status
	 * @return
	 */
	public long InsertOneRecord(Context context , long senderid , long groupid, ChatRecord record ,
                                String content , int status )
	{
		GroupMessageWorker db = DatabaseFactory.getGroupMessageWorker( context );
		
		long uid = Common.getInstance( ).loginUser.getUid( );
		return db.onInsertRecord( uid,senderid , groupid , record.getDatetime( ) , content , status ,
				record.getDatetime( ) , Integer.parseInt( record.getType( ) ) );
		
	}
	
	/**
	 * 带自增id和时间戳地向本地向数据库插入一条记录
	 */
	public long InsertOneRecord(Context context, long senderid , long groupid , ChatRecord record ,
                                String content , int status , int increaseid )
	{
		GroupMessageWorker db = DatabaseFactory.getGroupMessageWorker( context );
		
		long uid = Common.getInstance( ).loginUser.getUid( );
		return db.onInsertRecord( uid,senderid , groupid , record.getId( ) , content , status ,
				increaseid , record.getDatetime( ) , Integer.parseInt( record.getType( ) ) );
	}
	
	/** 更改消息id和状态 */
	public void modifyMessageIdAndStatus(Context mContext , String localId ,
                                         String messageId , String status , long increaseId )
	{
		GroupMessageWorker db = DatabaseFactory.getGroupMessageWorker( mContext );
		db.onModifyMessageIdByLocalId( localId , messageId , status , increaseId );
	}
	
	/**
	 * 获取私聊记录的本地ID
	 * 
	 * @param flag
	 * 
	 * @return
	 */
	public Long getRecordLocalId(long flag )
	{
		return recordLocalIdMap.get( Long.valueOf( flag ) ); // 记录的本地ID
	}
	
	/**
	 * 删除私聊记录的本地ID
	 * 
	 * @param flag
	 */
	public void removeRecordLocalId( long flag )
	{
		recordLocalIdMap.remove( Long.valueOf( flag ) );
	}
	
	/**
	 * 保存该记录的Flag，用于后续服务器返回该记录的msgId后，查出该记录，更新记录的msgId
	 * 
	 * @param flag
	 * @param localId
	 */
	public void saveRecordLocalId( long flag , long localId )
	{
		recordLocalIdMap.put( flag , localId );
	}
	
	public void modifyGroupMessageStatus(Context mContext , long localID , long flag ,
                                         String status , long increaseId )
	{
		GroupMessageWorker groupMessageDB = DatabaseFactory.getGroupMessageWorker( mContext );
		groupMessageDB.onUpdateRecordStatus( localID , status );
		
		GroupContactWorker groupContactdb = DatabaseFactory.getGroupContactWorker( mContext );
		groupContactdb.onUpdateRecordStatus( flag , status );
	}
	
	public void modifyGroupMessageStatus(Context mContext , long messageId , long localID ,
                                         long flag , String status , long increaseId )
	{
		
		String messageIdStr = String.valueOf( messageId );
		String localIdStr = String.valueOf( localID );
		modifyMessageIdAndStatus( mContext , localIdStr , messageIdStr , status , increaseId );
		
		GroupContactWorker groupContactdb = DatabaseFactory.getGroupContactWorker( mContext );
		groupContactdb.onUpdateRecordStatus( flag , status );
	}
	
	public String getGroupLastContent(Context mContext , String groupid )
	{
		GroupMessageWorker db = DatabaseFactory.getGroupMessageWorker( mContext );
		Cursor cursor = db.onSelectRecord( groupid );
		String content = "";
		if ( cursor != null && cursor.moveToLast( ) )
		{
			int contentIndex = cursor.getColumnIndex( GroupMessageWorker.CONTENT );
			content = cursor.getString( contentIndex );
			
		}
		
		cursor.close( );
		return content;
	}
	
	public String getGroupLastMessageStatus(Context mContext , String groupid )
	{
		GroupMessageWorker db = DatabaseFactory.getGroupMessageWorker( mContext );
		Cursor cursor = db.onSelectRecord( groupid );
		String content = "";
		if ( cursor != null && cursor.moveToLast( ) )
		{
			int contentIndex = cursor.getColumnIndex( GroupMessageWorker.STATUS );
			content = cursor.getString( contentIndex );
			
		}
		cursor.close( );
		return content;
	}
	
	public void updateMessageSendingStatus(Context mContext , String userid , String groupid ,
                                           long dateTime )
	{
		GroupMessageWorker db = DatabaseFactory.getGroupMessageWorker( mContext );
		db.onModifySendingStatusMsg( userid , groupid , dateTime );
	}
	
	/**
	 * 解析群最新消息
	 * 
	 * @param message
	 * @return
	 * @time 2011-5-27 上午11:53:24
	 * @author:sunb
	 * @throws JSONException
	 */
	public ChatRecord parseGroupNewMessage( TransportMessage message ) throws JSONException
	{
		ChatRecord record;
		JSONObject objJson = new JSONObject( message.getContentBody( ) );
		// 解析群消息
		record = parseGroupRecord( objJson.toString( ) );
		return record;
	}
	
	/**
	 * 添加聊天记录
	 * 
	 * @param list
	 *            需要操作的聊天记录列表
	 * @param index
	 *            要添加到的具体位置；若为-1，表示添加到末尾
	 * @param record
	 *            聊天记录
	 */
	public synchronized void addRecord(ArrayList< ChatRecord > list , int index , ChatRecord record )
	{
		if ( list == null || TextUtils.isEmpty( record.getType( ) )
				|| SuperChat.TIME_LINE_TYPE.equals( record.getType( ) ) )
		{
			return;
		}
		
		int size = list.size( );
		if ( size > 0 )
		{
			ChatRecord lastRecord = list.get( size - 1 );
			if ( lastRecord != null && record != null )
			{
				long interval = record.getDatetime( ) - lastRecord.getDatetime( );
				if ( interval > ( 3 * 60 * 1000 ) )
				{
					ChatRecord timeRecord = new ChatRecord( );
					timeRecord.setType( SuperChat.TIME_LINE_TYPE );
					timeRecord.setDatetime( record.getDatetime( ) );
					if ( index < 1 )
					{
						list.add( timeRecord );
					}
					else
					{
						list.add( index - 1 , timeRecord );
					}
				}
			}
		}
		else
		{
			ChatRecord timeRecord = new ChatRecord( );
			timeRecord.setType( SuperChat.TIME_LINE_TYPE );
			timeRecord.setDatetime( record.getDatetime( ) );
			if ( index < 1 )
			{
				list.add( timeRecord );
			}
			else
			{
				list.add( index - 1 , timeRecord );
			}
		}
		
		if ( -1 == index )
		{
			list.add( record );
		}
		else
		{
			list.add( index , record );
		}
	}
	
	public synchronized void addTimerRecord( ArrayList< ChatRecord > list )
	{
		
		if ( list == null || list.size( ) <= 0 )
			return;
		
		long dataTime = 0;
		long timeOffset = 3 * 60 * 1000;
		int count = list.size( );
		
		ArrayList< ChatRecord > tmpList = new ArrayList< ChatRecord >( );
		tmpList.addAll( list );
		list.clear( );
		
		for ( int i = 0 ; i < count ; i++ )
		{
			ChatRecord record = tmpList.get( i );
			if ( record.getDatetime( ) - dataTime > timeOffset )
			{
				ChatRecord timeRecord = new ChatRecord( );
				timeRecord.setType( SuperChat.TIME_LINE_TYPE );
				timeRecord.setDatetime( record.getDatetime( ) );
				dataTime = record.getDatetime( );
				list.add( timeRecord );
			}
			list.add( record );
		}
	}
	
	private User convertToUser( UserTypeOne userTypeOne )
	{
		User fUser = new User( );
		fUser.setUid( userTypeOne.userid );
		fUser.setIcon( userTypeOne.getIcon( ) );
		
		// 1--男 2--女 0--其他
		String genderStr = userTypeOne.getGender( ).trim( );
		fUser.setSex( genderStr.equals( "all" ) ? 0 : ( genderStr.equals( "m" ) ? 1 : 2 ) );
		fUser.setAge( userTypeOne.age );
		fUser.setLat( userTypeOne.lat );
		fUser.setLng( userTypeOne.lng );
		fUser.setNoteName( userTypeOne.getNotes( ) );
		fUser.setNickname( userTypeOne.getNickname( ) );
		fUser.setViplevel( userTypeOne.viplevel );
		fUser.setSVip( userTypeOne.svip );
		fUser.setLevel(userTypeOne.level );
		fUser.setPhotoNum( userTypeOne.photonum );
		/**聊吧新增字段*/
		fUser.setDatatype(userTypeOne.type);
		fUser.setTop(userTypeOne.top);
		fUser.setCat(userTypeOne.cat);
		
		return fUser;
	}
	
	public boolean checkMessageIsExist(Context mContext , long groupid , long messageId ,
                                       long increaseId )
	{
		GroupMessageWorker db = DatabaseFactory.getGroupMessageWorker( mContext );
		
		Cursor cursor = db.onSelectedChatRecordByMsgId( groupid , messageId );
		
		if ( cursor != null && cursor.getCount( ) > 0 )
		{
			db.onModifyIncreaseidByMessage( groupid , messageId , increaseId );
			cursor.close( );
			return true;
		}
		else
		{
			cursor.close( );
			return false;
		}
	}
	
	public boolean checkMessageAndIncreaseIsExist(Context mContext , long groupid ,
                                                  long messageid , long increaseid )
	{
		GroupMessageWorker db = DatabaseFactory.getGroupMessageWorker( mContext );
		
		Cursor mCursor = db.onSelectedChatRecordByMsgId( groupid , messageid );
		Cursor iCurosr = db.onSelectedChatRecordByIncreaseid( groupid , increaseid );
		
		if ( mCursor != null && mCursor.getCount( ) > 0 )
		{
			db.onModifyIncreaseidByMessage( groupid , messageid , increaseid );
			mCursor.close( );
			iCurosr.close( );
			return true;
		}
		else if ( iCurosr != null && iCurosr.getCount( ) > 0 )
		{
			mCursor.close( );
			iCurosr.close( );
			return true;
		}
		else
		{
			mCursor.close( );
			iCurosr.close( );
			return false;
		}
	}
	
	/**
	 * 删除圈子聊天消息，根据服务端id
	 * 
	 * @param groupid
	 * @return void
	 */
	public void removeGroupMsgByServerId(Context context , long groupid , long msgId )
	{
		GroupMessageWorker db = DatabaseFactory.getGroupMessageWorker( context );
		db.deleteGroupMessageByMessageId( groupid , msgId );
		db.onClose( );
	}


	/*改变 消息记录中的称号
	* */
	public void modifyGroupMessageUserTitle(Context mContext , long localID , String titleKey, int titleValue )
	{
		GroupMessageWorker groupMessageDB = DatabaseFactory.getGroupMessageWorker( mContext );

		Cursor cursor = groupMessageDB.onSelectLocalID( localID );
		if ( cursor != null && cursor.moveToFirst( ) )
		{
			int contentIndex = cursor.getColumnIndex( GroupMessageWorker.CONTENT );
			String content = cursor.getString( contentIndex );

			// 把服务器下发的URL更新到数据库中
			JSONObject json = null;
			try
			{
				json = new JSONObject( content );
				JSONObject item = new JSONObject();
				item.put("key",titleKey);
				item.put("value",titleValue);
				json.put( "item" , item );
			}
			catch ( JSONException e )
			{
				e.printStackTrace( );
			}

			groupMessageDB.onUpdateRecordContent( localID , json.toString( ) );
		}
		cursor.close( );
	}
}
