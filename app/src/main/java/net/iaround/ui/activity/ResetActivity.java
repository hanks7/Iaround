
package net.iaround.ui.activity;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.GtAppDlgTask;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.LoginHttpProtocol;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;


/**
 * Class: 重置密码
 * Author：gh
 * Date: 2016/12/6 15:34
 * Emial：jt_gaohang@163.com
 */
public class ResetActivity extends ActionBarActivity implements OnClickListener,HttpCallBack
{
	
	/** 区域 */
	private TextView mTelArea;
	
	/** 手机号输入框 */
	private EditText mEdtTelphone;
	
	/** 提示信息 */
	private TextView mInfo;
	
	/** 下一步 */
	private Button mBtnNext;

	/** 加载等待框 */
	private Dialog waitDialog;
	
	// 国家代码
	private String mCountryCode;
	// 用户手机号
	private String mTelphone;
	/**请求返回值*/
	private long checkPhoneNumFlag;
	private GtAppDlgTask gtAppDlgTask;//极验弹框
	private String mAuthKey;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_enter_telphone );
		mTelArea = ( TextView ) findViewById( R.id.txt_area );
		mEdtTelphone = ( EditText ) findViewById( R.id.edittext_telphone );
		mInfo = ( TextView ) findViewById( R.id.TextView02 );
		mBtnNext = ( Button ) findViewById( R.id.btn_next );
		waitDialog = DialogUtil.getProgressDialog( mContext , "" , getString( R.string.please_wait ) , null );


		setActionBarTitle(R.string.reset_password);
		mInfo.setText( getString( R.string.please_input_register_or_bind_phone_number ) );

		findViewById(R.id.ly_register_phone_are).setOnClickListener( this );
		findViewById(R.id.fl_left).setOnClickListener(this);
		findViewById(R.id.iv_left).setOnClickListener(this);
		mBtnNext.setOnClickListener( this );
		
		mEdtTelphone.setFilters( new InputFilter[ ]{ new InputFilter.LengthFilter( 11 ) } );
		mCountryCode = "+86";

		mEdtTelphone.addTextChangedListener(textWatcher);

		gtAppDlgTask = GtAppDlgTask.getInstance();
		gtAppDlgTask.setContext(ResetActivity.this);
		gtAppDlgTask.setGeetBackListener(geetBackListener);
	}
	
	@Override
	protected void onDestroy( )
	{
		super.onDestroy( );
		if (gtAppDlgTask != null) {
			gtAppDlgTask.setGeetBackListener(null);
			gtAppDlgTask.onDestory();
		}
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
			case R.id.btn_next :
				String telphone = mEdtTelphone.getText( ).toString( ).trim( );
				Pattern decimalPattern = Pattern.compile( "\\d*" );
				if ( CommonFunction.isEmptyOrNullStr( telphone ) ) {
					CommonFunction.toastMsg(ResetActivity.this, R.string.telphone_cannot_be_null );
					return;
				} else if ( !decimalPattern.matcher( telphone ).matches( ) ) {
					CommonFunction.toastMsg(ResetActivity.this, R.string.telphone_not_correct );
					return;
				}
				// 对大陆手机号做11位判断
				if ( (mCountryCode.trim( ).equals( "+86" ) ) ) {
					boolean isPhoneNum = CommonFunction.isChinaPhoneLegal(telphone);
					if(!isPhoneNum) {
						CommonFunction.toastMsg(ResetActivity.this, R.string.telphone_not_correct);
						return;
					}
				}else if(telphone.trim( ).length( ) <=5){
					CommonFunction.toastMsg(ResetActivity.this, R.string.telphone_not_correct);
					return;
				}

				mTelphone = telphone;
				if (gtAppDlgTask != null) {
					if(gtAppDlgTask.getGeetBackListener()!=geetBackListener){
						gtAppDlgTask.setContext(ResetActivity.this);
						gtAppDlgTask.setGeetBackListener(geetBackListener);
					}
					gtAppDlgTask.show();
				}
				break;
			case R.id.ly_register_phone_are :
				Intent intent = new Intent( mContext , CountrySelectActivity.class );
				startActivityForResult( intent , 1 );
				break;
			case R.id.fl_left:
			case R.id.iv_left:
				setBackEvent();
			default :
				break;
		}
		
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
				String[ ] spitString = area.split( "\\+" );
				mCountryCode = "+" + spitString[ 1 ];
				String countryName = spitString[ 0 ];
				mTelArea.setText( countryName + mCountryCode.trim( ) );
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

	private TextWatcher textWatcher = new TextWatcher(){

		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

		}

		@Override
		public void afterTextChanged(Editable editable) {
			String number = mEdtTelphone.getText().toString();
			if (number.length() > 0){
				mBtnNext.setEnabled(true);
			}else{
				mBtnNext.setEnabled(false);
			}
		}
	};


	@Override
	public void onGeneralSuccess(String result, long flag) {

		if (checkPhoneNumFlag == flag) {
			try {
				JSONObject object = new JSONObject(result);
				String status = object.optString("status");
				if (status.equals("-100"))
				{
					if (object.optInt("error") == 5608) {
						ErrorCode.showError(mContext, result);
					}
				}else if (status.equals("200"))
				{
					Intent phoneIntent = new Intent(mContext, ResetPasswordActivity.class);
					phoneIntent.putExtra("telphone", mTelphone);
					phoneIntent.putExtra("areaCode", mCountryCode);
					phoneIntent.putExtra("authkey", mAuthKey);
					startActivity(phoneIntent);
					//finish();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if (flag <= 0) {
			Toast.makeText(ResetActivity.this, getString(R.string.start_reconnect), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onGeneralError(int e, long flag) {

	}

	/**
	 * 极验回调
	 */
	private GtAppDlgTask.GeetBackListener geetBackListener = new GtAppDlgTask.GeetBackListener(){

		@Override
		public void onSuccess(String authKey) {
			Common.getInstance().setGeetAuthKey(authKey);
			getSMSCode(authKey);
		}

		@Override
		public void onClose() {

		}
	};

	private void getSMSCode(String authKey){
		mAuthKey = authKey;
		checkPhoneNumFlag = LoginHttpProtocol.getMsgCode_662(this,mCountryCode,mTelphone,1,this,"","","",authKey);
	}
}
