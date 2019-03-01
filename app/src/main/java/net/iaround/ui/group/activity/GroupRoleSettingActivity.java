
package net.iaround.ui.group.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.view.dialog.CustomContextDialog;


/**
 * @ClassName: GroupRoleSettingActivity
 * @Description: 圈子权限设置界面
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-12-10 下午5:47:01
 * 
 */
public class GroupRoleSettingActivity extends SuperActivity implements OnClickListener
{
	
	/** 标题 */
	private TextView mTitltName;
	/** 返回按钮 */
	private ImageView mTitleBack;
	private FrameLayout flLeft;
	
	/** 聊天权限设置 */
	private RelativeLayout mTalkSetting;
	/** 话题权限设置 */
	private RelativeLayout mTopicSetting;
	
	/** 所选聊天权限 */
	private TextView mSelTalkSetting;
	/** 所选话题权限 */
	private TextView mSelTopicSetting;

	/**对话框*/
	private CustomContextDialog mCustomContextDialog;
	private TextView mTvHeader;

	private int talkRole;
	private int topicRole;
	
	private int talkSelRole;
	private int topicSelRole;
	
	/** 权限设置列表 */
	SparseArray< String > mSettingMap = new SparseArray< String >( );
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_group_role_setting );
		
		initViews( );
		setListeners( );
		initData( );
	}
	
	private void initViews( )
	{
		mTitltName = (TextView) findViewById( R.id.tv_title );
		mTitleBack = (ImageView) findViewById( R.id.iv_left );
		flLeft = (FrameLayout) findViewById(R.id.fl_left);

		mTitleBack.setImageResource(R.drawable.title_back);
		mTalkSetting = (RelativeLayout) findViewById( R.id.talk_setting_layout );
		mTopicSetting = (RelativeLayout) findViewById( R.id.topic_setting_layout );
		mSelTalkSetting = (TextView) findViewById( R.id.talk_role );
		mSelTopicSetting = (TextView) findViewById( R.id.topic_role );
		
		mTitltName.setText( R.string.edit_group_role );

	}
	
	private void setListeners( )
	{
		flLeft.setOnClickListener(this);
		mTitleBack.setOnClickListener( this );
		mTalkSetting.setOnClickListener( this );
		registerForContextMenu( mTalkSetting );
		mTopicSetting.setOnClickListener( this );
		registerForContextMenu( mTopicSetting );
	}
	
	private void initData( )
	{
		mSettingMap.put( 1 , getString( R.string.groupowner_and_admimistrator ) );
		mSettingMap.put( 0 , getString( R.string.group_all_member ) );
		
		talkRole = getIntent( ).getIntExtra( "talk" , -1 );
		topicRole = getIntent( ).getIntExtra( "topic" , -1 );
		
		talkSelRole = talkRole;
		topicSelRole = topicRole;
		
		mSelTalkSetting.setText( mSettingMap.get( talkRole ) );
		mSelTopicSetting.setText( mSettingMap.get( topicRole ) );
	}

	//聊天发言权限
	private OnClickListener groupTalkListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch ((int)v.getTag())
			{
				case 0://所有成员
					talkSelRole = 0;
					break;
				case 1://圈主和管理员
					talkSelRole = 1;
					break;
			}
			mCustomContextDialog.dismiss();
			mSelTalkSetting.setText( mSettingMap.get( talkSelRole ) );
		}
	};
	//话题发言权限
	private OnClickListener groupTopicListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch ((int)v.getTag())
			{
				case 0://所有成员
					topicSelRole = 0;
					break;
				case 1://圈主和管理员
					topicSelRole = 1;
					break;
			}
			mCustomContextDialog.dismiss();
			mSelTopicSetting.setText( mSettingMap.get( topicSelRole ) );
		}
	};

	@Override
	protected void onDestroy( )
	{
		super.onDestroy( );
	}
	
	@Override
	public boolean onKeyDown( int keyCode , KeyEvent event )
	{
		if ( keyCode == KeyEvent.KEYCODE_BACK )
		{
			handleBackEvent( );
			return true;
		}
		return super.onKeyDown( keyCode , event );
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu , View v , ContextMenuInfo menuInfo )
	{
		String title = "";
		int groupId = 1;
		if ( v.equals( mTalkSetting ) )
		{
			title = getString( R.string.role_talk_sel_title );
			groupId = 1;
		}
		else
		{
			title = getString( R.string.role_topic_sel_title );
			groupId = 0;
		}
		menu.setHeaderTitle( title );
		menu.add( groupId , 1 , Menu.NONE , mSettingMap.get( 1 ) );
		menu.add( groupId , 0 , Menu.NONE , mSettingMap.get( 0 ) );
		
	}
	
	@Override
	public boolean onContextItemSelected( MenuItem item )
	{
		if ( item.getGroupId( ) == 1 )
		{
			// 圈消息
			if ( item.getItemId( ) == 1 )
			{
				talkSelRole = 1;
			}
			else if ( item.getItemId( ) == 0 )
			{
				talkSelRole = 0;
			}
			mSelTalkSetting.setText( mSettingMap.get( talkSelRole ) );
		}
		else
		{
			// 圈话题
			if ( item.getItemId( ) == 1 )
			{
				topicSelRole = 1;
			}
			else if ( item.getItemId( ) == 0 )
			{
				topicSelRole = 0;
			}
			mSelTopicSetting.setText( mSettingMap.get( topicSelRole ) );
			
		}
		return true;
	}
	
	@Override
	public void onClick( View v )
	{
		if ( v.equals( mTitleBack ) | v.equals(flLeft))
		{
			handleBackEvent( );
		}
		else if ( v.equals( mTalkSetting ) )
		{
			mCustomContextDialog = new CustomContextDialog(this,5,false);
			mCustomContextDialog.setListenner(groupTalkListener);
			mCustomContextDialog.show();
		}
		else if ( v.equals( mTopicSetting ) )
		{
			mCustomContextDialog = new CustomContextDialog(this,5,true);
			mCustomContextDialog.setListenner(groupTopicListener);
			mCustomContextDialog.show();
		}

	}
	
	private void handleBackEvent( )
	{
		if ( talkRole == talkSelRole && topicRole == topicSelRole )
		{
			// 未修改，直接返回
			finish( );
		}
		else
		{
			Intent intent = new Intent( this , GroupManagerActivity.class );
			intent.putExtra( "talk" , talkSelRole );
			intent.putExtra( "topic" , topicSelRole );
			this.setResult( Activity.RESULT_OK , intent );
			finish( );
		}
	}
	
	
}
