package net.iaround.ui.postbar.bean;


import net.iaround.ui.group.bean.PublishBaseBean;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-11-11 下午3:51:35
 * @Description: 动态发布实体类
 */
public class PostBarPublishBean extends PublishBaseBean {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1554401804375495974L;
	public long postbarid;
	public long topicid;
	public int type;//类型， 1.图文 2.分享 @DynamicType
	
}
