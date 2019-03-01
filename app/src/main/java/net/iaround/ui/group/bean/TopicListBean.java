
package net.iaround.ui.group.bean;


import net.iaround.model.im.BaseServerBean;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * @ClassName: TopicListBean
 * @Description: 话题列表实体bean
 * 
 */
public class TopicListBean extends BaseServerBean implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -987503802680084699L;
	public int amount;
	public int pageno ;//:1
	public int pagesize ;
	public ArrayList< TopicListContentBeen > topics;
	
}
