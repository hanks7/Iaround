package net.iaround.pay.bean;

import net.iaround.model.im.BaseServerBean;

import java.util.ArrayList;

/**
 * java钱包返回数据
 */

public class PayChannelBean extends BaseServerBean {
	public int gold;//金币
	public int love;
	public int diamond;
	public int star;

	public ArrayList< PayGoodsBean > loves;
	public ArrayList< PayGoodsBean > stars;
	public ArrayList< PayGoodsBean > goods;
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

	public double todayGameEarnings;//主播今日收益
	public int hasFirstLoveRecharge;//充值状态 充值过爱心:1   没充值过爱心:0
	public int coinShowType;  //1:只展示钻石 2:只展示爱心 3:全部展示
	
}
