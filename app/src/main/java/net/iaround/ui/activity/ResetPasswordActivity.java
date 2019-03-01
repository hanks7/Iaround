
package net.iaround.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.LoginHttpProtocol;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.CryptoUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.VerCodeTimerTask;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Class: 重置密码
 * Author：gh
 * Date: 2016/12/6 15:34
 * Emial：jt_gaohang@163.com
 */
public class ResetPasswordActivity extends ActionBarActivity implements OnClickListener,HttpCallBack
{
	private long restPasswordFlag;
	/** 验证码输入框 */
	EditText mEdtMsgCode;
	
	/** 密码输入框 */
	EditText mEdtPwd;
	
	/** 绑定按钮 */
	Button mBtnBind;
	
	/** 重发验证码 */
	Button TextView;

	private TextView mBtnResendMsg;
	
	/** 用户的手机号码 */
	private String mTelphoneNum;

	/** 区域号码 */
	private String mAreaCode;

	/** 验证码 */
	private String msgCode;
	/**点击重发验证码的次数*/
	private int count = 1;

	private String mAuthKey;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_check_telphone );
		setActionBarTitle(R.string.reset_password);

		mEdtMsgCode = ( EditText ) findViewById( R.id.edittext_msg_code );
		mEdtPwd = ( EditText ) findViewById( R.id.etPassword );
		mBtnResendMsg = (TextView) findViewById( R.id.btn_resend_code );
		mBtnBind = ( Button ) findViewById( R.id.btn_start_bind );
		
		mBtnResendMsg.setOnClickListener( this );
		mBtnBind.setOnClickListener( this );
		
		mTelphoneNum = getIntent( ).getStringExtra( "telphone" );
		mAreaCode = getIntent( ).getStringExtra( "areaCode" );
		mAuthKey = getIntent( ).getStringExtra( "authkey" );

		mEdtMsgCode.addTextChangedListener(textWatcher);
		mEdtPwd.addTextChangedListener(textWatcher);

//		LoginHttpProtocol.sendPhone(
//				ResetPasswordActivity.this ,mTelphoneNum , httpStringCallback );

//		long flag = LoginHttpProtocol.getMsgCode_662(this,mAreaCode,mTelphoneNum,1,this,"","","");
//		if ( flag <= 0 )
//		{
//			toastMsg( getString(R.string.start_reconnect) );
//		}
		toastMsg( getString( R.string.msg_code_have_send ) + mAreaCode + " " + mTelphoneNum );
		Common.telphoneMsgMap.put( mTelphoneNum , SystemClock.elapsedRealtime( ) );
		VerCodeTimerTask.setTimer(ResetPasswordActivity.this,mBtnResendMsg);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		destroyWaitDialog();
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
		switch ( v.getId( ) )
		{
			case R.id.btn_resend_code :
				count++;
				if (count >= 10)
				{
					Toast.makeText(ResetPasswordActivity.this, "", Toast.LENGTH_SHORT).show();
				}
				VerCodeTimerTask.setTimer(ResetPasswordActivity.this,mBtnResendMsg);
				long flag = LoginHttpProtocol.getMsgCode_662(this,mAreaCode,mTelphoneNum,1,this,"","","", mAuthKey);
				if ( flag <= 0 )
				{
					toastMsg( getString(R.string.start_reconnect) );
				}
				break;
			case R.id.btn_start_bind :
				msgCode = mEdtMsgCode.getText( ).toString( );
				if ( CommonFunction.isEmptyOrNullStr( msgCode ) )
				{
					toastMsg( getString( R.string.msg_code_cannot_be_null ) );
					return;
				}

				// 绑定手机需验证密码
				String strPwd = mEdtPwd.getText( ).toString( );

				if (!CommonFunction.isPassword(strPwd))
				{
					Toast.makeText(ResetPasswordActivity.this, getResString(R.string.register_password_hint), Toast.LENGTH_SHORT).show();
				}
				if ( strPwd == null || strPwd.length( ) <= 0 )
				{ // 密码为空
					Toast.makeText( this , R.string.register_please_enter_password , Toast.LENGTH_SHORT )
							.show( );
					return;
				}

				boolean pswValid = CommonFunction.isPassword( strPwd );
				if ( !pswValid )
				{ // 密码非法
					Toast.makeText( this , R.string.register_password_hint , Toast.LENGTH_SHORT )
							.show( );
					return;
				}
				restPasswordFlag = LoginHttpProtocol.findUserPwd(ResetPasswordActivity.this,mAreaCode,mTelphoneNum,msgCode, CryptoUtil.md5( strPwd ),ResetPasswordActivity.this);
				showWaitDialog();

				break;
			
			default :
				break;
		}
		
	}
	
	/**
	 * 处理返回事件
	 */
	private void setBackEvent( )
	{
		CommonFunction.hideInputMethod( mContext , findViewById( R.id.input_layout_foucus ) );
		finish( );
	}

	/**
	 * @Title: toastMsg
	 * @Description: Toast一个消息
	 * @param msg
	 *            Toast内容
	 */
	private void toastMsg( String msg )
	{
		Toast.makeText( mContext , msg , Toast.LENGTH_SHORT ).show( );
	}

	private TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

		}

		@Override
		public void afterTextChanged(Editable editable) {

			String ver = mEdtMsgCode.getText().toString();
			String password = mEdtPwd.getText().toString();
			if (ver.length() > 0 && password.length() > 0){
				mBtnBind.setEnabled(true);
			}else {
				mBtnBind.setEnabled(false);
			}

		}
	};

	@Override
	public void onGeneralSuccess(String result, long flag) {
		if (restPasswordFlag == flag){
			hideWaitDialog();
			try
			{
				JSONObject json = new JSONObject( result );
				if ( json != null )
				{
					if ( json.optInt( "status" ) == 200 )
					{
						Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
						startActivity(intent);
						//finish();
					} else if ( json.optInt( "error" ) == -100 )
					{
						ErrorCode.showError(mContext, result);
						 if (json.optInt("error") == 5613)
						{
							Toast.makeText(ResetPasswordActivity.this, R.string.user_findpwd_by_phone_hint, Toast.LENGTH_SHORT).show();
						}
					}
				}
			}
			catch ( JSONException e )
			{
				e.printStackTrace( );
			}
		}else{
			BaseServerBean bean = GsonUtil.getInstance( ).getServerBean( result ,
					BaseServerBean.class );
			if(bean.isSuccess())
			{
				// 短信发送成功，记录号码和当前时间
				toastMsg( getString( R.string.msg_code_have_send ) + mAreaCode + " "
						+ mTelphoneNum );
				Common.telphoneMsgMap.put( mTelphoneNum ,
						SystemClock.elapsedRealtime( ) );
			}
			else {
				if(bean.error == 8200||bean.error ==8201)
				{
					//gh 机验设置
//					handler.sendEmptyMessage( MSG_SMS_CODE_ERR );
				}

			}
		}
	}

	@Override
	public void onGeneralError(int e, long flag) {
		ErrorCode.toastError( mContext , e );
	}
}
