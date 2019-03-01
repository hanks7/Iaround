
package net.iaround.model.im;


import net.iaround.ui.datamodel.BaseUserInfo;
import net.iaround.ui.friend.bean.NewFansBean;

import java.util.ArrayList;


/**
 * @Description: 81027 新增粉丝推送的消息实体
 * @author tanzy
 * @date 2015-4-16
 */
public class NewFansListBean extends BaseServerBean
{
	public int total;
	public int addnewfans;
	public BaseUserInfo fan;
	public ArrayList< NewFansBean > fans;
}
