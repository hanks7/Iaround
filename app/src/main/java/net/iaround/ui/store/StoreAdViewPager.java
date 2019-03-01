
package net.iaround.ui.store;


import android.content.Context;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import net.iaround.R;

import net.iaround.ui.datamodel.ResourceListBean.ResourceItemBean;
import net.iaround.utils.ImageViewUtil;

import java.util.ArrayList;


public class StoreAdViewPager extends RelativeLayout
{
	private ViewPager mAdViewPager;
	private LinearLayout mDotsLy;
	private int mCurrentItem = 0;
	private View[ ] mDots;
	private ResourceItemBean[ ] mBanner;
	private OnClickListener mOnClickListener;
	private float xLast , yLast , xDistance , yDistance;
	
	public StoreAdViewPager(Context context )
	{
		super( context );
		init( );
	}
	
	public StoreAdViewPager(Context context , AttributeSet attrs )
	{
		super( context , attrs );
		init( );
	}
	
	public StoreAdViewPager(Context context , AttributeSet attrs , int defStyle )
	{
		super( context , attrs , defStyle );
		init( );
	}
	
	private void init( )
	{
		LayoutInflater.from( getContext( ) ).inflate( R.layout.gamead_view_pager , this );
		mAdViewPager = (ViewPager) findViewById( R.id.vp );
		mDotsLy = (LinearLayout) findViewById( R.id.dots_ly );
	}
	
	public void setOnTouchListener( OnTouchListener onTouchListener )
	{
		mAdViewPager.setOnTouchListener( onTouchListener );
	}
	
	public void showNext( )
	{
		int position = mCurrentItem + 1;
		if ( position > ( mBanner.length - 1 ) )
		{
			position = 0;
		}
		mAdViewPager.setCurrentItem( position );
	}
	
	public void showPre( )
	{
		int position = mCurrentItem - 1;
		if ( position < 0 )
		{
			position = mBanner.length - 1;
		}
		mAdViewPager.setCurrentItem( position );
	}
	
	public void setData( ArrayList< ResourceItemBean > ads )
	{
		if ( ads == null || ads.size( ) == 0 )
		{
			return;
		}
		
		mDotsLy.removeAllViews( );
		mDots = null;
		mBanner = null;
		
		int size = ads.size( );
		mDots = new View[ size ];
		float density = getResources( ).getDisplayMetrics( ).density;
		int dp_5 = ( int ) ( 5 * density );
		int dp_2 = ( int ) ( 2 * density );
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( dp_5 , dp_5 );
		params.setMargins( dp_2 , 0 , dp_2 , 0 );
		for ( int i = 0 ; i < size ; i++ )
		{
			mDots[ i ] = new View( getContext( ) );
			mDots[ i ].setLayoutParams( params );
			if ( i == 0 )
			{
				mDots[ i ].setBackgroundResource( R.drawable.dot_focused );
			}
			else
			{
				mDots[ i ].setBackgroundResource( R.drawable.dot_normal );
			}
			mDotsLy.addView( mDots[ i ] );
		}
		
		mBanner = ads.toArray( new ResourceItemBean[ size ] );
		mCurrentItem = 0;
		mAdViewPager.setAdapter( new FaceAdAdapter( ) );
		mAdViewPager.setOnPageChangeListener( new MyPageChangeListener( ) );
	}
	
	public void setOnItemClickListener( OnClickListener listener )
	{
		mOnClickListener = listener;
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
		}
		else if ( ev.getAction( ) == MotionEvent.ACTION_UP
				|| ev.getAction( ) == MotionEvent.ACTION_CANCEL )
		{
			// 通知父组建拦截子组建事件
			if ( getParent( ) != null )
			{
				getParent( ).requestDisallowInterceptTouchEvent( false );
			}
		}
		
		return super.dispatchTouchEvent( ev );
	}
	
	@Override
	public boolean onTouchEvent( MotionEvent event )
	{
		// switch ( event.getAction( ) )
		// {
		// case MotionEvent.ACTION_DOWN :
		// xLast = event.getX( );
		// yLast = event.getY( );
		// xDistance = yDistance = 0f;
		// break;
		//
		// case MotionEvent.ACTION_MOVE :
		// float curX = event.getX( );
		// final float curY = event.getY();
		// xDistance += Math.abs(curX - xLast);
		// yDistance += Math.abs(curY - yLast);
		// xLast = curX;
		// yLast = curY;
		//
		// if(xDistance > yDistance){
		// getParent( ).requestDisallowInterceptTouchEvent( true );
		// }
		// break;
		//
		// case MotionEvent.ACTION_UP :
		// getParent( ).requestDisallowInterceptTouchEvent( false );
		// break;
		// case MotionEvent.ACTION_CANCEL :
		// getParent( ).requestDisallowInterceptTouchEvent( false );
		// break;
		// }
		
		if ( event.getAction( ) == MotionEvent.ACTION_DOWN
				|| event.getAction( ) == MotionEvent.ACTION_MOVE )
		{
			// 通知父组建不要拦截本子组建事件
			if ( getParent( ) != null )
			{
				getParent( ).requestDisallowInterceptTouchEvent( true );
			}
		}
		else if ( event.getAction( ) == MotionEvent.ACTION_UP
				|| event.getAction( ) == MotionEvent.ACTION_CANCEL )
		{
			// 通知父组建拦截子组建事件
			if ( getParent( ) != null )
			{
				getParent( ).requestDisallowInterceptTouchEvent( false );
			}
		}
		return super.onTouchEvent( event );
	}
	
	private class MyPageChangeListener implements OnPageChangeListener
	{
		private int oldPosition = 0;
		
		/**
		 * This method will be invoked when a new page becomes selected.
		 * position: Position index of the new selected page.
		 */
		public void onPageSelected( int position )
		{
			mCurrentItem = position;
			mDots[ oldPosition ].setBackgroundResource( R.drawable.dot_normal );
			mDots[ position ].setBackgroundResource( R.drawable.dot_focused );
			oldPosition = position;
		}
		
		public void onPageScrollStateChanged( int arg0 )
		{
			
		}
		
		public void onPageScrolled( int arg0 , float arg1 , int arg2 )
		{
			
		}
	}
	
	private OnClickListener onItemClickListener = new OnClickListener( )
	{
		
		@Override
		public void onClick( View v )
		{
			// TODO Auto-generated method stub
			if ( mOnClickListener != null )
			{
				mOnClickListener.onClick( v );
			}
		}
	};
	
	private class FaceAdAdapter extends PagerAdapter
	{
		
		@Override
		public int getCount( )
		{
			if ( mBanner != null )
			{
				return mBanner.length;
			}
			return 0;
		}
		
		@Override
		public Object instantiateItem(View arg0 , int arg1 )
		{
			ImageView img = new ImageView( getContext( ) );
			img.setLayoutParams( new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT ,
					RelativeLayout.LayoutParams.MATCH_PARENT ) );
			img.setScaleType( ScaleType.FIT_XY );
			img.setTag( mBanner[ arg1 ] );
			img.setBackgroundColor( Color.BLACK );
			img.setOnClickListener( onItemClickListener );
			
			ImageViewUtil.getDefault( ).fadeInLoadImageInConvertView(
					mBanner[ arg1 ].banner.getImageUrl( ) , img ,
					R.drawable.game_main_ad_default_icon , R.drawable.game_main_ad_default_icon );
			
			( (ViewPager) arg0 ).addView( img );
			return img;
		}
		
		@Override
		public void destroyItem(View arg0 , int arg1 , Object arg2 )
		{
			( (ViewPager) arg0 ).removeView( (View) arg2 );
		}
		
		@Override
		public boolean isViewFromObject(View arg0 , Object arg1 )
		{
			return arg0 == arg1;
		}
		
		@Override
		public void restoreState(Parcelable arg0 , ClassLoader arg1 )
		{
			
		}
		
		@Override
		public Parcelable saveState( )
		{
			return null;
		}
		
		@Override
		public void startUpdate( View arg0 )
		{
			
		}
		
		@Override
		public void finishUpdate( View arg0 )
		{
			
		}
	}
}
