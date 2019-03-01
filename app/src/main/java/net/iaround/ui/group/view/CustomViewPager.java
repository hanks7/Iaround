
package net.iaround.ui.group.view;


import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;


/**
 * @ClassName: CustomViewPager
 * @Description: 自定义ViewPager（可设置是否允许滑动）
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-12-25 下午7:23:59
 * 
 */
public class CustomViewPager extends ViewPager {
	
	private boolean isCanFlip = true;
	
	public CustomViewPager(Context context )
	{
		super( context );
	}
	
	public CustomViewPager(Context context , AttributeSet attrs )
	{
		super( context , attrs );
	}
	
	/**
	 * @Title: setIsCanFlip
	 * @Description: 是否可以手指滑动
	 * @param flip
	 */
	public void setIsCanFlip( boolean flip )
	{
		this.isCanFlip = flip;
	}
	
	@Override
	public boolean onTouchEvent( MotionEvent arg0 ) {
		if ( !isCanFlip ) {
			return false;
		}
		return super.onTouchEvent( arg0 );
	}
	
	@Override
	public boolean onInterceptTouchEvent( MotionEvent arg0 ) {
		if ( !isCanFlip ) {
			return false;
		}
		return super.onInterceptTouchEvent( arg0 );
	}
	
}
