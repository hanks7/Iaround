
package net.iaround.ui.group.activity;


import android.view.View;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.MessageID;
import net.iaround.tools.DialogUtil;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.datamodel.GroupModel;
import net.iaround.ui.group.GroupOut;


/**
 * @ClassName: GroupHandleActivity
 * @Description: 圈子操作相关的Activity 注： 1.涉及到圈子角色变更相关的操作（如被踢出圈子），进行统一处理
 *               2.子类如果需重写onResume方法时，也必须调用super.onResume()使该方法生效
 * @author zhonglong kylin17@foxmail.com
 * @date 2014-1-17 下午2:36:52
 * @修改者 kevinsu kevinsu917@126.com  2014-7-21
 * @修改内容 取消对onReceiveMessages的实现，通过调用接口的方式来判断是否需要踢出圈子
 * @Note GroupChatTopicActivity圈子聊天界面中也实现GroupOut接口
 */
public abstract class GroupHandleActivity extends SuperActivity implements GroupOut
{
	@Override
	protected void onResume( )
	{
		super.onResume( );
		HandleOutOfGroupEvent();
	}
	
	
	/**
	 * @Title: getGroupId
	 * @Description: 获取出圈子id
	 * @return
	 */
	abstract protected String getGroupId( );
	
	//处于圈子中时需要判断是否被排除圈子外
	public void HandleOutOfGroupEvent( )
	{
		if ( Common.groupKickDisbandedMap.containsKey( getGroupId( ) ) )
		{
			String dialogMsg = "";
			
			int methodId = Common.groupKickDisbandedMap.get( getGroupId( ) ).getMethodId( );
			
			if(methodId == MessageID.GROUP_PUSH_KICK)
			{
				dialogMsg = mContext.getString( R.string.has_kick_from_group );
			}else if(methodId == MessageID.GROUP_PUSH_DISSOLVE)
			{
				dialogMsg = mContext.getString( R.string.group_is_disbanded );
			}
			
			DialogUtil.showOKDialog( mContext ,
					mContext.getString( R.string.dialog_title ) , dialogMsg ,
					new View.OnClickListener( )
					{
						
						@Override
						public void onClick( View v )
						{
							String userid = String.valueOf( Common.getInstance( ).loginUser.getUid( ) );
							GroupModel.getInstance( ).removeGroupAndAllMessage( mContext , userid , getGroupId( ) );
							finish( );
						}
					} ).setCancelable( false );
			
			Common.groupKickDisbandedMap.remove( getGroupId( ) );
			GroupModel.getInstance( ).isNeedRefreshGroupList = true;
		}
	}

	
	/**
	 * @Title: backToMainActivity
	 * @Description: 返回到一级界面
	 */
	protected void backToMainActivity( )
	{
		CloseAllActivity.getInstance( ).backToMainActivity( );
	}
	
}
