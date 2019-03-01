package net.iaround.model.im;


import net.iaround.ui.datamodel.User;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * @ClassName ContactsBean.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-7-9 下午4:30:16
 * @Description: 联系人，保存于文件
 */

public class ContactsBean implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5891724860322511690L;

	/** 好友数 */
	public int friendsnum;
	
	/** 关注数 */
	public int attentionsnum;
	
	/** 粉丝数 */
	public int fansnum;
	
	/** 圈子数 */
	public int groupnum;
	
	/** 好友列表 */
	public ArrayList<User> friends;
	
	/** 关注列表 */
	public ArrayList< User > attentions;
}
