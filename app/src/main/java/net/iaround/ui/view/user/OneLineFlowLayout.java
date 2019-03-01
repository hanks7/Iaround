package net.iaround.ui.view.user;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class OneLineFlowLayout extends ViewGroup {

	private ArrayList<View> oneLineViews = null;	// 一行View
	private int horizotalSpacing = 6;
	private int verticalSpacing = 6;

	public OneLineFlowLayout(Context context) {
		super(context);
	}

	public OneLineFlowLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public OneLineFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	/**
	 * 测量FlowLayout及它的子View
	 * @param widthMeasureSpec 父容器希望FlowLayout的宽
	 * @param heightMeasureSpec 父容器希望FlowLayout的高
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 获取FlowLayout测量的宽
		int parentMeasuredWidth = MeasureSpec.getSize(widthMeasureSpec);
		
		// 遍历所有的子View，测量所有的子View
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);	// 获取子View
			// 把测量规格传给子View，让子View完成测量
			child.measure(MeasureSpec.makeMeasureSpec(parentMeasuredWidth, MeasureSpec.AT_MOST), 0);
			
			//  获取子View测量的宽
			int childMeasuredWidth = child.getMeasuredWidth();
			
			// 如果是第0个需要创建新行，或者当前View的宽度大于一行中剩余的可用宽度也需要创建新行
			if (i == 0) {
				oneLineViews = new ArrayList<View>();
			}
			if(childMeasuredWidth > parentMeasuredWidth
					- getPaddingLeft()
					- getPaddingRight()
					- getLineWidth(oneLineViews)){
				break;  //如果超出一行(目的是只让布局显示一行)，直接结束循环
			}
			oneLineViews.add(child);
		}
		
		// 设置FlowLayout的宽和高，宽就用父容器传的宽，高用所有行的高
		setMeasuredDimension(parentMeasuredWidth, getLineHeight() + getPaddingTop() + getPaddingBottom());
	}
	
	/** 获取所有行的高 */
	private int getLineHeight() {

		if(oneLineViews==null ||oneLineViews.size()<0){
			return 0;
		} else {
			return  oneLineViews.get(0).getMeasuredHeight();
		}
	}
	
	/** 获取行宽 */
	private int getLineWidth(ArrayList<View> views) {
		int lineWidth = 0;
		for (int i = 0; i < views.size(); i++) {
			lineWidth = lineWidth + views.get(i).getMeasuredWidth();
		}
		int totalSpacing = (views.size() - 1) * horizotalSpacing;
		return lineWidth + totalSpacing;
	}

	// 对FlowLayout的子View进行排版
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		int usableWidth = getMeasuredWidth() - getPaddingLeft()
				- getPaddingRight()
				- getLineWidth(oneLineViews);

		int preColumnRight = 0;
		for (int j = 0; j < oneLineViews.size(); j++) {
			View column = oneLineViews.get(j);	// 获取一行中的某个子View

			// 获取子View的宽和高
			int childWidth = column.getMeasuredWidth();
			int childHeight = column.getMeasuredHeight();

			// 设置子View在容器中的位置
			int childLeft;
			if (j == 0) {
				// 如果是第一列，则它的left位置要加上paddingLeft
				childLeft 	= getPaddingLeft() + preColumnRight;	// 显示到上一列右边的位置
			} else {
				// 如果不是第一列，则它的left需要加上spacing
				childLeft 	= preColumnRight + horizotalSpacing;	// 显示到上一列右边的位置
			}

			int childTop = getPaddingTop();
			int childRight 	= childLeft + childWidth;
			int childBottom = childTop + childHeight;
			column.layout(childLeft, childTop, childRight, childBottom);

			// view的宽高改变了，需要重新测量。如果不重新测量的话宽度显示是没有问题的，但是文本内容水平方向不会居中
			int widthMeasureSpec = MeasureSpec.makeMeasureSpec(childRight - childLeft, MeasureSpec.EXACTLY);
			int heightMeasureSpec = MeasureSpec.makeMeasureSpec(childBottom - childTop, MeasureSpec.EXACTLY);
			column.measure(widthMeasureSpec, heightMeasureSpec);

			// 保存当前列的right坐标，用于下次使用
			preColumnRight = childRight;
		}
	}

}
