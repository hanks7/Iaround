
package net.iaround.ui.group.bean;


import com.google.gson.annotations.Expose;


/**
 * @ClassName: GroupType
 * @Description: 圈子类型
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-12-7 下午5:56:31
 * 
 */
public class GroupType
{
	
	/** 类型id */
	public int id;
	/** 图标 */
	public String icon;
	/** 类型名称 */
	public String name;
	
	/** 是否选中（显示用） */
	@Expose( deserialize = false , serialize = false )
	public boolean isSelect = false;
	
	
}
