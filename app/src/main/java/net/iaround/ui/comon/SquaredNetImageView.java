
package net.iaround.ui.comon;


import android.content.Context;
import android.util.AttributeSet;


/** 通过异步加载图片 */
public class SquaredNetImageView extends NetImageView
{
	
	public SquaredNetImageView(Context context )
	{
		super( context );
	}
	
	public SquaredNetImageView(Context context , AttributeSet attrs )
	{
		super( context , attrs );
	}
	
	public SquaredNetImageView(Context context , AttributeSet attrs , int defStyle )
	{
		super( context , attrs , defStyle );
	}
	
	@Override
	protected void onMeasure( int widthMeasureSpec , int heightMeasureSpec )
	{
		setMeasuredDimension( getDefaultSize( 0 , widthMeasureSpec ) ,
				getDefaultSize( 0 , heightMeasureSpec ) );
		
		int childWidthSize = getMeasuredWidth( );
		heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec( childWidthSize ,
				MeasureSpec.EXACTLY );
		super.onMeasure( widthMeasureSpec , heightMeasureSpec );
	}
	
}
