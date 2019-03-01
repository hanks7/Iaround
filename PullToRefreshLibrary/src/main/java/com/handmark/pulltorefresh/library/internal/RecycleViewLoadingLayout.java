package com.handmark.pulltorefresh.library.internal;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.LoadingLayoutBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.R;

/**
 * Created by zwenkai on 2015/12/19.
 */
public class RecycleViewLoadingLayout extends LoadingLayoutBase {

    static final String LOG_TAG = "PullToRefresh-JingDongHeaderLayout";
    private FrameLayout mInnerLayout;
    private AnimationDrawable mAnimationDrawable;
    protected final ImageView mHeaderImage;
    protected final ProgressBar mHeaderProgress;
    private CharSequence mRefreshingLabel;
    private CharSequence mReleaseLabel;
    private final TextView mHeaderText;
    private final TextView mSubHeaderText;

    public RecycleViewLoadingLayout(Context context) {
        this(context, PullToRefreshBase.Mode.PULL_FROM_START);
    }

    public RecycleViewLoadingLayout(Context context, PullToRefreshBase.Mode mode) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header_vertical, this);
        mInnerLayout = (FrameLayout) findViewById(R.id.fl_inner);
        mHeaderText = (TextView) mInnerLayout.findViewById(R.id.pull_to_refresh_text);
        mHeaderProgress = (ProgressBar) mInnerLayout.findViewById(R.id.pull_to_refresh_progress);
        mSubHeaderText = (TextView) mInnerLayout.findViewById(R.id.pull_to_refresh_sub_text);
        mHeaderImage = (ImageView) mInnerLayout.findViewById(R.id.pull_to_refresh_image);
        mRefreshingLabel = context.getString(R.string.pull_to_refresh_refreshing_label);
        mReleaseLabel = context.getString(R.string.pull_to_refresh_release_label);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mInnerLayout.getLayoutParams();
        lp.gravity = mode == PullToRefreshBase.Mode.PULL_FROM_END ? Gravity.TOP : Gravity.BOTTOM;
        mHeaderImage.setImageResource(R.drawable.cycle_frame_1);
        reset();
    }

    @Override
    public final int getContentSize() {
        return mInnerLayout.getHeight();
    }

    @Override
    public final void pullToRefresh() {
        if (null != mHeaderText) {
            mHeaderText.setText(mReleaseLabel);
        }
    }

    @Override
    public final void onPull(float scaleOfLayout) {
        if ( null != mHeaderText ) {
            mHeaderText.setVisibility( View.VISIBLE );
        }
        mHeaderImage.setImageResource( R.drawable.cycle_frame_1 );
        setTextAppearance( R.style.HeaderTextAppearance );
    }

    private void setTextAppearance(int value) {
        if (null != mHeaderText) {
            mHeaderText.setTextAppearance(getContext(), value);
        }
        if (null != mSubHeaderText) {
            mSubHeaderText.setTextAppearance(getContext(), value);
        }
    }

    @Override
    public final void refreshing() {
        if ( null != mHeaderText ) {
            mHeaderText.setVisibility( View.VISIBLE );
        }
        if (null != mHeaderText) {
            mHeaderText.setText(mRefreshingLabel);
        }
        setTextAppearance( R.style.HeaderTextAppearance );
        mHeaderImage.setImageResource( R.drawable.cycle_animation_list );
        mAnimationDrawable = ( AnimationDrawable ) mHeaderImage.getDrawable( );
        mAnimationDrawable.start( );
    }

    @Override
    public final void releaseToRefresh() {
        if (null != mHeaderText) {
            mHeaderText.setText(mReleaseLabel);
        }
    }

    @Override
    public final void reset() {
        if ( null != mHeaderText ) {
            mHeaderText.setVisibility( View.VISIBLE );
        }
        if ( mAnimationDrawable != null ) {
            mAnimationDrawable.stop( );
        }
        //goneAllViews( );
        mHeaderImage.setVisibility( View.VISIBLE );
        mHeaderImage.setImageResource( R.drawable.cycle_frame_1 );
    }

    @Override
    public void setLastUpdatedLabel(CharSequence label) {
    }

    @Override
    public void setPullLabel(CharSequence pullLabel) {
    }

    @Override
    public void setRefreshingLabel(CharSequence refreshingLabel) {
    }

    @Override
    public void setReleaseLabel(CharSequence releaseLabel) {
    }

    @Override
    public void setTextTypeface(Typeface tf) {
        mHeaderText.setTypeface(tf);
    }

    private void setTextColor(ColorStateList color) {
        if (null != mHeaderText) {
            mHeaderText.setTextColor(color);
        }
        if (null != mSubHeaderText) {
            mSubHeaderText.setTextColor(color);
        }
    }
}