/*
 * HorizontalListView.java v1.5
 *
 * 
 * The MIT License
 * Copyright (c) 2011 Paul Soucy (paul@dev-smart.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package net.iaround.ui.game;


import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Scroller;

import net.iaround.tools.CommonFunction;

import java.util.LinkedList;
import java.util.Queue;


public class HorizontalListView extends AdapterView< ListAdapter >
{
	
	public boolean mAlwaysOverrideTouch = true;
	protected ListAdapter mAdapter;
	private int mLeftViewIndex = -1;
	private int mRightViewIndex = 0;
	protected int mCurrentX;
	protected int mNextX;
	private int mMaxX = Integer.MAX_VALUE;
	private int mDisplayOffset = 0;
	protected Scroller mScroller;
	private GestureDetector mGesture;
	private Queue< View > mRemovedViewQueue = new LinkedList< View >( );
	private OnItemSelectedListener mOnItemSelected;
	private OnItemClickListener mOnItemClicked;
	private boolean mDataChanged = false;
	private ScrollStateWatcher watcher;
	private ScrollFinish scrollFinish;
	private int mItemW;
	private boolean isSetPosition;
	private int curPosition;
	private boolean isScrolling;
	private boolean isAdater;
	
	private long aimPoint = 0;
	private Handler mHandler = new Handler( )
	{
		@Override
		public void handleMessage( Message msg )
		{
			super.handleMessage( msg );
			switch ( msg.what )
			{
				case 0 :
					if ( isAdater )
					{
						isAdater = false;
						
						curPosition = ( mCurrentX + mItemW / 2 ) / mItemW;
						if ( mCurrentX != curPosition * mItemW )
						{
							curPosition = ( mCurrentX + mItemW / 2 ) / mItemW;
							
							scrollTo( curPosition * mItemW );
							
							requestLayout( );
											
							mHandler.postAtTime( new Runnable( )
							{
								
								@Override
								public void run( )
								{
									// TODO Auto-generated method stub
									if ( isSetPosition )
									{
										isSetPosition = false;
									}
									else
									{
										if ( watcher != null )
										{
											
											watcher.onScroll( HorizontalListView.this );
											
										}
										if ( scrollFinish != null )
										{
											scrollFinish.onScrollFinish( curPosition );
										}
									}
								}
							} , 500 );
							

							
						}
						else
						{
							mScroller.forceFinished( true );
							if ( isSetPosition )
							{
								isSetPosition = false;
							}
							else
							{
								if ( watcher != null )
								{
									
									watcher.onScroll( HorizontalListView.this );
									
								}
								if ( scrollFinish != null )
								{
									scrollFinish.onScrollFinish( curPosition );
								}
							}
						}
						
					}
					CommonFunction.log( "ChatGame" , "handleMessage::::LeftIndex="
							+ mLeftViewIndex + ";mRightIndex =" + mRightViewIndex + ";Offset="
							+ mDisplayOffset + ";CurrentX=" + mCurrentX + ";w=" + mItemW
							+ ";getFinalX" + mScroller.getFinalX( ) );
					
					
					break;
				case 1 :
					CommonFunction.log( "ChatGame" , "curPosition ====" + curPosition
							+ ";mItemW=" + mItemW );
					
					scrollTo( curPosition * mItemW );
					aimPoint = curPosition * mItemW;
					break;
			}
		}
		
	};
	
	public HorizontalListView(Context context , AttributeSet attrs , int defStyle )
	{
		super( context , attrs , defStyle );
		initView( );
	}
	
	public HorizontalListView(Context context )
	{
		super( context );
		initView( );
	}
	
	public HorizontalListView(Context context , AttributeSet attrs )
	{
		super( context , attrs );
		initView( );
	}
	
	private synchronized void initView( )
	{
		mLeftViewIndex = -1;
		mRightViewIndex = 0;
		mDisplayOffset = 0;
		mCurrentX = 0;
		mNextX = 0;
		mMaxX = Integer.MAX_VALUE;
		mScroller = new Scroller( getContext( ) );
		mGesture = new GestureDetector( getContext( ) , mOnGesture );
	}
	
	@Override
	public void setOnItemSelectedListener( AdapterView.OnItemSelectedListener listener )
	{
		mOnItemSelected = listener;
	}

	@Override
	public void setOnItemClickListener( AdapterView.OnItemClickListener listener )
	{
		mOnItemClicked = listener;
	}
	
	private DataSetObserver mDataObserver = new DataSetObserver( )
	{
		
		@Override
		public void onChanged( )
		{
			synchronized ( HorizontalListView.this )
			{
				mDataChanged = true;
			}
			invalidate( );
			requestLayout( );
		}
		
		@Override
		public void onInvalidated( )
		{
			reset( );
			invalidate( );
			requestLayout( );
		}
		
	};
	
	@Override
	public ListAdapter getAdapter( )
	{
		return mAdapter;
	}
	
	@Override
	public View getSelectedView( )
	{
		// TODO: implement
		return null;
	}
	
	@Override
	public void setAdapter( ListAdapter adapter )
	{
		if ( mAdapter != null )
		{
			mAdapter.unregisterDataSetObserver( mDataObserver );
		}
		mAdapter = adapter;
		mAdapter.registerDataSetObserver( mDataObserver );
		reset( );
	}
	
	private synchronized void reset( )
	{
		initView( );
		removeAllViewsInLayout( );
		requestLayout( );
	}
	
	public int getSelection( )
	{
		
		curPosition = ( mCurrentX + mItemW / 2 ) / mItemW;
		return curPosition;
	}
	
	@Override
	public void setSelection( int position )
	{
		// TODO: implement
		
		// mNextX = position*mItemW ;
		// mScroller.setFinalX( mNextX ) ;
		// requestLayout( );
		
		isSetPosition = true;
		curPosition = position;
		mHandler.sendEmptyMessage( 1 );
		
//		mHandler.postAtTime( new Runnable( )
//		{
//			
//			@Override
//			public void run( )
//			{
//				// TODO Auto-generated method stub
//				if(isSetPosition)
//				{
//				
//					mScroller.forceFinished( true );
//					isSetPosition = true;
//					isAdater = true;
//				}
//			}
//		} , 500 ) ;
		
		
	}
	
	private void addAndMeasureChild(final View child , int viewPos )
	{
		LayoutParams params = child.getLayoutParams( );
		if ( params == null )
		{
			params = new LayoutParams( LayoutParams.FILL_PARENT , LayoutParams.FILL_PARENT );
		}
		
		addViewInLayout( child , viewPos , params , true );
		child.measure( MeasureSpec.makeMeasureSpec( getWidth( ) , MeasureSpec.AT_MOST ) ,
				MeasureSpec.makeMeasureSpec( getHeight( ) , MeasureSpec.AT_MOST ) );
	}
	
	@Override
	protected synchronized void onLayout( boolean changed , int left , int top , int right ,
			int bottom )
	{
		super.onLayout( changed , left , top , right , bottom );
		
		if ( mAdapter == null )
		{
			return;
		}
		
		if ( mDataChanged )
		{
			int oldCurrentX = mCurrentX;
			initView( );
			removeAllViewsInLayout( );
			mNextX = oldCurrentX;
			mDataChanged = false;
		}
		
		if ( mScroller.computeScrollOffset( ) )
		{
			int scrollx = mScroller.getCurrX( );
			int finalx = mScroller.getFinalX( );
			mNextX = scrollx;
			if ( mNextX == mCurrentX && finalx != mCurrentX )
			{
				// mNextX = (mScroller.getFinalX( ) - mCurrentX)/2;
				// mNextX = finalx;
				if ( finalx - mCurrentX > 2 )
				{
					mNextX += 2;
				}
				else if ( mCurrentX - finalx > 2 )
				{
					mNextX -= 2;
				}
				else
				{
					mNextX = finalx;
					
				}
			}
			isScrolling = true;
			CommonFunction.log( "ChatGame" , "computeScrollOffset mNextX =" + mNextX
					+ ";mCurrentX=" + mCurrentX + ";FinalX==" + finalx );
		}
		
		if ( mNextX <= 0 )
		{
			mNextX = 0;
			mScroller.forceFinished( true );
		}
		if ( mNextX >= mMaxX )
		{
			mNextX = mMaxX;
			mScroller.forceFinished( true );
		}
		
		int dx = mCurrentX - mNextX;
		
		removeNonVisibleItems( dx );
		fillList( dx );
		positionItems( dx );
		
		mCurrentX = mNextX;
		
		if ( !mScroller.isFinished( ) )
		{
			post( new Runnable( )
			{
				@Override
				public void run( )
				{
					requestLayout( );
				}
			} );
			
		}
		else
		{
			
			
			if ( isSetPosition )
			{
				
			}
			
			
		}
	}
	
	@Override
	public void computeScroll( )
	{
		
		// 先判断mScroller滚动是否完成
		if ( !mScroller.computeScrollOffset( ) )
		{
			
			
			
			// 必须调用该方法，否则不一定能看到滚动效果
			
			postInvalidate( );
			if ( isScrolling )
			{
				isScrolling = false;
				isAdater = true;
				mHandler.sendEmptyMessage( 0 );
			}
		}
		super.computeScroll( );
	}
	
	private void fillList( final int dx )
	{
		int edge = 0;
		View child = getChildAt( getChildCount( ) - 1 );
		if ( child != null )
		{
			edge = child.getRight( );
			
		}
		fillListRight( edge , dx );
		
		edge = 0;
		child = getChildAt( 0 );
		if ( child != null )
		{
			edge = child.getLeft( );
			
		}
		fillListLeft( edge , dx );
		
	}
	
	private void fillListRight( int rightEdge , final int dx )
	{
		// CommonFunction.log( "ChatGame","fillListRight  rightEdge =="
		// +rightEdge +";dx=="+dx);
		while ( rightEdge + dx < getWidth( ) && mRightViewIndex < mAdapter.getCount( ) )
		{
			
			View child = mAdapter.getView( mRightViewIndex , mRemovedViewQueue.poll( ) , this );
			addAndMeasureChild( child , -1 );
			rightEdge += child.getMeasuredWidth( );
			
			if ( mRightViewIndex == mAdapter.getCount( ) - 1 )
			{
				mMaxX = mCurrentX + rightEdge - getWidth( );
			}
			
			if ( mMaxX < 0 )
			{
				mMaxX = 0;
			}
			mRightViewIndex++;
		}
		
	}
	
	private void fillListLeft( int leftEdge , final int dx )
	{
		// CommonFunction.log( "ChatGame","fillListLeft  leftEdge ==" +leftEdge
		// +";dx=="+dx);
		while ( leftEdge + dx > 0 && mLeftViewIndex >= 0 )
		{
			View child = mAdapter.getView( mLeftViewIndex , mRemovedViewQueue.poll( ) , this );
			addAndMeasureChild( child , 0 );
			leftEdge -= child.getMeasuredWidth( );
			mLeftViewIndex--;
			mDisplayOffset -= child.getMeasuredWidth( );
		}
	}
	
	private void removeNonVisibleItems( final int dx )
	{
		View child = getChildAt( 0 );
		while ( child != null && child.getRight( ) + dx <= 0 )
		{
			mDisplayOffset += child.getMeasuredWidth( );
			mRemovedViewQueue.offer( child );
			removeViewInLayout( child );
			mLeftViewIndex++;
			child = getChildAt( 0 );
			
		}
		
		child = getChildAt( getChildCount( ) - 1 );
		
		while ( child != null && child.getLeft( ) + dx >= getWidth( ) )
		{
			mRemovedViewQueue.offer( child );
			removeViewInLayout( child );
			mRightViewIndex--;
			child = getChildAt( getChildCount( ) - 1 );
		}
	}
	
	private void positionItems( final int dx )
	{
		if ( getChildCount( ) > 0 )
		{
			mDisplayOffset += dx;
			int left = mDisplayOffset;
			for ( int i = 0 ; i < getChildCount( ) ; i++ )
			{
				View child = getChildAt( i );
				int childWidth = child.getMeasuredWidth( );
				child.layout( left , 0 , left + childWidth , child.getMeasuredHeight( ) );
				left += childWidth;
				mItemW = childWidth;
			}
		}
	}
	
	public synchronized void scrollTo( int x )
	{
		// CommonFunction.log( "ChatGame" ,"scrollTo  mNextX ==="+mNextX);
		mScroller.startScroll( mNextX , 0 , x - mNextX , 0 , 1000 );
		
		invalidate( );
		requestLayout( );
	}
	
	
	
	@Override
	public boolean dispatchTouchEvent( MotionEvent ev )
	{
		if ( ev.getAction( ) == MotionEvent.ACTION_DOWN
				|| ev.getAction( ) == MotionEvent.ACTION_MOVE )
		{
			// 通知父组建不要拦截本子组建事件
			if ( getParent( ) != null )
			{
				getParent( ).requestDisallowInterceptTouchEvent( true );
			}
			return mGesture.onTouchEvent( ev );
		}
		else if ( ev.getAction( ) == MotionEvent.ACTION_UP
				|| ev.getAction( ) == MotionEvent.ACTION_CANCEL )
		{
			// 通知父组建拦截子组建事件
			if ( getParent( ) != null )
			{
				getParent( ).requestDisallowInterceptTouchEvent( false );
			}
			return mGesture.onTouchEvent( ev );
		}
		
		return super.dispatchTouchEvent( ev );
	}
	
	@Override
	public boolean onTouchEvent( MotionEvent event )
	{
		if ( event.getAction( ) == MotionEvent.ACTION_DOWN
				|| event.getAction( ) == MotionEvent.ACTION_MOVE )
		{
			// 通知父组建不要拦截本子组建事件
			if ( getParent( ) != null )
			{
				getParent( ).requestDisallowInterceptTouchEvent( true );
				// postCheckForLongClick();
			}
			return mGesture.onTouchEvent( event );
		}
		else if ( event.getAction( ) == MotionEvent.ACTION_UP
				|| event.getAction( ) == MotionEvent.ACTION_CANCEL )
		{
			// 通知父组建拦截子组建事件
			if ( getParent( ) != null )
			{
				getParent( ).requestDisallowInterceptTouchEvent( false );
			}
			performClick( );
			return mGesture.onTouchEvent( event );
		}
		
		return super.onTouchEvent( event );
	}
	
	protected boolean onFling(MotionEvent e1 , MotionEvent e2 , float velocityX ,
							  float velocityY )
	{
		synchronized ( HorizontalListView.this )
		{
			mScroller.fling( mNextX , 0 , ( int ) -velocityX , 0 , 0 , mMaxX , 0 , 0 );
		}
		requestLayout( );
		CommonFunction.log( "ChatGame" , "onFling::::velocityX=" + velocityX + ";;;mNextX ="
				+ mNextX );
		return true;
	}
	
	protected boolean onDown( MotionEvent e )
	{
		mScroller.forceFinished( true );
		return true;
	}
	
	private OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener( )
	{
		
		@Override
		public boolean onDown( MotionEvent e )
		{
			return HorizontalListView.this.onDown( e );
		}
		
		@Override
		public boolean onFling(MotionEvent e1 , MotionEvent e2 , float velocityX ,
							   float velocityY )
		{
			return HorizontalListView.this.onFling( e1 , e2 , velocityX , velocityY );
		}
		
		@Override
		public boolean onScroll(MotionEvent e1 , MotionEvent e2 , float distanceX ,
								float distanceY )
		{
			if ( watcher != null )
			{
//				 if ( Math.abs( distanceX ) > 2 )
				{
					watcher.onScroll( HorizontalListView.this );
				}
			}
			synchronized ( HorizontalListView.this )
			{
				mNextX += ( int ) distanceX;
			}
			requestLayout( );
			CommonFunction.log( "ChatGame" , "onScroll::::distanceX=" + distanceX
					+ ";;;mNextX =" + mNextX );
			return true;
		}
		
		@Override
		public boolean onSingleTapConfirmed( MotionEvent e )
		{
			Rect viewRect = new Rect( );
			for ( int i = 0 ; i < getChildCount( ) ; i++ )
			{
				View child = getChildAt( i );
				int left = child.getLeft( );
				int right = child.getRight( );
				int top = child.getTop( );
				int bottom = child.getBottom( );
				viewRect.set( left , top , right , bottom );
				if ( viewRect.contains( ( int ) e.getX( ) , ( int ) e.getY( ) ) )
				{
					if ( mOnItemClicked != null )
					{
						mOnItemClicked.onItemClick( HorizontalListView.this , child ,
								mLeftViewIndex + 1 + i ,
								mAdapter.getItemId( mLeftViewIndex + 1 + i ) );
					}
					if ( mOnItemSelected != null )
					{
						mOnItemSelected.onItemSelected( HorizontalListView.this , child ,
								mLeftViewIndex + 1 + i ,
								mAdapter.getItemId( mLeftViewIndex + 1 + i ) );
					}
					break;
				}
				
			}
			return true;
		}
		
	};
	
	public void setScrollStateWatcher( ScrollStateWatcher watcher )
	{
		this.watcher = watcher;
	}
	
	public interface ScrollStateWatcher
	{
		void onScroll(HorizontalListView listView);
	}
	
	public void setScrollFinish( ScrollFinish onScrollFinish )
	{
		this.scrollFinish = onScrollFinish;
	}
	
	public interface ScrollFinish
	{
		void onScrollFinish(int position);
	}
}
