
package net.iaround.ui.group.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.ScrollView;
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
import net.iaround.ui.group.bean.GroupInfoBean;
import net.iaround.ui.group.bean.GroupPublishBean;
import net.iaround.ui.group.bean.GroupPublishGatherBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.GalleryUtils;
import cn.finalteam.galleryfinal.model.PhotoInfo;


/**
 * @ClassName: PublishGroupGatherActivity
 * @Description: 5.6发布圈聚会
 * @author zhengshaota
 * @date
 * 
 */
public class GroupPublishGatherActivity extends BaseActivity implements OnClickListener,HttpCallBack

{
	// 图片上传的相关
	private final int MAX_IMAGE_COUNT = 6;// 最多可以添加多少张图片
	private final int MAX_IMAGE_PER_ROW = 3;// 每行最多有多少张图片
	private final int UPDATE_IMAGE_LAYOUT_FALG = 0x00;
	private LinearLayout llFirstRow;// 图片布局的第一行
	private LinearLayout llSecondRow;// 图片布局的第二行
	private int marginLeft;// 图片的间距
	private int parentSize;// 图片的父View的大小
	private int imageSize;// 图片的大小
	private Dialog progressDialog;// 加载栏
	// 保存每一个照片组件的List
	private ArrayList< View > imageLayoutList = new ArrayList< View >( MAX_IMAGE_COUNT );
	// 保存每一张照片的URL的List
	private ArrayList< String > imageUrlList = new ArrayList< String >( MAX_IMAGE_COUNT );
	
	// 日期时间控件相关
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
	private ScrollView svContent;// 整个滑动的布局
	private LinearLayout llContent;
	private TextView tvTitle , tvRight , tvCountLimit;
	private ImageView ivFace;
	private EditText etTextContent , etTextphone , etTextAddress , etTextCost , etTextTime ,
			etTextDate;
	/** 圈子信息 */
	private GroupInfoBean mGroupInfoBean;
	/** 发布新聚会的Flag */
	private long PUBLISH_FLAG;
	/** 成功发布聚会 */
	private static final int PUBLISH_SUCCESS = 500;
	/** 发布聚会失败 */
	private static final int PUBLISH_FAIL = 600;
	/** 上传照片失败 */
	private static final int UPLOADIMAGE_FAIL_FLAG = 700;
	
	private String groupId;
	/** 当前用户在该圈中的角色（0圈主；1管理员；2圈成员；3非圈成员） */
	private int groupRole;
	protected long IMAGE_TASK_MASK = Long.MAX_VALUE ^ 7;// Image对应Task的Mask
	private RelativeLayout rlFaceLayout;// 表情显示的布局
	private static int FACE_TAG_NUM = 2;// 单个表情占几个长度
	private int mLength = 140; // 限制长度
	private int inputState = 0;// 初始为0，显示表情为1，显示键盘为2
	protected ChatFace chatFace = null;// 表情按钮对象
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_NO_TITLE );// 去掉标题栏
		setContentView( R.layout.activity_publish_group_gather );
		
		groupId = getIntent( ).getStringExtra( "group_id" );
		groupRole = getIntent( ).getIntExtra( "group_role" , 2 );
		mGroupInfoBean = GroupInfoActivity.getGroupInfoBean( );
		
		Configuration config = getResources( ).getConfiguration( );
		marginLeft = CommonFunction.dipToPx( GroupPublishGatherActivity.this , 6 );
		if ( config.orientation == Configuration.ORIENTATION_LANDSCAPE )
		{
			parentSize = ( CommonFunction.getScreenPixHeight( GroupPublishGatherActivity.this ) - 7 * marginLeft ) / 4;
		}
		else
		{
			parentSize = ( CommonFunction.getScreenPixWidth( GroupPublishGatherActivity.this ) - 7 * marginLeft ) / 4;
		}
		imageSize = parentSize - marginLeft;
		
		initViews( );
		setListeners( );
		
	}
	
	
	/**
	 * @Title: initViews
	 * @Description: 初始化控件
	 */
	private void initViews( )
	{
		// 初始化日期时间控件
		etTextDate = (EditText) findViewById( R.id.editDate );
		etTextTime = (EditText) findViewById( R.id.editTime );
		pickDate = (Button) findViewById( R.id.pickdate );
		pickTime = (Button) findViewById( R.id.picktime );
		setDateTime( );
		setTimeOfDay( );
		
		tvTitle = (TextView) findViewById( R.id.title_name );
		tvTitle.setText( R.string.group_inf_group_gatherings );
		tvRight = (TextView) findViewById( R.id.title_right_text );
		tvRight.setText( R.string.group_publish_gatherings_save );
		svContent = (ScrollView) findViewById( R.id.svContent );
		// 添加图片
		llFirstRow = (LinearLayout) findViewById( R.id.llFirstRow );
		llFirstRow.setLayoutParams( new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT , LinearLayout.LayoutParams.WRAP_CONTENT ) );
		llSecondRow = (LinearLayout) findViewById( R.id.llSecondRow );
		llContent = (LinearLayout) findViewById( R.id.llContent );
		rlFaceLayout = (RelativeLayout) findViewById( R.id.rlFaceLayout );
		ivFace = (ImageView) findViewById( R.id.ivFace );
		tvCountLimit = (TextView) findViewById( R.id.tvCountLimit );
		etTextContent = (EditText) findViewById( R.id.etTextContent );
		etTextphone = (EditText) findViewById( R.id.edit_phone );
		etTextAddress = (EditText) findViewById( R.id.address_content );
		etTextCost = (EditText) findViewById( R.id.edit_pay );
		tvTitle.setFocusable( true );
		tvTitle.setFocusableInTouchMode( true );
		tvTitle.requestFocus( );

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

		// 限制手机号码字数输入
		EditTextUtil.autoLimitLength( etTextphone , 20 );
		// 限制费用字数输入
		EditTextUtil.autoLimitLength( etTextCost , 20 );
		// 限制聚会地点字数输入
		EditTextUtil.autoLimitLength( etTextAddress , 50 );

		initImageLayout( );
		displayPublishBtn( );
		updateEditTextStatus( );
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
	 * 更新必填项输入框状态
	 */
	private void updateEditTextStatus( )
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
				updateFontNum( StringUtil.getLengthCN1( s.toString( ).trim( ) ) );
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

	private void updateFontNum( int num )
	{
		tvCountLimit.setText( Html.fromHtml( "<font color='black'>" + num + "</font>" + "/"
				+ mLength ) );
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

	@Override
	protected void onResume( )
	{
		super.onResume( );
		displayPublishBtn( );
	}

	@Override
	public void onClick( View v )
	{
		switch ( v.getId( ) )
		{
			case R.id.title_back :
				showIsSaveDialog( );
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
				publish( );// 上传照片并发布
				break;

			case R.id.ivFace :

				etTextContent.requestFocus( );
				if ( etTextContent.hasFocus( ) == true )
				{
					if ( !isFaceMenuShow( ) )
					{
						CommonFunction.hideInputMethod( GroupPublishGatherActivity.this , etTextContent );
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
	 * 填完必填项按返回键，提示是否保存
	 */
	private void showIsSaveDialog( )
	{
		String str = etTextContent.getText( ).toString( );
		String address = etTextAddress.getText( ).toString( );

		if ( !TextUtils.isEmpty( str.trim( ) ) && !TextUtils.isEmpty( address.trim( ) ) )
		{
			DialogUtil.showTowButtonDialog( GroupPublishGatherActivity.this , getString( R.string.dialog_title ) ,
					getString( R.string.group_isPublish_gatherings ) ,
					getString( R.string.main_exitapp ) , getString( R.string.cancel ) ,
					new OnClickListener( )
					{
						@Override
						public void onClick( View v )
						{
							finish( );
						}
					} );
		}
		else
		{
			finish( );
		}
	}

	/**
	 * 上传照片并发布
	 */
	private void publish( )
	{
		if ( CommonFunction.forbidSay( GroupPublishGatherActivity.this ) == true )
		{
			CommonFunction
					.toastMsg( GroupPublishGatherActivity.this , getString( R.string.group_getherings_noTalk_power ) );
		}
		else
		{
			showProgressDialog( );
			UploadImage( );
		}
	}


	/**
	 * 获取发布内容
	 *
	 * @return GroupPublishGatherBean
	 */
	protected GroupPublishGatherBean publishGroupGather( )
	{
		GroupPublishGatherBean bean = new GroupPublishGatherBean( );
		// 必填项
		String content = etTextContent.getText( ).toString( );
		String address = etTextAddress.getText( ).toString( );
		String Date = etTextDate.getText( ).toString( );
		String time = etTextTime.getText( ).toString( );
		String str = Date + " " + time;
		if ( !CommonFunction.isEmptyOrNullStr( str ) )
		{
			long jointime = convertTimeString2Long( str );
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

		return bean;
	}

	private BatchUploadCallBack callback = new BatchUploadCallBack( )
	{
		@Override
		public void batchUploadFail( long taskFlag )
		{
			mHandler.sendEmptyMessage( UPLOADIMAGE_FAIL_FLAG );
		}

		@Override
		public void batchUploadSuccess( long taskFlag , ArrayList< String > serverUrlList )
		{

			GroupPublishGatherBean bean = publishGroupGather( );
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

			PUBLISH_FLAG = GroupHttpProtocol.publishGroupGather( GroupPublishGatherActivity.this , bean.getGroupid( ) ,
					bean.getContent( ) , photoStr , bean.getJointime( ) , bean.getAddress( ) ,
					bean.getPhone( ) , bean.getCost( ) , GroupPublishGatherActivity.this );
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
				GroupPublishGatherBean bean = publishGroupGather( );

				if ( bean.getPhotoList( ).size( ) != 0 )
				{
					BatchUploadManager manager = new BatchUploadManager( GroupPublishGatherActivity.this );
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
				else
				{// 纯文本直接发布

					PUBLISH_FLAG = GroupHttpProtocol.publishGroupGather( GroupPublishGatherActivity.this ,
							bean.getGroupid( ) , bean.getContent( ) , "" , bean.getJointime( ) ,
							bean.getAddress( ) , bean.getPhone( ) , bean.getCost( ) ,
							GroupPublishGatherActivity.this );
				}
			}
		} ).start( );
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

				case PUBLISH_SUCCESS :
					hideProgressDialog( );
					finish( );
					// 进入圈聊页面
					Intent intent = new Intent( GroupPublishGatherActivity.this , GroupChatTopicActivity.class );
					intent.putExtra( "id" , groupId );
					intent.putExtra( "icon" , mGroupInfoBean.icon );
					intent.putExtra( "name" , mGroupInfoBean.name );
					intent.putExtra( "userid" , mGroupInfoBean.user.userid );
					intent.putExtra( "usericon" , mGroupInfoBean.user.icon );
					intent.putExtra( "grouprole" , groupRole );
					intent.putExtra( "has_new_message" , false );
					intent.putExtra( "isChat" , true );
//					startActivity( intent );
					GroupChatTopicActivity.ToGroupChatTopicActivity(GroupPublishGatherActivity.this, intent);
					GroupGatherActivity.isPublishOrDeleteGather = true;
					break;

				case PUBLISH_FAIL :
					hideProgressDialog( );
					CommonFunction.toastMsg( GroupPublishGatherActivity.this ,
							getString( R.string.group_gatherings_Publish_frequently ) );
					break;

				case UPLOADIMAGE_FAIL_FLAG :
					hideProgressDialog( );
					CommonFunction.toastMsg( GroupPublishGatherActivity.this ,
							getString( R.string.group_gatherings_Publish_Fail ) );
					break;

				case SHOW_TIMEPICK :
					showDialog( TIME_DIALOG_ID );
					break;

				case SHOW_DATAPICK :
					showDialog( DATE_DIALOG_ID );
					break;
			}
		}
	};

	@Override
	public void onGeneralSuccess(final String result , long flag )
	{
		if ( flag == PUBLISH_FLAG )
		{
			BaseServerBean bean = GsonUtil.getInstance( ).getServerBean( result ,
					BaseServerBean.class );
			if ( bean != null && bean.isSuccess( ) )
			{
				GroupPublishBean publishBean = GsonUtil.getInstance( ).getServerBean( result ,
						GroupPublishBean.class );
				Message msg = mHandler.obtainMessage( );
				msg.what = PUBLISH_SUCCESS;
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
				if ( error == 6055 )
				{
					Message msg = mHandler.obtainMessage( );
					msg.what = PUBLISH_FAIL;
					mHandler.sendMessage( msg );
				}
				else if ( error == 4002 )
				{
					CommonFunction.toastMsg( GroupPublishGatherActivity.this ,
							getString( R.string.group_gatherings_Publish_timeError ) );
				}else if ( error == 6069 )
				{
					CommonFunction.toastMsg( GroupPublishGatherActivity.this ,
							getString( R.string.group_getherings_noTalk_power ) );
				}
				else
				{
					ErrorCode.showError( GroupPublishGatherActivity.this , result );
				}
			}
		}
	}

	@Override
	public void onGeneralError( int e , long flag )
	{
		if ( flag == PUBLISH_FLAG )
		{
			hideProgressDialog( );
			ErrorCode.toastError( GroupPublishGatherActivity.this , e );
		}
	}

	@Override
	protected void onActivityResult( int requestCode , int resultCode , Intent data )
	{
		super.onActivityResult( requestCode , resultCode , data );
		if ( RESULT_OK == resultCode )
		{
			/*if ( CommonFunction.TAKE_PHOTO_REQ == requestCode )
			{
				if ( data != null )
				{
					final String path = data.getStringExtra( PictureMultiSelectActivity.FILE_PATH );
					showProgressDialog( );
					new Thread(new Runnable( )
					{
						@Override
						public void run( )
						{
							rotaingImage( path );
							mHandler.sendEmptyMessage( UPDATE_IMAGE_LAYOUT_FALG );
						}
					} ).start( );
				}
			}*/
			/*else if ( CommonFunction.PICK_PHOTO_REQ == requestCode )
			{
				final ArrayList< String > list = data
						.getStringArrayListExtra( PictureMultiSelectActivity.PATH_LIST );

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
			}*/
		}
	}

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
			Toast.makeText(GroupPublishGatherActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
		}
	};

	/**
	 * 根据需求展示发布按钮的形态
	 */
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
					tvRight.setOnClickListener( GroupPublishGatherActivity.this );
					tvRight.setTextColor( getResources( ).getColor( R.color.white ) );
				}
				tvRight.invalidate( );
			}
		} );
	}

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
				CommonFunction.setFace( GroupPublishGatherActivity.this , etTextContent , icon.key , icon.iconId ,
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
						CommonFunction.showInputMethodForQuery( GroupPublishGatherActivity.this , etTextContent );
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
			View imageItemView = new View( GroupPublishGatherActivity.this );
			imageItemView = View.inflate( GroupPublishGatherActivity.this , R.layout.dynamic_publish_image_item , null );

			imageItemView.setLayoutParams( paras );

			ImageView ivPic = (ImageView) imageItemView.findViewById( R.id.ivImage );
			ivPic.setLayoutParams( imageparas );

			ivPic.setScaleType( ScaleType.CENTER_CROP );
			int defResID = R.drawable.default_pitcure_small;
			String url = imageUrlList.get( i ).contains( PathUtil.getFILEPrefix( ) ) ? imageUrlList
					.get( i ) : PathUtil.getFILEPrefix( ) + imageUrlList.get( i );

//			ImageViewUtil.getDefault( ).loadImage( url , ivPic , defResID , defResID );
			GlideUtil.loadImage(BaseApplication.appContext,url,ivPic);
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
			View imageAddView = new View( GroupPublishGatherActivity.this );
			imageAddView.setBackgroundColor( R.color.white );
			imageAddView = View.inflate( GroupPublishGatherActivity.this , R.layout.dynamic_publish_image_item , null );

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

				initImageLayout( );

			}
		}
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
	private ArrayList<String> imageBeautifyFlagList = new ArrayList<String>(
			MAX_IMAGE_COUNT);
	// 显示照片选取的菜单
	private void showPicPickMenu( )
	{
		CommonFunction.hideInputMethod( GroupPublishGatherActivity.this , etTextContent );
		int count = MAX_IMAGE_COUNT - imageUrlList.size( );
//		PictureMultiSelectActivity.skipToPictureMultiSelectAlbum(
//				GroupPublishGatherActivity.this , CommonFunction.PICK_PHOTO_REQ , count );
		GalleryUtils.getInstance().openGalleryMuti(this,REQUEST_CODE_GALLERY,count,imageBeautifyFlagList,mOnHanlderResultCallback);
	}
	
	/**
	 * 设置日期
	 */
	private void setDateTime( )
	{
		final Calendar c = Calendar.getInstance( );
		
		mYear = c.get( Calendar.YEAR );
		mMonth = c.get( Calendar.MONTH );
		mDay = c.get( Calendar.DAY_OF_MONTH ) + 1;
		
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
	
	public static long convertTimeString2Long( String time )
	{
		String format = "yyyy-MM-dd HH:mm";
		SimpleDateFormat sdf = new SimpleDateFormat( format );
		Date date = null;
		long second = 0;
		try
		{
			date = sdf.parse( time );
		}
		catch ( ParseException e )
		{
			e.printStackTrace( );
		}
		if ( date != null )
		{
			second = date.getTime( );
		}
		return second;
	}
	
	// 显示加载框
	private void showProgressDialog( )
	{
		if ( progressDialog == null )
		{
			progressDialog = DialogUtil.showProgressDialog( GroupPublishGatherActivity.this , R.string.dialog_title ,
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
	
	
}
