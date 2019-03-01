package net.iaround.model.im;

import com.tencent.connect.UserInfo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-11-24 上午11:23:34
 * @Description: 同步来自或者同步到哪里的实体
 */
public class SyncInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4253988212826770074L;
	public static final int FROM_POSTBAR = 1;//来自贴吧
	public static final int FROM_GROUP = 2;//来自圈子
	public static final int FROM_DYNAMIC = 3;//来自动态
	
	public int synctype;//(1-同步到, 2-同步来自)
	private ArrayList<SyncItemBean> list;//同步列表
	
	//设置同步列表
	public void setSyncList(ArrayList<SyncItemBean> value){
		if(list == null)
		{
			list = new ArrayList<SyncItemBean>();
		}

		list.clear();
		list.addAll(value);
	}

	//获取同步列表
	public ArrayList<SyncItemBean> getSyncList()
	{
		if(list == null)
		{
			list = new ArrayList<SyncItemBean>();
		}
		return list;
	}
	
	public class SyncItemBean
	{
		public int sync;//(1-贴吧, 2-圈子, 3-动态)
		public long syncvalue;//(贴吧的id,圈子id,或者动态用户的id)
		private String syncname;//(贴吧名字, 圈子名字, 动态用户的名字)
		public int amount;//成员总数
		private ArrayList<net.iaround.ui.datamodel.UserInfo> members;//成员列表
		
		//设置同步的名字
		public void setSyncName(String value)
		{
			syncname = value;
		}
		
		//获取同步名字
		public String getSyncName()
		{
			return syncname == null ? "" : syncname;
		}
		
		//设置成员列表
		public void setMemberList(ArrayList<net.iaround.ui.datamodel.UserInfo> value)
		{
			if(members == null)
			{
				members = new ArrayList<net.iaround.ui.datamodel.UserInfo>();
			}
			members.clear();
			members.addAll(value);
		}
		
		//获取成员列表
		public ArrayList<net.iaround.ui.datamodel.UserInfo> getMemberList()
		{
			if(members == null)
			{
				members = new ArrayList<net.iaround.ui.datamodel.UserInfo>();
			}
			
			return members;
		}
	}
	
	//判断是否同步进来的
	public boolean isSyncIn()
	{
		return synctype == 2;
	}
	
//	public class SyncMember
//	{
//		public long id;//用户id
//		public String name;//用户名
//		public String icon;//用户头像
//	}
}
