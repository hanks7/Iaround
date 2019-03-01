package net.iaround.model.entity;

import net.iaround.ui.datamodel.BaseUserInfo;
import net.iaround.ui.datamodel.ResourceBanner;

import java.util.ArrayList;

/**
 * Created by 施丰雄 on 2016/4/11.
 * email 119535453@qq.com
 */
public class NearByExtendUser
{
	/** 资源类型，1表示侃啦，2表示推荐好友，3表示banner，4表示焦点用户（6.5.1版本添加） 5 广点通广告 6、inmobi广告*/
	public int type;

	public NearByUser user;

	public int distance;

	public ArrayList< RankData > rankList; // 排行列表，默认会下发10个，按limit把后面的不显示

	/** 推荐的侃啦 */
	public ArrayList< NearbyPostbarExtendInfo > postbars;

	/** 推荐的好友 */
	public ArrayList< NearByUser > recommends;

	/** 活动Banner */
	public ResourceBanner banner;

	//附近的人所创建的聊吧或加入的聊吧
	private int gid;
	private String gname;
	private String gicon;

	public class NearByUser extends BaseUserInfo
	{
		private static final long serialVersionUID = 7635127122654526974L;

		/**
		 * @Title: getSex
		 * @Description: 获取性别的int值（1男，2女）
		 * @return
		 */
		public int getSex( )
		{
			if ( gender != null )
			{
				return gender.equals( "m" ) ? 1 : 2;
			}
			else
			{
				return 0;
			}
		}
	}

	public class NearbyPostbarExtendInfo
	{
		public NearbyPostbarInfo postbar;

		/** 是否粉丝，即是否关注（0-否，1-是） */
		public int isfan;

		/** 粉丝数 */
		public int fansnum;
	}


	public class NearbyPostbarInfo
	{
		/** 贴吧id */
		public long postbarid;

		/** 贴吧名称 */
		public String postbarname;

		/** 贴吧图标 */
		public String icon;

		/** 0表示没有，1表示显示新标签，2表示显示热标签 */
		public int flag;

		/** 介绍 */
		public String desc;
	}

	public int getGid() {
		return gid;
	}

	public void setGid(int gid) {
		this.gid = gid;
	}

	public String getGname() {
		return gname;
	}

	public void setGname(String gname) {
		this.gname = gname;
	}

	public String getGicon() {
		return gicon;
	}

	public void setGicon(String gicon) {
		this.gicon = gicon;
	}

}