
package net.iaround.pay;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.protocol.GoldHttpProtocol;
import net.iaround.pay.bean.Goods;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.datamodel.PayModel;
import net.iaround.utils.ImageViewUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * 支付方式列表
 * 
 * @author linyg
 * 
 */
public class PayGoodsList extends SuperActivity implements OnClickListener
{
	public static final String GOODSLIST_CHANNEL = "goodslist_channel";
	public static final String GOODSLIST_PAYLOAD = "goodslist_payload";
	public static final String GOODSLIST_TITLE = "goodslist_title";
	public static final String CHANNEL_TYPE = "channel_type";
	
	public static final int RESQUESTCODE = 101;
	public static final int RESULT_FAILURE = 3;
	public static final int RESULT_NO_DES = 4; // 无需描述
	
	private ImageView title_back;
	private TextView title_name;
	private PullToRefreshScrollView llEmpty;
	private PullToRefreshListView listview;
	private ArrayList<Goods> goodsList;
	private DataAdapter adapter;
	
	private long getGoodsFlag = 0;
	private final int MSG_SUCCES = 1001;
	private final int MSG_ERROR = 1002;
	private Handler mHandler = new MHandler( );
	private int channelId = 0;
	private String payload = "";
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.pay_goodslist );
		
		title_back = (ImageView) findViewById( R.id.title_back );
		title_name = (TextView) findViewById( R.id.title_name );
		title_back.setOnClickListener( this );
		
		channelId = getIntent( ).getIntExtra( GOODSLIST_CHANNEL , 0 );
		payload = getIntent( ).getStringExtra( GOODSLIST_PAYLOAD );
		
		if ( channelId < 1 )
		{
			finish( Activity.RESULT_CANCELED );
		}
		String title = getIntent( ).getStringExtra( GOODSLIST_TITLE );
		title_name.setText( title );
		
		llEmpty = (PullToRefreshScrollView) findViewById( R.id.llEmpty );
		llEmpty.getRefreshableView( ).setFillViewport( true );
		
		listview = (PullToRefreshListView) findViewById( R.id.message_list );
		listview.getRefreshableView( ).setSelector( R.drawable.listview_item_divider );
		
		goodsList = new ArrayList< Goods >( );
		
		listview.setOnRefreshListener( new OnRefreshListener< ListView >( )
		{
			
			@Override
			public void onRefresh( PullToRefreshBase< ListView > refreshView )
			{
				getGoodsFlag = GoldHttpProtocol.goodsList( mActivity , channelId ,
						PayGoodsList.this );
				if ( getGoodsFlag < 0 )
				{
					mHandler.sendEmptyMessage( MSG_ERROR );
				}
			}
		} );
		
		llEmpty.setOnRefreshListener( new OnRefreshListener< ScrollView >( )
		{
			
			@Override
			public void onRefresh( PullToRefreshBase< ScrollView > refreshView )
			{
				getGoodsFlag = GoldHttpProtocol.goodsList( mActivity , channelId ,
						PayGoodsList.this );
				if ( getGoodsFlag < 0 )
				{
					mHandler.sendEmptyMessage( MSG_ERROR );
				}
			}
		} );
		
		adapter = new DataAdapter( );
		listview.setAdapter( adapter );
		listview.setRefreshing( );
	}
	
	@Override
	public void onClick( View v )
	{
		if ( v == title_back )
		{
			finish( );
		}
	}
	
	private class DataAdapter extends BaseAdapter
	{
		
		@Override
		public int getCount( )
		{
			return goodsList.size( );
		}
		
		@Override
		public Goods getItem( int position )
		{
			return goodsList.get( position );
		}
		
		@Override
		public View getView(int position , View convertView , ViewGroup parent )
		{
			ItemHolder holder = null;
			if ( convertView == null )
			{
				holder = new ItemHolder( );
				convertView = mInflater.inflate( R.layout.x_paymian_goods_item , null );
				holder.icon= (ImageView) convertView.findViewById( R.id.diamond_icon );
				holder.all_money = (TextView) convertView.findViewById( R.id.diamond_num );
				holder.money_btn = (TextView) convertView.findViewById( R.id.price );
				convertView.setTag( holder );
			}
			else
			{
				holder = ( ItemHolder ) convertView.getTag( );
			}
			Goods goods = getItem( position );
			if ( goods != null )
			{
				
				holder.all_money.setText( String.valueOf( goods.getDiamondnum( ) ) );
				String unit = "";
				try
				{
					unit = goods.getSign( );
				}
				catch ( Exception e )
				{
					e.printStackTrace( );
				}
				
				holder.money_btn.setCompoundDrawables( null , null , null , null );
				
				holder.money_btn.setText( unit + " " + goods.getPrice( ) );
				
				ImageViewUtil.getDefault( ).fadeInLoadImageInConvertView( goods.getIcon() ,
						holder.icon , R.drawable.z_pay_diamond_default , R.drawable.z_pay_diamond_default ) ;
			}
			SkipPayListener skipListener = new SkipPayListener( goods );
			holder.money_btn.setOnClickListener( skipListener );
			convertView.setOnClickListener( skipListener );
			return convertView;
		}
		
		public void notifyDataSetChanged( )
		{
			// 是否为空
			if ( goodsList.size( ) < 1 )
			{
				showEmptyView( );
			}
			else
			{
				hideEmptyView( );
			}
			super.notifyDataSetChanged( );
		}
		
		@Override
		public long getItemId( int position )
		{
			return 0;
		}
	}
	
	// 跳转点击
	private class SkipPayListener implements OnClickListener
	{
		Goods goods = null;
		
		public SkipPayListener( Goods goods )
		{
			this.goods = goods;
		}
		
		@Override
		public void onClick( View v )
		{
			if ( goods != null )
			{
				skipToChannelPay( goods );
			}
		}
	}
	
	private class ItemHolder
	{
		private ImageView icon;
		private TextView all_money;
		private TextView money_btn;
	}
	
	// 跳转到各个渠道支付
	private void skipToChannelPay( Goods goods )
	{
		Intent intent = null;
		switch ( goods.getType( ) )
		{
			case ChannelType.ALIPAY :// 支付宝
//				intent = new Intent( mActivity , AlipayActivity.class );
//				intent.putExtra( AlipayActivity.ALIPAY_GOODSID , goods.getGoodsid( ) );
//				intent.putExtra( AlipayActivity.ALIPAY_MONEY , goods.getPrice( ) );
//				intent.putExtra( AlipayActivity.ALIPAY_CODE , payload );
				break;
			case ChannelType.SZF : // 神州付
//				intent = new Intent( mActivity , SZXPayActivity.class );
//				intent.putExtra( SZXPayActivity.SZF_GOODSID , goods.getGoodsid( ) );
//				intent.putExtra( SZXPayActivity.SZF_GOODSNAME ,
//						String.valueOf( goods.getDiamondnum( ) ) );
//				intent.putExtra( SZXPayActivity.SZF_GOODSMONEY , goods.getPrice( ) );
				break;
			case ChannelType.TENPAY :// 财付通
//				intent = new Intent( mActivity , TenpayActivity.class );
//				intent.putExtra( TenpayActivity.TENPAY_GOODSID , goods.getGoodsid( ) );
				break;
			case ChannelType.UNIONPAY : // 银联
//				intent = new Intent( mActivity , UnionpayActivity.class );
//				intent.putExtra( UnionpayActivity.UNIONPAY_GOODSID , goods.getGoodsid( ) );
				break;
			case ChannelType.MYCARD : // MyCARD
//				intent = new Intent( mActivity , MyCardDepositPayActivity.class );
//				intent.putExtra( MyCardDepositPayActivity.MYCARDPAY_GOODID ,
//						goods.getGoodsid( ) );
//				intent.putExtra( MyCardDepositPayActivity.MYCARDPAY_TYPE , 1 );
				break;
			case ChannelType.GASH : // Gash
//				intent = new Intent( mActivity , GashDepositPayActivity.class );
//				intent.putExtra( GashDepositPayActivity.GASHPAY_GOODID , goods.getGoodsid( ) );
				break;
			case ChannelType.GOOGLEPAY :
//				intent = new Intent( mActivity , GooglePayActivity.class );
//				intent.putExtra( GooglePayActivity.GOOGLEPAY_GOODSID , goods.getGoodsid( ) );
				break;
			case ChannelType.AMAZON : // Amazon
//				intent = new Intent( mActivity , AmazonPayActivity.class );
//				intent.putExtra( AmazonPayActivity.AMAZONPAY_GOODID , goods.getGoodsid( ) );
				break;
		}
		if ( intent != null )
		{
			mActivity.startActivityForResult( intent , RESQUESTCODE );
		}
	}
	
	@Override
	public void onGeneralError( int e , long flag )
	{
		if ( getGoodsFlag == flag )
		{
			mHandler.sendEmptyMessage( MSG_ERROR );
		}
		super.onGeneralError( e , flag );
	}
	
	@Override
	public void onGeneralSuccess(String result , long flag )
	{
		if ( getGoodsFlag == flag )
		{
			Message msg = new Message( );
			msg.what = MSG_SUCCES;
			msg.obj = result;
			mHandler.sendMessage( msg );
		}
		super.onGeneralSuccess( result , flag );
	}
	
	protected void onActivityResult( int requestCode , int resultCode , Intent data )
	{
		if ( requestCode > 100 )
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
				case RESULT_FAILURE : // 支付失败
					DialogUtil.showOKDialog( mActivity , R.string.pay_error ,
							R.string.pay_error_des , null );
					break;
				case RESULT_NO_DES : // 支付失败，无需提示
					
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
	
	private void showEmptyView( )
	{
		llEmpty.setVisibility( View.VISIBLE );
		listview.setVisibility( View.GONE );
	}
	
	private void hideEmptyView( )
	{
		llEmpty.setVisibility( View.GONE );
		listview.setVisibility( View.VISIBLE );
	}
	
	public void stopPulling( )
	{
		llEmpty.onRefreshComplete( );
		listview.onRefreshComplete( );
	}
	
	private class MHandler extends Handler
	{
		@Override
		public void handleMessage( Message msg )
		{
			System.out.println( msg );
			switch ( msg.what )
			{
				case MSG_SUCCES :
					stopPulling( );
					String result = String.valueOf( msg.obj );
					try
					{
						JSONObject json = new JSONObject( result );
						if ( json.optInt( "status" ) == 200 )
						{
							long goldnum = json.optLong( "goldnum" );
							long diamondnum = json.optLong( "diamondnum" );
							PayModel.getInstance( ).setGoldNum( goldnum );
							PayModel.getInstance( ).setDiamondNum( diamondnum );
							paramGoods( json );
						}
						else
						{
							ErrorCode.showError( mActivity , result );
						}
					}
					catch ( Exception e )
					{
						e.printStackTrace( );
					}
					adapter.notifyDataSetChanged( );
					break;
				case MSG_ERROR :
					stopPulling( );
					adapter.notifyDataSetChanged( );
					break;
			}
		}
	}
	
	// 解析商品列表
	private void paramGoods( JSONObject json )
	{
		JSONArray items = json.optJSONArray( "goods" );
		if ( items.length( ) > 0 )
		{
			goodsList.clear( );
		}
		for ( int i = 0 ; i < items.length( ) ; i++ )
		{
			JSONObject item = items.optJSONObject( i );
			Goods good = new Goods( );
			good.setType( item.optInt( "type" ) );
			good.setIcon( CommonFunction.jsonOptString(item, "icon" ) );
			good.setUnit( item.optInt( "unit" ) );
			good.setDiamondnum( item.optInt( "diamondnum" ) );
			good.setSign( CommonFunction.jsonOptString(item, "sign" ) );
			good.setGoodsid( CommonFunction.jsonOptString( item,"goodsid" ) );
			good.setAppendcontent( CommonFunction.jsonOptString(item, "appendcontent" ) );
			good.setAppendicon( CommonFunction.jsonOptString(item, "appendicon" ) );
			good.setPrice( item.optDouble( "price" ) );
			goodsList.add( good );
		}
	}

}
