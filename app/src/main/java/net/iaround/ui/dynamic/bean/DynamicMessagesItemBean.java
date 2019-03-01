package net.iaround.ui.dynamic.bean;


import net.iaround.model.entity.ReviewsListServerBean.ReviewsItem;

import java.util.ArrayList;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2015-6-24 下午03:01:58
 * @Description: 动态消息列表中的每个Item实体
 */
public class DynamicMessagesItemBean {

	public static int HEAD_TYPE = 0;
	public static int CONTENT_TYPE = 1;
	public static int FOOTER_TYPE = 2;
	
	private int itemType;//显示的类型, 0:HeadView,1:contentView,2:footerView
	private ArrayList<ReviewsItem> greeterList;//当显示类型为HeadView的时候用
	private ReviewsItem reviewItem;//当显示类型为contentView的时候用
	private boolean bIsMore;//当显示类型为footerView的时候用,是否有更多内容
	private int itemCount;//当显示类型为footerView的时候用,当bIsMOre = false时候,显示数量用
	
	
	public int getItemType() {
		return itemType;
	}
	public void setItemType(int itemType) {
		this.itemType = itemType;
	}
	public ArrayList< ReviewsItem > getGreeterList() {
		return greeterList;
	}
	public void setGreeterList(ArrayList< ReviewsItem > greeterList) {
		this.greeterList = greeterList;
	}
	public ReviewsItem getReviewItem() {
		return reviewItem;
	}
	public void setReviewItem(ReviewsItem reviewItem) {
		this.reviewItem = reviewItem;
	}
	public boolean isHasMore() {
		return bIsMore;
	}
	public void setHasMore(boolean bIsMore) {
		this.bIsMore = bIsMore;
	}
	public int getItemCount() {
		return itemCount;
	}
	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}
	
}
