package net.iaround.ui.dynamic.bean;

import net.iaround.model.entity.DynamicReviewItem;
import net.iaround.model.im.BaseServerBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-9-25 上午9:54:08
 * @Description: 动态中评论的实体
 */
public class DynamicReviewInfo extends BaseServerBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2648755488367583549L;
	public int total;//评论总数
	public ArrayList<DynamicReviewItem> reviews;//评论列表
	
	public DynamicReviewInfo() {
		reviews = new ArrayList<DynamicReviewItem>();
	}
}
