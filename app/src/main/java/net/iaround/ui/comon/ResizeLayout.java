
package net.iaround.ui.comon;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;


/**
 * 用于捕获屏幕大小改变的布局
 * 
 * @author chenlb
 * 
 */
public class ResizeLayout extends LinearLayout
{
	private OnResizeListener mListener;
	
	public interface OnResizeListener
	{
		void OnResize(int w, int h, int oldw, int oldh);
	}
	
	public void setOnResizeListener( OnResizeListener l )
	{
		mListener = l;
	}
	
	public ResizeLayout(Context context , AttributeSet attrs )
	{
		super( context , attrs );
	}
	
	@Override
	protected void onSizeChanged( int w , int h , int oldw , int oldh )
	{
		super.onSizeChanged( w , h , oldw , oldh );
		
		if ( mListener != null )
		{
			mListener.OnResize( w , h , oldw , oldh );
		}
	}
}
