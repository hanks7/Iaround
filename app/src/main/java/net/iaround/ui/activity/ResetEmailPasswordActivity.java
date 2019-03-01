
package net.iaround.ui.activity;


import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;


import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.BaseHttp;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.LoginHttpProtocol;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.ui.adapter.EmailAutoCompleteAdapter;
import net.iaround.ui.view.EmailAutoCompleteTextWatcher;

import org.json.JSONObject;


/**
 * 忘记密码
 * 
 * @author 余勋杰
 */
public class ResetEmailPasswordActivity extends TitleActivity implements OnClickListener , Callback
{
	private static final int FIND_PSW_FIN = 2;

	private AutoCompleteTextView etRegEmail;
//	private Button btnSend;
	private TextView mBtnSend;
	private Handler handler;
	private Dialog pd;
	
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		handler = new Handler( this );
		
		setContentView( R.layout.activity_reset_email_password);
//		setActionBarTitle(getResources().getString(R.string.reset_password));
		TextView tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.reset_password);
//		btnSend = ( Button ) findViewById( R.id.btnSend );
		mBtnSend = (TextView) findViewById( R.id.btnSend );
		mBtnSend.setOnClickListener( this );
		
		etRegEmail = ( AutoCompleteTextView ) findViewById( R.id.etRegEmail );
		etRegEmail.requestFocus( );
		etRegEmail.setSelection( etRegEmail.getText( ).length( ) );

		etRegEmail.addTextChangedListener(textWatcher);
		findViewById(R.id.fl_left).setOnClickListener(this);
		findViewById(R.id.iv_left).setOnClickListener(this);
	}
	
	@Override
	public void onWindowFocusChanged( boolean hasFocus )
	{
		super.onWindowFocusChanged( hasFocus );
		if ( hasFocus )
		{
			EmailAutoCompleteAdapter emailAdapter = new EmailAutoCompleteAdapter( this );
			etRegEmail.setAdapter( emailAdapter );
			etRegEmail.addTextChangedListener( new EmailAutoCompleteTextWatcher( etRegEmail ,
					emailAdapter ) );
		}
	}
	
	public boolean handleMessage( Message msg )
	{
		if ( pd != null && pd.isShowing( ) )
		{
			pd.dismiss( );
		}
		switch ( msg.what )
		{
			case FIND_PSW_FIN :
			{
				if ( msg.arg1 > 0 )
				{ // 成功
					if ( msg.obj != null )
					{
						Toast.makeText( this , String.valueOf( msg.obj ) , Toast.LENGTH_SHORT )
								.show( );
					}
					finish( );
				}
				else
				{
					if ( msg.obj != null )
					{
						ErrorCode.showError( mContext , ( String ) msg.obj );
					}
				}
			}
		}
		return false;
	}
	
	public void onClick( View v )
	{
		if ( mBtnSend.equals( v ) )
		{ // 请求找回密码邮件
			doFindPsw( );
		}else if (v.getId() == R.id.fl_left || v.getId() == R.id.iv_left)
		{
			finish();
		}
	}
	
	// 跳转找回密码
	private void doFindPsw( )
	{
		int netType = BaseHttp.checkNetworkType( this );
		if ( netType == BaseHttp.TYPE_NET_WORK_DISABLED )
		{
			CommonFunction.toastMsg( this , R.string.network_req_failed );
			return;
		}
		String address = etRegEmail.getText( ).toString( ).trim( );
		if ( address == null || address.length( ) <= 0 )
		{ // 邮箱地址为空
			Toast.makeText( this , R.string.please_enter_email_add , Toast.LENGTH_SHORT )
					.show( );
			return;
		}
		
		if ( !CommonFunction.isEmailAddValid( address ) )
		{ // 邮箱非法
			Toast.makeText( this , R.string.email_add_invalid , Toast.LENGTH_SHORT ).show( );
			return;
		}
		
		// 找密码请求
		if ( pd != null && pd.isShowing( ) )
		{
			pd.dismiss( );
		}
		pd = DialogUtil.showProgressDialog( this , R.string.login , R.string.please_wait ,
				null );

//		LoginHttpProtocol.passwordReset( this , address, "" ,"","", httpStringCallback );
		LoginHttpProtocol.passwordReset(this, address, httpCallBack);
	}

	private HttpCallBack httpCallBack = new HttpCallBack() {
		@Override
		public void onGeneralSuccess(String result, long flag) {
			try {
				JSONObject json = new JSONObject(result);
				int status = json.optInt("status");
				if (status != 200) { // 错误返回
					Message msg = new Message();
					msg.what = FIND_PSW_FIN;
					msg.obj = result;
					handler.sendMessage(msg);
				} else { // 正确返回
					Message msg = new Message();
					msg.what = FIND_PSW_FIN;
					msg.arg1 = 1;
					msg.obj = getString(R.string.password_reset_email_sent);
					handler.sendMessage(msg);
				}
			}catch (Throwable e){
				e.printStackTrace( );
				CommonFunction.toastMsg(ResetEmailPasswordActivity.this, getActivity().getString(R.string.e_1) );
			}
		}

		@Override
		public void onGeneralError(int e, long flag) {

		}
	};
//	private HttpStringCallback httpStringCallback = new HttpStringCallback() {
//		@Override
//		public void onGeneralSuccess(String result, int id) {
//			try {
//				JSONObject json = new JSONObject(result);
//				int status = json.optInt("status");
//				if (status != 200) { // 错误返回
//					Message msg = new Message();
//					msg.what = FIND_PSW_FIN;
//					msg.obj = result;
//					handler.sendMessage(msg);
//				} else { // 正确返回
//					Message msg = new Message();
//					msg.what = FIND_PSW_FIN;
//					msg.arg1 = 1;
//					msg.obj = getString(R.string.password_reset_email_sent);
//					handler.sendMessage(msg);
//				}
//			}catch (Throwable e){
//				e.printStackTrace( );
//				CommonFunction.toastMsg(ResetEmailPasswordActivity.this, getActivity().getString(R.string.e_1) );
//			}
//		}
//
//		@Override
//		public void onGeneralError(String error, Exception e, int id) {
//
//		}
//	};

	private TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

		}

		@Override
		public void afterTextChanged(Editable editable) {
			String email = etRegEmail.getText().toString();
			if (email.length() > 0){
				mBtnSend.setEnabled(true);
			}else {
				mBtnSend.setEnabled(false);
			}
		}
	};

}
