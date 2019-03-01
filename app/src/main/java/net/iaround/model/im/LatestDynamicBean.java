
package net.iaround.model.im;

import java.io.Serializable;

/**
 * 最新动态协议返回数据类型
 * */
public class LatestDynamicBean implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -364857011082082271L;
	
	/** 协议返回状态 */
	public int status;
	/** 最新动态 */
	public NewDynamicInnerBean newdynamic;
	/** 最近动态数 */
	public int total;
	
	
	public class NewDynamicInnerBean implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 9131918601377239517L;
		
		/** 动态内容 */
		public String content;
		/** 头像图片 */
		public String image;
	}
}
