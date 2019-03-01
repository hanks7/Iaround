package net.iaround.ui.dynamic.bean;

import android.text.TextUtils;

import net.iaround.model.im.BaseServerBean;

import java.io.Serializable;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-9-24 下午5:06:33
 * @Description: 获取我发布的最新动态 /dynamic/new
 */
public class DynamicMineNewBean extends BaseServerBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 353306032287030325L;
	public int total;// 动态数量
	public NewDynamic newdynamic;// 最新动态

	public DynamicMineNewBean() {
		// TODO Auto-generated constructor stub
		newdynamic = new NewDynamic();
	}

	public class NewDynamic implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7170559308944023484L;
		public String content;// 动态内容
		public String image;// 图片url
	}

	
	
	/**设置最新动态的内容 */
	public void setNewDynamicContent(String value) {
		if (newdynamic == null) {
			newdynamic = new NewDynamic();
		}
		newdynamic.content = value;
	}

	/**获取最新动态的内容 */
	public String getNewDynamicContent()
	{
		if(newdynamic == null)
		{
			newdynamic = new NewDynamic();
		}
		return TextUtils.isEmpty(newdynamic.content) ? "" : newdynamic.content;
	}
	
	
	/**设置最新动态的图片 */
	public void setNewDynamicImage(String value) {
		if (newdynamic == null) {
			newdynamic = new NewDynamic();
		}
		newdynamic.image = value;
	}

	/**获取最新动态的图片 */
	public String getNewDynamicImage()
	{
		if(newdynamic == null)
		{
			newdynamic = new NewDynamic();
		}
		return TextUtils.isEmpty(newdynamic.image) ? "" : newdynamic.image;
	}
}
