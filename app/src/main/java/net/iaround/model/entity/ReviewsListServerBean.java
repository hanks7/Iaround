
package net.iaround.model.entity;


import net.iaround.model.im.BaseServerBean;
import net.iaround.ui.datamodel.BaseUserInfo;

import java.util.ArrayList;


/**
 * @Description:/user/dynamic/message/mylist 和 /user/dynamic/message/history
 *                                           的协议返回数据类型
 * 相关接口: /user/dynamic/message/likelist
 * @author tanzy
 * @date 2014-9-28
 */
public class ReviewsListServerBean extends BaseServerBean
{
	public int pageno;
	public int pagesize;
	public int amount;
	public ArrayList< ReviewsItem > msgs;
	
	public class ReviewsItem
	{
		public ReviewsItemDynamic dynamic;
		public ReviewsItemMsg msg;
	}
	
	public class ReviewsItemDynamic
	{
		public long dynamicid;
		public String content;
		public String image;
	}
	
	public class ReviewsItemMsg
	{
		public ReviewsItemMsgUser user;
		public int type;//1-评论，2-点赞
		public long datetime;
		public String content;
	}
	
	public class ReviewsItemMsgUser extends BaseUserInfo
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 4350289913244180630L;
		
	}
}
