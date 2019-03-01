package net.iaround.ui.group.bean;

import net.iaround.model.im.BaseServerBean;

import java.util.ArrayList;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-9-30 下午4:55:10
 * @Description: 获取用户动态评论列表的返回 
 * @相关业务 /user/dynamic/reviews/list 5.5
 */
public class GroupTopicCommentListBack extends BaseServerBean{

	public int pageno;//第几页
	public int pagesize;//每一页数量
	public int amount;//评论总数
	public long dynamicid;//动态id
	public ArrayList<TopicReviewBasicInfo> reviews;//评论列表
}
