
package net.iaround.ui.friend.bean;


import net.iaround.model.im.BaseServerBean;
import net.iaround.ui.datamodel.BaseUserInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * @Description: /friends/get_2_4 协议返回类型
 * @author tanzy
 * @date 2015-3-31
 */
public class FriendUsersBean extends BaseServerBean
{
	public ArrayList< UserAndDistance > users;
	public List<UserAndDistance> fans;
	public int amount;
	public int pageno;
	public int pagesize;

	
	public class UserAndDistance
	{
		public BaseUserInfo user;
		public int distance;
		public int relation;
		public int newflag;
	}
	
}
