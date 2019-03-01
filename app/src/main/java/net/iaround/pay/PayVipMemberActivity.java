
package net.iaround.pay;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.analytics.ums.DataTag;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GoldHttpProtocol;
import net.iaround.model.im.BaseServerBean;
import net.iaround.pay.bean.VipBuyMemberListBean;
import net.iaround.pay.bean.VipBuyMemberListBean.Goods;
import net.iaround.pay.google.IabHelper;
import net.iaround.pay.google.IabResult;
import net.iaround.pay.google.Inventory;
import net.iaround.pay.mycard.MyCardDepositPayActivity;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.activity.BaseFragmentActivity;
import net.iaround.ui.activity.WebViewAvtivity;
import net.iaround.ui.comon.NetImageView;
import net.iaround.ui.view.face.MyGridView;

import java.util.ArrayList;


/**
 * vip会员购买页面 此页面只有使用MyCar或者Gash支付方式才会进来
 * 
 * @author zhengst
 * 
 */
public class PayVipMemberActivity extends BaseFragmentActivity implements OnClickListener,HttpCallBack
{
	/** 是否为大陆地区，1是，2否 */
	
	private final static int RESQUESTCODE = 100;
	private String Uid = "";
	private long mGetMemberList;
	private VipBuyMemberListBean bean;
	private ArrayList< Goods > mList = new ArrayList< Goods >( );
	
	private NetImageView bannerView;
	private TextView foot_left_text;
	private TextView foot_right_text;
	private MyGridView myGridView;
	private Dialog mProgressDialog;
	private static MyGridViewAdapter GridAdapter;
	private int DefaulHeadPic = R.drawable.z_vip_banner_default_img;
	
	private static final int SHOW_DATA = 1000;
	private static final int GET_DATA_FAIL = 1001;
	private static final int PAY_REQUESTCODE = 1002;
	
	/** google pay start */
	private IabHelper mHelper;
	private ArrayList< String > skuArray;
	private static final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkDjXyvs5ql3E/CWemJDXdhw6HM5LAXtjIs7+jfsESol032Qn+nJUwSvdeNOLOl+Owe3p2sQ/M5v2z5YvuI6IviWcqRAH2gMwpJ1FSbMQMCHq4FWyWpeJCuAumNrBUMARdr+VHy+eYlCgXtwOJNMEi/9Qs1g2P/qQN098KxRJTgxm1nTauFn5Nem0jPSAf2BGw8Th9kAUUGxiw0SiWCqIwwsA9P8OUFJGotFLmijyoozzDT2AVd/cPqQukHA2fxnPtCIncyWv41t9hCyZPL6PN/gJRWBRi5uxQRizd0tjy0Dw9D9nu879AkmPxQAqbsj7iWGxg1gNCx1vGshKSKJl1QIDAQAB";
	/** google pay end */
	
	private int paywayId;
	private String title;
	private int channelType;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		
		setContentView( R.layout.x_vip_buy_member_gridview );
		Uid = String.valueOf( Common.getInstance( ).loginUser.getUid( ) );
		
		paywayId = getIntent( ).getIntExtra( PayGoodsList.GOODSLIST_CHANNEL , 0 );
		title = getIntent( ).getStringExtra( PayGoodsList.GOODSLIST_TITLE );
		channelType = getIntent( ).getIntExtra( PayGoodsList.CHANNEL_TYPE , 0 );
//		// 启动事件统计，添加入口界面的tag
//		String enterTag = getIntent( ).getStringExtra( EventBuffer.TAG_NAME );
//		if ( !CommonFunction.isEmptyOrNullStr( enterTag ) && !DataTag.UNKONW.equals( enterTag ) )
//		{
//			EventBuffer.getInstant( ).startEvent( DataTag.EVENT_buy_vip );
//			EventBuffer.getInstant( ).appen( enterTag );
//			EventBuffer.getInstant( ).appen( DataTag.VIEW_me_vip_pay );
//		}//gh
		
		initView( );
		initCacheData( );
		setListener( );
	}
	
	
	private void initView( )
	{
		TextView title_name = (TextView) findViewById( R.id.title_name );
		// title_name.setText( R.string.vip_member_pay );
		title_name.setText( title );
		TextView header_text = (TextView) findViewById( R.id.header_text );
		header_text.setText( R.string.vip_member_pay );
		foot_left_text = (TextView) findViewById( R.id.foot_left_text2 );
		foot_right_text = (TextView) findViewById( R.id.foot_right_text );
		bannerView = ( NetImageView ) findViewById( R.id.header_image );

		myGridView = ( MyGridView ) findViewById( R.id.gridview );
		GridAdapter = new MyGridViewAdapter( );
		myGridView.setAdapter( GridAdapter );
		myGridView.setOnItemClickListener( OnItemClickListener );
	}
	
	
	/**
	 * 加载缓存数据展示
	 */
	private void initCacheData( )
	{
		// 缓存数据时，如果是大陆地区，从服务端获取到的数据可直接展示做缓存，
		// 如果是海外地区，从谷歌转换后得到的数据再做缓存。
		// String data = SharedPreferenceCache.getInstance( mContext
		// ).getString(
		// "vip_pay_member" + Uid );
		// if ( data != null && !data.isEmpty( ) )
		// {
		// bean = GsonUtil.getInstance( ).getServerBean( data ,
		// VipBuyMemberListBean.class );
		// mList = bean.goods;
		// if (!CommonFunction.isEmptyOrNullStr(bean.icon)) {
		// bannerView.setVisibility(View.VISIBLE);
		// bannerView.execute( DefaulHeadPic , bean.icon );
		// }else {
		// bannerView.setVisibility(View.GONE);
		// }
		// GridAdapter.notifyDataSetChanged( );
		// }
		//
		requestData( );
	}
	
	
	/**
	 * 请求购买会员列表列表数据
	 */
	private void requestData( )
	{
		 showProgressDialog( );
		
		 mGetMemberList = GoldHttpProtocol.getVipGoodsList( mContext ,
		 String.valueOf( paywayId ) , this );
		
//		boolean area = PhoneInfoUtil.getInstance( mContext ).isChinaCarrier( );
//		int isChina = area ? 1 : 2;
//		CommonFunction.log( "" , "--->isChina = " + isChina );
//		mGetMemberList = GoldHttpProtocol.getBuyMemberList( mContext ,
//				String.valueOf( isChina ) , this );
	}
	
	private void setListener( )
	{
		bannerView.setOnClickListener( this );
		foot_left_text.setOnClickListener( this );
		foot_right_text.setOnClickListener( this );
		findViewById( R.id.title_back ).setOnClickListener( this );
	}
	
	
	class MyGridViewAdapter extends BaseAdapter
	{
		@Override
		public int getCount( )
		{
			if ( mList.size( ) > 0 )
			{
				return mList.size( );
			}
			return 0;
		}
		
		@Override
		public Object getItem(int position )
		{
			return mList.get( position );
		}
		
		@Override
		public long getItemId( int position )
		{
			return position;
		}
		
		@Override
		public View getView(int position , View convertView , ViewGroup parent )
		{
			ViewHolder viewHolder = null;
			if ( convertView == null )
			{
				viewHolder = new ViewHolder( );
				convertView = LayoutInflater.from( PayVipMemberActivity.this ).inflate(
						R.layout.x_vip_buymember_gridview_item , null );
				viewHolder.time = (TextView) convertView.findViewById( R.id.time_text );
				viewHolder.price = (TextView) convertView.findViewById( R.id.price_text );
				convertView.setTag( viewHolder );
			}
			else
			{
				viewHolder = ( ViewHolder ) convertView.getTag( );
			}
			
			Goods goods = mList.get( position );
			
			// 商品价格
			if ( goods.price != null && !goods.price.isEmpty( ) )
			{
				String price = goods.price;
				viewHolder.price.setText( price );
			}
			
			// 购买时长
			String timePostfix = getResources( ).getString( R.string.vip_buymenber_time_month );
			String timeStr = String.format( timePostfix , goods.month );
			viewHolder.time.setText( timeStr );
			
			return convertView;
		}
	}
	
	private AdapterView.OnItemClickListener OnItemClickListener = new OnItemClickListener( )
	{
		@Override
		public void onItemClick(AdapterView< ? > parent , View view , int position , long id )
		{
			Goods data = ( Goods ) parent.getAdapter( ).getItem( position );
			if ( data != null )
			{
				if ( ChannelType.MYCARD == channelType )
				{
					Intent intent = new Intent( mActivity , MyCardDepositPayActivity.class );
					intent.putExtra( MyCardDepositPayActivity.MYCARDPAY_GOODID , data.goodsid );
					intent.putExtra( MyCardDepositPayActivity.MYCARDPAY_TYPE , 1 );
					mActivity.startActivityForResult( intent , RESQUESTCODE );
				}
//				else
//				{
//					Intent intent = new Intent( mActivity , GashDepositPayActivity.class );
//					intent.putExtra( GashDepositPayActivity.GASHPAY_GOODID , data.goodsid );
//					mActivity.startActivityForResult( intent , RESQUESTCODE );
//				}//gh Gash取消掉
				
//				if ( data.month == 1 )
//					addBtnEvent( DataTag.BTN_me_vip_buyVip_1m );
//				if ( data.month == 3 )
//					addBtnEvent( DataTag.BTN_me_vip_buyVip_3m );
//				if ( data.month == 6 )
//					addBtnEvent( DataTag.BTN_me_vip_buyVip_6m );
//				if ( data.month == 12 )
//					addBtnEvent( DataTag.BTN_me_vip_buyVip_12m );
			}
		}
	};
	
	@Override
	public void onGeneralError( int e , long flag )
	{
		if ( flag == mGetMemberList )
		{
			hideProgressDialog( );
			ErrorCode.toastError( mContext , e );
		}
		super.onGeneralError( e , flag );
	}
	
	@Override
	public void onGeneralSuccess(String result , long flag )
	{
		if ( flag == mGetMemberList )
		{
			BaseServerBean Basebean = GsonUtil.getInstance( ).getServerBean( result ,
					BaseServerBean.class );
			if ( Basebean != null && Basebean.isSuccess( ) )
			{
				VipBuyMemberListBean data = GsonUtil.getInstance( ).getServerBean( result ,
						VipBuyMemberListBean.class );
				if ( data != null && data.goods.size( ) > 0 )
				{
					Message msg = Message.obtain( );
					msg.what = SHOW_DATA;
					msg.obj = data;
					mHandler.sendMessage( msg );
					
				}
				else
				{
					Message msg = Message.obtain( );
					msg.what = GET_DATA_FAIL;
					mHandler.sendMessage( msg );
				}
			}
			else
			{
				Message msg = Message.obtain( );
				msg.what = GET_DATA_FAIL;
				mHandler.sendMessage( msg );
			}
		}
		super.onGeneralSuccess( result , flag );
	}
	
	
	private Handler mHandler = new Handler( Looper.getMainLooper( ) )
	{
		@Override
		public void handleMessage( Message msg )
		{
			super.handleMessage( msg );
			switch ( msg.what )
			{
				case SHOW_DATA :
					bean = ( VipBuyMemberListBean ) msg.obj;
					refreshView( );
					break;
				
				case GET_DATA_FAIL :
					hideProgressDialog( );
					break;
			}
		}
	};
	
	
	/**
	 * 更新页面
	 */
	protected void refreshView( )
	{
		// banner栏是否显示
		
		
		bannerView.setVisibility( View.GONE );
		
		handleQueryFail( );
	}
	
	/**
	 * 大陆地区，处理数据
	 */
	private void handleChinaData( )
	{
		hideProgressDialog( );
		
		for ( Goods i : bean.goods )
		{
			i.price = "¥" + i.price;
		}
		mList = bean.goods;
		GridAdapter.notifyDataSetChanged( );
		
	}
	
	/**
	 * 非大陆地区，处理数据，需要连接google，查询该商品转换成对应汇率的价格
	 */
	private void handleOverseasData( )
	{
		try
		{
			initGoogleConnect( );
		}
		catch ( Exception e )
		{
			handleQueryFail( );
			e.printStackTrace( );
		}
	}
	
	
	/**
	 * 连接谷歌，查询商品列表
	 */
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
					handleQueryFail( );
					// Toast.makeText( VipBuyMemberActivity.this , "安装IAB失败" , 1
					// ).show( );
					return;
				}
				
				// 查询商品列表(异步)
				skuArray = new ArrayList< String >( );
				for ( Goods goods : bean.goods )
				{
					skuArray.add( goods.goodsid );
				}
				mHelper.queryInventoryAsync( true , skuArray , mQueryFinishedListener );
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
				handleQueryFail( );
				// Toast.makeText( VipBuyMemberActivity.this , "查询商品失败" , 1
				// ).show( );
				return;
			}
			
			for ( int i = 0 ; i < skuArray.size( ) ; i++ )
			{
				String SKU = skuArray.get( i );
				try
				{
					if ( inventory.getSkuDetails( SKU ) != null )
					{
						updateProductData( inventory.getSkuDetails( SKU ).getPrice( ) , SKU ,
								i );
					}
					else
					{
						CommonFunction.log( "" , "--->SKU RETURNED NULL" + SKU );
					}
				}
				catch ( Exception e )
				{
					e.printStackTrace( );
				}
			}
		}
	};
	
	/**
	 * 从谷歌获取到转换后的价格 更新页面
	 * 
	 * @param price
	 * @param SKU
	 */
	protected void updateProductData(String price , String SKU , int i )
	{
		for ( Goods goods : bean.goods )
		{
			if ( goods.goodsid == SKU )
			{
				goods.price = price;
			}
		}
		
		if ( i == skuArray.size( ) - 1 )
		{
			hideProgressDialog( );
			mList = bean.goods;
			GridAdapter.notifyDataSetChanged( );
			
			// 将获取到的数据缓存
		}
	}
	
	/**
	 * 连接google查询商品价格失败
	 */
	private void handleQueryFail( )
	{
		hideProgressDialog( );
		
		for ( Goods i : bean.goods )
		{
			i.price = "NT$" + i.price;
		}
		mList = bean.goods;
		GridAdapter.notifyDataSetChanged( );
		
		
	}
	
	@Override
	public void onClick( View v )
	{
		switch ( v.getId( ) )
		{
			case R.id.title_back :
				finish( );
				break;
			
			case R.id.header_image :
				if ( !CommonFunction.isEmptyOrNullStr( bean.url ) )
				{
					Uri uri = Uri.parse( bean.url );
					Intent i = new Intent( mContext , WebViewAvtivity.class );
					i.putExtra( WebViewAvtivity.WEBVIEW_TITLE , "" );
					i.putExtra( WebViewAvtivity.WEBVIEW_URL , uri.toString( ) );
					startActivity( i );
				}
				break;
			case R.id.foot_left_text2 :// 遇见会员协议
				jumpWebViewActivity(1);
				break;
			case R.id.foot_right_text :// 遇见充值帮助
				jumpWebViewActivity(2);
				break;
		}
	}
	
	/**
	 * 跳转到webview 
	 * @param type（1-遇见会员协议  2-遇见充值帮助）
	 */
	private void jumpWebViewActivity(int type){
		String str = "";
		String url = "";
		if (type == 1) {
			str = getString( R.string.vip_protocol );
			url = CommonFunction.getLangText( mContext, Config.VIP_AGREEMENT_URL );
		}else {
			url = CommonFunction.getLangText( mContext, Config.iAroundPayFAQUrl );
			str = getResources().getString(R.string.common_questions);
		}
		Intent intent = new Intent( mContext , WebViewAvtivity.class );
		intent.putExtra( WebViewAvtivity.WEBVIEW_TITLE , str );
		intent.putExtra( WebViewAvtivity.WEBVIEW_URL , url );
		startActivity( intent );
	}
	protected void onActivityResult( int requestCode , int resultCode , Intent data )
	{
		if ( requestCode == RESQUESTCODE )
		{
			switch ( resultCode )
			{
				case Activity.RESULT_OK : // 支付成功
					DialogUtil.showOKDialog( mActivity , R.string.pay_complete ,
							R.string.pay_complete_des , new View.OnClickListener( )
							{
								
								@Override
								public void onClick( View v )
								{
									finish( Activity.RESULT_OK );
								}
								
							} );
					break;
				case Activity.RESULT_CANCELED : // 取消支付
					Toast.makeText( mActivity ,
							mActivity.getString( R.string.pay_paypal_cancel ) ,
							Toast.LENGTH_SHORT ).show( );
					break;
				case PayGoodsList.RESULT_FAILURE : // 支付失败
					DialogUtil.showOKDialog( mActivity , R.string.pay_error ,
							R.string.pay_error_des , null );
					break;
				case PayGoodsList.RESULT_NO_DES : // 支付失败，无需提示
					
					break;
			}
		}
	}

    public void finish( int resutcode )
	{
		Intent intent = new Intent( );
		setResult( resutcode , intent );
		super.finish( );
	}
	
	// protected void onActivityResult( int requestCode , int resultCode ,
	// Intent data )
	// {
	// if ( requestCode == PAY_REQUESTCODE )
	// {
	// if ( resultCode == Activity.RESULT_OK ){// 支付成功
	// String Str = String.format( getResString( R.string.vip_buy_success ) ,
	// currentMonth );
	// CommonFunction.toastMsg( mContext , Str );
	// finish( );
	// }
	// }
	// };
	
	@Override
	protected void onDestroy( )
	{
//		EventBuffer.getInstant( ).uploadEvent( mContext , 0 );//gh
		
		super.onDestroy( );
	}
	
	
	
	// 显示加载框
	private void showProgressDialog( )
	{
		if ( mProgressDialog == null )
		{
			mProgressDialog = DialogUtil.showProgressDialog( mContext , "" ,
					getString( R.string.please_wait ) , null );
		}
		
		mProgressDialog.show( );
	}
	
	// 隐藏加载框
	private void hideProgressDialog( )
	{
		if ( mProgressDialog != null && mProgressDialog.isShowing( ) )
		{
			mProgressDialog.cancel( );
		}
	}
	
	class ViewHolder
	{
		TextView time;
		TextView price;
	}
	
}
