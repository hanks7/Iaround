
package net.iaround.ui.group.bean;


import net.iaround.model.im.BaseServerBean;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * @Description: 获取圈消息接收状态列表的数据实体，且用于缓存成文件
 * @author tanzy
 * @date 2015-4-24
 */
public class GroupsMsgStatusBean extends BaseServerBean implements Serializable
{
	
	private static final long serialVersionUID = 4527802444527614974L;
	
	public int assistant;// 是否开启圈助手（0-关，1-开）
	public ArrayList< GroupMsgInfo > groups;
}
