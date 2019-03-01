
package net.iaround.ui.view.dynamic;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;


public class GrapeGridview extends GridView
{
	
	public GrapeGridview(Context context )
	{
		super( context );
	}
	
	public GrapeGridview(Context context , AttributeSet attrs )
	{
		super( context , attrs );
	}
	
	// 通过重新dispatchTouchEvent方法来禁止滑动
	@Override
	public boolean dispatchTouchEvent( MotionEvent ev )
	{
		if ( ev.getAction( ) == MotionEvent.ACTION_MOVE )
		{
			return true;// 禁止Gridview进行滑动
		}
		return super.dispatchTouchEvent( ev );
	}
}
