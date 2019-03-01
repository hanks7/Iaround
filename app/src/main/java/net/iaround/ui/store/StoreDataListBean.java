
package net.iaround.ui.store;


import android.text.TextUtils;

import net.iaround.model.im.BaseServerBean;

import java.util.ArrayList;


/**
 * 6.0新增 商店首页数据
 * 
 * @author Administrator zhengst
 *
 */
public class StoreDataListBean extends BaseServerBean
{
	public int goldnum;
	public int diamondnum;
	public int ratio;//30个金币兑换一个金币
	public int discount;//钻石直购，兑换金币折扣80%
	public ArrayList< Categorys > categorys;
	public ArrayList< Sections > sections;
	public ArrayList< GiftsBags > giftbags;
	
	
	/**
	 * 礼物分类
	 * 
	 * @author zhengst
	 *
	 */
	public class Categorys
	{
		public String name;// 类别名称
		public String icon;// 类别图标
		public int categoryid;
		public String categoryicon;//类别图标
	}
	
	/**
	 * 栏目
	 * 
	 * @author zhengst
	 *
	 */
	public class Sections
	{

		public  int type;
		public ArrayList< Gifts > gifts;
		public String title;

		public ArrayList< GiftsBags > giftbags; //礼包
		
		public ArrayList< Gifts > getGiftList( )
		{
			if ( gifts == null )
				gifts = new ArrayList< Gifts >( );
			return gifts;
		}
		
		
//		public class Gifts
//		{
//			public int giftid;
//			public String name;
//			public String icon;
//			public int goldnum;
//			public int charmnum;
//			public int expvalue;
//			public int viptype;//0普通1VIP
//			public int currencytype;//1金币2钻石
//			public String color;
//			public String giftdesc;
//			public String categoryname;
//			public long startTime;
//			public long endTime;
//			public int ishot;//0：不是，1：是
//			public int isnew;//0：不是，1：是
//			public String discountgoldnum;//折扣价
//			public int flag1;//图标1
//			public int flag2;//图标2
//
//		}
	}
	
	
	/**
	 * 大礼包
	 * @author Administrator
	 *
	 */
	public class GiftsBags
	{
		public int giftbagid;// 大礼包的id
		private String giftbagname;// 大礼包名称
		private String imageurl;// 大礼包图片url
		public int currencytype;
		public int price;
		

		
		/** 获取大礼包名称 */
		public String getGiftBagName( )
		{
			if ( TextUtils.isEmpty( giftbagname ) )
			{
				return "";
			}
			return giftbagname;
		}
		
		/** 获取大礼包图片的Url */
		public String getImageUrl( )
		{
			if ( TextUtils.isEmpty( imageurl ) )
			{
				return "";
			}
			return imageurl;
		}
	}

	public  Sections newSections()
	{
		return new Sections();
	}
	
}
