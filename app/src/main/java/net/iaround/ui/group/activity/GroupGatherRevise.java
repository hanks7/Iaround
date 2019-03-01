
package net.iaround.ui.group.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.BatchUploadManager;
import net.iaround.connector.BatchUploadManager.BatchUploadCallBack;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.model.im.BaseServerBean;
import net.iaround.model.type.FileUploadType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.EditTextUtil;
import net.iaround.tools.EditTextUtil.OnLimitLengthListener;
import net.iaround.tools.FaceManager.FaceIcon;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.JsonParseUtil;
import net.iaround.tools.PathUtil;
import net.iaround.tools.StringUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.activity.BaseActivity;
import net.iaround.ui.activity.PhotoCropActivity;
import net.iaround.ui.chat.ChatFace;
import net.iaround.ui.comon.NetImageView;
import net.iaround.ui.group.adapter.GroupGatherAdapter;
import net.iaround.ui.group.bean.GatherDetailBean.DetailBean;
import net.iaround.ui.group.bean.GatherListBean.GatherItemBean;
import net.iaround.ui.group.bean.GroupPublishGatherBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.GalleryUtils;
import cn.finalteam.galleryfinal.model.PhotoInfo;


/**
 * @author zhengshaota
 * @version 创建时间：2014-11-27
 * @Description: 修改圈聚会资料
 */
public class GroupGatherRevise extends BaseActivity implements OnClickListener,HttpCallBack
{
	private ImageView ivFace;
	private int marginLeft;// 图片的间距
	private int parentSize;// 图片的父View的大小
	private int imageSize;// 图片的大小
	private LinearLayout llContent;
	private LinearLayout llFirstRow;// 图片布局的第一行
	private TextView tvTitle , tvRight , tvCountLimit;
	private EditText etTextCost , etTextTime , etTextDate;
	private EditText etTextContent , etTextphone , etTextAddress;
	private LinearLayout llSecondRow;// 图片布局的第二行
	private RelativeLayout rlFaceLayout;// 表情显示的布局
	private static int FACE_TAG_NUM = 2;// 单个表情占几个长度
	private int mLength = 140; // 限制长度
	private int inputState = 0;// 初始为0，显示表情为1，显示键盘为2
	protected ChatFace chatFace = null;// 表情按钮对象
	private final int MAX_IMAGE_COUNT = 6;// 最多可以添加多少张图片
	private final int MAX_IMAGE_PER_ROW = 3;// 每行最多有多少张图片
	private final int UPDATE_IMAGE_LAYOUT_FALG = 0x00;
	// 保存每一个照片组件的List
	private ArrayList< View > imageLayoutList = new ArrayList< View >( MAX_IMAGE_COUNT );
	// 保存每一张照片的URL的List
	private ArrayList< String > imageUrlList = new ArrayList< String >( MAX_IMAGE_COUNT );
	protected long IMAGE_TASK_MASK = Long.MAX_VALUE ^ 7;// Image对应Task的Mask
	
	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMinute;
	private Button pickTime , pickDate;
	private static final int SHOW_DATAPICK = 100;
	private static final int DATE_DIALOG_ID = 200;
	private static final int SHOW_TIMEPICK = 300;
	private static final int TIME_DIALOG_ID = 400;
	private DetailBean detailBean;
	private GatherItemBean data;
	private String partyId = "";
	private String groupId = "";
	private Dialog progressDialog;
	private GroupPublishGatherBean bean;
	ArrayList< String > UrlList = new ArrayList< String >( );
	private long SAVE_FLAG;// 修改圈聚会的Flag
	private static final int UPLOADIMAGE_FAIL_FLAG = 1000;// 上传照片失败
	private static final int SAVE_SUCCESS_FLAG = 2000;// 修改圈聚会成功的FLAG
	private boolean isFromDetail = false;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature( Window.FEATURE_NO_TITLE );// 去掉标题栏
		setContentView( R.layout.activity_publish_group_gather );

		int partyid = getIntent( ).getIntExtra( "partyId" , 0 );
		long groupid = getIntent( ).getLongExtra( "groupId" , 0 );
		partyId = String.valueOf( partyid );
		groupId = String.valueOf( groupid );
		isFromDetail = getIntent( ).getBooleanExtra( "isFromDetail" , false );

		Configuration config = getResources( ).getConfiguration( );
		marginLeft = CommonFunction.dipToPx( GroupGatherRevise.this , 6 );
		if ( config.orientation == Configuration.ORIENTATION_LANDSCAPE )
		{
			parentSize = ( CommonFunction.getScreenPixHeight( GroupGatherRevise.this ) - 7 * marginLeft ) / 4;
		}
		else
		{
			parentSize = ( CommonFunction.getScreenPixWidth( GroupGatherRevise.this ) - 7 * marginLeft ) / 4;
		}
		imageSize = parentSize - marginLeft;

		initViews( );
		setListeners( );
		if ( isFromDetail == true )
		{
			detailBean = GroupGatherDetail.getDetailBean( );
			initData( detailBean );
		}
		else
		{
			data = GroupGatherAdapter.getGatherItemBean( );
			initData( data );
		}
		displayPublishBtn( );
	}

	private void initViews( )
	{
		// 初始化日期时间控件
		etTextDate = (EditText) findViewById( R.id.editDate );
		etTextTime = (EditText) findViewById( R.id.editTime );
		pickDate = (Button) findViewById( R.id.pickdate );
		pickTime = (Button) findViewById( R.id.picktime );
		final Calendar c = Calendar.getInstance( );
		mYear = c.get( Calendar.YEAR );
		mMonth = c.get( Calendar.MONTH );
		mDay = c.get( Calendar.DAY_OF_MONTH );
		mHour = c.get( Calendar.HOUR_OF_DAY );
		mMinute = c.get( Calendar.MINUTE );
		setDateTime( );
		setTimeOfDay( );
		
		tvTitle = (TextView) findViewById( R.id.title_name );
		tvTitle.setText( R.string.group_inf_group_gatherings );
		tvRight = (TextView) findViewById( R.id.title_right_text );
		tvRight.setText( R.string.group_publish_gatherings_save );
		
		// 添加图片
		llFirstRow = (LinearLayout) findViewById( R.id.llFirstRow );
		llFirstRow.setLayoutParams( new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT , LinearLayout.LayoutParams.WRAP_CONTENT ) );
		llSecondRow = (LinearLayout) findViewById( R.id.llSecondRow );
		ivFace = (ImageView) findViewById( R.id.ivFace );
		rlFaceLayout = (RelativeLayout) findViewById( R.id.rlFaceLayout );
		llContent = (LinearLayout) findViewById( R.id.llContent );
		tvCountLimit = (TextView) findViewById( R.id.tvCountLimit );
		etTextContent = (EditText) findViewById( R.id.etTextContent );
		etTextphone = (EditText) findViewById( R.id.edit_phone );
		etTextAddress = (EditText) findViewById( R.id.address_content );
		etTextCost = (EditText) findViewById( R.id.edit_pay );

		int count = StringUtil.getLengthCN1( tvCountLimit.getText( ).toString( ) );
		updateFontNum( count );

		// 限制聚会内容字数输入
		EditTextUtil.autoLimitLength( etTextContent , mLength , new OnLimitLengthListener( )
		{
			@Override
			public void limit( long limitCount , long overCount )
			{
				updateFontNum( ( int ) overCount );
			}
		} );


		// 限制聚会内容字数输入
		EditTextUtil.autoLimitLength( etTextContent , 140 );
		// 限制手机号码字数输入
		EditTextUtil.autoLimitLength( etTextphone , 20 );
		// 限制费用字数输入
		EditTextUtil.autoLimitLength( etTextCost , 20 );
		// 限制聚会地点字数输入
		EditTextUtil.autoLimitLength( etTextAddress , 50 );

		initImageLayout( );
		initEditTextStatus( );
		displayPublishBtn( );
	}

	/**
	 * @Title: setListeners
	 * @Description: 初始化监听器
	 */
	private void setListeners( )
	{
		tvRight.setOnClickListener( this );
		pickDate.setOnClickListener( this );
		pickTime.setOnClickListener( this );
		ivFace.setOnClickListener( this );
		etTextAddress.setOnTouchListener( hideFaceMenuListener );
		etTextphone.setOnTouchListener( hideFaceMenuListener );
		etTextCost.setOnTouchListener( hideFaceMenuListener );
		findViewById( R.id.title_back ).setOnClickListener( this );
		etTextContent.setOnClickListener( new OnClickListener( )
		{
			@Override
			public void onClick( View v )
			{
				// mHandler.postDelayed(new Runnable() {
				//
				// @Override
				// public void run() {
				// //将ScrollView滚动到最上
				// svContent.fullScroll(View.FOCUS_UP);
				// }
				// }, 100);

				if ( chatFace != null && chatFace.isShown( ) )
				{
					hideFaceMenu( );
					etTextContent.requestFocus( );
					inputState = 0;
				}
			}
		} );
	}

	private OnTouchListener hideFaceMenuListener = new OnTouchListener( )
	{
		@Override
		public boolean onTouch(View v , MotionEvent event )
		{
			if ( chatFace != null && chatFace.isShown( ) )
			{
				hideFaceMenu( );
				inputState = 2;
			}
			return false;
		}
	};


	/**
	 * 跳转到修改聚会页面
	 *
	 * @param context
	 * @param groupId
	 * @param partyid
	 */
	public static void skipToGatherRevise(Context context , long groupId , int partyid ,
                                          int requestCode , boolean isFromDetail )
	{
		Intent intent = new Intent( context , GroupGatherRevise.class );
		intent.putExtra( "groupId" , groupId );
		intent.putExtra( "partyId" , partyid );
		intent.putExtra( "isFromDetail" , isFromDetail );
		( (Activity) context ).startActivityForResult( intent , requestCode );
	}

	/**
	 * 跳转到修改聚会页面
	 *
	 * @param context
	 * @param groupId
	 * @param partyid
	 * @param isFromDetail
	 */
	public static void skipToGatherRevise(Context context , long groupId , int partyid ,
                                          boolean isFromDetail )
	{
		Intent intent = new Intent( context , GroupGatherRevise.class );
		intent.putExtra( "groupId" , groupId );
		intent.putExtra( "partyId" , partyid );
		intent.putExtra( "isFromDetail" , isFromDetail );
		context.startActivity( intent );
	}

	/**
	 * 初始化修改前的列表中聚会的数据
	 */
	private void initData( GatherItemBean data )
	{
		if ( data != null )
		{
			etTextContent.setText( data.party.content );
			etTextphone.setText( data.party.phone );
			etTextAddress.setText( data.party.address );
			etTextCost.setText( data.party.cost );
			// 赋值给日期时间控件
			String format_time = null;
			SimpleDateFormat sdf = new SimpleDateFormat( );
			sdf.applyPattern( "yyyy-MM-dd HH:mm" );
			format_time = sdf.format( data.party.jointime );
			String dataStr = format_time.substring( 0 , format_time.length( ) - 5 );
			String timeStr = format_time.substring( format_time.length( ) - 5 ,
					format_time.length( ) );
			etTextDate.setText( dataStr );
			etTextTime.setText( timeStr );
			// 初始化照片~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			if ( data.party.photos.size( ) != 0 )
			{
				UrlList.clear( );
				UrlList.addAll( data.party.photos );
				for ( String url : UrlList )
				{
					imageUrlList.add( url );
				}
			}

			initImageLayout( );
		}
	}

	/**
	 * 初始化修改前的聚会详情数据
	 */
	private void initData( DetailBean data )
	{
		if ( data != null )
		{
			etTextContent.setText( data.party.content );
			etTextphone.setText( data.party.phone );
			etTextAddress.setText( data.party.address );
			etTextCost.setText( data.party.cost );
			// 赋值给日期时间控件
			String format_time = null;
			SimpleDateFormat sdf = new SimpleDateFormat( );
			sdf.applyPattern( "yyyy-MM-dd HH:mm" );
			format_time = sdf.format( data.party.jointime );
			String dataStr = format_time.substring( 0 , format_time.length( ) - 5 );
			String timeStr = format_time.substring( format_time.length( ) - 5 ,
					format_time.length( ) );
			etTextDate.setText( dataStr );
			etTextTime.setText( timeStr );
			// 初始化照片~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			if ( data.party.photos.size( ) != 0 )
			{
//				UrlList = data.party.photos;
				UrlList.clear( );
				UrlList.addAll( data.party.photos );
				for ( String url : UrlList )
				{
					imageUrlList.add( url );
				}
			}

			initImageLayout( );
		}
	}

	@Override
	public void onClick( View v )
	{
		switch ( v.getId( ) )
		{
			case R.id.title_back :

				finish( );
				break;

			case R.id.pickdate :

				Message msg = mHandler.obtainMessage( );
				if ( pickDate.equals(v) )
				{
					msg.what = SHOW_DATAPICK;
				}
				mHandler.sendMessage( msg );
				break;

			case R.id.picktime :

				Message msg1 = mHandler.obtainMessage( );
				if ( pickTime.equals(v) )
				{
					msg1.what = SHOW_TIMEPICK;
				}
				mHandler.sendMessage( msg1 );
				break;

			case R.id.title_right_text :

				if ( isFromDetail == true )
				{
					GroupGatherDetail.isInDetailReviseGather = true;
				}
				saveData( );// 修改完圈聚会，保存数据发布
				break;
			case R.id.ivFace :

				etTextContent.requestFocus( );
				if ( etTextContent.hasFocus( ) == true )
				{
					if ( !isFaceMenuShow( ) )
					{
						CommonFunction.hideInputMethod( GroupGatherRevise.this , etTextContent );
						if ( inputState == 0 || inputState == 2 )
						{
							new Handler( ).postDelayed(new Runnable( )
							{
								@Override
								public void run( )
								{
									if ( inputState == 0 || inputState == 2 )
									{
										showFaceMenu( );
									}
								}
							} , 200 );
						}
					}
					else
					{
						hideFaceShowKeyboard( );
					}
				}
				break;
		}
	}

	/**
	 * 上传照片并发布
	 */
	private void saveData( )
	{
		long jointime = 0;
		String data = etTextDate.getText( ).toString( );
		String time = etTextTime.getText( ).toString( );

		String str = data + " " + time;
		if ( !CommonFunction.isEmptyOrNullStr( str ) )
		{
			jointime = GroupPublishGatherActivity.convertTimeString2Long( str );
		}

		if ( jointime <= TimeFormat.getCurrentTimeMillis( ) )
		{
			CommonFunction.toastMsg( GroupGatherRevise.this ,
					getString( R.string.group_gatherings_Publish_timeError ) );
		}
		else
		{
			DialogUtil.showOKCancelDialog( GroupGatherRevise.this , getString( R.string.dialog_title ) ,
					getString( R.string.group_isRevise_gatherings ) , new OnClickListener( )
					{
						@Override
						public void onClick( View v )
						{
							showProgressDialog( );
							UploadImage( );// 先上传照片
						}
					} , null );

		}
	}

	/**
	 * 获取聚会内容
	 *
	 * @return GroupPublishGatherBean
	 */
	protected GroupPublishGatherBean publishGroupGather( )
	{
		bean = new GroupPublishGatherBean( );
		// 必填项
		String content = etTextContent.getText( ).toString( );
		String address = etTextAddress.getText( ).toString( );
		String Date = etTextDate.getText( ).toString( );
		String time = etTextTime.getText( ).toString( );
		String str = Date + " " + time;

		if ( !CommonFunction.isEmptyOrNullStr( str ) )
		{
			long jointime = GroupPublishGatherActivity.convertTimeString2Long( str );
			bean.setJointime( jointime );
		}
		else
		{
			bean.setJointime( 0 );
		}

		bean.setContent( content );
		bean.setAddress( address );
		bean.setGroupid( groupId );

		// 选填项
		// 费用
		String cost = etTextCost.getText( ).toString( );
		if ( CommonFunction.isEmptyOrNullStr( cost ) )
		{
			bean.setCost( " " );
		}
		else
		{
			bean.setCost( cost );
		}
		// 手机号码
		String phone = etTextphone.getText( ).toString( );
		if ( CommonFunction.isEmptyOrNullStr( phone ) )
		{
			bean.setPhone( " " );
		}
		else
		{
			bean.setPhone( phone );
		}
		if ( imageUrlList.size( ) != 0 )
		{
			bean.setPhotoList( imageUrlList );

		}
		bean.setPartyid( partyId );

		return bean;
	}

	private BatchUploadCallBack callback = new BatchUploadCallBack( )
	{
		@Override
		public void batchUploadSuccess( long taskFlag , ArrayList< String > serverUrlList )
		{
			bean = publishGroupGather( );
			// 组装
			String photoStr = "";
			String str = "";
			if ( serverUrlList.size( ) > 0 )
			{
				for ( int i = 0 ; i < serverUrlList.size( ) ; i++ )
				{
					str += serverUrlList.get( i ) + ",";
				}
				photoStr = str.substring( 0 , str.length( ) - 1 );
			}
			SAVE_FLAG = GroupHttpProtocol.reviseGroupGather( GroupGatherRevise.this , bean.getGroupid( ) ,
					partyId , bean.getContent( ) , photoStr , bean.getJointime( ) ,
					bean.getAddress( ) , bean.getPhone( ) , bean.getCost( ) ,
					GroupGatherRevise.this );
		}

		@Override
		public void batchUploadFail( long taskFlag )
		{
			mHandler.sendEmptyMessage( UPLOADIMAGE_FAIL_FLAG );
		}
	};

	/**
	 * 批量上传图片到服务端，换取图片的URL
	 *
	 * @return
	 * @throws Exception
	 */
	private void UploadImage( )
	{
		new Thread(new Runnable( )
		{
			@Override
			public void run( )
			{
				bean = publishGroupGather( );
				if ( bean.getPhotoList( ).size( ) > 0 )
				{
					int count = 0;
					for ( String str : bean.getPhotoList( ) )
					{
						if ( str.contains( PathUtil.getHTTPPrefix( ) ) )
						{
							count++;
						}
					}
					if ( count == bean.getPhotoList( ).size( ) )
					{
						PublishImageAndText( bean );
					}
					else if ( count == 0 )
					{
						Publish( bean );
					}
					else if ( count < bean.getPhotoList( ).size( ) && count > 0 )
					{
						Publish( bean );
					}

				}
				else
				{
					PublishPlainText( bean );
				}
			}
		} ).start( );
	}

	/**
	 * 在修改其他资料，但没有添加照片的情况下发布
	 *
	 * @param bean
	 */
	private void PublishImageAndText( GroupPublishGatherBean bean )
	{
		// 组装
		String photoStr = "";
		String str = "";
		if ( UrlList.size( ) > 0 )
		{
			for ( int i = 0 ; i < UrlList.size( ) ; i++ )
			{
				str += UrlList.get( i ) + ",";
			}
			photoStr = str.substring( 0 , str.length( ) - 1 );
		}
		SAVE_FLAG = GroupHttpProtocol.reviseGroupGather( GroupGatherRevise.this , bean.getGroupid( ) , partyId ,
				bean.getContent( ) , photoStr , bean.getJointime( ) , bean.getAddress( ) ,
				bean.getPhone( ) , bean.getCost( ) , GroupGatherRevise.this );
	}

	/**
	 * 纯文本发布
	 *
	 * @param bean
	 */
	private void PublishPlainText( GroupPublishGatherBean bean )
	{
		SAVE_FLAG = GroupHttpProtocol.reviseGroupGather( GroupGatherRevise.this , bean.getGroupid( ) , partyId ,
				bean.getContent( ) , "" , bean.getJointime( ) , bean.getAddress( ) ,
				bean.getPhone( ) , bean.getCost( ) , GroupGatherRevise.this );
	}

	/**
	 * 图文发布
	 *
	 * @param bean
	 */
	private void Publish( GroupPublishGatherBean bean )
	{
		BatchUploadManager manager = new BatchUploadManager( GroupGatherRevise.this );
		final long taskFlag = TimeFormat.getCurrentTimeMillis( ) & IMAGE_TASK_MASK;
		try
		{
			manager.uploadImage( taskFlag , bean.getPhotoList( ) ,
					FileUploadType.PIC_GROUP_TOPIC_PUBLISH , callback );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}

	}

	@SuppressLint( "HandlerLeak" )
    Handler mHandler = new Handler( )
	{
		@SuppressWarnings( "deprecation" )
		@Override
		public void handleMessage( Message msg )
		{
			super.handleMessage( msg );
			switch ( msg.what )
			{
				case UPDATE_IMAGE_LAYOUT_FALG :
					hideProgressDialog( );
					initImageLayout( );
					break;

				case SHOW_TIMEPICK :
					showDialog( TIME_DIALOG_ID );
					break;

				case SHOW_DATAPICK :
					showDialog( DATE_DIALOG_ID );
					break;

				case SAVE_SUCCESS_FLAG :
					hideProgressDialog( );
					CommonFunction.toastMsg( GroupGatherRevise.this ,
							getString( R.string.group_gatherings_ReviseSuccess ) );
					backBtnClick( );
					break;
				case UPLOADIMAGE_FAIL_FLAG :
					hideProgressDialog( );
					CommonFunction.toastMsg( GroupGatherRevise.this ,
							getString( R.string.group_gatherings_Publish_Fail ) );
					break;
			}
		}
	};

	// 点击返回按键
	private void backBtnClick( )
	{
		Intent data = new Intent( );
		String reviseInfo = GsonUtil.getInstance( ).getStringFromJsonObject( bean );
		data.putExtra( "reviseInfo" , reviseInfo );
		setResult( GroupGatherActivity.GATHER_REVISE , data );
		finish( );
	}

//	@Override
//	protected void onActivityResult( int requestCode , int resultCode , Intent data )
//	{
//		super.onActivityResult( requestCode , resultCode , data );
//		if ( RESULT_OK == resultCode )
//		{
//			if ( CommonFunction.TAKE_PHOTO_REQ == requestCode )
//			{
//				if ( data != null )
//				{
//					final String path = data.getStringExtra( PictureMultiSelectActivity.FILE_PATH );
//
//					showProgressDialog( );
//					new Thread(new Runnable( )
//					{
//						@Override
//						public void run( )
//						{
//							rotaingImage( path );
//							mHandler.sendEmptyMessage( UPDATE_IMAGE_LAYOUT_FALG );
//						}
//					} ).start( );
//				}
//
//			}
//			else if ( CommonFunction.PICK_PHOTO_REQ == requestCode )
//			{
//				final ArrayList< String > list = data
//						.getStringArrayListExtra( PictureMultiSelectActivity.PATH_LIST );
//
//				showProgressDialog( );
//				new Thread(new Runnable( )
//				{
//					@Override
//					public void run( )
//					{
//						for ( int i = 0 ; i < list.size( ) ; i++ )
//						{
//							rotaingImage( list.get( i ) );
//						}
//						mHandler.sendEmptyMessage( UPDATE_IMAGE_LAYOUT_FALG );
//					}
//				} ).start( );
//			}
//
//		}
//	}

	private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
		@Override
		public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
			if (resultList != null) {
				imageBeautifyFlagList.clear();
				showProgressDialog();
				final List<String> list = new ArrayList<>();
				for (PhotoInfo photoInfo : resultList){
					list.add(photoInfo.getPhotoPath());
				}
				imageBeautifyFlagList.addAll(list);
				showProgressDialog( );
				new Thread(new Runnable( )
				{
					@Override
					public void run( )
					{
						for ( int i = 0 ; i < list.size( ) ; i++ )
						{
							rotaingImage( list.get( i ) );
						}
						mHandler.sendEmptyMessage( UPDATE_IMAGE_LAYOUT_FALG );
					}
				} ).start( );
			}
		}

		@Override
		public void onHanlderFailure(int requestCode, String errorMsg) {
			Toast.makeText(GroupGatherRevise.this, errorMsg, Toast.LENGTH_SHORT).show();
		}
	};

	private boolean isFaceMenuShow( )
	{
        return rlFaceLayout.getHeight() > 0;
	}

	private void showFaceMenu( )
	{

		if ( chatFace == null || rlFaceLayout.getChildAt( 0 ) == null )
		{
			chatFace = createChatFace( );
			rlFaceLayout.addView( chatFace , 0 );
		}
		chatFace.setVisibility( View.VISIBLE );
		rlFaceLayout.invalidate( );
		showScrollViewBottom( );
		inputState = 1;
	}

	private void showScrollViewBottom( )
	{
		new Handler( ).postDelayed(new Runnable( )
		{

			@Override
			public void run( )
			{
				// svContent.fullScroll(ScrollView.FOCUS_DOWN);
			}
		} , 300 );
	}

	/**
	 * 构造和初始化表情视图
	 */
	private ChatFace createChatFace( )
	{
		ChatFace cf = new ChatFace( this , ChatFace.TYPE_NOMAL );
		cf.setKeyboardClickListener( new KeyboardClickListener( ) );
		cf.setIconClickListener( new IconClickListener( ) );
		cf.initFace( );

		return cf;
	}

	/**
	 * 表情视图：点击表情图片的事件响应
	 */
	class IconClickListener implements OnItemClickListener
	{

		@Override
		public void onItemClick(AdapterView< ? > parent , View view , int position , long id )
		{
			FaceIcon icon = ( FaceIcon ) view.getTag( );
			if ( "back".equals( icon.key ) )
			{
				KeyEvent keyEventDown = new KeyEvent( KeyEvent.ACTION_DOWN , KeyEvent.KEYCODE_DEL );
				etTextContent.onKeyDown( KeyEvent.KEYCODE_DEL , keyEventDown );
			}
			else if ( StringUtil.getLengthCN1( etTextContent.getText( ).toString( ).trim( ) ) <= ( mLength - FACE_TAG_NUM ) )
			{
				// 设置表情
				CommonFunction.setFace( GroupGatherRevise.this , etTextContent , icon.key , icon.iconId ,
						Integer.MAX_VALUE );
			}
		}
	}

	/**
	 * 表情视图：切换到键盘的事件响应
	 *
	 * @author chenlb
	 *
	 */
	class KeyboardClickListener implements OnClickListener
	{
		@Override
		public void onClick( View v )
		{
			hideFaceShowKeyboard( );
		}
	}

	/**
	 * 表情界面隐藏，显示键盘
	 */
	public void hideFaceShowKeyboard( )
	{
		// 菜单处于显示状态，则隐藏菜单
		hideFaceMenu( );
		if ( inputState == 0 || inputState == 1 )
		{
			new Handler( ).postDelayed(new Runnable( )
			{

				@Override
				public void run( )
				{

					if ( inputState == 0 || inputState == 1 )
					{
						CommonFunction.showInputMethodForQuery( GroupGatherRevise.this , etTextContent );
						inputState = 2;
					}
				}
			} , 200 );
		}
		showScrollViewBottom( );
	}

	private void hideFaceMenu( )
	{
		if ( chatFace != null )
		{
			chatFace.setVisibility( View.GONE );
		}
	}

	@Override
	public boolean onKeyDown( int keyCode , KeyEvent event )
	{
		if ( keyCode == KeyEvent.KEYCODE_BACK )
		{
			if ( chatFace != null && chatFace.isShown( ) )
			{
				// 菜单处于显示状态，则隐藏菜单
				hideFaceMenu( );
				inputState = 0;
				return true;
			}
			else
			{
				finish( );
			}
		}
		return super.onKeyDown( keyCode , event );
	}

	// 处理图片旋转的问题
	private void rotaingImage( String path )
	{
		String outputPath = PathUtil.getImageRotatePath( path );
		try
		{
			Bitmap bitmap = BitmapFactory.decodeFile( path );
			int degree = PhotoCropActivity.readPictureDegree( path );
			if ( degree == 0 )
			{
				outputPath = path;
			}
			else
			{
				File outputFile = new File( outputPath );
				if ( !outputFile.exists( ) )
				{
					outputFile.createNewFile( );

					bitmap = PhotoCropActivity.rotaingImageView( degree , bitmap );

					FileOutputStream os = new FileOutputStream( outputFile );
					bitmap.compress( CompressFormat.JPEG , 100 , os );
					os.flush( );
					os.close( );
				}
			}

		}
		catch ( Exception e )
		{
			e.printStackTrace( );
			outputPath = path;
		}

		imageUrlList.add( outputPath );
	}

	// 根据需求展示发布按钮的形态
	private void displayPublishBtn( )
	{
		final String str = etTextContent.getText( ).toString( );
		final String address = etTextAddress.getText( ).toString( );
		runOnUiThread( new Runnable( )
		{
			public void run( )
			{
				if ( TextUtils.isEmpty( str.trim( ) ) | TextUtils.isEmpty( address.trim( ) ) )
				{
					tvRight.setClickable( false );
					tvRight.setOnClickListener( null );
					tvRight.setTextColor( getResources( ).getColor( R.color.c_ccffffff ) );
				}
				else
				{
					tvRight.setClickable( true );
					tvRight.setOnClickListener( GroupGatherRevise.this );
					tvRight.setTextColor( getResources( ).getColor( R.color.white ) );
				}
				tvRight.invalidate( );
			}
		} );
	}

	private void initEditTextStatus( )
	{
		etTextContent.addTextChangedListener( new TextWatcher( )
		{
			@Override
			public void onTextChanged(CharSequence s , int start , int before , int count )
			{
			}

			@Override
			public void beforeTextChanged(CharSequence s , int start , int count , int after )
			{
			}

			@Override
			public void afterTextChanged( Editable s )
			{
				displayPublishBtn( );
				CommonFunction.replaceBlank( s.toString( ) );

			}
		} );

		etTextAddress.addTextChangedListener( new TextWatcher( )
		{

			@Override
			public void onTextChanged(CharSequence s , int start , int before , int count )
			{
			}

			@Override
			public void beforeTextChanged(CharSequence s , int start , int count , int after )
			{
			}

			@Override
			public void afterTextChanged( Editable s )
			{
				displayPublishBtn( );
				CommonFunction.replaceBlank( s.toString( ) );
			}
		} );

	}

	// 初始化照片布局
	private void initImageLayout( )
	{
		llFirstRow.removeAllViews( );
		llSecondRow.removeAllViews( );
		imageLayoutList.clear( );

		LinearLayout.LayoutParams paras = new LinearLayout.LayoutParams( parentSize , parentSize );
		paras.rightMargin = marginLeft;

		RelativeLayout.LayoutParams imageparas = new RelativeLayout.LayoutParams( imageSize ,
				imageSize );
		imageparas.addRule( RelativeLayout.ALIGN_PARENT_BOTTOM , RelativeLayout.TRUE );

		int count = imageUrlList.size( );

		for ( int i = 0 ; i < count ; i++ )
		{
			ViewGroup rootView = i < MAX_IMAGE_PER_ROW ? llFirstRow : llSecondRow;
			View imageItemView = new View( GroupGatherRevise.this );
			imageItemView = View.inflate( GroupGatherRevise.this , R.layout.dynamic_publish_image_item , null );

			imageItemView.setLayoutParams( paras );

			ImageView ivPic = (ImageView) imageItemView.findViewById( R.id.ivImage );
			ivPic.setLayoutParams( imageparas );

			ivPic.setScaleType( ScaleType.CENTER_CROP );
			int defResID = NetImageView.DEFAULT_SMALL;
			String url = imageUrlList.get( i ).contains( PathUtil.getHTTPPrefix( ) ) ? imageUrlList
					.get( i ) : PathUtil.getFILEPrefix( ) + imageUrlList.get( i );

			//gh
//			ImageViewUtil.getDefault( ).loadImage( url , ivPic , defResID , defResID );
			GlideUtil.loadImage(BaseApplication.appContext,url , ivPic );
			ivPic.setTag( i );

			int wrap = LayoutParams.WRAP_CONTENT;
			RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams( wrap , wrap );
			p.addRule( RelativeLayout.ALIGN_PARENT_RIGHT );
			p.addRule( RelativeLayout.ALIGN_PARENT_TOP );

			ImageView ivDelete = (ImageView) imageItemView.findViewById( R.id.ivDelete );
			ivDelete.setTag( i );
			ivDelete.setLayoutParams( p );
			ivDelete.setOnClickListener( new DeleteBtnClickListener( ) );

			imageLayoutList.add( imageItemView );

			rootView.addView( imageItemView );
		}

		int addViewPositon = imageUrlList.size( );
		if ( addViewPositon < MAX_IMAGE_COUNT )
		{
			ViewGroup rootView = addViewPositon < MAX_IMAGE_PER_ROW ? llFirstRow : llSecondRow;
			View imageAddView = new View( GroupGatherRevise.this );
			imageAddView.setBackgroundColor( R.color.white );
			imageAddView = View.inflate( GroupGatherRevise.this , R.layout.dynamic_publish_image_item , null );

			imageAddView.setLayoutParams( paras );
			ImageView ivAdd = (ImageView) imageAddView.findViewById( R.id.ivImage );
			ivAdd.setLayoutParams( imageparas );

			ivAdd.setScaleType( ScaleType.CENTER );
			ivAdd.setBackgroundResource( R.drawable.dynamic_game_image_button_shape );
			ivAdd.setImageResource( R.drawable.dynamic_image_add );
			ivAdd.setOnClickListener( new AddBtnClickListener( ) );

			ImageView ivDelete = (ImageView) imageAddView.findViewById( R.id.ivDelete );
			ivDelete.setVisibility( View.GONE );

			rootView.addView( imageAddView );
		}

	}

	class DeleteBtnClickListener implements View.OnClickListener
	{

		@Override
		public void onClick( View arg0 )
		{
			int positon = (Integer) arg0.getTag( );
			String url = imageUrlList.get( positon );
			if ( !TextUtils.isEmpty( url ) )
			{
				// 移除图片view + url
				imageUrlList.remove( positon );
				imageUrlList.trimToSize( );
				if ( UrlList.contains( url ) )
				{
					UrlList.remove( url );
				}
				initImageLayout( );

			}
		}
	}

	private void updateFontNum( int num )
	{
		tvCountLimit.setText( Html.fromHtml( "<font color='black'>" + num + "</font>" + "/"
				+ mLength ) );
	}

	class AddBtnClickListener implements View.OnClickListener
	{
		@Override
		public void onClick( View arg0 )
		{
			// 跳转到选择图片的界面
			showPicPickMenu( );
		}
	}
	private final int REQUEST_CODE_GALLERY = 1001;
	// 缓存一下选中的图片
	private ArrayList<String> imageBeautifyFlagList = new ArrayList<String>();
	// 显示照片选取的菜单
	private void showPicPickMenu( )
	{
		CommonFunction.hideInputMethod( GroupGatherRevise.this , etTextContent );
		int count = MAX_IMAGE_COUNT - imageUrlList.size( );
//		PictureMultiSelectActivity.skipToPictureMultiSelectAlbum( GroupGatherRevise.this ,
//				CommonFunction.PICK_PHOTO_REQ , count );
		GalleryUtils.getInstance().openGalleryMuti(GroupGatherRevise.this,REQUEST_CODE_GALLERY,count,imageBeautifyFlagList,mOnHanlderResultCallback);
	}
	
	/**
	 * 设置日期
	 */
	private void setDateTime( )
	{
		final Calendar c = Calendar.getInstance( );
		
		mYear = c.get( Calendar.YEAR );
		mMonth = c.get( Calendar.MONTH );
		mDay = c.get( Calendar.DAY_OF_MONTH );
		
		updateDateDisplay( );
	}
	
	/**
	 * 更新日期显示
	 */
	private void updateDateDisplay( )
	{
		etTextDate.setText( new StringBuilder( ).append( mYear ).append( "-" )
				.append( ( mMonth + 1 ) < 10 ? "0" + ( mMonth + 1 ) : ( mMonth + 1 ) ).append( "-" )
				.append( ( mDay < 10 ) ? "0" + mDay : mDay ) );
	}
	
	/**
	 * 日期控件的事件
	 */
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener( )
	{
		
		public void onDateSet(DatePicker view , int year , int monthOfYear , int dayOfMonth )
		{
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			
			updateDateDisplay( );
		}
	};
	
	/**
	 * 设置时间
	 */
	private void setTimeOfDay( )
	{
		final Calendar c = Calendar.getInstance( );
		mHour = c.get( Calendar.HOUR_OF_DAY );
		mMinute = c.get( Calendar.MINUTE );
		updateTimeDisplay( );
	}
	
	/**
	 * 更新时间显示
	 */
	private void updateTimeDisplay( )
	{
		etTextTime.setText( new StringBuilder( ).append( ( mHour < 10 ) ? "0" + mHour : mHour )
				.append( ":" ).append( ( mMinute < 10 ) ? "0" + mMinute : mMinute ) );
	}
	
	/**
	 * 时间控件事件
	 */
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener( )
	{
		
		@Override
		public void onTimeSet(TimePicker view , int hourOfDay , int minute )
		{
			mHour = hourOfDay;
			mMinute = minute;
			
			updateTimeDisplay( );
		}
	};
	
	@Override
	protected Dialog onCreateDialog(int id )
	{
		switch ( id )
		{
			case DATE_DIALOG_ID :
				return new DatePickerDialog( this , mDateSetListener , mYear , mMonth , mDay );
			case TIME_DIALOG_ID :
				return new TimePickerDialog( this , mTimeSetListener , mHour , mMinute , true );
		}
		
		return null;
	}
	
	@Override
	protected void onPrepareDialog( int id , Dialog dialog )
	{
		switch ( id )
		{
			case DATE_DIALOG_ID :
				( (DatePickerDialog) dialog ).updateDate( mYear , mMonth , mDay );
				break;
			case TIME_DIALOG_ID :
				( (TimePickerDialog) dialog ).updateTime( mHour , mMinute );
				break;
		}
	}
	
	// 显示加载框
	private void showProgressDialog( )
	{
		if ( progressDialog == null )
		{
			progressDialog = DialogUtil.showProgressDialog( GroupGatherRevise.this , R.string.dialog_title ,
					R.string.content_is_loading , null );
			progressDialog.setCancelable( false );
		}
		
		progressDialog.show( );
	}
	
	// 隐藏加载框
	private void hideProgressDialog( )
	{
		if ( progressDialog != null )
		{
			progressDialog.hide( );
		}
	}
	
	@Override
	protected void onDestroy( )
	{
		super.onDestroy( );
		if ( progressDialog != null )
		{
			progressDialog.dismiss( );
			progressDialog = null;
		}
	}
	
	@Override
	public void onGeneralError( int e , long flag )
	{
		if ( flag == SAVE_FLAG )
		{
			hideProgressDialog( );
			ErrorCode.toastError( GroupGatherRevise.this , e );
		}
	}
	
	@Override
	public void onGeneralSuccess(String result , long flag )
	{
		if ( flag == SAVE_FLAG )
		{
			BaseServerBean bean = GsonUtil.getInstance( ).getServerBean( result ,
					BaseServerBean.class );
			if ( bean != null && bean.isSuccess( ) )
			{
				Message msg = mHandler.obtainMessage( );
				msg.what = SAVE_SUCCESS_FLAG;
				mHandler.sendMessage( msg );
			}
			else
			{
				hideProgressDialog( );
				JSONObject json = null;
				try
				{
					json = new JSONObject( result );
				}
				catch ( JSONException e )
				{
					e.printStackTrace( );
				}
				int error = JsonParseUtil.getInt( json , "error" , -1 );
				if ( error == 4002 )
				{
					CommonFunction.toastMsg( GroupGatherRevise.this ,
							getString( R.string.group_alreadyoverdue_gatherings ) );
					
				}
				else
				{
					
					ErrorCode.showError( GroupGatherRevise.this , result );
				}
				
				
			}
		}
	}
	
}
