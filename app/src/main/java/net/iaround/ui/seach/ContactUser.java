
package net.iaround.ui.seach;


import net.iaround.ui.datamodel.GroupUser;

/**
 * @ClassName: ContactUser
 * @Description: 用户的实体类
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-12-17 下午2:20:30
 * 
 */
public class ContactUser
{
	
	/** 用户基本信息 */
	public GroupUser user;
	
	/** 距离 */
	public int distance;
	
	/** 是否显示更多项（显示用） */
	public boolean isShowMore = false;
	
	/**关系来源*/
	public int contact;
	
	/** 更多的数量（显示用）仅isShowMore为true时有效 */
	public int moreCount;
	
}
