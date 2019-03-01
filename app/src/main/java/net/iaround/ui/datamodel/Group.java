
package net.iaround.ui.datamodel;


import android.content.Context;
import android.text.TextUtils;

import com.google.gson.annotations.Expose;

import java.io.Serializable;


/**
 * @ClassName: Group
 * @Description: 圈子的实体类
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-12-14 上午11:16:25
 * 
 */
public class Group implements Serializable
{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7137132845177414772L;

	/** 圈子id */
	public String id;
	
	/** 圈子名称 */
	public String name;
	
	/** 圈图标地址 */
	public String icon;
	
	/** 圈等级 */
	public int level;
	
	/** 位置纬度 */
	public int lat;
	
	/** 位置经度 */
	public int lng;
	
	/** 聊天室人数 */
	public long usercount;
	
	/** 范围（单位：km） */
	public double rang;
	
	/** 金币数 */
	public String gold;
	
	/** 话题数 */
	public int topiccount;
	
	/** 当前用户在该圈中的角色（0创建者；1管理员；2圈成员；3非圈成员） */
	public int grouprole = -1;
	
	/** 圈子简介 */
	public String content;
	
	/** 分类id */
	public int categoryid;
	/** 分类id 5.0新增 */
	public int newcategoryid;
	
	/** 分类名称 */
	public String category;
	
	/** 分类Icon的url地址 */
	public String categoryicon;
	
	/** 地址 */
	public String address;
	
	/** 历史最大范围 */
	public double oldmaxrange;
	
	/** 圈中心id */
	public String landmarkid;
	
	/** 圈中心名称 */
	public String landmarkname;
	
	/** 创建时间 （单位：毫秒） */
	public long datetime;
	
	/** 群热度标示(0：无标识，1：new ,2:hot) */
	public int flag;
	
	/** 1系统图标；0自定义图标 */
	public int systemiconflag;
	
	/** 圈子创建者信息 */
	public GroupUser user;
	
	/** 圈子里的最新话题 */
	public GroupTopic topic;
	
	/** 是否有新消息，此状态通过socket下发进行更新 */
	public boolean isHaveNewMsg = false;
	
	/** 1、添加圈子；2、没加入任何圈子 */
	@Expose( serialize = false , deserialize = false )
	public int itemType = 0;
	
	/** 数据类型（显示用，1为头部，2为内容） */
	@Expose( serialize = false , deserialize = false )
	public int contentType = 2;
	
	/** 圈子成员上限 */
	public int maxcount;
	
	/** 1-小圈，2-万人圈，3-十万人圈 */
	public int classify;
	
	/** 是否显示分界线( 1为显示,  0为隐藏 ) */
	public int isShowDivider;
	
	/** 是否选中，选中为1，未选中为0 */
	public int isselect;
	
	/**
	 * @Title: getGroupDesc
	 * @Description: 获取圈介绍显示内容
	 * @param context
	 * @return
	 */
	public String getGroupDesc(Context context )
	{
		if ( content == null || TextUtils.isEmpty( content ) )
		{
			return "";//context.getString( R.string.not_any_group_desc );
		}
		else
		{
			return content;
		}
	}
	
}
