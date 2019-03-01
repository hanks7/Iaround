package com.handmark.pulltorefresh.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcelable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.MeasureSpec;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

import com.handmark.pulltorefresh.library.internal.EmptyViewMethodAccessor;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;

/**
 * @ClassName: PullToRefreshPinnedSectionListView
 * @Description: 滑动中固定组标题ListView
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-12-13 2:08:49
 *
 */
public class PullToRefreshPinnedSectionListView extends PullToRefreshAdapterViewBase<ListView>{
	private LoadingLayoutBase mHeaderLoadingView;
	private LoadingLayoutBase mFooterLoadingView;

	private FrameLayout mLvFooterLoadingFrame;

	private boolean mListViewExtrasEnabled;

	public PullToRefreshPinnedSectionListView(Context context) {
		super(context);
	}

	public PullToRefreshPinnedSectionListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullToRefreshPinnedSectionListView(Context context, Mode mode) {
		super(context, mode);
	}

	public PullToRefreshPinnedSectionListView(Context context, Mode mode, AnimationStyle style) {
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
			OverscrollHelper.overScrollBy(PullToRefreshPinnedSectionListView.this, deltaX, scrollX, deltaY, scrollY, isTouchEvent);

			return returnValue;
		}
	}

	//-- inner classes

	/** List adapter to be implemented for being used with PinnedSectionListView adapter. */
	public interface PinnedSectionListAdapter extends ListAdapter {
		/** This method shall return 'true' if views of given type has to be pinned. */
		boolean isItemViewTypePinned(int viewType);
	}

	/** Wrapper class for pinned section view and its position in the list. */
	static class PinnedSection {
		public View view;
		public int position;
		public long id;
	}

	protected class InternalListView extends ListView implements EmptyViewMethodAccessor {

		private boolean mAddedLvFooter = false;

		//-- class fields

	    // fields used for handling touch events
	    private final Rect mTouchRect = new Rect();
	    private final PointF mTouchPoint = new PointF();
	    private int mTouchSlop;
	    private View mTouchTarget;
	    private MotionEvent mDownEvent;

	    // fields used for drawing shadow under a pinned section
	    private GradientDrawable mShadowDrawable;
	    private int mSectionsDistanceY;
	    private int mShadowHeight;

	    /** Delegating listener, can be null. */
	    OnScrollListener mDelegateOnScrollListener;

	    /** Shadow for being recycled, can be null. */
	    PinnedSection mRecycleSection;

	    /** shadow instance with a pinned view, can be null. */
	    PinnedSection mPinnedSection;

	    /** Pinned view Y-translation. We use it to stick pinned view to the next section. */
	    int mTranslateY;

		/** Scroll listener which does the magic */
		private final OnScrollListener mOnScrollListener = new OnScrollListener() {

			@Override public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (mDelegateOnScrollListener != null) { // delegate
					mDelegateOnScrollListener.onScrollStateChanged(view, scrollState);
				}
			}

			@Override
	        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	            if (mDelegateOnScrollListener != null) { // delegate
	                mDelegateOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
	            }

	            // get expected adapter or fail fast
	            ListAdapter adapter = getAdapter();
	            if (adapter == null || visibleItemCount == 0) return; // nothing to do

	            final boolean isFirstVisibleItemSection =
	                    isItemViewTypePinned(adapter, adapter.getItemViewType(firstVisibleItem));

	            if (isFirstVisibleItemSection) {
	                View sectionView = getChildAt(0);
	                if (sectionView.getTop() == getPaddingTop()) { // view sticks to the top, no need for pinned shadow
	                    destroyPinnedShadow();
	                } else { // section doesn't stick to the top, make sure we have a pinned shadow
	                    ensureShadowForPosition(firstVisibleItem, firstVisibleItem, visibleItemCount);
	                }

	            } else { // section is not at the first visible position
	                int sectionPosition = findCurrentSectionPosition(firstVisibleItem);
	                if (sectionPosition > -1) { // we have section position
	                    ensureShadowForPosition(sectionPosition, firstVisibleItem, visibleItemCount);
	                } else { // there is no section for the first visible item, destroy shadow
	                    destroyPinnedShadow();
	                }
	            }
			}

        };

		/** Default change observer. */
	    private final DataSetObserver mDataSetObserver = new DataSetObserver() {
	        @Override public void onChanged() {
	            recreatePinnedShadow();
	        }

            @Override public void onInvalidated() {
	            recreatePinnedShadow();
	        }
	    };

		public InternalListView(Context context, AttributeSet attrs) {
			super(context, attrs);
			initView();
		}

		private void initView() {
	        setOnScrollListener(mOnScrollListener);
	        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
//	        initShadow(true);
	    }

		//-- public API methods

	    public void setShadowVisible(boolean visible) {
//	        initShadow(visible);
	        if (mPinnedSection != null) {
	            View v = mPinnedSection.view;
	            invalidate(v.getLeft(), v.getTop(), v.getRight(), v.getBottom() + mShadowHeight);
	        }
	    }

	  //-- pinned section drawing methods

//	    public void initShadow(boolean visible) {
//	        if (visible) {
//	            if (mShadowDrawable == null) {
//	                mShadowDrawable = new GradientDrawable(android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM,
//	                        new int[] { Color.parseColor("#ffa0a0a0"), Color.parseColor("#50a0a0a0"), Color.parseColor("#00a0a0a0")});
//	                mShadowHeight = (int) (8 * getResources().getDisplayMetrics().density);
//	            }
//	        } else {
//	            if (mShadowDrawable != null) {
//	                mShadowDrawable = null;
//	                mShadowHeight = 0;
//	            }
//	        }
//	    }

	    /** Create shadow wrapper with a pinned view for a view at given position */
		void createPinnedShadow(int position) {

			// try to recycle shadow
			PinnedSection pinnedShadow = mRecycleSection;
			mRecycleSection = null;

			// create new shadow, if needed
			if (pinnedShadow == null) pinnedShadow = new PinnedSection();
			// request new view using recycled view, if such
			View pinnedView = getAdapter().getView(position, pinnedShadow.view, InternalListView.this);

			// read layout parameters
			LayoutParams layoutParams = (LayoutParams) pinnedView.getLayoutParams();
			if (layoutParams == null) { // create default layout params
			    layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			}

			int heightMode = MeasureSpec.getMode(layoutParams.height);
			int heightSize = MeasureSpec.getSize(layoutParams.height);

			if (heightMode == MeasureSpec.UNSPECIFIED) heightMode = MeasureSpec.EXACTLY;

			int maxHeight = getHeight() - getListPaddingTop() - getListPaddingBottom();
			if (heightSize > maxHeight) heightSize = maxHeight;

			// measure & layout
			int ws = MeasureSpec.makeMeasureSpec(getWidth() - getListPaddingLeft() - getListPaddingRight(), MeasureSpec.EXACTLY);
			int hs = MeasureSpec.makeMeasureSpec(heightSize, heightMode);
			pinnedView.measure(ws, hs);
			pinnedView.layout(0, 0, pinnedView.getMeasuredWidth(), pinnedView.getMeasuredHeight());
			mTranslateY = 0;

			// initialize pinned shadow
			pinnedShadow.view = pinnedView;
			pinnedShadow.position = position;
			pinnedShadow.id = getAdapter().getItemId(position);

			// store pinned shadow
			mPinnedSection = pinnedShadow;
		}

		/** Destroy shadow wrapper for currently pinned view */
		void destroyPinnedShadow() {
		    if (mPinnedSection != null) {
		        // keep shadow for being recycled later
		        mRecycleSection = mPinnedSection;
		        mPinnedSection = null;
		    }
		}

		/** Makes sure we have an actual pinned shadow for given position. */
	    void ensureShadowForPosition(int sectionPosition, int firstVisibleItem, int visibleItemCount) {
	        if (visibleItemCount < 2) { // no need for creating shadow at all, we have a single visible item
	            destroyPinnedShadow();
	            return;
	        }

	        if (mPinnedSection != null
	                && mPinnedSection.position != sectionPosition) { // invalidate shadow, if required
	            destroyPinnedShadow();
	        }

	        if (mPinnedSection == null) { // create shadow, if empty
	            createPinnedShadow(sectionPosition);
	        }

	        // align shadow according to next section position, if needed
	        int nextPosition = sectionPosition + 1;
	        if (nextPosition < getCount()) {
	            int nextSectionPosition = findFirstVisibleSectionPosition(nextPosition,
	                    visibleItemCount - (nextPosition - firstVisibleItem));
	            if (nextSectionPosition > -1) {
	                View nextSectionView = getChildAt(nextSectionPosition - firstVisibleItem);
	                final int bottom = mPinnedSection.view.getBottom() + getPaddingTop();
	                mSectionsDistanceY = nextSectionView.getTop() - bottom;
	                if (mSectionsDistanceY < 0) {
	                    // next section overlaps pinned shadow, move it up
	                    mTranslateY = mSectionsDistanceY;
	                } else {
	                    // next section does not overlap with pinned, stick to top
	                    mTranslateY = 0;
	                }
	            } else {
	                // no other sections are visible, stick to top
	                mTranslateY = 0;
	                mSectionsDistanceY = Integer.MAX_VALUE;
	            }
	        }

	    }

		int findFirstVisibleSectionPosition(int firstVisibleItem, int visibleItemCount) {
			ListAdapter adapter = getAdapter();
			for (int childIndex = 0; childIndex < visibleItemCount; childIndex++) {
				int position = firstVisibleItem + childIndex;
				int viewType = adapter.getItemViewType(position);
				if (isItemViewTypePinned(adapter, viewType)) return position;
			}
			return -1;
		}

		int findCurrentSectionPosition(int fromPosition) {
			ListAdapter adapter = getAdapter();

			if (adapter instanceof SectionIndexer) {
				// try fast way by asking section indexer
				SectionIndexer indexer = (SectionIndexer) adapter;
				int sectionPosition = indexer.getSectionForPosition(fromPosition);
				int itemPosition = indexer.getPositionForSection(sectionPosition);
				int typeView = adapter.getItemViewType(itemPosition);
				if (isItemViewTypePinned(adapter, typeView)) {
					return itemPosition;
				} // else, no luck
			}

			// try slow way by looking through to the next section item above
			for (int position=fromPosition; position>=0; position--) {
				int viewType = adapter.getItemViewType(position);
				if (isItemViewTypePinned(adapter, viewType)) return position;
			}
			return -1; // no candidate found
		}

		void recreatePinnedShadow() {
		    destroyPinnedShadow();
	        ListAdapter adapter = getAdapter();
	        if (adapter != null && adapter.getCount() > 0) {
	            int firstVisiblePosition = getFirstVisiblePosition();
	            int sectionPosition = findCurrentSectionPosition(firstVisiblePosition);
	            if (sectionPosition == -1) return; // no views to pin, exit
	            ensureShadowForPosition(sectionPosition,
	                    firstVisiblePosition, getLastVisiblePosition() - firstVisiblePosition);
	        }
		}

		@Override
		public void setOnScrollListener(OnScrollListener listener) {
			if (listener == mOnScrollListener) {
				super.setOnScrollListener(listener);
			} else {
				mDelegateOnScrollListener = listener;
			}
		}

		@Override
		public void onRestoreInstanceState(Parcelable state) {
			super.onRestoreInstanceState(state);
			post(new Runnable() {
				@Override public void run() { // restore pinned view after configuration change
				    recreatePinnedShadow();
				}
			});
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
				if (mPinnedSection != null) {

					// prepare variables
					int pLeft = getListPaddingLeft();
					int pTop = getListPaddingTop();
					View view = mPinnedSection.view;

					// draw child
					canvas.save();

					int clipHeight = view.getHeight() +
					        (mShadowDrawable == null ? 0 : Math.min(mShadowHeight, mSectionsDistanceY));
					canvas.clipRect(pLeft, pTop, pLeft + view.getWidth(), pTop + clipHeight);

					canvas.translate(pLeft, pTop + mTranslateY);
					drawChild(canvas, mPinnedSection.view, getDrawingTime());

					if (mShadowDrawable != null && mSectionsDistanceY > 0) {
					    mShadowDrawable.setBounds(mPinnedSection.view.getLeft(),
					            mPinnedSection.view.getBottom(),
					            mPinnedSection.view.getRight(),
					            mPinnedSection.view.getBottom() + mShadowHeight);
					    mShadowDrawable.draw(canvas);
					}

					canvas.restore();
				}
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			} catch (Exception ex){
				ex.printStackTrace();
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
				final float x = ev.getX();
		        final float y = ev.getY();
		        final int action = ev.getAction();

		        if (action == MotionEvent.ACTION_DOWN
		                && mTouchTarget == null
		                && mPinnedSection != null
		                && isPinnedViewTouched(mPinnedSection.view, x, y)) { // create touch target

		            // user touched pinned view
		            mTouchTarget = mPinnedSection.view;
		            mTouchPoint.x = x;
		            mTouchPoint.y = y;

		            // copy down event for eventually be used later
		            mDownEvent = MotionEvent.obtain(ev);
		        }

		        if (mTouchTarget != null) {
		            if (isPinnedViewTouched(mTouchTarget, x, y)) { // forward event to pinned view
		                mTouchTarget.dispatchTouchEvent(ev);
		            }

		            if (action == MotionEvent.ACTION_UP) { // perform onClick on pinned view
		                super.dispatchTouchEvent(ev);
		                performPinnedItemClick();
		                clearTouchTarget();

		            } else if (action == MotionEvent.ACTION_CANCEL) { // cancel
		                clearTouchTarget();

		            } else if (action == MotionEvent.ACTION_MOVE) {
		                if (Math.abs(y - mTouchPoint.y) > mTouchSlop) {

		                    // cancel sequence on touch target
		                    MotionEvent event = MotionEvent.obtain(ev);
		                    event.setAction(MotionEvent.ACTION_CANCEL);
		                    mTouchTarget.dispatchTouchEvent(event);
		                    event.recycle();

		                    // provide correct sequence to super class for further handling
		                    super.dispatchTouchEvent(mDownEvent);
		                    super.dispatchTouchEvent(ev);
		                    clearTouchTarget();

		                }
		            }

		            return true;
		        }
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

			// assert adapter in debug mode
			if (BuildConfig.DEBUG && adapter != null) {
				if (!(adapter instanceof PinnedSectionListAdapter))
					throw new IllegalArgumentException("Does your adapter implement PinnedSectionListAdapter?");
				if (adapter.getViewTypeCount() < 2)
					throw new IllegalArgumentException("Does your adapter handle at least two types" +
							" of views in getViewTypeCount() method: items and sections?");
			}

			// unregister observer at old adapter and register on new one
			ListAdapter oldAdapter = getAdapter();
			if (oldAdapter != null) oldAdapter.unregisterDataSetObserver(mDataSetObserver);
			if (adapter != null) adapter.registerDataSetObserver(mDataSetObserver);

			// destroy pinned shadow, if new adapter is not same as old one
			if (oldAdapter != adapter) destroyPinnedShadow();

			super.setAdapter(adapter);
		}

		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b) {
		    super.onLayout(changed, l, t, r, b);
	        if (mPinnedSection != null) {
	            int parentWidth = r - l - getPaddingLeft() - getPaddingRight();
	            int shadowWidth = mPinnedSection.view.getWidth();
	            if (parentWidth != shadowWidth) {
	                recreatePinnedShadow();
	            }
	        }
		}

		@Override
		public void setEmptyView(View emptyView) {
			PullToRefreshPinnedSectionListView.this.setEmptyView(emptyView);
		}

		@Override
		public void setEmptyViewInternal(View emptyView) {
			super.setEmptyView(emptyView);
		}

		private boolean isPinnedViewTouched(View view, float x, float y) {
	        view.getHitRect(mTouchRect);

	        // by taping top or bottom padding, the list performs on click on a border item.
	        // we don't add top padding here to keep behavior consistent.
	        mTouchRect.top += mTranslateY;

	        mTouchRect.bottom += mTranslateY + getPaddingTop();
	        mTouchRect.left += getPaddingLeft();
	        mTouchRect.right -= getPaddingRight();
	        return mTouchRect.contains((int)x, (int)y);
	    }

	    private void clearTouchTarget() {
	        mTouchTarget = null;
	        if (mDownEvent != null) {
	            mDownEvent.recycle();
	            mDownEvent = null;
	        }
	    }

	    private boolean performPinnedItemClick() {
	        if (mPinnedSection == null) return false;

	        OnItemClickListener listener = getOnItemClickListener();
	        if (listener != null) {
	            View view =  mPinnedSection.view;
	            playSoundEffect(SoundEffectConstants.CLICK);
	            if (view != null) {
	                view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
	            }
	            listener.onItemClick(this, view, mPinnedSection.position, mPinnedSection.id);
	            return true;
	        }
	        return false;
	    }

	    public boolean isItemViewTypePinned(ListAdapter adapter, int viewType) {
	        if (adapter instanceof HeaderViewListAdapter) {
	            adapter = ((HeaderViewListAdapter)adapter).getWrappedAdapter();
	        }

	        return ((PinnedSectionListAdapter) adapter).isItemViewTypePinned(viewType);
	    }

	}
}
