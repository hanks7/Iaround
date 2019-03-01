
package net.iaround.ui.group.bean;


import net.iaround.model.im.BaseServerBean;

import java.util.ArrayList;


public class GroupMemberSearchBean extends BaseServerBean
{
	
	/** 页数 */
	public int pageno;
	
	/** 总页数 */
	public int pagesize;
	
	/** 总条数 */
	public int amount;
	
	/** 时间标识 */
	public long time;
	
	public ArrayList< GroupSearchUser > users;
	
	public ArrayList< GroupSearchUser > fans;
	
	/** 关注列表 */
	public ArrayList< GroupSearchUser > attentions;
	
}
