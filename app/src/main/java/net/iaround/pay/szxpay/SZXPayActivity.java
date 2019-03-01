
package net.iaround.pay.szxpay;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.protocol.PayHttpProtocol;
import net.iaround.pay.PayGoodsList;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.CryptoUtil;
import net.iaround.tools.DialogUtil;
import net.iaround.ui.activity.PayChannelListActivity;
import net.iaround.ui.comon.SuperActivity;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * 神州行支付
 * 
 */
public class SZXPayActivity extends SuperActivity implements OnClickListener
{
	public static String SZF_GOODSID = "szf_goodsid";
	public static String SZF_GOODSNAME = "szf_goodsname";
	public static String SZF_GOODSMONEY = "szf_goodsmoney";
	public static String SZF_SENDDIAMOND = "szf_senddiamond";
	private final int SZX_SUCCES = 1001;
	private final int SZX_FAIL = 1002;
	
	private String version = "3"; // 接口版本号
	private int verifyType = 1; // MD校验
	private int sendDiamond = 0; //送的钻石
	
	private String goodsId , goodsName;
//	private Spinner sp_card_money , sp_card_type;
	private EditText et_szx_card , et_szx_pass;
	private int goodsMoney;
	private Button btn_submit;
	private Dialog dialog;
	private long orderFlag = 0;
	
	private RelativeLayout[] payType = new RelativeLayout[3] ;
	
	private String payPrivatefield;
	private int cardTypeCombine=0; // 0移动 1联通 2电信
	
	private String[ ][ ] money_list =
		{
			{ "10元" , "20元" , "30元" , "50元" , "100元" } ,
			{ "20元" , "30元" , "50元" , "100元" } ,
			{ "20元" , "30元" , "50元" , "100元" } };
	private String szfurl = "http://pay3.shenzhoufu.com/interface/version3/serverconnszx/entry-noxml.aspx";
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.x_pay_szxpay );
		goodsId = getIntent( ).getStringExtra( SZF_GOODSID );
		goodsName = getIntent( ).getStringExtra( SZF_GOODSNAME );
		goodsMoney = ( int ) ( getIntent( ).getDoubleExtra( SZF_GOODSMONEY , 0 ) * 100 );
		
		sendDiamond = getIntent( ).getIntExtra( SZF_SENDDIAMOND , 0 ) ;
		
		payPrivatefield = String.valueOf( Common.getInstance( ).loginUser.getUid( ) );
		
		((TextView)findViewById( R.id.tv_title )).setText( R.string.szx_pay );
		findViewById( R.id.fl_back ).setOnClickListener( this );
		
		et_szx_pass = (EditText) findViewById( R.id.et_szx_pass );
		et_szx_card = (EditText) findViewById( R.id.et_szx_card );
		
		payType[0] = (RelativeLayout) findViewById( R.id.type_mobile );
		payType[1] = (RelativeLayout) findViewById( R.id.type_unicom );
		payType[2] = (RelativeLayout) findViewById( R.id.type_telecom );
		
		payType[0].setOnClickListener( this );
		payType[1].setOnClickListener( this );
		payType[2].setOnClickListener( this );
		payType[0].setEnabled( false );
		
		String goods = goodsMoney +"";
		if(sendDiamond> 0)
		{
			( (TextView) findViewById( R.id.good_name ) ).setText( String.format(
					getString( R.string.pay_szf_goods_send ) , goodsName,sendDiamond , goodsMoney / 100 )  );
		}
		else
		{
			( (TextView) findViewById( R.id.good_name ) ).setText( String.format(
					getString( R.string.pay_szf_goods_change ) , goodsName , goodsMoney / 100 )  );
		}
		
		
//		sp_card_type = ( Spinner ) findViewById( R.id.sp_card_type );
//		sp_card_money = ( Spinner ) findViewById( R.id.sp_card_money );
//		sp_card_money.setPrompt( mActivity.getResString( R.string.szx_card_money ) );
//		sp_card_type.setPrompt( mActivity.getResString( R.string.szx_card_type ) );
//		
//		ArrayAdapter< CharSequence > adapter = ArrayAdapter.createFromResource( this ,
//				R.array.szx_card_type , R.layout.simple_spinner_item_black );
//		adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
//		sp_card_type.setAdapter( adapter );
//		sp_card_type.setOnItemSelectedListener( new OnItemSelectedListener( )
//		{
//			@Override
//			public void onItemSelected( AdapterView< ? > arg0 , View arg1 , int index ,
//					long arg3 )
//			{
//				ArrayAdapter< String > adapter = new ArrayAdapter< String >( mActivity ,
//						R.layout.simple_spinner_item_black , money_list[ index ] );
//				adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
//				sp_card_money.setAdapter( adapter );
//			}
//			
//			@Override
//			public void onNothingSelected( AdapterView< ? > arg0 )
//			{
//				
//			}
//		} );
		
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
		else
		{
			if ( v == btn_submit )
			{
				String card_num = et_szx_card.getText( ).toString( ).trim( );
				String card_pass = et_szx_pass.getText( ).toString( ).trim( );
				if ( CommonFunction.isEmptyOrNullStr( card_num )
						|| CommonFunction.isEmptyOrNullStr( card_pass ) )
				{
					CommonFunction.showToast( mActivity ,
							mActivity.getString( R.string.szx_des6 ) , 0 );
					return;
				}
				dialog = DialogUtil.showProgressDialog( mActivity , "" ,
						mActivity.getResString( R.string.please_wait ) , null );
				// 组合金额
//				cardTypeCombine = sp_card_type.getSelectedItemPosition( );
//				CommonFunction.log( "cardTypeCombine" , "cardTypeCombine:" + cardTypeCombine );
//				int moneyPosition = sp_card_money.getSelectedItemPosition( );
//				String money = "";
//				try
//				{
//					money = ( money_list[ cardTypeCombine ][ moneyPosition ] ).replace( "元" ,
//							"" );
//				}
//				catch ( Exception e )
//				{
//					return;
//				}
				// 组合卡号信息
				int money = goodsMoney /100;
				String cardinfo = "";
				try
				{
					CommonFunction.log( "Pay" , money + "@" + card_num + "@" + card_pass );
					cardinfo = CryptoUtil.rsaEncrypt(
							money + "@" + card_num + "@" + card_pass ,
							CryptoUtil.SZF_PUBLIC_KEY );
					CommonFunction.log( "Pay" , "cardinfo:" + cardinfo );
				}
				catch ( Exception e )
				{
					e.printStackTrace( );
					return;
				}
				sendIaround( goodsId , goodsMoney , payPrivatefield , cardinfo );
			}
			else if(v.equals( payType[0] ))
			{
				payType[0].setEnabled( false );
				payType[1].setEnabled( true );
				payType[2].setEnabled( true );
				cardTypeCombine = 0;
			}
			else if(v.equals( payType[1] ))
			{
				payType[0].setEnabled( true );
				payType[1].setEnabled( false );
				payType[2].setEnabled( true );
				cardTypeCombine = 1;
			}
			else if(v.equals( payType[2] ))
			{
				payType[0].setEnabled( true );
				payType[1].setEnabled( true );
				payType[2].setEnabled( false );
				cardTypeCombine = 2;
			}
		}
	}
	
	private void sendIaround(String goodsid , int paymoney , String privatefield ,
                             String cardinfo )
	{
		orderFlag = PayHttpProtocol.szxPayOrder( mActivity , goodsId , paymoney ,
				privatefield , cardinfo , SZXPayActivity.this );
		if ( orderFlag == -1 )
		{
			mHandler.sendEmptyMessage( 2 );
		}
	}
	
	// 发送数据到神州行
	private void sendSZX(final String merid , final String orderid , final String md5String ,
                         final String returnUrl , final String merUserName , final String merUserMail ,
                         final String cardInfo )
	{
		String card = cardInfo;
		try
		{
			card = URLEncoder.encode( cardInfo , "utf-8" );
		}
		catch ( UnsupportedEncodingException e )
		{
		}
		String urlRequestData = szfurl + "?version=" + version + "&merId=" + merid
				+ "&payMoney=" + goodsMoney + "&orderId=" + orderid + "&returnUrl="
				+ returnUrl + "&cardInfo=" + card + "&merUserName=" + merUserName
				+ "&merUserMail=" + merUserMail + "&privateField=" + payPrivatefield
				+ "&verifyType=" + verifyType + "&cardTypeCombine=" + cardTypeCombine
				+ "&md5String=" + md5String + "&signString=";
		
		CommonFunction.log( "szx_pay" , urlRequestData );
		HttpRequest request = HttpRequest.getInstance( mActivity , urlRequestData );
		request.startAsynRequestString( new HttpRequest.OnRequestStringCallBack( )
		{
			@Override
			public void stringLoaded( String content )
			{
				Message msg = new Message( );
				msg.what = SZX_SUCCES;
				msg.obj = content;
				mHandler.sendMessage( msg );
			}
		} );
	}
	
	@Override
	public void onGeneralError( int e , long flag )
	{
		super.onGeneralError( e , flag );
		if ( flag == orderFlag )
		{
			mHandler.sendEmptyMessage( 2 );
		}
	}
	
	@Override
	public void onGeneralSuccess(String result , long flag )
	{
		super.onGeneralSuccess( result , flag );
		if ( flag == orderFlag )
		{
			Message msg = new Message( );
			msg.what = 1;
			msg.obj = result;
			mHandler.sendMessage( msg );
		}
	}
	
	private Handler mHandler = new Handler( )
	{
		public void handleMessage( final android.os.Message msg )
		{
			switch ( msg.what )
			{
				case 1 :
					try
					{
						String result = String.valueOf( msg.obj );
						JSONObject json = new JSONObject( result );
						if ( json.optInt( "status" ) == 200 )
						{

							String merid = CommonFunction.jsonOptString( json,"merid" );
							String orderid = CommonFunction.jsonOptString( json,"orderid" );
							String md5String = CommonFunction.jsonOptString(json, "md5string" );
							String returnUrl = CommonFunction.jsonOptString( json,"returnurl" );
							String merUserName = CommonFunction.jsonOptString(json, "merusername" );
							String merUserMail = CommonFunction.jsonOptString(json, "merusermail" );
							String cardInfo = CommonFunction.jsonOptString( json,"cardinfo" );
							// 接着神州行
							sendSZX( merid , orderid , md5String , returnUrl , merUserName ,
									merUserMail , cardInfo );
							PayChannelListActivity.mOrdernumber =orderid;
						}
						else
						{
							if ( dialog != null )
							{
								dialog.dismiss( );
							}
							if ( json.optInt( "error" ) != 0 )
							{
								ErrorCode.showError( mActivity , String.valueOf( msg.obj ) );
							}
						}
					}
					catch ( Exception e )
					{
						CommonFunction.log( "SZXpay" , e.getMessage( ) );
					}
					break;
				case 2 :
//					CommonFunction.showToast( mActivity ,
//							mActivity.getString( R.string.e_5901 ) , 1 );
					ErrorCode.toastError(mActivity, ErrorCode.E_5901);
					if ( dialog != null )
						dialog.dismiss( );
					break;
				case SZX_SUCCES : // 支付成功
					if ( dialog != null )
					{
						dialog.dismiss( );
					}

					if ( msg.obj != null )
					{
						String tips = "支付失败,请重试";
						String result = String.valueOf( msg.obj );
						if ( !CommonFunction.isEmptyOrNullStr( result ) )
						{
							final int pay_result = Integer.parseInt( result );
							CommonFunction.log( "SZXpay" , "result:" + pay_result );

							switch ( pay_result )
							{
								case 0 :
									tips = "网络连接失败";
									break;
								case 200 :
//									new UmengUtils( mActivity ).buyCoinsEvent( goodsMoney );//gh 统计取消掉
									tips = "订单请求成功,后台确认之后,将收到消息通知";
									break;
								case 104 :
									tips = "支付卡序号或密码有误，请确认";
									break;
								case 106 :
									tips = "系统繁忙，请稍后再试";
									break;
								case 107 :
									tips = "卡内余额不足";
									break;
								case 909 :
									tips = "该地方卡暂不支持";
									break;
								case 915 :
									tips = "支付卡卡面金额不正确，请确认";
									break;
							}

							DialogUtil.showOKDialog( mActivity , "支付提示" , tips ,
									new View.OnClickListener( )
									{
										
										@Override
										public void onClick( View v )
										{
											CommonFunction.log( "ddddddddddddd" ,
													"pay_result:" + pay_result );
											// 支付成功则返回
											try
											{
												if ( pay_result == 200 )
												{
													finish( PayGoodsList.RESULT_NO_DES );
												}
											}
											catch ( Exception e )
											{
												e.printStackTrace( );
											}
										}
										
									} );
						}
					}
					
					break;
				case SZX_FAIL : // 神舟行支付请求失败
					if ( dialog != null )
						dialog.dismiss( );
					CommonFunction.showToast( mActivity , "支付失败,请重试" , 1 );
					break;
			}
		}
    };
	
	public void finish( int resutcode )
	{
		Intent intent = new Intent( );
		setResult( resutcode , intent );
		super.finish( );
	}
	
	/**
	 * 101 = "md5验证失败"; 102 = "订单号重复"; 103 ="恶意用户"; 104 = "序列号，密码验证失败"; 105 =
	 * "密码正在处理中"; 106 = "系统繁忙，暂停提交"; 107 = "卡内余额不足"; 109 = "des解密失败"; 201 =
	 * "证书验证失败"; 501 = "插入数据库失败"; 200 = "请求成功（非订单支付成功）"; 902 = "商户参数不全"; 803 =
	 * "商户ID不存在"; 904 = "商户没有激活"; 905 = "商户没有使用该接口的权限"; 906 = "商户没有设置 密钥" 907 =
	 * "商户没有设置 DES"; 909 = "该笔订单不符合"; 910 = "服务器返回地址"; 911 = "订单号，不符合规范"; 912 =
	 * "非法订单"; 913 = "该地方卡暂不支持"; 914 = "支付金额非法"; 915 ="卡面金额非法"; 916
	 * ="商户不支持该充值卡"; 917 = "参数格式不正确"; 0="网络连接失败"
	 */
}
