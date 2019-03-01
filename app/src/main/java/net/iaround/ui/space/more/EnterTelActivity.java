
package net.iaround.ui.space.more;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Constants;
import net.iaround.conf.ErrorCode;
import net.iaround.model.database.SpaceModel;
import net.iaround.model.database.SpaceModel.SpaceModelReqTypes;
import net.iaround.model.database.SpaceModelNew;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.activity.CountrySelectActivity;
import net.iaround.ui.comon.SuperActivity;

import java.util.HashMap;
import java.util.regex.Pattern;


/**
 * @ClassName: EnterTelActivity
 * @Description: 输入手机号的界面
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-11-12 下午3:39:28
 * 
 */
public class EnterTelActivity extends SuperActivity implements OnClickListener
{
	
	/** 标题栏 */
	private FrameLayout flLeft;
	private ImageView title_back;
	private TextView title_name;
	/** 区域 */
	private TextView mTelArea;
	
	/** 手机号输入框 */
	private EditText mEdtTelphone;
	
	/** 提示信息 */
	private TextView mInfo;
	
	/** 下一步 */
	private Button mBtnNext;
	
	/** 用户的登录方式 */
	private int mLoginType;
	
	/** 类型（0：绑定手机且需设置密码/1：绑定手机/2：修改手机/3：找回密码） */
	private int type;

	private int bindPhoneStatus;
	
	/** 加载等待框 */
	private Dialog waitDialog;
	
	// 国家代码
	private String mCountryCode;
	// 用户手机号
	private String mTelphone;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_enter_telphone );
		Common.getInstance( ).addBindActivity( this );
		type = getIntent( ).getIntExtra( "type" , 0 );
		bindPhoneStatus = getIntent().getIntExtra(Constants.PHONE_AUTHEN_STATUS,-1);
		flLeft = (FrameLayout) findViewById(R.id.fl_left);
		title_back = (ImageView) findViewById( R.id.iv_left );
		title_name = (TextView) findViewById( R.id.tv_title );
		mTelArea = (TextView) findViewById( R.id.txt_area );
		mEdtTelphone = (EditText) findViewById( R.id.edittext_telphone );
		mInfo = (TextView) findViewById( R.id.TextView02 );
		mBtnNext = (Button) findViewById( R.id.btn_next );
		waitDialog = DialogUtil.getProgressDialog( mContext , "" ,
				getString( R.string.please_wait ) , null );
		
		if ( type == 0 )
		{
			if (bindPhoneStatus == Constants.PHONE_AUTHEN_SUC)
			{
				// 修改手机
				title_name.setText( getString( R.string.space_bind_mobile_desc2 ) );
				mInfo.setText( getString( R.string.change_tel_once_per_month ) );
			}else {
				// 绑定手机
				title_name.setText(getString(R.string.bind_telphone));
				mInfo.setText(getString(R.string.enter_tel_statement));
			}
		}
		else if ( type == 1 )
		{
			if (bindPhoneStatus == Constants.PHONE_AUTHEN_SUC)
			{
				// 修改手机
				title_name.setText( getString( R.string.space_bind_mobile_desc2 ) );
				mInfo.setText( getString( R.string.change_tel_once_per_month ) );
			}else {
				// 绑定手机
				title_name.setText(getString(R.string.bind_telphone));
				mInfo.setText(getString(R.string.enter_tel_statement));
			}
		}
		else if ( type == 2 )
		{
			// 修改手机
			title_name.setText( getString( R.string.space_bind_mobile_desc2 ) );
			mInfo.setText( getString( R.string.change_tel_once_per_month ) );
		}
		else
		{
			// 找回密码
			title_name.setText( getString( R.string.reset_password ) );
			mInfo.setText( getString( R.string.please_input_register_or_bind_phone_number ) );
		}
		// registerForContextMenu(mTelArea);
		flLeft.setOnClickListener(this);
		title_back.setOnClickListener( this );
		mTelArea.setOnClickListener( this );
		mBtnNext.setOnClickListener( this );

		mEdtTelphone.addTextChangedListener(tw);
		
		mLoginType = SharedPreferenceUtil.getInstance( mActivity ).getInt(SharedPreferenceUtil.LOGIN_TYPE );
		
		mEdtTelphone.setFilters( new InputFilter[ ]{ new InputFilter.LengthFilter( 11 ) } );
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
			setBackEvent( );
			return true;
		}
		return super.onKeyDown( keyCode , event );
	}
	
	@Override
	public void onClick( View v )
	{
		if ( v.equals( title_back ) || v.equals(flLeft) )
		{
			setBackEvent( );
			return;
		}
		switch ( v.getId( ) )
		{
			case R.id.btn_next :
				String telphone = mEdtTelphone.getText( ).toString( ).trim( );
				Pattern decimalPattern = Pattern.compile( "\\d*" );
				if ( CommonFunction.isEmptyOrNullStr( telphone ) )
				{
					toastMsg( R.string.telphone_cannot_be_null );
					return;
				}
				else if ( !decimalPattern.matcher( telphone ).matches( ) )
				{
					toastMsg( R.string.telphone_not_correct );
					return;
				}
				String areaCodeSel = mTelArea.getText( ).toString( ).trim( );
				String areaCode = "";
				if ( areaCodeSel.contains( "（" ) )
				{
					String splitStr = areaCodeSel.split( "（" )[ 1 ];
					areaCode = splitStr.substring( 0 , splitStr.length( ) - 1 );
				}
				else
				{
					String splitStr = areaCodeSel.split( "\\(" )[ 1 ];
					areaCode = splitStr.substring( 0 , splitStr.length( ) - 1 );
				}
				// 对大陆手机号做11位判断
				if ( areaCode.trim( ).equals( "+86" ) && telphone.trim( ).length( ) != 11 )
				{
					toastMsg( R.string.telphone_not_correct );
					return;
				}

				mCountryCode = areaCode;
				mTelphone = telphone;
				DialogUtil.showTwoButtonDialog( mContext ,
						getString( R.string.comfirm_telphone ) , "(" + areaCode + ") " + telphone , getString( R.string.dialog_change ) ,
						getString( R.string.ok ) , new View.OnClickListener( )
						{
							@Override
							public void onClick( View v )
							{
								// TODO Auto-generated method stub

							}
						} , new View.OnClickListener( ) {
							@Override
							public void onClick( View v )
							{
								if ( Common.telphoneMsgMap.containsKey( mTelphone ) ) {
									
									if ( SystemClock.elapsedRealtime( ) - Common.telphoneMsgMap.get( mTelphone ) < 60 * 1000 ) {
										// 如果包含之前输入过的号码，如果未超过60秒限制，直接跳转到下个界面继续等待
										Intent intent = new Intent( mContext , BindTelphoneActivity.class );
										intent.putExtra( "telphone" , mTelphone );
										intent.putExtra( "areaCode" , mCountryCode );
										intent.putExtra( "type" , type );
										mContext.startActivity( intent );
										return;
									}
								}
								
								showWaitDialog( true );
								int reqType = 3;
								if ( type == 0 || type == 1 ) {
									// 绑定手机号
									reqType = 2;
								} else if ( type == 2 ) {
									// 修改手机
									reqType = 3;
								} else if ( type == 3 ) {
									// 找回密码
									reqType = 1;
								}
								long flag = SpaceModelNew.getInstance( mActivity ).getMsgCodeReq( mActivity , mCountryCode , mTelphone , reqType , EnterTelActivity.this ,"","","" );
								if ( flag <= 0 ) {
									toastMsg( R.string.start_reconnect );
								}
							}
						} );
				
				break;
			case R.id.txt_area :
				// openContextMenu(v);
				Intent intent = new Intent( mContext , CountrySelectActivity.class );
				startActivityForResult( intent , 1 );
				break;
			default :
				break;
		}
		
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
				showWaitDialog( false );
				SpaceModelReqTypes reqType = SpaceModel.getInstance( mActivity ).getReqType(flag );
				if ( reqType == SpaceModelReqTypes.CHECK_PASSWORD )
				{
					ErrorCode.toastError( mContext , e );
				}
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
				showWaitDialog( false );
				try
				{
					HashMap< String , Object > response = SpaceModelNew.getInstance( mActivity ).getRes( result , flag );
					SpaceModelReqTypes reqType = ( SpaceModelReqTypes ) response.get( "reqType" );
					int statusCode = (Integer) response.get( "status" );
					if ( statusCode == 200 )
					{
						if ( reqType == SpaceModelReqTypes.GET_MSG_CODE )
						{
							// 发送了短信，将手机号码加入map队列
							Common.telphoneMsgMap.put( mTelphone , SystemClock.elapsedRealtime( ) );
							Intent intent = new Intent( mContext , BindTelphoneActivity.class );
							intent.putExtra( "telphone" , mTelphone );
							intent.putExtra( "areaCode" , mCountryCode );
							intent.putExtra( "type" , type );
							mContext.startActivity( intent );
							toastMsg( getString( R.string.msg_code_have_send ) + mCountryCode + " " + mTelphone );
						}
					}
					else if ( statusCode == -100 && response.containsKey( "error" ) )
					{
						ErrorCode.toastError( mContext , Integer.valueOf( response.get( "error" ).toString( ) ) );
					}
					else
					{
						toastMsg( R.string.e_1 );
					}
				}
				catch ( Throwable e )
				{
					e.printStackTrace( );
					toastMsg( R.string.e_1 );
				}
			}
		} );
	}
	
	/**
	 * @Title: showWaitDialog
	 * @Description: 显示加载框
	 */
	private void showWaitDialog( boolean isShow )
	{
		if ( waitDialog != null )
		{
			if ( isShow )
			{
				waitDialog.show( );
			}
			else
			{
				waitDialog.dismiss( );
			}
		}
	}
	
	/**
	 * @Title: toastMsg
	 * @Description:
	 * @param msg
	 */
	private void toastMsg( String msg )
	{
		Toast.makeText( mContext , msg , Toast.LENGTH_SHORT ).show( );
	}
	
	/**
	 * @Title: toastMsg
	 * @Description:
	 * @param r_msg
	 */
	private void toastMsg( int r_msg )
	{
		Toast.makeText( mContext , r_msg , Toast.LENGTH_SHORT ).show( );
	}
	
	@Override
	protected void onActivityResult( int requestCode , int resultCode , Intent data )
	{
		if ( resultCode == 1 && data != null )
		{
			String area = data.getStringExtra( "area" );
			if ( !CommonFunction.isEmptyOrNullStr( area ) && area.contains( "+" ) )
			{
				// 清空原来输入的手机号
				mEdtTelphone.setText( "" );
				
				CommonFunction.log( "country" , "get country result : " + area );
				String[ ] spitString = area.split( "\\+" );
				String areaCode = "(+" + spitString[ 1 ] + ")";
				String countryName = spitString[ 0 ];
				mTelArea.setText( countryName + areaCode.trim( ) );
				if ( spitString[ 1 ].equals( "+86" ) )
				{
					// 大陆手机限制11位输入
					mEdtTelphone.setFilters( new InputFilter[ ]{ new InputFilter.LengthFilter( 11 ) } );
				}
				else
				{
					mEdtTelphone.setFilters( new InputFilter[ ]{ new InputFilter.LengthFilter( 50 ) } );
				}
			}
		}
	}
	
	/**
	 * 处理返回事件
	 */
	private void setBackEvent( )
	{
		CommonFunction.hideInputMethod( mContext , mEdtTelphone );
		finish( );
	}

	private TextWatcher tw = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (mEdtTelphone.getText().length() > 0) {
				if (mEdtTelphone.getText().length() > 0) {
					mBtnNext.setEnabled(true);
				}
			} else {
				mBtnNext.setEnabled(false);
			}
		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};
	
}
