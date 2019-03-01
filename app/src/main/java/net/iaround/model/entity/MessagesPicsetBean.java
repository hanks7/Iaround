
package net.iaround.model.entity;

import java.util.ArrayList;

/**
 * @Description:消息界面游戏中心消息体
 * @author tanzy
 * @date 2014-11-22
 */
public class MessagesPicsetBean
{
	public String title;
	public String picurl;
	public String link;
	public int width;
	public int height;
	public String description;
	public ArrayList< Data > datas;
	
	public class Data
	{
		public String picurl;
		public String title;
		public String link;
	}
}
