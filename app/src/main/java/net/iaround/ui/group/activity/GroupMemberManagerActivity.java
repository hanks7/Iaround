
package net.iaround.ui.group.activity;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshPinnedSectionListView;
import com.handmark.pulltorefresh.library.PullToRefreshPinnedSectionListView.PinnedSectionListAdapter;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.entity.type.ChatFromType;
import net.iaround.model.entity.GeoData;
import net.iaround.model.im.enums.ProfileEntrance;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.datamodel.GroupModel;
import net.iaround.ui.group.bean.GroupBatchHandleBean;
import net.iaround.ui.group.bean.GroupMemberSearchBean;
import net.iaround.ui.group.bean.GroupSearchUser;
import net.iaround.ui.group.bean.ManagerRemainBean;
import net.iaround.ui.view.HeadPhotoView;

import java.util.ArrayList;
import java.util.Collections;


/**
 * @ClassName: GroupMemberManagerActivity
 * @Description: 圈成员管理界面
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-12-5 上午11:07:40
 *
 */
public class GroupMemberManagerActivity extends SuperActivity implements OnClickListener
{

	// {$ 控件声明

	/** 返回按钮 */
	private ImageView mTitleBack;
	/** 标题 */
	private TextView mTitleName;
	/** 管理按钮 */
	private TextView mTitleRight;

	/** 成员列表 */
	private PullToRefreshPinnedSectionListView mMemberListView;
	/** 编辑模式底部布局 */
	private LinearLayout mEditLayout;
	/** 设为管理员 */
	private TextView btnSetManager;
	/** 取消管理员 */
	private TextView btnCancelManager;
	/** 踢出圈子 */
	private TextView btnKickfromGroup;

	/** 加载框 */
	private Dialog mWaitDialog;

	// $}

	/** 当前列表模式 */
	private ManagerMode mCurrentMode = ManagerMode.EDIT;

	/** 获取成员列表的flag */
	private long FLAG_GET_MEMBER_LIST;

	/** 获取剩余管理员数量 */
	private long FLAG_GET_REMAIN_MANAGER_COUNT;

	/** 设置管理员的flag */
	private long FLAG_SET_MANAGER;
	/** 取消管理员的flag */
	private long FLAG_UNSET_MANAGER;
	/** 踢出圈子的flag */
	private long FLAG_KICK_USER;

	/** 用户列表的Bean */
	private GroupMemberSearchBean mGroupUserBean;

	/** 数据列表 */
	private ArrayList<GroupSearchUser> mMemberList = new ArrayList< GroupSearchUser >( );

	/** 数据适配器 */
	private DataAdapter mAdapter;

	/** 圈子id */
	private String mGroupId;

	/** 用户角色 */
	private int mGroupRole;

	/** 圈子名称 */
	private String mGroupName;

	/** 每页数 */
	private int PAGE_SIZE = 20;
	/** 当前页数 */
	private int mCurrentPage = 1;
	/** 总页数 */
	private int mTotalPage;

	/** 最大可选择数 */
	private int mMaxSelectCount = 30;
	/** 当前已选择数 */
	private int mCurrentSelCount = 0;

	/** 从服务端返回的剩余管理员数量 */
	private int mRemainManagerCount;

	private String mSetManagerUserIds;
	private ArrayList< Long > mSetManagerUserIdList = new ArrayList< Long >( );
	private String mDelManagerUserIds;
	private ArrayList< Long > mDelManagerUserIdList = new ArrayList< Long >( );
	private String mDelUserIds;
	private ArrayList< Long > mDelUserIdList = new ArrayList< Long >( );

	/** 是否有更改成员的标示 */
	private boolean isChangeUser = false;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_group_member_manager );

		mGroupId = getIntent( ).getStringExtra( "groupId" );
		mGroupRole = getIntent( ).getIntExtra( "groupRole" , 2 );
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
		mTitleBack = (ImageView) findViewById( R.id.iv_left );
		mTitleName = (TextView) findViewById( R.id.tv_title );
		mTitleRight = (TextView) findViewById( R.id.tv_right );

		mMemberListView = (PullToRefreshPinnedSectionListView) findViewById( R.id.member_listview );
		mMemberListView.setMode( Mode.BOTH );
		mMemberListView.getRefreshableView( ).setChoiceMode( ListView.CHOICE_MODE_SINGLE );
		mMemberListView.getRefreshableView( ).setDescendantFocusability(
				ListView.FOCUS_BLOCK_DESCENDANTS );
		mEditLayout = (LinearLayout) findViewById( R.id.member_operate_layout );
		btnSetManager = (TextView) findViewById( R.id.btn_set_manager );
		btnCancelManager = (TextView) findViewById( R.id.btn_cancel_manager );
		btnKickfromGroup = (TextView) findViewById( R.id.btn_kickoff_group );
		mWaitDialog = DialogUtil.getProgressDialog( mContext , "" ,
				getString( R.string.please_wait ) , new DialogInterface.OnCancelListener( )
				{

					@Override
					public void onCancel( DialogInterface dialog )
					{
						/*
						 * if (mCurrentMode == ManagerMode.SEE) { finish(); }
						 */
					}
				} );

		mTitleBack.setVisibility(View.VISIBLE);
		mTitleRight.setVisibility(View.VISIBLE);

		mTitleBack.setImageResource(R.drawable.title_back);
		mTitleName.setText( R.string.group_member_manager );// group_info_item_member
		mTitleRight.setText( R.string.complete );
		if ( mGroupRole == 0 )
		{

		}
		else if ( mGroupRole == 1 )
		{
			btnSetManager.setVisibility( View.GONE );
			btnCancelManager.setVisibility( View.GONE );
		}
		else
		{
			mTitleRight.setVisibility( View.INVISIBLE );
		}
	}

	/**
	 * @Title: setListeners
	 * @Description: 设置监听器
	 */
	private void setListeners( )
	{
		findViewById(R.id.fl_left).setOnClickListener(this);
		mTitleBack.setOnClickListener( this );
		mTitleRight.setOnClickListener( this );
		btnSetManager.setOnClickListener( this );
		btnCancelManager.setOnClickListener( this );
		btnKickfromGroup.setOnClickListener( this );

		// mMemberListView.setOnItemClickListener(onItemClickListener);
		mMemberListView
				.setOnRefreshListener( new PullToRefreshBase.OnRefreshListener2<ListView>( )
				{

					@Override
					public void onPullDownToRefresh( PullToRefreshBase< ListView > refreshView )
					{
						mCurrentPage = 1;
						mMemberList.clear( );
						initData( );
					}

					@Override
					public void onPullUpToRefresh( PullToRefreshBase< ListView > refreshView )
					{
						if ( mCurrentPage < mTotalPage )
						{
							loadPageData( mCurrentPage + 1 );
						}
						else
						{
							mMemberListView.postDelayed( new Runnable( )
							{

								@Override
								public void run( )
								{
									mMemberListView.onRefreshComplete( );
								}
							} , 200 );
						}
					}
				} );
	}

	/**
	 * @Title: initData
	 * @Description: 初始化数据
	 */
	private void initData( )
	{
		mMemberList.clear( );
		mAdapter = new DataAdapter( );
		mMemberListView.setAdapter( mAdapter );
		displayViews( );

		showWaitDialog( true );
		// 先从服务端获取剩余管理员数量
		FLAG_GET_REMAIN_MANAGER_COUNT = GroupHttpProtocol.getRemainManagerCount( mContext ,
				mGroupId , this );
		if ( FLAG_GET_REMAIN_MANAGER_COUNT < 0 )
		{
			showWaitDialog( false );
			handleDataFail( 104 , FLAG_GET_REMAIN_MANAGER_COUNT );
		}
	}

	/**
	 * @Title: loadPageData
	 * @Description: 加载分页数据
	 * @param pageIndex
	 */
	private void loadPageData( int pageIndex )
	{
		if ( pageIndex == 1 )
		{
			mMemberList.clear( );
		}
		FLAG_GET_MEMBER_LIST = GroupHttpProtocol.groupMember( mContext , mGroupId , pageIndex ,
				PAGE_SIZE , this );
		if ( FLAG_GET_MEMBER_LIST < 0 )
		{
			showWaitDialog( false );
			handleDataFail( 104 , FLAG_GET_MEMBER_LIST );
		}
	}

	/**
	 * @Title: displayViews
	 * @Description: 根据当前页面模式显示界面
	 */
	private void displayViews( )
	{
		if ( mCurrentMode == ManagerMode.SEE )
		{
			mTitleRight.setText( R.string.group_manager );
			mEditLayout.setVisibility( View.GONE );
			mTitleName.setText( R.string.group_info_item_member );
		}
		else if ( mCurrentMode == ManagerMode.EDIT )
		{
			mTitleRight.setText( R.string.complete );
			mEditLayout.setVisibility( View.VISIBLE );
			mTitleName.setText( R.string.group_member_manager );
		}
		mAdapter.notifyDataSetChanged( );
	}

	@Override
	protected void onDestroy( )
	{
		if ( mWaitDialog != null )
		{
			mWaitDialog.dismiss( );
			mWaitDialog = null;
		}
		super.onDestroy( );
	}

	@Override
	public boolean onKeyDown( int keyCode , KeyEvent event )
	{
		if ( keyCode == KeyEvent.KEYCODE_BACK )
		{
			if ( isChangeUser )
			{
				setResult( RESULT_OK );
			}
			finish( );
			return true;
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

			@Override
			public void run( )
			{
				handleDataSuccess( result , flag );
			}
		} );
	}

	/**
	 * @Title: handleDataSuccess
	 * @Description: 处理数据成功
	 * @param result
	 * @param flag
	 */

	private void handleDataSuccess(String result , long flag )
	{
		showWaitDialog( false );
		if ( flag == FLAG_GET_REMAIN_MANAGER_COUNT )
		{
			ManagerRemainBean bean = GsonUtil.getInstance( )
					.getServerBean( result , ManagerRemainBean.class );
			if ( bean != null )
			{
				if ( bean.isSuccess( ) )
				{
					mRemainManagerCount = bean.num;
					mCurrentPage = 1;
					loadPageData( 1 );
				}
				else
				{
					ErrorCode.showError( mContext , result );
					if ( flag == FLAG_GET_REMAIN_MANAGER_COUNT )
					{
						finish( );
					}

				}
			}
			else
			{
				handleDataFail( 104 , flag );


			}
		}
		else if ( flag == FLAG_GET_MEMBER_LIST )
		{
			showWaitDialog( false );
			mMemberListView.onRefreshComplete( );
			mGroupUserBean = GsonUtil.getInstance( ).getServerBean(
					result , GroupMemberSearchBean.class );
			if ( mGroupUserBean != null )
			{
				if ( mGroupUserBean.isSuccess( ) )
				{
					mCurrentPage = mGroupUserBean.pageno;
					mTotalPage = mGroupUserBean.amount / PAGE_SIZE;
					if ( mGroupUserBean.amount % PAGE_SIZE > 0 )
					{
						mTotalPage++;
					}
					handleGroupUserData( mGroupUserBean.users );
				}
				else
				{
					ErrorCode.showError( mContext , result );
				}
			}
			else
			{
				handleDataFail( 104 , flag );
			}

		}
		else if ( flag == FLAG_SET_MANAGER || flag == FLAG_UNSET_MANAGER
				|| flag == FLAG_KICK_USER )
		{
			showWaitDialog( false );
			GroupBatchHandleBean bean = GsonUtil.getInstance( )
					.getServerBean( result , GroupBatchHandleBean.class );
			if ( bean != null )
			{
				if ( bean.isSuccess( ) )
				{
					mRemainManagerCount = bean.num;
					if ( flag == FLAG_SET_MANAGER )
					{
						handleMemberRoleChangeSuccess( 1 );
					}
					else if ( flag == FLAG_UNSET_MANAGER )
					{
						handleMemberRoleChangeSuccess( 2 );
					}
					else if ( flag == FLAG_KICK_USER )
					{
						handleMemberRoleChangeSuccess( 3 );
					}
				}
				else
				{
					ErrorCode.showError( mContext , result );
				}
			}
			else
			{
				handleDataFail( 104 , flag );
			}
		}
	}

	/**
	 * @Title: handleSetManagerDataSuccess
	 * @Description: 对列表用户操作成功之后的数据处理
	 * @param type
	 *            操作类型(1设置管理员；2取消管理员；3踢出圈子)
	 */
	private void handleMemberRoleChangeSuccess( int type )
	{
		ArrayList< GroupSearchUser > userList = new ArrayList< GroupSearchUser >( );
		// Collections.sort(mMemberList);
		for ( GroupSearchUser user : mMemberList )
		{
			if ( user.headerType == GroupSearchUser.CONTENT )
			{
				user.isCheck = false;
				// 用户数据
				if ( type == 1 )
				{
					// 设为管理员
					if ( mSetManagerUserIdList.contains( user.user.userid ) )
					{
						user.grouprole = 1;
						// 添加到管理员列表的缓存
						GroupModel.getInstance( ).addToManagerIdList(
								String.valueOf( user.user.userid ) );
					}
				}
				else if ( type == 2 )
				{
					// 取消管理员
					if ( mDelManagerUserIdList.contains( user.user.userid ) )
					{
						user.grouprole = 2;
						// 从管理员列表的缓存中移除
						GroupModel.getInstance( ).delFromManagerIdList(
								String.valueOf( user.user.userid ) );
					}
				}
				else if ( type == 3 )
				{
					// 踢出圈子
					if ( mDelUserIdList.contains( user.user.userid ) )
					{
						user.grouprole = -1;
						// 从管理员列表的缓存中移除
						GroupModel.getInstance( ).delFromManagerIdList(
								String.valueOf( user.user.userid ) );
					}
				}
				if ( user.grouprole != -1 )
				{
					userList.add( user );
				}
			}
		}
		Collections.sort( userList );
		mMemberList.clear( );
		handleGroupUserData( userList );
		mCurrentSelCount = 0;
	}

	/**
	 * @Title: handleGroupUserData
	 * @Description: 处理数据，进行分组及添加标题
	 * @param users
	 */
	private void handleGroupUserData( ArrayList< GroupSearchUser > users )
	{
		int tmpRole = -1;
		if ( mMemberList.isEmpty( ) )
		{
			// 第一次加载数据或刷新列表数据
		}
		else
		{
			// 否则取上一页最后一条数据的角色
			tmpRole = mMemberList.get( mMemberList.size( ) - 1 ).grouprole;
		}
		for ( int i = 0 ; i < users.size( ) ; i++ )
		{
			if ( users.get( i ).grouprole != tmpRole )
			{
				GroupSearchUser user = new GroupSearchUser( );
				user.headerType = GroupSearchUser.HEADER;
				user.grouprole = users.get( i ).grouprole;
				mMemberList.add( user );
				tmpRole = user.grouprole;
			}
			mMemberList.add( users.get( i ) );
		}
		// 刷新数据显示
		// mMemberListView.setAdapter(mAdapter);
		// mAdapter.notifyDataSetChanged();
		if ( users.size( ) == 1 )
		{
			// 无任何圈管理员/圈成员时，添加无任何圈成员的显示。
			GroupSearchUser user = new GroupSearchUser( );
			user.headerType = GroupSearchUser.HEADER;
			user.grouprole = 2;
			mMemberList.add( user );
			GroupSearchUser userEmpty = new GroupSearchUser( );
			userEmpty.headerType = GroupSearchUser.CONTENT;
			userEmpty.contentType = 1;
			mMemberList.add( userEmpty );

		}

		displayViews( );
	}

	/**
	 * @Title: handleDataFail
	 * @Description: 处理数据失败
	 * @param e
	 * @param flag
	 */
	private void handleDataFail( int e , long flag )
	{
		if ( flag == FLAG_GET_MEMBER_LIST || flag == FLAG_KICK_USER
				|| flag == FLAG_SET_MANAGER || flag == FLAG_UNSET_MANAGER )
		{
			ErrorCode.toastError( mContext , e );
			showWaitDialog( false );
		}
		if ( flag == FLAG_GET_REMAIN_MANAGER_COUNT )
		{
			finish( );
		}
	}

	@Override
	public void onClick( View v )
	{
		switch ( v.getId( ) )
		{
			case R.id.fl_left:
			case R.id.iv_left:
				/*
				 * if (mCurrentMode == ManagerMode.EDIT) { mCurrentMode =
				 * ManagerMode.SEE; displayViews(); } else if (mCurrentMode ==
				 * ManagerMode.SEE) { if(isChangeUser){ setResult(RESULT_OK); }
				 * finish(); }
				 */
				if ( isChangeUser )
				{
					setResult( RESULT_OK );
				}
				finish( );
				break;
			case R.id.tv_right :
				if ( mCurrentMode == ManagerMode.SEE )
				{
					mCurrentMode = ManagerMode.EDIT;
					displayViews( );
				}
				else
				{
					if ( isChangeUser )
					{
						setResult( RESULT_OK );
					}
					finish( );
				}
				break;
			case R.id.btn_set_manager :
				// 批量设置管理员

				mSetManagerUserIds = "";
				mSetManagerUserIdList.clear( );
				int normalUserCount = 0;
				for ( GroupSearchUser user : mMemberList )
				{
					if ( user.isCheck )
					{
						mSetManagerUserIdList.add( user.user.userid );
						if ( user.grouprole == 2 )
						{
							mSetManagerUserIds += user.user.userid + ",";
							normalUserCount++;
						}
					}
				}
				// 未勾选或管理员数量大于剩余管理员数量
				if ( mSetManagerUserIdList.size( ) == 0 )
				{
					showWaitDialog( false );
					CommonFunction.toastMsg( mContext , R.string.not_check_any_user );
					return;
				}
				else if ( normalUserCount == 0 && mSetManagerUserIdList.size( ) > 0 )
				{
					showWaitDialog( false );
					CommonFunction.toastMsg( mContext , R.string.user_is_group_manager );
					return;
				}
				else if ( normalUserCount > mRemainManagerCount )
				{
					showWaitDialog( false );
					String msg = getString( R.string.set_manager_overflow );
					String infoMsg = String.format( msg , mRemainManagerCount );
					DialogUtil.showOKDialog( mContext , getString( R.string.dialog_title ) ,
							infoMsg , null );
					return;
				}
				DialogUtil.showTwoButtonDialog( mContext , getString( R.string.dialog_title ) ,
						getString( R.string.set_manager_info ) , getString( R.string.cancel ) ,
						getString( R.string.ok ) , new View.OnClickListener( )
						{

							@Override
							public void onClick( View v )
							{
								showWaitDialog( false );
							}
						} , new View.OnClickListener( )
						{

							@Override
							public void onClick( View v )
							{
								showWaitDialog( true );
								isChangeUser = true;
								FLAG_SET_MANAGER = GroupHttpProtocol.batchSetManager(
										mContext , mGroupId , mSetManagerUserIds ,
										GroupMemberManagerActivity.this );
								if ( FLAG_SET_MANAGER < 0 )
								{
									showWaitDialog( false );
									handleDataFail( 107 , FLAG_SET_MANAGER );
								}
							}

						} );

				break;
			case R.id.btn_cancel_manager :
				// 取消管理员
				showWaitDialog( true );
				mDelManagerUserIds = "";
				mDelManagerUserIdList.clear( );
				int managerCount = 0;
				for ( GroupSearchUser user : mMemberList )
				{
					if ( user.isCheck )
					{
						mDelManagerUserIdList.add( user.user.userid );
						if ( user.grouprole == 1 )
						{
							mDelManagerUserIds += user.user.userid + ",";
							managerCount++;
						}
					}
				}
				if ( mDelManagerUserIdList.size( ) == 0 )
				{
					showWaitDialog( false );
					CommonFunction.toastMsg( mContext , R.string.not_check_any_user );
					return;
				}
				else if ( managerCount == 0 && mDelManagerUserIdList.size( ) > 0 )
				{
					showWaitDialog( false );
					CommonFunction.toastMsg( mContext , R.string.user_is_normal_user );
					return;
				}
				isChangeUser = true;
				FLAG_UNSET_MANAGER = GroupHttpProtocol.batchDelManager( mContext , mGroupId ,
						mDelManagerUserIds , this );
				if ( FLAG_UNSET_MANAGER < 0 )
				{
					showWaitDialog( false );
					handleDataFail( 107 , FLAG_UNSET_MANAGER );
				}
				break;
			case R.id.btn_kickoff_group :
				// 踢出圈子
				mDelUserIds = "";
				mDelUserIdList.clear( );
				for ( GroupSearchUser user : mMemberList )
				{
					if ( user.isCheck )
					{
						mDelUserIds += user.user.userid + ",";
						mDelUserIdList.add( user.user.userid );
					}
				}
				if ( mDelUserIdList.size( ) == 0 )
				{
					showWaitDialog( false );
					CommonFunction.toastMsg( mContext , R.string.not_check_any_user );
					return;
				}
				DialogUtil.showTwoButtonDialog( mContext , getString( R.string.dialog_title ) ,
						getString( R.string.del_group_member_info ) ,
						getString( R.string.cancel ) , getString( R.string.ok ) ,
						new View.OnClickListener( )
						{

							@Override
							public void onClick( View v )
							{
								showWaitDialog( false );
							}
						} , new View.OnClickListener( )
						{

							@Override
							public void onClick( View v )
							{
								showWaitDialog( true );
								isChangeUser = true;
								FLAG_KICK_USER = GroupHttpProtocol.batchKickUser( mContext ,
										mGroupId , mDelUserIds ,
										GroupMemberManagerActivity.this );
								if ( FLAG_KICK_USER < 0 )
								{
									showWaitDialog( false );
									handleDataFail( 107 , FLAG_KICK_USER );
								}
							}

						} );
				break;

			default :
				break;
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
				mWaitDialog.hide( );
			}
		}
	}

	/**
	 * @ClassName: DataAdapter
	 * @Description: 数据适配器
	 * @author zhonglong kylin17@foxmail.com
	 * @date 2013-12-11 下午2:24:13
	 *
	 */
	class DataAdapter extends BaseAdapter implements PinnedSectionListAdapter
	{

		private GeoData userGeoData;

		public DataAdapter( )
		{
			this.userGeoData = LocationUtil.getCurrentGeo( mContext );
		}

		@Override
		public int getCount( )
		{
			return mMemberList.size( );
		}

		@Override
		public Object getItem(int position )
		{
			return mMemberList.get( position );
		}

		@Override
		public long getItemId( int position )
		{
			return position;
		}

		@Override
		public View getView(final int position , View convertView , ViewGroup parent )
		{
			ViewHolder viewHolder = null;
			if ( convertView == null )
			{
				viewHolder = new ViewHolder( );
				convertView = View.inflate( parent.getContext( ) ,
						R.layout.user_nearby_list_item2 , null );
				viewHolder.groupTitle = (TextView) convertView
						.findViewById( R.id.list_group_title );
				viewHolder.groupContent = (RelativeLayout) convertView
						.findViewById( R.id.list_group_content );
				viewHolder.groupContentEmpty = (TextView) convertView
						.findViewById( R.id.content_empty );
				viewHolder.checkUser = (CheckBox) convertView
						.findViewById( R.id.user_checkbox );
				viewHolder.userIcon = (HeadPhotoView) convertView
						.findViewById( R.id.friend_icon );
				viewHolder.tvNickName = (TextView) convertView
						.findViewById( R.id.tvNickName );
				viewHolder.tvAge = (TextView) convertView.findViewById( R.id.tvAge );
				viewHolder.distance = (TextView) convertView.findViewById( R.id.tvDistance );
				viewHolder.tvState = (TextView) convertView.findViewById( R.id.tvState );
				viewHolder.tvSign = (TextView) convertView.findViewById( R.id.tvSign );
				viewHolder.weiboIcon = (LinearLayout) convertView
						.findViewById( R.id.llWeiboIcon );
				viewHolder.divider = convertView.findViewById( R.id.divider );
				//添加性别
				viewHolder.ivSex = (ImageView) convertView.findViewById(R.id.iv_sex);
				viewHolder.llSexAndAge = (LinearLayout) convertView.findViewById(R.id.info_center);

				convertView.setTag( viewHolder );
			}
			else
			{
				viewHolder = ( ViewHolder ) convertView.getTag( );
			}

			final GroupSearchUser groupUser = mMemberList.get( position );
			if ( mCurrentMode == ManagerMode.EDIT )
			{
				if ( mGroupRole == 0 )
				{
					if ( groupUser.grouprole == 0 )
					{
						viewHolder.checkUser.setVisibility( View.GONE );
					}
					else
					{
						viewHolder.checkUser.setVisibility( View.VISIBLE );
					}
				}
				else if ( mGroupRole == 1 )
				{
					if ( groupUser.grouprole == 0 || groupUser.grouprole == 1 )
					{
						viewHolder.checkUser.setVisibility( View.GONE );
					}
					else
					{
						viewHolder.checkUser.setVisibility( View.VISIBLE );
					}
				}
				else
				{
					viewHolder.checkUser.setVisibility( View.VISIBLE );
				}
			}
			else
			{
				viewHolder.checkUser.setVisibility( View.GONE );
			}

			if ( groupUser.headerType == GroupSearchUser.HEADER )
			{
				viewHolder.groupTitle.setVisibility( View.VISIBLE );
				viewHolder.groupContent.setVisibility( View.GONE );
				viewHolder.groupContentEmpty.setVisibility( View.GONE );
				switch ( groupUser.grouprole )
				{
					case 0 :
						viewHolder.groupTitle.setText( R.string.group_owner );
						break;
					case 1 :
						viewHolder.groupTitle.setText( R.string.group_managers );
						break;
					case 2 :
						viewHolder.groupTitle.setText( R.string.group_member );
						break;
					default :
						break;
				}
				convertView.setOnClickListener( new View.OnClickListener( )
				{

					@Override
					public void onClick( View v )
					{

					}
				} );

			}
			else
			{
				if ( groupUser.contentType == 1 )
				{
					viewHolder.groupTitle.setVisibility( View.GONE );
					viewHolder.groupContent.setVisibility( View.GONE );
					viewHolder.groupContentEmpty.setVisibility( View.VISIBLE );
					convertView.setOnClickListener( new View.OnClickListener( )
					{

						@Override
						public void onClick( View v )
						{

						}
					} );
				}
				else
				{
					viewHolder.groupTitle.setVisibility( View.GONE );
					viewHolder.groupContent.setVisibility( View.VISIBLE );
					viewHolder.groupContentEmpty.setVisibility( View.GONE );

					viewHolder.groupTitle.setVisibility( View.GONE );
					viewHolder.groupContent.setVisibility( View.VISIBLE );
					// 头像
					viewHolder.userIcon.execute( ChatFromType.Group,groupUser.user.convertBaseToUser(),null );

					if ( groupUser.isCheck )
					{
						viewHolder.checkUser.setChecked( true );
					}
					else
					{
						viewHolder.checkUser.setChecked( false );
					}
					// 昵称
					String name = groupUser.user.nickname;
					if ( name == null || name.length( ) <= 0 )
					{
						name = String.valueOf( groupUser.user.userid );
					}
					SpannableString spName = FaceManager.getInstance( parent.getContext( ) )
							.parseIconForString( viewHolder.tvNickName , parent.getContext( ) ,
									name , 20 );
					viewHolder.tvNickName.setText( spName );

					// 年龄
					if ( groupUser.user.age <= 0 )
					{
						viewHolder.tvAge.setText( R.string.unknown );
					}
					else
					{
						viewHolder.tvAge.setText( String.valueOf( groupUser.user.age ) );
					}

					// 性别
					int sex = groupUser.user.getSex( );
					if ( sex == 2 )
					{//女
						viewHolder.ivSex.setImageResource(R.drawable.z_common_female_icon);
						viewHolder.llSexAndAge.setBackground(getResources().getDrawable(R.drawable.group_member_age_girl_bg));
					} else if (sex == 1)
					{//男
						viewHolder.ivSex.setImageResource(R.drawable.z_common_male_icon);
						viewHolder.llSexAndAge.setBackground(getResources().getDrawable(R.drawable.group_member_age_man_bg));
					}

					// 距离
					int distance = -1;
					try
					{
						distance = LocationUtil.calculateDistance( userGeoData.getLng( ) ,
								userGeoData.getLat( ) , groupUser.user.lng ,
								groupUser.user.lat );
					}
					catch ( Exception e )
					{

					}
					if ( distance < 0 )
					{ // 不可知
						viewHolder.distance.setText( R.string.unable_to_get_distance );
					}
					else
					{
						viewHolder.distance.setText( CommonFunction
								.covertSelfDistance( distance ) );
					}

					// 在线状态
					String time = TimeFormat.timeFormat1(viewHolder.tvState.getContext() ,
							groupUser.user.lastonlinetime );
					if ( time != null && time.length( ) > 0 )
					{
						viewHolder.tvState.setText( time );
					}
					else
					{
						viewHolder.tvState.setText( R.string.unable_to_get_time );
					}

					// 签名
					String infor = groupUser.user.getPersonalInfor( viewHolder.tvSign
							.getContext( ) );

					if ( infor != null && !"".equals(infor) )
					{

						SpannableString spSign = FaceManager
								.getInstance( parent.getContext( ) ).parseIconForString(
										viewHolder.tvSign , parent.getContext( ) , infor , 13 );
						viewHolder.tvSign.setVisibility(View.VISIBLE);
						viewHolder.tvSign.setText( spSign );
					}

					// TODO 微博

					// CommonFunction.showRightIcon( viewHolder.weiboIcon ,
					// groupUser ,
					// parent.getContext( ) );

					ImageView[ ] weibos = new ImageView[ 6 ];
					weibos[ 0 ] = (ImageView) convertView.findViewById( R.id.weibos_icon_1 );
					weibos[ 1 ] = (ImageView) convertView.findViewById( R.id.weibos_icon_2 );
					weibos[ 2 ] = (ImageView) convertView.findViewById( R.id.weibos_icon_3 );
					weibos[ 3 ] = (ImageView) convertView.findViewById( R.id.weibos_icon_4 );
					weibos[ 4 ] = (ImageView) convertView.findViewById( R.id.weibos_icon_5 );
					weibos[ 5 ] = (ImageView) convertView.findViewById( R.id.weibos_icon_6 );
//					CommonFunction.showWeibosIcon( weibos ,
//							User.parseWeiboStr( groupUser.user.weibo ) ,
//							groupUser.user.occupation , parent.getContext( ) );//jiqiang

					if ( position == 1 || position == mMemberList.size() - 1 )
					{
						viewHolder.divider.setVisibility( View.GONE );
					}
					else
					{
						viewHolder.divider.setVisibility( View.VISIBLE );
					}

					viewHolder.checkUser.setOnClickListener( new OnClickListener( )
					{

						@Override
						public void onClick( View v )
						{
							handleItemCheck( groupUser , (CompoundButton) v );
						}
					} );
					convertView.setOnClickListener( new OnClickListener( )
					{

						@Override
						public void onClick( View v )
						{
							CommonFunction.log( "" , "--->position==" + position );
							if ( mCurrentMode == ManagerMode.SEE )
							{
								// 查看用户的个人资料
								int entrance = ProfileEntrance.GROUP_MEMBER;
								if ( groupUser != null
										&& groupUser.headerType == GroupSearchUser.CONTENT )
								{
//									SpaceOther.launchUser( mContext , groupUser.user.userid ,
//											groupUser.user.convertBaseToUser( ) ,
//											ChatFromType.Group , mGroupName ,
//											Long.parseLong( mGroupId ) );//jiqiang  个人资料要兼容
								}
							}
						}
					} );
				}
			}

			return convertView;
		}

		protected void handleItemCheck(GroupSearchUser groupUser , CompoundButton buttonView )
		{
			if ( mCurrentMode == ManagerMode.EDIT )
			{
				if ( !groupUser.isCheck )
				{
					if ( groupUser.user.userid == Common.getInstance( ).loginUser.getUid( ) )
					{
						CommonFunction.toastMsg( mContext , R.string.cannot_operate_self );
						buttonView.setChecked( false );
						return;
					}
					if ( groupUser.grouprole == 0 )
					{
						CommonFunction.toastMsg( mContext , R.string.cannot_operate_owner );
						buttonView.setChecked( false );
						return;
					}

					if ( mCurrentSelCount >= mMaxSelectCount )
					{
						CommonFunction.toastMsg( mContext ,
								R.string.you_can_operate_only_50_user );
						buttonView.setChecked( false );
						return;
					}

					groupUser.isCheck = true;
					mCurrentSelCount++;
					buttonView.setChecked( true );

				}
				else
				{
					groupUser.isCheck = false;
					mCurrentSelCount--;
					buttonView.setChecked( false );
				}
			}
		}

		@Override
		public int getItemViewType( int position )
		{
			return mMemberList.get( position ).headerType;
		}

		@Override
		public int getViewTypeCount( )
		{
			return 2;
		}

		@Override
		public boolean isItemViewTypePinned( int viewType )
		{
			return viewType == GroupSearchUser.HEADER;
		}
	}

	static class ViewHolder
	{
		TextView groupTitle;//圈主，管理员，圈成员
		TextView groupContentEmpty;//空内容
		RelativeLayout groupContent;
		CheckBox checkUser;//成员管理
		HeadPhotoView userIcon;//用户头像
		TextView tvNickName;//昵称
		TextView tvAge;//年龄
		TextView distance;//距离
		TextView tvState;//在线状态
		TextView tvSign;//签名
		LinearLayout weiboIcon;
		View divider;//分割线
		ImageView ivSex;//性别icon
		LinearLayout llSexAndAge;
	}

	enum ManagerMode
	{
		SEE ,
		EDIT ,
	}

}
