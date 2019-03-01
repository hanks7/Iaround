
package net.iaround.connector.protocol;


import android.content.Context;

import net.iaround.connector.ConnectionException;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;

import java.util.LinkedHashMap;


/**
 * 支付模块协议接口
 * 
 * @author linyg
 * @date 2012-08-25
 * 
 */
public class PayHttpProtocol extends HttpProtocol
{
	
	public static long payPost(Context context , String url ,
							   LinkedHashMap< String , Object > map , HttpCallBack callback )
	{
		return ConnectorManage.getInstance( context ).asynPost( url , map ,
				ConnectorManage.HTTP_PAY , callback );
	}
	
	
	/**
	 * 生成支付宝钱包订单
	 * 
	 * @version v2.5.0
	 * @param context
	 * @param goodsid
	 *            商品id
	 * @return
	 */
	public static long alipaywalletTrade(Context context , String goodsid ,
										 HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "goodsid" , goodsid );
		return payPost( context , "/alipaywallet/trade" , map , callback );
	}
	
	/**
	 * 生成支付宝订单
	 * 
	 * @version v2.5.0
	 * @param context
	 * @param goodsid
	 *            商品id
	 * @return
	 */
	public static long alipayTrade(Context context , String goodsid , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "goodsid" , goodsid );
		return payPost( context , "/alipay/trade" , map , callback );
	}
	
//	public static long amazonTrade( Context context , String goodsid , HttpCallBack callback )
//	{
//		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
//		map.put( "goodsid" , goodsid );
//		return payPost( context , "/amazon/trade" , map , callback );
//	}
//
//	public static long amazonNotify( Context context , String orderid , String amazonuserid ,
//			String purchasetoken , HttpCallBack callback )
//	{
//		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
//		map.put( "orderid" , orderid );
//		map.put( "amazonuserid" , amazonuserid );
//		map.put( "purchasetoken" , purchasetoken );
//		return payPost( context , "/amazon/notify" , map , callback );
//	}
	
	/**
	 * 获取googlePay订单Id
	 * 
	 * @version v2.5.0
	 * @param context
	 * @param goodsid
	 * @return
	 */
	public static long googleTrade(Context context , String goodsid , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "goodsid" , goodsid );
		return payPost( context , "/google/trade" , map , callback );
	}
	
	/**
	 * 验证订单号
	 * 
	 * @version v2.5.0
	 * @param context
	 * @param signeddata
	 * @param signature
	 * @return
	 */
	public static long googleNotify(Context context , String signeddata , String signature ,
									HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "signeddata" , signeddata );
		map.put( "googlesignature" , signature );
		return payPost( context , "/google/notify" , map , callback );
	}
	
	/**
	 * 神州行支付订单请求
	 * 
	 * @param context
	 * @param goodsid
	 *            商品id
	 * @param paymoney
	 *            支付额度 单位为‘分’
	 * @param privatefield
	 *            私有数据
	 * @param cardinfo
	 *            卡号信息 原始数据格式：神州行充值卡面 额[单位：元]@神州行充值卡序列号@神州行充值卡密码 ,公钥加密后上传
	 * @return
	 */
	public static long szxPayOrder(Context context , String goodsid , int paymoney ,
								   String privatefield , String cardinfo , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "goodsid" , goodsid );
		map.put( "paymoney" , paymoney );
		map.put( "privatefield" , privatefield );
		map.put( "cardinfo" , cardinfo );
		return payPost( context , "/szf/trade" , map , callback );
	}
	
	/**
	 * 生成KT
	 * 
	 * @version v2.5.0
	 * @param context
	 * @param goodsid
	 *            商品id
	 * @return
	 */
//	public static long ktTrade( Context context , String goodsid , HttpCallBack callback )
//	{
//		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
//		map.put( "goodsid" , goodsid );
//		return payPost( context , "/koreakt/trade" , map , callback );
//	}
//
	/**
	 * 回调Kt支付
	 * 
	 * @version v2.5.0
	 * @param context
	 * @param orderid
	 *            订单id
	 * @param trid
	 *            KT返回的交易号
	 * @throws ConnectionException
	 */
//	public static String ktNotify( Context context , String orderid , String trid )
//	{
//		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
//		map.put( "orderid" , orderid );
//		map.put( "trid" , trid );
//		return ConnectorManage.getInstance( context ).syncPost( "/koreakt/notify" , map ,
//				ConnectorManage.HTTP_PAY );
//	}
//
	/**
	 * 银联订单生成
	 * 
	 * @param context
	 * @param goodsid
	 * @return
	 */
	public static long unionPayOrder(Context context , String goodsid , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "goodsid" , goodsid );
		return payPost( context , "/unionpay/trade" , map , callback );
	}
	
	/**
	 * 台湾大哥大订单生成
	 * 
	 * @param context
	 * @param goodsid
	 * @return
	 */
//	public static long twmPayOrder( Context context , String goodsid , HttpCallBack callback )
//	{
//		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
//		map.put( "goodsid" , goodsid );
//		return payPost( context , "/twwtxm/trade" , map , callback );
//	}
//
	/**
	 * 将台湾大哥大的交易号提交
	 * 
	 * @param context
	 * @param orderid
	 * @param trid
	 * @return
	 */
//	public static long twmPayNotify( Context context , String orderid , String trid ,
//			HttpCallBack callback )
//	{
//		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
//		map.put( "orderid" , orderid );
//		map.put( "trid" , trid );
//		return payPost( context , "/twwtxm/notify" , map , callback );
//	}
	
	/**
	 * 将台湾大哥大的交易号提交
	 * 
	 * @param context
	 * @param orderid
	 * @param trid
	 * @return
	 * @throws ConnectionException
	 */
	public static String sysTwmPayNotify(Context context , String orderid , String trid )
			throws ConnectionException
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "orderid" , orderid );
		map.put( "trid" , trid );
		return ConnectorManage.getInstance( context ).syncPost( "/twwtxm/notify" , map ,
				ConnectorManage.HTTP_PAY );
	}
	
	/**
	 * 财付通订单生成
	 * 
	 * @param context
	 * @param goodsid
	 * @return
	 */
//	public static long tenpayOrder( Context context , String goodsid , HttpCallBack callback )
//	{
//		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
//		map.put( "goodsid" , goodsid );
//		return payPost( context , "/tenpay/trade" , map , callback );
//	}
//
	
	/**
	 * 1、微信支付生成订单
	 * 
	 * @param context
	 * @param goodsid
	 * @return
	 */
	public static long wechatOrder(Context context , String goodsid , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "goodsid" , goodsid );
		return payPost( context , "/wechat/trade" , map , callback );
	}
	
	/**
	 * 台湾Mycard支付订单生成
	 * 
	 * @param context
	 * @param goodsid
	 * @return
	 */
	public static long twmycardOrder(Context context , String goodsid , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "goodsid" , goodsid );
		return payPost( context , "/twmycard/trade" , map , callback );
	}
	
	/**
	 * 台湾Mycard支付订单
	 * 
	 * @param context
	 * @param goodsid
	 * @return
	 */
	public static long twmycardCardInfo(Context context , String cardinfo ,
										HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "cardinfo" , cardinfo );
		return payPost( context , "/twmycard/pointtrade" , map , callback );
	}
	
	/**
	 * 台湾Gash支付订单生成
	 * 
	 * @param context
	 * @param goodsid
	 * @return
	 */
	public static long twGashOrder(Context context , String goodsid , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "goodsid" , goodsid );
		return payPost( context , "/gash/trade" , map , callback );
	}
	
	/**
	 * 产生支付宝Wap订单
	 * 
	 * @param context
	 * @param goodsid
	 * @param cashiercode
	 *            卡类别(为空表示默认，1表示银行储蓄卡，2表示银行信用卡) 字符串
	 * @return
	 */
	public static long alipayWapOrder(Context context , String goodsid , String cashiercode ,
									  HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "goodsid" , goodsid );
		map.put( "cashiercode" , cashiercode );
		return payPost( context , "/alipay/web/trade" , map , callback );
	}
	
	/**
	 * 产生财付通Wap订单
	 * 
	 * @param context
	 * @param goodsid
	 * @return
	 */
	public static long tenpayWapOrder(Context context , String goodsid , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "goodsid" , goodsid );
		return payPost( context , "/tenpay/web/trade" , map , callback );
	}
	
	/**
	 * 获取子渠道列表
	 * 
	 * @param context
	 * @param plat
	 *            平台
	 * @param appversion
	 *            版本
	 * @param paywayid
	 *            渠道id
	 * @param language
	 *            语言
	 * @return
	 */
	public static long paywaysChildlist(Context context , int plat , String appversion ,
										int paywayid , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "plat" , plat );
		map.put( "appversion" , appversion );
		map.put( "paywayid" , paywayid );
		return payPost( context , "/payways/childlist" , map , callback );
	}
	
	/**
	 * 订单状态查询
	 * 
	 * @param ordernumber
	 *            订单号
	 * @return
	 */
	public static long payResultQuery(Context context , String ordernumber ,
									  HttpCallBack callback )
	{
		
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "ordernumber" , ordernumber );
		return payPost( context , "/order/query" , map , callback );
	}
}
