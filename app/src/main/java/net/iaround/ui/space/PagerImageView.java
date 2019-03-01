
package net.iaround.ui.space;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;

import net.iaround.tools.picture.PhotoView;
import net.iaround.tools.picture.PhotoViewAttacher;


public class PagerImageView extends PhotoView
{
	private Matrix matrix;
	private OnMatrixChangedListener listener;
	private ScaledImageClickListener clickListener;
	private int dragScrollMinDistSquare;
	
	
	public PagerImageView(Context context )
	{
		super( context );
		init( context );
	}
	
	public PagerImageView(Context context , AttributeSet attrs )
	{
		super( context , attrs );
		init( context );
	}
	
	public PagerImageView(Context context , AttributeSet attrs , int defStyle )
	{
		super( context , attrs , defStyle );
		init( context );
	}
	
	private void init( Context context )
	{
		ViewConfiguration configuration = ViewConfiguration.get( context );
		dragScrollMinDistSquare = configuration.getScaledTouchSlop( );
		dragScrollMinDistSquare *= dragScrollMinDistSquare;
		setOnViewTapListener( new PhotoViewAttacher.OnViewTapListener( )
		{
			@Override
			public void onViewTap(View view , float x , float y )
			{
				// TODO Auto-generated method stub
				if ( clickListener != null )
				{
					clickListener.onScaledClick( PagerImageView.this );
				}
			}
		} ) ;
		
	}
	
	
	
	/**
	 * 设置图片
	 * 
	 * @param bm
	 */
	public void setBitmap( Bitmap bm )
	{
		setImageBitmap( bm );
		matrix = new Matrix( );
		matrix.set( getImageMatrix( ) );
		if ( listener != null )
		{
			listener.onMactrixChage( matrix );
		}
		setImageMatrix( matrix );
	}
	
	public void setOnMatrixChangedListener( OnMatrixChangedListener l )
	{
		listener = l;
		if ( matrix != null )
		{
			if ( listener != null )
			{
				listener.onMactrixChage( matrix );
			}
			setImageMatrix( matrix );
		}
	}
	

	
	




	public interface OnMatrixChangedListener
	{
		
		/** 每次旋转和缩放以后，都会调用这个方法来确保图片正确显示 */
        void onMactrixChage(Matrix matrix);
	}
	

	
	// 短按事件
	public interface ScaledImageClickListener
	{
		void onScaledClick(View v);
	}
	
	
	
	public void setScaledImageClickListener( ScaledImageClickListener clickListener )
	{
		this.clickListener = clickListener;
		
	}
	
	
	
	
}
