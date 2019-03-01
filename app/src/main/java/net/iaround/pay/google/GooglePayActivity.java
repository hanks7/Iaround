
package net.iaround.pay.google;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.protocol.PayHttpProtocol;
import net.iaround.entity.GoogleTradeRec;
import net.iaround.model.entity.GoogleNotifyRec;
import net.iaround.model.entity.GooglePayDBBean;
import net.iaround.model.entity.GooglePayNotifyBean;
import net.iaround.model.entity.GooglePayNotifyBean.GoogleOrder;
import net.iaround.pay.ChannelType;
import net.iaround.pay.PayGoodsList;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.activity.PayChannelListActivity;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.datamodel.PayModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


/**
 * @ClassName GooglePayActivity.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-7-18 下午1:27:39
 * @Description: 谷歌支付，【一个订单中只有一个商品】
 */

public class GooglePayActivity extends SuperActivity implements OnClickListener
{
	protected static final String TAG = "Google Play";
	public static String GOOGLEPAY_GOODSID = "google_pay_goodsid";
	private static final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkDjXyvs5ql3E/CWemJDXdhw6HM5LAXtjIs7+jfsESol032Qn+nJUwSvdeNOLOl+Owe3p2sQ/M5v2z5YvuI6IviWcqRAH2gMwpJ1FSbMQMCHq4FWyWpeJCuAumNrBUMARdr+VHy+eYlCgXtwOJNMEi/9Qs1g2P/qQN098KxRJTgxm1nTauFn5Nem0jPSAf2BGw8Th9kAUUGxiw0SiWCqIwwsA9P8OUFJGotFLmijyoozzDT2AVd/cPqQukHA2fxnPtCIncyWv41t9hCyZPL6PN/gJRWBRi5uxQRizd0tjy0Dw9D9nu879AkmPxQAqbsj7iWGxg1gNCx1vGshKSKJl1QIDAQAB";
	
	private IabHelper mHelper;
	// 商品id
	private String goodsID;
	
	private ImageView backBtn;
	private TextView titleView;
	
	/**
	 * requestCode A request code (to differentiate from other responses -- as
	 * in {@link android.app.Activity#startActivityForResult})
	 */
	private final int REQUEST_COED = 10000;
	
	private long TRADE_FLAG;
	private long NOTIFY_FLAG;
	
	private String mOrderID;// 我们服务端下发的订单id
	
	private Dialog dialog;
	
	private WorkerThread mThread;
	
	private static final int INSERT_ORDER_TO_DATABASE = 1;
	private static final int DELETE_ORDER_FROM_DATABASE = 2;
	private long orderDBID;
	
	private String payLoad;
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_googlepay );
		Intent intent = getIntent( );
		goodsID = intent.getStringExtra( GOOGLEPAY_GOODSID );
		if ( TextUtils.isEmpty( goodsID ) )
		{
			finish( );
		}
		
		initView( );
		initVar();
		createOrder( );
	}
	
	private void initView( )
	{
		backBtn = (ImageView) findViewById( R.id.title_back );
		backBtn.setOnClickListener( this );
		
		titleView = (TextView) findViewById( R.id.title_name );
		titleView.setText( R.string.google_pay );
	}
	
	private void initVar()
	{
		mThread = new WorkerThread( this );
		mThread.start( );
	}
	
	
	private void createOrder( )
	{
		TRADE_FLAG = PayHttpProtocol.googleTrade( this , goodsID , this );
		showDialog( );
	}
	
	private GooglePayNotifyBean composeSignedData(Purchase purchase )
	{
		if ( purchase == null )
		{
			return null;
		}
		
		GooglePayNotifyBean bean = new GooglePayNotifyBean( );
		bean.nonce = "0";//服务端没用字段
		
		GoogleOrder order = new GoogleOrder( );
		order.token = purchase.getToken( );
		order.notificationId = "";//服务端没用字段
		order.orderId = purchase.getOrderId( );
		order.packageName = purchase.getPackageName( );
		order.productId = purchase.getSku( );
		order.purchaseTime = purchase.getPurchaseTime( );
		order.purchaseState = purchase.getPurchaseState( );
		order.developerPayload = purchase.getDeveloperPayload();
		
		bean.orders.add( order );
		
		return bean;
	}
	
	@Override
	public void onClick( View v )
	{
		finish( PayGoodsList.RESULT_CANCELED );
	}
	
	// 按下返回
	public boolean onKeyDown( int keyCode , KeyEvent event )
	{
		if ( keyCode == KeyEvent.KEYCODE_BACK )
		{
			finish( PayGoodsList.RESULT_CANCELED );
			return true;
		}
		return false;
	}
	
	
	private void initGoogleConnect( )
	{
		mHelper = new IabHelper( this , base64EncodedPublicKey );
		mHelper.enableDebugLogging( true );
		mHelper.startSetup( new IabHelper.OnIabSetupFinishedListener( )
		{
			public void onIabSetupFinished( IabResult result )
			{
				if ( !result.isSuccess( ) )
				{
					Log.d( TAG , "Problem setting up In-app Billing: " + result );
//					Toast.makeText( GooglePayActivity.this , "安装IAB失败" , Toast.LENGTH_LONG )
//							.show( );
					finish( PayGoodsList.RESULT_FAILURE );
					return;
				}
				
				// 查询商品列表(异步)
				ArrayList< String > listSKU = new ArrayList< String >( );
				listSKU.add( goodsID );
				mHelper.queryInventoryAsync( true , listSKU , mQueryFinishedListener );
			}
		} );
	}
	
	// 查询商品列表完成的侦听器
	IabHelper.QueryInventoryFinishedListener mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener( )
	{
		public void onQueryInventoryFinished(IabResult result , Inventory inventory )
		{
			if ( result.isFailure( ) )
			{
				Toast.makeText( GooglePayActivity.this , "查询商品失败" , Toast.LENGTH_LONG )
						.show( );
				finish( PayGoodsList.RESULT_FAILURE );
				return;
			}
			
			// 如果有购买成功但是没消费的商品，先消费该商品
			boolean bPurchased = inventory.hasPurchase( goodsID );
			if ( bPurchased )
			{
				mHelper.consumeAsync( inventory.getPurchase( goodsID ) ,
						mConsumeFinishedListener );
			}
			else
			{
				mHelper.launchPurchaseFlow( GooglePayActivity.this , goodsID , REQUEST_COED ,
						mPurchaseFinishedListener, payLoad );
			}
		}
	};
	
	// 购买商品完成的侦听器
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener( )
	{
		public void onIabPurchaseFinished(IabResult result , Purchase purchase )
		{
			if ( result.isFailure( ) )
			{
				Toast.makeText( GooglePayActivity.this , "购买商品失败" , Toast.LENGTH_LONG ).show( );
				finish( PayGoodsList.RESULT_FAILURE );
				return;
			}
			
			// 获知响应数据
			GooglePayNotifyBean bean = composeSignedData( purchase );
			String signeddata = GsonUtil.getInstance( ).getStringFromJsonObject( bean );
			
//			NOTIFY_FLAG = PayHttpProtocol.googleNotify( GooglePayActivity.this ,
//					signeddata , purchase.getSignature( ) , GooglePayActivity.this );
//			showDialog( );
			
			
			
			// 异步提交消费该商品的请求（购买完成后需消费该该商品，否则该用户不能再次购买该商品）
			mHelper.consumeAsync( purchase , mConsumeFinishedListener );
		}
	};
	
	// 消费商品完成的侦听器
	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener( )
	{
		public void onConsumeFinished(Purchase purchase , IabResult result )
		{
			if ( result.isSuccess( ) )
			{
				// 获知响应数据
				
				
				
				GooglePayNotifyBean bean = composeSignedData( purchase );
				String signeddata = GsonUtil.getInstance( ).getStringFromJsonObject( bean );
				
				//插入数据库
				GooglePayDBBean dbBean = new GooglePayDBBean();
				dbBean.signature = purchase.getSignature( );
				dbBean.signeddata = signeddata;
				String content = GsonUtil.getInstance( ).getStringFromJsonObject( dbBean );
				Message msg = new Message();
				msg.what = INSERT_ORDER_TO_DATABASE;
				msg.obj = content;
				mThread.workerHandler.sendMessage( msg );
				
				NOTIFY_FLAG = PayHttpProtocol.googleNotify( GooglePayActivity.this ,
						signeddata , purchase.getSignature( ) , GooglePayActivity.this );
				showDialog( );
			}
			else
			{
				Toast.makeText( GooglePayActivity.this , "消费商品失败" , Toast.LENGTH_LONG ).show( );
				finish( PayGoodsList.RESULT_FAILURE );
			}
		}
	};
	
	@Override
	protected void onActivityResult( int requestCode , int resultCode , Intent data )
	{
		// 传递activity result给mHelper处理（否则onIabPurchaseFinished不会被调用）
		if ( !mHelper.handleActivityResult( requestCode , resultCode , data ) )
		{
			super.onActivityResult( requestCode , resultCode , data );
		}
		else
		{
			Log.d( TAG , "onActivityResult handled by IABUtil." );
		}
	}
	
	@Override
	public void onDestroy( )
	{
		super.onDestroy( );
		if ( mHelper != null )
		{
			mHelper.dispose( );
			mHelper = null;
		}
		
		if(dialog != null)
		{
			dialog.dismiss( );
			dialog = null;
		}
		
		if(mThread != null)
		{
			mThread.workerHandler.removeMessages( DELETE_ORDER_FROM_DATABASE );
			mThread.workerHandler.removeMessages( INSERT_ORDER_TO_DATABASE );
			mThread.interrupt( );
			mThread = null;
		}
	}
	
	@Override
	public void onGeneralSuccess(String result , long flag )
	{
		super.onGeneralSuccess( result , flag );
		hideDialog( );
		if ( flag == TRADE_FLAG )
		{
			GoogleTradeRec bean = GsonUtil.getInstance( ).getServerBean( result ,
					GoogleTradeRec.class );
			PayChannelListActivity.mOrdernumber =bean.orderid;
			TradeBack( bean );
			
		}
		else if ( flag == NOTIFY_FLAG )
		{
			GoogleNotifyRec bean = GsonUtil.getInstance( ).getServerBean( result ,
					GoogleNotifyRec.class );
			NotifyBack( bean );
		}
	}
	
	@Override
	public void onGeneralError( int e , long flag )
	{
		super.onGeneralError( e , flag );
		ErrorCode.toastError( mContext , e );
		hideDialog( );
		if ( flag == TRADE_FLAG )
		{
			finish( PayGoodsList.RESULT_FAILURE );
		}
		else if ( flag == NOTIFY_FLAG )
		{
			finish( PayGoodsList.RESULT_FAILURE );
		}
	}
	
	private void TradeBack( GoogleTradeRec bean )
	{
		if ( bean.isSuccess( ) )
		{
			mOrderID = bean.orderid;
			payLoad = Common.getInstance( ).loginUser.getUid( ) + "," + mOrderID;
			initGoogleConnect( );
		}
		else
		{
			// 失败
			finish( PayGoodsList.RESULT_FAILURE );
			ErrorCode.toastError( mContext , bean.error );
		}
	}
	
	
	private void NotifyBack( GoogleNotifyRec bean )
	{
		if ( bean.isSuccess( ) )
		{
			if(!bean.orders.isEmpty( ) && !bean.orders.get( 0 ).bIsUnknowFail( ))
			{
				mThread.workerHandler.sendEmptyMessage( DELETE_ORDER_FROM_DATABASE );
				finish( PayGoodsList.RESULT_OK );
			}else
			{
				finish( PayGoodsList.RESULT_FAILURE );
			}
		}
		else
		{
			// 失败
			finish( PayGoodsList.RESULT_FAILURE );
			ErrorCode.toastError( mContext , bean.error );
		}
	}
	
	public void finish( int resutcode )
	{
		Intent intent = new Intent( );
		setResult( resutcode , intent );
		super.finish( );
	}

	private void showDialog( )
	{
		if ( dialog == null )
		{
			dialog = DialogUtil.showProgressDialog( mActivity , "" ,
					mActivity.getResString( R.string.please_wait ) , null );
		}
		dialog.show( );
	}
	
	private void hideDialog( )
	{
		if ( dialog != null && dialog.isShowing( ) )
		{
			dialog.hide( );
		}
	}
	
	
	class WorkerThread extends Thread
	{
		public WorkerHandler workerHandler;
		public WeakReference< GooglePayActivity > mActivity;
		
		public WorkerThread( GooglePayActivity activity )
		{
			mActivity = new WeakReference< GooglePayActivity >( activity );
		}
		
		@Override
		public void run( )
		{
			Looper.prepare( );
			workerHandler = new WorkerHandler();
			Looper.loop( );
		}
		
		class WorkerHandler extends Handler
		{
			@Override
			public void handleMessage( Message msg )
			{
				super.handleMessage( msg );
				switch ( msg.what )
				{
					case INSERT_ORDER_TO_DATABASE:
						String content = (String) msg.obj;
						insertOrder( content );
						break;
					case DELETE_ORDER_FROM_DATABASE:
						deleteOrder();
						break;
					default :
						break;
				}
			}
		}
		
		private void insertOrder(String content)
		{
			orderDBID = PayModel.getInstance( ).insertOrder( GooglePayActivity.this , ChannelType.GOOGLEPAY , content );
		}
		
		private void deleteOrder()
		{
			PayModel.getInstance( ).deleteOrder( GooglePayActivity.this , orderDBID );
		}
	}
	
}
