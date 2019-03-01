package net.iaround.ui.dynamic.bean;

import net.iaround.ui.datamodel.UserInfo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-11-24 上午11:49:53
 * @Description: 分享来自哪里的实体
 */
public class ShareInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3021816366227522557L;
	public static final int FROM_POSTBAR = 1;//来自贴吧
	public static final int FROM_GROUP = 2;//来自圈子
	public static final int FROM_DYNAMIC = 3;//来自动态
	
	public int sharesource;//(1-分享来自贴吧, 2-分享来自圈话题)
	public long sharevalue;//(贴吧的id, 2-分享来自圈话题)
	private String sharename;//(贴吧的名称, 圈子的名称)
	private String shareusername;//分享者的名字
	public int amount;//成员总数
	public ArrayList<UserInfo> members;//成员列表
	
	/** 获取(贴吧的名称, 圈子的名称) */
	public String getShareName()
	{
		return sharename == null ? "" : sharename;
	}
	
	/** 获取分享者的名字 */
	public String getShareUserName()
	{
		return shareusername == null ? "" : shareusername;
	}
	
	public ArrayList<UserInfo> getMembers()
	{
		return members == null ? new ArrayList<UserInfo>() : members;
	}
}
