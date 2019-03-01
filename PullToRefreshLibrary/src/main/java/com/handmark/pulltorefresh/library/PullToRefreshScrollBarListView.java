package com.handmark.pulltorefresh.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

import com.handmark.pulltorefresh.library.internal.EmptyViewMethodAccessor;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;

public class PullToRefreshScrollBarListView extends PullToRefreshAdapterViewBase<ListView> {

	private LoadingLayoutBase mHeaderLoadingView;
	private LoadingLayoutBase mFooterLoadingView;

	private FrameLayout mLvFooterLoadingFrame;

	private boolean mListViewExtrasEnabled;

	public PullToRefreshScrollBarListView(Context context) {
		super(context);
	}

	public PullToRefreshScrollBarListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullToRefreshScrollBarListView(Context context, Mode mode) {
		super(context, mode);
	}

	public PullToRefreshScrollBarListView(Context context, Mode mode, AnimationStyle style) {
		super(context, mode, style);
	}

	@Override
	public final Orientation getPullToRefreshScrollDirection() {
		return Orientation.VERTICAL;
	}

	@Override
	protected void onRefreshing(final boolean doScroll) {
		/**
		 * If we're not showing the Refreshing view, or the list is empty, the
		 * the header/footer views won't show so we use the normal method.
		 */
//		ListAdapter adapter = mRefreshableView.getAdapter();
//		if (!mListViewExtrasEnabled || !getShowViewWhileRefreshing() || null == adapter || adapter.isEmpty()) {
//			super.onRefreshing(doScroll);
//			return;
//		}

		super.onRefreshing(false);

		final LoadingLayoutBase origLoadingView, listViewLoadingView, oppositeListViewLoadingView;
		final int selection, scrollToY;

		switch (getCurrentMode()) {
			case MANUAL_REFRESH_ONLY:
			case PULL_FROM_END:
				origLoadingView = getFooterLayout();
				listViewLoadingView = mFooterLoadingView;
				oppositeListViewLoadingView = mHeaderLoadingView;
				selection = mRefreshableView.getCount() - 1;
				scrollToY = getScrollY() - getFooterSize();
				break;
			case PULL_FROM_START:
			default:
				origLoadingView = getHeaderLayout();
				listViewLoadingView = mHeaderLoadingView;
				oppositeListViewLoadingView = mFooterLoadingView;
				selection = 0;
				scrollToY = getScrollY() + getHeaderSize();
				break;
		}

		// Hide our original Loading View
		origLoadingView.reset();
		origLoadingView.hideAllViews();

		// Make sure the opposite end is hidden too
		oppositeListViewLoadingView.setVisibility(View.GONE);

		// Show the ListView Loading View and set it to refresh.
		listViewLoadingView.setVisibility(View.VISIBLE);
		listViewLoadingView.refreshing();

		if (doScroll) {
			// We need to disable the automatic visibility changes for now
			disableLoadingLayoutVisibilityChanges();

			// We scroll slightly so that the ListView's header/footer is at the
			// same Y position as our normal header/footer
			setHeaderScroll(scrollToY);

			// Make sure the ListView is scrolled to show the loading
			// header/footer
			mRefreshableView.setSelection(selection);

			// Smooth scroll as normal
			smoothScrollTo(0);
		}
	}

	@Override
	protected void onReset() {
		/**
		 * If the extras are not enabled, just call up to super and return.
		 */
		if (!mListViewExtrasEnabled) {
			super.onReset();
			return;
		}

		final LoadingLayoutBase originalLoadingLayout, listViewLoadingLayout;
		final int scrollToHeight, selection;
		final boolean scrollLvToEdge;

		switch (getCurrentMode()) {
			case MANUAL_REFRESH_ONLY:
			case PULL_FROM_END:
				originalLoadingLayout = getFooterLayout();
				listViewLoadingLayout = mFooterLoadingView;
				selection = mRefreshableView.getCount() - 1;
				scrollToHeight = getFooterSize();
				scrollLvToEdge = Math.abs(mRefreshableView.getLastVisiblePosition() - selection) <= 1;
				break;
			case PULL_FROM_START:
			default:
				originalLoadingLayout = getHeaderLayout();
				listViewLoadingLayout = mHeaderLoadingView;
				scrollToHeight = -getHeaderSize();
				selection = 0;
				scrollLvToEdge = Math.abs(mRefreshableView.getFirstVisiblePosition() - selection) <= 1;
				break;
		}

		// If the ListView header loading layout is showing, then we need to
		// flip so that the original one is showing instead
		if (listViewLoadingLayout.getVisibility() == View.VISIBLE) {

			// Set our Original View to Visible
			originalLoadingLayout.showInvisibleViews();

			// Hide the ListView Header/Footer
			listViewLoadingLayout.setVisibility(View.GONE);

			/**
			 * Scroll so the View is at the same Y as the ListView
			 * header/footer, but only scroll if: we've pulled to refresh, it's
			 * positioned correctly
			 */
			if (scrollLvToEdge && getState() != State.MANUAL_REFRESHING) {
				mRefreshableView.setSelection(selection);
				setHeaderScroll(scrollToHeight);
			}
		}

		// Finally, call up to super
		super.onReset();
	}

	@Override
	protected LoadingLayoutProxy createLoadingLayoutProxy(final boolean includeStart, final boolean includeEnd) {
		LoadingLayoutProxy proxy = super.createLoadingLayoutProxy(includeStart, includeEnd);

		if (mListViewExtrasEnabled) {
			final Mode mode = getMode();

			if (includeStart && mode.showHeaderLoadingLayout()) {
				proxy.addLayout(mHeaderLoadingView);
			}
			if (includeEnd && mode.showFooterLoadingLayout()) {
				proxy.addLayout(mFooterLoadingView);
			}
		}

		return proxy;
	}

	protected ListView createListView(Context context, AttributeSet attrs) {
		final ListView lv;
		if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
			lv = new InternalListViewSDK9(context, attrs);
		} else {
			lv = new InternalListView(context, attrs);
		}
		return lv;
	}

	@Override
	protected ListView createRefreshableView(Context context, AttributeSet attrs) {
		ListView lv = createListView(context, attrs);

		// Set it to this so it can be used in ListActivity/ListFragment
		lv.setId(android.R.id.list);
		return lv;
	}

	@Override
	protected void handleStyledAttributes(TypedArray a) {
		super.handleStyledAttributes(a);

		mListViewExtrasEnabled = a.getBoolean(R.styleable.PullToRefresh_ptrListViewExtrasEnabled, true);

		if (mListViewExtrasEnabled) {
			final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
					FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);

			// Create Loading Views ready for use later
			FrameLayout frame = new FrameLayout(getContext());
			mHeaderLoadingView = createLoadingLayout(getContext(), Mode.PULL_FROM_START, a);
			mHeaderLoadingView.setVisibility(View.GONE);
			frame.addView(mHeaderLoadingView, lp);
			mRefreshableView.addHeaderView(frame, null, false);

			mLvFooterLoadingFrame = new FrameLayout(getContext());
			mFooterLoadingView = createLoadingLayout(getContext(), Mode.PULL_FROM_END, a);
			mFooterLoadingView.setVisibility(View.GONE);
			mLvFooterLoadingFrame.addView(mFooterLoadingView, lp);

			/**
			 * If the value for Scrolling While Refreshing hasn't been
			 * explicitly set via XML, enable Scrolling While Refreshing.
			 */
			if (!a.hasValue(R.styleable.PullToRefresh_ptrScrollingWhileRefreshingEnabled)) {
				setScrollingWhileRefreshingEnabled(true);
			}
		}
	}

	@TargetApi(9)
	final class InternalListViewSDK9 extends InternalListView {

		public InternalListViewSDK9(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
				int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

			final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
					scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

			// Does all of the hard work...
			OverscrollHelper.overScrollBy(PullToRefreshScrollBarListView.this, deltaX, scrollX, deltaY, scrollY, isTouchEvent);

			return returnValue;
		}
	}

	public void setOnPositionChangedListener(OnPositionChangedListener onPositionChangedListener){
		((InternalListView)this.mRefreshableView).setOnPositionChangedListener(onPositionChangedListener);
	}

	public interface OnPositionChangedListener {
		void onPositionChanged(PullToRefreshScrollBarListView listView, int position, View scrollBarPanel);
	}

	protected class InternalListView extends ListView implements EmptyViewMethodAccessor, OnScrollListener {

		private boolean mAddedLvFooter = false;

		private OnScrollListener mOnScrollListener = null;

		private View mScrollBarPanel = null;
		private int mScrollBarPanelPosition = 0;

		private OnPositionChangedListener mPositionChangedListener;
		private int mLastPosition = -1;

		private Animation mInAnimation = null;
		private Animation mOutAnimation = null;

		private final Handler mHandler = new Handler();

		private final Runnable mScrollBarPanelFadeRunnable = new Runnable() {

			@Override
			public void run() {
				if (mOutAnimation != null) {
					mScrollBarPanel.startAnimation(mOutAnimation);
				}
			}
		};

		/*
		 * keep track of Measure Spec
		 */
		private int mWidthMeasureSpec;
		private int mHeightMeasureSpec;

		public InternalListView(Context context, AttributeSet attrs) {
			super(context, attrs);
			super.setOnScrollListener(this);

			final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScrollBarListView);
			final int scrollBarPanelLayoutId = a.getResourceId(R.styleable.ScrollBarListView_scrollBarPanel, -1);
			final int scrollBarPanelInAnimation = a.getResourceId(R.styleable.ScrollBarListView_scrollBarPanelInAnimation, R.anim.in_animation);
			final int scrollBarPanelOutAnimation = a.getResourceId(R.styleable.ScrollBarListView_scrollBarPanelOutAnimation, R.anim.out_animation);
			a.recycle();

			if (scrollBarPanelLayoutId != -1) {
				setScrollBarPanel(scrollBarPanelLayoutId);
			}

			final int scrollBarPanelFadeDuration = ViewConfiguration.getScrollBarFadeDuration();

			if (scrollBarPanelInAnimation > 0) {
				mInAnimation = AnimationUtils.loadAnimation(getContext(), scrollBarPanelInAnimation);
			}

			if (scrollBarPanelOutAnimation > 0) {
				mOutAnimation = AnimationUtils.loadAnimation(getContext(), scrollBarPanelOutAnimation);
				mOutAnimation.setDuration(scrollBarPanelFadeDuration);

				mOutAnimation.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						if (mScrollBarPanel != null) {
							mScrollBarPanel.setVisibility(View.GONE);
						}
					}
				});
			}

		}

		public void setOnPositionChangedListener(OnPositionChangedListener onPositionChangedListener) {
			mPositionChangedListener = onPositionChangedListener;
		}

		@Override
		public void setOnScrollListener(OnScrollListener onScrollListener) {
			mOnScrollListener = onScrollListener;
		}

		public void setScrollBarPanel(View scrollBarPanel) {
			mScrollBarPanel = scrollBarPanel;
			mScrollBarPanel.setVisibility(View.GONE);
			requestLayout();
		}

		public void setScrollBarPanel(int resId) {
			setScrollBarPanel(LayoutInflater.from(getContext()).inflate(resId, this, false));
		}

		public View getScrollBarPanel() {
			return mScrollBarPanel;
		}

		@Override
		protected boolean awakenScrollBars(int startDelay, boolean invalidate) {
			final boolean isAnimationPlayed = super.awakenScrollBars(startDelay, invalidate);

			if (isAnimationPlayed == true && mScrollBarPanel != null) {
				if (mScrollBarPanel.getVisibility() == View.GONE) {
					mScrollBarPanel.setVisibility(View.VISIBLE);
					if (mInAnimation != null) {
						mScrollBarPanel.startAnimation(mInAnimation);
					}
				}

				mHandler.removeCallbacks(mScrollBarPanelFadeRunnable);
				mHandler.postAtTime(mScrollBarPanelFadeRunnable, AnimationUtils.currentAnimationTimeMillis() + startDelay);
			}

			return isAnimationPlayed;
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);

			if (mScrollBarPanel != null && getAdapter() != null) {
				mWidthMeasureSpec = widthMeasureSpec;
				mHeightMeasureSpec = heightMeasureSpec;
				measureChild(mScrollBarPanel, widthMeasureSpec, heightMeasureSpec);
			}
		}

		@Override
		protected void dispatchDraw(Canvas canvas) {
			/**
			 * This is a bit hacky, but Samsung's ListView has got a bug in it
			 * when using Header/Footer Views and the list is empty. This masks
			 * the issue so that it doesn't cause an FC. See Issue #66.
			 */
			try {
				super.dispatchDraw(canvas);
				if (mScrollBarPanel != null && mScrollBarPanel.getVisibility() == View.VISIBLE) {
					drawChild(canvas, mScrollBarPanel, getDrawingTime());
				}
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}

		@Override
		public void onDetachedFromWindow() {
			super.onDetachedFromWindow();

			mHandler.removeCallbacks(mScrollBarPanelFadeRunnable);
		}

		@Override
		protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
			super.onLayout(changed, left, top, right, bottom);

			if (mScrollBarPanel != null) {
				final int x = getMeasuredWidth() - mScrollBarPanel.getMeasuredWidth() - getVerticalScrollbarWidth();
				mScrollBarPanel.layout(x, mScrollBarPanelPosition, x + mScrollBarPanel.getMeasuredWidth(),
						mScrollBarPanelPosition + mScrollBarPanel.getMeasuredHeight());
			}
		}

		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
			/**
			 * This is a bit hacky, but Samsung's ListView has got a bug in it
			 * when using Header/Footer Views and the list is empty. This masks
			 * the issue so that it doesn't cause an FC. See Issue #66.
			 */
			try {
				return super.dispatchTouchEvent(ev);
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
				return false;
			} catch (Exception ex){
				ex.printStackTrace();
				return false;
			}
		}

		@Override
		public void setAdapter(ListAdapter adapter) {
			// Add the Footer View at the last possible moment
			if (null != mLvFooterLoadingFrame && !mAddedLvFooter) {
				addFooterView(mLvFooterLoadingFrame, null, false);
				mAddedLvFooter = true;
			}

			super.setAdapter(adapter);
		}

		@Override
		public void setEmptyView(View emptyView) {
			PullToRefreshScrollBarListView.this.setEmptyView(emptyView);
		}

		@Override
		public void setEmptyViewInternal(View emptyView) {
			super.setEmptyView(emptyView);
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if (null != mPositionChangedListener && null != mScrollBarPanel) {

				// Don't do anything if there is no itemviews
				if (totalItemCount > 0) {
					/*
					 * from android source code (ScrollBarDrawable.java)
					 */
					final int thickness = getVerticalScrollbarWidth();
					int height = Math.round((float) getMeasuredHeight() * computeVerticalScrollExtent() / computeVerticalScrollRange());
					int thumbOffset = Math.round((float) (getMeasuredHeight() - height) * computeVerticalScrollOffset() / (computeVerticalScrollRange() - computeVerticalScrollExtent()));
					final int minLength = thickness * 2;
					if (height < minLength) {
						height = minLength;
					}
					thumbOffset += height / 2;

					/*
					 * find out which itemviews the center of thumb is on
					 */
					final int count = getChildCount();
					for (int i = 0; i < count; ++i) {
						final View childView = getChildAt(i);
						if (childView != null) {
							if (thumbOffset > childView.getTop() && thumbOffset < childView.getBottom()) {
								/*
								 * we have our candidate
								 */
								if (mLastPosition != firstVisibleItem + i) {
									mLastPosition = firstVisibleItem + i;

									/*
									 * inform the position of the panel has changed
									 */
									mPositionChangedListener.onPositionChanged(PullToRefreshScrollBarListView.this, mLastPosition, mScrollBarPanel);

									/*
									 * measure panel right now since it has just changed
									 *
									 * INFO: quick hack to handle TextView has ScrollBarPanel (to wrap text in
									 * case TextView's content has changed)
									 */
									measureChild(mScrollBarPanel, mWidthMeasureSpec, mHeightMeasureSpec);
								}
								break;
							}
						}
					}

					/*
					 * update panel position
					 */
					mScrollBarPanelPosition = thumbOffset - mScrollBarPanel.getMeasuredHeight() / 2;
					final int x = getMeasuredWidth() - mScrollBarPanel.getMeasuredWidth() - getVerticalScrollbarWidth();
					mScrollBarPanel.layout(x, mScrollBarPanelPosition, x + mScrollBarPanel.getMeasuredWidth(),
							mScrollBarPanelPosition + mScrollBarPanel.getMeasuredHeight());
				}
			}

			if (mOnScrollListener != null) {
				mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (mOnScrollListener != null) {
				mOnScrollListener.onScrollStateChanged(view, scrollState);
			}


		}

	}

}
