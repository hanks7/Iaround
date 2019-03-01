package net.iaround.ui.postbar.bean;

import android.text.TextUtils;

import java.util.ArrayList;


/**
 * @author KevinSu kevinsu917@126.com
 * @create 2015年4月2日 下午6:28:19
 * @Description: 推荐侃啦资源实体
 */
public class ResourcePostbar {
	
	public int isfan;//是否粉丝(0-否, 1-是)
	public int fansnum;//粉丝数
	public ArrayList<PostbarBean> postbarList;//推荐侃啦的list
	
	public class PostbarBean{
		public int postbarid;//贴吧id
		private String postbarname;//贴吧名称
		private String icon;//贴吧图标
		
		/** 获取侃啦的名字 */
		public String getPostbarName()
		{
			if(TextUtils.isEmpty(postbarname)){
				return "";
			}
			return postbarname;
		}
		
		/** 获取侃啦的Icon */
		public String getPostbarIcon()
		{
			if(TextUtils.isEmpty(icon)){
				return "";
			}
			return icon;
		}
		
	}
}
