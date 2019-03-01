
package net.iaround.ui.group.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.eventbus.FirstEvent;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.datamodel.GroupModel;
import net.iaround.ui.group.bean.GroupInfoBean;
import net.iaround.ui.group.bean.GroupRoleBean;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @ClassName: GroupManagerActivity
 * @Description: 圈管理界面
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-12-10 下午2:35:29
 * 
 */
public class GroupManagerActivity extends GroupHandleActivity implements OnClickListener
{
	
	// {$控件声明
	
	/** 标题 */
	private TextView mTitleName;
	/** 返回按钮 */
	private ImageView mTitleBack;
	private FrameLayout flLeft;
	/** 修改圈资料 */
	private RelativeLayout mBtnEditInfo;
	/** 新成员加入需审核 */
	private CheckBox mCkbJoinCheck;
	/** 非成员查看话题 */ 
	private CheckBox mCkbViewTopicCheck;
	/** 通过圈号搜索 */
	private CheckBox mCkbSearchCheck;
	/** 权限管理 */
	private RelativeLayout mBtnEditRole;
	/** 圈子年度服务 */
	private LinearLayout mBtnRenew;
	/** 有效期 */
	private TextView mExpireDate;
	
	/** 加载框 */
	private Dialog mWaitDialog;
	
	// $}
	
	/** 圈子权限 */
	private GroupRoleBean mGroupRoleBean;
	
	/** 获取圈子权限的flag */
	private long FLAG_GET_GROUP_ROLE;
	
	/** 提交圈子权限的flag */
	private long FLAG_POST_GROUP_ROLE;
	
	/** 话题权限 */
	private int publishtopic = -1;
	/** 加入审核 */
	private int joincheck = -1;
	/** 发言权限 */
	private int talk = -1;
	/** 查看话题权限 */
	private int viewtopic = -1;
	/** 通过圈号搜索 */
	private int issearch = -1;
	
	private GroupInfoBean mGroupInfo;
	//欢迎语
	private String welcome;
	
	/** 修改圈资料返回的圈子实体，如果未修改则为null */
	private GroupInfoBean changeGroupInfoBean = null;
	
	private final int REQ_MODIFY_PERMISSIONS = 1;
	private final int REQ_EDIT_INFO = 2;
	private final int REQ_RENEW_SERVICE = 3;
	
	/** 修改圈资料后是否需要刷新( 1为需要，0为不需要 ) */
	private int hasEdit = 0;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_group_manager );
		mGroupInfo = getIntent( ).getParcelableExtra( "groupInfo" );
		welcome = getIntent().getStringExtra("welcome");
		if ( mGroupInfo == null )
		{
			CommonFunction.toastMsg( mContext , R.string.e_1 );
			finish( );
			return;
		}
		initViews( );
		setListeners( );
		initData( );
		
	}
	
	/**
	 * @Title: initViews
	 * @Description: 初始化控件
	 */
	private void initViews( )
	{
		mTitleName = (TextView) findViewById( R.id.tv_title );
		mTitleBack = (ImageView) findViewById( R.id.iv_left );
		flLeft = (FrameLayout) findViewById(R.id.fl_left);

		mTitleBack.setImageResource(R.drawable.title_back);
		mTitleBack.setVisibility(View.VISIBLE);

		mBtnEditInfo = (RelativeLayout) findViewById( R.id.group_edit_view );
		
		mCkbJoinCheck = (CheckBox) findViewById( R.id.new_member_checkbox );
		mCkbViewTopicCheck = (CheckBox) findViewById( R.id.non_member_view_topic_checkbox);
		mCkbSearchCheck = (CheckBox) findViewById( R.id.search_by_id_checkbox );//只能通过圈号搜索到
		
		mBtnEditRole = (RelativeLayout) findViewById( R.id.group_permission_settings_view );
		mBtnRenew = (LinearLayout) findViewById( R.id.group_renew_service_view);
		mExpireDate = (TextView) findViewById( R.id.group_expire_date );
		
		mWaitDialog = DialogUtil.getProgressDialog( mContext , "" ,
				getString( R.string.please_wait ) , new OnCancelListener( )
				{
					
					@Override
					public void onCancel( DialogInterface dialog )
					{
						finish( );
					}
				} );
		
		mTitleName.setText( R.string.group_setting );
		Log.v("group", "mGroupInfo.grouprole ===" + mGroupInfo.grouprole );
		Log.v("group", "mGroupInfo.classify ===" + mGroupInfo.classify );
		if (mGroupInfo.grouprole == 0
				 && mGroupInfo.classify > 1) {
			//仅大圈的圈主本人可见
			mBtnRenew.setVisibility(View.VISIBLE);
//			findViewById( R.id.last_divider ).setVisibility( View.VISIBLE );
		}
		else {
			mBtnRenew.setVisibility(View.GONE);
//			findViewById( R.id.last_divider ).setVisibility( View.GONE );
		}

		mBtnRenew.setVisibility(View.GONE);
	}
	
	/**
	 * @Title: setListeners
	 * @Description: 设置监听器
	 */
	private void setListeners( )
	{
		flLeft.setOnClickListener(this);
		mTitleBack.setOnClickListener( this );
		mBtnEditInfo.setOnClickListener( this );
		mBtnEditRole.setOnClickListener( this );
		
		mCkbJoinCheck.setEnabled( false );
		mCkbJoinCheck.setOnCheckedChangeListener( new OnCheckedChangeListener( )
		{
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView , boolean isChecked )
			{
				joincheck = isChecked ? 1 : 0;
			}
		} );
		
		mCkbViewTopicCheck.setEnabled( false );
		mCkbViewTopicCheck.setOnCheckedChangeListener( new OnCheckedChangeListener( )
		{
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView , boolean isChecked )
			{
				viewtopic = isChecked ? 1 : 0;
				Log.v("group", "viewtopic***" + viewtopic);
			}
		} );
		
		mCkbSearchCheck.setEnabled( false );
		mCkbSearchCheck.setOnCheckedChangeListener( new OnCheckedChangeListener( )
		{
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView , boolean isChecked )
			{
				issearch = isChecked ? 1 : 0;
				Log.v("group", "issearch***" + issearch);
			}
		} );
		
		mBtnRenew.setOnClickListener( this );
	}
	
	/**
	 * @Title: initData
	 * @Description: 初始化数据
	 */
	private void initData( )
	{
		showWaitDialog( true );
		FLAG_GET_GROUP_ROLE = GroupHttpProtocol.getGroupRole( mContext , mGroupInfo.id , this );
		if ( FLAG_GET_GROUP_ROLE < 0 )
		{
			showWaitDialog( false );
			handleDataFail( 107 , FLAG_GET_GROUP_ROLE );
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	private void refreshView()
	{
		mCkbJoinCheck.setEnabled( true );
		mCkbJoinCheck.setChecked( mGroupRoleBean.joincheck == 1 );
		
		mCkbSearchCheck.setEnabled( true );
		mCkbSearchCheck.setChecked( mGroupRoleBean.issearch == 1 );
		
		mCkbViewTopicCheck.setEnabled( true );
		mCkbViewTopicCheck.setChecked( mGroupRoleBean.viewtopic == 1 );
		
		if ( mGroupRoleBean.expires > 0) {
			SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");
			String expireDate = format.format( new Date( mGroupRoleBean.expires ) );
			mExpireDate.setText( String.format( getString( R.string.group_expires_date ), expireDate) );
		}
	}
	
	@Override
	protected void onResume( )
	{
		super.onResume( );
	}
	
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
		}
		return super.onKeyDown( keyCode , event );
	}
	
	@Override
	public void onGeneralError( final int e , final long flag )
	{
		super.onGeneralError( e , flag );
		runOnUiThread( new Runnable( )
		{
			
			@Override
			public void run( )
			{
				handleDataFail( e , flag );
			}
		} );
	}
	
	@Override
	public void onGeneralSuccess(final String result , final long flag )
	{
		super.onGeneralSuccess( result , flag );
		runOnUiThread( new Runnable( )
		{
			public void run( )
			{
				handleDataSuccess( result , flag );
			}
		} );
	}
	
	/**
	 * @Title: handleDataFail
	 * @Description: 处理数据失败
	 * @param error
	 */
	private void handleDataFail( int error , long flag )
	{
		if ( flag == FLAG_GET_GROUP_ROLE )
		{
			showWaitDialog( false );
			ErrorCode.toastError( mContext , error );
			finish( );
		}
		else if ( flag == FLAG_POST_GROUP_ROLE )
		{
			showWaitDialog( false );
			ErrorCode.toastError( mContext , error );
			if ( changeGroupInfoBean != null )
			{
				Intent intent = new Intent( );
				intent.putExtra( "groupInfo" , changeGroupInfoBean );
				intent.putExtra("hasEdit", hasEdit);
				setResult( RESULT_OK , intent );
			}
			finish( );
		}
	}
	
	/**
	 * @Title: handleDataSuccess
	 * @Description: 处理数据成功
	 * @param result
	 * @param flag
	 */
	private void handleDataSuccess(String result , long flag )
	{
		if ( !result.equals( "" ) )
		{
			if ( flag == FLAG_GET_GROUP_ROLE )
			{
				showWaitDialog( false );
				mGroupRoleBean = GsonUtil.getInstance( ).getServerBean( result ,
						GroupRoleBean.class );
				if ( mGroupRoleBean != null )
				{
					if ( mGroupRoleBean.isSuccess( ) )
					{					
						joincheck = mGroupRoleBean.joincheck;
						viewtopic = mGroupRoleBean.viewtopic;
						issearch = mGroupRoleBean.issearch;
						refreshView();
					}
					else
					{
						handleDataFail( mGroupRoleBean.error , FLAG_GET_GROUP_ROLE );
					}
				}
				else
				{
					handleDataFail( 107 , FLAG_GET_GROUP_ROLE );
				}
			}
			else if ( flag == FLAG_POST_GROUP_ROLE )
			{
				showWaitDialog( false );
				BaseServerBean bean = GsonUtil.getInstance( ).getServerBean( result ,
						BaseServerBean.class );
				if ( bean != null )
				{
					if ( bean.isSuccess( ) )
					{
						CommonFunction.toastMsg( mContext , R.string.save_success );
						boolean isReturn = false;
						Intent intent = null;
						if ( changeGroupInfoBean != null )
						{
							isReturn = true;
							intent = new Intent( );
							intent.putExtra( "groupInfo" , changeGroupInfoBean );							
						}
						if (issearch != mGroupRoleBean.issearch) {
							isReturn = true;
							hasEdit = 1;
							intent = new Intent( );
							intent.putExtra( "groupInfo" , mGroupInfo );	
						}
						if (joincheck != mGroupRoleBean.joincheck) {
							isReturn = true;
							hasEdit = 1;
							GroupModel.getInstance( ).clearByGroupID(
									Integer.parseInt( mGroupInfo.id ) );
							intent = new Intent( );
							intent.putExtra( "groupInfo" , mGroupInfo );
						}
						if (isReturn) {					
							intent.putExtra("hasEdit", hasEdit);
							setResult( RESULT_OK , intent );
						}
						
						finish( );
					}
					else
					{
						handleDataFail( bean.error , FLAG_POST_GROUP_ROLE );
					}
				}
				else
				{
					handleDataFail( 107 , FLAG_POST_GROUP_ROLE );
				}
			}
		}
		else
		{
			handleDataFail( 107 , -100 );
			
		}
	}
	
	/**
	 * @Title: handleBackEvent
	 * @Description: 处理返回事件
	 */
	private void handleBackEvent( )
	{
		if ( mGroupRoleBean == null )
		{
			finish( );
		}
		if ( publishtopic == -1 && joincheck == mGroupRoleBean.joincheck && talk == -1
				&& viewtopic == mGroupRoleBean.viewtopic && issearch == mGroupRoleBean.issearch )
		{
			// 未修改，直接退出
			if ( changeGroupInfoBean != null )
			{
				Intent intent = new Intent( );
				intent.putExtra( "groupInfo" , changeGroupInfoBean );
				intent.putExtra("hasEdit", hasEdit);
				setResult( RESULT_OK , intent );
			}
			finish( );
		}
		else
		{
			// 提交数据
			int postPublishTopic = publishtopic == -1 ? mGroupRoleBean.publishtopic : publishtopic;
			int postTalk = talk == -1 ? mGroupRoleBean.talk : talk;
			int postCheck = joincheck == -1 ? mGroupRoleBean.joincheck : joincheck;
			int postViewTopic = viewtopic == -1 ? mGroupRoleBean.viewtopic : viewtopic;
			int postSearch = issearch == -1 ? mGroupRoleBean.issearch : issearch;
			showWaitDialog( true );
			FLAG_POST_GROUP_ROLE = GroupHttpProtocol.postGroupRole( mContext , mGroupInfo.id ,
					postTalk , postPublishTopic , postCheck , postViewTopic, postSearch,  this );
			if ( FLAG_POST_GROUP_ROLE < 0 )
			{
				handleDataFail( 1000 , FLAG_POST_GROUP_ROLE );
			}
		}
	}
	
	@Override
	public void onClick( View v )
	{
		if ( v.equals( mTitleBack ) || v.equals(flLeft) )
		{
			FirstEvent event = new FirstEvent("");
			event.setUpdateChatbarInfo("updateChatbarInfo");
			EventBus.getDefault().post(event);
			handleBackEvent( );
		}
		else if ( v.equals( mBtnEditInfo ) )
		{
			// TODO 编辑圈资料
			if ( mGroupInfo == null )
			{
				CommonFunction.toastMsg( mContext , "获取设置信息出错，请返回重试。" );
				return;
			}
			else
			{
				Intent intent = new Intent( mContext , GroupEditActivity.class );
				intent.putExtra("welcome",welcome);
				intent.putExtra( "groupInfo" , mGroupInfo );
				startActivityForResult( intent , REQ_EDIT_INFO );
			}
		}
		else if ( v.equals( mBtnEditRole ) )
		{
			// TODO 权限管理
			if ( mGroupRoleBean == null )
			{
				CommonFunction.toastMsg( mContext , "获取设置信息出错，请返回重试。" );
				return;
			}
			Intent intent = new Intent( mContext , GroupRoleSettingActivity.class );
			intent.putExtra( "talk" , mGroupRoleBean.talk );
			intent.putExtra( "topic" , mGroupRoleBean.publishtopic );
			startActivityForResult( intent , REQ_MODIFY_PERMISSIONS );
		}
		else if ( v.equals( mBtnRenew ) ) {
			//大圈年度服务
			if ( mGroupInfo == null ) {
				return;
			}
			Intent intent = new Intent( mContext , GroupRenewServiceActivity.class );
			intent.putExtra( "groupid" , mGroupInfo.id );
			intent.putExtra( "groupname" , mGroupInfo.name );
			startActivityForResult( intent , REQ_RENEW_SERVICE );
		}
	}
	
	@Override
	protected void onActivityResult( int requestCode , int resultCode , Intent data )
	{
		super.onActivityResult( requestCode , resultCode , data );
		if ( requestCode == REQ_MODIFY_PERMISSIONS && resultCode == Activity.RESULT_OK )
		{
			talk = data.getIntExtra( "talk" , -1 );
			publishtopic = data.getIntExtra( "topic" , -1 );
		}
		else if ( requestCode == REQ_EDIT_INFO && resultCode == Activity.RESULT_OK )
		{
			changeGroupInfoBean = data.getParcelableExtra( "groupInfo" );
			this.mGroupInfo = changeGroupInfoBean;
			this.welcome = changeGroupInfoBean.welcome;
			Log.v("group", "Manage  hasEdit***" + data.getIntExtra("hasEdit", 0));
			if ( data.getIntExtra("hasEdit", 0) == 1 ) {
				hasEdit = 1;
			}			
		}
		else if (requestCode == REQ_RENEW_SERVICE && resultCode == Activity.RESULT_OK ) {
			String expireDate = data.getStringExtra( "expires" );
			mExpireDate.setText( String.format( getString( R.string.group_expires_date ), expireDate) );
		}
	}
	
	/**
	 * @Title: showWaitDialog
	 * @Description: 显示加载框
	 * @param isShow
	 */
	private void showWaitDialog( boolean isShow )
	{
		if ( mWaitDialog != null )
		{
			if ( isShow )
			{
				mWaitDialog.show( );
			}
			else
			{
				mWaitDialog.dismiss( );
			}
		}
	}
	
	@Override
	protected String getGroupId( )
	{
		return mGroupInfo.id;
	}
	
}
