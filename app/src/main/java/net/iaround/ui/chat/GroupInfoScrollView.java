
package net.iaround.ui.chat;


import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;


public class GroupInfoScrollView extends ScrollView
{
	private View scaleView;
	
	private int imageHeight;
	private int imageWidth;
	private float rawY;
	private int perH; // 拉伸并松手后，每次缩小的高度值
	private boolean isLock = false; // 是否锁定滑动
	private Handler mHandler;
	
	public GroupInfoScrollView(Context context , AttributeSet attrs , int defStyle )
	{
		super( context , attrs , defStyle );
		init( );
	}
	
	public GroupInfoScrollView(Context context , AttributeSet attrs )
	{
		super( context , attrs );
		init( );
	}
	
	public GroupInfoScrollView( Context context )
	{
		super( context );
		init( );
	}
	
	private void init( )
	{
		mHandler = new Handler( );
	}
	
	public void initComponent( View view )
	{
		scaleView = view;
		if ( null == scaleView )
			return;
		imageWidth = scaleView.getMeasuredWidth( );
		imageHeight = scaleView.getMeasuredHeight( );
		
		Log.d( "################图片的宽高：" , "" + imageWidth + "--" + imageHeight );
	}
	
	private class ScaleImageRunnable implements Runnable
	{
		public ScaleImageRunnable( )
		{
		}
		
		@Override
		public void run( )
		{
			if ( null == scaleView )
				return;
			android.view.ViewGroup.LayoutParams params = scaleView.getLayoutParams( );
			int h = params.height;
			h -= perH;
			if ( h > imageHeight )
			{
				params.height = h;
				scaleView.setLayoutParams( params );
				scaleView.invalidate( );
				
				mHandler.postDelayed( new ScaleImageRunnable( ) , 1 );
			}
			else
			{
				h = imageHeight;
				
				params.height = h;
				scaleView.setLayoutParams( params );
				scaleView.invalidate( );
				
				isLock = false;
			}
		}
	}
	
	@Override
	public boolean onTouchEvent( MotionEvent ev )
	{
		super.onTouchEvent( ev );
		
		if ( null == scaleView )
			return true;
		switch ( ev.getAction( ) )
		{
			case MotionEvent.ACTION_UP :
				if ( isLock )
					break;
				
				isLock = true;
				android.view.ViewGroup.LayoutParams params1 = scaleView.getLayoutParams( );
				int h1 = params1.height;
				
				if ( h1 > imageHeight )
				{ // 缩小回原来大小
					perH = imageWidth / 40;
					
					mHandler.postDelayed( new ScaleImageRunnable( ) , 16 );
				}
				else
				{
					params1.height = imageHeight;
					scaleView.setLayoutParams( params1 );
					scaleView.invalidate( );
					
					isLock = false;
				}
				break;
			case MotionEvent.ACTION_DOWN :
				if ( isLock )
					break;
				
				rawY = ev.getRawY( );
				break;
			case MotionEvent.ACTION_MOVE :
				if ( isLock )
					break;
				
				if ( ev.getRawY( ) - rawY > 0 )
				{ // 向下滑动
					int offsetH = ( int ) ( ev.getRawY( ) - rawY );
					android.view.ViewGroup.LayoutParams params = scaleView.getLayoutParams( );
					
					int h = params.height;
					h += offsetH;
					if ( h > imageWidth )
						h = imageWidth; // 变更后的高度大于图片的宽度时，将高度的大小设置为跟宽度一样
						
					if ( this.getScrollY( ) < 10 )
					{ // 滑动到顶部时，才能出现拉伸效果
						params.height = h;
						if ( h > 0 )
						{
							scaleView.setLayoutParams( params );
							scaleView.invalidate( );
						}
					}
				}
				else
				/* if (ev.getRawY() - rawY < 0) */{ // 向上滑动
					int offsetH = ( int ) Math.abs( ev.getRawY( ) - rawY );
					android.view.ViewGroup.LayoutParams params = scaleView.getLayoutParams( );
					
					int h = params.height;
					h -= offsetH;
					if ( h < imageHeight )
						h = imageHeight; // 变更后的高度小于图片的最小高度时，将高度的大小设置为最小高度
						
					params.height = h;
					scaleView.setLayoutParams( params );
					scaleView.invalidate( );
				}
				
				rawY = ev.getRawY( );
				break;
		}
		
		return true;
	}
}
