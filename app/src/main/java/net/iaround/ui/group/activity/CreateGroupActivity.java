
package net.iaround.ui.group.activity;


import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.model.entity.GeoData;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.comon.SuperView;
import net.iaround.ui.group.CreateGroupEditInfo;
import net.iaround.ui.group.CreateGroupJoinCondition;
import net.iaround.ui.group.ICreateGroupParentCallback;
import net.iaround.ui.group.INextCheck;
import net.iaround.ui.group.bean.CreateGroupInfo;
import net.iaround.ui.group.bean.GroupNextStep;
import net.iaround.ui.group.bean.GroupTypeBean;
import net.iaround.ui.group.view.CreateGroupSelectCenter;
import net.iaround.ui.group.view.CreateGroupSubmitView;
import net.iaround.ui.group.view.CustomViewPager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * @ClassName: CreateGroupActivity
 * @Description: 创建圈子
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-12-7 上午11:37:59
 *
 */
public class CreateGroupActivity extends SuperActivity implements OnClickListener ,
		ICreateGroupParentCallback
{

	// 当前步骤标题
	private String[ ] mStepTitles;
	// ViewPager容器
	private CustomViewPager mViewPager;
	// 标题
	private TextView mTextTitle;
	// 返回按钮
	private ImageView mBtnBack;
	private FrameLayout flLeft;
	// 下一步按钮
	private TextView mBtnNext;
	// 当前步骤
	private int mCurrentStep = 0;
	// 当前的view
	private SuperView mCurrentPagerView;

	private GroupPagerAdapter mPagerAdapter;

	private Dialog mWaitDialog;

	/** 是否第一次初始化 */
	private boolean isFirstInit = true;

	SparseBooleanArray requestArray = new SparseBooleanArray( );

	/** 是否为创建圈子 */
	public static boolean isCreateGroup = true;

	// 圈子类型的Bean
	private GroupTypeBean mGroupTypeBean;

	/************************** 请求数据的flag ***************/
	/** 获取圈子类型的flag */
	public static long GET_GROUP_TYPE_FLAG;

	/** 上传圈图的flag */
	public static long UPLOAD_GROUPIMG_FLAG;

	/** 金币与圈子范围对应表的flag */
	public static long GET_GOLD_DISTANCE_FLAG;

	/** 创建圈子的flag */
	public static long CREATE_GROUP_FLAG;

	/** 获取用户当前的金币数量 */
	public static long GET_USER_GOLD_FLAG;

	private CreateGroupInfo mGroupInfo;

	private GeoData mGeoData;

	private boolean isSubmit = false;
	private int isFrom = 0;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_create_group );

		try
		{
			initViews( );
			setListeners( );
			initData( );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}


	}

	/**
	 * @Title: initViews
	 * @Description: 初始化界面控件
	 */
	private void initViews( )
	{
		mTextTitle = (TextView) findViewById( R.id.tv_title );
		mBtnBack = (ImageView) findViewById( R.id.iv_left );
		flLeft = (FrameLayout) findViewById(R.id.fl_left);
		mBtnNext = (TextView) findViewById( R.id.tv_right );

		mBtnBack.setVisibility(View.VISIBLE);
		mBtnBack.setImageResource(R.drawable.title_back);
//		getString( R.string.create_group_step1 ) ,
		mStepTitles = new String[ ]
				{
						getString( R.string.create_group_step2 ) ,
						getString( R.string.create_group_step3 ) ,
						getString( R.string.create_group_step4 ) , };
		mBtnNext.setText( R.string.next );
		mBtnNext.setTextColor(getResources().getColor(R.color.login_btn));
//		mBtnNext.setVisibility( View.GONE );
		mBtnNext.setVisibility(View.VISIBLE);//默认第一页是填写资料，显示下一步
		mViewPager = ( CustomViewPager ) findViewById( R.id.viewpager );
		mViewPager.setIsCanFlip( false );
	}

	/**
	 * @Title: setListeners
	 * @Description: 设置监听器
	 */
	private void setListeners( )
	{
		flLeft.setOnClickListener(this);
		mBtnBack.setOnClickListener( this );
		mBtnNext.setOnClickListener( this );
	}

	/**
	 * @Title: initData
	 * @Description: 初始化数据显示
	 */
	private void initData( )
	{
		mGroupInfo = new CreateGroupInfo( );
		mGroupInfo.groupType = getIntent().getIntExtra("group_type", 0);
		mGroupInfo.diamondCost = getIntent().getIntExtra("diamond_cost", 0);

		mPagerAdapter = new GroupPagerAdapter( );
		mViewPager.setAdapter( mPagerAdapter );
		// 防止view被重新加载
//		mViewPager.setOffscreenPageLimit( 4 );//yuchao  取消选择类型界面
		mViewPager.setOffscreenPageLimit( 3 );
		Intent intent = getIntent( );
		int pageIndex = intent.getIntExtra( "pagerIndex" , 0 );
		isCreateGroup = intent.getBooleanExtra( "isCreateGroup" , true );
		mViewPager.setCurrentItem( pageIndex );
		isFrom = intent.getIntExtra("isFrom",0);

		if ( isCreateGroup )
		{//创建圈子
			mTextTitle.setText( mStepTitles[ 0 ] );
//			mTextTitle.setText(R.string.create_chatbar_selectcenter_title);
		} else
		{
			// TODO 如果不是从创建圈子进来，则需要重新设置标题
			mBtnNext.setVisibility( View.INVISIBLE );
			switch ( pageIndex ) {
				case 0:
					// 选择圈类型 取消圈类型
//					mTextTitle.setText( getString( R.string.change_group_type ) );
					break;
				case 1:
					// 选择圈中心
					if (isFrom == 1) {//新版创建圈子进来
						mTextTitle.setText(getString(R.string.create_chatbar_selectcenter_title));
					} else
					{
						mTextTitle.setText( getString( R.string.change_group_center ) );
					}
					// 修改圈中心允许更改位置
					/*
					 * mGeoData = new GeoData();
					 * mGeoData.setLat(intent.getIntExtra("lat", 0));
					 * mGeoData.setLng(intent.getIntExtra("lng", 0));
					 */

					break;
				default :
					break;
			}
		}
		mViewPager.setOnPageChangeListener( new OnPageChangeListener( )
		{

			@Override
			public void onPageSelected( int position )
			{
				boolean isBack = false;
				if ( mCurrentStep > position )
				{
					isBack = true;
				}
				mCurrentStep = position;
				CommonFunction.log( "create_group" , "position " + position + " selected" );
				mTextTitle.setText( mStepTitles[ position ] );
				/*
				 * if(position == mPagerAdapter.getCount() - 1){
				 * mBtnNext.setVisibility(View.VISIBLE);
				 * mBtnNext.setText(R.string.complete); }else{
				 */
				if ( position == 1 || position == 2 )
				{
					mBtnNext.setVisibility( View.GONE );
				}
//				else if (position == 0)
//				{
//					mBtnNext.setVisibility( View.VISIBLE );
//					mBtnNext.setText( R.string.next );
//				}//yuchao  默认填写资料是第一步，下一步按钮显示
				initDataAtPager( position , isBack );
			}

			@Override
			public void onPageScrolled( int arg0 , float arg1 , int arg2 )
			{

			}

			@Override
			public void onPageScrollStateChanged( int state )
			{
				/*
				 * switch (state) { case ViewPager.SCROLL_STATE_SETTLING:
				 * mViewPager.setIsCanFlip(false); break; case
				 * ViewPager.SCROLL_STATE_IDLE: mViewPager.setIsCanFlip(true);
				 * break; }
				 */
			}
		} );

//		mWaitDialog.setOnKeyListener( new OnKeyListener( )
//		{
//
//			@Override
//			public boolean onKey( DialogInterface dialog , int keyCode , KeyEvent event )
//			{
//				if ( keyCode == KeyEvent.KEYCODE_BACK )
//				{
//					dialog.dismiss( );
//					if ( mCurrentStep == 0 )
//					{
//						finish( );
//					}
//					else
//					{
//						mViewPager.setCurrentItem( mCurrentStep - 1 );
//					}
//					return true;
//				}
//				return false;
//			}
//		} );

	}

	/**
	 * @Title: initDataAtPager
	 * @Description: 加载某一页的数据
	 * @param position
	 * @param isBack
	 *            是否返回操作
	 */
	private void initDataAtPager( int position , boolean isBack )
	{
		mCurrentPagerView = ( SuperView ) mViewPager.findViewWithTag( "pager_" + position );
		if ( mCurrentPagerView != null )
		{
			if (position == 2) {
				((CreateGroupSubmitView)mCurrentPagerView).refreshView(mGroupInfo);
			}
			INextCheck view = ( INextCheck ) mCurrentPagerView;
			view.initData( mGroupTypeBean , isBack );
		}
	}

	@Override
	public void onClick( View v )
	{
		if ( v.equals( flLeft ) || v.equals(mBtnBack) )
		{
			if ( mCurrentStep == 0 || !isCreateGroup )
			{
				finish( );
			}
			else
			{
				CommonFunction.hideInputMethod( mContext , mViewPager );
				mViewPager.setCurrentItem( mCurrentStep - 1 );
			}
		}
		else if ( v.equals( mBtnNext ) )
		{
			// TODO 判断是否允许进入下一步
			// TODO 如果不是从创建圈子过来，有可能是完成操作
			if ( mCurrentPagerView instanceof INextCheck )
			{
				GroupNextStep nextStep = ( ( INextCheck ) mCurrentPagerView )
						.getGroupNextStep( );
				goNext( nextStep );
			}
			/*
			 * if(mCurrentStep < mStepTitles.length){
			 * mViewPager.setCurrentItem(mCurrentStep+1); }
			 */
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
		dismissDialog() ;
		super.onDestroy( );
	}

	@Override
	public boolean onKeyDown( int keyCode , KeyEvent event )
	{
		if ( keyCode == KeyEvent.KEYCODE_BACK  && !isSubmit)
		{
			finish( );
			return true;
		}
		return super.onKeyDown( keyCode , event );
	}

	@Override
	public void onGeneralSuccess( final String result , final long flag )
	{
		if ( mCurrentPagerView != null )
		{
			runOnUiThread( new Runnable( )
			{

				@Override
				public void run( )
				{
					mCurrentPagerView.onGeneralSuccess( result , flag );
					if ( flag == CREATE_GROUP_FLAG )
					{
						isSubmit = false;
						mWaitDialog.setCancelable(true);
						mWaitDialog.setCanceledOnTouchOutside(true);
						showWaitDialog( false );
						BaseServerBean bean = GsonUtil.getInstance( )
								.getServerBean( result , BaseServerBean.class );
						if ( bean != null )
						{
							if ( bean.isSuccess( ) )
							{
								// 圈子创建完成，提交成功
								CommonFunction.toastMsg( mContext ,
										R.string.new_group_has_submit );
								setResult( RESULT_OK );
								finish( );
							}
							else
							{
								onGeneralError( bean.error , flag );
							}
						}
						else
						{
							onGeneralError( 107 , flag );
						}
					}
				}
			} );
		}
	}

	public void onGeneralError( final int e , final long flag )
	{
		if ( mCurrentPagerView != null )
		{
			runOnUiThread( new Runnable( )
			{

				@Override
				public void run( )
				{
					mCurrentPagerView.onGeneralError( e , flag );
					if ( flag == CREATE_GROUP_FLAG )
					{
						isSubmit = false;
						mWaitDialog.setCancelable(true);
						mWaitDialog.setCanceledOnTouchOutside(true);
						showWaitDialog( false );
						ErrorCode.toastError( mContext , e );
					}
				}
			} );
		}
	}

    @Override
	protected void onActivityResult( int requestCode , int resultCode , Intent data )
	{
		super.onActivityResult( requestCode , resultCode , data );
		if ( mCurrentPagerView instanceof CreateGroupEditInfo)
		{
			mCurrentPagerView.onActivityResult( requestCode ,
					resultCode , data );
		}
		else if(mCurrentPagerView instanceof CreateGroupJoinCondition)
		{
			CommonFunction.log( "shifengxiong" ,"mCurrentPagerView instanceof CreateGroupJoinCondition ============getUserGold");
			( ( CreateGroupJoinCondition ) mCurrentPagerView ).getUserGold( );
		}
	}



	/**
	 * @ClassName: GroupPagerAdapter
	 * @Description: ViewPager的适配器
	 * @author zhonglong kylin17@foxmail.com
	 * @date 2013-12-7 下午3:24:47
	 *
	 */
	class GroupPagerAdapter extends PagerAdapter
	{

		private SuperView mCurrentView;

		@Override
		public int getCount( )
		{
			return mStepTitles.length;
		}

		@Override
		public boolean isViewFromObject( View view , Object obj )
		{
			return view.equals( obj );
		}

		@Override
		public Object instantiateItem( ViewGroup container , int position )
		{
			SuperView superView = null;
			switch ( position )
			{
//				case 0 :
//					superView = new CreateGroupSelectType( mActivity ,
//							CreateGroupActivity.this );
//					break;
				case 0 :
					superView = new CreateGroupEditInfo( mActivity , CreateGroupActivity.this );
					break;
				case 1 :
					superView = new CreateGroupSelectCenter( mActivity ,
							CreateGroupActivity.this , mGeoData );
					break;
				case 2 :
					/*superView = new CreateGroupJoinCondition( mActivity ,
							CreateGroupActivity.this );*/
					superView = new CreateGroupSubmitView(mActivity,
							CreateGroupActivity.this);
					break;
			}

			if ( superView != null )
			{

				superView.setTag( "pager_" + position );
				container.addView( superView );
				if ( isFirstInit )
				{
					initDataAtPager( mViewPager.getCurrentItem( ) , false );
					isFirstInit = false;
				}
				return superView;
			}
			else
			{
				return new View( mContext );
			}
		}

		@Override
		public void destroyItem( ViewGroup container , int position , Object object )
		{
			container.removeView( ( View ) object );
		}

		@Override
		public void setPrimaryItem( ViewGroup container , int position , Object object )
		{
			mCurrentView = ( SuperView ) object;
		}

	}

	/*****************************************
	 *
	 * ICreateGroupParentCallback接口实现
	 *
	 *****************************************/

	private void dismissDialog()
	{
		if ( mWaitDialog != null )
		{
			mWaitDialog.dismiss( );
		}
	}

	@Override
	public void showWaitDialog( boolean isShow )
	{
		if ( isShow )
		{
			if(mWaitDialog==null)
			{
				mWaitDialog = DialogUtil.getProgressDialog( this ,
						getString( R.string.dialog_title ) , getString( R.string.please_wait ) ,
						null );
			}
			mWaitDialog.show( );
		}
		else
		{
			if ( mWaitDialog != null )
			{

				if(mWaitDialog.isShowing( ))
				{
					mWaitDialog.hide( );

				}
			}

		}
	}

	@Override
	public CreateGroupInfo getGroupInfo( )
	{
		return this.mGroupInfo;
	}

	@Override
	public void goNext( GroupNextStep step )
	{
		switch ( mViewPager.getCurrentItem( ) )
		{
//			case 0 :
//				// 选择类型
//				if ( isCreateGroup )
//				{
//					if ( step.nextMsg.equals( "" ) && step.nextParams.length > 0 )
//					{
//						mGroupInfo.groupTypeId = step.nextParams[ 0 ];
//						mGroupInfo.groupTypeName = step.nextParams[ 1 ];
//						mGroupInfo.groupTypeIcon = step.nextParams[ 2 ];
//						if ( mCurrentStep < mStepTitles.length )
//						{
//							mViewPager.setCurrentItem( mCurrentStep + 1 );
//						}
//					}
//					else
//					{
//						CommonFunction.toastMsg( mContext , step.nextMsg );
//						CommonFunction.hideInputMethod( mContext , mCurrentPagerView );
//					}
//				}
//				else
//				{
//					// 修改圈类型返回
//					Intent intent = new Intent( );
//					intent.putExtra( "typeId" , step.nextParams[ 0 ] );
//					intent.putExtra( "typeName" , step.nextParams[ 1 ] );
//					intent.putExtra( "typeIcon" , step.nextParams[ 2 ] );
//					setResult( Activity.RESULT_OK , intent );
//					finish( );
//				}
//				break;
			case 0 :
				// 填写资料
				if ( step.nextMsg.equals( "" ) && step.nextParams.length > 0 )
				{
					mGroupInfo.groupIconUrl = step.nextParams[ 0 ];
					mGroupInfo.groupName = step.nextParams[ 1 ];
					mGroupInfo.groupDesc = step.nextParams[ 2 ];
					if ( mCurrentStep < mStepTitles.length )
					{
						mViewPager.setCurrentItem( mCurrentStep + 1 );
						CommonFunction.hideInputMethod( mContext , mCurrentPagerView );
					}
				}
				else
				{
					CommonFunction.toastMsg( mContext , step.nextMsg );
				}
				break;
			case 1 :
				// 选择圈中心
				if ( isCreateGroup )
				{
					if ( TextUtils.isEmpty( step.nextMsg ) && step.nextParams.length > 0 )
					{
						mGroupInfo.groupBuildingId = step.nextParams[ 0 ];
						mGroupInfo.groupBuildingName = step.nextParams[ 1 ];
						mGroupInfo.groupCenterLat = step.nextParams[ 2 ];
						mGroupInfo.groupCenterLng = step.nextParams[ 3 ];
						mGroupInfo.userLat = step.nextParams[ 4 ];
						mGroupInfo.userLng = step.nextParams[ 5 ];
						mGroupInfo.userAddress = step.nextParams[ 6 ];
						if ( mCurrentStep < mStepTitles.length )
						{
							mViewPager.setCurrentItem( mCurrentStep + 1 );
						}
					}
					else
					{
						CommonFunction.toastMsg( mContext , step.nextMsg );
						CommonFunction.hideInputMethod( mContext , mCurrentPagerView );
					}


				}
				else
				{
					// 修改圈中心返回
					Intent intent = new Intent( );
					intent.putExtra( "centerId" , step.nextParams[ 0 ] );
					intent.putExtra( "centerName" , step.nextParams[ 1 ] );
					intent.putExtra( "centerLat" , step.nextParams[ 2 ] );
					intent.putExtra( "centerLng" , step.nextParams[ 3 ] );
					setResult( Activity.RESULT_OK , intent );
					finish( );
				}
				break;
			/*case 3 :
				// 设置加入条件
				if ( TextUtils.isEmpty( step.nextMsg ) && step.nextParams.length > 0 )
				{
					mGroupInfo.groupRang = step.nextParams[ 0 ];
					startCreateGroup( );
				}
				else
				{
					DialogUtil.showTwoButtonDialog( mContext ,
							getString( R.string.dialog_title ) , step.nextMsg ,
							getString( R.string.ok ) , getString( R.string.get_gold_coins ) ,
							new View.OnClickListener( )
							{

								@Override
								public void onClick( View v )
								{

								}
							} , new View.OnClickListener( )
							{

								@Override
								public void onClick( View v )
								{
									Intent intent = new Intent( mContext ,
											PayMainActivity.class );
									intent.putExtra( PayMainActivity.TAB_INDEX ,
											PayMainActivity.TAB_ONLINE_TIME );
									intent.putExtra( PayMainActivity.FROM_INDEX ,
											PayMainActivity.CREATE_GROUP );
									startActivityForResult( intent , PayMainActivity.CREATE_GROUP ) ;

								}
							} );
				}
				break;*/
			case 2 :
				startCreateGroup( );
				break;
			default :
				break;
		}
	}

	/**
	 * @Title: startCreateGroup
	 * @Description: 开始创建圈子
	 */
	private void startCreateGroup( )
	{
		isSubmit = true;
		showWaitDialog( true );
		mWaitDialog.setCancelable(false);
		mWaitDialog.setCanceledOnTouchOutside(false);

		/*CREATE_GROUP_FLAG = GroupHttpProtocol.createGroup( mContext , mGroupInfo.groupName ,
				mGroupInfo.groupIconUrl , mGroupInfo.groupTypeId , mGroupInfo.userLat ,
				mGroupInfo.userLng , mGroupInfo.userAddress , mGroupInfo.groupRang ,
				mGroupInfo.groupDesc , mGroupInfo.groupBuildingId ,
				mGroupInfo.groupBuildingName , mGroupInfo.groupCenterLat ,
				mGroupInfo.groupCenterLng , this );*/
		CREATE_GROUP_FLAG = GroupHttpProtocol.createGroup_5_3( mContext ,
				mGroupInfo.groupType,  mGroupInfo.groupName ,
				mGroupInfo.groupIconUrl, "20" , mGroupInfo.userLat ,
				mGroupInfo.userLng , mGroupInfo.userAddress , mGroupInfo.groupRang ,
				mGroupInfo.groupDesc , mGroupInfo.groupBuildingId ,
				mGroupInfo.groupBuildingName , mGroupInfo.groupCenterLat ,
				mGroupInfo.groupCenterLng , this );
		if ( CREATE_GROUP_FLAG < 0 )
		{
			showWaitDialog( false );
			onGeneralError( 107 , CREATE_GROUP_FLAG );
		}
	}


	public void onBackPressed() {

		super.onBackPressed( );
		showWaitDialog( false );

	}

}
