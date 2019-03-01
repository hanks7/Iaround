
package net.iaround.ui.chat;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;

import net.iaround.R;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.model.im.enums.ProfileEntrance;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.datamodel.GroupModel;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.view.menu.CustomContextMenu;

import java.util.HashMap;


/**
 * 圈主（圈子的所有者）使用：圈子成员的点击事件（如：成员头像、成员列表的每一项）
 */
public class GroupUserIconDialogForOwner
{
	/**
	 * 消息：升为管理员失败
	 */
	public static final int MSG_BECOME_MANAGER_FAIL = 91;
	/**
	 * 消息：免去管理员失败
	 */
	public static final int MSG_CANCLE_MANAGER_FAIL = 92;
	
	private SuperActivity mActivity;
	
	/**
	 * 圈子ID
	 */
	private String roomId;
	private HashMap< String , HashMap< String , Object >> becomeManagerReqMap;
	private HashMap< String , HashMap< String , Object >> cancleManagerReqMap;
	private Handler mHandler;
	/**
	 * 禁言的状态：0-没有“禁言/取消禁言”项；1-没有被禁言(显示“禁言”)；2-已经被禁言(显示“取消禁言”)；3-不确定(显示“禁言/解除禁言”)
	 */
	private int forbidStatus;
	private ProfileEntrance entrance;
	private HttpCallBack callBack;
	
	/**
	 * 
	 * @param activity
	 * @param roomId
	 * @param mHandler
	 * @param forbidStatus
	 *            禁言的状态：0-没有“禁言/取消禁言”项；1-没有被禁言(显示“禁言”)；2-已经被禁言(显示“取消禁言”)；3-不确定(
	 *            显示“禁言/解除禁言”)
	 * @param becomeManagerReqMap
	 * @param cancleManagerReqMap
	 */
	public GroupUserIconDialogForOwner(SuperActivity activity ,
									   String roomId , Handler mHandler ,
									   int forbidStatus ,
									   HashMap< String , HashMap< String , Object >> becomeManagerReqMap ,
									   HashMap< String , HashMap< String , Object >> cancleManagerReqMap ,
									   ProfileEntrance entrance , HttpCallBack callback )
	{
		this.mActivity = activity;
		this.forbidStatus = forbidStatus;
		this.roomId = roomId;
		this.becomeManagerReqMap = becomeManagerReqMap;
		this.cancleManagerReqMap = cancleManagerReqMap;
		this.mHandler = mHandler;
		this.entrance = entrance;
		this.callBack = callback;
	}
	
	/**
	 * 点击用户头像弹出的对话框：用于创建者
	 * 
	 * @param user
	 */
	public void show(final User user , View view )
	{
		final long userId = user.getUid( );
		final int role = user.getGroupRole( ); // 成员角色：0：创建者，1：管理员，2：普通成员
		final String nickname = user.getNoteName( false );
		
		CustomContextMenu menu = new CustomContextMenu( mActivity );
		// 查看他的资料
		menu.addMenuItem( 0 ,
				mActivity.getResString( R.string.chat_room_user_icon_dialog_item_look_info ) ,
				new OnClickListener( )
				{
					
					@Override
					public void onClick( View v )
					{
//						SpaceOther.launchUser( mActivity , user.getUid( ) , user , ChatFromType.Group , GroupInfoForRelation.GROUP_NAME , GroupInfoForRelation.GROUP_ID  );//jiqiang
					}
				}, false );
		
		// 禁言/取消禁言
		if ( user.getGroupRole( ) == 2 )
		{
			if ( 0 != forbidStatus )
			{
				String str = "";
				if ( 1 == forbidStatus )
				{ // 显示“禁言”
					str = mActivity.getResString( R.string.group_forbid_say_title );
				}
				else if ( 3 == forbidStatus )
				{ // 显示“禁言/解除禁言”
					str = mActivity.getResString( R.string.group_user_forbid_for_chat );
				}
				else
				{ // 显示“取消禁言”
					str = mActivity.getResString( R.string.group_cancle_forbid_say_title );
				}
				
				menu.addMenuItem( 1 , str , new OnClickListener( )
				{
					
					@Override
					public void onClick( View v )
					{
						forbidUser( userId );
					}
				}, false );
			}
		}
		
		// 免去/设为管理员
		if ( forbidStatus != 2 )
		{
			String str = "";
			if ( 1 == role )
			{
				str = mActivity.getResString( R.string.group_user_list_cancle_manager_title );
			}
			else
			{
				str = mActivity.getResString( R.string.group_user_list_become_manager_title );
			}
			menu.addMenuItem( 2 , str , new OnClickListener( )
			{
				
				@Override
				public void onClick( View v )
				{
					becomeCancleManager( role , userId , nickname );
				}
			}, false );
		}
		
		// 举报该用户
		menu.addMenuItem(
				3 ,
				mActivity.getResString( R.string.chat_room_user_icon_dialog_item_report_user ) ,
				new OnClickListener( )
				{
					
					@Override
					public void onClick( View v )
					{
						reportUser( userId );
					}
				}, false );
		
		// 群主踢人
		menu.addMenuItem( 4 , mActivity.getResString( R.string.group_kick_title ) , Color.RED ,
				new OnClickListener( )
				{
					
					@Override
					public void onClick( View v )
					{
//						new GroupKickUserDialog( mActivity ).show( roomId , userId , nickname );//jiqiang
					}
				}, true );
		
		menu.showMenu( view );
	}
	
	/**
	 * 禁言/取消禁言
	 * 
	 * @param userId
	 */
	private void forbidUser( long userId )
	{
		Intent intent5 = new Intent( mActivity , GroupUserForbid.class );
		intent5.putExtra( "group_id" , roomId );
		intent5.putExtra( "user_id" , userId );
		mActivity.startActivityForResult( intent5 , GroupUserForbid.REQUEST_CODE_FORBID );
	}
	
	/**
	 * 举报该用户
	 * 
	 * @param userId
	 */
	private void reportUser( long userId )
	{
//		Intent intent = new Intent(mActivity, ReportChatAcitvity.class);
//		intent.putExtra(ReportChatAcitvity.USER_ID_KEY, userId);
//		intent.putExtra(ReportChatAcitvity.REPORT_FROM_KEY, ChatRecordReport.TYPE_ROOM);
//		intent.putExtra(ReportChatAcitvity.GROUP_ID_KEY, Long.valueOf( roomId ) );
//		mActivity.startActivity(intent);//jiqiang
	}
	
	/**
	 * 升为管理员 或 免去管理员
	 * 
	 * @param role
	 * @param userId
	 * @param nickname
	 */
	private void becomeCancleManager( int role , long userId , String nickname )
	{
		if ( 1 == role )
		{ // 为管理员，则免去管理员
			displayCancleManagerDialog( userId , nickname );
		}
		else
		{ // 为普通成员，则升为管理员
			displayBecomeManagerDialog( userId , nickname );
		}
	}
	
	/**
	 * 显示升为管理员的对话框
	 */
	private void displayBecomeManagerDialog( final long userId , final String nickname )
	{
		AlertDialog.Builder builder = new AlertDialog.Builder( mActivity );
		builder.setTitle( R.string.group_user_list_become_manager_title )
				.setMessage(
						String.format( mActivity
								.getString( R.string.group_user_list_become_manager_msg ) ,
								nickname ) ).setCancelable( false )
				.setNegativeButton( R.string.ok , new DialogInterface.OnClickListener( )
				{
					public void onClick(DialogInterface dialog , int id )
					{
						long requestBecomeManagerFlag = GroupHttpProtocol.groupManagerAdd(
								mActivity , GroupModel.getInstance( ).getGroupId( ) ,
								String.valueOf( userId ) , callBack );
						if ( -1 == requestBecomeManagerFlag )
						{
							mHandler.sendEmptyMessage( MSG_BECOME_MANAGER_FAIL );
						}
						else
						{
							HashMap< String , Object > map = new HashMap< String , Object >( );
							map.put( "user_id" , userId );
							map.put( "nickname" , nickname );
							becomeManagerReqMap.put(
									String.valueOf( requestBecomeManagerFlag ) , map );
						}//jiqiang
					}
				} ).setPositiveButton( R.string.cancel , new DialogInterface.OnClickListener( )
				{
					public void onClick(DialogInterface dialog , int id )
					{
						dialog.cancel( );
					}
				} );
		AlertDialog alert = builder.create( );
		alert.show( );
	}
	
	/**
	 * 显示免去管理员的对话框
	 */
	private void displayCancleManagerDialog( final long userId , final String nickname )
	{
		AlertDialog.Builder builder = new AlertDialog.Builder( mActivity );
		builder.setTitle( R.string.group_user_list_cancle_manager_title )
				.setMessage(
						String.format( mActivity
								.getString( R.string.group_user_list_cancle_manager_msg ) ,
								nickname ) ).setCancelable( false )
				.setNegativeButton( R.string.ok , new DialogInterface.OnClickListener( )
				{
					public void onClick(DialogInterface dialog , int id )
					{
						long requestCancleManagerFlag = GroupHttpProtocol.groupManagerCancel(
								mActivity , GroupModel.getInstance( ).getGroupId( ) ,
								String.valueOf( userId ) , callBack );
						if ( -1 == requestCancleManagerFlag )
						{
							mHandler.sendEmptyMessage( MSG_CANCLE_MANAGER_FAIL );
						}
						else
						{
							HashMap< String , Object > map = new HashMap< String , Object >( );
							map.put( "user_id" , userId );
							map.put( "nickname" , nickname );
							cancleManagerReqMap.put(
									String.valueOf( requestCancleManagerFlag ) , map );
						}//jiqiangokhttp
					}
				} ).setPositiveButton( R.string.cancel , new DialogInterface.OnClickListener( )
				{
					public void onClick(DialogInterface dialog , int id )
					{
						dialog.cancel( );
					}
				} );
		AlertDialog alert = builder.create( );
		alert.show( );
	}
}
