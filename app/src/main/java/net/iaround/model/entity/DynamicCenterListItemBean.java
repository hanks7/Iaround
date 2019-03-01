package net.iaround.model.entity;
/**
 * @author KevinSu kevinsu917@126.com
 * @create 2015年4月11日 上午11:28:56
 * @Description: 动态中心列表单项的实体
 */
public class DynamicCenterListItemBean {

	public final static int IMAGE_TEXT = 1;//图文
	public final static int RESOURCE_BANNER = 3;//广告资源
	public final static int TOTOAL_NOTICE = 4;//共多少条的标示

	public int itemType;//类型 是图文,分享or广告资源
	/**
	 * 根据类型可以强转成不同的类<br>
	 * 图文和分享的实体是:@DynamicItemBean<br>
	 * 广告资源的实体是:@ResourceItemBean<br>
	 */
	public Object itemBean;
}
