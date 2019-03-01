
package net.iaround.ui.datamodel;


import android.content.Context;
import android.database.Cursor;

import net.iaround.conf.Common;
import net.iaround.connector.protocol.SocketSessionProtocol;
import net.iaround.database.DatabaseFactory;
import net.iaround.database.GroupNoticeWorker;
import net.iaround.entity.type.GroupMsgReceiveType;
import net.iaround.entity.type.GroupNoticeType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.PathUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.group.bean.GroupNoticeBean;
import net.iaround.ui.group.bean.GroupsMsgStatusBean;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * @Description: 圈事务模型，用于处理圈通知表和圈消息接收状态缓存
 * @author tanzy
 * @date 2015-4-7
 */
public class GroupAffairModel extends Model
{
	private static GroupAffairModel instance;

	public GroupsMsgStatusBean groupsMsgStatus;
	public HashMap< Long , Integer > statusMap = new HashMap< Long , Integer >( );

	private GroupAffairModel( )
	{
	}

	public static GroupAffairModel getInstance( )
	{
		if ( instance == null )
			instance = new GroupAffairModel( );
		return instance;
	}

	/**
	 * 接收到圈通知的处理，其中type为1、2、3的为互斥事件，需回写数据库 当原type为1时，当收到2或3时删除原1的数据
	 * */
	public void recieveNotice(Context context , ArrayList< GroupNoticeBean > beans )
	{
		if ( beans == null || beans.size( ) == 0 )
		{
			CommonFunction.log( "sherlock" , "81072 GroupNoticeListBean == null" );
			return;
		}

		String suid = String.valueOf( Common.getInstance( ).loginUser.getUid( ) );

		long latestTime = 0;
		GroupNoticeWorker db = DatabaseFactory.getGroupNoticeWoker( context );
		for ( int i = 0 ; i < beans.size( ) ; i++ )
		{
			GroupNoticeBean bean = beans.get( i );
			latestTime = latestTime > bean.datetime ? latestTime : bean.datetime;
			Cursor c = db.selectGroupTypeMessage(
					String.valueOf( Common.getInstance( ).loginUser.getUid( ) ) ,
					bean.groupid , bean.targetuser.userid , 1 );
			int count = c.getCount( );
			c.close( );
			if ( bean.type == GroupNoticeType.ALLOW_JOIN_GROUP
					|| bean.type == GroupNoticeType.REJECT_JOIN_GROUP )
			{// 如果收到的是同意或拒绝通知
				if ( count > 0 )
				{// 如果前面有这个人的申请消息，则回写该条数据
					db.updateOneRecord( suid , String.valueOf( bean.groupid ) , 1 , String
							.valueOf( bean.targetuser.userid ) , bean.type , String
							.valueOf( bean.datetime ) , GsonUtil.getInstance( )
							.getStringFromJsonObject( bean ) , 1 );
				}
				else
				{// 否则直接插入数据库
					db.insertOneRecord( suid , String.valueOf( bean.groupid ) , String
							.valueOf( bean.targetuser.userid ) , bean.type , String
							.valueOf( bean.datetime ) , GsonUtil.getInstance( )
							.getStringFromJsonObject( bean ) , 1 );
				}
			}
			else
			{
				db.insertOneRecord( suid , String.valueOf( bean.groupid ) , String
						.valueOf( bean.targetuser.userid ) , bean.type , String
						.valueOf( bean.datetime ) , GsonUtil.getInstance( )
						.getStringFromJsonObject( bean ) , 1 );
			}
		}
		if ( db != null )
			db.onClose( );

		SocketSessionProtocol.seesionSendGroupNoticeLatestTime( context , latestTime );
	}

	/** 获取用户圈通知数据 */
	public ArrayList< GroupNoticeBean > getNoticeList(Context context )
	{
		ArrayList< GroupNoticeBean > list = new ArrayList< GroupNoticeBean >( );
		GroupNoticeWorker db = DatabaseFactory.getGroupNoticeWoker( context );
		Cursor cursor = db.selectPage(
				String.valueOf( Common.getInstance( ).loginUser.getUid( ) ) , 0 , 100 );
		try
		{
			for ( cursor.moveToFirst( ) ; !cursor.isAfterLast( ) ; cursor.moveToNext( ) )
			{
				String content = cursor.getString( cursor
						.getColumnIndex( GroupNoticeWorker.CONTENT ) );
				GroupNoticeBean bean = GsonUtil.getInstance( ).getServerBean( content ,
						GroupNoticeBean.class );

				CommonFunction.log( "shifengxiong","getNoticeList ==="+content );

				if ( bean != null && bean.datetime != 0 )
					list.add( bean );
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
		return list;
	}

	/** 获取最新圈通知信息 */
	public GroupNoticeBean getLatestNotice( Context context )
	{
		GroupNoticeBean bean = new GroupNoticeBean( );
		GroupNoticeWorker db = DatabaseFactory.getGroupNoticeWoker( context );
		Cursor cursor = db.selectPage(
				String.valueOf( Common.getInstance( ).loginUser.getUid( ) ) , 0 , 1 );
		if ( cursor.getCount( ) > 0 )
		{
			cursor.moveToFirst( );
			String content = cursor.getString( cursor
					.getColumnIndex( GroupNoticeWorker.CONTENT ) );
			bean = GsonUtil.getInstance( ).getServerBean( content , GroupNoticeBean.class );
		}
		else
			bean = null;

		if ( cursor != null )
			cursor.close( );
		if ( db != null )
			db.onClose( );
		return bean;
	}

	/** 获取未读圈通知数 */
	public int getUnreadCount( Context context )
	{
		GroupNoticeWorker db = DatabaseFactory.getGroupNoticeWoker( context );
		Cursor cursor = db
				.selectUnreadMessage( String.valueOf( Common.getInstance( ).loginUser.getUid( ) ) );
		int count = cursor.getCount( );
		if ( cursor != null )
			cursor.close( );
		if ( db != null )
			db.onClose( );
		return count;
	}

	/** 获取一条最新的某人某圈某类型的圈通知消息 */
	public GroupNoticeBean getGroupTypeOne(Context context , long groupid , long targetUid ,
										   int type )
	{
		GroupNoticeWorker db = DatabaseFactory.getGroupNoticeWoker( context );
		Cursor cursor = db.selectGroupTypeMessage(
				String.valueOf( Common.getInstance( ).loginUser.getUid( ) ) , groupid ,
				targetUid , type );
		GroupNoticeBean bean = null;
		if ( cursor.getCount( ) > 0 )
		{
			cursor.moveToFirst( );
			String content = cursor.getString( cursor
					.getColumnIndex( GroupNoticeWorker.CONTENT ) );
			bean = GsonUtil.getInstance( ).getServerBean( content , GroupNoticeBean.class );
		}
		if ( cursor != null )
			cursor.close( );
		if ( db != null )
			db.onClose( );
		return bean;
	}

	/** 同意或拒绝申请时改写本地数据库 */
	public void agreeRejectUpadteData(Context context , GroupNoticeBean bean , int newType )
	{
		GroupNoticeWorker db = DatabaseFactory.getGroupNoticeWoker( context );
		int oldType = bean.type;
		BaseUserInfo dealer = new BaseUserInfo( );
		dealer.userid = Common.getInstance( ).loginUser.getUid( );
		dealer.nickname = Common.getInstance( ).loginUser.getNickname( );
		long newDatetime = System.currentTimeMillis( )
				+ Common.getInstance( ).serverToClientTime;

		bean.dealuser = dealer;
		bean.type = newType;
		bean.datetime = newDatetime;
		String newContent = GsonUtil.getInstance( ).getStringFromJsonObject( bean );

		db.updateOneRecord( String.valueOf( Common.getInstance( ).loginUser.getUid( ) ) ,
				String.valueOf( bean.groupid ) , oldType ,
				String.valueOf( bean.targetuser.userid ) , newType ,
				String.valueOf( newDatetime ) , newContent , 0 );
		if ( db != null )
			db.onClose( );
	}

	/** 删除某用户所有圈通知 */
	public void deleteUserAllNotice( Context context )
	{
		GroupNoticeWorker db = DatabaseFactory.getGroupNoticeWoker( context );
		db.deleteOneUserAll( String.valueOf( Common.getInstance( ).loginUser.getUid( ) ) );
		if ( db != null )
			db.onClose( );
	}

	/** 标记所有圈通知为已读 */
	public void setAllRead( Context context )
	{
		GroupNoticeWorker db = DatabaseFactory.getGroupNoticeWoker( context );
		db.updateAllUnread( String.valueOf( Common.getInstance( ).loginUser.getUid( ) ) );
		if ( db != null )
			db.onClose( );
	}


	// *********************以上为数据库操作，以下为缓存文件操作******************************************/

	/** 获取圈消息接收状态 */
	public int getGroupMsgStatus( long groupID )
	{
		if ( statusMap == null || statusMap.get( groupID ) == null )
		{
			return GroupMsgReceiveType.RECEIVE_AND_NOTICE;
		}
		return statusMap.get( groupID );
	}

	/** 设置圈消息接收状态 */
	public void setGroupMsgStatus( long groupID , int type )
	{
		if ( statusMap == null )
			statusMap = new HashMap< Long , Integer >( );
			statusMap.put( groupID , type );

		if ( groupsMsgStatus != null && groupsMsgStatus.groups != null )
		{
			for ( int i = 0 ; i < groupsMsgStatus.groups.size( ) ; i++ )
			{// 如果列表中没有该圈子也没关系，消息接收类型判断只用map判断，再次请求时可获得
				if ( groupsMsgStatus.groups.get( i ).group.id == groupID )
				{
					groupsMsgStatus.groups.get( i ).type = type;
					break;
				}
			}
		}
		saveStatusToFile( );
	}

	/** 获取顶置圈助手开关，默认为不顶置 */
	public int getGroupHeplerTopAtMsg( Context context )
	{
		return SharedPreferenceUtil.getInstance( context ).getInt(
				Common.getInstance( ).loginUser.getUid( )
						+ SharedPreferenceUtil.GROUP_HEPLER_TOP_AT_MSG , 0 );
	}

	/** 设置顶置圈助手开关，0：不顶置，1：顶置 */
	public void setGroupHelperTopAtMsg(Context context , int value )
	{
		SharedPreferenceUtil.getInstance( context ).putInt(
				Common.getInstance( ).loginUser.getUid( )
						+ SharedPreferenceUtil.GROUP_HEPLER_TOP_AT_MSG , value );
	}

	/** 获取圈助手开关，默认为开，1：开、0：关 */
	public int getGroupHelperOnOff( )
	{
		if ( groupsMsgStatus != null )
			return groupsMsgStatus.assistant;
		else
			return 1;
	}

	/** 设置圈助手开关，1：开、0：关 */
	public void setGroupHelperOnOff( int value )
	{
		if ( groupsMsgStatus != null )
			groupsMsgStatus.assistant = value;
	}

	public void saveStatusToFile( )
	{
		if ( groupsMsgStatus != null || groupsMsgStatus.groups != null && groupsMsgStatus.groups.size( ) > 0 )
		{
			for ( int i = 0 ; i < groupsMsgStatus.groups.size( ) ; i++ )
			{
				statusMap.put( groupsMsgStatus.groups.get( i ).group.id ,
						groupsMsgStatus.groups.get( i ).type );
			}
		}
		saveBufferToFile( PathUtil.getGroupMsgStatus( ) , groupsMsgStatus );
	}

	public void getStatusFromFile( )
	{
		groupsMsgStatus = (GroupsMsgStatusBean) getBufferFromFile( PathUtil
				.getGroupMsgStatus( ) );
		CommonFunction.log( "sherlock" , "have get buffer from file" );
		if ( groupsMsgStatus != null && groupsMsgStatus.groups.size( ) > 0 )
		{
			CommonFunction.log( "sherlock" , "buffer have data" );
			for ( int i = 0 ; i < groupsMsgStatus.groups.size( ) ; i++ )
			{
				statusMap.put( groupsMsgStatus.groups.get( i ).group.id ,
						groupsMsgStatus.groups.get( i ).type );
			}
		}
	}


}
