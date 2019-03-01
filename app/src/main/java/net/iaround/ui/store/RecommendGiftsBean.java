package net.iaround.ui.store;

import net.iaround.model.im.BaseServerBean;

import java.util.ArrayList;

public class RecommendGiftsBean extends BaseServerBean {

	/**兑换比例  钻石:人民币=1:0.1*/
	public double rmbrate;
	/**兑换比例  钻石:新台币=1:0.1*/
	public double xtbrate;
	public ArrayList<recommendGifts> gifts;
	
	public class recommendGifts{
		
		public int giftid;
		public String name;
		public String icon;
		public int goldnum;
		public int charmnum; 
		public int expvalue;
		public int currencytype;// 1金币商品　2为钻石商品
		public int viptype;//是否是vip礼物
		/**折扣价 */
		public String discountgoldnum;
	}
}
