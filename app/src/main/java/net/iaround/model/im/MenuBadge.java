
package net.iaround.model.im;


import android.widget.ImageView;

import net.iaround.ui.view.DragPointView;


/**
 * @ClassName: MenuBadge
 * @Description: 侧栏标示项
 * @author zhonglong kylin17@foxmail.com
 * @date 2014-2-14 上午11:33:23
 * 
 */
public class MenuBadge
{
	public enum ItemType
	{
		 Nearby,
		 DynamicCenter,
		 Message,
		 Find,
		
	}
	
//	public DragPointView badgeView;
	public DragPointView badgeView;

	public ImageView ivView;
	
	public ItemType itemType;
	public int badgeType = 0;
	
	public int badgeNumber =4;
	
//	public MenuBadge(DragPointView mBadgeView  , ItemType mItemType , ImageView ivView)
//	{
//		this.badgeView = mBadgeView;
//		this.itemType = mItemType;
//		this.ivView = ivView;
//	}
	public MenuBadge(DragPointView mBadgeView  , ItemType mItemType , ImageView ivView)
	{
		this.badgeView = mBadgeView;
		this.itemType = mItemType;
		this.ivView = ivView;
	}
}
