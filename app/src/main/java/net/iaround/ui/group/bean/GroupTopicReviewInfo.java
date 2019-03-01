package net.iaround.ui.group.bean;


import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-9-25 上午9:54:08
 * @Description: 动态中评论的实体
 */
public class GroupTopicReviewInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4479320714794523316L;
	public int total;//评论总数
	public ArrayList<TopicReviewBasicInfo> reviews;//评论列表
	
	public GroupTopicReviewInfo() {
		// TODO Auto-generated constructor stub
		reviews = new ArrayList<TopicReviewBasicInfo>();
	}
}
