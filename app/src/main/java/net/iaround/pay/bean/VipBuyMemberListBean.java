
package net.iaround.pay.bean;


import net.iaround.model.im.BaseServerBean;

import java.util.ArrayList;

/**
 * vip购买会员列表
 * @author Administrator
 *
 */
public class VipBuyMemberListBean extends BaseServerBean
{
	public String icon; //图片
	public String url; //跳转URL

	public ArrayList< Goods > goods;// 购买会员列表

	public class Goods
	{
		public String goodsid;//商品编号
		public String price;//价格
		public int month;//时长 月
		public String discount; //描述
	}
	
	public int apple; //苹果商店
	public int alipay;//支付宝支付
	public int unionpayonline;//支付宝银联在线支付
	public int visa;//支付宝信用卡支付
	public int shenzhoufu;//神州付支付
	public int tenpay;//财付通支付
	public int unionpay;//银联支付
	public int mycard;//台湾Mycard支付
	public int gash; //台湾Gash支付
	public int google;//谷歌支付
	public int wechat;//微信支付
}
