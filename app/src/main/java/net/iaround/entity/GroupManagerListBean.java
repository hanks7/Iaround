package net.iaround.entity;

import net.iaround.model.im.BaseServerBean;

/**
 * @ClassName GroupManagerListBean.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-5-12 下午4:31:16
 * @Description: 获取群管理员ID列表
 * @相关协议 /group/manager/list
 */

public class GroupManagerListBean extends BaseServerBean
{
	public String groupownerid;//圈子所有者
	public String userids;//用,间隔用户id，例子："12345,54253,3423532,56321,235235"
}
