/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.handmark.pulltorefresh.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class PullToRefreshScrollView extends PullToRefreshBase<ScrollView> {
	
	private OnTouchListener mCustomOnTouchListener;
	
	private OnTouchListener mTouchListener = new OnTouchListener( )
	{
		
		@Override
		public boolean onTouch( View view , MotionEvent event )
		{
			// TODO Auto-generated method stub
			switch ( event.getAction( ) )
			{
				case MotionEvent.ACTION_DOWN :
					
					break;
				case MotionEvent.ACTION_MOVE :
					int scrollY = view.getScrollY( );
					int height = view.getHeight( );
					int scrollViewMeasuredHeight = getChildAt( 0 )
							.getMeasuredHeight( );
					/**
					 * scrollViewMeasuredHeight表示ScrollView所占的高度，即ScrollView内容的高度
					 * scrollY表示ScrollView顶端已经滑出去的高度
					 * height表示ScrollView的可见高度
					 * */
					//load more data when it scrolls to bottom
					if ( ( scrollY + height ) >= scrollViewMeasuredHeight )
					{
						if ( canAutoLoad )
						{
							loadData( );				
							canAutoLoad = false;
						}
					}
					break;
				
				default :
					break;
			}
			if ( getOnTouchListener( ) == null )
			{
				return false;
			}
			return getOnTouchListener( ).onTouch( view , event );
		}
	};

	public PullToRefreshScrollView(Context context) {
		super(context);
		initOnTouchListener( );
	}

	public PullToRefreshScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initOnTouchListener( );
	}

	public PullToRefreshScrollView(Context context, Mode mode) {
		super(context, mode);
		initOnTouchListener( );
	}

	public PullToRefreshScrollView(Context context, Mode mode, AnimationStyle style) {
		super(context, mode, style);
		initOnTouchListener( );
	}

	@Override
	public final Orientation getPullToRefreshScrollDirection() {
		return Orientation.VERTICAL;
	}

	@Override
	protected ScrollView createRefreshableView(Context context, AttributeSet attrs) {
		ScrollView scrollView;
		if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
			scrollView = new InternalScrollViewSDK9(context, attrs);
		} else {
			scrollView = new ScrollView(context, attrs);
		}

		scrollView.setId(R.id.scrollview);
		return scrollView;
	}

	@Override
	protected boolean isReadyForPullStart() {
		return mRefreshableView.getScrollY() == 0;
	}

	@Override
	protected boolean isReadyForPullEnd() {
		View scrollViewChild = mRefreshableView.getChildAt(0);
		if (null != scrollViewChild) {
			return mRefreshableView.getScrollY() >= (scrollViewChild.getHeight() - getHeight());
		}
		return false;
	}

	@TargetApi(9)
	final class InternalScrollViewSDK9 extends ScrollView {

		public InternalScrollViewSDK9(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
				int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

			final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
					scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

			// Does all of the hard work...
			OverscrollHelper.overScrollBy(PullToRefreshScrollView.this, deltaX, scrollX, deltaY, scrollY,
					getScrollRange(), isTouchEvent);

			return returnValue;
		}

		/**
		 * Taken from the AOSP ScrollView source
		 */
		private int getScrollRange() {
			int scrollRange = 0;
			if (getChildCount() > 0) {
				View child = getChildAt(0);
				scrollRange = Math.max(0, child.getHeight() - (getHeight() - getPaddingBottom() - getPaddingTop()));
			}
			return scrollRange;
		}
	}

	@Override
	public void handleRefreshComplete( )
	{
		// TODO Auto-generated method stub
		if ( getCurrentMode( ) == Mode.PULL_FROM_END )
		{
			canAutoLoad = true;
		}
	}
	
	private void initOnTouchListener( )
	{
		getRefreshableView( ).setOnTouchListener( mTouchListener );
	}
	
	private void loadData( )
	{
		if ( getOnRefreshListener2( ) != null )
		{
			getOnRefreshListener2( ).onPullUpToRefresh( this );
		}			
	}

	public OnTouchListener getOnTouchListener( )
	{
		return mCustomOnTouchListener;
	}

	public void setCustomOnTouchListener( OnTouchListener onTouchListener )
	{
		this.mCustomOnTouchListener = onTouchListener;
	}
}
