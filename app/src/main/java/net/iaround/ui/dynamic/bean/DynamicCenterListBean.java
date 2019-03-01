package net.iaround.ui.dynamic.bean;

import net.iaround.model.im.BaseServerBean;

import java.util.ArrayList;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-9-24 下午8:01:27
 * @Description: 动态中心的列表实体 相关业务：/dynamic/list
 */
public class DynamicCenterListBean extends BaseServerBean {

	public int pagesize;//每页的总数
	public byte hasnext;//是否还有下一页（0-否，1-有）
	public long last;//最后一条动态的下标
	public int amount;//动态数量
	public int pageno;//页数
	public ArrayList<DynamicItemBean> dynamics;//动态详情
	
	/**
	 * 是否还有下一页
	 * @return true 是
	 */
	public boolean isHasNext()
	{
		return hasnext == 1;
	}
}
