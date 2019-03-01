
package net.iaround.ui.space.more;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
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
import net.iaround.tools.CryptoUtil;
import net.iaround.tools.DialogUtil;
import net.iaround.ui.comon.SuperActivity;

import java.util.HashMap;


/**
 * @ClassName: EnterPwdActivity
 * @Description: 绑定手机号/修改手机号需要输入密码的界面（无密码不经过此页面）
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-11-23 上午10:17:01
 * 
 */
public class EnterPwdActivity extends SuperActivity implements OnClickListener
{
	/** 标题栏 */
	private ImageView title_back;
	private TextView title_name;
	private FrameLayout flLeft;
	/** 下一步 */
	Button mBtnNext;
	ImageView deletePassword;
	
	/** 密码输入框 */
	EditText mPwdEditText;
	
	/** 加载等待框 */
	private Dialog waitDialog;
	
	// /** 显示密码 */
	// CheckBox mShowPwd;
	
	/** 验证密码的flag */
	private long FLAG_VERIFY_PWD;
	
	/** 类型（0：绑定手机且需设置密码/1：绑定手机/2：修改手机/3：修改密码） */
	int type;

	/**
	 * 绑定手机的状态
	 */
	private int bindPhoneSatus;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_enter_password );
		Common.getInstance( ).addBindActivity( this );
		type = getIntent( ).getIntExtra( "type" , 0 );
		bindPhoneSatus = getIntent().getIntExtra(Constants.PHONE_AUTHEN_STATUS,-1);
		title_back = (ImageView) findViewById( R.id.iv_left );
		title_name = (TextView) findViewById( R.id.tv_title );
		flLeft = (FrameLayout) findViewById(R.id.fl_left);
		mBtnNext = (Button) findViewById( R.id.btn_next );
		mPwdEditText = (EditText) findViewById( R.id.etPassword );
		mPwdEditText.addTextChangedListener( tw );
		deletePassword = (ImageView) findViewById( R.id.delete_password_btn );
		deletePassword.setOnClickListener( this );
		// mShowPwd = ( CheckBox ) findViewById( R.id.cbShowPsw );

		if (bindPhoneSatus == Constants.PHONE_AUTHEN_SUC)
		{
			// 更换手机号
			title_name.setText( getString( R.string.space_bind_mobile_desc2 ) );
		}else
		{
			// 绑定手机号
			title_name.setText( getString( R.string.bind_telphone ) );
		}
		if ( type == 0 || type == 1 )
		{
			if (bindPhoneSatus == Constants.PHONE_AUTHEN_SUC)
			{
				// 更换手机号
				title_name.setText( getString( R.string.space_bind_mobile_desc2 ) );
			}else
			{
				// 绑定手机号
				title_name.setText( getString( R.string.bind_telphone ) );
			}
		}
		else if ( type == 2 )
		{
			// 更换手机号
			title_name.setText( getString( R.string.space_bind_mobile_desc2 ) );
		}
		else
		{
			// 重置密码
			title_name.setText( getString( R.string.reset_password ) );
		}
		waitDialog = DialogUtil.getProgressDialog( mContext , "" ,
				getString( R.string.please_wait ) , null );
		title_back.setOnClickListener( this );
		mBtnNext.setOnClickListener( this );
		flLeft.setOnClickListener(this);
		// mShowPwd.setOnCheckedChangeListener( this );
	}
	
	@Override
	protected void onDestroy( )
	{
		if ( waitDialog != null )
			waitDialog.dismiss( );
		waitDialog = null;
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
				String userPwd = mPwdEditText.getText( ).toString( );
				if ( CommonFunction.isEmptyOrNullStr( userPwd ) )
				{
					Toast.makeText( mContext , getString( R.string.password_not_null ) ,
							Toast.LENGTH_SHORT ).show( );
					return;
				}
				checkUserPwd( userPwd );
				break;
			case R.id.delete_password_btn :
			{
				mPwdEditText.setText( "" );
			}
				break;
			default :
				break;
		}
	}
	
	// @Override
	// public void onCheckedChanged( CompoundButton buttonView , boolean
	// isChecked )
	// {
	// if ( buttonView.equals( mShowPwd ) )
	// {
	// if ( isChecked )
	// {
	// mShowPwd.setText( "..." );
	// }
	// else
	// {
	// mShowPwd.setText( "ABC" );
	// }
	// int type = isChecked ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
	// : ( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD );
	// mPwdEditText.setInputType( type ); // 判断是否显示密码
	// mPwdEditText.setSelection( mPwdEditText.getText( ).toString( ).length( )
	// );
	// }
	// }
	
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
				if ( flag == FLAG_VERIFY_PWD )
				{
					HashMap< String , Object > res = null;
					try
					{
						res = SpaceModel.getInstance( mActivity ).getRes( result , flag );
					}
					catch ( Throwable e )
					{
						CommonFunction.log( e );
					}
					SpaceModelReqTypes reqType = ( SpaceModelReqTypes ) res.get( "reqType" );
					if ( res != null )
					{
						int statusCode = (Integer) res.get( "status" );
						if ( statusCode == 200 )
						{
							if ( reqType == SpaceModelReqTypes.CHECK_PASSWORD )
							{
								// 验证成功
								// 保存md5密码
								Common.getInstance( )
										.setUserPwd(
												CryptoUtil.md5( mPwdEditText.getText( ).toString( )
														.trim( ) ) );
								
								Intent intent = new Intent( mContext , EnterTelActivity.class );
								intent.putExtra( "type" , type );
								intent.putExtra(Constants.PHONE_AUTHEN_STATUS,bindPhoneSatus);
								mContext.startActivity( intent );
							}
						}
						else if ( statusCode == -100 && res.containsKey( "error" ) )
						{
							// 密码错误
							ErrorCode.toastError( mContext ,
									Integer.valueOf( res.get( "error" ).toString( ) ) );
						}
						else
						{//抱歉！操作失败，请稍后再试
//							toastMsg( R.string.e_1000 );
							ErrorCode.toastError(mContext, ErrorCode.E_1000);
						}
						
					}
				}
			}
		} );
		
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
				SpaceModelReqTypes reqType = SpaceModel.getInstance( mActivity ).getReqType( flag );
				if ( reqType == SpaceModelReqTypes.CHECK_PASSWORD )
				{
					ErrorCode.toastError( mContext , e );
				}
			}
		} );
	}
	
	/**
	 * 处理返回事件
	 */
	private void setBackEvent( )
	{
		CommonFunction.hideInputMethod( mContext , mPwdEditText );
		finish( );
	}
	
	/**
	 * @Title: checkUserPwd
	 * @Description: 验证用户密码
	 */
	private void checkUserPwd( String pwd )
	{
		showWaitDialog( true );
		FLAG_VERIFY_PWD = SpaceModelNew.getInstance( mActivity ).checkUserPwdReq( mActivity ,
				CryptoUtil.md5( pwd ) , EnterPwdActivity.this );
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
				waitDialog.hide( );
			}
		}
	}
	
	TextWatcher tw = new TextWatcher( )
	{
		
		@Override
		public void onTextChanged(CharSequence s , int start , int before , int count )
		{
			if ( mPwdEditText.getText( ).length( ) > 0 )
			{
				findViewById( R.id.delete_password_layout ).setVisibility( View.VISIBLE );
			}
			else
			{
				findViewById( R.id.delete_password_layout ).setVisibility( View.INVISIBLE );
			}
		}
		
		@Override
		public void beforeTextChanged(CharSequence s , int start , int count , int after )
		{
			
		}
		
		@Override
		public void afterTextChanged( Editable s )
		{
			
		}
	};
	
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
}
