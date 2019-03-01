
package net.iaround.ui.group.bean;


import net.iaround.model.im.BaseServerBean;

import java.util.ArrayList;


/**
 * @ClassName: GroupNearBean
 * @Description: 附近圈子的实体Bean
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-12-19 下午6:24:48
 * 
 */
public class GroupNearBean extends BaseServerBean
{
	/**
	 * 我的圈子数量
	 */
	public int groupnum;
	/** 当前页 */
	public int pageno;
	
	/** 每页个数 */
	public int pagesize;
	
	/** 总数 */
	public int amount;
	
	/** 用于分页标识的地标id */
	public String landmarkid;
	
	/** 是否显示广告 （1-显示，0-隐藏） */
	public int visible;
	
	/** 圈子列表 */
	public ArrayList< Landmark > landmarks;
	
	/** 7日内新建圈子列表 */
	public ArrayList< Group > activegroups;
	
	//推荐的圈子
	public ArrayList< Group > recommendgroups;
}
