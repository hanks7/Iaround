package net.iaround.model.im;

import java.io.Serializable;

/** 用户关系链 */
public class UserRelationLink implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5985588854128230968L;

	/**  关系类型
	 * (0:自己 ，1：好友 ，2：陌生人 
	 * 3:关注， 4:粉丝， 5:推荐)
	 * */
	public int relation;
	
	/** 当前用户与中间节点关系来源 */
	public int curmidcontact;
	
	/** 目标用户与中间节点关系来源 */
	public int tarmidcontact;
	
	/** 中间节点 */
	public MiddleNode middle;
	
	/** 隐身设置
	 * （1-已对TA隐身 2-TA看不到我的动态
	 *  3-不看TA的动态 4-黑名单） 
	 *  */
	public InvisibleOption option;
	
	/** 当前用户与中间节点关系来源 */
	public MiddleNode curmiddle;
	
	/** 目标用户与中间节点关系来源 */
	public MiddleNode tarmiddle;
	
	public class MiddleNode implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -4687537184195205766L;

		/** 关系来源 */
		public int contact;
		
		/** 中间节点id */
		public int id;
		
		/** 中间节点名称 */
		public String name;
		
		/** 头像 */
		public String icon;
	}
	
	public class InvisibleOption implements Serializable
	{

		/**
		 * 
		 */
		public static final long serialVersionUID = 5905044039596252671L;
		
		/** 已对TA隐身(y对其隐身，n不隐身) */
		public String invisible;
		
		/** 看不到我的动态(y看到我的动态，n看不到我的动态) */
		public String showmydynamic;
		
		/** 不看TA的动态(y看他（她）的动态，n不看他（她）的动态) */
		public String showdynamic;
		
		/** 黑名单(y已列入黑名单，n未列入黑名单) */
		public String blacklisted;
	}
}
