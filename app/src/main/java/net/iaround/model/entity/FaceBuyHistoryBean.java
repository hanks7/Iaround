
package net.iaround.model.entity;


import net.iaround.model.im.BaseServerBean;

import java.util.ArrayList;


public class FaceBuyHistoryBean extends BaseServerBean
{
	public ArrayList< FaceBuy > history;
	
	public class FaceBuy
	{
		/** 贴图ID */
		public int pkgid;
		/** 贴图名称 */
		public String name;
		/** 图标 */
		public String icon;
		/** 是否动态图(1是 0否) */
		public int dynamic;
		/** 货币类型(1金币2钻石) */
		public int currencytype;
		/** 免费类型(1免费2 VIP免费 3收费4参加活动5限时免费 6打折) */
		public int feetype;
		/** 原价 */
		public String oldgoldnum;
		/** 现价 */
		public String goldnum;
		/** VIP价格 */
		public int vipgoldnum;
		/** 购买时间 */
		public long buytime;
		/** 使用期限 */
		public int expires;
		/** 下载地址 */
		public String downurl;
		
		
		public String format_time;
		public String getFormatTime( )
		{
			return format_time;
		}
		
		
		public void setFormatTime( String format_time )
		{
			this.format_time = format_time;
		}
	}
	
}
