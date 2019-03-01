
package net.iaround.wxapi;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import net.iaround.R;
import net.iaround.conf.MessageID;
import net.iaround.connector.CallBackNetwork;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.PayHttpProtocol;
import net.iaround.model.im.TransportMessage;
import net.iaround.pay.PayGoodsList;
import net.iaround.pay.bean.PayWechatOrderBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.activity.BaseFragmentActivity;
import net.iaround.ui.activity.PayChannelListActivity;

import org.json.JSONObject;

/**
 * 微信支付
 * 
 * @author Administrator
 * 
 */
public class WXPayEntryActivity extends BaseFragmentActivity implements IWXAPIEventHandler,CallBackNetwork,HttpCallBack
{
	
	private WXPayEntryActivity  instance;
	private final String TAG = "WechatPayActivity";
	public static String WECHAT_GOODSID = "goodsid";
	private String token_id = "";
	private String bargainor_id = "";
	private final static int MSG_PAY_RESULT = 1001;
	private final static int MSG_LOGIN_RESULT = 1002;
	/** 订单号成功生成 **/
	private final int MSG_ORDER_SUCCESS = 2001;
	/** 订单号生成失败 **/
	private final int MSG_ORDER_FAIL = 2002;
	private final int MSG_WAP_ORDER_SUCCESS = 2003;
	private final int TENPAY_BACK_SUCCESS = 2004;
	
	private long getOrder_flag = 0;
	
	private Dialog dialog;
	
	// webview
	
	private String payurl = "";
	private String backurl = "";
	
	private IWXAPI api;
//	private static final String CONSUMER_KEY = "wx82ae7d5986311ba1";
//	private static final String CONSUMER_KEY = "wx681bcb961cfba401";
//	private static final String CONSUMER_KEY = "wx3a3e1883deda3c59";
	private static final String CONSUMER_KEY = "wx0e4a408c5839e9c4";

	private boolean isPayResult = false;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		String goodsid = getIntent( ).getStringExtra( WECHAT_GOODSID );
		getOrderID( goodsid );
		api = WXAPIFactory.createWXAPI(this, CONSUMER_KEY , false );
		api.handleIntent(getIntent(), this);
	}

	/**
	 * 获取订单号
	 * 
	 * 
	 */
	private void getOrderID( String goodsid )
	{
		dialog = DialogUtil.showProgressDialog( mActivity , "" , mActivity.getResources( )
				.getString( R.string.please_wait ) , null );
		getOrder_flag = PayHttpProtocol.wechatOrder( mActivity , goodsid , this );
		
	}
	
	@Override
	public void onGeneralError( int e , long flag )
	{
		if ( getOrder_flag == flag )
		{
			mHandler.sendEmptyMessage( MSG_ORDER_FAIL );
		}
	}
	
	@Override
	public void onGeneralSuccess(String result , long flag )
	{
		if ( getOrder_flag == flag )
		{
			isPayResult = true;
			Message msg = new Message( );
			msg.what = MSG_ORDER_SUCCESS;
			msg.obj = result;
			mHandler.sendMessage( msg );
		}
	}
	
	// 接收支付返回值的Handler
	protected Handler mHandler = new Handler( )
	{
		public void handleMessage( Message msg )
		{
			switch ( msg.what )
			{
				case MSG_PAY_RESULT :
					String strRet = (String) msg.obj; // 支付返回值
					CommonFunction.log( TAG , "pay_result:" + strRet );
					String statusCode = null;
					String info = null;
					String result = null;
					JSONObject jo;
					try
					{
						jo = new JSONObject( strRet );
						if ( jo != null )
						{
							statusCode = jo.getString( "statusCode" );
							info = jo.getString( "info" );
							result = java.net.URLDecoder.decode( jo.getString( "result" ) );
							CommonFunction.log( TAG , "pay_result:" + result );
							if ( "0".equals( statusCode )
									&& result.contains( "&pay_result=0&" ) )
							{
								finish( PayGoodsList.RESULT_OK );
							}
							else
							{
								DialogUtil.showOKDialog( mActivity ,
										mActivity.getString( R.string.prompt ) , info ,
										new View.OnClickListener( )
										{
											
											@Override
											public void onClick( View v )
											{
												finish( PayGoodsList.RESULT_FAILURE );
											}
											
										} );
							}
							return;
						}
					}
					catch ( Exception e1 )
					{
						e1.printStackTrace( );
					}
					finish( PayGoodsList.RESULT_FAILURE );
					break;
				case MSG_ORDER_SUCCESS : // 订单成功
					if ( dialog != null )
						dialog.dismiss( );
					try
					{
						
						String json = (String) msg.obj;
						
						PayWechatOrderBean bean = GsonUtil.getInstance( ).getServerBean( json , PayWechatOrderBean.class );
						
						PayChannelListActivity.mOrdernumber = bean.ordernumber;
						if ( bean.isSuccess( ) )
						{
							weChatPay(bean);
						}
						else
						{
							finish( PayGoodsList.RESULT_FAILURE );
						}
					}
					catch ( Exception e )
					{
						e.printStackTrace( );
					}
					break;
				case MSG_ORDER_FAIL : // 订单失败
					if ( dialog != null )
						dialog.dismiss( );
					finish( PayGoodsList.RESULT_FAILURE );
					break;
				case TENPAY_BACK_SUCCESS : // 财付通回调
					finish( PayGoodsList.RESULT_OK );
					break;
			}
		}
	};

	//订单支付
	private void weChatPay(PayWechatOrderBean bean){
		PayReq payRequest = new PayReq();
		payRequest.appId = bean.appid;
		payRequest.partnerId = bean.partnerid;
		payRequest.prepayId = bean.prepayid;
		payRequest.packageValue = bean.packagevalue;
		payRequest.nonceStr = bean.noncestr;
		payRequest.timeStamp = bean.timestamp;
		payRequest.sign = bean.sign;
		api.sendReq(payRequest);
	}
	
	public void finish( int resutcode )
	{
		setResult( resutcode );
		super.finish( );
	}
	
	@Override
	protected void onResume( )
	{
		CommonFunction.log( "shifengxiong" ,"WXPayEntryActivity   onResume ");
		
		super.onResume( );
		// 微信充值成功后，不能执行此业务

		if (instance == null) {
			instance = this;
		} else {
			if (!isPayResult) {
				setResult(RESULT_CANCELED);
				finish();
			}
		}

		ConnectorManage.getInstance( this ).setCallBackAction( this );
	}
	
	@Override
	public boolean onKeyDown( int keyCode , KeyEvent event )
	{
		if ( keyCode == KeyEvent.KEYCODE_BACK )
		{
			this.finish( Activity.RESULT_CANCELED );
			return true;
		}
		return super.onKeyDown( keyCode , event );
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}
	
	// 微信发送请求到第三方应用时，会回调到该方法
	@Override
	public void onReq( BaseReq req )
	{
		// TODO Auto-generated method stub
		switch ( req.getType( ) )
		{
			case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX :
				Toast.makeText( this , "COMMAND_GETMESSAGE_FROM_WX" , Toast.LENGTH_LONG ).show( );
				break;
			case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX :
				Toast.makeText( this , "COMMAND_SHOWMESSAGE_FROM_WX" , Toast.LENGTH_LONG ).show( );
				break;
			default :
				break;
		}
	}
	
	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	@Override
	public void onResp( BaseResp resp )
	{
		String result = "";
		switch ( resp.errCode )
		{
			case BaseResp.ErrCode.ERR_OK :
				
				finish( PayGoodsList.RESULT_OK );
				result = "发送成功";
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL :
				result = "发送取消";
				finish( PayGoodsList.RESULT_CANCELED );
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED :
				result ="发送被拒绝";
				finish( PayGoodsList.RESULT_FAILURE );
				break;
			default :
				result = "发送返回";
				finish( PayGoodsList.RESULT_NO_DES );
				break;
		}
	}





	@Override
	public void onReceiveMessage( TransportMessage message )
	{
		// TODO Auto-generated method stub
		if ( message.getMethodId( ) == MessageID.SESSION_PUHS_PAY_RESULT )
		{
			setResult( PayGoodsList.RESULT_OK  );
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
