
package com.handmark.pulltorefresh.library.internal;


import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Orientation;
import com.handmark.pulltorefresh.library.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;


public class CycleLoadingLayout extends LoadingLayout
{
	private AnimationDrawable mAnimationDrawable;

	public CycleLoadingLayout( Context context , Mode mode , Orientation scrollDirection ,
			TypedArray attrs )
	{
		super( context , mode , scrollDirection , attrs );
		// TODO Auto-generated constructor stub
	}

	@Override
	protected int getDefaultDrawableResId( )
	{
		// TODO Auto-generated method stub
		return R.drawable.cycle_frame_1;
	}

	@Override
	protected void onLoadingDrawableSet( Drawable imageDrawable )
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void onPullImpl( float scaleOfLayout )
	{
		// TODO Auto-generated method stub
		setHeaderTextVisible( );
		mHeaderImage.setImageResource( R.drawable.cycle_frame_1 );
	}

	@Override
	protected void pullToRefreshImpl( )
	{
		// TODO Auto-generated method stub
	}

	@Override
	protected void refreshingImpl( )
	{
		// TODO Auto-generated method stub
		setHeaderTextVisible( );
		mHeaderImage.setImageResource( R.drawable.cycle_animation_list );
		mAnimationDrawable = ( AnimationDrawable ) mHeaderImage.getDrawable( );
		mAnimationDrawable.start( );
	}

	@Override
	protected void releaseToRefreshImpl( )
	{
		// TODO Auto-generated method stub
	}

	@Override
	protected void resetImpl( )
	{
		// TODO Auto-generated method stub
		if ( mAnimationDrawable != null )
		{
			mAnimationDrawable.stop( );
		}
		//goneAllViews( );
		mHeaderImage.setVisibility( View.VISIBLE );
		mHeaderImage.setImageResource( R.drawable.cycle_frame_1 );
		setHeaderTextVisible( );
	}

}
