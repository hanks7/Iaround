
package net.iaround.tools.picture;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.widget.ImageView;

import net.iaround.tools.CommonFunction;


public class ScaledImageView extends ImageView implements OnTouchListener
{
	private static final int NONE = 0; // 触摸松开
	private static final int DRAG_1 = 1; // 单点触摸
	private static final int DRAG_2 = 2; // 两点触摸
	private static final int ZOOM = 3; // 缩放操作
	private static final int HANDLER_LONG_CLICK = 0;
	private static final int HANDLER_CLICK = 1;
	private Bitmap bitmap;
	private Matrix matrix;
	private Matrix savedMatrix;
	private float[ ] downPoint;
	private int mode; // 触摸事件类型
	private float distSquare; // 两个手指的距离平方
	private float lastX;
	private float lastY;
	private OnMatrixChangedListener listener;
	private ScaledImageClickListener clickListener;
	private ScaledImageLongClickListener longClickListener;
	private int dragScrollMinDistSquare;
	private int clickState = 1;// 0按下，1移动过
	private boolean isCanMove;
	private Handler handler = new Handler( )
	{
		
		@Override
		public void handleMessage( Message msg )
		{
			super.handleMessage( msg );
			switch ( msg.what )
			{
				case HANDLER_CLICK :
					if ( clickListener != null )
					{
						clickListener.onScaledClick( ScaledImageView.this );
					}
					
					break;
				case HANDLER_LONG_CLICK :
					clickState = 1;
					if ( longClickListener != null )
					{
						longClickListener.onScaledLongClick( ScaledImageView.this );
					}
					break;
			}
		}
		
	};
	
	public ScaledImageView(Context context )
	{
		super( context );
		init( context );
	}
	
	public ScaledImageView(Context context , AttributeSet attrs )
	{
		super( context , attrs );
		init( context );
	}
	
	public ScaledImageView(Context context , AttributeSet attrs , int defStyle )
	{
		super( context , attrs , defStyle );
		init( context );
	}
	
	private void init( Context context )
	{
		ViewConfiguration configuration = ViewConfiguration.get( context );
		dragScrollMinDistSquare = configuration.getScaledTouchSlop( );
		dragScrollMinDistSquare *= dragScrollMinDistSquare;
		setOnTouchListener( this );
		isCanMove = true;
	}
	
	public void setMoveGesture(boolean isCanMove)
	{
		this.isCanMove = isCanMove;
	}
	
	/**
	 * 设置图片
	 * 
	 * @param bm
	 */
	public void setBitmap( Bitmap bm )
	{
		bitmap = bm;
		setImageBitmap( bm );
		matrix = new Matrix( );
		matrix.set( getImageMatrix( ) );
		if ( listener != null )
		{
			listener.onMactrixChage( matrix );
		}
		setImageMatrix( matrix );
	}
	
	public Bitmap getBitmap()
	{
		return bitmap;
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
	
	public boolean onTouch( View v , MotionEvent event )
	{
		try
		{
			if ( event.getAction( ) == MotionEvent.ACTION_DOWN )
			{
				lastX = event.getX( );
				lastY = event.getY( );
				handler.sendEmptyMessageDelayed( HANDLER_LONG_CLICK , 500 );// 长按事件
				clickState = 0;
			}
			else if ( event.getAction( ) == MotionEvent.ACTION_UP )
			{
				handler.removeMessages( HANDLER_LONG_CLICK );
				if ( clickState == 0 )
				{
					handler.sendEmptyMessage( HANDLER_CLICK );// 短按事件
				}
				
			}
			else
			{
				float currentX = event.getX( );
				float currentY = event.getY( );
				if ( Math.abs( currentX - lastX ) > 10 || Math.abs( currentY - lastY ) > 10 )
				{
					handler.removeMessages( HANDLER_LONG_CLICK );
					clickState = 1;
				}
				lastX = event.getX( );
				lastY = event.getY( );
				
			}
			switch ( event.getAction( ) )
			{
				case MotionEvent.ACTION_DOWN :
				{
					matrix = new Matrix( );
					matrix.set( getImageMatrix( ) );
					savedMatrix = new Matrix( );
					savedMatrix.set( matrix );
					downPoint = new float[ ]
						{ event.getX( 0 ) , event.getY( 0 ) };
					mode = DRAG_1;
				}
					break;
				case MotionEvent.ACTION_UP :
				{
					if ( listener != null )
					{
						listener.onMactrixChage( matrix );
					}
					float dx = event.getX( 0 ) - downPoint[ 0 ];
					float dy = event.getY( 0 ) - downPoint[ 1 ];
					if ( mode == DRAG_1 && ( dx * dx + dy * dy <= 0 ) )
					{
						performClick( );
					}
					mode = NONE;
				}
					break;
				case MotionEvent.ACTION_POINTER_1_DOWN :
				{
					float[ ] start = new float[ ]
						{ event.getX( 0 ) , event.getY( 0 ) };
					float[ ] end = new float[ ]
						{ event.getX( 1 ) , event.getY( 1 ) };
					float dx = start[ 0 ] - end[ 0 ];
					float dy = start[ 1 ] - end[ 1 ];
					distSquare = dx * dx + dy * dy;
					mode = ZOOM;
				}
					break;
				case MotionEvent.ACTION_POINTER_1_UP :
				{
					downPoint = new float[ ]
						{ event.getX( 1 ) , event.getY( 1 ) };
					savedMatrix.set( matrix );
					mode = DRAG_2;
				}
					break;
				case MotionEvent.ACTION_POINTER_2_DOWN :
				{
					float[ ] start = new float[ ]
						{ event.getX( 0 ) , event.getY( 0 ) };
					float[ ] end = new float[ ]
						{ event.getX( 1 ) , event.getY( 1 ) };
					float dx = start[ 0 ] - end[ 0 ];
					float dy = start[ 1 ] - end[ 1 ];
					distSquare = dx * dx + dy * dy;
					mode = ZOOM;
				}
					break;
				case MotionEvent.ACTION_POINTER_2_UP :
				{
					downPoint = new float[ ]
						{ event.getX( 0 ) , event.getY( 0 ) };
					savedMatrix.set( matrix );
					mode = DRAG_1;
				}
					break;
				case MotionEvent.ACTION_MOVE :
				{
					if ( mode == DRAG_1 )
					{
						if(!isCanMove) return false;
						float[ ] end = new float[ ]
							{ event.getX( 0 ) , event.getY( 0 ) };
						matrix.set( savedMatrix );
						matrix.postTranslate( end[ 0 ] - downPoint[ 0 ] , end[ 1 ]
								- downPoint[ 1 ] );
					}
					else if ( mode == DRAG_2 )
					{
						if(!isCanMove) return false;
						float[ ] end = new float[ ]
							{ event.getX( 1 ) , event.getY( 1 ) };
						matrix.set( savedMatrix );
						matrix.postTranslate( end[ 0 ] - downPoint[ 0 ] , end[ 1 ]
								- downPoint[ 1 ] );
					}
					else if ( mode == ZOOM )
					{
						float[ ] start = new float[ ]
							{ event.getX( 0 ) , event.getY( 0 ) };
						float[ ] end = new float[ ]
							{ event.getX( 1 ) , event.getY( 1 ) };
						float dx = start[ 0 ] - end[ 0 ];
						float dy = start[ 1 ] - end[ 1 ];
						float newDistSquare = dx * dx + dy * dy;
						matrix.set( savedMatrix );
						float scale = (float)Math.sqrt( newDistSquare / distSquare );
						float[ ] middle = new float[ ]
							{ ( start[ 0 ] + end[ 0 ] ) / 2 , ( start[ 1 ] + end[ 1 ] ) / 2 };
						matrix.postScale( scale , scale , middle[ 0 ] , middle[ 1 ] );
					}
				}
					break;
				default :
				{
					return false;
				}
			}
			setImageMatrix( matrix );
		}
		catch ( Throwable t )
		{
			CommonFunction.log( t );
		}
		return true;
	}
	
	/**
	 * 获取指定区域内的图片
	 * 
	 * @param cropRect
	 * @return
	 */
	public Bitmap getCropedBitmap( Rect cropRect )
	{
		try
		{
			setDrawingCacheEnabled( true );
			buildDrawingCache( );
			Bitmap bmTmp = CommonFunction.captureView( this );
			if ( bmTmp == null )
			{
				return null;
			}
			
			Bitmap scaledBm = CommonFunction.createBitmap( bmTmp , cropRect.left ,
					cropRect.top , cropRect.width( ) , cropRect.height( ) );
			bmTmp.recycle( );
			bmTmp = null;
			setDrawingCacheEnabled( false );
			
			return scaledBm;
		}
		catch ( Throwable e )
		{
			CommonFunction.log( e );
		}
		return null;
	}
	
	/** 左转90度 */
	public void rotateLeft( )
	{
		try
		{
			matrix = new Matrix( );
			float[ ] matrixValue = new float[ ]
				{ 0 , 1 , 0 , -1 , 0 , 0 , 0 , 0 , 1 };
			matrix.setValues( matrixValue );
			Bitmap resizedBitmap = CommonFunction.createBitmap( bitmap , 0 , 0 ,
					bitmap.getWidth( ) , bitmap.getHeight( ) , matrix , true );
			if ( resizedBitmap != null && !resizedBitmap.isRecycled( ) )
			{
				bitmap.recycle( );
				bitmap = resizedBitmap;
			}
			setImageBitmap( bitmap );
			
			matrix = new Matrix( );
			matrix.set( getImageMatrix( ) );
			
			
			if ( listener != null )
			{
				listener.onMactrixChage( matrix );
			}
			setImageMatrix( matrix );
		}
		catch ( Throwable t )
		{
			CommonFunction.log( t );
		}
	}
	
	/** 右转90度 */
	public void rotateRight( )
	{
		try
		{
			matrix = new Matrix( );
			float[ ] matrixValue = new float[ ]
				{ 0 , -1 , 0 , 1 , 0 , 0 , 0 , 0 , 1 };
			matrix.setValues( matrixValue );
			Bitmap resizedBitmap = CommonFunction.createBitmap( bitmap , 0 , 0 ,
					bitmap.getWidth( ) , bitmap.getHeight( ) , matrix , true );
			if ( resizedBitmap != null && !resizedBitmap.isRecycled( ) )
			{
				bitmap.recycle( );
				bitmap = resizedBitmap;
			}
			setImageBitmap( bitmap );
			
			matrix = new Matrix( );
			matrix.set(getImageMatrix( ) );
			if ( listener != null )
			{
				listener.onMactrixChage( matrix );
			}
			setImageMatrix( matrix );
		}
		catch ( Throwable t )
		{
			CommonFunction.log( t );
		}
	}
	
	/** 放大（本方法执行十次，放大一倍） */
	public void zoomIn( )
	{
		matrix = new Matrix( );
		matrix.set( getImageMatrix( ) );
		matrix.postScale( 1.072f , 1.072f ); // Math.pow(1.072, 10) = 2
		if ( listener != null )
		{
			listener.onMactrixChage( matrix );
		}
		setImageMatrix( matrix );
	}
	
	/** 缩小 （本方法执行十次，缩小一半） */
	public void zoomOut( )
	{
		matrix = new Matrix( );
		matrix.set( getImageMatrix( ) );
		matrix.postScale( 0.933f , 0.933f ); // Math.pow(0.933, 10) = 0.5
		if ( listener != null )
		{
			listener.onMactrixChage( matrix );
		}
		setImageMatrix( matrix );
	}
	
	/**
	 * 当ScaledImageView的图片被修改时，次方法会被调用
	 * 
	 * @author 余勋杰
	 */
	public interface OnMatrixChangedListener
	{
		
		/** 每次旋转和缩放以后，都会调用这个方法来确保图片正确显示 */
        void onMactrixChage(Matrix matrix);
	}
	
	// 长按事件
	public interface ScaledImageLongClickListener
	{
		void onScaledLongClick(View v);
	}
	
	// 短按事件
	public interface ScaledImageClickListener
	{
		void onScaledClick(View v);
	}
	
	public void setScaledImageLongClickListener( ScaledImageLongClickListener longClickListener )
	{
		this.longClickListener = longClickListener;
	}
	
	public void setScaledImageClickListener( ScaledImageClickListener clickListener )
	{
		this.clickListener = clickListener;
		
	}
	
	
}
