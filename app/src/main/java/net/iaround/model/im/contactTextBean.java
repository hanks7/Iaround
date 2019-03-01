
package net.iaround.model.im;


public class contactTextBean extends BaseServerBean
{
	public Texts texts;
	
	/** 导入权限URL */
	public String url;
	
   public class Texts{
		/** 弹窗文案标题 */
		public String poptitle;
		/** 弹窗文案内容 */
		public String popcontent;
		/** 批量隐身文案标题 */
		public String batchtitle;
		/** 批量隐身文案内容 */
		public String batchcontent;
		/** 主动导入通讯录文案标题 */
		public String importtitle;
		/** 导入通讯录文案内容 */
		public String importcontent;
		/** 导入通讯录失败文案标题 */
		public String failuretitle;
		
   }
}
