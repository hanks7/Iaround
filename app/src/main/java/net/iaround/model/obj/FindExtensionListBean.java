
package net.iaround.model.obj;


import net.iaround.model.im.BaseServerBean;

import java.util.ArrayList;


public class FindExtensionListBean extends BaseServerBean
{
	public ArrayList< Recompostbars > recompostbars;
	public ArrayList< ExtensionItem > discoverylist;
	
	/**
	 * 推广活动
	 * @author zhengst
	 *
	 */
	public class ExtensionItem
	{
		public String title;
		public String icon;
		public String content;
		public String url;
		public int isnew;
		public int clickid;
		public int share;
		public String titleColor;
	}
	
	/**
	 * 推荐的侃啦
	 * @author zhengst
	 *
	 */
	public class Recompostbars
	{
		public Postbar postbar;
		public int isfan;
		public int fansnum;
	}
	
	public class Postbar
	{
		public String postbarname;
		public String icon;
		public int postbarid;
	}
	
	
}
