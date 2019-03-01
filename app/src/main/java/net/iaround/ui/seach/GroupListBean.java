
package net.iaround.ui.seach;


import net.iaround.model.im.BaseServerBean;
import net.iaround.ui.group.bean.Group;

import java.util.ArrayList;


public class GroupListBean extends BaseServerBean
{
	
	/** 当前页 */
	public int pageno;
	
	/** 每页个数 */
	public int pagesize;
	
	/** 总数 */
	public int amount;
	
	/** 圈子列表 */
	public ArrayList< Group > groups;
	
}
