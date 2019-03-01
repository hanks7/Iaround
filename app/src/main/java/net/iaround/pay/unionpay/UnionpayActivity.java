
package net.iaround.pay.unionpay;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;
import android.widget.Toast;


import com.unionpay.upomp.tbow.activity.UPOMP;
import com.unionpay.upomp.tbow.utils.MyBaseActivity;

import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.conf.MessageID;
import net.iaround.connector.CallBackNetwork;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.protocol.PayHttpProtocol;
import net.iaround.model.im.TransportMessage;
import net.iaround.pay.PayGoodsList;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.ui.activity.PayChannelListActivity;
import net.iaround.ui.comon.SuperActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;


/**
 * 银联支付
 * 
 */
public class UnionpayActivity extends SuperActivity implements CallBackNetwork
{
	private final String TAG = UnionpayActivity.class.getName( );
	public static String UNIONPAY_GOODSID = "unionpay_goodsid";
	
	/** 订单号成功生成 **/
	private final int MSG_ORDER_SUCCESS = 2001;
	/** 订单号生成失败 **/
	private final int MSG_ORDER_FAIL = 2002;
	
	/** 请求订单 */
	private final int MSG_ORDER = 2003;
	
	private long flagCreateOrder = 0;
	private String goodsid;
	private Dialog dialog;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.pay_unionpay );
		goodsid = getIntent( ).getStringExtra( UNIONPAY_GOODSID );
		if ( CommonFunction.isEmptyOrNullStr( goodsid ) )
		{
			finish( PayGoodsList.RESULT_FAILURE );
			return;
		}
		
		mHandler.sendEmptyMessageDelayed( MSG_ORDER , 200 );
	}
	
	
	
	// 启动银联支付插件
	private void startUnionPlug( String result )
	{
		MyBaseActivity.setPackageName( this.getPackageName( ) );// 设置当前包的R路径
		Intent intent = new Intent( this , com.unionpay.upomp.tbow.paymain.class );
		Bundle xmlData = new Bundle( );
		xmlData.putString( "xml" , result.toString( ) ); // 需要传入的订单数据
		intent.putExtras( xmlData );
		startActivity( intent );
	}
	
	// 生成订单
	private void createOrder( String goodsid )
	{
		dialog = DialogUtil.showProgressDialog( mActivity , "" ,
				mActivity.getResString( R.string.please_wait ) , null );
		flagCreateOrder = PayHttpProtocol.unionPayOrder( mActivity , goodsid , this );
		if ( flagCreateOrder == -1 )
		{
			mHandler.sendEmptyMessage( MSG_ORDER_FAIL );
		}
	}
	
	@Override
	public void onResume( )
	{// 在当前activity
		super.onResume( );
		
		ConnectorManage.getInstance( this ).setCallBackAction( this );
		String xml = UPOMP.getPayResult( );// 返回的插件数据
		if ( xml != null && xml.contains( "UpPay.Rsp" ) )
		{
			CommonFunction.log( TAG , "result_xml:" + xml );
			try
			{
				// 解析返回的结果
				XmlPullParser parser = Xml.newPullParser( );
				parser.setInput( new StringReader( xml ) );
				int event = parser.getEventType( );
				String respCode = "";
				String respDesc = "";
				while ( event != XmlPullParser.END_DOCUMENT )
				{
					if ( event == XmlPullParser.START_TAG )
					{
						if ( "respCode".equals( parser.getName( ) ) )
						{
							respCode = parser.nextText( );
						}
						else if ( "respDesc".equals( parser.getName( ) ) )
						{
							respDesc = parser.nextText( );
						}
					}
					event = parser.next( );
				}
				// 支付情况
				if ( respCode.equals( "0000" ) )
				{
					finish( PayGoodsList.RESULT_OK );
				}
				else
				{
					Toast.makeText( mActivity , respDesc , Toast.LENGTH_LONG ).show( );
					finish( PayGoodsList.RESULT_FAILURE );
				}
			}
			catch ( Exception e )
			{
				e.printStackTrace( );
				finish( PayGoodsList.RESULT_FAILURE );
			}
		}
	}
	
	public void finish( int resutcode )
	{
		Intent intent = new Intent( );
		setResult( resutcode , intent );
		super.finish( );
	}
	
	
	@Override
	public void onGeneralError( int e , long flag )
	{
		if ( flag == flagCreateOrder )
		{
			mHandler.sendEmptyMessage( MSG_ORDER_FAIL );
		}
		super.onGeneralError( e , flag );
	}
	
	@Override
	public void onGeneralSuccess(String result , long flag )
	{
		if ( flag == flagCreateOrder )
		{
			Message msg = new Message( );
			msg.what = MSG_ORDER_SUCCESS;
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
				case MSG_ORDER_SUCCESS : // 订单成功
					try
					{
						if ( dialog != null )
							dialog.dismiss( );
					}
					catch ( Exception e )
					{
						// TODO: handle exception
					}
					
					String result = String.valueOf( msg.obj );
					try
					{
						JSONObject json = new JSONObject( result );
						if ( json.optInt( "status" ) == 200 )
						{
							String payinfo = "";
							try
							{
								payinfo = java.net.URLDecoder.decode(
										CommonFunction.jsonOptString( json , "payinfo" ) ,
										"UTF-8" );
								
								PayChannelListActivity.mOrdernumber = CommonFunction
										.jsonOptString( json , "orderid" );
							}
							catch ( UnsupportedEncodingException e )
							{
								e.printStackTrace( );
								payinfo = java.net.URLDecoder.decode( CommonFunction
										.jsonOptString( json , "payinfo" ) );
							}
							startUnionPlug( payinfo );
							break;
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
					finish( PayGoodsList.RESULT_FAILURE );
					break;
				case MSG_ORDER_FAIL : // 订单失败
					try
					{
						if ( dialog != null )
							dialog.dismiss( );
					}
					catch ( Exception e )
					{
						// TODO: handle exception
					}
					
					finish( PayGoodsList.RESULT_FAILURE );
					break;
				case MSG_ORDER : // 请求订单
					createOrder( goodsid );
					break;
			}
		}
    };
	
	
	@Override
	public void onReceiveMessage( TransportMessage message )
	{
		// TODO Auto-generated method stub
		if ( message.getMethodId( ) == MessageID.SESSION_PUHS_PAY_RESULT )
		{
			setResult( PayGoodsList.RESULT_OK );
		}
	}
	
	
	
	@Override
	public void onSendCallBack( int e , long flag )
	{
		// TODO Auto-generated method stub
		
	}
	
	
	
	@Override
	public void onConnected( )
	{
		// TODO Auto-generated method stub
		
	}
}
