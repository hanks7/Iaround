
package net.iaround.ui.group.bean;


import net.iaround.ui.datamodel.BaseUserInfo;

/**
 * @Description: 单条圈通知实体，圈通知表查询返回实体
 * @author tanzy
 * @date 2015-4-7
 */
public class GroupNoticeBean
{
	public long groupid;
	public String groupname;
	public String icon;
	public String category;// 圈类型：英|简|繁
	public int type;// 1：新成员申请加入，2：同意加入，3：拒绝加入，4：用户退出，5：管理员移除成员，6：设置管理员，7取消管理员（其中1、2、3为互斥事件）
	public long datetime;
	public BaseUserInfo targetuser;
	public BaseUserInfo dealuser;
}
