package net.iaround.model.entity;

import net.iaround.model.im.BaseServerBean;

import java.util.ArrayList;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-11-26 下午3:38:51
 * @Description: 点赞列表的返回(圈话题,贴吧,动态)
 */
public class GreetListBack extends BaseServerBean {

	public long dynamicid;//动态id
	public long groupid;//圈id
	public long postbarid;//贴吧id

	public long topicid;//话题id(圈子/贴吧共用, 动态不需要)
	
	public int pageno;//页码
	public int pagesize;//每页的数量
	public int amount;//总共条数
	public byte curruserlove;//当前用户是否点赞(0-否,1-是)
	private ArrayList<GreetListItemBean> likeusers;//点赞用户列表[圈话题部分使用,贴吧使用]
	
	/** 返回点赞用户列表 */
	public ArrayList<GreetListItemBean> getLikeuserList()
	{
		return likeusers == null ? new ArrayList<GreetListItemBean>() : likeusers;
	}
}
