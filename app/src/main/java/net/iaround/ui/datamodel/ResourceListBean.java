package net.iaround.ui.datamodel;

import net.iaround.model.im.BaseServerBean;
import net.iaround.ui.postbar.bean.ResourcePostbar;

import java.util.ArrayList;

/**
 * @author KevinSu kevinsu917@126.com
 * @create 2015年4月2日 下午5:22:09
 * @Description:/resource/list, @BusinessHttpProtocol.getResourceList 资源列表
 */
public class ResourceListBean extends BaseServerBean {

	public static final int RESOURCE_TYPE_POST_BAR = 1;
	public static final int RESOURCE_TYPE_RECOMMEND_USER = 2;
	public static final int RESOURCE_TYPE_BANNER = 3;
	public static final int RESOURCE_TYPE_THRID_AD = 5;

	public ArrayList<ResourceItemBean> resources;
	public ArrayList<ResourceBanner> topbanners;
	
	public class ResourceItemBean {

		public int type;// 1表示侃啦，2表示推荐好友，3表示banner，5 第三方广告(根据type对应下面的资源list才不会空)
		public int pageposition;//列表展示的位置
		public ArrayList<ResourcePostbar> postbars;
		public ArrayList<ResourceRecommend> recommends;
		public ResourceBanner banner;
	}
}
