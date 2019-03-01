package net.iaround.pay.bean;


import net.iaround.model.im.BaseServerBean;

public class PayResultBean extends BaseServerBean {
	public int goodstype;//商品类型：1钻石，2会员，需要补充一个 3 爱心  4  星星lyh
	public int diamondnum;//充值的数量  包括： 钻石 爱心  星星
	public long expiredtime; //会员到期时间
	public int senddiamond; //赠送钻石数量
}
