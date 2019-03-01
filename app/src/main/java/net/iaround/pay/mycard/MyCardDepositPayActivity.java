
package net.iaround.pay.mycard;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.protocol.PayHttpProtocol;
import net.iaround.pay.PayGoodsList;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.ui.activity.PayChannelListActivity;
import net.iaround.ui.comon.SuperActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


/**
 * mycard支付
 * 
 * @author linyg
 * 
 */
public class MyCardDepositPayActivity extends SuperActivity implements OnClickListener
{
	public static final String MYCARDPAY_GOODID = "mycardpay_goodid";
	
	public static final String MYCARDPAY_TYPE = "mycardpay_type";// 类型
																	// 1：小额支付，2：点卡支付
	private String goodsid = "";
	private int pay_type = 0;// 类型 1：小额支付，2：点卡支付
	private String currentUrl = "";
	private WebView webview;
	private Dialog pd;
	
	private long deposit_flag = 0;
	
	private final int deposit_order = 1001;
	private final int deposit_handler_order = 1002;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.pay_mycard );
		goodsid = getIntent( ).getStringExtra( MYCARDPAY_GOODID );
		pay_type = getIntent( ).getIntExtra( MYCARDPAY_TYPE , 1 );
		
		findViewById( R.id.title_back ).setOnClickListener( this );
		findViewById( R.id.title_right_img ).setVisibility( View.INVISIBLE );
		( (TextView) findViewById( R.id.title_name ) ).setText( R.string.mycard_deposit );
		
		webview = (WebView) findViewById( R.id.mycard_webview );
		webview.requestFocus( );
		webview.setVisibility( View.VISIBLE );
		webview.getSettings( ).setJavaScriptEnabled( true );
		webview.getSettings( ).setSupportZoom( true );
		webview.getSettings( ).setBuiltInZoomControls( true );
		
		webview.setWebChromeClient( new WebChromeClient( )
		{
			public void onProgressChanged(WebView view , int newProgress )
			{
				if ( newProgress == 100 )
				{
					if ( pd != null )
						pd.dismiss( );
				}
				super.onProgressChanged( view , newProgress );
			}
		} );
		
		webview.setWebViewClient( new WebViewClient( )
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view , String url )
			{
				return super.shouldOverrideUrlLoading( view , url );
			}
			
			@Override
			public void onPageStarted(WebView view , String url , Bitmap favicon )
			{
				CommonFunction.log( "myCardPay" , url );
				if ( url.contains( "TradeSeq=" ) && url.contains( "/twmycard/notify" ) )
				{
					Message msg = new Message( );
					msg.what = deposit_handler_order;
					msg.obj = url;
					mHandler.sendMessageDelayed( msg , 200 );
				}
				super.onPageStarted( view , url , favicon );
			}
		} );
		
		webview.setOnTouchListener( new OnTouchListener( )
		{
			public boolean onTouch(View v , MotionEvent event )
			{
				if ( event.getAction( ) == MotionEvent.ACTION_DOWN )
				{
					v.requestFocus( );
				}
				return false;
			}
		} );
		
		getDepositOrder( );
	}
	
	// 请求小额支付订单号
	private void getDepositOrder( )
	{
		pd = DialogUtil.showProgressDialog( mActivity , "" ,
				mActivity.getString( R.string.please_wait ) , null );
		deposit_flag = PayHttpProtocol.twmycardOrder( mActivity , goodsid ,
				MyCardDepositPayActivity.this );
		if ( deposit_flag == -1 )
		{
			pd.dismiss( );
			finish( PayGoodsList.RESULT_FAILURE );
		}
	}
	
	@Override
	public void onGeneralError( int e , long flag )
	{
		if ( flag == deposit_flag )
		{
			mHandler.sendEmptyMessage( 1 );
		}
		super.onGeneralError( e , flag );
	}
	
	@Override
	public void onGeneralSuccess(String result , long flag )
	{
		if ( flag == deposit_flag )
		{
			Message msg = new Message( );
			msg.what = deposit_order;
			msg.obj = result;
			mHandler.sendMessage( msg );
		}
		super.onGeneralSuccess( result , flag );
	}
	
	private Handler mHandler = new Handler( )
	{
		@Override
		public void handleMessage( Message msg )
		{
			super.handleMessage( msg );
			switch ( msg.what )
			{
				case 1 : // 请求订单失败
					if ( pd != null )
						pd.dismiss( );
					finish( PayGoodsList.RESULT_FAILURE );
					break;
				case deposit_order :
					try
					{
						String result = String.valueOf( msg.obj );
						JSONObject json = new JSONObject( result );
						if ( json.optInt( "status" ) == 200 )
						{
							PayChannelListActivity.mOrdernumber =CommonFunction.jsonOptString(json, "orderid" );
							currentUrl = CommonFunction.jsonOptString(json, "callurl" );
							// 加载webview
							if ( !CommonFunction.isEmptyOrNullStr( currentUrl ) )
							{
								webview.loadUrl( currentUrl );
							}
							else
							{
								finish( PayGoodsList.RESULT_FAILURE );
							}
						}
						else
						{
							if ( pd != null )
								pd.dismiss( );
							ErrorCode.showError( mActivity , result );
							finish( PayGoodsList.RESULT_FAILURE );
						}
					}
					catch ( JSONException e )
					{
						CommonFunction.log( "MyCardPay" , e.getMessage( ) );
					}
					break;
				case deposit_handler_order : // 拦截url处理结果,
					String returnMsgNo = "";
					String returnMsg = "";
					try
					{
						String result = String.valueOf( msg.obj );
						Map< String , String > map = CommonFunction.paramURL( result );
						returnMsgNo = map.get( "ReturnMsgNo" );
						returnMsg = map.get( "ReturnMsg" );
					}
					catch ( Exception e )
					{
						e.printStackTrace( );
					}
					if ( returnMsgNo.contains( "1" ) )
					{ // 支付成功
						finish( PayGoodsList.RESULT_OK );
					}
					else
					{ // 支付失败
						Toast.makeText( mContext , returnMsg , Toast.LENGTH_SHORT ).show( );
						finish( PayGoodsList.RESULT_NO_DES );
					}
					break;
			}
		}
	};
	
	@Override
	public void onClick( View v )
	{
		if ( v.getId( ) == R.id.title_back )
		{
			finish( PayGoodsList.RESULT_CANCELED );
		}
	}
	
	public void finish( int resutcode )
	{
		Intent intent = new Intent( );
		setResult( resutcode , intent );
		super.finish( );
	}
}
