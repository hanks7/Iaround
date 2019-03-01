package net.iaround.model.entity;

import java.io.Serializable;

/** 附近筛选条件数据实体类 */
public class NearbyFilterConditionsInfo implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6362815328367940269L;

	/** 最近登录时间：统一换算成分钟。 0表示不限 */
	public int logintime; 
	
	/** 性别： 2：女，1：男，0:全部 */
	public int gender;
	
	/** 最小年龄 */
	public int minage;
	
	/** 最大年龄 */ 
	public int maxage; 
	
	/** 星座 */ 
	public int constellation; 
	
	/** 职业 */ 
	public int profession; 
	
	/** 情感状态 */ 
	public int love;
	
	/** 掌握语言 */ 
	public int dialects; 
	
	/** 出生地 */ 
	public String hometown;
	
	/** 距离，单位m（2公里之外） */
	public int distance;
	
	public void setFilterConditionsInfo(int logintime , int gender , int minage ,
                                        int maxage , int constellation , int profession , int love , int dialects ,
                                        String hometown , int distance )
	{
		this.logintime = logintime;
		this.gender = gender;
		this.minage = minage;
		this.maxage = maxage;
		this.constellation = constellation;
		this.profession = profession;
		this.love = love;
		this.dialects = dialects;
		this.hometown = hometown;
		this.distance = distance;
	}
}
