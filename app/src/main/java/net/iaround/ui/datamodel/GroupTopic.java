
package net.iaround.ui.datamodel;


import java.io.Serializable;


/**
 * @ClassName: GroupTopic
 * @Description: 圈话题的实体类
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-12-10 上午11:41:51
 * 
 */
public class GroupTopic implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5114970343539734674L;
	
	/** 圈话题的发起人 */
	public GroupUser user;
	
	/** 圈话题id */
	public long topicid;
	
	/** 圈话题内容 */
	public String content;
	
	/** 圈话题的图片 */
	public String url;
	
	/** 圈话题类型 */
	public int type;
	
	
}
