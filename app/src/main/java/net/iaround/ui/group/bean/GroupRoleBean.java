
package net.iaround.ui.group.bean;


import net.iaround.model.im.BaseServerBean;

/**
 * @ClassName: GroupRoleBean
 * @Description: 圈子权限
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-12-10 下午4:00:11
 * 
 */
public class GroupRoleBean extends BaseServerBean
{
	
	/** 发言权限1仅圈主和管理员，0所有 */
	public int talk;
	
	/** 发表话题1仅圈子和管理员，0所有 */
	public int publishtopic;
	
	/** 加入圈子是否需审核1要审核 0无需审核 */
	public int joincheck;
	
	/** 当前用户在圈子的权限 */
	public int grouprole;
	
	/** 非成员是否可查看圈话题(1-可以、0-不可以) */
	public int viewtopic;
	
	/** 是否开启只能通过圈号搜索到本圈(1-开、0-关) */
	public int issearch;
	
	/** 年度服务到期时间，长整型，只有圈主可见 */
	public long expires;
	
	/** 圈子id */
	public String id;
}
