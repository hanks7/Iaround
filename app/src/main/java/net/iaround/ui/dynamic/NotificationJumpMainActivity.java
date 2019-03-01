
package net.iaround.ui.dynamic;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import net.iaround.conf.Config;
import net.iaround.conf.MessageID;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.activity.BaseActivity;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.activity.SplashActivity;


/**
 * @author tanzy
 * 
 *         本activity没有界面，用于点击通知栏推送时跳转到对应activity
 *         如果是跳到MainActivity，则将MainActivity调整到目标View之后，去掉MainActivity之上的Activity
 * */
public class NotificationJumpMainActivity extends BaseActivity
{
	
	public static final int JUMP_TO_MESSAGE = 1;// 消息页面
	
	long msgID;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		CommonFunction.log( "sherlock" , "notificationjumpmain oncreate" );
		if ( CommonFunction.isEmptyOrNullStr( Config.sBusinessHost ) )
		{// 如果用户没有登录，直接只登陆不跳转
			Intent intent = new Intent( this , SplashActivity.class );
			this.startActivity( intent );
			finish( );
			return;
		}
		
		msgID = getIntent( ).getLongExtra( "msgID" , -1 );
		if ( msgID == MessageID.SESSION_PRIVATE_CHAT )
		{// 点击私聊推送
		
			CloseAllActivity.getInstance( ).backToMainActivity( );
			//gh 暂时忽略
//			if ( CloseAllActivity.getInstance( ).getTopActivity( ) != null
//					&& ( (MainFragmentActivity) CloseAllActivity.getInstance( ).getTopActivity( ) ).notificationHandler != null )
//				( ( MainFragmentActivity ) CloseAllActivity.getInstance( ).getTopActivity( ) ).notificationHandler
//						.sendEmptyMessage( JUMP_TO_MESSAGE );
			
		}
		else if ( msgID == MessageID.SESSION_PUSH_USER_UNREAD_DYNAMIC )
		{// 点击新动态的消息列表推送

			Intent intent = new Intent( this , DynamicMessagesActivity.class );
			intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
			this.startActivity( intent );
		}
//		else if ( msgID == MessageID.SESSION_PUSH_USER_POSTBAR_UNREAD_MESSAGES )
//		{// 点击侃啦推送消息
//			Intent intent = new Intent( this , PostbarMessagesActivity.class );
//			intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
//			this.startActivity( intent );
//		}
		else
		{// 当为未知类型时默认跳转至MainActivity
			CloseAllActivity.getInstance( ).backToMainActivity( );
		}
		
		
		finish( );
	}
}
