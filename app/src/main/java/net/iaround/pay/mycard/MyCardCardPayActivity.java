
package net.iaround.pay.mycard;


import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.protocol.PayHttpProtocol;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.CryptoUtil;
import net.iaround.tools.DialogUtil;
import net.iaround.ui.comon.SuperActivity;

import org.json.JSONException;
import org.json.JSONObject;


public class MyCardCardPayActivity extends SuperActivity implements OnClickListener
{
	private Button btn_submit;
	private EditText et_mycard_pass , et_mycard_card;
	
	private long card_flag = 0;
	
	private Dialog pd;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.pay_mycard_cardtype );
//		findViewById( R.id.title_right_img ).setVisibility( View.INVISIBLE );
		( (TextView) findViewById( R.id.tv_title ) ).setText( R.string.mycard_card );
		findViewById( R.id.fl_back ).setOnClickListener( this );
		
		et_mycard_card = (EditText) findViewById( R.id.et_mycard_card );
		et_mycard_pass = (EditText) findViewById( R.id.et_mycard_pass );
		btn_submit = (Button) findViewById( R.id.btn_submit );
		
		btn_submit.setOnClickListener( this );
	}
	
	@Override
	public void onClick( View v )
	{
		if ( v.getId( ) == R.id.fl_back )
		{
			finish( );
		}
		else if ( v.getId( ) == R.id.btn_submit )
		{
			String card = et_mycard_card.getText( ).toString( ).trim( );
			String pass = et_mycard_pass.getText( ).toString( ).trim( );
			if ( CommonFunction.isEmptyOrNullStr( card )
					|| CommonFunction.isEmptyOrNullStr( pass ) )
			{
				Toast.makeText( mActivity , "卡號或密碼不能為空" , Toast.LENGTH_LONG ).show( );
				return;
			}
			else
			{
				try
				{
					String cardInfo = CryptoUtil.rsaEncrypt( card + "@" + pass ,
							CryptoUtil.SZF_PUBLIC_KEY );
					payInfo( cardInfo );
				}
				catch ( Exception e )
				{
					e.printStackTrace( );
				}
			}
		}
	}
	
	// 提交账号和密码
	private void payInfo( String cardInfo )
	{
		pd = DialogUtil.showProgressDialog( mActivity , "" ,
				mActivity.getString( R.string.please_wait ) , null );
		card_flag = PayHttpProtocol.twmycardCardInfo( mActivity , cardInfo , this );
		if ( card_flag == -1 )
		{
			pd.dismiss( );
			DialogUtil.showOKDialog( mActivity , R.string.pay_error , R.string.pay_error_des ,
					null );
		}
	}
	
	@Override
	public void onGeneralError( int e , long flag )
	{
		if ( flag == card_flag )
		{
			mHandler.sendEmptyMessage( 1 );
		}
		super.onGeneralError( e , flag );
	}
	
	@Override
	public void onGeneralSuccess(String result , long flag )
	{
		if ( flag == card_flag )
		{
			Message msg = new Message( );
			msg.what = 2;
			msg.obj = result;
			mHandler.sendMessage( msg );
		}
		super.onGeneralSuccess( result , flag );
	}
	
	private Handler mHandler = new Handler( )
	{
		public void handleMessage( android.os.Message msg )
		{
			switch ( msg.what )
			{
				case 1 :
					if ( pd != null )
					{
						pd.dismiss( );
					}
					break;
				case 2 :
					if ( pd != null )
					{
						pd.dismiss( );
					}
					String result = String.valueOf( msg.obj );
					if ( result != null )
					{
						try
						{
							JSONObject json = new JSONObject( result );
							if ( json.optInt( "status" ) == 200 )
							{
								final String errormsg = CommonFunction.jsonOptString(json, "errormsg" );
								String tip = mActivity.getString( R.string.pay_complete );
								String sussmsg = mActivity
										.getString( R.string.pay_complete_des );
								if ( !CommonFunction.isEmptyOrNullStr( errormsg ) )
								{
									sussmsg = errormsg;
								}
								DialogUtil.showOKDialog( mActivity , tip , sussmsg ,
										new View.OnClickListener( )
										{
											
											@Override
											public void onClick( View v )
											{
												if ( CommonFunction
														.isEmptyOrNullStr( errormsg ) )
												{
													finish( );
												}
											}
											
										} );
							}
							else
							{
								ErrorCode.showError( mActivity , result );
							}
						}
						catch ( JSONException e )
						{
							e.printStackTrace( );
						}
					}
					break;
			}
		}
    };
}
