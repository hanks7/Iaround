package net.iaround.pay.bean;

import net.iaround.model.im.BaseServerBean;

import java.io.Serializable;


public class PayWechatOrderBean extends BaseServerBean implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2215229584722798712L;
	/**
	 * 
	 */
	public String appid;  //
	public String partnerid;    //商户ID
	public String ordernumber ;//订单ID
	public String noncestr; //随机字符串
	public String prepayid ;//预支付ID
	public String timestamp; //时间戳
	public String packagevalue; //订单详情
	public String sign ;//签名
	
}
