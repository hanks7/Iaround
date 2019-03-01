
package net.iaround.ui.group.activity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Config;
import net.iaround.conf.Constants;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.FileUploadManager;
import net.iaround.connector.FileUploadManager.FileProfix;
import net.iaround.connector.UploadFileCallback;
import net.iaround.connector.protocol.GoldHttpProtocol;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.model.entity.GeoData;
import net.iaround.model.im.BaseServerBean;
import net.iaround.model.type.FileUploadType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.JsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.PicIndex;
import net.iaround.ui.activity.editpage.EditNicknameActivity;
import net.iaround.tools.DialogUtil;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.group.bean.GoldDistanceBean;
import net.iaround.ui.group.bean.GroupInfoBean;
import net.iaround.ui.group.bean.UserGoldBean;
import net.iaround.utils.ImageViewUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.GalleryUtils;
import cn.finalteam.galleryfinal.model.PhotoInfo;


/**
 * @ClassName: GroupEditActivity
 * @Description: 修改圈资料界面
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-12-11 下午7:41:34
 *
 */
public class GroupEditActivity extends SuperActivity implements OnClickListener, UploadFileCallback,LocationUtil.MLocationListener
{
	//REQUESTCODE


	// {$ 控件声明
	private TextView mTitleName;
	private ImageView mTitleBack;
	private ImageView mTitleSave;
	private FrameLayout flLeft;

	private LinearLayout mGroupInfoLayout;
	private RelativeLayout mGroupImageLayout;
	private ImageView mGroupImage;
	private RelativeLayout mGroupNameLayout;
	private TextView mGroupName;
	private RelativeLayout mGroupTypeLayout;
	private TextView mGroupType;
	private RelativeLayout mGroupCenterLayout;
	private TextView mGroupCenter;
	private RelativeLayout mGroupDescLayout;
	private TextView mGroupDesc;
	private TextView mGroupDistance;
	private TextView mNeedCoins;

	/** 加载框 */
	private Dialog mWaitDialog;

	// $}

	/** 圈子信息实体 */
	private GroupInfoBean mGroupInfoBean;
	/**欢迎语*/
	String welcome;
	/** 金币范围对应实体 */
	private GoldDistanceBean mGoldDistanceBean;
	/** 用户的金币实体 */
	private UserGoldBean mUserGoldBean;
	/** 范围数组 */
	private String[ ] mRangArray;
	/** 当前所选范围 */
	private int mRangeItem = 0;
	/** 获取金币范围对应map的flag */
	private long FLAG_GET_RANGE_GOLD_MAP;
	/** 修改圈资料的flag */
	private long FLAG_EDIT_GROUP_INFO;

	// {$ 圈图操作

	/** 上传聊天室图标状态：未上传 */
	private final int UPLOAD_STATUS_NO = 1;
	/** 上传聊天室图标状态：上传中 */
	private final int UPLOAD_STATUS_ING = 2;
	/** 上传聊天室图标状态：上传完毕 */
	private final int UPLOAD_STATUS_FINISHED = 3;
	/** 上传聊天室图标状态：上传失败 */
	private final int UPLOAD_STATUS_FAIL = 4;
	/** 上传聊天室图标的状态 */
	private int uploadRoomIconStatus = UPLOAD_STATUS_NO;

	/**编辑圈子名称*/
	private static final int EDIT_NICKNAME = 11;

	/** 选择聊天室图标 */
	private final int REQUEST_CODE_SEL_ROOM_ICON = 1001;

	/** 选择圈子类型 */
	private final int REQUEST_CODE_SEL_ROOM_TYPE = 1002;

	/** 选择圈中心 */
	private final int REQUEST_CODE_SEL_ROOM_CENTER = 1003;

	/**编辑圈子介绍*/
	public static final int REQUEST_CODE_SEL_ROOM_DESC = 1004;
	/**编辑聊吧欢迎语*/
	public static final int REQUEST_CODE_SEL_ROOM_WEL = 1005;

	/**编辑圈子介绍,传递参数*/
	public static final String EDIT_GROUP_DESC = "edit_groupDesc";
	/**编辑圈子欢迎语,传递参数*/
	public static final String EDIT_GROUP_WEL = "edit_groupWel";
	/** 是否已上传了圈图 */
	private boolean isUploadImg = false;
	/** 已上传成功的圈图地址 */
	private String roomIconUrl = "";

	private long FLAG_UPLOAD_GROUP_IMAGE;

	/** 获取用户金币数的flag */
	private long FLAG_GET_USER_GOLD;

	// $}

	private String mGroupTypeId = "";
	private String mGroupTypeName = "";
	private String mGroupTypeIcon = "";

	private String mCenterId = "";
	private String mCenterName = "";
	private int mCenterLat = 0;
	private int mCenterLng = 0;

	/** 是否使用系统图标 */
	private boolean isUserCategoryIcon = false;

	/** 是否修改了需要审核的资料 */
	private boolean isChangeCheckInfo = false;

	/***切换城市*/
	private String address;//以前地址
	private String myAddress;//当前城市
	/**编辑欢迎语*/
	private RelativeLayout rlEditWelcome;
	private TextView tvEditWelcome;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_group_edit );
		mGroupInfoBean = getIntent( ).getParcelableExtra( "groupInfo" );
		welcome = getIntent().getStringExtra("welcome");
		if ( mGroupInfoBean == null )
		{
			ErrorCode.toastError( mContext , 104 );
			finish( );
		}
		initViews( );
		setListeners( );
		initData( );
	}

	private void initViews( )
	{
		mTitleName = (TextView) findViewById( R.id.tv_title );
		flLeft = (FrameLayout) findViewById(R.id.fl_left);
		mTitleBack = (ImageView) findViewById( R.id.iv_left );
		mTitleSave = (ImageView) findViewById( R.id.iv_right );

		mTitleBack.setVisibility(View.VISIBLE);
		mTitleSave.setVisibility(View.VISIBLE);

		mTitleBack.setImageResource(R.drawable.title_back);
		mTitleName.setText( R.string.edit_group_info );
		mTitleSave.setImageResource(R.drawable.icon_publish);

		isUserCategoryIcon = mGroupInfoBean.systemiconflag == 1;
		mGroupInfoLayout = (LinearLayout) findViewById( R.id.info_layout );
		mGroupImage = (ImageView) findViewById( R.id.group_img );
		ImageViewUtil.getDefault( ).loadRoundFrameImageInConvertView(
				CommonFunction.thumPicture( mGroupInfoBean.icon ) , mGroupImage ,
				net.iaround.ui.comon.PicIndex.DEFAULT_AVATAR_ROUND_LIGHT , net.iaround.ui.comon.PicIndex.DEFAULT_AVATAR_ROUND_LIGHT ,
				null , 0 , "#00000000" );

		//修改圈子名称
		mGroupNameLayout = (RelativeLayout) findViewById(R.id.layout_group_name);
		mGroupName = (TextView) findViewById( R.id.edit_group_name );
		mGroupName.setText( mGroupInfoBean.name );

		//修改圈子类型
		mGroupTypeLayout = (RelativeLayout) findViewById(R.id.layout_group_type);
		mGroupType = (TextView) findViewById( R.id.edit_group_type );
		try
		{
			int langIndex = CommonFunction.getLang( mContext );
			String typeArray[] = mGroupInfoBean.category.split( "\\|" );
			mGroupType.setText( typeArray[ langIndex - 1 ] );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}

		//修改圈子中心
		mGroupCenter = (TextView) findViewById( R.id.edit_group_center );
		mGroupCenter.setText( mGroupInfoBean.landmarkname );
		if ( mGroupInfoBean.grouprole != 0 )
		{
			findViewById( R.id.layout_group_center ).setVisibility( View.GONE );
		}

		if (mGroupInfoBean.grouprole == 0)
		{
			address = mGroupInfoBean.landmarkname;
			GeoData geoData = LocationUtil.getCurrentGeo(this);
//			myAddress = geoData.getCity() + "市";
			myAddress = geoData.getCity();
			/**
			 * 切换城市到当前所在城市
			 */
			if (!address.equals(myAddress)) {
				net.iaround.tools.DialogUtil.showOKDialog(GroupEditActivity.this, getResources().getString(R.string.edit_chatbar_position_hint_title),
						getResString(R.string.edit_chatbar_position_hint_message), new OnClickListener() {
							@Override
							public void onClick(View v) {
								mGroupCenter.setText(myAddress);
							}
						});
			}
		}

		//修改圈子介绍
		mGroupDescLayout = (RelativeLayout) findViewById(R.id.layout_group_desc);
		mGroupDesc = (TextView) findViewById( R.id.edit_group_desc );
		mGroupDesc.setText( mGroupInfoBean.content );

		//修改圈子距离
		mGroupDistance = (TextView) findViewById( R.id.edit_group_distance );
		mGroupDistance.setText( mGroupInfoBean.rang + "km" );
		mNeedCoins = (TextView) findViewById( R.id.edit_group_coins );

		//修改圈子欢迎语
		rlEditWelcome = (RelativeLayout) findViewById(R.id.layout_group_welcome);
		tvEditWelcome = (TextView) findViewById(R.id.edit_group_welcome);
		//欢迎语均可见
		if (welcome != null || !"".equals(welcome))
		{
			tvEditWelcome.setText(welcome);
		}
	}


	private void setListeners( )
	{
		flLeft.setOnClickListener(this);
		mTitleBack.setOnClickListener( this );
		mTitleSave.setOnClickListener( this );

		findViewById( R.id.layout_edit_group_img ).setOnClickListener( this );//圈图标
		findViewById(R.id.layout_group_name).setOnClickListener(this);//圈名称
		findViewById( R.id.layout_group_type ).setOnClickListener( this );//圈分类
		findViewById( R.id.layout_group_distance ).setOnClickListener( this );//暂时没有使用
//		findViewById( R.id.layout_group_center ).setOnClickListener( this );//圈子中心
		findViewById(R.id.layout_group_desc).setOnClickListener(this);//圈子介绍
		rlEditWelcome.setOnClickListener(this);
		tvEditWelcome.setOnClickListener(this);

	}

	/**
	 * @Title: initData
	 * @Description: 加载数据
	 */
	private void initData( )
	{
		showWaitDialog( true );
		FLAG_GET_RANGE_GOLD_MAP = GroupHttpProtocol.groupGoldUsedList( mContext , this );
		if ( FLAG_GET_RANGE_GOLD_MAP < 0 )
		{
			showWaitDialog( false );
			handleDataFail( 107 , FLAG_GET_RANGE_GOLD_MAP, "" );
		}
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
			handleFinish( );
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
				handleDataFail( e , flag, "" );
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

	@Override
	public void onUploadFileProgress( int lengthOfUploaded , long flag )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onUploadFileError(String e , long flag )
	{
		/*if ( FLAG_UPLOAD_GROUP_IMAGE == flag )
		{
			handleUploadGroupIconFail( );
		}*/
		Message msg = new Message( );
		msg.what = REQUEST_CODE_SEL_ROOM_ICON;
		msg.arg1 = 0;
		msg.obj = new Object[]{ flag, e  } ;
		mHandler.sendMessage( msg );
	}

	@Override
	public void onUploadFileFinish( long flag , String result )
	{
		/*if ( FLAG_UPLOAD_GROUP_IMAGE == flag )
		{
			handleUploadGroupIconSuccess( result );
		}*/
		Message msg = new Message( );
		msg.what = REQUEST_CODE_SEL_ROOM_ICON;
		msg.arg1 = 1;
		msg.obj = new Object[]{ flag, result  } ;
		mHandler.sendMessage( msg );
	}

	/**
	 * @Title: handleDataSuccess
	 * @Description: 处理数据成功
	 * @param result
	 * @param flag
	 */
	private void handleDataSuccess(String result , long flag )
	{
		if ( flag == FLAG_GET_RANGE_GOLD_MAP )
		{
			showWaitDialog( false );
			mGoldDistanceBean = GsonUtil.getInstance( ).getServerBean(
					result , GoldDistanceBean.class );
			if ( mGoldDistanceBean != null )
			{
				if ( mGoldDistanceBean.isSuccess( ) )
				{
					mRangArray = new String[ mGoldDistanceBean.goldused.size( ) ];
					for ( int i = 0 ; i < mGoldDistanceBean.goldused.size( ) ; i++ )
					{
						mRangArray[ i ] = mGoldDistanceBean.goldused.get( i ).rang + "";
						if ( mRangArray[ i ].equals( mGroupInfoBean.rang + "" ) )
						{
							mRangeItem = i;
						}
					}
					// rangArray = goldDistanceBean.goldused.toArray(new
					// String[0]);
					initGoldUsedList( mRangeItem );
				}
				else
				{
					handleDataFail( mGoldDistanceBean.error , flag, result );
				}
			}
			else
			{
				handleDataFail( 1 , flag, result );
			}
		}
		else if ( flag == FLAG_EDIT_GROUP_INFO )
		{
			showWaitDialog( false );
			BaseServerBean bean = GsonUtil.getInstance( ).getServerBean( result ,
					BaseServerBean.class );
			if ( bean != null )
			{
				if ( bean.isSuccess( ) )
				{
					mGroupInfoBean.name = this.mGroupName.getText( ).toString( ).trim( );
					mGroupInfoBean.content = this.mGroupDesc.getText( ).toString( ).trim( );
//					mGroupInfoBean.rang = Double.valueOf( mRangArray[ mRangeItem ] );


					if ( mGroupInfoBean.icon == null )
					{
						mGroupInfoBean.icon = "";
					}
					if ( isUserCategoryIcon )
					{
						mGroupInfoBean.icon = !mGroupTypeIcon.equals( "" ) ? mGroupTypeIcon
								: mGroupInfoBean.categoryicon;
					}
					else
					{
						mGroupInfoBean.systemiconflag = 0;
						mGroupInfoBean.icon = !roomIconUrl.equals( "" ) ? roomIconUrl
								: mGroupInfoBean.icon;
					}

					mGroupInfoBean.category = !mGroupTypeName.equals( "" ) ? mGroupTypeName
							: mGroupInfoBean.category;
					mGroupInfoBean.categoryid = Integer
							.valueOf( !mGroupTypeId.equals( "" ) ? mGroupTypeId
									: mGroupInfoBean.categoryid + "" );

					mGroupInfoBean.landmarkid = !mCenterId.equals( "" ) ? mCenterId
							: mGroupInfoBean.landmarkid;
					mGroupInfoBean.landmarkname = !mCenterName.equals( "" ) ? mCenterName
							: mGroupInfoBean.landmarkname;
					mGroupInfoBean.landmarklat = mCenterLat != 0 ? mCenterLat
							: mGroupInfoBean.landmarklat;
					mGroupInfoBean.landmarklng = mCenterLng != 0 ? mCenterLng
							: mGroupInfoBean.landmarklng;
					if ( isChangeCheckInfo )
					{
						CommonFunction.toastMsg( mContext ,
								R.string.chat_modify_room_succ_need_check );
					}
					else
					{
						CommonFunction.toastMsg( mContext , R.string.chat_modify_room_succ );
					}
					Intent intent = new Intent( );
					mGroupInfoBean.landmarkname = mGroupCenter.getText().toString();
					intent.putExtra( "groupInfo" , mGroupInfoBean );
					if (isChangeCheckInfo) {
						intent.putExtra( "hasEdit" , 1 );
					}
					setResult( RESULT_OK , intent );
					finish( );
				}
				else
				{
					handleDataFail( bean.error , flag, result );
				}
			}
			else
			{
				handleDataFail( 104 , flag, result );
			}

		}
		else if ( flag == FLAG_GET_USER_GOLD )
		{
			showWaitDialog( false );
			mUserGoldBean = GsonUtil.getInstance( )
					.getServerBean( result , UserGoldBean.class );
			if ( mUserGoldBean != null )
			{
				if ( mUserGoldBean.isSuccess( ) )
				{
					String range = mRangArray[ mRangeItem ];
					double needGold = mGoldDistanceBean.goldused.get( mRangeItem ).gold;
					String note = String.format( getString( R.string.edit_group_note ) ,
							range , needGold + "" );
					if ( Double.valueOf( range ) <= mGroupInfoBean.oldmaxrange )
					{
						// 直接修改
						uploadGroupInfo( true );
					}
					else
					{
						// 弹出扣费提示
						DialogUtil.showOKCancelDialog( mContext ,
								getString( R.string.dialog_title ) , note ,
								new View.OnClickListener( )
								{

									@Override
									public void onClick( View v )
									{
										uploadGroupInfo( true );
									}
								} );
					}

				}
				else
				{
					onGeneralError( mUserGoldBean.error , flag );
				}
			}
			else
			{
				onGeneralError( 107 , flag );
			}
		}
	}

	/**
	 * @Title: handleDataFail
	 * @Description: 处理数据失败
	 * @param e
	 * @param flag
	 */
	private void handleDataFail( int e , long flag , String result)
	{
		if ( flag == FLAG_GET_RANGE_GOLD_MAP || flag == FLAG_EDIT_GROUP_INFO
				|| flag == FLAG_GET_USER_GOLD )
		{
			showWaitDialog( false );
			if(!TextUtils.isEmpty(result))
			{
				ErrorCode.showError(mContext, result);
			}else
			{
				ErrorCode.toastError( mContext , e );
			}
		}
	}


	private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
		@Override
		public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {

			if (resultList != null) {
				if (reqeustCode == REQUEST_CODE_GALLERY){
					final ArrayList<String> list = new ArrayList<>();

					for (PhotoInfo photoInfo : resultList) {
						list.add(photoInfo.getPhotoPath());
					}
					showWaitDialog( true );
					final String bmPath = list.get(0);

					ImageViewUtil.getDefault( ).loadRoundedImageInConvertView(
							"file://" + bmPath , mGroupImage ,
							PicIndex.DEFAULT_GROUP_SMALL ,
							PicIndex.DEFAULT_GROUP_SMALL , null );
					// 获取需要发送的图片
					try
					{
						Map< String , String > map = new HashMap< String , String >( );
						map.put( "key" , ConnectorManage.getInstance( mContext ).getKey( ) );
						map.put( "type" , String.valueOf( FileUploadType.PIC_GROUP_FACE ) );

						FLAG_UPLOAD_GROUP_IMAGE = System.currentTimeMillis( );

						FileUploadManager
								.createUploadTask( GroupEditActivity.this , bmPath , FileProfix.JPG , Config.sPictureHost , map , GroupEditActivity.this , FLAG_UPLOAD_GROUP_IMAGE )
								.start( );

						uploadRoomIconStatus = UPLOAD_STATUS_ING;
					}
					catch ( Throwable t )
					{
						t.printStackTrace( );
						handleUploadGroupIconFail( );
					}

				}


			}
		}

		@Override
		public void onHanlderFailure(int requestCode, String errorMsg) {
			Toast.makeText(GroupEditActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	protected void onActivityResult( int requestCode , int resultCode , Intent data )
	{
		switch ( requestCode )
		{
			case REQUEST_CODE_SEL_ROOM_ICON ://圈子图标
				// 选择圈图
//				if ( resultCode == SuperActivity.RESULT_OK )
//				{
//					showWaitDialog( true );
//					final ArrayList<String> list = data
//							.getStringArrayListExtra(PictureMultiSelectActivity.PATH_LIST);
//					final String bmPath = list.get(0);
//
//					ImageViewUtil.getDefault( ).loadRoundedImageInConvertView(
//							"file://" + bmPath , mGroupImage ,
//							PicIndex.DEFAULT_GROUP_SMALL ,
//							PicIndex.DEFAULT_GROUP_SMALL , null );
//					// 获取需要发送的图片
//					try
//					{
//						Map< String , String > map = new HashMap< String , String >( );
//						map.put( "key" , ConnectorManage.getInstance( mContext ).getKey( ) );
//						map.put( "type" , String.valueOf( FileUploadType.PIC_GROUP_FACE ) );
//
//						FLAG_UPLOAD_GROUP_IMAGE = System.currentTimeMillis( );
////						getConnectorManage( ).upLoadFile( Config.sPictureHost , fis ,
////								"picture.jpg" , map , FLAG_UPLOAD_GROUP_IMAGE );
//
//						FileUploadManager
//						.createUploadTask( mContext , bmPath , FileProfix.JPG , Config.sPictureHost , map , this , FLAG_UPLOAD_GROUP_IMAGE )
//						.start( );
//
//						uploadRoomIconStatus = UPLOAD_STATUS_ING;
//					}
//					catch ( Throwable t )
//					{
//						t.printStackTrace( );
//						handleUploadGroupIconFail( );
//					}
//				}
//				break;
			case REQUEST_CODE_SEL_ROOM_TYPE ://圈子类型
				if ( resultCode == RESULT_OK )
				{
					mGroupTypeId = data.getStringExtra( "typeId" );
					mGroupTypeName = data.getStringExtra( "typeName" );
					mGroupTypeIcon = data.getStringExtra( "typeIcon" );
					if ( isUserCategoryIcon )
					{
						// 如果使用的是类别作为圈图，则需要更新圈图图标
						ImageViewUtil.getDefault( ).loadRoundedImage(
								CommonFunction.thumPicture( mGroupTypeIcon ) , mGroupImage ,
								R.drawable.default_pitcure_small ,
								R.drawable.default_pitcure_small , null );
					}
					try
					{
						String[ ] typeArray = mGroupTypeName.split( "\\|" );
						int langIndex = CommonFunction.getLanguageIndex( mContext );
						this.mGroupType.setText( typeArray[ langIndex ] );
					}
					catch ( Exception e )
					{
						e.printStackTrace( );
					}

				}
				break;
			case REQUEST_CODE_SEL_ROOM_CENTER ://圈子中心
				if ( resultCode == RESULT_OK )
				{
					mCenterId = data.getStringExtra( "centerId" );
					mCenterName = data.getStringExtra( "centerName" );
					mCenterLat = Integer.valueOf( data.getStringExtra( "centerLat" ) );
					mCenterLng = Integer.valueOf( data.getStringExtra( "centerLng" ) );
					this.mGroupCenter.setText( mCenterName );
				}
				break;
			case EDIT_NICKNAME://编辑圈名称
				if (resultCode == RESULT_OK) {
					String nickname = data.getStringExtra(Constants.EDIT_RETURN_INFO);
					if (!TextUtils.isEmpty(nickname)) {
						mGroupName.setText(nickname);
					}
				}
				break;
			case REQUEST_CODE_SEL_ROOM_DESC://编辑圈介绍
				if (resultCode == RESULT_OK) {
					String groupDesc = data.getStringExtra(GroupEditActivity.EDIT_GROUP_DESC);
					mGroupDesc.setText(groupDesc);
				}
				break;
			case REQUEST_CODE_SEL_ROOM_WEL://编辑聊吧欢迎语
				if (resultCode == RESULT_OK)
				{
					String welcomHint = data.getStringExtra(GroupEditActivity.EDIT_GROUP_WEL);
					tvEditWelcome.setText(welcomHint);
				}
				break;
			default :
				break;
		}
	}

	/**
	 * @Title: handleUploadGroupIconFail
	 * @Description: 处理圈图上传失败
	 */
	private void handleUploadGroupIconFail( )
	{
		showWaitDialog( false );
		uploadRoomIconStatus = UPLOAD_STATUS_FAIL;
		CommonFunction.toastMsg( mContext , R.string.upload_fail );
	}

	/**
	 * @Title: handleUploadGroupIconSuccess
	 * @Description: 圈图上传成功
	 */
	private void handleUploadGroupIconSuccess( String result )
	{
		Map< String , Object > map = null;
		map = JsonUtil.jsonToMap( result );
		roomIconUrl = String.valueOf( map.get( "url" ) );
		if ( CommonFunction.isEmptyOrNullStr( roomIconUrl ) )
		{
			handleUploadGroupIconFail( );
			return;
		}
		showWaitDialog( false );
		uploadRoomIconStatus = UPLOAD_STATUS_FINISHED;
		// 上传了圈图，修改状态
		isUserCategoryIcon = false;

		CommonFunction.toastMsg( mContext , R.string.upload_complete );
	}

	/**
	 * 初始地理范围、金币消耗对照表
	 */
	private void initGoldUsedList( int rangItem )
	{
		if ( mRangArray != null && mRangArray.length > 0 )
		{
			String key = mRangArray[ mRangeItem ];
			mGroupDistance.setText( key + "km" );
			String gold = mGoldDistanceBean.goldused.get( rangItem ).gold + "";
			if ( Double.valueOf( key ) <= mGroupInfoBean.oldmaxrange )
			{
				gold = "0";
			}
			mNeedCoins.setText( gold + "" );
		}
	}
	private final int REQUEST_CODE_GALLERY = 1001;
	@Override
	public void onClick( View v )
	{
		Intent intent = new Intent();
		switch ( v.getId( ) )
		{
			case R.id.fl_left:
			case R.id.iv_left :
				handleFinish( );
				break;
			case R.id.layout_edit_group_img ://
				// 编辑圈图

//				PictureMultiSelectActivity.skipToPictureMultiSelectAlbumCrop(mContext,
//						REQUEST_CODE_SEL_ROOM_ICON);

				requestCamera();
				break;

			case R.id.layout_group_name:
				//编辑圈子名字
				EditNicknameActivity editNicknameActivity = new EditNicknameActivity();
				editNicknameActivity.actionStartForResult(this,mGroupName.getText().toString() ,0, EDIT_NICKNAME,0);
				break;
			case R.id.layout_group_desc:
				//编辑圈子介绍
				intent.setClass(this,EditGroupDesc.class);
				intent.putExtra(EDIT_GROUP_DESC,mGroupDesc.getText().toString());//mGroupInfoBean.getGroupDesc(this)
				intent.putExtra("isFrom",0);
				startActivityForResult(intent,REQUEST_CODE_SEL_ROOM_DESC);
				break;
			case R.id.layout_group_distance :
				// 编辑圈可加入范围
				if ( mGoldDistanceBean != null )
				{
					AlertDialog.Builder builder = new AlertDialog.Builder( mContext );
					ArrayAdapter< String > adapter = new ArrayAdapter< String >( mContext ,
							R.layout.chat_normal_phrase_list_item , mRangArray );
					builder.setSingleChoiceItems( adapter , mRangeItem ,
							new DialogInterface.OnClickListener( )
							{
								public void onClick(DialogInterface dialog , int item )
								{
									mRangeItem = item;
									initGoldUsedList( item );
									dialog.dismiss( );
								}
							} );
					AlertDialog alert = builder.create( );
					alert.show( );
				}
				break;
			case R.id.iv_right :
				// TODO 提交修改信息
				if ( mGoldDistanceBean == null )
				{
					// CommonFunction.toastMsg(mContext,
					// "未能成功获取数据，保存失败，请退出本页重试。");
					ErrorCode.toastError( mContext , 104 );

					return;
				}
				else
				{
					// 判断信息是否修改
					if ( !isEdit( ) )
					{
						CommonFunction.toastMsg( mContext , R.string.has_not_change );
						return;
					}
					if ( ! ( uploadRoomIconStatus == UPLOAD_STATUS_NO || uploadRoomIconStatus == UPLOAD_STATUS_FINISHED ) )
					{
						CommonFunction.toastMsg( mContext ,
								R.string.please_reupload_group_icon );
						return;
					}
					String groupName = this.mGroupName.getText( ).toString( ).trim( );
					String groupSensitiveName = CommonFunction.getSensitiveKeyword( mContext ,
							groupName );
					if ( TextUtils.isEmpty( groupName ) )
					{
						CommonFunction.toastMsg( mContext , R.string.enter_group_name );
						return;
					}
//					else if ( groupName.length( ) < 2 || groupName.length( ) > 15 )
//					{
//						CommonFunction.toastMsg( mContext ,
//								R.string.group_name_length_not_correct );
//						return;
//					}
					else if ( !groupSensitiveName.equals( "" ) )
					{
						// 圈名包含敏感字符
						CommonFunction.toastMsg( mContext ,
								getString( R.string.have_sensitive_keyword ) + ":"
										+ groupSensitiveName );
						return;
					}

					String groupDesc = this.mGroupDesc.getText( ).toString( ).trim( );
					String groupSensitiveDesc = CommonFunction.getSensitiveKeyword( mContext ,
							groupDesc );
					/*
					 * if(TextUtils.isEmpty(groupDesc)){
					 * CommonFunction.toastMsg(mContext,
					 * R.string.enter_group_desc); return; }else
					 * if(groupDesc.length() < 15 || groupDesc.length() > 140){
					 * CommonFunction.toastMsg(mContext,
					 * R.string.group_desc_length_not_correct); return; }
					 */if ( !groupSensitiveDesc.equals( "" ) )
					{
						CommonFunction.toastMsg( mContext ,
								getString( R.string.have_sensitive_keyword ) + ":"
										+ groupSensitiveDesc );
						return;
					}

					//YC
//					String groupRang = mRangArray[ mRangeItem ];
//					double needGold = mGoldDistanceBean.goldused.get( mRangeItem ).gold;
//					if ( Double.valueOf( groupRang ) <= mGroupInfoBean.oldmaxrange )
//					{
//						needGold = 0;
//					}
					// 记录是否修改了需要审核的资料
					isChangeCheckInfo = isEditCheckInfo( );
					uploadGroupInfo( false );
					/*if ( groupRang.equals( mGroupInfoBean.rang + "" ) || needGold == 0 )
					{
						// 未修改圈范围，直接提交修改信息
						uploadGroupInfo( false );
					}
					else
					{
						// 修改了范围，获取用户金币数，并判断用户金币是否足够
						getUserGold( );
					}*/
				}
				break;
			case R.id.layout_group_welcome:
			case R.id.edit_group_welcome://编辑欢迎语
				/**
				 * isFrom 字段
				 * 1 表示编辑欢迎语
				 * 0 表示编辑聊吧介绍
				 */
				intent.setClass(this,EditGroupDesc.class);
				intent.putExtra(EDIT_GROUP_WEL,welcome);
				intent.putExtra("isFrom",1);
				startActivityForResult(intent,REQUEST_CODE_SEL_ROOM_WEL);
			default :
				break;
		}
	}

	/**
	 * @Title: getUserGold
	 * @Description: 获取用户金币数
	 */
	private void getUserGold( )
	{
		showWaitDialog( true );
		FLAG_GET_USER_GOLD = GoldHttpProtocol.userGoldGet( mContext , this );
		if ( CreateGroupActivity.GET_USER_GOLD_FLAG < 0 )
		{
			showWaitDialog( false );
			onGeneralError( 107 , FLAG_GET_USER_GOLD );
		}
	}


	/**
	 * @Title: uploadGroupInfo
	 * @Description: 提交圈资料修改信息
	 * @param isChangeRang
	 *            是否修改了圈范围
	 */
	private void uploadGroupInfo( boolean isChangeRang )
	{
		//圈子范围，不知道干嘛用，老是你妹的报数组越界
//		String groupRang = mRangArray[ mRangeItem ];
//		double needGold = mGoldDistanceBean.goldused.get( mRangeItem ).gold;
//		if ( isChangeRang )
//		{
//			long curGold = mUserGoldBean.goldnum;
//			if ( curGold < needGold )
//			{
//				String infoMsg = getString( R.string.chat_edit_room_no_gold_msg );
//				infoMsg = String.format( infoMsg , groupRang , needGold + "" , curGold + "" );
//				CommonFunction.toastMsg( mContext , infoMsg );
//				return;
//			}
//		}
		String groupIcon = "";
		if ( isUserCategoryIcon )
		{
			// 使用系统图标，则传空
		}
		else
		{
			groupIcon = !roomIconUrl.equals( "" ) ? roomIconUrl : mGroupInfoBean.icon;
		}
		String groupCategoryId = !mGroupTypeId.equals( "" ) ? mGroupTypeId
				: mGroupInfoBean.categoryid + "";
		String centerId = !mCenterId.equals( "" ) ? mCenterId : mGroupInfoBean.landmarkid;
		String centerName = !mCenterName.equals( "" ) ? mCenterName
				: mGroupInfoBean.landmarkname;
		int centerLat = mCenterLat != 0 ? mCenterLat : mGroupInfoBean.landmarklat;
		int centerLng = mCenterLng != 0 ? mCenterLng : mGroupInfoBean.landmarklng;
		String uploadDesc = CommonFunction.filterKeyWordAndReplaceEmoji( mContext ,
				this.mGroupDesc.getText( ).toString( ) );
		String welcomeHint = CommonFunction.filterKeyWordAndReplaceEmoji(mContext,this.tvEditWelcome.getText().toString());
		if ( uploadDesc == null )
		{
			uploadDesc = "";
		}
		showWaitDialog( true );
//		FLAG_EDIT_GROUP_INFO = GroupHttpProtocol.editGroup( mContext , mGroupInfoBean.id ,
//				CommonFunction.filterKeyWordAndReplaceEmoji( mContext , this.mGroupName
//						.getText( ).toString( ) ) , groupIcon , groupCategoryId , null ,
//				uploadDesc , centerId , mGroupCenter.getText().toString() , centerLat + "" , centerLng + "" , this );
		FLAG_EDIT_GROUP_INFO = GroupHttpProtocol.editChatbarInfo( mContext , mGroupInfoBean.id ,
				CommonFunction.filterKeyWordAndReplaceEmoji( mContext , this.mGroupName
						.getText( ).toString( ) ) , groupIcon , groupCategoryId , welcomeHint ,
				uploadDesc , centerId , mGroupCenter.getText().toString() , centerLat + "" , centerLng + "" , this );
		if ( FLAG_EDIT_GROUP_INFO < 0 )
		{
			showWaitDialog( false );
			handleDataFail( 107 , FLAG_EDIT_GROUP_INFO, "" );
		}
	}

	/**
	 * 选择聊天室图标
	 *
	 * @param type
	 *            1: 从相册选择圈子图标 <br/>
	 *            2: 从相机拍照，选择圈子图标
	 */
	private void selGroupIcon( int type )
	{
		if ( UPLOAD_STATUS_ING == uploadRoomIconStatus )
		{
			CommonFunction.showToast( mContext ,
					getResString( R.string.chat_create_room_uploading_icon_tip ) , 1 );
			return;
		}
//		selRoomIcon( type );
	}

	/**
	 * 选择聊天室图标
	 *
	 * @param type
	 *            1: 从相册选择圈子图标 <br/>
	 *            2: 从相机拍照，选择圈子图标
	 */
//	private void selRoomIcon( int type )
//	{
//		switch ( type )
//		{
//			case 1 :
//			{ // 相册
//				Intent i = PictureCaptureHandle.getPickPhotoFromAlbumIntent( mContext ,
//						new int[ ]
//							{ 640 , 640 } , 80 , false , true );
//				startActivityForResult( i , REQUEST_CODE_SEL_ROOM_ICON );
//			}
//				break;
//			case 2 :
//			{ // 相机
//				Intent i = PictureCaptureHandle.getPickPhotoFromCameraIntent( mContext ,
//						new int[ ]
//							{ 640 , 640 } , 100 , false , true );
//				startActivityForResult( i , REQUEST_CODE_SEL_ROOM_ICON );
//			}
//				break;
//		}
//	}

	/**
	 * @Title: showWaitDialog
	 * @Description: 显示加载框
	 * @param isShow
	 */
	private void showWaitDialog( boolean isShow )
	{
		if ( isShow )
		{
			mWaitDialog = DialogUtil.getProgressDialog( mContext , "" ,
					getString( R.string.please_wait ) , new DialogInterface.OnCancelListener( )
					{

						@Override
						public void onCancel( DialogInterface dialog )
						{
							if ( mGoldDistanceBean == null )
							{
								finish( );
							}
						}
					} );
			mWaitDialog.show( );
		}
		else
		{
			mWaitDialog.dismiss( );
		}
	}

	/**
	 * @Title: isEdit
	 * @Description: 是否修改过圈资料
	 * @return
	 */
	private boolean isEdit( )
	{
		String groupName = this.mGroupName.getText( ).toString( );
		String groupDesc = this.mGroupDesc.getText( ).toString( );
		//YC
		String groupWel = tvEditWelcome.getText().toString();
		groupName = CommonFunction.filterKeyWordAndReplaceEmoji( mContext , groupName );
		groupDesc = CommonFunction.filterKeyWordAndReplaceEmoji( mContext , groupDesc );
		groupWel = CommonFunction.filterKeyWordAndReplaceEmoji( mContext , groupWel );
		if ( groupName == null )
		{
			groupName = "";
		}
		if ( groupDesc == null )
		{
			groupDesc = "";
		}
		if (groupWel == null)
		{
			groupWel = "";
		}
		if ( mGroupInfoBean.content == null )
		{
			mGroupInfoBean.content = "";
		}
//		String groupRang = mRangArray[mRangeItem];yc
		String groupCategoryId = !mGroupTypeId.equals( "" ) ? mGroupTypeId
				: mGroupInfoBean.categoryid + "";
		boolean isChangeIcon = false;
		if ( isUserCategoryIcon )
		{
			// 使用系统图标，则只需要判断类别是否更改过
		}
		else
		{
			if ( mGroupInfoBean.icon == null )
			{
				mGroupInfoBean.icon = "";
			}
			String groupIcon = !roomIconUrl.equals( "" ) ? roomIconUrl : mGroupInfoBean.icon;
			isChangeIcon = !groupIcon.equals( mGroupInfoBean.icon );
		}
		//|| !groupRang.equals( mGroupInfoBean.rang + "" ) yc
		String groupCenterId = !mCenterId.equals( "" ) ? mCenterId : mGroupInfoBean.landmarkid;
        return isChangeIcon || !groupName.equals(mGroupInfoBean.name)
                || !groupCategoryId.equals(mGroupInfoBean.categoryid + "")
                || !groupDesc.equals(mGroupInfoBean.content)
                || !groupCenterId.equals(mGroupInfoBean.landmarkid)
                || !groupWel.equals(mGroupInfoBean.welcome);

    }

	/**
	 * @Title: isEditCheckInfo
	 * @Description: 是否修改了审核资料
	 * @return
	 */
	private boolean isEditCheckInfo( )
	{
		String groupName = this.mGroupName.getText( ).toString( );
		String groupDesc = this.mGroupDesc.getText( ).toString( );
		groupName = CommonFunction.filterKeyWordAndReplaceEmoji( mContext , groupName );
		groupDesc = CommonFunction.filterKeyWordAndReplaceEmoji( mContext , groupDesc );
		if ( groupName == null )
		{
			groupName = "";
		}
		if ( groupDesc == null )
		{
			groupDesc = "";
		}
		if ( mGroupInfoBean.content == null )
		{
			mGroupInfoBean.content = "";
		}
		String groupCategoryId = !mGroupTypeId.equals( "" ) ? mGroupTypeId
				: mGroupInfoBean.categoryid + "";
		boolean isChangeIcon = false;
		if ( isUserCategoryIcon )
		{
			// 使用系统图标，则只需要判断类别是否更改过
			if ( !groupCategoryId.equals( mGroupInfoBean.categoryid + "" ) )
			{
				isChangeIcon = true;
			}
		}
		else
		{
			if ( mGroupInfoBean.icon == null )
			{
				mGroupInfoBean.icon = "";
			}
			String groupIcon = !roomIconUrl.equals( "" ) ? roomIconUrl : mGroupInfoBean.icon;
			isChangeIcon = !groupIcon.equals( mGroupInfoBean.icon );
		}
        return isChangeIcon || !groupName.equals(mGroupInfoBean.name)
                || !groupDesc.equals(mGroupInfoBean.content);
    }

	private void handleFinish( )
	{
		if ( isEdit( ) )
		{
			DialogUtil.showOKCancelDialog( mContext , getString( R.string.dialog_title ) ,
					getString( R.string.give_up_change ) , new View.OnClickListener( )
					{

						@Override
						public void onClick( View v )
						{
							CommonFunction.hideInputMethod( mContext , mGroupInfoLayout );
							finish( );
						}
					} );

		}
		else
		{
			CommonFunction.hideInputMethod( mContext , mGroupInfoLayout );
			finish( );
		}
	}

	private Handler mHandler = new Handler( Looper.getMainLooper( ) )
	{
		@Override
		public void handleMessage( Message msg )
		{
			switch ( msg.what ) {
			case REQUEST_CODE_SEL_ROOM_ICON:
				Object[] extras = null;
				long flag = 0;
				if (msg.obj != null) {
					extras = (Object[]) msg.obj;
					flag = (Long)extras[ 0 ];
				}
				if (msg.arg1 == 1) {
					if ( FLAG_UPLOAD_GROUP_IMAGE == flag )
					{
						CommonFunction.log("group", "handleUploadGroupIconSuccess");
						handleUploadGroupIconSuccess( String.valueOf( extras[ 1 ] ) );
					}
				}
				else if ( msg.arg1 == 0) {
					if ( FLAG_UPLOAD_GROUP_IMAGE == flag )
					{
						CommonFunction.log("group", "handleUploadGroupIconFail");
						handleUploadGroupIconFail( );
					}
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void updateLocation(int type, int lat, int lng, String address, String simpleAddress) {

	}

	@Override
	public void doCamera() {
		super.doCamera();
		GalleryUtils.getInstance().openGallerySingleCrop(this,REQUEST_CODE_GALLERY,mOnHanlderResultCallback);
	}
}
